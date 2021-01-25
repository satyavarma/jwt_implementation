package com.test.daos;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RedisDAO {

    Logger logger = Logger.getLogger(RedisDAO.class);

    private static JedisPool jedisPool;

    public RedisDAO(){

    }

    @PostConstruct
    public void init(){
        try{
            String host = "localhost";
            Integer port = 6379;
            logger.info("creating redis connection with " + host + ":" + port);
            if(jedisPool == null){
                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                jedisPoolConfig.setMaxIdle(30);
                jedisPoolConfig.setMinIdle(20);
                jedisPool = new JedisPool(jedisPoolConfig,host,port);
                if(jedisPool == null){
                    logger.info("connection not created");
                }else{
                    logger.info("connection created successfully");
                }
            }
        }catch(JedisConnectionException ex){
            logger.error("Unable to Establis Connection with Redis");
            throw ex;
        }
    }

    private Jedis getJedisClient(){
        return jedisPool.getResource();
    }

    private void closeJedisClient(Jedis jedisClient){
        if(jedisClient != null){
            jedisClient.disconnect();
            logger.info("jedisClient disconnected..");
        }
    }

    public String set(String key, String value) throws Exception {
        Jedis jedisClient = null;
        String resp = null;
        try {
            jedisClient = getJedisClient();
            resp = jedisClient.set(key, value);
        } catch (Exception e) {
            logger.error("Some error in set " + key + ", " + value + ", Error: " + e.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
        return resp;
    }

    public Long del(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        Long resp = null;
        try {
            jedisClient = getJedisClient();
            resp = jedisClient.del(key);
        } catch (Exception e) {
            logger.error("Some error in del " + key + ", Error: " + e.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
        return resp;
    }

    public String get(String key) throws Exception {
        logger.info("KEY__ is {}"+key);
        if (StringUtils.isEmpty(key)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        String resp = null;
        try {
            jedisClient = getJedisClient();
            resp = jedisClient.get(key);
        } catch (Exception e) {
            logger.error("Some error in get " + key + ", Error: " + e.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
        return resp;
    }

    public void addToList(String key, String value) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        try {
            jedisClient = getJedisClient();
            jedisClient.rpush(key, value);

        } catch (Exception ex) {
            logger.error("Error in adding value to list: " + value + " to key: " + key + ",  exception: " + ex.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
    }

    public List<String> getEntireList(String key) throws  Exception{
        if (StringUtils.isEmpty(key)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        List<String> list = null;
        try {
            jedisClient = getJedisClient();
            list = jedisClient.lrange(key,0,-1);
        } catch (Exception ex) {
            logger.error("  exception: " + ex.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
        return list;
    }

    public void removeFromList(String key, String value) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        try {
            jedisClient = getJedisClient();
            jedisClient.lrem(key,1,value);

        } catch (Exception ex) {
            logger.error("  exception: " + ex.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
    }

    public void removeEntireList(String key) throws Exception {
        logger.info("ddddddddddddddddddddd");
        logger.info(key);
        if (StringUtils.isEmpty(key)) {
            throw new Exception();
        }
        Jedis jedisClient = null;
        try {
            jedisClient = getJedisClient();
            jedisClient.ltrim(key, -1, 0);

        } catch (Exception ex) {
            logger.error("exception: " + ex.getMessage());
        } finally {
            closeJedisClient(jedisClient);
        }
    }
}
