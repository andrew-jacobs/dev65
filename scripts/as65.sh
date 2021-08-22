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
MAIN_CLASS=uk.co.demon.obelisk.w65xx.As65
OUTPUT_CAPTURE=as65-output.log

# As65 does not exit with a non-zero code when it fails, so another method
# has to be used to detect failure, so that this script can exit with a
# non-zero code when it fails. This is necessary for "make" to work properly.
# If As65 writes any output, then it will be assumed that it failed and then
# this script will exit with a non-zero exit code.
java -classpath "$CLASS_PATH" "$MAIN_CLASS" "%@" 2>&1 \
    | tee "$OUTPUT_CAPTURE" && [ ! -s "$OUTPUT_CAPTURE" ]

