package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ValuedMovementsDetailDataSource;
import org.openXpertya.model.MProductLines;

public class LaunchValuedMovementsDetail extends LaunchValuedMovements {

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ValuedMovementsDetailDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), getWarehouseID(),
				getPriceListVersionID(), getChargeID(), getProductLineID(), 
				get_TrxName());
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		super.loadReportParameters();
		// Línea de Artículo
		if(getProductLineID() != null){
			MProductLines productLine = new MProductLines(getCtx(),
					getProductLineID(), get_TrxName());
			addReportParameter("PRODUCT_LINE_VALUE", productLine.getValue());
			addReportParameter("PRODUCT_LINE_NAME", productLine.getName());
		}
	}
	
	protected Integer getProductLineID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}

}
