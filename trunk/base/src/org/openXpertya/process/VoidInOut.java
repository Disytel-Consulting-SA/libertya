package org.openXpertya.process;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MOrder;

public class VoidInOut extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		MInOut inOut = new MInOut(getCtx(), getParamValueAsInt("M_INOUT_ID"), get_TrxName());
		MDocType dt = MDocType.get(getCtx(), inOut.getC_DocType_ID());
		DocumentCompleteProcess dcp = new DocumentCompleteProcess(getCtx(), dt, MOrder.DOCACTION_Void, null, null,
				" AND " + inOut.get_TableName() + "." + inOut.get_TableName() + "_ID = " + inOut.getID(), get_TrxName());
		return dcp.start();
	}

}
