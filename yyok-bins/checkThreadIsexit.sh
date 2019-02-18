#!/bin/bash

sec=1000
while true
do
    ocrThread=`ps -ef|grep hf-lib-other-logconsume-1.0.1-jar-with-dependencies.jar|grep -v 'grep'`
    echo $ocrThread
    count=`ps -ef|grep hf-lib-other-logconsume-1.0.1-jar-with-dependencies.jar|grep -v 'grep'|wc -l`
    echo $count
    
    if [ $count -eq  0 ];then
        echo "start process....."
        java -jar /ddhome/tmp/hf-lib-other-logconsume-1.0.1-jar-with-dependencies.jar &
    else
        echo "runing now....."
    fi
done