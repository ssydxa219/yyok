package com.yyokay.lib.mr

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.sql.{SQLContext}

object DataFrameOperations {
  def main (args: Array[String ]) {
    val sparkConf = new SparkConf().setAppName( "Spark SQL DataFrame Operations").setMaster( "local" )
    val sparkContext = new SparkContext(sparkConf)

    val sqlContext = new SQLContext(sparkContext)
    val url = "jdbc:mysql://ddn:3306/test"

    val jdbcDF = sqlContext.read.format( "jdbc" ).options(
      Map( "url" -> url,
        "user" -> "root",
        "password" -> "root",
        "dbtable" -> "spark_sql_test" )).load()

    val joinDF1 = sqlContext.read.format( "jdbc" ).options(
      Map("url" -> url ,
        "user" -> "root",
        "password" -> "root",
        "dbtable" -> "spark_sql_join1" )).load()

    val joinDF2 = sqlContext.read.format( "jdbc" ).options(
      Map ( "url" -> url ,
        "user" -> "root",
        "password" -> "root",
        "dbtable" -> "spark_sql_join2" )).load()


  }
}