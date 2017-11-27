package com.darcy.Scheme2016FineGrained.test;

import com.darcy.Scheme2016FineGrained.utils.EncryptionUtils;
import com.darcy.Scheme2016FineGrained.base.Initialization;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;

/*
 * author: darcy
 * date: 2017/11/8 17:25
 * description: 
*/
public class FilesTest {
	public static void main(String[] args) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

		File parentDir = new File(Initialization.PLAIN_DIR);

		if (parentDir.exists()) {
			File[] files = parentDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				Path path = files[i].toPath();
				byte[] bytes = Files.readAllBytes(path);
				byte[] encrypt = EncryptionUtils.encrypt(bytes);

				System.out.println(path.getFileName());
				String encryptedFileName = Initialization.ENCRYPTED_DIR + "\\encrypted_" + path.getFileName().toString();
				encryptedFileName = encryptedFileName.substring(0, encryptedFileName.lastIndexOf('.')) + ".dat";
				System.out.println(encryptedFileName);
				Files.write(new File(encryptedFileName).toPath(), encrypt);
				byte[] decrypt = EncryptionUtils.decrypt(encrypt);
				String text = new String(decrypt);
				System.out.println(text);
				System.out.println();
			}
		}

	}
}
