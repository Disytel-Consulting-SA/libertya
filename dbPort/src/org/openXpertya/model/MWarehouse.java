/*
 * @(#)MWarehouse.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Warehouse Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MWarehouse.java,v 1.6 2005/05/28 21:18:09 jjanke Exp $
 */
public class MWarehouse extends X_M_Warehouse {

    /** Cache */
    private static CCache	s_cache	= new CCache("M_Warehouse", 5);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MWarehouse.class);

    /** Warehouse Locators */
    private MLocator[]	m_locators	= null;

    /**
     *      Organization Constructor
     *      @param org parent
     */
    public MWarehouse(MOrg org) {

        this(org.getCtx(), 0, org.get_TrxName());
        setClientOrg(org);
        setValue(org.getValue());
        setName(org.getName());

        if (org.getInfo() != null) {
            setC_Location_ID(org.getInfo().getC_Location_ID());
        }

    }		// MWarehouse

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param M_Warehouse_ID id
     * @param trxName
     */
    public MWarehouse(Properties ctx, int M_Warehouse_ID, String trxName) {

        super(ctx, M_Warehouse_ID, trxName);

        if (M_Warehouse_ID == 0) {

            // setValue (null);
            // setName (null);
            // setC_Location_ID (0);
            setSeparator("*");		// *
        }

    }					// MWarehouse

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MWarehouse(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MWarehouse
    
    /**
     * Get warehouses of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MWarehouse> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM m_warehouse WHERE ad_client_id = ? "; 
    		
    	List<MWarehouse> list = new ArrayList<MWarehouse>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MWarehouse(ctx,rs,trxName));	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
    }
    

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord) {
            insert_Accounting("M_Warehouse_Acct", "C_AcctSchema_Default", null);
        }

        return success;

    }		// afterSave

    /**
     *      Before Delete
     *      @return true
     */
    protected boolean beforeDelete() {
        return delete_Accounting("M_Warehouse_Acct");
    }		// beforeDelete

    //~--- get methods --------------------------------------------------------

    /**
     *      Get from Cache
     *      @param ctx context
     *      @param M_Warehouse_ID id
     *      @return warehouse
     */
    public static MWarehouse get(Properties ctx, int M_Warehouse_ID) {

        Integer		key		= new Integer(M_Warehouse_ID);
        MWarehouse	retValue	= (MWarehouse) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        //
        retValue	= new MWarehouse(ctx, M_Warehouse_ID, null);
        s_cache.put(key, retValue);

        return retValue;

    }		// get

    /**
     *      Get Default Locator
     *      @return (first) default locator
     */
    public MLocator getDefaultLocator() {

        MLocator[]	locators	= getLocators(false);

        for (int i = 0; i < locators.length; i++) {

            if (locators[i].isDefault() && locators[i].isActive()) {
                return locators[i];
            }
        }

        log.warning("No default locator for " + getName());

        // No Default - first one
        if (locators.length > 0) {
            return locators[0];
        }

        // No Locator - create one
        MLocator	loc	= new MLocator(this, "Standard");

        loc.setIsDefault(true);
        loc.save();

        return loc;

    }		// getLocators

    /**
     *      Get Warehouses for Org
     *      @param ctx context
     *      @param AD_Org_ID id
     *      @return warehouse
     */
    public static MWarehouse[] getForOrg(Properties ctx, int AD_Org_ID) {

        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM M_Warehouse WHERE AD_Org_ID=? ORDER BY Created";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Org_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MWarehouse(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getForOrg", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MWarehouse[]	retValue	= new MWarehouse[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get

    /**
     *      Get Locators
     *      @param reload if true reload
     *      @return array of locators
     */
    public MLocator[] getLocators(boolean reload) {

        if (!reload && (m_locators != null)) {
            return m_locators;
        }

        //
        String	sql	= "SELECT * FROM M_Locator WHERE M_Warehouse_ID=? ORDER BY X,Y,Z";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getM_Warehouse_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MLocator(getCtx(), rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLocators", e);
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
        m_locators	= new MLocator[list.size()];
        list.toArray(m_locators);

        return m_locators;

    }		// getLocators
}	// MWarehouse



/*
 * @(#)MWarehouse.java   02.jul 2007
 * 
 *  Fin del fichero MWarehouse.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
