package com.asciily.foundation.common.util;

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

	/**
	 * 获取小数
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static BigDecimal getBigDecimal(BigDecimal value, int scale) {
		if (value == null) {
			return BigDecimal.ZERO.setScale(scale);
		} else {
			return value.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
	}

	/**
	 * 元转分
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static int yuan2Fen(BigDecimal value, int scale) {
		if (value == null) {
			return BigDecimal.ZERO.setScale(scale).intValue();
		} else {
			return value.multiply(BigDecimal.valueOf(100)).setScale(scale, BigDecimal.ROUND_HALF_UP).intValue();
		}
	}

	/**
	 * 分转元
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double fen2Yuan(BigDecimal value, int scale) {
		if (value == null) {
			return BigDecimal.ZERO.setScale(scale).doubleValue();
		} else {
			return value.divide(BigDecimal.valueOf(100)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
}
