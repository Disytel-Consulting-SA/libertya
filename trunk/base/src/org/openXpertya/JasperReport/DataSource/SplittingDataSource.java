package org.openXpertya.JasperReport.DataSource;

public class SplittingDataSource extends QueryDataSource {

	/** ID de Fraccionamiento */
	private Integer splittingID;
	
	public SplittingDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public SplittingDataSource(Integer splittingID, String trxName) {
		this(trxName);
		setSplittingID(splittingID);
	}
	
	@Override
	protected String getQuery() {
		String sql = "select p.value as product_value, p.name as product_name, u.name as um, l.value as locator, productqty as qty, convertedqty as converted_qty " +
					 "from m_splittingline as sl " +
					 "inner join m_product as p on p.m_product_id = sl.m_product_to_id " +
					 "inner join c_uom as u on u.c_uom_id = sl.c_uom_id " +
					 "inner join m_locator as l on l.m_locator_id = sl.m_locator_id " +
					 "where m_splitting_id = ?";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[]{getSplittingID()};
	}

	protected Integer getSplittingID() {
		return splittingID;
	}

	protected void setSplittingID(Integer splittingID) {
		this.splittingID = splittingID;
	}

}
