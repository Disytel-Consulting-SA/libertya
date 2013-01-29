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



package org.openXpertya.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportTree {

    /** Descripción de Campos */

    private static CCache s_trees = new CCache( "MReportTree",20 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ElementType
     *
     * @return
     */

    public static MReportTree get( Properties ctx,String ElementType ) {
        MReportTree tree = ( MReportTree )s_trees.get( ElementType );

        if( tree == null ) {
            tree = new MReportTree( ctx,ElementType );
            s_trees.put( ElementType,tree );
        }

        return tree;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ElementType
     * @param ID
     *
     * @return
     */

    public static String getWhereClause( Properties ctx,String ElementType,int ID ) {
        MReportTree tree = get( ctx,ElementType );

        return tree.getWhereClause( ID );
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ElementType
     */

    public MReportTree( Properties ctx,String ElementType ) {
        m_ElementType = ElementType;
        m_TreeType    = m_ElementType;

        if( MAcctSchemaElement.ELEMENTTYPE_Account.equals( m_ElementType ) || MAcctSchemaElement.ELEMENTTYPE_User1.equals( m_ElementType ) || MAcctSchemaElement.ELEMENTTYPE_User2.equals( m_ElementType )) {
            m_TreeType = MTree.TREETYPE_ElementValue;
        }

        //

        m_tree = MTree.getTree( ctx,m_TreeType,true );    // client
    }                                                     // MReportTree

    /** Descripción de Campos */

    private String m_ElementType = null;

    /** Descripción de Campos */

    private String m_TreeType = null;

    /** Descripción de Campos */

    private MTree m_tree = null;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getElementType() {
        return m_ElementType;
    }    // getElementType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTreeType() {
        return m_TreeType;
    }    // getTreeType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTree getTree() {
        return m_tree;
    }    // getTreeType

    /**
     * Descripción de Método
     *
     *
     * @param ID
     *
     * @return
     */

    public String getWhereClause( int ID ) {
        log.fine( "getWhereClause - (" + m_ElementType + ") ID=" + ID );

        String ColumnName = MAcctSchemaElement.getColumnName( m_ElementType );

        //

        MTreeNode node = m_tree.getRoot().findNode( ID );

        log.finest( "Root=" + node );

        //

        StringBuffer result = null;

        if( (node != null) && node.isSummary()) {
            StringBuffer sb = new StringBuffer();
            Enumeration  en = node.preorderEnumeration();

            while( en.hasMoreElements()) {
                MTreeNode nn = ( MTreeNode )en.nextElement();

                if( !nn.isSummary()) {
                    if( sb.length() > 0 ) {
                        sb.append( "," );
                    }

                    sb.append( nn.getNode_ID());
                    log.finest( "- " + nn );
                } else {
                    log.finest( "- skipped parent (" + nn + ")" );
                }
            }

            result = new StringBuffer( ColumnName ).append( " IN (" ).append( sb ).append( ")" );
        } else {    // not found or not summary
            result = new StringBuffer( ColumnName ).append( "=" ).append( ID );
        }

        //

        log.finest( "getWhereClause - " + result );

        return result.toString();
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportTree[ElementType=" );

        sb.append( m_ElementType ).append( ",TreeType=" ).append( m_TreeType ).append( "," ).append( m_tree ).append( "]" );

        return sb.toString();
    }    // toString
    
    /**
	 * 	Get Child IDs
	 *	@param ctx context
	 *	@param PA_Hierarchy_ID optional hierarchie
	 *	@param ElementType Account Schema Element Type
	 *	@param ID id
	 *	@return array of IDs
	 */
	public static Integer[] getChildIDs (Properties ctx,
		int PA_Hierarchy_ID, String ElementType, int ID)
	{
		MReportTree tree = get (ctx, PA_Hierarchy_ID, ElementType);
		return tree.getChildIDs(ID);	
	}	//	getChildIDs
	/**
	 * 	Get Child IDs
	 *	@param ID start node
	 *	@return array if IDs
	 */	
	public Integer[] getChildIDs (int ID)
	{
		log.fine("(" + m_ElementType + ") ID=" + ID);
		ArrayList<Integer> list = new ArrayList<Integer>(); 
		//
		MTreeNode node = m_tree.getRoot().findNode(ID);
		log.finest("Root=" + node);
		//
		if (node != null && node.isSummary())
		{
			Enumeration en = node.preorderEnumeration();
			while (en.hasMoreElements())
			{
				MTreeNode nn = (MTreeNode)en.nextElement();
				if (!nn.isSummary())
				{
					list.add(new Integer(nn.getNode_ID()));
					log.finest("- " + nn);
				}
				else
					log.finest("- skipped parent (" + nn + ")");
			}
		}
		else	//	not found or not summary 
			list.add(new Integer(ID));
		//
		Integer[] retValue = new Integer[list.size()];
		list.toArray(retValue);
		return retValue;
	}	//	getWhereClause

	public static MReportTree get (Properties ctx, int PA_Hierarchy_ID, String ElementType)
	{
		String key = PA_Hierarchy_ID + ElementType;
		MReportTree tree = (MReportTree)s_trees.get(key);
		if (tree == null)
		{
			tree = new MReportTree (ctx, PA_Hierarchy_ID, ElementType);
			s_trees.put(key, tree);
		}
		return tree;	
	}	//	get

	/**************************************************************************
	 * 	Report Tree
	 *	@param ctx context
	 *	@param PA_Hierarchy_ID optional hierarchy
	 *	@param ElementType Account Schema Element Type
	 */
	public MReportTree (Properties ctx, int PA_Hierarchy_ID, String ElementType)
	{
		m_ElementType = ElementType;
		m_TreeType = m_ElementType;
		if (MAcctSchemaElement.ELEMENTTYPE_Account.equals(m_ElementType)
			|| /*MAcctSchemaElement.ELEMENTTYPE_UserList1*/ "U1".equals(m_ElementType)
			|| /*MAcctSchemaElement.ELEMENTTYPE_UserList2*/ "U2".equals(m_ElementType) )
			m_TreeType = MTree.TREETYPE_ElementValue;
		if (MAcctSchemaElement.ELEMENTTYPE_OrgTrx.equals(m_ElementType))
				m_TreeType = MTree.TREETYPE_Organization;
		m_PA_Hierarchy_ID = PA_Hierarchy_ID;
		m_ctx = ctx;
		//
		int AD_Tree_ID = getAD_Tree_ID();
		//	Not found
		if (AD_Tree_ID == 0)
			throw new IllegalArgumentException("No AD_Tree_ID for TreeType=" + m_TreeType 
				+ ", PA_Hierarchy_ID=" + PA_Hierarchy_ID);
		//
		boolean clientTree = true;
		m_tree = new MTree (ctx, AD_Tree_ID, true, clientTree, null);  // include inactive and empty summary nodes
		// remove summary nodes without children
		m_tree.trimTree();
	}	//	MReportTree

	/** Optional Hierarchy		*/
	private int			m_PA_Hierarchy_ID = 0;
	/** Context					*/
	private Properties	m_ctx = null;
	/**
	 * 	Get AD_Tree_ID 
	 *	@return tree
	 */
	protected int getAD_Tree_ID ()
	{
		if (m_PA_Hierarchy_ID == 0)
			return getDefaultAD_Tree_ID();

//		MHierarchy hierarchy = MHierarchy.get(m_ctx, m_PA_Hierarchy_ID);
		int AD_Tree_ID = 0; // hierarchy.getAD_Tree_ID (m_TreeType)*/;

		if (AD_Tree_ID == 0)
			return getDefaultAD_Tree_ID();
		
		return AD_Tree_ID;
	}	//	getAD_Tree_ID
	
	/**
	 * 	Get Default AD_Tree_ID 
	 * 	see MTree.getDefaultAD_Tree_ID
	 *	@return tree
	 */
	protected int getDefaultAD_Tree_ID()
	{
		int AD_Tree_ID = 0;
		int AD_Client_ID = Env.getAD_Client_ID(m_ctx);
		
		String sql = "SELECT AD_Tree_ID, Name FROM AD_Tree "
			+ "WHERE AD_Client_ID=? AND TreeType=? AND IsActive='Y' AND IsAllNodes='Y' "
			+ "ORDER BY IsDefault DESC, AD_Tree_ID";	//	assumes first is primary tree
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, AD_Client_ID);
			pstmt.setString(2, m_TreeType);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				AD_Tree_ID = rs.getInt(1);
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}

		return AD_Tree_ID;
	}	//	getDefaultAD_Tree_ID

	
}    // MReportTree



/*
 *  @(#)MReportTree.java   02.07.07
 * 
 *  Fin del fichero MReportTree.java
 *  
 *  Versión 2.2
 *
 */
