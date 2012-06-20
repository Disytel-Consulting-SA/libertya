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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CStatementVO implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param resultSetType
     * @param resultSetConcurrency
     */

    public CStatementVO( int resultSetType,int resultSetConcurrency ) {
        setResultSetType( resultSetType );
        setResultSetConcurrency( resultSetConcurrency );
    }    // CStatementVO

    /**
     * Constructor de la clase ...
     *
     *
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql
     */

    public CStatementVO( int resultSetType,int resultSetConcurrency,String sql ) {
        this( resultSetType,resultSetConcurrency );
        setSql( sql );
    }    // CStatementVO

    /** Descripción de Campos */

    static final long serialVersionUID = -3393389471515956399L;

    /** Descripción de Campos */

    private int m_resultSetType;

    /** Descripción de Campos */

    private int m_resultSetConcurrency;

    /** Descripción de Campos */

    private String m_sql;

    /** Descripción de Campos */

    private ArrayList m_parameters = new ArrayList();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "CStatementVO[" );

        sb.append( getSql());

        for( int i = 0;i < m_parameters.size();i++ ) {
            sb.append( "; #" ).append( i + 1 ).append( "=" ).append( m_parameters.get( i ));
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param index1
     * @param element
     */

    public void setParameter( int index1,Object element ) {
        if( (element != null) &&!( element instanceof Serializable )) {
            throw new java.lang.RuntimeException( "setParameter not Serializable - " + element.getClass().toString());
        }

        int zeroIndex = index1 - 1;

        if( m_parameters.size() == zeroIndex ) {
            m_parameters.add( element );
        } else if( m_parameters.size() < zeroIndex ) {
            while( m_parameters.size() < zeroIndex ) {
                m_parameters.add( null );    // fill with nulls
            }

            m_parameters.add( element );
        } else {
            m_parameters.set( zeroIndex,element );
        }
    }                                        // setParametsr

    /**
     * Descripción de Método
     *
     */

    public void clearParameters() {
        m_parameters = new ArrayList();
    }    // clearParameters

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getParameters() {
        return m_parameters;
    }    // getParameters

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getParameterCount() {
        return m_parameters.size();
    }    // getParameterCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSql() {
        return m_sql;
    }    // getSql

    /**
     * Descripción de Método
     *
     *
     * @param sql
     */

    public void setSql( String sql ) {
        if( (sql != null) && DB.isRemoteObjects()) {

            // Handle RowID in the select part (not where clause)

            int pos      = sql.indexOf( "ROWID" );
            int posTrim  = sql.indexOf( "TRIM(ROWID)" );
            int posWhere = sql.indexOf( "WHERE" );

            if( (pos != -1) && (posTrim == -1) && ( (posWhere == -1) || (pos < posWhere) ) ) {
                m_sql = sql.substring( 0,pos ) + "TRIM(ROWID)" + sql.substring( pos + 5 );
            } else {
                m_sql = sql;
            }
        } else {
            m_sql = sql;
        }
    }    // setSql

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getResultSetConcurrency() {
        return m_resultSetConcurrency;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getResultSetType() {
        return m_resultSetType;
    }

    /**
     * Descripción de Método
     *
     *
     * @param resultSetType
     */

    public void setResultSetType( int resultSetType ) {
        m_resultSetType = resultSetType;
    }

    /**
     * Descripción de Método
     *
     *
     * @param resultSetConcurrency
     */

    public void setResultSetConcurrency( int resultSetConcurrency ) {
        m_resultSetConcurrency = resultSetConcurrency;
    }
}    // CStatementVO



/*
 *  @(#)CStatementVO.java   25.03.06
 * 
 *  Fin del fichero CStatementVO.java
 *  
 *  Versión 2.2
 *
 */
