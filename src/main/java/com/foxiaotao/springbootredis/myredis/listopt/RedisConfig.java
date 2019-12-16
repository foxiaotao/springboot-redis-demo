package com.foxiaotao.springbootredis.myredis.listopt;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by BrightSun on 2016/10/28.
 */

@Configuration
public class RedisConfig {

    @Value("${spring.redis.defaultExpiration}")
    private Long defaultExpiration;
    @Value("${spring.redis.host}")
    private String masterHost;
    @Value("${spring.redis.port}")
    private int masterPort;
    @Value("${spring.redis.name}")
    private String masterName;

    @Value("${spring.redis.sentinel1.host:}")
    private String sentinel1Host;
    @Value("${spring.redis.sentinel1.port:1}")
    private int sentinel1port;
    @Value("${spring.redis.sentinel2.host:}")
    private String sentinel2Host;
    @Value("${spring.redis.sentinel2.port:1}")
    private int sentinel2port;
    @Value("${spring.redis.sentinel3.host:}")
    private String sentinel3Host;
    @Value("${spring.redis.sentinel3.port:1}")
    private int sentinel3port;


    private RedisConnectionFactory generateDevConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(masterHost);
        factory.setPort(masterPort);
        factory.setUsePool(true);
        factory.setConvertPipelineAndTxResults(true);
        JedisPoolConfig poolConfig = generatePoolConfig();
        factory.setPoolConfig(poolConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    private RedisConnectionFactory generateReleaseConnectionFactory() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        RedisNode master = new RedisNode(masterHost, masterPort);
        master.setName(masterName);
        Set<RedisNode> sentinels = new HashSet<>();
        RedisNode sentinel1 = new RedisNode(sentinel1Host, sentinel1port);
        RedisNode sentinel2 = new RedisNode(sentinel2Host, sentinel2port);
        RedisNode sentinel3 = new RedisNode(sentinel3Host, sentinel3port);
        sentinels.add(sentinel1);
        sentinels.add(sentinel2);
        sentinels.add(sentinel3);
        sentinelConfiguration.setMaster(master);
        sentinelConfiguration.setSentinels(sentinels);
        JedisPoolConfig poolConfig = generatePoolConfig();
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
        factory.setHostName(masterHost);
        factory.setPort(masterPort);
        factory.setTimeout(10000);
        factory.setUsePool(true);
        factory.setConvertPipelineAndTxResults(true);
        factory.afterPropertiesSet();
        return factory;
    }

    private JedisPoolConfig generatePoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(20);
        poolConfig.setMaxTotal(300);
        poolConfig.setMaxWaitMillis(5000);
        poolConfig.setTestOnBorrow(true);
        return poolConfig;
    }

    @Bean(name = "redisConnectionFactory")
    RedisConnectionFactory factory() {
        if (StringUtils.isBlank(masterName)) {
            return generateDevConnectionFactory();
        } else {
            return generateReleaseConnectionFactory();
        }
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        template.setEnableTransactionSupport(false);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jdkSerializationRedisSerializer);
        template.setDefaultSerializer(jdkSerializationRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean(name = "stringRedisTemplate")
    public RedisTemplate<String, String> stringRedisTemplate(
            RedisConnectionFactory factory) {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setEnableTransactionSupport(false);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.setDefaultSerializer(stringRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }

}


