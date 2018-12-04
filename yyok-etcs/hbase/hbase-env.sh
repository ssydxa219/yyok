export HBASE_OPTS="$HBASE_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode"
export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export HBASE_MANAGES_ZK=false
export HADOOP_HOME=/ddhome/bin/hadoop
export HBASE_LOG_DIR=/data/local/hbase/log
export HBASE_HOME=/ddhome/bin/hbase