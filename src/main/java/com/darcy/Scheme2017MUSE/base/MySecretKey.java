package com.darcy.Scheme2017MUSE.base;

import Jama.Matrix;

import javax.crypto.SecretKey;
import java.util.BitSet;

/*
 * author: darcy
 * date: 2017/11/7 16:56
 * description: 
*/
public class MySecretKey {

	public BitSet S;
	public Matrix M1;
	public Matrix M2;
	public SecretKey secretKey;

	@Override
	public String toString() {
		return "MySecretKey{" +
				"S.length=" + S.length() +
				", M1.rank=" + M1.rank() +
				", M2.rank=" + M2.rank() +
				'}';
	}

}
