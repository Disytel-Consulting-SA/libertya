# $Id: EstablecerVariables.sh,v 2.0 $
echo Comprobar y Establecer Variables

if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
# Las Variables son leidas del guión Variables.sh
. ./Variables.sh

echo General ...
echo PATH      = $PATH
echo CLASSPTH  = $CLASSPATH

echo .
echo Directorios base ...
echo OXP_HOME        = $OXP_HOME
echo JAVA_HOME       = $JAVA_HOME
echo URL_BD_OXP      = $URL_BD_OXP

echo .
echo Base de Datos ...
echo USUARIO_BD_OXP     = $USUARIO_BD_OXP
echo PASSWD_BD_OXP = $PASSWD_BD_OXP
echo RUTA_BD_OXP     = $RUTA_BD_OXP

echo .. Para Oracle
echo NOMBRE_BD_OXP      = $NOMBRE_BD_OXP
echo SYSTEM_BD_OXP   = $SYSTEM_BD_OXP

echo .
echo Comprueba Java  ... debe ser 1.5 o superior
$JAVA_HOME/bin/java -version

echo .
echo Testeo de Conexion para la Base de Datos \(1\) ... TNS
echo Iniciando tnsping $NOMBRE_BD_OXP
tnsping $NOMBRE_BD_OXP

echo .
echo Testeo de Conexion con la Base de Datos \(2\)... System
echo Running sqlplus system/$SYSTEM_BD_OXP@$NOMBRE_BD_OXP @$RUTA_BD_OXP/Test.sql
sqlplus system/$SYSTEM_BD_OXP@$NOMBRE_BD_OXP @$HOME_BD_OXP/Test.sql 

echo .
echo Testeando Tamaño de Base de Datos \(3\)
sqlplus system/$SYSTEM_BD_OXP@$NOMBRE_BD_OXP @$HOME_BD_OXP/CheckDB.sql $USUARIO_BD_OXP

echo .
echo == Es normal que falle si no ha sido importada la base de datos ==
echo Testeo de Conexión de la Base de Datos \(4\) ... openXpertya \(No debe funcionar si el usuario no ha sido importado\)
sqlplus $USUARIO_BD_OXP/$PASSWD_BD_OXP@$NOMBRE_BD_OXP @$HOME_BD_OXP/Test.sql

echo .
echo Hecho

