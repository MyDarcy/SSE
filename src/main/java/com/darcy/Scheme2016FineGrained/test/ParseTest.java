package com.darcy.Scheme2016FineGrained.test;

import org.apache.commons.text.StrTokenizer;

import java.util.Arrays;

/*
 * author: darcy
 * date: 2017/12/15 17:15
 * description: 
*/
public class ParseTest {

	public static void main(String[] args) {
		String str = "\"java . hell0 sksk\" \" , *ss";
		System.out.println(str);
		System.out.println(Arrays.toString(str.split("\\s+")));
		String[] array = str.split("\\s+");
		for (int i = 0; i < array.length; i++) {
			System.out.println(str.trim());
		}

		StrTokenizer strTokenizer = new StrTokenizer(str, " ");
		while (strTokenizer.hasNext()) {
			System.out.println(strTokenizer.next());
		}

	}
}
