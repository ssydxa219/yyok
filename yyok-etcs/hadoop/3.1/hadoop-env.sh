export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export HADOOP_NAMENODE_OPTS=" -Xms1024m -Xmx1024m -XX:+UseParallelGC"
export HADOOP_DATANODE_OPTS=" -Xms1024m -Xmx1024m"
export HADOOP_HOME=/ddhome/bin/hadoop
export HADOOP_PREFIX=$HADOOP_HOME
export HADOOP_LOG_DIR=/data/local/hadoop/log
export HADOOP_PID_DIR=$HADOOP_PREFIX/tmp
export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export YARN_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export HADOOP_OPTS="-Djava.library.path=${HADOOP_PREFIX}/lib/native"