package com.darcy.linux.plain;

import Jama.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/*
 * author: darcy
 * date: 2017/12/19 17:05
 * description: 
*/
public class Query2 {

	public static void test1() {
		try {
			MySecretKey mySecretKey = Initialization.getMySecretKey();
			HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey);
			hacTreeIndexBuilding.buildHACTreeIndex();

			String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			System.out.println("Query2 start generating trapdoor.");
			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
			trapdoorGenerating.generateTrapdoor(query);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void test2() {
		try {
			MySecretKey mySecretKey = Initialization.getMySecretKey();
			HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey);

			HACTreeNode root = hacTreeIndexBuilding.buildHACTreeIndex();
			// System.out.println(root);

			// for-16
			// String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			// for-40
			// String query = "clinton broadcasting voice Francis honorary citizenship Democratic Revolution church president conferences";
			String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			System.out.println("Query2 start generating trapdoor.");
			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
			Matrix queryVector = trapdoorGenerating.generateTrapdoor(query);
			SearchAlgorithm searchAlgorithm = new SearchAlgorithm();

			// for-40
			int requestNumber = 10;
			// int requestNumber = 6;
			PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, queryVector, requestNumber);
			System.out.println("Query2 priorityQueue.size():" + priorityQueue.size());
			/*for (HACTreeNode node : priorityQueue) {
				System.out.println(node.fileDescriptor);
			}*/
			System.out.println();

			List<String> filenameList = priorityQueue.stream().map((node) -> node.fileDescriptor).collect(toList());

			String keywordPatternStr = getQueryPattern(query);

			// 验证搜索结果是否包含特定的文档。
			searchResultVerify(filenameList, keywordPatternStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static void searchResultVerify(List<String> filenameList, String keywordPatternStr) throws IOException {
		Pattern keywordPattern = Pattern.compile(keywordPatternStr);
		for (int i = 0; i < filenameList.size(); i++) {
			System.out.println("passage " + filenameList.get(i));
			String separator = "\\";
			if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
				separator = "/";
			}
			List<String> allLines = Files.readAllLines(new File(Initialization.PLAIN_DIR + separator + filenameList.get(i)).toPath());
			String passage = allLines.stream().map(String::toLowerCase).collect(joining("\n"));

			Matcher matcher = keywordPattern.matcher(passage);
			int count = 0;
			while (matcher.find()) {
				String keyword = matcher.group().toLowerCase();
				/*System.out.println(filenameArray[i] + "\t" + keyword + "\t" + Initialization.keywordFrequencyInDocument.get(filenameArray[i]).get(keyword) + "\t" + "documentNumber\t" + Initialization.numberOfDocumentContainsKeyword.get(keyword));*/
				/*System.out.printf("%-60s\t%-15s\t%-10s%-15s\t%10s\n", filenameList.get(i), keyword,
						Initialization.keywordFrequencyInDocument.get(filenameList.get(i)).get(keyword),
						"docsNumber", Initialization.numberOfDocumentContainsKeyword.get(keyword));*/
				System.out.printf("%-15s\t%-10s%-15s\t%10s\n", keyword,
						Initialization.keywordFrequencyInDocument.get(filenameList.get(i)).get(keyword),
						"docsNumber", Initialization.numberOfDocumentContainsKeyword.get(keyword));
				count++;
			}
			System.out.println("count:" + count);
			System.out.println();
		}
	}

	public static String getQueryPattern(String str) {
		Matcher matcher = Initialization.WORD_PATTERN.matcher(str);
		String result = "";
		while (matcher.find()) {
			result += matcher.group().toLowerCase() + "|";
		}
		return "(" + result.substring(0, result.lastIndexOf('|')) + ")";
	}


	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		System.out.println(Query2.class.getName() + " search.");
		System.out.println("plain search.");

		/*test1();*/

		test2();
	}
}
