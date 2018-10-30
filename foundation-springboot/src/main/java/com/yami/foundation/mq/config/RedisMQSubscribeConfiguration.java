package com.yami.foundation.mq.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.yami.foundation.mq.listener.MQMessageListenerAdapter;
import com.yami.foundation.mq.pubsub.Subscriber;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Configurable
public class RedisMQSubscribeConfiguration {

	@Bean
	RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory connectionFactory, List<Subscriber> subscribers) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setBeanName("redis-pubsub-mq");
		container.setConnectionFactory(connectionFactory);
		for (Subscriber subscriber : subscribers) {
			MQMessageListenerAdapter listener = new MQMessageListenerAdapter(subscriber, "subscribe");
			listener.afterPropertiesSet();
			container.addMessageListener(listener, new ChannelTopic(subscriber.getTopic()));
		}
		return container;
	}
}
