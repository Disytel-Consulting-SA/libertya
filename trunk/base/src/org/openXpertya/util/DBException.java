/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import java.sql.SQLException;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DBException extends RuntimeException {

    /**
     * Constructor de la clase ...
     *
     *
     * @param e
     */

    public DBException( Exception e ) {
        super( e );
    }    // DBException

    /**
     * Constructor de la clase ...
     *
     *
     * @param msg
     */

    public DBException( String msg ) {
        super( msg );
    }    // DBException
    
	public DBException(SQLException e, String sql)
	{
		this(e);
		m_sql = sql;
	}
	private String m_sql = null;

	
    /**
     * Check if "child record found" exception (aka ORA-02292)
     * @param e exception
     */
    public static boolean isChildRecordFoundError(Exception e) {
    	if (DB.isPostgreSQL())
    		return isSQLState(e, "23503");
    	return isErrorCode(e, 2292);
    }
    
    /**
     * Check if "invalid identifier" exception (aka ORA-00904)
     * @param e exception
     */
    public static boolean isInvalidIdentifierError(Exception e) {
    	return isErrorCode(e, 904);
    }
    
    private static final boolean isSQLState(Exception e, String SQLState) {
    	if (e == null) {
    		return false;
    	}
    	else if (e instanceof SQLException) {
    		return ((SQLException)e).getSQLState().equals(SQLState);
    	}
    	else if (e instanceof DBException) {
    		SQLException sqlEx = ((DBException)e).getSQLException();
    		if (sqlEx != null)
    			return sqlEx.getSQLState().equals(SQLState);
    		else
    			return false;
    	}
    	return false;
    }

    private static final boolean isErrorCode(Exception e, int errorCode) {
    	if (e == null) {
    		return false;
    	}
    	else if (e instanceof SQLException) {
    		return ((SQLException)e).getErrorCode() == errorCode;
    	}
    	else if (e instanceof DBException) {
    		SQLException sqlEx = ((DBException)e).getSQLException();
    		if (sqlEx != null)
    			return sqlEx.getErrorCode() == errorCode;
    		else
    			return false;
    	}
    	return false;
    }

    /**
	 * @return Wrapped SQLException or null
	 */
	public SQLException getSQLException() {
		Throwable cause = getCause();
		if (cause instanceof SQLException)
			return (SQLException)cause;
		return null;
	}

	/**
     * Check if Unique Constraint Exception (aka ORA-00001)
     * @param e exception
     */
    public static boolean isUniqueContraintError(Exception e) {
    	if (DB.isPostgreSQL())
    		return isSQLState(e, "23505");
    	//
    	return isErrorCode(e, 1);
    }

	
}    // DBException



/*
 *  @(#)DBException.java   25.03.06
 * 
 *  Fin del fichero DBException.java
 *  
 *  Versión 2.2
 *
 */
