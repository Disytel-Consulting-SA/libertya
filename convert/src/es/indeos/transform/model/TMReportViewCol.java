/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.model.X_AD_ReportView_Col;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class TMReportViewCol extends X_AD_ReportView_Col implements ImpExPoAdapter {

	public static TMReportViewCol getReportView(Properties ctx, int AD_ReportView_ID, String ColumnName, String trx) {
		return (TMReportViewCol)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "AD_ReportView_ID = ? AND EXISTS ( SELECT * FROM AD_Column c WHERE c.ad_column_id = " + Table_Name + ".ad_column_id AND lower(c.columnname) = ? ) ", TMReportViewCol.class, new Object[]{AD_ReportView_ID,ColumnName});
	}
	
	public static ArrayList getReportView(Properties ctx, int AD_ReportView_ID, String trx) {
		return ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "AD_ReportView_ID = ?", TMReportViewCol.class, new Object[]{AD_ReportView_ID}, Integer.MAX_VALUE);
	}
	
	public TMReportViewCol(Properties ctx, int AD_ReportView_Col_ID, String trxName) {
		super(ctx, AD_ReportView_Col_ID, trxName);
		doExport();
	}

	public TMReportViewCol(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		doExport();
	}

	private void doExport() {
		orig_ColumnName = DB.getSQLValueString(get_TrxName(), "SELECT ColumnName FROM AD_Table WHERE AD_Table_ID = ? ", getAD_Column_ID());
	}
	
	//
	
	/** AD_Window_ID Original	*/
	private int orig_AD_Process_ID;
	
	private String orig_ColumnName = null;
	
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
		TMReportView pn = (TMReportView)parentNew;
		
		return getReportView(ctx, pn.getID(), orig_ColumnName, trx);
	}

	public void setImp_ID(int x) {
		imp_AD_Process_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_AD_Process_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMReportView pn = (TMReportView)parentNew;

		int ColumnID = (Integer)DB.getSQLObject(get_TrxName(), "SELECT AD_Column_ID FROM AD_Column WHERE AD_Table_ID = ? AND lower(ColumnName) = ? ", new Object[]{pn.getAD_Table_ID(),orig_ColumnName.toLowerCase()});
		
		setAD_ReportView_ID(pn.getID());
		setAD_Column_ID(ColumnID);
	}
}

