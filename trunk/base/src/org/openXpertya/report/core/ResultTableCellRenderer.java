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



package org.openXpertya.report.core;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class ResultTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param rm
     * @param rc
     */

    public ResultTableCellRenderer( RModel rm,RColumn rc ) {
        m_rm = rm;
        m_rc = rc;

        int dt = m_rc.getDisplayType();

        // Numbers

        if( DisplayType.isNumeric( dt )) {
            super.setHorizontalAlignment( JLabel.TRAILING );
            m_nFormat = DisplayType.getNumberFormat( dt );
        }

        // Dates

        else if( DisplayType.isDate( m_rc.getDisplayType())) {
            super.setHorizontalAlignment( JLabel.TRAILING );
            m_dFormat = DisplayType.getDateFormat( dt );
        }

        //

        else if( dt == DisplayType.YesNo ) {
            m_check = new JCheckBox();
            m_check.setMargin( new Insets( 0,0,0,0 ));
            m_check.setHorizontalAlignment( JLabel.CENTER );
        }
    }    // ResultTableCellRenderer

    /** Descripción de Campos */

    private RModel m_rm = null;

    /** Descripción de Campos */

    private RColumn m_rc = null;

    /** Descripción de Campos */

    private DecimalFormat m_nFormat = null;

    /** Descripción de Campos */

    private SimpleDateFormat m_dFormat = null;

    /** Descripción de Campos */

    private JCheckBox m_check;

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

        // Get Component

        Component c = m_check;

        if( c == null ) {    // default JLabel
            c = super.getTableCellRendererComponent( table,value,isSelected,hasFocus,row,col );
        }

        // Background

        if( m_rm.isCellEditable( row,col )) {
            c.setBackground( CompierePLAF.getFieldBackground_Normal());
        } else {
            c.setBackground( CompierePLAF.getFieldBackground_Inactive());
        }

        //

        if( m_rm.isGroupRow( row )) {
            c.setFont( c.getFont().deriveFont( Font.BOLD ));
        }

        // Value

        setValue( value );

        return c;
    }    // getTableCellRendererComponent

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    protected void setValue( Object value ) {

        // Boolean

        if( m_check != null ) {
            boolean sel = false;

            if( (value != null) && (( Boolean )value ).booleanValue()) {
                sel = true;
            }

            m_check.setSelected( sel );

            return;
        }

        // JLabel

        if( value == null ) {
            setText( "" );
        } else if( m_nFormat != null ) {
            try {
                setText( m_nFormat.format( value ));
            } catch( Exception e ) {
                setText( value.toString());
            }
        } else if( m_dFormat != null ) {
            try {
                setText( m_dFormat.format( value ));
            } catch( Exception e ) {
                setText( value.toString());
            }
        } else {
            setText( value.toString());
        }
    }    // setValue
}    // ResultTableCellRenderer



/*
 *  @(#)ResultTableCellRenderer.java   02.07.07
 * 
 *  Fin del fichero ResultTableCellRenderer.java
 *  
 *  Versión 2.2
 *
 */
