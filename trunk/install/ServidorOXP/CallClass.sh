if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
  echo JAVA_HOME is not set.
  echo You may not be able to start the Installer
  echo Set JAVA_HOME to the directory of your local JDK.
fi

$JAVA -classpath lib/OXP.jar:lib/OXPXLib.jar $1 $2 $3 $4 $5 $6 $7 $8 $9 $10

