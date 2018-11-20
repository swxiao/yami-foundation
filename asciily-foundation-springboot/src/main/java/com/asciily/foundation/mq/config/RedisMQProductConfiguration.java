package com.asciily.foundation.mq.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import com.asciily.foundation.mq.prdcsm.impl.RedisMQProducter;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Configurable
public class RedisMQProductConfiguration {

	@Bean
	RedisMQProducter redisMQProducter(RedisTemplate<String, Object> redisTemplate) {
		RedisMQProducter producter = new RedisMQProducter();
		producter.setRedisTemplate(redisTemplate);
		return producter;
	}
}
