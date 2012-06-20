/*
 * @(#)CTable.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.compiere.swing;

import org.openXpertya.util.MSort;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *      Model Independent enhanced JTable.
 *  Provides sizing and sorting
 *
 *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *      @version        $Id: CTable.java,v 1.7 2005/03/11 20:34:38 jjanke Exp $
 */
public class CTable extends JTable {

    /** Last model index sorted */
    protected int	p_lastSortIndex	= -1;

    /** Model Index of Key Column */
    protected int	p_keyColumnIndex	= -1;

    /** Sort direction */
    protected boolean	p_asc	= true;

    /** Sizing: making sure it fits in a column */
    private final int	SLACK	= 15;

    /** Sizing: space for sort images in headers */
    private final int SORT_SLACK = 10;
    
    /** Sizing: max size in pt */
    private final int	MAXSIZE	= 250;

    /**
     *      Default Constructor
     */
    public CTable() {

        super(new DefaultTableModel());
        setColumnSelectionAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTableHeader().addMouseListener(new CTableMouseListener());

    }		// CTable

    /**
     *      Size Columns.
     *      @param useColumnIdentifier if false uses plain content -
     *  otherwise uses Column Identifier to indicate displayed columns
     */
    public void autoSize(boolean useColumnIdentifier) {

        TableModel	model	= this.getModel();
        int		size	= model.getColumnCount();

        // for all columns
        for (int c = 0; c < size; c++) {

            TableColumn	column	= getColumnModel().getColumn(c);

            // Not displayed columns
            if (useColumnIdentifier && ((column.getIdentifier() == null) || (column.getMaxWidth() == 0) || (column.getIdentifier().toString().length() == 0))) {
                continue;
            }

            int	width	= 0;

            // Header
            TableCellRenderer	renderer	= column.getHeaderRenderer();

            if (renderer == null) {
                renderer	= new DefaultTableCellRenderer();
            }

            Component	comp	= null;

            if (renderer != null) {
                comp	= renderer.getTableCellRendererComponent(this, column.getHeaderValue(), false, false, 0, 0);
            }

            //
            if (comp != null) {

                width	= comp.getPreferredSize().width + SLACK + SORT_SLACK;
                width	= Math.max(width, comp.getWidth());

                // Cells
                int	col	= column.getModelIndex();
                int	maxRow	= Math.min(30, getRowCount());

                try {

                    for (int row = 0; row < maxRow; row++) {

                        renderer	= getCellRenderer(row, col);
                        comp		= renderer.getTableCellRendererComponent(this, getValueAt(row, col), false, false, row, col);

                        int	rowWidth	= comp.getPreferredSize().width + SLACK;

                        width	= Math.max(width, rowWidth);
                    }

                } catch (Exception e) {

                    System.out.println(column.getIdentifier());
                    e.printStackTrace();
                }

                // Width not greater than 250
                width	= Math.min(MAXSIZE, width);
            }

            //

            column.setPreferredWidth(width);

            // JOptionPane.showMessageDialog( null,"Columna = "+column.toString()+ " En CTable con column.setreferencedWidth= " + width,"..Fin", JOptionPane.INFORMATION_MESSAGE );

        }	// for all columns

    }		// autoSize

    /**
     *  Sort Table
     *  @param modelColumnIndex model column sort index
     */
    protected void sort(int modelColumnIndex) {
        DefaultTableModel	model	= (DefaultTableModel) getModel();

        if (modelColumnIndex < 0 || modelColumnIndex >= model.getColumnCount())
        	return;
        
        int	rows	= getRowCount();

        if (rows == 0) {
            return;
        }

        // other column
        if (modelColumnIndex != p_lastSortIndex) {
            p_asc	= true;
        } else {
            p_asc	= !p_asc;
        }

        p_lastSortIndex	= modelColumnIndex;

        //
        // System.out.println("CTable.sort #" + modelColumnIndex + " - rows=" + rows + ", asc=" + p_asc);

        // Selection
        Object	selected	= null;
        int	selRow		= getSelectedRow();
        int	selCol		= (p_keyColumnIndex == -1)
                                  ? 0
                                  : p_keyColumnIndex;		// used to identify current row

        if (getSelectedRow() >= 0) {
            selected	= getValueAt(selRow, selCol);
        }

        // Prepare sorting
        MSort			sort	= new MSort(0, null);

        sort.setSortAsc(p_asc);

        // Create sortList
        ArrayList	sortList	= new ArrayList(rows);

        // fill with data entity
        for (int i = 0; i < rows; i++) {

            Object	value	= model.getValueAt(i, modelColumnIndex);

            sortList.add(new MSort(i, value));
        }

        // sort list it
        Collections.sort(sortList, sort);

        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        for( int i = 0;i < rows;i++ ) {
            int index = (( MSort )sortList.get( i )).index;
            	m.put(i, index);
            }
            
        reSort(m, model.getDataVector() );
            
        // selection
        clearSelection();

        if (selected != null) {

            for (int r = 0; r < rows; r++) {

                if (selected.equals(getValueAt(r, selCol))) {

                    setRowSelectionInterval(r, r);

                    break;
                }
            }

        }	// selected != null

    }		// sort

    
    public void reSort(HashMap<Integer, Integer> m, Vector rows)
    {
    	// copiar los datos temporalmente a fin de limpiar rows
    	ArrayList temp = new ArrayList(rows);
    	rows.clear();
    	
    	// insertar ordenado en rows
    	int totalRows = m.size();
    	for( int index = 0;index < totalRows; index++ ) {
   			rows.add(temp.get((Integer)m.get(index)));    			
        }
    	// garbage collector
    	temp.clear();
    	temp = null;
    }
    
    /**
     *  Stop Table Editors and remove focus
     *  @param saveValue save value
     */
    public void stopEditor(boolean saveValue) {

        // MultiRow - remove editors
        ChangeEvent	ce	= new ChangeEvent(this);

        if (saveValue) {
            editingStopped(ce);
        } else {
            editingCanceled(ce);
        }

        //
        if (getInputContext() != null) {
            getInputContext().endComposition();
        }

        // change focus to next
        transferFocus();
    }		// stopEditor

    /**
     *  String Representation
     *  @return info
     */
    public String toString() {
        return new StringBuffer("CTable[").append(getModel()).append("]").toString();
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Model index of Key Column
     *  @return model index
     */
    public int getKeyColumnIndex() {
        return p_keyColumnIndex;
    }		// getKeyColumnIndex

    /**
     *      Get Current Row Key Column Value
     *  @return value or null
     */
    public Object getSelectedKeyColumnValue() {

        int	row	= getSelectedRow();

        if ((row != -1) && (p_keyColumnIndex != -1)) {
            return getModel().getValueAt(row, p_keyColumnIndex);
        }

        return null;

    }		// getKeyColumnValue

    /**
     *  Get Selected Value or null
     *  @return value
     */
    public Object getSelectedValue() {

        int	row	= getSelectedRow();
        int	col	= getSelectedColumn();

        if ((row == -1) || (col == -1)) {
            return null;
        }

        return getValueAt(row, col);

    }		// getSelectedValue

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Model index of Key Column.
     *  Used for identifying previous selected row after fort complete to set as selected row.
     *  If not set, column 0 is used.
     *      @param keyColumnIndex model index
     */
    public void setKeyColumnIndex(int keyColumnIndex) {
        p_keyColumnIndex	= keyColumnIndex;
    }		// setKeyColumnIndex

    /**
     *  MouseListener
     */
    class CTableMouseListener extends MouseAdapter {

        /**
         *  Constructor
         */
        public CTableMouseListener() {
            super();
        }	// CTableMouseListener

        /**
         *  Mouse clicked
         *  @param e event
         */
        public void mouseClicked(MouseEvent e) {
        	if (isSorted()) {
	            int	vc	= getColumnModel().getColumnIndexAtX(e.getX());
	
	            // log.info( "Sort " + vc + "=" + getColumnModel().getColumn(vc).getHeaderValue());
	            int	mc	= convertColumnIndexToModel(vc);
	            
	            sort(mc);
        	}
        }
    }		// CTableMouseListener
    
    /** Permite configurar si la tabla tiene capacidades de ordenamiento o no */
    private boolean sorted = true;

	/**
	 * @return the sorted
	 */
	public boolean isSorted() {
		return sorted;
	}

	/**
	 * @param sorted the sorted to set
	 */
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
    
    
}		// CTable



/*
 * @(#)CTable.java   02.jul 2007
 * 
 *  Fin del fichero CTable.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
