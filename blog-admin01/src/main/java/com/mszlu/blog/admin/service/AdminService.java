package com.mszlu.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.admin.mapper.AdminMapper;
import com.mszlu.blog.admin.pojo.Admin;
import com.mszlu.blog.admin.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public Admin findAdminByUsername(String username){
//      修改的代码
//        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Admin::getUsername,username);
//        queryWrapper.last("limit 1");
        return  adminMapper.selectOneAdmin(username);
    }

    public List<Permission> findPermissionByAdminId(Long adminId) {
        //SELECT * FROM `ms_permission` where id in (select permission_id from ms_admin_permission where admin_id=1)
        return adminMapper.findPermissionByAdminId(adminId);
    }
}
