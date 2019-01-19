package com.yyok.hadoop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLServerDriveUtils {
	final static String cfn = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	final static String url = "jdbc:sqlserver://rm-sqlserver-001-wai.sqlserver.rds.aliyuncs.com:1433;DatabaseName=student";
	final static String un = "datas";
	final static String pwd = "99cd48ms6y1S1mk7G3d88";
	final static String db = "UserCenter";

	public static void main(String[] args) {

	}

	public static PreparedStatement pstatement(String sql) {
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			Class.forName(cfn);
			con = DriverManager.getConnection(url, un, pwd);
			statement = con.prepareStatement(sql);
			/*
			 * res = statement.executeQuery(); while (res.next()) { String con1 =
			 * res.getString("tableName.col");// 获取tableName.col列的元素 ;
			 * System.out.println("列：" + con1); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (statement != null)
					statement.close();
				if (con != null)
					con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return statement;
	}

}
