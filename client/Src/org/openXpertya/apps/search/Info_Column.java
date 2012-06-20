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



package org.openXpertya.apps.search;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Info_Column {

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     */

    public Info_Column( String colHeader,String colSQL,Class colClass ) {
        this( colHeader,colSQL,colClass,true,false,null );
    }    // Info_Column

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     * @param IDcolSQL
     */

    public Info_Column( String colHeader,String colSQL,Class colClass,String IDcolSQL ) {
        this( colHeader,colSQL,colClass,true,false,IDcolSQL );
    }    // Info_Column

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     * @param readOnly
     * @param colorColumn
     * @param IDcolSQL
     */

    public Info_Column( String colHeader,String colSQL,Class colClass,boolean readOnly,boolean colorColumn,String IDcolSQL ) {
        setColHeader( colHeader );
        setColSQL( colSQL );
        setColClass( colClass );
        setReadOnly( readOnly );
        setColorColumn( colorColumn );
        setIDcolSQL( IDcolSQL );
    }    // Info_Column

    /** Descripción de Campos */

    private String m_colHeader;

    /** Descripción de Campos */

    private String m_colSQL;

    /** Descripción de Campos */

    private Class m_colClass;

    /** Descripción de Campos */

    private boolean m_readOnly;

    /** Descripción de Campos */

    private boolean m_colorColumn;

    /** Descripción de Campos */

    private String m_IDcolSQL = "";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Class getColClass() {
        return m_colClass;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColHeader() {
        return m_colHeader;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColSQL() {
        return m_colSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadOnly() {
        return m_readOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colClass
     */

    public void setColClass( Class colClass ) {
        m_colClass = colClass;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colHeader
     */

    public void setColHeader( String colHeader ) {
        m_colHeader = colHeader;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colSQL
     */

    public void setColSQL( String colSQL ) {
        m_colSQL = colSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @param readOnly
     */

    public void setReadOnly( boolean readOnly ) {
        m_readOnly = readOnly;
    }

    /**
     * Descripción de Método
     *
     *
     * @param colorColumn
     */

    public void setColorColumn( boolean colorColumn ) {
        m_colorColumn = colorColumn;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isColorColumn() {
        return m_colorColumn;
    }

    /**
     * Descripción de Método
     *
     *
     * @param IDcolSQL
     */

    public void setIDcolSQL( String IDcolSQL ) {
        m_IDcolSQL = IDcolSQL;

        if( m_IDcolSQL == null ) {
            m_IDcolSQL = "";
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getIDcolSQL() {
        return m_IDcolSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isIDcol() {
        return m_IDcolSQL.length() > 0;
    }
}    // infoColumn



/*
 *  @(#)Info_Column.java   02.07.07
 * 
 *  Fin del fichero Info_Column.java
 *  
 *  Versión 2.2
 *
 */
