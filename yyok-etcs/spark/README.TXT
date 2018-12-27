sbt assembly -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive

./dev/make-distribution.sh --name custom-spark --pip --r --tgz -Psparkr -Phadoop-3.1.1  -Phive -Phive-thriftserver -Pmesos -Pyarn -Pkubernetes

export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"

mvn clean package -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive -DskipTests

mvn -Pyarn -Dyarn.version=2.9.2 -Phadoop-2.9 -Dhadoop.version=2.9.2 -DskipTests clean package


1. 生成支持yarn 、hadoop3.1.1 、hive 的部署包：
cd /ddhome/src/spark-2.4.0/dev

./make-distribution.sh --tgz --name 2.4.0 -Pyarn -Phadoop-3.1.1 -Phive

2. 生成支持yarn 、hadoop3.1.1 、hive 、ganglia 的部署包：

./make-distribution.sh --tgz --name 2.4.0 -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -P hive



spark-shell --master spark://101.37.14.199:7077 --executor-memory 500m
spark-shell --master spark://101.37.14.199:7077 --executor-memory 512m --driver-memory 512m

http://101.37.14.63:8088/
http://101.37.14.199:8081/
http://101.37.14.199:8080


hadoop fs  -mkdir  -p   /data/spark/log
hadoop   fs  -chmod  777  /data/spark/log
mkdir -p  /data/local/{spark/work,logs,run}
mkdir -p /data/local/hive/logs

#hive进入客户端

set hive.execution.engine=spark; (将执行引擎设为Spark，默认是mr，退出hive CLI后，回到默认设置。若想让引擎默认为Spark，需要在hive-site.xml里设置）
create table test(ts BIGINT,line STRING); (创建表）
select count(*) from test;


解决方案

第一个坑：要想在Hive中使用Spark执行引擎，最简单的方法是把spark-assembly-1.5.0-hadoop2.4.0.jar包直接拷贝 到$HIVE_HOME/lib目录下。

第二个坑：版本不对，刚开始以为hive 能使用 spark的任何版本，结果发现错了，hive对spark版本有着严格要求，具体对应版本你可以下载hive源码里面，搜索他pom.xml文件里面的spark版本，如果版本不对，启动hive后会报错。具体错误如下：

Failed to execute spark task, with exception 'org.apache.hadoop.hive.ql.metadata.HiveException(Failed to create spark client.)' FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.spark.SparkTask

第三个坑：./make-distribution.sh --name "hadoop2-without-hive" --tgz "-Pyarn,hadoop-provided,hadoop-2.4" ，开启spark报错找不到类

解决办法是在spark-env.sh里面添加 ：export SPARK_DIST_CLASSPATH=$(hadoop classpath)

#如果启动包日志包重复需要删除
#根据实际修改hive/bin/hive:(根据spark2后的包分散了)
sparkAssemblyPath='ls ${SPARK_HOME}/lib/spark-assembly-*.jar'
将其修改为：sparkAssemblyPath='ls ${SPARK_HOME}/jars/*.jar'

#spark1 拷贝spark/lib/spark-* 到/usr/app/hive/lib


FAILED: SemanticException Failed to get a spark session: org.apache.hadoop.hive.ql.metadata.HiveException: Failed to create spark client.


java.sql.SQLException: org.apache.hive.service.cli.HiveSQLException: Error while processing statement: FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.spark.SparkTask. Failed to create spark client.

Spark开发性能调优




























