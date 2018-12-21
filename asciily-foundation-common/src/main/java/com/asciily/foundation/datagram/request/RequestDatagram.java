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
package com.asciily.foundation.datagram.request;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.asciily.foundation.common.util.BeanUtil;
import com.asciily.foundation.common.util.DateUtil;
import com.asciily.foundation.common.util.PKUtil;
import com.asciily.foundation.common.util.digest.AesRsaUtil;
import com.asciily.foundation.common.util.digest.Des3Util;
import com.asciily.foundation.datagram.AbstractDatagram;
import com.asciily.foundation.datagram.exception.DatagramException;

/**
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2016年1月26日
 */
public class RequestDatagram extends AbstractDatagram {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String authNo) {
		this.token = authNo;
	}

	public RequestDatagram() {
		initDatagram();
	}

	private RequestDatagram initDatagram() {
		this.setVersion("1.0");
		this.setNonce(PKUtil.getInstance().generateNonceStr());
		this.setTimestamp(DateUtil.DateToString(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS"));
		return this;
	}

	public RequestDatagram decryptRSA(String encryptString, String privateKey) throws DatagramException {
		try {
			RequestDatagram rd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setNonce(rd.getNonce());
			this.setVersion(rd.getVersion());
			this.setTimestamp(rd.getTimestamp());
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, rd.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) rd.getBody(), aesKeyByte, "UTF-8"), Map.class));
			this.setSignature(rd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptRSA(String privateKey) throws DatagramException {
		try {
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, this.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) this.getBody(), aesKeyByte, "UTF-8"), Map.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptDES(String encryptString, String key) throws DatagramException {
		try {
			RequestDatagram rd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setNonce(rd.getNonce());
			this.setVersion(rd.getVersion());
			this.setTimestamp(rd.getTimestamp());
			String signature = DigestUtils.md5Hex(rd.getBody() + key);
			if (!rd.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) rd.getBody(), key);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(rd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptDES(String key) throws DatagramException {
		try {
			String signature = DigestUtils.md5Hex(this.getBody() + key);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) this.getBody(), key);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

}
