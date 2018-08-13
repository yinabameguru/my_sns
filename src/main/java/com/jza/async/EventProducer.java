package com.jza.async;

import com.alibaba.fastjson.JSONObject;
import com.jza.service.QuestionService;
import com.jza.service.UserService;
import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Transaction;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventModel.class);

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(RedisKeyUtil.getEventQueueKey(), json);
            return true;
        } catch (Exception e) {
            LOGGER.error("事件产生错误" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean fireEvent(Transaction transaction, EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            transaction.lpush(RedisKeyUtil.getEventQueueKey(), json);
            return true;
        } catch (Exception e) {
            LOGGER.error("事件产生错误" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
