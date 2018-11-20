package com.asciily.foundation.mq.prdcsm.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import com.asciily.foundation.mq.prdcsm.Producter;

/**
 * @author kakashi
 * @since 2018年10月19日
 */
public class RedisMQProducter implements Producter {

	private RedisTemplate<String, Object> redisTemplate;

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void produce(String channel, Object message) throws Exception {
		Assert.notNull(channel, "channel must not be null.");
		Assert.notNull(message, "message must not be null.");
		long result = redisTemplate.opsForList().leftPush(channel, message);
	}

}
