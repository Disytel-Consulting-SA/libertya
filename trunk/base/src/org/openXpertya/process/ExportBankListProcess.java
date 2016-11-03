package org.openXpertya.process;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MDocType;

public class ExportBankListProcess extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		MBankList bankList = new MBankList(getCtx(), getRecord_ID(), get_TrxName());
		MDocType bankListDocType = MDocType.get(getCtx(), bankList.getC_DocType_ID());
		ExportBankList exporter = null;

		if (MDocType.DOCTYPE_Lista_Galicia.equals(bankListDocType.getDocTypeKey())) {
			exporter = new ExportListaGalicia(getCtx(), bankList, get_TrxName());
		} else if (MDocType.DOCTYPE_Lista_Patagonia.equals(bankListDocType.getDocTypeKey())) {
			exporter = new ExportRetentionsPatagonia(getCtx(), bankList, get_TrxName());
			exporter.export();
			exporter = new ExportListaPatagonia(getCtx(), bankList, get_TrxName());
		}
		return exporter.export();
	}

}
