sudo yum -y update
sudo yum -y remove java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
sudo nohup /home/ec2-user/run.sh >/dev/null & 
