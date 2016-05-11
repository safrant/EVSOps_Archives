#!/bin/sh
# *********************************************
# **** Command file to invoke Package util ****
# *********************************************
if [ -z "${JAVA_HOME}" ]; then
    echo JAVA_HOME is not set
    exit
fi

JAVAOPTS="-Xms1024M -Xmx1024M -XX:PermSize=256M -XX:MaxPermSize=256M"
# Execute the JVM
java $JAVAOPTS -jar package.jar $1 $2 $3 $4 $5 $6 $7 $8 $9