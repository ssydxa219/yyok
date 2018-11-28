
hostnamectl --static set-hostname DDX
-----------

vim /etc/hosts
---



yum install iptables-services
--
systemctl stop iptables
systemctl disable iptables
systemctl stop firewalld
systemctl disable firewalld

ssh-keygen -t rsa
---

vim /etc/profile
---
BASE_HOME=/ddhome/bin
export JAVA_HOME=/usr/java/jdk1.8.0_191-amd64
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
export ZOOKEEPER_HOME=$BASE_HOME/zookeeper
export PATH=$PATH:$ZOOKEEPER_HOME/bin
export HADOOP_HOME=$BASE_HOME/hadoop
export HADOOP_PID_DIR=$HADOOP_HOME/pids
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_OPTS="$HADOOP_OPTS-Djava.library.path=$HADOOP_HOME/lib/native"
export HADOOP_PREFIX=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HDFS_CONF_DIR=$HADOOP_HOME/etc/hadoop
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
export JAVA_LIBRARY_PATH=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin



source /etc/profile

scp -r /usr/java/jdk1.8.0_191-amd64 ddx://usr/java/
scp /etc/profile h05:/etc
source /etc/profile



hadoop-env.sh
---
export JAVA_HOME=/usr/java/jdk1.8.0_191-amd64
export HADOOP_HOME=/ddhome/bin/hadoop

core-site.xml
---
<configuration>
 <!-- 指定hdfs的nameservice为ns1 -->
     <property>
         <name>fs.defaultFS</name>
         <value>hdfs://ns1/</value>
     </property>
     <!-- 指定hadoop临时目录 -->
     <property>
         <name>hadoop.tmp.dir</name>
         <value>/hdata/tmp</value>
     </property>
     <!-- 指定zookeeper地址 -->
     <property>
         <name>ha.zookeeper.quorum</name>
         <value>h03:2181,h04:2181,h05:2181</value>
     </property>
     <property>
         <name>ha.zookeeper.session-timeout.ms</name>
         <value>3000</value>
      </property>
 </configuration>

hdfs-site.xml
--
<configuration>
 
    <!--指定hdfs的nameservice为ns1，需要和core-site.xml中的保持一致 -->
 
    <property>
 
        <name>dfs.nameservices</name>
 
        <value>ns1</value>
 
    </property>
 
    <!-- ns1下面有两个NameNode，分别是nn1，nn2 -->
 
    <property>
 
        <name>dfs.ha.namenodes.ns1</name>
 
        <value>nn1,nn2</value>
 
    </property>
 
    <!-- nn1的RPC通信地址 -->
 
    <property>
 
        <name>dfs.namenode.rpc-address.ns1.nn1</name>
 
        <value>h01:9000</value>
 
    </property>
 
    <!-- nn1的http通信地址 -->
 
    <property>
 
        <name>dfs.namenode.http-address.ns1.nn1</name>
 
        <value>h01:50070</value>
 
    </property>
 
    <!-- nn2的RPC通信地址 -->
 
    <property>
 
        <name>dfs.namenode.rpc-address.ns1.nn2</name>
 
        <value>h02:9000</value>
 
    </property>
 
    <!-- nn2的http通信地址 -->
 
    <property>
 
        <name>dfs.namenode.http-address.ns1.nn2</name>
 
        <value>h02:50070</value>
 
    </property>
 
    <!-- 指定NameNode的edits元数据在JournalNode上的存放位置 -->
 
    <property>
 
        <name>dfs.namenode.shared.edits.dir</name>
 
        <value>qjournal://h03:8485;h04:8485;h05:8485/ns1</value>
 
    </property>
 
    <!-- 指定JournalNode在本地磁盘存放数据的位置 -->
 
    <property>
 
        <name>dfs.journalnode.edits.dir</name>
 
        <value>/hdata/jdata</value>
 
    </property>
 
    <!-- 开启NameNode失败自动切换 -->
 
    <property>
 
        <name>dfs.ha.automatic-failover.enabled</name>
 
        <value>true</value>
 
    </property>
 
    <!-- 配置失败自动切换实现方式 -->
 
    <property>
 
        <name>dfs.client.failover.proxy.provider.ns1</name>
 
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
 
    </property>
 
    <!-- 配置隔离机制方法，多个机制用换行分割，即每个机制暂用一行-->
 
    <property>
 
        <name>dfs.ha.fencing.methods</name>
 
        <value>
 
            sshfence
 
            shell(/bin/true)
 
        </value>
 
    </property>
 
    <!-- 使用sshfence隔离机制时需要ssh免登陆 -->
 
    <property>
 
        <name>dfs.ha.fencing.ssh.private-key-files</name>
 
        <value>/root/.ssh/id_rsa</value>
 
    </property>
 
    <!-- 配置sshfence隔离机制超时时间 -->
 
    <property>
 
        <name>dfs.ha.fencing.ssh.connect-timeout</name>
 
        <value>30000</value>
 
    </property>
 
</configuration>

mapred-site.xml
-------
<configuration>
 
    <!-- 指定mr框架为yarn方式 -->
 
    <property>
 
        <name>mapreduce.framework.name</name>
 
        <value>yarn</value>
 
    </property>
 
</configuration>

yarn-site.xml
----
<configuration>
 
 
 
<!-- Site specific YARN configuration properties -->
 
<!-- 开启RM高可用 -->
 
    <property>
 
        <name>yarn.resourcemanager.ha.enabled</name>
 
        <value>true</value>
 
    </property>
 
    <!-- 指定RM的cluster id -->
 
    <property>
 
        <name>yarn.resourcemanager.cluster-id</name>
 
        <value>yrc</value>
 
    </property>
 
    <!-- 指定RM的名字 -->
 
    <property>
 
        <name>yarn.resourcemanager.ha.rm-ids</name>
 
        <value>rm1,rm2</value>
 
    </property>
 
    <!-- 分别指定RM的地址 -->
 
    <property>
 
        <name>yarn.resourcemanager.hostname.rm1</name>
 
        <value>h01</value>
 
    </property>
 
    <property>
 
        <name>yarn.resourcemanager.hostname.rm2</name>
 
        <value>h02</value>
 
    </property>
 
    <!-- 指定zk集群地址 -->
 
    <property>
 
        <name>yarn.resourcemanager.zk-address</name>
 
        <value>h03:2181,h04:2181,h05:2181</value>
 
    </property>
 
    <property>
 
        <name>yarn.nodemanager.aux-services</name>
 
        <value>mapreduce_shuffle</value>
 
    </property>
 
</configuration>

vim workers
--

start-dfs.sh，stop-dfs.sh
---
#!/usr/bin/env bash
HDFS_DATANODE_USER=root
 
HADOOP_SECURE_DN_USER=hdfs
 
HDFS_NAMENODE_USER=root
 
HDFS_SECONDARYNAMENODE_USER=root
 
HDFS_JOURNALNODE_USER=root
 
HDFS_ZKFC_USER=root

start-yarn.sh，stop-yarn.sh
---
#!/usr/bin/env bash
YARN_RESOURCEMANAGER_USER=root
 
HADOOP_SECURE_DN_USER=yarn
 
YARN_NODEMANAGER_USER=root


scp -r 
---




sbin/hadoop-daemon.sh start journalnode
hdfs namenode -format
hdfs zkfc -formatZK
start-dfs.sh

