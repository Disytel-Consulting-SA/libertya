package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.InventoryDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MTransfer;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.Util;

public class LaunchInventory extends JasperReportLaunch {

	/** Inventario Actual */
	private MInventory inventory;
	
	@Override
	protected void loadReportParameters() throws Exception {
		setInventory(new MInventory(getCtx(), getRecord_ID(), get_TrxName()));
		MOrg org = new MOrg(getCtx(), getInventory().getAD_Org_ID(), get_TrxName());
		MOrgInfo orgInfo = MOrgInfo.get(getCtx(), org.getAD_Org_ID());
		MWarehouse warehouse = new MWarehouse(getCtx(), getInventory()
				.getM_Warehouse_ID(), get_TrxName());
		// Parámetros jasper
		addReportParameter("DOCUMENTNO", getInventory().getDocumentNo());
		addReportParameter("CLIENT_NAME", JasperReportsUtil.getClientName(
				getCtx(), getInventory().getAD_Client_ID()));
		addReportParameter("ORG_NAME",org.getName());
		addReportParameter("DOCTYPE_NAME", JasperReportsUtil.getDocTypeName(
				getCtx(), getInventory().getC_DocType_ID(), "INVENTARIO",
				get_TrxName()));
		addReportParameter("DATE", getInventory().getMovementDate());
		addReportParameter("CHARGE_NAME", JasperReportsUtil.getChargeName(
				getCtx(), getInventory().getC_Charge_ID(), get_TrxName()));
		addReportParameter("DESCRIPTION", getInventory().getDescription());
		addReportParameter("WAREHOUSE_NAME", warehouse.getName());
		addReportParameter("DOC_STATUS", JasperReportsUtil.getListName(
				getCtx(), MTransfer.DOCSTATUS_AD_Reference_ID, getInventory()
						.getDocStatus()));
		// Localizaciones
		if(!Util.isEmpty(orgInfo.getC_Location_ID(), true)){
			addReportParameter(
					"ORG_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							orgInfo.getC_Location_ID(), false));
		}
		addReportParameter(
				"WAREHOUSE_LOCATION",
				JasperReportsUtil.formatLocation(getCtx(),
						warehouse.getC_Location_ID(), false));
		addReportParameter("CREATED_BY", JasperReportsUtil.getUserName(
				getCtx(), getInventory().getCreatedBy(), get_TrxName()));
		addReportParameter("UPDATED_BY", JasperReportsUtil.getUserName(
				getCtx(), getInventory().getUpdatedBy(), get_TrxName()));
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new InventoryDataSource(getCtx(), get_TrxName(), getInventory().getID());
	}

	protected void setInventory(MInventory inventory) {
		this.inventory = inventory;
	}

	protected MInventory getInventory() {
		return inventory;
	}

}
