package com.bubble.foundation.mq.pubsub;

/**
 * @author kakashi
 * @since 2018年10月18日
 */
public interface Publisher {

	/**
	 * @param topic
	 * @param message
	 * @throws Exception
	 */
	void publish(String topic, Object message) throws Exception;
}
