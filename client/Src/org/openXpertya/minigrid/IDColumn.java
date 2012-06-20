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

public class IDColumn {

    /**
     * Constructor de la clase ...
     *
     *
     * @param record_ID
     */

    public IDColumn( int record_ID ) {
        this( new Integer( record_ID ));
    }    // IDColumn

    /**
     * Constructor de la clase ...
     *
     *
     * @param record_ID
     */

    public IDColumn( Integer record_ID ) {
        super();
        setRecord_ID( record_ID );
        setSelected( true );
    }    // IDColumn

    /** Descripción de Campos */

    private boolean m_selected = false;

    /** Descripción de Campos */

    private Integer m_record_ID;

    /**
     * Descripción de Método
     *
     *
     * @param selected
     */

    public void setSelected( boolean selected ) {
        m_selected = selected;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSelected() {
        return m_selected;
    }

    /**
     * Descripción de Método
     *
     *
     * @param record_ID
     */

    public void setRecord_ID( Integer record_ID ) {
        m_record_ID = record_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Integer getRecord_ID() {
        return m_record_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "IDColumn - ID=" + m_record_ID + ", Selected=" + m_selected;
    }    // toString
}    // IDColumn



/*
 *  @(#)IDColumn.java   02.07.07
 * 
 *  Fin del fichero IDColumn.java
 *  
 *  Versión 2.2
 *
 */
