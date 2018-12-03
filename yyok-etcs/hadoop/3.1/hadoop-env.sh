export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export HADOOP_NAMENODE_OPTS=" -Xms1024m -Xmx1024m -XX:+UseParallelGC"
export HADOOP_DATANODE_OPTS=" -Xms512m -Xmx512m"
export HADOOP_HOME=/ddhome/bin/hadoop
export HADOOP_LOG_DIR=/data/local/hadoop/log
export HADOOP_PID_DIR=/ddhome/bin/hadoop/tmp
export HADOOP_CONF_DIR=/ddhome/bin/hadoop/etc/hadoop
export HADOOP_OPTS="-Djava.library.path=/ddhome/bin/hadoop/lib/native"