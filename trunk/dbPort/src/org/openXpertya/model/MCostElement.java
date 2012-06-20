/*
 * @(#)MCostElement.java   12.oct 2007  Versión 2.2
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
 *      Cost Element Model
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MCostElement.java,v 1.2 2005/04/25 06:02:02 jjanke Exp $
 */
public class MCostElement extends X_M_CostElement {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MCostElement.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param M_CostElement_ID id
     *      @param trxName trx
     */
    public MCostElement(Properties ctx, int M_CostElement_ID, String trxName) {

        super(ctx, M_CostElement_ID, trxName);

        if (M_CostElement_ID == 0) {

            // setName (null);
            setCostElementType(COSTELEMENTTYPE_Material);
            setIsCalculated(false);
        }

    }		// MCostElement

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     *      @param trxName trx
     */
    public MCostElement(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MCostElement

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

        // Maintain Calclated
        if (COSTELEMENTTYPE_Material.equals(getCostElementType())) {

            String	cm	= getCostingMethod();

            if ((cm == null) || (cm.length() == 0) || COSTINGMETHOD_StandardCosting.equals(cm)) {
                setIsCalculated(false);
            } else {
                setIsCalculated(true);
            }

        } else {
            setIsCalculated(false);
        }

        return true;
    }		// beforeSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Material Cost Element or create it
     *      @param po parent
     *      @param CostingMethod method
     *      @return cost element
     */
    public static MCostElement getMaterialCostElement(PO po, String CostingMethod) {

        if ((CostingMethod == null) || (CostingMethod.length() == 0)) {

            s_log.severe("No CostingMethod");

            return null;
        }

        //
        MCostElement	retValue	= null;
        String		sql		= "SELECT * FROM M_CostElement WHERE AD_Client_ID=? AND CostingMethod=? ORDER BY AD_Org_ID";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, po.getAD_Client_ID());
            pstmt.setString(2, CostingMethod);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MCostElement(po.getCtx(), rs, po.get_TrxName());
            }

            if (rs.next()) {
                s_log.warning("More then one Material Cost Element for CostingMethod=" + CostingMethod);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        if (retValue != null) {
            return retValue;
        }

        // Create New
        retValue	= new MCostElement(po.getCtx(), 0, po.get_TrxName());
        retValue.setClientOrg(po.getAD_Client_ID(), 0);

        String	name	= MRefList.getListName(po.getCtx(), COSTINGMETHOD_AD_Reference_ID, CostingMethod);

        if ((name == null) || (name.length() == 0)) {
            name	= CostingMethod;
        }

        retValue.setName(name);
        retValue.setCostElementType(COSTELEMENTTYPE_Material);
        retValue.setCostingMethod(CostingMethod);
        retValue.save();

        //
        return retValue;

    }		// getMaterialCostElement

    /**
     *      Get Material Cost Element or create it
     *      @param po parent
     *      @return cost element
     */
    public static MCostElement[] getMaterialCostElements(PO po) {

        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM M_CostElement WHERE AD_Client_ID=? ORDER BY AD_Org_ID";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, po.getAD_Client_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MCostElement(po.getCtx(), rs, po.get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        MCostElement[]	retValue	= new MCostElement[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getMaterialCostElement
}	// MCostElement



/*
 * @(#)MCostElement.java   02.jul 2007
 * 
 *  Fin del fichero MCostElement.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
