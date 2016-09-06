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

export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
ags="$@ generate -i ${APP_DIR}/news.yaml -l java -c ${APP_DIR}/news.json -t ${APP_DIR}/templates/dropwizard -o ${APP_DIR}/swagger-output -DhideGenerationTimestamp=true"

java $JAVA_OPTS -jar $executable $ags

cd ${APP_DIR}/swagger-output/src/main/java/info/glennengstrand
rm ApiClient.java StringUtil.java ApiException.java Configuration.java Pair.java JSON.java
rm -Rf auth
rm ${APP_DIR}/swagger-output/src/main/AndroidManifest.xml
cd ${APP_DIR}/swagger-output
rm build.*
rm settings.*
rm -Rf gradle
rm gradle*
rm git_push.sh




