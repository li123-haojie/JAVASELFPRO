package com.mszlu.blog.admin.service;

import com.mszlu.blog.admin.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //登录的时候，会把username 传递到这里
        //通过username查询 admin表，如果 admin存在 将密码告诉spring security
        //如果不存在 返回null 认证失败了
        Admin admin = this.adminService.findAdminByUsername(username);
//        添加的代码27-34
        if (admin == null){
            //登录失败
            throw new AuthenticationServiceException("用户名或密码错误");
        }
        admin.setPermissions(adminService.findPermissionByAdminId(admin.getId()));
        return admin;
    }
}
