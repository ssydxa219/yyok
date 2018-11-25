#!/bin/bash                            #指定脚本解释器路径
echo "initial a disk……"
echo -e "\033[31mWarning!:\033[0m"     #提醒用户危险操作

fdisk -l 2> /dev/null | grep -o "disk /dev/[sh]d[a-z]" #显示所有分区

read -p "your choice:" partdisk #读入要操作的分区

if [ $partdisk == "quit" ];then
    echo "quit"
    exit 7    #错误退出，非0
fi

until fdisk -l 2> /dev/null | grep -o "disk /dev/[sh]d[a-z]" | grep "disk $partdisk$" &> /dev/null;do
    read -p "wrong option,your choice:"partdisk
done

read -p"are you sure?" choice

until [ $choice=='y' -o $choice =='n'];do
    read choice
done

if [$choice = n ];then
    exit 0
else
    dd if=/dev/zero of=$partdisk bs=512 count =1          #从设备/dev/zero输出0覆盖所在磁盘的第一个扇区，删除所有分区
    sync
    ##删除所有分区
    echo "partion"   ###
    sleep 3 #
    echo 'n                    #32-45行echo的内容，和命令行中操作步骤一样
    p                          #
    1                          #
                               #
    +20M                       #
    n                          #
    p                          #
    3                          #

    +128M
    t
    3
    82
    w'|fdisk $partition &>/dev/null      #返回消息送入/dev/null 设备
    sync
    partprobe $partdisk                  #写入硬盘
    sync
    sleep 3                             #同步内容花费时间较长，休眠3s否则下面格式化命令无法执行
    mke2fs -j ${partdisk}1 &>/dev/null
    mke2fs -j ${partdisk}2 &>/dev/null    #格式化分区，消息送入/dev/null
    mkswap ${partdisk}3&>/dev/null        #格式化交换分区


#make partition
dd if=/dev/zero of=/dev/mmcblk1 bs=1024 count=1024
fdisk /dev/mmcblk1 << EOF
n
p
1
2048
+100M
n
p
2


t
1
c
a
1
w
EOF