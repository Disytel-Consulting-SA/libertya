/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
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
import org.openXpertya.model.X_AD_ReportView;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class TMReportView extends X_AD_ReportView implements ImpExPoAdapter {

	public static TMReportView getReportView (Properties ctx, String Name, String trx) {
		return (TMReportView)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "lower(Name)=?", TMReportView.class, new Object[]{Name.toLowerCase()});
	}
	
	public TMReportView(Properties ctx, int AD_ReportView_ID, String trxName) {
		super(ctx, AD_ReportView_ID, trxName);
		doExport();
	}

	public TMReportView(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		doExport();
	}

	private void doExport() {
		orig_TableName = DB.getSQLValueString(get_TrxName(), "SELECT TableName FROM AD_Table WHERE AD_Table_ID = ? ", getAD_Table_ID());
	}
	
	//
	
	/** AD_Window_ID Original	*/
	private int orig_AD_Process_ID;
	
	private String orig_TableName = null;
	
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
		return getReportView(ctx, getName(), trx);
	}

	public void setImp_ID(int x) {
		imp_AD_Process_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_AD_Process_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMReportView orv = (TMReportView)oldpo;
		
		int TableID = (Integer)DB.getSQLObject(get_TrxName(), "SELECT AD_Table_ID FROM AD_Table WHERE TableName = ? ", new Object[]{orv.orig_TableName});
		
		setAD_Table_ID(TableID);
	}

}
