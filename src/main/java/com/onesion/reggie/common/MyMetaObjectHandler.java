package com.onesion.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * 对公共字段进行填充
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());

        setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);

        setFieldValByName("createUser", 1L, metaObject);
        setFieldValByName("updateUser", 1L, metaObject);

    }


    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        setFieldValByName("updateUser", 1L, metaObject);

    }
}
