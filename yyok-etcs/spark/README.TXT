sbt assembly -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive


export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"
mvn -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive -DskipTests clean package

1. 生成支持yarn 、hadoop3.1.1 、hive 的部署包：
cd /ddhome/src/spark-2.4.0/dev

./make-distribution.sh --tgz --name 2.4.0 -Pyarn -Phadoop-3.1.1 -Phive

2. 生成支持yarn 、hadoop3.1.1 、hive 、ganglia 的部署包：

./make-distribution.sh --tgz --name 2.4.0 -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -P hive



spark-shell --master spark://101.37.14.199:7077 --executor-memory 500m
spark-shell --master spark://101.37.14.199:7077 --executor-memory 512m --driver-memory 512m

http://101.37.14.63:8088/
http://101.37.14.199:8081/


