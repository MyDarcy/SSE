package com.hankcs.textrank;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * TextRank 自动摘要
 *
 * @author hankcs
 */
public class TextRankSummary {
	/**
	 * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
	 */
	final double d = 0.85f;
	/**
	 * 最大迭代次数
	 */
	final int max_iter = 200;
	final double min_diff = 0.001f;
	/**
	 * 文档句子的个数
	 */
	int D;
	/**
	 * 拆分为[句子[单词]]形式的文档
	 */
	List<List<String>> docs;
	/**
	 * 排序后的最终结果 score <-> index
	 */
	TreeMap<Double, Integer> top;

	/**
	 * 句子和其他句子的相关程度
	 */
	double[][] weight;
	/**
	 * 该句子和其他句子相关程度之和
	 */
	double[] weight_sum;
	/**
	 * 迭代之后收敛的权重
	 */
	double[] vertex;

	/**
	 * BM25相似度
	 */
	BM25 bm25;

	public TextRankSummary(List<List<String>> docs) {
		this.docs = docs;
		bm25 = new BM25(docs);
		D = docs.size();
		weight = new double[D][D];
		weight_sum = new double[D];
		vertex = new double[D];
		top = new TreeMap<Double, Integer>(Collections.reverseOrder());
		solve();
	}

	private void solve() {
		int cnt = 0;
		for (List<String> sentence : docs) {
			double[] scores = bm25.simAll(sentence);
//            System.out.println(Arrays.toString(scores));
			weight[cnt] = scores;
			weight_sum[cnt] = sum(scores) - scores[cnt]; // 减掉自己，自己跟自己肯定最相似
			vertex[cnt] = 1.0;
			++cnt;
		}
		for (int i1 = 0; i1 < max_iter; ++i1) {
			double[] m = new double[D];
			double max_diff = 0;
			for (int i = 0; i < D; ++i) {
				m[i] = 1 - d;
				for (int j = 0; j < D; ++j) {
					if (j == i || weight_sum[j] == 0) {continue;}
					m[i] += (d * weight[j][i] / weight_sum[j] * vertex[j]);
				}
				double diff = Math.abs(m[i] - vertex[i]);
				if (diff > max_diff) {
					max_diff = diff;
				}
			}
			vertex = m;
			if (max_diff <= min_diff){ break;}
		}
		// 我们来排个序吧
		for (int i = 0; i < D; ++i) {
			top.put(vertex[i], i);
		}
	}

	/**
	 * 获取前几个关键句子
	 *
	 * @param size 要几个
	 * @return 关键句子的下标
	 */
	public int[] getTopSentence(int size) {
		Collection<Integer> values = top.values();
		size = Math.min(size, values.size());
		int[] indexArray = new int[size];
		Iterator<Integer> it = values.iterator();
		for (int i = 0; i < size; ++i) {
			indexArray[i] = it.next();
		}
		return indexArray;
	}

	/**
	 * 简单的求和
	 *
	 * @param array
	 * @return
	 */
	private static double sum(double[] array) {
		double total = 0;
		for (double v : array) {
			total += v;
		}
		return total;
	}

	public static void main(String[] args) {
		String document = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、" +
				"动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、" +
				"随机森林算法。\n算法可以宽泛的分为三类，\n" +
				"一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，" +
				"但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
				"二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法" +
				"的结果并不是唯一的或确定的。\n" +
				"三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行" +
				"的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";

		String document2 =
				"However, China alleges the men are part of the East Turkestan Islamic Movement " +
						"-- a group the U.S. State Department considers a terrorist organization -- " +
						"that operates in the Xinjiang region. East Turkestan is another name for Xinjiang.\n" +
						"China on Thursday urged the United States to hand over all 17 of the Uyghurs instead " +
						"of sending them elsewhere. The Chinese statement followed an offer by Palau, a Pacific " +
						"island nation, to accept the Uyghur detainees.\n" +
						"The Xinjiang region of 20 million people is largely populated by ethnic Uyghurs and" +
						" other Muslim minorities who have traditionally opposed Beijing's rule and clamored " +
						"for greater autonomy.\n" +
						"A senior U.S. administration official told CNN the State Department is working on a" +
						" final agreement with Palau to settle the matter of the 13 remaining Uyghur detainees.\n" +
						"Issues to be worked out include how to transfer the Uyghurs to Palau and how much money" +
						" the United States would give the men for resettlement, the official said.\n";

		List<String> topSentenceList = TextRankSummary.getTopSentenceList(document, 3);
		System.out.println(topSentenceList.size());
		for (int i = 0; i < topSentenceList.size(); i++) {
			System.out.println(topSentenceList.get(i));
		}
	}

	/**
	 * 将文章分割为句子
	 *
	 * @param document
	 * @return
	 */
	static List<String> spiltSentence(String document) {
		List<String> sentences = new ArrayList<String>();
		if (document == null) {
			return sentences;
		}

		for (String line : document.split("[\r\n]")) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			for (String sent : line.split("[，,。:：“”？?！!；;]")) {
				sent = sent.trim();
				if (sent.length() == 0) {
					continue;
				}
				sentences.add(sent);
			}
		}

		return sentences;
	}

	/**
	 * 是否应当将这个term纳入计算，词性属于名词、动词、副词、形容词
	 *
	 * @param term
	 * @return 是否应当
	 */
	public static boolean shouldInclude(Term term) {
		return CoreStopWordDictionary.shouldInclude(term);
	}

	/**
	 * 一句话调用接口
	 *
	 * @param document 目标文档
	 * @param size     需要的关键句的个数
	 * @return 关键句列表
	 */
	public static List<String> getTopSentenceList(String document, int size) {
		List<String> sentenceList = spiltSentence(document);
		List<List<String>> docs = new ArrayList<List<String>>();
		for (String sentence : sentenceList) {
			List<Term> termList = HanLP.segment(sentence);
			List<String> wordList = new LinkedList<String>();
			for (Term term : termList) {
				if (shouldInclude(term)) {
					wordList.add(term.word);
				}
			}
			docs.add(wordList);
		}
		TextRankSummary textRankSummary = new TextRankSummary(docs);
		int[] topSentence = textRankSummary.getTopSentence(size);
		List<String> resultList = new LinkedList<String>();
		for (int i : topSentence) {
			resultList.add(sentenceList.get(i));
		}
		return resultList;
	}
}
