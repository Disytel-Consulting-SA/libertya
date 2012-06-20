package org.openXpertya.JasperReport.DataSource;

public class MaterialTransferDataSource extends QueryDataSource {

	/** ID de transferencia */
	private Integer transferID = null;
	
	public MaterialTransferDataSource(String trxName, Integer transferID) {
		super(trxName);
		setTransferID(transferID);
	}

	@Override
	protected String getQuery() {
		String sql = "SELECT tl.line::integer as LINE, " +
					 "		p.value ||' - '|| p.name as PRODUCT_NAME, " +
					 "		tl.qty as QTY,	" +
					 "		tl.confirmedqty as CONFIRMED_QTY," +
					 "		uc.name AS CREATED_BY," +
					 "		uu.name AS UPDATED_BY," +
					 "		lf.value AS LOCATOR_FROM_NAME," +
					 "		lt.value AS LOCATOR_TO_NAME " +
					 "FROM m_transferline as tl " +
					 "INNER JOIN m_product as p ON (p.m_product_id = tl.m_product_id) " +
					 "INNER JOIN m_locator as lf ON (lf.m_locator_id = tl.m_locator_id) " +
					 "LEFT JOIN m_locator as lt ON (lt.m_locator_id = tl.m_locator_to_id) " +
					 "INNER JOIN ad_user as uc ON (uc.ad_user_id = tl.createdby) " +
					 "INNER JOIN ad_user as uu ON (uu.ad_user_id = tl.updatedby) " +
					 "WHERE tl.m_transfer_id = ?";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[]{getTransferID()};
	}

	// Getters y Setters
	
	protected void setTransferID(Integer transferID) {
		this.transferID = transferID;
	}

	protected Integer getTransferID() {
		return transferID;
	}

}
