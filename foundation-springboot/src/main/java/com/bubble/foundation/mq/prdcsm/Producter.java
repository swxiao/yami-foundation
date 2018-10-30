package com.bubble.foundation.mq.prdcsm;

/**
 * @author kakashi
 * @since 2018年10月19日
 */
public interface Producter {

	/**
	 * @param channel
	 * @param message
	 * @throws Exception
	 */
	void produce(String channel, Object message) throws Exception;
}
