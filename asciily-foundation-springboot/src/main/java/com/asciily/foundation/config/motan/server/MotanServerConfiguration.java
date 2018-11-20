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
package com.asciily.foundation.config.motan.server;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.asciily.foundation.config.motan.BaseMotanConfiguration;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月17日
 */
@Import(BaseMotanConfiguration.class)
@Configurable
public class MotanServerConfiguration extends BaseMotanConfiguration{

	@Value("${motan.protocol.port}")
	private int protocolPort = 10801;

	@Bean
	public BasicServiceConfigBean basicServiceConfig() {
		BasicServiceConfigBean basicService = new BasicServiceConfigBean();
		basicService.setBeanName("basicServiceConfig");
		basicService.setProtocol(protocolConfig());
		basicService.setRegistry(registryConfig());
		basicService.setExport("protocolConfig:" + protocolPort);
		basicService.setApplication(application);
		basicService.setGroup(group);
		basicService.setModule(module);
		basicService.setAccessLog(false);
		basicService.setShareChannel(true);
	
		return basicService;
	}

}
