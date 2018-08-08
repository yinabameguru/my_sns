package com.jza.async;

import com.alibaba.fastjson.JSON;
import com.jza.utils.JedisAdapter;
import com.jza.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EventConsumer  implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> eventHandlers = applicationContext.getBeansOfType(EventHandler.class);
        if (eventHandlers != null) {
            for (EventHandler eventHandler : eventHandlers.values()) {
                List<EventType> supportEventTypes = eventHandler.getSupportEventTypes();
                for (EventType eventType : supportEventTypes) {
                    if (!config.containsKey(eventType))
                        config.put(eventType, new LinkedList<>());
                    config.get(eventType).add(eventHandler);
                }
            }
        }
        ExecutorService pool = Executors.newFixedThreadPool(9);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<String> list = jedisAdapter.brpop(RedisKeyUtil.getEventQueueKey());
                    for (String eventType : list) {
                        if (eventType.equals(RedisKeyUtil.getEventQueueKey()))
                            continue;
                        EventModel eventModel = JSON.parseObject(eventType, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            LOGGER.error("不能识别的事件");
                            continue;
                        }
                        for (EventHandler eventHandler : config.get(eventModel.getType())) {
                            eventHandler.doHandler(eventModel);
                        }
                    }
                }
            }
        };
        for (int i = 0; i < 2; i++) {
            pool.submit(runnable);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
