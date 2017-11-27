package com.darcy.Scheme2016FineGrained.test;

import java.util.BitSet;
import java.util.Random;

/*
 * author: darcy
 * date: 2017/11/7 17:00
 * description:
*/
public class BitSetTest {

	public static void main(String[] args) {
		int length = 100;
		BitSet bitSet = new BitSet(length + 1);

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			if (random.nextBoolean()) {
				bitSet.set(i);
			}
		}

		bitSet.set(length);

		System.out.println(bitSet);
		System.out.println(bitSet.size());
		System.out.println(bitSet.length());
		for (int i = 0; i < bitSet.length(); i++) {
			/*BitSet bitSet1 = bitSet.get(1, 10);
			System.out.println(bitSet1);*/

			if (bitSet.get(i)) {
				System.out.print(1 + "\t");
			} else {
				System.out.print(0 + "\t");
			}
			if ((i + 1) % 10 == 0) {
				System.out.println();
			}
		}
	}
}
