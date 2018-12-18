#! /bin/bash
#all the username
usernames=('dda' 'ddb' 'ddc' 'dde' 'ddf')
bh=/ddhome/bin
apps=('zookeeper' 'hadoop' 'hbase' 'hive' 'spark')
#for un in ${usernames[@]};do
#systemctl disable firewalld
#systemctl stop firewalld.service
#ntpdate -u cn.pool.ntp.org
#clock -w
#done

for un in ${usernames[@]};do
    ssh $un "$bh/zookeeper/bin/zkServer.sh start;exit"
done

#for un in ${usernames[@]};do
#    $bh/zookeeper/bin/zkServer.sh status
#    ntpdate -u cn.pool.ntp.org
#done

for un in ${usernames[@]};do
echo "==============$un================"
    ssh $un "
    source /etc/profile
    #timedatectl set-ntp yes
    #ntpdate -u cn.pool.ntp.org
    #hwclock --systohc --localtime
    #clock -w
    #systemctl disable firewalld
    #systemctl stop firewalld.service
    $bh/zookeeper/bin/zkServer.sh status
    $bh/hadoop/bin/hdfs --daemon start journalnode
    if [ $un == 'dda' ];then
        $bh/hadoop/sbin/start-all.sh
       # hdfs haadmin -transitionToStandby -forcemanual nna
       # hdfs haadmin -transitionToStandby -forcemanual nnb
        $bh/hadoop/bin/hdfs haadmin -getServiceState nna
        $bh/hadoop/bin/hdfs haadmin -getServiceState nnb
        $bh/hadoop/bin/hdfs haadmin -transitionToActive --forcemanual nna
        $bh/hadoop/bin/hdfs haadmin -getServiceState nna
        $bh/hadoop/bin/hdfs haadmin -getServiceState nnb
        $bh/hbase/bin/hbase-daemon.sh start master
        $bh/hbase/bin/hbase-daemon.sh start thrift
        $bh/hive/bin/hive --service metastore 1>/dev/null 2>&1 &
        $bh/hive/bin/hive --service hiveserver2 1>/dev/null 2>&1 &
    fi
    if [ $un == 'ddb' ];then
        $bh/hadoop/sbin/start-yarn.sh
        $bh/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver
        $bh/hadoop/bin/ mapred --daemon start
        $bh/hadoop/bin/yarn rmadmin -getServiceState rma
        $bh/hbase/bin/hbase-daemon.sh start master
        $bh/spark/sbin/start-all.sh
    fi
    if [ $un == 'ddc' ];then
        $bh/hadoop/sbin/start-yarn.sh
        $bh/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver
        $bh/hadoop/bin/ mapred --daemon start
        $bh/hadoop/bin/yarn rmadmin -getServiceState rmb
        $bh/hbase/bin/hbase-daemon.sh start regionserver
    fi
    if [ $un == 'dde' -o $un == 'ddf' ]
    then
        $bh/hbase/bin/hbase-daemon.sh start regionserver
        $bh/kafka/bin/kafka-server-start.sh -daemon $bh/kafka/config/server.properties
    fi
    jps
    exit"
done
