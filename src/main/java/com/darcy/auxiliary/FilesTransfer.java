package com.darcy.auxiliary;

import java.io.*;
import java.nio.channels.FileChannel;

/*
 * author: darcy
 * date: 2017/12/15 15:26
 * description:
 * 文件从一个文件夹复杂到另一个文件夹
*/
public class FilesTransfer {

	/**
	 * 方法1
	 * @param source
	 * @param target
	 */
	private static void customBufferStreamCopy(File source, File target) {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			byte[] buf = new byte[4096];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 方法2
	 * @param source
	 * @param target
	 */
	private static void customBufferBufferedStreamCopy(File source, File target) {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(source));
			fos = new BufferedOutputStream(new FileOutputStream(target));
			byte[] buf = new byte[4096];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param fromFile 被复制的文件
	 * @param toFile 复制的目录文件
	 * @param rewrite 是否重新创建文件
	 *
	 * <p>文件的复制操作方法
	 */
	public static void copyfile(File fromFile, File toFile,Boolean rewrite ){

		if(!fromFile.exists()){
			return;
		}

		if(!fromFile.isFile()){
			return;
		}
		if(!fromFile.canRead()){
			return;
		}
		if(!toFile.getParentFile().exists()){
			toFile.getParentFile().mkdirs();
		}
		if(toFile.exists() && rewrite){
			toFile.delete();
		}


		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);

			byte[] bt = new byte[1024];
			int c;
			while((c=fosfrom.read(bt)) > 0){
				fosto.write(bt,0,c);
			}
			//关闭输入、输出流
			fosfrom.close();
			fosto.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// 要复制的文件的个数
		int count = 200;
		while (count <= 900) {
			// 从原文件夹
			String sourceDir = "D:\\MrDarcy\\ForGraduationWorks\\Data\\cnn_stories\\cnn\\stories";
			// 复制到目标文件夹
			String destinationDir = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\muse\\extend\\plain" + count;

			if (!new File(destinationDir).exists()) {
				new File(destinationDir).mkdir();
			}

			System.out.println("list1");
			File sourceParent = new File(sourceDir);
			String[] fileList = sourceParent.list();
			System.out.println("list2");

			long start = System.currentTimeMillis();
			System.out.println("start.");
			for (int i = 0; i < count; i++) {
				System.out.println("start copy:" + fileList[i]);
				File sourceFile = new File(sourceDir + "\\" + fileList[i]);
				File destinationFile = new File(destinationDir + "\\" + fileList[i]);
				FileChannel in = null;
				FileChannel out = null;
				try {
					in = new FileInputStream(sourceFile).getChannel();
					out = new FileOutputStream(destinationFile).getChannel();
					in.transferTo(0, in.size(), out);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("finish copy:" + fileList[i]);
			}
			System.out.println("time consume:" + (System.currentTimeMillis() - start));
			count += 100;
		}

	}

}
