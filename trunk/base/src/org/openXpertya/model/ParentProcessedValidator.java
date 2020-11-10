package org.openXpertya.model;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ParentProcessedValidator implements ModelValidator {

	/** ID de la compañía */
	private int AD_Client_ID;
	
	public ParentProcessedValidator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String docValidate(PO po, int timing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		setAD_Client_ID(client.getID());
		// Modelo
		engine.addModelChange(X_C_InvoiceLine.Table_Name, this);
	}

	@Override
	public CallResult login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String loginString(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {
		// Verificar cual es la columna enlace a tabla principal y si está procesado, entonces error
		// Solo funciona para enlaces a tabla principal, no para columnas primarias de
		// pestañas, y que la tabla padre tenga el mismo nombre que la columna sin "_ID"
		if(type != TYPE_CHANGE){
			String parentColumnName = DB.getSQLValueString(po.get_TrxName(),
					"SELECT columnname FROM ad_column WHERE ad_table_id = ? and isparent = 'Y' ORDER BY created desc",
					po.get_Table_ID());
			if(!Util.isEmpty(parentColumnName, true)) {
				String processed = DB.getSQLValueString(po.get_TrxName(), "SELECT processed FROM "
						+ parentColumnName.toUpperCase().replace("_ID", "") + " WHERE " + parentColumnName + " = ?",
						(Integer) po.get_Value(parentColumnName));
				if(!Util.isEmpty(processed, true) && processed.equals("Y")) {
					String adMsg = type == TYPE_NEW ? 
							"InsertLinesInDocumentAlreadyProcessed"
							: "RecordProcessedDeleteError";
					return Msg.getMsg(po.getCtx(), adMsg); 
				}
			}
		}
		
		return null;
	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	public void setAD_Client_ID(int aD_Client_ID) {
		AD_Client_ID = aD_Client_ID;
	}

}
