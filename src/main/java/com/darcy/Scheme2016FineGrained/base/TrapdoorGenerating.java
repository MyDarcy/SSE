package com.darcy.Scheme2016FineGrained.base;

import Jama.Matrix;

import java.util.Random;

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

	/**
	 * 最后的结果一般都是负值, 不知道为什么, 感觉应该是 P*Q - s的时候 s太大了， P*Q的成绩 - 一个大数为负值。
	 *
	 * @param query
	 * @return
	 */
	public Trapdoor generateTrapdoor(String query) {
		String[] keywords = query.split("\\s+");
		Matrix Q = new Matrix(Initialization.lengthOfDict + 1, 1);
		// 初始化矩阵Q, 表示query在dict中的存在情况.
		/**
		 * 这里的问题是Q[0...len]都是置为0, 1; 而最末尾是一个positive整数.
		 * 那么下面切分的时候兼顾哪一个呢???
		 */
		for (String keyword : keywords) {
			if (keyword != null && !keyword.equals("")) {
				System.out.println(keyword);
				int index = Initialization.dict.indexOf(keyword);
				if (index != -1) {
					// 找到了表示存在此元素, 置1, 没有找到此元素，置0;
					Q.set(index, 0, 1);

					// 找不到, 不处理.
				}
				/* else {
					Q.set(index, 0, 0);
				}*/
			}
		}
		Random random = new Random();
		/*int seed = 65536;*/
		int seed = Integer.MAX_VALUE;
		int s = 1 + random.nextInt(Integer.MAX_VALUE);
		int r = 1 + random.nextInt(Integer.MAX_VALUE); // Integer.MAX_VALUE
		Q.set(Initialization.lengthOfDict, 0, -s);
		// Q 更新为 rQ
		Q = Q.times(r);
		Matrix qa = new Matrix(Initialization.lengthOfDict + 1, 1);
		Matrix qb = new Matrix(Initialization.lengthOfDict + 1, 1);

		for (int i = 0; i < Initialization.lengthOfDict + 1; i++) {
			// S[i] == 0;
			if (!mySecretKey.S.get(i)) {
				/*int v1 = random.nextInt(seed);
				qa.set(i, 0, v1);
				qb.set(i, 0, Q.get(i, 0) - v1);*/

				double v1 = random.nextDouble();
				qa.set(i, 0, v1);
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
