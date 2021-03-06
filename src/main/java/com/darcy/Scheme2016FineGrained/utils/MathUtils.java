package com.darcy.Scheme2016FineGrained.utils;

import java.util.Arrays;
import java.util.Random;

/*
 * author: darcy
 * date: 2017/11/20 14:35
 * description: 
*/
public class MathUtils {

	/**
	 * 生成超递增序列. 要生成的超递增序列数组的大小为 size; 上届为upper.
	 * @param size
	 * @param upper
	 * @return
	 */
	public static double[] generateHyperIncreasingSequence(int size, int upper) {
		double[] array = new double[size];
		double sum = 1;
		double value = 1;
		Random random = new Random(31);
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				// 累加和上引入随机数.
				value = sum * upper + upper * random.nextInt(10);
			}
			array[i] = value;
			sum += value;
		}
		return array;
	}

	public static void main(String[] args) {
		double[] array = generateHyperIncreasingSequence(10, 5);
		System.out.println(Arrays.toString(array));
	}

}
