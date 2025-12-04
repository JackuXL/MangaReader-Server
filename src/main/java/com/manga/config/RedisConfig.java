package com.manga.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存配置 - 优化支持 3000+ PV
 * 
 * 缓存策略：
 * - mangaList: 漫画列表（1小时）
 * - mangaDetail: 漫画详情（2小时）
 * - mangaSearch: 搜索结果（1小时）
 * - chapterList: 章节列表（2小时）
 * - recommended: 推荐内容（30分钟）
 * - tags: 标签列表（1小时）
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 创建配置好的 ObjectMapper
     * 支持 Java 8 日期时间和安全的多态类型处理
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册 JavaTimeModule 支持 LocalDateTime 等 Java 8 日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期写为时间戳，使用 ISO-8601 格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 使用安全的类型验证器，只允许特定包下的类
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType("com.manga.")
                .allowIfSubType("org.springframework.data.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.lang.")
                .allowIfSubType("java.time.")
                .build();
        
        // 使用 NON_FINAL 避免为 String 等 final 类添加类型信息
        objectMapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        
        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());
        
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());
        
        // 默认缓存配置：1小时
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues()
                .prefixCacheNameWith("manga:");

        // 针对不同业务场景配置不同的 TTL
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 搜索结果：1小时（高频访问，中等时效性）
        cacheConfigurations.put("mangaSearch", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 漫画列表：1小时（高频访问）
        cacheConfigurations.put("mangaList", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 漫画详情：2小时（访问频繁但更新较少）
        cacheConfigurations.put("mangaDetail", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // 章节列表：2小时（基本不变）
        cacheConfigurations.put("chapterList", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // 推荐内容：30分钟（需要更频繁更新）
        cacheConfigurations.put("recommended", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 精选/热门：30分钟
        cacheConfigurations.put("featured", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 用户收藏：10分钟（个性化内容，较短时效）
        cacheConfigurations.put("favorites", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // 标签列表：1小时
        cacheConfigurations.put("tags", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}


