package org.openXpertya.process;

import java.util.List;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MProductPO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Proceso que activa/desactiva los artículos del proveedor
 * @author MC - Disytel
 *
 */
public class ProcessPO extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		Integer bpID = getParamValueAsInt("C_BPARTNER_ID");
		bpID = !Util.isEmpty(bpID, true)?bpID:getRecord_ID();
		
		MBPartner bp = new MBPartner(getCtx(), bpID, get_TrxName());
		boolean isActive = bp.getProcessPO().equals(MBPartner.PROCESSPO_ActivateVendorProducts);
		List<MProductPO> pos = MProductPO.getOfBPartner(getCtx(), bp.getID(), false, get_TrxName());
		for (MProductPO mProductPO : pos) {
			mProductPO.setIsActive(isActive);
			if(!mProductPO.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		bp.setProcessPO(isActive
				? MBPartner.PROCESSPO_DesactivateVendorProducts
				: MBPartner.PROCESSPO_ActivateVendorProducts);
		if(!bp.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		return Msg.translate(getCtx(), "@Updated@ @Records@: "+pos.size());
	}

}
