#!/bin/bash
LY_HOME=/ServidorOXP
PG_USR=libertya
PG_DBN=libertya_prod
PG_HST=localhost
PG_BIN=/usr/local/pgsql/bin
PG_KILL_PID=keepalive_target_pid.txt
PG_KILL_MAIL=daemon_target_mail.txt
PG_KILL_LOG=$HOME/keepalive_timeout_$(date +\%Y\%m\%d).log

# Verificar si hay un caso para matar, luego de un tiempo prudente que el keepAlive no ejecuto
$PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -f $LY_HOME/utils/keepAliveDaemon.sql -v p_dbname="'$PG_DBN'" -v p_timeout="'2 minutes'" > $PG_KILL_PID;
TARGET_LOCKED=`cat $PG_KILL_PID`;

# Tenemos un pid que matar?
if [ $TARGET_LOCKED ]; then
	echo "==========================================================" >> $PG_KILL_LOG;
	echo "$(date +\%H:\%M:\%S) KILLING PID $TARGET_LOCKED" >> $PG_KILL_LOG;
	$PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "SELECT current_query FROM pg_stat_activity WHERE procpid = $TARGET_LOCKED" >> $PG_KILL_LOG;
	echo "==========================================================" >> $PG_KILL_LOG;
	$PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "SELECT pg_terminate_backend($TARGET_LOCKED)"
	$PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "DELETE FROM ad_keepalive WHERE pid = $TARGET_LOCKED"
else
	echo "$(date +\%H:\%M:\%S) Nothing to kill" >> $PG_KILL_LOG;
fi

# Eliminar eventuales keepalives innecesarios que no llegaron a ejecutar el removeStatement (por ejemplo por cierre de la aplicacion LY)
$PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "DELETE FROM ad_keepalive WHERE age(now(), created) > '30 seconds' AND pid NOT IN (SELECT procpid FROM pg_stat_activity WHERE datname = '$PG_DBN')"