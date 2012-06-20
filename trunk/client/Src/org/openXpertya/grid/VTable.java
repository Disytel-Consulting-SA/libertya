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



package org.openXpertya.grid;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.compiere.swing.CTable;
import org.openXpertya.grid.ed.VCellEditor;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VHeaderRenderer;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTable;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VTable extends CTable implements PropertyChangeListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VTable() {
        super();
    }    // VTable

     /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VTable.class );

 
    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {

        // log.config( "VTable.propertyChange", evt);

        if( evt.getPropertyName().equals( MTab.PROPERTY )) {
            int row    = (( Integer )evt.getNewValue()).intValue();
            int selRow = getSelectedRow();

            if( row == selRow ) {
                return;
            }

            log.config( MTab.PROPERTY + "=" + row + " from " + selRow );
            setRowSelectionInterval( row,row );

            // log.config( "VTable.propertyChange - fini", MTab.PROPERTY + "=" + row + " from " + selRow);

        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param row
     *
     * @return
     */

    public int getColorCode( int row ) {
        return(( MTable )getModel()).getColorCode( row );
    }    // getColorCode

    /**
     * Descripción de Método
     *
     *
     * @param modelColumnIndex
     */

    protected void sort( int modelColumnIndex ) {
        int rows = getRowCount();

        if( rows == 0 ) {
            return;
        }

        //

        TableModel model = getModel();

        if( !( model instanceof MTable )) {
            super.sort( modelColumnIndex );

            return;
        }

        // other sort column

        if( modelColumnIndex != p_lastSortIndex ) {
            p_asc = true;
        } else {
            p_asc = !p_asc;
        }

        p_lastSortIndex = modelColumnIndex;

        //

        log.config( "#" + modelColumnIndex + " - rows=" + rows + ", asc=" + p_asc );
        (( MTable )model ).sort( modelColumnIndex,p_asc );

        // table model fires "Sorted" DataStatus event which causes MTab to position to row 0

    }    // sort

    /**
     * Descripción de Método
     *
     *
     * @param row
     * @param column
     * @param e
     *
     * @return
     */

    public boolean editCellAt( int row,int column,java.util.EventObject e ) {
        if( !super.editCellAt( row,column,e )) {
            return false;
        }

        // log.fine( "VTable.editCellAt", "r=" + row + ", c=" + column);

        Object ed = getCellEditor();

        if( ed instanceof VEditor ) {
            (( Component )ed ).requestFocus();
        } else if( ed instanceof VCellEditor ) {
            ed = (( VCellEditor )ed ).getEditor();
            (( Component )ed ).requestFocus();
        }

        return true;
    }    // editCellAt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return new StringBuffer( "VTable[" ).append( getModel()).append( "]" ).toString();
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void removeAll() {
        super.removeAll();
    }    // removeAll
    
    /**
     * Crear los renderers de los headers para que muestren una imágen con el
     * estado del orden de los datos.  
     */
    public void createSortRenderers() {
    	TableColumn column = null;
    	TableCellRenderer renderer = null;
    	// Se recorren todas las columnas de la tabla...
        for (int i = 0; i < getColumnCount(); i++) {
        	// Se obtiene el renderer actual.
        	column = getColumnModel().getColumn(i);
        	renderer = column.getHeaderRenderer();
        	if(renderer != null) {
        		// Si tiene renderer, se wrappea con un nuevo renderer que muestra
        		// la imágen.
        		column.setHeaderRenderer(new SortHeaderRenderer(renderer));
        	}
        }
    }
    
    private static ImageIcon sortDescIcon = ImageFactory.getImageIcon("SortDesc.gif"); 
    private static ImageIcon sortAscIcon = ImageFactory.getImageIcon("SortAsc.gif");
    private static ImageIcon sortNullIcon = ImageFactory.getImageIcon("SortNull.gif");
    
    /** Wrapper de renderer de headers. Agrega la imagen que muestra el órden
     * actual de la columna, en caso de que se hayan ordenado los datos por esta
     * columna */
    private class SortHeaderRenderer extends DefaultTableCellRenderer {

    	private TableCellRenderer originalRenderer;

		public SortHeaderRenderer(TableCellRenderer originalRenderer) {
			super();
			this.originalRenderer = originalRenderer;
		}

		public Component getTableCellRendererComponent(JTable table, Object obj,boolean isSelected, boolean hasFocus, int row, int column) {
    		Component component = originalRenderer.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
    		ImageIcon sortIcon = null; //sortNullIcon;	
    		// Si esta columna es por la que se ordenaron los datos entonces seteo
    		// la imágen a mostrar.
    		if (column == getViewColumnIndex(table, p_lastSortIndex))
    			sortIcon = (p_asc ? sortAscIcon : sortDescIcon);
			// Solo se agrega la imágen si el renderer original renderiza el componente
    		// a un JLabel o JButton. Se asigna la imágen y la alineación del texto.
    		if (component instanceof JLabel) {
				JLabel label = (JLabel) component; 
				label.setIcon(sortIcon);
				label.setHorizontalAlignment(JLabel.CENTER);
			} else if (component instanceof JButton) {
				JButton button = (JButton)component;
				button.setIcon(sortIcon);
				button.setHorizontalAlignment(JLabel.CENTER);
			} else
				log.warning("Not supported component for rendering sort state");

    		return component;
    	}
    }

	@Override
	public void setModel(TableModel model) {
		super.setModel(model);
		createSortRenderers();
	}

	private int getViewColumnIndex(JTable table, int modelColumnIndex) {
        for (int c=0; c<table.getColumnCount(); c++) {
            TableColumn col = table.getColumnModel().getColumn(c);
            if (col.getModelIndex() == modelColumnIndex) {
                return c;
            }
        }
        return -1;
    }
   
}    // VTable



/*
 *  @(#)VTable.java   02.07.07
 * 
 *  Fin del fichero VTable.java
 *  
 *  Versión 2.2
 *
 */
