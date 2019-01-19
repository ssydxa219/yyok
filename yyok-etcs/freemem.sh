#! /bin/sh
used=`free -m | awk 'NR==2' | awk '{print $3}'`
free=`free -m | awk 'NR==2' | awk '{print $4}'`
echo "===========================" >> /app/memory/logs/mem.log
date >> /app/memory/logs/mem.log
echo "Memory usage before | [Use：${used}MB][Free：${free}MB]" >> /app/memory/logs/mem.log
if [ $free -le 4000 ] ; then
sync && echo 1 > /proc/sys/vm/drop_caches 
sync && echo 2 > /proc/sys/vm/drop_caches 
sync && echo 3 > /proc/sys/vm/drop_caches
used_ok=`free -m | awk 'NR==2' | awk '{print $3}'`
				free_ok=`free -m | awk 'NR==2' | awk '{print $4}'`
				echo "Memory usage after | [Use：${used_ok}MB][Free：${free_ok}MB]" >> /app/memory/logs/mem.log 
				echo "OK" >> /app/memory/logs/mem.log
else
                echo "Not required" >> /app/memory/logs/mem.log
fi
exit 1