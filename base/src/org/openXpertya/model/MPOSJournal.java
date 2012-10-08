package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Modelo de Caja Diaria de TPV
 * 
 * @author Franco Bonafine - Disytel
 */
public class MPOSJournal extends X_C_POSJournal implements DocAction {

	/** Mensaje de error para documentos que no se pueden completar por falta de caja diaria */
	public static String DOCUMENT_COMPLETE_ERROR_MSG = "@CompleteDocumentPOSJournalRequiredError@";
	
	/** Mensaje de error por caja diaria cerrada */
	public static String POS_JOURNAL_VOID_CLOSED_ERROR_MSG = "@POSJournalVoidClosed@";
	
	/**
	 * Nombre de la preference que determina la diferencia máxima entre el saldo
	 * y la declaración
	 */
	public static String VALID_DIFFERENCE_PREFERENCE = "POSCashJounal_ValidDiff";
	
	/** Diferencia por defecto en caso que no exista preference */
	public static BigDecimal DEFAULT_DIFFERENCE = new BigDecimal(0.5);
	
	/** Log estático */
	private static CLogger s_log = CLogger.getCLogger(MPOSJournal.class);
	
	/** Referencia al TPV asociado a esta Caja Diaria */
	private MPOS pos = null;
	
	/** Libro de caja asociado a esta caja diaria */
	private MCash cash = null;

	/**
	 * @return Indica si la funcionalidad de cajas diarias está activada para la
	 *         compañía actual.
	 */
	public static boolean isActivated() {
		return MClient.get(Env.getCtx()).getInfo().isPOSJournalActive();
	}
	
	/**
	 * Devuelve la Caja Diaria para la fecha y usuario actual, cuyo estado sea
	 * ABIERTO o EN VERIFICACION.
	 */
	public static MPOSJournal getCurrent() {
		Properties ctx = Env.getCtx();
		return get(ctx, Env.getAD_User_ID(ctx), Env.getDate(), new String[] {
				DOCSTATUS_Opened, DOCSTATUS_Completed }, null);
	}
	
	/**
	 * Busca una Caja Diaria que cumpla con una serie de condiciones
	 * determinadas por los parámetros.
	 * 
	 * @param ctx
	 *            Contexto para instanciación del modelo.
	 * @param userID
	 *            ID de usuario al cual debe pertenecer la Caja Diaria
	 * @param date
	 *            Fecha de la Caja Diaria
	 * @param docStatus
	 *            La Caja Diaria debe estar en alguno de los estados indicados
	 *            en este arreglo
	 * @param trxName
	 *            Transacción para instanciación del modelo y consultas.
	 * @return el {@link MPOSJournal} encontrado o <code>null</code> si no se
	 *         encontró ninguna caja diaria con esas condiciones.
	 */
	public static MPOSJournal get(Properties ctx, int userID, Timestamp date, String[] docStatus, String trxName) {
		MPOSJournal journal = null;
		StringBuffer sql = new StringBuffer(); 
		sql.append("SELECT * ")
		   .append("FROM C_POSJournal ")
		   .append("WHERE AD_User_ID = ? ")
		   .append(  "AND DateTrx = ? ");
		
		// Filtro de los dosStatus
		if (docStatus != null && docStatus.length > 0) {
			sql.append( "AND DocStatus IN (");
			for (int i = 0; i < docStatus.length; i++) {
				String status = docStatus[i];
				sql.append("'").append(status).append("'");
				if (i < docStatus.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") "); 
		}
		sql.append("ORDER BY Created DESC");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, userID);
			pstmt.setTimestamp(2, date);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				journal = new MPOSJournal(ctx, rs, trxName);
			}
			
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "Error getting POS Journal. UserID="
					+ userID + ", Date=" + date, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) { }
		}
		return journal;
	}

	/**
	 * Verifica si las cajas diarias están activas para la compañía actual y en
	 * ese caso asigna la caja diaria actual al documento. Para ello debe
	 * existir una Caja Diaria para la fecha actual y el usuario logueado al
	 * sistema donde la misma se encuentre Abierta o En Verificación.
	 * 
	 * @param document
	 *            Documento a registrar
	 * @return <code>true</code> si las cajas diarias están desactivadas para la
	 *         compañía o si están activadas y el documento se pudo registrar a
	 *         la caja diaria actual. <code>false</code> en caso de que no se
	 *         haya encontrado una caja diaria actual en el estado indicado para
	 *         registrar el documento.
	 */
	public static boolean registerDocument(PO document) {
		// Si no están activas las Cajas Diarias no hay nada que registrar
		if (!isActivated()) {
			return true;
		}
		// Verifica si hay una caja diaria activa (usuario y fecha actual) para
		// asociar el documento. Si la hay, asocia el documento a esa caja, sino
		// devuelve false indicando que las cajas diarias están activas pero
		// que no hay una caja diaria abierta para el usuario y fecha actual.
		boolean registered;
		MPOSJournal journal = getCurrent();
		if (journal != null) {
			document.set_Value("C_POSJournal_ID", journal.getC_POSJournal_ID());
			registered = true;
		} else {
			registered = false;
		}
		
		return registered;
	}

	/**
	 * @return Devuelve el Número de Punto de Venta fiscal de la caja diaria
	 *         actual para el usuario logueado. El nro lo obtiene de la
	 *         configuración de TPV (C_POS) asociada a la caja diaria. Si no
	 *         existe la caja diaria o el nro de punto de venta, devuelve
	 *         <code>null</code>.
	 */
	public static Integer getCurrentPOSNumber() {
		Integer posNumber = null;
		MPOSJournal journal = getCurrent();
		if (journal != null) {
			posNumber = journal.getPOS().getPOSNumber();
			posNumber = posNumber == 0 ? null : posNumber;
		}
		return posNumber;
	}

	/**
	 * @return Devuelve el libro de caja actual para el usuario logueado. Si no
	 *         existe la caja diaria o el libro de caja, devuelve
	 *         <code>null</code>.
	 */
	public static Integer getCurrentCashID() {
		Integer cashID = null;
		MPOSJournal journal = getCurrent();
		if (journal != null) {
			cashID = journal.getC_Cash_ID();
			cashID = Util.isEmpty(cashID, true) ? null : cashID;
		}
		return cashID;
	}

	/**
	 * @return Devuelve el libro de caja para la caja diaria parámetro. Si no
	 *         existe el libro de caja, devuelve <code>null</code>.
	 */
	public static Integer getCashID(Properties ctx, Integer posJournalID, String trxName) {
		Integer cashID = null;
		MPOSJournal journal = new MPOSJournal(ctx, posJournalID, trxName);
		if (journal != null) {
			cashID = journal.getC_Cash_ID();
			cashID = Util.isEmpty(cashID, true) ? null : cashID;
		}
		return cashID;
	}
	
	/**
	 * @param ctx
	 * @param posJournalID
	 * @param trxName
	 * @return true si la caja diaria parámetro se encuentra en estado ABIERTO o
	 *         EN VERIFICACION.
	 */
	public static boolean isPOSJournalOpened(Properties ctx, Integer posJournalID, String trxName){
		return DB
				.getSQLValue(
						trxName,
						"SELECT count(*) FROM c_posjournal WHERE c_posjournal_id = ? AND docstatus IN ('"
								+ DOCSTATUS_Opened
								+ "','"
								+ DOCSTATUS_Completed + "')", posJournalID) > 0;
	}
	
	/**
	 * Constructor de PO.
	 * @param ctx
	 * @param C_POSJournal_ID
	 * @param trxName
	 */
	public MPOSJournal(Properties ctx, int C_POSJournal_ID, String trxName) {
		super(ctx, C_POSJournal_ID, trxName);
	}

	/**
	 * Constructor de PO.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPOSJournal(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	
	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#beforeSave(boolean)
	 */
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// No se pueden crear cajas diarias si la funcionalidad no está activada para
		// la compañía.
		if (!isActivated()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "POSJournalNotActivatedError"));
			return false;
		}
		
		MPOS pos = getPOS(true);
		
		// Valida que el Terminal TPV esté configurado como Operación por Cajas Diarias y
		// que el CashBook asociado sea de tipo Caja Diaria. Estos son requisitos necesarios
		// para el correcto funcionamiento de esta entidad.
		if (!MPOS.OPERATIONMODE_POSJournal.equals(pos.getOperationMode())
				|| !MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCashBook().getCashBookType())) {
			
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidPOSOrCashBook"));
			return false;
		}
		
		// El Libro destino no puede ser el mismo que el libro de la caja diaria
		if (!DOCSTATUS_Drafted.equals(getDocStatus()) 
				&& getC_CashTarget_ID() == getC_Cash_ID()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "InvalidPOSTargetCash"));
			return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean beforeDelete() {
		// Solo se permiten borrar los registros en Borrador.
		// Es necesaria esta validación ya que al Abrir una caja, cambia el estado
		// pero no se marca como Processed ya que se requiere poder ingresar 
		// la declaración de valores en caja. Con esta validación se simula como 
		// si el registro estuviese procesado aunque en verdad no lo esté.
		if (!STATUS_Drafted.equals(getDocStatus())) {
			log.saveError("RecordProcessedDeleteError", "", false);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		// Si el perfil no es Supervisor de Cajas Diarias, entonces no puede
		// procesar registros
		if(!Env.isPOSJournalSupervisor(getCtx())){
			m_processMsg = Msg.getMsg(getCtx(), "NotAllowedUserToCompleteOperationShort");
			return false;
		}
		String status = getDocStatus();
		// Bypass para permitir Cerrar una Caja Diaria directamente sin pasar por el
		// completeIt.
		if (DOCACTION_Close.equals(action) || DOCACTION_Close.equals(getDocAction())) {
			status = DOCSTATUS_Completed;
		}
		DocumentEngine engine = new DocumentEngine(this, status);
		return engine.processIt(action, getDocAction(),log ); 
	}

	/**
	 * Apertura de la Caja Diaria
	 * @return indicador de si se pudo o no abrir la caja diaria.
	 */
	private boolean openIt() {
		
		// No es posible abrir una Caja Diaria si ya existe otra abierta para el mismo
		// TPV.
		if (!validateOpenedJournal()) {
			m_processMsg = "@CannotOpenMultiplesJournals@";
			return false;
		}
		
		// No es posible abrir una Caja Diaria para un usuario que ya tiene una
		// caja abierta o en verificación.
		if (!validateUserJournal()) {
			m_processMsg = "@ExistUserJournalInProcess@";
			return false;
		}
		
		// Se crea el Libro de Caja diario para esta Caja
		MCash cash = new MCash(getCashBook(), getDateTrx());
		cash.setC_POSJournal_ID(getC_POSJournal_ID());
		cash.setC_Project_ID(getC_Project_ID());
		if (!cash.save()) {
			m_processMsg = "@CashSaveError@: " + CLogger.retrieveErrorAsString();
			return false;
		}
		
		setC_Cash_ID(cash.getC_Cash_ID());
		
		setDocStatus(DOCSTATUS_Opened);
		setDocAction(DOCACTION_Complete);
		setProcessed(false);
		return true;
	}
	
	@Override
	public String prepareIt() {
		/*
		 * El prepareIt de una Caja Diaria implica la apertura de la misma. Es decir que
		 * cuando se invoca esta acción lo que se está haciendo en realidad es abrir la
		 * caja diaria para que el usuario asignado a la misma tenga la posibilidad de
		 * comenzar a operar en el TPV, siempre y cuando la caja esté en Borrador.
		 * (el prepareIt también se invoca antes de completar. En ese caso se ignora
		 * la lógica de apertura de caja).
		 */
		if (DOCSTATUS_Drafted.equals(getDocStatus())) {
			if (!openIt()) {
				return STATUS_Invalid;
			}
		}
		
		return STATUS_InProgress;
	}

	@Override
	public String completeIt() {
		// Simplemente cambia el estado de la Caja Diaria para que no 
		// quede habilitada para el acceso desde el TPV, pero el Libro
		// de caja sigue en Borrador para que se puedan realizar ajustes.
		
		setDocAction(DOCACTION_Close);
		return STATUS_Completed;
	}

	@Override
	public boolean closeIt() {
		
		// Se requiere el libro de caja destino para poder transferir el saldo
		// del libro de la caja diaria a este libro destino antes de cerrar la
		// caja.
		if (getCashBalance().compareTo(BigDecimal.ZERO) != 0
				&& getC_CashTarget_ID() == 0) {
			m_processMsg = "@POSTargetCashRequired@";
			return false;
		}
		
		// El libro de caja destino de efectivo debe estar en borrador
		if(getC_CashTarget_ID() != 0){
			MCash targetCash = getTargetCash();
			if (!MCash.DOCSTATUS_Drafted.equals(targetCash.getDocStatus())) {
				m_processMsg = "@POSTargetCashInvalidStatus@";
				return false;
			}
		}
		
		// Valida la declaración de valores contra el saldo del libro de caja
		String diffValue = MPreference.searchCustomPreferenceValue(
				VALID_DIFFERENCE_PREFERENCE, getAD_Client_ID(), getAD_Org_ID(),
				getAD_User_ID(), true);
		if(Util.isEmpty(diffValue, true))
			log.severe("No exists preference " + VALID_DIFFERENCE_PREFERENCE
					+ " for cash journal difference. Default "
					+ DEFAULT_DIFFERENCE);
		BigDecimal validDiff = Util.isEmpty(diffValue, true) ? DEFAULT_DIFFERENCE
				: new BigDecimal(diffValue);
		if (getCashBalance().subtract(getCashStatementAmt()).abs().compareTo(validDiff) > 0) {
			m_processMsg = "@DifferentCashStatementAndBalanceError@";
			return false;
		}
		
		// Verifica que no hayan quedado facturas que requieran impresión fiscal
		// y que no hayan sido emitidas fiscalmente.
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos() 
				&& !validateFiscalPrintedInvoices()) {
			return false;
		}
		
		// Transfiere el saldo del libro de caja de la caja diaria hacia el
		// libro de caja destino, siempre que el saldo sea mayor que 0
		if(getCashBalance().compareTo(BigDecimal.ZERO) != 0){
			try {
				MCash.transferCash(getC_Cash_ID(), getC_CashTarget_ID(),
						getCashBalance(), getC_Project_ID(), true, getCtx(), get_TrxName());
			} catch (Exception e) {
				m_processMsg = "@TargetCashTransferError@: " + e.getMessage();
				return false;
			}
		}
		
		// Al cerrar no se podrá operar en el TPV y se debe completar
		// el libro de caja asociado a la caja diaria.
		cash = null; // forzamos a recargar el cash para actualizar valores desde BD.
		// El libro de caja pertenece a una caja diaria
		getCash().setPOSJournalCash(true);
		if (MCash.DOCSTATUS_Drafted.equals(getCash().getDocStatus()) && 
				!DocumentEngine.processAndSave(getCash(), MCash.DOCACTION_Complete, false)) {
			m_processMsg = "@CashCompleteError@: " + getCash().getProcessMsg();
			return false;
		}
		
		// Elimina las correcciones de cobros que hayan quedado sin procesar.
		deleteUnprocessedPaymentFix();
		
		setDocStatus(DOCSTATUS_Closed);
		setDocAction(DOCACTION_None);
		setProcessed(true);
		return true;
	}

	/**
	 * @return La configuración del TPV asociado a esta Caja Diaria (no fuerza
	 *         la carga desde la BD)
	 */
	public MPOS getPOS() {
		return getPOS(false);
	}
	
	/**
	 * @return La configuración del TPV asociado a esta Caja Diaria.
	 */
	public MPOS getPOS(boolean requery) {
		if (pos == null || requery) {
			pos = new MPOS(getCtx(), getC_POS_ID(), get_TrxName());
		}
		return pos;
	}

	/**
	 * @return El Libro de Caja a utilizar. Este es el libro de caja que está
	 *         configurado en el Terminal TPV asociado a esta Caja Diaria.
	 */
	public MCashBook getCashBook() {
		return MCashBook.get(getCtx(), getPOS().getC_CashBook_ID(), get_TrxName());
	}
	
	/**
	 * Verifica si existen cajas diarias abiertas que pertenezcan al mismo TPV
	 * al cual pertenece esta caja.
	 * 
	 * @return <code>true</code> si no hay cajas abiertas, <code>false</code> si
	 *         hay al menos una abierta.
	 */
	private boolean validateOpenedJournal() {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM C_POSJournal " +
			"WHERE DocStatus = '" + DOCSTATUS_Opened + "' " +
			  "AND C_POS_ID = ?";
		int count = DB.getSQLValue(get_TrxName(), sql, getC_POS_ID());
		return count == 0;
	}
	
	/**
	 * Verifica si existe una caja diaria para el usuario de esta caja, que esté
	 * Abierta o En Verificación
	 * 
	 * @return <code>true</code> si no hay una caja con las condiciones
	 *         descriptas anteriormente, <code>false</code> si la caja ya
	 *         existe.
	 */
	private boolean validateUserJournal() {
		String sql =
			"SELECT COUNT(*) " +
			"FROM C_POSJournal " +
			"WHERE AD_User_ID = ? " +
			  "AND DocStatus IN (?,?)";
		long count = (Long)DB.getSQLObject(get_TrxName(), sql, new Object[] {
				getAD_User_ID(), DOCSTATUS_Opened, DOCSTATUS_Completed });
		return count == 0;
	}

	/**
	 * Elimina las correcciones de cobros sin procesar, que están asociadas a
	 * facturas creadas en el ámbito de esta caja diaria
	 */
	private void deleteUnprocessedPaymentFix() {
		String sql = 
			"DELETE FROM C_PaymentFix " + 
			"WHERE C_PaymentFix_ID IN " + 
				"(SELECT C_PaymentFix_ID " + 
				" FROM C_PaymentFix pf " +
				" INNER JOIN C_Invoice i ON (pf.C_Invoice_ID = i.C_Invoice_ID) " +
				" WHERE pf.Processed = 'N' AND i.C_POSJournal_ID = "+ getC_POSJournal_ID() +")";
		int no = DB.executeUpdate(sql, get_TrxName());
		log.fine("Deleted PaymentFix Count = " + no);
	}

	/**
	 * Calcula el importa total de declaración de valores en caja y lo setea
	 * mediante {@link #setCashStatementAmt(BigDecimal)}
	 */
	public void setCashStatementAmt() {
		String sql = 
			"SELECT COALESCE(SUM(Amount),0) FROM C_POSCashStatement WHERE C_POSJournal_ID = ?";
		BigDecimal cashStmtAmt = (BigDecimal) DB.getSQLObject(get_TrxName(),
				sql, new Object[] { getC_POSJournal_ID() });
		setCashStatementAmt(cashStmtAmt);
	}
	
	/**
	 * @return Devuelve el libro de caja asociado a esta caja diaria.
	 */
	public MCash getCash() {
		if (cash == null && getC_Cash_ID() > 0) {
			cash = new MCash(getCtx(), getC_Cash_ID(), get_TrxName());
		}
		return cash;
	}
	
	/**
	 * @return Devuelve el libro de caja destino de efectivo
	 */
	public MCash getTargetCash() {
		MCash cash = null;
		if (getC_CashTarget_ID() > 0) {
			cash = new MCash(getCtx(), getC_CashTarget_ID(), get_TrxName());
		}
		return cash;
	}
	
	@Override
	public BigDecimal getCashBalance() {
		BigDecimal balance = BigDecimal.ZERO;
		if (getCash() != null) {
			balance = getCash().getEndingBalance();
		}
		return balance;
	}

	/**
	 * Solo LOCALE_AR. Busca facturas que esten en estado Completo y que
	 * requieran impresión fiscal pero que no se hayan emitido fiscalmente aún.
	 * En ese caso no es posible cerrar la caja diaria.
	 * 
	 * @return <code>true</code> si no se encuentran facturas con las
	 *         condiciones mencionadas.
	 */
	private boolean validateFiscalPrintedInvoices() {
		List<String> invalidInvoices = new ArrayList<String>();
		String sql =
			"SELECT i.DocumentNo " +
			"FROM C_Invoice i " +
			"INNER JOIN C_DocType dt ON (i.C_DocType_ID = dt.C_DocType_ID) " +
			"WHERE i.C_POSJournal_ID = ? " +      // Pertenecen a esta caja diaria
			  "AND dt.IsFiscal = 'Y' " +          // Requiere impresion fiscal (según tipo de documento)
			  "AND i.DocStatus IN ('CO','CL') " + // Esta Completada o Cerrada
			  "AND i.FiscalAlreadyPrinted = 'N'"; // No fue emitida por controlador fiscal
		
		PreparedStatement pstmt = null;
		ResultSet         rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_POSJournal_ID());
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				invalidInvoices.add(rs.getString("DocumentNo"));
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Cannot get not fiscal printed invoices", e);
			return false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		// Si hay facturas inválidas crea el mensaje de error.
		if (!invalidInvoices.isEmpty()) {
			StringBuffer msg = new StringBuffer("@ExistsNotFiscalPrintedInvoicesError@ <br>");
			for (String invoiceDocumentNo : invalidInvoices) {
				msg.append("- ").append(invoiceDocumentNo).append("<br>");
			}
			m_processMsg = msg.toString();
		}
		
		return invalidInvoices.isEmpty();
	}
	
	//
	// Métodos de DocAction que no aplican para Caja Diaria
	//

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
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

}
