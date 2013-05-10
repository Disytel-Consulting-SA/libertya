package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ValuedMovementsDataSource;
import org.openXpertya.model.MCharge;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchValuedMovements extends LaunchProductMovements {

	/** Fecha de hoy para cuando no viene el parámetro hasta */
	protected Timestamp today = Env.getDate();
	
	@Override
	protected void loadReportParameters() throws Exception {
		super.loadReportParameters();
		// Depósito
		Integer warehouseID = getWarehouseID();
		addReportParameter(
				"WAREHOUSE_NAME",
				Util.isEmpty(warehouseID, true) ? null : JasperReportsUtil
						.getWarehouseName(getCtx(), warehouseID, get_TrxName()));
		// Versión de Tarifa
		MPriceListVersion priceListVersion = new MPriceListVersion(getCtx(),
				getPriceListVersionID(), get_TrxName());
		addReportParameter("PRICELIST_VERSION_NAME", priceListVersion.getName());
		if(!Util.isEmpty(getChargeID(), true)){
			MCharge charge = new MCharge(getCtx(), getChargeID(), get_TrxName());
			addReportParameter("CHARGE_VALUE", charge.getValue());
			addReportParameter("CHARGE_NAME", charge.getName());
		}
	}
	
	protected Integer getOrgID(){
		Integer orgID = (Integer)getParameterValue("AD_Org_ID");
		if(Util.isEmpty(orgID, true)){
			orgID = Env.getAD_Org_ID(getCtx());
		}
		return orgID;
	}
	
	protected Timestamp getDateTo(){
		Timestamp dateTo = (Timestamp) getParameterValue("Date_TO");
		if(dateTo == null){
			dateTo = today;
		}
		return dateTo;
	}
	
	protected Integer getWarehouseID(){
		return (Integer)getParameterValue("M_Warehouse_ID");
	}
	
	protected Integer getPriceListVersionID(){
		return (Integer)getParameterValue("M_PriceList_Version_ID");
	}
	
	protected Integer getChargeID(){
		return (Integer)getParameterValue("C_Charge_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ValuedMovementsDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), getWarehouseID(),
				getPriceListVersionID(), getChargeID(), get_TrxName());
	}
	
}
