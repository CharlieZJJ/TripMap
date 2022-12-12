package com.ecnu.tripmap.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DatConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //设置属性值
        this.setFieldValByName("userCreateTime", new Date(), metaObject);
        this.setFieldValByName("postPublishTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }
}
