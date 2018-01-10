package com.darcy.linux.accelerate.pv;

import com.darcy.linux.accelerate.DiagonalMatrixUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

	public static void test2() {
		try {
			MySecretKey mySecretKey = Initialization.getMySecretKey();
			HACTreeIndexBuilding hacTreeIndexBuilding = new HACTreeIndexBuilding(mySecretKey);
			hacTreeIndexBuilding.encryptFiles();
			hacTreeIndexBuilding.generateAuxiliaryMatrix();
			HACTreeNode root = hacTreeIndexBuilding.buildHACTreeIndex();
			// System.out.println(root);

			// for-16
			// String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			// for-40
      // String query = "clinton broadcasting voice Francis honorary citizenship Democratic Revolution church president conferences";
			String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			System.out.println("Query2 start generating trapdoor.");
			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
			Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);

			// for-40
       int requestNumber1 = 15;
			// int requestNumber = 6;
			for (int requestNumber :
					 Arrays.asList(/*15, 20, 25, 30, 35, 40*/20, 25, 30)) {

			  SearchAlgorithm searchAlgorithm = new SearchAlgorithm();
				PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, trapdoor, requestNumber);
				System.out.println("Query2 priorityQueue.size():" + priorityQueue.size());
				Map<String, Double> nodeScoreMap = new HashMap<>();
				for (HACTreeNode node : priorityQueue) {
					nodeScoreMap.put(node.fileDescriptor, scoreForPruning(node, trapdoor));
				}
				System.out.println("\n"+ query);
				List<String> filenameList = priorityQueue.stream().map((node) -> node.fileDescriptor).collect(toList());
				String keywordPatternStr = getQueryPattern(query);
				// 验证搜索结果是否包含特定的文档。
				searchResultVerify(filenameList, keywordPatternStr, nodeScoreMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private static double scoreForPruning(HACTreeNode root, Trapdoor trapdoor) {
		/*return root.pruningVector.times(queryVector).get(0, 0);*/
		return DiagonalMatrixUtils.score(root.pruningVectorPart1, trapdoor.trapdoorPart1) +
				DiagonalMatrixUtils.score(root.pruningVectorPart2, trapdoor.trapdoorPart2);
	}

	private static void searchResultVerify(List<String> filenameList, String keywordPatternStr, Map<String, Double> nodeScoreMap) throws IOException {
		System.out.println();

		Pattern keywordPattern = Pattern.compile(keywordPatternStr);
		for (int i = 0; i < filenameList.size(); i++) {
			System.out.println(filenameList.get(i) + "\tscore:" + nodeScoreMap.get(filenameList.get(i)));
			List<String> allLines = Files.readAllLines(new File(Initialization.PLAIN_DIR
					+ Initialization.SEPERATOR + filenameList.get(i)).toPath());
			String passage = allLines.stream().map(String::toLowerCase).collect(joining("\n"));

			Matcher matcher = keywordPattern.matcher(passage);
			int count = 0;
			while (matcher.find()) {
				String keyword = matcher.group().toLowerCase();
				/*System.out.println(filenameArray[i] + "\t" + keyword + "\t" + Initialization.keywordFrequencyInDocument.get(filenameArray[i]).get(keyword) + "\t" + "documentNumber\t" + Initialization.numberOfDocumentContainsKeyword.get(keyword));*/
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
		System.out.println("accelerate pv search.");
		test2();
	}
}
