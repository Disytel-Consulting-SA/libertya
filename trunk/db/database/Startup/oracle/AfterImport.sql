/******************************************************************************
 *     El contenido de este fichero est� sujeto a la Licencia P�blica openXpertya versi�n 1.1 (LPO) en
 * tanto cuanto forme parte �ntegra del total del producto denominado:     openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *     Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *     Partes del c�digo son CopyRight � 2002-2005 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son CopyRight  � 2003-2005 de Consultor�a y Soporte en Redes y  Tecnolog�as de 
 * la  Informaci�n S.L., otras partes son adaptadas, ampliadas o mejoradas a partir de c�digo original
 * de terceros, recogidos en el ADDENDUM A, secci�n 3 (A.3) de dicha licencia LPO, y si dicho c�digo
 * es extraido como parte del total del producto, estar� sujeto a sus respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/licencia.html
 ******************************************************************************
 * $Id: AfterImport.sql,v 1.0 $
 ***
 * Title:	After Import
 * Description:	
 *	- General Info
 *	- Create Context
 *	- Check Imported User and list ivalid objects
 *	You can ignore import warnings as long as this does not 
 *	return error messages or records.
 ************************************************************************/

SELECT 'DB_Name=' || SYS_CONTEXT('USERENV', 'DB_NAME')
	|| ', Language=' || SYS_CONTEXT('USERENV', 'LANGUAGE')
	|| ', Host=' || SYS_CONTEXT('USERENV', 'HOST')
	|| ', IP=' || SYS_CONTEXT('USERENV', 'IP_ADDRESS')
	|| ', User=' || SYS_CONTEXT('USERENV', 'CURRENT_USER')
	|| ', ID=' || SYS_CONTEXT('USERENV', 'CURRENT_USERID')
	|| ', Session=' || SYS_CONTEXT('USERENV', 'SESSIONID')
	AS "DBInfo"
FROM DUAL
/
SET serveroutput ON

--	Recompile invalids
BEGIN
	DBA_Cleanup();
	DBA_AfterImport;
--	DBA_Recompile(NULL);    -- called in DBA_AfterImport
END;
/
--	Correct DataFile sizing
DECLARE
	CURSOR Cur_TS IS
		SELECT	FILE_NAME, Tablespace_Name, Bytes/1024/1024 as MB
		FROM	DBA_DATA_FILES
		WHERE	(TABLESPACE_NAME='USERS' AND BYTES < 100*1024*1024)
		  OR	(TABLESPACE_NAME='INDX' AND BYTES < 100*1024*1024)
		  OR	(TABLESPACE_NAME='TEMP' AND BYTES < 100*1024*1024);
	v_CMD			VARCHAR2(300);
BEGIN
	FOR ts IN Cur_TS LOOP 
    	DBMS_OUTPUT.PUT_LINE('Resize:');
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

--	Any invalids
SELECT	Object_Type "Type", Object_Name "Invalid", Status
FROM	User_Objects
WHERE	Status <> 'VALID'
/
--	Trigger Info
SELECT	Trigger_Name AS Trigger_NotEnabled, Status 
FROM	User_Triggers 
WHERE	Status != 'ENABLED'
/
--	Constraint Info
SELECT	Constraint_Name AS Constraint_Problem, Status, Validated, Table_Name, Search_Condition, R_Constraint_Name 
FROM	User_Constraints 
WHERE	Status <> 'ENABLED' OR Validated <> 'VALIDATED'
/
SELECT	* 
FROM	USER_ERRORS
/
COMMIT
/
EXIT
