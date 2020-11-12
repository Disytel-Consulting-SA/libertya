package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.model.X_C_BankStatement;
import org.openXpertya.model.X_C_BankTransfer;
import org.openXpertya.model.X_C_CreditCardSettlement;
import org.openXpertya.model.X_C_ProjectIssue;
import org.openXpertya.model.X_MPC_Order;
import org.openXpertya.model.X_M_Amortization;
import org.openXpertya.model.X_M_BoletaDeposito;
import org.openXpertya.model.X_M_Inventory;
import org.openXpertya.model.X_M_Movement;
import org.openXpertya.model.X_M_Production;
import org.openXpertya.model.X_M_Requisition;
import org.openXpertya.util.DB;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Util;

public class FactAcctResetDelete extends AbstractSvrProcess {

	/** Nombre de columna de fecha por tabla */
	private static Map<String, String> dateColumnNameExceptionalByTableFact;
	
	static {
		// Nombre de columnas de fecha por tabla
		dateColumnNameExceptionalByTableFact = new HashMap<String, String>();
		
		dateColumnNameExceptionalByTableFact.put(X_C_BankStatement.Table_Name, "StatementDate");
		dateColumnNameExceptionalByTableFact.put(X_C_BankTransfer.Table_Name, "DateTrx");
		dateColumnNameExceptionalByTableFact.put(X_C_CreditCardSettlement.Table_Name, "PaymentDate");
		dateColumnNameExceptionalByTableFact.put(X_C_ProjectIssue.Table_Name, "MovementDate");
		dateColumnNameExceptionalByTableFact.put(X_M_Amortization.Table_Name, "AmortizationDate");
		dateColumnNameExceptionalByTableFact.put(X_M_BoletaDeposito.Table_Name, "FechaDeposito");
		dateColumnNameExceptionalByTableFact.put(X_M_Inventory.Table_Name, "MovementDate");
		dateColumnNameExceptionalByTableFact.put(X_M_Movement.Table_Name, "MovementDate");
		dateColumnNameExceptionalByTableFact.put(X_MPC_Order.Table_Name, "DateOrdered");
		dateColumnNameExceptionalByTableFact.put(X_M_Production.Table_Name, "MovementDate");
		dateColumnNameExceptionalByTableFact.put(X_M_Requisition.Table_Name, "DateRequired");
	}

	/** Cantidad de hechos contables eliminados por tabla */
	private Map<String, Integer> table_deleteds = new HashMap<String, Integer>();
	
	/** Cantidad de registros marcados posted = N */
	private Map<String, Integer> table_markeds = new HashMap<String, Integer>();
	
	@Override
	protected String doIt() throws Exception {
		// Obtener las tablas con la columna Posted o la tabla parámetro
		PreparedStatement ps = DB.prepareStatement(getPostedTablesSQL(), get_TrxName());
		ResultSet rs = ps.executeQuery();
		// Por tabla: 
		// 1) Eliminar la contabilidad en base a la cláusula where por parámetros
		// 2) Marcar los registros de la tabla para contabilizar
		String tableName;
		while(rs.next()) {
			tableName = rs.getString("TableName");
			// 1)
			table_deleteds.put(tableName, deletePosting(rs.getInt("AD_Table_ID")));
			// 2)
			table_markeds.put(tableName, markNoPosted(tableName));
		}
		
		ps.close();
		rs.close();
		
		return getMsg();
	}

	/**
	 * @return query con las tablas que contienen la columna Posted o sólo la tabla
	 *         parámetro ingresada
	 */
	protected String getPostedTablesSQL() {
		StringBuffer sql = new StringBuffer(" SELECT distinct t.AD_Table_ID, t.TableName " + 
											" FROM AD_Table t " + 
											" JOIN AD_Column c on c.ad_table_id = t.ad_table_id " + 
											" WHERE t.IsView='N' and position('T_' in t.tablename) <> 1 and c.ColumnName='Posted' AND c.IsActive='Y' ");
		if(!Util.isEmpty(getTableID(), true)) {
            sql.append(" AND t.AD_Table_ID=" + getTableID());
        }
		sql.append(" order by t.tablename ");
		return sql.toString();
	}
	
	/**
	 * @param tableName tabla actual
	 * @return la consulta sql para la tabla actual
	 */
	protected String getWhereClause(String tableName) {
		StringBuffer where = new StringBuffer(" AD_Client_ID = ? ");
		if(!Util.isEmpty(getOrgID(), true)) {
			where.append(" AND AD_Org_ID = ? ");
		}
		if(getDateFrom() != null) {
			where.append(" AND "+getDateColumnName(tableName)+"::date >= ?::date ");
		}
		if(getDateTo() != null) {
			where.append(" AND "+getDateColumnName(tableName)+"::date <= ?::date ");
		}
		return where.toString();
	}
	
	/**
	 * Setea los parámetros a la consulta actual
	 * @param ps
	 * @throws Exception
	 */
	protected void setWhereClauseParams(PreparedStatement ps) throws Exception {
		int i = 1;
		ps.setInt(i++, getAD_Client_ID());
		if(!Util.isEmpty(getOrgID(), true)) {
			ps.setInt(i++, getOrgID());
		}
		if(getDateFrom() != null) {
			ps.setTimestamp(i++, getDateFrom());
		}
		if(getDateTo() != null) {
			ps.setTimestamp(i++, getDateTo());
		}
	}
	
	/**
	 * Obtiene el nombre de la columna de fecha de la tabla parámetro para filtrar
	 * 
	 * @param tableName nombre de la tabla
	 * @return nombre de la columna de fecha de la tabla parámetro
	 */
	private String getDateColumnName(String tableName) {
		return dateColumnNameExceptionalByTableFact.get(tableName) == null ? "DateAcct"
				: dateColumnNameExceptionalByTableFact.get(tableName);
	}
	
	/**
	 * Ejecuta una consulta sql de modificación de datos (DELETE o UPDATE)
	 * 
	 * @param sql consulta sql DELETE o UPDATE
	 * @return cantidad de registros afectados
	 * @throws Exception en caso de error
	 */
	protected int executeUpdate(String sql) throws Exception {
		PreparedStatement psd = DB.prepareStatement(sql, get_TrxName(), true);
		setWhereClauseParams(psd);
		int affected = psd.executeUpdate();
		psd.close();
		return affected;
	}
	
	/**
	 * Elimina la contabilidad de la tabla parámetro y las condiciones parámetro del
	 * proceso
	 * 
	 * @param tableID id de la tabla parámetro
	 * @return cantidad de registros eliminados
	 * @throws Exception en caso de error
	 */
	protected int deletePosting(Integer tableID) throws Exception {
		String sql = " DELETE FROM Fact_Acct WHERE "+getWhereClause("Fact_Acct");
		return executeUpdate(sql);
	}
	
	/**
	 * Marca los registros a contabilizar de la tabla parámetro y las condiciones
	 * parámetro del proceso
	 * 
	 * @param tableName nombre de tabla a modificar
	 * @return cantidad de registros afectados
	 * @throws Exception en caso de error
	 */
	protected int markNoPosted(String tableName) throws Exception {
		String sql = " UPDATE " + tableName + " SET posted = 'N', processing = 'N' WHERE " + getWhereClause(tableName)
				+ " AND Posted <> 'N' AND isactive = 'Y' and processed = 'Y' ";
		return executeUpdate(sql);
	}
	
	/**
	 * @return mensaje final del proceso
	 */
	protected String getMsg() {
		HTMLMsg msg = new HTMLMsg();
		HTMLList tl = msg.createList("t", "ul", "@AffectedTables@:");
		for (String tableName : table_deleteds.keySet()) {
			msg.createAndAddListElement(tableName, tableName + ": " + "@Updated@ = " + table_markeds.get(tableName)
					+ ", @Deleted@ = " + table_deleteds.get(tableName), tl);
		}
		msg.addList(tl);
		return Msg.parseTranslation(getCtx(), msg.toString());
	}
	
	private Integer getOrgID() {
		return (Integer)getParametersValues().get("AD_ORG_ID");
	}
	
	private Integer getTableID() {
		return (Integer)getParametersValues().get("AD_TABLE_ID");
	}
	
	private Timestamp getDateFrom() {
		return (Timestamp)getParametersValues().get("DATEACCT");
	}
	
	private Timestamp getDateTo() {
		return (Timestamp)getParametersValues().get("DATEACCT_TO");
	}
}
