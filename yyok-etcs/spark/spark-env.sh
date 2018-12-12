#!/usr/bin/env bash
export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export SPARK_MASTER_IP=dda
export SPARK_MASTER_PORT=7077
export SPARK_WORKER_CORES=1
export SPARK_WORKER_INSTANCES=1
export SPARK_WORKER_MEMORY=1024
export HADOOP_HOME=/ddhome/bin/hadoop
export HADOOP_CONF_DIR=/ddhome/bin/hadoop/etc/hadoop
#export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=hadoop1:2181,hadoop2:2181,hadoop3:2181,hadoop4:2181 -Dspark.deploy.zookeeper.dir=/spark"