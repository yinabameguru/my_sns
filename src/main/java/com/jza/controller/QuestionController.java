package com.jza.controller;

import com.github.pagehelper.PageInfo;
import com.jza.model.*;
import com.jza.service.CommentService;
import com.jza.service.LikeService;
import com.jza.service.QuestionService;
import com.jza.service.SensitiveService;
import com.jza.utils.SnsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Controller
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(
        @RequestParam("title") String title,
        @RequestParam("content") String content
    ){
        try {
            if (hostHolder.getUser() == null)
                return SnsUtils.getJSONString(999);
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            question.setUserId(hostHolder.getUser().getId());
            if (questionService.addQuestion(question) > 0)
            return SnsUtils.getJSONString(0);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("增加题目失败" + e.getMessage());
        }
        return SnsUtils.getJSONString(1,"服务器错误");
    }

    @RequestMapping(value = "/question/{questionId}",method = {RequestMethod.GET})
    public String questionDetail(
            @PathVariable("questionId") Integer questionId,
            @RequestParam(value = "currentPage",required = false) Integer currentPage,
            Model model
    ){
        try {
            Question question = questionService.getQuestionById(questionId);
            model.addAttribute("question", question);
            if (currentPage == null)
                currentPage = 0;
            PageInfo<Comment> commentPageInfo = commentService.getComment(questionId, CommentType.QUESTION.ordinal(), currentPage);
            List<Comment> comments = commentPageInfo.getList();
            LinkedList<ViewObject> viewObjects = new LinkedList<>();
            ListIterator<Comment> iterator = comments.listIterator();
            while (iterator.hasNext()) {
                Comment next = iterator.next();
                ViewObject viewObject = new ViewObject();
                viewObject.set("content", next.getContent());
                viewObject.set("createdDate", next.getCreatedDate());
                viewObject.set("commentId", next.getId());
                viewObject.set("userId", next.getUser().getId());
                viewObject.set("userName", sensitiveService.filter(next.getUser().getName()));
                viewObject.set("headUrl", next.getUser().getHeadUrl());
                viewObject.set("likeCount",likeService.likeCount(CommentType.COMMENT.ordinal(),next.getId()));
                viewObject.set("liked",likeService.liked(CommentType.COMMENT.ordinal(),next.getId(),hostHolder.getUser().getId()));
//                viewObject.set("commentCount", "");
                viewObjects.add(viewObject);
            }
            model.addAttribute("vos",viewObjects);
            return "detail";
        } catch (Exception e) {
            logger.error("问题详情错误！" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "err";
        }
    }

}
