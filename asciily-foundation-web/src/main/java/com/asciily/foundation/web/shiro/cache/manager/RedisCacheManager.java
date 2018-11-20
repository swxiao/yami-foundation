package com.asciily.foundation.web.shiro.cache.manager;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import com.asciily.foundation.web.shiro.cache.RedisCache;

/**
 * @author kakashi
 * @since 2018年8月11日
 */
public class RedisCacheManager<K, V> implements CacheManager {

	private RedisTemplate<Object, Object> redisTemplate = null;

	public RedisCacheManager(RedisTemplate redisTemplate) {
		JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
		redisTemplate.setValueSerializer(valueSerializer);
		redisTemplate.setHashValueSerializer(valueSerializer);
		this.redisTemplate = redisTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.CacheManager#getCache(java.lang.String)
	 */
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		return (Cache<K, V>) new RedisCache(redisTemplate);
	}

}
