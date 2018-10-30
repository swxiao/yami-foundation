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
package com.bubble.foundation.config.motan;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;

/**
 * 
 * @author xiaoshiwen<xiaoshiwen@zitopay.com>
 * @since 2017年1月16日
 */
@Configurable
public class BaseMotanConfiguration {

	@Value("${motan.registry.address}")
	protected String registryAddress = "127.0.0.1:2181";

	@Value("${motan.registry.protocol}")
	protected String registryProtocol = "zookeeper";

	@Value("${motan.registry.connection.timeout}")
	protected int registryConnectionTimeout = 2000;

	@Bean
	public RegistryConfigBean registryConfig() {
		RegistryConfigBean registry = new RegistryConfigBean();
		registry.setBeanName("registryConfig");
		registry.setAddress(registryAddress);
		registry.setRegProtocol(registryProtocol);
		registry.setConnectTimeout(registryConnectionTimeout);
		registry.setCheck("true");
		return registry;

	}

	@Value("${motan.protocol.cluster}")
	protected String cluster = "motanCluster";

	@Value("${motan.protocol.loadbalance}")
	protected String loadbalance = "activeWeight";

	@Value("${motan.protocol.maxServerConnection}")
	protected int maxServerConnection = 100000;

	@Value("${motan.protocol.maxContentLength}")
	protected int maxContentLength = 1048576;// 10M

	@Value("${motan.protocol.maxWorkThread}")
	protected int maxWorkerThread = 1024;

	@Value("${motan.protocol.minWorkerThread}")
	protected int minWorkerThread = 32;

	@Value("${motan.protocol.requestTimeout}")
	protected int requestTimeout = 30000;

	@Bean
	public ProtocolConfigBean protocolConfig() {
		ProtocolConfigBean protocol = new ProtocolConfigBean();
		protocol.setBeanName("protocolConfig");
		protocol.setName("motan");
		protocol.setDefault(true);
		// protocol.setCluster(cluster);
		protocol.setRequestTimeout(requestTimeout);
		protocol.setLoadbalance(loadbalance);
		protocol.setMaxServerConnection(maxServerConnection);
		protocol.setMaxContentLength(maxContentLength);
		protocol.setMaxWorkerThread(maxWorkerThread);
		protocol.setMinWorkerThread(minWorkerThread);
		protocol.setAsync(false);
		return protocol;
	}

	@Value("${motan.service.application}")
	protected String application = "motan-service-application";

	@Value("${motan.service.group}")
	protected String group = "motan-service-group";

	@Value("${motan.service.module}")
	protected String module = "motan-service-module";

}
