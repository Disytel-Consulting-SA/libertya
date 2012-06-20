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



package org.openXpertya.grid.tree;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public final class VTreePanel extends CPanel implements ActionListener,DragGestureListener,DragSourceListener,DropTargetListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param WindowNo
     * @param hasBar
     * @param editable
     */

    public VTreePanel( int WindowNo,boolean hasBar,boolean editable ) {
        super();
        log.config( "Bar=" + hasBar + ", Editable=" + editable );
        m_WindowNo = WindowNo;
        m_hasBar   = hasBar;
        m_editable = editable;

        // static init

        jbInit();

        if( !hasBar ) {
            bar.setPreferredSize( new Dimension( 0,0 ));
            centerSplitPane.setDividerLocation( 0 );
            centerSplitPane.setDividerSize( 0 );
            popMenuTree.remove( mBarAdd );
        } else {
            centerSplitPane.setDividerLocation( 80 );
        }

        // base settings

        if( editable ) {
            tree.setDropTarget( dropTarget );
        } else {
            popMenuTree.remove( mFrom );
            popMenuTree.remove( mTo );
        }
    }    // VTreePanel

    /**
     * Descripción de Método
     *
     *
     * @param AD_Tree_ID
     *
     * @return
     */

    public boolean initTree( int AD_Tree_ID ) {
        log.config( "AD_Tree_ID=" + AD_Tree_ID );

        //

        m_AD_Tree_ID = AD_Tree_ID;

        // Get Tree

        MTree vTree = new MTree( Env.getCtx(),AD_Tree_ID,m_editable,true,null );

        m_root = vTree.getRoot();
        log.config( "root=" + m_root );
        m_nodeTableName = vTree.getNodeTableName();
        treeModel       = new DefaultTreeModel( m_root,true );
        tree.setModel( treeModel );

        // Shortcut Bar

        if( m_hasBar ) {
            bar.removeAll();    // remove all existing buttons

            Enumeration en = m_root.preorderEnumeration();

            while( en.hasMoreElements()) {
                MTreeNode nd = ( MTreeNode )en.nextElement();

                if( nd.isOnBar()) {
                    addToBar( nd );
                }
            }
        }

        return true;
    }    // initTree

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VTreePanel.class );

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

//      private JTree tree = new JTree();

    /** Descripción de Campos */

    private MyTree tree = new MyTree();    // Gracias a Red1 y Germancheung (Hecho por Conserti)

    /** Descripción de Campos */

    private DefaultTreeModel treeModel;

    /** Descripción de Campos */

    private DefaultTreeSelectionModel treeSelect = new DefaultTreeSelectionModel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private CCheckBox treeExpand = new CCheckBox();

    /** Descripción de Campos */

    private CTextField treeSearch = new CTextField( 10 );

    /** Descripción de Campos */

    private JPopupMenu popMenuTree = new JPopupMenu();

    /** Descripción de Campos */

    private JPopupMenu popMenuBar = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mFrom = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mTo = new JMenuItem();

    /** Descripción de Campos */

    private CPanel bar = new CPanel();

    /** Descripción de Campos */

    private JMenuItem mBarAdd = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mBarRemove = new JMenuItem();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private JSplitPane centerSplitPane = new JSplitPane();

    /** Descripción de Campos */

    private JScrollPane treePane = new JScrollPane();

    /** Descripción de Campos */

    private MouseListener mouseListener = new VTreePanel_mouseAdapter( this );

    /** Descripción de Campos */

    private KeyListener keyListener = new VTreePanel_keyAdapter( this );

    //

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private int m_AD_Tree_ID = 0;

    /** Descripción de Campos */

    private String m_nodeTableName = null;

    /** Descripción de Campos */

    private boolean m_editable;

    /** Descripción de Campos */

    private boolean m_hasBar;

    /** Descripción de Campos */

    private MTreeNode m_root = null;

    /** Descripción de Campos */

    private MTreeNode m_moveNode;    // the node to move

    /** Descripción de Campos */

    private String m_search = "";

    /** Descripción de Campos */

    private Enumeration m_nodeEn;

    /** Descripción de Campos */

    private MTreeNode m_selectedNode;    // the selected model node

    /** Descripción de Campos */

    private CButton m_buttonSelected;

    // Property Listener

    /** Descripción de Campos */

    public static final String NODE_SELECTION = "NodeSelected";

    /**
     * Descripción de Método
     *
     */

    private void jbInit() {
        this.setLayout( mainLayout );
        mainLayout.setVgap( 5 );

        //
        // only one node to be selected

        treeSelect.setSelectionMode( DefaultTreeSelectionModel.SINGLE_TREE_SELECTION );
        tree.setSelectionModel( treeSelect );

        //

        tree.setEditable( false );    // allows to change the text
        tree.addMouseListener( mouseListener );
        tree.addKeyListener( keyListener );
        tree.setCellRenderer( new VTreeCellRenderer());
        treePane.getViewport().add( tree,null );

//              treePane.setPreferredSize(new Dimension(50,200));
//              tree.setPreferredSize(new Dimension(100,150));
        //

        treeExpand.setText( "Expand" );
        treeExpand.setActionCommand( "Expand" );
        treeExpand.addMouseListener( mouseListener );
        treeExpand.addActionListener( this );
        treeSearch.setBackground( CompierePLAF.getInfoBackground());
        treeSearch.addKeyListener( keyListener );
        southPanel.setLayout( southLayout );
        southPanel.add( treeExpand,BorderLayout.WEST );
        southPanel.add( treeSearch,BorderLayout.EAST );
        this.add( southPanel,BorderLayout.SOUTH );

        //

        centerSplitPane.add( treePane,JSplitPane.RIGHT );
        centerSplitPane.add( bar,JSplitPane.LEFT );
        this.add( centerSplitPane,BorderLayout.CENTER );

        //

        mFrom.setText( "From." );
        mFrom.setActionCommand( "From" );
        mFrom.addActionListener( this );
        mTo.setEnabled( false );
        mTo.setText( "To." );
        mTo.setActionCommand( "To" );
        mTo.addActionListener( this );

        //

        bar.setLayout( new BoxLayout( bar,BoxLayout.Y_AXIS ));
        bar.setMinimumSize( new Dimension( 50,50 ));
        mBarAdd.setText( "BarAdd." );
        mBarAdd.setActionCommand( "BarAdd" );
        mBarAdd.addActionListener( this );
        mBarRemove.setText( "BarRemove." );
        mBarRemove.setActionCommand( "BarRemove" );
        mBarRemove.addActionListener( this );

        //

        popMenuTree.setLightWeightPopupEnabled( false );
        popMenuTree.add( mBarAdd );
        popMenuTree.addSeparator();
        popMenuTree.add( mFrom );
        popMenuTree.add( mTo );
        popMenuBar.setLightWeightPopupEnabled( false );
        popMenuBar.add( mBarRemove );

        // translation

        treeExpand.setText( Msg.getMsg( Env.getCtx(),"ExpandTree" ));
        treeSearch.setToolTipText( Msg.getMsg( Env.getCtx(),"EnterSearchText" ));
        mBarAdd.setText( Msg.getMsg( Env.getCtx(),"BarAdd" ));
        mBarRemove.setText( Msg.getMsg( Env.getCtx(),"BarRemove" ));
        mFrom.setText( Msg.getMsg( Env.getCtx(),"ItemMove" ));
        mTo.setText( Msg.getMsg( Env.getCtx(),"ItemInsert" ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param location
     */

    public void setDividerLocation( int location ) {
        centerSplitPane.setDividerLocation( location );
    }    // setDividerLocation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDividerLocation() {
        return centerSplitPane.getDividerLocation();
    }    // getDividerLocation

    /** Descripción de Campos */

    protected DragSource dragSource = DragSource.getDefaultDragSource();

    /** Descripción de Campos */

    protected DropTarget dropTarget = new DropTarget( tree,DnDConstants.ACTION_MOVE,this,true,null );

    /** Descripción de Campos */

    protected DragGestureRecognizer recognizer = dragSource.createDefaultDragGestureRecognizer( tree,DnDConstants.ACTION_MOVE,this );

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragGestureRecognized( DragGestureEvent e ) {
        if( !m_editable ) {
            return;
        }

        //

        try {
            m_moveNode = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();
        } catch( Exception ex )    // nothing selected
        {
            return;
        }

        // start moving

        StringSelection content = new StringSelection( m_moveNode.toString());

        e.startDrag( DragSource.DefaultMoveDrop,    // cursor
            content,                                // Transferable
                this );
        log.fine( "Drag: " + m_moveNode.toString());
    }                                               // dragGestureRecognized

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragDropEnd( DragSourceDropEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragEnter( DragSourceDragEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragExit( DragSourceEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragOver( DragSourceDragEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dropActionChanged( DragSourceDragEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragEnter( DropTargetDragEvent e ) {
        e.acceptDrag( DnDConstants.ACTION_MOVE );
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dropActionChanged( DropTargetDragEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragExit( DropTargetEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dragOver( DropTargetDragEvent e ) {
        Point    mouseLoc = e.getLocation();    // where are we?
        TreePath path     = tree.getClosestPathForLocation( mouseLoc.x,mouseLoc.y );

        tree.setSelectionPath( path );    // show it by selecting

        MTreeNode toNode = ( MTreeNode )path.getLastPathComponent();

        //
        // log.fine( "Move: " + toNode);

        if( (m_moveNode == null              // nothing to move
                ) || (toNode == null) ) {    // nothing to drop on
            e.rejectDrag();
        } else {
            e.acceptDrag( DnDConstants.ACTION_MOVE );
        }
    }                                        // dragOver

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void drop( DropTargetDropEvent e ) {
        Point    mouseLoc = e.getLocation();    // where are we?
        TreePath path     = tree.getClosestPathForLocation( mouseLoc.x,mouseLoc.y );

        tree.setSelectionPath( path );    // show it by selecting

        MTreeNode toNode = ( MTreeNode )path.getLastPathComponent();

        //

        log.fine( "Drop: " + toNode );

        if( (m_moveNode == null            // nothing to move
                ) || (toNode == null) )    // nothing to drop on
                {
            e.rejectDrop();

            return;
        }

        //

        e.acceptDrop( DnDConstants.ACTION_MOVE );
        moveNode( m_moveNode,toNode );
        e.dropComplete( true );
        m_moveNode = null;
    }    // drop

    /**
     * Descripción de Método
     *
     *
     * @param movingNode
     * @param toNode
     */

    private void moveNode( MTreeNode movingNode,MTreeNode toNode ) {
        log.info( movingNode.toString() + " to " + toNode.toString());

        if( movingNode == toNode ) {
            return;
        }

        // remove

        MTreeNode oldParent = ( MTreeNode )movingNode.getParent();

        movingNode.removeFromParent();
        treeModel.nodeStructureChanged( oldParent );

        // insert

        MTreeNode newParent;
        int       index;

        if( !toNode.isSummary())                             // drop on a child node
        {
            newParent = ( MTreeNode )toNode.getParent();
            index     = newParent.getIndex( toNode ) + 1;    // the next node
        } else                                               // drop on a summary node
        {
            newParent = toNode;
            index     = 0;                                   // the first node
        }

        newParent.insert( movingNode,index );
        treeModel.nodeStructureChanged( newParent );

        // ***     Save changes to disk

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        Trx trx = Trx.get( "VTreePanel",true );

        try {
            Statement stmt = trx.getConnection().createStatement();

            // START TRANSACTION   **************
            
            /* Tabla tree correspondiente */
            int tableID = DB.getSQLValue(trx.getTrxName(), " SELECT AD_Table_ID FROM AD_Table WHERE tablename ilike '" + m_nodeTableName + "'");
            M_Table table = M_Table.get(Env.getCtx(), tableID);

            for( int i = 0;i < oldParent.getChildCount();i++ ) {
                MTreeNode    nd  = ( MTreeNode )oldParent.getChildAt( i );
                
                /* Recuperar los registros correspondientes y almacenar */
                PreparedStatement pstmt = DB.prepareStatement("SELECT * FROM " + m_nodeTableName + " WHERE AD_Tree_ID = " + m_AD_Tree_ID + " AND Node_ID = " + nd.getNode_ID(), trx.getTrxName());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                	PO nodePO = table.getPO(rs, trx.getTrxName());	
                	nodePO.set_Value("Parent_ID", oldParent.getNode_ID());
                	nodePO.set_Value("SeqNo", new BigDecimal(i));
                	nodePO.save();
                }
            }

            if( oldParent != newParent ) {
                for( int i = 0;i < newParent.getChildCount();i++ ) {
                    MTreeNode    nd  = ( MTreeNode )newParent.getChildAt( i );

                    /* Recupear los registros correspondientes y almacenar */
                    PreparedStatement pstmt = DB.prepareStatement("SELECT * FROM " + m_nodeTableName + " WHERE AD_Tree_ID = " + m_AD_Tree_ID + " AND Node_ID = " + nd.getNode_ID(), trx.getTrxName());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next())
                    {
                    	PO nodePO = table.getPO(rs, trx.getTrxName());	
                    	nodePO.set_Value("Parent_ID", newParent.getNode_ID());
                    	nodePO.set_Value("SeqNo", new BigDecimal(i));
                    	nodePO.save();
                    }

                }
            }

            // COMMIT          *********************

            trx.commit();
            stmt.close();
        } catch( SQLException e ) {
            trx.rollback();
            log.log( Level.SEVERE,"move",e );
            ADialog.error( m_WindowNo,this,"TreeUpdateError",e.getLocalizedMessage());
        }

        trx.close();
        trx = null;
        setCursor( Cursor.getDefaultCursor());
        log.config( "complete" );
    }    // moveNode

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void keyPressed( KeyEvent e ) {

        // *** Tree ***

        if( (e.getSource() instanceof JTree) || ( (e.getSource() == treeSearch) && (e.getModifiers() != 0) ) )    // InputEvent.CTRL_MASK
        {
            TreePath tp = tree.getSelectionPath();

            if( tp == null ) {
                ADialog.beep();
            } else {
                MTreeNode tn = ( MTreeNode )tp.getLastPathComponent();

                setSelectedNode( tn );
            }
        }

        // *** treeSearch ***

        else if( e.getSource() == treeSearch ) {
            String  search = treeSearch.getText();
            boolean found  = false;

            // at the end - try from top

            if( (m_nodeEn != null) &&!m_nodeEn.hasMoreElements()) {
                m_search = "";
            }

            // this is the first time

            if( !search.equals( m_search )) {

                // get enumeration of all nodes

                m_nodeEn = m_root.preorderEnumeration();
                m_search = search;
            }

            // search the nodes

            while( !found && (m_nodeEn != null) && m_nodeEn.hasMoreElements()) {
                MTreeNode nd = ( MTreeNode )m_nodeEn.nextElement();

                // compare in upper case

                if( nd.toString().toUpperCase().indexOf( search.toUpperCase()) != -1 ) {
                    found = true;

                    TreePath treePath = new TreePath( nd.getPath());

                    tree.setSelectionPath( treePath );
                    tree.makeVisible( treePath );    // expand it
                    tree.scrollPathToVisible( treePath );
                }
            }

            if( !found ) {
                ADialog.beep();
            }
        }                                            // treeSearch
    }                                                // keyPressed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void mouseClicked( MouseEvent e ) {

        // *** JTree ***

        if( e.getSource() instanceof JTree ) {

            // Left Double Click
        	// Modificado por Dataware 31/05/2006 
        	// Antigua Linea:
        	// if( SwingUtilities.isLeftMouseButton( e ) && (e.getClickCount() > 0) ) {
            if( SwingUtilities.isLeftMouseButton( e ) && (e.getClickCount() > 1) ) {
                int selRow = tree.getRowForLocation( e.getX(),e.getY());

                if( selRow != -1 ) {
                    MTreeNode tn = ( MTreeNode )tree.getPathForLocation( e.getX(),e.getY()).getLastPathComponent();

                    setSelectedNode( tn );
                }
            }

            // Right Click for PopUp

            else if(( m_editable || m_hasBar ) && SwingUtilities.isRightMouseButton( e ) && (tree.getSelectionPath() != null) )    // need select first
            {
                MTreeNode nd = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();

                // if (nd.isLeaf())                    //  only leaves

                {
                    Rectangle r = tree.getPathBounds( tree.getSelectionPath());

                    popMenuTree.show( tree,( int )r.getMaxX(),( int )r.getY());
                }
            }
        }    // JTree

        // *** JButton ***

        else if( e.getSource() instanceof JButton ) {
            if( SwingUtilities.isRightMouseButton( e )) {
                m_buttonSelected = ( CButton )e.getSource();
                popMenuBar.show( m_buttonSelected,e.getX(),e.getY());
            }
        }    // JButton
    }        // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTreeNode getSelectedNode() {
        return m_selectedNode;
    }    // getSelectedNode

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public JComponent getSearchField() {
        return treeSearch;
    }    // getSearchField

    /**
     * Descripción de Método
     *
     *
     * @param nodeID
     *
     * @return
     */

    public boolean setSelectedNode( int nodeID ) {
        log.config( "ID=" + nodeID );

        if( nodeID != -1 ) {                   // new is -1
            return selectID( nodeID,true );    // show selection
        }

        return false;
    }    // setSelectedNode

    /**
     * Descripción de Método
     *
     *
     * @param nodeID
     * @param show
     *
     * @return
     */

    private boolean selectID( int nodeID,boolean show ) {
        if( m_root == null ) {
            return false;
        }

        log.config( "NodeID=" + nodeID + ", Show=" + show + ", root=" + m_root );

        // try to find the node

        MTreeNode node = m_root.findNode( nodeID );

        if( node != null ) {
            TreePath treePath = new TreePath( node.getPath());

            log.config( "Node=" + node + ", Path=" + treePath.toString());
            tree.setSelectionPath( treePath );

            if( show ) {
                tree.makeVisible( treePath );    // expand it
                tree.scrollPathToVisible( treePath );
            }

            return true;
        }

        log.info( "Node not found; ID=" + nodeID );

        return false;
    }    // selectID

    /**
     * Descripción de Método
     *
     *
     * @param nd
     */

    private void setSelectedNode( MTreeNode nd ) {
        log.config( "Node = " + nd );
        m_selectedNode = nd;

        //

        firePropertyChange( NODE_SELECTION,null,nd );
    }    // setSelectedNode

    /**
     * Descripción de Método
     *
     *
     * @param save
     * @param keyID
     * @param name
     * @param description
     * @param isSummary
     * @param imageIndicator
     */

    public void nodeChanged( boolean save,int keyID,String name,String description,boolean isSummary,String imageIndicator ) {
        log.config( "Save=" + save + ", KeyID=" + keyID + ", Name=" + name + ", Description=" + description + ", IsSummary=" + isSummary + ", ImageInd=" + imageIndicator + ", root=" + m_root );

        // if ID==0=root - don't update it

        if( keyID == 0 ) {
            return;
        }

        // try to find the node

        MTreeNode node = m_root.findNode( keyID );

        // Node not found and saved -> new

        if( (node == null) && save ) {
            node = new MTreeNode( keyID,0,name,description,m_root.getNode_ID(),isSummary,imageIndicator,false,null );
            m_root.add( node );
        }

        // Node found and saved -> change

        else if( (node != null) && save ) {
            node.setName( name );
            node.setAllowsChildren( isSummary );
        }

        // Node found and not saved -> delete

        else if( (node != null) &&!save ) {
            MTreeNode parent = ( MTreeNode )node.getParent();

            node.removeFromParent();
            node = parent;    // select Parent
        }

        // Error

        else {
            log.log( Level.SEVERE,"Save=" + save + ", KeyID=" + keyID + ", Node=" + node );
            node = null;
        }

        // Nothing to display

        if( node == null ) {
            return;
        }

        // (Re) Display Node

        tree.updateUI();

        TreePath treePath = new TreePath( node.getPath());

        tree.setSelectionPath( treePath );
        tree.makeVisible( treePath );    // expand it
        tree.scrollPathToVisible( treePath );
    }                                    // nodeChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // bar button pressed

        if( e.getSource() instanceof JButton ) {

            // Find Node - don't show

            selectID( Integer.parseInt( e.getActionCommand()),false );

            // Select it

            MTreeNode tn = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();

            setSelectedNode( tn );
        }

        // popup menu commands

        else if( e.getSource() instanceof JMenuItem ) {
            if( e.getActionCommand().equals( "From" )) {
                moveFrom();
            } else if( e.getActionCommand().equals( "To" )) {
                moveTo();
            } else if( e.getActionCommand().equals( "BarAdd" )) {
                barAdd();
            } else if( e.getActionCommand().equals( "BarRemove" )) {
                barRemove();
            }
        } else if( e.getSource() instanceof JCheckBox ) {
            if( e.getActionCommand().equals( "Expand" )) {
                expandTree();
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void moveFrom() {
        m_moveNode = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();

        if( m_moveNode != null ) {
            mTo.setEnabled( true );    // enable menu
        }
    }                                  // mFrom_actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void moveTo() {
        mFrom.setEnabled( true );
        mTo.setEnabled( false );

        if( m_moveNode == null ) {
            return;
        }

        MTreeNode toNode = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();

        moveNode( m_moveNode,toNode );

        // cleanup

        m_moveNode = null;
    }    // mTo_actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void barAdd() {
        MTreeNode nd = ( MTreeNode )tree.getSelectionPath().getLastPathComponent();

        if( barDBupdate( true,nd.getNode_ID())) {
            addToBar( nd );
        }
    }    // barAdd

    /**
     * Descripción de Método
     *
     *
     * @param nd
     */

    private void addToBar( MTreeNode nd ) {

        // Only first word of Label

        String label = nd.toString().trim();
        int    space = label.indexOf( " " );

        // if (space != -1)
        // label = label.substring(0, space);

        CButton button = new CButton( label );    // Create the button

        button.setToolTipText( nd.getDescription());
        button.setActionCommand( String.valueOf( nd.getNode_ID()));

        //

        button.setMargin( new Insets( 0,0,0,0 ));
        button.setIcon( nd.getIcon());
        button.setBorderPainted( false );

        
        
        Font f = CompierePLAF.getFont_Label();
        f=f.deriveFont(Font.BOLD ,f.getSize()+4);
        button.setFont(f);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
        button.setBorderPainted(false);
        button.setBorder(null);
        
        
        
        
        // button.setFocusPainted(false);

        button.setRequestFocusEnabled( false );

        //

        button.addActionListener( this );
        button.addMouseListener( mouseListener );

        //

        bar.add( button );
        bar.validate();

        if( centerSplitPane.getDividerLocation() == -1 ) {
            centerSplitPane.setDividerLocation( button.getPreferredSize().width );
        }

        bar.repaint();
    }    // addToBar

    /**
     * Descripción de Método
     *
     */

    private void barRemove() {
        bar.remove( m_buttonSelected );
        bar.validate();
        bar.repaint();
        barDBupdate( false,Integer.parseInt( m_buttonSelected.getActionCommand()));
    }    // barRemove

    /**
     * Descripción de Método
     *
     *
     * @param add
     * @param Node_ID
     *
     * @return
     */

    private boolean barDBupdate( boolean add,int Node_ID ) {
        int          AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
        int          AD_Org_ID    = Env.getContextAsInt( Env.getCtx(),"#AD_Org_ID" );
        int          AD_User_ID   = Env.getContextAsInt( Env.getCtx(),"#AD_User_ID" );
        StringBuffer sql          = new StringBuffer();
        VDate dateField = new VDate( DisplayType.Date );
        dateField.setValue( new Timestamp( System.currentTimeMillis()));
        
        if( add ) {
        //// begin JRBV - Dataware - BugNo: 255
        	sql.append(
        	"INSERT INTO AD_TreeBar (" ).append(  
			   	"AD_Tree_ID, " ).append( 
			    "AD_User_ID, " ).append(  
			    "Node_ID, " ).append(  
			   	"AD_Client_ID, " ).append( 
			   	"AD_Org_ID, " ).append(  
	   			"IsActive, " ).append( 
	   			"CreatedBy, " ).append( 
	   			"UpdatedBy " ).append( 
			")VALUES (" ).append( 
	   			m_AD_Tree_ID ).append(  ", " ).append( 
	   			AD_User_ID ).append(  ", " ).append( 
	   			Node_ID ).append(  ", " ).append( 
	   			AD_Client_ID ).append(  ", " ).append( 
	   			AD_Org_ID ).append(  ", " ).append( 
	   			"'Y', " ).append(
	   			AD_User_ID ).append(  ", " ).append( 	//CreatedBy
	   			AD_User_ID ).append(  ")" 

	   			////end JRBV - Dataware - BugNo: 255
	   					
           ); //UpdatedBy

            // if already exist, will result in ORA-00001: unique constraint (COMPIERE.AD_TREEBAR_KEY)

        } else {
            sql.append( "DELETE AD_TreeBar WHERE AD_Tree_ID=" ).append( m_AD_Tree_ID ).append( " AND AD_User_ID=" ).append( AD_User_ID ).append( " AND Node_ID=" ).append( Node_ID );
        }

        int no = DB.executeUpdate( sql.toString(),true );

        return no == 1;
    }    // barDBupdate

    /**
     * Descripción de Método
     *
     */

    private void expandTree() {
        if( treeExpand.isSelected()) {
            for( int row = 0;row < tree.getRowCount();row++ ) {
                tree.expandRow( row );
            }
        } else {
            for( int row = 0;row < tree.getRowCount();row++ ) {
                tree.collapseRow( row );
            }
        }
    }    // expandTree
}    // VTreePanel


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

class VTreePanel_mouseAdapter extends java.awt.event.MouseAdapter {

    /** Descripción de Campos */

    VTreePanel adaptee;

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VTreePanel_mouseAdapter( VTreePanel adaptee ) {
        this.adaptee = adaptee;
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        adaptee.mouseClicked( e );
    }
}    // VTreePanel_mouseAdapter


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

class VTreePanel_keyAdapter extends java.awt.event.KeyAdapter {

    /** Descripción de Campos */

    VTreePanel adaptee;

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VTreePanel_keyAdapter( VTreePanel adaptee ) {
        this.adaptee = adaptee;
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyPressed( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
            adaptee.keyPressed( e );
        }
    }
}    // VTreePanel_keyAdapter



/*
 *  @(#)VTreePanel.java   02.07.07
 *
 *  Fin del fichero VTreePanel.java
 *
 *  Versión 2.2
 *
 */
