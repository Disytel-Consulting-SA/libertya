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

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MField;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VCellRenderer extends DefaultTableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mField
     */

    public VCellRenderer( MField mField ) {
        this( mField.getDisplayType());
        m_columnName = mField.getColumnName();
        this.setName( m_columnName );
        m_lookup   = mField.getLookup();
        m_password = mField.isEncryptedField();
    }    // VCellRenderer

    /**
     * Constructor de la clase ...
     *
     *
     * @param displayType
     */

    public VCellRenderer( int displayType ) {
        super();
        m_displayType = displayType;

        // Number

        if( DisplayType.isNumeric( m_displayType )) {
            m_numberFormat = DisplayType.getNumberFormat( m_displayType );
            setHorizontalAlignment( JLabel.RIGHT );
        }

        // Date

        else if( DisplayType.isDate( m_displayType )) {
            m_dateFormat = DisplayType.getDateFormat( m_displayType );

            //

        } else if( m_displayType == DisplayType.YesNo ) {
            m_check = new JCheckBox();
            m_check.setMargin( new Insets( 0,0,0,0 ));
            m_check.setHorizontalAlignment( JLabel.CENTER );
            m_check.setOpaque( true );
        }
    }    // VCellRenderer

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private String m_columnName = null;

    /** Descripción de Campos */

    private Lookup m_lookup = null;

    /** Descripción de Campos */

    private boolean m_password = false;

    //

    /** Descripción de Campos */

    private SimpleDateFormat m_dateFormat = null;

    /** Descripción de Campos */

    private DecimalFormat m_numberFormat = null;

    /** Descripción de Campos */

    private JCheckBox m_check = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VCellRenderer.class );

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

        // log.fine( "VCellRenderer.getTableCellRendererComponent - " + (value == null ? "null" : value.toString()),
        // "Row=" + row + ", Col=" + col);

        Component c = null;

        if( m_displayType == DisplayType.YesNo ) {
            c = m_check;
        } else {
            c = super.getTableCellRendererComponent( table,value,isSelected,hasFocus,row,col );
            c.setFont( CompierePLAF.getFont_Field());
        }

        // Background & Foreground

        Color bg = CompierePLAF.getFieldBackground_Normal();
        Color fg = CompierePLAF.getTextColor_Normal();

        // Selected is white on blue in Windows

        if( isSelected &&!hasFocus ) {
            bg = table.getSelectionBackground();
            fg = table.getSelectionForeground();
        }

        // row not selected or field has focus

        else {

            // Foregroundw

            int cCode = 0;

            // Grid

            if( table instanceof org.openXpertya.grid.VTable ) {
                cCode = (( org.openXpertya.grid.VTable )table ).getColorCode( row );

                // MiniGrid

            } else if( table instanceof org.openXpertya.minigrid.MiniTable ) {
                cCode = (( org.openXpertya.minigrid.MiniTable )table ).getColorCode( row );
            }

            //

            if( cCode == 0 ) {
                ;
            } else if( cCode < 0 ) {
                fg = CompierePLAF.getTextColor_Issue();
            } else {
                fg = CompierePLAF.getTextColor_OK();
            }

            // Inactive Background

            if( !table.isCellEditable( row,col )) {
                bg = CompierePLAF.getFieldBackground_Inactive();
            }
        }

        // Set Color

        c.setBackground( bg );
        c.setForeground( fg );

        //
        // log.fine( "r=" + row + " c=" + col, // + " - " + c.getClass().getName(),
        // "sel=" + isSelected + ", focus=" + hasFocus + ", edit=" + table.isCellEditable(row, col));
        // Log.trace(7, "BG=" + (bg.equals(Color.white) ? "white" : bg.toString()), "FG=" + (fg.equals(Color.black) ? "black" : fg.toString()));

        // Format it

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
        String retValue = null;

        try {

            // Checkbox

            if( m_displayType == DisplayType.YesNo ) {
                if( value instanceof Boolean ) {
                    m_check.setSelected((( Boolean )value ).booleanValue());
                } else {
                    m_check.setSelected( "Y".equals( value ));
                }

                return;
            } else if( value == null ) {
                ;

                // Number

            } else if( DisplayType.isNumeric( m_displayType )) {
                retValue = m_numberFormat.format( value );

                // Date

            } else if( DisplayType.isDate( m_displayType )) {
                retValue = m_dateFormat.format( value );

                // Row ID

            } else if( m_displayType == DisplayType.RowID ) {
                retValue = "";

                // Lookup

            } else if( (m_lookup != null) && ( DisplayType.isLookup( m_displayType ) || (m_displayType == DisplayType.Location) || (m_displayType == DisplayType.Account) || (m_displayType == DisplayType.Locator) || (m_displayType == DisplayType.PAttribute) ) ) {
                retValue = m_lookup.getDisplay( value );

                // Button

            } else if( m_displayType == DisplayType.Button ) {
                if( "Record_ID".equals( m_columnName )) {
                    retValue = "#" + value + "#";
                } else {
                    retValue = null;
                }
            }

            // Password (fixed string)

            else if( m_password ) {
                retValue = "**********";

                // other (String ...)

            } else {
                super.setValue( value );

                return;
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VCellRenderer - setValue (" + value + ") " + value.getClass().getName(),e );
            retValue = value.toString();
        }

        super.setValue( retValue );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "VCellRenderer - DisplayType=" + m_displayType + " - " + m_lookup;
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_lookup != null ) {
            m_lookup.dispose();
        }

        m_lookup = null;
    }    // dispose
}    // VCellRenderer



/*
 *  @(#)VCellRenderer.java   02.07.07
 * 
 *  Fin del fichero VCellRenderer.java
 *  
 *  Versión 2.2
 *
 */
