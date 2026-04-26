package cn.overthinker.common.redis.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 作用：自定义 RedisTemplate 的序列化方式。
 * <p>
 * 背景知识：
 * Spring Boot 默认提供的 RedisTemplate<Object, Object> 使用 JDK 原生序列化（把对象变成二进制字节流），
 * 存进 Redis 后的内容是乱码，既难以阅读，也与其他语言不兼容。
 * 本配置将序列化方式改为：
 * - key   → 普通字符串（StringRedisSerializer），可读性好
 * - value → JSON 字符串（JsonRedisSerializer），可读性好且跨语言兼容
 * <p>
 * 继承 CachingConfigurerSupport：
 * 允许我们自定义 Spring Cache（@Cacheable 等注解）底层使用的缓存管理器和 key 生成策略，
 * 当前类没有覆写这些方法，使用默认行为即可，继承本身不影响功能。
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 向 Spring 容器注册一个自定义的 RedisTemplate Bean。
     *
     * @param connectionFactory Spring Boot 自动配置好的 Redis 连接工厂，
     *                          连接地址、端口、密码等均来自 application.yml 中的 spring.redis.* 配置。
     * @return 配置好序列化方式的 RedisTemplate 实例
     * <p>
     * RedisTemplate 是操作 Redis 的核心工具类，类似 JDBC 的 JdbcTemplate，
     * 提供 opsForValue()、opsForHash()、opsForList() 等方法来操作不同数据结构。
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // 创建 RedisTemplate 实例，泛型 <Object, Object> 表示 key 和 value 都可以是任意类型
        RedisTemplate<Object, Object> template = new RedisTemplate<>();

        // 绑定 Redis 连接工厂，Template 通过它与 Redis 服务器建立连接
        template.setConnectionFactory(connectionFactory);

        // 创建 JSON 序列化器（基于 fastjson2 实现）
        // 作用：把 Java 对象 <-> JSON 字符串 <-> byte[] 三者互转，存入 Redis 时是可读的 JSON 格式
        JsonRedisSerializer serializer = new JsonRedisSerializer(Object.class);

        // ---- String 类型（opsForValue）的序列化配置 ----

        // key 使用 StringRedisSerializer：直接把字符串编码为 UTF-8 字节，保证 key 可读（如 "user:1001"）
        template.setKeySerializer(new StringRedisSerializer());

        // value 使用 JSON 序列化器：把任意 Java 对象序列化为 JSON 字符串再存入 Redis
        template.setValueSerializer(serializer);

        // ---- Hash 类型（opsForHash）的序列化配置 ----
        // Redis Hash 结构：key -> { field1: value1, field2: value2, ... }
        // Hash 的外层 key 和内层 field 都用 StringRedisSerializer，保持可读性
        template.setHashKeySerializer(new StringRedisSerializer());

        // Hash 的 field 对应的 value 同样用 JSON 序列化器
        template.setHashValueSerializer(serializer);

        // 完成所有属性设置后，手动触发初始化（校验必要属性是否已设置，如 connectionFactory）
        // 相当于通知 Template："配置已完毕，可以正式使用了"
        template.afterPropertiesSet();

        return template;
    }
}