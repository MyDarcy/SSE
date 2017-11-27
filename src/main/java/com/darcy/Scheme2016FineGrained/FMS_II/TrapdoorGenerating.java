package com.darcy.Scheme2016FineGrained.FMS_II;

import Jama.Matrix;
import com.darcy.Scheme2016FineGrained.utils.MathUtils;

import java.util.*;

/*
 * author: darcy
 * date: 2017/11/8 21:27
 * description:
 *
 * 第三阶段: 陷门的生成.
*/
public class TrapdoorGenerating {

	/**
	 * initialization应该只提供一个实例.
	 */
	private Initialization initialization;
	private MySecretKey mySecretKey;

	public TrapdoorGenerating(Initialization initialization, MySecretKey mySecretKey) {
		this.initialization = initialization;
		this.mySecretKey = mySecretKey;
	}

	static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			return e1.getValue() - e2.getValue();
		}
	}

	/**
	 * @param query
	 * @return
	 */
	public Trapdoor generateTrapdoor(String query) {
		Random random = new Random();
		int seed = 65536;

		/**
		 * FMS_II中需要关键词按照重要性排序, 但是单纯的逻辑操作时跟关键词的重要性没有关系.
		 * 后面这里可能需要调整.
		 */
		/*List<Map.Entry<String, Integer>> list = new ArrayList<>();
		list.addAll(null);
		ValueComparator vc = new ValueComparator();
		Collections.sort(list, vc);*/

		/*System.out.println(list);*/

		String[] orKeywords = null;
		String[] andKeywords = null;
		String[] notKeywords = null;

		double[] orHyperIncreasingSequence = null;
		double[] andHyperIncreasingSequence = null;
		double[] notHyperIncreasingSequence = null;

		int orCharIndex = query.indexOf('!');
		// 说明查询支付字符串中有or操作符.
		if (orCharIndex != -1) {
			int last = query.indexOf(')', orCharIndex);
			String temp = query.substring(orCharIndex + 2, last);
			orKeywords = temp.split("\\s+");
		}

		int andCharIndex = query.indexOf('&');
		if (andCharIndex != -1) {
			int last = query.indexOf(')', andCharIndex);
			String temp = query.substring(andCharIndex + 2, last);
			andKeywords = temp.split("\\s+");
		}

		int notCharIndex = query.indexOf('!');
		if (notCharIndex != -1) {
			int last = query.indexOf(')', notCharIndex);
			String temp = query.substring(notCharIndex + 2, last);
			notKeywords = temp.split("\\s+");
		}


		int upper = 100;
		// 生成or关键词所对应的超递增序列.
		if (orKeywords != null) {
			orHyperIncreasingSequence = MathUtils.generateHyperIncreasingSequence(orKeywords.length, upper);
		}

		if (andKeywords != null) {
			andHyperIncreasingSequence = new double[andKeywords.length];
			if (orHyperIncreasingSequence != null) {
				double sum = Arrays.stream(orHyperIncreasingSequence).reduce(Double::sum).getAsDouble();
				for (int i = 0; i < andKeywords.length; i++) {
					andHyperIncreasingSequence[i] = sum + upper * random.nextInt(upper);
				}
			}
		}

		if (notKeywords != null) {
			double orSum = 0;
			double andSum = 0;
			notHyperIncreasingSequence = new double[notKeywords.length];
			if (orHyperIncreasingSequence != null) {
				orSum = Arrays.stream(orHyperIncreasingSequence).reduce(Double::sum).getAsDouble();
			}
			if (andHyperIncreasingSequence != null) {
				andSum = Arrays.stream(andHyperIncreasingSequence).reduce(Double::sum).getAsDouble();
			}

			for (int i = 0; i < notKeywords.length; i++) {
				notHyperIncreasingSequence[i] = -(orSum + andSum + upper * random.nextInt(upper));
			}
		}

		/**
		 * 根据FMS_II的思路,
		 * s是and所对应的关键词的超递增序列的和.
		 */
		double s = 0;
		if (andHyperIncreasingSequence != null) {
			s = Arrays.stream(andHyperIncreasingSequence).reduce(Double::sum).getAsDouble();
		}
		// r作为任意的随机数即可.
		int r = random.nextInt(seed);

		Matrix Q = new Matrix(Initialization.lengthOfDict + 1, 1);

		/**
		 * the corresponding values in Q are set as
		 * (a1, a2,.. al1; b1, b2...bl2; -c1,-c2...-cl3).
		 * Other values in Q are set as 0.
		 */
		int count = 0;
		if (orHyperIncreasingSequence != null) {
			for (String keyword : orKeywords) {
				int index = Initialization.dict.indexOf(keyword);
				if (index != -1) {
					Q.set(index, 0, orHyperIncreasingSequence[count++]);
				}
			}
		}

		count = 0;
		if (andHyperIncreasingSequence != null) {
			for (String keyword : andKeywords) {
				int index = Initialization.dict.indexOf(keyword);
				if (index != -1) {
					Q.set(index, 0, andHyperIncreasingSequence[count++]);
				}
			}
		}

		count = 0;
		if (notHyperIncreasingSequence != null) {
			for (String keyword : notKeywords) {
				int index = Initialization.dict.indexOf(keyword);
				if (index != -1) {
					Q.set(index, 0, notHyperIncreasingSequence[count++]);
				}
			}
		}


		Q.set(Initialization.lengthOfDict, 0, -s);
		// Q 更新为 rQ
		Q = Q.times(r);
		Matrix qa = new Matrix(Initialization.lengthOfDict + 1, 1);
		Matrix qb = new Matrix(Initialization.lengthOfDict + 1, 1);

		for (int i = 0; i < Initialization.lengthOfDict + 1; i++) {
			// S[i] == 0;
			if (!mySecretKey.S.get(i)) {
				double v1 = random.nextDouble();
				qa.set(i, 0, Q.get(i, 0) * v1);
				qb.set(i, 0, Q.get(i, 0) * (1 - v1));

				/**
				 * S[i] == 1;
				 */
			} else {
				qa.set(i, 0, Q.get(i, 0));
				qb.set(i, 0, Q.get(i, 0));
			}
		}

		Matrix inverseM1 = mySecretKey.M1.inverse();
		Matrix inverseM2 = mySecretKey.M2.inverse();

		/*System.out.println(mySecretKey.M1.getRowDimension() + "\t" + mySecretKey.M2.getColumnDimension());
		System.out.println(inverseM1.getRowDimension() + "\t" +inverseM2.getColumnDimension());
		System.out.println(qa.getRowDimension() + "\t" +qb.getColumnDimension());*/

		Matrix part1 = inverseM1.times(qa);
		Matrix part2 = inverseM2.times(qb);
		return new Trapdoor(part1, part2);
	}

	public static void main(String[] args) {

	}
}
