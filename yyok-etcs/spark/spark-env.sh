#!/usr/bin/env bash
export JAVA_HOME=/usr/java/jdk1.8.0_192-amd64
export SCALA_HOME=/ddhome/bin/scala
export HADOOP_HOME=/ddhome/bin/hadoop
export HADOOP_PROFIX=/ddhome/bin/adoop
export SPARK_HOME=/ddhome/bin/spark
export SPARK_LIBRARY_PATH=/ddhome/bin/spark/jars
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
export SPARK_DIST_CLASSPATH=$(hadoop classpath)
export SPARK_MASTER_IP=ddb
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_INSTANCES=2
export SPARK_MASTER_WEBUI_PORT=18080
export SPARK_WORKER_CORES=2
export SPARK_WORKER_INSTANCES=1
export SPARK_WORKER_MEMORY=1024m
export SPARK_WORKER_PORT=7078
export SPARK_WORKER_DIR=/data/local/spark/work
export SPARK_DRIVER_MEMORY=1024m
export SPARK_LAUNCH_WITH_SCALA=0
export SPARK_EXECUTOR_MEMORY=1g
export SPARK_LOG_DIR=/data/local/spark/logs
export SPARK_PID_DIR='/ddhome/bin/spark/run'
export MASTER=spark://ddb:7077


