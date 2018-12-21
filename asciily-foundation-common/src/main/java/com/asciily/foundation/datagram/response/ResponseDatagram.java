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
package com.asciily.foundation.datagram.response;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.asciily.foundation.common.util.BeanUtil;
import com.asciily.foundation.common.util.DateUtil;
import com.asciily.foundation.common.util.digest.AesRsaUtil;
import com.asciily.foundation.common.util.digest.Des3Util;
import com.asciily.foundation.datagram.AbstractDatagram;
import com.asciily.foundation.datagram.ResponseInfo;
import com.asciily.foundation.datagram.exception.DatagramException;

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

	/**
	 * 使用指定私钥解密指定内容
	 * 
	 * @param encryptString
	 * @param privateKey
	 * @return
	 * @throws DatagramException
	 */
	public ResponseDatagram decryptRSA(String encryptString, String privateKey) throws DatagramException {
		try {
			ResponseDatagram rsd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setNonce(rsd.getNonce());
			this.setTimestamp(rsd.getTimestamp());
			this.setCode(rsd.getCode());
			this.setMsg(rsd.getMsg());
			this.setVersion(rsd.getVersion());
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, rsd.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) rsd.getBody(), aesKeyByte, "UTF-8"), Map.class));
			this.setSignature(rsd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	/**
	 * rsa私钥加密后返回对象
	 * 
	 * @param privateKey
	 * @return
	 * @throws DatagramException
	 */
	public ResponseDatagram decryptRSA(String privateKey) throws DatagramException {
		try {
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, this.getSignature(), "UTF-8");
			this.setBody(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) this.getBody(), aesKeyByte, "UTF-8"), Map.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	/**
	 * 使用指定密钥3des解密指定内容
	 * 
	 * @param encryptString
	 * @param secretKey
	 * @param signKey
	 * @return
	 * @throws DatagramException
	 */
	public ResponseDatagram decryptDesded(String encryptString, String secretKey, String signKey) throws DatagramException {
		try {
			ResponseDatagram rsd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setNonce(rsd.getNonce());
			this.setTimestamp(rsd.getTimestamp());
			this.setCode(rsd.getCode());
			this.setMsg(rsd.getMsg());
			this.setVersion(rsd.getVersion());
			String signature = DigestUtils.md5Hex(rsd.getBody() + signKey);
			if (!rsd.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) rsd.getBody(), secretKey);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(rsd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	/**
	 * 使用指定密钥解密
	 * 
	 * @param secretKey
	 * @param signKey
	 * @return
	 * @throws DatagramException
	 */
	public ResponseDatagram decryptDesded(String secretKey, String signKey) throws DatagramException {
		try {
			String signature = DigestUtils.md5Hex(this.getBody() + signKey);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) this.getBody(), secretKey);
			this.setBody(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(signature);
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

}
