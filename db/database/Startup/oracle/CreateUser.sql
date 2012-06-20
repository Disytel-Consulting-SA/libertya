SET ECHO ON
/******************************************************************************
 *     El contenido de este fichero está sujeto a la Licencia Pública openXpertya versión 1.1 (LPO) en
 * tanto cuanto forme parte íntegra del total del producto denominado:     openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *     Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *     Partes del código son CopyRight © 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son CopyRight  © 2003-2005 de Consultoría y Soporte en Redes y  Tecnologías de 
 * la  Información S.L., otras partes son adaptadas, ampliadas o mejoradas a partir de código original
 * de terceros, recogidos en el ADDENDUM A, sección 3 (A.3) de dicha licencia LPO, y si dicho código
 * es extraido como parte del total del producto, estará sujeto a sus respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/licencia.html
 ******************************************************************************
 * $Id: CreateUser.sql,v 1.0 $
 ***
 * Title:	Drop User and re-create new
 * Description:	
 *	Parameter: UserID UserPwd
 *	Run as system
 ************************************************************************/
DROP USER &1 CASCADE
/
CREATE USER &1 IDENTIFIED BY &2
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    PROFILE DEFAULT
    ACCOUNT UNLOCK
/
GRANT CONNECT TO &1
/
GRANT DBA TO &1
/
GRANT RESOURCE TO &1
/
GRANT UNLIMITED TABLESPACE TO &1
/
ALTER USER &1 DEFAULT ROLE CONNECT, RESOURCE, DBA
/
GRANT CREATE TABLE TO &1
/
EXIT
