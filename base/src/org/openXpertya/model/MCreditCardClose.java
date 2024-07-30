package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.ImportFromFidelius;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

public class MCreditCardClose extends X_C_CreditCard_Close implements DocAction {

	private static final long serialVersionUID = 1L;

	public MCreditCardClose(Properties ctx, int C_CreditCard_Close_ID, String trxName) {
		super(ctx, C_CreditCard_Close_ID, trxName);
	}

	public MCreditCardClose(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	// Métodos estáticos

	// Creo el where y el arreglo de parámetros para buscar el PO
	public static MCreditCardClose get(Properties ctx, int ad_org_id, Date dateTrx, String trxName) {
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		if (ad_org_id != 0) {
			whereClause.append("(ad_org_id = ?)");
			params.add(ad_org_id);
		}
		if (dateTrx != null) {
			if (whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("(datetrx = ?)");
			params.add(new java.sql.Date(dateTrx.getTime()));
		}
		return (MCreditCardClose) PO.findFirst(ctx, "c_creditcard_close", whereClause.toString(), params.toArray(),
				null, trxName);
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(action, getDocAction(), log);
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {
		// Verificar que haya cierre del día anterior completo para este almacén
		if (!existsPreviousDayCloseCompleted(getDateTrx(), getAD_Org_ID(), get_TrxName())) {
			// Aquí sabemos que no existe un cierre completado para el día
			// anterior.
			// Además, hay que tener en cuenta que si es el primer cierre que
			// está completando
			// el resultado no es error debido a que el primer cierre siempre
			// hay que dejar completarlo.
			if (getCreditCardCloseCount(getID(), getDateTrx(), getAD_Org_ID(), get_TrxName()) >= 1) {
				m_processMsg = "@NotExistBeforeCreditCardClose@";
				return DocAction.STATUS_Invalid;
			}
		}

		// Verificar que no haya cupones de tarjeta para la fecha de cierre y
		// organización que no tengan asignada la Referencia al Cierre de
		// Tarjetas
		if (DB.getSQLValue(get_TrxName(),
				"SELECT COUNT(C_Payment_ID) FROM C_Payment p WHERE AD_Org_ID = ? AND p.DocStatus in ('CO','CL') AND datetrx::date = '"
						+ getDateTrx()
						+ "'::date AND tendertype='C' AND not exists (select cccl.c_payment_id from c_creditcard_close ccc inner join c_creditcard_closeline cccl on cccl.c_creditcard_close_id = ccc.c_creditcard_close_id where cccl.c_payment_id = p.c_payment_id and ccc.datetrx::date = p.datetrx::date)",
				getAD_Org_ID(), true) > 0) {
			m_processMsg = "@CouponsWithoutReference@";
			return DocAction.STATUS_Invalid;
		}

		// Verifico que no haya números de cupones erróneos
		if ((validarCampo("couponnumber") > 0) || (validarNroCupon() > 0)) {
			m_processMsg = "@CouponsInvalid@";
			return DocAction.STATUS_Invalid;
		}

		// Verifico que no haya números de tarjetas erróneos
		if ((validarCampo("creditcardnumber") > 0) || (validarNroTarjeta() > 0)) {
			m_processMsg = "@CreditCardsWithInvalidNumber@";
			return DocAction.STATUS_Invalid;
		}

		// Verifico que no haya números de lotes erróneos
		if ((validarCampo("couponbatchnumber") > 0) || (validarNroLote() > 0)) {
			m_processMsg = "@BatchNumberInvalid@";
			return DocAction.STATUS_Invalid;
		}

		// Verifico que no haya cupones repetidos (Misma Organización, Nro
		// Cupon, Nro de Lote, Fecha de Cupón, EC asociada a la EF del cupón)
		String duplicados = "SELECT COUNT(*) FROM ("
				+ "select couponnumber, couponbatchnumber, ef.c_bpartner_id, COUNT(*) "
				+ "FROM C_CreditCard_CloseLine cl "
				+ "inner join m_entidadfinancieraplan efp on efp.m_entidadfinancieraplan_id = cl.m_entidadfinancieraplan_id "
				+ "inner join m_entidadfinanciera ef on ef.m_entidadfinanciera_id = efp.m_entidadfinanciera_id "
				+ "where c_creditcard_close_id = " + getC_CreditCard_Close_ID() + " "
				+ "GROUP BY couponnumber, couponbatchnumber, ef.c_bpartner_id HAVING COUNT(*) > 1) as duplicados";
		if (DB.getSQLValue(get_TrxName(), duplicados, true) > 0) {
			m_processMsg = "@RepeatedCoupons@";
			return DocAction.STATUS_Invalid;
		}

		return DocAction.STATUS_InProgress;
	}

	// El Número de Lote debe ser siempre de 3 dígitos, ni uno mas ni uno menos.
	private int validarNroLote() {
		return DB.getSQLValue(get_TrxName(),
				"SELECT COALESCE(COUNT(C_Payment_ID),0) FROM C_CreditCard_CloseLine WHERE (length(couponbatchnumber) > 3 or length(couponbatchnumber) < 3 ) AND C_CreditCard_Close_ID = "
						+ getC_CreditCard_Close_ID() + " AND AD_Org_ID = ? ",
				getAD_Org_ID(), true);
	}

	// El Nro de Tarjeta debe tener que como mínimo 4 dígitos.
	private int validarNroTarjeta() {
		return DB.getSQLValue(get_TrxName(),
				"SELECT COALESCE(COUNT(C_Payment_ID),0) FROM C_CreditCard_CloseLine WHERE length(creditcardnumber) < 4  AND C_CreditCard_Close_ID = "
						+ getC_CreditCard_Close_ID() + " AND AD_Org_ID = ? ",
				getAD_Org_ID(), true);
	}

	// El Nro de Cupón debe ser siempre de 4 dígitos, ni uno mas ni uno menos.
	private int validarNroCupon() {
		return DB.getSQLValue(get_TrxName(),
				"SELECT COALESCE(COUNT(C_Payment_ID),0) FROM C_CreditCard_CloseLine WHERE (length(couponnumber) > 4 or length(couponnumber) < 4 ) AND C_CreditCard_Close_ID = "
						+ getC_CreditCard_Close_ID() + " AND AD_Org_ID = ? ",
				getAD_Org_ID(), true);
	}

	// Verificar campo inválido o vacío
	public int validarCampo(String campo) {
		return DB.getSQLValue(get_TrxName(),
				"SELECT COALESCE(COUNT(C_Payment_ID),0) FROM C_CreditCard_CloseLine WHERE (isnumeric(" + campo
						+ ") = 'f' OR " + campo + " is null) AND C_CreditCard_Close_ID = " + getC_CreditCard_Close_ID()
						+ " AND AD_Org_ID = ? ",
				getAD_Org_ID(), true);
	}

	/**
	 * Verifica si existe un Cierre de Tarjeta completo para un día anterior a
	 * la fecha indicada.
	 * 
	 * @param date
	 *            Fecha origen de la verificación. Se buscará un cierre para
	 *            <code>date - 1</code>.
	 * @param creditcar_close_ID
	 *            cierre que se está completando
	 * @param trxName
	 *            Transacción utilizada para instanciación de POs.
	 * @return <code>true</code> si existe un cierre en estado completado,
	 *         <code>false</code> en caso contrario (no existe la tupla de
	 *         cierre o existe pero en estado Borrador)
	 */
	public static boolean existsPreviousDayCloseCompleted(Date date, int creditcar_close_ID, String trxName) {
		// Obtiene el día anterior a la fecha parámetro
		Calendar previousDayCalendar = Calendar.getInstance();
		previousDayCalendar.setTimeInMillis(date.getTime());
		previousDayCalendar.add(Calendar.DATE, -1);
		Date previousDay = previousDayCalendar.getTime();

		// Busca un cierre para el día anterior
		MCreditCardClose previousDayCC = MCreditCardClose.get(Env.getCtx(), creditcar_close_ID, previousDay, trxName);
		// Se genera la condición de retorno. El cierre debe existir y estar en
		// estado Completado.
		return previousDayCC != null && previousDayCC.isCompleted();
	}

	/**
	 * @param actualcreditcardID
	 *            id del cierre de tarjeta
	 * @param AD_Org_ID
	 *            id del cierre de organización del cierre actual
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return cantidad de registros para el cierre actual
	 */
	private static int getCreditCardCloseCount(int actualcreditcardID, Date date, int AD_Org_ID, String trxName) {
		return DB.getSQLValue(trxName,
				"SELECT coalesce(count(C_CreditCard_Close_id),0) " + "FROM C_CreditCard_Close "
						+ " WHERE Datetrx::date <= '" + date + "'::date "
						+ (actualcreditcardID != 0 ? "AND C_CreditCard_Close_ID <> " + actualcreditcardID : "")
						// + " AND DocStatus not in ('CL','CO')"
						+ " AND AD_Org_ID = " + AD_Org_ID);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Verificar si hay un cierre para la fecha elegída
		if (DB.getSQLValue(get_TrxName(),
				"SELECT COUNT(C_CreditCard_Close_ID) FROM C_CreditCard_Close WHERE DateTrx::date = '" + getDateTrx()
						+ "'::date AND C_CreditCard_Close_ID <> " + getC_CreditCard_Close_ID() + " AND Ad_Org_ID = ?",
				getAD_Org_ID()) > 0) {
			// Hay una tupla para la misma fecha y almacén, por lo tanto no
			// seguir
			log.saveError("CreditCardCloseRepeated", "");
			return false;
		}
		return true;
	}

	@Override
	protected boolean beforeDelete() {
		// Sólo se puede borrar registros en estado "Borrador"
		if (!getDocStatus().equals(DOCSTATUS_Drafted)) {
			log.saveError("CannotDeleteRecordInProcessState", "");
			return false;
		}
		return true;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public String completeIt() {
		try {
			
			// dREHER se importan los cupones pendiente en una ventana de X dias hacia atras
			String ic = MPreference.GetCustomPreferenceValue("ImpCupPend_FromCierreTarjeta", Env.getAD_Client_ID(getCtx()));
			if(ic!=null && ic.equals("Y"))
				doImportPendCoupons();
			else
				debug("Se evita importar cupones pendientes");
			
			// Se eliminan los cupones anulados del cierre actual
			int deleted = deleteVoidPayments();
			debug("Elimino cupones (payments) anulados de las lineas del cierre: " + deleted);
			
			// Setear las líneas A Verificar
			int updated = updatePayments(MPayment.AUDITSTATUS_ToVerify);
			debug("Setea todos los cupones (payments) relacionados a las lineas de cierre a estado " + MPayment.AUDITSTATUS_ToVerify + " (A verificar): " + updated);

			// dREHER se deben verificar todos los payments que coincidan, tanto pendientes como no pendientes
			// desde los cupones recuperados desde Fidelius
			int verified = doVerifyCoupons();
			debug("Setea todos los cupones (payments) relacionados a las lineas de cierre a estado " + MPayment.AUDITSTATUS_Verified + " (Verificados): " + verified);
			
		} catch (Exception e) {
			setProcessMsg(e.getMessage());
			return DOCSTATUS_Invalid;
		}

		setAllowReopening(false);
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return DocAction.STATUS_Completed;
	}

	/**
	 * Se deben verificar todos los cupones importados, sin importar si estan o no pendientes
	 * y que esten para verificar
	 * @author dREHER
	 */
	private int doVerifyCoupons() {
		int verified = 0;
		
		StringBuffer sql = new StringBuffer();
		Timestamp fromFecha = new Timestamp((new Date()).getTime());
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int saved = 0;
		int lost = 0;
		
		sql.append("SELECT p.C_Payment_ID, p.DateTrx, ");
		sql.append(" p.PayAmt, p.CouponBatchNumber AS Lote, ");
		sql.append(" p.CouponNumber as Cupon, p.Posnet, ");
		sql.append(" p.AuditStatus ");
		sql.append("FROM C_Payment p ");
		sql.append("WHERE p.tendertype='C' ");
		sql.append(" AND p.DocStatus IN ('CO','CL') ");
		sql.append(" AND NOT p.AuditStatus IN (?)");
		sql.append(" AND p.IsActive='Y' ");
		sql.append(" AND p.AD_Org_ID=?" );
		sql.append(" AND p.DateTrx >= ?::date");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromFecha);
		cal.add(Calendar.DATE, -getDays("FromProcess"));
		

		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			pstmt.setString(1, getCompleteStatus());
			pstmt.setInt(2, Env.getAD_Org_ID(getCtx()));
			pstmt.setTimestamp(3, fromFecha);

			rs = pstmt.executeQuery();
			while (rs.next()) {
			
				Timestamp fecha = rs.getTimestamp("DateTrx");
				BigDecimal monto = rs.getBigDecimal("PayAmt");
				String lote = rs.getString("Lote");
				String cupon = rs.getString("Cupon");
				int C_Payment_ID = rs.getInt("C_Payment_ID");
				String posnet = rs.getString("Posnet");
				String status = MPayment.AUDITSTATUS_Verified;
				String auditStatus = rs.getString("AuditStatus");
				
				debug("Payment:" + C_Payment_ID + " Fecha:" + fecha + " Lote:" + lote +
						" Cupon:" + cupon + "Cuotas:" + posnet + " Monto:" + monto);
				
				int I_Fidelius_ID = DB.getSQLValueEx(get_TrxName(), 
								"SELECT I_FideliusPendientes_ID " +
								"FROM I_FideliusPendientes " +
								"WHERE fechaoper::date=?::date AND " +
								" importe::numeric=? AND " +
								" nrolote=? AND ticket=? AND " +
								" cuota_tipeada=?", new Object[] {fecha, monto, lote, cupon, posnet});
				if(I_Fidelius_ID > 0) {
					debug("Encontro cupon pendiente en Fidelius..." + I_Fidelius_ID);
				}else {
					I_Fidelius_ID = DB.getSQLValueEx(get_TrxName(), 
							"SELECT I_FideliusCupones_ID " +
							"FROM I_FideliusCupones " +
							"WHERE fvta::date=?::date AND " +
							" imp_vta::numeric=? AND " +
							" nrolote=? AND nrocupon=? AND " +
							" cuotas=?", new Object[] {fecha, monto, lote, cupon, posnet});
					if(I_Fidelius_ID > 0) {
						debug("Encontro cupon en Fidelius..." + I_Fidelius_ID);
						X_I_FideliusCupones fc = new X_I_FideliusCupones(Env.getCtx(), I_Fidelius_ID, get_TrxName());
						if(fc.getrechazo()!=null && fc.getrechazo().equals("Y")) {
							status = MPayment.AUDITSTATUS_Rejected;
							debug("Cupon rechazado!");
						}else
							if(fc.getI_Fideliusliquidaciones_ID() > 0) {
								status = MPayment.AUDITSTATUS_Paid;
								debug("Cupon liquidado!");
							}
						
					}
				}
				
				if(I_Fidelius_ID > 0) {
					debug("Encontro cupon fidelius, actualizar estado de auditoria del pago (VE): " + C_Payment_ID);
					DB.executeUpdate("UPDATE C_Payment SET UpdatedBy=" + Env.getAD_User_ID(getCtx()) + ", " +
							"Updated='" + Env.getDateFormatted(Env.getDate()) + "', " +
							"AuditStatus='" + status + "' " +
							"WHERE C_Payment_ID=" + C_Payment_ID, get_TrxName());
					
					debug("Se actualiza pago. status=" + status);
					verified++;
					
				}else {
					if(auditStatus==null || auditStatus.isEmpty()) {
						status = MPayment.AUDITSTATUS_ToVerify;
						debug("NO Encontro cupon fidelius, actualizar estado de auditoria del pago (TV): " + C_Payment_ID);
						DB.executeUpdate("UPDATE C_Payment SET UpdatedBy=" + Env.getAD_User_ID(getCtx()) + ", " +
								"Updated='" + Env.getDateFormatted(Env.getDate()) + "', " +
								"AuditStatus='" + status + "' " +
								"WHERE C_Payment_ID=" + C_Payment_ID, get_TrxName());
						
						debug("Se deja pago pendiente de verificacion. status=" + status);
					}
				}
				
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Verificacion de Cupones.doIt", e);
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		return verified;
	}

	/**
	 * Salida por consola simple
	 * @param string
	 * @author dREHER
	 */
	private void debug(String string) {
		System.out.println("--> MCreditCardClose. " + string);
	}

	/**
	 * @return devuelve cadena de pagos con tarjetas pagados o rechazados
	 * @author dREHER
	 */
	private String getCompleteStatus() {
		return "'" + MPayment.AUDITSTATUS_Paid + "','" + MPayment.AUDITSTATUS_Rejected + "','" + MPayment.AUDITSTATUS_ToVerify + "'";
	}

	/**
	 * Dispara el proceso de importacion de cupones pendientes desde Fidelius
	 * 
	 * @return resultado del proceso
	 * @throws Exception
	 * @author dREHER 
	 */
	private String doImportPendCoupons() throws Exception {
		String msg = "";
		
		ImportFromFidelius iff = new ImportFromFidelius();
		
		boolean processSuccess = iff.startProcess(getCtx(), getProcessInfo(), getTrx(get_TrxName()));

		if (!processSuccess) {
			throw new Exception( iff.getProcessInfo().getSummary() + " - "
					+ iff.getProcessInfo().getLogInfo());
		}
		else {
			msg = iff.getProcessInfo().getSummary();
		}
		
		return msg;
	}
	
	/**
	 * @return ProcessInfo de importacion desde Fidelius
	 * @author dREHER
	 */
	private ProcessInfo getProcessInfo() {
		int AD_Process_ID = DB.getSQLValue(get_TrxName(), "SELECT AD_Process_ID FROM AD_Process WHERE Name=?", 
				"ImportFromFidelius");
		
		String nombreComercio; // Nombre del Comercio en la ORGInfo formato Fidelius-Clover
		nombreComercio = DB.getSQLValueString(get_TrxName(), "SELECT NombreComercio FROM AD_OrgInfo WHERE AD_Org_ID=?", Env.getAD_Org_ID(getCtx()));
		
		ProcessInfo pi = new ProcessInfo("ImportFromFidelius", AD_Process_ID);
		pi.setParameter(new ProcessInfoParameter[] {
				new ProcessInfoParameter("DaysFromBack", getDays("From"),null,null,null),
				new ProcessInfoParameter("DaysToBack", getDays("To"),null,null,null),
				new ProcessInfoParameter("Type", new String("P"),null,null,null),
				new ProcessInfoParameter("OrgName", nombreComercio,null,null,null)
				});
		
		return pi;
	}

	/**
	 * Devuelve los dias hacia atras para importar cupones pendientes
	 * @param tipo
	 * @author dREHER
	 * @return
	 */
	private int getDays(String tipo) {
		int dias = 3;
		
		if(tipo.equals("From")) {
			
			String tmp = MPreference.GetCustomPreferenceValue("DaysFromBackCreditCardClose", Env.getAD_Client_ID(getCtx()));
			if(Util.isEmpty(tmp, true))
				tmp = "3";
			dias = Integer.parseInt(tmp);
			
		}else if(tipo.equals("To")) {
			
			String tmp = MPreference.GetCustomPreferenceValue("DaysToBackCreditCardClose", Env.getAD_Client_ID(getCtx()));
			if(Util.isEmpty(tmp, true))
				tmp = "0";
			dias = Integer.parseInt(tmp);
		}else if(tipo.equals("FromProcess")) {
			
			String tmp = MPreference.GetCustomPreferenceValue("DaysFromBackCreditCardCloseProcess", Env.getAD_Client_ID(getCtx()));
			if(Util.isEmpty(tmp, true))
				tmp = "3";
			dias = Integer.parseInt(tmp);
		}
		
		return dias;
	}

	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre contenido en la variable de instancia o una nueva
	 */
	private Trx getTrx(String trxName){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(trxName, false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx(trxName);
		}
		
		return trx;
	}
	
	private Trx createTrx(String trxName){
		//Creo la transacción
		return Trx.get(trxName, true);
	}

	/**
	 * Eliminar los cupones anulados de las líneas del cierre
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	private int deleteVoidPayments() throws Exception {
		String sql = "DELETE FROM c_creditcard_closeline cccl "
					+ "WHERE cccl.c_creditcard_close_id = "+getID()
					+ "		AND EXISTS (SELECT p.c_payment_id "
					+ "					FROM c_payment p "
					+ "					WHERE p.c_payment_id = cccl.c_payment_id "
					+ "						AND p.docstatus NOT IN ('CO','CL'))";
		return DB.executeUpdate(sql, get_TrxName());
	}
	
	/**
	 * Actualiza el estado de auditoría de los pagos relacionados al cierre
	 * 
	 * @param auditStatus
	 *            estado de auditoría a asignar a los pagos
	 * @return cantidad de pagos actualizados
	 */
	protected int updatePayments(String auditStatus){
		String sql = "update c_payment p "
					+ "set updated = now(), auditstatus = '"+auditStatus+"' "
					+ "where exists (select c_creditcard_closeline_id "
					+ "					from c_creditcard_closeline cl "
					+ "					where cl.c_creditcard_close_id = "+getID()
					+" 							and p.c_payment_id = cl.c_payment_id)";
		return DB.executeUpdate(sql, get_TrxName());
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		m_processMsg = "@NotAllowedVoidCreditCardClose@";
		return false;
	}

	@Override
	public boolean closeIt() {
		m_processMsg = "@NotAllowedCloseCreditCardClose@";
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		m_processMsg = "@NotAllowedReverseCreditCardClose@";
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		if (isAllowReopening()) {
			setDocAction(DOCACTION_Complete);
			setProcessed(false);
			return true;
		}
		m_processMsg = "@LockCreditCardClose@";
		return false;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}

	/**
	 * @return Indica si este cierre está en estado Completado.
	 */
	public boolean isCompleted() {
		return DOCSTATUS_Completed.equals(getDocStatus());
	}
}
