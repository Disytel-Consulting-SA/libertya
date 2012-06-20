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



package org.openXpertya.grid.ed;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VRowIDRenderer implements TableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param enableSelection
     */

    public VRowIDRenderer( boolean enableSelection ) {
        m_select = enableSelection;
    }    // VRowIDRenderer

    /** Descripción de Campos */

    private boolean m_select = false;

    /** Descripción de Campos */

    private JButton m_button = new JButton();

    /** Descripción de Campos */

    private JCheckBox m_check = null;

    /**
     * Descripción de Método
     *
     *
     * @param showSelection
     */

    public void setEnableSelection( boolean showSelection ) {
        m_select = showSelection;
    }    // setEnableSelection

    /**
     * Descripción de Método
     *
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     *
     * @return
     */

    public Component getTableCellRendererComponent( JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column ) {
        if( m_select ) {
            if( m_check == null ) {
                m_check = new JCheckBox();
            }

            Object[] data = ( Object[] )value;

            if( (data == null) || (data[ 1 ] == null) ) {
                m_check.setSelected( false );
            } else {
                Boolean sel = ( Boolean )data[ 1 ];

                m_check.setSelected( sel.booleanValue());
            }

            return m_check;
        } else {
            return m_button;
        }
    }    // getTableCellRenderereComponent
}    // VRowIDRenderer



/*
 *  @(#)VRowIDRenderer.java   02.07.07
 * 
 *  Fin del fichero VRowIDRenderer.java
 *  
 *  Versión 2.2
 *
 */
