package com.jza.service;

import com.jza.model.EntityType;
import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int curUserId, int entityType, int entityId) {
        String followKey = RedisKeyUtil.getFollowKey(entityType, curUserId);
        String followedKey = RedisKeyUtil.getFollowedKey(entityType, entityId);
        Date date = new Date();
        Long l = date.getTime();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        transaction.zadd(followKey, l.doubleValue(), String.valueOf(entityId));
        transaction.zadd(followedKey, l.doubleValue(), String.valueOf(curUserId));
        jedisAdapter.exec(transaction, jedis);
        return true;
    }

    public boolean unFollow(int curUserId, int entityType, int entityId) {
        String followKey = RedisKeyUtil.getFollowKey(entityType, curUserId);
        String followedKey = RedisKeyUtil.getFollowedKey(entityType, entityId);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        transaction.zrem(followKey, String.valueOf(entityId));
        transaction.zrem(followedKey, String.valueOf(curUserId));
        jedisAdapter.exec(transaction, jedis);
        return true;
    }

    public List<Integer> getFollowers(int entityType, int curUserId) {
        String followKey = RedisKeyUtil.getFollowKey(entityType, curUserId);
        return getIdsFromSet(jedisAdapter.zrevrange(followKey, 0, 8));
    }

    public List<Integer> getFollowees(int entityType, int entityId) {
        String followedKey = RedisKeyUtil.getFollowedKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followedKey, 0, 8));
    }

    public long getFollowerCount(int entityType, int curUserId) {
        String followKey = RedisKeyUtil.getFollowKey(entityType, curUserId);
        return jedisAdapter.zcard(followKey);
    }

    public long getFolloweeCount(int entityType, int entityId) {
        String followedKey = RedisKeyUtil.getFollowedKey(entityType, entityId);
        return jedisAdapter.zcard(followedKey);
    }

    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


    public boolean isFollower(int curUserId, Integer entityId, int entityType) {
        String followKey = RedisKeyUtil.getFollowKey(entityType, curUserId);
        return jedisAdapter.zscore(followKey, String.valueOf(entityId)) != null;
    }
}
