package org.openXpertya.process;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MDocType;

public class ExportBankListProcess extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		MBankList bankList = new MBankList(getCtx(), getRecord_ID(), get_TrxName());
		MDocType bankListDocType = MDocType.get(getCtx(), bankList.getC_DocType_ID());
		ExportBankList bankListExporter = null;
		if(MDocType.DOCTYPE_Lista_Galicia.equals(bankListDocType.getDocTypeKey())){
			bankListExporter = new ExportListaGalicia(getCtx(), bankList, get_TrxName());
		}
		else if(MDocType.DOCTYPE_Lista_Patagonia.equals(bankListDocType.getDocTypeKey())){
			bankListExporter = new ExportListaPatagonia(getCtx(), bankList, get_TrxName());
		}
			
		return bankListExporter.export();
	}

}
