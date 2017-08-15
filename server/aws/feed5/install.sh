sudo yum -y update
sudo apt-get -y install git
sudo yum install -y yum-utils
sudo yum-builddep -y python
tar xf Python-3.6.1.tgz
cd Python-3.6.1
./configure
make
sudo make install
cd ..
unzip mysql-connector-python-2.0.4.zip
cd mysql-connector-python-2.0.4
sudo /usr/local/bin/python3.6 setup.py install
cd ..
git clone https://github.com/gengstrand/clojure-news-feed.git
cd clojure-news-feed/server/feed5
pip3 install --no-cache-dir -r requirements.txt
export APP_CONFIG=/home/$USER/config-aws.cfg
python3 -m swagger_server
