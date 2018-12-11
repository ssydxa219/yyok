package com.yyok.dbs

import org.apache.spark.rdd.RDD
import java.util.Properties
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}
import org.apache.spark.sql.types.{LongType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

object MysqlSparkHDFS {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //通过并行化创建RDD
    val personRDD = sc.parallelize(Array("14 tom 5", "15 jerry 3", "16 kitty 6")).map(_.split(" "))
    //通过StrutType直接指定每个字段的schema
    val schema = StructType(List(StructField("id", IntegerType, true), StructField("name", StringType, true), StructField("age", IntegerType, true)))
    //将RDD映射到rowRDD
    val rowRDD = personRDD.map(p => Row(p(0).toInt, p(1).trim, p(2).toInt))
    //将schema信息应用到rowRDD上
    val personDataFrame = sqlContext.createDataFrame(rowRDD, schema)
    //创建Properties存储数据库相关属性
    val prop = new Properties()
    prop.put("user", "root")
    prop.put("password", "dd@2018")
    //将数据追加到数据库
    personDataFrame.write.mode("append").jdbc("jdbc:mysql://17.0..0.12:3306/bigdata", "bigdata.person", prop)
    //停止 SparkContext
    sc.stop()

  }

}

