#!/bin/bash

# chkconfig: 2345 98 02
# description: Libertya Application Server

## Libertyad v0.3
## path: /etc/init.d/libertyad
## author: Hermann D. Schimpf

# verificamos si el usuario es root
if [ $EUID -ne 0 ] ;then
# mostramos un mensaje
echo "Se requiere ejecutar como root" 2>&1
# salimos de la ejecucion
exit 1
fi

# creamos una funcion
function findLogs() {
# esta funcion retorna la cantidad de ficheros log del directorio de Libertya
(( $1 = `find /ServidorOXP/log/ServidorOXP_\`date +%Y-%m-%d\`_* -type f -exec ls -l {} \; 2> /dev/null | wc -l` ))
}

# almacenamos el nombre del fichero PID para el demonio Libertya
PID=/var/run/libertya/libertya.pid
# almacenamos la ruta al script a ejecutar para iniciar el demonio Libertya
STARTLIBERTYA=/ServidorOXP/utils/IniciarServidor.sh
# almacenamos la ruta al script para detener el demonio Libertya
STOPLIBERTYA=/ServidorOXP/utils/DetenerServidor.sh

# exportamos la variable con la ruta a java
export JAVA_HOME=/usr/lib/jvm/java-6-sun
# exportamos la variable con la ruta al servidor de aplicaciones
export OXP_HOME=/ServidorOXP

# ingresamos a un case para verificar que accion tomamos
case "$1" in
# si especifico 'start'
start)
# verificamos si el demonio esta corriendo
if test -f $PID ;then
# mostramos un mensaje
echo "El servidor ya se encuentra iniciado"
else
# mostramos un mensaje
echo -n "Iniciando Servidor Libertya "
# iniciamos el servidor de aplicaciones como demonio
mkdir /var/run/libertya/
chown libertya.libertya /var/run/libertya/
start-stop-daemon --start --chuid libertya --background -m --pidfile $PID --exec $STARTLIBERTYA
# obtenemos la cantidad actual de ficheros
findLogs cantLogs
# obtenemos la cantidad actual de ficheros
findLogs cantLogsNew
# ingresamos a un bucle, mientras exista la misma cantidad de ficheros
while [ $cantLogs = $cantLogsNew ]; do
# esperamos 900 milisegundos
sleep 0.9
echo -n "."
# volvemos a obtener la cantidad de ficheros
findLogs cantLogsNew
done
# mostramos ok al mensaje
echo "[OK]"
fi
;;
# si especifico stop
stop)
# mostramos un mensaje
echo -n "Deteniendo servidor Libertya "
# verificamos si el demonio esta corriendo
if test -f $PID ;then
# detenemos el demonio
cd $OXP_HOME/utils
$STOPLIBERTYA -S > /dev/null 2>&1
# esperamos 10 segundos
wait=13
s=0
while [ $s -lt $wait ]; do
sleep 0.8
echo -n "."
s=`expr $s + 1`
done

# eliminamos el fichero PID del demonio
rm -f $PID
# mostramos ok al mensaje
echo " [OK]"
else
# mostramos un mensaje
echo "El servidor no esta iniciado"
fi
;;
# si especifico restart
restart)
# nos reejecutamos con el comando stop
$0 stop
# esperamos un segundo
sleep 1
# nos reejecutamos con el comando start
$0 start
;;
# si se especifico status
status)
# verificamos si el demonio esta corriendo
if test -f $PID ;then
# mostramos un mensaje. 1 implica que si esta corriendo
echo "1"
else
# mostramos un mensaje. 0 implica que no esta corriendo
echo "0"
fi
;;
# en cualquier otro caso
*)
# mostramos un mensaje
echo "Utilizacion: /etc/init.d/$0 {start|stop|restart|status}"
# salimos de la ejecucion
exit 1
;;
esac
exit 0
