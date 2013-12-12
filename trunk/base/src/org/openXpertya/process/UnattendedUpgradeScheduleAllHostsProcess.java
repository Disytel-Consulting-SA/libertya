package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.X_AD_UnattendedUpgradeHost;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class UnattendedUpgradeScheduleAllHostsProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {
		// cantidad eliminados
		int deleted = 0;
		// cantidad generados
		int inserted = 0;
		// Eliminar schedule de hosts existente
		deleted = DB.executeUpdate("DELETE FROM AD_UnattendedUpgradeHost WHERE AD_UnattendedUpgrade_ID = " + getRecord_ID(), get_TrxName());
		PreparedStatement pstmt = DB.prepareStatement("SELECT AD_Org_ID FROM AD_ReplicationHost WHERE isActive = 'Y'", get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			X_AD_UnattendedUpgradeHost unUpHost = new X_AD_UnattendedUpgradeHost(getCtx(), 0, get_TrxName());
			unUpHost.setAD_UnattendedUpgrade_ID(getRecord_ID());
			unUpHost.setAD_TargetOrg_ID(rs.getInt("AD_Org_ID"));
			unUpHost.setStatus(X_AD_UnattendedUpgradeHost.STATUS_Pending);
			if (!unUpHost.save()) 
				throw new Exception(CLogger.retrieveErrorAsString());
			inserted++;
		}
		return "Host de configuarcion previa eliminados:" + deleted + ". Hosts generados: " + inserted;		
	}

}
