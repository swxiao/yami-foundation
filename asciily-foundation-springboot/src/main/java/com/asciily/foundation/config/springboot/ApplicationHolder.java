package com.asciily.foundation.config.springboot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author kakashi
 * @since 2018年8月11日
 */
public class ApplicationHolder implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationHolder.class);

	private static ConfigurableApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}

	private static final Map<String, Object> specifiedBeanMap = new ConcurrentHashMap<String, Object>();

	public static Map<String, Object> getSpecifiedBeanMap() {
		return specifiedBeanMap;
	}

	public static <T> T getSpecifiedBean(String key) {
		return (T) specifiedBeanMap.get(key);
	}

	/**
	 * 从spring上下文获取bean
	 * 
	 * @author xiaosw 2014-9-2
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	/**
	 * 从spring上下文获取beanMap
	 * 
	 * @param clz
	 * @return
	 */
	public static Map<String, Object> getBeanMapWithAnnotation(Class<? extends Annotation> clz) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		if (applicationContext != null) {
			Map<String, Object> proxyMap = applicationContext.getBeansWithAnnotation(clz);
			if (proxyMap != null && proxyMap.size() > 0) {
				for (Iterator<String> iterator = proxyMap.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					Object obj = getTargetObject(proxyMap.get(key));
					objMap.put(key, obj);
				}
			}
		}
		return objMap;
	}

	private static Object getTargetObject(Object proxy) {
		try {
			if (!AopUtils.isAopProxy(proxy)) {// 不是代理对象
				return proxy;
			}
			if (AopUtils.isCglibProxy(proxy)) {// cglib代理对象
				return getCglibProxyTargetObject(proxy);
			}
			if (AopUtils.isJdkDynamicProxy(proxy)) {// jdk动态代理
				return getJdkDynamicProxyTargetObject(proxy);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取cglib代理目标对象
	 * 
	 * @param proxy
	 * @return
	 * @throws Exception
	 */
	private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
		try {
			Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
			field.setAccessible(true);
			Object dynamicAdvisedInterceptor = field.get(proxy);
			Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
			advised.setAccessible(true);
			return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("The method of object with cglib can't read", e);
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Find filed in object of cglib has error", e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Inject parameter to object of cglib has error", e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Access object of cglib has error", e);
		}
	}

	/**
	 * 获取JDK动态代理目标对象
	 * 
	 * @param proxy
	 * @return
	 * @throws Exception
	 */
	private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
		try {
			Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
			field.setAccessible(true);
			AopProxy aopProxy = (AopProxy) field.get(proxy);
			Field advised = aopProxy.getClass().getDeclaredField("advised");
			advised.setAccessible(true);
			Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
			return target;
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("The method of object with jdk dynamic can't read", e);
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Find filed in object of jdk dynamic has error", e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Inject parameter to object of jdk dynamic has error", e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Access object of jdk dynamic has error", e);
		}

	}

}
