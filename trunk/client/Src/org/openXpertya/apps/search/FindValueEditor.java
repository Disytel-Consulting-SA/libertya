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
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VEditorFactory;
import org.openXpertya.grid.ed.VString;
import org.openXpertya.model.MField;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class FindValueEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Constructor de la clase ...
     *
     *
     * @param find
     * @param valueTo
     */

    public FindValueEditor( Find find,boolean valueTo ) {
        super();
        m_find          = find;
        m_valueToColumn = valueTo;
    }    // FindValueEditor

    /** Descripción de Campos */

    private Find m_find;

    /** Descripción de Campos */

    private boolean m_valueToColumn;

    /** Descripción de Campos */

    private boolean m_between = false;

    /** Descripción de Campos */

    private VEditor m_editor = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( FindValueEditor.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {
        if( m_editor == null ) {
            return null;
        }

        Object obj = m_editor.getValue();    // returns Integer, BidDecimal, String

        log.config( "Obj=" + obj );

        return obj;
    }    // getCellEditorValue

    /**
     * Descripción de Método
     *
     *
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param col
     *
     * @return
     */

    public Component getTableCellEditorComponent( JTable table,Object value,boolean isSelected,int row,int col ) {

        // log.config( "FindValueEditor.getTableCellEditorComponent", "r=" + row + ", c=" + col);
        // Between - enables valueToColumn

        m_between = false;

        Object betweenValue = table.getModel().getValueAt( row,Find.INDEX_OPERATOR );

        if( m_valueToColumn && (betweenValue != null) && betweenValue.equals( MQuery.OPERATORS[ MQuery.BETWEEN_INDEX ] )) {
            m_between = true;
        }

        boolean enabled = !m_valueToColumn || ( m_valueToColumn && m_between );

        log.config( "FindValueEditor.getTableCellEditorComponent - (" + value + ") - Enabled=" + enabled );

        String columnName = null;
        Object column     = table.getModel().getValueAt( row,Find.INDEX_COLUMNNAME );

        if( column != null ) {
            columnName = (( ValueNamePair )column ).getValue();
        }

        // Create Editor

        MField field = m_find.getTargetMField( columnName );

        // log.fine( "Field=" + field.toStringX());

        m_editor = VEditorFactory.getEditor( field,true );

        if( m_editor == null ) {
            m_editor = new VString();
        }

        m_editor.setValue( value );
        m_editor.setReadWrite( enabled );
        m_editor.setBorder( null );

        //

        return( Component )m_editor;
    }    // getTableCellEditorComponent

    /**
     * Descripción de Método
     *
     *
     * @param e
     *
     * @return
     */

    public boolean isCellEditable( EventObject e ) {

        // log.config( "FindValueEditor.isCellEditable");

        return true;
    }    // isCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param e
     *
     * @return
     */

    public boolean shouldSelectCell( EventObject e ) {
        boolean retValue = !m_valueToColumn || ( m_valueToColumn && m_between );

        // log.config( "FindValueEditor.shouldSelectCell - " + retValue);

        return retValue;
    }    // shouldSelectCell
}    // FindValueEditor



/*
 *  @(#)FindValueEditor.java   02.07.07
 * 
 *  Fin del fichero FindValueEditor.java
 *  
 *  Versión 2.2
 *
 */
