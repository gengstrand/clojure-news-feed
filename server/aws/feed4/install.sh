sudo yum -y update
apt-get -y install git
sudo yum -y install nodejs npm --enablerepo=epel
git clone https://github.com/gengstrand/clojure-news-feed.git
cd clojure-news-feed/server/feed4
npm install
npm test

