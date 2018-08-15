package com.jza.service;

import com.jza.async.EventModel;
import com.jza.async.EventProducer;
import com.jza.async.EventType;
import com.jza.model.Comment;
import com.jza.model.HostHolder;
import com.jza.model.Message;
import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import com.jza.utils.SnsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.List;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;

    public long like(Integer userId, Integer entityType, Integer entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        likeDislike(userId, entityId, likeKey, disLikeKey, "like");

        return jedisAdapter.scard(likeKey) - jedisAdapter.scard(disLikeKey);
    }

    public long disLike(Integer userId, Integer entityType, Integer entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);

        likeDislike(userId, entityId, disLikeKey, likeKey, "disLike");


        return jedisAdapter.scard(likeKey) - jedisAdapter.scard(disLikeKey);
    }

    private void likeDislike(Integer userId, Integer commentId, String key1, String key2,String flag1) {
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        if (jedisAdapter.sismember(key1, String.valueOf(userId))) {
            transaction.srem(key1, String.valueOf(userId));

            sendMessage(jedis, transaction, userId, commentId, flag1,"remove");

        }else {
            transaction.sadd(key1, String.valueOf(userId));
            if (jedisAdapter.sismember(key2,String.valueOf(userId)))
                transaction.srem(key2, String.valueOf(userId));
            sendMessage(jedis, transaction, userId, commentId, flag1,"add");

        }
    }

    private void sendMessage(Jedis jedis, Transaction transaction, Integer userId, Integer commentId, String flag1, String flag2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userService.findUser(userId).getName());
        if (flag1.equals("like") && flag2.equals("add"))
            stringBuilder.append("点赞了你的评论");
        else if (flag1.equals("like") && flag2.equals("remove"))
            stringBuilder.append("取消点赞了你的评论");
        else if (flag1.equals("disLike") && flag2.equals("add"))
            stringBuilder.append("踩了你的评论");
        else
            stringBuilder.append("取消踩了你的评论");
        stringBuilder.append("\r\n");
        stringBuilder.append("查看请点");
        stringBuilder.append("<a href=\"");
        stringBuilder.append(SnsUtils.PATH);
        stringBuilder.append("/question/");
        Comment comment = commentService.getComment(commentId);
        stringBuilder.append(comment.getEntityId());
        stringBuilder.append("\">这里");

        EventModel eventModel = new EventModel(EventType.MESSAGE);
        Message message = new Message();
        message.setHasRead(0);
        message.setConversationId(SnsUtils.MESSAGE_USER_ID, comment.getUserId());
        message.setContent(stringBuilder.toString());
        message.setCreatedDate(new Date());
        message.setFromId(SnsUtils.MESSAGE_USER_ID);
        message.setToId(comment.getUserId());
        eventModel.set("message", message);
        eventProducer.fireEvent(transaction, eventModel);
        List<Object> exec = jedisAdapter.exec(transaction, jedis);
        if (exec == null)
            throw new RuntimeException("踩赞错误");
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
