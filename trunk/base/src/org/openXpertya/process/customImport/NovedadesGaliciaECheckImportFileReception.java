package org.openXpertya.process.customImport;

import java.util.Properties;

import org.openXpertya.model.X_I_PaymentBankNews;

public class NovedadesGaliciaECheckImportFileReception extends NovedadesGaliciaECheckImportFile {

	public NovedadesGaliciaECheckImportFileReception() {
		// TODO Auto-generated constructor stub
	}
	
	public NovedadesGaliciaECheckImportFileReception(Properties ctx, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		initialize();
	}

	@Override
	public void readHead(String line) throws Exception {
		// No se hace nada con los datos de la cabecera
	}

	@Override
	protected void setPaymentNewsFields(X_I_PaymentBankNews pbn, String[] fields) {
		// Campo 12: Estado
		// Campo 13: Motivos del Rechazo
		pbn.setPayment_Status(fields[11]);
		String errores = "";
		if(fields.length > 11) {
			for (int i = 12; i < fields.length; i++) {
				errores += fields[i]+" ; ";
			}
		}
		pbn.setPayment_Status_Msg_Description(errores);
	}

}
