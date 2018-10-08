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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
//~--- Importaciones JDK ------------------------------------------------------
import org.openXpertya.util.Util;

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

            MUserOrgAccess.createForOrg(this);
            
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
    	ArrayList<MOrg> orgs = new ArrayList<MOrg>();
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
		
		// dREHER, agrego validaciones de control de padre/hijo
		
		// Pasar a org padre la org de login
		if(this.getAD_Org_ID()==Env.getAD_Org_ID(getCtx())){
			if(this.isSummary()){
				// log.warning("No se puede convertir a organizacion 'Padre' la organizacion de login!");
				log.saveError("SaveError", Msg.translate(Env.getCtx(),
				"No se puede convertir a organizacion 'Padre' la organizacion de login!"));
				return false;
			}
		}
		
		if(!newRecord && is_ValueChanged("IsSummary")){
			// Pasar a org Padre una org con documentos asociados
			if(isSummary()){
				List<String> documentTableNames = new ArrayList<String>();
				documentTableNames.add(X_C_Order.Table_Name);
				documentTableNames.add(X_C_Invoice.Table_Name);
				documentTableNames.add(X_C_Payment.Table_Name);
				documentTableNames.add(X_M_InOut.Table_Name);
				documentTableNames.add(X_M_Warehouse.Table_Name);
				boolean poseeDocumentos = false;
				for (int i = 0; i < documentTableNames.size() && !poseeDocumentos; i++) {
					poseeDocumentos = PO.existRecordFor(getCtx(),
							documentTableNames.get(i), "ad_org_id = ?",
							new Object[]{getAD_Org_ID()}, get_TrxName());
				}
				if(poseeDocumentos){
					log.saveError("SaveError", Msg.translate(Env.getCtx(),"No se puede convertir a organizacion 'Padre' una organizacion que contiene documentos asociados!"));
					return false;
				}
			}
			else{
				// Pasar a org hija una org padre que ya contiene hijas
				ArrayList<MOrg> hijas = this.getOrgsChilds();
				if(hijas.size() > 0){
					log.saveError("SaveError", Msg.translate(Env.getCtx(), "No se puede convertir a organizacion 'Hija' una organizacion que ya contiene organizaciones 'hijas!'"));
					return false;
				}
			}
		}
		
    	return true;
    }

    /*
     * dREHER jorge.dreher@gmail.com 
     * 
     * Genera un string con todos los ID de las organizaciones hojas hijas, en caso de que la organizacion actual sea del tipo carpeta isSummary='Y'
     * 
     */
     public String getOrgsChildsAsString(){
    	 
    	 String orgs = null;
    	 String orgsX = "";
    	 
    	 // LLamo a metodo getOrgsChilds, para evitar duplicidad de codigo
    	 
    	 ArrayList<MOrg> orgsA = this.getOrgsChilds();
    	 
    	 for(MOrg org : orgsA)
    		 orgsX += (!orgsX.isEmpty()?",":"") + org.getAD_Org_ID();
    		 
    	 if(orgsX != "")
    		 orgs = orgsX;
    	 
    	 return orgs;
    	 
     }

     /*
      * dREHER jorge.dreher@gmail.com 
      * 
      * Devuelve un array de organizaciones hojas hijas, en caso de que la organizacion actual sea del tipo carpeta isSummary='Y'
      * 
      */
      public ArrayList<MOrg> getOrgsChilds(){
     	 
     	 ArrayList<MOrg> orgs = new ArrayList<MOrg>();
     	 
     	 // Buscar siempre, en caso de llamarse desde una validacion, podia no funcionar
     	 // if(isSummary()){
     		 
     	 // Si estoy dando de alta, no existen orgs hijas
     	 if(getAD_Org_ID() <= 0)
     		 return orgs;
     	 
     	 log.fine("Busco organizaciones hijas para AD_Org_ID=" + getAD_Org_ID());
     	 
     		 String sql = "SELECT org.AD_Org_ID " +
     				 "FROM AD_Org AS org " +
     				 "WHERE getisnodechild(" + Env.getAD_Client_ID(getCtx()) + "," + getAD_Org_ID() + ", org.AD_Org_ID, 'OO') = 'Y'";
     		 
     		 PreparedStatement pstmt = null;
     		 ResultSet rs = null;
     		 
     		 try{
     			 
     			 log.fine("sql getOrgsChilds=" + sql);
     			 pstmt = DB.prepareStatement( sql );
     			 rs = pstmt.executeQuery();
     			 while(rs.next()){
     				 
     				 int orgID = rs.getInt("AD_Org_ID");
     				 
     				 orgs.add(new MOrg(Env.getCtx(), orgID, null));

     				 log.fine("Encontro org hija ID=" + orgID);
     				 
     			 }
     			 DB.close(rs, pstmt);

     		 }catch(SQLException ex){
     			 log.log( Level.SEVERE,sql,ex );
     		 }finally{
     			 DB.close(rs, pstmt);
     			 rs = null; pstmt=null;
     		 }
     		 
     		 
     	 // }
     	 
     	 return orgs;
     	 
      }

      public static Integer getOrgParentID(Properties ctx, Integer orgID, String trxName){
		return DB.getSQLValue(trxName, "select getnodepadre(?, ?, '"
				+ X_AD_Tree.TREETYPE_Organization + "')",
				Env.getAD_Client_ID(ctx), orgID);
      }
    
	  public static List<Integer> getParentOrgs(Properties ctx, Integer orgID, boolean includeItIfIsFolder, String trxName){
		  List<Integer> parentOrgsID = new ArrayList<Integer>();
		  if(includeItIfIsFolder){
			  MOrg org = MOrg.get(ctx, orgID);
			  if(org.isSummary()){
				  parentOrgsID.add(orgID);
			  }
		  }
		  Integer orgParentID = 0;
		  Integer auxOrgID = orgID;
		  do {
			  orgParentID = MOrg.getOrgParentID(ctx, auxOrgID, trxName);
			  parentOrgsID.add(orgParentID);
			  auxOrgID = orgParentID;
		  } while (orgParentID != null && orgParentID >= 0);
		  return parentOrgsID;
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
