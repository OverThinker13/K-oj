package cn.overthinker.common.redis.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    public RedisTemplate redisTemplate;

    // ============================ 操作 key ============================

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在 false 不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    // ============================ 操作 String 类型 ============================

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key   缓存键值
     * @param clazz 目标类型
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key, Class<T> clazz) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        T t = operation.get(key);
        if (t instanceof String) {
            return t;
        }
        return JSON.parseObject(String.valueOf(t), clazz);
    }

    // ============================ 操作 List 结构 ============================

    /**
     * 获取 list 中存储数据数量
     */
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取 list 中指定范围数据
     */
    public <T> List<T> getCacheListByRange(final String key, long start, long end, Class<T> clazz) {
        List range = redisTemplate.opsForList().range(key, start, end);
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(range), clazz);
    }

    /**
     * 底层使用 list 结构存储数据（尾插 批量插入）
     */
    public <T> Long rightPushAll(final String key, Collection<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list);
    }

    /**
     * 底层使用 list 结构存储数据（头插）
     */
    public <T> Long leftPushForList(final String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 底层使用 list 结构，删除指定数据
     */
    public <T> Long removeForList(final String key, T value) {
        return redisTemplate.opsForList().remove(key, 1L, value);
    }

    // ============================ 操作 Hash 类型 ============================
    public <T> T getCacheMapValue(final String key, final String hKey, Class<T> clazz) {
        Object cacheMapValue = redisTemplate.opsForHash().get(key, hKey);
        if (cacheMapValue != null) {
            return JSON.parseObject(String.valueOf(cacheMapValue), clazz);
        }
        return null;
    }

    /**
     * 获取多个 Hash 中的数据
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys, Class<T> clazz) {
        List list = redisTemplate.opsForHash().multiGet(key, hKeys);
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            result.add(JSON.parseObject(JSON.toJSONString(item), clazz));
        }
        return result;
    }

    /**
     * 往 Hash 中存入数据
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 缓存 Map
     */
    public <K, T> void setCacheMap(final String key, final Map<K, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    public Long deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey);
    }
}