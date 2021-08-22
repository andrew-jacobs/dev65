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
PARENT_DIR=$(dirname "$SELF_DIR")
JAR_DIR="$PARENT_DIR"/lib
CLASS_PATH="$JAR_DIR"/dev65.jar
MAIN_CLASS=uk.co.demon.obelisk.w65xx.Lk65

java -classpath "$CLASS_PATH" "$MAIN_CLASS" "%@"

