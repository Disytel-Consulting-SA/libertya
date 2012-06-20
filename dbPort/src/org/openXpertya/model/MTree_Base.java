/*
 * @(#)MTree_Base.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Base Tree Model.
 *      (see also MTree in project base)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MTree_Base.java,v 1.9 2005/03/11 20:28:35 jjanke Exp $
 */
public class MTree_Base extends X_AD_Tree {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Tree", 10);

    /**
     *      Parent Constructor
     *      @param client client
     * @param name
     * @param treeType
     */
    public MTree_Base(MClient client, String name, String treeType) {

        this(client.getCtx(), 0, client.get_TrxName());
        setClientOrg(client);
        setName(name);
        setTreeType(treeType);

    }		// MTree_Base

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Tree_ID id
     * @param trxName
     */
    public MTree_Base(Properties ctx, int AD_Tree_ID, String trxName) {

        super(ctx, AD_Tree_ID, trxName);

        if (AD_Tree_ID == 0) {

            // setName (null);
            // setTreeType (null);
            setIsAllNodes(true);	// complete tree
        }

    }					// MTree_Base

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MTree_Base(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MTree_Base

    /**
     *      Add Node to correct tree
     *      @param ctx cpntext
     *      @param treeType tree type
     *      @param Record_ID id
     * @param trxName
     *
     * @return
     */
    public static boolean addNode(Properties ctx, String treeType, int Record_ID, String trxName) {

        // Get Tree
        int		AD_Tree_ID	= 0;
        MClient		client		= MClient.get(ctx);
        MClientInfo	ci		= client.getInfo();

        if (TREETYPE_Activity.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Activity_ID();
        } else if (TREETYPE_BoM.equals(treeType)) {
            throw new IllegalArgumentException("BoM Trees not supported");
        } else if (TREETYPE_BPartner.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_BPartner_ID();
        } else if (TREETYPE_Campaign.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Campaign_ID();
        } else if (TREETYPE_ElementValue.equals(treeType)) {
            throw new IllegalArgumentException("ElementValue cannot use this API");
        } else if (TREETYPE_Menu.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Menu_ID();
        } else if (TREETYPE_Organization.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Org_ID();
        } else if (TREETYPE_Product.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Product_ID();
        } else if (TREETYPE_ProductCategory.equals(treeType)) {
            throw new IllegalArgumentException("Product Category Trees not supported");
        } else if (TREETYPE_Project.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_Project_ID();
        } else if (TREETYPE_SalesRegion.equals(treeType)) {
            AD_Tree_ID	= ci.getAD_Tree_SalesRegion_ID();
        }

        if (AD_Tree_ID == 0) {
            throw new IllegalArgumentException("No Tree found");
        }

        MTree_Base	tree	= MTree_Base.get(ctx, AD_Tree_ID, trxName);

        if (tree.getID() != AD_Tree_ID) {
            throw new IllegalArgumentException("Tree found AD_Tree_ID=" + AD_Tree_ID);
        }

        // Insert Tree in correct tree
        boolean	saved	= false;

        if (TREETYPE_Menu.equals(treeType)) {

            MTree_NodeMM	node	= new MTree_NodeMM(tree, Record_ID);

            saved	= node.save();

        } else if (TREETYPE_BPartner.equals(treeType)) {

            MTree_NodeBP	node	= new MTree_NodeBP(tree, Record_ID);

            saved	= node.save();

        } else if (TREETYPE_Product.equals(treeType)) {

            MTree_NodePR	node	= new MTree_NodePR(tree, Record_ID);

            saved	= node.save();

        } else {

            MTree_Node	node	= new MTree_Node(tree, Record_ID);

            saved	= node.save();
        }

        return saved;
    }		// addNode

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord)		// Base Node
        {

            if (TREETYPE_BPartner.equals(getTreeType())) {

                MTree_NodeBP	ndBP	= new MTree_NodeBP(this, 0);

                ndBP.save();

            } else if (TREETYPE_Menu.equals(getTreeType())) {

                MTree_NodeMM	ndMM	= new MTree_NodeMM(this, 0);

                ndMM.save();

            } else if (TREETYPE_Product.equals(getTreeType())) {

                MTree_NodePR	ndPR	= new MTree_NodePR(this, 0);

                ndPR.save();

            } else {

                MTree_Node	nd	= new MTree_Node(this, 0);

                nd.save();
            }
        }

        return success;

    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get MTree_Base from Cache
     *      @param ctx context
     * @param AD_Tree_ID
     * @param trxName
     *      @return MTree_Base
     */
    public static MTree_Base get(Properties ctx, int AD_Tree_ID, String trxName) {

        Integer		key		= new Integer(AD_Tree_ID);
        MTree_Base	retValue	= (MTree_Base) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MTree_Base(ctx, AD_Tree_ID, trxName);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get fully qualified Name of Action/Color Column
     *      @return NULL or Action or Color
     */
    public String getActionColorName() {

        String	tableName	= getSourceTableName(getTreeType());

        // log.log(Level.SEVERE,"tableName= "+tableName);
        if ("AD_Menu".equals(tableName)) {
            return "t.Action";
        }

        if ("M_Product".equals(tableName) || "C_BPartner".equals(tableName) || "AD_Org".equals(tableName) || "C_Campaign".equals(tableName)) {
            return "x.AD_PrintColor_ID";
        }

        if ("C_SalesRegion".equals(tableName)) {

//          Añadido por ConSerti.    
            return "t.salesrep_id";	// Si es el arbol de Region de ventas.
        }
        if ("C_Activity".equals(tableName)) {

//          Modificado por ConSerti, 
            return "t.c_activity_id";	// Si es el arbol de Region de ventas.
        }
        if ("C_Project".equals(tableName)) {

//          Añadido por ConSerti.      
            return "t.c_project_id";	// Si es el arbol proyectos.
        }
        if ("C_ElementValue".equals(tableName)) {

//          Añadido por ConSerti.      
            return "t.c_element_id";	// Si es el arbol de contabilidad.
        }
       
        log.fine("La tablename es = "+ tableName);

        return "t.c_element_id";	// 

    }					// getSourceTableName

    /**
     *      Get Node TableName
     *      @return node table name, e.g. AD_TreeNode
     */
    public String getNodeTableName() {
        return getNodeTableName(getTreeType());
    }		// getNodeTableName

    /**
     *      Get Node TableName
     *      @param treeType tree type
     *      @return node table name, e.g. AD_TreeNode
     */
    public static String getNodeTableName(String treeType) {

        String	nodeTableName	= "AD_TreeNode";

        if (TREETYPE_Menu.equals(treeType)) {
            nodeTableName	+= "MM";
        } else if (TREETYPE_BPartner.equals(treeType)) {
            nodeTableName	+= "BP";
        } else if (TREETYPE_Product.equals(treeType)) {
            nodeTableName	+= "PR";
        }

        return nodeTableName;

    }		// getNodeTableName

    /**
     *      Get Source TableName (i.e. where to get the name and color)
     *      @param tableNameOnly if false return From clause (alias = t)
     *      @return source table name, e.g. AD_Org or null
     */
    public String getSourceTableName(boolean tableNameOnly) {

        String	tableName	= getSourceTableName(getTreeType());

        if (tableNameOnly) {
            return tableName;
        }

        if ("M_Product".equals(tableName)) {
            return "M_Product t INNER JOIN M_Product_Category x ON (t.M_Product_Category_ID=x.M_Product_Category_ID)";
        }

        if ("C_BPartner".equals(tableName)) {
            return "C_BPartner t INNER JOIN C_BP_Group x ON (t.C_BP_Group_ID=x.C_BP_Group_ID)";
        }

        if ("AD_Org".equals(tableName)) {
            return "AD_Org t INNER JOIN AD_OrgInfo i ON (t.AD_Org_ID=i.AD_Org_ID) " + "LEFT OUTER JOIN AD_OrgType x ON (i.AD_OrgType_ID=x.AD_OrgType_ID)";
        }

        if ("C_Campaign".equals(tableName)) {
            return "C_Campaign t LEFT OUTER JOIN C_Channel x ON (t.C_Channel_ID=x.C_Channel_ID)";
        }

        if (tableName != null) {
            tableName	+= " t";
        }

        return tableName;

    }		// getSourceTableName

    /**
     *      Get Source TableName
     *      @param treeType tree typw
     *      @return source table name, e.g. AD_Org or null
     */
    public static String getSourceTableName(String treeType) {

        if (treeType == null) {
            return null;
        }

        String	sourceTable	= null;

        if (treeType.equals(TREETYPE_Menu)) {
            sourceTable	= "AD_Menu";
        } else if (treeType.equals(TREETYPE_Organization)) {
            sourceTable	= "AD_Org";
        } else if (treeType.equals(TREETYPE_Product)) {
            sourceTable	= "M_Product";
        } else if (treeType.equals(TREETYPE_ProductCategory)) {
            sourceTable	= "M_Product_Category";
        } else if (treeType.equals(TREETYPE_BoM)) {
            sourceTable	= "M_BOM";
        } else if (treeType.equals(TREETYPE_ElementValue)) {
            sourceTable	= "C_ElementValue";
        } else if (treeType.equals(TREETYPE_BPartner)) {
            sourceTable	= "C_BPartner";
        } else if (treeType.equals(TREETYPE_Campaign)) {
            sourceTable	= "C_Campaign";
        } else if (treeType.equals(TREETYPE_Project)) {
            sourceTable	= "C_Project";
        } else if (treeType.equals(TREETYPE_Activity)) {
            sourceTable	= "C_Activity";
        } else if (treeType.equals(TREETYPE_SalesRegion)) {
            sourceTable	= "C_SalesRegion";
        }

        return sourceTable;

    }		// getSourceTableName
}	// MTree_Base



/*
 * @(#)MTree_Base.java   02.jul 2007
 * 
 *  Fin del fichero MTree_Base.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
