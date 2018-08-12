package com.jza.service;

import com.jza.dao.TicketDao;
import com.jza.dao.UserDao;
import com.jza.model.Ticket;
import com.jza.model.User;
import com.jza.utils.SnsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    TicketDao ticketDao;
    @Autowired
    SensitiveService sensitiveService;

    public User findUser(int userId) {
        return userDao.selectUserById(userId);
    }

    public User getUserByName(String name) {
        return userDao.selectUserByName(new User(name));
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        User userResult = userDao.selectUserByName(user);
        if (userResult != null)
            map.put("errMsg", "用户已存在");
        else {
            String salt = UUID.randomUUID().toString().substring(0, 5);
            user.setSalt(salt);
            user.setPassword(SnsUtils.MD5(user.getPassword() + salt));
            user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
            user.setActivationCode(UUID.randomUUID().toString());
            user.setActivationStatus(0);
            userDao.insertUser(user);
        }
        ;
        return map;
    }

    public Ticket activationLogin(User user, boolean rememberme) {

        return addTicket(user.getId(), rememberme);

    }

    public Map<String, Object> login(User user, boolean rememberme) {
        Map<String, Object> map = new HashMap<>();
        User userResult = userDao.selectUserByName(user);
        if (userResult == null) {
            map.put("errMsg", "用户名不存在");
            return map;
        }
        if (!SnsUtils.MD5(user.getPassword() + userResult.getSalt()).equals(userResult.getPassword())) {
            map.put("errMsg", "密码错误");
            return map;
        }
        if (userResult.getActivationStatus() == 0) {
            StringBuilder msg = new StringBuilder();
            msg.append("用户未激活,点击");
            msg.append("这里激活");
            map.put("errMsg", msg.toString());
            return map;
        }
        Ticket ticket = addTicket(userResult.getId(), rememberme);
        map.put("user", userResult);
        map.put("ticket", ticket);
        return map;
    }

    private Ticket addTicket(Integer userId, boolean remeberme) {
        String ticket = UUID.randomUUID().toString().replaceAll("-", "");
        Date date = new Date();
        if (remeberme)
            date.setTime(date.getTime() + 3600 * 1000 * 24 * 10);
        else
            date.setTime(date.getTime() + 3600 * 1000);
        if (ticketDao.addTicket(userId, ticket, date) == 1)
            return new Ticket(userId, ticket, date, 0);
        throw new RuntimeException();

    }

    public Integer logout(String ticket) {
        return ticketDao.updateStatus(ticket);
    }

    public Integer activation(String name, String activationCode) {
        return userDao.updateActivationStatus(name, activationCode);
    }
}
