package com.darcy.auxiliary.extractions;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;

/*
 * author: darcy
 * date: 2017/12/25 9:48
 * description: 
*/
public class Utilities {
	public static String stem(String term) throws IOException {

		TokenStream tokenStream = null;
		try {

			// tokenize
			// tokenStream = new ClassicTokenizer(Version.LUCENE_7_1_0, new StringReader(term));
			Analyzer a = new StandardAnalyzer();
			// TokenStream ts = a.tokenStream("", term);
			tokenStream = a.tokenStream("", term);

			// stem
			tokenStream = new PorterStemFilter(tokenStream);

			// add each token in a set, so that duplicates are removed
			Set<String> stems = new HashSet<String>();
			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				stems.add(token.toString());
			}
			tokenStream.end();
			tokenStream.close();

			// if no stem or 2+ stems have been found, return null
			if (stems.size() != 1) {
				return null;
			}
			String stem = stems.iterator().next();
			// if the stem has non-alphanumerical chars, return null
			if (!stem.matches("[a-zA-Z0-9-]+")) {
				return null;
			}

			return stem;

		} finally {
			if (tokenStream != null) {
				tokenStream.close();
			}
		}

	}

	public static <T> T find(Collection<T> collection, T example) {
		for (T element : collection) {
			if (element.equals(example)) {
				return element;
			}
		}
		collection.add(example);
		return example;
	}

	public static List<Keyword> guessFromString(String input) throws IOException {

		TokenStream tokenStream = null;
		try {

			/*// hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
			input = input.replaceAll("-+", "-0");
			// replace any punctuation char but apostrophes and dashes by a space
			input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
			// replace most common english contractions
			input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");*/

			// tokenize input
			// tokenStream = new ClassicTokenizer(Version.LUCENE_36, new StringReader(input));
			// ClassicTokenizer was named StandardTokenizer in Lucene versions prior to 3.1

			Analyzer a = new StandardAnalyzer();

			tokenStream = a.tokenStream(null, input);
			// to lowercase
			tokenStream = new LowerCaseFilter(tokenStream);
			// remove dots from acronyms (and "'s" but already done manually above)
			tokenStream = new ClassicFilter(tokenStream);
			// convert any char to ASCII
			tokenStream = new ASCIIFoldingFilter(tokenStream);
			// remove english stop words
			// tokenStream = new StopFilter(Version.LUCENE_7_1_0, tokenStream, EnglishAnalyzer.getDefaultStopSet());
			tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());

			List<Keyword> keywords = new LinkedList<Keyword>();
			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String term = token.toString();
				System.out.println(term);
				// stem each term

				String stem = stem(term);
				if (stem != null) {
					// create the keyword or get the existing one if any
					Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
					// add its corresponding initial token
					keyword.add(term.replaceAll("-0", "-"));
				}
			}
			tokenStream.end();
			tokenStream.close();

			// reverse sort by frequency
			Collections.sort(keywords);

			return keywords;

		} finally {
			if (tokenStream != null) {
				tokenStream.close();
			}
		}

	}

	public static void main(String[] args) throws IOException {
		String input = "Java is a general-purpose computer programming language that is concurrent, " +
				"class-based, object-oriented,[15] and specifically designed to have as few implementation " +
				"dependencies as possible. It is intended to let application developers \"write once, run anywhere\" " +
				"(WORA),[16] meaning that compiled Java code can run on all platforms that support Java without the " +
				"need for recompilation.[17] Java applications are typically compiled to bytecode that can run on " +
				"any Java virtual machine (JVM) regardless of computer architecture. As of 2016, Java is one of the " +
				"most popular programming languages in use,[18][19][20][21] particularly for client-server web " +
				"applications, with a reported 9 million developers.[22] Java was originally developed by James " +
				"Gosling at Sun Microsystems (which has since been acquired by Oracle Corporation) and released " +
				"in 1995 as a core component of Sun Microsystems' Java platform. The language derives much of its " +
				"syntax from C and C++, but it has fewer low-level facilities than either of them.\n" +
				"\n" +
				"The original and reference implementation Java compilers, virtual machines, and class libraries " +
				"were originally released by Sun under proprietary licenses. As of May 2007, in compliance with the " +
				"specifications of the Java Community Process, Sun relicensed most of its Java technologies under " +
				"the GNU General Public License. Others have also developed alternative implementations of these " +
				"Sun technologies, such as the GNU Compiler for Java (bytecode compiler), GNU Classpath (standard " +
				"libraries), and IcedTea-Web (browser plugin for applets).\n" +
				"\n" +
				"The latest version is Java 9, released on September 21, 2017,[23] and is one of the two versions " +
				"currently supported for free by Oracle. Versions earlier than Java 8 are supported by companies " +
				"on a commercial basis; e.g. by Oracle back to Java 6 as of October 2017 (while they still \"highly " +
				"recommend that you uninstall\"[citation needed] pre-Java 8 from at least Windows computers).";

		List<Keyword> keywords = guessFromString(input);


	}


}
