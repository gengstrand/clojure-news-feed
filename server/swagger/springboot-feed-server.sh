#!/bin/sh

SCRIPT="$0"

while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

if [ ! -d "${APP_DIR}" ]; then
  APP_DIR=`dirname "$SCRIPT"`/..
  APP_DIR=`cd "${APP_DIR}"; pwd`
fi

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

export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
ags="$@ generate -t templates/springboot -i news.yaml -l spring -c news-springboot.json -o ${APP_DIR}/swagger-output -DhideGenerationTimestamp=true -DapiTests=false"

java $JAVA_OPTS -jar $executable $ags
