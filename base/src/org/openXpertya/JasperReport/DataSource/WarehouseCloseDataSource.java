package org.openXpertya.JasperReport.DataSource;

import java.sql.Date;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MProductChange;
import org.openXpertya.model.MSplitting;
import org.openXpertya.model.MTransfer;
import org.openXpertya.model.MWarehouseClose;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class WarehouseCloseDataSource extends QueryDataSource {

	/** Cierre de Almacén */
	private MWarehouseClose warehouseClose = null;
	
	/** Contexto */
	private Properties ctx = null;
	
	
	public WarehouseCloseDataSource(Properties ctx, MWarehouseClose warehouseClose, String trxName) {
		super(trxName);
		setCtx(ctx);
		setWarehouseClose(warehouseClose);
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT * FROM v_movements WHERE m_warehouse_id = ? AND movement_date = ?::date");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { getWarehouseClose().getM_Warehouse_ID(),
				new Date(getWarehouseClose().getDateTrx().getTime()) };
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = super.getFieldValue(field);
		if(field.getName().equalsIgnoreCase("DOCTYPENAME")){
			value = getDocTypeName((String)value);
		}
		else if(field.getName().equalsIgnoreCase("DOCSTATUS_NAME")){
			value = JasperReportsUtil.getListName(getCtx(),
					MInOut.DOCSTATUS_AD_Reference_ID,
					(String) getCurrentRecord().get("DOCSTATUS"));
		}
		else if(field.getName().equalsIgnoreCase("CHARGE_NAME")){
			Integer chargeID = (Integer) getCurrentRecord().get("C_CHARGE_ID");
			if(!Util.isEmpty(chargeID, true)){
				value = JasperReportsUtil.getChargeName(getCtx(), chargeID,
						getTrxName());
			}
		}
		else if(field.getName().equalsIgnoreCase("BPARTNER_NAME")){
			Integer bpartnerID = (Integer) getCurrentRecord().get("C_BPARTNER_ID");
			if(!Util.isEmpty(bpartnerID, true)){
				value = JasperReportsUtil.getBPartnerName(getCtx(), bpartnerID, getTrxName());
			}
		}
		else if(field.getName().equalsIgnoreCase("WAREHOUSE_NAME")){
			Integer warehouseID = (Integer) getCurrentRecord().get("M_WAREHOUSE_ID");
			if(!Util.isEmpty(warehouseID, true)){
				value = JasperReportsUtil.getWarehouseName(getCtx(), warehouseID, getTrxName());
			}
		}
		else if(field.getName().equalsIgnoreCase("WAREHOUSETO_NAME")){
			Integer warehouseID = (Integer) getCurrentRecord().get("M_WAREHOUSETO_ID");
			if(!Util.isEmpty(warehouseID, true)){
				value = JasperReportsUtil.getWarehouseName(getCtx(), warehouseID, getTrxName());
			}
		}
		else if(field.getName().equalsIgnoreCase("ORG_NAME")){
			value = JasperReportsUtil.getOrgName(getCtx(),
					(Integer) getCurrentRecord().get("AD_ORG_ID"));
		}
		return value;
	}

	/**
	 * Obtengo la descripción del tipo de documento dependiendo la tabla
	 * 
	 * @param value
	 *            valor en el registro
	 * @return descripción del tipo de documento dependiendo la tabla o null
	 *         caso que no sea necesario
	 */
	protected String getDocTypeName(String value){
		String name = value;
		String tableName = getTableName();
		if(tableName.equalsIgnoreCase(MProductChange.Table_Name)
				|| tableName.equalsIgnoreCase(MSplitting.Table_Name)){
			name = Msg.parseTranslation(getCtx(), value);
		}
		else if(tableName.equalsIgnoreCase(MTransfer.Table_Name)){
			name = JasperReportsUtil.getListName(getCtx(),
					MTransfer.TRANSFERTYPE_AD_Reference_ID, value);
		}
		return name;
	}
	
	/**
	 * @return nombre de la tabla actual
	 */
	protected String getTableName(){
		return (String)getCurrentRecord().get("TABLENAME");
	}
	

	protected void setWarehouseClose(MWarehouseClose warehouseClose) {
		this.warehouseClose = warehouseClose;
	}

	protected MWarehouseClose getWarehouseClose() {
		return warehouseClose;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected Properties getCtx() {
		return ctx;
	}
	
}
