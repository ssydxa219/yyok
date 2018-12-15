package com.yyokay.lib.mr

import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}


object RDDDataFrame {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("RDDDataFrame")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    //指定地址创建rdd
    val studentsRDD = sc.textFile("hdfs://master:9000/student/2016113012/data/students.txt").map(_.split(","))
    //将rdd映射到rowRDD
    val RowRDD = studentsRDD.map(x => Row(x(0).toInt,x(1),x(2).toInt))
    //以编程方式动态构造元素据
    val schema = StructType(
      List(
        StructField("id",IntegerType,true),
        StructField("name",StringType,true),
        StructField("age",IntegerType,true)
      )
    )
    //将schema信息映射到rowRDD
    val studentsDF = sqlContext.createDataFrame(RowRDD,schema)
    //注册表
    studentsDF.registerTempTable("t_students")
    val df = sqlContext.sql("select * from t_students order by age")
    df.rdd.collect().foreach(row => println(row))
  }

}

