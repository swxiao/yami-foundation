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
package com.bubble.foundation.config.dubbo.consumer;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.alibaba.dubbo.config.ConsumerConfig;
import com.bubble.foundation.config.dubbo.BaseDubboConfiguration;

/**
 * 
 * @author xiaoshiwen<xiaoshiwen@zitopay.com>
 * @since 2017年1月17日
 */
@Import(BaseDubboConfiguration.class)
@Configurable
public class DubboConsumerConfiguration extends BaseDubboConfiguration {

	@Value("${dubbo.consumer.check}")
	protected boolean consumerCheck;
	
	@Bean
	public ConsumerConfig consumerConfig() {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setRegistry(registryConfig());
		consumerConfig.setApplication(applicationConfig());
		consumerConfig.setCheck(consumerCheck);
		consumerConfig.setRetries(0);
		return consumerConfig;
	}

	/*
	 * @Bean public BasicRefererConfigBean basicRefererConfig() { BasicRefererConfigBean basicRefererConfig = new BasicRefererConfigBean();
	 * basicRefererConfig.setRegistry(registryConfig()); basicRefererConfig.setProtocol(protocolConfig());
	 * basicRefererConfig.setGroup(group); basicRefererConfig.setModule(module); basicRefererConfig.setApplication(application);
	 * basicRefererConfig.setCheck(false); basicRefererConfig.setAccessLog(true); basicRefererConfig.setRetries(2);
	 * basicRefererConfig.setThrowException(true); return basicRefererConfig; }
	 */

}
