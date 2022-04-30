package com.mszlu.blog.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mszlu.blog.admin.pojo.Admin;
import com.mszlu.blog.admin.service.SecurityUserService;
import com.mszlu.blog.admin.vo.Result;
import com.sun.deploy.security.BlockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    添加代码：使用自定义的用户认证信息
    @Autowired
    SecurityUserService securityUserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    public static void main(String[] args) {
        //加密策略 MD5 不安全 彩虹表  MD5 加盐
        String mszlu = new BCryptPasswordEncoder().encode("12345");
        System.out.println(mszlu);
    }
//添加代码
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(securityUserService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
//    创建LoginFilter对象
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler(((request, response, authentication) -> {
            response.setContentType("application/json;charset=utf-8");
            Admin admin = (Admin) authentication.getPrincipal();
            admin.setPassword(null);
//            Result result = new Result(true,200,"登录成功",admin);
//            PrintWriter out = response.getWriter();
//            out.write(new ObjectMapper().writeValueAsString(result));
//            out.flush();
//            out.close();
            response.sendRedirect("/pages/main.html");
        }));
        loginFilter.setAuthenticationFailureHandler(((request, response, exception) -> {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            Result result = new Result(300);
            if (exception instanceof LockedException){
                result.setMsg("账户被锁定，请联系管理员");
            }
            else if (exception instanceof CredentialsExpiredException){
                result.setMsg("账户过期，请联系管理员");
            }else {
                result.setMsg(exception.getMessage());
            }

            writer.write(new ObjectMapper().writeValueAsString(result));
            writer.flush();
            writer.close();
        }));
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);
        return loginFilter;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests() //开启登录认证
//                .antMatchers("/user/findAll").hasRole("admin") //访问接口需要admin的角色
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/plugins/**").permitAll()
                .antMatchers("/admin/**").access("@authService.auth(request,authentication)") //自定义service 来去实现实时的权限认证
                .antMatchers("/pages/**").authenticated()
                .and().formLogin()
                .loginPage("/login.html") //自定义的登录页面
                .loginProcessingUrl("/login") //登录处理接口
                .usernameParameter("username") //定义登录时的用户名的key 默认为username
                .passwordParameter("password") //定义登录时的密码key，默认是password
                .defaultSuccessUrl("/pages/main.html")
                .failureUrl("/login.html")
                .permitAll() //通过 不拦截，更加前面配的路径决定，这是指和登录表单相关的接口 都通过
                .and().logout() //退出登录配置
                .logoutUrl("/logout") //退出登录接口
                .logoutSuccessUrl("/login.html")
                .permitAll() //退出登录的接口放行
                .and()
                .httpBasic()
                .and()
                .csrf().disable() //csrf关闭 如果自定义登录 需要关闭
                .headers().frameOptions().sameOrigin();// 支持iframe页面嵌套
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
