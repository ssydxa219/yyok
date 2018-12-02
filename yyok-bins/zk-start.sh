#!/usr/bin/env bash
hosts=('dda' 'ddb' 'ddc' 'dde' 'ddf')
bh=/ddhome/bin/zookeeper
zkServer=$bh/bin/zkServer.sh
echo " 提示！"
echo "1 zkServer start" 
echo "2 zkServer stop"
echo "3 zkServer status"
echo "默认是 3"
for h in ${hosts[@]};do
echo "=========$h========================"
    if [ "$1" == "1" ];then
        ssh $h $zkServer start
        ssh $h $zkServer status
        echo "$h start" 
    elif [ "$1" == "2" ];then
        ssh $h $zkServer stop
        ssh $h $zkServer status
        echo "$h stop"
    else
        ssh $h $zkServer status
        echo "$h status"
    fi
done