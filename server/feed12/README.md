# news feed microservice on play and scala

This is another implementation of the news feed microservice written in [Scala](https://www.scala-lang.org/) on the [Play Framework](https://www.playframework.com/). This project started from the example project for [Making a REST API in Play](http://developer.lightbend.com/guides/play-rest-api/index.html).

## Deving

How to develop on this service. You need to download and install sbt for this application to run.

### Unit Tests

This command will run the unit tests.

```bash
sbt test
```

### Running Locally

The following at the command prompt will start up Play in development mode:

```bash
sbt "run 8080"
```

Play will start up on the HTTP port at <http://localhost:8080/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.


### Load Testing

The best way to see what Play can do is to run a load test.  We've included Gatling in this test project for integrated load testing.

Start Play in production mode, by [staging the application](https://www.playframework.com/documentation/2.5.x/Deploying) and running the play script.

```bash
sbt stage
cd target/universal/stage
./bin/news-feed-play -Dplay.http.secret.key=LGG7q:ZHimG613xJXWL50cwgJ:nx6soU:XcPj2MhJ;GR[F0]ag:90cqpJ_ 
```

Then you'll start the Gatling load test up (it's already integrated into the project):

```bash
sbt ";project gatling;gatling:test"
```

Once the test completes, you'll see an HTML file containing the load test chart.

### Build and Release

To be run in Kubernetes.

```bash
sbt dist
cd target/universal
unzip news-feed-play-1.0-SNAPSHOT.zip
cd ../..
docker build -t feed12:1.0 .
```

## Static Code Analysis

Average per file Lines of Code is 55.48 with a median of 51 and a standard deviation of 32.92. The news action builder scala file (mostly boilerplate) is the largest with 123 LoC but the unit tests file is a close second at 122 LoC. Total McCabe cyclomatic complexity is 1,764.

## Load Test Results

Play can be configured to run with a choice of two server backend technologies, eitgher akka http or netty. Here are the per minute outbound post performance results for both. Throughput is in requests per minute. The other columns measure average and percentile based latency in milliseconds.

| backend | throughput | mean | median | 95th | 99th |
|---------|------------|------|--------|------|------|
| akka | 14,255 | 3.3 | 3 | 6 | 9 |
| netty | 20,151 | 4.4 | 4 | 8 | 11 |


