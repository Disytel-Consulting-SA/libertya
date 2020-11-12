package org.openXpertya.process.customImport;

import java.util.Properties;

import org.openXpertya.model.X_I_PaymentBankNews;

public class NovedadesGaliciaECheckImportFileFeedback extends NovedadesGaliciaECheckImportFile {

	public NovedadesGaliciaECheckImportFileFeedback() {
		// TODO Auto-generated constructor stub
	}
	
	public NovedadesGaliciaECheckImportFileFeedback(Properties ctx, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		initialize();
	}

	@Override
	public void readHead(String line) throws Exception {
		// No hace nada con la cabecera
	}

	@Override
	protected void setPaymentNewsFields(X_I_PaymentBankNews pbn, String[] fields) {
		// Campo 12: Nro de Cheque
		// Campo 13: ID de Cheque Red Coelsa
		// Campo 14: Estado
		// Campo 15: Motivos del Rechazo
		pbn.setCheckNo(fields[11]);
		if(fields.length > 13){
			pbn.setPayment_Status_Msg(fields[13]);
			String errores = "";
			if(fields.length > 13) {
				for (int i = 14; i < fields.length; i++) {
					errores += fields[i];
				}
			}
			pbn.setPayment_Status_Msg_Description(errores);
		}
	}
	
}
