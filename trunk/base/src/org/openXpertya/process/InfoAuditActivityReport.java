package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class InfoAuditActivityReport extends SvrProcess {

	Timestamp dateFrom;
	Timestamp dateTo;
	int roleID;
	int userID;
	String sortCriteria;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (name.equalsIgnoreCase("AD_Role_ID")) {
				roleID = para[i].getParameterAsInt();
			}
			else if (name.equalsIgnoreCase("AD_User_ID")) {
				userID = para[i].getParameterAsInt();
			} 
			else if (name.equalsIgnoreCase("DateActivity")) {
				dateFrom = (Timestamp) para[i].getParameter();
				dateTo = (Timestamp) para[i].getParameter_To();
			}
			else if  (name.equalsIgnoreCase("SortCriteria")) {
				sortCriteria = (String)para[i].getParameter();
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		
		// Validar argumentos para no exceder tiempos de respuesta
		long interval = dateTo.getTime() - dateFrom.getTime();
		long aWeek = 7L * 24L * 3600L * 1000L;
		long aMonth = 30L * 24L * 3600L * 1000L;
		// no mas de una semana si no se especifico usuario
		if (userID == 0 && interval > aWeek) {
			throw new Exception("Si no se especifica un usuario, el periodo maximo a consultar es de una semana");
		}
		// no mas de un mes si no se especifico usuario
		if (userID > 0 && interval > aMonth) {
			throw new Exception("Si se especifica un usuario, el periodo maximo a consultar es de un mes");
		}
		// el usuario pertenece al perfil especificado?
		if (userID > 0 && DB.getSQLValue(null, "SELECT count(1) FROM AD_User_Roles WHERE AD_User_ID = " + userID + " AND AD_Role_ID = " + roleID ) == 0) {
			throw new Exception("El usuario especificado no pertenece al perfil indicado");
		}
		
		// delete old rows
		DB.executeUpdate("DELETE FROM T_InfoAuditActivity WHERE DATECREATED < ('now'::text)::timestamp(6) - interval '2 days'");
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_InfoAuditActivity WHERE AD_PInstance_ID = " + getAD_PInstance_ID());
		
		// Armado del query
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM v_audit_activity(")
			.append(userID).append(", ")
			.append(roleID).append(", ")
			.append("?, ?) ")
			.append("ORDER BY " + ("D".equals(sortCriteria)?"CREATED":"USUARIO"));
		
		// Obtencion de los registros
		PreparedStatement pstmt = DB.prepareStatement(sql.toString());
		pstmt.setTimestamp(1, dateFrom);
		pstmt.setTimestamp(2, dateTo);
		ResultSet rs = pstmt.executeQuery(); 
		
		// Impacto en tabla T
		int i = 0;
		StringBuffer insertSQL = new StringBuffer();
		while (rs.next()) {
			i++;
			insertSQL.append(" INSERT INTO T_InfoAuditActivity (")
						.append(" ad_pinstance_id, 	ad_client_id, 	ad_org_id, 		reportline, 		dateactivity,			ad_user_id, 	ad_role_id, 	usuario, ")
						.append(" cant_conexiones,  primer_sesion,  ultima_sesion,  ultima_desconexion, cant_informes_procesos, ")
						.append(" cant_pedidos, 	cant_facturas,	 cant_op_opa, 	cant_liq_tarjeta, 	cant_extractos, sortcriteria ")
						.append(" ) VALUES (")
						.append(getAD_PInstance_ID() + 	", ")
						.append(getAD_Client_ID() + 	", ")
						.append("0, ")
						.append(i + ", ")
						.append("'" + rs.getTimestamp("created") + "', ")
						.append(rs.getInt("ad_user_id") + ", ")
						.append(roleID + ", ")
						.append("'" + rs.getString("usuario") + "', ")
						.append(rs.getInt("cant_conexiones") + ", ")
						.append("'" + rs.getString("primer_sesion") + "', ")
						.append("'" + rs.getString("ultima_sesion") + "', ")
						.append("'" + rs.getString("ultima_desconexion") + "', ")
						.append(rs.getInt("cant_informes_procesos") + ", ")
						.append(rs.getInt("cant_pedidos") + ", ")
						.append(rs.getInt("cant_facturas") + ", ")
						.append(rs.getInt("cant_op_opa") + ", ")
						.append(rs.getInt("cant_liq_tarjeta") + ", ")
						.append(rs.getInt("cant_extractos") + ", ")
						.append("'" + sortCriteria + "'); ");
		}
		
		if (i>0)
			DB.executeUpdate(insertSQL.toString());
		
		return "";
	}

}
