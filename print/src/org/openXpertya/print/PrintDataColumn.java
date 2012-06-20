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



package org.openXpertya.print;

//import java.util.logging.*;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintDataColumn {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Column_ID
     * @param columnName
     * @param displayType
     * @param columnSize
     * @param alias
     * @param isPageBreak
     */

    public PrintDataColumn( int AD_Column_ID,String columnName,int displayType,int columnSize,String alias,boolean isPageBreak ) {
        m_AD_Column_ID = AD_Column_ID;
        m_columnName   = columnName;

        //

        m_displayType = displayType;
        m_columnSize  = columnSize;

        //

        m_alias = alias;

        if( m_alias == null ) {
            m_alias = columnName;
        }

        m_pageBreak = isPageBreak;
    }    // PrintDataColumn

    /** Descripción de Campos */

    private int m_AD_Column_ID;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private int m_columnSize;

    /** Descripción de Campos */

    private String m_alias;

    /** Descripción de Campos */

    private boolean m_pageBreak;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Column_ID() {
        return m_AD_Column_ID;
    }    // getAD_Column_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        return m_columnName;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDisplayType() {
        return m_displayType;
    }    // getDisplayType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAlias() {
        return m_alias;
    }    // getAlias

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean hasAlias() {
        return !m_columnName.equals( m_alias );
    }    // hasAlias

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPageBreak() {
        return m_pageBreak;
    }    // isPageBreak

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "PrintDataColumn[" );

        sb.append( "ID=" ).append( m_AD_Column_ID ).append( "-" ).append( m_columnName );

        if( hasAlias()) {
            sb.append( "(" ).append( m_alias ).append( ")" );
        }

        sb.append( ",DisplayType=" ).append( m_displayType ).append( ",Size=" ).append( m_columnSize ).append( "]" );

        return sb.toString();
    }    // toString
}    // PrintDataColumn



/*
 *  @(#)PrintDataColumn.java   23.03.06
 * 
 *  Fin del fichero PrintDataColumn.java
 *  
 *  Versión 2.2
 *
 */
