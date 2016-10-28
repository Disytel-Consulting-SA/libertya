/*
 * @(#)MClientInfo.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;

/**
 *  Client Info Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MClientInfo.java,v 1.9 2005/03/11 20:28:36 jjanke Exp $
 */
public class MClientInfo extends X_AD_ClientInfo {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_ClientInfo", 2);

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MClientInfo.class);

    /** Account Schema */
    private MAcctSchema	m_acctSchema	= null;

    /** New Record */
    private boolean	m_createNew	= false;

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MClientInfo(Properties ctx, int ignored, String trxName) {

        super(ctx, ignored, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MClientInfo

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MClientInfo(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MClientInfo

    /**
     *      Parent Constructor
     *      @param client client
     *      @param AD_Tree_Org_ID org tree
     *      @param AD_Tree_BPartner_ID bp tree
     *      @param AD_Tree_Project_ID project tree
     *      @param AD_Tree_SalesRegion_ID sr tree
     *      @param AD_Tree_Product_ID product tree
     * @param AD_Tree_Campaign_ID
     * @param AD_Tree_Activity_ID
     * @param trxName
     */
    public MClientInfo(MClient client, int AD_Tree_Org_ID, int AD_Tree_BPartner_ID, int AD_Tree_Project_ID, int AD_Tree_SalesRegion_ID, int AD_Tree_Product_ID, int AD_Tree_Campaign_ID, int AD_Tree_Activity_ID, String trxName) {

        super(client.getCtx(), 0, trxName);
        setAD_Client_ID(client.getAD_Client_ID());	// to make sure
        setAD_Org_ID(0);
        setAcct2_Active(false);
        setAcct3_Active(false);
        setIsDiscountLineAmt(false);

        //
        setAD_Tree_Menu_ID(10);		// HARDCODED

        //
        setAD_Tree_Org_ID(AD_Tree_Org_ID);
        setAD_Tree_BPartner_ID(AD_Tree_BPartner_ID);
        setAD_Tree_Project_ID(AD_Tree_Project_ID);
        setAD_Tree_SalesRegion_ID(AD_Tree_SalesRegion_ID);
        setAD_Tree_Product_ID(AD_Tree_Product_ID);
        setAD_Tree_Campaign_ID(AD_Tree_Campaign_ID);
        setAD_Tree_Activity_ID(AD_Tree_Activity_ID);

        //
        m_createNew	= true;

    }		// MClientInfo

    /**
     *      Save
     *      @return true if saved
     */
    public boolean save() {

        if (m_createNew) {
            return super.save();
        }

        return saveUpdate();

    }		// save
    
    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
		// Si cambió el valor del vencimiento de claves, entonces seteo el valor
		// por defecto de cada usuario a la fecha de hoy si se activa el
		// vencimiento de fechas, null caso contrario
    	if(is_ValueChanged("PasswordExpirationActive")){
			// Actualización de última fecha de modificación de clave de los
			// usuarios
    		if(isPasswordExpirationActive()){
    			DB.executeUpdate(
    					"UPDATE AD_User SET lastpasswordchangedate = current_date WHERE lastpasswordchangedate is null AND ad_client_id = "
    							+ getAD_Client_ID(), get_TrxName());
    		}
			Env.setContext(getCtx(), "#PasswordExpirationActive",
					isPasswordExpirationActive() ? "Y" : "N");
    	}
    	
    	return true;
    }

    //~--- get methods --------------------------------------------------------

    /**
     *      Get optionally cached client
     *      @param ctx context
     *      @return client
     */
    public static MClientInfo get(Properties ctx) {
        return get(ctx, Env.getAD_Client_ID(ctx));
    }		// get

    public static MClientInfo get(Properties ctx, int AD_Client_ID) {
    	return get(ctx, AD_Client_ID, null);
    }
    
    /**
     *      Get Client Info
     *      @param ctx context
     *      @param AD_Client_ID id
     *      @return Client Info
     */
    public static MClientInfo get(Properties ctx, int AD_Client_ID, String trxName) {

        Integer		key	= new Integer(AD_Client_ID);
        MClientInfo	info	= (MClientInfo) s_cache.get(key);

        if (info != null) {
            return info;
        }

        //
        String			sql	= "SELECT * FROM AD_ClientInfo WHERE AD_Client_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, AD_Client_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                info	= new MClientInfo(ctx, rs, trxName);
                s_cache.put(key, info);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, "get", ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //
        return info;

    }		// get

    /**
     *      Get Default Accounting Currency
     *      @return currency or 0
     */
    public int getC_Currency_ID() {

        if (m_acctSchema == null) {
            getMAcctSchema1();
        }

        if (m_acctSchema != null) {
            return m_acctSchema.getC_Currency_ID();
        }

        return 0;

    }		// getC_Currency_ID

    /**
     *      Get primary Acct Schema
     *      @return acct schema
     */
    public MAcctSchema getMAcctSchema1() {

        if ((m_acctSchema == null) && (getC_AcctSchema1_ID() != 0)) {
            m_acctSchema	= new MAcctSchema(getCtx(), getC_AcctSchema1_ID(), null);
        }

        return m_acctSchema;

    }		// getMAcctSchema1
    
    @Override
    protected boolean beforeSave(boolean newRecord) {
		// El valor de la columna de límite de crédito no puede ser menor a la
		// suma de todos los límites de las organizaciones
		if (is_ValueChanged("CuitControlCheckLimit")
				&& getCuitControlCheckLimit() != null) {
			BigDecimal checkLimitAllOrgs = DB
					.getSQLValueBD(
							get_TrxName(),
							"SELECT coalesce(sum(initialchecklimit),0) FROM ad_orginfo WHERE ad_client_id = ?",
							getAD_Client_ID());
			checkLimitAllOrgs = checkLimitAllOrgs != null ? checkLimitAllOrgs
					: BigDecimal.ZERO;
			if(checkLimitAllOrgs.compareTo(getCuitControlCheckLimit()) > 0){
				log.saveError("SaveError", Msg
						.getMsg(getCtx(),
								"CUITControlClientCheckLimitSurpassOrgs",
								new Object[] { getCuitControlCheckLimit(),
										checkLimitAllOrgs }));
			}
    	}
		
		// Aplicación de Cajas diarias
		if(!isPOSJournalActive()){
			setPOSJournalApplication(null);
		} 
		else if(Util.isEmpty(getPOSJournalApplication())){
			// Debe tener una configuración de aplicación
			log.saveError("SaveError", Msg.getMsg(getCtx(), "NoPOSJournalApplication"));
			return false;
		}
		
		return true;
	} 
}	// MClientInfo



/*
 * @(#)MClientInfo.java   02.jul 2007
 * 
 *  Fin del fichero MClientInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
