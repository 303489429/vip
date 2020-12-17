package com.vip.redis.vip;

import com.vip.redis.vip.redis.TestRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

//@SpringBootApplication
public class VipApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext ctx = SpringApplication.run(VipApplication.class, args);
        final TestRedis testRedis = ctx.getBean(TestRedis.class);
        testRedis.test();
        testRedis.test1();

    }

}
