## news feed swagger

http://swagger.io/ is a popular MDSD framework that focuses on building
micro-services. This folder explores how to use swagger for the news feed.

### installing swagger codegen

```bash
git clone https://github.com/swagger-api/swagger-codegen.git
cd swagger-codegen
mvn clean compile install
```

### generating the news feed project files

```bash
./news.sh /path/to/swagger-codegen-cli.jar
cd swagger-output
mvn compile
mvn test
```


