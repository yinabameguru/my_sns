package com.jza.controller;

import com.jza.async.EventModel;
import com.jza.async.EventProducer;
import com.jza.async.EventType;
import com.jza.model.EntityType;
import com.jza.model.HostHolder;
import com.jza.model.User;
import com.jza.model.ViewObject;
import com.jza.service.CommentService;
import com.jza.service.FollowService;
import com.jza.service.QuestionService;
import com.jza.service.UserService;
import com.jza.utils.SnsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    QuestionService questionService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FollowController.class);

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.USER.ordinal(), userId);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.USER.ordinal(), userId));
        model.addAttribute("curUser", userService.findUser(userId));
        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(EntityType.USER.ordinal(), userId);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(EntityType.USER.ordinal(), userId));
        model.addAttribute("curUser", userService.findUser(userId));
        return "followees";
    }


    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.findUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(uid, EntityType.USER.ordinal()));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.USER.ordinal()));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, uid, EntityType.USER.ordinal()));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

    @RequestMapping(value = "/followUser", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followUser(
            @RequestParam("userId") int userId
    ) {
        try {
            if (hostHolder.getUser() == null) {
                return SnsUtils.getJSONString(999);
            }
            followService.follow(hostHolder.getUser().getId(), EntityType.USER.ordinal(), userId);
//            eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
//                    .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
//                    .setEntityType(EntityType.USER.ordinal()).setEntityOwnerId(userId));
            return SnsUtils.getJSONString(0);
        } catch (Exception e) {
            LOGGER.error("followUserErr" + e.getMessage());
            return SnsUtils.getJSONString(1);
        }
    }

    @RequestMapping(value = "/unfollowUser", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(
            @RequestParam("userId") int userId
    ) {
        try {
            if (hostHolder.getUser() == null) {
                return SnsUtils.getJSONString(999);
            }
            followService.unFollow(hostHolder.getUser().getId(), EntityType.USER.ordinal(), userId);
            return SnsUtils.getJSONString(0);
        } catch (Exception e) {
            LOGGER.error("unfollowUserErr" + e.getMessage());
            return SnsUtils.getJSONString(1);
        }
    }

    @RequestMapping(value = "/followQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(
            @RequestParam("questionId") int questionId
    ) {
        try {
            if (hostHolder.getUser() == null) {
                return SnsUtils.getJSONString(999);
            }
            followService.follow(hostHolder.getUser().getId(), EntityType.QUESTION.ordinal(), questionId);
            Map<String, Object> info = new HashMap<>();
            info.put("headUrl", hostHolder.getUser().getHeadUrl());
            info.put("name", hostHolder.getUser().getName());
            info.put("id", hostHolder.getUser().getId());
            info.put("count", followService.getFolloweeCount(EntityType.QUESTION.ordinal(), questionId));

//            eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
//                    .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
//                    .setEntityType(EntityType.QUESTION.ordinal()).setEntityOwnerId(questionService.getQuestionById(questionId).getUserId()));

            return SnsUtils.getJSONString(0, info);
        } catch (Exception e) {
            LOGGER.error("followQuestionErr" + e.getMessage());
            return SnsUtils.getJSONString(1);
        }
    }

    @RequestMapping(value = "/unfollowQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(
            @RequestParam("questionId") int questionId
    ) {
        try {
            if (hostHolder.getUser() == null) {
                return SnsUtils.getJSONString(999);
            }
            followService.unFollow(hostHolder.getUser().getId(), EntityType.QUESTION.ordinal(), questionId);
            Map<String, Object> info = new HashMap<>();
            info.put("id", hostHolder.getUser().getId());
            info.put("count", followService.getFolloweeCount(EntityType.QUESTION.ordinal(), questionId));
            return SnsUtils.getJSONString(0, info);
        } catch (Exception e) {
            LOGGER.error("unfollowQuestionErr" + e.getMessage());
            return SnsUtils.getJSONString(1);
        }
    }

}
