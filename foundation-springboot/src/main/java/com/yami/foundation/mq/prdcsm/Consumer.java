package com.yami.foundation.mq.prdcsm;

/**
 * @author kakashi
 * @since 2018年10月19日
 */
public interface Consumer {

	void consume(Object message) throws Exception;

	String getTopic();
}
