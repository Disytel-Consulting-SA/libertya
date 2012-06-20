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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.MSort;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ResultTable extends JTable implements MouseListener {

    /**
     * Constructor de la clase ...
     *
     */

    public ResultTable() {
        super();
        setCellSelectionEnabled( false );
        setColumnSelectionAllowed( false );
        setRowSelectionAllowed( false );
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        // Default Editor

        ResultTableCellEditor rtce = new ResultTableCellEditor();

        setCellEditor( rtce );

        // Mouse Listener

        addMouseListener( this );
        getTableHeader().addMouseListener( this );
    }    // ResultTable

    /** Descripción de Campos */

    private int m_lastSortIndex = -1;

    /** Descripción de Campos */

    private boolean m_asc = true;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ResultTable.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param reportModel
     */

    public ResultTable( RModel reportModel ) {
        this();
        setModel( reportModel );
    }    // ResultTable

    /**
     * Descripción de Método
     *
     *
     * @param reportModel
     */

    public void setModel( RModel reportModel ) {
        log.config( "ResultTable.setModel" );
        super.setModel( new ResultTableModel( reportModel ));

        //

        TableColumnModel tcm = getColumnModel();

        // Set Editor/Renderer

        for( int i = 0;i < tcm.getColumnCount();i++ ) {
            TableColumn tc = tcm.getColumn( i );
            RColumn     rc = reportModel.getRColumn( i );

            if( rc.getColHeader().equals( tc.getHeaderValue())) {
                ResultTableCellRenderer rtcr = new ResultTableCellRenderer( reportModel,rc );

                tc.setCellRenderer( rtcr );

                //

            } else {
                log.log( Level.SEVERE,"RColumn=" + rc.getColHeader() + " <> TableColumn=" + tc.getHeaderValue());
            }
        }

        autoSize();
    }    // setModel

    /**
     * Descripción de Método
     *
     *
     * @param ignored
     */

    public void setModel( TableModel ignored ) {

        // throw new IllegalArgumentException("Requires RModel");  //  default construvtor calls this

        super.setModel( ignored );
    }    // setModel

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        super.tableChanged( e );
        log.fine( "ResultTable.tableChanged - Type=" + e.getType());
    }    // tableChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        int col = getColumnModel().getColumnIndexAtX( e.getX());

        log.info( "Column " + col + " = " + getColumnModel().getColumn( col ).getHeaderValue() + ", Table r=" + this.getSelectedRow() + " c=" + this.getSelectedColumn());

        // clicked Cell

        if( e.getSource() == this ) {}

        // clicked Header

        else {
            int mc = convertColumnIndexToModel( col );

            sort( mc );
        }
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}
    
    /**
     * Descripción de Método
     * Realizado por Mauricio Calgaro
     * Fecha: 13-12-2010
     */
    
    public String getClipboardData() {
    	log.config( "ResultTable.getClipboardData" );

    	TableColumnModel tcm = getColumnModel();



    	StringBuffer resultado= new StringBuffer("");

    	for( int col = 0;col < tcm.getColumnCount();col++ ) {
    		resultado.append(getColumnModel().getColumn( col ).getHeaderValue());
    		resultado.append('\t');
    	}
    	resultado.append('\n');


    	for( int row = 0;row < getRowCount() ;row++ ) {
    		for( int col = 0;col < tcm.getColumnCount();col++ ) {
    			Object unDato= this.dataModel.getValueAt(row, col);
    			if (unDato==null) {
    				unDato="";
    			}
    			if (col!=0) resultado.append('\t');
    			resultado=resultado.append(unDato);

    		}
    		resultado.append('\n');

    	}   
    	return resultado.toString();
    }        

    /**
     * Descripción de Método
     *
     */

    private void autoSize() {
        log.config( "ResultTable.autoSize" );

        //

        final int SLACK   = 8;      // making sure it fits in a column
        final int MAXSIZE = 300;    // max size of a column

        //

        TableColumnModel tcm = getColumnModel();

        // For all columns

        for( int col = 0;col < tcm.getColumnCount();col++ ) {
            TableColumn tc = tcm.getColumn( col );

            // log.config( "Column=" + col, tc.getHeaderValue());

            int width = 0;

            // Header

            TableCellRenderer renderer = tc.getHeaderRenderer();

            if( renderer == null ) {
                renderer = new DefaultTableCellRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent( this,tc.getHeaderValue(),false,false,0,0 );

            // log.fine( "Hdr - preferred=" + comp.getPreferredSize().width + ", width=" + comp.getWidth());

            width = comp.getPreferredSize().width + SLACK;

            // Cells

            int maxRow = Math.min( 30,getRowCount());    // first 30 rows

            for( int row = 0;row < maxRow;row++ ) {
                renderer = getCellRenderer( row,col );
                comp     = renderer.getTableCellRendererComponent( this,getValueAt( row,col ),false,false,row,col );

                int rowWidth = comp.getPreferredSize().width + SLACK;

                width = Math.max( width,rowWidth );
            }

            // Width not greater ..

            width = Math.min( MAXSIZE,width );
            tc.setPreferredWidth( width );

            // log.fine( "width=" + width);

        }    // for all columns
    }        // autoSize

    /**
     * Descripción de Método
     *
     *
     * @param modelColumnIndex
     */

    private void sort( int modelColumnIndex ) {
        int rows = getRowCount();

        if( rows == 0 ) {
            return;
        }

        // other column

        if( modelColumnIndex != m_lastSortIndex ) {
            m_asc = true;
        } else {
            m_asc = !m_asc;
        }

        m_lastSortIndex = modelColumnIndex;

        //

        log.config( "#" + modelColumnIndex + " - rows=" + rows + ", asc=" + m_asc );

        ResultTableModel model = ( ResultTableModel )getModel();

        // Prepare sorting

        MSort sort = new MSort( 0,null );

        sort.setSortAsc( m_asc );

        // Create sortList

        ArrayList sortList = new ArrayList( rows );

        // fill with data entity

        for( int i = 0;i < rows;i++ ) {
            Object value = model.getValueAt( i,modelColumnIndex );

            sortList.add( new MSort( i,value ));
        }

        // sort list it

        Collections.sort( sortList,sort );

        // ordenar en el modelo 

        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        for( int i = 0;i < rows;i++ ) {
            int index = (( MSort )sortList.get( i )).index;
            	m.put(i, index);
        }
        
        model.reSort(m);
    }
    
}    // ResultTable



/*
 *  @(#)ResultTable.java   02.07.07
 * 
 *  Fin del fichero ResultTable.java
 *  
 *  Versión 2.2
 *
 */