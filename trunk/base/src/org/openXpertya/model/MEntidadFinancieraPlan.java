package org.openXpertya.model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MEntidadFinancieraPlan extends X_M_EntidadFinancieraPlan {

	/** Log estático */
	private static CLogger s_log = CLogger.getCLogger(MEntidadFinancieraPlan.class);

	/**
	 * Obtener los planes disponibles de la entidad financiera parámetro. Un
	 * plan es disponible cuando es válido para la fecha actual. Si la entidad
	 * financiera parámetro es null o 0 entonces se retornan todos los planes
	 * existentes activos para la compañía actual.
	 * 
	 * @param ctx
	 *            contexto
	 * @param entidadFinancieraID
	 *            id de la entidad financiera
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return lista de planes de la entidad financiera parámetro
	 */
	public static List<MEntidadFinancieraPlan> getPlansAvailables(Properties ctx, Integer entidadFinancieraID, String trxName){
		// Se buscan los planes de la entidad financiera cuyo rango de validez
		// contenga la fecha actual.
		StringBuffer sql = new StringBuffer("SELECT * "
				+ "FROM M_EntidadFinancieraPlan " + "WHERE AD_Client_ID = ? "
				+ "AND ?::date BETWEEN DateFrom::Date AND DateTo::date " + "AND IsActive = 'Y' ");
		if(!Util.isEmpty(entidadFinancieraID, true)){
			sql.append(" AND M_EntidadFinanciera_ID = "+entidadFinancieraID);
		}
		sql.append(" ORDER BY Name ASC ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MEntidadFinancieraPlan> planes = new ArrayList<MEntidadFinancieraPlan>();
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName, true);
			int i = 1;
			pstmt.setInt(i++, Env.getAD_Client_ID(ctx));
			pstmt.setTimestamp(i++, Env.getDate());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				planes.add(new MEntidadFinancieraPlan(ctx, rs, trxName));
			}
		} catch(Exception e){
			s_log.log(Level.SEVERE, "Error getting Entidad Financiera Planes.", e);
		} finally{
			try {
				if(pstmt != null)pstmt.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "Error getting Entidad Financiera Planes.", e2);
			}
		}
		return planes;
	}
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param M_EntidadFinancieraPlan_ID
	 * @param trxName
	 */
	public MEntidadFinancieraPlan(Properties ctx,
			int M_EntidadFinancieraPlan_ID, String trxName) {
		super(ctx, M_EntidadFinancieraPlan_ID, trxName);
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MEntidadFinancieraPlan(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		// La fecha final de validez no puede ser anterior a la fecha
		// inicial.
		if (getDateTo().compareTo(getDateFrom()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDateRange"));
			return false;
		}
		
		// El valor para las cuotas a cobrar debe ser mayor que cero
		if (getCuotasCobro() <= 0) {
			log.saveError("SaveError", 
				Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero", 
						new Object[] {Msg.translate(getCtx(), "CuotasCobro")}
				)
			);
			return false;
		}

		// El valor para las cuotas a pagar debe ser mayor que cero
		if (getCuotasPago() <= 0) {
			log.saveError("SaveError", 
				Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero", 
						new Object[] {Msg.translate(getCtx(), "CuotasPago")}
				)
			);
			return false;
		}

		// El valor para los días de acreditación debe ser mayor o igual que cero
		if (getAccreditationDays() < 0) {
			log.saveError("SaveError", 
				Msg.getMsg(getCtx(), "FieldUnderZeroError", 
						new Object[] {Msg.translate(getCtx(), "AccreditationDays")}
				)
			);
			return false;
		}

		return true;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
