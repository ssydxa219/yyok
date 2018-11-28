#! /bin/bash

echo "===================Hello world! start ============================"
function excute2sql(){
  result= mysql -h$1 -P3306 -u$2 -p$3 --default-character-set=utf8 -A -N -e "$4"
  echo query success
  return $result
}

function load2local(){
echo $3>>/data/local/tmp/$1/$2.txt
echo 'success'
}

function local2hdfs(){
if [ ! -d "/data/$1/input" ]; then
  mkdir /data/$1/input
fi

hdfs dfs -moveFromLocal '/data/local/tmp/$1/$2.txt' '/data/$1/input'
echo 'success'
}

function createTbale(){

sql="CREATE EXTERNAL TABLE IF NOT EXISTS  $1.$2 ("
excute2sql "select count(distinct column_name) from information_schema.columns  where table_schema='$1' and table_name='$2'"
lenth=$?
excute2sql "select concat('`',column_name,'`',' ',if(data_type<>'int','string','int'),' ',case when column_comment <>'' then concat('comment "',column_comment,'",') else ','  end ) clm  from information_schema.columns  where table_schema='$1' and table_name='$2'"
columns=$?
i=0
let str
for column in $columns;do
  i=$(( $i + 1 ))
  str+=$column
  if [ $lenth -eq $i ];then
          hive -e "use $1;CREATE EXTERNAL TABLE IF NOT EXISTS  $1.$2 (${str%?}) ROw FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' STORED AS TEXTFILE;"
          echo 'create table success'
   fi

done

}

function load2Tbale(){
  hive -e "use $1;load data inpath '/data/$1/input/$2.txt'  INTO TABLE $1.$2;"
}

function main(){
    #修改字符集

    #load2local $1 $2
    #local2hdfs $1 $2
    #createTbale $1 $2
    load2Tbale $1 $2

}

echo "===================Hello world! over ============================"
date