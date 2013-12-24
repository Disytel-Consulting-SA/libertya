# ==========================================
# Mata todos los procesos con un nombre dado
# ==========================================

if [ -n "$1" ]; then
	echo Buscando procesos a matar
	PIDS=`ps -aeo pid,cmd | grep $1 | grep -v "grep" | awk '{print $1}'`
	if [ -n "$PIDS" ]; then
		echo Matando procesos con nombre $1
		echo $PIDS | xargs kill -9
	else
		echo No hay procesos con el nombre $1!
	fi
else
	echo Debe especificar nombre de la aplicacion!
fi


