package com.darcy.Scheme2016FineGrained.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

/**
 * author: darcy
 * date: 2017/11/9 22:50
 * description: 
 */
public class LuceneTest1 {

	public static void test() {
		try {
			// 要处理的文本
			// "lucene分析器使用分词器和过滤器构成一个“管道”，文本在流经这个管道后成为可以进入索引的最小单位，因此，一个标准的分析器有两个部分组成，一个是分词器tokenizer,它用于将文本按照规则切分为一个个可以进入索引的最小单位。另外一个是TokenFilter，它主要作用是对切出来的词进行进一步的处理（如去掉敏感词、英文大小写转换、单复数处理）等。lucene中的Tokenstram方法首先创建一个tokenizer对象处理Reader对象中的流式文本，然后利用TokenFilter对输出流进行过滤处理";
			String text = "The Lucene PMC is pleased to announce the release of the Apache Solr Reference Guide for Solr 7.1.0.";

			// 自定义停用词
			String[] self_stop_words = { "Lucene", "release", "Apache" };
			CharArraySet cas = new CharArraySet(1, true);

			for (int i = 0; i < self_stop_words.length; i++) {
				cas.add(self_stop_words[i]);
			}

			// 加入系统默认停用词
			Iterator<Object> itor = StandardAnalyzer.STOP_WORDS_SET.iterator();
			while (itor.hasNext()) {
				cas.add(itor.next());
			}

			// 标准分词器(Lucene内置的标准分析器,会将语汇单元转成小写形式，并去除停用词及标点符号)
			StandardAnalyzer sa = new StandardAnalyzer();

			TokenStream ts = sa.tokenStream("field", text);
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);

			ts.reset();
			while (ts.incrementToken()) {
				System.out.println(ch.toString());
			}
			ts.end();
			ts.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void test2() throws IOException {
		try {
			File file = new File("D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\base\\plain\\f00004.txt");
			Reader reader = new FileReader(file);
			Analyzer a = new StandardAnalyzer();
			//Analyzer a = new CJKAnalyzer();
			//Analyzer a = new ChineseAnalyzer();
			//Analyzer a = new WhitespaceAnalyzer();
			TokenStream ts = a.tokenStream("", reader);
			int n = 0;

			ts.reset();
			while(ts.incrementToken()){
				n ++ ;
				System.out.println("词条" + n + "的内容为 ：" + ts.toString());
			}
			ts.end();
			ts.close();
			System.out.println("== 共有词条 "+n+" 条 ==");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test3() {
		StandardAnalyzer analyzer = new StandardAnalyzer();

		try {
			List<String> lines = Files.readAllLines(new File("D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\base\\plain\\f00004.txt").toPath());
			for (String line : lines) {
				TokenStream tokenStream = analyzer.tokenStream("field", line);
				tokenStream.reset();
				CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);

				while (tokenStream.incrementToken()) {
					System.out.println(ch.toString());
				}
				tokenStream.end();
				tokenStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		StandardAnalyzer sa = new StandardAnalyzer();

		test();

		System.out.println();


		test2();

		System.out.println();

		test3();
	}

}
