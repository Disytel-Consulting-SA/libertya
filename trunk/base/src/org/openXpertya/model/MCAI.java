package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

public class MCAI extends X_C_CAI {

	public MCAI(Properties ctx, int C_CAI_ID, String trxName) {
		super(ctx, C_CAI_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCAI(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Centralización de validaciones de CAI
	 * @throws Exception
	 */
	public static void doCAIValidations(Properties ctx, Integer docTypeID, Timestamp documentDate, PO document,
			boolean setCAIControlData, String trxName) throws Exception {
		// Si el tipo de documento controla CAI, entonces se deben realizar las
		// siguientes validaciones
		// 1) Debe existir una configuración válida en la ventana de Gestión de CAI
		// 2) La configuración de la ventana de Gestión de CAI debe ser consistente con
		// los valores de este comprobante
		// 3) El CAI no debe estar vencido
		// (1)
		PreparedStatement ps = null;
		ResultSet rs = null;
		X_C_CAI cai = null;
		try {
			String sql = "select c.* " + 
						" from c_cai c " + 
						" join c_cai_doctype cd on cd.c_cai_id = c.c_cai_id " + 
						" where cd.c_doctype_id = ? and ?::date between c.validfrom::date and c.datecai::date " + 
						"	and cd.isactive = 'Y' and c.isactive = 'Y'";
			ps = DB.prepareStatement(sql, trxName, true);
			ps.setInt(1, docTypeID);
			ps.setTimestamp(2, documentDate);
			rs = ps.executeQuery();
			if(rs.next()) {
				cai = new X_C_CAI(ctx, rs, trxName);
				if(setCAIControlData) {
					document.set_Value("CAI", cai.getCAI());
					document.set_Value("DateCAI", cai.getDateCAI());
				}
			}
			else {
				throw new Exception(Msg.getMsg(ctx, "ControlCAINotExistsFor"));
			}
			
			String caiNo = (String)document.get_Value("CAI");
			Timestamp dateCAI = (Timestamp)document.get_Value("DateCAI");

			// (2)
			if(!setCAIControlData) {
				if(caiNo == null 
						|| dateCAI == null 
						|| !caiNo.equals(cai.getCAI()) 
						|| !TimeUtil.isSameDay(dateCAI, cai.getDateCAI())) {
					throw new Exception(Msg.getMsg(ctx, "DocumentCAINotEqualControlCAI", new Object[] { cai.getCAI(),
							(new SimpleDateFormat("dd-MM-yyyy")).format(cai.getDateCAI()) }));
				}
			}
			
			// (3)
			if(!dateCAI.after(Env.getDate())
					&& !TimeUtil.isSameDay(dateCAI, Env.getDate())) {
				throw new Exception(Msg.getMsg(ctx, "ControlCAIExpired"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
}
