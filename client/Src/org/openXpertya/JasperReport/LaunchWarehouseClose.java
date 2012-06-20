package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.WarehouseCloseDataSource;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.model.MWarehouseClose;
import org.openXpertya.util.Util;

public class LaunchWarehouseClose extends JasperReportLaunch {

	/** Cierre de Depósito */
	private MWarehouseClose warehouseClose = null;
	
	@Override
	protected void loadReportParameters() throws Exception {
		setWarehouseClose(new MWarehouseClose(getCtx(), getRecord_ID(), get_TrxName()));
		MOrg org = MOrg.get(getCtx(), getWarehouseClose().getAD_Org_ID());
		MOrgInfo orgInfo = MOrgInfo.get(getCtx(), org.getID());
		MWarehouse warehouse = new MWarehouse(getCtx(), getWarehouseClose()
				.getM_Warehouse_ID(), get_TrxName());
		addReportParameter("ORG_NAME", org.getValue() + " - " + org.getName());
		if(!Util.isEmpty(orgInfo.getC_Location_ID(), true)){
			addReportParameter(
					"ORG_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							orgInfo.getC_Location_ID(), false));
		}
		addReportParameter("WAREHOUSE_NAME", warehouse.getName());
		addReportParameter(
				"WAREHOUSE_LOCATION",
				JasperReportsUtil.formatLocation(getCtx(),
						warehouse.getC_Location_ID(), false));
		addReportParameter("DATETRX", getWarehouseClose().getDateTrx());
		addReportParameter("DESCRIPTION", getWarehouseClose().getDescription());
		addReportParameter("DOC_STATUS", getWarehouseClose().getDocStatus());
		addReportParameter("DOC_STATUS_NAME", JasperReportsUtil.getListName(
				getCtx(), MWarehouseClose.DOCSTATUS_AD_Reference_ID,
				getWarehouseClose().getDocStatus()));
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new WarehouseCloseDataSource(getCtx(), getWarehouseClose(), get_TrxName());
	}

	protected void setWarehouseClose(MWarehouseClose warehouseClose) {
		this.warehouseClose = warehouseClose;
	}

	protected MWarehouseClose getWarehouseClose() {
		return warehouseClose;
	}

}
