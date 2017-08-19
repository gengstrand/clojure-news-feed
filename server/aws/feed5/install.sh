sudo yum update -y
sudo yum -y install git
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
git clone https://github.com/gengstrand/clojure-news-feed.git
cp Dockerfile clojure-news-feed/server/feed5
cp config-aws.cfg clojure-news-feed/server/feed5

