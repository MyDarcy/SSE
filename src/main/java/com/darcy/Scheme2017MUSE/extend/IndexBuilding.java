package com.darcy.Scheme2017MUSE.extend;

import Jama.Matrix;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * author: darcy
 * date: 2017/11/18 19:33
 * description: 
*/
public class IndexBuilding {

	/*public Initialization initialization;
	public MySecretKey mySecretKey;

	public IndexBuilding(Initialization initialization, MySecretKey mySecretKey) {
		this.initialization = initialization;
		this.mySecretKey = mySecretKey;
	}*/

	public MySecretKey mySecretKey;
	public IndexBuilding(MySecretKey mySecretKey) {
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
				/*System.out.println(new String(decrypt));
				System.out.println();*/
			}
		}
	}

	public Map<String, Index> generateIndex() throws IOException {
		File parentFile = new File(Initialization.PLAIN_DIR);
		File[] files = parentFile.listFiles();
		// 之前使用list来存储文档索引.
		//List<Index> indexes = new ArrayList<>(files.length);

		Map<String, Index> indexMap = new HashMap<>(files.length);
		Random random = new Random(31);

		// System.out.println(mySecretKey.S);

		System.out.println("generate index:");
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
					/**
					 * 论文中评分 * 10 向上取整. 评分是不超过D的整数.
					 * 目前可以看到相关度评分都是 0.xx或者0.0xx
					 */
					int score = (int)Math.ceil(upper * score(lengthOfFile, keywordFrequencyInCurrentDocument.get(key),
							Initialization.numberOfDocumentContainsKeyword.get(key), files.length));
					/*System.out.println("keyword:" + key + "\tscore:" + score);*/
					P.set(0, index, score);
				}
			}

			Matrix pa = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1);
			Matrix pb = new Matrix(1, Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1);

			/**
			 * S[i] = 1, pa[i] + pb[i] = P[i]
			 * S[i] = 0, pa[i] = pb[i] = P[i]
			 */
			for (int j = 0; j < Initialization.DICTIONARY_SIZE + Initialization.DUMMY_KEYWORD_NUMBER + 1; j++) {
				if (mySecretKey.S.get(j)) {
					double v1 = random.nextDouble();
					pa.set(0, j, P.get(0, j) * v1);
					pb.set(0, j, P.get(0, j) * (1 - v1));

					//  S没有置位.
					// 都设置为P[0][j].
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
			indexMap.put(files[i].getName(), index);
			System.out.println();
		}
		return indexMap;
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


	public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, IOException {
		// 获取秘密钥.
		MySecretKey mySecretKey = Initialization.getMySecretKey();
		IndexBuilding indexBuilding = new IndexBuilding(mySecretKey);

		long start = System.currentTimeMillis();
		// 加密文件.
		indexBuilding.encryptFiles();
		System.out.println("encrypted files time:" + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		indexBuilding.decryptFiles();
		System.out.println("decrypted files time:" + (System.currentTimeMillis() - start));

		System.out.println("generateIndex");
		start = System.currentTimeMillis();
		indexBuilding.generateIndex();
		System.out.println("generateIndex time:" + (System.currentTimeMillis() - start));
    // 40个文件的索引构造时间大约是 buildIndex time:91135
	}

}
