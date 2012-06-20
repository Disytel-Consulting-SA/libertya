package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.JasperReport.DataSource.InOutDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCategoriaIva;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPaymentTerm;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MShipper;
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
	private int AD_Record_ID;

	/** Tipo de impresion */
	private String printType;

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
	}

	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport() throws Exception {
		MInOut inout = new MInOut(getCtx(), AD_Record_ID, null);
		MBPartner bpartner = new MBPartner(getCtx(), inout.getC_BPartner_ID(),
				null);
		MJasperReport jasperwrapper = new MJasperReport(getCtx(),
				AD_JasperReport_ID, get_TrxName());
		InOutDataSource ds = new InOutDataSource(getCtx(), inout);

		try {
			ds.loadData();
		} catch (RuntimeException e) {
			throw new RuntimeException(
					"No se pueden cargar los datos del informe", e);
		}

		MOrder order = null;
		if (inout.getC_Order_ID() > 0)
			order = new MOrder(getCtx(), inout.getC_Order_ID(), null);
		MInvoice invoice = null;
		if (!Util.isEmpty(inout.getC_Invoice_ID(), true)) {
			invoice = new MInvoice(getCtx(), inout.getC_Invoice_ID(),
					get_TrxName());
		}
		// Inicializar los montos necesarios para pasar como parámetro
		initializeAmts(order, invoice);
		BigDecimal totalNetLineDiscounts = linesTotalBonusNetAmt.add(
				linesTotalDocumentDiscountsNetAmt).add(
				linesTotalLineDiscountsNetAmt);
		BigDecimal totalLineDiscounts = linesTotalBonusAmt.add(
				linesTotalDocumentDiscountsAmt).add(linesTotalLineDiscountsAmt);
		MBPartnerLocation BPLocation = new MBPartnerLocation(getCtx(),
				inout.getC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(getCtx(),
				BPLocation.getC_Location_ID(), null);
		MRegion region = null;
		if (location.getC_Region_ID() > 0)
			region = new MRegion(getCtx(), location.getC_Region_ID(), null);
		MClient client = JasperReportsUtil.getClient(getCtx(),
				inout.getAD_Client_ID());
		MClientInfo clientInfo = client.getInfo();

		jasperwrapper.addParameter(
				"TIPOCOMPROBANTE",
				JasperReportsUtil.getDocTypeName(getCtx(),
						inout.getC_DocType_ID(), "REMITO", get_TrxName()));
		jasperwrapper.addParameter("FECHA", inout.getMovementDate());
		jasperwrapper.addParameter("MOVEMENT_DATE", inout.getMovementDate());
		jasperwrapper.addParameter("RAZONSOCIAL", bpartner.getName());
		jasperwrapper.addParameter("RAZONSOCIAL2", bpartner.getName2());
		jasperwrapper.addParameter("CODIGO", bpartner.getValue());
		jasperwrapper.addParameter(
				"DIRECCION",
				JasperReportsUtil.coalesce(location.getAddress1(), "")
						+ ". "
						+ ". ("
						+ JasperReportsUtil.coalesce(location.getPostal(), "")
						+ "). "
						+ (region == null ? "" : JasperReportsUtil.coalesce(
								region.getName(), "")));
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
		if (!Util.isEmpty(inout.getSalesRep_ID(), true)) {
			jasperwrapper.addParameter(
					"VENDEDOR",
					JasperReportsUtil.getUserName(getCtx(),
							inout.getSalesRep_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("NRODOCORIG",
				JasperReportsUtil.coalesce(inout.getPOReference(), ""));
		jasperwrapper.addParameter("NRO_OC", order == null ? ""
				: JasperReportsUtil.coalesce(order.getDocumentNo(), ""));
		if(invoice != null){
			String fechaVto = (String) JasperReportsUtil.coalesce(
					getFechaVto(invoice), invoice.getDateInvoiced().toString());
			fechaVto = fechaVto.substring(0, 11); // quitamos la hora del string
			jasperwrapper.addParameter("VCTO","Vencimiento: "+ fechaVto);
		}
		else if(order != null){
			jasperwrapper.addParameter(
					"VCTO",
					"Vencimiento: "
							+ inout.getDateOrdered() == null ? ""
									: (inout.getDateOrdered().toString()
											.substring(0, 11)));
		}
		jasperwrapper.addParameter("NROREMITO", inout.getDocumentNo());
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
				getTaxAmt(order, invoice, new BigDecimal(10.5)));
		jasperwrapper.addParameter("IVA_2",
				getTaxAmt(order, invoice, new BigDecimal(21)));
		jasperwrapper.addParameter("SUBDESC", totalLineDiscounts);
		jasperwrapper.addParameter("TOTAL", grandTotal);
		jasperwrapper.addParameter("LETRA_PESOS", NumeroCastellano.numeroACastellano(grandTotal));
		jasperwrapper.addParameter(
				"CODVTA",
				order == null ? "" : JasperReportsUtil.getListName(getCtx(),
						MInvoice.PAYMENTRULE_AD_Reference_ID,
						order.getPaymentRule()));
		if (invoice != null) {
			jasperwrapper.addParameter("NROCOMPROBANTE",
					invoice.getDocumentNo());
			jasperwrapper.addParameter("INVOICE", JasperReportsUtil
					.getPODisplayByIdentifiers(getCtx(), invoice,
							X_C_Invoice.Table_ID, get_TrxName()));
		}
		if (!Util.isEmpty(inout.getM_Shipper_ID(), true)) {
			jasperwrapper.addParameter(
					"TRANSPORTISTA",
					JasperReportsUtil.getShipperData(getCtx(),
							inout.getM_Shipper_ID(), get_TrxName()));
		}
		if (!Util.isEmpty(inout.getC_Currency_ID(), true)) {
			jasperwrapper.addParameter(
					"MONEDA",
					JasperReportsUtil.getCurrencyDescription(getCtx(),
							inout.getC_Currency_ID(), get_TrxName()));
			jasperwrapper.addParameter(
					"CURRENCY_SYMBOL",
					JasperReportsUtil.getCurrencySymbol(getCtx(),
							inout.getC_Currency_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("TIPOORIGEN", printType);
		jasperwrapper.addParameter("CLIENT", client.getName());
		jasperwrapper.addParameter("CLIENT_CUIT", clientInfo.getCUIT());
		jasperwrapper.addParameter(
				"CLIENT_CATEGORIA_IVA",
				JasperReportsUtil.getCategoriaIVAName(getCtx(),
						clientInfo.getC_Categoria_Iva_ID(), get_TrxName()));
		jasperwrapper.addParameter("ORG",
				JasperReportsUtil.getOrgName(getCtx(), inout.getAD_Org_ID()));
		jasperwrapper.addParameter("ORG_LOCATION_DESCRIPTION",
				JasperReportsUtil.getLocalizacion(getCtx(),
						inout.getAD_Client_ID(), inout.getAD_Org_ID(),
						get_TrxName()));
		if (!Util.isEmpty(inout.getAD_OrgTrx_ID(), true)) {
			jasperwrapper.addParameter(
					"ORGTRX_NAME",
					JasperReportsUtil.getOrgName(getCtx(),
							inout.getAD_OrgTrx_ID()));
		}
		if (!Util.isEmpty(inout.getAD_User_ID(), true)) {
			jasperwrapper.addParameter(
					"USER",
					JasperReportsUtil.getUserName(getCtx(),
							inout.getAD_User_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("APPROVAL_AMT", inout.getApprovalAmt());
		// Actividad
		if (!Util.isEmpty(inout.getC_Activity_ID(), true)) {
			jasperwrapper.addParameter(
					"ACTIVITY",
					JasperReportsUtil.getActivityName(getCtx(),
							inout.getC_Activity_ID(), get_TrxName()));
		}
		// Campaña
		if (!Util.isEmpty(inout.getC_Campaign_ID(), true)) {
			jasperwrapper.addParameter(
					"CAMPAIGN",
					JasperReportsUtil.getCampaignName(getCtx(),
							inout.getC_Campaign_ID(), get_TrxName()));
		}
		// Proyecto
		if (!Util.isEmpty(inout.getC_Project_ID(), true)) {
			jasperwrapper.addParameter(
					"PROJECT",
					JasperReportsUtil.getProjectName(getCtx(),
							inout.getC_Project_ID(), get_TrxName()));
		}
		// Cargo
		if (!Util.isEmpty(inout.getC_Charge_ID(), true)) {
			jasperwrapper.addParameter(
					"CHARGE",
					JasperReportsUtil.getChargeName(getCtx(),
							inout.getC_Charge_ID(), get_TrxName()));
		}
		// Tipo de documento
		if (!Util.isEmpty(inout.getC_DocType_ID(), true)) {
			jasperwrapper.addParameter(
					"DOCTYPE",
					JasperReportsUtil.getDocTypeName(getCtx(),
							inout.getC_DocType_ID(), "REMITO", get_TrxName()));
		}
		jasperwrapper.addParameter("CHARGE_AMT", inout.getChargeAmt());
		jasperwrapper.addParameter("DATE_ACCT", inout.getDateAcct());
		jasperwrapper.addParameter("DATE_RECEIVED", inout.getDateReceived());
		jasperwrapper.addParameter("DELIVERY_RULE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.DELIVERYRULE_AD_Reference_ID,
						inout.getDeliveryRule()));
		jasperwrapper.addParameter("DELIVERY_VIA_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_M_InOut.DELIVERYVIARULE_AD_Reference_ID,
						inout.getDeliveryViaRule()));
		jasperwrapper.addParameter("INOUT_DESCRIPTION", inout.getDescription());
		if (!Util.isEmpty(inout.getDoc_User_ID(), true)) {
			jasperwrapper.addParameter(
					"DOC_USER",
					JasperReportsUtil.getUserName(getCtx(),
							inout.getDoc_User_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("DOC_STATUS", JasperReportsUtil.getListName(
				getCtx(), X_M_InOut.DOCSTATUS_AD_Reference_ID,
				inout.getDocStatus()));
		jasperwrapper.addParameter("DOC_STATUS_VALUE", inout.getDocStatus());
		jasperwrapper.addParameter("FREIGHT_AMT", inout.getFreightAmt());
		jasperwrapper.addParameter("FREIGHT_COST_RULE", JasperReportsUtil
				.getListName(getCtx(),
						X_M_InOut.FREIGHTCOSTRULE_AD_Reference_ID,
						inout.getFreightCostRule()));
		jasperwrapper.addParameter("INOUT_DATE", inout.getInOutDate());
		jasperwrapper.addParameter("INOUT_RECEPTION_DATE",
				inout.getInOutReceptionDate());
		if (!Util.isEmpty(inout.getM_Warehouse_ID(), true)) {
			jasperwrapper.addParameter(
					"WAREHOUSE",
					JasperReportsUtil.getWarehouseName(getCtx(),
							inout.getM_Warehouse_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("MOVEMENT_TYPE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.MOVEMENTTYPE_AD_Reference_ID,
						inout.getMovementType()));
		jasperwrapper.addParameter("PICK_DATE", inout.getPickDate());
		jasperwrapper.addParameter("PRIORITY_RULE", JasperReportsUtil
				.getListName(getCtx(), X_M_InOut.PRIORITYRULE_AD_Reference_ID,
						inout.getPriorityRule()));
		jasperwrapper.addParameter("RECEPTION_DATE", inout.getReceptionDate());
		if (!Util.isEmpty(inout.getRef_InOut_ID(), true)) {
			MInOut refInOut = new MInOut(getCtx(), inout.getRef_InOut_ID(),
					get_TrxName());
			jasperwrapper.addParameter("REF_INOUT", JasperReportsUtil
					.getPODisplayByIdentifiers(getCtx(), refInOut,
							X_M_InOut.Table_ID, get_TrxName()));
		}
		jasperwrapper.addParameter("SHIP_DATE", inout.getShipDate());
		jasperwrapper.addParameter("TRACKINGNO", inout.getTrackingNo());
		if (!Util.isEmpty(inout.getUser1_ID(), true)) {
			jasperwrapper.addParameter("USER1", JasperReportsUtil.getUserName(
					getCtx(), inout.getUser1_ID(), get_TrxName()));
		}
		if (!Util.isEmpty(inout.getUser2_ID(), true)) {
			jasperwrapper.addParameter("USER2", JasperReportsUtil.getUserName(
					getCtx(), inout.getUser2_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("IS_APPROVED", inout.isApproved());
		jasperwrapper.addParameter("IS_COMPLETE", inout.isComplete());
		jasperwrapper.addParameter("IS_INDISPUTE", inout.isInDispute());
		jasperwrapper.addParameter("IS_INTRANSIT", inout.isInTransit());
		jasperwrapper.addParameter("IS_POSTED", inout.isPosted());
		jasperwrapper.addParameter("ISSOTRX", inout.isSOTrx());

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
}
