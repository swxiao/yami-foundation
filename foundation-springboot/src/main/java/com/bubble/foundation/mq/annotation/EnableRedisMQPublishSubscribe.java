package com.bubble.foundation.mq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.bubble.foundation.config.redis.RedisConfiguration;
import com.bubble.foundation.mq.config.RedisMQPublishConfiguration;
import com.bubble.foundation.mq.config.RedisMQSubscribeConfiguration;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ RedisConfiguration.class, RedisMQSubscribeConfiguration.class, RedisMQPublishConfiguration.class })
public @interface EnableRedisMQPublishSubscribe {
}
