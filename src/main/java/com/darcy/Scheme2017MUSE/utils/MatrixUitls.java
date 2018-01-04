package com.darcy.Scheme2017MUSE.utils;

import Jama.Matrix;

import java.util.Random;

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
				System.out.printf("%.8f\t", array[i][j]);
			}
			System.out.println();
		}
	}

	public static Matrix generateMatrix(int n, int m) {
		Random random = new Random(System.currentTimeMillis());
		double[][] doubles = new double[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				doubles[i][j] = random.nextDouble();
			}
		}
		return new Matrix(doubles);
	}

	public static String dimension(Matrix matrix) {
		return matrix.getRowDimension() + "\t" + matrix.getColumnDimension();
	}
}
