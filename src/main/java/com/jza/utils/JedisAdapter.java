package com.jza.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/1");
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            LOGGER.error("redis异常" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            LOGGER.error("redis异常" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            LOGGER.error("redis错误" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return 0;
    }

    public boolean sismember(String key,String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            LOGGER.error("redis错误" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return false;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            LOGGER.error("redis错误" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return 0;
    }

    public List<String> brpop(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(0, key);
        } catch (Exception e) {
            LOGGER.error("redis错误" + e.getMessage());
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return null;
    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            LOGGER.error("开启redis事务异常" + e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            LOGGER.error("执行redis事务异常" + e.getMessage());
        }finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    LOGGER.error("关闭redis事务异常" + ioe.getMessage());
                }
            }
            if (jedis != null)
                jedis.close();
        }
        return null;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        System.out.println(jedis.sadd("set1", "xx"));
        System.out.println(jedis.scard("set1"));
        System.out.println(jedis.sismember("set1", "xx"));
        System.out.println(jedis.smembers("set1"));
    }
}
