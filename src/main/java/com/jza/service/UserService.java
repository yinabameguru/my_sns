package com.jza.service;

import com.jza.dao.UserDao;
import com.jza.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public User findUser(int userId) {
        return userDao.selectUser(userId);
    }
}
