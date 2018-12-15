package com.yyokay.lib.mr;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class HiveJdbc {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws Exception {

        // Register driver and create driver instance
        Class.forName(driverName);

        // get connection
        Connection con = DriverManager.getConnection("jdbc://hive://101.37.14.63:10000/jianbing_hive", "root", "");
        Statement stmt = con.createStatement();
        //stmt.executeQuery("DROP DATABASE userdb");

        System.out.println("Drop userdb database successful.");

        con.close();

        SparkConf sparkConf = new SparkConf().setAppName("SparkHive").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        //不要使用SQLContext,部署异常找不到数据库和表
        HiveContext hiveContext = new HiveContext(sc);
        SQLContext sqlContext = new SQLContext(sc);
        //查询表前10条数据
       // hiveContext.sql("select * from bi_ods.owms_m_locator limit 10").show();

        sc.stop();

    }
}
