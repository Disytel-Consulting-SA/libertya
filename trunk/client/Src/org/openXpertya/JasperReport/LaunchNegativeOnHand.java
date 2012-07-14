package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.NegativeOnHandDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProductCategory;
import org.openXpertya.model.MProductGamas;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.MWarehouse;

public class LaunchNegativeOnHand extends JasperReportLaunch {
	
	/** Subfamilia */
	private MProductCategory productCategory;
	
	/** Familia */
	private MProductGamas productGamas;
	
	/** Línea de Artículo */
	private MProductLines productLines;
	
	/** Almacén */
	private MWarehouse warehouse;
	
	/** Organización */
	private MOrg org;
	
	protected void initialize(){
		setOrg(MOrg.get(getCtx(), (Integer) getParameterValue("AD_Org_ID")));
		if(getParameterValue("M_Warehouse_ID") != null){
			setWarehouse(MWarehouse.get(getCtx(),
					(Integer) getParameterValue("M_Warehouse_ID")));	
		}		
		if(getParameterValue("M_Product_Category_ID") != null){
			setProductCategory(MProductCategory.get(getCtx(),
					(Integer) getParameterValue("M_Product_Category_ID"),
					get_TrxName()));
		}
		if(getParameterValue("M_Product_Gamas_ID") != null){
			setProductGamas(MProductGamas.get(getCtx(),
					(Integer) getParameterValue("M_Product_Gamas_ID"),
					get_TrxName()));
		}
		if(getParameterValue("M_Product_Lines_ID") != null){
			setProductLines(new MProductLines(getCtx(),
					(Integer) getParameterValue("M_Product_Lines_ID"),
					get_TrxName()));
		}
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Inicializar la información requerida
		initialize();
		// Agregar los parámetros al reporte
		addReportParameter("ORG_VALUE", getOrg().getValue());
		addReportParameter("ORG_NAME", getOrg().getName());
		addReportParameter("WAREHOUSE_VALUE",
				getWarehouse() != null ? getWarehouse().getValue() : null);
		addReportParameter("WAREHOUSE_NAME",
				getWarehouse() != null ? getWarehouse().getName() : null);
		addReportParameter("PRODUCT_LINES_VALUE",
				getProductLines() != null ? getProductLines().getValue() : null);
		addReportParameter("PRODUCT_LINES_NAME",
				getProductLines() != null ? getProductLines().getName() : null);
		addReportParameter("PRODUCT_GAMAS_VALUE",
				getProductGamas() != null ? getProductGamas().getValue() : null);
		addReportParameter("PRODUCT_GAMAS_NAME",
				getProductGamas() != null ? getProductGamas().getName() : null);
		addReportParameter("PRODUCT_CATEGORY_VALUE",
				getProductCategory() != null ? getProductCategory().getValue()
						: null);
		addReportParameter("PRODUCT_CATEGORY_NAME",
				getProductCategory() != null ? getProductCategory().getName()
						: null);
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new NegativeOnHandDataSource(getCtx(), getOrg(), getWarehouse(),
				getProductLines(), getProductGamas(), getProductCategory(),
				get_TrxName());
	}

	protected void setProductCategory(MProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	protected MProductCategory getProductCategory() {
		return productCategory;
	}

	protected void setProductGamas(MProductGamas productGamas) {
		this.productGamas = productGamas;
	}

	protected MProductGamas getProductGamas() {
		return productGamas;
	}

	protected void setProductLines(MProductLines productLines) {
		this.productLines = productLines;
	}

	protected MProductLines getProductLines() {
		return productLines;
	}

	protected void setWarehouse(MWarehouse warehouse) {
		this.warehouse = warehouse;
	}

	protected MWarehouse getWarehouse() {
		return warehouse;
	}

	protected void setOrg(MOrg org) {
		this.org = org;
	}

	protected MOrg getOrg() {
		return org;
	}

}
