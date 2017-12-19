package com.darcy.Scheme2017MUSE.base;

import Jama.Matrix;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.regex.Matcher;

/*
 * author: darcy
 * date: 2017/12/19 17:05
 * description: 
*/
public class Query2 {

	public static void main(String[] args) throws IOException {
		MySecretKey mySecretKey = Initialization.getMySecretKey();
		HACTreeNode root = HACTreeIndexBuilding.buildHACTreeIndex();
		String query = "Pope Francis honorary citizenship Democratic Revolution";
		Matrix queryVector = new Matrix(Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1, 1);
		Matcher matcher = Initialization.WORD_PATTERN.matcher(query);
		while (matcher.find()) {
			int index = Initialization.dict.indexOf(matcher.group().toLowerCase());
			queryVector.set(index, 0, 1);
		}

		int requestNumber = 4;
		PriorityQueue<HACTreeNode> result = new SearchAlgorithm().search(root, queryVector, requestNumber);
		for (HACTreeNode node : result) {
			System.out.println(node.fileDescriptor);
		}
	}
}
