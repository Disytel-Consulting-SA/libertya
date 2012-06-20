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
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.grid.ed.VString;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MiniCellEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Constructor de la clase ...
     *
     *
     * @param c
     */

    public MiniCellEditor( Class c ) {
        super();

        // Date

        if( c == Timestamp.class ) {
            m_editor = new VDate();
        } else if( c == BigDecimal.class ) {
            m_editor = new VNumber( "Amount",false,false,true,DisplayType.Amount,"Amount" );
        } else if( c == Double.class ) {
            m_editor = new VNumber( "Number",false,false,true,DisplayType.Number,"Number" );
        } else if( c == Integer.class ) {
            m_editor = new VNumber( "Integer",false,false,true,DisplayType.Integer,"Integer" );
        } else {
            m_editor = new VString();
        }
    }    // MiniCellEditor

    /** Descripción de Campos */

    private VEditor m_editor = null;

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

        // ADebug.trace(ADebug.l5_DData, "VCellEditor.getTableCellEditorComponent - " + value == null ? "null" : value.toString());

        // Set Value

        m_editor.setValue( value );

        // Set UI

        m_editor.setBorder( null );

        // m_editor.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));

        m_editor.setFont( table.getFont());

        return( Component )m_editor;
    }    // getTableCellEditorComponent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {

        // ADebug.trace(ADebug.l5_DData, "VCellEditor.getCellEditorValue - ");

        if( m_editor != null ) {
            return m_editor.getValue();
        }

        return null;
    }    // getCellEditorValue
}    // MiniCellEditor



/*
 *  @(#)MiniCellEditor.java   02.07.07
 * 
 *  Fin del fichero MiniCellEditor.java
 *  
 *  Versión 2.2
 *
 */
