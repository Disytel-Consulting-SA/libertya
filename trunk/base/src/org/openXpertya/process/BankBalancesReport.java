package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MRole;
import org.openXpertya.model.X_T_BankBalances;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class BankBalancesReport extends SvrProcess {

	/** ID de la Cuenta Bancaria para consultar los documentos */
	private int p_C_BankAccount_ID = 0;
	// **************** Matías Cap - Disytel ****************  
	// Líneas comentadas ya que no se utilizarán los filtros por fecha de
	// vencimiento y emisión, solo uno de ellos con los nuevos parámetros
	// descritos debajo de estas líneas comentadas 
	// ---------------------------------------------
//	/** Fecha Inicio de la Fecha de Transacción */
//	private Timestamp p_DateTrx_From = null;
//	/** Fecha Fin de la Fecha de Transacción */
//	private Timestamp p_DateTrx_To = null;
//	/** Fecha Inicio de la Fecha de Vencimiento */
//	private Timestamp p_DueDate_From = null;
//	/** Fecha Fin de la Fecha de Vencimiento */
//	private Timestamp p_DueDate_To = null;
	// ---------------------------------------------
	
	/** Valor de lista de validación indicando el orden de fechas: Vto o Emisión */
	private String p_DateOrder = null;
	/** Fecha inicio parámetro. El tipo de fecha lo indica el orden */
	private Timestamp p_Date_From = null;
	/** Fecha fin parámetro. El tipo de fecha lo indica el orden */
	private Timestamp p_Date_To = null;
	/** Indica si se deben consultar documentos conciliados o no. Null implica todos. */
	private String p_IsReconciled = null;
	/** Estado de los documentos a consultar */
	private String p_DocStatus = null;
	/** Organización */
	private Integer p_AD_Org_ID = null;
	/**
	 * Nombre de Columna de BD relacionada con fechas. Esta columna será la
	 * columna de fechas a filtrar en el reporte y en la consulta de saldo
	 * inicial.
	 */
	private String dateColumnName = null;
	
	public BankBalancesReport() {
		super();
	}

	@Override
	protected void prepare() {
        ProcessInfoParameter[] params = getParameter();

        for( int i = 0;i < params.length;i++ ) {
            String name = params[i].getParameterName();

            if( params[i].getParameter() == null )
            	;
            else if( name.equalsIgnoreCase( "C_BankAccount_ID" )) {
            	p_C_BankAccount_ID = ((BigDecimal)params[i].getParameter()).intValue();
            // ********** Matías Cap - Disytel **********
            // Líneas comentadas por motivo descrito en la declaración de variables
            // ----------------------------------------------------------
//            } else if( name.equalsIgnoreCase( "DateTrx" )) {
//            	p_DateTrx_From = (Timestamp)params[i].getParameter();
//            	p_DateTrx_To   = (Timestamp)params[i].getParameter_To();
//            } else if( name.equalsIgnoreCase( "DueDate" )) {
//            	p_DueDate_From = (Timestamp)params[i].getParameter();
//            	p_DueDate_To   = (Timestamp)params[i].getParameter_To();
            // ----------------------------------------------------------
            } else if( name.equalsIgnoreCase( "CommonDate" ) ){
            	p_Date_From = (Timestamp)params[i].getParameter();
            	p_Date_To   = (Timestamp)params[i].getParameter_To();
        	} else if( name.equalsIgnoreCase( "DateOrder" ) ){
        		p_DateOrder = (String)params[i].getParameter();
        	} else if( name.equalsIgnoreCase( "IsReconciled" )) {
        		p_IsReconciled = (String)params[i].getParameter();
        	} else if( name.equalsIgnoreCase( "DocStatus" )) {
        		p_DocStatus = (String)params[i].getParameter();
        	} else if( name.equalsIgnoreCase( "Ad_Org_ID" )) {
        		p_AD_Org_ID = ((BigDecimal)params[i].getParameter()).intValue();
        	} else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_BankBalances WHERE Created < ('now'::text)::timestamp(6) - interval '7 days'");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_BankBalances WHERE AD_PInstance_ID = " + getAD_PInstance_ID());

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * ");
		sql.append(" FROM V_BankBalances as bb ");
		sql.append(" WHERE DocStatus <> 'IP' AND DocStatus <> 'DR' AND ");
		sql.append("       C_BankAccount_ID = ? AND ");
		// ************** Matías Cap - Disytel
		// Líneas comentadas ya que se utilizará solo una fecha para filtrar
		// ------------------------------------------------------------------
////		sql.append("       (? <= DateTrx OR ? IS NULL) AND ");
//		sql.append("       (? IS NULL OR date_trunc('day',?::date)::date <= date_trunc('day',DateTrx)::date) AND ");
//		
////		sql.append("       (DateTrx <= ? OR ? IS NULL) AND ");
//		sql.append("       (? IS NULL OR date_trunc('day',?::date)::date >= date_trunc('day',DateTrx)::date) AND ");
//
////		sql.append("       (? <= DueDate OR ? IS NULL) AND ");
//		sql.append("       (? IS NULL OR date_trunc('day',?::date)::date <= date_trunc('day',DueDate)::date) AND ");
//		
////		sql.append("       (DueDate <= ? OR ? IS NULL) AND ");
//		sql.append("       (? IS NULL OR date_trunc('day',?::date)::date >= date_trunc('day',DueDate)::date) AND ");
		// ------------------------------------------------------------------
		
		// Si existe un orden de fechas, filtro por ella
		if(p_DateOrder != null){
			sql.append("       (? IS NULL OR date_trunc('day',?::date)::date <= date_trunc('day',"+getColumnNameForDate()+")::date) AND ");
			sql.append("       (? IS NULL OR date_trunc('day',?::date)::date >= date_trunc('day',"+getColumnNameForDate()+")::date) AND ");
		}
		
//		sql.append("       (IsReconciled = ? OR ? IS NULL) AND ");
		sql.append("       (? IS NULL OR IsReconciled = ?) AND ");
		
//		sql.append("       (DocStatus = ? OR ? IS NULL) ");
		sql.append("       (? IS NULL OR DocStatus = ?) ");
		
		String realSql = null;
		if(p_AD_Org_ID != null){
			sql.append("	AND (AD_Org_ID = ?) ");
			realSql = sql.toString();
		}
		else{
			realSql = MRole.getDefault(getCtx(),false).addAccessSQL(sql.toString(), "bb", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);			
		}
		
		realSql += " ORDER BY "+((p_DateOrder == null)?"DateTrx":getColumnNameForDate())+" ASC ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// Se prepara la consulta y se asignan los parámetros.
//			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, realSql, get_TrxName() ,false);
			int i = 1;
			pstmt.setInt      (i++, p_C_BankAccount_ID);
			// ****** Matías Cap - Disytel ********
			// Líneas comentadas ya que se utilizará solo un filtro de fechas
			// ---------------------------------------------------------------------------
//			// HACK: si se usa setTimestamp se produce un error y no se puede ejecutar la consulta.
//			// Por eso se convierte el Timestamp a String y se utiliza setString.
////			pstmt.setTimestamp(2, p_DateTrx_From);
////			pstmt.setTimestamp(3, p_DateTrx_From);
////			Calendar calendar = new GregorianCalendar();
////			Date date_p_DateTrx_From = new Date(p_DateTrx_From.getTime());
////			pstmt.setDate(i++, date_p_DateTrx_From, calendar);
////			pstmt.setDate(i++, date_p_DateTrx_From, calendar);
//			pstmt.setString(i++, p_DateTrx_From == null? null : p_DateTrx_From.toString());
//			pstmt.setString(i++, p_DateTrx_From == null? null : p_DateTrx_From.toString());
//			// HACK: Idem anterior
////			pstmt.setTimestamp(4, p_DateTrx_To);
////			pstmt.setTimestamp(5, p_DateTrx_To);
////			Date date_p_DateTrx_To = new Date(p_DateTrx_To.getTime());
////			pstmt.setDate(i++, date_p_DateTrx_To, calendar);
////			pstmt.setDate(i++, date_p_DateTrx_To, calendar);
//			pstmt.setString(i++, p_DateTrx_To == null? null : p_DateTrx_To.toString());
//			pstmt.setString(i++, p_DateTrx_To == null? null : p_DateTrx_To.toString());
//			// HACK: Idem anterior
////			pstmt.setTimestamp(6, p_DueDate_From);
////			pstmt.setTimestamp(7, p_DueDate_From);
////			Date date_p_DueDate_From = new Date(p_DueDate_From.getTime());
////			pstmt.setDate(i++, date_p_DueDate_From, calendar);
////			pstmt.setDate(i++, date_p_DueDate_From, calendar);
//			pstmt.setString(i++, p_DueDate_From == null ? null : p_DueDate_From.toString());
//			pstmt.setString(i++, p_DueDate_From == null ? null : p_DueDate_From.toString());
//			// HACK: Idem anterior
////			pstmt.setTimestamp(8, p_DueDate_To);
////			pstmt.setTimestamp(9, p_DueDate_To);
////			Date date_p_DueDate_To = new Date(p_DueDate_To.getTime()); 
////			pstmt.setDate(i++, date_p_DueDate_To, calendar);
////			pstmt.setDate(i++, date_p_DueDate_To, calendar);
//			pstmt.setString(i++, p_DueDate_To == null ? null : p_DueDate_To.toString());
//			pstmt.setString(i++, p_DueDate_To == null ? null : p_DueDate_To.toString());
			// ---------------------------------------------------------------------------
			// Se filtra por fechas cuando verdaderamente hay un filtro de orden por ellas
			if(p_DateOrder != null){
				pstmt.setString(i++, p_Date_From == null ? null : p_Date_From.toString());
				pstmt.setString(i++, p_Date_From == null ? null : p_Date_From.toString());
				pstmt.setString(i++, p_Date_To == null ? null : p_Date_To.toString());
				pstmt.setString(i++, p_Date_To == null ? null : p_Date_To.toString());
			}
		
			pstmt.setString   (i++, p_IsReconciled);
			pstmt.setString   (i++, p_IsReconciled);
			pstmt.setString   (i++, p_DocStatus);
			pstmt.setString   (i++, p_DocStatus);
			
			if(p_AD_Org_ID != null){
				pstmt.setInt(i++, p_AD_Org_ID);
			}
			// Se obtienen los resultados
			rs = pstmt.executeQuery();
			X_T_BankBalances line = null;
			// Acumulador del saldo. 
			
			// ----------------------------------------------------------
			// Modified by Matías Cap - Disytel
			// Colocar el saldo inicial hasta la fecha desde del reporte
			// ********** Líneas comentadas *********
			// BigDecimal balance = BigDecimal.ZERO;
			// **************************************
			BigDecimal balance = getOpeningAmt(p_Date_From, true);
			// ----------------------------------------------------------			
			// Se recorren las tuplas y se crean las líneas en la tabla temporal.
			while (rs.next()) {
				// ----------------------------------------------------------
				// Modified by Matías Cap - Disytel
				// Todo esto se pasó a un método
				// ********** Líneas comentadas *********
				/*line = new X_T_BankBalances(getCtx(), 0, get_TrxName());
				line.setAD_PInstance_ID(getAD_PInstance_ID());
				line.setC_BankAccount_ID(rs.getInt("C_BankAccount_ID"));
				line.setDocumentType(rs.getString("DocumentType"));
				line.setDocumentNo(rs.getString("DocumentNo"));
				line.setDateTrx(rs.getTimestamp("DateTrx"));
				line.setDueDate(rs.getTimestamp("DueDate"));
				line.setDocStatus(rs.getString("DocStatus"));
				line.setDebit(rs.getBigDecimal("Debit"));
				line.setCredit(rs.getBigDecimal("Credit"));
				line.setIsReconciled("Y".equals(rs.getString("IsReconciled")));*/
				// **************************************
				// Creo cada línea
				line = createBankBalancesLine(rs.getInt("C_BankAccount_ID"), rs.getString("DocumentType"), rs.getString("DocumentNo"),rs.getTimestamp("DateTrx"), rs.getTimestamp("DueDate"),rs.getString("DocStatus"), rs.getBigDecimal("Debit"),rs.getBigDecimal("Credit"), rs.getString("IsReconciled"), p_DateOrder == null?null:rs.getTimestamp(getColumnNameForDate()),p_DateOrder);
				// Se calcula el saldo a partir de los datos de esta línea y el acumulado.
				balance = balance.add(line.getCredit().subtract(line.getDebit()));
				line.setBalance(balance);
				if (!line.save()) 
					log.warning("T_BankBalances line not saved");
				
			}
		
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error create T_BankBalances lines. ", e);
			e.printStackTrace();
		}
		
		return "";
	}

	/**
	 * Obtener el saldo inicial.
	 * @param dateFrom
	 *            fecha desde del reporte, si esta fecha es null entonces no se
	 *            calcula el monto inicial y se devuelve 0, caso contrario se
	 *            calcula el saldo inicial de la cuenta hasta esta fecha desde
	 * @param createInitialRecord booleano que determina si crear el registro inicial de saldo o no
	 * @return el monto de saldo inicial
	 * @throws Exception
	 */
	private BigDecimal getOpeningAmt(Timestamp dateFrom, boolean createInitialRecord) throws Exception{
		BigDecimal openingBalance = BigDecimal.ZERO;
		// Si la fecha desde es distinto de null, entonces verifico el saldo inicial
		if((dateFrom != null) && (p_DateOrder != null)){
			// Obtengo el saldo inicial hasta la fecha_desde del reporte
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT C_BankAccount_ID, sum(credit) as credit, sum(debit) as debit, sum(credit-debit) as balance ");
			sql.append(" FROM V_BankBalances as bb ");
			sql.append(" WHERE DocStatus <> 'IP' AND DocStatus <> 'DR' AND ");
			sql.append("       C_BankAccount_ID = ? AND ");
			sql.append("       (? IS NULL OR date_trunc('day',?::date)::date > date_trunc('day',"+getColumnNameForDate()+")::date) AND ");
			sql.append("       (? IS NULL OR IsReconciled = ?) AND ");
			sql.append("       (? IS NULL OR DocStatus = ?) ");
			
			String realSql = null;
			if(p_AD_Org_ID != null){
				sql.append("	AND (AD_Org_ID = ?) ");
				realSql = sql.toString();
			}
			else{
				realSql = MRole.getDefault(getCtx(),false).addAccessSQL(sql.toString(), "bb", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);			
			}
			
			realSql += " GROUP BY C_BankAccount_ID";
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			BigDecimal openingDebit = BigDecimal.ZERO;
			BigDecimal openingCredit = BigDecimal.ZERO;
			try {
				pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, realSql, get_TrxName() ,false);
				int i = 1;
				pstmt.setInt(i++, p_C_BankAccount_ID);
				pstmt.setString(i++, p_Date_From == null? null : p_Date_From.toString());
				pstmt.setString(i++, p_Date_From == null? null : p_Date_From.toString());
				pstmt.setString(i++, p_IsReconciled);
				pstmt.setString(i++, p_IsReconciled);
				pstmt.setString(i++, p_DocStatus);
				pstmt.setString(i++, p_DocStatus);
				if(p_AD_Org_ID != null){
					pstmt.setInt(i++, p_AD_Org_ID);
				}
				rs = pstmt.executeQuery();
				if(rs.next()){
					openingBalance = rs.getBigDecimal("balance");
					openingDebit = rs.getBigDecimal("debit");
					openingCredit = rs.getBigDecimal("credit");
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error opening balance", e);
				throw e;
			} finally{
				try {
					if(pstmt != null)pstmt.close();
					if(rs != null)rs.close();
				} catch (Exception e2) {
					log.log(Level.SEVERE, "Error opening balance", e2);
					throw e2;
				}
			}
			
			// Creo el registro inicial en el caso que se desee
			if(createInitialRecord){
				createInitialBalanceRecord(openingDebit, openingCredit, openingBalance);
			}
		}		
		return openingBalance;
	}
	
	/**
	 * Creo el registro inicial de saldo
	 * @param openingDebit debito inicial
	 * @param openingCredit credito inicial
	 * @param openingBalance monto de saldo inicial
	 * @throws Exception
	 */
	private void createInitialBalanceRecord(BigDecimal openingDebit, BigDecimal openingCredit, BigDecimal openingBalance) throws Exception{
		// Creo la línea inicial
		String description = Msg.getMsg(getCtx(), "Balance");
		X_T_BankBalances line = createBankBalancesLine(p_C_BankAccount_ID,description, description, p_Date_From, p_Date_From,p_DocStatus, openingDebit, openingCredit, "Y",p_Date_From,p_DateOrder);
		// Seteo el saldo inicial
		line.setBalance(openingBalance);
		if(!line.save()){
			log.log(Level.SEVERE, "Error creating T_BankBalances opening balance record");
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}

	/**
	 * Crear una línea de balance de cuenta para que luego lo levante el
	 * reporte. La línea retornada no se encuentra guardada y tampoco posee
	 * seteado el saldo o balance, se debe realizar explícitamente luego. 
	 * @param C_BankAccount_ID
	 * @param DocumentType
	 * @param DocumentNo
	 * @param DateTrx
	 * @param DueDate
	 * @param DocStatus
	 * @param Debit
	 * @param Credit
	 * @param IsReconciled
	 * @return
	 */
	private X_T_BankBalances createBankBalancesLine(int C_BankAccount_ID,
			String DocumentType, String DocumentNo, Timestamp DateTrx,
			Timestamp DueDate, String DocStatus, BigDecimal Debit,
			BigDecimal Credit, String IsReconciled, Timestamp commonDate,
			String dateOrder) {
		X_T_BankBalances line = new X_T_BankBalances(getCtx(), 0, get_TrxName());
		line.setAD_PInstance_ID(getAD_PInstance_ID());
		line.setC_BankAccount_ID(C_BankAccount_ID);
		line.setDocumentType(DocumentType);
		line.setDocumentNo(DocumentNo);
		line.setDateTrx(DateTrx);
		line.setDueDate(DueDate);
		line.setDocStatus(DocStatus == null ? "CO" : DocStatus);
		line.setDebit(Debit);
		line.setCredit(Credit);
		line.setIsReconciled("Y".equals(IsReconciled));
		line.setCommonDate(commonDate);
		line.setDateOrder(dateOrder);
		return line;
	}

	/**
	 * @return el nombre de la columna para el filtro por fechas, dependiendo
	 *         del orden elegido
	 */
	private String getColumnNameForDate(){
		if(dateColumnName == null){
			if(p_DateOrder.equals(X_T_BankBalances.DATEORDER_Transaction)){
				dateColumnName = "DateTrx";
			}
			else{
				dateColumnName = "DueDate";
			}
		}
		return dateColumnName;
	}
}
