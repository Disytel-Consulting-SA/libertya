package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.model.X_C_BPartner_Padron_BsAs;

public class ImportPadronManager extends AbstractSvrProcess {

	public ImportPadronManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doIt() throws Exception {
		// Clase de Importar Padrón
		ImportPadronBsAsFromCopy ip = null;
		int orgID = getParamValueAsInt("AD_ORG_ID");
		String fileName = (String)getParametersValues().get("NAMECSVFILE");
		String padronType = (String)getParametersValues().get("PADRONTYPE");
		BigDecimal chunkSize = (BigDecimal)getParametersValues().get("CHUNKSIZE");
		int chunkSizeInt = chunkSize.intValue();
		
    	if(padronType.compareTo(X_C_BPartner_Padron_BsAs.PADRONTYPE_PadronContribuyentesTucuman) == 0) {
			ip = new ImportPadronContribuyentesTucuman(getCtx(), orgID, fileName, padronType, chunkSizeInt, get_TrxName());
        }
    	else if(padronType.compareTo(X_C_BPartner_Padron_BsAs.PADRONTYPE_CoeficientesTucuman) == 0) {
    		ip = new ImportPadronCoeficientesTucuman(getCtx(), orgID, fileName, padronType, chunkSizeInt, get_TrxName());
    	}
    	else {
			ip = new ImportPadronBsAsFromCopy(getCtx(), orgID, fileName, padronType, chunkSizeInt, get_TrxName());
    	}
    	
    	return ip.doIt();
	}

}
