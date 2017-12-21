package com.darcy.auxiliary;

import com.darcy.Scheme2017MUSE.extend.Initialization;
import com.darcy.Scheme2017MUSE.extend.MySecretKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

/*
 * author: darcy
 * date: 2017/12/21 16:19
 * description: 
*/
public class SearchResultTest {
	/**
	 * 搜出出来的8篇文档只有四篇有相关的关键字。
	 * 还是决定随机化然后提取关键字max值的方式有问题，
	 * 毕竟矩阵的逆和矩阵的转置都可能使得转换后的矩阵相应位置处的元素为负数，那么原来的
	 * Pa*M_-1*.M_T * Qa + Pb*M_-1*.M_T * Qb = P*Q
	 * 经过向量提取以后，完全不能反映出这种效果。
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		MySecretKey my = Initialization.getMySecretKey();

		/*String filenames = "0001d1afc246a7964130f43ae940af6bc6c57f01.story\n" +
				"0010c870d3fc53ea7f2a4a50f6496dc2df17e02f.story\n" +
				"001ee59c375363263821474d40e4386ab91d5145.story\n" +
				"00156d9892fb27f1d2e100cbdd8a3997f8273781.story\n" +
				"001732b374f362d3961a510da315601e4b5e7e84.story\n" +
				"001097a19e2c96de11276b3cce11566ccfed0030.story\n" +
				"0005d61497d21ff37a17751829bd7e3b6e4a7c5c.story\n" +
				"0003ad6ef0c37534f80b55b4235108024b407f0b.story";*/
		String filenames = "001c839e1d76c400129f6c2799957c74e9895815.story\n" +
				"000e009f6b1d954d827c9a550f3f24a5474ee82b.story\n" +
				"00128f1ba30d5e9e0f17df83285a1bc2072e2f01.story\n" +
				"0020ede07ee7ad1f6cf654c7dc678e7341d0c0e5.story\n" +
				"001adf6209be103cb198b8599f236b4d5760a5fe.story\n" +
				"001cdbaf0607878f332e0202fadf5b82d2997c02.story\n" +
				"000ca3fc9d877f8d4bb2ebd1d6858c69be571fd8.story\n" +
				"001ee59c375363263821474d40e4386ab91d5145.story\n" +
				"0003ad6ef0c37534f80b55b4235108024b407f0b.story\n" +
				"00189f37b1c8bdc2b132b40270bb28ffcc622af1.story\n";

		String[] filenameArray = filenames.split("\\n");
		for (int i = 0; i < filenameArray.length; i++) {
			System.out.println(filenameArray[i]);
		}
		String basePath = Initialization.BASE + "\\doc\\muse\\extend\\plain40\\";
		String keywordPatternStr = "(clinton|broadcasting|voice|Francis|honorary|citizenship|Democratic|Revolution|church|president|conferences)".toLowerCase();

		Pattern keywordPattern = Pattern.compile(keywordPatternStr);
		for (int i = 0; i < filenameArray.length; i++) {
			System.out.println("passage " + filenameArray[i]);
			List<String> allLines = Files.readAllLines(new File(basePath + filenameArray[i]).toPath());
			String passage = allLines.stream().map(String::toLowerCase).collect(joining("\n"));
//			System.out.println(passage);
			Matcher matcher = keywordPattern.matcher(passage);
			int count = 0;
			while (matcher.find()) {
				String keyword = matcher.group().toLowerCase();
				/*System.out.println(filenameArray[i] + "\t" + keyword + "\t" + Initialization.keywordFrequencyInDocument.get(filenameArray[i]).get(keyword) +
						"\t" + "documentNumber\t" + Initialization.numberOfDocumentContainsKeyword.get(keyword));*/
				System.out.printf("%-60s\t%-15s\t%-10s%-15s\t%10s\n", filenameArray[i], keyword,
						Initialization.keywordFrequencyInDocument.get(filenameArray[i]).get(keyword),
						"docsNumber", Initialization.numberOfDocumentContainsKeyword.get(keyword));
				count++;
			}
			System.out.println("count:" + count);
			System.out.println();

		}
	}
}
