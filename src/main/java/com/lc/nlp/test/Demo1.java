package com.lc.nlp.test;

import com.lc.nlp.keyword.algorithm.TextRank;

/*
 * author: darcy
 * date: 2018/1/2 15:21
 * description: 
*/
public class Demo1 {
	public static void main(String[] args) {
		TextRank.setKeywordNumber(10);
		TextRank.setWindowSize(4);
		//String title = "关键词抽取";
		// String content = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
		// String content = "In other words, Francis wants a more decentralized church and wants to hear reform ideas from small communities that sit far from Catholicism's power centers, Bellitto said.";
		String content =
				"However, China alleges the men are part of the East Turkestan Islamic Movement " +
						"-- a group the U.S. State Department considers a terrorist organization -- " +
						"that operates in the Xinjiang region. East Turkestan is another name for Xinjiang.\n" +
						"China on Thursday urged the United States to hand over all 17 of the Uyghurs instead " +
						"of sending them elsewhere. The Chinese statement followed an offer by Palau, a Pacific " +
						"island nation, to accept the Uyghur detainees.\n" +
						"The Xinjiang region of 20 million people is largely populated by ethnic Uyghurs and" +
						" other Muslim minorities who have traditionally opposed Beijing's rule and clamored " +
						"for greater autonomy.\n" +
						"A senior U.S. administration official told CNN the State Department is working on a" +
						" final agreement with Palau to settle the matter of the 13 remaining Uyghur detainees.\n" +
						"Issues to be worked out include how to transfer the Uyghurs to Palau and how much money" +
						" the United States would give the men for resettlement, the official said.\n";
		System.out.println(TextRank.getKeyword(null, content));
	}
}
