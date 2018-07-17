package com.jza.service;

import com.jza.dao.UserDao;
import com.jza.model.User;
import com.jza.utils.SnsUtils;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static javax.swing.UIManager.put;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public User findUser(int userId) {
        return userDao.selectUserById(userId);
    }

    public Map<String,Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        try {
            User userResult = userDao.selectUserByName(user);
            if (userResult != null)
                map.put("errMsg","用户已存在");
            else {
                String salt = UUID.randomUUID().toString().substring(0,5);
                user.setSalt(salt);
                user.setPassword(SnsUtils.MD5(user.getPassword() + salt));
                user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
                userDao.insertUser(user);
            };
            return map;
        }catch (RuntimeException e){
            e.printStackTrace();
            map.put("errMsg","注册失败");
            return map;
        }
    }

    public Map<String, Object> login(User user) {
        Map<String, Object> map = new HashMap<>();
        try {
            User userResult = userDao.selectUserByName(user);
            if (userResult == null)

                map.put("errMsg","用户名不存在");
            else {
                if (SnsUtils.MD5(user.getPassword() + userResult.getSalt()) == userResult.getPassword())
                    map.put("errMsg","密码错误");
            }
            return map;
        }catch (RuntimeException e){
            e.printStackTrace();
            map.put("errMsg","登陆失败");
            return map;
        }
    }
}
