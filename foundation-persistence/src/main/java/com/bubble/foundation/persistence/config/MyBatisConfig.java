/**
 * Copyright [2015-2017]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.bubble.foundation.persistence.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.bubble.foundation.common.entity.AbstractEntity;
import com.bubble.foundation.common.env.DigestEnvironment;
import com.github.abel533.mapperhelper.MapperInterceptor;

/**
 * 
 * @author xiaoshiwen<xiaoshiwen@zitopay.com>
 * @since 2017年1月16日
 */
@Configurable
public class MyBatisConfig implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Inject
	DigestEnvironment environment;

	@Value("${mybatis.entity.package}")
	private String entityBasePackage;

	@Value("${mybatis.mapper.resources}")
	private String mapperResources = "classpath*:config/mybatis/mapper/**/*Mapper.xml";

	@Value("${mybatis.mapper.dialect}")
	private String dialect = "MYSQL";

	@Value("${mybatis.mappers}")
	private String mappers = "com.github.abel533.mapper.Mapper";

	private DataSource dataSource;

	public DataSource getDataSource() throws SQLException {
		if (dataSource == null) {
			dataSource();
		}
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private Resource[] getResources(String packagePath) throws IOException {
		ResourcePatternResolver resourceResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		Resource[] resources = resourceResolver.getResources(packagePath);
		return resources;
	}

	/*
	 * @Bean(initMethod = "init", destroyMethod = "close")
	 * @PostConstruct
	 */
	public DataSource dataSource() throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(environment.getProperty("ds.url"));
		dataSource.setUsername(environment.getProperty("ds.username"));
		dataSource.setPassword(environment.getProperty("digest.ds.password"));
		dataSource.setInitialSize(environment.getRequiredProperty("ds.initialSize", Integer.class));
		dataSource.setMinIdle(environment.getRequiredProperty("ds.minIdle", Integer.class));
		dataSource.setMaxActive(environment.getRequiredProperty("ds.maxActive", Integer.class));
		dataSource.setMaxWait(environment.getRequiredProperty("ds.maxWait", Long.class));
		dataSource.setTimeBetweenEvictionRunsMillis(environment.getRequiredProperty("ds.timeBetweenEvictionRunsMillis", Long.class));
		dataSource.setMinEvictableIdleTimeMillis(environment.getRequiredProperty("ds.minEvictableIdleTimeMillis", Long.class));
		dataSource.setValidationQuery("SELECT 'x'");
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestOnReturn(true);
		dataSource.setPoolPreparedStatements(false);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		dataSource.setFilters("stat");
		this.setDataSource(dataSource);
		return dataSource;
	}

	@Bean("sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}

	public Configuration configuration() {
		Configuration configuration = new Configuration();
		configuration.setCacheEnabled(true);
		configuration.setLazyLoadingEnabled(true);
		configuration.setMultipleResultSetsEnabled(true);
		configuration.setUseColumnLabel(true);
		configuration.setUseGeneratedKeys(false);
		configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
		configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
		configuration.setDefaultStatementTimeout(10 * 60);
		configuration.setSafeRowBoundsEnabled(false);
		configuration.setMapUnderscoreToCamelCase(true);
		configuration.setLocalCacheScope(LocalCacheScope.SESSION);
		configuration.setJdbcTypeForNull(JdbcType.OTHER);
		configuration.setLazyLoadTriggerMethods(new HashSet<String>() {

			{
				add("equals");
				add("clone");
				add("hashCode");
				add("toString");
			}
		});
		configuration.setAggressiveLazyLoading(false);
		return configuration;
	}

	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(getDataSource());
		sessionFactory.setConfiguration(configuration());
		sessionFactory.setTypeAliasesPackage(entityBasePackage);
		sessionFactory.setTypeAliasesSuperType(AbstractEntity.class);
		sessionFactory.setMapperLocations(getResources(mapperResources));
		MapperInterceptor mapperInterceptor = new MapperInterceptor();
		Properties mapperProperties = new Properties();
		mapperProperties.setProperty("mappers", mappers);
		mapperProperties.setProperty("IDENTITY", dialect);
		// properties.setProperty("ORDER","AFTER");
		mapperInterceptor.setProperties(mapperProperties);
		// 分页直接使用通用mapper,不再需要pagehelper组件
		/*
		 * PageInterceptor pageInterceptor = new PageInterceptor(); Properties pageProperties = new Properties();
		 * //pageProperties.setProperty("dialect", dialect.toLowerCase());//PageAutoDialect自动根据jdbc url确定方言
		 * pageProperties.setProperty("rowBoundsWithCount", "true"); pageInterceptor.setProperties(pageProperties);
		 */
		sessionFactory.setPlugins(new Interceptor[] { mapperInterceptor });
		return sessionFactory.getObject();
	}

	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager() throws SQLException {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(getDataSource());
		return dataSourceTransactionManager;
	}

}
