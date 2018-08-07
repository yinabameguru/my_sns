package com.jza.service;

import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public long like(Integer userId, Integer entityType, Integer entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        likeDislike(userId, likeKey, disLikeKey);
        return jedisAdapter.scard(likeKey) - jedisAdapter.scard(disLikeKey);
    }

    public long disLike(Integer userId, Integer entityType, Integer entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        likeDislike(userId, disLikeKey, likeKey);
        return jedisAdapter.scard(likeKey) - jedisAdapter.scard(disLikeKey);
    }

    private void likeDislike(Integer userId, String disLikeKey, String likeKey) {
        if (jedisAdapter.sismember(disLikeKey, String.valueOf(userId))) {
            jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        }else {
            jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
            if (jedisAdapter.sismember(likeKey,String.valueOf(userId)))
                jedisAdapter.srem(likeKey, String.valueOf(userId));
        }
    }

    public long likeCount(Integer entityType, Integer entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        long l1 = jedisAdapter.scard(likeKey);
        long l2 = jedisAdapter.scard(disLikeKey);
        return l1-l2;
    }

    public Integer liked(Integer entityType, Integer entityId, Integer userId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId)))
            return 1;
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(disLikeKey,String.valueOf(userId)))
            return -1;
        return 0;
    }
}
