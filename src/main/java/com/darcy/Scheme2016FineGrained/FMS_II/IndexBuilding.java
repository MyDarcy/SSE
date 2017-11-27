package com.darcy.Scheme2016FineGrained.FMS_II;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * author: darcy
 * date: 2017/11/7 21:33
 * description: 
*/
public class IndexBuilding {

	public Initialization initialization;
	public MySecretKey mySecretKey;

	public IndexBuilding(Initialization initialization, MySecretKey mySecretKey) {
		this.initialization = initialization;
		this.mySecretKey = mySecretKey;
	}

	public void encryptFiles() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		File parentDir = new File(Initialization.PLAIN_DIR);

		if (parentDir.exists()) {
			File[] files = parentDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				Path path = files[i].toPath();
				byte[] bytes = Files.readAllBytes(path);
				byte[] encrypt = EncryptionUtils.encrypt(bytes);

				/*System.out.println(path.getFileName());*/
				String encryptedFileName = Initialization.ENCRYPTED_DIR + "\\encrypted_" + path.getFileName().toString();
				// 二进制文件的后缀是.dat
				encryptedFileName = encryptedFileName.substring(0, encryptedFileName.lastIndexOf('.')) + ".dat";

				/*System.out.println(encryptedFileName);*/

				Files.write(new File(encryptedFileName).toPath(), encrypt);

				// 显示文件加密后的内容.
				/*byte[] decrypt = EncryptionUtils.decrypt(encrypt);
				String text = new String(decrypt);
				System.out.println(text);
				System.out.println();*/
			}
		}
	}

	public void decryptFiles() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		File parentFile = new File(Initialization.ENCRYPTED_DIR);
		if (parentFile.exists()) {
			File[] files = parentFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				byte[] bytes = Files.readAllBytes(files[i].toPath());
				byte[] decrypt = EncryptionUtils.decrypt(bytes);
				System.out.println(new String(decrypt));
				System.out.println();
			}
		}
	}

	public List<Index> generateIndex() throws IOException {
		File parentFile = new File(Initialization.PLAIN_DIR);
		File[] files = parentFile.listFiles();
		List<Index> indexes = new ArrayList<>(files.length);
		Random random = new Random(31);

		// System.out.println(mySecretKey.S);

		for (int i = 0; i < files.length; i++) {
			List<String> strings = Files.readAllLines(files[i].toPath());
			Matrix P = new Matrix(1, Initialization.lengthOfDict + 1);
			// 按照规定，该位需要置1
			P.set(0, Initialization.lengthOfDict, 1);

			for (String line : strings) {
				String[] words = line.split("\\s+");
				for (String word : words) {
					if (word != null && !word.equals("")) {
						int index = Initialization.dict.indexOf(word);
						if (index != -1) {
							P.set(0, index, 1);
						} else {
							P.set(0, index, 0);
						}
					}
				}
			}
			Matrix pa = new Matrix(1, Initialization.lengthOfDict + 1);
			Matrix pb = new Matrix(1, Initialization.lengthOfDict + 1);

			/**
			 * S[i] = 1, pa[i] + pb[i] = P[i]
			 * S[i] = 0, pa[i] = pb[i] = P[i]
			 */
			for (int j = 0; j < Initialization.lengthOfDict + 1; j++) {
				if (mySecretKey.S.get(j)) {
					// 如果P[j] = 0的话，那么矩阵的值可能为负值.
					/*if (Double.compare(P.get(0, j), 0) == 0) {
						pa.set(0, j, 0);
						pb.set(0, j, 0);
					} else {*/
					double v1 = random.nextDouble();
					pa.set(0, j, v1);
					pb.set(0, j, P.get(0, j) - v1);
					/*}*/

					// S没有置位.
					// 都设置为P[0][j]这个值.
				} else {
					pa.set(0, j, P.get(0, j));
					pb.set(0, j, P.get(0, j));
				}
			}

			Index index = new Index(pa.times(mySecretKey.M1), pb.times(mySecretKey.M2));
			/*System.out.println();
			MatrixUitls.print(pa);
			MatrixUitls.print(pb);
			System.out.println(index);
			System.out.println();*/
			indexes.add(index);
		}
		return indexes;
	}


	public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, IOException {
		Initialization init = new Initialization();
		MySecretKey mySecretKey = init.getMySecretKey();
		IndexBuilding indexBuilding = new IndexBuilding(init, mySecretKey);
		indexBuilding.encryptFiles();

		System.out.println("decrypt");

		indexBuilding.decryptFiles();

		System.out.println("generateIndex");

		indexBuilding.generateIndex();
	}

}
