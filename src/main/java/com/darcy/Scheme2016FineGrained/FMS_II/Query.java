package com.darcy.Scheme2016FineGrained.FMS_II;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.util.List;

/*
 * author: darcy
 * date: 2017/11/9 19:41
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
	public static Matrix innerProduct(Index index, Trapdoor trapdoor) {
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
		/*String query = "static final";*/
		String query = "|(new ois) &(java util) !(Initialization)";
		Trapdoor trapdoor = trapdoorGenerating.generateTrapdoor(query);

		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for (int i = 0; i < indexes.size(); i++) {
			Matrix matrix = innerProduct(indexes.get(i), trapdoor);

			System.out.println(matrix.get(0, 0));

			if (matrix.get(0, 0) > max) {
				max = matrix.get(0, 0);
				maxIndex = i;
			}
			/*double[][] array = matrix.getArray();
			System.out.println(array.length + "\t" + array[0].length);
			if (array[0][0] > max) {
				max = array[0][0];
				maxIndex = i;
			}*/
		}

		System.out.println("MOST MATCH.");
		System.out.println("fileIndex:" + maxIndex);

		File parentDir = new File(Initialization.ENCRYPTED_DIR);
		File[] encryptedFiles = parentDir.listFiles();
		byte[] encryptedBytes = Files.readAllBytes(encryptedFiles[maxIndex].toPath());
		byte[] decrypt = EncryptionUtils.decrypt(encryptedBytes);
		System.out.println(new String(decrypt));
	}
}
