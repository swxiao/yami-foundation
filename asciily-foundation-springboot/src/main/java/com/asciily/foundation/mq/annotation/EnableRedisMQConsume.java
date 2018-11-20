package com.asciily.foundation.mq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.asciily.foundation.config.redis.RedisConfiguration;
import com.asciily.foundation.mq.config.RedisMQConsumeConfiguration;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ RedisConfiguration.class, RedisMQConsumeConfiguration.class })
public @interface EnableRedisMQConsume {
}
