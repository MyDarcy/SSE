package com.darcy.Scheme2016FineGrained.base;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/*
 * author: darcy
 * date: 2017/11/9 19:41
 * description: 
*/


public class Query {

	static class Item {
		double max;
		int maxIndex;

		public Item(double max, int maxIndex) {
			this.max = max;
			this.maxIndex = maxIndex;
		}

		@Override
		public String toString() {
			return "Item{" +
					"max=" + max +
					", maxIndex=" + maxIndex +
					'}';
		}
	}

	/**
	 * index * trapdoor
	 * (Pa*M1, Pb*M2) * (reverseM1*Qa, reverseM2*Qb)
	 * = Pa*Qa + Pb * Qb
	 * = P' * Q"
	 * = rP' * Q' = r(P * Q - s)
	 *
	 * @param index
	 * @param trapdoor
	 * @return
	 */
	public static  Matrix innerProduct(Index index, Trapdoor trapdoor) {
		/*System.out.println(MatrixUitls.dimension(index.indexPart1));
		System.out.println(MatrixUitls.dimension(trapdoor.trapdoorPart1));

		System.out.println(MatrixUitls.dimension(index.indexPart2));
		System.out.println(MatrixUitls.dimension(trapdoor.trapdoorPart2));

		Matrix m1 = index.indexPart1.times(trapdoor.trapdoorPart1);*/

		return index.indexPart1.times(trapdoor.trapdoorPart1)
				.plus(index.indexPart2.times(trapdoor.trapdoorPart2));
	}

	public static void main(String[] args) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Initialization initialization = new Initialization();
		MySecretKey mySecretKey = initialization.getMySecretKey();

		IndexBuilding indexBuilding = new IndexBuilding(initialization, mySecretKey);
		TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(initialization, mySecretKey);

		// 加密文件集合.
		indexBuilding.encryptFiles();

		List<Index> indexes = indexBuilding.generateIndex();
		/*String query = "util Random java";*/
		/*String query = "java Bitset util";*/
		String query = "System out println ch toString";
		Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);

		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		int topK = 3;

		// 逆序.
		PriorityQueue<Item> queue = new PriorityQueue<Item>(new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				// 顺序.
				// 小顶堆.
				return Double.compare(o1.max, o2.max) < 0 ? -1 : Double.compare(o1.max, o2.max) == 0 ? 0 : 1;
			}
		});

		for (int i = 0; i < indexes.size(); i++) {
			Matrix matrix = innerProduct(indexes.get(i), trapdoor);

			double temp = matrix.get(0, 0);
			System.out.println(temp);

			if (temp > max) {
				max = temp;
				maxIndex = i;
			}

			queue.add(new Item(temp, i));
			if (queue.size() > topK) {
				queue.remove();
			}


			/*double[][] array = matrix.getArray();
			System.out.println(array.length + "\t" + array[0].length);
			if (array[0][0] > max) {
				max = array[0][0];
				maxIndex = i;
			}*/
		}

		System.out.println("MOST MATCH.");
		System.out.println(maxIndex);

		System.out.println(queue);

		File parentDir = new File(Initialization.ENCRYPTED_DIR);
		File[] encryptedFiles = parentDir.listFiles();
		System.out.println(encryptedFiles[maxIndex].getAbsolutePath());
		byte[] encryptedBytes = Files.readAllBytes(encryptedFiles[maxIndex].toPath());
		byte[] decrypt = EncryptionUtils.decrypt(encryptedBytes);
		System.out.println(new String(decrypt));
	}
}
