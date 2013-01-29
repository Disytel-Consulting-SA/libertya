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



package org.openXpertya.dbPort;

import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Join {

    /**
     * Constructor de la clase ...
     *
     *
     * @param joinClause
     */

    public Join( String joinClause ) {
        if( joinClause == null ) {
            throw new IllegalArgumentException( "Join - clause cannot be null" );
        }

        evaluate( joinClause );
    }    // Join

    /** Descripción de Campos */

    private String m_joinClause;

    /** Descripción de Campos */

    private String m_mainTable;

    /** Descripción de Campos */

    private String m_mainAlias;

    /** Descripción de Campos */

    private String m_joinTable;

    /** Descripción de Campos */

    private String m_joinAlias;

    /** Descripción de Campos */

    private boolean m_left;

    /** Descripción de Campos */

    private String m_condition;

    /**
     * Descripción de Método
     *
     *
     * @param joinClause
     */

    private void evaluate( String joinClause ) {
        m_joinClause = joinClause;

        int indexEqual = joinClause.indexOf( '=' );

        m_left = indexEqual < joinClause.indexOf( "(+)" );    // converts to LEFT if true

        // get table alias of it

        if( m_left )                                                                                 // f.AD_Column_ID = c.AD_Column_ID(+)  => f / c
        {
            m_mainAlias = joinClause.substring( 0,Util.findIndexOf( joinClause,'.','=' )).trim();    // f

            int end = joinClause.indexOf( '.',indexEqual );

            if( end == -1 ) {                                                   // no alias
                end = joinClause.indexOf( '(',indexEqual );
            }

            m_joinAlias = joinClause.substring( indexEqual + 1,end ).trim();    // c
        } else                                                                  // f.AD_Column_ID(+) = c.AD_Column_ID  => c / f
        {
            int end = joinClause.indexOf( '.',indexEqual );

            if( end == -1 ) {                                                   // no alias
                end = joinClause.length();
            }

            m_mainAlias = joinClause.substring( indexEqual + 1,end ).trim();    // c
            m_joinAlias = joinClause.substring( 0,Util.findIndexOf( joinClause,'.','(' )).trim();    // f
        }

        m_condition = Util.replace( joinClause,"(+)","" ).trim();
    }    // evaluate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getJoinClause() {
        return m_joinClause;
    }    // getJoinClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMainAlias() {
        return m_mainAlias;
    }    // getMainAlias

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getJoinAlias() {
        return m_joinAlias;
    }    // getJoinAlias

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLeft() {
        return m_left;
    }    // isLeft

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCondition() {
        return m_condition;
    }    // getCondition

    /**
     * Descripción de Método
     *
     *
     * @param mainTable
     */

    public void setMainTable( String mainTable ) {
        if( (mainTable == null) || (mainTable.length() == 0) ) {
            return;
        }

        m_mainTable = mainTable;

        if( m_mainAlias.equals( mainTable )) {
            m_mainAlias = "";
        }
    }    // setMainTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMainTable() {
        return m_mainTable;
    }    // getMainTable

    /**
     * Descripción de Método
     *
     *
     * @param joinTable
     */

    public void setJoinTable( String joinTable ) {
        if( (joinTable == null) || (joinTable.length() == 0) ) {
            return;
        }

        m_joinTable = joinTable;

        if( m_joinAlias.equals( joinTable )) {
            m_joinAlias = "";
        }
    }    // setJoinTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getJoinTable() {
        return m_joinTable;
    }    // getJoinTable

    /**
     * Descripción de Método
     *
     *
     * @param first
     *
     * @return
     */

    public boolean isConditionOf( Join first ) {
        if( (m_mainTable == null                                     // did not find Table from "Alias"
                ) && ( first.getJoinTable().equals( m_joinTable )    // same join table
                || first.getMainAlias().equals( m_joinTable ))) {    // same main table
            return true;
        }

        return false;
    }    // isConditionOf

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Join[" );

        sb.append( m_joinClause ).append( " - Main=" ).append( m_mainTable ).append( "/" ).append( m_mainAlias ).append( ", Join=" ).append( m_joinTable ).append( "/" ).append( m_joinAlias ).append( ", Left=" ).append( m_left ).append( ", Condition=" ).append( m_condition ).append( "]" );

        return sb.toString();
    }    // toString
}    // Join



/*
 *  @(#)Join.java   25.03.06
 * 
 *  Fin del fichero Join.java
 *  
 *  Versión 2.2
 *
 */
