package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MLetraComprobante extends X_C_Letra_Comprobante {

	private static CLogger log = CLogger.getCLogger(MLetraComprobante.class);
	
	public static MLetraComprobante buscarLetraComprobante(String letra, String trxName) {
		MLetraComprobante ret = null;
		
		try {
			String sql = " SELECT C_Letra_Comprobante_ID FROM C_Letra_Comprobante WHERE Letra = ? ";
			sql = MRole.getDefault().addAccessSQL( sql, "C_Letra_Comprobante", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO ) + "  ORDER BY C_Letra_Comprobante.AD_Client_ID DESC, C_Letra_Comprobante.AD_Org_ID DESC  ";
			
			CPreparedStatement pp = DB.prepareStatement( sql, trxName);
			
			pp.setString(1, letra);
			
			ResultSet rs = pp.executeQuery();
			if (rs.next())
				ret = new MLetraComprobante(Env.getCtx(), rs.getInt(1), trxName);
			
			pp.close();
			rs.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "buscarLetraComprobante", e);
		}
		
		return ret;
	}
	
	public MLetraComprobante (Properties ctx, int C_Letra_Comprobante_ID, String trxName) {
		super(ctx, C_Letra_Comprobante_ID, trxName);
	}
	
	public MLetraComprobante (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	protected boolean beforeSave(boolean newRecord) {
		
		int x = DB.getSQLValue(this.get_TrxName(), " SELECT COUNT(*) FROM C_Letra_Comprobante WHERE AD_Client_ID = ? AND Letra = ? ", this.getAD_Client_ID(), this.getLetra());
		if (x != 0) {
			log.saveError("SQLErrorNotUnique", "Letra");
			return false;
		}
		
		return true;
	}
	
}
