export OXP_HOME=/ServidorOXP
LOGFILE=log.txt
flock -n lock -c "date; cd /$OXP_HOME/utils/replicacion; java -classpath lib/repClient.jar:lib/lyws.jar/:../../lib/OXP.jar:../../lib/OXPSLib.jar:../../lib/mail.jar:../../lib/AxisJar.jar org.libertya.ws.client.ReplicationClientProcess $1 $2 $3 $4 $5 $6; date; echo ----------; "


