@echo off

set JAR_DIR=%~dp0../lib
set CLASS_PATH="%JAR_DIR%"/dev65.jar
set MAIN_CLASS=uk.co.demon.obelisk.w65xx.As65
set OUTPUT_CAPTURE=as65-output.log

rem As65 does not exit with a non-zero code when it fails, so another method
rem has to be used to detect failure, so that this script can exit with a
rem non-zero code when it fails. This is necessary for "make" to work properly.
rem If As65 writes any output, then it will be assumed that it failed and then
rem this script will exit with a non-zero exit code.
java -classpath "%CLASS_PATH%" "%MAIN_CLASS%" %* 2>&1 ^
    > "%OUTPUT_CAPTURE%"

