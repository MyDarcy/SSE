package com.darcy.Scheme2017MUSE.extend;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.util.Map;

/*
 * author: darcy
 * date: 2017/11/18 21:41
 * description: 
*/
public class Query {

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

		long start = System.currentTimeMillis();

		MySecretKey mySecretKey = Initialization.getMySecretKey();

		IndexBuilding indexBuilding = new IndexBuilding( mySecretKey);
		TrapdoorGenerating trapdoorGenerating = new TrapdoorGenerating(mySecretKey);

		// 加密文件集合.
		indexBuilding.encryptFiles();

		// 构建索引.
		Map<String, Index> indexes = indexBuilding.generateIndex();
		/*String query = "util Random java";*/
		/*String query = "static final";*/
		String query = "Pope Francis honorary citizenship Democratic Revolution";

		// 构建陷门
		Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);

		double max = Double.NEGATIVE_INFINITY;
		String targetFileName = "";
		for (String key : indexes.keySet()) {
			Matrix matrix = innerProduct(indexes.get(key), trapdoor);

			System.out.println(key + "\t:" + matrix.get(0, 0));

			if (matrix.get(0, 0) > max) {
				max = matrix.get(0, 0);
				targetFileName = key;
			}
			/*double[][] array = matrix.getArray();
			System.out.println(array.length + "\t" + array[0].length);
			if (array[0][0] > max) {
				max = array[0][0];
				maxIndex = i;
			}*/
		}

		System.out.println("MOST MATCH.");
		System.out.println("targetFileName:" + targetFileName);

		File parentDir = new File(Initialization.ENCRYPTED_DIR);
		File[] encryptedFiles = parentDir.listFiles();
		String fileName = Initialization.BASE + "\\doc\\muse\\base\\encrypted40\\encrypted_"
				+ targetFileName.substring(0, targetFileName.lastIndexOf('.')) + ".dat";
		byte[] encryptedBytes = Files.readAllBytes(new File(fileName).toPath());
		byte[] decrypt = EncryptionUtils.decrypt(encryptedBytes);
		System.out.println(new String(decrypt));
		System.out.println("Total Query:" + (System.currentTimeMillis() - start));
	}
}
