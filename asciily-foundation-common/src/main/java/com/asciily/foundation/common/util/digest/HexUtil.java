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
package com.asciily.foundation.common.util.digest;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月24日
 */
public class HexUtil {

	public static String toHexString(byte[] value) {
		if (value == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer(value.length * 2);
		for (int i = 0; i < value.length; i++) {
			sb.append(toHexString(value[i]));
		}
		return sb.toString();
	}

	private static String toHexString(byte value) {
		String hex = Integer.toHexString(value & 0xFF);

		return padZero(hex, 2);
	}

	private static String padZero(String hex, int length) {
		for (int i = hex.length(); i < length; i++) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	}

	public static String byte2hex(byte[] b) {
		StringBuffer sb = new StringBuffer();
		String tmp = "";
		for (int i = 0; i < b.length; i++) {
			tmp = Integer.toHexString(b[i] & 0XFF);
			if (tmp.length() == 1) {
				sb.append("0" + tmp);
			} else {
				sb.append(tmp);
			}

		}
		return sb.toString();
	}

	public static byte[] hex2byte(String str) {
		if (str == null) {
			return null;
		}

		str = str.trim();
		int len = str.length();

		if (len == 0 || len % 2 == 1) {
			return null;
		}

		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}
}
