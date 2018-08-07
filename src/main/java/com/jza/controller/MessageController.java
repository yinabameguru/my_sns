package com.jza.controller;

import com.github.pagehelper.PageInfo;
import com.jza.model.HostHolder;
import com.jza.model.Message;
import com.jza.model.User;
import com.jza.model.ViewObject;
import com.jza.service.MessageService;
import com.jza.service.SensitiveService;
import com.jza.service.UserService;
import com.jza.utils.SnsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    SensitiveService sensitiveService;

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(value = "/msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(
            @RequestParam("toName") String name,
            @RequestParam("content") String content
    ) {
        try {
            User toUser = userService.getUserByName(name);
            if (hostHolder.getUser() == null)
                return SnsUtils.getJSONString(999);
            if (toUser == null)
                return SnsUtils.getJSONString(1, "用户不存在");
            if(toUser.getId() == hostHolder.getUser().getId())
                return SnsUtils.getJSONString(1, "不能给自己发消息！！！");
            content = sensitiveService.filter(HtmlUtils.htmlEscape(content));
            Message message = new Message();
            message.setContent(content);
            message.setConversationId(toUser.getId(),hostHolder.getUser().getId());
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setHasRead(0);
            message.setToId(toUser.getId());
            messageService.addMessage(message);
            return SnsUtils.getJSONString(0);
        } catch (Exception e) {
            LOGGER.error("添加站内信错误" + e.getMessage());
            return SnsUtils.getJSONString(1, "服务器错误");
        }
    }

    @RequestMapping(value = "/msg/detail",method = {RequestMethod.GET})
    public String messageDetail(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "currentPage",required = false) Integer currentPage,
            Model model
    ) {
        try {
            PageInfo<Message> pageInfo = messageService.getMessages(conversationId, currentPage);
            LinkedList<ViewObject> viewObjects = new LinkedList<>();
            ListIterator<Message> iterator = pageInfo.getList().listIterator();
            while (iterator.hasNext()) {
                ViewObject viewObject = new ViewObject();
                Message next = iterator.next();
                viewObject.set("content",next.getContent());
                viewObject.set("createdDate", next.getCreatedDate());
                viewObject.set("fromName", sensitiveService.filter(HtmlUtils.htmlEscape(next.getFromUser().getName())));
                viewObject.set("fromHeadUrl", next.getFromUser().getHeadUrl());
                viewObjects.add(viewObject);
            }
            model.addAttribute("vos", viewObjects);
            messageService.updateHasRead(conversationId);
            return "letterDetail";
        } catch (Exception e) {
            LOGGER.error("站内信查找错误" + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errMsg", "服务器错误");
            return "err";
        }
    }

    @RequestMapping(value = "/msg/list", method = {RequestMethod.GET})
    public String messageList(
            @RequestParam(value = "currentPage",required = false) Integer currentPage,
            Model model
    ) {
        try {
            LinkedList<ViewObject> viewObjects = new LinkedList<>();
            PageInfo<Message> pageInfo = messageService.getMessageList(hostHolder.getUser().getId(), currentPage);
            ListIterator<Message> iterator = pageInfo.getList().listIterator();
            while (iterator.hasNext()) {
                Message next = iterator.next();
                ViewObject viewObject = new ViewObject();
                viewObject.set("fromHeadUrl", next.getFromUser().getHeadUrl());
                viewObject.set("fromName", sensitiveService.filter(HtmlUtils.htmlEscape(next.getFromUser().getName())));
                viewObject.set("createdDate", next.getCreatedDate());
                viewObject.set("content", next.getContent());
                viewObject.set("conversationId", next.getConversationId());
                viewObject.set("hasReadCount", next.getHasRead());
                viewObject.set("count", next.getId());
                viewObjects.add(viewObject);
            }
            model.addAttribute("vos", viewObjects);
            return "letter";
        } catch (Exception e) {
            LOGGER.error("站内信列表错误" + e.getMessage());
            e.printStackTrace();
            return "err";
        }
    }
}
