package com.jza.controller;

import com.jza.model.EntityType;
import com.jza.model.HostHolder;
import com.jza.model.User;
import com.jza.model.ViewObject;
import com.jza.service.CommentService;
import com.jza.service.FollowService;
import com.jza.service.UserService;
import com.jza.utils.SnsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

    @RequestMapping(value = "/followUser/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public String followUser(
            @PathVariable("userId") int userId
    ) {
        try {
            followService.follow(hostHolder.getUser().getId(), EntityType.USER.ordinal(), userId);
            return SnsUtils.getJSONString(0);
        } catch (Exception e) {
            LOGGER.error("followErr" + e.getMessage());
            return SnsUtils.getJSONString(1);
        }
    }

}
