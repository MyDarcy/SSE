package com.darcy.auxiliary;

import com.darcy.Scheme2017MUSE.base.EncryptionUtils;
import com.darcy.Scheme2017MUSE.base.Initialization;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/*
 * author: darcy
 * date: 2017/12/19 20:56
 * description: 
*/
public class HMACTest1 {
	/**
	 * 算法种类                摘要长度
	 *
	 * HmacMD5                 128
	 * HmacSHA1                160
	 * HmacSHA256              256
	 * HmacSHA384              384
	 * HmacSHA512              512
	 *
	 * 参考;
	 * http://www.jianshu.com/p/3fe2add1eb42
	 * https://www.cnblogs.com/SirSmith/p/4986835.html
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String test() throws NoSuchAlgorithmException, InvalidKeyException {
		// hmac需要一个对称密钥.
		SecretKey secretKey = Initialization.secretKey;
		byte[] keyBytes = secretKey.getEncoded();

		// https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac
		Mac mac = Mac.getInstance("HmacSHA256");

		// 初始化mac的对称密钥secretKey
		mac.init(secretKey);
		String str = "000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story" +
				"000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story" +
				"000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story" +
				"000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story" +
				"000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story" +
				"000c835555db62e319854d9f8912061cdca1893e.story\n" +
				"0006021f772fad0aa78a977ce4a31b3faa6e6fe5.story\n" +
				"001789cf9b865dcac3d9fc032a6b1533e3318eda.story\n" +
				"00083697263e215e5e7eda753070f08aa374dd45.story";


		byte[] bytes = mac.doFinal(str.getBytes());
		System.out.println(bytes.length);

		byte[] encodeBytes = Base64.getEncoder().encode(bytes);
		System.out.println(new String(encodeBytes));
		return null;
	}

	/**
	 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html#HmacEx
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String test1() throws NoSuchAlgorithmException, InvalidKeyException {
		// Generate secret key for HmacSHA256
		KeyGenerator kg = KeyGenerator.getInstance("HmacSHA256");
		SecretKey sk = kg.generateKey();

		// Get instance of Mac object implementing HmacSHA256, and
		// initialize it with the above secret key
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(sk);
		byte[] result = mac.doFinal("Hi There".getBytes());
		System.out.println(new String(Base64.getEncoder().encode(result)));
		return null;
	}

	/**
	 * ref:
	 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html#MDEx
	 * 消息摘要的使用和计算.
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static void messageDigestTest() throws NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		String str1 = "000c835555db62e319854d9f8912061cdca1893e";
		String str2 = "0006021f772fad0aa78a977ce4a31b3faa6e6fe5";
		String str3 = "cccc";
		messageDigest.update(str1.getBytes());
		messageDigest.update(str2.getBytes());
		messageDigest.update(str3.getBytes());

		byte[] str1Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str1.getBytes()));
		byte[] str2Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str2.getBytes()));
		byte[] str3Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str3.getBytes()));
		System.out.println(new String(str1Bytes) + "\t" + new String(str2Bytes) + "\t" + new String(str3Bytes));

		MessageDigest messageDigest2 = MessageDigest.getInstance("SHA-256");
		// here, 顺序不同, 结果也不一样.
		messageDigest2.update(str1Bytes);
		messageDigest2.update(str2Bytes);
		messageDigest2.update(str3Bytes);
		byte[] digest = messageDigest.digest();
		byte[] digest2 = messageDigest2.digest();
		System.out.println(digest.length + "\t" + digest2.length);
		System.out.println(Arrays.toString(digest));
		System.out.println(Arrays.toString(digest2));
		for (int i = 0; i < digest.length; i++) {
			if (digest[i] != digest2[i]) {
				System.out.println("false");
				return;
			}
		}
		System.out.println("true");
		System.out.println(Arrays.equals(digest, digest2));

		System.out.println();
	}

	/**
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static void messageDigestTest2() throws NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		SecretKey secretKey = Initialization.secretKey;
		byte[] keyBytes = secretKey.getEncoded();

		String str1 = "000c835555db62e319854d9f8912061cdca1893e";
		String str2 = "0006021f772fad0aa78a977ce4a31b3faa6e6fe5";
		String str3 = "cccc";
		List<String> stringList = Arrays.asList(str1, str2, str3);
		/*messageDigest.update(str1.getBytes());
		messageDigest.update(str2.getBytes());
		messageDigest.update(str3.getBytes());*/
		for (int i = 0; i < stringList.size(); i++) {
			byte[] strBytes = stringList.get(i).getBytes();
			byte[] bytes = new byte[keyBytes.length + strBytes.length];
			System.arraycopy(keyBytes, 0, bytes, 0, keyBytes.length);
			System.arraycopy(strBytes, 0, bytes, keyBytes.length, strBytes.length);
			messageDigest.update(bytes);
		}

		byte[] str1Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str1.getBytes()));
		byte[] str2Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str2.getBytes()));
		byte[] str3Bytes = EncryptionUtils.decrypt(EncryptionUtils.encrypt(str3.getBytes()));
		List<byte[]> decryptedBytes = Arrays.asList(str1Bytes, str2Bytes, str3Bytes);
		MessageDigest messageDigest2 = MessageDigest.getInstance("SHA-256");
		// here, 顺序不同, 结果也不一样.
		/*messageDigest2.update(str1Bytes);
		messageDigest2.update(str2Bytes);
		messageDigest2.update(str3Bytes);*/
		for (int i = 0; i < decryptedBytes.size(); i++) {
			byte[] strBytes = decryptedBytes.get(i);
			byte[] bytes = new byte[keyBytes.length + strBytes.length];
			System.arraycopy(keyBytes, 0, bytes, 0, keyBytes.length);
			System.arraycopy(strBytes, 0, bytes, keyBytes.length, strBytes.length);
			messageDigest2.update(bytes);
		}

		byte[] digest = messageDigest.digest();
		byte[] digest2 = messageDigest2.digest();

		System.out.println(Arrays.equals(digest, digest2));

	}

	public static void main(String[] args) {
		try {
			test();
			System.out.println();
			test1();
			System.out.println();

			messageDigestTest();
			System.out.println();

			System.out.println("for muse.");
			messageDigestTest2();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
	}
}
