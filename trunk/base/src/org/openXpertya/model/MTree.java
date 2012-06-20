/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.sql.RowSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openXpertya.print.MPrintColor;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTree extends MTree_Base {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param id
     * @param trxName
     */

    public MTree( Properties ctx,int id,String trxName ) {
        super( ctx,id,trxName );
    }    // MTree Visual Studio orcas

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Tree_ID
     * @param editable
     * @param clientTree
     * @param trxName
     */

    public MTree( Properties ctx,int AD_Tree_ID,boolean editable,boolean clientTree,String trxName ) {
        this( ctx,AD_Tree_ID,trxName );
        m_editable = editable;

        int AD_User_ID = Env.getContextAsInt( ctx,"AD_User_ID" );

        m_clientTree = clientTree;
        log.info( "AD_Tree_ID=" + AD_Tree_ID + ", AD_User_ID=" + AD_User_ID + ", Editable=" + editable + ", OnClient=" + clientTree );

        //

        loadNodes( AD_User_ID );
    }    // MTree

    /** Descripci�n de Campos */

    private boolean m_editable = false;

    /** Descripci�n de Campos */

    private MTreeNode m_root = null;

    /** Descripci�n de Campos */

    private ArrayList m_buffer = new ArrayList();

    /** Descripci�n de Campos */

    private RowSet m_nodeRowSet;

    /** Descripci�n de Campos */

    private boolean m_clientTree = true;

    /** Descripci�n de Campos */

    private static CLogger s_log = CLogger.getCLogger( MTree.class );

    /**
     * Descripci�n de M�todo
     *
     *
     * @param AD_Client_ID
     * @param keyColumnName
     *
     * @return
     */

    public static int getAD_Tree_ID( int AD_Client_ID,String keyColumnName ) {
        s_log.config( "en getAd_Tree_ID con : = "+keyColumnName );

        if( (keyColumnName == null) || (keyColumnName.length() == 0) ) {
            return 0;
        }

        String TreeType = null;
        //Gestionamos el tipo de arbol que se ha predefinido previamente.
        if( keyColumnName.equals( "AD_Menu_ID" )) {
            TreeType = TREETYPE_Menu;
        } else if( keyColumnName.equals( "C_ElementValue_ID" )) {
            TreeType = TREETYPE_ElementValue;
        } else if( keyColumnName.equals( "M_Product_ID" )) {
            TreeType = TREETYPE_Product;
        } else if( keyColumnName.equals( "C_BPartner_ID" )) {
            TreeType = TREETYPE_BPartner;
        } else if( keyColumnName.equals( "AD_Org_ID" )) {
            TreeType = TREETYPE_Organization;
        } else if( keyColumnName.equals( "C_Project_ID" )) {
            TreeType = TREETYPE_Project;
        } else if( keyColumnName.equals( "M_ProductCategory_ID" )) {
            TreeType = TREETYPE_ProductCategory;
        } else if( keyColumnName.equals( "M_BOM_ID" )) {
            TreeType = TREETYPE_BoM;
        } else if( keyColumnName.equals( "C_SalesRegion_ID" )) {
            TreeType = TREETYPE_SalesRegion;
        } else if( keyColumnName.equals( "C_Campaign_ID" )) {
            TreeType = TREETYPE_Campaign;
        } else if( keyColumnName.equals( "C_Activity_ID" )) {
            TreeType = TREETYPE_Activity;
        }  else {
            s_log.log( Level.SEVERE,"MTree.getAD_Tree_ID - Could not map " + keyColumnName );

            return 0;
        }

        int    AD_Tree_ID = 0;
        String sql        = "SELECT AD_Tree_ID,Name FROM AD_Tree " + "WHERE AD_Client_ID=? AND TreeType=? AND IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setString( 2,TreeType );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                AD_Tree_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"MTree.getAD_Tree_ID",e );
        }

        return AD_Tree_ID;
    }    // getAD_Tree_ID

    /**
     * Descripci�n de M�todo
     *
     *
     * @param ctx
     * @param TreeType
     * @param clientTree
     * 
     * @return
     */

    public static MTree getTree( Properties ctx,String TreeType,boolean clientTree ) {
        int    AD_Tree_ID   = 0;
        int    AD_Client_ID = Env.getAD_Client_ID( ctx );
        String sql          = "SELECT AD_Tree_ID,Name FROM AD_Tree " + "WHERE AD_Client_ID=? AND TreeType=? AND IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setString( 2,TreeType );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                AD_Tree_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"MTree.getTree",e );
        }

        // Not found

        if( AD_Tree_ID == 0 ) {
            s_log.config( "MTree.getTree - No AD_Tree_ID for TreeType=" + TreeType + ", AD_Client_ID=" + AD_Client_ID );

            return null;
        }

        //

        MTree tree = new MTree( ctx,AD_Tree_ID,false,clientTree,null );

        return tree;
    }    // getTree

    /**
     * Descripci�n de M�todo
     *
     *
     * @param AD_User_ID
     */

    private void loadNodes( int AD_User_ID ) {

        // SQL for TreeNodes

        StringBuffer sql = new StringBuffer( "SELECT " + "tn.Node_ID,tn.Parent_ID,tn.SeqNo,tb.IsActive " + "FROM " ).append( getNodeTableName()).append( " tn" + " LEFT OUTER JOIN AD_TreeBar tb ON (tn.AD_Tree_ID=tb.AD_Tree_ID" + " AND tn.Node_ID=tb.Node_ID AND tb.AD_User_ID=?) "    // #1
            + "WHERE tn.AD_Tree_ID=?" );    // #2

        if( !m_editable ) {
            sql.append( " AND tn.IsActive='Y'" );
        }

        sql.append( " ORDER BY COALESCE(tn.Parent_ID, -1), tn.SeqNo" );
        log.finest( sql.toString());

        // The Node Loop

        try {

            // load Node details - addToTree -> getNodeDetail

            getNodeDetails();

            //

            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,AD_User_ID );
            pstmt.setInt( 2,getAD_Tree_ID());

            // Get Tree & Bar

            ResultSet rs = pstmt.executeQuery();

            m_root = new MTreeNode( 0,0,getName(),getDescription(),0,true,null,false,null );

            while( rs.next()) {
                int     node_ID   = rs.getInt( 1 );
                int     parent_ID = rs.getInt( 2 );
                int     seqNo     = rs.getInt( 3 );
                boolean onBar     = ( rs.getString( 4 ) != null );

                //

                if( (node_ID == 0) && (parent_ID == 0) ) {
                    ;
                } else {
                    addToTree( node_ID,parent_ID,seqNo,onBar );    // calls getNodeDetail
                }
            }

            rs.close();
            pstmt.close();

            //

            m_nodeRowSet.close();
            m_nodeRowSet = null;
            
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadNodes",e );
            m_nodeRowSet = null;
        }

        // Done with loading - add remainder from buffer

        if( m_buffer.size() != 0 ) {
            log.finest( "loadNodes - clearing buffer - Adding to: " + m_root );

            for( int i = 0;i < m_buffer.size();i++ ) {
                MTreeNode node   = ( MTreeNode )m_buffer.get( i );
                MTreeNode parent = m_root.findNode( node.getParent_ID());

                if( (parent != null) && parent.getAllowsChildren()) {
                    parent.add( node );
                    checkBuffer( node );
                    m_buffer.remove( node );
                    i = -1;    // start again with i=0
                }
            }
        }

        // Nodes w/o parent

        if( m_buffer.size() != 0 ) {
            log.severe( "loadNodes - Nodes w/o parent - adding to root - " + m_buffer );

            for( int i = 0;i < m_buffer.size();i++ ) {
                MTreeNode node = ( MTreeNode )m_buffer.get( i );

                m_root.add( node );
                checkBuffer( node );
                m_buffer.remove( node );
                i = -1;
            }

            if( m_buffer.size() != 0 ) {
                log.severe( "loadNodes - still nodes in Buffer - " + m_buffer );
            }
        }    // nodes w/o parents

        // clean up

        if( !m_editable && (m_root.getChildCount() > 0) ) {
            trimTree();
        }

//              diagPrintTree();

        if( CLogMgt.isLevelFinest() || (m_root.getChildCount() == 0) ) {
            log.fine( "loadTree - ChildCount=" + m_root.getChildCount());
        }
        
        // dispose node cache
        nodos.clear();
        
        //dispose colorCache
        s_cacheColors.clear();
        
    }    // loadNodes

    /**
     * Descripci�n de M�todo
     *
     *
     * @param node_ID
     * @param parent_ID
     * @param seqNo
     * @param onBar
     */

    private void addToTree( int node_ID,int parent_ID,int seqNo,boolean onBar ) {

        // Create new Node

        MTreeNode child = getNodeDetail( node_ID,parent_ID,seqNo,onBar );

        if( child == null ) {
            return;
        }

        // Add to Tree

        MTreeNode parent = null;

        if( m_root != null ) {
            parent = m_root.findNode( parent_ID );
        }

        // Parent found

        if( (parent != null) && parent.getAllowsChildren()) {
            parent.add( child );

            // see if we can add nodes from buffer

            if( m_buffer.size() > 0 ) {
                checkBuffer( child );
            }
        } else {
            m_buffer.add( child );
        }
    }    // addToTree

    /**
     * Descripci�n de M�todo
     *
     *
     * @param newNode
     */

    private void checkBuffer( MTreeNode newNode ) {

        // Ability to add nodes

        if( !newNode.isSummary() ||!newNode.getAllowsChildren()) {
            return;
        }

        //

        for( int i = 0;i < m_buffer.size();i++ ) {
            MTreeNode node = ( MTreeNode )m_buffer.get( i );

            if( node.getParent_ID() == newNode.getNode_ID()) {
                try {
                    newNode.add( node );
                } catch( Exception e ) {
                    log.severe( "Adding " + node.getName() + " to " + newNode.getName() + ": " + e.getMessage());
                }

                m_buffer.remove( i );
                i--;
            }
        }
    }    // checkBuffer

    private static Map<Integer,Boolean>	s_cacheColors	= new HashMap<Integer,Boolean>();
    /**
     * Verificar que el color exista
     * @param ad_printcolor_id id del color 
     * @return true si existe, false cc
     */

    
    private boolean existsColor(int ad_printcolor_id){
    	/*
    	 * Verificar que el color exista para que no tire ua excepci�n fea
    	 * ---------------------------------------------------------------
    	 * SELECT ad_printcolor_id 
    	 * FROM ad_printcolor
    	 * WHERE ad_printcolor_id = ?
    	 * ---------------------------------------------------------------
    	 */
    	boolean retValue=false;
    	Integer testColor = new Integer(ad_printcolor_id);
    	if (s_cacheColors.containsKey(testColor))
    	{
    		Boolean exists = s_cacheColors.get(testColor);
    		retValue= exists.booleanValue();
    	}else
    	{
    		String sql = "SELECT ad_printcolor_id FROM ad_printcolor WHERE ad_printcolor_id = ?";    		
    		retValue = (DB.getSQLValue(this.get_TrxName(), sql, ad_printcolor_id) != -1);
    		s_cacheColors.put(testColor,new Boolean(retValue));
    		
    	}
    	return retValue;
    }
    
    /**
     * Descripci�n de M�todo
     *
     */

    private void getNodeDetails() {

        // SQL for Node Info

        StringBuffer sqlNode     = new StringBuffer();
        String       sourceTable = "t";
        String       fromClause  = getSourceTableName( false );    // fully qualified
        String       columnNameX = getSourceTableName( true );
        String       color       = getActionColorName();
        //log.info( "\nEn getNodeDetail Color= "+color );

        if( getTreeType().equals( TREETYPE_Menu )) {
            boolean base = Env.isBaseLanguage( p_ctx,"AD_Menu" );

            sourceTable = "m";

            if( base ) {
                sqlNode.append( "SELECT m.AD_Menu_ID, m.Name,m.Description,m.IsSummary,m.Action, " + "m.AD_Window_ID, m.AD_Process_ID, m.AD_Form_ID, m.AD_Workflow_ID, m.AD_Task_ID, m.AD_Workbench_ID " + "FROM AD_Menu m" );
            } else {
                sqlNode.append( "SELECT m.AD_Menu_ID,  t.Name,t.Description,m.IsSummary,m.Action, " + "m.AD_Window_ID, m.AD_Process_ID, m.AD_Form_ID, m.AD_Workflow_ID, m.AD_Task_ID, m.AD_Workbench_ID " + "FROM AD_Menu m, AD_Menu_Trl t" );
            }

            if( !base ) {
                sqlNode.append( " WHERE m.AD_Menu_ID=t.AD_Menu_ID AND t.AD_Language='" ).append( Env.getAD_Language( p_ctx )).append( "'" );
            }

            if( !m_editable ) {
                boolean hasWhere = sqlNode.indexOf( " WHERE " ) != -1;

                sqlNode.append( hasWhere
                                ?" AND "
                                :" WHERE " ).append( "m.IsActive='Y' " );
            }

            // Do not show Beta
            // 

            if( !MClient.get( getCtx()).isUseBetaFunctions()) {
                boolean hasWhere = sqlNode.indexOf( " WHERE " ) != -1;

                sqlNode.append( hasWhere
                                ?" AND "
                                :" WHERE " );
                sqlNode.append( "(m.AD_Window_ID IS NULL OR EXISTS (SELECT * FROM AD_Window w WHERE m.AD_Window_ID=w.AD_Window_ID AND w.IsBetaFunctionality='N'))" ).append( " AND (m.AD_Process_ID IS NULL OR EXISTS (SELECT * FROM AD_Process p WHERE m.AD_Process_ID=p.AD_Process_ID AND p.IsBetaFunctionality='N'))" ).append( " AND (m.AD_Form_ID IS NULL OR EXISTS (SELECT * FROM AD_Form f WHERE m.AD_Form_ID=f.AD_Form_ID AND f.IsBetaFunctionality='N'))" );
            }

            // In R/O Menu - Show only defined Forms  
            //---------------------------------------

            if( !m_editable ) {
                boolean hasWhere = sqlNode.indexOf( " WHERE " ) != -1;

                sqlNode.append( hasWhere
                                ?" AND "
                                :" WHERE " );
                sqlNode.append( "(m.AD_Form_ID IS NULL OR EXISTS (SELECT * FROM AD_Form f WHERE m.AD_Form_ID=f.AD_Form_ID AND " );

                if( m_clientTree ) {
                    sqlNode.append( "f.Classname" );
                } else {
                    sqlNode.append( "f.JSPURL" );
                }

                sqlNode.append( " IS NOT NULL))" );
            }
        } else {
            if( columnNameX == null ) {
                throw new IllegalArgumentException( "Unknown TreeType=" + getTreeType());
            }

            sqlNode.append( "SELECT t." ).append( columnNameX ).append( "_ID,t.Name,t.Description,t.IsSummary," ).append( color ).append( " FROM " ).append( fromClause );

            if( !m_editable ) {
                sqlNode.append( " WHERE t.IsActive='Y'" );
            }
        }

        String sql = sqlNode.toString();

        if( !m_editable ) {    // editable = menu/etc. window
            sql = MRole.getDefault( getCtx(),false ).addAccessSQL( sql,sourceTable,MRole.SQL_FULLYQUALIFIED,m_editable );
        }

        //log.fine( "--------sql :>"+sql );
        m_nodeRowSet = DB.getRowSet( sql,true );
        loadNodeDetails();
        
    }    // getNodeDetails

    
    
    private Map<Integer, MTreeNode> nodos =new HashMap<Integer,MTreeNode>();
    private Map<Integer, MImage> imagenes =new HashMap<Integer,MImage>();
    
    
    
    //Preprocesa los nodos cargandolos al hashmap para acelerar la cosa. 
    //No asigna algunos valores que luego necesita el metodo getNodeDetails
    //Jorge Vidal - Disytel 2009-02-25
    private void loadNodeDetails( ) {
        MTreeNode unNodo = null;
        loadImages();
        try {
        	
        	MRole   role   = MRole.getDefault( getCtx(),false );
            m_nodeRowSet.beforeFirst();

            while( m_nodeRowSet.next()) {
                int node = m_nodeRowSet.getInt( 1 );

               // ,Name,Description,IsSummary,Action/Color 

                int     index       = 2;
                String  name        = m_nodeRowSet.getString( index++ );
                //log.info( "En getNodeDetail con name= "+name );
                String  description = m_nodeRowSet.getString( index++ );
                //log.info( "\nEn getNodeDetail con descripcion= "+description );
                boolean isSummary   = "Y".equals( m_nodeRowSet.getString( index++ ));
                //log.info( "\nEn getNodeDetail con dIsSumary= "+isSummary );
                //No hay color, anulamos momentaneamente
                String actionColor = m_nodeRowSet.getString( index++ ); 
                //String actionColor=null;
                //log.info( "\nEn getNodeDetail actionColor= "+actionColor );
                

                // Menu only

                if( getTreeType().equals( TREETYPE_Menu ) &&!isSummary ) {
                    int AD_Window_ID    = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_window_id");
                    int AD_Process_ID   = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_process_id");
                    int AD_Form_ID      = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_form_id");
                    int AD_Workflow_ID  = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_workflow_id");
                    int AD_Task_ID      = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_task_id");
                    int AD_Workbench_ID = m_nodeRowSet.getInt( index++ );
                    //log.info( "\n asignado ad_workbench_id");

                    //

                  
                    //log.info( "\n asignado role");
                    Boolean access = null;

                    if( X_AD_Menu.ACTION_Window.equals( actionColor )) {
                        access = role.getWindowAccess( AD_Window_ID );
                    } else if( X_AD_Menu.ACTION_Process.equals( actionColor ) || X_AD_Menu.ACTION_Report.equals( actionColor )) {
                        access = role.getProcessAccess( AD_Process_ID );
                    } else if( X_AD_Menu.ACTION_Form.equals( actionColor )) {
                        access = role.getFormAccess( AD_Form_ID );
                    } else if( X_AD_Menu.ACTION_WorkFlow.equals( actionColor )) {
                        access = role.getWorkflowAccess( AD_Workflow_ID );
                    } else if( X_AD_Menu.ACTION_Task.equals( actionColor )) {
                        access = role.getTaskAccess( AD_Task_ID );
                    }

                    // else if (X_AD_Menu.ACTION_Workbench.equals(action))
                    // access = role.getWorkbenchAccess(AD_Window_ID);
                    // log.fine("getNodeDetail - " + name + " - " + actionColor + " - " + access);

                    if( (access != null ) || m_editable )                                                                                         // rw or ro for Role
                    {                                                     // Menu Window can see all
                    	//log.info("En acces!null || m_editable");
                        if (AD_Window_ID!=0)
                        {
                        	Icon aIcon = getImageForWindow(AD_Window_ID);
                            unNodo = new MTreeNode( node,0,name,description,0,isSummary,actionColor,false,null, aIcon );    // menu has no color                        	
                        	
                        }
                        else
                        {
                        	unNodo = new MTreeNode( node,0,name,description,0,isSummary,actionColor,false,null );    // menu has no color}
                    	
                        }
                        nodos.put(new Integer(node),unNodo);
                    }
                } else                     // always add
                {
                    Color color = null;    // action

                    if((actionColor != null) && this.existsColor(Integer.parseInt(actionColor)) && !getTreeType().equals( TREETYPE_Menu )) {
                        MPrintColor printColor = MPrintColor.get( getCtx(),actionColor );

                        if( printColor != null ) {
                            color = printColor.getColor();
                        }
                    }

                    //
                    //log.info("Antes del new MTreeNode");
                    unNodo = new MTreeNode( node,0,name,description,0,isSummary,null,false,color );    // no action
                    nodos.put(new Integer(node),unNodo);
                }
            
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getNodeDetails-:>",e );
        }
        releaseImages();
    }    // getNodeDetails

    
    private void loadImages()
    {
    	StringBuffer	sql	= new StringBuffer("SELECT AD_Image_ID, AD_Window_ID FROM AD_Window WHERE AD_Image_ID<>0");
   	 try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString() );                              
            ResultSet rs = pstmt.executeQuery();
            
            while( rs.next()) {
                MImage mImage = MImage.get(Env.getCtx(),rs.getInt(1));
                imagenes.put(new Integer(rs.getInt(2)),mImage);
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"MTree.getAD_Tree_ID",e );
        }

    }
    
    private void releaseImages()
    {
    	imagenes.clear();
    }
    
    private Icon getImageForWindow(int AD_Window_ID)
    {
    	int AD_Image_ID  =0;    	
    	Icon aIcon= null;
    	Integer key = new Integer (AD_Window_ID);
    	MImage mImage = imagenes.get(key);
    	    	 
        if (mImage!=null)
        {
        	
        	MAttachment attach = mImage.getAttachment(true);
        	
        	// Si el attach tiene una imagen, la creamos desde el mismo
        	// sino desde la url que tiene el jar
        	if (attach!=null)
        		{
        		try {
					MAttachmentEntry entry = attach.getEntry(0);
					byte[] data =  entry.getData();
					if (data!=null)
						aIcon = new ImageIcon(data);
				} catch (RuntimeException e) {
					log.fine("Error al cargar la imagen del menu");
					e.printStackTrace();
				}
        		
        		}
        	if (aIcon==null)
        		aIcon= mImage.getIcon();
        	
        }
        return aIcon;
    	
    }    
    
    
    /**
     * Descripci�n de M�todo
     *  Modificado por JorgeV - Disytel para mejorar la performance de la carga.
     *
     * @param node_ID
     * @param parent_ID
     * @param seqNo
     * @param onBar
     *
     * @return
     */    
    
    private MTreeNode getNodeDetail( int node_ID,int parent_ID,int seqNo,boolean onBar ) {
    	
    	MTreeNode nodoNuevo = null;
    	MTreeNode origValue = nodos.get(new Integer(node_ID));   
    	if (origValue!=null)
    	{
        	
    		nodoNuevo = new MTreeNode(node_ID, seqNo, origValue.getName(), origValue.getDescription(), parent_ID, origValue.isSummary(),origValue.getImageIndiactor(),onBar,origValue.getColor(), origValue.getIcon());
 
    	}	
    			
        return nodoNuevo;
    }    // getNodeDetails

    /**
     * Descripci�n de M�todo
     *
     */

    public void trimTree() {
        boolean needsTrim = m_root != null;

        while( needsTrim ) {
            needsTrim = false;

            Enumeration en = m_root.preorderEnumeration();

            while( (m_root.getChildCount() > 0) && en.hasMoreElements()) {
                MTreeNode nd = ( MTreeNode )en.nextElement();

                if( nd.isSummary() && (nd.getChildCount() == 0) ) {
                    nd.removeFromParent();
                    needsTrim = true;
                }
            }
        }
    }    // trimTree

    /**
     * Descripci�n de M�todo
     *
     */

    private void dumpTree() {
        Enumeration en    = m_root.preorderEnumeration();
        int         count = 0;

        while( en.hasMoreElements()) {
            StringBuffer sb = new StringBuffer();
            MTreeNode    nd = ( MTreeNode )en.nextElement();

            for( int i = 0;i < nd.getLevel();i++ ) {
                sb.append( " " );
            }

            sb.append( "ID=" ).append( nd.getNode_ID()).append( ", SeqNo=" ).append( nd.getSeqNo()).append( " " ).append( nd.getName());
            System.out.println( sb.toString());
            count++;
        }

        System.out.println( "Count=" + count );
    }    // diagPrintTree

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public MTreeNode getRoot() {
        return m_root;
    }    // getRoot

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public boolean isMenu() {
        return TREETYPE_Menu.equals( getTreeType());
    }    // isMenu

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public boolean isProduct() {
        return TREETYPE_Product.equals( getTreeType());
    }    // isProduct

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public boolean isBPartner() {
        return TREETYPE_BPartner.equals( getTreeType());
    }    // isBPartner

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MTree[" );

        sb.append( "AD_Tree_ID=" ).append( getAD_Tree_ID()).append( ", Name=" ).append( getName());
        sb.append( "]" );

        return sb.toString();
    }
}    // MTree



/*
 *  @(#)MTree.java   02.07.07
 * 
 *  Fin del fichero MTree.java
 *  
 *  Versi�n 2.2
 *
 */