package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.model.MBPartner;
import org.openXpertya.util.DB;
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
		String sql = "SELECT c_bpartner_id FROM c_bpartner WHERE ad_client_id = ? AND iscustomer = 'Y' AND socreditstatus <> '"
				+ MBPartner.SOCREDITSTATUS_NoCreditCheck + "' ORDER BY value";
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
		log.printDebug("All BP", "Inicio");
		try{
			while(rs.next()){
				Trx.getTrx(get_TrxName()).start();
				bpartner = new MBPartner(getCtx(), rs.getInt("C_BPartner_ID"), get_TrxName());
				bpartnerBalanceProcess.setBpartner(bpartner);
				log.printDebug(bpartner.getValue(), "Inicio");
				try{
					bpartnerBalanceProcess.doIt();
					updated++;
					Trx.getTrx(get_TrxName()).commit();
				} catch(Exception e1){
					errors.put(bpartner.getValue(), e1.getMessage());
					Trx.getTrx(get_TrxName()).rollback();
				}
				log.printDebug(bpartner.getValue(), "End");
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
		log.printDebug("All BP", "End");
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
	
	protected boolean isUpdateBalance(){
		return true;
	}
	
	protected boolean isUpdateStatus(){
		return true;
	}
}
