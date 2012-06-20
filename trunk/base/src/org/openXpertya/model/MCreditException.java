package org.openXpertya.model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MCreditException extends X_C_CreditException {

	// Métodos de clase

	/**
	 * @param ctx
	 *            contexto
	 * @param org
	 *            organización
	 * @param bp
	 *            entidad comercial
	 * @param date
	 *            fecha de excepción
	 * @param exceptionType
	 *            tipo de excepción
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return una excepción de crédito en base a los parámetros, null caso
	 *         contrario
	 */
	public static MCreditException get(Properties ctx, MOrg org, MBPartner bp,
			Date date, String exceptionType, String trxName) {
		return (MCreditException) PO
				.findFirst(
						ctx,
						Table_Name,
						"(ad_org_id = ? OR ad_org_id = 0) AND (c_bpartner_id = ?) AND (? BETWEEN exceptionstartdate AND exceptionenddate) AND (exceptiontype = ?)",
						new Object[] { org.getID(), bp.getID(), date,
								exceptionType },
						new String[] { "ad_org_id desc" }, trxName);
	}	
	
	// Constructores
	
	public MCreditException(Properties ctx, int C_CreditException_ID,
			String trxName) {
		super(ctx, C_CreditException_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCreditException(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave( boolean newRecord ) {
		// Fecha de inicio posterior a la de fin?
		if(getExceptionStartDate().after(getExceptionEndDate())){
			log.saveError("StartDateAfterEndDate", "");
			return false;
		}
		// No permitir excepciones del mismo tipo y misma organización que se
		// superponga en el rango de fechas, que sea distinto de esta instancia
		// también
		if(isSuperimposedCreditException(newRecord)){
			log.saveError("ExistsACreditException", "");
			return false;
		}
		
		return true;
	}

	/**
	 * @param newRecord
	 *            boolean que determina si es nuevo registro
	 * @return true si este registro se superpone por fecha de inicio o fin con
	 *         otra excepción del mismo tipo, false caso contrario
	 */
	protected boolean isSuperimposedCreditException(boolean newRecord){
		boolean superimposed = false;
		StringBuffer sql = new StringBuffer("SELECT count(1) FROM ");
		sql.append(Table_Name);
		sql.append(" WHERE ((? between exceptionstartdate AND exceptionenddate) ");
		sql.append(" OR (? between exceptionstartdate AND exceptionenddate)) ");
		sql.append(" AND (ad_org_id = ?) ");
		sql.append(" AND (c_bpartner_id = ?) ");
		sql.append(" AND (exceptiontype = ?) ");
		if(!newRecord){
			sql.append(" AND (c_creditexception_id <> ?) ");	
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			ps.setTimestamp(i++, getExceptionStartDate());
			ps.setTimestamp(i++, getExceptionEndDate());
			ps.setInt(i++, getAD_Org_ID());
			ps.setInt(i++, getC_BPartner_ID());
			ps.setString(i++, getExceptionType());
			if(!newRecord){
				ps.setInt(i++, getID());
			}
			rs = ps.executeQuery();
			if(rs.next()){
				superimposed = rs.getInt(1)	> 0; 
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(Util.isEmpty(e.getMessage()) ? e.getCause().getMessage()
					: e.getMessage());
			superimposed = true;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
				log.severe(Util.isEmpty(e2.getMessage()) ? e2.getCause().getMessage()
						: e2.getMessage());
				superimposed = true;
			}
		}
		return superimposed;
	}
	
}
