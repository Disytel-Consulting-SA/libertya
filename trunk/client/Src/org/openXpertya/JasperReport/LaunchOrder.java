package org.openXpertya.JasperReport;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OrderDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MTax;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfo.JasperReportDTO;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchOrder extends SvrProcess {

	/** Jasper Report			*/
	private int AD_JasperReport_ID;
	
	/** Table					*/
	private int AD_Table_ID;
	
	/** Record					*/
	private int AD_Record_ID;	
	
	/** Total de líneas con impuestos */
	private BigDecimal linesTotalAmt = BigDecimal.ZERO;
	
	/** Total de líneas sin impuestos */
	private BigDecimal linesTotalNetAmt = BigDecimal.ZERO;
	
	/** Total de bonus de líneas con impuestos */
	private BigDecimal linesTotalBonusAmt = BigDecimal.ZERO;

	/** Total de bonus de líneas sin impuestos */
	private BigDecimal linesTotalBonusNetAmt = BigDecimal.ZERO;
	
	/** Total de descuento de líneas con impuestos */
	private BigDecimal linesTotalLineDiscountsAmt = BigDecimal.ZERO;
	
	/** Total de descuento de líneas sin impuestos */
	private BigDecimal linesTotalLineDiscountsNetAmt = BigDecimal.ZERO;
	
	/** Total de descuento de documento de líneas con impuestos */
	private BigDecimal linesTotalDocumentDiscountsAmt = BigDecimal.ZERO;
	
	/** Total de descuento de documento de líneas sin impuestos */
	private BigDecimal linesTotalDocumentDiscountsNetAmt = BigDecimal.ZERO;
	
	/** Jasper Report Wrapper*/
	private MJasperReport jasperwrapper;
	
	@Override
	protected void prepare() {
		// Se toma el parametro C_Order_ID. Este parametro es necesario cuando se invoca el proceso 
		// imprimir desde codigo.
		// En el caso de ser necesario también se podria pasar como parámetro AD_Table_ID
		Integer c_Order_ID = null;
		ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_Order_ID" )) {
            	c_Order_ID = para[ i ].getParameterAsInt();
        	} 
        }

		// Determinar JasperReport para wrapper, tabla y registro actual
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport() != true)
			return;

		AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
		AD_Table_ID = getTable_ID();
		
		if(getRecord_ID()==0){
			AD_Record_ID = c_Order_ID;
		}
		else{
			AD_Record_ID = getRecord_ID();	
		}
		jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
	}
	
	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport() throws Exception {
		
		MOrder order = new MOrder(getCtx(), AD_Record_ID, get_TrxName());
		
		if(getProcessInfo().getJasperReportDTO() == null){
			JasperReportDTO jasperDTO = getProcessInfo().new JasperReportDTO();
	    	jasperDTO.setDocTypeID(order.getC_DocTypeTarget_ID());
	    	jasperDTO.setDocumentNo(order.getDocumentNo());
	    	getProcessInfo().setJasperReportDTO(jasperDTO);	
		}
		
		MBPartner bpartner = new MBPartner(getCtx(), order.getC_BPartner_ID(), null);
		
		OrderDataSource ds = new OrderDataSource(getCtx(), order);
		
		 try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
			// Inicializar los montos necesarios para pasar como parámetro
			initializeAmts(order);
			
			MClient client = JasperReportsUtil.getClient(getCtx(), order.getAD_Client_ID());
			MClientInfo clientInfo = JasperReportsUtil.getClientInfo(getCtx(),
				order.getAD_Client_ID(), get_TrxName());
			// Establecemos parametros
			String paymentTermName = JasperReportsUtil.getPaymentTermName(getCtx(),
				order.getC_PaymentTerm_ID(), get_TrxName());
			String salesUserName = JasperReportsUtil.getUserName(getCtx(),
				order.getSalesRep_ID(), get_TrxName());
			MBPartnerLocation bpLocation = new MBPartnerLocation(getCtx(),
				order.getC_BPartner_Location_ID(), null);
			MLocation location = new MLocation(getCtx(), bpLocation.getC_Location_ID(), null);
			MRegion region = null;
			if (location.getC_Region_ID() > 0)
				region = new MRegion(getCtx(), location.getC_Region_ID(), null);
			BigDecimal totalNetLineDiscounts = linesTotalBonusNetAmt.add(
					linesTotalDocumentDiscountsNetAmt).add(
					linesTotalLineDiscountsNetAmt);
			BigDecimal totalLineDiscounts = linesTotalBonusAmt.add(
						linesTotalDocumentDiscountsAmt).add(
						linesTotalLineDiscountsAmt);
			
			jasperwrapper.addParameter("NROCOMPROBANTE", order.getDocumentNo());
			jasperwrapper.addParameter("TIPOCOMPROBANTE", JasperReportsUtil
				.getDocTypeName(getCtx(), order.getC_DocTypeTarget_ID(),
						"NOTA DE PEDIDO", get_TrxName()));
			jasperwrapper.addParameter("FECHA", order.getDateOrdered() );
			jasperwrapper.addParameter("RAZONSOCIAL", bpartner.getName()); 
			jasperwrapper.addParameter("RAZONSOCIAL2", JasperReportsUtil.coalesce(bpartner.getName2(), "") );
			jasperwrapper.addParameter("CODIGO", bpartner.getValue());
			jasperwrapper.addParameter("DIRECCION", JasperReportsUtil
					.formatLocation(getCtx(), location.getID(), false));
			jasperwrapper.addParameter("TELEFONO", bpLocation.getPhone() );
			if(!Util.isEmpty(bpartner.getC_Categoria_Iva_ID(), true)){
				jasperwrapper.addParameter(
					"TIPO_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(),
							bpartner.getC_Categoria_Iva_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("DNI_CUIT", bpartner.getTaxID());
			jasperwrapper.addParameter("INGBRUTO", bpartner.getIIBB());
			jasperwrapper.addParameter("LOCALIDAD", location.getCity() );
			jasperwrapper.addParameter(
				"PAIS",
				JasperReportsUtil.coalesce(location.getCountry().getName(), ""));
			jasperwrapper.addParameter("REFERENCIA", ""  ); // FIXME: ver que campo es
			jasperwrapper.addParameter("VENDEDOR", salesUserName);
			jasperwrapper.addParameter("NRODOCORIG", JasperReportsUtil.coalesce(order.getPOReference(), "") );
			jasperwrapper.addParameter("NRO_OC", order.getPOReference() );   
			jasperwrapper.addParameter("VCTO", "Vencimiento: " + order.getDatePromised() );			
			jasperwrapper.addParameter(
				"CODVTA",
				JasperReportsUtil.getListName(getCtx(),
						X_C_Invoice.PAYMENTRULE_AD_Reference_ID,
						order.getPaymentRule())
						+ "-" + paymentTermName);
			try{
				Set<String> inouts = JasperReportsUtil.getInOutsDocumentsNo(
					getCtx(), order, get_TrxName());
				Iterator<String> inoutsIt = inouts.iterator();
				int i = 1;
				while (inoutsIt.hasNext()) {
					jasperwrapper.addParameter("NROREMITO_"+i, inoutsIt.next());
				}
			} catch(Exception e){
				log.severe("Error loading invoice in outs");
			}
			jasperwrapper.addParameter("LETRA_PESOS", NumeroCastellano.numeroACastellano(order.getGrandTotal()) );
			jasperwrapper.addParameter("FECHA_ENTREGA", order.getDatePromised() );
			jasperwrapper.addParameter("SUBTOTAL", linesTotalNetAmt);
			jasperwrapper.addParameter("SUBTOTAL_WITHTAX", linesTotalAmt);
			jasperwrapper.addParameter("SUBTOTAL2",  linesTotalNetAmt.subtract(totalNetLineDiscounts));
			jasperwrapper.addParameter("SUBTOTAL2_WITHTAX",  linesTotalAmt.subtract(totalLineDiscounts));
			jasperwrapper.addParameter("DESC_REC", totalLineDiscounts);
			jasperwrapper.addParameter("DESCUENTO_NETO", totalNetLineDiscounts); 
			jasperwrapper.addParameter("IVA_105", new BigDecimal(10.5) );
			jasperwrapper.addParameter("IVA_21", new BigDecimal(21) );
			jasperwrapper.addParameter("IVA_1", getTaxAmt(order, new BigDecimal(10.5)) );			
			jasperwrapper.addParameter("IVA_2", getTaxAmt(order, new BigDecimal(21)) ); 
			jasperwrapper.addParameter("SUBDESC", totalLineDiscounts );
			jasperwrapper.addParameter("TIPOORIGEN", "ORIGINAL" );
			jasperwrapper.addParameter("TOTAL", order.getGrandTotal() );
			jasperwrapper.addParameter("DESCRIPTION", order.getDescription());
			jasperwrapper.addParameter(
				"OBSERVACION",
				JasperReportsUtil.getListName(getCtx(),
						MOrder.PAYMENTRULE_AD_Reference_ID,
						order.getPaymentRule())
						+ " - "
						+ (JasperReportsUtil.getPaymentTermName(getCtx(),
								order.getC_PaymentTerm_ID(), get_TrxName())
								+ " - " + JasperReportsUtil.coalesce(
								order.getDescription(), "")));
			jasperwrapper.addParameter("RESPONSABLE", getInitials(salesUserName));
			if(!Util.isEmpty(order.getM_Shipper_ID(), true)){
				jasperwrapper.addParameter(
					"TRANSPORTE",
					JasperReportsUtil.getShipperName(getCtx(), order.getM_Shipper_ID(),
							get_TrxName()));
			}
			jasperwrapper.addParameter("COMPANIA",client.getName());
			jasperwrapper.addParameter("CLIENT_CUIT",clientInfo.getCUIT());
			jasperwrapper.addParameter("LOCALIZACION", JasperReportsUtil
				.getLocalizacion(getCtx(), order.getAD_Client_ID(),
						order.getAD_Org_ID(), get_TrxName()));
			// NOmbre de organización
			jasperwrapper.addParameter("ORG_NAME",
				JasperReportsUtil.getOrgName(getCtx(), order.getAD_Org_ID()));
			if(!Util.isEmpty(order.getAD_OrgTrx_ID(), false)){
				jasperwrapper.addParameter(
					"ORGTRX_NAME",
					JasperReportsUtil.getOrgName(getCtx(),
							order.getAD_OrgTrx_ID()));
			}
			jasperwrapper.addParameter("ACCEPTANCE", order.getAcceptance());
			if(!Util.isEmpty(order.getAD_User_ID(), true)){
				jasperwrapper.addParameter(
					"USER",
					JasperReportsUtil.getUserName(getCtx(),
							order.getAD_User_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("APPROVAL_AMT", order.getApprovalAmt());
			if(!Util.isEmpty(order.getBill_BPartner_ID(), true)){
				// Nombre de bill bpartner
				jasperwrapper.addParameter(
					"BILL_BPARTNER_NAME",
					JasperReportsUtil.getBPartnerName(getCtx(),
							order.getBill_BPartner_ID(), get_TrxName()));
				// Dirección de bill bpartner
				if(!Util.isEmpty(order.getBill_Location_ID(), true)){
					MBPartnerLocation billLocation = new MBPartnerLocation(
							getCtx(), order.getC_BPartner_Location_ID(),
							get_TrxName());
					jasperwrapper.addParameter("BILL_ADDRESS", billLocation.getName());
					jasperwrapper.addParameter("BILL_PHONE", billLocation.getPhone());
					jasperwrapper.addParameter("BILL_CITY", billLocation.getLocation(false).getCity());
				}
				// Usuario bill bpartner
				if(!Util.isEmpty(order.getBill_User_ID(), true)){
					jasperwrapper.addParameter(
						"BILL_USER",
						JasperReportsUtil.getUserName(getCtx(),
								order.getBill_User_ID(), get_TrxName()));
				}
			}
			// Actividad
			if(!Util.isEmpty(order.getC_Activity_ID(), true)){
				jasperwrapper.addParameter(
					"ACTIVITY",
					JasperReportsUtil.getActivityName(getCtx(),
							order.getC_Activity_ID(), get_TrxName()));
			}
			// Campaña
			if(!Util.isEmpty(order.getC_Campaign_ID(), true)){
				jasperwrapper.addParameter(
					"CAMPAIGN",
					JasperReportsUtil.getCampaignName(getCtx(),
							order.getC_Campaign_ID(), get_TrxName()));
			}
			// Proyecto
			if(!Util.isEmpty(order.getC_Project_ID(), true)){
				jasperwrapper.addParameter(
					"PROJECT",
					JasperReportsUtil.getProjectName(getCtx(),
							order.getC_Project_ID(), get_TrxName()));
			}
			// Cargo
			if(!Util.isEmpty(order.getC_Charge_ID(), true)){
				jasperwrapper.addParameter(
					"CHARGE",
					JasperReportsUtil.getChargeName(getCtx(),
							order.getC_Charge_ID(), get_TrxName()));
			}
			// Moneda
			if(!Util.isEmpty(order.getC_Currency_ID(), true)){
				jasperwrapper.addParameter(
					"CURRENCY",
					JasperReportsUtil.getCurrencyDescription(getCtx(),
							order.getC_Currency_ID(), get_TrxName()));
				jasperwrapper.addParameter(
						"CURRENCY_SIMBOL",
						JasperReportsUtil.getCurrencySymbol(getCtx(),
							order.getC_Currency_ID(), get_TrxName()));
			}
			// Factura relacionada
			if(!Util.isEmpty(order.getC_Invoice_ID(),true)){
				jasperwrapper.addParameter(
					"INVOICE",
					JasperReportsUtil.getInvoiceDisplay(getCtx(),
							order.getC_Invoice_ID(), get_TrxName()));
			}
			// Repair Order
			if(!Util.isEmpty(order.getC_Repair_Order_ID(),true)){
				jasperwrapper.addParameter(
					"REPAIR_ORDER",
					JasperReportsUtil.getOrderDisplay(getCtx(),
							order.getC_Repair_Order_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("CHARGE_AMT", order.getChargeAmt());
			jasperwrapper.addParameter("DATE_ACCT", order.getDateAcct());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			jasperwrapper.addParameter("FECHA_UPDATED", sdf.format(new Date()));
			jasperwrapper.addParameter("DELIVERY_RULE", JasperReportsUtil
				.getListName(getCtx(), X_C_Order.DELIVERYRULE_AD_Reference_ID,
						order.getDeliveryRule()));
			jasperwrapper.addParameter("DELIVERY_VIA_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_C_Order.DELIVERYVIARULE_AD_Reference_ID,
						order.getDeliveryViaRule()));
			if(!Util.isEmpty(order.getDoc_User_ID(), true)){
				jasperwrapper.addParameter(
					"DOC_USER",
					JasperReportsUtil.getUserName(getCtx(),
							order.getDoc_User_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("DOC_STATUS", JasperReportsUtil.getListName(
				getCtx(), X_C_Order.DOCSTATUS_AD_Reference_ID,
				order.getDocStatus()));
			jasperwrapper.addParameter("FREIGHT_AMT", order.getFreightAmt());
			jasperwrapper.addParameter("FREIGHT_COST_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_C_Order.FREIGHTCOSTRULE_AD_Reference_ID,
						order.getFreightCostRule()));
			jasperwrapper.addParameter(
				"PRICELIST",
				JasperReportsUtil.getPriceListName(getCtx(),
						order.getM_PriceList_ID(), get_TrxName()));
			jasperwrapper.addParameter(
				"WAREHOUSE",
				JasperReportsUtil.getWarehouseName(getCtx(),
						order.getM_Warehouse_ID(), get_TrxName()));
			jasperwrapper.addParameter("PRIORITY_RULE", JasperReportsUtil.getListName(
				getCtx(), X_C_Order.PRIORITYRULE_AD_Reference_ID,
				order.getPriorityRule()));
			if(!Util.isEmpty(order.getRef_Order_ID(), true)){
				jasperwrapper.addParameter(
						"REF_ORDER",
						JasperReportsUtil.getOrderDisplay(getCtx(),
								order.getRef_Order_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("REPAIR_PRIORITY", JasperReportsUtil
				.getListName(getCtx(),
						X_C_Order.REPAIR_PRIORITY_AD_Reference_ID,
						order.getrepair_priority()));
			jasperwrapper.addParameter("REPAIR_STATE", JasperReportsUtil
				.getListName(getCtx(), X_C_Order.REPAIR_STATE_AD_Reference_ID,
						order.getrepair_state()));
			jasperwrapper.addParameter("RESUMEN", order.getResumen());
			if(!Util.isEmpty(order.getUser1_ID(), true)){
				jasperwrapper.addParameter("USER1", JasperReportsUtil.getUserName(
					getCtx(), order.getUser1_ID(), get_TrxName()));
			}
			if(!Util.isEmpty(order.getUser2_ID(), true)){
				jasperwrapper.addParameter("USER2", JasperReportsUtil.getUserName(
					getCtx(), order.getUser2_ID(), get_TrxName()));
			}
			jasperwrapper.addParameter("VALID_TO", order.getValidTo());
			jasperwrapper.addParameter("IS_APPROVED", order.isApproved());
			jasperwrapper.addParameter("IS_CREDITAPPROVED", order.isCreditApproved());
			jasperwrapper.addParameter("IS_DELIVERED", order.isDelivered());
			jasperwrapper.addParameter("IS_DROPSHIP", order.isDropShip());
			jasperwrapper.addParameter("IS_POSTED", order.isPosted());
			jasperwrapper.addParameter("ISSOTRX", order.isSOTrx());
			jasperwrapper.addParameter("IS_TAXINCLUDED", order.isTaxIncluded());
			jasperwrapper.addParameter("IS_TRANSFERRED", order.isTransferred());
			
			jasperwrapper.addParameter("PERCEPCION_TOTAL_AMT", order.getPercepcionesTotalAmt());
			
			try {
				jasperwrapper.fillReport(ds);
				jasperwrapper.showReport(getProcessInfo());
			}
				
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se ha podido rellenar el informe.", e);
			}
			
			return "doIt";
	}	
	
	private BigDecimal getTaxAmt(MOrder order, BigDecimal rate)
	{
		BigDecimal retValue = new BigDecimal(0);
		int size = order.getTaxes(true).length;
		boolean found = false;
		MTax tax = null;
		// buscar y matchear el tax correspondiente
		for (int i=0; i<size && !found; i++)
		{
			tax = new MTax(getCtx(), order.getTaxes(false)[i].getC_Tax_ID(), null);
			if (tax.getRate().compareTo(rate) == 0 && tax.getName().toUpperCase().indexOf("IVA") >= 0 )
			{
				found = true;
				retValue = order.getTaxes(false)[i].getTaxAmt();
			}
		}
		return retValue;
	}
	
	
	protected void initializeAmts(MOrder order){
		for (MOrderLine orderLine : order.getLines()) {
			// Total de líneas con impuestos
			linesTotalAmt = linesTotalAmt.add(orderLine.getTotalPriceEnteredWithTax());
			// Total de líneas sin impuestos
			linesTotalNetAmt = linesTotalNetAmt.add(orderLine.getTotalPriceEnteredNet());
			// Total de bonificaciones con impuestos
			linesTotalBonusAmt = linesTotalBonusAmt.add(orderLine.getTotalBonusUnityAmtWithTax());
			// Total de bonificaciones sin impuestos
			linesTotalBonusNetAmt = linesTotalBonusNetAmt.add(orderLine.getTotalBonusUnityAmtNet());
			// Total de descuento de línea con impuestos
			linesTotalLineDiscountsAmt = linesTotalLineDiscountsAmt.add(orderLine.getTotalLineDiscountUnityAmtWithTax());
			// Total de descuento de línea sin impuestos
			linesTotalLineDiscountsNetAmt = linesTotalLineDiscountsNetAmt.add(orderLine.getTotalLineDiscountUnityAmtNet());
			// Total de descuento de documento con impuestos
			linesTotalDocumentDiscountsAmt = linesTotalDocumentDiscountsAmt.add(orderLine.getTotalDocumentDiscountUnityAmtWithTax());
			// Total de descuento de documento sin impuestos
			linesTotalDocumentDiscountsNetAmt = linesTotalDocumentDiscountsNetAmt.add(orderLine.getTotalDocumentDiscountUnityAmtNet());
		}
	}
	
	protected String getInitials(String name) {
		String initials = "";
		String letter = "";
		String letter2 = "";
		for (int i = 0; i < name.length(); i++) {
			letter = name.charAt(i) + "";
			if (letter.compareTo(" ") == 0 && i != name.length()) {
				letter2 = name.charAt(i + 1) + "";
				if (letter2.compareTo(" ") != 0) {
					initials += letter2;
				}
			} else {
				if (i == 0) {
					initials += letter;
				}
			}
		}
		return initials;
	}

	public MJasperReport getJasperwrapper() {
		return jasperwrapper;
	}

	public void setJasperwrapper(MJasperReport jasperwrapper) {
		this.jasperwrapper = jasperwrapper;
	}

	public int getAD_Record_ID() {
		return AD_Record_ID;
	}

	public void setAD_Record_ID(int aD_Record_ID) {
		AD_Record_ID = aD_Record_ID;
	}
}
