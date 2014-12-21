#!/bin/bash

PG_USR=libertya
PG_DBN=libertya_prod
PG_HST=localhos
PG_BIN=/usr/local/pgsql/bin
PG_KILL_PID=daemon_target_pid.txt
PG_KILL_LOG=daemon_trxtimeout.log

# Verificar si hay un caso para matar
sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -f trxTimeOut.sql -v p_dbname="'$PG_DBN'" -v p_timeout="'2 minutes'" > $PG_KILL_PID;
TARGET_LOCKED=`cat $PG_KILL_PID`;

# Tenemos un pid que matar?
date >> $PG_KILL_LOG;
if [ $TARGET_LOCKED ]; then
	echo "Matando PID $TARGET_LOCKED" >> $PG_KILL_LOG;
	sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "select * from pg_stat_activity where procpid = $TARGET_LOCKED" >> $PG_KILL_LOG;
	kill $TARGET_LOCKED;
else
	echo "Nada que matar" >> $PG_KILL_LOG;
fi



