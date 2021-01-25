package com.test.controllers;

import com.test.daos.RedisDAO;
import com.test.managers.JWTManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class JWTController {
    Logger logger = Logger.getLogger(JWTController.class);

    @Autowired
    JWTManager jwtManager;

    @Autowired
    RedisDAO redisDAO;

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public Boolean login(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response)throws Exception {
        return jwtManager.login(email, request, response);
    }

    @RequestMapping(value="/verify", method = RequestMethod.GET)
    public Boolean verify(@RequestParam("email") String email, HttpServletRequest request) throws Exception{
        return jwtManager.verify(email,request);
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public Boolean logout(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws Exception{
        return jwtManager.logout(email,request, response);
    }

    @RequestMapping(value="/changePassword", method = RequestMethod.GET)
    public Boolean changePassword(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws Exception{
        return jwtManager.changePassword(email,request,response);
    }
}
