package com.darcy.auxiliary;

/*
 * author: darcy
 * date: 2018/1/4 14:19
 * description: 
*/
public class OS {
	public static void main(String[] args) {
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("os:" + os);

		String version = System.getProperty("os.version").toLowerCase();
		System.out.println("version:" + version);
	}
}
