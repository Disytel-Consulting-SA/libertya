OXP_HOME=/ServidorOXP
java -Dfile.encoding=UTF-8 -classpath $OXP_HOME/lib/OXP.jar:$OXP_HOME/lib/OXPXLib.jar:$OXP_HOME/lib/OXPSLib.jar org.openXpertya.plugin.install.ExportPlugin "$@" 