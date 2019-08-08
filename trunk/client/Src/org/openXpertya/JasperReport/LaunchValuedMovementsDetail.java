package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ValuedMovementsDetailDataSource;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.DB;

public class LaunchValuedMovementsDetail extends LaunchValuedMovements {

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ValuedMovementsDetailDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), getWarehouseID(),
				getPriceListVersionID(), getChargeID(), getProductLineID(),  
				getPriceType(), get_TrxName());
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
		// Tipo de precio
		addReportParameter("PRICE_TYPE", getPriceType());
		addReportParameter("PRICE_TYPE_NAME",
				MRefList.getListName(getCtx(), getPriceTypeReferenceID(), getPriceType()));
	}
	
	protected Integer getProductLineID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}

	protected String getPriceType(){
		return (String)getParameterValue("PriceType");
	}
	
	protected Integer getPriceTypeReferenceID(){
		return DB.getSQLValue(get_TrxName(), "select ad_reference_id from ad_reference where ad_componentobjectuid = 'CORE-AD_Reference-1010435'");
	}
}
