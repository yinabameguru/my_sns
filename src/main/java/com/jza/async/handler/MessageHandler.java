package com.jza.async.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jza.async.EventHandler;
import com.jza.async.EventModel;
import com.jza.async.EventType;
import com.jza.model.Message;
import com.jza.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class MessageHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailHandler.class);
    @Autowired
    MessageService messageService;

    @Override
    public void doHandler(EventModel eventModel) {
        try {
            JSONObject jsonObject =(JSONObject)eventModel.get("message");
            Message message = jsonObject.toJavaObject(Message.class);
            messageService.addMessage(message);
        } catch (Exception e) {
            LOGGER.error("发送站内信失败" + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return new LinkedList<>(Arrays.asList(EventType.MESSAGE));
    }
}
