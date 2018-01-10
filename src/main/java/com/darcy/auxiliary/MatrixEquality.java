package com.darcy.auxiliary;

import Jama.Matrix;

import java.util.BitSet;
import java.util.Random;

/*
 * author: darcy
 * date: 2017/12/24 17:03
 * description: 
*/
public class MatrixEquality {
	public static final int MATRIX_SIZE = 10;
	public static final Random RANDOM = new Random(System.currentTimeMillis());
	private static Matrix m1;
	private static Matrix m2;
	private static Matrix m1Transpose;
	private static Matrix m2Transpose;
	private static Matrix m1Inverse;
	private static Matrix m2Inverse;
	private static BitSet s;

	public static void main(String[] args) {
		s = new BitSet();
		for (int i = 0; i < MATRIX_SIZE; i++) {
			if (RANDOM.nextBoolean()) {
				s.set(i);
			}
		}
		s.set(MATRIX_SIZE);
		System.out.println(s.length());

		m1 = Matrix.random(MATRIX_SIZE, MATRIX_SIZE);
		m2 = Matrix.random(MATRIX_SIZE, MATRIX_SIZE);
		m1Transpose = m1.transpose();
		m2Transpose = m2.transpose();

		m1Inverse = m1.inverse();
		m2Inverse = m2.inverse();

		double[][] P1array = new double[][]{
				{1.0, 0.0, 2.0, 0.0, 0.0, 3.0, 4.0, 0.0, 0.0, 5.0}};
		double[][] P2array = {{1.0}, {0.0}, {2.0}, {0.0}, {0.0}, {3.0}, {4.0}, {0.0}, {0.0}, {5.0}};

		double[][] Q1array = new double[][]{{1.0}, {1.0}, {2.0}, {3.0}, {4.0}, {0.0}, {0.0}, {0.0}, {4.0}, {5.0}};

		testEquality1(P1array, Q1array);
		System.out.println();
		testEquality2(P2array, Q1array);

	}

	private static void testEquality2(double[][] p2array, double[][] q1array) {
		// M * 1
		Matrix p = new Matrix(p2array);
		// M * 1
		Matrix q = new Matrix(q1array);
		Matrix pa = new Matrix(MATRIX_SIZE, 1);
		Matrix pb = new Matrix(MATRIX_SIZE, 1);

		Matrix qa = new Matrix(MATRIX_SIZE, 1);
		Matrix qb = new Matrix(MATRIX_SIZE, 1);
		for (int j = 0; j < MATRIX_SIZE; j++) {
			// 置0
			if (!s.get(j)) {
				double rand = RANDOM.nextDouble();
				double v = 1.0 / 2.0 * p.get(j, 0);
				pa.set(j,0,v + rand);
				pb.set(j, 0, v - rand);
				// 置1
			} else {
				pa.set(j, 0, p.get(j, 0));
				pb.set(j, 0, p.get(j, 0));
			}
		}

		for (int i = 0; i < MATRIX_SIZE; i++) {
			// S[i] == 1;
			if (s.get(i)) {
				double rand = RANDOM.nextDouble();
				double v = 1.0 / 2.0 * q.get(i, 0);
				qa.set(i, 0, v + rand);
				qb.set(i, 0, v - rand);

				//S[i] == 0;
			} else {
				qa.set(i, 0, q.get(i, 0));
				qb.set(i, 0, q.get(i, 0));
			}
		}

		double managedValue = m1.transpose().times(pa).transpose().times(m1Inverse.times(qa)).get(0, 0)
				+ m2.transpose().times(pb).transpose().times(m2Inverse.times(qb)).get(0, 0);
		double value = p.transpose().times(q).get(0, 0);
		System.out.println("managedValue:" + managedValue);
		System.out.println("value:" + value);
	}

	private static void testEquality1(double[][] p1array, double[][] q1array) {
		// 1 * M
		Matrix p = new Matrix(p1array);
		// M * 1
		Matrix q = new Matrix(q1array);
		Matrix pa = new Matrix(1, MATRIX_SIZE);
		Matrix pb = new Matrix(1, MATRIX_SIZE);

		Matrix qa = new Matrix(MATRIX_SIZE, 1);
		Matrix qb = new Matrix(MATRIX_SIZE, 1);
		for (int j = 0; j < MATRIX_SIZE; j++) {
			// 置0
			if (!s.get(j)) {
				double rand = RANDOM.nextDouble();
				double v = 1.0 / 2.0 * p.get(0, j);
				pa.set(0, j,v + rand);
				pb.set(0, j, v - rand);
				// 置1
			} else {
				pa.set(0, j, p.get(0, j));
				pb.set(0, j, p.get(0, j));
			}
		}

		for (int i = 0; i < MATRIX_SIZE; i++) {
			// S[i] == 1;
			if (s.get(i)) {
				double rand = RANDOM.nextDouble();
				double v = 1.0 / 2.0 * q.get(i, 0);
				qa.set(i, 0, v + rand);
				qb.set(i, 0, v - rand);

				//S[i] == 0;
			} else {
				qa.set(i, 0, q.get(i, 0));
				qb.set(i, 0, q.get(i, 0));
			}
		}

		double managedValue = pa.times(m1Transpose).times(m1Inverse.times(qa)).get(0, 0)
				+ pb.times(m1Transpose).times(m2Inverse.times(qb)).get(0, 0);
		double value = p.times(q).get(0, 0);
		System.out.println("managedValue:" + managedValue);
		System.out.println("value:" + value);
	}
}
