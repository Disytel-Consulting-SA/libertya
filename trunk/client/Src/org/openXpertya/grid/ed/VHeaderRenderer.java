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
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.compiere.swing.CButton;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VHeaderRenderer implements TableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param displayType
     */

    public VHeaderRenderer( int displayType ) {
        super();

        // Alignment

        if( DisplayType.isNumeric( displayType )) {
            m_button.setHorizontalAlignment( JLabel.RIGHT );
        } else if( displayType == DisplayType.YesNo ) {
            m_button.setHorizontalAlignment( JLabel.CENTER );
        } else {
            m_button.setHorizontalAlignment( JLabel.LEFT );
        }

        m_button.setMargin( new Insets( 0,0,0,0 ));
    }    // VHeaderRenderer

    // for 3D effect in Windows

    /** Descripción de Campos */

    private CButton m_button = new CButton();

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

        // Log.trace(this,10, "VHeaderRenderer.getTableCellRendererComponent", value==null ? "null" : value.toString());
        // indicator for invisible column

        if( value == null ) {
            m_button.setPreferredSize( new Dimension( 0,0 ));

            return m_button;
        }

        m_button.setText( value.toString());

        return m_button;
    }    // getTableCellRendererComponent
}    // VHeaderRenderer



/*
 *  @(#)VHeaderRenderer.java   02.07.07
 * 
 *  Fin del fichero VHeaderRenderer.java
 *  
 *  Versión 2.2
 *
 */
