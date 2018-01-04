package com.darcy.auxiliary;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * author: darcy
 * date: 2018/1/4 10:27
 * description: 
*/
public class LucuneDemo {


	public static void test() {
		Set<String> dict = new HashSet<>();
		// 自定义停用词
		String[] self_stop_words = { "lucene", "release", "Apache" };
		CharArraySet cas = new CharArraySet(1, true);

		for (int i = 0; i < self_stop_words.length; i++) {
			cas.add(self_stop_words[i]);
		}
		// 加入系统默认停用词
		Iterator<Object> itor = StandardAnalyzer.STOP_WORDS_SET.iterator();
		while (itor.hasNext()) {
			cas.add(itor.next());
		}
		System.out.println(StandardAnalyzer.STOP_WORDS_SET);
		StandardAnalyzer analyzer = new StandardAnalyzer(cas);
		try {
			String parentFileName = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\muse\\extend\\plain1000";
			File[] files = new File(parentFileName).listFiles();
			for (File file : files) {
				List<String> lines = Files.readAllLines(file.toPath());
				for (String line : lines) {
					TokenStream tokenStream = analyzer.tokenStream("", line);
					tokenStream.reset();
					CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);

					while (tokenStream.incrementToken()) {
						String str = ch.toString();
						System.out.println(str);
						dict.add(str);
					}
					tokenStream.end();
					tokenStream.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("dict.size():" + dict.size());
		dict.stream().sorted().forEach(System.out::println);
	}

	public static void main(String[] args) {
		test();
	}

}
