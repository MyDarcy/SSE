package com.darcy.Scheme2017MUSE.utils;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Author by darcy
 * Date on 17-4-22 下午4:11.
 * Description:
 * <p>
 * 加解密工具
 */
public class OriginalEncryptionUtils {

	private static Cipher cipher;
	private static KeyGenerator keyGenerator;
	private static SecretKey sk;

	/**
	 * 如果密钥文件存在, 那么从中读取密密钥, 如果该文件不存在, 那么先生成一个密钥，然后写入到密钥文件中.
	 */
	static {
	}

	public static void originalInit() {
		try {
			cipher = Cipher.getInstance("AES"); //  DES/CBC/NoPadding
			keyGenerator = KeyGenerator.getInstance("AES");
			sk = keyGenerator.generateKey();
			System.out.println("sk:" + sk);
			System.out.println(sk.getAlgorithm() + "\t" + sk.getEncoded().length);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

    /*public static void init()*/

	static {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			cipher = Cipher.getInstance("AES"); //  DES/CBC/NoPadding
			keyGenerator = KeyGenerator.getInstance("AES");

			String filename = "./doc/";
			String secreKeyFileName = filename + "MySecretKey.dat";
			File file = new File(filename);
			File secretKeyFile = new File(secreKeyFileName);
			if (secretKeyFile.exists()) {
				ois = new ObjectInputStream(new FileInputStream(secreKeyFileName));
				sk = (SecretKey) ois.readObject();
			} else {
				// 密钥不存在, 先生成密钥.
				sk = keyGenerator.generateKey();
				oos = new ObjectOutputStream(new FileOutputStream(secreKeyFileName));
				oos.writeObject(sk);
			}

			System.out.println("sk:" + sk);
			System.out.println(sk.getAlgorithm() + "\t" + sk.getEncoded().length);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加密字符串信息
	 *
	 * @param message
	 * @return
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encrytMessage(String message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		byte[] messageBytes = message.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		byte[] encryptedBytes = cipher.doFinal(messageBytes);
		return encryptedBytes;
	}

	/**
	 * 解密byte[]到String
	 *
	 * @param encryptedBytes
	 * @return
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String decryptMessage(byte[] encryptedBytes) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.DECRYPT_MODE, sk);
		byte[] decrytedBytes = cipher.doFinal(encryptedBytes);
		return new String(decrytedBytes);
	}

	/**
	 * 解密密文字符到明文字符串
	 *
	 * @param cipherMessage
	 * @return
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static String decryptMessage(String cipherMessage) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		return decryptMessage(cipherMessage.getBytes());
	}

	/**
	 * 加密Integer到byte[]
	 *
	 * @param integer
	 * @return
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encrytMessage(Integer integer) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		return encrytMessage(String.valueOf(integer));
	}
}
