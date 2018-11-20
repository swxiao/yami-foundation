package com.asciily.foundation.common.util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2016年12月27日
 */
public class PKUtil {

	private static final String DATE_PATTERN = "yyyyMMddHHmmssSSS";

	/**
	 * Long PK生成锁,用来限定同一时刻只有一个线程进入PK生成计算 <br/>
	 * LONG_LOCK
	 */
	private final Lock LONG_LOCK = new ReentrantLock();

	/**
	 * String PK生成锁,用来限定同一时刻只有一个线程进入PK生成计算 <br/>
	 * STRING_LOCK
	 */
	private static final Lock STRING_LOCK = new ReentrantLock();

	/**
	 * 线程变量,维护SimpleDateFormat <br/>
	 * THREADLOCAL_DATE
	 */
	private static ThreadLocal<SimpleDateFormat> THREADLOCAL_DATE = new ThreadLocal<SimpleDateFormat>();

	/**
	 * 记录上一次生成 的 PK,如果新生成的PK和上次相等,则需要再次生成 每次被线程访问时,都强迫从共享内存中重读该成员变量的值.而且,当成员变量发生变化时,强迫线程将变化值回写到共享内存 lastPK <br/>
	 */
	private volatile long lastLongPK = -1;

	private volatile String lastStringPK = "";

	/**
	 * 后缀 suffix
	 */
	private static int suffix = 0;

	/**
	 * 初始化时的毫秒数,因为该时间会随系统时间的改变而改变, 所以计算方法为该时间加上通过 nanoTime 计算出来的时间差 <br/>
	 * startMilli
	 */
	private final static Long startMilli = System.currentTimeMillis();

	/**
	 * 初始化时的纳秒数,用来计量时间差,nanoTime不会随着系统时间的修改而改变 <br/>
	 * startNano
	 */
	private final static long startNano = System.nanoTime();

	private PKUtil() {
		initSuffix();
		System.out.println("suffix is: " + suffix);
	}

	/**
	 * 初始化suffix<br/>
	 * 通过获取本机ip后进行hash,两两相邻hash码取模之后再进行求和， 将得到的总和取十位的数字作为suffix
	 * 
	 */
	private static void initSuffix() {
		try {
			String hash = String.valueOf(IpUtil.getLocalIp().hashCode());
			int y = 0;
			for (int i = 0; i < hash.length(); i++) {
				if (i < hash.length() - 1) {
					int a = Integer.valueOf(hash.charAt(i));
					int b = Integer.valueOf(hash.charAt(i + 1));
					y += mod(a, b);
				}
			}
			suffix = y % 10;// hash 求模之后总和必然小于100,除以10之后取个位作为最终suffix
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Ip address error!");
		}

	}

	/**
	 * a,b不能为0,若有一个为0则直接返回非零数字,两个为0时直接返回9<br/>
	 * 当a<b时,b对a取模,<br/>
	 * 当a>b时,a对b取模,<br/>
	 * 当ab相等时,a+b对a取模<br/>
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static int mod(int a, int b) {
		int x = 9;
		if (a == 0 && b == 0) {
			return x;
		}
		if (a == 0 || b == 0) {
			return a | b;
		}
		if (a > b) {
			x = a % b;
		} else if (b > a) {
			x = b % a;
		} else {
			x = a + b % a;
		}
		return x;
	}

	/**
	 * Threadlocal维护SimpleDateFormat对象
	 * 
	 * @return
	 */
	private static SimpleDateFormat getDateFormat() {

		SimpleDateFormat sdf = THREADLOCAL_DATE.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat(DATE_PATTERN);
			THREADLOCAL_DATE.set(sdf);
		}
		return THREADLOCAL_DATE.get();
	}

	/**
	 * 格式化时间(yyyyMMddHHmmssSSS)
	 * 
	 * @param date
	 * @return
	 */
	private static String formatDate(Date source) {
		String dateStr = getDateFormat().format(source);
		return dateStr;
	}

	/**
	 * 返回指定时间类型
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private static Date parseDate(String source) throws ParseException {
		Date date = getDateFormat().parse(source);
		return date;
	}

	/**
	 * 内部类，保持单例模式唯一性
	 * 
	 * @author xiaosw<xiaosw@msn.cn>
	 * @since 2017年12月1日
	 */
	private static class KeyGenInncerClass implements Serializable {

		private static PKUtil instance = new PKUtil();

		/**
		 * 阻止反序列化生成多个instance
		 * 
		 * @return
		 */
		protected Object readResolve() {
			return KeyGenInncerClass.instance;
		}
	}

	/**
	 * @return 主键生成器实例
	 */
	public static PKUtil getInstance() {
		return KeyGenInncerClass.instance;
	}

	/**
	 * 返回long 型 PK<br/>
	 * 例: 2007101011023022291 <br/>
	 * yyyyMMddHHmmssSSS + Micro Seconds + suffix <br/>
	 * 并发情况下生成重复率较高，当做主键时慎用
	 * 
	 * @return long PK
	 */
	public long longPK() {
		LONG_LOCK.lock();
		try {
			long newPK;
			do {
				long pastNano = System.nanoTime() - startNano; // 纳秒时间差
				long milliTime = pastNano / 1000000; // 取得毫秒差
				long microTime = (pastNano / 100000) % 10; // 取得微秒第一位,
				// 计算出来的long PK,精度到万分之一秒（百微妙）,加上尾数,一共19位,这是 Long.MAX_VALUE的最大位数了
				newPK = Long.parseLong(formatDate((new Date(startMilli + milliTime))) + microTime + suffix);
			} while (lastLongPK == newPK); // 如果生成的相同,则再次计算
			lastLongPK = newPK; // 设置 lastPK
			return newPK;
		} finally {
			LONG_LOCK.unlock();
		}
	}

	/**
	 * 返回string 型PK<br/>
	 * 例: 201712041645386880695508 <br>
	 * yyyyMMddHHmmss(14) + milliTime(3) + microTime(3) + nanoTime(3)+ suffix(1)<br/>
	 * 
	 * @return PK
	 */
	public String varcharPK() {
		STRING_LOCK.lock();
		try {
			String PK;
			do {
				long pastNano = System.nanoTime() - startNano; // 纳秒时间差
				long milliTime = pastNano / 1000000; // 取得毫秒差
				String microTime = String.format("%03d", (pastNano / 1000) % 1000); // 取得微秒差
				String nanoTime = String.format("%03d", pastNano % 1000);// 取得纳秒差
				// yyyyMMddHHmmss(14) + milliTime(3) + microTime(3) + nanoTime(3)+ suffix(1)
				PK = formatDate((new Date(startMilli + milliTime))) + microTime + nanoTime + suffix;// 精确到纳秒
			} while (lastStringPK == PK); // 如果生成的相同,则再次计算
			lastStringPK = PK; // 设置 lastStringPK
			return PK;
		} finally {
			STRING_LOCK.unlock();
		}
	}

	/**
	 * UUID key
	 * 
	 * @return
	 */
	public String UUIDPK() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
