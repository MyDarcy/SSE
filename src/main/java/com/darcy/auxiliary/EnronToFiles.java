package com.darcy.auxiliary;

import java.io.*;
import java.sql.*;

/*
 * author: darcy
 * date: 2018/1/11 10:32
 * description: 
*/
public class EnronToFiles {

	public static void main(String[] args) {
		int number = 1000;
		for (int i = number * 2; i <= number * 10; i += number) {
			String fileDestination = "D:\\MrDarcy\\ForGraduationWorks\\Code\\SSE\\doc\\enron\\enron" + i;
			db2File(i, fileDestination);
		}
	}

	private static void db2File(int number, String fileDestination) {

		if (!new File(fileDestination).exists()) {
			new File(fileDestination).mkdir();
		}

		//声明Connection对象
		Connection connection;
		//驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		//URL指向要访问的数据库名mydata
		String url = "jdbc:mysql://localhost:3306/enron";
		//MySQL配置时的用户名
		String user = "root";
		//MySQL配置时的密码
		String password = "1992";
		//遍历查询结果集
		try {
			//加载驱动程序
			Class.forName(driver);
			//1.getConnection()方法，连接MySQL数据库！！
			connection = DriverManager.getConnection(url,user,password);
			if (!connection.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			}
			//2.创建statement类对象，用来执行SQL语句！！
			Statement statement = connection.createStatement();
			//要执行的SQL语句
			String sql = "select * from message " + " limit " + number;
			//3.ResultSet类，用来存放获取的结果集！！
			ResultSet rs = statement.executeQuery(sql);
			System.out.println("-----------------");
			System.out.println("执行结果如下所示:");
			System.out.println("-----------------");
			System.out.println("id" + "\t" + "subject" + "\t" + "body");
			System.out.println("-----------------");

			int id = 0;
			String subject = null;
			String body = null;
			while(rs.next()){
				id = rs.getInt("mid");
				subject = rs.getString("subject");
				body = rs.getString("body");
				// System.out.println(id + "\t" + subject + "\t\t\t\t" + body);
				String sb = subject + "\n" + body;
				BufferedWriter out = new BufferedWriter(new FileWriter(fileDestination + "\\" + "f" + id + ".txt"));
				out.write(sb);

				out.flush();
				out.close();
			}
			rs.close();
			connection.close();
		} catch(ClassNotFoundException e) {
			//数据库驱动类异常处理
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch(SQLException e) {
			//数据库连接失败异常处理
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			System.out.println("数据库数据成功获取！！");
		}
	}

}
