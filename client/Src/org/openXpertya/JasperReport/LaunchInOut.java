package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.JasperReport.DataSource.InOutDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MUser;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchInOut extends SvrProcess {

	/** Jasper Report */
	private int AD_JasperReport_ID;

	/** Table */
	private int AD_Table_ID;

	/** Record */
	protected int AD_Record_ID;

	/** Tipo de impresion */
	protected String printType;

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

	/** Total */
	private BigDecimal grandTotal = BigDecimal.ZERO;
	
	/** Jasper Report Wrapper*/
	MJasperReport jasperwrapper;

	/** Entidad Comercial relacionada con el remito */
	protected MBPartner bpartner;
	
	/** Localización de la Entidad Comercial relacionada con el remito */
	protected MBPartnerLocation BPLocation;
	
	/** Dirección de la lozalización de la EC relacionada con el remito */
	protected MLocation location;
	
	/** Pedido relacionado con el remito */
	private MOrder order = null;
	
	/** Factura relacionada con el remito o con el pedido del remito */
	private MInvoice invoice = null;
	
	/** Remito actual */
	private MInOut inout = null;
	
	/** Tipo de documento del remito actual */
	private MDocType docType = null;
	
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

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equalsIgnoreCase("TipoDeImpresion")) {
				printType = (String) para[i].getParameter();
			}
		}
		jasperwrapper = new MJasperReport(getCtx(),AD_JasperReport_ID, get_TrxName());
	}

	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport() throws Exception {
		bpartner = new MBPartner(getCtx(), getInout().getC_BPartner_ID(),
				null);
		InOutDataSource ds = new InOutDataSource(getCtx(), getInout());

		try {
			ds.loadData();
		} catch (RuntimeException e) {
			throw new RuntimeException(
					"No se pueden cargar los datos del informe", e);
		}

		// Inicializar los montos necesarios para pasar como parámetro
		initializeAmts(getOrder(), getInvoice());
		BigDecimal totalNetLineDiscounts = linesTotalBonusNetAmt.add(
				linesTotalDocumentDiscountsNetAmt).add(
				linesTotalLineDiscountsNetAmt);
		BigDecimal totalLineDiscounts = linesTotalBonusAmt.add(
				linesTotalDocumentDiscountsAmt).add(linesTotalLineDiscountsAmt);
		BPLocation = new MBPartnerLocation(getCtx(),
				getInout().getC_BPartner_Location_ID(), null);
		location = new MLocation(getCtx(),
				BPLocation.getC_Location_ID(), null);
		MRegion region = null;
		if (location.getC_Region_ID() > 0)
			region = new MRegion(getCtx(), location.getC_Region_ID(), null);
		MClient client = JasperReportsUtil.getClient(getCtx(),
				getInout().getAD_Client_ID());
		MClientInfo clientInfo = client.getInfo();

		jasperwrapper.addParameter(
				"TIPOCOMPROBANTE",
				JasperReportsUtil.getDocTypeName(getCtx(),
						getInout().getC_DocType_ID(), "REMITO", get_TrxName()));
		jasperwrapper.addParameter("FECHA", getInout().getMovementDate());
		jasperwrapper.addParameter("MOVEMENT_DATE", getInout().getMovementDate());
		jasperwrapper.addParameter("RAZONSOCIAL", bpartner.getName());
		jasperwrapper.addParameter("RAZONSOCIAL2", bpartner.getName2());
		jasperwrapper.addParameter("CODIGO", bpartner.getValue());
		jasperwrapper.addParameter("DIRECCION", JasperReportsUtil
				.formatLocation(getCtx(), location.getID(), false));
		jasperwrapper.addParameter("BP_LOCATION_NAME", BPLocation.getName());
		jasperwrapper.addParameter("CIUDAD",
				JasperReportsUtil.coalesce(location.getCity(), ""));
		jasperwrapper
				.addParameter("PAIS", JasperReportsUtil.coalesce(location
						.getCountry().getName(), ""));
		if (!Util.isEmpty(bpartner.getC_Categoria_Iva_ID(), true)) {
			jasperwrapper.addParameter(
					"TIPO_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(),
							bpartner.getC_Categoria_Iva_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("CUIT", bpartner.getTaxID());
		jasperwrapper.addParameter("INGBRUTO", bpartner.getIIBB());
		if (!Util.isEmpty(getInout().getSalesRep_ID(), true)) {
			MUser salesRepUser = new MUser(getCtx(), getInout().getSalesRep_ID(),
					get_TrxName());
			jasperwrapper.addParameter("VENDEDOR",salesRepUser.getName());
			
			// FB - Nombre de la EC asociada al Usuario Vendedor (a veces el
			// nombre de usuario suele ser todo en minúsculas y es mas correcto
			// mostrar Nombre y Apellido de la EC que es un empleado)
			jasperwrapper.addParameter(
				"SALESREP_BP_NAME",
				salesRepUser.getC_BPartner_ID() > 0 ? JasperReportsUtil
						.getBPartnerName(getCtx(),
								salesRepUser.getC_BPartner_ID(),
								get_TrxName()) 
						: salesRepUser.getName());
		}
		jasperwrapper.addParameter("NRODOCORIG",
				JasperReportsUtil.coalesce(getInout().getPOReference(), ""));
		jasperwrapper.addParameter("NRO_OC", getOrder() == null ? ""
				: JasperReportsUtil.coalesce(getOrder().getDocumentNo(), ""));
		if(getInvoice() != null){
			String fechaVto = (String) JasperReportsUtil.coalesce(
					getFechaVto(getInvoice()), getInvoice().getDateInvoiced().toString());
			fechaVto = fechaVto.substring(0, 11); // quitamos la hora del string
			jasperwrapper.addParameter("VCTO","Vencimiento: "+ fechaVto);
		}
		else if(getOrder() != null){
			jasperwrapper.addParameter(
					"VCTO",
					"Vencimiento: "
							+ getInout().getDateOrdered() == null ? ""
									: (getInout().getDateOrdered().toString()
											.substring(0, 11)));
		}
		jasperwrapper.addParameter("NROREMITO", getInout().getDocumentNo());
		jasperwrapper.addParameter("SUBTOTAL", linesTotalNetAmt);
		jasperwrapper.addParameter("SUBTOTAL_WITHTAX", linesTotalAmt);
		jasperwrapper.addParameter("SUBTOTAL2",
				linesTotalNetAmt.subtract(totalNetLineDiscounts));
		jasperwrapper.addParameter("SUBTOTAL2_WITHTAX",
				linesTotalAmt.subtract(totalLineDiscounts));
		jasperwrapper.addParameter("DESC_REC", totalLineDiscounts);
		jasperwrapper.addParameter("DESCUENTO_NETO", totalNetLineDiscounts);
		jasperwrapper.addParameter("IVA_105", new BigDecimal(10.5));
		jasperwrapper.addParameter("IVA_21", new BigDecimal(21));
		jasperwrapper.addParameter("IVA_1",
				getTaxAmt(getOrder(), getInvoice(), new BigDecimal(10.5)));
		jasperwrapper.addParameter("IVA_2",
				getTaxAmt(getOrder(), getInvoice(), new BigDecimal(21)));
		jasperwrapper.addParameter("SUBDESC", totalLineDiscounts);
		jasperwrapper.addParameter("TOTAL", grandTotal);
		jasperwrapper.addParameter("LETRA_PESOS", NumeroCastellano.numeroACastellano(grandTotal));
		jasperwrapper.addParameter(
				"CODVTA",
				getOrder() == null ? "" : JasperReportsUtil.getListName(getCtx(),
						MInvoice.PAYMENTRULE_AD_Reference_ID,
						getOrder().getPaymentRule()));
		if (getInvoice() != null) {
			jasperwrapper.addParameter("NROCOMPROBANTE",
					getInvoice().getDocumentNo());
			jasperwrapper.addParameter("INVOICE", JasperReportsUtil
					.getPODisplayByIdentifiers(getCtx(), getInvoice(),
							X_C_Invoice.Table_ID, get_TrxName()));
		}
		if (!Util.isEmpty(getInout().getM_Shipper_ID(), true)) {
			jasperwrapper.addParameter(
					"TRANSPORTISTA",
					JasperReportsUtil.getShipperData(getCtx(),
							getInout().getM_Shipper_ID(), get_TrxName()));
		}
		if (!Util.isEmpty(getInout().getC_Currency_ID(), true)) {
			jasperwrapper.addParameter(
					"MONEDA",
					JasperReportsUtil.getCurrencyDescription(getCtx(),
							getInout().getC_Currency_ID(), get_TrxName()));
			jasperwrapper.addParameter(
					"CURRENCY_SYMBOL",
					JasperReportsUtil.getCurrencySymbol(getCtx(),
							getInout().getC_Currency_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("TIPOORIGEN", printType);
		jasperwrapper.addParameter("CLIENT", client.getName());
		jasperwrapper.addParameter("CLIENT_CUIT", clientInfo.getCUIT());
		jasperwrapper.addParameter(
				"CLIENT_CATEGORIA_IVA",
				JasperReportsUtil.getCategoriaIVAName(getCtx(),
						clientInfo.getC_Categoria_Iva_ID(), get_TrxName()));
		jasperwrapper.addParameter("ORG",
				JasperReportsUtil.getOrgName(getCtx(), getInout().getAD_Org_ID()));
		jasperwrapper.addParameter("ORG_LOCATION_DESCRIPTION",
				JasperReportsUtil.getLocalizacion(getCtx(),
						getInout().getAD_Client_ID(), getInout().getAD_Org_ID(),
						get_TrxName()));
		if (!Util.isEmpty(getInout().getAD_OrgTrx_ID(), true)) {
			jasperwrapper.addParameter(
					"ORGTRX_NAME",
					JasperReportsUtil.getOrgName(getCtx(),
							getInout().getAD_OrgTrx_ID()));
		}
		if (!Util.isEmpty(getInout().getAD_User_ID(), true)) {
			jasperwrapper.addParameter(
					"USER",
					JasperReportsUtil.getUserName(getCtx(),
							getInout().getAD_User_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("APPROVAL_AMT", getInout().getApprovalAmt());
		// Actividad
		if (!Util.isEmpty(getInout().getC_Activity_ID(), true)) {
			jasperwrapper.addParameter(
					"ACTIVITY",
					JasperReportsUtil.getActivityName(getCtx(),
							getInout().getC_Activity_ID(), get_TrxName()));
		}
		// Campaña
		if (!Util.isEmpty(getInout().getC_Campaign_ID(), true)) {
			jasperwrapper.addParameter(
					"CAMPAIGN",
					JasperReportsUtil.getCampaignName(getCtx(),
							getInout().getC_Campaign_ID(), get_TrxName()));
		}
		// Proyecto
		if (!Util.isEmpty(getInout().getC_Project_ID(), true)) {
			jasperwrapper.addParameter(
					"PROJECT",
					JasperReportsUtil.getProjectName(getCtx(),
							getInout().getC_Project_ID(), get_TrxName()));
		}
		// Cargo
		if (!Util.isEmpty(getInout().getC_Charge_ID(), true)) {
			jasperwrapper.addParameter(
					"CHARGE",
					JasperReportsUtil.getChargeName(getCtx(),
							getInout().getC_Charge_ID(), get_TrxName()));
		}
		// Tipo de documento
		if (!Util.isEmpty(getInout().getC_DocType_ID(), true)) {
			jasperwrapper.addParameter(
					"DOCTYPE",
					JasperReportsUtil.getDocTypeName(getCtx(),
							getInout().getC_DocType_ID(), "REMITO", get_TrxName()));
		}
		jasperwrapper.addParameter("CHARGE_AMT", getInout().getChargeAmt());
		jasperwrapper.addParameter("DATE_ACCT", getInout().getDateAcct());
		jasperwrapper.addParameter("DATE_RECEIVED", getInout().getDateReceived());
		jasperwrapper.addParameter("DELIVERY_RULE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.DELIVERYRULE_AD_Reference_ID,
						getInout().getDeliveryRule()));
		jasperwrapper.addParameter("DELIVERY_VIA_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_M_InOut.DELIVERYVIARULE_AD_Reference_ID,
						getInout().getDeliveryViaRule()));
		jasperwrapper.addParameter("INOUT_DESCRIPTION", getInout().getDescription());
		if (!Util.isEmpty(getInout().getDoc_User_ID(), true)) {
			jasperwrapper.addParameter(
					"DOC_USER",
					JasperReportsUtil.getUserName(getCtx(),
							getInout().getDoc_User_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("DOC_STATUS", JasperReportsUtil.getListName(
				getCtx(), X_M_InOut.DOCSTATUS_AD_Reference_ID,
				getInout().getDocStatus()));
		jasperwrapper.addParameter("DOC_STATUS_VALUE", getInout().getDocStatus());
		jasperwrapper.addParameter("FREIGHT_AMT", getInout().getFreightAmt());
		jasperwrapper.addParameter("FREIGHT_COST_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_M_InOut.FREIGHTCOSTRULE_AD_Reference_ID,
						getInout().getFreightCostRule()));
		jasperwrapper.addParameter("INOUT_DATE", getInout().getInOutDate());
		jasperwrapper.addParameter("INOUT_RECEPTION_DATE",
				getInout().getInOutReceptionDate());
		if (!Util.isEmpty(getInout().getM_Warehouse_ID(), true)) {
			jasperwrapper.addParameter(
					"WAREHOUSE",
					JasperReportsUtil.getWarehouseName(getCtx(),
							getInout().getM_Warehouse_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("MOVEMENT_TYPE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.MOVEMENTTYPE_AD_Reference_ID,
						getInout().getMovementType()));
		jasperwrapper.addParameter("PICK_DATE", getInout().getPickDate());
		jasperwrapper.addParameter("PRIORITY_RULE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.PRIORITYRULE_AD_Reference_ID,
						getInout().getPriorityRule()));
		jasperwrapper.addParameter("RECEPTION_DATE", getInout().getReceptionDate());
		if (!Util.isEmpty(getInout().getRef_InOut_ID(), true)) {
			MInOut refInOut = new MInOut(getCtx(), getInout().getRef_InOut_ID(),
					get_TrxName());
			jasperwrapper.addParameter("REF_INOUT", JasperReportsUtil
					.getPODisplayByIdentifiers(getCtx(), refInOut,
							X_M_InOut.Table_ID, get_TrxName()));
		}
		jasperwrapper.addParameter("SHIP_DATE", getInout().getShipDate());
		jasperwrapper.addParameter("TRACKINGNO", getInout().getTrackingNo());
		// Contacto (AD_User_ID)
		if(!Util.isEmpty(getInout().getAD_User_ID(), true)) {
			MUser contact = new MUser(getCtx(), getInout().getAD_User_ID(),
				get_TrxName());
			// FB - Nuevos parámetros de contacto
			jasperwrapper.addParameter("CONTACT_NAME", contact.getName());
			jasperwrapper.addParameter("CONTACT_PHONE", contact.getPhone());
			jasperwrapper.addParameter("CONTACT_PHONE2", contact.getPhone2());
			jasperwrapper.addParameter("CONTACT_PHONE3", contact.getphone3());
			jasperwrapper.addParameter("CONTACT_FAX", contact.getFax());
			jasperwrapper.addParameter("CONTACT_EMAIL", contact.getEMail());
		}
		
		if (!Util.isEmpty(getInout().getUser1_ID(), true)) {
			jasperwrapper.addParameter("USER1", JasperReportsUtil.getUserName(
					getCtx(), getInout().getUser1_ID(), get_TrxName()));
		}
		if (!Util.isEmpty(getInout().getUser2_ID(), true)) {
			jasperwrapper.addParameter("USER2", JasperReportsUtil.getUserName(
					getCtx(), getInout().getUser2_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("IS_APPROVED", getInout().isApproved());
		jasperwrapper.addParameter("IS_COMPLETE", getInout().isComplete());
		jasperwrapper.addParameter("IS_INDISPUTE", getInout().isInDispute());
		jasperwrapper.addParameter("IS_INTRANSIT", getInout().isInTransit());
		jasperwrapper.addParameter("IS_POSTED", getInout().isPosted());
		jasperwrapper.addParameter("ISSOTRX", getInout().isSOTrx());

		// Datos de Localización 
		MLocation loc = BPLocation.getLocation(false);
		jasperwrapper.addParameter("LOC_ADDRESS1", loc.getAddress1());
		jasperwrapper.addParameter("LOC_ADDRESS2", loc.getAddress2());
		jasperwrapper.addParameter("LOC_ADDRESS3", loc.getAddress3());
		jasperwrapper.addParameter("LOC_ADDRESS4", loc.getAddress1());
		jasperwrapper.addParameter("LOC_PLAZA", loc.getPlaza());
		jasperwrapper.addParameter("LOC_CITY", loc.getCity());
		jasperwrapper.addParameter("LOC_POSTAL", loc.getPostal());
		jasperwrapper.addParameter("LOC_REGION", loc.getC_Region_ID() > 0 ? loc.getRegion().getName() : "");
		jasperwrapper.addParameter("LOC_COUNTRY", loc.getC_City_ID() > 0 ? loc.getCountry().getName() : "");
		
		jasperwrapper.addParameter("BP_LOCATION_PHONE", BPLocation.getPhone());
		jasperwrapper.addParameter("BP_LOCATION_PHONE2", BPLocation.getPhone2());
		jasperwrapper.addParameter("BP_LOCATION_FAX", BPLocation.getFax());
		jasperwrapper.addParameter("BP_LOCATION_ISDN", BPLocation.getISDN());

		// Agregar datos del cliente en el caso que sea consumidor final
		if(getInvoice() != null || getOrder() != null){
			jasperwrapper.addParameter(
					"CUSTOMER_NAME",
					getInvoice() != null ? getInvoice().getNombreCli() : getOrder()
							.getNombreCli());
			jasperwrapper.addParameter(
					"CUSTOMER_ADDRESS",
					getInvoice() != null ? getInvoice().getInvoice_Adress() : getOrder()
							.getInvoice_Adress());
			jasperwrapper.addParameter(
					"CUSTOMER_NROIDENTIFIC_CLIENTE",
					getInvoice() != null ? getInvoice().getNroIdentificCliente() : getOrder()
							.getNroIdentificCliente());
			jasperwrapper.addParameter("CUSTOMER_CUIT",
					getInvoice() != null ? getInvoice().getCUIT() : getOrder().getCUIT());
		}
		
		jasperwrapper.addParameter("CREATEDBY", JasperReportsUtil.getUserName(
				getCtx(), getInout().getCreatedBy(), get_TrxName()));
		jasperwrapper.addParameter("UPDATEDBY", JasperReportsUtil.getUserName(
				getCtx(), getInout().getUpdatedBy(), get_TrxName()));
		
		try {
			jasperwrapper.fillReport(ds);
			jasperwrapper.showReport(getProcessInfo());
		}

		catch (RuntimeException e) {
			throw new RuntimeException("No se ha podido rellenar el informe.",
					e);
		}

		return "doIt";
	}

	protected void initializeAmts(MOrder order, MInvoice invoice) {
		if (invoice != null) {
			initializeAmts(invoice);
		} else if (order != null) {
			initializeAmts(order);
		}
	}

	protected void initializeAmts(MOrder order) {
		grandTotal = order.getGrandTotal();
		for (MOrderLine orderLine : order.getLines()) {
			// Total de líneas con impuestos
			linesTotalAmt = linesTotalAmt.add(orderLine
					.getTotalPriceEnteredWithTax());
			// Total de líneas sin impuestos
			linesTotalNetAmt = linesTotalNetAmt.add(orderLine
					.getTotalPriceEnteredNet());
			// Total de bonificaciones con impuestos
			linesTotalBonusAmt = linesTotalBonusAmt.add(orderLine
					.getTotalBonusUnityAmtWithTax());
			// Total de bonificaciones sin impuestos
			linesTotalBonusNetAmt = linesTotalBonusNetAmt.add(orderLine
					.getTotalBonusUnityAmtNet());
			// Total de descuento de línea con impuestos
			linesTotalLineDiscountsAmt = linesTotalLineDiscountsAmt
					.add(orderLine.getTotalLineDiscountUnityAmtWithTax());
			// Total de descuento de línea sin impuestos
			linesTotalLineDiscountsNetAmt = linesTotalLineDiscountsNetAmt
					.add(orderLine.getTotalLineDiscountUnityAmtNet());
			// Total de descuento de documento con impuestos
			linesTotalDocumentDiscountsAmt = linesTotalDocumentDiscountsAmt
					.add(orderLine.getTotalDocumentDiscountUnityAmtWithTax());
			// Total de descuento de documento sin impuestos
			linesTotalDocumentDiscountsNetAmt = linesTotalDocumentDiscountsNetAmt
					.add(orderLine.getTotalDocumentDiscountUnityAmtNet());
		}
	}

	protected void initializeAmts(MInvoice invoice) {
		grandTotal = invoice.getGrandTotal();
		for (MInvoiceLine invoiceLine : invoice.getLines()) {
			// Total de líneas con impuestos
			linesTotalAmt = linesTotalAmt.add(invoiceLine
					.getTotalPriceEnteredWithTax());
			// Total de líneas sin impuestos
			linesTotalNetAmt = linesTotalNetAmt.add(invoiceLine
					.getTotalPriceEnteredNet());
			// Total de bonificaciones con impuestos
			linesTotalBonusAmt = linesTotalBonusAmt.add(invoiceLine
					.getTotalBonusUnityAmtWithTax());
			// Total de bonificaciones sin impuestos
			linesTotalBonusNetAmt = linesTotalBonusNetAmt.add(invoiceLine
					.getTotalBonusUnityAmtNet());
			// Total de descuento de línea con impuestos
			linesTotalLineDiscountsAmt = linesTotalLineDiscountsAmt
					.add(invoiceLine.getTotalLineDiscountUnityAmtWithTax());
			// Total de descuento de línea sin impuestos
			linesTotalLineDiscountsNetAmt = linesTotalLineDiscountsNetAmt
					.add(invoiceLine.getTotalLineDiscountUnityAmtNet());
			// Total de descuento de documento con impuestos
			linesTotalDocumentDiscountsAmt = linesTotalDocumentDiscountsAmt
					.add(invoiceLine.getTotalDocumentDiscountUnityAmtWithTax());
			// Total de descuento de documento sin impuestos
			linesTotalDocumentDiscountsNetAmt = linesTotalDocumentDiscountsNetAmt
					.add(invoiceLine.getTotalDocumentDiscountUnityAmtNet());
		}
	}

	private BigDecimal getTaxAmt(MOrder order, MInvoice invoice, BigDecimal rate) {
		BigDecimal retValue = BigDecimal.ZERO;
		if (invoice != null) {
			retValue = getTaxAmt(invoice, rate);
		} else if (order != null) {
			retValue = getTaxAmt(order, rate);
		}
		return retValue;
	}

	private BigDecimal getTaxAmt(MOrder order, BigDecimal rate) {
		BigDecimal retValue = new BigDecimal(0);
		int size = order.getTaxes(true).length;
		boolean found = false;
		MTax tax = null;
		// buscar y matchear el tax correspondiente
		for (int i = 0; i < size && !found; i++) {
			tax = new MTax(getCtx(), order.getTaxes(false)[i].getC_Tax_ID(),
					null);
			if (tax.getRate().compareTo(rate) == 0
					&& tax.getName().toUpperCase().indexOf("IVA") >= 0) {
				found = true;
				retValue = order.getTaxes(false)[i].getTaxAmt();
			}
		}
		return retValue;
	}

	private BigDecimal getTaxAmt(MInvoice invoice, BigDecimal rate) {
		BigDecimal retValue = new BigDecimal(0);
		int size = invoice.getTaxes(true).length;
		boolean found = false;
		MTax tax = null;
		// buscar y matchear el tax correspondiente
		for (int i = 0; i < size && !found; i++) {
			tax = new MTax(getCtx(), invoice.getTaxes(false)[i].getC_Tax_ID(),
					null);
			if (tax.getRate().compareTo(rate) == 0
					&& tax.getName().toUpperCase().indexOf("IVA") >= 0) {
				found = true;
				retValue = invoice.getTaxes(false)[i].getTaxAmt();
			}
		}
		return retValue;
	}
	
	private String getFechaVto(MInvoice invoice)
	{
		try 
		{
			int invoicePayScheduleID = 0;
			PreparedStatement stmt = DB.prepareStatement("SELECT c_invoicepayschedule_id FROM c_invoice_v WHERE c_invoice_id = ? ORDER BY c_invoicepayschedule_id DESC");
			stmt.setInt(1, invoice.getC_Invoice_ID());
			ResultSet rs = stmt.executeQuery();
			if (!rs.next() || rs.getInt(1) == 0)
				return null;
			
			invoicePayScheduleID = rs.getInt(1);
			MInvoicePaySchedule invoicePaySchedule = new MInvoicePaySchedule(getCtx(), invoicePayScheduleID, null);
			return invoicePaySchedule.getDueDate().toString();	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
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

	protected MInOut getInout() {
		if(inout == null){
			inout = new MInOut(getCtx(), AD_Record_ID, null);
		}
		return inout;
	}

	protected void setInout(MInOut inout) {
		this.inout = inout;
	}

	protected MOrder getOrder() {
		if(order == null){
			if (getInout().getC_Order_ID() > 0)
				setOrder(new MOrder(getCtx(), getInout().getC_Order_ID(), null));
		}
		return order;
	}

	protected void setOrder(MOrder order) {
		this.order = order;
	}

	protected MInvoice getInvoice() {
		if(invoice == null){
			if (!Util.isEmpty(getInout().getC_Invoice_ID(), true)) {
				setInvoice(new MInvoice(getCtx(), getInout().getC_Invoice_ID(),
						get_TrxName()));
			}
			// Buscar la factura por el pedido
			else if (getOrder() != null) {
				Integer invoiceID = getOrder().getC_Invoice_ID();
				if(!Util.isEmpty(getOrder().getC_Invoice_ID(), true)){
					setInvoice(new MInvoice(getCtx(), invoiceID, get_TrxName()));
				}
			}
		}
		return invoice;
	}

	protected void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	protected MDocType getDocType() {
		if(docType == null){
			docType = new MDocType(getCtx(), getInout().getC_DocType_ID(),
					get_TrxName());
		}
		return docType;
	}

	protected void setDocType(MDocType docType) {
		this.docType = docType;
	}
}
