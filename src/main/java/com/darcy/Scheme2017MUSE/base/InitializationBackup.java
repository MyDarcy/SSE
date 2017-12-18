package com.darcy.Scheme2017MUSE.base;


import Jama.Matrix;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/*
 * author: darcy
 * date: 2017/11/7 16:53
 * description: 
*/
public class InitializationBackup {

	public static int lengthOfDict;
	public static List<String> dict;

	public static int DICTIONARY_SIZE;
	public static final int DUMMY_KEYWORD_NUMBER = 10;

	public static final String BASE = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE";
	public static final String SECRET_KEY_DIR = BASE + "\\" + "doc\\muse\\key\\aesKey.dat";
	public static final String PLAIN_DIR = BASE + "\\" + "doc\\muse\\plain40";
	public static final String ENCRYPTED_DIR = BASE + "\\" + "doc\\muse\\encrypted40";

	public static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
	public static Cipher cipher;
	public static KeyGenerator keyGenerator;
	public static SecretKey secretKey;

	/**
	 * 主要是为了密钥的生成。
	 */
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
			if (new File(SECRET_KEY_DIR).exists()) {
				ois = new ObjectInputStream(new FileInputStream(SECRET_KEY_DIR));
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
			if (!new File(SECRET_KEY_DIR).exists()) {
				System.out.println(new File(SECRET_KEY_DIR).getAbsolutePath());
				oos = new ObjectOutputStream(new FileOutputStream(new File(SECRET_KEY_DIR)));
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


	public static MySecretKey getMySecretKey() throws IOException {

		File parentFile = new File(PLAIN_DIR);
		HashSet<String> set = new HashSet<>();
		if (parentFile.exists()) {
			File[] files = parentFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				List<String> strings = Files.readAllLines(files[i].toPath());
				for (String line : strings) {
					/*String[] words = line.split("\\s+");
					for (String str : words) {
						if (str != null && !str.equals("")) {
							set.add(str);
						}
					}*/
					Matcher matcher = WORD_PATTERN.matcher(line);
					while (matcher.find()) {
						set.add(matcher.group());
					}
				}
			}
		}
		List<String> dict = set.stream().sorted().collect(toList());

		System.out.println("dict.size():" + dict.size());
		System.out.println(dict);

		// 初始化字典的长度和字典本身.
		InitializationBackup.lengthOfDict = dict.size();
		InitializationBackup.dict = dict;

		InitializationBackup.DICTIONARY_SIZE = dict.size();


		/*Arrays.stream(parentFile.listFiles()).map(File::toPath).flatMap(Files::readAllLines).collect()*/

		MySecretKey sk = new MySecretKey();
		// 最末尾为1是因为BitSet是自增的, 必须length+1置位, 那么总的length才能是length + 1;
		BitSet bitSet = new BitSet(DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1);
		Random random = new Random(31);

		// lengthOfDict + 1的长度.
		for (int i = 0; i <= lengthOfDict; i++) {
			if (random.nextBoolean()) {
				bitSet.set(i);
			}
		}
		//
		bitSet.set(DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1);
		System.out.println(bitSet.length());

		/*Matrix m1 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);
		Matrix m2 = Matrix.random(lengthOfDict + 1, lengthOfDict + 1);*/
		long start = System.currentTimeMillis();
		Matrix m1 = Matrix.random(DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1, DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1);
		Matrix m2 = Matrix.random(DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1, DICTIONARY_SIZE + DUMMY_KEYWORD_NUMBER + 1);
		System.out.println("time cousume:" + (System.currentTimeMillis() - start) + "ms");
		sk.S = bitSet;
		sk.M1 = m1;
		sk.M2 = m2;
		sk.secretKey = secretKey;
		return sk;
	}


	public SecretKey getSecretKey() {
		SecretKey secretKey = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {

			// 存储密钥的文件存在.
			if (new File(SECRET_KEY_DIR).exists()) {
				ois = new ObjectInputStream(new FileInputStream(SECRET_KEY_DIR));
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
			if (!new File(SECRET_KEY_DIR).exists()) {
				System.out.println(new File(SECRET_KEY_DIR).getAbsolutePath());
				oos = new ObjectOutputStream(new FileOutputStream(new File(SECRET_KEY_DIR)));
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

	public static void main(String[] args) throws IOException {

		MySecretKey mySecretKey = InitializationBackup.getMySecretKey();
		System.out.println(mySecretKey);

		long start = System.currentTimeMillis();
		mySecretKey.M1.transpose();
		System.out.println("Matrix transpose time consume:" + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		mySecretKey.M1.inverse();
		System.out.println("Matrix reverse time consume:" + (System.currentTimeMillis() - start) + "ms");


		System.out.println();
		System.out.println(InitializationBackup.secretKey);
	}
}

/*
D:\MrDarcy\ForGraduationWorks\Code\SSE\src\main\java>javac com\darcy\Scheme2017MUSE\base\Initialization.java

D:\MrDarcy\ForGraduationWorks\Code\SSE\src\main\java>javac com\darcy\Scheme2017MUSE\base\Initialization.java


# 40
5906
time cousume:3785ms
MySecretKey{S.length=5906, M1.rank=5905, M2.rank=5905, secretKey=javax.crypto.spec.SecretKeySpec@16172}
Matrix transpose time consume:2944ms
Matrix reverse time consume:667690ms

javax.crypto.spec.SecretKeySpec@16172


# 100
*/