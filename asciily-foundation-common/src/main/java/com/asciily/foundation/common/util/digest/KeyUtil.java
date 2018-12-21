package com.asciily.foundation.common.util.digest;

import org.springframework.util.Assert;

public class KeyUtil {

	public static String generateDesdeKey(String nonce) {
		Assert.notNull(nonce, "nonce can't be null");
		StringBuilder tempA = new StringBuilder();
		StringBuilder tempB = new StringBuilder();
		for (int i = 0; i < nonce.length(); i++) {
			if (i % 2 == 1) {
				tempA.append(nonce.charAt(i));
			} else {
				tempB.append(nonce.charAt(i));
			}
		}
		for (int i = 0; i < tempB.length(); i++) {
			if (i % 2 == 0) {
				tempA.append(tempB.charAt(i));
			}
		}

		char a = tempA.charAt(1);
		char b = tempA.charAt(9);
		char c = tempA.charAt(6);
		char d = tempA.charAt(23);
		char e = tempA.charAt(11);
		char f = tempA.charAt(19);
		tempA.replace(1, 2, String.valueOf(b));
		tempA.replace(9, 10, String.valueOf(a));
		tempA.replace(6, 7, String.valueOf(d));
		tempA.replace(23, 24, String.valueOf(c));
		tempA.replace(11, 12, String.valueOf(f));
		tempA.replace(19, 20, String.valueOf(e));
		return tempA.toString();
	}

	public static String generateMd5Key(String nonce) {
		Assert.notNull(nonce, "nonce can't be null");
		StringBuilder tempA = new StringBuilder();
		for (int i = 0; i < nonce.length(); i++) {
			if (i % 2 == 0) {
				tempA.append(nonce.charAt(i));
			}
		}
		return tempA.reverse().toString();
	}
}
