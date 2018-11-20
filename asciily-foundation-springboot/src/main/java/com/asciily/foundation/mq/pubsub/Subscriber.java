package com.asciily.foundation.mq.pubsub;

/**
 * @author kakashi
 * @since 2018年10月18日
 */
public interface Subscriber {

	void subscribe(Object message) throws Exception;

	String getTopic();
}
