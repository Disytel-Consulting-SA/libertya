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
 * $Id: Daily.sql,v 1.0 $
 ***
 * Title:   Daily Tasks
 * Description:
 *	- Recompile 
 *  - Cleanup
 ************************************************************************/
DECLARE
	Result		VARCHAR2(2000);
BEGIN
    DBA_Recompile(Result);
    DBMS_OUTPUT.PUT_LINE(Result);
    DBA_Cleanup();
END;
/
EXIT
