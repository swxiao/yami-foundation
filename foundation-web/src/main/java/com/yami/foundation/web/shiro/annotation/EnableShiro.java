package com.yami.foundation.web.shiro.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.yami.foundation.web.shiro.ShiroConfiguration;

/**
 * @author kakashi
 * @since 2018年10月24日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ShiroConfiguration.class)
public @interface EnableShiro {

}
