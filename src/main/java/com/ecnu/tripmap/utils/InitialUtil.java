package com.ecnu.tripmap.utils;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ecnu.tripmap.mysql.mapper.UserMapper;
import com.ecnu.tripmap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class InitialUtil implements CommandLineRunner {
    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("init start");
        Integer count = new LambdaQueryChainWrapper<>(userMapper).count();
        for (int i = 1; i <= count; i++){
            userService.recommend(i);
        }
        System.out.println("end");
    }
}
