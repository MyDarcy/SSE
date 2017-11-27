package com.darcy.Scheme2016FineGrained.FMS_I;

import Jama.Matrix;

import java.util.BitSet;

/*
 * author: darcy
 * date: 2017/11/7 16:56
 * description: 
*/
public class MySecretKey {

	public BitSet S;
	// public int[][] M1;
	// public int[][] M2;

	public Matrix M1;
	public Matrix M2;

	@Override
	public String toString() {
		return "MySecretKey{" +
				"S.length=" + S.length() +
				", M1.rank=" + M1.rank() +
				", M2.rank=" + M2.rank() +
				'}';
	}

}
