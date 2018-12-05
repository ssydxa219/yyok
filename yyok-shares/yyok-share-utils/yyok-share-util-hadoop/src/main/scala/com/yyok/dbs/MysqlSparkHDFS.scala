package com.yyok.dbs
import org.apache.spark.sql.{ DataFrame, Row, SQLContext, SparkSession }
import org.apache.spark.{ SparkConf, SparkContext }
import java.util.Properties
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{ LongType, StringType, StructField, StructType }

object MysqlSparkHDFS {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}")


  }

}

