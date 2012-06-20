package org.openXpertya.JasperReport.DataSource;

public class InventoryDataSource extends QueryDataSource {

	/** ID de inventario */
	private Integer inventoryID = null;
	
	public InventoryDataSource(String trxName, Integer inventoryID) {
		super(trxName);
		setInventoryID(inventoryID);
	}

	@Override
	protected String getQuery() {
		String sql = "SELECT p.value ||' - '|| p.name as PRODUCT_NAME, " +
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

	protected void setInventoryID(Integer inventoryID) {
		this.inventoryID = inventoryID;
	}

	protected Integer getInventoryID() {
		return inventoryID;
	}

}
