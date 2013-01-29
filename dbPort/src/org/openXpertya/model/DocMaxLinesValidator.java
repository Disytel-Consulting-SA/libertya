package org.openXpertya.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class DocMaxLinesValidator implements ModelValidator {

	/** Tablas de los documentos a validar */
	private static List<String> tablesToValidate;
	
	/**
	 * Asociación entre los nombres de tablas y sus tablas detalle. Si se
	 * agregan tablas a validar, entonces se debe actualizar la inicialización
	 * de esta hash
	 */
	private static Map<String, String> linesTable;
	
	/**
	 * Asociación entre las tablas y el nombre de la columna relacionada en la
	 * tabla detalle. Si se agregan tablas a validar, entonces se debe
	 * actualizar la inicialización de esta hash
	 */
	private static Map<String, String> headerColumnsID;
	
	static{
		// Inicializa las tablas a validar, las tablas detalle de éstas y las
		// columnas FK de la cabecera en la tabla detalle
		setTablesToValidate(new ArrayList<String>());
		setHeaderColumnsID(new HashMap<String, String>());
		setLinesTable(new HashMap<String, String>());
		
		getTablesToValidate().add(X_C_AllocationHdr.Table_Name);
		getLinesTable().put(X_C_AllocationHdr.Table_Name, X_C_AllocationLine.Table_Name);
		getHeaderColumnsID().put(X_C_AllocationHdr.Table_Name, X_C_AllocationHdr.Table_Name+"_ID");
		
		getTablesToValidate().add(X_C_BankStatement.Table_Name);
		getLinesTable().put(X_C_BankStatement.Table_Name, X_C_BankStatementLine.Table_Name);
		getHeaderColumnsID().put(X_C_BankStatement.Table_Name, X_C_BankStatement.Table_Name+"_ID");
		
		getTablesToValidate().add(X_C_Cash.Table_Name);
		getLinesTable().put(X_C_Cash.Table_Name, X_C_CashLine.Table_Name);
		getHeaderColumnsID().put(X_C_Cash.Table_Name, X_C_Cash.Table_Name+"_ID");
		
		getTablesToValidate().add(X_M_InOut.Table_Name);
		getLinesTable().put(X_M_InOut.Table_Name, X_M_InOutLine.Table_Name);
		getHeaderColumnsID().put(X_M_InOut.Table_Name, X_M_InOut.Table_Name+"_ID");
		
//		getTablesToValidate().add(X_M_InOutConfirm.Table_Name);
		
		getTablesToValidate().add(X_M_Inventory.Table_Name);
		getLinesTable().put(X_M_Inventory.Table_Name, X_M_InventoryLine.Table_Name);
		getHeaderColumnsID().put(X_M_Inventory.Table_Name, X_M_Inventory.Table_Name+"_ID");
		
		getTablesToValidate().add(X_C_Invoice.Table_Name);
		getLinesTable().put(X_C_Invoice.Table_Name, X_C_InvoiceLine.Table_Name);
		getHeaderColumnsID().put(X_C_Invoice.Table_Name, X_C_Invoice.Table_Name+"_ID");
		
		getTablesToValidate().add(X_GL_Journal.Table_Name);
		getLinesTable().put(X_GL_Journal.Table_Name, X_GL_JournalLine.Table_Name);
		getHeaderColumnsID().put(X_GL_Journal.Table_Name, X_GL_Journal.Table_Name+"_ID");
		
		getTablesToValidate().add(X_GL_JournalBatch.Table_Name);
		getLinesTable().put(X_GL_JournalBatch.Table_Name, X_GL_Journal.Table_Name);
		getHeaderColumnsID().put(X_GL_JournalBatch.Table_Name, X_GL_JournalBatch.Table_Name+"_ID");
		
		getTablesToValidate().add(X_M_Movement.Table_Name);
		getLinesTable().put(X_M_Movement.Table_Name, X_M_MovementLine.Table_Name);
		getHeaderColumnsID().put(X_M_Movement.Table_Name, X_M_Movement.Table_Name+"_ID");
		
		getTablesToValidate().add(X_C_Order.Table_Name);
		getLinesTable().put(X_C_Order.Table_Name, X_C_OrderLine.Table_Name);
		getHeaderColumnsID().put(X_C_Order.Table_Name, X_C_Order.Table_Name+"_ID");
		
//		getTablesToValidate().add(X_C_Payment.Table_Name);
		
		getTablesToValidate().add(X_M_Requisition.Table_Name);
		getLinesTable().put(X_M_Requisition.Table_Name, X_M_RequisitionLine.Table_Name);
		getHeaderColumnsID().put(X_M_Requisition.Table_Name, X_M_Requisition.Table_Name+"_ID");
		
		getTablesToValidate().add(X_M_RMA.Table_Name);
		getLinesTable().put(X_M_RMA.Table_Name, X_M_RMALine.Table_Name);
		getHeaderColumnsID().put(X_M_RMA.Table_Name, X_M_RMA.Table_Name+"_ID");
		
		getTablesToValidate().add(X_S_TimeExpense.Table_Name);
		getLinesTable().put(X_S_TimeExpense.Table_Name, X_S_TimeExpenseLine.Table_Name);
		getHeaderColumnsID().put(X_S_TimeExpense.Table_Name, X_S_TimeExpense.Table_Name+"_ID");
	}
	
	/** ID de la compañía */
	private int AD_Client_ID;
	
	@Override
	public String docValidate(PO po, int timing) {
		// Validar que el documento no exceda la cantidad máxima de líneas
		// configurada en el tipo de documento
		if(timing == TIMING_BEFORE_PREPARE){
			// Si no existe tabla detalle, entonces nada
			if (Util.isEmpty(getLinesTable().get(po.get_TableName()), true)) {
				return null;
			}
			// Obtener la cantidad máxima de líneas que posee el tipo de doc
			Integer docTypeID = (Integer)po.get_Value("C_DocTypeTarget_ID");
			// Si la columna no existe, verificar la otra
			if(Util.isEmpty(docTypeID, true)){
				docTypeID = (Integer)po.get_Value("C_DocType_ID");
			}
			// Si no existe ninguna columna de las anteriores, entonces return
			if(Util.isEmpty(docTypeID, true)){
				return null;
			}
			// Obtener la cantidad máxima de líneas en el documento
			Integer linesMaxCount = DB
					.getSQLValue(
							po.get_TrxName(),
							"SELECT linescountmax FROM c_doctype WHERE c_doctype_id = ?",
							docTypeID);
			// Si la cantidad de la línea es 0, entonces hay límites
			if(linesMaxCount == 0){
				return null;
			}
			// Obtener la cantidad de líneas del documento
			Integer linesCount = DB.getSQLValue(
					po.get_TrxName(),
					"SELECT count(*) as linesCount FROM "
							+ getLinesTable().get(po.get_TableName())
							+ " WHERE "
							+ getHeaderColumnsID().get(po.get_TableName())
							+ " = ?", po.getID());
			// Si la cantidad de líneas del doc es mayor a la cantidad máxima de
			// líneas configurada en el tipo de documento, entonces error
			if(linesCount > linesMaxCount){
				return Msg.getMsg(po.getCtx(), "DocLineSurpassMaxLines");
			}
		}
		return null;
	}

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		setAD_Client_ID(client.getID());
		// Agregar las tablas a validar y adicionales
		for (String docTable : getTablesToValidate()) {
			// Agregar la validación en esta tabla
			engine.addDocValidate(docTable, this);
		}
	}

	@Override
	public CallResult login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	protected static void setLinesTable(Map<String, String> linesTable) {
		DocMaxLinesValidator.linesTable = linesTable;
	}

	protected static Map<String, String> getLinesTable() {
		return linesTable;
	}

	protected static void setHeaderColumnsID(Map<String, String> headerColumnsID) {
		DocMaxLinesValidator.headerColumnsID = headerColumnsID;
	}

	protected static Map<String, String> getHeaderColumnsID() {
		return headerColumnsID;
	}

	public void setAD_Client_ID(int aD_Client_ID) {
		AD_Client_ID = aD_Client_ID;
	}

	protected static void setTablesToValidate(List<String> tablesToValidate) {
		DocMaxLinesValidator.tablesToValidate = tablesToValidate;
	}

	protected static List<String> getTablesToValidate() {
		return tablesToValidate;
	}

	@Override
	public String loginString(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		// TODO Auto-generated method stub
		return null;
	}

}
