package com.asciily.foundation.mq.pubsub.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import com.asciily.foundation.mq.pubsub.Publisher;

/**
 * @author kakashi
 * @since 2018年10月18日
 */
public class RedisMQPublisher implements Publisher {

	private RedisTemplate<String, Object> redisTemplate;

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void publish(String topic, Object message) throws Exception {
		Assert.notNull(topic, "channel must not be null.");
		Assert.notNull(message, "message must not be null.");
		redisTemplate.convertAndSend(topic, message);
	}
}
