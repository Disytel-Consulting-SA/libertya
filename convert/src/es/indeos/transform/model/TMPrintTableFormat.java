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

import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.print.MPrintTableFormat;
import org.openXpertya.util.CLogger;

public class TMPrintTableFormat extends MPrintTableFormat implements ImpExPoAdapter {

	public static TMPrintTableFormat getPrintTableFormat(Properties ctx, String Name, String trx) {
		return (TMPrintTableFormat)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "Name = ?", TMPrintTableFormat.class, new Object[]{Name});
	}
	
	public TMPrintTableFormat(Properties ctx, int AD_PrintTableFormat_ID, String trxName) {
		super(ctx, AD_PrintTableFormat_ID, trxName);
	}

	public TMPrintTableFormat(Properties ctx, ResultSet rs, String trxName) {
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
		TMPrintTableFormat pn = (TMPrintTableFormat)parentNew;
		
		return getPrintTableFormat(ctx, getName(), trx);
	}

	public void setImp_ID(int x) {
		imp_AD_Process_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_AD_Process_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		
	}

}
