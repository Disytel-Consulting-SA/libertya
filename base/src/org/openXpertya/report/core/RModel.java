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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RModel implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param TableName
     */

    public RModel( String TableName ) {
        m_data = new RModelData( TableName );
    }    // RModel

    /** Descripción de Campos */

    public static final String TABLE_ALIAS = "zz";

    /** Descripción de Campos */

    public static final String FUNCTION_COUNT = "Count";

    /** Descripción de Campos */

    public static final String FUNCTION_SUM = "Sum";

    /** Descripción de Campos */

    private RModelData m_data = null;

    /** Descripción de Campos */

    private boolean m_editable = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( RModel.class );

    /**
     * Descripción de Método
     *
     *
     * @param col
     *
     * @return
     */

    protected RColumn getRColumn( int col ) {
        if( (col < 0) || (col > m_data.cols.size())) {
            throw new java.lang.IllegalArgumentException( "Column invalid" );
        }

        return( RColumn )m_data.cols.get( col );
    }    // getRColumn

    /**
     * Descripción de Método
     *
     *
     * @param rc
     */

    public void addColumn( RColumn rc ) {
        m_data.cols.add( rc );
    }    // addColumn

    /**
     * Descripción de Método
     *
     *
     * @param rc
     * @param index
     */

    public void addColumn( RColumn rc,int index ) {
        m_data.cols.add( index,rc );
    }    // addColumn

    /**
     * Descripción de Método
     *
     */

    public void addRow() {
        m_data.rows.add( new ArrayList());
        m_data.rowsMeta.add( null );
    }    // addRow

    /**
     * Descripción de Método
     *
     *
     * @param index
     */

    public void addRow( int index ) {
        m_data.rows.add( index,new ArrayList());
        m_data.rowsMeta.add( index,null );
    }    // addRow

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public void addRow( ArrayList l ) {
        m_data.rows.add( l );
        m_data.rowsMeta.add( null );
    }    // addRow

    /**
     * Descripción de Método
     *
     *
     * @param l
     * @param index
     */

    public void addRow( ArrayList l,int index ) {
        m_data.rows.add( index,l );
        m_data.rowsMeta.add( index,null );
    }    // addRow

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public int getColumnIndex( String columnName ) {
        if( (columnName == null) || (columnName.length() == 0) ) {
            return -1;
        }

        //

        for( int i = 0;i < m_data.cols.size();i++ ) {
            RColumn rc = ( RColumn )m_data.cols.get( i );

            // log.fine( "Column " + i + " " + rc.getColSQL() + " ? " + columnName);

            if( rc.getColSQL().startsWith( columnName )) {
                log.fine( "Column " + i + " " + rc.getColSQL() + " = " + columnName );

                return i;
            }
        }

        return -1;
    }    // getColumnIndex

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param whereClause
     * @param orderClause
     */

    public void query( Properties ctx,String whereClause,String orderClause ) {
        m_data.query( ctx,whereClause,orderClause );
    }    // query

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     */

    public void setGroup( String columnName ) {
        setGroup( getColumnIndex( columnName ));
    }    // setGroup

    /**
     * Descripción de Método
     *
     *
     * @param col
     */

    public void setGroup( int col ) {
        log.config( "RModel.setGroup col=" + col );

        if( (col < 0) || (col >= m_data.cols.size())) {
            return;
        }

        Integer ii = new Integer( col );

        if( !m_data.groups.contains( ii )) {
            m_data.groups.add( ii );
        }
    }    // setGroup

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public boolean isGroupRow( int row ) {
        return m_data.isGroupRow( row );
    }    // isGroupRow

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param function
     */

    public void setFunction( String columnName,String function ) {
        setFunction( getColumnIndex( columnName ),function );
    }    // setFunction

    /**
     * Descripción de Método
     *
     *
     * @param col
     * @param function
     */

    public void setFunction( int col,String function ) {
        log.config( "RModel.setFunction col=" + col + " - " + function );

        if( (col < 0) || (col >= m_data.cols.size())) {
            return;
        }

        m_data.functions.put( new Integer( col ),function );
    }    // setFunction

    // TableModel interface

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_data.rows.size();
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColumnCount() {
        return m_data.cols.size();
    }    // getColumnCount

    /**
     * Descripción de Método
     *
     *
     * @param col
     *
     * @return
     */

    public String getColumnName( int col ) {
        if( (col < 0) || (col > m_data.cols.size())) {
            throw new java.lang.IllegalArgumentException( "Column invalid" );
        }

        RColumn rc = ( RColumn )m_data.cols.get( col );

        if( rc != null ) {
            return rc.getColHeader();
        }

        return null;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @param col
     *
     * @return
     */

    public Class getColumnClass( int col ) {
        if( (col < 0) || (col > m_data.cols.size())) {
            throw new java.lang.IllegalArgumentException( "Column invalid" );
        }

        RColumn rc = ( RColumn )m_data.cols.get( col );

        if( rc != null ) {
            return rc.getColClass();
        }

        return null;
    }    // getColumnC;ass

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
        return m_editable;
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

        // invalid row

        if( (row < 0) || (row >= m_data.rows.size())) {
            return null;
        }

        // throw new java.lang.IllegalArgumentException("Row invalid");

        if( (col < 0) || (col >= m_data.cols.size())) {
            return null;
        }

        // throw new java.lang.IllegalArgumentException("Column invalid");
        //

        ArrayList myRow = ( ArrayList )m_data.rows.get( row );

        // invalid column

        if( (myRow == null) || (col >= myRow.size())) {
            return null;
        }

        // setValue

        return myRow.get( col );
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

        // invalid row

        if( (row < 0) || (row >= m_data.rows.size())) {
            throw new IllegalArgumentException( "Row invalid" );
        }

        if( (col < 0) || (col >= m_data.cols.size())) {
            throw new IllegalArgumentException( "Column invalid" );
        }

        if( !isCellEditable( row,col )) {
            throw new IllegalArgumentException( "Cell is read only" );
        }

        //

        ArrayList myRow = ( ArrayList )m_data.rows.get( row );

        // invalid row

        if( myRow == null ) {
            throw new java.lang.IllegalArgumentException( "Row not initialized" );
        }

        // not enough columns - add nulls

        if( col >= myRow.size()) {
            while( myRow.size() < m_data.cols.size()) {
                myRow.add( null );
            }
        }

        // setValue

        myRow.set( col,aValue );
    }    // setValueAt

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     */

    public void moveRow( int from,int to ) {
        m_data.moveRow( from,to );
    }    // moveRow
    
    public void reSort( HashMap m) {
    	m_data.reSort( m );
    }    // moveRow  

	/**
	 * Returns the ArrayList of ArrayLists that contains the table's data values.
	 * The ArrayLists contained in the outer vector are each a single row of values.
	 * @return the ArrayList of ArrayLists containing the tables data values
	 * @author Teo Sarca [ 1734327 ]
	 */
	public ArrayList<ArrayList<Object>> getRows() {
		return m_data.rows;
	}

    
}    // RModel



/*
 *  @(#)RModel.java   02.07.07
 * 
 *  Fin del fichero RModel.java
 *  
 *  Versión 2.2
 *
 */
