#!/bin/bash

export JAVA_HOME=`/usr/libexec/java_home -v 1.7`

JAVA_OPTS="-server -Xms2g -Xmx2g"

java $JAVA_OPTS -jar target/speedtest-0.1-SNAPSHOT-jar-with-dependencies.jar $@
