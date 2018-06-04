# News Feed Business Network

This is another implementation of the news feed using Hyperledger Composer.

## installation
This is dev focused. Spin up an m4.xlarge with Ubuntu AMI then ssh to it.
```
curl -O https://hyperledger.github.io/composer/latest/prereqs-ubuntu.sh
chmod u+x prereqs-ubuntu.sh
./prereqs-ubuntu.sh
# log out and back in
npm install -g composer-cli
mkdir ~/fabric-tools
cd ~/fabric-tools
curl -O https://raw.githubusercontent.com/hyperledger/composer-tools/master/packages/fabric-dev-servers/fabric-dev-servers.zip
unzip fabric-dev-servers.zip
./downloadFabric.sh
./startFabric.sh 
./createPeerAdminCard.sh 
cd ~/
git clone https://github.com/gengstrand/clojure-news-feed.git
cd clojure-news-feed/server/feed7/business-network
composer archive create -t dir -n .
composer network install --archiveFile news-feed@0.2.4-deploy.11.bna --card PeerAdmin@hlfv1
composer network start --networkName news-feed --networkVersion 0.2.4-deploy.11 --card PeerAdmin@hlfv1 --networkAdmin admin --networkAdminEnrollSecret adminpw --file networkAdmin.card
composer card import --file networkAdmin.card
composer network ping --card admin@news-feed
cd ../micro-service
export CARD_NAME=admin@news-feed 
export SEARCH_HOST=<<ip address>>
export SEARCH_PATH=/feed/stories
npm install
npm start
```

## License <a name="license"></a>
Hyperledger Project source code files are made available under the Apache License, Version 2.0 (Apache-2.0), located in the LICENSE file. Hyperledger Project documentation files are made available under the Creative Commons Attribution 4.0 International License (CC-BY-4.0), available at http://creativecommons.org/licenses/by/4.0/.
