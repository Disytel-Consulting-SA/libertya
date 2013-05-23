package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.MPriceList;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class InventoryDataSource extends QueryDataSource {

	/** Nombre de la Preference con el ID de la tarifa de costo a utilizar */
	private static final String SIMPLE_INOUT_PRINT_COST_PRICELISTID = "SimpleInOutPrint_CostPriceListID";
	
	/** Nombre de la Preference con el ID de la tarifa de ventas a utilizar */
	private static final String SIMPLE_INOUT_PRINT_SALES_PRICELISTID = "SimpleInOutPrint_SalesPriceListID";
	
	/** Contexto actual */
	private Properties ctx;
	
	/** ID de inventario */
	private Integer inventoryID = null;
	
	/** Tarifa de Costo */
	private Integer costPriceListID;
	
	/** Tarifa de Venta */
	private Integer salesPriceListID;
	
	public InventoryDataSource(Properties ctx, String trxName, Integer inventoryID) {
		super(trxName);
		setCtx(ctx);
		setInventoryID(inventoryID);
		initPriceLists();
	}

	private void initPriceLists(){
		// 1) Inicializar las tarifas de venta y costo en base a las
		// preferencias
		// 2) Buscar las tarifas marcadas como isDefault para la organización actual
		// 3) Buscar las tarifas marcadas como isDefault para la compañía actual
		setCostPriceListID(getPriceListID(false));
		setSalesPriceListID(getPriceListID(true));
	}
	
	/**
	 * Obtiene el id de la tarifa de ventas o costo en base al parámetro.
	 * La tarifa se obtiene de las siguientes maneras:
	 * <ol>
	 * <li>En base a las preferencias con nombre {@link #SIMPLE_INOUT_PRINT_COST_PRICELISTID} y {@link #SIMPLE_INOUT_PRINT_SALES_PRICELISTID}.</li>
	 * <li>Marcada como isDefault para la organización actual.</li>
	 * <li>Marcada como isDefault para la compañía actual.</li>
	 * </ol>
	 * @param isSOPriceList
	 *            true si es de ventas, false de costo
	 * @return id de la tarifa
	 */
	private Integer getPriceListID(boolean isSOPriceList){
		Integer clientID = Env.getAD_Client_ID(getCtx());
		Integer orgID = Env.getAD_Org_ID(getCtx());
		Integer userID = Env.getAD_User_ID(getCtx());
		Integer priceListID = null;
		// 1) 		
		String priceListID_str = MPreference.searchCustomPreferenceValue(
				isSOPriceList ? SIMPLE_INOUT_PRINT_SALES_PRICELISTID
						: SIMPLE_INOUT_PRINT_COST_PRICELISTID, clientID, orgID,
				userID, true);
		if(Util.isEmpty(priceListID_str, true)){
			// 2) 
			MPriceList priceList = MPriceList.getDefault(getCtx(), isSOPriceList ,true);
			if(priceList == null){
				// 3)
				priceList = MPriceList.getDefault(getCtx(), isSOPriceList);
			}
			if(priceList != null){
				priceListID = priceList.getID();
			}
		}
		else{
			priceListID = Integer.valueOf(priceListID_str);
		}
		return priceListID;
	}
	
	@Override
	protected String getQuery() {
		String sql = "SELECT p.m_product_id, " +
					 "		p.value ||' - '|| p.name as PRODUCT_NAME, " +
					 "		il.qtycount as QTY_COUNT, " +
					 "		il.qtybook as QTY_BOOK, " +
					 "		uc.name AS CREATED_BY, " +
					 "		uu.name AS UPDATED_BY, " +
					 "		lf.value AS LOCATOR_NAME "+
					 "FROM m_inventoryline as il " +
					 "INNER JOIN m_product as p ON (p.m_product_id = il.m_product_id) " +
					 "INNER JOIN m_locator as lf ON (lf.m_locator_id = il.m_locator_id) " +
					 "INNER JOIN ad_user as uc ON (uc.ad_user_id = il.createdby) " +
					 "INNER JOIN ad_user as uu ON (uu.ad_user_id = il.updatedby) " +
					 "WHERE il.m_inventory_id = ?";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[]{getInventoryID()};
	}

	protected String getProductPriceSQL(){
		return "SELECT pp.pricestd " +
				"FROM m_productprice pp " +
				"INNER JOIN m_pricelist_version plv ON plv.m_pricelist_version_id = pp.m_pricelist_version_id " +
				"INNER JOIN m_pricelist pl on pl.m_pricelist_id = plv.m_pricelist_id " +
				"WHERE pp.m_product_id = ? AND pl.m_pricelist_id = ? AND pp.isactive = 'Y' " +
				"ORDER BY plv.validfrom DESC " +
				"LIMIT 1";
	}
	
	protected BigDecimal getProductPrice(Integer productID, Integer priceListID){
		BigDecimal pricestd = null;
		if (!Util.isEmpty(priceListID, true) && !Util.isEmpty(productID, true)) {
			pricestd = (BigDecimal) DB.getSQLObject(getTrxName(),
					getProductPriceSQL(),
					new Object[] { productID, priceListID }, true);
		}
		return pricestd;
	}
	
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().equalsIgnoreCase("COST_PRICE")){
			value = getProductPrice(
					(Integer) getCurrentRecord().get("M_PRODUCT_ID"),
					getCostPriceListID());
		}
		else if(field.getName().equalsIgnoreCase("SALES_PRICE")){
			value = getProductPrice(
					(Integer) getCurrentRecord().get("M_PRODUCT_ID"),
					getSalesPriceListID());
		}
		return value;
	}
	
	protected void setInventoryID(Integer inventoryID) {
		this.inventoryID = inventoryID;
	}

	protected Integer getInventoryID() {
		return inventoryID;
	}

	public Integer getCostPriceListID() {
		return costPriceListID;
	}

	public void setCostPriceListID(Integer costPriceListID) {
		this.costPriceListID = costPriceListID;
	}

	public Integer getSalesPriceListID() {
		return salesPriceListID;
	}

	public void setSalesPriceListID(Integer salesPriceListID) {
		this.salesPriceListID = salesPriceListID;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
