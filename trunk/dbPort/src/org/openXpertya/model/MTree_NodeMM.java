/*
 * @(#)MTree_NodeMM.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.model;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      (Disk) Tree Node Model Menu
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MTree_NodeMM.java,v 1.4 2005/03/11 20:28:37 jjanke Exp $
 */
public class MTree_NodeMM extends X_AD_TreeNodeMM {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MTree_NodeMM.class);

    /**
     *      Full Constructor
     *      @param tree tree
     *      @param Node_ID node
     */
    public MTree_NodeMM(MTree_Base tree, int Node_ID) {

        super(tree.getCtx(), 0, tree.get_TrxName());
        setClientOrg(tree);
        setAD_Tree_ID(tree.getAD_Tree_ID());
        setNode_ID(Node_ID);

        // Add to root
        setParent_ID(0);
        setSeqNo(0);

    }		// MTree_NodeMM

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MTree_NodeMM(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MTree_NodeMM
    
    
    /**
     *      Custom Constructor
     *      @param ctx context
     *      @param Node_ID node
     * 		@param trxName
     */
    public MTree_NodeMM(Properties ctx,int Node_ID,String trxName){
    	
    	super(ctx, 0, trxName);
    	
    }	// MTree_NodeMM

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Tree Node
     *      @param tree tree
     *      @param Node_ID node
     *      @return node or null
     */
    public static MTree_NodeMM get(MTree_Base tree, int Node_ID) {

        MTree_NodeMM	retValue	= null;
        String		sql		= "SELECT * FROM AD_TreeNodeMM WHERE AD_Tree_ID=? AND Node_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, tree.getAD_Tree_ID());
            pstmt.setInt(2, Node_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MTree_NodeMM(tree.getCtx(), rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "get", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// get
    
    
    public static MTree_NodeMM[] getOfTree(Properties ctx, int AD_Tree_ID, String trxName){
    	
    	String sql = "SELECT * FROM ad_treenodemm WHERE ad_tree_id = ?";
    	ArrayList nodes = new ArrayList();
    	PreparedStatement psmt = null;
    	
    	try{
    		psmt = DB.prepareStatement(sql, trxName);
    		psmt.setInt(1, AD_Tree_ID);
    		
    		ResultSet rs = psmt.executeQuery();
    		
    		while(rs.next()){
    			nodes.add(new MTree_NodeMM(ctx,rs,trxName));
    		}
    		rs.close();
            psmt.close();
            psmt = null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "get", e);
        }

        try {

            if (psmt != null) {
                psmt.close();
            }

            psmt	= null;

        } catch (Exception e) {
            psmt	= null;
        }
        
        MTree_NodeMM[] retValue = new MTree_NodeMM[ nodes.size()];

        nodes.toArray( retValue );

        return retValue;

    }	//getOfTree
}	// MTree_NodeMM



/*
 * @(#)MTree_NodeMM.java   02.jul 2007
 * 
 *  Fin del fichero MTree_NodeMM.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
