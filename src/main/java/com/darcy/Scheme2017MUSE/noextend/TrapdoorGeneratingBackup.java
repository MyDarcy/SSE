package com.darcy.Scheme2017MUSE.noextend;

import Jama.Matrix;
import com.darcy.Scheme2017MUSE.utils.MathUtils;

import java.util.*;
import java.util.regex.Matcher;

/*
 * author: darcy
 * date: 2017/11/18 20:27
 * description:
 *
 * 第三阶段: 陷门的生成.
*/
public class TrapdoorGeneratingBackup {

	/*private Initialization initialization;
	private MySecretKey mySecretKey;

	public TrapdoorGenerating(Initialization initialization, MySecretKey mySecretKey) {
		this.initialization = initialization;
		this.mySecretKey = mySecretKey;
	}*/

	private MySecretKey mySecretKey;

	public TrapdoorGeneratingBackup(MySecretKey mySecretKey) {
		this.mySecretKey = mySecretKey;
	}

	static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			return e1.getValue() - e2.getValue();
		}
	}

	/**
	 * The preference factors of keywords indicate the importance of keywords in the
	 * search keyword set personalized defined by the search user. For a search user,
	 * he may pay more attention to the preference factors of keywords defined by himself
	 * than the relevance scores of the keywords. Thus, our goal is that if a document
	 * has a keyword with larger preference factor than other documents, it should have a
	 * higher priority in the returned FID ; and for two documents, if their
	 * largest preference factor keywords are the same, the document with higher relevance
	 * score of the keyword is the better matching result.
	 * (最高偏好因子的关键字相同, 那么那么拥有相关度评分高的关键词的文档会优先返回)
	 *
	 * 关键字的偏好因子表示关键字在搜索用户定制的搜索关键字集中的重要性。 对于搜索用户，他可能比
	 * 自己定义的关键词的相关性得分更关注自己定义的关键词的偏好因子。 因此，我们的目标是，如果一个
	 * 文档具有比其他文档更大的偏好因子的关键字，则在返回的FID中应该具有更高的优先级; 对于两个文档，
	 * 如果它们的最大偏好因子关键字相同，则关键词相关度得分较高的文档就是较好的匹配结果。
	 *
	 * 关于用户的搜索关键词集合. 首先搜索关键词按照偏好因子排序, 然后构造超递增序列.
	 * 根据文章中的定理,如果F1文件中按偏好因子升序排序的最后一个关键词的偏好因子比F2文件中
	 * 按照偏好因子升序排序的最后一个关键词的偏好因子大， 那么在包含这两个关键词的query中，
	 * F1在结果中优先返回.
	 *
	 * @param query
	 * @return
	 */
	public Trapdoor generateTrapdoor(String query) {

		/**
		 * 关键词的偏好因子.
		 *
		 */
		Map<String, Integer> interestModel = new HashMap<>();
		for (String str : Initialization.dict) {
			interestModel.put(str, 0);
		}

		// Pope Francis honorary citizenship Democratic Revolution
		interestModel.put("Pope", 4);
		interestModel.put("Francis", 9);
		interestModel.put("honorary", 12);
		interestModel.put("Democratic", 13);
		interestModel.put("Revolution", 14);
		interestModel.put("citizenship", 15);

		/**
		 * 先根据重要性进行排序.这里重要性是根据用户指定的来生成的.
		 */
		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		Matrix Q = new Matrix(Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1, 1);
		// 初始化矩阵Q, 表示query在dict中的存在情况.
		/**
		 * 这里的问题是Q[0...len]都是置为0, 1; 而最末尾是一个positive整数.
		 * 那么下面切分的时候兼顾哪一个呢???
		 */
		int upper = 10000;
		// 超递增序列是关键词的偏好因子.
		double[] hyperIncreasingSequence = MathUtils.generateHyperIncreasingSequence(keywordList.size(), upper);
		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		list.addAll(interestModel.entrySet());
		ValueComparator vc = new ValueComparator();
		//
		Collections.sort(list, vc);

		/*System.out.println(list);*/

		/**
		 * 生成搜索关键词:偏好因子的映射关系.
		 */
		Map<String, Double> preferenceFactors = new HashMap<>();
		// list中的元素按照 重要性排序.
		int count = 0;
		for (Map.Entry<String, Integer> item : list) {
			// 现在list中的关键词重要性是递增的.
			// keywordList的大小和超递增序列的大小是相同的.
			// 那么就可以实现 搜索关键词和偏好因子的对应.
			int index = keywordList.indexOf(item.getKey());
			if (index != -1) {
				preferenceFactors.put(item.getKey(), hyperIncreasingSequence[count++]);

				// index = -1 找不到直接置0;
			} /*else {
				preferenceFactors.put(item.getKey(), 0.0);
			}*/
		}

		for (String keyword : preferenceFactors.keySet()) {
			int index = Initialization.dict.indexOf(keyword);
			if (index != -1) {
				Q.set(index, 0, preferenceFactors.get(keyword));
			} else {
				Q.set(index, 0, 0);
			}
		}

		/*for (String keyword : keywords) {
			if (keyword != null && !keyword.equals("")) {
				int index = Initialization.dict.indexOf(keyword);
				if (index != -1) {
					// 找到了表示存在此元素, 置0或者置为兴趣模型指定的大小, 没有找到此元素，置0;
					Q.set(index, 0, interestModel.get(Initialization.dict.get(index)));
				} else {
					Q.set(index, 0, 0);
				}
			}
		}*/
		Random random = new Random();
		int seed = 65536;
		int s = random.nextInt(seed);
		int r = random.nextInt(seed);
		Q.set(Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER, 0, -s);
		// Q 更新为 rQ
		Q = Q.times(r);
		Matrix qa = new Matrix(Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1, 1);
		Matrix qb = new Matrix(Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1, 1);

		for (int i = 0; i < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1; i++) {
			// S[i] == 0;
			if (!mySecretKey.S.get(i)) {
				/*int v1 = random.nextInt(seed);
				qa.set(i, 0, v1 );
				qb.set(i, 0, (Q.get(i, 0) - v1));*/

				double v1 = random.nextDouble();
				qa.set(i, 0, Q.get(i, 0) * v1);
				qb.set(i, 0, Q.get(i, 0) * (1 - v1));
				/**
				 * S[i] == 1;
				 */
			} else {
				qa.set(i, 0, Q.get(i, 0) );
				qb.set(i, 0, Q.get(i, 0) );
			}
		}

		System.out.println("before matirx inverse.");
		long start = System.currentTimeMillis();
		Matrix inverseM1 = mySecretKey.M1.inverse();
		Matrix inverseM2 = mySecretKey.M2.inverse();
		System.out.println("end matrix inverse. time consuming:" + (System.currentTimeMillis() - start));
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
