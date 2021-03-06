package com.darcy.linux.accelerate.extend;

import com.darcy.linux.accelerate.DiagonalMatrixUtils;

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

	public TrapdoorGenerating(MySecretKey mySecretKey) {
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
		System.out.println("TrapdoorGenerating trapdoorGenerating start.");
		long start = System.currentTimeMillis();

		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		Map<String, Double> idfs = generateIDFs(keywordList);

		double[] Q = new double[Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER];


		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			int index = Initialization.dict.indexOf(keyword);
			if (index != -1) {
				System.out.printf("%-10s\t%-10s\t%.8f\n", keyword, "idf-value", idfs.get(keyword));
				Q[index] = idfs.get(keyword);
				/*System.out.printf("%-10s\t%-10s\t%.8f\n", keyword, "Q.get(index, 0)", Q.get(index, 0));*/
			}
		}

		// 之前一直都忘记了这一部分。
		for (int i = 0; i < Initialization.DUMMY_KEYWORD_NUMBER; i++) {
			String str = Initialization.extendDummyDict.get(i);
			int index = Initialization.dict.indexOf(str);
			if (index != -1) {
				// 设置一部分bit为1.
				if (Initialization.RANDOM.nextBoolean()) {
					Q[index] = 1;
				}
			}
		}

		/*System.out.println("Q Qa Qb transponse.");
		MatrixUitls.print(Q.transpose());*/


		Random random = new Random(31);

		double[] qa = new double[Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER];
		double[] qb = new double[Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER];


		for (int i = 0; i < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER; i++) {
			// S[i] == 1;
			if (mySecretKey.S.get(i)) {
				double v1 = random.nextDouble();
				qa[i] = Q[i] * v1;
				qb[i] = Q[i] * (1 - v1);

				//S[i] == 0;
			} else {
				qa[i] = Q[i];
				qb[i] = Q[i];
			}
		}

		/*MatrixUitls.print(qa.transpose());
		MatrixUitls.print(qb.transpose());*/

		/*System.out.println(mySecretKey.M1.getRowDimension() + "\t" + mySecretKey.M2.getColumnDimension());
		System.out.println(inverseM1.getRowDimension() + "\t" +inverseM2.getColumnDimension());
		System.out.println(qa.getRowDimension() + "\t" +qb.getColumnDimension());*/

		double[] part1 = DiagonalMatrixUtils.times(AuxiliaryMatrix.M1Inverse, qa);
		double[] part2 = DiagonalMatrixUtils.times(AuxiliaryMatrix.M2Inverse, qb);

		System.out.println("generate trapdoor total time:" + (System.currentTimeMillis() - start));
		System.out.println("TrapdoorGenerating trapdoorGenerating finished.");
		return new Trapdoor(part1, part2);
	}

	private Map<String, Double> generateIDFs(List<String> keywordList) {
		double sum = 0;
		// 文档的个数.
		List<Double> keywordIDFLists = new ArrayList<>(keywordList.size());
		int fileNumber = Initialization.fileLength.size();
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (Initialization.dict.contains(keyword)) {
				// sum(ln(1 + m / fwi) ^ 2)
				double idf = Math.log(1 + fileNumber / Initialization.numberOfDocumentContainsKeyword.get(keyword));
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
		int fileNumber = Initialization.fileLength.size();
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (Initialization.dict.contains(keyword)) {
				// sum(ln(1 + m / fwi) ^ 2)
				sum += Math.pow(Math.log(1 + fileNumber / Initialization.numberOfDocumentContainsKeyword.get(keyword)), 2);
			}
		}
		return Math.sqrt(sum);
	}

	public static void main(String[] args) throws IOException {
		MySecretKey mySecretKey = Initialization.getMySecretKey();
		TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
		String query = "Pope Francis honorary citizenship Democratic Revolution clinton owners oversee would half pick";
		List<String> keywordList = new ArrayList<>();
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			keywordList.add(matcher.group().toLowerCase());
		}

		System.out.println(Initialization.dict.contains("clinton"));
		System.out.println(Initialization.numberOfDocumentContainsKeyword.keySet().contains("clinton"));

		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = keywordList.get(i);
			if (Initialization.dict.contains(keyword)) {
				System.out.printf("%-15s\t %6d\n", keyword, Initialization.numberOfDocumentContainsKeyword.get(keyword));
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
