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
package com.yami.foundation.datagram.response;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.yami.foundation.common.util.BeanUtil;
import com.yami.foundation.common.util.DateUtil;
import com.yami.foundation.common.util.digest.AesRsaUtil;
import com.yami.foundation.common.util.digest.Des3Util;
import com.yami.foundation.datagram.AbstractDatagram;
import com.yami.foundation.datagram.ResponseInfo;
import com.yami.foundation.datagram.exception.DatagramException;

/**
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2016年1月26日
 */
public class ResponseDatagram extends AbstractDatagram {

	public ResponseDatagram() {
		this.setTimestamp(DateUtil.DateToString(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS"));
		this.setVersion("1.0");
	}

	private String code;

	private String msg;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ResponseDatagram setResponseTips(ResponseInfo responseInfo) {
		this.code = responseInfo.getCode();
		this.msg = responseInfo.getMsg();
		return this;
	}

	public ResponseDatagram decryptRSA(String encryptString, String privateKey) throws DatagramException {
		try {
			ResponseDatagram rsd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setSerialNo(rsd.getSerialNo());
			this.setTimestamp(rsd.getTimestamp());
			this.setCode(rsd.getCode());
			this.setMsg(rsd.getMsg());
			this.setVersion(rsd.getVersion());
			this.setAttach(rsd.getAttach());
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, rsd.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) rsd.getBody(), aesKeyByte, "UTF-8"), Map.class));
			this.setSignature(rsd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public ResponseDatagram decryptRSA(String privateKey) throws DatagramException {
		try {
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, this.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) this.getBody(), aesKeyByte, "UTF-8"), Map.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public ResponseDatagram decryptDES(String encryptString, String key) throws DatagramException {
		try {
			ResponseDatagram rsd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setSerialNo(rsd.getSerialNo());
			this.setTimestamp(rsd.getTimestamp());
			this.setCode(rsd.getCode());
			this.setMsg(rsd.getMsg());
			this.setVersion(rsd.getVersion());
			this.setAttach(rsd.getAttach());
			String signature = DigestUtils.md5Hex(rsd.getBody() + key);
			if (!rsd.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) rsd.getBody(), key);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(rsd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public ResponseDatagram decryptDES(String key) throws DatagramException {
		try {
			String signature = DigestUtils.md5Hex(this.getBody() + key);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) this.getBody(), key);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(signature);
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}
}
