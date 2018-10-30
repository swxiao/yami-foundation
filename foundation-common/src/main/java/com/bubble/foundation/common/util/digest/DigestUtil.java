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
package com.bubble.foundation.common.util.digest;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

/**
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2018年10月29日
 */
public class DigestUtil extends DigestUtils {

	/**
	 * @param originalValue
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeBase64(String originalValue) throws UnsupportedEncodingException {
		Assert.notNull(originalValue, "the original value must not be null!");
		return new String(Base64.decodeBase64(originalValue), "UTF-8");
	}

	public static String encodeBase64(String originalValue) throws UnsupportedEncodingException {
		Assert.notNull(originalValue, "the original value must not be null!");
		return new String(Base64.encodeBase64(originalValue.getBytes("UTF-8")), "UTF-8");
	}

}
