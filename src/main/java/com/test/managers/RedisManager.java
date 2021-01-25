package com.test.managers;

import com.test.daos.RedisDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisManager {

    Logger logger = Logger.getLogger(RedisManager.class);

    @Autowired
    RedisDAO redisDAO;


}
