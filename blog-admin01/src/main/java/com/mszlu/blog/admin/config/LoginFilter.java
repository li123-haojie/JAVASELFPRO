package com.mszlu.blog.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mszlu.blog.admin.pojo.Admin;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @program: boot-demo
 * @description:
 * @packagename: com.mszlu.blog.admin.config
 * @author: 1522
 * @date: 2022-04-15 18:37
 **/

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired

    SessionRegistry sessionRegistry;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)||
                request.getContentType().contains(MediaType.APPLICATION_JSON_UTF8_VALUE))
        {
            try {
                Map<String,String> params = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String  username = params.get("username");
                System.out.println(username);
                username = username != null ? username : "";
                username = username.trim();
                String  password  = params.get("password");
                password = password != null ? password : "";
                Admin admin = new Admin();
                admin.setUsername(username);
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                this.setDetails(request, authRequest);
                sessionRegistry.registerNewSession(request.getSession(true).getId(), admin);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return super.attemptAuthentication(request,response);
    }
}
