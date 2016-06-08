package org.openXpertya.process;

import java.util.List;

import org.openXpertya.model.MCreditCardClose;
import org.openXpertya.model.MCreditCardCloseLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;

public class UpdateCouponDetail extends SvrProcess {
	
	private int C_CreditCard_Close_ID;
	private int AD_Org_ID;
	MCreditCardClose creditcardclose;

	@Override
	protected void prepare() {
		setC_CreditCard_Close_ID(getRecord_ID());
		creditcardclose = new MCreditCardClose(getCtx(), getC_CreditCard_Close_ID(), get_TrxName()); 		
	}

	@Override
	protected String doIt() throws Exception {
		//Se obtienen todos los Cupones cuya Fecha de Transacción es igual a la Fecha de Cierre y la Organización es igual a la Organización del Cierre, filtrando solo Cupones en estado Completo o Cerrado.
		List<PO> cupones = PO.find(getCtx(), "c_payment", "(ad_client_id = ?) AND (ad_org_id = ?) AND (datetrx::date = ?::date) AND (docstatus IN ('CO','CL')) AND tendertype = 'C' ", new Object[]{creditcardclose.getAD_Client_ID(),creditcardclose.getAD_Org_ID(),creditcardclose.getDateTrx()}, null, get_TrxName(),true);
		//Por cada cupón se genera en Registro de Línea de Cierre asociando el mismo al Cupón Original 
		int cant= 0;
		if(cupones.size() > 0){
			for (PO cupon : cupones) {
				MPayment cuponAux = (MPayment) cupon;
				if (!MCreditCardCloseLine.existRecordFor(getCtx(), "c_creditcard_closeline", "C_Payment_ID = ? AND AD_Org_ID = ? ", new Object[]{cuponAux.getC_Payment_ID(), cuponAux.getAD_Org_ID()}, get_TrxName())){
					MCreditCardCloseLine line = new MCreditCardCloseLine(getCtx(), 0, get_TrxName());
					line.setAD_Org_ID(cuponAux.getAD_Org_ID());
					line.setDocumentNo(cuponAux.getDocumentNo());
					line.setM_EntidadFinancieraPlan_ID(cuponAux.getM_EntidadFinancieraPlan_ID());
					line.setCouponNumber(cuponAux.getCouponNumber());
					line.setCouponBatchNumber(cuponAux.getCouponBatchNumber());
					line.setCreditCardNumber(cuponAux.getCreditCardNumber());
					line.setPayAmt(cuponAux.getPayAmt());
					line.setDescription(cuponAux.getDescription());
					line.setDateTrx(cuponAux.getCreated()); 
					line.setC_POSJournal_ID(cuponAux.getC_POSJournal_ID());
					line.setC_CreditCard_Close_ID(creditcardclose.getC_CreditCard_Close_ID());
					line.setC_Payment_ID(cuponAux.getC_Payment_ID());
					if (!line.save()){
							throw new Exception(CLogger.retrieveErrorAsString());
					}
					cant++;
					}
				}
		}
		return "@RecordsSaved@: "+cant;
	}

	public int getC_CreditCard_Close_ID() {
		return C_CreditCard_Close_ID;
	}

	public void setC_CreditCard_Close_ID(int C_CreditCard_Close_ID) {
		this.C_CreditCard_Close_ID = C_CreditCard_Close_ID;
	}

	public int getAD_Org_ID() {
		return AD_Org_ID;
	}

	public void setAD_Org_ID(int aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

}
