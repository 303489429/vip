package com.vip.redis.vip.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @author wangzhilong
 * @date 2020/12/8
 */
@Configuration
public class MyTemplet {

    @Bean
    public StringRedisTemplate ooxx(RedisConnectionFactory factory) {
        final StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return redisTemplate;
    }

}
