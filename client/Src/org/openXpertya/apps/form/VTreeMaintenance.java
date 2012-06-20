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



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.grid.tree.VTreePanel;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTable;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.model.MTree_Node;
import org.openXpertya.model.MTree_NodeBP;
import org.openXpertya.model.MTree_NodeMM;
import org.openXpertya.model.MTree_NodePR;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.POInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VTreeMaintenance extends CPanel implements FormPanel,ActionListener,ListSelectionListener,PropertyChangeListener {

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private MTree m_tree;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VTreeMaintenance.class );

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private FlowLayout northLayout = new FlowLayout();

    /** Descripción de Campos */

    private CLabel treeLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox treeField;

    /** Descripción de Campos */

    private CButton bAddAll = new CButton( Env.getImageIcon( "FastBack24.gif" ));

    /** Descripción de Campos */

    private CButton bAdd = new CButton( Env.getImageIcon( "StepBack24.gif" ));

    /** Descripción de Campos */

    private CButton bDelete = new CButton( Env.getImageIcon( "StepForward24.gif" ));

    /** Descripción de Campos */

    private CButton bDeleteAll = new CButton( Env.getImageIcon( "FastForward24.gif" ));

    /** Descripción de Campos */

    private CCheckBox cbAllNodes = new CCheckBox();

    /** Descripción de Campos */

    private CLabel treeInfo = new CLabel();

    //

    /** Descripción de Campos */

    private JSplitPane splitPane = new JSplitPane();

    /** Descripción de Campos */

    private VTreePanel centerTree;

    /** Descripción de Campos */

    private JList centerList = new JList();

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        m_WindowNo = WindowNo;
        m_frame    = frame;
        log.info( "VMerge.init - WinNo=" + m_WindowNo );

        try {
            preInit();
            jbInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );

            // frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

            action_loadTree();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VTreeMaintenance.init",ex );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    private void preInit() {
        KeyNamePair[] trees = DB.getKeyNamePairs( MRole.getDefault().addAccessSQL( "SELECT AD_Tree_ID, Name FROM AD_Tree WHERE TreeType NOT IN ('BB','PC') ORDER BY 2","AD_Tree",MRole.SQL_NOTQUALIFIED,MRole.SQL_RW ),false );

        treeField = new CComboBox( trees );
        treeField.addActionListener( this );

        //

        centerTree = new VTreePanel( m_WindowNo,false,true );
        centerTree.addPropertyChangeListener( VTreePanel.NODE_SELECTION,this );
        
    }    // preInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        treeLabel.setText( Msg.translate( Env.getCtx(),"AD_Tree_ID" ));
        cbAllNodes.setEnabled( false );
        cbAllNodes.setText( Msg.translate( Env.getCtx(),"IsAllNodes" ));
        treeInfo.setText( " " );
        bAdd.setToolTipText( "Add to Tree" );
        bAddAll.setToolTipText( "Add ALL to Tree" );
        bDelete.setToolTipText( "Delete from Tree" );
        bDeleteAll.setToolTipText( "Delete ALL from Tree" );
        bAdd.addActionListener( this );
        bAddAll.addActionListener( this );
        bDelete.addActionListener( this );
        bDeleteAll.addActionListener( this );
        northPanel.setLayout( northLayout );
        northLayout.setAlignment( FlowLayout.LEFT );

        //

        this.add( northPanel,BorderLayout.NORTH );
        northPanel.add( treeLabel,null );
        northPanel.add( treeField,null );
        northPanel.add( cbAllNodes,null );
        northPanel.add( treeInfo,null );
        northPanel.add( bAddAll,null );
        northPanel.add( bAdd,null );
        northPanel.add( bDelete,null );
        northPanel.add( bDeleteAll,null );

        //

        this.add( splitPane,BorderLayout.CENTER );
        splitPane.add( centerTree,JSplitPane.LEFT );
        splitPane.add( new JScrollPane( centerList ),JSplitPane.RIGHT );
        centerList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        centerList.addListSelectionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == treeField ) {
            action_loadTree();
        } else if( e.getSource() == bAddAll ) {
            action_treeAddAll();
        } else if( e.getSource() == bAdd ) {
            action_treeAdd(( ListItem )centerList.getSelectedValue());
        } else if( e.getSource() == bDelete ) {
            action_treeDelete(( ListItem )centerList.getSelectedValue());
        } else if( e.getSource() == bDeleteAll ) {
            action_treeDeleteAll();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void action_loadTree() {
        KeyNamePair tree = ( KeyNamePair )treeField.getSelectedItem();

        log.info( "VTreeMaintenance.action_loadTree - " + tree );

        if( tree.getKey() <= 0 ) {
            centerList.setModel( new DefaultListModel());

            return;
        }

        // Tree

        m_tree = new MTree( Env.getCtx(),tree.getKey(),null );
        cbAllNodes.setSelected( m_tree.isAllNodes());
        bAddAll.setEnabled( !m_tree.isAllNodes());
        bAdd.setEnabled( !m_tree.isAllNodes());
        bDelete.setEnabled( !m_tree.isAllNodes());
        bDeleteAll.setEnabled( !m_tree.isAllNodes());

        //

        String fromClause = m_tree.getSourceTableName( false );    // fully qualified
        String columnNameX = m_tree.getSourceTableName( true );
        String actionColor = m_tree.getActionColorName();

        // List
        
        String hasTrlSql = "SELECT IsTranslated FROM AD_Column INNER JOIN AD_Table ON (AD_Table.AD_Table_ID=AD_Column.AD_Table_ID) WHERE TableName = ? AND ColumnName = ? ";
        boolean hasNameTrl = "Y".equals(DB.getSQLObject(null, hasTrlSql, new Object[]{columnNameX, "Name"}));
        boolean hasDescriptionTrl = "Y".equals(DB.getSQLObject(null, hasTrlSql, new Object[]{columnNameX, "Description"}));
        
        String           sql;
        DefaultListModel model = new DefaultListModel();
        if (hasNameTrl || hasDescriptionTrl) {
        	sql = "SELECT t." + columnNameX + "_ID," + (hasNameTrl ? "COALESCE(trl.Name,t.Name)" : "t.Name") + "," + (hasDescriptionTrl ? "COALESCE(trl.Description,t.Description)" : "t.Description") + ",t.IsSummary," + actionColor + ",t.Name,t.Description " + 
        		" FROM " + fromClause +
        		" LEFT JOIN " + columnNameX + "_trl trl ON (t." + columnNameX + "_ID = trl." + columnNameX + "_ID AND trl.AD_Language = ?) " +
        		" ORDER BY 2";
        } else {
        	sql = "SELECT t." + columnNameX + "_ID,t.Name,t.Description,t.IsSummary," + actionColor + " FROM " + fromClause + " ORDER BY 2";
        }
        
        // + " WHERE t.IsActive='Y'"       //      R/O

        sql = MRole.getDefault().addAccessSQL( sql,"t",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );
        log.config( "VTreeMaintenance.action_loadTree - " + sql );

        //

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            if (hasNameTrl || hasDescriptionTrl)
            	pstmt.setString(1, Env.getAD_Language(Env.getCtx()));
            
            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	ListItem item ;
            	if (hasNameTrl || hasDescriptionTrl)
            		item = new ListItem( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 6 ),rs.getString( 3 ),rs.getString( 7 ),"Y".equals( rs.getString( 4 )),rs.getString( 5 ));
            	else
            		item = new ListItem( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 3 ),"Y".equals( rs.getString( 4 )),rs.getString( 5 ));

                model.addElement( item );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VTreeMaintenance.action_loadTree",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // List

        log.config( "VTreeMaintenance.action_loadTree #" + model.getSize());
        centerList.setModel( model );

        // Tree

        centerTree.initTree( m_tree.getAD_Tree_ID());
    }    // action_fillTree

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void valueChanged( ListSelectionEvent e ) {
        if( e.getValueIsAdjusting()) {
            return;
        }

        ListItem selected = null;

        try {    // throws a ArrayIndexOutOfBoundsException if root is selected
            selected = ( ListItem )centerList.getSelectedValue();
        } catch( Exception ex ) {
        }

        log.info( "Selected=" + selected );

        if( selected != null ) {    // allow add if not in tree
            bAdd.setEnabled( !centerTree.setSelectedNode( selected.id ));
        }
    }                               // valueChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void propertyChange( PropertyChangeEvent e ) {
        MTreeNode tn = ( MTreeNode )e.getNewValue();

        log.info( tn.toString());

        if( tn == null ) {
            return;
        }

        ListModel model = centerList.getModel();
        int       size  = model.getSize();
        int       index = -1;

        for( index = 0;index < size;index++ ) {
            ListItem item = ( ListItem )model.getElementAt( index );

            if( item.id == tn.getNode_ID()) {
                break;
            }
        }

        centerList.setSelectedIndex( index );
        centerList.ensureIndexIsVisible( index );
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param item
     */

    private void action_treeAdd( ListItem item ) {
        log.info( "Item=" + item );

        if( item != null ) {
            centerTree.nodeChanged( true,item.id,item.name,item.description,item.isSummary,item.imageIndicator );

            // May cause Error if in tree

            if( m_tree.isProduct()) {
                MTree_NodePR node = new MTree_NodePR( m_tree,item.id );

                node.save();
            } else if( m_tree.isBPartner()) {
                MTree_NodeBP node = new MTree_NodeBP( m_tree,item.id );

                node.save();
            } else if( m_tree.isMenu()) {
                MTree_NodeMM node = new MTree_NodeMM( m_tree,item.id );

                node.save();
            } else {
                MTree_Node node = new MTree_Node( m_tree,item.id );

                node.save();
            }
        }
    }    // action_treeAdd

    /**
     * Descripción de Método
     *
     *
     * @param item
     */

    private void action_treeDelete( ListItem item ) {
        log.info( "Item=" + item );

        if( item != null ) {
            centerTree.nodeChanged( false,item.id,item.name,item.description,item.isSummary,item.imageIndicator );

            //

            if( m_tree.isProduct()) {
                MTree_NodePR node = MTree_NodePR.get( m_tree,item.id );

                if( node != null ) {
                    node.delete( true );
                }
            } else if( m_tree.isBPartner()) {
                MTree_NodeBP node = MTree_NodeBP.get( m_tree,item.id );

                if( node != null ) {
                    node.delete( true );
                }
            } else if( m_tree.isMenu()) {
                MTree_NodeMM node = MTree_NodeMM.get( m_tree,item.id );

                if( node != null ) {
                    node.delete( true );
                }
            } else {
                MTree_Node node = MTree_Node.get( m_tree,item.id );

                if( node != null ) {
                    node.delete( true );
                }
            }
        }
    }    // action_treeDelete

    /**
     * Descripción de Método
     *
     */

    private void action_treeAddAll() {
        log.info( "" );

        ListModel model = centerList.getModel();
        int       size  = model.getSize();
        int       index = -1;

        for( index = 0;index < size;index++ ) {
            ListItem item = ( ListItem )model.getElementAt( index );

            action_treeAdd( item );
        }
    }    // action_treeAddAll

    /**
     * Descripción de Método
     *
     */

    private void action_treeDeleteAll() {
        log.info( "VTreeMaintenance.action_treeDeleteAll" );

        ListModel model = centerList.getModel();
        int       size  = model.getSize();
        int       index = -1;

        for( index = 0;index < size;index++ ) {
            ListItem item = ( ListItem )model.getElementAt( index );

            action_treeDelete( item );
        }
    }    // action_treeDeleteAll

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class ListItem {

        /**
         * Constructor de la clase ...
         *
         *
         * @param id
         * @param name
         * @param description
         * @param isSummary
         * @param imageIndicator
         */

    	public ListItem( int id,String name,String description,boolean isSummary,String imageIndicator ) {
    		this(id, name, null, description, null, isSummary, imageIndicator);
    	}
    	
        public ListItem( int id,String name, String defName,String description, String defDescription,boolean isSummary,String imageIndicator ) {
            this.id             = id;
            this.name           = name;
            this.description    = description;
            this.isSummary      = isSummary;
            this.imageIndicator = imageIndicator;
            this.defname        = defName;
            this.defdescription = defDescription;
        }    // ListItem

        /** Descripción de Campos */

        public int id;

        /** Descripción de Campos */

        public String name;

        /** Descripción de Campos */

        public String description;

        /** Descripción de Campos */

        public String defname;

        /** Descripción de Campos */

        public String defdescription;

        /** Descripción de Campos */

        public boolean isSummary;

        /** Descripción de Campos */

        public String imageIndicator;    // Menu - Action

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public String toString() {
            String retValue = name;

            if( (description != null) && (description.length() > 0) ) {
                retValue += " (" + description + ")";
            }

            if (defname != null && defname.length() > 0)
            	retValue += " - " + defname;
            
            if (defdescription != null)
            	retValue += " (" + defdescription + ")";
            
            return retValue;
        }    // toString
    }    // ListItem
}        // VTreeMaintenance



/*
 *  @(#)VTreeMaintenance.java   02.07.07
 * 
 *  Fin del fichero VTreeMaintenance.java
 *  
 *  Versión 2.2
 *
 */
