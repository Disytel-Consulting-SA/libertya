package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_BankStatement;
import org.openXpertya.model.X_C_BankTransfer;
import org.openXpertya.model.X_C_Cash;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_C_Repair_Order;
import org.openXpertya.model.X_GL_Journal;
import org.openXpertya.model.X_M_Amortization;
import org.openXpertya.model.X_M_BoletaDeposito;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.model.X_M_Inventory;
import org.openXpertya.model.X_M_Movement;
import org.openXpertya.model.X_M_Production;
import org.openXpertya.model.X_M_Transfer;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;


public class DocumentCompleteProcess extends SvrProcess {

	/**
	 * Tablas a verificar por tipo de documento. Tienen prioridad por sobre las
	 * de tipo de documento base
	 */
	private static Map<String, List<String>> docTables;
	
	/** Tablas a verificar por tipo de documento base */
	private static Map<String, List<String>> docBaseTables;
	
	/** Estados de los documentos en base a la acción a tomar */
	private static Map<String, List<String>> docStatusByDocAction;
	
	/** Nombre de columna de fecha por tabla */
	private static Map<String, String> dateColumnNameByTable;
	
	static{
		// Tablas por tipo de documento base
		// Orden de los doc base como la referencia
		
		docBaseTables = new HashMap<String, List<String>>();
		docBaseTables.put(MDocType.DOCBASETYPE_Amortization,
				Arrays.asList((new String[] { X_M_Amortization.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_APCreditMemo,
				Arrays.asList((new String[] { X_C_Invoice.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_APInvoice,
				Arrays.asList((new String[] { X_C_Invoice.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_APPayment,
				Arrays.asList((new String[] { X_C_BankTransfer.Table_Name, X_C_Payment.Table_Name, X_C_AllocationHdr.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ARCreditMemo,
				Arrays.asList((new String[] { X_C_Invoice.Table_Name })));
	
		docBaseTables.put(MDocType.DOCBASETYPE_ARProFormaInvoice,
				Arrays.asList((new String[] { X_C_Invoice.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ARInvoice,
				Arrays.asList((new String[] { X_C_Invoice.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ARReceipt,
				Arrays.asList((new String[] { X_C_BankTransfer.Table_Name, X_C_Payment.Table_Name, X_C_AllocationHdr.Table_Name, X_M_BoletaDeposito.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_PaymentAllocation,
				Arrays.asList((new String[] { X_C_AllocationHdr.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_BankStatement,
				Arrays.asList((new String[] { X_C_BankStatement.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_CashJournal,
				Arrays.asList((new String[] { X_C_Cash.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_GLDocument,
				Arrays.asList((new String[] { X_GL_Journal.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_GLJournal,
				Arrays.asList((new String[] { X_GL_Journal.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaterialPhysicalInventory,
				Arrays.asList((new String[] { X_M_Inventory.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaterialMovement,
				Arrays.asList((new String[] { X_M_Movement.Table_Name, X_M_Transfer.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaterialProduction,
				Arrays.asList((new String[] { X_M_Production.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaterialReceipt,
				Arrays.asList((new String[] { X_M_InOut.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaterialDelivery,
				Arrays.asList((new String[] { X_M_InOut.Table_Name })));		
		
		docBaseTables.put(MDocType.DOCBASETYPE_MaintenanceOrder,
				Arrays.asList((new String[] { X_C_Repair_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrderIssue,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrderMethodVariation,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrder,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));		
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrderReceipt,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrderUseVariation,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_ManufacturingOrderRateVariation,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		// Se comenta porque no existe clase DocAction para los siguientes tipos
		// de documento base
		/*docBaseTables.put(MDocType.DOCBASETYPE_MatchInvoice,
				Arrays.asList((new String[] { X_M_MatchInv.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_MatchPO,
				Arrays.asList((new String[] { X_M_MatchPO.Table_Name })));

		docBaseTables.put(MDocType.DOCBASETYPE_ProjectIssue,
				Arrays.asList((new String[] { X_M_Amortization.Table_Name })));*/
		
		docBaseTables.put(MDocType.DOCBASETYPE_PurchaseOrder,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_PurchaseRequisition,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		docBaseTables.put(MDocType.DOCBASETYPE_SalesOrder,
				Arrays.asList((new String[] { X_C_Order.Table_Name })));
		
		// Tablas por tipo de documento
		docTables = new HashMap<String, List<String>>();
		
		// Estados de documento en base a acción a realizar
		// Se toman las acciones y estados de la factura como base, deberían ser todos iguales
		docStatusByDocAction = new HashMap<String, List<String>>();
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Approve,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted,
						MInvoice.DOCSTATUS_InProgress }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Close,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Complete,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted,
						MInvoice.DOCSTATUS_InProgress, 
						MInvoice.DOCSTATUS_Approved }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Invalidate,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_InProgress }));
		
//		docStatusByDocAction.put(
//				MInvoice.DOCACTION_None,
//				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted,
//						MInvoice.DOCSTATUS_InProgress }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Post,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed,
						MInvoice.DOCSTATUS_Closed, 
						MInvoice.DOCSTATUS_Reversed, 
						MInvoice.DOCSTATUS_Voided }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Prepare,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Re_Activate,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Reject,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted,
						MInvoice.DOCSTATUS_InProgress, 
						MInvoice.DOCSTATUS_Approved }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Reverse_Accrual,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Reverse_Correct,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed }));
		
//		docStatusByDocAction.put(
//				MInvoice.DOCACTION_Unlock,
//				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted,
//						MInvoice.DOCSTATUS_InProgress }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_Void,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Completed }));
		
		docStatusByDocAction.put(
				MInvoice.DOCACTION_WaitComplete,
				Arrays.asList(new String[] { MInvoice.DOCSTATUS_Drafted, 
						MInvoice.DOCSTATUS_InProgress, 
						MInvoice.DOCSTATUS_Approved }));
		
		// Nombre de columnas de fecha por tabla
		dateColumnNameByTable = new HashMap<String, String>();
		
		dateColumnNameByTable.put(X_M_Amortization.Table_Name, "AmortizationDate");
		dateColumnNameByTable.put(X_C_Invoice.Table_Name, "DateInvoiced");
		dateColumnNameByTable.put(X_C_BankTransfer.Table_Name, "DateTrx");
		dateColumnNameByTable.put(X_C_Payment.Table_Name, "DateTrx");
		dateColumnNameByTable.put(X_C_AllocationHdr.Table_Name, "DateTrx");
		dateColumnNameByTable.put(X_C_BankStatement.Table_Name, "StatementDate");
		dateColumnNameByTable.put(X_C_Cash.Table_Name, "StatementDate");
		dateColumnNameByTable.put(X_GL_Journal.Table_Name, "DateDoc");
		dateColumnNameByTable.put(X_M_Inventory.Table_Name, "MovementDate");
		dateColumnNameByTable.put(X_M_Movement.Table_Name, "MovementDate");
		dateColumnNameByTable.put(X_M_Transfer.Table_Name, "DateTrx");
		dateColumnNameByTable.put(X_M_Production.Table_Name, "MovementDate");
		dateColumnNameByTable.put(X_M_InOut.Table_Name, "MovementDate");
		dateColumnNameByTable.put(X_C_Repair_Order.Table_Name, "DateOrdered");
		dateColumnNameByTable.put(X_C_Order.Table_Name, "DateOrdered");
	}
	
	/** Tipo de documento */
	private MDocType docType;
	
	/** Acción sobre los documentos */
	private String docAction;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Condiciones where adicionales */
	private String aditionalWhereClause;
	
	/** Contexto local */
	private Properties localCtx;
	
	/** Nombre de transacción local */
	private String localTrxName;
	
	public DocumentCompleteProcess(){}
	
	public DocumentCompleteProcess(Properties ctx, MDocType docType,
			String docAction, Timestamp dateFrom, Timestamp dateTo,
			String aditionalWhereClause, String trxName) {
		this.localCtx = ctx;
		this.localTrxName = trxName;
		setDocType(docType);
		setDocAction(docAction);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setAditionalWhereClause(aditionalWhereClause);
	}
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_DocType_ID")){
				setDocType(MDocType.get(getCtx(), para[i].getParameterAsInt()));
			}
			else if (name.equals("DateFrom")){
				setDateFrom((Timestamp)para[i].getParameter());
				setDateTo((Timestamp)para[i].getParameter_To());
			}
			else if (name.equals("DocAction")){
				setDocAction((String)para[i].getParameter());
			}
		}
	}

	/**
	 * @return Lista de tablas de documentos del tipo de documento en cuestión
	 */
	protected List<String> getDocumentTables(){
		List<String> documentTables = docTables.get(getDocType().getDocTypeKey());
		if(documentTables == null || documentTables.size() == 0){
			documentTables = docBaseTables.get(getDocType().getDocBaseType());
		}
		return documentTables;
	}
	
	/**
	 * @return el conjunto con los estados del documento dependiendo la acción a
	 *         realizar. Por ejemplo, si los estados son DR e IP, entonces este
	 *         método devuelve ('DR','IP')
	 */
	protected String getDocStatusByDocActionSetWhereClause(){
		StringBuffer setWhereClause = new StringBuffer(" (");
		// Iterar por los estados del tipo de documento y armar el conjunto para
		// la cláusula where
		for (String docStatus : docStatusByDocAction.get(getDocAction())) {
			setWhereClause.append("'").append(docStatus).append("'")
					.append(",");
		}
		setWhereClause = new StringBuffer(
				setWhereClause.lastIndexOf(",") > 0 ? setWhereClause.substring(
						0, setWhereClause.lastIndexOf(","))
						: setWhereClause);
		setWhereClause.append(") ");
		return setWhereClause.toString();
	}
	
	/**
	 * @param table tabla en cuestión
	 * @return condición del where para la búsqueda por tipo de documento, vacío
	 *         en caso que no existan las columnas C_DocTypeTarget_ID o
	 *         C_DocType_ID
	 */
	protected String getDocTypeCondition(M_Table table, PO generalPO){
		String docTypeCondition = "";
		// 1) C_DocTypeTarget_ID
		// 2) C_DocType_ID
		// Si no exista ninguna de ellas, no lleva condición por tipo de
		// documento
		if(generalPO.get_ColumnIndex("C_DocTypeTarget_ID") > -1){
			docTypeCondition = " AND C_DocTypeTarget_ID = "+getDocType().getID()+" ";
		}
		else if(generalPO.get_ColumnIndex("C_DocType_ID") > -1){
			docTypeCondition = " AND C_DocType_ID = "+getDocType().getID()+" ";
		}
		return docTypeCondition;
	}
	
	/**
	 * @param table tabla en cuestión
	 * @return condición del where para la búsqueda por fechas, vacío
	 *         en caso que no se hayan pasado los parámetros de fecha
	 */
	protected String getDateCondition(M_Table table, PO generalPO){
		StringBuffer dateCondition = new StringBuffer();
		String dateColumnName = dateColumnNameByTable.get(table.getTableName());
		if(getDateFrom() != null){
			dateCondition.append(" AND ").append(dateColumnName)
					.append("::date >= ?::date ");
		}
		if(getDateTo() != null){
			dateCondition.append(" AND ").append(dateColumnName)
					.append("::date <= ?::date ");
		}
		return dateCondition.toString();
	}
	
	/**
	 * 
	 * @param table tabla en cuestión
	 * @return ORDER BY para la consulta de búsqueda de documentos, vacío en
	 *         caso que no exista la columna DocumentNo
	 */
	protected String getDocTypeOrder(M_Table table, PO generalPO){
		String docTypeOrder = "";
		// 1) DocumentNo
		// Si no exista ninguna de ellas, no lleva orden
		if(generalPO.get_ColumnIndex("DocumentNo") > -1){
			docTypeOrder = " ORDER BY DocumentNo ";
		}
		return docTypeOrder;
	}
	
	protected String getDocumentSearchSQL(M_Table table, PO generalPO){
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(table.getTableName());
		sql.append(" WHERE ad_org_id = ? AND isactive = 'Y' AND docstatus IN "
				+ getDocStatusByDocActionSetWhereClause());
		// TODO Filtro de documentos por fecha. Hay que crear otra hash con
		// clave tabla y valor el nombre de la columna de fecha del documento ya
		// que las columnas de fecha tienen nombres diferentes por tabla 
		if(table != null){
			sql.append(getDocTypeCondition(table, generalPO));
			sql.append(getDateCondition(table, generalPO));
			sql.append(!Util.isEmpty(getAditionalWhereClause(), true) ? getAditionalWhereClause()
					: "");
			sql.append(getDocTypeOrder(table, generalPO));
		}
		return sql.toString();
	}
	
	/**
	 * @param completeds documentos completados correctamente
	 * @param erroneous documentos completados con error
	 * @return mensaje final del proceso
	 */
	protected String getMsg(List<String> completeds, List<String> erroneous){
		HTMLMsg msg = new HTMLMsg();
		// Completados correctamente
		HTMLMsg.HTMLList listCompleted = msg.createList(
				"GOOD",
				"ul",
				completeds.size() + " "
						+ Msg.getMsg(getCtx(), "CorrectlyProcessedDocuments"));
		for (String completed : completeds) {
			msg.createAndAddListElement(completed, completed, listCompleted);
		}
		msg.addList(listCompleted);
		// Completados con error
		HTMLMsg.HTMLList listErroneus = msg.createList(
				"ERROR",
				"ul",
				erroneous.size() + " "
						+ Msg.getMsg(getCtx(), "NotCorrectlyProcessedDocuments"));
		for (String error : erroneous) {
			msg.createAndAddListElement(error, error, listErroneus);
		}
		msg.addList(listErroneus);
		return msg.toString();
	}
	
	@Override
	protected String doIt() throws Exception {
		// Obtener las tablas a verificar documentos a completar a partir del
		// tipo de documento o del tipo de documento base 
		List<String> documentTables = getDocumentTables();
		if(documentTables == null || documentTables.size() == 0){
			return Msg.getMsg(getCtx(), "NoSearchDocuments");
		}
		
		// Si no se agregó una acción, por defecto es Completar
		if(Util.isEmpty(getDocAction(), true)){
			setDocAction(MInvoice.ACTION_Complete);
		}
		
		// Iterar por las tablas, realizando las búsquedas y completando los
		// documentos
		PreparedStatement ps = null;
		ResultSet rs = null;
		M_Table table;
		PO document, generalPO;
		List<String> completed = new ArrayList<String>();
		List<String> erroneous = new ArrayList<String>();
		String poIdentifier;
		for (String documentTable : documentTables) {
			// Conseguir los documentos e iterar por todos ellos y completarlos
			table = M_Table.get(getCtx(), documentTable);
			generalPO = table.getGeneralPO(0, get_TrxName());
			ps = DB.prepareStatement(getDocumentSearchSQL(table, generalPO),
					get_TrxName(), true);
			int i = 1;
			ps.setInt(i++, Env.getAD_Org_ID(getCtx()));
			if(getDateFrom() != null){
				ps.setTimestamp(i++, getDateFrom());
			}
			if(getDateTo() != null){
				ps.setTimestamp(i++, getDateTo());
			}
			rs = ps.executeQuery();
			while(rs.next()){
				// Transacción por documento
				Trx.getTrx(get_TrxName()).start();
				// Obtener el documento
				document = table.getPO(rs, get_TrxName());
				// Completar
				poIdentifier = generalPO.get_ColumnIndex("DocumentNo") > -1 ? document
						.get_ValueAsString("DocumentNo") : DisplayUtil
						.getDisplayByIdentifiers(getCtx(), document,
								table.getID(), get_TrxName());
				if (!DocumentEngine.processAndSave((DocAction)document,
						getDocAction(), false)) {
					// Rollback
					erroneous.add(poIdentifier+": "+document.getProcessMsg());
					Trx.getTrx(get_TrxName()).rollback();
				}
				else{
					// Commit
					completed.add(poIdentifier);
					Trx.getTrx(get_TrxName()).commit();	
				}
			}
			rs.close();
			ps.close();
		}
		
		return getMsg(completed, erroneous);
	}

	/**
	 * Comienza la ejecución del proceso.
	 * 
	 * @return Mesaje HTML con los documentos procesados
	 * @throws Exception
	 *             cuando se produce un error en el proceso 
	 */
	public String start() throws Exception {
		return doIt();
	}
	
	public MDocType getDocType() {
		return docType;
	}

	public void setDocType(MDocType docType) {
		this.docType = docType;
	}

	public String getDocAction() {
		return docAction;
	}

	public void setDocAction(String docAction) {
		this.docAction = docAction;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public String getAditionalWhereClause() {
		return aditionalWhereClause;
	}

	public void setAditionalWhereClause(String aditionalWhereClause) {
		this.aditionalWhereClause = aditionalWhereClause;
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}
	
	@Override
	public String get_TrxName() {
		if (!Util.isEmpty(localTrxName, true)) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}
}
