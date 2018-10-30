package com.yami.foundation.config.dubbo;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@Configurable
public class BaseDubboConfiguration {

	@Value("${dubbo.registry.address}")
	protected String registryAddress;

	@Value("${dubbo.registry.protocol}")
	protected String registryProtocol;

	@Value("${dubbo.registry.connection.timeout}")
	protected int registryConnectionTimeout;
	
	@Value("${dubbo.registry.check}")
	protected boolean registryCheck;

	@Value("${dubbo.application.name}")
	protected String applicationName;

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(applicationName);
		return applicationConfig;
	}
	
	@Bean
	public RegistryConfig registryConfig(){
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(registryAddress);
		registryConfig.setProtocol(registryProtocol);
//		registryConfig.setTimeout(registryConnectionTimeout);
		registryConfig.setCheck(registryCheck);
		return registryConfig;
	}

}
