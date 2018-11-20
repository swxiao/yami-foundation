package com.asciily.foundation.config.springboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.asciily.foundation.config.springboot.SpringBootApplicationConfiguration;

/**
 * @author kakashi
 * @since 2018年10月24日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SpringBootApplicationConfiguration.class)
public @interface EnableSpringBoot {

}
