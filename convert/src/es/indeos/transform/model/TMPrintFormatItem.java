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
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

import es.indeos.transform.Convert;

public class TMPrintFormatItem extends MPrintFormatItem implements ImpExPoAdapter {

	public static TMPrintFormatItem getPrintFormatItem(Properties ctx, int AD_PrintFormat_ID, String ColumnName, String trx) {
		return (TMPrintFormatItem)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "AD_PrintFormat_ID = ? AND EXISTS ( SELECT * FROM AD_Column c WHERE c.ad_column_id = " + Table_Name + ".ad_column_id AND lower(c.columnname) = ? ) ", TMPrintFormatItem.class, new Object[]{AD_PrintFormat_ID, ColumnName.toLowerCase()});
	}
	
	public static ArrayList getPrintFormatItem(Properties ctx, int AD_PrintFormat_ID, String trx) {
		return ImpExPoCommon.FactoryPO(ctx, trx, "AD_PrintFormatItem", "AD_PrintFormat_ID = ? ", TMPrintFormatItem.class, new Object[]{AD_PrintFormat_ID}, Integer.MAX_VALUE);
	}
	
	public TMPrintFormatItem(Properties ctx, int AD_PrintFormatItem_ID, String trxName) {
		super(ctx, AD_PrintFormatItem_ID, trxName);
		doExport();
	}
	
	public TMPrintFormatItem(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		doExport();
	}

	private void doExport() {
		orig_ColumnName = ImpExPoCommon.GetColumnName(getAD_Column_ID()); // DB.getSQLValueString(get_TrxName(), "SELECT ColumnName FROM AD_Column WHERE AD_Column_ID = ?", getAD_Column_ID());
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
		TMPrintFormat pn = (TMPrintFormat)parentNew;
		
		return getPrintFormatItem(ctx, pn.getAD_PrintFormat_ID(), orig_ColumnName, trx);
	}

	public void setImp_ID(int x) {
		imp_AD_Process_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_AD_Process_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMPrintFormatItem opfi = (TMPrintFormatItem)oldpo;
		TMPrintFormat pn = (TMPrintFormat)parentNew;
		
		int ColumnID = ImpExPoCommon.GetColumnIdByName(pn.getAD_Table_ID(), opfi.orig_ColumnName.toLowerCase()); //(Integer)DB.getSQLObject(get_TrxName(), "SELECT AD_Column_ID FROM AD_Column WHERE AD_Table_ID = ? AND lower(ColumnName) = ? ", new Object[]{pn.getAD_Table_ID(),orig_ColumnName.toLowerCase()});
		
		setAD_PrintFormat_ID(pn.getAD_PrintFormat_ID());
		setAD_Column_ID(ColumnID);
		
		// FIXME: 
		
		if (passNo == 1) {
		
			setAD_PrintFormatChild_ID(Convert.getNewId(TMPrintFormatItem.class, opfi.getAD_PrintFormatChild_ID()));
		
		} else {
			
			setAD_PrintFormatChild_ID(0);
			
		}
		
		//
		
		
	}
}

