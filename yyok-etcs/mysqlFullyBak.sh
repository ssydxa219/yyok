#!/bin/bash
TIMESTAMP=`date +%Y%m%d%H%M%S`
HOST=rr-bp1yra9pcvl3jb2i7.mysql.rds.aliyuncs.com
#HOST=gjj-slave-01.mysql.rds.aliyuncs.com
USER=shujuzhongxin
PASSWORD=CL3eQ8HxBTjBcueG
DATABASE=zfgjj
PORT=3306
EXPORT_PATH=/data/expim #导出目录
LOG_PATH=/data/exp/data.log #日志
EXPORTNUM=500000 #导出条数
mkdir -p $EXPORT_PATH
pointTables="'cfg_app_main', 'cfg_app_type', 'sys_guest', 'sys_guest_phoneinfo', 'sys_user', 'sys_user_app', 'sys_user_ext', 'sys_user_phoneinfo'"
EXE_MYSQL="mysql -h${HOST} -u${USER} -p${PASSWORD} --default-character-set=utf8 -A -N"

#all
# and table_name in (${pointTables})
function tables(){
mysql -u${USER} -p${PASSWORD} -P${PORT} -h${HOST} -D${DATABASE} >${EXPORT_PATH}/tabless.txt << EOF
    select table_name from information_schema.tables where table_schema="${DATABASE}" and table_name in (${pointTables})
EOF
sed -e '/table_name/d' ${EXPORT_PATH}/tabless.txt >${EXPORT_PATH}/tables.txt
rm -rf ${EXPORT_PATH}/tabless.txt
}

#
function tablecount(){
for line in $(cat ${EXPORT_PATH}/tables.txt);do
sql="select count(*) from ${DATABASE}.${line}"
result="$($EXE_MYSQL -e "${sql}")"
echo  "${line}:${result}" >>${EXPORT_PATH}/tablescount_${TIMESTAMP}.txt
done
}

#all tables bakup
function backupAllTables(){
lasttimefile=`ls -l $EXPORT_PATH | grep tablescount | tail -n 1 | awk '{print $9}'`
TIMESTAMPa=${lasttimefile#*tablescount_}
TIMESTAMP=${TIMESTAMPa%%.txt*}
#echo 'the file name : ' $lasttimefile
mkdir -p ${EXPORT_PATH}/${DATABASE}/${TIMESTAMP}
for line in $(cat ${EXPORT_PATH}/${lasttimefile});do
array=(${line//:/ })
tablename=${array[0]}
tablecount=${array[1]}
imported=`ls -l ${EXPORT_PATH}/${DATABASE}/${TIMESTAMP} |awk '{print $9}'`
looptimes=$(expr $tablecount/$EXPORTNUM)+1

if [ "$imported"=="" ];then
                for((i=0;i<=${looptimes};i++));  
                do
                currentnum=$(expr $i \* $EXPORTNUM)
                sql="select * from ${DATABASE}.${tablename} limit  ${currentnum}, ${EXPORTNUM}"
                result="$($EXE_MYSQL -e "${sql}")"
                echo "${result}" >>${EXPORT_PATH}/${DATABASE}/${TIMESTAMP}/${tablename}.txt
                done

else
result=$(echo $imported | grep "${tablename}")
        if [[ "$result" != "" ]]
            then
               # echo $tablename no inc
               #echo $tablename  :  $tablecount
                for((i=0;i<=${looptimes};i++));  
                do
                currentnum=$(expr $i \* $EXPORTNUM)
                sql="select * from ${DATABASE}.${tablename} limit  ${currentnum}, ${EXPORTNUM}"
                result="$($EXE_MYSQL -e "${sql}")"
                echo  "${result}" >${EXPORT_PATH}/${DATABASE}/${TIMESTAMP}/${tablename}.txt
                done
                else
                echo inc $tablename
         fi
fi
done
}

#laster file is the same as lastest file the compare result write tables_increment.txt
function compareFile(){
lasttimefile=`ls -l $EXPORT_PATH | grep tablescount | tail -n 2 | awk '{print $9}'`
files=(${lasttimefile// / })
cat ${EXPORT_PATH}/${files[0]} | sort | uniq | sort > ${EXPORT_PATH}/a_u.txt
cat ${EXPORT_PATH}/${files[1]} | sort | uniq | sort > ${EXPORT_PATH}/b_u.txt
comm  ${EXPORT_PATH}/a_u.txt  ${EXPORT_PATH}/b_u.txt > ${EXPORT_PATH}/tables_increment.txt
rm -rf ${EXPORT_PATH}/a_u.txt
rm -rf ${EXPORT_PATH}/b_u.txt
}

#compare the file context
function compareContext(){
tables_incrementstr=""
tablesIncrement=`ls -l $EXPORT_PATH | grep tables_increment | tail -n 1 | awk '{print $9}'`
for line in $(cat ${EXPORT_PATH}/${tablesIncrement});do
array=(${line//:/ })
tablename=${array[0]}
tablecount=${array[1]}
for linecp in $(cat ${EXPORT_PATH}/${tablesIncrement});do
arraya=(${linecp//:/ })
tablenamea=${arraya[0]}
tablecounta=${arraya[1]}
if [ $tablename == $tablenamea ];then
    if [ $tablecount != $tablecounta ];then
        result=$(echo $tables_incrementstr | grep "${tablename}")
        if [[ "$result" != "" ]]
            then
                echo $tablename no inc
            else
            tables_incrementstr+="$tablename:$tablecount:$tablecounta\\n"
        fi
    fi
fi
done
done
echo -e "$tables_incrementstr" >${EXPORT_PATH}/tables_increment.txt
}


#incremant table export
function backupIncrementTables(){
mkdir -p ${EXPORT_PATH}/${DATABASE}/incr
echo  e `date` \n >>${EXPORT_PATH}/${DATABASE}/incr/${tablename}.txt
tables_increments=`ls -l $EXPORT_PATH | grep tables_increment | tail -n 1 | awk '{print $9}'`
for line in $(cat ${EXPORT_PATH}/${tables_increments});do
array=(${line//:/ })
tablename=${array[0]}
tablecount=${array[1]}
tablecountincr=${array[2]}
if [[ "$tablename" != "table_name" ]];then
sql="select * from ${DATABASE}.${tablename} limit  ${tablecount}, ${tablecountincr}"
result="$($EXE_MYSQL -e "${sql}")"
echo  "${line}:${result}" >>${EXPORT_PATH}/${DATABASE}/incr/${tablename}.txt
fi
done
}

#
function main(){
tables
tablecount
backupAllTables
#compareFile
#compareContext
#backupIncrementTables
}
main