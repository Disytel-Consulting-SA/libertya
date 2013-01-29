/*
 * @(#)MTree_NodeBP.java   12.oct 2007  Versión 2.2
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

import java.util.Properties;
import java.util.logging.Level;

/**
 *      (Disk) Tree Node Model BPartner
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MTree_NodeBP.java,v 1.5 2005/03/11 20:28:38 jjanke Exp $
 */
public class MTree_NodeBP extends X_AD_TreeNodeBP {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MTree_NodeBP.class);

    /**
     *      Full Constructor
     *      @param tree tree
     *      @param Node_ID node
     */
    public MTree_NodeBP(MTree_Base tree, int Node_ID) {

        super(tree.getCtx(), 0, tree.get_TrxName());
        setClientOrg(tree);
        setAD_Tree_ID(tree.getAD_Tree_ID());
        setNode_ID(Node_ID);

        // Add to root
        setParent_ID(0);
        setSeqNo(0);

    }		// MTree_NodeBP

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MTree_NodeBP(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MTree_NodeBP

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Tree Node
     *      @param tree tree
     *      @param Node_ID node
     *      @return node or null
     */
    public static MTree_NodeBP get(MTree_Base tree, int Node_ID) {

        MTree_NodeBP	retValue	= null;
        String		sql		= "SELECT * FROM AD_TreeNodeBP WHERE AD_Tree_ID=? AND Node_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, tree.getAD_Tree_ID());
            pstmt.setInt(2, Node_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MTree_NodeBP(tree.getCtx(), rs, null);
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
}	// MTree_NodeBP



/*
 * @(#)MTree_NodeBP.java   02.jul 2007
 * 
 *  Fin del fichero MTree_NodeBP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
