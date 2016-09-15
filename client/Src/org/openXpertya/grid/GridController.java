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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.APanel;
import org.openXpertya.apps.AppsAction;
import org.openXpertya.grid.ed.VCellEditor;
import org.openXpertya.grid.ed.VCellRenderer;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VEditorFactory;
import org.openXpertya.grid.ed.VHeaderRenderer;
import org.openXpertya.grid.ed.VRowIDEditor;
import org.openXpertya.grid.ed.VRowIDRenderer;
import org.openXpertya.grid.tree.VTreePanel;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.model.DataStatusListener;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTable;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.model.MWindow;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class GridController extends CPanel implements DataStatusListener,ListSelectionListener,VetoableChangeListener,PropertyChangeListener,MouseListener {

    /**
     * Constructor de la clase ...
     *
     */

    public GridController() {
        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"GridController",e );
        }
    }    // GridController

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "GridController for " + m_mTab;
    }    // toString

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( GridController.class );

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JSplitPane splitPane = new JSplitPane();

    /** Descripción de Campos */

    private CPanel graphPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout graphLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel cardPanel = new CPanel();

    /** Descripción de Campos */

    private CardLayout cardLayout = new CardLayout();

    /** Descripción de Campos */

    private JSplitPane srPane = new JSplitPane();

    /** Descripción de Campos */

    private JScrollPane vPane = new JScrollPane();

    /** Descripción de Campos */

    private GridController vIncludedGC = null;

    /** Descripción de Campos */

    private JScrollPane mrPane = new JScrollPane();

    /** Descripción de Campos */

    private CPanel xPanel = new CPanel();

    /** Descripción de Campos */

    private FlowLayout xLayout = new FlowLayout();

    /** Descripción de Campos */

    private VTable vTable = new VTable();

    /** Descripción de Campos */

    private VPanel vPanel = new VPanel();

    /** Asociación entre editores y campos */
    private Map<MField, VEditor> fieldEditors;
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        this.add( splitPane,BorderLayout.CENTER );
        splitPane.setOpaque( false );
        graphPanel.setLayout( graphLayout );

        //

        splitPane.add( graphPanel,JSplitPane.LEFT );
        splitPane.add( cardPanel,JSplitPane.RIGHT );
        splitPane.setBorder( null );
        splitPane.setName( "gc_splitPane" );

        //

        cardPanel.setLayout( cardLayout );
        cardPanel.add( srPane,"srPane" );    // Sequence Important!
        cardPanel.add( mrPane,"mrPane" );
        cardPanel.setBorder( null );
        cardPanel.setName( "gc_cardPanel" );

        // single row (w/o xPane it would be centered)

        srPane.setBorder( null );
        srPane.setName( "gc_srPane" );
        srPane.setOrientation( JSplitPane.VERTICAL_SPLIT );
        srPane.add( vPane,JSplitPane.TOP );
        srPane.setTopComponent( vPane );
        vPane.getViewport().add( xPanel,null );
        xPanel.add( vPanel );
        vPane.setBorder( null );
        xPanel.setLayout( xLayout );
        xPanel.setName( "gc_xPanel" );
        xLayout.setAlignment( FlowLayout.LEFT );
        xLayout.setHgap( 0 );
        xLayout.setVgap( 0 );

        // multi-row

        mrPane.setBorder( null );
        mrPane.getViewport().add( vTable,null );
        mrPane.setName( "gc_mrPane" );

        //

        graphPanel.setBorder( null );
        graphPanel.setName( "gc_graphPanel" );
        srPane.setDividerLocation( 200 );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    
    public void setRowSelectionAllowed(boolean flag) {
    	vTable.setRowSelectionAllowed(false);
    }
    
    public void dispose() {
        log.config( "(" + m_mTab.toString() + ")" );

        // clear info

        stopEditor( false );

        if( m_mTab.needSave( true,false )) {
            m_mTab.dataIgnore();
        }

        vIncludedGC = null;

        // Listeners

        m_mTab.getTableModel().removeDataStatusListener( this );
        m_mTab.getTableModel().removeVetoableChangeListener( this );
        vTable.getSelectionModel().removeListSelectionListener( this );
        m_mTab.removePropertyChangeListener( vTable );

        // editors

        Component[] comp = vPanel.getComponents();

        for( int i = 0;i < comp.length;i++ ) {
            if( comp[ i ] instanceof VEditor ) {
                VEditor vEditor = ( VEditor )comp[ i ];

                vEditor.removeVetoableChangeListener( this );

                String columnName = comp[ i ].getName();
                MField mField     = m_mTab.getField( columnName );

                if( mField != null ) {
                    mField.removePropertyChangeListener( vEditor );
                }

                vEditor.dispose();
            }
        }

        vTable.removeAll();
        vTable.setModel( new DefaultTableModel());    // remove reference
        vTable = null;
        vPanel.removeAll();
        vPanel = null;
        splitPane.removeAll();
        splitPane = null;
        m_mTab    = null;
        m_tree    = null;
        this.removeAll();
    }    // dispose

    /** Descripción de Campos */

    private MTab m_mTab = null;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private boolean m_onlyMultiRow = false;

    /** Descripción de Campos */

    private boolean m_singleRow = true;

    /** Descripción de Campos */

    private boolean m_vetoActive = false;

    /** Descripción de Campos */

    private VTreePanel m_tree;

    /**
     * Descripción de Método
     *
     *
     * @param mTab
     * @param onlyMultiRow
     * @param WindowNo
     * @param aPanel
     * @param mWindow
     *
     * @return
     */

    public boolean initGrid( MTab mTab,boolean onlyMultiRow,int WindowNo,APanel aPanel,MWindow mWindow ) {
        log.config( "(" + mTab.toString() + ")" );
        m_mTab         = mTab;
        m_WindowNo     = WindowNo;
        m_onlyMultiRow = onlyMultiRow;
        fieldEditors = new HashMap<MField, VEditor>();
        
        // Set up Multi Row Table

        vTable.setModel( m_mTab.getTableModel());

        // Update Table Info -------------------------------------------------

        int size = setupVTable( aPanel,m_mTab,vTable );

        // Set Color on Tab Level
        // this.setBackgroundColor (mTab.getColor());

        // Single Row  -------------------------------------------------------

        if( !m_onlyMultiRow ) {
            for( int i = 0;i < size;i++ ) {
                MField mField = m_mTab.getField( i );

                if( mField.isDisplayed()) {
                    VEditor vEditor = VEditorFactory.getEditor( m_mTab,mField,false );

                    if( vEditor == null ) {
                        log.severe( "Editor not created for " + mField.getColumnName());

                        continue;
                    }

                    // MField => VEditor - New Field value to be updated to editor

                    mField.addPropertyChangeListener( vEditor );

                    // VEditor => this - New Editor value to be updated here (MTable)

                    vEditor.addVetoableChangeListener( this );

                    // Add to VPanel

                    vPanel.addField( VEditorFactory.getLabel( mField ),vEditor,mField );

                    // APanel Listen to buttons

                    if( (mField.getDisplayType() == DisplayType.Button) && (aPanel != null) ) {
                        (( JButton )vEditor ).addActionListener( aPanel );
                    }
                    
                    fieldEditors.put(mField, vEditor);
                }
            }    // for all fields

            // No Included Grid Controller

            srPane.setResizeWeight( 1 );    // top part gets all
            srPane.setDividerSize( 0 );
            srPane.setDividerLocation( 9999 );

            // Use SR to size MR

            mrPane.setPreferredSize( vPanel.getPreferredSize());
        }    // Single-Row

        // Tree Graphics Layout

        int AD_Tree_ID = 0;

        if( m_mTab.isTreeTab()) {
            AD_Tree_ID = MTree.getAD_Tree_ID( Env.getAD_Client_ID( Env.getCtx()),m_mTab.getKeyColumnName());
        }

        if( m_mTab.isTreeTab() && (AD_Tree_ID != 0) ) {
            m_tree = new VTreePanel( m_WindowNo,false,true );

            if( m_mTab.getTabNo() == 0 ) {    // initialize other tabs later
                m_tree.initTree( AD_Tree_ID );
            }

            m_tree.addPropertyChangeListener( VTreePanel.NODE_SELECTION,this );
            graphPanel.add( m_tree,BorderLayout.CENTER );
            splitPane.setDividerLocation( 250 );

            // splitPane.resetToPreferredSizes();

        } else                                // No Graphics - hide
        {
            graphPanel.setPreferredSize( new Dimension( 0,0 ));
            splitPane.setDividerSize( 0 );
            splitPane.setDividerLocation( 0 );
        }

        // Receive DataStatusChanged info from MTab

        m_mTab.addDataStatusListener( this );

        // Receive vetoableChange info from MTable when saving

        m_mTab.getTableModel().addVetoableChangeListener( this );

        // Selection Listener -> valueChanged

        vTable.getSelectionModel().addListSelectionListener( this );

        // Navigation (RowChanged)

        m_mTab.addPropertyChangeListener( vTable );

        // Update UI
        vTable.autoSize( true );//Original
        //Modificado por ConSerTi.
        //vTable.autoSize( false );//Modificada
        setTabLevel( m_mTab.getTabLevel());

        // Set initial presentation

        if( onlyMultiRow ||!m_mTab.isSingleRow()) {
            switchMultiRow();
        } else {
            switchSingleRow();
        }

        // log.config( "GridController.dynInit (" + mTab.toString() + ") - fini");

        return true;
    }    // initGrid

    /**
     * Descripción de Método
     *
     *
     * @param gc
     *
     * @return
     */

    public boolean includeTab( GridController gc ) {
        MTab imcludedMTab = gc.getMTab();

        if( m_mTab.getIncluded_Tab_ID() != imcludedMTab.getAD_Tab_ID()) {
            return false;
        }

        //

        vIncludedGC = gc;
        vIncludedGC.switchMultiRow();
        vIncludedGC.setRowSelectionAllowed(false);
        //

        Dimension size = getPreferredSize();

        srPane.setResizeWeight( .75 );    // top part gets 75%
        srPane.add( vIncludedGC,JSplitPane.BOTTOM );
        srPane.setBottomComponent( vIncludedGC );
        srPane.setDividerSize( 5 );

        //

        int height = 150;

        vIncludedGC.setPreferredSize( new Dimension( 600,height ));
        setPreferredSize( new Dimension( size.width,size.height + height ));
        srPane.setDividerLocation( size.height );

        //

        imcludedMTab.setIncluded( true );
        imcludedMTab.query( false,0 );

        //

        JRootPane rt = SwingUtilities.getRootPane( this );

        if( rt == null ) {
            System.out.println( "Root pane null" );
        } else {
            // System.out.println( "Root=" + rt );
            rt.addMouseListener( vIncludedGC );

            Component gp = rt.getGlassPane();

            if( gp == null ) {
                System.out.println( "No Glass Pane" );
            } else {
                // System.out.println( "Glass=" + gp );
                gp.addMouseListener( vIncludedGC );
            }
        }

        vIncludedGC.addMouseListener( vIncludedGC );
        vIncludedGC.enableEvents( AWTEvent.HIERARCHY_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK );

        return true;
    }    // IncludeTab

    /**
     * Descripción de Método
     *
     *
     * @param aPanel
     * @param mTab
     * @param table
     *
     * @return
     */

    private int setupVTable( APanel aPanel,MTab mTab,VTable table ) {
    	if( !mTab.isDisplayed()) {
            return 0;
        }

        int size = mTab.getFieldCount();

        for( int i = 0;i < size;i++ ) {
            TableColumn tc     = getTableColumn( table,i );
            MField      mField = mTab.getField( i );

            //

            if( mField.getColumnName().equals( tc.getIdentifier().toString())) {
                if( mField.getDisplayType() == DisplayType.RowID ) {
                    tc.setCellRenderer( new VRowIDRenderer( false ));
                    tc.setCellEditor( new VRowIDEditor( false ));
                    tc.setHeaderValue( "" );
                    tc.setMaxWidth( 2 );
                } else {

                    // need to set CellEditor explicitly as default editor based on class causes problem (YesNo-> Boolean)
                    if( mField.isDisplayed() && mField.isDisplayedInGrid()) {
                        tc.setCellRenderer( new VCellRenderer( mField ));

                        VCellEditor ce = new VCellEditor( mField, this );

                        tc.setCellEditor( ce );

                        //

                        tc.setHeaderValue( mField.getHeader());
                        //tc.setPreferredWidth( Math.min( mField.getDisplayLength(),30 )); original, Modificado por ConSerTi
                        tc.setPreferredWidth(mField.getDisplayLength());
                        tc.setMinWidth(1);
                        	
                        tc.setHeaderRenderer( new VHeaderRenderer( mField.getDisplayType()));

                        // Enable Button actions in grid

                        if( mField.getDisplayType() == DisplayType.Button ) {
                            VEditor button = ce.getEditor();

                            if( (button != null) && (aPanel != null) ) {
                                (( JButton )button ).addActionListener( aPanel );
                            }
                        }
                    } else    // column not displayed
                    {
                        tc.setHeaderValue( null );
                        tc.setMinWidth( 0 );
                        tc.setMaxWidth( 0 );
                        tc.setPreferredWidth( 0 );
                    }
                }

                // System.out.println ("TableColumnID " + tc.getIdentifier ()
                // + "  Renderer=" + tc.getCellRenderer ()
                // + mField.getHeader ());
                //Modificado Por ConSerTi, para permitir campos de longuitud menor de 30
                tc.setMinWidth( 10 );
            }                 // found field
                    else {
                log.log( Level.SEVERE,"TableColumn " + tc.getIdentifier() + " <> MField " + mField.getColumnName() + mField.getHeader());
            }
        }    // for all fields

        table.createSortRenderers();
        
        return size;
    }    // setupVTable

    /**
     * Descripción de Método
     *
     */

    public void activate() {

        // Tree to be initiated on second/.. tab

        if( m_mTab.isTreeTab() && (m_mTab.getTabNo() != 0) ) {
            int AD_Tree_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"AD_Tree_ID" );

            if( AD_Tree_ID == 0 ) {
                AD_Tree_ID = MTree.getAD_Tree_ID( Env.getAD_Client_ID( Env.getCtx()),m_mTab.getKeyColumnName());
            }

            m_tree.initTree( AD_Tree_ID );
        }
    }    // activate

    /**
     * Descripción de Método
     *
     *
     * @param aIgnore
     */

    public void registerESCAction( AppsAction aIgnore ) {
        int c = VTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

        vTable.getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE,0 ),aIgnore.getName());
        vTable.getActionMap().put( aIgnore.getName(),aIgnore );

        // AEnv.printActionInputMap(vTable);

    }    // registerESCAction

    /**
     * Descripción de Método
     *
     *
     * @param onlyCurrentRows
     * @param onlyCurrentDays
     */

    public void query( boolean onlyCurrentRows,int onlyCurrentDays ) {

        // start loading while building screen

        m_mTab.query( onlyCurrentRows,onlyCurrentDays );

        // Update UI
        //vTable.autoSize( true );Original
        //Modificada por ConSerTi.
        vTable.autoSize( false );
    }    // query

    /**
     * Descripción de Método
     *
     */

    public void switchRowPresentation() {
        stopEditor( true );

        if( m_singleRow ) {
            switchMultiRow();
        } else {
            switchSingleRow();
        }
    }    // switchRowPresentation

    /**
     * Descripción de Método
     *
     */

    public void switchSingleRow() {
        if( m_onlyMultiRow ) {
            return;
        }

        cardLayout.first( cardPanel );
        m_singleRow = true;
        dynamicDisplay( 0 );
    }    // switchSingleRow

    /**
     * Descripción de Método
     *
     */

    public void switchMultiRow() {
        cardLayout.last( cardPanel );
        m_singleRow = false;
    }    // switchSingleRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSingleRow() {
        return m_singleRow;
    }    // isSingleRow

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void removeDataStatusListener( DataStatusListener l ) {
        m_mTab.removeDataStatusListener( l );
    }    // removeDataStatusListener

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public synchronized void addDataStatusListener( DataStatusListener l ) {
        m_mTab.addDataStatusListener( l );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dataStatusChanged( DataStatusEvent e ) {

        // if (e.getChangedColumn() == 0)
        // return;

        int col = e.getChangedColumn();

        log.config( "(" + m_mTab + ") Col=" + col + ": " + e.toString());

        // Process Callout only for specific columns

        if( col != 0 ) {
            MField mField = m_mTab.getField( col );

            if( (mField != null) /*&& (mField.getCallout().length() > 0)*/ ) {
                String msg = m_mTab.processFieldChange( mField );    // Dependencies & Callout

                if( msg.length() > 0 ) {
                    ADialog.error( m_WindowNo,this,msg );
                }
            // Agregado por Disytel - Franco Bonafine
            // Si el campo no tiene callout igual se procesan las dependencias para actualizar
            // los valores mostrados de campos dependientes.    
            } else if (mField != null) {
                m_mTab.processDependencies(mField);
            }
        }

        dynamicDisplay( col );
    }    // dataStatusChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void valueChanged( ListSelectionEvent e ) {

        // nothing or initiated by mouse (wait for "real" one)

        if( e.getValueIsAdjusting()) {
            return;
        }

        // no rows

        if( m_mTab.getRowCount() == 0 ) {
            return;
        }

        // vTable.stopEditor(graphPanel);

        int rowTable   = vTable.getSelectedRow();
        int rowCurrent = m_mTab.getCurrentRow();

        log.config( "(" + m_mTab.toString() + ") Row in Table=" + rowTable + ", in Model=" + rowCurrent );

        if( rowTable == -1 )                                                // nothing selected
        {
            if( rowCurrent >= 0 ) {
                vTable.setRowSelectionInterval( rowCurrent,rowCurrent );    // causes this method to be called again

                return;
            }
        } else {
            if( rowTable != rowCurrent ) {
                // En caso de que al querer navegar hacia la fila de selección se retorne
            	// la fila actual, significa que no es posible navegar hacia dicha fila,
            	// con lo cual se modifica la selección de la grilla a la fila actual.
            	if (m_mTab.navigate( rowTable ) == rowCurrent)
                	vTable.setRowSelectionInterval(rowCurrent, rowCurrent);
            }

            dynamicDisplay( 0 );
        }

        // TreeNavigation - Synchronize    -- select node in tree

        if( m_tree != null ) {
            m_tree.setSelectedNode( m_mTab.getRecord_ID());    // ignores new (-1)
        }

        // log.config( "GridController.valueChanged (" + m_mTab.toString() + ") - fini",
        // "Row in Table=" + rowTable + ", in Model=" + rowCurrent);

        // Query Included Tab

        if( vIncludedGC != null && (!m_mTab.isInserting() || m_mTab.getCurrentRow() == 0)) {
        	vIncludedGC.getMTab().query( false,0 );
        }
    }    // valueChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void propertyChange( PropertyChangeEvent e ) {

        // System.out.println("propertyChange");
        // System.out.println(e);

        if( e == null ) {
            return;
        }

        Object value = e.getNewValue();

        if( value == null ) {
            return;
        }

        log.config( e.getPropertyName() + "=" + value + " - " + value.getClass().toString());

        if( !( value instanceof MTreeNode )) {
            return;
        }

        // We Have a TreeNode

        int nodeID = (( MTreeNode )value ).getNode_ID();

        // root of tree selected - ignore

        if( nodeID == 0 ) {
            return;
        }

        // Search all rows for mode id

        int size = m_mTab.getRowCount();
        int row  = -1;

        for( int i = 0;i < size;i++ ) {
            if( m_mTab.getKeyID( i ) == nodeID ) {
                row = i;

                break;
            }
        }

        if( row == -1 ) {
            log.log( Level.SEVERE,"Tab does not have ID with Node_ID=" + nodeID );

            return;
        }

        // Navigate to node row

        m_mTab.navigate( row );
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param col
     */

    public void dynamicDisplay( int col ) {

        // log.config( "GridController.dynamicDisplay (" + m_mTab.toString() + ") SingleRow=" + isSingleRow() + ", OnlyMultiRow=" + m_onlyMultiRow);
        // Don't update if multi-row

        if( !isSingleRow() || m_onlyMultiRow ) {
            return;
        }

        if( !m_mTab.isOpen()) {
            return;
        }

        // Selective

        if( col != 0 ) {
            MField    changedField = m_mTab.getField( col );
            String    columnName   = changedField.getColumnName();
            ArrayList dependants   = m_mTab.getDependantList( columnName );

            log.config( "(" + m_mTab.toString() + ") " + columnName + " - Dependents=" + dependants.size());

            // No Dependents and no Callout - Set just Background

            if( (dependants.size() == 0) && (changedField.getCallout().length() > 0) ) {
                Component[] comp = vPanel.getComponents();

                for( int i = 0;i < comp.length;i++ ) {
                    if( columnName.equals( comp[ i ].getName()) && (comp[ i ] instanceof VEditor) ) {
                        VEditor ve         = ( VEditor )comp[ i ];
                        boolean manMissing = false;
                        boolean noValue    = (changedField.getValue() == null) || (changedField.getValue().toString().length() == 0);

                        if( noValue && changedField.isEditable( true ) && changedField.isMandatory( true )) {    // check context
                            manMissing = true;
                        }

                        ve.setBackground( manMissing || changedField.isError());

                        break;
                    }
                }

                return;
            }
        }    // selective

        // complete single row re-display

        log.config( "(" + m_mTab.toString() + ")" );

        // All Components in vPanel (Single Row)
        
        updateComponents(vPanel.getComponents(), m_mTab.getMapFields(), true, true, true);
        
        log.config( "(" + m_mTab.toString() + ") - fini - " + ( (col == 0)
                ?"complete"
                :"seletive" ));
    }    // dynamicDisplay

    /**
     * Descripción de Método
     *
     *
     * @param save
     * @param keyID
     */

    public void rowChanged( boolean save,int keyID ) {
        if( m_tree == null ) {
            return;
        }

        String  name           = ( String )m_mTab.getValue( "Name" );
        String  description    = ( String )m_mTab.getValue( "Description" );
        Boolean IsSummary      = ( Boolean )m_mTab.getValue( "IsSummary" );
        String  imageIndicator = ( String )m_mTab.getValue( "Action" );    // Menu - Action

        //

        m_tree.nodeChanged( save,keyID,name,description,IsSummary.booleanValue(),imageIndicator );
    }    // rowChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     *
     * @throws PropertyVetoException
     */

    public void vetoableChange( PropertyChangeEvent e ) throws PropertyVetoException {
        log.config( "En vetoableChange(" + m_mTab.toString() + ") " + e.getPropertyName() + "=" + e.getNewValue() + " (" + e.getOldValue() + ") " + ( (e.getOldValue() == null)
                ?""
                :e.getOldValue().getClass().getName()));

        // Save Confirmation dialog    MTable-RowSave

        if( e.getPropertyName().equals( MTable.PROPERTY )) {

            // throw new PropertyVetoException calls this method (??) again

            if( m_vetoActive ) {
                m_vetoActive = false;

                return;
            }

            if( !Env.isAutoCommit( Env.getCtx(),m_WindowNo ) || (m_mTab.getCommitWarning().length() > 0) ) {
                if( !ADialog.ask( m_WindowNo,this,"SaveChanges?",m_mTab.getCommitWarning())) {
                    m_vetoActive = true;

                    throw new PropertyVetoException( "UserDeniedSave",e );
                }
            }

            return;
        }    // saveConfirmation

        // Get Row/Col Info

        MTable mTable = m_mTab.getTableModel();
        int    row    = m_mTab.getCurrentRow();
        int    col    = mTable.findColumn( e.getPropertyName());

        //

        // Modificado por Disytel - Franco Bonafine
        // Cuando un editor devuelve null, la ejecución entra por esta rama del if
        // haciendo que no se guarde el valor null en el MTable. Esto produce que
        // al asignar un null a un editor, y luego guardar los cambios (desde una ventana)
        // el APanel dispare el mensaje "No Guardado", dado que no se detectan
        // modificaciones en la fila.
        // Por esto, por mas que el valor sea null, se debe guardar el valor en la MTable.
        
        // ****************
        // Codigo Original
        //if((e.getNewValue() == null) && (e.getOldValue() != null) && (e.getOldValue().toString().length() > 0) ) {    // some editors return "" instead of null
          //  mTable.setChanged( true );
        //} else {
        //*****************

            //mTable.setValueAt (e.getNewValue(), row, col, true);

            mTable.setValueAt( e.getNewValue(),row,col);    // -> dataStatusChanged -> dynamicDisplay


            // Force Callout

            if( e.getPropertyName().equals( "S_ResourceAssignment_ID" )) {
                MField mField = m_mTab.getField( col );

                if( (mField != null) /*&& (mField.getCallout().length() > 0)*/ ) {
                    m_mTab.processFieldChange( mField );    // Dependencies & Callout
                }
            }
        //}

        // log.config( "GridController.vetoableChange (" + m_mTab.toString() + ") - fini", e.getPropertyName() + "=" + e.getNewValue());

    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTab getMTab() {
        return m_mTab;
    }    // getMTab

    /**
     * Descripción de Método
     *
     *
     * @param table
     * @param index
     *
     * @return
     */

    protected TableColumn getTableColumn( VTable table,int index ) {

        // log.fine( "GridController.getTableColumn", "index=" + index);

        TableColumnModel tcm = table.getColumnModel();

        if( (index >= 0) && (index < tcm.getColumnCount())) {
            return tcm.getColumn( index );
        }

        log.log( Level.SEVERE,"No TableColumn for index=" + index );

        return null;
    }    // getTableColumn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public VTable getTable() {
        return vTable;
    }    // getTable

    /**
     * Descripción de Método
     *
     *
     * @param saveValue
     */

    public void stopEditor( boolean saveValue ) {
        log.config( "(" + m_mTab.toString() + ") TableEditing=" + vTable.isEditing());

        // MultiRow - remove editors

        vTable.stopEditor( saveValue );

        // SingleRow - stop editors by changing focus

        if( m_singleRow ) {
            vPanel.transferFocus();
        }

        // graphPanel.requestFocus();
        //
        // log.config( "GridController.stopEditor (" + m_mTab.toString() + ") - fini",
        // "Editing=" + vTable.isEditing());

    }    // stopEditors

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        log.finest( "." + this + " - " + e );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {
        log.finest( "" + this + " - " + e );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {
        log.finest( ".." + this + " - " + e );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {
        log.finest( "..." + this + " - " + e );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {
        log.finest( "...." + this + " - " + e );
    }
    
    public void refreshIncludedGC() {
    	if (vIncludedGC != null)
    		vIncludedGC.getMTab().dataRefreshAll();
    }
    
    
    public static void updateComponents(Component[] comp, Map<String, MField> fields, boolean checkContextForAccess, boolean checkContextForDisplayed, boolean checkContextForReadOnlyLogic){
    	
        for( int i = 0;i < comp.length;i++ ) {
            String columnName = comp[ i ].getName();

            if( columnName != null ) {
                MField mField = fields.get(columnName);

                if( mField != null ) {
                    if( mField.isDisplayed( checkContextForDisplayed ))                    // check context
                    {
                        if( !comp[ i ].isVisible()) {
                            comp[ i ].setVisible( true );              // visibility
                        }

                        if( comp[ i ] instanceof VEditor ) {
                            VEditor ve = ( VEditor )comp[ i ];
                            boolean rw = mField.isEditable( checkContextForAccess, checkContextForReadOnlyLogic, checkContextForDisplayed );    // r/w - check Context

                            ve.setReadWrite( rw );

                            // Log.trace(7, "GridController.dynamicDisplay RW=" + rw, mField);

                            boolean manMissing = false;

                            // least expensive operations first        //  missing mandatory

                            if( rw && (mField.getValue() == null) && mField.isMandatory( true )) {    // check context
                                manMissing = true;
                            }

                            ve.setBackground( manMissing || mField.isError());
                        }
                    } else if( comp[ i ].isVisible()) {
                        comp[ i ].setVisible( false );
                    }
                }
            }
        }    // all components
    }
    
    public String getClipboardData(){
    	int size = m_mTab.getFieldCount();
    	int currentRow = m_mTab.getCurrentRow();
    	
    	StringBuffer header = new StringBuffer();
    	StringBuffer body = new StringBuffer();
    	StringBuffer data = new StringBuffer();
    	
		boolean justCreatedHeader = false;
    	MField mField;
    	String value;
    	for( int row = 0;row < m_mTab.getRowCount() ;row++ ) {
	        for( int i = 0;i < size;i++ ) {
	            mField = m_mTab.getField(i);
	            
	            if( mField.isDisplayed()) {
	            	if(!justCreatedHeader){
	            		header.append(mField.getHeader()).append("\t");
	            	}
					fieldEditors.get(mField).setValue(
							m_mTab.getValue(row, mField.getColumnName()));
					value = fieldEditors.get(mField).getValue() != null ? fieldEditors
							.get(mField).getDisplay() : "";
	            	body.append(value).append("\t");
	            }
	        }
	        justCreatedHeader = true;
            body.append("\n");
    	}
    	
    	// Restaurar datos de la fila actual
    	for( int i = 0;i < size;i++ ) {
    		mField = m_mTab.getField(i);
    		if( mField.isDisplayed()) {
				fieldEditors.get(mField).setValue(
						m_mTab.getValue(currentRow, mField.getColumnName()));
    		}
    	}
    	
        data.append(header).append("\n").append(body);
    	return data.toString();
    }
}    // GridController



/*
 *  @(#)GridController.java   02.07.07
 * 
 *  Fin del fichero GridController.java
 *  
 *  Versión 2.2
 *
 */
