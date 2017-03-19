function uninstall-if-present {
  P=$1
  T=`yum list installed $P | grep $P | wc -l`
  if [ $T -ge 1 ]
  then
    sudo yum -y remove $P
  fi
}
sudo yum -y update
uninstall-if-present java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
sudo nohup /home/ec2-user/run.sh >/dev/null & 


