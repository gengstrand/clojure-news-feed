FROM gradle:8.5.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM eclipse-temurin:17
EXPOSE 8080
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/newsfeed-0.0.1-SNAPSHOT.jar .
RUN bash -c 'touch /app/newsfeed-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/newsfeed-0.0.1-SNAPSHOT.jar"]
