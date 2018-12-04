package com.yyok.dbs
import org.apache.spark.sql.{ DataFrame, Row, SQLContext, SparkSession }
import org.apache.spark.{ SparkConf, SparkContext }
import java.util.Properties
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{ LongType, StringType, StructField, StructType }

object MysqlSparkHDFS {

    val sparkConf = new SparkConf().setMaster("spark://101.37.14.63:7080").setAppName("sparkExportsql2HDFS	")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    
  def loadMysqlData(): Unit = {
    //创建连接mysql连接
    val jdbcOptions = Map("url" -> "jdbc:mysql://192.168.100.212:3306/mysql?user=root&password=dd@2018", "dbtable" -> "areas")
    val reader = sqlContext.read.format("jdbc")
    val jdbcOptions2 = Map("url" -> "jdbc:mysql://192.168.100.212:3306/mysql?user=root&password=dd@2018", "dbtable" -> "product")
    val reader2 = sqlContext.read.format("jdbc")
    //把查询出来的表注册为临时表
    reader.options(jdbcOptions).load().registerTempTable("spark_areas")
    reader2.options(jdbcOptions2).load().registerTempTable("spark_product")
  }
  
  def areaRowCount(): Unit ={
    sqlContext.sql("select " +
      "CASE WHEN areaName IN ( '华北地区', '东北地区') THEN 'A' "+
      " WHEN areaName IN ( '华东地区', '华中地区') THEN 'B' "+
      " WHEN areaName IN ( '华南地区', '西南地区') THEN 'C' "+
      "WHEN areaName IN ('西北地区') THEN 'D' "+
      "ELSE'数据错误' END as areaLevel,areaName,productName," +
      "sumClick," +
      "Row_Number() OVER (PARTITION BY areaName order by sumClick DESC) AS numSum," +
      "if(extendInfo='1','自营','第三方') extendName "+
      "from areaNameCount ").registerTempTable("click_row_count")
  }
  /**
    * 按地区统计点击量
    */
  def areaNameCount(): Unit ={
    sqlContext.sql("select areas.area_name areaName," +
      "product.product_name productName,count(1) sumClick," +
      "product.extend_info extendInfo from tPagesClick " +
      "join spark_areas areas " +
      "on  tPagesClick.city_id=areas.city_id " +
      "join spark_product  product " +
      "on  product.product_id=tPagesClick.click_product_id " +
      "group by areas.area_name,product.product_name,product.extend_info").registerTempTable("areaNameCount")
  }
  
  def main(args: Array[String]): Unit = {

    
    sqlContext.sql("use bigdata")
    sqlContext.sql("select * from t_pages_click ").registerTempTable("tPagesClick")
    loadMysqlData()
    areaNameCount()
    areaRowCount()
    sqlContext.sql("select areaLevel,areaName,productName,sumClick,extendName " +
      "from click_row_count " +
      "where numSum<=3 " +
      "order by areaLevel asc,sumClick desc" ).show(50)

      
      
    //1. 不指定查询条件
    //这个方式链接MySql的函数原型是：
    //我们只需要提供Driver的url，需要查询的表名，以及连接表相关属性properties。下面是具体例子：
   // val url = "jdbc:mysql://mysql51gjj2016.mysql.rds.aliyuncs.com:3306/zfgjj?user=shujuzhongxin&password=CL3eQ8HxBTjBcueG"
    val url = "jdbc:mysql://192.168.101.181:3306/mysql?user=root&password=dd@2018"
    val prop = new Properties();
    val df = sqlContext.read.jdbc(url, "stock", prop);
    println("第一种方法输出：" + df.count());
    println("1.------------->" + df.count());
    println("1.------------->" + df.rdd.partitions.size);

    //2.指定数据库字段的范围
    //这种方式就是通过指定数据库中某个字段的范围，但是遗憾的是，这个字段必须是数字，来看看这个函数的函数原型：
    /* def jdbc(
    url: String,
    table: String,
    columnName: String,
    lowerBound: Long,
    upperBound: Long,
    numPartitions: Int,
    connectionProperties: Properties): DataFrame*/
    //前两个字段的含义和方法一类似。columnName就是需要分区的字段，这个字段在数据库中的类型必须是数字；
    //lowerBound就是分区的下界；upperBound就是分区的上界；numPartitions是分区的个数。同样，我们也来看看如何使用：
    val lowerBound = 1;
    val upperBound = 6;
    val numPartitions = 2;
    val url1 = "jdbc:mysql://mysql51gjj2016.mysql.rds.aliyuncs.com:3306/zfgjj?user=shujuzhongxin&password=CL3eQ8HxBTjBcueG"
    val prop1 = new Properties();
    val df1 = sqlContext.read.jdbc(url1, "stock", "id", lowerBound, upperBound, numPartitions, prop1);
    println("第二种方法输出：" + df1.rdd.partitions.size);
    df1.collect().foreach(println)

    /*这个方法可以将iteblog表的数据分布到RDD的几个分区中，分区的数量由numPartitions参数决定，在理想情况下，每个分区处理相同数量的数据，我们在使用的时候不建议将这个值设置的比较大，因为这可能导致数据库挂掉！但是根据前面介绍，这个函数的缺点就是只能使用整形数据字段作为分区关键字。
这个函数在极端情况下，也就是设置将numPartitions设置为1，其含义和第一种方式一致。*/

    //3.根据任意字段进行分区
    //基于前面两种方法的限制， Spark 还提供了根据任意字段进行分区的方法，函数原型如下：
    /*def jdbc(
    url: String,
    table: String,
    predicates: Array[String],
    connectionProperties: Properties): DataFrame*/
    //这个函数相比第一种方式多了predicates参数，我们可以通过这个参数设置分区的依据，来看看例子：
    //这个函数相比第一种方式多了predicates参数，我们可以通过这个参数设置分区的依据，来看看例子：
    val predicates = Array[String]("id <= 2", "id >= 4 and id <= 5 ")
    val url2 = "jdbc:mysql://mysql51gjj2016.mysql.rds.aliyuncs.com:3306/zfgjj?user=shujuzhongxin&password=CL3eQ8HxBTjBcueG"
    val prop2 = new Properties()
    val df2 = sqlContext.read.jdbc(url, "stock", predicates, prop2)
    println("第三种方法输出：" + df2.rdd.partitions.size + "," + predicates.length);
    df2.collect().foreach(println)
    //最后rdd的分区数量就等于predicates.length。

    //4.通过load获取
    //Spark还提供通过load的方式来读取数据。
    val url3 = "jdbc:mysql://mysql51gjj2016.mysql.rds.aliyuncs.com:3306/zfgjj?user=shujuzhongxin&password=CL3eQ8HxBTjBcueG"
    val df3 = sqlContext.read.format("jdbc").option("url", url).option("dbtable", "stock").load()
    println("第四种方法输出：" + df3.rdd.partitions.size);
    df.collect().foreach(println)

    sc.stop()
  }

}


