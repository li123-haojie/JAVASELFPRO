package com.mszlu.blog.admin.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class Admin implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;
//    添加权限集合属性
    private List<Permission> permissions;

//将用户权限信息进行封装，在进行权限验证时会进行比较
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(permissions.size());
        for (Permission per:
             permissions) {
            authorities.add(new SimpleGrantedAuthority(per.getName()));
        }
        return authorities;
    }
//设置账户未过期，默认账户过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //设置账户未被锁定，默认账户被锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
//
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
//设置账号未被禁止，默认账号被禁止
    @Override
    public boolean isEnabled() {
        return true;
    }
}
