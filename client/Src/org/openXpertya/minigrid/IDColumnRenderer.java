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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class IDColumnRenderer extends DefaultTableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param multiSelection
     */

    public IDColumnRenderer( boolean multiSelection ) {
        super();
        m_multiSelection = multiSelection;

        // Multi => Check

        if( m_multiSelection ) {
            m_check = new JCheckBox();
            m_check.setMargin( new Insets( 0,0,0,0 ));
            m_check.setHorizontalAlignment( JLabel.CENTER );
        } else    // Single => Button
        {
            m_button = new JButton();
            m_button.setMargin( new Insets( 0,0,0,0 ));
            m_button.setSize( new Dimension( 5,5 ));
        }
    }             // IDColumnRenderer

    /** Descripción de Campos */

    private boolean m_multiSelection;

    /** Descripción de Campos */

    private JButton m_button;

    /* The Multi-Selection renderer */

    /** Descripción de Campos */

    private JCheckBox m_check;

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    protected void setValue( Object value ) {
        if( m_multiSelection ) {
            boolean sel = false;

            if( value == null ) {
                ;
            } else if( value instanceof IDColumn ) {
                sel = (( IDColumn )value ).isSelected();
            } else if( value instanceof Boolean ) {
                sel = (( Boolean )value ).booleanValue();
            } else {
                sel = value.toString().equals( "Y" );
            }

            //

            m_check.setSelected( sel );
        }
    }    // setValue

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
        setValue( value );

        if( m_multiSelection ) {
            return m_check;
        } else {
            return m_button;
        }
    }    // setTableCellRenderereComponent
}    // IDColumnRenderer



/*
 *  @(#)IDColumnRenderer.java   02.07.07
 * 
 *  Fin del fichero IDColumnRenderer.java
 *  
 *  Versión 2.2
 *
 */
