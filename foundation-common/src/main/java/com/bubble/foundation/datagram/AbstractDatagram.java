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
package com.bubble.foundation.datagram;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.bubble.foundation.common.util.BeanUtil;
import com.bubble.foundation.common.util.digest.AesRsaUtil;
import com.bubble.foundation.common.util.digest.Des3Util;
import com.bubble.foundation.datagram.exception.DatagramException;

/**
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2016年1月26日
 */
public abstract class AbstractDatagram {

	private String serialNo;

	private String version;

	private String timestamp;

	private Object data;

	private String signature;

	private Object extras;

	/**
	 * @return the serialNo
	 */
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * @param serialNo
	 *            the serialNo to set
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		if (data instanceof Map) {
			if (((Map) data).containsKey(ResponseInfo.KEY_CODE))
				((Map) data).remove(ResponseInfo.KEY_CODE);
			if (((Map) data).containsKey(ResponseInfo.KEY_MSG))
				((Map) data).remove(ResponseInfo.KEY_MSG);
		}
		this.data = data;
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

	public Object getExtras() {
		return extras;
	}

	public void setExtras(Object extras) {
		this.extras = extras;
	}

	public String encryptDESString(String key) {
		try {
			String data = BeanUtil.bean2JSON(this.getData());
			String encodeData = Des3Util.encode(data, key);
			this.setData(encodeData);
			String signature = DigestUtils.md5Hex(encodeData + key);
			this.setSignature(signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	public String encryptRSAString(String publicKey) {
		try {
			String data = BeanUtil.bean2JSON(this.getData());
			String aesKey = AesRsaUtil.generateLenString(16);
			this.setData(AesRsaUtil.encryptData(data, aesKey, "UTF-8"));
			this.setSignature(AesRsaUtil.encrtptKey(publicKey, aesKey, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	public String decryptDESString(String key) {
		try {
			String data = (String) this.getData();
			String signature = DigestUtils.md5Hex(data + key);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			this.setData(BeanUtil.json2Bean(Des3Util.decode(data, key), Map.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatagramException(e.getMessage(), e.getCause());
		}
		return toString();
	}

	public String decryptRSAString(String privateKey) {
		try {
			String signatute = this.getSignature();
			String data = (String) this.getData();
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, signatute, "UTF-8");
			this.setData(BeanUtil.json2Bean(AesRsaUtil.decryptData(data, aesKeyByte, "UTF-8"), Map.class));
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
