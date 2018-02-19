#!/bin/sh

SCRIPT="$0"
if [ "$#" -lt 1 ]
then
    echo "usage: ${SCRIPT} /path/to/swagger-codegen-cli.jar"
    exit -1
fi
EXECUTABLE="$1"
if [ ! -f "$EXECUTABLE" ]
then
    echo "$EXECUTABLE does not exist"
    exit -1
fi

APP_DIR=`pwd`

if [ ! -d "${APP_DIR}/swagger-output" ]
then
    mkdir ${APP_DIR}/swagger-output
else
    rm -Rf ${APP_DIR}/swagger-output/*
fi

# if you've executed sbt assembly previously it will use that instead.
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
ags="generate -i ${APP_DIR}/news.yaml -c ${APP_DIR}/news-scala.json -t ${APP_DIR}/templates/scalatra -l scalatra -o ${APP_DIR}/swagger-output"

java $JAVA_OPTS -jar $EXECUTABLE $ags






