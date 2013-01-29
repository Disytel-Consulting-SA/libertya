/*
 * @(#)MOrg.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Organization Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MOrg.java,v 1.15 2005/04/22 05:49:11 jjanke Exp $
 */
public class MOrg extends X_AD_Org {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Org", 20);

    /** Org Info */
    private MOrgInfo	m_info	= null;

    /** Linked Business Partner */
    private Integer	m_linkedBPartner	= null;

    /**
     *      Parent Constructor
     *      @param client client
     * @param name
     */
    public MOrg(MClient client, String name) {

        this(client.getCtx(), 0, client.get_TrxName());
        setAD_Client_ID(client.getAD_Client_ID());
        setValue(name);
        setName(name);

    }		// MOrg

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Org_ID id
     * @param trxName
     */
    public MOrg(Properties ctx, int AD_Org_ID, String trxName) {

        super(ctx, AD_Org_ID, trxName);

        if (AD_Org_ID == 0) {

            // setValue (null);
            // setName (null);
            setIsSummary(false);
        }

    }		// MOrg

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MOrg(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MOrg

    /**
     *      After Delete
     *      @param success
     *      @return deleted
     */
    protected boolean afterDelete(boolean success) {

        if (success) {
            delete_Tree(MTree_Base.TREETYPE_Organization);
        }

        return true;

    }		// afterDelete

    /**
     *      After Save
     *      @param newRecord new Record
     *      @param success save success
     *
     * @return
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (!success) {
            return success;
        }

        if (newRecord) {

            // Info
            MOrgInfo	oInfo	= new MOrgInfo(this);

            oInfo.save();

            // Access
            MRoleOrgAccess.createForOrg(this);
            MRole.getDefault(getCtx(), true);		// reload

            // TreeNode
            insert_Tree(MTree_Base.TREETYPE_Organization);
        }

        // Value/Name change
        if (!newRecord && (is_ValueChanged("Value") || is_ValueChanged("Name"))) {

            MAccount.updateValueDescription(getCtx(), "AD_Org_ID=" + getAD_Org_ID(), get_TrxName());

            if ("Y".equals(Env.getContext(getCtx(), "$Element_OT"))) {
                MAccount.updateValueDescription(getCtx(), "AD_OrgTrx_ID=" + getAD_Org_ID(), get_TrxName());
            }
        }

        return true;

    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Org from Cache
     *      @param ctx context
     *      @param AD_Org_ID id
     *      @return MOrg
     */
    public static MOrg get(Properties ctx, int AD_Org_ID) {

        Integer	key		= new Integer(AD_Org_ID);
        MOrg	retValue	= (MOrg) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MOrg(ctx, AD_Org_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get
    
    
    public static MOrg[] getOfClient(Properties ctx, String trxName){
    	String sql = "SELECT * FROM ad_org WHERE ad_client_id = ?";
    	ArrayList orgs = new ArrayList();
    	PreparedStatement psmt = null;
    	
    	try{
    		psmt = DB.prepareStatement(sql, trxName);
    		psmt.setInt(1, Env.getAD_Client_ID(ctx));
    		ResultSet rs = psmt.executeQuery();
    		
    		while(rs.next()){
    			orgs.add(new MOrg(ctx,rs,trxName));
    		}
    		
    		rs.close();
            psmt.close();
            psmt = null;    	
    	}catch(Exception e){
    		
    	}
    	 try {
             if( psmt != null ) {
                 psmt.close();
             }

             psmt = null;
         } catch( Exception e ) {
             psmt = null;
         }
         
         MOrg[] retValue = new MOrg[ orgs.size()];

         orgs.toArray( retValue );

         return retValue;
    } 

    /**
     *      Get Org Info
     *      @return Org Info
     */
    public MOrgInfo getInfo() {

        if (m_info == null) {
            m_info	= MOrgInfo.get(getCtx(), getAD_Org_ID());
        }

        return m_info;

    }		// getMOrgInfo

    /**
     *      Get Linked BPartner
     *      @return C_BPartner_ID
     */
    public int getLinkedC_BPartner_ID() {

        if (m_linkedBPartner == null) {

            int	C_BPartner_ID	= DB.getSQLValue(null, "SELECT C_BPartner_ID FROM C_BPartner WHERE AD_OrgBP_ID=?", Integer.toString(getAD_Org_ID()));

            if (C_BPartner_ID < 0) {	// not found = -1
                C_BPartner_ID	= 0;
            }

            m_linkedBPartner	= new Integer(C_BPartner_ID);
        }

        return m_linkedBPartner.intValue();

    }		// getLinkedC_BPartner_ID
    
    @Override
	protected boolean beforeSave(boolean newRecord) {
    	// Validación de campo Value duplicado: no se permiten organizaciones con el mismo código.
		if (sameColumnValueValidation(get_TableName(), "Value", "AD_Org_ID",
				getValue(), newRecord, true)) {
			return false;
		}
    	return true;
    }
}	// MOrg



/*
 * @(#)MOrg.java   02.jul 2007
 * 
 *  Fin del fichero MOrg.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
