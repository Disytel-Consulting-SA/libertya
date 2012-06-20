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

import java.awt.Component;
import java.awt.Insets;
import java.sql.Date;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MField;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class FindValueRenderer extends DefaultTableCellRenderer {

    /**
     * Constructor de la clase ...
     *
     *
     * @param find
     * @param valueTo
     */

    public FindValueRenderer( Find find,boolean valueTo ) {
        super();
        m_find          = find;
        m_valueToColumn = valueTo;
    }    // FindValueRenderer

    /** Descripción de Campos */

    private Find m_find;

    /** Descripción de Campos */

    private boolean m_valueToColumn;

    /** Descripción de Campos */

    private boolean m_between = false;

    /** Descripción de Campos */

    private volatile String m_columnName = null;

    /** Descripción de Campos */

    private JCheckBox m_check = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( FindValueRenderer.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private JCheckBox getCheck() {
        if( m_check == null ) {
            m_check = new JCheckBox();
            m_check.setMargin( new Insets( 0,0,0,0 ));
            m_check.setHorizontalAlignment( JLabel.CENTER );
        }

        return m_check;
    }    // getCheck

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

        // log.config( "FindValueRenderer.getTableCellRendererComponent", "r=" + row + ", c=" + col );
        // Column

        m_columnName = null;

        Object column = table.getModel().getValueAt( row,Find.INDEX_COLUMNNAME );

        if( column != null ) {
            m_columnName = (( ValueNamePair )column ).getValue();
        }

        // Between - enables valueToColumn

        m_between = false;

        Object betweenValue = table.getModel().getValueAt( row,Find.INDEX_OPERATOR );

        if( m_valueToColumn && (betweenValue != null) && betweenValue.equals( MQuery.OPERATORS[ MQuery.BETWEEN_INDEX ] )) {
            m_between = true;
        }

        boolean enabled = !m_valueToColumn || ( m_valueToColumn && m_between );

        // set Background

        if( enabled ) {
            setBackground( CompierePLAF.getFieldBackground_Normal());
        } else {
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        }

        // log.config( "FindValueRenderer.getTableCellRendererComponent - (" + value + ") - Enabled=" + enabled);

        Component c = super.getTableCellRendererComponent( table,value,isSelected,hasFocus,row,col );

        if( (value == null) || ( m_valueToColumn &&!m_between )) {
            return c;
        }

        //

        MField field = getMField();

        if( (field != null) && (field.getDisplayType() == DisplayType.YesNo) ) {
            JCheckBox cb = getCheck();

            if( value instanceof Boolean ) {
                cb.setSelected((( Boolean )value ).booleanValue());
            } else {
                cb.setSelected( value.toString().indexOf( "Y" ) != -1 );
            }

            return cb;
        }

        return c;
    }    // getTableCellRendererComponent

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    protected void setValue( Object value ) {
        boolean enabled = !m_valueToColumn || ( m_valueToColumn && m_between );

        // Log.trace (Log.l4_Data, "FindValueRenderer.setValue (" + value + ") - Enabled=" + enabled);

        if( (value == null) ||!enabled ) {
            super.setValue( null );

            return;
        }

        String retValue = null;

        // Strip ' '

        if( value != null ) {
            String str = value.toString();

            if( str.startsWith( "'" ) && str.endsWith( "'" )) {
                str   = str.substring( 1,str.length() - 1 );
                value = str;
            }
        }

        int    displayType = 0;
        MField field       = getMField();

        if( field != null ) {
            displayType = field.getDisplayType();
        } else {
            log.log( Level.SEVERE,"FindValueRenderer.setValue (" + value + ") ColumnName=" + m_columnName + " No Target Column" );
        }

        setHorizontalAlignment( JLabel.LEFT );

        // Number

        if( DisplayType.isNumeric( displayType )) {
            setHorizontalAlignment( JLabel.RIGHT );
            
            if(value.equals(""))
            	retValue = (String)value;
            else
            	retValue = DisplayType.getNumberFormat( displayType ).format( value );
            
            	
        }

        // Date

        else if( DisplayType.isDate( displayType )) {
            if( value instanceof Date ) {
                retValue = DisplayType.getDateFormat( displayType ).format( value );
                setHorizontalAlignment( JLabel.RIGHT );
            } else if( value instanceof String )    // JDBC format
            {
                try {
                    java.util.Date date = DisplayType.getDateFormat_JDBC().parse(( String )value );

                    retValue = DisplayType.getDateFormat( displayType ).format( date );
                    setHorizontalAlignment( JLabel.RIGHT );
                } catch( Exception e ) {

                    // log.log(Level.SEVERE, "FindValueRenderer.setValue", e);

                    retValue = value.toString();
                }
            } else {
                retValue = value.toString();
            }
        }

        // Row ID

        else if( displayType == DisplayType.RowID ) {
            retValue = "";

            // Lookup

        } else if( DisplayType.isLookup( displayType ) && (field != null) ) {
            Lookup lookup = field.getLookup();

            if( lookup != null ) {
                retValue = lookup.getDisplay( value );
            }
        }

        // other

        else {
            super.setValue( value );

            return;
        }

        // log.config( "FindValueRenderer.setValue (" + retValue + ") - DT=" + displayType);

        super.setValue( retValue );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MField getMField() {
        return m_find.getTargetMField( m_columnName );
    }    // getMField
}    // FindValueRenderer



/*
 *  @(#)FindValueRenderer.java   02.07.07
 * 
 *  Fin del fichero FindValueRenderer.java
 *  
 *  Versión 2.2
 *
 */
