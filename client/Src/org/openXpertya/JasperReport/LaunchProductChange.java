package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.jasperreports.engine.JREmptyDataSource;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductChange;
import org.openXpertya.model.MUser;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchProductChange extends SvrProcess {

	/** Jasper Report */
	private int AD_JasperReport_ID;

	/** Table */
	private int AD_Table_ID;

	/** Record */
	private int AD_Record_ID;


	@Override
	protected void prepare() {

		// Determinar JasperReport para wrapper, tabla y registro actual
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if (proceso.isJasperReport() != true)
			return;

		AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
		AD_Table_ID = getTable_ID();
		AD_Record_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport() throws Exception {

		MProductChange productChange = new MProductChange(getCtx(), AD_Record_ID, null);

		MJasperReport jasperwrapper = new MJasperReport(getCtx(),
				AD_JasperReport_ID, get_TrxName());
		
		MInventory inventory = null;
		if(!Util.isEmpty(productChange.getM_Inventory_ID(),true)){
			inventory = new MInventory(getCtx(),productChange.getM_Inventory_ID(),null);
		}
		MProduct product = new MProduct(getCtx(),productChange.getM_Product_ID(),null);
		MProduct productTo = new MProduct(getCtx(),productChange.getM_Product_To_ID(),null);
		MAttributeSetInstance instance = new MAttributeSetInstance(getCtx(),productChange.getM_AttributeSetInstance_ID(),null);		
		MAttributeSetInstance instanceTo = new MAttributeSetInstance(getCtx(),productChange.getM_AttributeSetInstanceTo_ID(),null);
		MLocator locator =new MLocator(getCtx(),productChange.getM_Locator_ID(),null);
		MLocator locatorTo =new MLocator(getCtx(),productChange.getM_Locator_To_ID(),null);
		MUser user= new MUser(getCtx(),productChange.getUpdatedBy(),null);
		
		MClient client = JasperReportsUtil.getClient(getCtx(), productChange
				.getAD_Client_ID());	
		
		// Establecemos parametros	
		jasperwrapper.addParameter("NROCOMPROBANTE", productChange.getDocumentNo());
		jasperwrapper.addParameter("TIPOCOMPROBANTE", "CAMBIO DE ARTÍCULOS");
		jasperwrapper.addParameter("FECHA", productChange.getDateTrx());
		jasperwrapper.addParameter(
				"ALMACEN",
				JasperReportsUtil.getWarehouseName(getCtx(),
						productChange.getM_Warehouse_ID(), get_TrxName()));
		/*if(productChange.getDescription().compareTo("")!=0){
			jasperwrapper.addParameter("OBSERVACION", productChange.getDescription());
		}*/
		jasperwrapper.addParameter("RESPONSABLE", user.getName());
		jasperwrapper.addParameter("COMPANIA", client.getName());
		jasperwrapper.addParameter("LOCALIZACION", JasperReportsUtil
				.getLocalizacion(getCtx(), productChange.getAD_Client_ID(), productChange
						.getAD_Org_ID(), get_TrxName()));
		// NOmbre de organización
		jasperwrapper.addParameter("ORG_NAME", JasperReportsUtil.getOrgName(
				getCtx(), productChange.getAD_Org_ID()));		
		jasperwrapper.addParameter("CODARTICULO", product.getValue());
		jasperwrapper.addParameter("ARTICULO", product.getName());	
		if(productChange.getInstance()==null){
			jasperwrapper.addParameter("UPC", product.getUPC());
		}else{
			jasperwrapper.addParameter("UPC", getUPCInstance(product.getM_Product_ID(),instance.getM_AttributeSetInstance_ID()));
		}
		jasperwrapper.addParameter("INSTANCIA", instance.getDescription());
		jasperwrapper.addParameter("CODARTICULODESTINO", productTo.getValue());
		jasperwrapper.addParameter("ARTICULODESTINO", productTo.getName());		
		if(productChange.getInstanceTo()==null){
			jasperwrapper.addParameter("UPCDESTINO", productTo.getUPC());
		}else{
			jasperwrapper.addParameter("UPCDESTINO", getUPCInstance(productTo.getM_Product_ID(),instanceTo.getM_AttributeSetInstance_ID()));
		}	
		jasperwrapper.addParameter("INSTANCIADESTINO", instanceTo.getDescription());
		jasperwrapper.addParameter("UBICACION", locator.getValue());		
		jasperwrapper.addParameter("UBICACIONDESTINO", locatorTo.getValue());
		jasperwrapper.addParameter("CANTIDAD", productChange.getProductQty());		
		jasperwrapper.addParameter("CANTIDADN", productChange.getProductQty());
		
		if(inventory != null){
			jasperwrapper.addParameter("INVENTORY_DOCUMENTNO", inventory.getDocumentNo());
		}
		
		// Variación de precio
		BigDecimal priceDiff = productChange.getProductPrice().subtract(
				productChange.getProductToPrice());
		int stdPrecision = MCurrency.getStdPrecision(getCtx(),
				productChange.getC_Currency_ID());
		BigDecimal hundred = new BigDecimal(100);
		jasperwrapper.addParameter("PRODUCT_PRICE", productChange.getProductPrice());
		jasperwrapper.addParameter("PRODUCT_TO_PRICE", productChange.getProductToPrice());
		jasperwrapper.addParameter("PRICE_DIFF", priceDiff);
		jasperwrapper.addParameter("PRICE_DIFF_ABS", priceDiff.abs());
		jasperwrapper.addParameter(
				"ALLOWED_DIFF",productChange.getProductPrice()
						.multiply(productChange.getMaxPriceVariationPerc())
						.divide(hundred, stdPrecision, BigDecimal.ROUND_HALF_UP));
		jasperwrapper.addParameter("MAX_PRICE_VARIATION_PERC",
				productChange.getMaxPriceVariationPerc());
		if (productChange.getProductPrice() != null
				&& productChange.getProductPrice().compareTo(BigDecimal.ZERO) != 0) {
			jasperwrapper.addParameter(
					"PRICE_VARIATION_PERC",
					priceDiff
							.abs()
							.divide(productChange.getProductPrice(), stdPrecision,
									BigDecimal.ROUND_HALF_UP).multiply(hundred));
		}
		jasperwrapper.addParameter("DOCSTATUS_NAME", JasperReportsUtil
				.getListName(getCtx(),
						MProductChange.DOCSTATUS_AD_Reference_ID,
						productChange.getDocStatus()));
		jasperwrapper.addParameter(
				"CREATED_BY",
				JasperReportsUtil.getUserName(getCtx(),
						productChange.getCreatedBy(), get_TrxName()));
		jasperwrapper.addParameter(
				"UPDATED_BY",
				JasperReportsUtil.getUserName(getCtx(),
						productChange.getUpdatedBy(), get_TrxName()));
		
		
		try {
			jasperwrapper.fillReport(new JREmptyDataSource());
			jasperwrapper.showReport(getProcessInfo());
		}

		catch (RuntimeException e) {
			throw new RuntimeException("No se ha podido rellenar el informe.",
					e);
		}

		return "doIt";
	}

	protected String getInitials(String name){
		String initials="";
		String letter="";
		String letter2="";
		for(int i=0;i<name.length();i++){
			letter=name.charAt(i)+"";			
			if(letter.compareTo(" ")==0&&i!=name.length()){
				letter2=name.charAt(i+1)+"";
				if(letter2.compareTo(" ")!=0){
					initials+=letter2;
				}
			}
			else{
				if(i==0){
					initials+=letter;
				}
			}
		}
		return initials;
	}
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param ctx
	 * @param C_BPartner_ID
	 * @param trxName
	 * 
	 * @return
	 */

	public static String getUPCInstance(int M_Product_ID, int M_AttributeSetInstance_ID) {
		String UPC="";
		String sql = "SELECT UPC FROM M_Product_UPC_Instance WHERE M_Product_ID=? AND M_AttributeSetInstance_ID=?";
		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_Product_ID);
			pstmt.setInt(2, M_AttributeSetInstance_ID);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UPC=rs.getString(1);
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		return UPC;
	} // getOfBPartner

}
