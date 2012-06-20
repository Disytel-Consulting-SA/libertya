echo Instalar_DB_Postgresql.bat,v 2.0

echo Crear Base de Datos - $OXP_HOME ($NOMBRE_BD_OXP)

# Crea el rol de openxp y la base de datsos
$OXP_HOME/utils/Postgresql/Crear_DB_Postgresql.sh
# Importa la base de datos inicial
$OXP_HOME/utils/Postgresql/Importar_DB_Postgresql.sh
