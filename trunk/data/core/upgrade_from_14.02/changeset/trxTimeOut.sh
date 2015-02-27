#!/bin/bash

PG_USR=libertya
PG_DBN=libertya_prod
PG_HST=localhost
PG_BIN=/usr/local/pgsql/bin
PG_KILL_PID=daemon_target_pid.txt
PG_KILL_MAIL=daemon_target_mail.txt
PG_KILL_LOG=$HOME/daemon_trxtimeout_$(date +\%Y\%m\%d).log

# Verificar si hay un caso para matar
sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -f trxTimeOut.sql -v p_dbname="'$PG_DBN'" -v p_timeout="'1 minute'" > $PG_KILL_PID;
TARGET_LOCKED=`cat $PG_KILL_PID`;

# Tenemos un pid que matar?
if [ $TARGET_LOCKED ]; then
	echo "$(date +\%H:\%M:\%S) KILLING PID $TARGET_LOCKED" >> $PG_KILL_LOG;
	echo "====================================================ACTIVITY=======================================================" >> $PG_KILL_LOG;
	sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "select * from pg_stat_activity" >> $PG_KILL_LOG;
	echo "======================================================LOCKS========================================================" >> $PG_KILL_LOG;
	sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "select * from pg_locks" >> $PG_KILL_LOG;
	echo "===================================================================================================================" >> $PG_KILL_LOG;
	kill $TARGET_LOCKED;

	sudo -u postgres $PG_BIN/psql -h $PG_HST -U $PG_USR -t -d $PG_DBN -c "select value from ad_preference where attribute ilike 'ReplicationAdmin'" -v p_dbname="'$PG_DBN'" > $PG_KILL_MAIL;
	TARGET_MAIL=`cat $PG_KILL_MAIL`;
	mail -s "IDLE IN TRANSACTION KILLED!" $TARGET_MAIL < $PG_KILL_LOG;
else
	echo "$(date +\%H:\%M:\%S) Nothing to kill" >> $PG_KILL_LOG;
fi
