package com.onesion.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 创建一个redis配置类，其实不是必须的，但是建议来配置一下
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {  // 继承了这个类，这个类是spring data redis里面给我们提供的

    @Bean  // 继承之后，需要自己创建RedisTemplate对象，实际上不创建，框架也会创建RedisTemplate对象
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        //默认的Key序列化器为：JdkSerializationRedisSerializer 它指的是redis当中它的key的序列化器
        // 为什么自己创建呢？是因为这里需要自己设置一个序列化器，如果自己不设置，框架会默认设置一个
        redisTemplate.setKeySerializer(new StringRedisSerializer());  // 这里需要改一下为StringRedisSerialize这个序列器，

        // 为什么要改呢？其实是便于我们去观察，在图形界面观察，如果用默认的这个序列器，里面的值不好理解，很多斜线，看上去跟乱码似的，实际只是展现的形式
        // 所以为了方便我们查看，可以改造一下，使用StringRedisSerialize序列器，便于观察key
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

}
