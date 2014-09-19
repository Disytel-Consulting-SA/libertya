package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OrderedProductsDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductCategory;
import org.openXpertya.model.MProductGamas;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.X_M_Product_Family;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class LaunchOrderedProducts extends JasperReportLaunch {

	/** UID de la referencia de la lista */
	private static final String GROUPBY_REFERENCE_UID = "CORE-AD_Reference-1010248";
	private static final String RECEPTION_REFERENCE_UID = "CORE-AD_Reference-1010247";
	
	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("TITLE", getTitle());
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// Fecha desde
		if(getDateFrom() != null){
			addReportParameter("DATE_FROM", getDateFrom());
		}
		// Fecha hasta
		if(getDateTo() != null){
			addReportParameter("DATE_TO", getDateTo());
		}
		// Entidad Comercial
		if(!Util.isEmpty(getBPartnerID(), true)){
			MBPartner bPartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("BPARTNER_VALUE", bPartner.getValue());
			addReportParameter("BPARTNER_NAME", bPartner.getName());
		}
		// Línea de Artículo
		if(!Util.isEmpty(getProductLinesID(), true)){
			MProductLines productLines = new MProductLines(getCtx(),
					getProductLinesID(), get_TrxName());
			addReportParameter("PRODUCT_LINES_VALUE", productLines.getValue());
			addReportParameter("PRODUCT_LINES_NAME", productLines.getName());
		}
		// Familia
		if(!Util.isEmpty(getProductGamasID(), true)){
			MProductGamas productGamas = new MProductGamas(getCtx(),
					getProductGamasID(), get_TrxName());
			addReportParameter("PRODUCT_GAMAS_VALUE", productGamas.getValue());
			addReportParameter("PRODUCT_GAMAS_NAME", productGamas.getName());
		}
		// Familia
		if(!Util.isEmpty(getProductCategoryID(), true)){
			MProductCategory productCategory = new MProductCategory(getCtx(),
					getProductCategoryID(), get_TrxName());
			addReportParameter("PRODUCT_CATEGORY_VALUE", productCategory.getValue());
			addReportParameter("PRODUCT_CATEGORY_NAME", productCategory.getName());
		}
		// Marca
		if(!Util.isEmpty(getProductFamilyID(), true)){
			X_M_Product_Family productFamily = new X_M_Product_Family(getCtx(),
					getProductFamilyID(), get_TrxName());
			addReportParameter("PRODUCT_FAMILY_VALUE", productFamily.getValue());
			addReportParameter("PRODUCT_FAMILY_NAME", productFamily.getName());
		}
		// Artículo
		if(!Util.isEmpty(getProductID(), true)){
			MProduct product = MProduct.get(getCtx(), getProductID());
			addReportParameter("PRODUCT_VALUE", product.getValue());
			addReportParameter("PRODUCT_NAME", product.getName());
		}
		// Agrupación
		addReportParameter("GROUP_BY", JasperReportsUtil.getListName(getCtx(),
				getReferenceListID(GROUPBY_REFERENCE_UID), getGroupBy()));
		// Estado de Recepción
		addReportParameter("RECEPTION_STATE", JasperReportsUtil.getListName(getCtx(),
				getReferenceListID(RECEPTION_REFERENCE_UID), getReceptionState()));
		// Transacción de ventas
		addReportParameter("ISSOTRX", isSOTrx(false)?"Y":"N");
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new OrderedProductsDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), getBPartnerID(),
				getProductLinesID(), getProductGamasID(),
				getProductCategoryID(), getProductFamilyID(), getProductID(),
				getGroupBy(), getReceptionState(), isSOTrx(false), get_TrxName());
	}
	
	private Integer getReferenceListID(String uid){
		return DB.getSQLValue(get_TrxName(),
				"SELECT ad_reference_id FROM ad_reference WHERE ad_componentobjectuid = '"
						+ uid + "'");
	}

	protected String getTitle(){
		return "Reporte de Artículos Pedidos";
	}
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}

	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("Date_To");
	}
	
	protected Integer getBPartnerID(){
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	protected Integer getProductLinesID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}
	
	protected Integer getProductGamasID(){
		return (Integer)getParameterValue("M_Product_Gamas_ID");
	}
	
	protected Integer getProductCategoryID(){
		return (Integer)getParameterValue("M_Product_Category_ID");
	}
	
	protected Integer getProductFamilyID(){
		return (Integer)getParameterValue("M_Product_Family_ID");
	}
	
	protected Integer getProductID(){
		return (Integer)getParameterValue("M_Product_ID");
	}
	
	protected String getGroupBy(){
		return (String)getParameterValue("GroupBy");
	}
	
	protected String getReceptionState(){
		return (String)getParameterValue("ReceptionState");
	}
	
	protected boolean isSOTrx(boolean defaultValue){
		return ((String) getParameterValue("isSOTrx") != null) ? ((String) getParameterValue("isSOTrx"))
				.equals("Y") : defaultValue;
	}
}
