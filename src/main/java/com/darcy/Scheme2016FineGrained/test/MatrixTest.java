package com.darcy.Scheme2016FineGrained.test;

import Jama.Matrix;

import java.util.Random;

/*
 * author: darcy
 * date: 2017/11/7 19:53
 * description: 
*/
public class MatrixTest {

	public static void main(String[] args) {
		int lengthOfDict = 10;

		// 生成长lengthOfDict+1, 宽为lengthOfDict+1的随机矩阵.
		Matrix m1 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		Matrix m2 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);

		// 底层数组.
		double[][] result = m1.getArray();
		/*for (int i = 0; i < lengthOfDict + 1; i++) {
			System.out.println(Arrays.toString(result[i]));
		}*/

		// 矩阵的转制.
		Matrix inverseOfM1 = m1.inverse();

		// 获取单位阵
		Matrix sMatrix = m1.times(inverseOfM1);
		double[][] sArray = sMatrix.getArray();

		for (int i = 0; i < lengthOfDict + 1; i++) {
			for (int j = 0; j < lengthOfDict + 1; j++) {
				System.out.printf("%-6f\t", Math.abs(sArray[i][j]));
			}
			System.out.println();
		}

		// 矩阵的行列式值
		System.out.println(sMatrix.det());

		Random random = new Random(31);
		m2.set(random.nextInt(lengthOfDict), random.nextInt(lengthOfDict), random.nextInt(10));
		System.out.println("randk:" + m2.rank());

		int size = 3000;
		Matrix temp = null;
		int canInverse = 0;
		for (int i = 0; i < size; i++) {
			temp = Matrix.random(size, size);
			if (temp.rank() == size) {
				canInverse++;
			} else {
				break;
			}
		}
		System.out.println(canInverse);
	}

}
