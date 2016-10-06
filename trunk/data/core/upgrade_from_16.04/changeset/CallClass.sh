if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
  echo JAVA_HOME is not set.
  echo You may not be able to start the Installer
  echo Set JAVA_HOME to the directory of your local JDK.
fi

$JAVA -Dfile.encoding=UTF-8 -classpath ../lib/OXP.jar:../lib/OXPXLib.jar:../lib/OXPSLib.jar:../lib/JasperReports.jar "$@" 

