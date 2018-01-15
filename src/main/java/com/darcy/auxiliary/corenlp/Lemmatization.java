package com.darcy.auxiliary.corenlp;

/*
 * author: darcy
 * date: 2018/1/12 14:41
 * description: 
*/
import java.util.*;
import java.util.stream.IntStream;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.util.CoreMap;
import edu.washington.cs.knowitall.morpha.MorphaStemmer;

import static edu.washington.cs.knowitall.morpha.MorphaStemmer.stem;

// edu.washington.cs.knowitall.morpha.MorphaStemmer

/**
 * https://stackoverflow.com/questions/771918/how-do-i-do-word-stemming-or-lemmatization
 * https://stackoverflow.com/questions/9343929/how-to-stem-words-in-python-list
 * https://pypi.python.org/pypi/stemming/1.0  Python2.x的库。
 * https://stackoverflow.com/questions/190775/stemming-algorithm-that-produces-real-words 提供了一个Morpha的库。
 *
 */
public class Lemmatization {

	public static  void test1() {
		Document doc = new Document("add your text here! It could contains multiple sentences.");
		for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
			// We're only asking for words -- no need to load any models yet
			System.out.println("The second word of the sentence '" + sent + "' is " + sent.lemma(1));
			// When we ask for the lemma, it will load and run the part of speech tagger
			System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
			// When we ask for the parse, it will load and run the parser
			System.out.println("lemmas:" + sent.lemmas());
			System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
			// ...
			System.out.println();
		}
	}

	public static void test2() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, /*true*/ false);
		String text = "runners cats utilities factionally happily written used"; /* the string you want */;

		text = "In other words, Francis wants a more decentralized church and wants to hear reform ideas from small communities that sit far from Catholicism's power centers, Bellitto said.";
		text = "cats can could running ran  runs cactus cactuses cacti community communities," +
				" In other words, Francis wants a more decentralized church and wants to hear reform ideas from small communities that sit far from Catholicism's power centers, Bellitto said.";

		Annotation document = pipeline.process(text);

		System.out.printf("%-40s%-40s\n", "text:",  text);
		StringBuilder sb = new StringBuilder();
		IntStream.rangeClosed(1, 40).forEach(i -> System.out.print(" "));
		for(CoreMap sentence: document.get(SentencesAnnotation.class))
		{
			for(CoreLabel token: sentence.get(TokensAnnotation.class))
			{
				String word = token.get(TextAnnotation.class);
				String lemma = token.get(LemmaAnnotation.class);
//				String stem = token.get(StemAnnotation.class);
//				System.out.println("lemmatized version :" + lemma);
//				System.out.println("stem version:" + stem);
				System.out.print(lemma + " ");
				sb.append(lemma.toLowerCase() + " ");
			}
		}
		System.out.println();
		System.out.printf("%-40s%-30s\n", "stem(text):" , stem(text.toLowerCase()));
		System.out.printf("%-40s%-30s\n", "stem(sb.toString()):", stem(sb.toString()));
		System.out.printf("%-40s%-30s\n", "MorphaStemmer.morpha(sb.toString()", MorphaStemmer.morpha(text, false));

	}

	public static void main(String[] args) {
		test1();
		System.out.println();
		test2();

	}
}
