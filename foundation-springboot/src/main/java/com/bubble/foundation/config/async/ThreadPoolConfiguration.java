package com.bubble.foundation.config.async;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
@Configurable
public class ThreadPoolConfiguration {

	@Value("${threadPool.corePoolSize}")
	private int corePoolSize;

	@Value("${threadPool.keepAliveSeconds}")
	private int keepAliveSeconds;

	@Value("${threadPool.maxPoolSize}")
	private int maxPoolSize;

	@Value("${threadPool.queueCapacity}")
	private int queueCapacity;

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setKeepAliveSeconds(keepAliveSeconds);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setRejectedExecutionHandler(new CallerRunsPolicy());
		return executor;
	}

}
