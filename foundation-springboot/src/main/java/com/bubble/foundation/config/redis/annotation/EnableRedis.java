package com.bubble.foundation.config.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.bubble.foundation.config.redis.RedisConfiguration;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RedisConfiguration.class)
public @interface EnableRedis {
}
