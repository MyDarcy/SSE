package com.darcy.Scheme2017MUSE.base;

import Jama.Matrix;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.*;

/*
 * author: darcy
 * date: 2017/12/18 22:22
 * description: 
*/
public class HACTreeIndexBuilding {

	private static Set<HACTreeNode> currentProcessingHACTreeNodeSet = new HashSet<>();
	private static Set<HACTreeNode> newlyGeneratedHACTreeNodeSet = new HashSet<>();

	public MySecretKey mySecretKey;

	public HACTreeIndexBuilding(MySecretKey mySecretKey) {
		this.mySecretKey = mySecretKey;
	}


	public HACTreeNode buildHACTreeIndex() {
		File parentFile = new File(Initialization.PLAIN_DIR);
		File[] files = parentFile.listFiles();

		System.out.println("generate hac-tree index:");
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());

			Matrix P = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1);
			// 按照规定，该位需要置1
			P.set(0, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER, 1);

			// 字典长度.
			int lengthOfFile = Initialization.fileLength.get(files[i].getName());

			Map<String, Integer> keywordFrequencyInCurrentDocument =
					Initialization.keywordFrequencyInDocument.get(files[i].getName());
			// 用以向上取整以方便计算.
			int upper = 10000;
			for (String key : keywordFrequencyInCurrentDocument.keySet()) {
				int index = Initialization.dict.indexOf(key);
				if (index != -1) {
					int score = (int)Math.ceil(upper * score(lengthOfFile, keywordFrequencyInCurrentDocument.get(key),
							Initialization.numberOfDocumentContainsKeyword.get(key), files.length));
					/*System.out.println("keyword:" + key + "\tscore:" + score);*/
					P.set(0, index, score);
				}
			}

			HACTreeNode currentNode = new HACTreeNode(P, null, null, files[i].getName(), files[i].getName());


		}

		return null;
	}

	/**
	 *
	 * @param lengthOfFile 文件i的长度.
	 * @param frequency 当前关键词在文档i中出现的频率.
	 * @param numberOfDocumentContainsKeyword 有多少个文档包含关键词.
	 * @param filesNumber 总的文档的数目.
	 * @return
	 */
	private double score(int lengthOfFile, Integer frequency, Integer numberOfDocumentContainsKeyword,int filesNumber) {
		return ((1 + Math.log(frequency)) / lengthOfFile)
				* Math.log(1 + filesNumber / numberOfDocumentContainsKeyword);
	}

}
