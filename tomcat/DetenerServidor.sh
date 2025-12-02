echo Deteniendo Servidor Libertya - $OXP_HOME \($NOMBRE_BD_OXP\)
CATALINA_OPTS="-Xms512M -Xmx1024M -DOXP_HOME=$OXP_HOME -Djava.awt.headless=true -Dfile.encoding=UTF-8"
export CATALINA_OPTS
./shutdown.sh