package com.darcy.auxiliary;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/*
 * author: darcy
 * date: 2017/12/21 14:55
 * description: 
*/
public class MD5Test {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			/*
			该步骤一般指定密钥的长度。如果该步骤省略的话，会根据算法自动使用默认的密钥长度。
			指定长度时，若第一步密钥生成器使用的是“DES”算法，则密钥长度必须是56位；
			若是“DESede”，则可以是112或168位，其中112位有效；若是“AES”，可以是
			128, 192或256位；若是“Blowfish”，则可以是32至448之间可以被8整除的数；
			“HmacMD5”和“HmacSHA1”默认的密钥长度都是64个字节。
			 */
		keyGenerator.init(128);

		// 使用第一步获得的KeyGenerator类型的对象中generateKey( )方法可以获得密钥。
		// 其类型为SecretKey类型，可用于以后的加密和解密。
		SecretKey secretKey = keyGenerator.generateKey();

		String passwd = "mypassword123";
		byte[] keyBytes = secretKey.getEncoded();

		// https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac
		Mac mac = Mac.getInstance("HmacMD5");
		// 初始化mac的对称密钥secretKey
		mac.init(secretKey);
		byte[] bytes = mac.doFinal(passwd.getBytes());
		byte[] encodeBytes = Base64.getEncoder().encode(bytes);
		System.out.println(passwd);
		String passwdToDB = new String(encodeBytes);
		System.out.println(passwdToDB);
	}
}