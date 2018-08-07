package com.jza.controller;

import com.jza.model.Comment;
import com.jza.model.CommentType;
import com.jza.model.HostHolder;
import com.jza.service.CommentService;
import com.jza.service.LikeService;
import com.jza.utils.SnsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;

    @RequestMapping(value = "/like",method = {RequestMethod.POST})
    @ResponseBody
    public String like(
            @RequestParam("commentId") Integer commentId
    ) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), CommentType.COMMENT.ordinal(), commentId);
        return SnsUtils.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = "/dislike",method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(
            @RequestParam("commentId") Integer commentId
    ) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), CommentType.COMMENT.ordinal(), commentId);
        return SnsUtils.getJSONString(0, String.valueOf(likeCount));
    }
}
