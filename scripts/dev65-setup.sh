#!/bin/sh

getAbsolutePath()
{
    if [ -d "$1" ]
    then
        (cd "$1" && $(pwd) || exit)
    else
        (cd $(dirname "$1") && echo $(pwd)/$(basename "$1") || exit)
    fi
}

SELF=$(getAbsolutePath "$0")
SELF_DIR=$(dirname "$SELF")
JAR="$SELF_DIR"/$(basename --suffix=.sh "$0").jar
JVM_OPTS="-Dsun.java2d.uiScale=2 -Dsun.java2d.uiScale.enabled=true"

java $JVM_OPTS -jar "$JAR"
