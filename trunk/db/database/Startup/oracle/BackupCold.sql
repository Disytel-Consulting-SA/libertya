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
 * $Id: BackupCold.sql,v 1.0 $
 ***
 * Title:	Cold Backup
 * Description:	
 *	Generate OS cold Backup script
 *	Change the parameter variables p_ as required
 *	Run as system
 ************************************************************************/
SPOOL BackupCold.bat
DECLARE
	p_backupLocation		VARCHAR2(255) 	:= 'D:\Backup';
	p_unix					BOOLEAN 		:= FALSE;
	p_useZip				BOOLEAN			:= TRUE;
	p_zipCmd				VARCHAR(60)		:= 'wzzip '; 
	--
	v_delimiter				VARCHAR(10);
	v_remark				VARCHAR(10);
	v_copy					VARCHAR(60);
	v_sid					VARCHAR(30);
	v_cmd					VARCHAR2(2000);
	--
	CURSOR	CUR_DataFiles	IS
		SELECT	Name	
		FROM 		v$datafile;
	CURSOR	CUR_CtlFiles	IS
		SELECT 	Name		
		FROM 		v$controlfile;
BEGIN
	--	OS Specifics
	IF (p_unix) THEN
		v_delimiter := '/';
		v_remark := '# ';
		v_copy := 'cp ';
	ELSE
		v_delimiter := '\';
		v_remark := 'Rem ';
		v_copy := 'copy ';
	END IF;
	--	
	SELECT 	Value 
	  INTO	v_sid
	FROM 	v$parameter 
	WHERE 	Name ='instance_name';
	p_backupLocation := p_backupLocation || v_delimiter || v_sid;
	--
	DBMS_OUTPUT.PUT_LINE(v_remark || 'Backup script for "' || v_sid || '" to  ' || p_backupLocation);
	DBMS_OUTPUT.PUT_LINE(v_remark || 'This script is automatically created by ColdBackup.sql and may need to be edited';
	DBMS_OUTPUT.NEW_LINE;
	--
	DBMS_OUTPUT.PUT_LINE(v_remark || 'mkdir ' || p_backupLocation);
	DBMS_OUTPUT.NEW_LINE;

	DBMS_OUTPUT.PUT_LINE('sqlplus "system/manager@%AccortoDBService% AS SYSDBA" @%AccortoHome%\util\orastop.sql');
	DBMS_OUTPUT.NEW_LINE;

	IF (p_useZip) THEN
		DBMS_OUTPUT.PUT_LINE(p_zipCmd || p_backupLocation || v_delimiter || v_sid || '.zip ');
		DBMS_OUTPUT.PUT(' ');
	END IF;
	FOR f IN CUR_DataFiles LOOP
		IF (p_useZip) THEN
			DBMS_OUTPUT.PUT(' ' || f.Name);
	  	ELSE
			DBMS_OUTPUT.PUT_LINE(v_copy || f.Name || ' ' || p_backupLocation);
	  	END IF;
	END LOOP;
	IF (p_useZip) THEN
		DBMS_OUTPUT.NEW_LINE;
		DBMS_OUTPUT.PUT(' ');
	END IF;
	FOR f IN CUR_CtlFiles LOOP
		IF (p_useZip) THEN
			DBMS_OUTPUT.PUT(' ' || f.Name);
	  	ELSE
			DBMS_OUTPUT.PUT_LINE(v_copy || f.Name || ' ' || p_backupLocation);
	  	END IF;
	END LOOP;
	IF (p_useZip) THEN
		DBMS_OUTPUT.NEW_LINE;
	END IF;
	DBMS_OUTPUT.NEW_LINE;

	DBMS_OUTPUT.PUT_LINE('sqlplus  "system/manager@%AccortoDBService% AS SYSDBA" @%AccortoHome%\util\orastart.sql');
END;
/
SPOOL OFF
EXIT
