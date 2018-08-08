package com.jza.async.handler;

import com.jza.async.EventHandler;
import com.jza.async.EventModel;
import com.jza.async.EventType;
import com.jza.model.Message;
import com.jza.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class MessageHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Override
    public void doHandler(EventModel eventModel) {
        messageService.addMessage((Message) eventModel.get("message"));
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return new LinkedList<>(Arrays.asList(EventType.MESSAGE));
    }
}
