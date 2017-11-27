package com.darcy.Scheme2016FineGrained.test;

import com.hankcs.hanlp.HanLP;

/*
 * author: darcy
 * date: 2017/11/13 11:02
 * description: 
*/
public class HanNLPTest {
	public static void main(String[] args) {
		System.out.println(HanLP.segment("你好，欢迎使用HanLP汉语处理包！"));
	}
}
