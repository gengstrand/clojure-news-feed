This is a quickstart for a Vert.x Scala project. It provides a few examples for doing 
unit-tests.

It comes with `vertx-core` and `vertx-web` so you are good to go for a little REST-project.
Take your time and take a look.

# Scala console

After launching `sbt` you can switch to the _scala-console_. There we took care that you
get an already initialized Vert.x-instance and the necessary imports to start playing around.

```
sbt
> console
scala> vertx.deployVerticle(nameForVerticle[info.glennengstrand.news.HttpVerticle])
scala> vertx.deploymentIDs.foreach(id => { vertx.undeploy(id) })
```

From here you can freely interact with the Vert.x API inside the sbt-scala-shell.


# Fat-jar

Take a look at the _build.sbt_ and search for the entry _packageOptions_. Enter the fully qualified class name 
of your primary verticle. This will be used as entry point for a generated fat-jar.

To create the runnable fat-jar use:
```
sbt assembly
```


# Dockerize

The project also contains everything you need to create a Docker-container. Simply run the following command to package your fat-jar inside a Docker-container

```
sbt docker
```

To run use

```
docker run -p 8666:8666 default/vertx-scala-sbt
```

Point your browser to [http://127.0.0.1:8666/hello](http://127.0.0.1:8666/hello) and enjoy :)
