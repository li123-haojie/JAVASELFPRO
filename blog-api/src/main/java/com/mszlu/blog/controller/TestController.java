package com.mszlu.blog.controller;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/type")
    public Result test01(){
        System.out.println("/types");
//        SysUser sysUser = UserThreadLocal.get();
//        System.out.println(sysUser);
        return Result.success("123123");
    }
    @GetMapping("/test")
    public Result test02(){
        return Result.success("test");
    }
}
