export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export HADOOP_NAMENODE_OPTS=" -Xms1024m -Xmx1024m -XX:+UseParallelGC"
export HADOOP_DATANODE_OPTS=" -Xms512m -Xmx512m"
export HADOOP_LOG_DIR=/data/hadoop/logs