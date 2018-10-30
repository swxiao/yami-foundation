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
package com.yami.foundation.common.env;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import com.yami.foundation.common.exception.CommonException;
import com.yami.foundation.common.util.digest.DigestUtil;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月13日
 */
public class DigestEnvironment {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DigestEnvironment.class);

	private static final String KEY_DIGEST_PREFIX = "digest.";

	@Inject
	private Environment env;

	/**
	 * 获取属性文件内容
	 * 
	 * @param propertyName
	 * @return
	 */
	public String getProperty(String propertyName) {
		try {
			if (propertyName.startsWith(KEY_DIGEST_PREFIX)) {
				String originalValue = env.getProperty(propertyName);
				return DigestUtil.decodeBase64(originalValue);
			} else {
				return env.getProperty(propertyName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommonException("read env value error!", e);
		}
	}

	/**
	 * 获取属性文件内容(带默认值)
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String propertyName, String defaultValue) {
		try {
			if (propertyName.startsWith(KEY_DIGEST_PREFIX)) {
				String originalValue = env.getProperty(propertyName);
				return originalValue == null ? null : DigestUtil.decodeBase64(originalValue);
			} else {
				return env.getProperty(propertyName) == null ? defaultValue : env.getProperty(propertyName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommonException("read env value error!", e);
		}

	}

	/**
	 * 根据类型获取属性值
	 * 
	 * @param propertyName
	 * @param targetType
	 * @return
	 */
	public <T> T getRequiredProperty(String propertyName, Class<T> targetType) {
		return env.getRequiredProperty(propertyName, targetType);
	}

	/**
	 * 根据类型获取属性值(带默认值)
	 * 
	 * @param propertyName
	 * @param targetType
	 * @param defaultValue
	 * @return
	 */
	public <T> T getRequiredProperty(String propertyName, Class<T> targetType, T defaultValue) {
		try {
			return env.getRequiredProperty(propertyName, targetType);
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
