package com.darcy.Scheme2018PLVMSE.example.pv_base_2;

import Jama.Matrix;
import com.darcy.Scheme2018PLVMSE.utils.MathUtils;
import com.darcy.Scheme2018PLVMSE.utils.MatrixUitls;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/*
 * author: darcy
 * date: 2017/11/18 20:27
 * description:
 *
 * 第三阶段: 陷门的生成.
*/
public class TrapdoorGenerating {

	private MySecretKey mySecretKey;
	public Initialization initialization;

	public TrapdoorGenerating(MySecretKey mySecretKey) {
		this.mySecretKey = mySecretKey;
	}

	public TrapdoorGenerating(MySecretKey mySecretKey, Initialization initialization) {
		this.mySecretKey = mySecretKey;
		this.initialization = initialization;
	}

	static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			return e1.getValue() - e2.getValue();
		}
	}

	/**
	 * for test.
	 * no prefer factor but 0 or 1.
	 * @param query
	 * @return
	 */
	public Trapdoor generateTrapdoor2(String query) {
		System.out.println("TrapdoorGenerating trapdoorGenerating start.");
		long start = System.currentTimeMillis();

		/**
		 * 先根据重要性进行排序.这里重要性是根据用户指定的来生成的.
		 */
		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		Matrix Q = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);

		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			int index = initialization.dict.indexOf(keyword);
			if (index != -1) {
				Q.set(index, 0, 1);
			}
		}

		for (int i = 0; i < initialization.DUMMY_KEYWORD_NUMBER; i++) {
			String str = initialization.extendDummyDict.get(i);
			int index = initialization.dict.indexOf(str);
			if (index != -1) {
				// 设置一部分bit为1.
				if (Initialization.RANDOM.nextBoolean()) {
					Q.set(index, 0, 1);
				}
			}
		}

		System.out.println("Q Qa Qb transponse.");
		MatrixUitls.print(Q.transpose());


		Random random = new Random(31);

		Matrix qa = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);
		Matrix qb = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);

		for (int i = 0; i < initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER; i++) {
			// S[i] == 1;
			if (mySecretKey.S.get(i)) {
				double v1 = random.nextDouble();
				qa.set(i, 0, Q.get(i, 0) * v1);
				qb.set(i, 0, Q.get(i, 0) * (1 - v1));

				//S[i] == 0;
			} else {
				qa.set(i, 0, Q.get(i, 0));
				qb.set(i, 0, Q.get(i, 0));
			}
		}

		/*MatrixUitls.print(qa.transpose());
		MatrixUitls.print(qb.transpose());*/

		Matrix part1 = AuxiliaryMatrix.M1Inverse.times(qa);
		Matrix part2 = AuxiliaryMatrix.M2Inverse.times(qb);
		MatrixUitls.print(part1.transpose());
		MatrixUitls.print(part2.transpose());
		System.out.println("generate trapdoor total time:" + (System.currentTimeMillis() - start));
		System.out.println("TrapdoorGenerating trapdoorGenerating finished.");
		return new Trapdoor(part1, part2);
	}

	/**
	 * @param query
	 * @return
	 */
	public Trapdoor generateTrapdoor(String query) {
		System.out.println("TrapdoorGenerating trapdoorGenerating start.");
		long start = System.currentTimeMillis();

		/**
		 * 关键词的偏好因子.
		 *
		 */
		Map<String, Integer> interestModel = new HashMap<>();
		for (String str : initialization.dict) {
			interestModel.put(str, 0);
		}

		interestModel.put("c", 2);
		interestModel.put("cpp", 5);
		interestModel.put("javascript", 1);
		interestModel.put("python", 8);
		interestModel.put("java", 7);
		interestModel.put("go", 10);
		interestModel.put("scala", 6);


		/**
		 * 先根据重要性进行排序.这里重要性是根据用户指定的来生成的.
		 */
		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		Matrix Q = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);

		// 超递增序列是关键词的偏好因子.
		double factor = 0.2;
		double[] hyperIncreasingSequence = MathUtils.generateHyperIncreasingSequence(keywordList.size(), factor);
		List<Map.Entry<String, Integer>> keyPreferenceList = new ArrayList<>();
		keyPreferenceList.addAll(interestModel.entrySet());
		ValueComparator vc = new ValueComparator();
		Collections.sort(keyPreferenceList, vc);

		/**
		 * 生成搜索关键词:偏好因子的映射关系.
		 */
		Map<String, Double> preferenceFactors = new HashMap<>();
		// list中的元素按照 重要性排序.
		int count = 0;
		for (Map.Entry<String, Integer> item : keyPreferenceList) {
			// 现在list中的关键词重要性是递增的.
			// keywordList的大小和超递增序列的大小是相同的.
			// 那么就可以实现 搜索关键词和偏好因子的对应.
			int index = keywordList.indexOf(item.getKey());
			if (index != -1) {
				double pFactor = hyperIncreasingSequence[count++];
				System.out.printf("%-20s%-15d%.8f\n", item.getKey(), interestModel.get(item.getKey()), pFactor);
				preferenceFactors.put(item.getKey(), pFactor);
				// 找不到, 那么构建查询陷门也用不到此关键词
			}
		}
		/*
		voice               1              1.00000000
		broadcasting        2              22.77136412
		hospital            2              136.60041943
		interview           3              817.03708997
		church              4              4890.57318302
		citizenship         4              29362.13188629
		performance         5              176161.98234286
		democratic          5              1056982.66025510
		china               9              6341892.87149841
		british             17             38051349.95250095
		 */
		System.out.println();
		// Map<String, Double> idfs = generateIDFs(keywordList);

		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			int index = initialization.dict.indexOf(keyword);
			if (index != -1) {
				Double preferenceFacotr = preferenceFactors.get(keyword);
				System.out.printf("%-20s%-15s%.8f\n", keyword, "preference", preferenceFacotr);
				/*Q.set(index, 0, idfs.get(keyword));*/

				Q.set(index, 0, preferenceFacotr);

				/*System.out.printf("%-10s\t%-10s\t%.8f\n", keyword, "Q.get(index, 0)", Q.get(index, 0));*/
			}
		}
		/*
		church              preference     4890.57318302
		china               preference     6341892.87149841
		hospital            preference     136.60041943
		performance         preference     176161.98234286
		british             preference     38051349.95250095
		interview           preference     817.03708997
		democratic          preference     1056982.66025510
		citizenship         preference     29362.13188629
		broadcasting        preference     22.77136412
		voice               preference     1.00000000
		 */

		// 之前一直都忘记了这一部分。
		for (int i = 0; i < initialization.DUMMY_KEYWORD_NUMBER; i++) {
			String str = initialization.extendDummyDict.get(i);
			int index = initialization.dict.indexOf(str);
			if (index != -1) {
				// 设置一部分bit为1.
				if (Initialization.RANDOM.nextBoolean()) {
					Q.set(index, 0, 1);
				}
			}
		}

		System.out.println("Q Qa Qb transponse.");
		MatrixUitls.print(Q.transpose());


		Random random = new Random(31);

		Matrix qa = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);
		Matrix qb = new Matrix(initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER, 1);

		for (int i = 0; i < initialization.DICTIONARY_SIZE + initialization.DUMMY_KEYWORD_NUMBER; i++) {
			// S[i] == 1;
			if (mySecretKey.S.get(i)) {
				double v1 = random.nextDouble();
				qa.set(i, 0, Q.get(i, 0) * v1);
				qb.set(i, 0, Q.get(i, 0) * (1 - v1));

				//S[i] == 0;
			} else {
				qa.set(i, 0, Q.get(i, 0));
				qb.set(i, 0, Q.get(i, 0));
			}
		}

		/*MatrixUitls.print(qa.transpose());
		MatrixUitls.print(qb.transpose());*/

		Matrix part1 = AuxiliaryMatrix.M1Inverse.times(qa);
		Matrix part2 = AuxiliaryMatrix.M2Inverse.times(qb);
		MatrixUitls.print(part1.transpose());
		MatrixUitls.print(part2.transpose());
		System.out.println("generate trapdoor total time:" + (System.currentTimeMillis() - start));
		System.out.println("TrapdoorGenerating trapdoorGenerating finished.");
		return new Trapdoor(part1, part2);
	}

	private Map<String, Double> generateIDFs(List<String> keywordList) {
		double sum = 0;
		// 文档的个数.
		List<Double> keywordIDFLists = new ArrayList<>(keywordList.size());
		int fileNumber = initialization.fileLength.size();
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (initialization.dict.contains(keyword)) {
				// sum(ln(1 + m / fwi) ^ 2)
				double idf = Math.log(1 + fileNumber / initialization.numberOfDocumentContainsKeyword.get(keyword));
				sum += Math.pow(idf, 2);
				keywordIDFLists.add(idf);
			} else {
				keywordIDFLists.add(0.0);
			}
		}

		double denominator = Math.sqrt(sum);
		Map<String, Double> idfs = new HashMap<>(keywordList.size());
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			idfs.put(keyword, keywordIDFLists.get(i));
		}

		return idfs;
	}

	private double idfDenominator(List<String> keywordList) {
		double sum = 0;
		// 文档的个数.
		int fileNumber = initialization.fileLength.size();
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (initialization.dict.contains(keyword)) {
				// sum(ln(1 + m / fwi) ^ 2)
				sum += Math.pow(Math.log(1 + fileNumber / initialization.numberOfDocumentContainsKeyword.get(keyword)), 2);
			}
		}
		return Math.sqrt(sum);
	}

	public static void main(String[] args) throws IOException {
		Initialization initialization = new Initialization();
		MySecretKey mySecretKey = initialization.getMySecretKey();
		TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey, initialization);
		String query = "Pope Francis honorary citizenship Democratic Revolution clinton owners oversee would half pick";
		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		System.out.println(initialization.dict.contains("clinton"));
		System.out.println(initialization.numberOfDocumentContainsKeyword.keySet().contains("clinton"));

		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (initialization.dict.contains(keyword)) {
				System.out.printf("%-15s\t %6d\n", keyword, initialization.numberOfDocumentContainsKeyword.get(keyword));
			} else {
				System.out.printf("%-15s\t %6d\n", keyword, 0);
			}
		}

		Map<String, Double> idfs = trapdoorGenerating.generateIDFs(keywordList);
		for (int i = 0; i < keywordList.size(); i++) {
			System.out.printf("%-15s\t %.6f\n", keywordList.get(i), idfs.get(keywordList.get(i)));
		}
	}
}
