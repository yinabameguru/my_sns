package com.jza.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jza.dao.MessageDao;
import com.jza.model.HostHolder;
import com.jza.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    HostHolder hostHolder;

    public Integer addMessage(Message message) {
        return messageDao.insertMessage(message);
    }

    public PageInfo<Message> getMessages(String conversationId, Integer currentPage) {
        if (currentPage == null)
            currentPage = 0;
        PageHelper.startPage(currentPage, 999);
        List<Message> messages = messageDao.selectMessage(conversationId);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);
        return pageInfo;
    }

    public PageInfo<Message> getMessageList(Integer userId,Integer currentPage) {
        if (currentPage == null)
            currentPage = 0;
        PageHelper.startPage(currentPage, 8);
        List<Message> messages = messageDao.selectMessageList(userId);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);
        return pageInfo;
    }

    public Integer updateHasRead(String conversationId) {
        return messageDao.updateHasRead(hostHolder.getUser().getId(), conversationId, 1);
    }
}
