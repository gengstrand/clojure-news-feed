# newsfeed-dropwizard

This Java project implements a rudimentary news feed micro-service using [Swagger](http://swagger.io/) and [DropWizard](http://www.dropwizard.io/1.0.5/docs/).

This [article](http://glennengstrand.info/media/dropwizard.pdf) goes into detail about what was discovered while developing this micro-service.

http://glennengstrand.info/software/performance/clojure/dropwizard

## newsfeed-swagger

The rest of these docs are swagger generated and cover how to call the micro-service.

See the docs/setup.md for tips on how to stand up the service.

### Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

### Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

#### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>info.glennengstrand</groupId>
    <artifactId>newsfeed-swagger</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

#### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/newsfeed-swagger-1.0.0-SNAPSHOT.jar
* target/lib/*.jar

### Documentation for API Endpoints

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*FriendApi* | [**addFriend**](docs/FriendApi.md#addFriend) | **POST** /friends/new | create a new friendship
*FriendApi* | [**getFriend**](docs/FriendApi.md#getFriend) | **GET** /friends/{id} | retrieve the list of friends for an individual participant
*InboundApi* | [**getInbound**](docs/InboundApi.md#getInbound) | **GET** /inbound/{id} | retrieve the inbound feed for an individual participant
*OutboundApi* | [**addOutbound**](docs/OutboundApi.md#addOutbound) | **POST** /outbound/new | create a participant news item
*OutboundApi* | [**getOutbound**](docs/OutboundApi.md#getOutbound) | **GET** /outbound/{id} | retrieve the news posted by an individual participant
*OutboundApi* | [**searchOutbound**](docs/OutboundApi.md#searchOutbound) | **POST** /outbound/search | create a participant news item
*ParticipantApi* | [**addParticipant**](docs/ParticipantApi.md#addParticipant) | **POST** /participant/new | create a new participant
*ParticipantApi* | [**getParticipant**](docs/ParticipantApi.md#getParticipant) | **GET** /participant/{id} | retrieve an individual participant


### Documentation for Models

 - [Friend](docs/Friend.md)
 - [Inbound](docs/Inbound.md)
 - [Outbound](docs/Outbound.md)
 - [Participant](docs/Participant.md)


