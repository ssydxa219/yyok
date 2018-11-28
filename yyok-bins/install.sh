#! /bin/bash
mkdir /ddhome/src
chmod -R 777 /ddhome
cd /ddhome/src
#----------------down tar -zxvf mv -------------------

echo " -----------------jdk------------------"
ls /ddhome/src | while read files;do
echo "==================$files======================="
#------------------maven---------------
	if [[ $files =~ 'maven' ]]; then
			echo "====================exeits the " ${files#*.tar}  
			tar -zxvf $files
			mv ${files#*.tar} maven
		elif [[ $files =~ 'zookeeper' ]]; then
			echo "====================exeits the " $files
			tar -zxvf $files
			mv ${files#*.tar} zookeeper
		elif [[ $files =~ 'hadoop' ]]; then
			echo "====================exeits the " $files
			tar -zxvf $files
			#mv ${files#*.tar} hadoop
		elif [[ $files =~ 'hbase' ]]; then
			echo "====================exeits the " $files
			tar -zxvf $files
				#mv ${files#*.tar} hbase
		elif [[ $files =~ 'hive' ]]; then
			echo "====================exeits the " $files
			tar -zxvf $files
			mv ${files#*.tar} hive
		elif [[ $files =~ 'spark' ]]
			then
			echo "====================exeits the " $files
			tar -zxvf $files
		else
			if [[ $files =~ 'maven' || ! -d 'maven' ]]; then
				wget http://mirrors.hust.edu.cn/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
				tar -zxvf apache-maven-3.6.0-bin.tar.gz
				mv apache-maven-3.6.0-bin maven
			fi
			if [[ $files =~ 'zookeeper' || ! -d 'zookeeper' ]]; then
				wget http://mirrors.hust.edu.cn/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz
				tar -zxvf zookeeper-3.4.13.tar.gz
				mv
			fi
			if [[ $files =~ 'hadoop' || ! -d 'hadoop' ]]; then
					wget http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-2.9.2/hadoop-2.9.2-src.tar.gz
					tar -zxvf hadoop-2.9.2-src.tar.gz
					wget http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-3.1.1/hadoop-3.1.1-src.tar.gz
					tar -zxvf hadoop-3.1.1-src.tar.gz
			fi
			if [[ $files =~ 'hbase' || ! -d 'hbase' ]]; then
					wget http://mirrors.shu.edu.cn/apache/hbase/2.1.1/hbase-2.1.1-bin.tar.gz
					tar -zxvf hbase-2.1.1-bin.tar.gz
					mv hbase-2.1.1-bin hbase
					wget http://mirrors.shu.edu.cn/apache/hbase/2.1.1/hbase-2.1.1-src.tar.gz
					tar -zxvf hbase-2.1.1-src.tar.gz
			fi
			if [[ $files =~ 'hive' || ! -d 'hive' ]]; then
				wget http://mirrors.shu.edu.cn/apache/hive/stable-2/apache-hive-2.3.4-bin.tar.gz
				tar -zxvf apache-hive-2.3.4-bin.tar.gz
				mv apache-hive-2.3.4-bin hive
			fi
			if [[ $files =~ 'spark' || ! -d 'spark' ]]; then
					wget http://mirrors.shu.edu.cn/apache/spark/spark-2.4.0/spark-2.4.0-bin-hadoop2.7.tgz
					tar -zxvf spark-2.4.0-bin-hadoop2.7.tgz
					wget https://archive.apache.org/dist/spark/spark-2.4.0/spark-2.4.0.tgz
					tar -zxvf spark-2.4.0.tgz
			fi
		fi	
	#----------------down tar -zxvf mv -------------------
	echo "------------------maven---------------"
	echo "------------------maven---------------"
	echo "------------------maven---------------"
	echo "------------------maven---------------"
	echo "------------------maven---------------"
	echo "------------------maven---------------"
done