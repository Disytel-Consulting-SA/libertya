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

import java.util.HashMap;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class ResultTableModel implements TableModel {

    /**
     * Constructor de la clase ...
     *
     *
     * @param reportModel
     */

    public ResultTableModel( RModel reportModel ) {
        m_model = reportModel;
    }    // ResultTableModel

    /** Descripción de Campos */

    private RModel m_model;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_model.getRowCount();
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColumnCount() {
        return m_model.getColumnCount();
    }    // getColumnCount

    /**
     * Descripción de Método
     *
     *
     * @param columnIndex
     *
     * @return
     */

    public String getColumnName( int columnIndex ) {
        return m_model.getColumnName( columnIndex );
    }    // getColumnIndex

    /**
     * Descripción de Método
     *
     *
     * @param columnIndex
     *
     * @return
     */

    public Class getColumnClass( int columnIndex ) {
        return m_model.getColumnClass( columnIndex );
    }    // getColumnClass

    /**
     * Descripción de Método
     *
     *
     * @param rowIndex
     * @param columnIndex
     *
     * @return
     */

    public boolean isCellEditable( int rowIndex,int columnIndex ) {
        return false;
    }    // isCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param col
     *
     * @return
     */

    public Object getValueAt( int row,int col ) {
        return m_model.getValueAt( row,col );
    }    // getValueAt

    /**
     * Descripción de Método
     *
     *
     * @param aValue
     * @param row
     * @param col
     */

    public void setValueAt( Object aValue,int row,int col ) {
        m_model.setValueAt( aValue,row,col );
        fireTableChanged( new TableModelEvent( this,row,row,col,TableModelEvent.UPDATE ));
    }    // setValueAt

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     */

    public void moveRow( int from,int to ) {
        m_model.moveRow( from,to );
    }    // moveRow

    public void reSort(HashMap m) {
    	m_model.reSort(m);
    }

    	
    
    
    /** Descripción de Campos */

    transient private Vector tableModelListeners;

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removeTableModelListener( TableModelListener l ) {
        if( (tableModelListeners != null) && tableModelListeners.contains( l )) {
            Vector v = ( Vector )tableModelListeners.clone();

            v.removeElement( l );
            tableModelListeners = v;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addTableModelListener( TableModelListener l ) {
        Vector v = (tableModelListeners == null)
                   ?new Vector( 2 )
                   :( Vector )tableModelListeners.clone();

        if( !v.contains( l )) {
            v.addElement( l );
            tableModelListeners = v;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void fireTableChanged( TableModelEvent e ) {
        if( tableModelListeners != null ) {
            Vector listeners = tableModelListeners;
            int    count     = listeners.size();

            for( int i = 0;i < count;i++ ) {
                (( TableModelListener )listeners.elementAt( i )).tableChanged( e );
            }
        }
    }
}    // ResultTableModel



/*
 *  @(#)ResultTableModel.java   02.07.07
 * 
 *  Fin del fichero ResultTableModel.java
 *  
 *  Versión 2.2
 *
 */
