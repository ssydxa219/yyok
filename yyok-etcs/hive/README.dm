hive --service metastore 2>&1 >> /dev/null &
# User specific environment and startup programs
export JAVA_HOME=/app/java/jdk1.8.0_141
export HADOOP_HOME=/app/hadoop/hadoop-2.7.3
export SCALA_HOME=/app/scala/scala-2.11.8
export SPARK_HOME=/app/spark/spark-2.1.1
export ZOOKEEPER_HOME=/app/zookeeper/zookeeper-3.4.6
export KAFKA_HOME=/app/kafka/kafka_2.10-0.9.0.0
export HIVE_HOME=/app/hive/apache-hive-2.1.1-bin
PATH=$PATH:$HOME/bin:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$SCALA_HOME/bin:$SPARK_HOME/bin:$SPARK_HOME/sbin:$ZOOKEEPER_HOME/bin:$KAFKA_HOME/bin:$HIVE_HOME/bin
export PATH
