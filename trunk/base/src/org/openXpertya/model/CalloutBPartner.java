package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class CalloutBPartner extends CalloutEngine {

	public String contactInterestUser( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		
		if(isCalloutActive())
			return "";
		
		int c_BPartner_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_ID");
		Integer ad_User_ID = (Integer)mTab.getValue("AD_User_ID");
		
		setCalloutActive(true);
		
		// Validación para no permitir crear áreas de interés erróneamente a usuarios
		// de otras entidades comerciales, o si la entidad comercial no existe en el 
		// contexto.
		if (c_BPartner_ID == 0 || ad_User_ID == null) {
			mTab.setValue("AD_User_ID", null);
		} else {
			boolean validUser = false;
			MBPartner bPartner = new MBPartner(ctx, c_BPartner_ID, null);
			MUser[] bpContacts = bPartner.getContacts(false); 
			for (int i = 0; i < bpContacts.length && !validUser; i++) {
				MUser user = bpContacts[i];
				if (user.getID() == ad_User_ID)
					validUser = true;
			}
			
			if (!validUser)
				mTab.setValue("AD_User_ID", null);
		}
				
		setCalloutActive(false);
		
		return "";
	}
	
	/**
	 * Verifica si ya existe una entidad comercial con ese cuit
	 * @param ctx
	 * @param cuit
	 * @param c_BPartner_id
	 * @return el nombre de la entidad comercial que tiene ese cuit, si es que existe alguno 
	 */
	public static String existCUIT(Properties ctx,String cuit, int c_BPartner_ID){
		String vCuit = cuit.replaceAll("-","");
		String sql = " SELECT Name " +
					 " FROM C_BPartner " +
				     " WHERE replace(TaxID,'-','') = ? AND " +
				     "       C_BPartner_ID <> ? AND " +
				     "       IsActive = 'Y' AND AD_Client_ID = ?";
				     
		return (String)DB.getSQLObject(null, sql, new Object[] {vCuit, c_BPartner_ID, Env.getAD_Client_ID(ctx)});
	}
	
	
	public String cuit( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		
		String cuit = (String)mTab.getValue("TaxID");
		Integer c_BPartner_ID = ((Integer)mTab.getValue("C_BPartner_ID") != null)?(Integer)mTab.getValue("C_BPartner_ID"):0;
		
		String bpName = null;
		// Validación de CUIT existente. En caso de que una EC ya contenga el CUIT
		// ingresado se configura un mensaje de advertencia en la Pestaña para que 
		// muestre antes de guardar el registro.
		if (cuit != null && cuit.length() > 0 && CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
			bpName = existCUIT(ctx,cuit,c_BPartner_ID);
		}
        
		// Si exite una entidad comercial con el cuit de la actual se setea
		// el mensaje de advertencia.
		if (bpName != null) {
			String msg = Msg.getMsg(ctx, "ExistentBPartnerCUIT", new Object[] {cuit, bpName});
			mTab.setCurrentRecordWarning(msg);
		} else
			mTab.clearCurrentRecordWarning();

		return "";
	}
}
