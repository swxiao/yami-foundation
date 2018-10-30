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
package com.yami.foundation.config.redis;

import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

import com.yami.foundation.common.env.DigestEnvironment;
import com.yami.foundation.common.util.StringUtil;
import com.yami.foundation.config.redis.serialize.JacksonSerializer;

/**
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年5月3日
 */
@Configurable
public class RedisConfiguration {

	@Inject
	DigestEnvironment environment;

	@Value("${redis.maxTotal}")
	private int maxTotal;

	@Value("${redis.maxIdle}")
	private int maxIdle;

	@Value("${redis.minIdle}")
	private int minIdle;

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		return jedisPoolConfig;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, RedisClusterConfiguration redisClusterConfiguration) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		jedisConnectionFactory.setUsePool(true);
		jedisConnectionFactory.afterPropertiesSet();
		return jedisConnectionFactory;
	}

	@Bean
	public RedisClusterConfiguration redisClusterConfiguration() {
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
		ArrayList<RedisNode> redisNodeList = new ArrayList<>();
		int i = 1;
		while (true) {
			String host = environment.getProperty("redis.node" + i + ".host");
			String port = environment.getProperty("redis.node" + i + ".port");
			if (StringUtil.isEmpty(host) || StringUtil.isEmpty(port) || !StringUtil.isNumeric(port)) {
				break;
			}
			i++;
			RedisNode node = new RedisNode(host, Integer.parseInt(port));
			redisNodeList.add(node);
		}
		redisClusterConfiguration.setClusterNodes(redisNodeList);
		return redisClusterConfiguration;
	}

	@Bean
	public RedisTemplate redisTemplate(JedisConnectionFactory connectionFactory) {
		// KryoRedisSerializer<Object> valueSerializer = new KryoRedisSerializer<Object>(Object.class);
		// GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

		StringRedisSerializer stringSerializer = new StringRedisSerializer(Charset.forName("UTF-8"));
		JacksonSerializer valueSerializer = new JacksonSerializer();

		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setDefaultSerializer(valueSerializer);
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(valueSerializer);
		redisTemplate.setHashValueSerializer(valueSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
