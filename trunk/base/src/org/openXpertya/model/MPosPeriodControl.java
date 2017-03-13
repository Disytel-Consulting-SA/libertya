package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level; 

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MPosPeriodControl extends X_C_PosPeriodControl {
	
	private static CLogger		s_log = CLogger.getCLogger (MPosPeriodControl.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1775402070669399784L;


	public MPosPeriodControl(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	public MPosPeriodControl(Properties ctx, int C_PosPeriodControl_ID, String trxName) {
		super(ctx, C_PosPeriodControl_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	protected boolean beforeSave( boolean newRecord ) {
		if (!validateDuplicateDocType()) {
			log.saveError("Error", Msg.getMsg(getCtx(), "Duplicated DocType Reference"));
			return false;
		}
		return true;
		
	}
	
	private boolean validateDuplicateDocType() {
		String sql = "SELECT * FROM C_PosPeriodControl WHERE C_PeriodControl_ID = ? AND C_DocType_ID = ?";
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
    	try {
    		pstmt = DB.prepareStatement(sql, null);
    		pstmt.setInt(1, getC_PeriodControl_ID());
    		pstmt.setInt(2, getC_DocType_ID());
    		rs = pstmt.executeQuery();
    		while (rs.next()) {
    			return false;
    		}
    		
    	} catch (SQLException e) {
    		s_log.log(Level.SEVERE, "Error loading PosPeriodControls. C_PeriodControl_ID = " + getC_PeriodControl_ID(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {	}
		}
		
		return true;
	}

}
