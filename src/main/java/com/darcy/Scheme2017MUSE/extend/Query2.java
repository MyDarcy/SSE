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
			System.out.println(root);

			// for-16
			// String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			// for-40
      // String query = "clinton broadcasting voice Francis honorary citizenship Democratic Revolution church president conferences";
			String query = "church China hospital performance British interview Democratic citizenship broadcasting voice";

			System.out.println("Query2 start generating trapdoor.");
			TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);
			Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);
			SearchAlgorithm searchAlgorithm = new SearchAlgorithm();

			// for-40
       int requestNumber = 10;
			// int requestNumber = 6;
			PriorityQueue<HACTreeNode> priorityQueue = searchAlgorithm.search(root, trapdoor, requestNumber);
			System.out.println("Query2 priorityQueue.size():" + priorityQueue.size());
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
