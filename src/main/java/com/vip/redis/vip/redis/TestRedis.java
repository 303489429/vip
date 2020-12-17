package com.vip.redis.vip.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public void test(){
        stringRedisTemplate.opsForValue().set("hello", "china");
        final Object hello = stringRedisTemplate.opsForValue().get("hello");
        System.out.println(hello);

        final HashOperations<String, String, Object> hash = stringRedisTemplate.opsForHash();
        hash.put("hash","name","sean");
        hash.put("hash","age","11");

        System.out.println(hash.entries("hash"));

        Person person = new Person("peter", 19);
        Jackson2HashMapper mapper = new Jackson2HashMapper(objectMapper,false);
        final Map<String, Object> map = mapper.toHash(person);
        hash.putAll("peter", map);


        final Map<String, Object> peter = hash.entries("peter");
        final Person person2 = objectMapper.convertValue(peter, Person.class);
        System.out.println(person2);
    }
    public void test1(){
        final RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
        connection.set("hello02".getBytes(), "vip".getBytes());
        byte[] bytes = connection.get("hello02".getBytes());
        System.out.println(new String(bytes));

    }


}