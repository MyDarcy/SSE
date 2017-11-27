package com.darcy.Scheme2016FineGrained.test;

import com.darcy.Scheme2016FineGrained.utils.JDBCUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

/*
 * author: darcy
 * date: 2017/11/13 10:09
 * description: 
*/
public class JDBCTest {

	public static void main(String[] args) throws SQLException, IOException {
		Connection connection = JDBCUtils.getConnection("enron");
		String sql = "select * from message;";
		Statement statement =
				connection.createStatement();
		statement.execute(sql);
		ResultSet resultSet =
				statement.getResultSet();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		HashSet hashSet = new HashSet();
		int itemsCount = 0;
		while (resultSet.next()) {
			System.out.println(itemsCount++);
			/*System.out.println(resultSet.getString(1));
			System.out.println(resultSet.getString(2));
			System.out.println(resultSet.getString(3));
			System.out.println(resultSet.getString(4));
			System.out.println(resultSet.getString(5));*/
			String data = resultSet.getString(6);
			/*System.out.println(data);*/

			TokenStream tokenStream = analyzer.tokenStream("field", data);
			tokenStream.reset();
			CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);
			int count = 0;
			while (tokenStream.incrementToken()) {
				String token = ch.toString();
//				System.out.println(token);
				hashSet.add(token);
				count++;
			}
			tokenStream.end();
			tokenStream.close();
			System.out.println("count:" + count);
		}
		System.out.println(hashSet);
		System.out.println(hashSet.size());
	}
}
