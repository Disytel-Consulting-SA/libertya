/***************************************************************************************************
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2006 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2006 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 ***************************************************************************************************
 * $Id: CheckDB.sql,v 2.0 $
 ***
 * Title:	TabeSpace Sizing	 
 * Description:
 *			Make sure, that Tablespace 
 *				USERS is 150 MB, 10 MB Autoextend
 *				INDX is 100 MB, 10 MB Autoextend
 *				TEMP is 100 MB, 10 MB Autoextend
 *			Set SGA Size (optional)
 * Ejecutar con usuario System y parámetro %USUARIO_BD_OXP% de ArrancarVariablesOXP
 ************************************************************************/

-- Check existance
SELECT 'Tablespace USERS does not exist - You need to create it first' AS Missing FROM DUAL
WHERE NOT EXISTS (SELECT * FROM DBA_TABLESPACES WHERE TABLESPACE_NAME='USERS');
SELECT 'Tablespace INDX does not exist - You need to create it first' AS Missing FROM DUAL
WHERE NOT EXISTS (SELECT * FROM DBA_TABLESPACES WHERE TABLESPACE_NAME='INDX');
SELECT 'Tablespace TEMP does not exist - You need to create it first' AS Missing FROM DUAL
WHERE NOT EXISTS (SELECT * FROM DBA_TABLESPACES WHERE TABLESPACE_NAME='TEMP');

/*****
 *	Changing System Parameters
 *	directly - (e.g. 400 MB for 10 Users)
		ALTER SYSTEM SET SGA_MAX_SIZE=400M COMMENT='400MB' SCOPE=SPFILE;
		ALTER SYSTEM SET SHARED_POOL_SIZE=100M SCOPE=SPFILE;
		ALTER SYSTEM SET DB_CACHE_SIZE=200M SCOPE=SPFILE;
		ALTER SYSTEM SET JAVA_POOL_SIZE=40M SCOPE=SPFILE;
		ALTER SYSTEM SET LARGE_POOL_SIZE=10M SCOPE=SPFILE;
 **	indirectly - sqlplus "system/manager@openxp AS SYSDBA"
		CREATE PFile='pfileopenxp.ora' FROM SPFile;
 *	creates file in $OXP_HOME\database or $OXP_HOME/dbs
 *	edit file and then overwrite the fila via
		CREATE SPFile FROM PFile='pfileopenxp.ora';
 *****/

--	Create System Record - OK, if it fails
--	Schema is parameter.
INSERT INTO &1..AD_System
  (AD_System_ID,AD_Client_ID,AD_Org_ID, 
  IsActive,Created,CreatedBy,Updated,UpdatedBy,
  Name, UserName, Info)
SELECT	0,0,0,'Y', SysDate,0,SysDate,0, '?','?','?'
FROM	Dual
WHERE NOT EXISTS 
  (SELECT * FROM &1..AD_System WHERE AD_System_ID=0);

-- Add Info - OK, if fails
UPDATE &1..AD_System
SET Info = (SELECT SYS_CONTEXT('USERENV', 'DB_DOMAIN')
	|| ',' || SYS_CONTEXT('USERENV', 'DB_NAME')
	|| ',IP=' || SYS_CONTEXT('USERENV', 'IP_ADDRESS')
	|| ',' || SYS_CONTEXT('USERENV', 'HOST')
	|| ',' || SYS_CONTEXT('USERENV', 'INSTANCE')
	|| ',UID=' || SYS_CONTEXT('USERENV', 'CURRENT_USER')
	|| ',' || SYS_CONTEXT('USERENV', 'CURRENT_USERID')
	|| ',C#=' || (SELECT COUNT(*) FROM &1..AD_Client)
	FROM DUAL),
	Updated=SysDate;
COMMIT;

set serveroutput on
--	Correct sizing
DECLARE
	CURSOR Cur_TS IS
		SELECT	FILE_NAME, Tablespace_Name, Bytes/1024/1024 as MB
		FROM	DBA_DATA_FILES
		WHERE	(TABLESPACE_NAME='USERS' AND BYTES < 100*1024*1024)
		  OR	(TABLESPACE_NAME='INDX' AND BYTES < 100*1024*1024)
		  OR	(TABLESPACE_NAME='TEMP' AND BYTES < 100*1024*1024);
	v_CMD			VARCHAR2(300);
BEGIN
	DBMS_OUTPUT.PUT_LINE('Resize:');
	FOR ts IN Cur_TS LOOP 
		v_CMD := 'ALTER DATABASE DATAFILE ''' || ts.FILE_NAME
			|| ''' RESIZE 100M';
		DBMS_OUTPUT.PUT_LINE(' executing: ' || v_CMD);
		EXECUTE IMMEDIATE v_CMD;
		v_CMD := 'ALTER DATABASE DATAFILE ''' || ts.FILE_NAME
			|| ''' AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED';
		DBMS_OUTPUT.PUT_LINE(' executing: ' || v_CMD);
		EXECUTE IMMEDIATE v_CMD;
	END LOOP;
END;
/

EXIT
