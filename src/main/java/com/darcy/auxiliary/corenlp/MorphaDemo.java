package com.darcy.auxiliary.corenlp;

import java.io.*;
import java.util.Scanner;

import static edu.washington.cs.knowitall.morpha.MorphaStemmer.stem;

/*
 * author: darcy
 * date: 2018/1/12 15:06
 * description: 
*/
public class MorphaDemo {
	public static void main(String[] args) throws FileNotFoundException {
		String filename = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\muse\\extend\\plain16\\000c835555db62e319854d9f8912061cdca1893e.story";
//		BufferedReader scanner = new BufferedReader(new FileReader(filename));
		Scanner scanner = new Scanner(new FileInputStream(filename));

		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println(line);
			System.out.println(stem(line));
		}
	}
}
