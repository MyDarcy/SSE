package com.darcy.auxiliary;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.util.Arrays;

/*
 * author: darcy
 * date: 2017/12/20 16:07
 * description: 
*/
public class UniformDistributionTest {
	public static void main(String[] args) {
		RealDistribution realDistribution = new UniformRealDistribution(-0.01, 0.01);
		double[] sample = realDistribution.sample(10);
		System.out.println(Arrays.toString(sample));

	}
}
