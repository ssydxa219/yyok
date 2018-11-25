    #!/bin/bash

    fdisk -l 2> /dev/null | egrep "^Disk /dev/[s,h]d[[:alpha:]]" | awk -F: '{print $1}'
    read -p "Please input the device path that you want to set:" choice1

    until fdisk -l 2> /dev/null | egrep "^Disk /dev/[s,h]d[[:alpha:]]" | awk -F: '{print $1}'  | egrep "^Disk $choice1$" &> /dev/null
    do
    	[ $choice1 == "quit" ] && echo "Quiting..."  && exit 8
    	read -p "Please input the device path that you want to set:" choice1
    done
    read -p  "Your choice is $choice1, Partition will damamge the data, Please confirm(y|n):" choice2
    until [ $choice2 == "y"  -o $choice2 == "Y" -o $choice2 == "n" -o $choice2 == "N" ]
    do
    	read -p  "Your choice is $choice2 ,Partition will damamge the data, Please confirm(y|n):" choice2
    done
    if [ $choice2 == "N" -o $choice2 == "n" ]
    then
    	echo "Quiting.."
    	exit 9
    else
    	df | grep $choice1 &> /dev/null  && echo "$choice1 are using. Please umount it  first." && exit 9
    	echo "erasing the partition data....."
    	dd if=/dev/zero of=$choice1 bs=512 count=1 &> /dev/null
    	sync &&	sleep 3
    	partprobe $choice1
    	echo "Creating new partition..."
    echo 'n
    p
    1
    +20M
    n
    p
    2
    +512M
    n
    3
    +128M
    t
    3
    82
    w' | fdisk $choice1 &> /dev/null
    	sync && sleep 3
    	partprobe $choice1
    	echo "creating filesystem..."
    	mke2fs -j ${choice1}1 &> /dev/null
    	mke2fs -j ${choice1}2 &> /dev/null
    	mkswap ${choice3} &> /dev/null
    	mkdir -p /data/test1 /data/test2
    	df  | grep ${choice1}1 &> /dev/null && umount ${choice1}1
    	df  | grep ${choice1}2 &> /dev/null && umount ${choice1}2
    	mount ${choice1}1 /data/test1
    	mount ${choice1}2 /data/test2
    	swapon ${choice1}3  &> /dev/null
    	echo "The new filesystem has been mounted in /data/test"
    fi