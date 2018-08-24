package com.jza.controller;

import com.jza.async.EventModel;
import com.jza.async.EventProducer;
import com.jza.async.EventType;
import com.jza.model.Comment;
import com.jza.model.CommentType;
import com.jza.model.HostHolder;
import com.jza.service.CommentService;
import com.jza.service.QuestionService;
import com.jza.service.SensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/addComment",method = {RequestMethod.POST})
    @Transactional
    public String addComment(
            @RequestParam("questionId") Integer questionId,
            @RequestParam("content") String content,
            Model model
    ) {
        try {

            content = HtmlUtils.htmlEscape(sensitiveService.filter(content));
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(CommentType.QUESTION.ordinal());
            comment.setStatus(0);
            comment.setUserId(hostHolder.getUser().getId());
            commentService.addComment(comment);
            Integer count = commentService.getCount(questionId, CommentType.QUESTION.ordinal());
            questionService.updateCommentCount(count, questionId);

            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("添加评论错误！" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "err";
        }
        return "redirect:/question/" + String.valueOf(questionId);

    }
}
