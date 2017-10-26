#!/bin/bash
if [ "xJAVA_HOME" = "x" ]; then
  echo "JAVA_HOME not set yet!"
  exit 1
fi


# Minimal version
MINIMAL_VERSION=1.7.0

# Check if Java is present and the minimal version requierement
_java=`type java | awk '{ print $ NF }'`
CURRENT_VERSION=`"$_java" -version 2>&1 | awk -F'"' '/version/ {print $2}'`
minimal_version=`echo $MINIMAL_VERSION | awk -F'.' '{ print $2 }'`
current_version=`echo $CURRENT_VERSION | awk -F'.' '{ print $2 }'`
if [ $current_version ]; then
        if [ $current_version -lt $minimal_version ]; then
                 echo "Error: Java version is too low to run JMeter. Needs at least Java >= ${MINIMAL_VERSION}."
                 exit 1
        fi
    else
         echo "Not able to find Java executable or version. Please check your Java installation."
         exit 1
fi

JVM_ARGS="-Xms1024m -Xmx1024m"
JVM_ARGS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=11067 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

PRGDIR=`dirname $0`

echo PRGDIR is ${PRGDIR}

ALARM_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

echo ALARM_HOME is ${ALARM_HOME}

export ALARM_HOME

LIB_PATH=${ALARM_HOME}/lib

echo LIB_PATH = ${LIB_PATH}

for jar in $(ls ${LIB_PATH}/*.jar);do
 CLASS_PATH=$CLASS_PATH:$jar
done

CLASS_PATH=${CLASS_PATH}:${ALARM_HOME}

echo CLASS_PATH is ${CLASS_PATH}

echo exec Main
nohup $_java ${JVM_ARGS} -cp ${CLASS_PATH} com.hxht.iov.alarm.AppMain &>${ALARM_HOME}/log/alarm.out &
echo $!>${ALARM_HOME}/data/pid

