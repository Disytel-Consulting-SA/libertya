package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MRefList;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_M_Transfer;
import org.openXpertya.util.Msg;

public class DetailedProductMovementsDataDource extends QueryDataSource {

	/** ID de Artículo */
	private Integer productID;
	
	/** ID de Almacén */
	private Integer warehouseID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Contexto */
	private Properties ctx;
	
	public DetailedProductMovementsDataDource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DetailedProductMovementsDataDource(String trxName, Properties ctx, Integer productID, Integer warehouseID, Timestamp dateFrom, Timestamp dateTo) {
		this(trxName);
		setCtx(ctx);
		setProductID(productID);
		setWarehouseID(warehouseID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
	}

	protected String getSQLOrderBy(){
		return " ORDER BY movementdate, updated ";
	}

	protected String getSelectSQL(){
		return " movement_table, receiptvalue, receiptvalue as io_kind_no_parsed, movementdate, doctypename, documentno, qty, "
				+ " (select documentno "
				+ "	from c_invoice i "
				+ "	where i.c_order_id = m.c_order_id "
				+ "	order by i.created "
				+ "	limit 1) as invoice_documentno ";
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select "+getSelectSQL());
		sql.append(" from v_product_movements_detailed_filtered("+getProductID()+") as m ");
		sql.append(" where m_warehouse_id = ? AND docstatus IN ('CO','CL') ");
		if(getDateFrom() != null){
			sql.append(" AND date_trunc('day',movementdate) >= date_trunc('day',?::date) ");
		}
		if(getDateTo() != null){
			sql.append(" AND date_trunc('day',movementdate) <= date_trunc('day',?::date) ");
		}
		sql.append(getSQLOrderBy());
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getWarehouseID());
		if(getDateFrom() != null){
			params.add(getDateFrom());
		}
		if(getDateTo() != null){
			params.add(getDateTo());
		}
		return params.toArray();
	}
	
	public Object getFieldValue(JRField field) throws JRException {
		Object fieldValue = super.getFieldValue(field);
		if(field.getName().equalsIgnoreCase("io_kind")){
			String strValue = (String)getCurrentRecord().get("RECEIPTVALUE");
			fieldValue = MRefList.getListName("English",
					X_C_Payment.ISRECEIPT_AD_Reference_ID, strValue);
		}
		else if(field.getName().equalsIgnoreCase("doctypename")){
			String strValue = (String)fieldValue;
			String tableName = (String)getCurrentRecord().get("MOVEMENT_TABLE");
			// Si es transferencia de mercadería, tengo que ir a buscar el tipo
			// de transferencia
			if(X_M_Transfer.Table_Name.equalsIgnoreCase(tableName)){
				fieldValue = JasperReportsUtil.getListName(getCtx(),
						X_M_Transfer.TRANSFERTYPE_AD_Reference_ID, strValue);
			}
			// Si es un fraccionamiento, el elemento de fraccionamiento
			else if(strValue.endsWith("_ID")){
				fieldValue = Msg.getElement(getCtx(), strValue);
			}
		}
		return fieldValue;
		
	}

	public Integer getProductID() {
		return productID;
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public Integer getWarehouseID() {
		return warehouseID;
	}

	public void setWarehouseID(Integer warehouseID) {
		this.warehouseID = warehouseID;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
