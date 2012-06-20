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



package org.openXpertya.minigrid;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ColumnInfo {

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     */

    public ColumnInfo( String colHeader,String colSQL,Class colClass ) {
        this( colHeader,colSQL,colClass,true,false,null );
    }    // ColumnInfo

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     * @param keyPairColSQL
     */

    public ColumnInfo( String colHeader,String colSQL,Class colClass,String keyPairColSQL ) {
        this( colHeader,colSQL,colClass,true,false,keyPairColSQL );
    }    // ColumnInfo

    /**
     * Constructor de la clase ...
     *
     *
     * @param colHeader
     * @param colSQL
     * @param colClass
     * @param readOnly
     * @param colorColumn
     * @param keyPairColSQL
     */

    public ColumnInfo( String colHeader,String colSQL,Class colClass,boolean readOnly,boolean colorColumn,String keyPairColSQL ) {
        setColHeader( colHeader );
        setColSQL( colSQL );
        setColClass( colClass );
        setReadOnly( readOnly );
        setColorColumn( colorColumn );
        setKeyPairColSQL( keyPairColSQL );
    }    // ColumnInfo

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

    private String m_keyPairColSQL = "";

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
     * @param keyPairColSQL
     */

    public void setKeyPairColSQL( String keyPairColSQL ) {
        m_keyPairColSQL = keyPairColSQL;

        if( m_keyPairColSQL == null ) {
            m_keyPairColSQL = "";
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getKeyPairColSQL() {
        return m_keyPairColSQL;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isKeyPairCol() {
        return m_keyPairColSQL.length() > 0;
    }
}    // infoColumn



/*
 *  @(#)ColumnInfo.java   02.07.07
 * 
 *  Fin del fichero ColumnInfo.java
 *  
 *  Versión 2.2
 *
 */
