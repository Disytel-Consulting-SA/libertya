package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MCheckCuitControl extends X_C_CheckCuitControl {

	/**
	 * @param orgID
	 * @param trxName
	 * @return El monto límite inicial para cheque por CUIT 
	 */
	public static BigDecimal getInitialCheckLimit(Integer orgID, String trxName){
		return DB.getSQLValueBD(trxName,
				"SELECT initialchecklimit FROM ad_orginfo WHERE ad_org_id = ?",
				orgID);
	}
	
	/**
	 * @return true si el control de cuit está activado para la organización de
	 *         login, false caso contrario
	 */
	public static boolean isCheckCUITControlActive(){
		return isCheckCUITControlActive(Env.getCtx());
	}
	
	/**
	 * @param ctx
	 * @return true si el control de cuit está activado para la organización de
	 *         login, false caso contrario
	 */
	public static boolean isCheckCUITControlActive(Properties ctx){
		return isCheckCUITControlActive(ctx, null);
	}
	
	/**
	 * @param ctx
	 * @param trxName
	 * @return true si el control de cuit está activado para la organización de
	 *         login, false caso contrario
	 */
	public static boolean isCheckCUITControlActive(Properties ctx, String trxName){
		return isCheckCUITControlActive(ctx, Env.getAD_Org_ID(ctx), trxName);
	}
	
	/**
	 * @param ctx
	 * @param orgID
	 * @return true si el control de cuit está activado para la organización
	 *         parámetro, false caso contrario
	 */
	public static boolean isCheckCUITControlActive(Properties ctx, Integer orgID, String trxName){
		return DB.getSQLValueString(trxName,
				"SELECT checkcuitcontrol FROM ad_orginfo WHERE ad_org_id = ?",
				orgID).equals("Y");
	}
	
	public MCheckCuitControl(Properties ctx, int C_CheckCuitControl_ID,
			String trxName) {
		super(ctx, C_CheckCuitControl_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCheckCuitControl(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// CUIT Existente
		if(Util.isEmpty(getCUIT(), true)){
			log.saveError("InvalidCUIT",Msg.translate(getCtx(),"RequiredCUIT"));
			return false;
		}
		// CUIT válido
		if(!CalloutInvoiceExt.ValidarCUIT(getCUIT().trim())){ 
			log.saveError("InvalidCUIT", "");
			return false;
		}
		// La organización seteada debe tener el control de cuit activado
		if (!MCheckCuitControl.isCheckCUITControlActive(getCtx(),
				getAD_Org_ID(), get_TrxName())) {
			log.saveError("CheckControlCUITNotActivated", "");
			return false;
		}
		// No se permite agregar mismo CUIT para la misma Organización
		if (PO.existRecordFor(getCtx(), get_TableName(),
				"ad_org_id = ? AND upper(trim(cuit)) = upper(trim('"+getCUIT()+"'))"
						+ (newRecord ? "" : " AND c_checkcuitcontrol_id <> ?"),
				(newRecord ? new Object[] { getAD_Org_ID() }
						: new Object[] { getAD_Org_ID(), getID() }),
				get_TrxName())) {
			log.saveError("SameCUITInOrg", "");
			return false;
		}
		
		// Si se modificó el monto, no se debe permitir que sobrepase el límite
		// asignado por perfil
		MRole role = MRole.get(getCtx(), Env.getAD_Role_ID(getCtx()));
		if ((newRecord || is_ValueChanged("CheckLimit"))
				&& role.getControlCUITLimit().compareTo(BigDecimal.ZERO) > 0
				&& getCheckLimit().compareTo(role.getControlCUITLimit()) > 0) {
			log.saveError(Msg.getMsg(getCtx(), "CheckLimitSurpassRoleLimit",
					new Object[] { role.getControlCUITLimit() }), "");
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param toDate
	 * @return la suma de los cheques con fecha de vencimiento mayor a la fecha
	 *         parámetro
	 */
	public BigDecimal getBalance(Timestamp toDate) throws Exception{
		String sql = "SELECT coalesce(sum(payamt),0) as balance "
				+ "FROM c_payment " + "WHERE tendertype = 'K' "
				+ "		AND isreceipt = 'Y' " 
				+ "		AND ad_org_id = ? "
				+ " 	AND docstatus NOT IN ('DR') "
				+ "		AND upper(trim(a_cuit)) = upper('" + getCUIT() + "') "
				+ " 	AND date_trunc('day', duedate) >= date_trunc('day', ?::timestamp) ";
		PreparedStatement ps = DB.prepareStatement(sql, get_TrxName());
		ps.setInt(1, getAD_Org_ID());
		ps.setTimestamp(2, toDate);
		ResultSet rs = ps.executeQuery();
		BigDecimal balance = BigDecimal.ZERO;
		if(rs.next()){
			balance = rs.getBigDecimal("balance");
		}
		rs.close();
		ps.close();
		return balance;
	}
}
