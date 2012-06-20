/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 * 
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MProcess;
import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.util.CLogger;

import es.indeos.transform.Convert;

public class TMProcess extends MProcess implements ImpExPoAdapter  {

	public static TMProcess getProcess(Properties ctx, String value, String trx)	{
		return (TMProcess)ImpExPoCommon.FactoryPO(ctx, trx, "AD_Process", "value=?", TMProcess.class, new Object[]{value});
	}
	
	public TMProcess(Properties ctx, int AD_Process_ID, String trxName) {
		super(ctx, AD_Process_ID, trxName);
	}

	public TMProcess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	//
	
	/** AD_Window_ID Original	*/
	private int orig_AD_Process_ID;
	
	/** AD_Window_ID Importado	*/
	private int imp_AD_Process_ID;
	
	protected ImpExPoCommon tie = new ImpExPoCommon(this);
	
	protected Map trlMap = null;

	public Map getTrlMap() {
		// TODO Auto-generated method stub
		return trlMap;
	}

	public void setTrlMap(Map trlMap) {
		this.trlMap = trlMap;
	}
	
	private Object readResolve() {
		log = CLogger.getCLogger(getClass());
		if (tie == null) tie = new ImpExPoCommon(this);
		return this;
	}
	
	public boolean exportTrl() {
		return tie.exportTrl();
	}

	public int getImp_ID() {
		return imp_AD_Process_ID;
	}

	public int getOrig_ID() {
		return orig_AD_Process_ID;
	}

	public boolean importTrl(String trx) {
		return tie.importTrl(trx);
	}

	public POInfo pa_GetPOInfo() {
		return p_info;
	}

	public String pa_get_Translation(String columnName, String AD_Language) {
		return get_Translation(columnName, AD_Language);
	}

	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return tie.doImport(ctx, trx, parentOld, parentNew);
	}

	public void copyValuesFrom(PO other, int AD_Client_ID, int AD_Org_ID) {
		copyValues(other, this, AD_Client_ID, AD_Org_ID);
	}

	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return getProcess(ctx, getValue(), trx);
	}

	public void setImp_ID(int x) {
		imp_AD_Process_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_AD_Process_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMProcess o = (TMProcess)oldpo;
		
		setAD_Workflow_ID(0);
		setAD_JasperReport_ID(0);
		
		if (passNo == 1) {
			setAD_PrintFormat_ID(Convert.getNewId(TMPrintFormat.class, o.getAD_PrintFormat_ID()));
			setAD_ReportView_ID(Convert.getNewId(TMReportView.class, o.getAD_ReportView_ID()));
		} else {
			setAD_PrintFormat_ID(0);
			setAD_ReportView_ID(0);
		}
	}

}
