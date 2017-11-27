package com.darcy.Scheme2016FineGrained.FMS_II;

import Jama.Matrix;

/*
 * author: darcy
 * date: 2017/11/8 21:27
 * description: 
*/
public class Index {

	public Matrix indexPart1;
	public Matrix indexPart2;

	public Index(Matrix part1, Matrix part2) {
		this.indexPart1 = part1;
		this.indexPart2 = part2;
	}

	@Override
	public String toString() {
		double[][] a1 = indexPart1.getArray();
		double[][] a2 = indexPart2.getArray();
		for (int i = 0; i < a1.length; i++) {
			for (int j = 0; j < a1[i].length; j++) {
				System.out.printf("%-6f\t", a1[i][j]);
			}
			System.out.println();
		}

		for (int i = 0; i < a2.length; i++) {
			for (int j = 0; j < a2[i].length; j++) {
				System.out.printf("%-6f\t", a2[i][j]);
			}
			System.out.println();
		}
		return null;
	}

	public double multiply(Trapdoor trapdoor) {
		Matrix result = indexPart1.times(trapdoor.trapdoorPart1).plus(indexPart2.times(trapdoor.trapdoorPart2));
		return result.get(0, 0);
	}
}
