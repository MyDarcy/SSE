package com.darcy.Scheme2016FineGrained.utils;

import Jama.Matrix;

/*
 * author: darcy
 * date: 2017/11/8 22:39
 * description: 
*/
public class MatrixUitls {

	public static void print(Matrix matrix) {
		double[][] array = matrix.getArray();
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.printf("%-6f\t", array[i][j]);
			}
			System.out.println();
		}
	}

	public static String dimension(Matrix matrix) {
		return matrix.getRowDimension() + "\t" + matrix.getColumnDimension();
	}
}
