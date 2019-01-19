package com.yyok.hadoop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MySQLUtils {

	private Connection con = null;
	private static String driver = "com.mysql.jdbc.Driver";
	
	/*private static String url = "jdbc:mysql://gjj-slave-01.mysql.rds.aliyuncs.com:3306/zfgjj?characterEncoding=utf-8";
	private static String username = "shujuzhongxin";
	private static String password = "CL3eQ8HxBTjBcueG";*/
	
	
	private static String url = "jdbc:mysql://rm-bp1mnwmta5778y0d3jo.mysql.rds.aliyuncs.com:3306/devgjj?characterEncoding=utf-8";
	private static String username  = "wangluning";
	private static String password  = "EhyQ5RL61NIE74J263M7i";
	
	
	private static Statement NULL = null;
	
	

	public static void main(String[] args) {
		MySQLUtils mu = new MySQLUtils();
		mu.readMySQL("devgjj", "sys_user", "select * from sys_user");
	}

	public Statement MysqlOpen() {
		try {
			Class.forName(driver); // 加载驱动类
			con = DriverManager.getConnection(url, username, password); // 连接数据库
			if (!con.isClosed())
				System.out.println("***数据库成功连接***");
			Statement state = (Statement) con.createStatement();
			return state;
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类，加载驱动失败");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}
		return NULL;
	}

	public void readMySQL(String dbs, String tables, String sql) {
		ResultSet rs = null;
		Statement state = MysqlOpen();

		try {
			int colnum = 0;
			
			rs = state.executeQuery("select count(*) from information_schema.COLUMNS where TABLE_SCHEMA='"+ dbs +"' and table_name='"+ tables +"'");
			while (rs.next()) {
					colnum = Integer.parseInt(rs.getString(1));
			}
			rs = state.executeQuery(sql);
			while (rs.next()) {
				String recordstr = "";
				for (int i = 1; i <= colnum; i++) {
					if(rs.getDate("Date") != null) {
						recordstr += rs.getString(i) + "\t";
					}else {
						recordstr += rs.getString(i) + "\t";
					}
					
				}
				System.out.println(recordstr);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				state.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
