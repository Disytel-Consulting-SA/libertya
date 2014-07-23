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



package org.openXpertya.grid.ed;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.grid.GridController;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTable;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VCellEditor extends AbstractCellEditor implements TableCellEditor,VetoableChangeListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mField
     */

    public VCellEditor( MField mField ) {
    	super();
        m_mField = mField;

        // Click

    }    // VCellEditor

    /**
     * Constructor que recibe el gridController a fin de poder propagarle el vetoableChange
     */
    public VCellEditor( MField mField, GridController owner ) {
    	this(mField);
    	gridController = owner;
        // Click

    }    // VCellEditor
    
    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private VEditor m_editor = null;

    /** Descripción de Campos */

    private JTable m_table = null;

    /** Descripción de Campos */

    private static int CLICK_TO_START = 2;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VCellEditor.class );

    /** Owner indirecto para propagar vetoableChange */
    private GridController gridController;
    
    /**
     * Descripción de Método
     *
     */

    private void createEditor() {
        m_editor = VEditorFactory.getEditor( m_mField,true );
        m_editor.addVetoableChangeListener( this );
        m_editor.addActionListener( this );
    }    // createEditor

    /**
     * Descripción de Método
     *
     *
     * @param anEvent
     *
     * @return
     */

    public boolean isCellEditable( EventObject anEvent ) {

        // log.config( "VCellEditor.isCellEditable", anEvent);

        if( !m_mField.isEditable( true )) {    // row data is in context
            return false;
        }

        // not enough mouse clicks

        if( (anEvent instanceof MouseEvent) && (( MouseEvent )anEvent ).getClickCount() < CLICK_TO_START ) {
            return false;
        }

        if( m_editor == null ) {
            createEditor();
        }

        return true;
    }    // isCellEditable

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
        log.fine( "Value=" + value + ", row=" + row + ", col=" + col );
        table.setRowSelectionInterval( row,row );    // force moving to new row

        if( m_editor == null ) {
            createEditor();
        }

        m_table = table;

        // Set Value

        m_editor.setValue( value );

        // Set Background/Foreground to "normal" (unselected) colors

        m_editor.setBackground( m_mField.isError());
        m_editor.setForeground( CompierePLAF.getTextColor_Normal());

        // Other UI

        m_editor.setFont( table.getFont());
        m_editor.setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder" ));

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

    public boolean shouldSelectCell( EventObject e ) {

        // log.fine( "VCellEditor.shouldSelectCell", e.toString());

        return true;
    }    // shouldSelectCell

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getCellEditorValue() {

        // log.fine( "VCellEditor.getCellEditorValue", m_editor.getValue());

        return m_editor.getValue();
    }    // getCellEditorValue

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        if( m_table == null ) {
            return;
        }

        log.fine( e.getPropertyName() + "=" + e.getNewValue());

        try {
        	// https://code.google.com/p/libertya/issues/detail?id=17
        	// Los VCellEditors consumen el vetoableChange, y es por ésto que el evento nunca llega al gridController.
        	// Este es el motivo por el cual no se actualizan los valores de los lookups en modo grilla (Issue Google Code GC-17).
        	if (gridController != null && e.getSource() instanceof VLookup) 
        		gridController.vetoableChange(e);
        }
        catch (Exception ex) {
        	// No quedan muchas alternativas en caso de excepción
        	ex.printStackTrace();
        }
        (( MTable )m_table.getModel()).setChanged( true );
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public VEditor getEditor() {
        return m_editor;
    }    // getEditor

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.fine( "Value=" + m_editor.getValue());
        super.stopCellEditing();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_editor = null;
        m_mField = null;
        m_table  = null;
    }    // dispose
}    // VCellEditor



/*
 *  @(#)VCellEditor.java   02.07.07
 * 
 *  Fin del fichero VCellEditor.java
 *  
 *  Versión 2.2
 *
 */
