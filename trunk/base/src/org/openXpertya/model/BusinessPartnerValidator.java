package org.openXpertya.model;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class BusinessPartnerValidator implements ModelValidator {

	/** ID de la compañía */
	private int AD_Client_ID;
	
	public BusinessPartnerValidator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Validación de habilitación de la entidad comercial para transacciones
	 * 
	 * @param bp entidad comercial
	 * @return resultado de la llamada
	 */
	protected CallResult validateTrxEnabled(MBPartner bp, PO po, boolean controlData) {
		CallResult cr = new CallResult();
		// Si se debe controlar los datos significa que debemos verificar que si
		// estamos bajo una modificación de un PO ya procesado, se debería poder
		// sin realizar esta restricción 
		if(!bp.isTrxEnabled()){
			boolean sendMsg = !controlData || po.get_Value("Processed") == null
					|| !(((Boolean) po.get_Value("Processed"))).booleanValue();
			if(sendMsg){
				cr.setMsg(Msg.getMsg(bp.getCtx(), "BPartnerTrxDisabled",
					new Object[] { bp.getValue() + " - " + bp.getName() }), true);
			}
		}
		return cr;
	}
	
	@Override
	public String docValidate(PO po, int timing) {
		// Para este validator, la EC es obligatoria
		Integer bPartnerID = (Integer)po.get_Value("C_BPartner_ID");
		if(Util.isEmpty(bPartnerID, true)){
			return null;
		}
		// En prepareIt
		if(timing == TIMING_BEFORE_PREPARE){
			MBPartner bp = new MBPartner(po.getCtx(), bPartnerID, po.get_TrxName());
			// Si la EC relacionada al documento no esta habilitada para transacciones, 
			// no se puede trabajar sobre ningun comprobante
			CallResult cr = validateTrxEnabled(bp, po, false);
			if(cr.isError()) {
				return cr.getMsg();
			}
		}
		return null;
	}

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		setAD_Client_ID(client.getID());
		// Documentos
		engine.addDocValidate(X_C_Order.Table_Name, this);
		engine.addDocValidate(X_C_Invoice.Table_Name, this);
		engine.addDocValidate(X_C_Payment.Table_Name, this);
		engine.addDocValidate(X_C_CashLine.Table_Name, this);
		engine.addDocValidate(X_C_AllocationHdr.Table_Name, this);
		engine.addDocValidate(X_C_BankStatement.Table_Name, this);
		engine.addDocValidate(X_M_InOut.Table_Name, this);
		engine.addDocValidate(X_M_Inventory.Table_Name, this);
		engine.addDocValidate(X_M_Movement.Table_Name, this);
		engine.addDocValidate(X_M_Transfer.Table_Name, this);
		// Modelo
		engine.addModelChange(X_C_Order.Table_Name, this);
		engine.addModelChange(X_C_Invoice.Table_Name, this);
		engine.addModelChange(X_C_Payment.Table_Name, this);
		engine.addModelChange(X_C_CashLine.Table_Name, this);
		engine.addModelChange(X_C_AllocationHdr.Table_Name, this);
		engine.addModelChange(X_C_BankStatement.Table_Name, this);
		engine.addModelChange(X_M_InOut.Table_Name, this);
		engine.addModelChange(X_M_Inventory.Table_Name, this);
		engine.addModelChange(X_M_Movement.Table_Name, this);
		engine.addModelChange(X_M_Transfer.Table_Name, this);
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
		// Para este validator, la EC es obligatoria
		Integer bPartnerID = (Integer)po.get_Value("C_BPartner_ID");
		if(Util.isEmpty(bPartnerID, true)){
			return null;
		}
		// En prepareIt
		if(type == TYPE_NEW || type == TYPE_CHANGE){
			MBPartner bp = new MBPartner(po.getCtx(), bPartnerID, po.get_TrxName());
			// Si la EC relacionada al documento no esta habilitada para transacciones, 
			// no se puede trabajar sobre ningun comprobante
			CallResult cr = validateTrxEnabled(bp, po, true);
			if(cr.isError()) {
				return cr.getMsg();
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