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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.alibaba.dubbo.config.spring.AnnotationBean;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月17日
 */
@Configurable
@Import(value = { DubboProviderConfiguration.class })
public class DubboServerAnnotaionConfiguration {

	/**
	 * 启动motan
	 * 
	 * @return
	 */
	@Bean
	public AnnotationBean annotationBean() {
		AnnotationBean bean = new AnnotationBean();
		bean.setPackage("com");
		return bean;
	}
}