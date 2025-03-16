package com.onesion.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan  // 扫描过滤器注解（用在登录功能完善）
@EnableTransactionManagement  // 开启对事务的支持
@EnableCaching  // 开启Spring Cache注解方式是缓存功能 开启缓存
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功");
        log.info("后台地址：http://localhost:8080/backend/page/login/login.html");
        log.info("前端地址：http://localhost:8080/front/index.html");
    }

}
