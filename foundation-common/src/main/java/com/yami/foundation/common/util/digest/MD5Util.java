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
package com.yami.foundation.common.util.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yami.foundation.common.util.StringUtil;

/**
 * @author kakashi
 * @since 2017年5月8日
 */
public class MD5Util {

	/**
	 * 生成大写MD5串
	 * 
	 * @param text
	 * @param key
	 * @param charset
	 * @return
	 */
	public static String MD5(String text, String key, String charset) {
		String message = text + key;
		MessageDigest digest = getDigest("MD5");
		digest.update(StringUtil.getContentBytes(message, charset));
		byte[] signed = digest.digest();
		return HexUtil.toHexString(signed);
	}

	/**
	 * 生成小写MD5串
	 * 
	 * @param plainText
	 * @return
	 */
	public final static String md5(String plainText) {
		// 16进制字符
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		try {
			if (plainText == null) {
				return null;
			}
			byte[] btInput = plainText.getBytes("utf-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			String jm = new String(str);
			return jm;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalArgumentException("Not support:" + algorithm, ex);
		}
	}

	/**
	 * 验证md5
	 *
	 * @param text
	 *            要签名的字符
	 * @param sign
	 *            签名结果(小写)
	 * @param key
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static boolean verifymd5(String text, String sign, String key, String charset) throws Exception {
		text = text + key;
		String mysign = org.apache.commons.codec.digest.DigestUtils.md5Hex(StringUtil.getContentBytes(text, charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证MD5
	 * 
	 * @param text
	 *            原文
	 * @param sign
	 *            签名串(大写)
	 * @param key
	 *            密钥
	 * @param charset
	 *            字符集
	 * @return 验证结果
	 * @throws Exception
	 */
	public static boolean verifyMD5(String text, String sign, String key, String charset) throws Exception {
		text = text + key;
		String mysign = org.apache.commons.codec.digest.DigestUtils.md5Hex(StringUtil.getContentBytes(text, charset)).toUpperCase();
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

}
