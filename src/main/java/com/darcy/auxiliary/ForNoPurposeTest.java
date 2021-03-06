package com.darcy.auxiliary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * author: darcy
 * date: 2017/12/20 15:52
 * description: 
*/
public class ForNoPurposeTest {
	public static Pattern pattern = Pattern.compile("\\w+");


	public static void main(String[] args) {
		/**
		 * 大概153个数字相关的单词, 从长度为 5457 的字典中去掉153个单词作用也非常有限。
		 */
		String str = "0, 000, 1, 10, 100, 101, 10th, 11, 12, 13, 14, 140, 149, 14th, 15, 1500m, 15th, 16, 160, 16th, 17, 17th, 18, 18m, 19, 1920s, 1926, 1950s, 1960s, 1977, 1979, 1985, 1986, 1994, 1995, 1996, 1998, 2, 20, 200, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2020, 20c, 21, 221, 22nd, 23rd, 24, 240, 25, 26, 28, 29, 292, 2nd, 2s, 3, 30, 300, 302, 32, 33, 330, 34, 35, 350, 357, 36, 37, 373, 398, 4, 40, 400, 41, 42, 429, 440, 46, 460, 48, 490, 4th, 4x100m, 5, 50, 500, 50th, 52, 53, 54, 540, 55, 550, 56, 580, 59, 6, 60, 600, 60th, 61, 63, 635th, 65, 7, 70, 700, 70s, 72, 73, 750, 76, 760, 78, 786, 787, 7pm, 8, 80, 800m, 80s, 82, 823, 84, 85, 853, 87, 88, 9, 90, 900, 911, 92, 93, 940, 95,";
		System.out.println(str.split(",").length);

		System.out.println("'java'");

		System.out.println(Double.MIN_VALUE);
		System.out.println(Double.MIN_VALUE > 0);
		System.out.println(Double.NEGATIVE_INFINITY);
		System.out.println(-100 > Double.NEGATIVE_INFINITY);

		// str = "Pope Francis honorary citizenship Democratic Revolution church president conferences";
		// str = "clinton broadcasting voice Francis honorary citizenship Democratic Revolution church president conferences";
		// str = "church China hospital performance British interview Democratic citizenship";
		str = "church China hospital performance British interview Democratic citizenship broadcasting voice";
		String replaceStr = str.replace(' ', '|');
		StringBuilder targetStr = new StringBuilder("").append("(").append(replaceStr).append(")");
		String targetStr2 = getQueryPattern(str);
		System.out.println(replaceStr);
		System.out.println(targetStr);
		System.out.println(targetStr2);
		// (clinton|broadcasting|voice|Francis|honorary|citizenship|Democratic|Revolution|church|president|conferences)
		// (church|China|hospital|performance|British|interview|Democratic|citizenship)
		// (church|China|hospital|performance|British|interview|Democratic|citizenship|broadcasting|voice)


	}

	public static String getQueryPattern(String str) {
		Matcher matcher = pattern.matcher(str);
		String result = "";
		while (matcher.find()) {
			result += matcher.group().toLowerCase() + "|";
		}
		return "(" + result.substring(0, result.lastIndexOf('|')) + ")";
	}
}