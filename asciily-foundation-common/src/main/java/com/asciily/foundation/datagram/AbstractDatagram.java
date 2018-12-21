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
package com.asciily.foundation.datagram;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.asciily.foundation.common.util.BeanUtil;
import com.asciily.foundation.common.util.digest.AesRsaUtil;
import com.asciily.foundation.common.util.digest.Des3Util;
import com.asciily.foundation.datagram.exception.DatagramException;

/**
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2016年1月26日
 */
public abstract class AbstractDatagram {

	private String nonce;

	private String version;

	private String timestamp;

	private Object body;

	private String signature;

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	protected void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the bory
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the data to set
	 */
	public void setBody(Object body) {
		if (body instanceof Map) {
			if (((Map) body).containsKey(ResponseInfo.KEY_CODE))
				((Map) body).remove(ResponseInfo.KEY_CODE);
			if (((Map) body).containsKey(ResponseInfo.KEY_MSG))
				((Map) body).remove(ResponseInfo.KEY_MSG);
		}
		this.body = body;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature
	 *            the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * rsa公钥加密返回字符串
	 * 
	 * @param publicKey
	 * @return
	 */
	public String encryptRSAString(String publicKey) {
		try {
			String data = BeanUtil.bean2JSON(this.getBody());
			String aesKey = AesRsaUtil.generateLenString(16);
			this.setBody(AesRsaUtil.encryptData(data, aesKey, "UTF-8"));
			this.setSignature(AesRsaUtil.encrtptKey(publicKey, aesKey, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	/**
	 * rsa私钥解密后返回字符串
	 * 
	 * @param privateKey
	 * @return
	 */
	public String decryptRSAString(String privateKey) {
		try {
			String signatute = this.getSignature();
			String data = (String) this.getBody();
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, signatute, "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData(data, aesKeyByte, "UTF-8"), Map.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	/**
	 * 使用指定密钥3des加密后返回字符串
	 * 
	 * @param secretKey
	 * @param signKey
	 * @return
	 */
	public String encryptDesdedString(String secretKey, String signKey) {
		try {
			String data = BeanUtil.bean2JSON(this.getBody());
			String encodeData = Des3Util.encode(data, secretKey);
			this.setBody(encodeData);
			String signature = DigestUtils.md5Hex(encodeData + signKey);
			this.setSignature(signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	/**
	 * 使用指定密钥3des解密后返回字符串
	 * 
	 * @param encryptKey
	 * @param signKey
	 * @return
	 */
	public String decryptDesdedString(String encryptKey, String signKey) {
		try {
			String data = (String) this.getBody();
			String signature = DigestUtils.md5Hex(data + signKey);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			this.setBody(BeanUtil.json2Bean(Des3Util.decode(data, encryptKey), Map.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return BeanUtil.bean2JSON(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
	}
}
