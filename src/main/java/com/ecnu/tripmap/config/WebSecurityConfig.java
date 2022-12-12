package com.ecnu.tripmap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration   //告诉项目这是个配置文件早点加载
@EnableWebSecurity //以实现Web安全性
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {//基类是个牛逼的类包含了很多涉及安全要使用的方法比如下面个重写的方法！

    //下面这个方法简单的可以理解放开所有拦截！详细自己查！
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }
}
