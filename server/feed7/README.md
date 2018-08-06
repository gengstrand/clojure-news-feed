# News Feed via Hyperledger Composer

This is another implementation of the news feed using Hyperledger Composer. You can read more about how this project was used in [Evaluating Hyperledger Composer](https://www.infoq.com/articles/evaluating-hyperledger-composer).

## Components

This project is made up of two components, a [Hyperledger Composer](https://www.hyperledger.org/wp-content/uploads/2017/05/Hyperledger-Composer-Overview.pdf) business network and a DApp microservice that calls the business network.

### business-network

Contains the model, transactions, and query needed to implement the news feed on [Hyperledger Fabric](https://www.hyperledger.org/projects/fabric).

### micro-service

This is a Node.js microservice where I took the feed4 application and replaced all calls to MySql, Redis, and Cassandra with calls to the Composer API referencing the business network.

## Installation on AWS

This is dev focused. Spin up an m4.2xlarge with Ubuntu AMI then ssh to it.

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

The Hyperledger Composer Client API version has to match what is running on the server and they release new versions pretty frequently so be prepared to edit the micro-service/package.json file by editing the composer-client version number.

## License <a name="license"></a>
Hyperledger Project source code files are made available under the Apache License, Version 2.0 (Apache-2.0), located in the LICENSE file. Hyperledger Project documentation files are made available under the Creative Commons Attribution 4.0 International License (CC-BY-4.0), available at http://creativecommons.org/licenses/by/4.0/.
