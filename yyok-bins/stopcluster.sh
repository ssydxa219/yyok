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
    $bh/hbase/bin/hbase-daemon.sh stop master
    $bh/hbase/bin/hbase-daemon.sh stop regionserver
    $bh/hbase/bin/hbase/bin/hbase-stop.sh
    if [ $un == 'dda' ];then
        $bh/hbase/bin/hbase-daemon.sh stop master
        $bh/hive/bin/hive --service metastore 1>/dev/null 2>&1 &
        $bh/hive/bin/hive --service hiveserver2 1>/dev/null 2>&1 &
       # hdfs haadmin -transitionToStandby -forcemanual nna
       # hdfs haadmin -transitionToStandby -forcemanual nnb
        hdfs haadmin -getServiceState nna
        hdfs haadmin -getServiceState nnb
        hdfs haadmin -transitionToActive --forcemanual nna
        hdfs haadmin -getServiceState nna
        hdfs haadmin -getServiceState nnb
        $bh/hadoop/sbin/stop-all.sh

    fi
    if [ $un == 'ddb' ];then
        $bh/hbase/bin/hbase-daemon.sh stop master
        $bh/hadoop/sbin/stop-yarn.sh
        $bh/hadoop/sbin/mr-jobhistory-daemon.sh stop historyserver
        yarn rmadmin -getServiceState rma
        $bh/spark/sbin/stop-all.sh
    fi
    if [ $un == 'ddc' ];then
        $bh/hadoop/sbin/stop-yarn.sh
        $bh/hadoop/sbin/mr-jobhistory-daemon.sh stop historyserver
        yarn rmadmin -getServiceState rmb
        $bh/spark/sbin/stop-all.sh
    fi
    if [ $un == 'dde' -o $un == 'ddf' ]
    then
        $bh/hbase/bin/hbase-daemon.sh stop regionserver
        $bh/kafka/bin/kafka-server-stop.sh -daemon $bh/kafka/config/server.properties
    fi
    $bh/hadoop/bin/hdfs --daemon stop journalnode
    #$bh/zookeeper/bin/zkServer.sh status
    jps
    exit"
done


for un in ${usernames[@]};do
    ssh $un "$bh/zookeeper/bin/zkServer.sh stop;exit"
done

