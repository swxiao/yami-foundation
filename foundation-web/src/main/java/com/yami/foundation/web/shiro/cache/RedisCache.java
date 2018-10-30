package com.yami.foundation.web.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/**
 * @author kakashi
 * @since 2018年8月11日
 */
public class RedisCache implements Cache<Object, Object> {

	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

	public RedisTemplate<Object, Object> redisTemplate;

	private final String KEY_PREFIX = "shiro-redis-cache";

	public RedisCache(RedisTemplate<Object, Object> redisTemplate) {
		JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
		redisTemplate.setValueSerializer(valueSerializer);
		redisTemplate.setHashValueSerializer(valueSerializer);
		this.redisTemplate = redisTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) throws CacheException {
		logger.debug("从redis获取session,key:{}", key);
		return redisTemplate.opsForValue().get(KEY_PREFIX + key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value) throws CacheException {
		long timeout = 0;
		if (value instanceof SimpleSession) {
			timeout = ((SimpleSession) value).getTimeout();
		}
		logger.debug("更新session到redis,key:{},  value:{}", key, value);
		redisTemplate.opsForValue().set(KEY_PREFIX + key, value);
		redisTemplate.expire(KEY_PREFIX + key, (timeout / 1000), TimeUnit.SECONDS);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key) throws CacheException {
		Object v = get(key);
		if (v == null) {
			return null;
		}
		logger.debug("从redis删除session,key:{}", key);
		redisTemplate.delete(KEY_PREFIX + key);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#clear()
	 */
	@Override
	public void clear() throws CacheException {
		Set<Object> keys = keys();
		logger.debug("从redis清除所有session,keys:[{}]", keys);
		redisTemplate.delete(keys);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#size()
	 */
	@Override
	public int size() {
		return keys().size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#keys()
	 */
	@Override
	public Set<Object> keys() {
		Set<Object> keys = redisTemplate.keys(KEY_PREFIX + "*");
		logger.debug("从redis获取所有session,keys:[{}]", keys);
		return keys;
	}

	@Override
	public Collection<Object> values() {
		Set keys = this.keys();
		List<Object> values = new ArrayList<Object>();
		for (Object key : keys) {
			values.add(redisTemplate.opsForValue().get(key));
		}
		logger.debug("从redis获取所有session,values:[{}]", values);
		return values;
	}

}
