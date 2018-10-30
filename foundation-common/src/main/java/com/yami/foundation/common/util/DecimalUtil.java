package com.yami.foundation.common.util;

import java.math.BigDecimal;

/**
 * 小数操作
 * 
 * @since 2016/11/28
 */
public class DecimalUtil {

	/**
	 * 保留小数的位数
	 * 
	 * @param dec
	 *            原始数据
	 * @param num
	 *            保留位数值
	 * @return
	 */
	public static String reserveDecimal(double dec, int num) {
		String decStr = String.format("%." + num + "f", dec);
		return decStr;
	}

	public static BigDecimal getBigDecimal(BigDecimal value, int scale) {
		if (value == null) {
			return BigDecimal.ZERO.setScale(scale);
		} else {
			return value.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
	}

}
