package com.asciily.foundation.mq.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;

import com.asciily.foundation.mq.container.PrdCsmRedisMessageListenerContainer;
import com.asciily.foundation.mq.listener.MQMessageListenerAdapter;
import com.asciily.foundation.mq.prdcsm.Consumer;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Configurable
public class RedisMQConsumeConfiguration {

	@Bean
	PrdCsmRedisMessageListenerContainer prdCsmRedisMessageListenerContainer(JedisConnectionFactory connectionFactory, List<Consumer> consumers) {
		PrdCsmRedisMessageListenerContainer container = new PrdCsmRedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setBeanName("redis-prdcsm-mq");
		for (Consumer consumer : consumers) {
			MQMessageListenerAdapter listener = new MQMessageListenerAdapter(consumer, "consume");
			listener.afterPropertiesSet();
			container.addMessageListener(listener, new ChannelTopic(consumer.getTopic()));
		}
		return container;
	}

}
