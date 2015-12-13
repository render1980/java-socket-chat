#! /bin/sh
echo ":: JAVA version ::"
java --version || exit 1

export DERBY_HOME=$JAVA_HOME/db
$DERBY_HOME/bin/startNetworkServer -h 0.0.0.0 -p 1527
