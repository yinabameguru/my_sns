package com.jza.controller;

import com.jza.model.EntityType;
import com.jza.model.Feed;
import com.jza.model.HostHolder;
import com.jza.service.FeedService;
import com.jza.service.FollowService;
import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        Set<String> set = jedisAdapter.zrevrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<String> feedIds = new ArrayList<>(set);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed != null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/getPullFeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            // 关注的人
            followees = followService.getFollowers(EntityType.USER.ordinal(), localUserId);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushPullFeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String pushPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        String timelineKey = RedisKeyUtil.getTimelineKey(localUserId);
        Set<String> keys = jedisAdapter.keys(timelineKey);
        if (!keys.contains(timelineKey)) {
            List<Integer> followees = new ArrayList<>();
            if (localUserId != 0) {
                // 关注的人
                followees = followService.getFollowers(EntityType.USER.ordinal(), localUserId);
            }
            List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
            model.addAttribute("feeds", feeds);

            HashSet<String> map = new HashSet<>();
            for (Feed feed : feeds) {
                Long l = feed.getCreatedDate().getTime();
                jedisAdapter.zadd(timelineKey, l.doubleValue(), String.valueOf(feed.getId()));
                //控制长度
            }
            jedisAdapter.expire(timelineKey, 60 * 60 * 24);
            return "feeds";
        } else {
            if (!timelineKey.equals("TIMELINE:0"))
                jedisAdapter.expire(timelineKey, 60 * 60 * 24);
            Set<String> set = jedisAdapter.zrevrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
            List<String> feedIds = new ArrayList<>(set);
            List<Feed> feeds = new ArrayList<>();
            for (String feedId : feedIds) {
                Feed feed = feedService.getById(Integer.parseInt(feedId));
                if (feed != null) {
                    feeds.add(feed);
                }
            }
            model.addAttribute("feeds", feeds);
            return "feeds";
        }
    }

}
