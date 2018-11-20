package com.asciily.foundation.web.shiro;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * @author kakashi
 * @since 2018年8月13日
 */
@Configurable
public class ShiroFilterBeanConfig {

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		ShiroConfiguration.filterBean = shiroFilter;
		return shiroFilter;
	}
}
