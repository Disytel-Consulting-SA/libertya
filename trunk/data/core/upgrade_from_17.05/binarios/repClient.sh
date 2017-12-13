export OXP_HOME=/ServidorOXP
LOGFILE=log.txt
cd $OXP_HOME/utils/replicacion
flock -n lock -c "date; cd /$OXP_HOME/utils/replicacion; java -Xms256m -Xmx1024m -Dfile.encoding=UTF-8 -classpath lib/repClient.jar:lib/lyws.jar/:../../lib/OXP.jar:../../lib/OXPSLib.jar:../../lib/mail.jar:../../lib/AxisJar.jar org.libertya.ws.client.ReplicationClientProcess \"$1\" \"$2\" \"$3\" \"$4\" \"$5\" \"$6\" \"$7\" \"$8\"; date; echo ----------; "
