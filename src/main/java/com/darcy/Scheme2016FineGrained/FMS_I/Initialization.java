package com.darcy.Scheme2016FineGrained.FMS_I;


import Jama.Matrix;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.util.stream.Collectors.toList;

/*
 * author: darcy
 * date: 2017/11/7 16:53
 * description: 
*/
public class Initialization {

	public static int lengthOfDict;
	public static List<String> dict;
	public static Map<String, Integer> numberOfDocumentContainsKeyword = new HashMap<>();

	// 统计所有文档的长度. 这里可以使用list, 即使当前有多少个文档并不知情.
	public static int[] documentLength;
	// keyword在document中出现的频率. 这里也可以使用Map<Map>来记录有文档中包含的特定的关键词有多少个.
	// public static int[][] keywordFrequency;
	public static Map<Integer, Map<String, Integer>> keywordFrequencyInDocument = new HashMap<>();

	public static final String base = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE";
	public static final String keyDir = base + "\\" + "doc\\interest\\key\\aesKey.dat";
	public static final String PLAIN_DIR = base + "\\" + "doc\\interest\\plain";
	public static final String ENCRYPTED_DIR = base + "\\" + "doc\\interest\\encrypted";

	public static Cipher cipher;
	public static KeyGenerator keyGenerator;
	public static SecretKey secretKey;

	static {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			cipher = Cipher.getInstance("AES"); //  DES/CBC/NoPadding
		 /*
		方法getInstance（ ）的参数为字符串类型，指定加密算法的名称。
		可以是 “Blowfish”、“DES”、“DESede”、“HmacMD5”或“HmacSHA1”等。
		其中“DES”是目前最常用的对称加密算法，但安全性较差。针对DES安全性
		的改进产生了能满足当前安全需要的TripleDES算法，即“DESede”。“Blowfish”
		的密钥长度可达448位，安全性很好。“AES”是一种替代DES算法的新算法，
		可提供很好的安全性。
		 */
			keyGenerator = KeyGenerator.getInstance("AES");
		/*
		该步骤一般指定密钥的长度。如果该步骤省略的话，会根据算法自动使用默认的密钥长度。
		指定长度时，若第一步密钥生成器使用的是“DES”算法，则密钥长度必须是56位；
		若是“DESede”，则可以是112或168位，其中112位有效；若是“AES”，可以是
		128, 192或256位；若是“Blowfish”，则可以是32至448之间可以被8整除的数；
		“HmacMD5”和“HmacSHA1”默认的密钥长度都是64个字节。
		 */
			keyGenerator.init(128);

			// 存储密钥的文件存在.
			if (new File(keyDir).exists()) {
				ois = new ObjectInputStream(new FileInputStream(keyDir));
				try {
					secretKey = (SecretKey) ois.readObject();
					// System.out.println("read");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					ois.close();
				}
				// 没有此文件存储密钥.
			} else {
				// 使用第一步获得的KeyGenerator类型的对象中generateKey()方法可以获得密钥。
				// 其类型为SecretKey类型，可用于以后的加密和解密。
				secretKey = keyGenerator.generateKey();
			}


			// 是否写入密钥到文件中.
			if (!new File(keyDir).exists()) {
				System.out.println(new File(keyDir).getAbsolutePath());
				oos = new ObjectOutputStream(new FileOutputStream(new File(keyDir)));
				oos.writeObject(secretKey);
				System.out.println("write");
			}

			// System.out.println("secretKey:" + secretKey);
			// System.out.println(secretKey.getAlgorithm() + "\t" + secretKey.getEncoded().length);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * @param lengthOfDict 字典的长度.
	 */
	public Initialization(int lengthOfDict) {
		Initialization.lengthOfDict = lengthOfDict;
	}

	public Initialization() {
	}


	public MySecretKey getMySecretKey() throws IOException {

		File parentFile = new File(PLAIN_DIR);
		HashSet<String> setDict = new HashSet<>();
		if (parentFile.exists()) {
			File[] files = parentFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 有多少个文档包含此关键词.
				Set<String> currentDocumentSet = new HashSet<>();
				List<String> strings = Files.readAllLines(files[i].toPath());
				// 当前文档中出现关键词的频率.
				Map<String, Integer> frequency = new HashMap<>();
				for (String line : strings) {
					String[] words = line.split("\\s+");
					for (String str : words) {
						if (str != null && !str.equals("")) {
							currentDocumentSet.add(str);
							setDict.add(str);
							if (!frequency.containsKey(str)) {
								frequency.put(str, 1);
							} else {
								frequency.put(str, frequency.get(str) + 1);
							}
						}
					}
				}

				// 文档中包含关键词的情况.
				keywordFrequencyInDocument.put(i, frequency);
				// 更新有多少个文档包含keyword;
				for (String word : currentDocumentSet) {
					if (!numberOfDocumentContainsKeyword.containsKey(word)) {
						numberOfDocumentContainsKeyword.put(word, 1);
					} else {
						numberOfDocumentContainsKeyword.put(word, numberOfDocumentContainsKeyword.get(word) + 1);
					}
				}
			}
		}

		System.out.println("keywordFrequencyInDocument:" + keywordFrequencyInDocument);
		System.out.println("numberOfDocumentContainsKeyword:" + numberOfDocumentContainsKeyword);
		// System.out.println(numberOfDocumentContainsKeyword.size());
		/*try {
			Arrays.stream(parentFile.listFiles())
					.map(File::toPath)
					.flatMap(Files::readAllLines)
					.map((String s) -> {return s.split("\\s+");})
		} catch (IOException e) {

		}*/
		List<String> dict = setDict.stream().sorted().collect(toList());
		// System.out.println(dict.size());

		/*System.out.println("dict.size():" + dict.size());
		System.out.println(dict);*/

		// 初始化字典的长度和字典本身.
		Initialization.lengthOfDict = dict.size();
		Initialization.dict = dict;

		/*Arrays.stream(parentFile.listFiles()).map(File::toPath).flatMap(Files::readAllLines).collect()*/

		MySecretKey sk = new MySecretKey();
		// 最末尾为1是因为BitSet是自增的, 必须length+1置位, 那么总的length才能是length + 1;
		BitSet bitSet = new BitSet(lengthOfDict + 1);
		Random random = new Random(31);

		// lengthOfDict + 1的长度.
		for (int i = 0; i <= lengthOfDict; i++) {
			if (random.nextBoolean()) {
				bitSet.set(i);
			}
		}
		bitSet.set(lengthOfDict + 1);
		// System.out.println(bitSet.length());


		Matrix m1 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		Matrix m2 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		sk.S = bitSet;
		sk.M1 = m1;
		sk.M2 = m2;
		return sk;
	}


	public SecretKey getSecretKey() {
		SecretKey secretKey = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {

			// 存储密钥的文件存在.
			if (new File(keyDir).exists()) {
				ois = new ObjectInputStream(new FileInputStream(keyDir));
				try {
					secretKey = (SecretKey) ois.readObject();
					System.out.println("read");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					ois.close();
				}
				// 没有此文件存储密钥.
			} else {
			 /*
			方法getInstance（ ）的参数为字符串类型，指定加密算法的名称。
			可以是 “Blowfish”、“DES”、“DESede”、“HmacMD5”或“HmacSHA1”等。
			其中“DES”是目前最常用的对称加密算法，但安全性较差。针对DES安全性
			的改进产生了能满足当前安全需要的TripleDES算法，即“DESede”。“Blowfish”
			的密钥长度可达448位，安全性很好。“AES”是一种替代DES算法的新算法，
			可提供很好的安全性。
			 */
				KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			/*
			该步骤一般指定密钥的长度。如果该步骤省略的话，会根据算法自动使用默认的密钥长度。
			指定长度时，若第一步密钥生成器使用的是“DES”算法，则密钥长度必须是56位；
			若是“DESede”，则可以是112或168位，其中112位有效；若是“AES”，可以是
			128, 192或256位；若是“Blowfish”，则可以是32至448之间可以被8整除的数；
			“HmacMD5”和“HmacSHA1”默认的密钥长度都是64个字节。
			 */
				keyGenerator.init(128);

				// 使用第一步获得的KeyGenerator类型的对象中generateKey( )方法可以获得密钥。其类型为SecretKey类型，可用于以后的加密和解密。

				secretKey = keyGenerator.generateKey();
			}


			// 是否写入密钥到文件中.
			if (!new File(keyDir).exists()) {
				System.out.println(new File(keyDir).getAbsolutePath());
				oos = new ObjectOutputStream(new FileOutputStream(new File(keyDir)));
				oos.writeObject(secretKey);
				System.out.println("write");
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return secretKey;
	}

	public static MySecretKey getMySecretKey(int lengthOfDict) {
		MySecretKey sk = new MySecretKey();
		// 最末尾为1是因为BitSet是自增的, 必须length+1置位, 那么总的length才能是length + 1;
		BitSet bitSet = new BitSet(lengthOfDict + 1);
		Random random = new Random(31);

		// lengthOfDict + 1的长度.
		for (int i = 0; i <= lengthOfDict; i++) {
			if (random.nextBoolean()) {
				bitSet.set(i);
			}
		}
		bitSet.set(lengthOfDict + 1);
		// System.out.println(bitSet.length());

		Matrix m1 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		Matrix m2 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		sk.S = bitSet;
		sk.M1 = m1;
		sk.M2 = m2;
		return sk;
	}

	public static void main1(String[] args) throws IOException {
		int M = 100;
		Initialization initialization = new Initialization(M);
		MySecretKey mySecretKey = initialization.getMySecretKey();
		System.out.println(mySecretKey);
/*		if (new File(".\\doc").exists()) {
			System.out.println(new File(".\\doc").getAbsolutePath());
		  new File("D:\\MrDarcy\\IntelliJIDEA\\SSE\\doc\\aesKey.dat").createNewFile();
		}*/

		System.out.println();
		System.out.println(initialization.getSecretKey());
	}

	public static void main(String[] args) throws IOException {
		Initialization initialization = new Initialization();
		initialization.getMySecretKey();
	}
}
