echo "-------------$1: from file  $2: to dir---------------"
hostnames=('ddb' 'ddc' 'dde' 'ddf')
for hostname in ${hostnames[@]}
do
    echo "=======$hostname======="
    if [ $1=='' || $2=='' ]; then
    	echo "-------------$1: from file  $2: to dir---------------"
    else
    	scp -r $1 root@$hostname:$2
    fi
done