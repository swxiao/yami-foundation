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
package com.yami.foundation.config.dubbo.provider;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.yami.foundation.config.dubbo.BaseDubboConfiguration;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月17日
 */
@Import(BaseDubboConfiguration.class)
@Configurable
public class DubboProviderConfiguration extends BaseDubboConfiguration {

	@Value("${dubbo.protocol.port}")
	private int protocolPort = 10801;

	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setCharset("UTF-8");
		protocol.setName("dubbo");
		protocol.setPort(protocolPort);
		return protocol;
	}

	@Value("${dubbo.provider.timeout}")
	private int timeout = 60000;

	@Value("${dubbo.provider.threadpool}")
	private String threadpool = "cached";

	@Value("${dubbo.provider.threads}")
	private int threads = 200;

	@Bean
	public ProviderConfig provider() {
		ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setApplication(applicationConfig());
		providerConfig.setTimeout(timeout);
		providerConfig.setProtocol(protocolConfig());
		providerConfig.setRegistry(registryConfig());
		providerConfig.setThreadpool(threadpool);
		providerConfig.setThreads(threads);
		providerConfig.setRetries(1);
		return providerConfig;
	}

	/*
	 * @Bean public BasicServiceConfigBean basicServiceConfig() { BasicServiceConfigBean basicService = new BasicServiceConfigBean();
	 * basicService.setBeanName("basicServiceConfig"); basicService.setProtocol(protocolConfig());
	 * basicService.setRegistry(registryConfig()); basicService.setExport("protocolConfig:" + protocolPort);
	 * basicService.setApplication(application); basicService.setGroup(group); basicService.setModule(module);
	 * basicService.setAccessLog(false); basicService.setShareChannel(true); return basicService; }
	 */

}
