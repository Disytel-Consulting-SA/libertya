package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.MaterialTransferDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCharge;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MTransfer;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.Util;

public class LaunchMaterialTransfer extends JasperReportLaunch {

	/** Registro de transferencia actual */
	private MTransfer transfer = null;
	
	@Override
	protected void loadReportParameters() throws Exception {
		setTransfer(new MTransfer(getCtx(), getRecord_ID(), get_TrxName()));
		MCharge charge = new MCharge(getCtx(), getTransfer().getC_Charge_ID(),
				get_TrxName());
		MBPartner shipper = new MBPartner(getCtx(), getTransfer().getC_BPartner_ID(), get_TrxName());
		// Almacenes origen y destino
		MOrg org = new MOrg(getCtx(), getTransfer().getAD_Org_ID(), get_TrxName());
		MOrgInfo orgInfo = MOrgInfo.get(getCtx(), org.getAD_Org_ID());
		MWarehouse warehouseFrom = new MWarehouse(getCtx(), getTransfer()
				.getM_Warehouse_ID(), get_TrxName());
		MOrg orgFrom = new MOrg(getCtx(), warehouseFrom.getAD_Org_ID(), get_TrxName());
		MOrgInfo orgFromInfo = MOrgInfo.get(getCtx(), orgFrom.getAD_Org_ID());
		// Parámetros jasper
		addReportParameter("DOCUMENTNO", getTransfer().getDocumentNo());
		addReportParameter("CLIENT_NAME", JasperReportsUtil.getClientName(
				getCtx(), getTransfer().getAD_Client_ID()));
		addReportParameter("ORG_FROM_NAME",orgFrom.getName());
		addReportParameter("TRANSFER_TYPE", JasperReportsUtil.getListName(
				getCtx(), MTransfer.TRANSFERTYPE_AD_Reference_ID, getTransfer()
						.getTransferType()));
		addReportParameter("MOVEMENT_TYPE", JasperReportsUtil.getListName(
				getCtx(), MTransfer.MOVEMENTTYPE_AD_Reference_ID, getTransfer()
						.getMovementType()));
		addReportParameter("DATE_TRX", getTransfer().getDateTrx());
		addReportParameter("DUE_DATE", getTransfer().getDueDate());
		addReportParameter("CHARGE_NAME", charge.getName());
		addReportParameter("CHARGE_VALUE", charge.getValue());
		addReportParameter("DESCRIPTION", getTransfer().getDescription());
		addReportParameter("WAREHOUSE_FROM_NAME", warehouseFrom.getName());
		addReportParameter("WAREHOUSE_FROM_VALUE", warehouseFrom.getValue());
		addReportParameter("SHIPPER_NAME", shipper.getName());
		addReportParameter("SHIPPER_VALUE", shipper.getValue());
		addReportParameter("SHIPPER_CUIT", shipper.getTaxID());
		MBPartnerLocation shipperLocation = shipper.getLocations(true)[0];
		if(shipperLocation != null){
			addReportParameter(
					"SHIPPER_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							shipperLocation.getC_Location_ID(), false));
		}
		addReportParameter("DOC_STATUS", JasperReportsUtil.getListName(
				getCtx(), MTransfer.DOCSTATUS_AD_Reference_ID, getTransfer()
						.getDocStatus()));
		addReportParameter("ORG_NAME", org.getName());
		// Localizaciones
		if(!Util.isEmpty(orgInfo.getC_Location_ID(), true)){
			addReportParameter(
					"ORG_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							orgInfo.getC_Location_ID(), false));
		}
		if(!Util.isEmpty(orgFromInfo.getC_Location_ID(), true)){
			addReportParameter(
					"ORG_FROM_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							orgFromInfo.getC_Location_ID(), false));
		}
		addReportParameter(
				"WAREHOUSE_FROM_LOCATION",
				JasperReportsUtil.formatLocation(getCtx(),
						warehouseFrom.getC_Location_ID(), false));
		addReportParameter("CREATED_BY", JasperReportsUtil.getUserName(
				getCtx(), getTransfer().getCreatedBy(), get_TrxName()));
		addReportParameter("UPDATED_BY", JasperReportsUtil.getUserName(
				getCtx(), getTransfer().getUpdatedBy(), get_TrxName()));
		// Inventario generado
		if(!Util.isEmpty(getTransfer().getM_Inventory_ID(), true)){
			MInventory inventory = new MInventory(getCtx(), getTransfer()
					.getM_Inventory_ID(), get_TrxName());
			addReportParameter("INVENTORY_DOCUMENTNO", inventory.getDocumentNo());
		}
		// Guía de Transporte
		if(!Util.isEmpty(getTransfer().getTransport_Guide(), true)){
			addReportParameter("TRANSPORT_GUIDE", getTransfer().getTransport_Guide());
		}
		// Datos relacionados con Almacen destino
		// En el caso que no sea movimiento en 2 etapas
		if(!getTransfer().getTransferType().equals(MTransfer.TRANSFERTYPE_TwoPhaseMovement)
				&& !Util.isEmpty(getTransfer().getM_WarehouseTo_ID(), true)){
			MWarehouse warehouseTo = new MWarehouse(getCtx(), getTransfer()
					.getM_WarehouseTo_ID(), get_TrxName());
			MOrg orgTo = new MOrg(getCtx(), warehouseTo.getAD_Org_ID(), get_TrxName());
			MOrgInfo orgToInfo = MOrgInfo.get(getCtx(), orgTo.getAD_Org_ID());
			addReportParameter("WAREHOUSE_TO_NAME", warehouseTo.getName());
			addReportParameter("WAREHOUSE_TO_VALUE", warehouseTo.getValue());
			addReportParameter("ORG_TO_NAME",orgTo.getName());
			if(!Util.isEmpty(orgToInfo.getC_Location_ID(), true)){
				addReportParameter(
						"ORG_TO_LOCATION",
						JasperReportsUtil.formatLocation(getCtx(),
								orgToInfo.getC_Location_ID(), false));
			}
			addReportParameter(
					"WAREHOUSE_TO_LOCATION",
					JasperReportsUtil.formatLocation(getCtx(),
							warehouseTo.getC_Location_ID(), false));
		}
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new MaterialTransferDataSource(get_TrxName(), getTransfer().getID());
	}

	protected void setTransfer(MTransfer transfer) {
		this.transfer = transfer;
	}

	protected MTransfer getTransfer() {
		return transfer;
	}

}
