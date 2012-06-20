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
import java.awt.Insets;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VRowIDEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Constructor de la clase ...
     *
     *
     * @param select
     */

    public VRowIDEditor( boolean select ) {
        super();
        m_select = select;
        m_cb.setMargin( new Insets( 0,0,0,0 ));
        m_cb.setHorizontalAlignment( JLabel.CENTER );
    }    // VRowIDEditor

    /** Descripción de Campos */

    private JCheckBox m_cb = new JCheckBox();

    /** Descripción de Campos */

    private Object[] m_rid;

    /** Descripción de Campos */

    private boolean m_select;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VRowIDEditor.class );

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
     * @param anEvent
     *
     * @return
     */

    public boolean isCellEditable( EventObject anEvent ) {
        return m_select;
    }    // isCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param column
     *
     * @return
     */

    public Component getTableCellEditorComponent( JTable table,Object value,boolean isSelected,int row,int column ) {
        m_rid = ( Object[] )value;

        if( (m_rid == null) || (m_rid[ 1 ] == null) ) {
            m_cb.setSelected( false );
        } else {
            Boolean sel = ( Boolean )m_rid[ 1 ];

            m_cb.setSelected( sel.booleanValue());
        }

        return m_cb;
    }    // getTableCellEditorComponent

    /**
     * Descripción de Método
     *
     *
     * @param anEvent
     *
     * @return
     */

    public boolean shouldSelectCell( EventObject anEvent ) {
        return m_select;
    }    // shouldSelectCell

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {
        log.fine( "VRowIDEditor.getCellEditorValue - " + m_cb.isSelected());

        if( m_rid == null ) {
            return null;
        }

        m_rid[ 1 ] = new Boolean( m_cb.isSelected());

        return m_rid;
    }    // getCellEditorValue
}    // VRowIDEditor



/*
 *  @(#)VRowIDEditor.java   02.07.07
 * 
 *  Fin del fichero VRowIDEditor.java
 *  
 *  Versión 2.2
 *
 */
