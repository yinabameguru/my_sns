package com.jza.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.jza.async.EventHandler;
import com.jza.async.EventModel;
import com.jza.async.EventType;
import com.jza.model.Mail;
import com.jza.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class MailHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailHandler.class);
    @Autowired
    MailService mailSender;

    @Override
    public void doHandler(EventModel eventModel) {
        try {
            JSONObject jsonObject = (JSONObject) eventModel.get("mail");
            Mail mail = jsonObject.toJavaObject(Mail.class);
            mailSender.sendWithHTMLTemplate(mail);
        } catch (Exception e) {
            LOGGER.error("发送邮件失败" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return new LinkedList<>(Arrays.asList(EventType.MAIL));
    }
}
