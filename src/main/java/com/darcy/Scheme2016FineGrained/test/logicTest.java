package com.darcy.Scheme2016FineGrained.test;

import java.util.Arrays;

/*
 * author: darcy
 * date: 2017/11/20 16:33
 * description: 
*/
public class logicTest {

	public static void main(String[] args) {
		String query = "java|cpp class&static !python!ruby";
		String[] strArray = query.split("\\s+");
		Arrays.stream(strArray).forEach(System.out::println);

		query = "|(java cpp) &(class static) !(python ruby)";

		String[] orKeywords = null;
		String[] andKeywords = null;
		String[] notKeywords = null;

		int orCharIndex = query.indexOf('!');
		// 说明查询支付字符串中有or操作符.
		if (orCharIndex != -1) {
			int last = query.indexOf(')', orCharIndex);
			String temp = query.substring(orCharIndex + 2, last);
			orKeywords = temp.split("\\s+");
			System.out.println(orKeywords);
		}

		int andCharIndex = query.indexOf('&');
		if (andCharIndex != -1) {
			int last = query.indexOf(')', andCharIndex);
			String temp = query.substring(andCharIndex + 2, last);
			andKeywords = temp.split("\\s+");
			System.out.println(andKeywords);
		}

		int notCharIndex = query.indexOf('!');
		if (notCharIndex != -1) {
			int last = query.indexOf(')', notCharIndex);
			String temp = query.substring(notCharIndex + 2, last);
			notKeywords = temp.split("\\s+");
			System.out.println(notKeywords);
		}

	}
}
