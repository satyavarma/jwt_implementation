package com.test.managers;

import com.test.daos.RedisDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class JWTManager {
    Logger logger = Logger.getLogger(JWTManager.class);

    @Autowired
    RedisDAO redisDAO;

    String secretKey = "secret";

    public Boolean login(String email, HttpServletRequest request, HttpServletResponse response)throws Exception{

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 6);
        Date expiration = cal.getTime();
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(expiration)
                .setIssuer("ved.com")
                .claim("sessionData", null)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        Cookie tokenCookie = new Cookie("JWT-Token", token);
        tokenCookie.setMaxAge(-1);
        tokenCookie.setDomain("localhost");
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        redisDAO.addToList(email,token);
        return true;
    }

    public Boolean verify(String email,HttpServletRequest request) throws Exception{
        Cookie cookie = null;
        if (request.getCookies() != null) {
            for (Cookie ck : request.getCookies()) {
                if (ck.getName().equals("JWT-Token")) {
                    cookie = ck;
                    break;
                }
            }
        }
        if(cookie == null){
            logger.info("cookie not found");
            return false;
        }
        String token = cookie.getValue();
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        }catch(Exception e){
            logger.info(e.getMessage());
            redisDAO.del(email);
            return false;
        }
        List<String> tokensList = redisDAO.getEntireList(email);
        int found = 0;
        for(String tokenStored: tokensList){
            if(tokenStored.equals(token)){
                found = 1;
            }
        }
        if(found == 1){
            return true;
        }
        return false;
    }

    public Boolean logout(String email, HttpServletRequest request, HttpServletResponse response) throws  Exception{
        Boolean verifyRes = verify(email,request);
        if(verifyRes == false){
            return false;
        }
        Cookie cookie = null;
        if (request.getCookies() != null) {
            for (Cookie ck : request.getCookies()) {
                if (ck.getName().equals("JWT-Token")) {
                    cookie = ck;
                    break;
                }
            }
        }
        if(cookie == null){
            logger.info("cookie not found");
            return false;
        }
        String token = cookie.getValue();
        redisDAO.removeFromList(email,token);
        Cookie tokenCookie = new Cookie("JWT-Token", "");
        tokenCookie.setMaxAge(-1);
        tokenCookie.setDomain("localhost");
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        return true;
    }

    public Boolean changePassword(String email, HttpServletRequest request, HttpServletResponse response) throws  Exception{
        Boolean verifyRes = verify(email,request);
        if(verifyRes == false){
            return false;
        }
        Cookie cookie = null;
        if (request.getCookies() != null) {
            for (Cookie ck : request.getCookies()) {
                if (ck.getName().equals("JWT-Token")) {
                    cookie = ck;
                    break;
                }
            }
        }
        if(cookie == null){
            logger.info("cookie not found");
            return false;
        }
        redisDAO.del(email);
        Cookie tokenCookie = new Cookie("JWT-Token", "");
        tokenCookie.setMaxAge(-1);
        tokenCookie.setDomain("localhost");
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        return true;
    }
}
