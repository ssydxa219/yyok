

sbt assembly -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive


export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"
mvn -Pyarn -Phadoop-3.1.1 -Pspark-ganglia-lgpl -Pkinesis-asl -Phive -DskipTests clean package


spark-shell --master spark://101.37.14.199:7077 --executor-memory 500m
spark-shell --master spark://101.37.14.199:7077 --executor-memory 512m --driver-memory 500m

http://101.37.14.199:8080


