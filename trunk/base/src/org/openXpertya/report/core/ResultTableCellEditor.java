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
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ResultTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Constructor de la clase ...
     *
     */

    public ResultTableCellEditor() {}    // ResultTableCellEditor

    /**
     * Constructor de la clase ...
     *
     *
     * @param rc
     */

    public ResultTableCellEditor( RColumn rc ) {
        m_rc = rc;
    }    // ResultTableCellEditor

    /** Descripción de Campos */

    private RColumn m_rc = null;

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
        if( m_rc == null ) {
            return null;
        }

        return null;
    }    // getTableCellEditorComponent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {
        if( m_rc == null ) {
            return null;
        }

        return null;
    }    // getCellEditorValue

    /**
     * Descripción de Método
     *
     *
     * @param anEvent
     *
     * @return
     */

    public boolean isCellEditable( EventObject anEvent ) {
        if( m_rc == null ) {
            return false;
        }

        return !m_rc.isReadOnly();
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
        if( m_rc == null ) {
            return false;
        }

        return !m_rc.isReadOnly();
    }    // shouldSelectCell
}    // ResultTableCellEditor



/*
 *  @(#)ResultTableCellEditor.java   02.07.07
 * 
 *  Fin del fichero ResultTableCellEditor.java
 *  
 *  Versión 2.2
 *
 */
