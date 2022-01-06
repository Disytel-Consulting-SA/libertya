package org.openXpertya.process;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class BusinessPartnerSalesRepChangeProcess extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		boolean isSalesRepTo = !Util.isEmpty(getParamValueAsInt("SALESREP_TO_ID"), true); 
		// Desetear el sales rep de todas las entidades comerciales
		// Y, en caso que exista, setear el nuevo
		String sql = "update c_bpartner set salesrep_id = "
				+ (isSalesRepTo ? getParamValueAsInt("SALESREP_TO_ID"):"null")
				+ " where salesrep_id = "+getParamValueAsInt("SALESREP_ID");
		int bpu = DB.executeUpdate(sql, get_TrxName());
		
		return "@UpdatedBPartnersSuccessfully@: "+bpu;
	}

}
