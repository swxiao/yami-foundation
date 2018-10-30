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
package com.yami.foundation.datagram.request;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.yami.foundation.common.util.BeanUtil;
import com.yami.foundation.common.util.DateUtil;
import com.yami.foundation.common.util.PKUtil;
import com.yami.foundation.common.util.digest.AesRsaUtil;
import com.yami.foundation.common.util.digest.Des3Util;
import com.yami.foundation.datagram.AbstractDatagram;
import com.yami.foundation.datagram.exception.DatagramException;

/**
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2016年1月26日
 */
public class RequestDatagram extends AbstractDatagram {

	private String deviceNo;

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public RequestDatagram() {
		initDatagram();
	}

	private RequestDatagram initDatagram() {
		this.setVersion("1.0");
		this.setSerialNo(PKUtil.getInstance().UUIDPK());
		this.setTimestamp(DateUtil.DateToString(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS"));
		return this;
	}

	public RequestDatagram decryptRSA(String encryptString, String privateKey) throws DatagramException {
		try {
			RequestDatagram rd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setSerialNo(rd.getSerialNo());
			this.setVersion(rd.getVersion());
			this.setTimestamp(rd.getTimestamp());
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, rd.getSignature(), "UTF-8");
			this.setData(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) rd.getData(), aesKeyByte, "UTF-8"), Map.class));
			this.setSignature(rd.getSignature());
			this.setExtras(rd.getExtras());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptRSA(String privateKey) throws DatagramException {
		try {
			byte[] aesKeyByte = AesRsaUtil.decryptKey(privateKey, this.getSignature(), "UTF-8");
			this.setData(BeanUtil.json2Bean(AesRsaUtil.decryptData((String) this.getData(), aesKeyByte, "UTF-8"), Map.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptDES(String encryptString, String key) throws DatagramException {
		try {
			RequestDatagram rd = BeanUtil.json2Bean(encryptString, this.getClass());
			this.setSerialNo(rd.getSerialNo());
			this.setVersion(rd.getVersion());
			this.setTimestamp(rd.getTimestamp());
			this.setExtras(rd.getExtras());
			String signature = DigestUtils.md5Hex(rd.getData() + key);
			if (!rd.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) rd.getData(), key);
			this.setData(BeanUtil.json2Bean(dataStr, Object.class));
			this.setSignature(rd.getSignature());
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

	public RequestDatagram decryptDES(String key) throws DatagramException {
		try {
			String signature = DigestUtils.md5Hex(this.getData() + key);
			if (!this.getSignature().equalsIgnoreCase(signature)) {// MD5验证忽略大小写
				throw new IllegalArgumentException("signature error!");
			}
			String dataStr = Des3Util.decode((String) this.getData(), key);
			this.setData(BeanUtil.json2Bean(dataStr, Object.class));
			return this;
		} catch (Exception e) {
			throw new DatagramException(e.getMessage(), e);
		}
	}

}
