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

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.compiere.plaf.CompierePLAF;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class CheckRenderer extends DefaultTableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     */

    public CheckRenderer() {
        super();
        m_check.setMargin( new Insets( 0,0,0,0 ));
        m_check.setHorizontalAlignment( JLabel.CENTER );
        m_check.setOpaque( true );
    }    // CheckRenderer

    /** Descripción de Campos */

    private JCheckBox m_check = new JCheckBox();

    /**
     * Descripción de Método
     *
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param col
     *
     * @return
     */

    public Component getTableCellRendererComponent( JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int col ) {

        // Background & Foreground

        Color bg = CompierePLAF.getFieldBackground_Normal();

        // Selected is white on blue in Windows

        if( isSelected &&!hasFocus ) {
            bg = table.getSelectionBackground();

            // row not selected or field has focus

        } else {

            // Inactive Background

            if( !table.isCellEditable( row,col )) {
                bg = CompierePLAF.getFieldBackground_Inactive();
            }
        }

        // Set Color

        m_check.setBackground( bg );

        // Value

        setValue( value );

        return m_check;
    }    // getTableCellRendererComponent

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        if( (value != null) && (( Boolean )value ).booleanValue()) {
            m_check.setSelected( true );
        } else {
            m_check.setSelected( false );
        }
    }    // setValue
}    // CheckRenderer



/*
 *  @(#)CheckRenderer.java   02.07.07
 * 
 *  Fin del fichero CheckRenderer.java
 *  
 *  Versión 2.2
 *
 */
