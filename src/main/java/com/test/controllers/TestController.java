package com.test.controllers;

import com.test.daos.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RedisDAO redisDAO;

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public Boolean isAvailabe(){
        return true;
    }

    @RequestMapping(value="/set/{key}/{value}", method=RequestMethod.GET)
    public String isRedisAvailable(@PathVariable("key") String key, @PathVariable("value") String value) throws Exception{
        return redisDAO.set(key, value);
    }
    @RequestMapping("get/{key}")
    public Object get(@PathVariable("key") String key) throws Exception{
        return redisDAO.get(key);
    }
    @RequestMapping("del/{key}")
    public Long del(@PathVariable("key") String key) throws Exception{
        return redisDAO.del(key);
    }
}
