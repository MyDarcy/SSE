package com.darcy.Scheme2017MUSE.extend;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.PriorityQueue;
import java.util.regex.Matcher;

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

			String query = "Pope Francis honorary citizenship Democratic Revolution";

			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
			Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);
			SearchAlgorithm searchAlgorithm = new SearchAlgorithm();

			int requestNumber = 4;
			PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, trapdoor, requestNumber);
			for (HACTreeNode node : priorityQueue) {
				System.out.println(node.fileDescriptor);
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

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		test2();

	}
}
