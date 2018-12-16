## news feed swagger

http://swagger.io/ is a popular MDSD framework that focuses on building
micro-services. The Swagger Code Generator is licensed under Apache 2.
This folder explores how to use swagger for the news feed.

### installing swagger codegen

```bash
git clone https://github.com/swagger-api/swagger-codegen.git
cd swagger-codegen
mvn clean compile install
```

### generating the news feed project files

#### for dropwizard

```bash
./news.sh /path/to/swagger-codegen-cli.jar
cd swagger-output
mvn compile
mvn test
```

#### for node.js

```bash
./news-nodejs.sh /path/to/swagger-codegen-cli.jar
```

#### for python

```bash
./news-flask.sh /path/to/swagger-codegen-cli.jar
```

#### for spring boot

```bash
./springboot-feed-server.sh /path/to/swagger-codegen-cli.jar
cd swagger-output
mvn compile
mvn test
```
