export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export HADOOP_NAMENODE_OPTS=" -Xms1024m -Xmx1024m -XX:+UseParallelGC"
export HADOOP_DATANODE_OPTS=" -Xms1024m -Xmx1024m"
export HADOOP_HOME=/ddhome/bin/hadoop
export HADOOP_PREFIX=$HADOOP_HOME
export HADOOP_LOG_DIR=/data/local/hadoop/log
export HADOOP_PID_DIR=$HADOOP_PREFIX/tmp
export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_OPTS="$HADOOP_OPTS-Djava.library.path=$HADOOP_HOME/lib/native"
export HADOOP_PREFIX=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HDFS_CONF_DIR=$HADOOP_HOME/etc/hadoop
export YARN_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
export HADOOP_OPTS="-Djava.library.path=${HADOOP_PREFIX}/lib/native"
export JAVA_LIBRARY_PATH=$HADOOP_HOME/lib/native
export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HDFS_JOURNALNODE_USER=root
export HDFS_ZKFC_USER=root