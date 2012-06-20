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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import org.compiere.swing.CCheckBox;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class IDColumnEditor extends AbstractCellEditor implements TableCellEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public IDColumnEditor() {
        m_check.setMargin( new Insets( 0,0,0,0 ));
        m_check.setHorizontalAlignment( JLabel.CENTER );
        m_check.addActionListener( this );
    }    // IDColumnEditor

    /** Descripción de Campos */

    private JCheckBox m_check = new CCheckBox();

    /** Descripción de Campos */

    private IDColumn m_value = null;

    /** Descripción de Campos */

    private JTable m_table;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {

        // log.fine( "IDColumnEditor.getCellEditorValue - " + m_check.isSelected());

        if( m_value != null ) {
            m_value.setSelected( m_check.isSelected());
        }

        return m_value;
    }    // getCellEditorValue

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

        // log.fine( "IDColumnEditor.getTableCellEditorComponent", value);

        m_table = table;

        // set value

        if( (value != null) && (value instanceof IDColumn) ) {
            m_value = ( IDColumn )value;
        } else {
            m_value = null;

            throw new IllegalArgumentException( "ICColumnEditor.getTableCellEditorComponent - value=" + value );
        }

        // set editor value

        m_check.setSelected( m_value.isSelected());

        return m_check;
    }    // getTableCellEditorComponent

    /**
     * Descripción de Método
     *
     *
     * @param anEvent
     *
     * @return
     */

    public boolean isCellEditable( EventObject anEvent ) {
        return true;
    }    // isCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param anEvent
     *
     * @return
     */

    public boolean shouldSelectCell( EventObject anEvent ) {
        return true;
    }    // shouldSelectCell

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_table != null ) {
            m_table.editingStopped( new ChangeEvent( this ));
        }
    }    // actionPerformed
}    // IDColumnEditor



/*
 *  @(#)IDColumnEditor.java   02.07.07
 * 
 *  Fin del fichero IDColumnEditor.java
 *  
 *  Versión 2.2
 *
 */
