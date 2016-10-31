package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.java.cwm.objectmodel.instance.Instance;
import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class MassiveUpdateBPartnerBalance extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		String sql = getQuery();
		PreparedStatement ps = DB.prepareStatement(sql, get_TrxName());
		ps.setInt(1, getAD_Client_ID());
		ResultSet rs = ps.executeQuery();
		UpdateBPartnerBalance bpartnerBalanceProcess = new UpdateBPartnerBalance(
				getCtx(), get_TrxName());
		bpartnerBalanceProcess.setUpdateBalance(isUpdateBalance());
		bpartnerBalanceProcess.setUpdateStatus(isUpdateStatus());
		Integer updated = 0;
		Map<String, String> errors = new HashMap<String, String>();
		MBPartner bpartner;
		try{
			while(rs.next()){
				Trx.getTrx(get_TrxName()).start();
				bpartner = new MBPartner(getCtx(), rs.getInt("C_BPartner_ID"), get_TrxName());
				bpartnerBalanceProcess.setBpartner(bpartner);
				try{
					bpartnerBalanceProcess.doIt();
					updated++;
					Trx.getTrx(get_TrxName()).commit();
				} catch(Exception e1){
					errors.put(bpartner.getValue(), bpartner.getValue()+": "+e1.getMessage());
					Trx.getTrx(get_TrxName()).rollback();
				}
			}
		} catch(Exception e){
			throw e;
		} finally{
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return getMsg(updated, errors);
	}

	private String getMsg(Integer updated, Map<String, String> errors){
		HTMLMsg msg = new HTMLMsg();
		HTMLMsg.HTMLList allList = msg.new HTMLList("all", "ul");
		msg.createAndAddListElement("Success", Msg.parseTranslation(getCtx(),
				"@UpdatedBPartnersSuccessfully@:" + updated), allList);
		msg.createAndAddListElement("Errors", Msg.parseTranslation(getCtx(),
				"@UpdatedBPartnersWrong@:" + errors.size()), allList);
		msg.addList(allList);
		if(errors.size() > 0){
			HTMLMsg.HTMLList errorList = msg.new HTMLList("allErrors", "ol");
			for (String value : errors.keySet()) {
				msg.createAndAddListElement(value, errors.get(value), errorList);
			}
			msg.addList(errorList);
		}
		return msg.toString();
	}
	
	protected String getQuery(){
		return "SELECT c_bpartner_id "
					+ "FROM c_bpartner "
					+ "WHERE ad_client_id = ? "
					+ "			AND iscustomer = 'Y' "
					+ "			AND isactive = 'Y' "
					+ "			AND so_creditlimit > 0 "
					+ "			AND socreditstatus <> '"
					+ MBPartner.SOCREDITSTATUS_NoCreditCheck + "' ";
	}
	
	protected boolean isUpdateBalance(){
		return true;
	}
	
	protected boolean isUpdateStatus(){
		return false;
	}
	
	/**
	 * Entrada principal desde terminal
	 */
	public static void main(String[] args) {
		// Help
		if (args!=null) {
			for (String arg : args) {
				if (arg.toLowerCase().startsWith("-h")) {
					System.out.println(" No recibe parámetros ");
					System.exit(0);
				}
			}
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) { 
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )) {
	  		System.err.println("ERROR: Error al iniciar la configuracion... Postgres esta levantado?");
	  		System.exit(1);
	  	}
		
		// Configuracion: Compañía la cual tiene configurado replicacion.  Si no hay configuración, no hay nada mas que hacer 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null ||
	  	    Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID") <= 0 || Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID") <= 0) {
	  		System.err.println("Configuracion faltante o incorrecta en AD_ReplicationHost");
	  		return;
	  	}

	  	// Si no hay configuración de tablas de replicación, simplemente no hará nada
		String trxName = Trx.createTrxName();
		MassiveUpdateBPartnerBalance mubb = new MassiveUpdateBPartnerBalance();
		try {
			Trx.getTrx(trxName).start();
			mubb.prepare();
			mubb.doIt();
			Trx.getTrx(trxName).commit();
			System.out.println("MassiveUpdateBPartnerBalance OK");
		}
		catch (Exception e) {
			Trx.getTrx(trxName).rollback();
			System.out.println("MassiveUpdateBPartnerBalance ERROR: " + e.toString());
			System.exit(1);
		}
		finally {
			Trx.getTrx(trxName).close();			
		}
	}
}
