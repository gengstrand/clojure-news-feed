sudo yum -y update
gunzip redis-3.2.1.tar.gz 
tar -xf redis-3.2.1.tar 
cd redis-3.2.1
sudo yum -y install gcc
sudo yum -y install tcl
cd deps
make hiredis jemalloc linenoise lua
cd ..
make
make test
cp ../redis.conf .
src/redis-server redis.conf
