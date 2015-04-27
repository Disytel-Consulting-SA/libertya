#!/bin/bash

# Reinicio de Postgres.
# Uso:
# 	restartPostgres.sh reiniciarLibertyad nombreDeBaseDeDatos
# donde:
#	reiniciarLibertyad intenta levantar libertyad si estaba levantado y ademas se recibe Y 
#	nombreDeBaseDeDatos permite redefinir libertya_prod (por defecto) con otro nombre

PG_USR=libertya
PG_DBN=libertya_prod
PG_HST=localhost
PG_BIN=/usr/local/pgsql/bin
PG_DAT=/usr/local/pgsql/data
PG_CMD=pg_ctl
PG_STARTING_RESULT_FILE=$HOME/resultStartingPostgres.txt
LY_STATUS_RESULT_FILE=$HOME/resultStatusLibertya.txt
PG_LOCKS_FILE=$HOME/locks_$(date +\%Y\%m\%d-\%H\%M\%S).sql
PG_ACTIVITY_FILE=$HOME/activity_$(date +\%Y\%m\%d-\%H\%M\%S).sql
OXP_HOME=/ServidorOXP


# Verificar si esta redefinido el nombre de la base de datos
if [ $2 ]; then
	PG_DBN=$2
fi

# =====================================================================================================================
# =================================================PostgreSQL==========================================================
# =====================================================================================================================


# Guardar estado de pg_stat_activity y de pg_locks
sudo -u postgres $PG_BIN/psql -U $PG_USR -h $PG_HST -d $PG_DBN -c "select * from pg_stat_activity" > $PG_ACTIVITY_FILE
sudo -u postgres $PG_BIN/psql -U $PG_USR -h $PG_HST -d $PG_DBN -c "select * from pg_locks" > $PG_LOCKS_FILE

# Reiniciar postgres, intentando los diferentes modos
echo =======================================
echo === Intentando reiniciar PostgreSQL ===
echo =======================================
STOPPED=0
for MODE in " " "-m s" "-m f" "-m i"
do
	echo === Ejecutando detencion - $MODE ===
	OUTPUT=$(sudo -u postgres $PG_BIN/$PG_CMD -D $PG_DAT stop $MODE)
	if [[ "$OUTPUT" =~ "stopped" ]]; then
		STOPPED=1
		break
	fi
done

if [[ "$STOPPED" == "1" ]]; then
	echo === Detenido! ===
else
	echo === No se pudo detener! estaba corriendo? ===
fi

# Garantizar que Postgre se detuvo por completo
echo Esperando un rato...
sleep 30;

# Matar toda posible existencia de postgres anterior (si aparece un solo proceso no es problema, es el grep)
echo === Eliminando eventuales procesos postgres remanentes ===
$OXP_HOME/utils/killEmAll.sh $PG_BIN/postgres

# Darle tiempo al kill
echo Esperando otro rato...
sleep 10;

# Al parecer no funciona que el resultado del comando de inicio del servidor postgres se guarde en una variable, por lo tanto guardamos la salida a un archivo txt y levantamos esa salida, primeramente borramos lo que hay en el archivo por si queda basura y estamos leyendo log de un inicio viejo, no debería pasar, pero por las dudas
echo Iniciando postgres mediante $PG_BIN/$PG_CMD -D $PG_DAT start
echo "" > $PG_STARTING_RESULT_FILE
sudo -u postgres $PG_BIN/$PG_CMD -D $PG_DAT start > $PG_STARTING_RESULT_FILE
STARTED=`cat $PG_STARTING_RESULT_FILE`

echo Estado: $STARTED
if [[ "$STARTED" =~ "starting" ]]; then
	# Inicio correctamente
	echo === Postgre Iniciado! ===
else
	# Si no pudo iniciar, evaluar el motivo
	if [[ "$STOPPED" == "0" ]]; then
		# Ninguno de los 4 modos lo detuvo
		echo === ERROR: No se pudo iniciar dado que no se pudo detener! ===
	else
		# Uno de los 4 modos lo detuvo
		echo === ERROR: No se pudo reiniciar luego de detener! ===
	fi
fi

# Garantizar que Postgre inicio por completo
echo Esperando un rato...
sleep 30;

# =====================================================================================================================
# =================================================ServidorOXP=========================================================
# =====================================================================================================================

# Reiniciar también el servidor de aplicaciones (en caso de que el mismo estaba corriendo)
service libertyad status > $LY_STATUS_RESULT_FILE
LYSTARTED=`cat $LY_STATUS_RESULT_FILE`

if [[ "$LYSTARTED" == "1" ]]; then
	if [[ $1 == "Y" ]]; then
	    echo === libertyad estaba corriendo, se reinicia ===
		# Bajar el server de manera normal
		service libertyad stop; 
		echo Esperando un rato...
		sleep 30; 
		# Matar posibles existencias del server.  Eliminar eventual existencia del PID generado por libertyad. (si aparece un solo proceso no es problema, es el grep)
		$OXP_HOME/utils/killEmAll.sh $OXP_HOME/jboss
		rm -f /var/run/libertya.pid
		# Iniciar normalmente
		service libertyad start;
		echo Esperando un rato mas...
		sleep 30; 
	else
		echo === libertyad estaba corriendo, pero no se reinicia debido al argumento en la invocacion ===
	fi
else
        echo === libertyad no estaba corriendo, no se reinicia ===		
fi

