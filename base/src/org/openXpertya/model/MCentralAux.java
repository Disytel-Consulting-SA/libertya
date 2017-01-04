package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class MCentralAux extends X_C_CentralAux {
	
	// Métodos estáticos
	
	/**
	 * Crea y copia la información del DTO dentro de la M y la retorna
	 * 
	 * @param auxiliarDTO
	 *            DTO
	 * @return nueva clase persistente de la tabla auxiliar con los datos
	 *         copiados del DTO parámetro
	 */
	public static MCentralAux createAuxiliar(Properties ctx, AuxiliarDTO auxiliarDTO, String trxName){
		// Creo uno nuevo
		MCentralAux auxiliar = new MCentralAux(ctx, 0,	trxName);		
		// Obtener la organización a partir del UID
		int orgID = DB.getSQLValue(auxiliarDTO.getTrxName(),
				"SELECT ad_org_id FROM ad_org WHERE "
						+ auxiliarDTO.getOrgUIDColumnName() + " = ?",
				auxiliarDTO.getOrgUID());
		// Obtener la entidad comercial a partir del UID
		int bpID = DB.getSQLValue(auxiliarDTO.getTrxName(),
				"SELECT c_bpartner_id FROM c_bpartner WHERE "
						+ auxiliarDTO.getBpartnerUIDColumnName() + " = ?",
				auxiliarDTO.getBpartnerUID());
		// Obtener el tipo de documento a partir del value
		Integer docTypeID = 0;
		if (!Util.isEmpty(auxiliarDTO.getDocTypeKey())) {
			docTypeID = DB
					.getSQLValue(
							trxName,
							"SELECT c_doctype_id FROM c_doctype WHERE (ad_client_id = ?) AND (doctypekey = ?)",
							Env.getAD_Client_ID(ctx), 
							auxiliarDTO.getDocTypeKey()); 
		}
		// Seteo el tipo de doc y value
		auxiliar.setC_DocType_ID(docTypeID);
		auxiliar.setDocTypeKey(auxiliarDTO.getDocTypeKey());
		// Copio la info del DTO a la M
		auxiliar.setClientOrg(Env.getAD_Client_ID(ctx),
				orgID);
		auxiliar.setBPartnerUID(auxiliarDTO.getBpartnerUID());
		auxiliar.setAmt(auxiliarDTO.getAmt());
		auxiliar.setAuthCode(auxiliarDTO.getAuthCode());
		auxiliar.setC_BPartner_ID(bpID);
		auxiliar.setDateTrx(auxiliarDTO.getDateTrx());
		auxiliar.setDocStatus(auxiliarDTO.getDocStatus());
		auxiliar.setDocType(auxiliarDTO.getDocType());
		auxiliar.setDocumentNo(auxiliarDTO.getDocumentNo());
		auxiliar.setDocumentRecord_ID(auxiliarDTO.getDocID());
		auxiliar.setDocumentUID(auxiliarDTO.getDocUID());
		auxiliar.setDueDate(auxiliarDTO.getDueDate());
		auxiliar.setPaymentRule(auxiliarDTO.getPaymentRule());
		auxiliar.setPrepayment(auxiliarDTO.isPrepayment());
		auxiliar.setSign(auxiliarDTO.getSign());
		auxiliar.setTenderType(auxiliarDTO.getTenderType());
		auxiliar.setTransactionType(auxiliarDTO.getTransactionType());
		auxiliar.setConfirmed(auxiliarDTO.isConfirmed());
		auxiliar.setReconciled(auxiliarDTO.isReconciled());
		auxiliar.setRegisterType(auxiliarDTO.getRegisterType());
		return auxiliar;
	}
	
	
	// Constructores
	
	public MCentralAux(Properties ctx, int C_CentralAux_ID, String trxName) {
		super(ctx, C_CentralAux_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCentralAux(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Seteo la compañía y organización
	 */
	public void setClientOrg(int AD_Client_ID, int AD_Org_ID){
		setAD_Client_ID(AD_Client_ID);
		setAD_Org_ID(AD_Org_ID);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Verificar que la organización logueada sea la central
		MCentralConfiguration central = MCentralConfiguration.get(getCtx(),
				Env.getAD_Client_ID(getCtx()), get_TrxName());
		int replicationHostID = DB
				.getSQLValue(
						get_TrxName(),
						"SELECT ad_replicationhost_id FROM ad_replicationhost WHERE ad_org_id = ?",
						Env.getAD_Org_ID(getCtx()));
		if (central != null && replicationHostID > 0
				&& getRegisterType().equals(MCentralAux.REGISTERTYPE_Offline) 
				&& central.getAD_ReplicationHost_ID() != replicationHostID) {
			log.saveError("CentralOrgNotLoginOrg", "");
			return false;
		}
		// Si el tipo de doc es factura/comprobante, el tipo de documento no
		// tiene y la clave de doc type no está vacío, seteo el id del tipo de
		// documento en base a la clave 
		if (getDocType().equals(DOCTYPE_Invoice)
				&& Util.isEmpty(getC_DocType_ID(), true)
				&& !Util.isEmpty(getDocTypeKey())) {
			// Obtengo el doc type
			MDocType docType = MDocType.getDocType(getCtx(), getDocTypeKey(),
					get_TrxName());
			setC_DocType_ID(docType.getID());
		}
		// Seteo el signo si no existe ninguno en base al tipo de documento o si es transacción de ventas o compras para pagos/cobros
		if (Util.isEmpty(getSign(), true)) {
			Integer sign = 0;
			if(!Util.isEmpty(getC_DocType_ID(), true)){
				MDocType docType = new MDocType(getCtx(), getC_DocType_ID(),
						get_TrxName());
				sign = Integer.parseInt(docType.getsigno_issotrx());
			}
			else if(getDocType().equals(DOCTYPE_PaymentReceipt)){
				sign = getTransactionType().equals(TRANSACTIONTYPE_Customer)?-1:1;			
			}			
			setSign(sign);
		}		
		return true;
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		if(getRegisterType().equals(MCentralAux.REGISTERTYPE_Offline)){
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(MCentralAux.TRANSACTIONTYPE_Customer.equals(getTransactionType()));
			CallResult result = new CallResult();
			try{
				result = manager.updateBalanceAndStatus(getCtx(),
						new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()),
								get_TrxName()), new MBPartner(getCtx(),
								getC_BPartner_ID(), get_TrxName()), get_TrxName());
			} catch(Exception e){
				result.setMsg(e.getMessage(), true);
			} 
			if(result.isError()){
				log.saveError("Error",result.getMsg());
				return false;
			}
		}
		return success;
	}
	
	@Override
	public void setAuthCode (String AuthCode){
		if(!Util.isEmpty(AuthCode)){
			super.setAuthCode(AuthCode);
		}
		else{
			set_ValueNoCheck("AuthCode", null);
		}
	}
	
	@Override
	public void setPaymentRule (String PaymentRule){
		if(!Util.isEmpty(PaymentRule)){
			super.setPaymentRule(PaymentRule);
		}
		else{
			set_ValueNoCheck("PaymentRule", null);
		}
	}
	
	
}
