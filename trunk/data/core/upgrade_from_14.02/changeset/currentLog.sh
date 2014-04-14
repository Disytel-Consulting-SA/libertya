LINES=150;

if [ $1 ]; then
	LINES=$1
fi

echo
echo
echo ======================================================================================
echo Mostrando $LINES lineas del log de replicacion del dia $(date +\%Y-\%m-\%d)
echo ======================================================================================
echo
echo

tail -n $LINES "/root/repLog/repLog_$(date +\%Y-\%m-\%d).log"
