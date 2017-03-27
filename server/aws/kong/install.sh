sudo yum -y update
sudo yum -y install kong-0.8.3.aws.rpm
sudo cp kong.yml /etc/kong/kong.yml
ulimit -n 4096
kong start
sleep 30
./setup.sh
