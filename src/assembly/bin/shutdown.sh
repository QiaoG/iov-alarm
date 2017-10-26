#!/bin/bash
PRGDIR=`dirname $0`
ALARM_HOME=`cd "${PRGDIR}/../" >/dev/null;pwd`;

kill -9 `cat ${ALARM_HOME}/data/pid`

echo shutdown ok!

