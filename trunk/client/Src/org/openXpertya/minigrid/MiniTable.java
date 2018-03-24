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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.*;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.compiere.swing.CCheckBox;
import org.compiere.swing.CTable;
import org.openXpertya.grid.ed.VCellRenderer;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MiniTable extends CTable {

    /**
     * Constructor de la clase ...
     *
     */

    public MiniTable() {
        super();

        // log.config( "MiniTable");

        setCellSelectionEnabled( false );
        setRowSelectionAllowed( false );

        // Default Editor

        this.setCellEditor( new ROCellEditor());
    }    // MiniTable

    /** Descripción de Campos */

    private ArrayList m_readWriteColumn = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_minWidth = new ArrayList();

    /** Descripción de Campos */

    private int m_colorColumnIndex = -1;

    /** Descripción de Campos */

    private Object m_colorDataCompare = Env.ZERO;

    /** Descripción de Campos */

    private boolean m_multiSelection = false;

    /** Descripción de Campos */

    private ColumnInfo[] m_layout = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MiniTable.class );

    /**
     * Descripción de Método
     *
     */

    public void autoSize() {
        log.finest( "" );

        //

        final int SLACK   = 8;      // making sure it fits in a column
        final int MAXSIZE = 300;    // max size of a column

        //

        TableModel model = this.getModel();
        int        size  = model.getColumnCount();

        // for all columns

        for( int col = 0;col < size;col++ ) {

            // Column & minimum width

            TableColumn tc    = this.getColumnModel().getColumn( col );
            int         width = 0;

            if( m_minWidth.size() > col ) {
                width = (( Integer )m_minWidth.get( col )).intValue();
            }

            // log.config( "Column=" + col + " " + column.getHeaderValue());

            // Header

            TableCellRenderer renderer = tc.getHeaderRenderer();

            if( renderer == null ) {
                renderer = new DefaultTableCellRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent( this,tc.getHeaderValue(),false,false,0,0 );

            // log.fine( "Hdr - preferred=" + comp.getPreferredSize().width + ", width=" + comp.getWidth());

            width = Math.max( width,comp.getPreferredSize().width + SLACK );

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
     * @param row
     * @param column
     *
     * @return
     */

    public boolean isCellEditable( int row,int column ) {

        // if the first column is a boolean and it is false, it is not editable

        if( (column != 0) && (getValueAt( row,0 ) instanceof Boolean) &&!(( Boolean )getValueAt( row,0 )).booleanValue()) {
            return false;
        }

        // is the column RW?

        if( m_readWriteColumn.contains( new Integer( column ))) {
            return true;
        }

        return false;
    }    // isCellEditable

    /**
     * Descripción de Método
     *
     *
     * @param column
     * @param readOnly
     */

    public void setColumnReadOnly( int column,boolean readOnly ) {

        // Column is ReadWrite

        if( m_readWriteColumn.contains( new Integer( column ))) {

            // Remove from list

            if( readOnly ) {
                int size = m_readWriteColumn.size();

                for( int i = 0;i < size;i++ ) {
                    if((( Integer )m_readWriteColumn.get( i )).intValue() == column ) {
                        m_readWriteColumn.remove( i );

                        break;
                    }
                }
            }    // ReadOnly
        }

        // current column is R/O - ReadWrite - add to list

        else if( !readOnly ) {
            m_readWriteColumn.add( new Integer( column ));
        }
    }            // setColumnReadOnly

    /**
     * Descripción de Método
     *
     *
     * @param layout
     * @param from
     * @param where
     * @param multiSelection
     * @param tableName
     *
     * @return
     */

    public String prepareTable( ColumnInfo[] layout,String from,String where,boolean multiSelection,String tableName ) {
    	return prepareTable(layout, from, where, multiSelection, tableName, false);
    }
    
    public String prepareTable( ColumnInfo[] layout,String from,String where,boolean multiSelection,String tableName, boolean distinct ) {
        m_layout         = layout;
        m_multiSelection = multiSelection;

        //

        StringBuffer sql = new StringBuffer( "SELECT " );

        if (distinct)
        	sql.append(" DISTINCT ");
        
        // add columns & sql

        for( int i = 0;i < layout.length;i++ ) {

            // create sql

            if( i > 0 ) {
                sql.append( ", " );
            }

            sql.append( layout[ i ].getColSQL());

            // adding ID column

            if( layout[ i ].isKeyPairCol()) {
                sql.append( "," ).append( layout[ i ].getKeyPairColSQL());
            }

            // add to model

            addColumn( layout[ i ].getColHeader());

            if( layout[ i ].isColorColumn()) {
                setColorColumn( i );
            }

            if( layout[ i ].getColClass() == IDColumn.class ) {
                p_keyColumnIndex = i;
            }
        }

        // set editors (two steps)

        for( int i = 0;i < layout.length;i++ ) {
            setColumnClass( i,layout[ i ].getColClass(),layout[ i ].isReadOnly(),layout[ i ].getColHeader());
        }

        sql.append( " FROM " ).append( from );
        sql.append( " WHERE " ).append( where );

        // Table Selection

        setRowSelectionAllowed( true );

        // org.openXpertya.apps.form.VMatch.dynInit calls routine for initial init only

        if( from.length() == 0 ) {
            return sql.toString();
        }

        //

        String finalSQL = MRole.getDefault().addAccessSQL( sql.toString(),tableName,MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );

        log.finest( finalSQL );

        return finalSQL;
    }    // prepareTable

    /**
     * Descripción de Método
     *
     *
     * @param header
     */

    public void addColumn( String header ) {

        // log.config( "MiniTable.addColumn", header);

        int index = getColumnCount();

        if( getModel() instanceof DefaultTableModel ) {
            DefaultTableModel model = ( DefaultTableModel )getModel();

            model.addColumn( header );
        } else {
            throw new IllegalArgumentException( "Model must be instance of DefaultTableModel" );
        }
    }    // addColumn

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param c
     * @param readOnly
     */

    public void setColumnClass( int index,Class c,boolean readOnly ) {
        setColumnClass( index,c,readOnly,null );
    }    // setColumnClass

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param c
     * @param readOnly
     * @param header
     */

    public void setColumnClass( int index,Class c,boolean readOnly,String header ) {

         log.info( "MiniTable.setColumnClass - " + index +"-"+ c.getName() + ", r/o=" + readOnly+ "header= "+ header);

        TableColumn tc = getColumnModel().getColumn( index );

        if( tc == null ) {
            return;
        }

        // Set R/O

        setColumnReadOnly( index,readOnly );

        // Header

        if( (header != null) && (header.length() > 0) ) {
            tc.setHeaderValue( header );
        }

        // ID Column & Selection

        if( c == IDColumn.class ) {
            tc.setCellRenderer( new IDColumnRenderer( m_multiSelection ));

            if( m_multiSelection ) {
                tc.setCellEditor( new IDColumnEditor());
                setColumnReadOnly(index, false);
                setRowSelectionAllowed(true);
            } else {
                tc.setCellEditor( new ROCellEditor());
            }

            m_minWidth.add( new Integer( 10 ));
            tc.setMaxWidth( 20 );
            tc.setPreferredWidth( 20 );
            tc.setResizable( false );
        }

        // Boolean

        else if( c == Boolean.class ) {
            tc.setCellRenderer( new CheckRenderer());

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
            } else {
                CCheckBox check = new CCheckBox();

                check.setMargin( new Insets( 0,0,0,0 ));
                check.setHorizontalAlignment( JLabel.CENTER );
                tc.setCellEditor( new DefaultCellEditor( check ));
            }

            m_minWidth.add( new Integer( 30 ));
        }

        // Date

        else if( c == Timestamp.class ) {
            tc.setCellRenderer( new VCellRenderer( DisplayType.Date ));

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
            } else {
                tc.setCellEditor( new MiniCellEditor( c ));
            }

            m_minWidth.add( new Integer( 30 ));
        }

        // Amount

        else if( c == BigDecimal.class ) {
            tc.setCellRenderer( new VCellRenderer( DisplayType.Amount ));

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
                m_minWidth.add( new Integer( 70 ));
            } else {
                tc.setCellEditor( new MiniCellEditor( c ));
                m_minWidth.add( new Integer( 80 ));
            }
        }

        // Number

        else if( c == Double.class ) {
            tc.setCellRenderer( new VCellRenderer( DisplayType.Number ));

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
                m_minWidth.add( new Integer( 70 ));
            } else {
                tc.setCellEditor( new MiniCellEditor( c ));
                m_minWidth.add( new Integer( 80 ));
            }
        }

        // Integer

        else if( c == Integer.class ) {
            tc.setCellRenderer( new VCellRenderer( DisplayType.Integer ));

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
            } else {
                tc.setCellEditor( new MiniCellEditor( c ));
            }

            m_minWidth.add( new Integer( 30 ));
        }

        // String

        else {
            tc.setCellRenderer( new VCellRenderer( DisplayType.String ));

            if( readOnly ) {
                tc.setCellEditor( new ROCellEditor());
            } else {
                tc.setCellEditor( new MiniCellEditor( String.class ));
            }

            m_minWidth.add( new Integer( 30 ));
        }

        // log.fine( "Renderer=" + tc.getCellRenderer().toString() + ", Editor=" + tc.getCellEditor().toString());

    }    // setColumnClass

    /**
     * Descripción de Método
     *
     *
     * @param no
     */

    public void setRowCount( int no ) {
        if( getModel() instanceof DefaultTableModel ) {
            DefaultTableModel model = ( DefaultTableModel )getModel();

            model.setRowCount( no );

            // log.config( "MiniTable.setRowCount", "rows=" + getRowCount() + ", cols=" + getColumnCount());

        } else {
            throw new IllegalArgumentException( "Model must be instance of DefaultTableModel" );
        }
    }    // setRowCount

    /**
     * Descripción de Método
     *
     *
     * @param rs
     */

    public void loadTable( ResultSet rs ) {
        if( m_layout == null ) {
            throw new UnsupportedOperationException( "MiniTable.loadTable - layout not defined" );
        }

        // Clear Table

        setRowCount( 0 );

        //

        try {
            while( rs.next()) {
                int row = getRowCount();

                setRowCount( row + 1 );

                int colOffset = 1;    // columns start with 1

                for( int col = 0;col < m_layout.length;col++ ) {
                    Object data     = null;
                    Class  c        = m_layout[ col ].getColClass();
                    int    colIndex = col + colOffset;

                    if( c == IDColumn.class ) {
                        data = new IDColumn( rs.getInt( colIndex ));
                    } else if( c == Boolean.class ) {
                        data = new Boolean( rs.getString( colIndex ).equals( "Y" ));
                    } else if( c == Timestamp.class ) {
                        data = rs.getTimestamp( colIndex );
                    } else if( c == BigDecimal.class ) {
                        data = rs.getBigDecimal( colIndex );
                    } else if( c == Double.class ) {
                        data = new Double( rs.getDouble( colIndex ));
                    } else if( c == Integer.class ) {
                        data = new Integer( rs.getInt( colIndex ));
                    } else if( c == KeyNamePair.class ) {
                        String display = rs.getString( colIndex );
                        int    key     = rs.getInt( colIndex + 1 );

                        data = new KeyNamePair( key,display );
                        colOffset++;
                    } else {
                        String s = rs.getString( colIndex );

                        if( s != null ) {
                            data = s.trim();    // problems with NCHAR
                        }
                    }

                    // store

                    setValueAt( data,row,col );

                    // log.fine( "r=" + row + ", c=" + col + " " + m_layout[col].getColHeader(),
                    // "data=" + data.toString() + " " + data.getClass().getName() + " * " + m_table.getCellRenderer(row, col));

                }
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadTable",e );
        }

        autoSize();
        log.config( "Row(rs)=" + getRowCount());
    }    // loadTable

    /**
     * Descripción de Método
     *
     *
     * @param pos
     */

    public void loadTable( PO[] pos ) {
        if( m_layout == null ) {
            throw new UnsupportedOperationException( "MiniTable.loadTable - layout not defined" );
        }

        // Clear Table

        setRowCount( 0 );

        //

        for( int i = 0;i < pos.length;i++ ) {
            PO  myPO = pos[ i ];
            int row  = getRowCount();

            setRowCount( row + 1 );

            for( int col = 0;col < m_layout.length;col++ ) {
                String columnName = m_layout[ col ].getColSQL();
                Object data       = myPO.get_Value( columnName );

                if( data != null ) {
                    Class c = m_layout[ col ].getColClass();

                    if( c == IDColumn.class ) {
                        data = new IDColumn((( Integer )data ).intValue());
                    } else if( c == Double.class ) {
                        data = new Double((( BigDecimal )data ).doubleValue());
                    }
                }

                // store

                setValueAt( data,row,col );
            }
        }

        autoSize();
        log.config( "Row(array)=" + getRowCount());
    }    // loadTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Integer getSelectedRowKey() {
        if( m_layout == null ) {
            throw new UnsupportedOperationException( "MiniTable.getSelectedRowKey - layout not defined" );
        }

        int row = getSelectedRow();

        if( (row != -1) && (p_keyColumnIndex != -1) ) {
            Object data = getModel().getValueAt( row,p_keyColumnIndex );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // getSelectedRowKey

    
	/**
	 * @return collection of selected IDs
	 */
	public Collection<Integer> getSelectedKeys()
	{
		if (m_layout == null)
		{
			throw new UnsupportedOperationException("Layout not defined");
		}
		if (p_keyColumnIndex < 0)
		{
			throw new UnsupportedOperationException("Key Column is not defined");
		}
		//
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int row = 0; row < getRowCount(); row++)
		{
			Object data = getModel().getValueAt(row, p_keyColumnIndex);
			if (data instanceof IDColumn)
			{
				IDColumn record = (IDColumn)data;
				if (record.isSelected())
				{
					list.add(record.getRecord_ID());
				}
			}
		}
		return list;
	}
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ColumnInfo[] getLayoutInfo() {
        return m_layout;
    }    // getLayout

    /**
     * Descripción de Método
     *
     *
     * @param multiSelection
     */

    public void setMultiSelection( boolean multiSelection ) {
        m_multiSelection = multiSelection;
    }    // setMultiSelection

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMultiSelection() {
        return m_multiSelection;
    }    // isMultiSelection

    /**
     * Descripción de Método
     *
     *
     * @param modelIndex
     */

    public void setColorColumn( int modelIndex ) {
        m_colorColumnIndex = modelIndex;
    }    // setColorColumn

    /**
     * Descripción de Método
     *
     *
     * @param dataCompare
     */

    public void setColorCompare( Object dataCompare ) {
        m_colorDataCompare = dataCompare;
    }    //

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getColorCode( int row ) {
        if( m_colorColumnIndex == -1 ) {
            return 0;
        }

        Object data = getModel().getValueAt( row,m_colorColumnIndex );
        int    cmp  = 0;

        // We need to have a Number

        if( data == null ) {
            return 0;
        }

        try {
            if( data instanceof Timestamp ) {
                if( (m_colorDataCompare == null) ||!( m_colorDataCompare instanceof Timestamp )) {
                    m_colorDataCompare = new Timestamp( System.currentTimeMillis());
                }

                cmp = (( Timestamp )m_colorDataCompare ).compareTo(( Timestamp )data );
            } else {
                if( (m_colorDataCompare == null) ||!( m_colorDataCompare instanceof BigDecimal )) {
                    m_colorDataCompare = Env.ZERO;
                }

                if( !( data instanceof BigDecimal )) {
                    data = new BigDecimal( data.toString());
                }

                cmp = (( BigDecimal )m_colorDataCompare ).compareTo(( BigDecimal )data );
            }
        } catch( Exception e ) {
            return 0;
        }

        if( cmp > 0 ) {
            return -1;
        }

        if( cmp < 0 ) {
            return 1;
        }

        return 0;
    }    // getColorCode

    /**
     * Descripción de Método
     * Realizado por Mauricio Calgaro (Maurix)
     * Fecha: 14-12-2010
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
    
}    // MiniTable



/*
 *  @(#)MiniTable.java   02.07.07
 * 
 *  Fin del fichero MiniTable.java
 *  
 *  Versión 2.2
 *
 */