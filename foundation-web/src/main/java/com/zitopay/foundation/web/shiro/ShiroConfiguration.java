package com.zitopay.foundation.web.shiro;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.bubble.foundation.common.util.BeanUtil;
import com.bubble.foundation.config.redis.RedisConfiguration;
import com.zitopay.foundation.web.shiro.cache.manager.RedisCacheManager;
import com.zitopay.foundation.web.shiro.session.CachingShiroSessionDao;
import com.zitopay.foundation.web.shiro.session.RedisShiroSessionManager;

/**
 * @author kakashi
 * @since 2018年8月11日
 */
@Import({ ShiroFilterBeanConfig.class, RedisConfiguration.class, ApplicationHolder.class })
@Configurable
public class ShiroConfiguration implements InitializingBean {

	@Inject
	private RedisTemplate redisTemplate;

	@Value("${shiro.realms}")
	private String shiroRealms;

	@Value("${shiro.login.url}")
	private String loginUrl;

	@Value("${shiro.success.url}")
	private String successUrl;

	@Value("${shiro.unauthorized.url}")
	private String unauthorizedUrl;

	@Value("${shiro.filter.chain.definition}")
	private String filterChainDefinition;

	@Value("${shiro.session.timeout}")
	private int globalSessionTimeout;

	@Value("${shiro.session.validation.interval}")
	private int sessionValidationInterval;

	static ShiroFilterFactoryBean filterBean;

	private static CacheManager cacheManager;

	public DefaultWebSecurityManager defaultWebSecurityManager() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		final String[] realms = shiroRealms.split(",");
		manager.setCacheManager(cacheManager());
		manager.setSessionManager(sessionManager());
		manager.setRealms(new ArrayList<Realm>() {

			{
				for (int i = 0; i < realms.length; i++) {
					AuthorizingRealm realm = (AuthorizingRealm) ApplicationHolder.getBean(realms[i]);
					realm.setCachingEnabled(true);
					realm.setAuthenticationCachingEnabled(true);
					realm.setAuthenticationCacheName("authenticationCache");
					realm.setAuthorizationCachingEnabled(true);
					realm.setAuthorizationCacheName("authorizationCache");
					add(realm);
				}

			}
		});
		return manager;
	}

	private CacheManager cacheManager() {
		cacheManager = new RedisCacheManager<>(redisTemplate);
		return cacheManager;
	}

	private SessionManager sessionManager() {
		RedisShiroSessionManager sessionManager = new RedisShiroSessionManager();
		CachingShiroSessionDao sessionDao = new CachingShiroSessionDao();
		sessionDao.setCacheManager(cacheManager);
		sessionManager.setSessionDAO(sessionDao);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setCacheManager(cacheManager);
		sessionManager.setGlobalSessionTimeout(globalSessionTimeout);
		sessionManager.setSessionValidationInterval(sessionValidationInterval);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.getSessionIdCookie().setName("jscookieid");// session避免被容器串改
		return sessionManager;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		filterBean.setLoginUrl(loginUrl);
		filterBean.setSuccessUrl(successUrl);
		filterBean.setUnauthorizedUrl(unauthorizedUrl);
		filterBean.setSecurityManager(defaultWebSecurityManager());
		filterBean.setFilterChainDefinitionMap(BeanUtil.json2Bean(filterChainDefinition, Map.class));
	}
}
