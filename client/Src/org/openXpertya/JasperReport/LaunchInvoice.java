package org.openXpertya.JasperReport;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.openXpertya.JasperReport.DataSource.InvoiceDataSource;
import org.openXpertya.JasperReport.DataSource.InvoicePerceptionsDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MElectronicInvoice;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MRetencionType;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MUser;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.FacturaElectronicaBarcodeGenerator;
import org.openXpertya.util.FacturaElectronicaQRCodeGenerator;
import org.openXpertya.util.Util;
import org.openXpertya.util.Utils;

public class LaunchInvoice extends SvrProcess {

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
	MJasperReport jasperwrapper;
	
	@Override
	protected void prepare() {

		// Determinar JasperReport para wrapper, tabla y registro actual
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport() != true)
			return;

		AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
		AD_Table_ID = getTable_ID();
		AD_Record_ID = getRecord_ID();	

        jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
	}
	
	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport()	throws Exception{
		
		MInvoice invoice = new MInvoice(getCtx(), getInvoiceID(), null);
		MBPartner bpartner = new MBPartner(getCtx(), invoice.getC_BPartner_ID(), null);
		
		//Ader mejora de caches
		invoice.initCaches();
		
		OXPJasperDataSource ds = getDataSource(invoice);
		
		 try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
			// Agrego los parámetros al reporte
			addReportParameter(jasperwrapper, invoice, bpartner);
			
			try {
				jasperwrapper.fillReport(ds, this);
				jasperwrapper.showReport(getProcessInfo());
			}
				
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se ha podido rellenar el informe.", e);
			}
			
			return "";
	}
	
	protected OXPJasperDataSource getDataSource(MInvoice invoice){
		return new InvoiceDataSource(getCtx(), invoice);
	}
	
	protected Integer getInvoiceID(){
		return AD_Record_ID;
	}
	
	protected void addReportParameter(MJasperReport jasperwrapper, MInvoice invoice, MBPartner bpartner) throws Exception{
		// Establecemos parametros
		// Total de líneas con impuestos
		initializeAmts(invoice);
		
		MClient client = JasperReportsUtil.getClient(getCtx(), invoice.getAD_Client_ID());
		MClientInfo clientInfo = JasperReportsUtil.getClientInfo(getCtx(), invoice.getAD_Client_ID(), get_TrxName());
		MBPartnerLocation BPLocation = new MBPartnerLocation(getCtx(), invoice.getC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(getCtx(), BPLocation.getC_Location_ID(), null);
		MRegion region = null;
		if (location.getC_Region_ID() > 0)
			region = new MRegion(getCtx(), location.getC_Region_ID(), null);

		MDocType docType = new MDocType(getCtx(), invoice.getC_DocTypeTarget_ID(), get_TrxName());
		MOrder order = null;
		if(!Util.isEmpty(invoice.getC_Order_ID(), true)){
			order = new MOrder(getCtx(), invoice.getC_Order_ID(), get_TrxName());
		}
		else {
			// Buscar el pedido en base al pedido que está en la líneas si así existe
			order = getMOrderByLines(invoice);
		}
		
		if(client.getLogoImg() != null){
			InputStream logo = new ByteArrayInputStream(client.getLogoImg());
			jasperwrapper.addParameter("LOGO",(InputStream)logo);
		}
		
		// Descuentos aplicados totales
		jasperwrapper.addParameter("NROCOMPROBANTE", (!Util.isEmpty(invoice.getNumeroDeDocumento(), true)
				? invoice.getNumeroDeDocumento() : invoice.getDocumentNo()).replace("<", "").replace(">", ""));
		jasperwrapper.addParameter("NROCOMPROBANTESMALL",
				invoice.getPuntoDeVenta()
						+ "00000000".substring(String.valueOf(invoice.getNumeroComprobante()).length())
						+ invoice.getNumeroComprobante());
		jasperwrapper.addParameter("TIPOCOMPROBANTE", JasperReportsUtil
			.getDocTypeName(getCtx(), invoice.getC_DocTypeTarget_ID(),
					"FACTURA", get_TrxName()));
		jasperwrapper.addParameter("DOCUMENTNO", invoice.getDocumentNo());
		jasperwrapper.addParameter("DOCTYPEKEY", docType.getDocTypeKey());
		jasperwrapper.addParameter("DOCTYPENOTES", docType.getDocumentNote());
				
		jasperwrapper.addParameter("FECHA", invoice.getDateInvoiced());
		jasperwrapper.addParameter("FECHA_CONTABLE", invoice.getDateAcct());
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(invoice.getDateInvoiced().getTime());
		jasperwrapper.addParameter("DIA", Integer.toString(c.get(Calendar.DATE)));
		jasperwrapper.addParameter("MES", Integer.toString(c.get(Calendar.MONTH)+1)); // Mas 1 porque el Calendar maneja los meses de 0 a 11
		jasperwrapper.addParameter("ANIO", Integer.toString(c.get(Calendar.YEAR)));		
		// NombreCli
		if(!Util.isEmpty(invoice.getNombreCli()))
			jasperwrapper.addParameter("RAZONSOCIAL", invoice.getNombreCli()); // NombreCli de la Factura
		else
			jasperwrapper.addParameter("RAZONSOCIAL", JasperReportsUtil.coalesce(bpartner.getName(), ""));
		jasperwrapper.addParameter("RAZONSOCIAL2", JasperReportsUtil.coalesce(bpartner.getName2(), "") );
		jasperwrapper.addParameter("BPARTNER_NAME", bpartner.getName());
		jasperwrapper.addParameter("CODIGO", bpartner.getValue());
		jasperwrapper.addParameter("DIRECCION", JasperReportsUtil.coalesce(
				invoice.getInvoice_Adress(), JasperReportsUtil.formatLocation(
						getCtx(), location.getID(), false)));
		jasperwrapper.addParameter("BP_LOCATION_NAME", BPLocation.getName());
		jasperwrapper.addParameter("ADDRESS1", JasperReportsUtil.coalesce(location.getAddress1(), ""));
		jasperwrapper.addParameter("CIUDAD", JasperReportsUtil.coalesce(location.getCity(),""));
		jasperwrapper.addParameter("PAIS", JasperReportsUtil.coalesce(location.getCountry().getName(),""));
		
		// Categoria IVA
		if(!Util.isEmpty(invoice.getCAT_Iva_ID(),true)) {			
			jasperwrapper.addParameter("TIPO_IVA", 
					JasperReportsUtil.getCategoriaIVAName(getCtx(), invoice.getCAT_Iva_ID(), get_TrxName()));
		} else {
			if(!Util.isEmpty(bpartner.getC_Categoria_Iva_ID(), true)){
				jasperwrapper.addParameter("TIPO_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(), bpartner.getC_Categoria_Iva_ID(), get_TrxName()));
			}
		}
		
		jasperwrapper.addParameter("LETRA", JasperReportsUtil.coalesce(invoice.getLetra(),""));
		// CUIT
		if(!Util.isEmpty(invoice.getNroIdentificCliente()))
			jasperwrapper.addParameter("CUIT", invoice.getNroIdentificCliente());
		else
			jasperwrapper.addParameter("CUIT", bpartner.getTaxID());
		jasperwrapper.addParameter("INGBRUTO", bpartner.getIIBB());
		if (invoice.getSalesRep_ID() > 0) {
			MUser salesRepUser = new MUser(getCtx(), invoice.getSalesRep_ID(),
					get_TrxName());
			
			jasperwrapper.addParameter("VENDEDOR", salesRepUser.getName());
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
				JasperReportsUtil.coalesce(invoice.getPOReference(), ""));
		if(order != null){
			jasperwrapper.addParameter("NRO_OC",
					JasperReportsUtil.coalesce(order.getDocumentNo(), ""));
			jasperwrapper.addParameter(
					"ORDER_CURRENCY",
					JasperReportsUtil.getCurrencyDescription(getCtx(),
							order.getC_Currency_ID(), get_TrxName()));
			jasperwrapper.addParameter(
					"ORDER_CURRENCY_SIMBOL",
					JasperReportsUtil.getCurrencySymbol(getCtx(),
							order.getC_Currency_ID(), get_TrxName()));
		} 
		Timestamp fechaVto = getFechaVto(invoice);
		if(fechaVto == null){
			fechaVto = invoice.getDateInvoiced();
		}
		DateFormat sdp = new SimpleDateFormat("dd/MM/yyyy");
		jasperwrapper.addParameter("VCTO", "Vencimiento: " +  sdp.format(fechaVto));
		jasperwrapper.addParameter("VCTO_DATE", fechaVto);
		jasperwrapper.addParameter("CODVTA", JasperReportsUtil.getListName(
			getCtx(), X_C_Invoice.PAYMENTRULE_AD_Reference_ID,
			invoice.getPaymentRule()));
		try{
			Set<String> inouts = JasperReportsUtil.getInOutsDocumentsNo(
				getCtx(), invoice, get_TrxName());
			int i = 1;
			for (String idocumentNo : inouts) {
				jasperwrapper.addParameter("NROREMITO_"+(i++), idocumentNo);
			}
			// Si no existen remitos asociados en la inversa, desde el remito, 
			// se toman desde las líneas de la factura
			if(inouts.size() == 0) {
				MInOut io = getMInOutByLines(invoice);
				if(io != null) {
					jasperwrapper.addParameter("NROREMITO_1", io.getDocumentNo());
				}
			}
		} catch(Exception e){
			log.severe("Error loading invoice in outs");
		}
		jasperwrapper.addParameter("LETRA_PESOS", NumeroCastellano.numeroACastellano(invoice.getGrandTotal()) );
		jasperwrapper.addParameter("SUBTOTAL", linesTotalNetAmt);
		jasperwrapper.addParameter("SUBTOTAL_WITHTAX", linesTotalAmt);
		BigDecimal totalNetLineDiscounts = linesTotalBonusNetAmt.add(
			linesTotalDocumentDiscountsNetAmt).add(
			linesTotalLineDiscountsNetAmt);
		BigDecimal totalLineDiscounts = linesTotalBonusAmt.add(
				linesTotalDocumentDiscountsAmt).add(
				linesTotalLineDiscountsAmt);
		jasperwrapper.addParameter("DESCUENTO", totalNetLineDiscounts);
		jasperwrapper.addParameter("DESCUENTO_NETO",totalNetLineDiscounts);
		jasperwrapper.addParameter("SUBTOTAL2", linesTotalNetAmt.subtract(totalNetLineDiscounts));
		jasperwrapper.addParameter("SUBTOTAL2_WITHTAX",  linesTotalAmt.subtract(totalLineDiscounts));
		jasperwrapper.addParameter("IVA_105", new BigDecimal(10.5) );
		jasperwrapper.addParameter("IVA_21", new BigDecimal(21) );
		BigDecimal iva105Amt = getTaxAmt(invoice, new BigDecimal(10.5));
		BigDecimal iva21Amt = getTaxAmt(invoice, new BigDecimal(21));
		jasperwrapper.addParameter("SUBIVA_105", iva105Amt);
		jasperwrapper.addParameter("SUBIVA_21", iva21Amt);		
		jasperwrapper.addParameter("SUBDESC", invoice.getDiscountsAmt());
		jasperwrapper.addParameter("TOTAL", invoice.getGrandTotal() );
		jasperwrapper.addParameter("TOTAL_USD", getTotalUSD(invoice));
		jasperwrapper
				.addParameter(
						"TIPOORIGEN",
						JasperReportsUtil.getListName(
								getCtx(),
								MInvoice.PRINTTYPE_AD_Reference_ID,
								Util.isEmpty(invoice.getPrintType(), true) ? MInvoice.PRINTTYPE_Original
										: invoice.getPrintType()));
		
		jasperwrapper.addParameter("ALLOCATED_AMT", invoice.getAllocatedAmt() );
		jasperwrapper.addParameter("APPROVAL_AMT", invoice.getApprovalAmt() );
		jasperwrapper.addParameter("AUTH_CODE", invoice.getAuthCode() );
		// Actividad
		if(!Util.isEmpty(invoice.getC_Activity_ID(),true)){
			jasperwrapper.addParameter(
				"ACTIVITY",
				JasperReportsUtil.getActivityName(getCtx(),
						invoice.getC_Activity_ID(), get_TrxName()));
		}
		// Campaña
		if(!Util.isEmpty(invoice.getC_Campaign_ID(),true)){
			jasperwrapper.addParameter(
				"CAMPAIGN",
				JasperReportsUtil.getCampaignName(getCtx(),
						invoice.getC_Campaign_ID(), get_TrxName()));
		}
		// Proyecto
		if(!Util.isEmpty(invoice.getC_Project_ID(),true)){
			jasperwrapper.addParameter(
				"PROJECT",
				JasperReportsUtil.getProjectName(getCtx(),
						invoice.getC_Project_ID(), get_TrxName()));
		}
		// Cargo
		if(!Util.isEmpty(invoice.getC_Charge_ID(),true)){
			jasperwrapper.addParameter(
				"CHARGE",
				JasperReportsUtil.getChargeName(getCtx(),
						invoice.getC_Charge_ID(), get_TrxName()));
		}
		// Moneda
		if(!Util.isEmpty(invoice.getC_Currency_ID(),true)){
			jasperwrapper.addParameter(
				"CURRENCY",
				JasperReportsUtil.getCurrencyDescription(getCtx(),
						invoice.getC_Currency_ID(), get_TrxName()));
			jasperwrapper.addParameter(
					"CURRENCY_SIMBOL",
					JasperReportsUtil.getCurrencySymbol(getCtx(),
						invoice.getC_Currency_ID(), get_TrxName()));
		}
		// Factura original
		if(!Util.isEmpty(invoice.getC_Invoice_Orig_ID(),true)){
			jasperwrapper.addParameter(
				"ORIGINAL_INVOICE",
				JasperReportsUtil.getInvoiceDisplay(getCtx(),
						invoice.getC_Invoice_Orig_ID(), get_TrxName()));
		}
		// Caja diaria
		if(!Util.isEmpty(invoice.getC_POSJournal_ID(),true)){
			MPOSJournal posJournal = new MPOSJournal(getCtx(),
				invoice.getC_POSJournal_ID(), get_TrxName());
			jasperwrapper.addParameter("POS_JOURNAL", JasperReportsUtil
				.getPODisplayByIdentifiers(getCtx(), posJournal,
						X_C_POSJournal.Table_ID, get_TrxName()));
		}
		// Región
		if(!Util.isEmpty(invoice.getC_Region_ID(),true)){
			jasperwrapper.addParameter(
				"INVOICE_REGION",
				JasperReportsUtil.getRegionName(getCtx(),
						invoice.getC_Region_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("CAE", invoice.getcae());
		jasperwrapper.addParameter("CAECBTE", invoice.getcaecbte());
		jasperwrapper.addParameter("CAEERROR", invoice.getcaeerror());
		jasperwrapper.addParameter("CAI", invoice.getCAI());
		jasperwrapper.addParameter("CHARGE_AMT", invoice.getChargeAmt());
		jasperwrapper.addParameter("DATE_CAI", invoice.getDateCAI());
		jasperwrapper.addParameter("INVOICE_DESCRIPTION", invoice.getDescription());
		jasperwrapper.addParameter("DOC_STATUS", invoice.getDocStatusName());
		jasperwrapper.addParameter("ID_CAE", invoice.getidcae());
		jasperwrapper.addParameter("SUBTYPE_CAE", docType.getdocsubtypecae());
		jasperwrapper.addParameter(
			"PRICE_LIST",
			JasperReportsUtil.getPriceListName(getCtx(),
					invoice.getM_PriceList_ID(), get_TrxName()));
		jasperwrapper.addParameter("OPEN_AMT", invoice.getOpenAmt());
		jasperwrapper.addParameter("TAXES_AMT", invoice.getTaxesAmt());
		// Contacto (AD_User_ID)
		if(!Util.isEmpty(invoice.getAD_User_ID(), true)) {
			MUser contact = new MUser(getCtx(), invoice.getAD_User_ID(),
				get_TrxName());
			// FB - Nuevos parámetros de contacto
			jasperwrapper.addParameter("CONTACT_NAME", contact.getName());
			jasperwrapper.addParameter("CONTACT_PHONE", contact.getPhone());
			jasperwrapper.addParameter("CONTACT_PHONE2", contact.getPhone2());
			jasperwrapper.addParameter("CONTACT_PHONE3", contact.getphone3());
			jasperwrapper.addParameter("CONTACT_FAX", contact.getFax());
			jasperwrapper.addParameter("CONTACT_EMAIL", contact.getEMail());
		}
		// Usuario 1
		if(!Util.isEmpty(invoice.getUser1_ID(),true)){
			jasperwrapper.addParameter(
				"USER1",
				JasperReportsUtil.getUserName(getCtx(),
						invoice.getUser1_ID(), get_TrxName()));
		}
		// Usuario 2
		if(!Util.isEmpty(invoice.getUser2_ID(),true)){
			jasperwrapper.addParameter(
					"USER2",
					JasperReportsUtil.getUserName(getCtx(),
							invoice.getUser2_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("VTO_CAE", invoice.getvtocae());
		jasperwrapper.addParameter("IS_APPROVED", invoice.isApproved());
		jasperwrapper.addParameter("AUTH_MATCH", invoice.isAuthMatch());
		jasperwrapper.addParameter("IS_CONFIRMED_ADDWORKS", invoice.isConfirmAditionalWorks());
		jasperwrapper.addParameter("IS_PAID", invoice.isPaid());
		jasperwrapper.addParameter("IS_PAY_SCHEDULE_VALID", invoice.isPayScheduleValid());
		jasperwrapper.addParameter("IS_POSTED", invoice.isPosted());
		jasperwrapper.addParameter("ISSOTRX", invoice.isSOTrx());
		jasperwrapper.addParameter("IS_TAX_INCLUDED", invoice.isTaxIncluded());
		jasperwrapper.addParameter("IS_TRANSFERRED", invoice.isTransferred());
		jasperwrapper.addParameter("PAST_DUE", MInvoice.isPastDue(getCtx(),
			invoice, new Date(), false, get_TrxName()) > 0);
		jasperwrapper.addParameter("PAST_DUE_AND_UNPAIDED", MInvoice.isPastDue(
			getCtx(), invoice, new Date(), true, get_TrxName()) > 0);
		jasperwrapper.addParameter("CLIENT",client.getName());
		
		// dREHER, cambie el llamado de estos metodos por el del cliente
		// que incluye la logica en cascada de busqueda org hoja, org carpeta, client (compania)
				
		String clientCUIT = client.getCUIT(invoice.getAD_Org_ID());
		jasperwrapper.addParameter("CLIENT_CUIT",clientCUIT);
		jasperwrapper.addParameter(
					"CLIENT_CATEGORIA_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(),
							client.getCategoriaIva(invoice.getAD_Org_ID()), get_TrxName()));
		
		jasperwrapper.addParameter("INGBRUTO_CLIENT", clientInfo.getIIBB());
		jasperwrapper.addParameter("IIBB_DATE_CLIENT", clientInfo.getIIBBDate());
		
		/* Codigo original
		jasperwrapper.addParameter("CLIENT_CUIT",clientInfo.getCUIT());
		jasperwrapper.addParameter(
			"CLIENT_CATEGORIA_IVA",
			JasperReportsUtil.getCategoriaIVAName(getCtx(),
					clientInfo.getC_Categoria_Iva_ID(), get_TrxName()));
		*/
		
		if(!Util.isEmpty(clientInfo.getC_Location_ID(), true)){
			MLocation clientLocation = MLocation.get(getCtx(), clientInfo.getC_Location_ID(), get_TrxName());
			jasperwrapper.addParameter("CLIENT_LOCATION_DESCRIPTION",
					JasperReportsUtil.formatLocation(getCtx(), clientInfo.getC_Location_ID(), true));
			jasperwrapper.addParameter("CLIENT_REGION",
					JasperReportsUtil.getRegionName(getCtx(), clientLocation.getC_Region_ID(), get_TrxName()));
			jasperwrapper.addParameter("CLIENT_CITY", clientLocation.getCity());
			jasperwrapper.addParameter("CLIENT_POSTAL", clientLocation.getPostal());
			jasperwrapper.addParameter("CLIENT_ADDRESS1", clientLocation.getAddress1());
		}
		
		jasperwrapper.addParameter(
				"ORG",
				JasperReportsUtil.getOrgName(getCtx(), invoice.getAD_Org_ID()));
		jasperwrapper.addParameter(
				"ORG_LOCATION_DESCRIPTION",
				JasperReportsUtil.getLocalizacion(getCtx(),
					invoice.getAD_Client_ID(), invoice.getAD_Org_ID(),
					get_TrxName()));
		BigDecimal percepcionTotalAmt = invoice.getPercepcionesTotalAmt();
		jasperwrapper.addParameter("PERCEPCION_TOTAL_AMT", percepcionTotalAmt);
		
		// Datos de Localización 
		MLocation loc = BPLocation.getLocation(false);
		if(!Util.isEmpty(invoice.getDireccion()))
			jasperwrapper.addParameter("LOC_ADDRESS1", invoice.getDireccion());
		else
			jasperwrapper.addParameter("LOC_ADDRESS1", loc.getAddress1());
		jasperwrapper.addParameter("LOC_ADDRESS2", loc.getAddress2());
		jasperwrapper.addParameter("LOC_ADDRESS3", loc.getAddress3());
		jasperwrapper.addParameter("LOC_ADDRESS4", loc.getAddress4());
		jasperwrapper.addParameter("LOC_PLAZA", loc.getPlaza());
		if(!Util.isEmpty(invoice.getLocalidad()))
			jasperwrapper.addParameter("LOC_CITY", invoice.getLocalidad());
		else
			jasperwrapper.addParameter("LOC_CITY", loc.getCity());
		if(!Util.isEmpty(invoice.getCP()))
			jasperwrapper.addParameter("LOC_POSTAL", invoice.getCP());
		else
			jasperwrapper.addParameter("LOC_POSTAL", loc.getPostal());
		if(!Util.isEmpty(invoice.getprovincia()))
			jasperwrapper.addParameter("LOC_REGION", invoice.getprovincia());
		else
			jasperwrapper.addParameter("LOC_REGION", loc.getC_Region_ID() > 0 ? loc.getRegion().getName() : "");
		jasperwrapper.addParameter("LOC_COUNTRY", loc.getC_City_ID() > 0 ? loc.getCountry().getName() : "");
		
		jasperwrapper.addParameter("BP_LOCATION_PHONE", BPLocation.getPhone());
		jasperwrapper.addParameter("BP_LOCATION_PHONE2", BPLocation.getPhone2());
		jasperwrapper.addParameter("BP_LOCATION_FAX", BPLocation.getFax());
		jasperwrapper.addParameter("BP_LOCATION_ISDN", BPLocation.getISDN());
		
		// Direccción de la Organización asociada a la Factura
		MOrg org = MOrg.get(getCtx(), invoice.getAD_Org_ID());
		MLocation locationOrg = new MLocation(getCtx(), org.getInfo().getC_Location_ID(), null);
		MRegion regionOrg = null;
		if (locationOrg.getC_Region_ID() > 0)
			regionOrg = new MRegion(getCtx(), locationOrg.getC_Region_ID(), null);
		jasperwrapper.addParameter(
			"DIRECCION_ORG",
			JasperReportsUtil.coalesce(locationOrg.getAddress1(), "")
					+ ". "
					+ JasperReportsUtil.coalesce(locationOrg.getCity(), "")
					+ ". ("
					+ JasperReportsUtil.coalesce(locationOrg.getPostal(), "")
					+ "). "
					+ JasperReportsUtil.coalesce(regionOrg == null ? ""
							: regionOrg.getName(), ""));
		
		// Parámetros de la retención aplicada a la Factura
		X_M_Retencion_Invoice retencion_invoice = getM_Retencion_Invoice(invoice);
		if (retencion_invoice != null){
			MRetencionSchema retencionSchema =  new MRetencionSchema(getCtx(), retencion_invoice.getC_RetencionSchema_ID(), null);
			// Nombre del Esquema de Retención
			jasperwrapper.addParameter("RET_SCHEMA_NAME", (retencionSchema.getName()));
			// Nombre del Tipo de Retención
			jasperwrapper.addParameter("RET_RETENTION_TYPE_NAME", (new MRetencionType(getCtx(), retencionSchema.getC_RetencionType_ID(), null).getName()) );
			// Monto de la Retención
			jasperwrapper.addParameter("RET_AMOUNT", retencion_invoice.getamt_retenc());
			// Se comenta ya que se debe tomar de los datos de la retención
			jasperwrapper.addParameter("RET_BASE_IMPONIBLE", retencion_invoice.getbaseimponible_amt());
			jasperwrapper.addParameter("RET_PERCENT", retencion_invoice.getretencion_percent());
			// Monto del Recibo
			//jasperwrapper.addParameter("RET_ALLOC_AMOUNT", allocation.getGrandTotal());
			MAllocationHdr allocation = new MAllocationHdr(getCtx(), retencion_invoice.getC_AllocationHdr_ID(), null);
			// Monto del pago actual
			jasperwrapper.addParameter("RET_ALLOC_AMOUNT", MAllocationHdr
					.getPayNetAmt(getCtx(), allocation, get_TrxName())); 
			// Comprobante/s que origina/n la retención. (Números de Documento de las facturas en el Recibo) 
			jasperwrapper.addParameter("RET_ALLOC_INVOICES", get_Retencion_Invoices(allocation));
		}
		
		// Código de barras de Factura Electrónica
		addElectronicInvoiceBarcode(jasperwrapper, invoice, docType, clientCUIT);
		
		// Código QR de Factura Electrónica
		addElectronicInvoiceQRCode(jasperwrapper, invoice, docType, clientCUIT);
		
		// Subreporte de otros tributos/percepciones
		MJasperReport perceptionSubreport = getPerceptionSubreport();
		if(perceptionSubreport != null && perceptionSubreport.getBinaryData() != null){
			jasperwrapper.addParameter("COMPILED_SUBREPORT_PERCEPCIONES", new ByteArrayInputStream(perceptionSubreport.getBinaryData()));
			InvoicePerceptionsDataSource perceptionDS = getPerceptionsDataSource(invoice);
			jasperwrapper.addParameter("SUBREPORT_PERCEPCIONES_DATASOURCE", perceptionDS);
			jasperwrapper.addParameter("PERCEPCION_TOTAL_AMT", perceptionDS.getTotalAmt());
		}
		
		// Importe Total en Moneda de Compañía
		jasperwrapper.addParameter("CLIENT_CURRENCY_GRAND_TOTAL", getClientCurrencyGrandTotal(invoice));

		// Tasa de Cambio
		jasperwrapper.addParameter("CURRENCY_RATE", getCurrencyRate(invoice));
		
		// Código de Comprobante
		jasperwrapper.addParameter("COD_CBANTE",
				MElectronicInvoice.getRefTablaComprobantes(getCtx(), docType.getID(), get_TrxName()));
		
		// Código de Comprobante
		jasperwrapper.addParameter("IMPORT_CLEARANCE", invoice.getImportClearance());
		
		// Impuesto 27%
		BigDecimal iva27Amt = getTaxAmt(invoice, new BigDecimal(27));
		jasperwrapper.addParameter("IVA_27", new BigDecimal(27));
		jasperwrapper.addParameter("SUBIVA_27", iva27Amt);
		
		// Otros impuestos
		BigDecimal allTaxesAmt = getTaxAmt(invoice, (BigDecimal)null);
		BigDecimal othersTaxesAmt = allTaxesAmt.subtract(iva105Amt).subtract(iva21Amt).subtract(iva27Amt)
				.subtract(percepcionTotalAmt);
		jasperwrapper.addParameter("OTHER_TAXES_AMT", othersTaxesAmt);
		
		// dREHER
		// Datos para facturas de exportacion
		
		String data = invoice.get_ValueAsString("Cintolo_Incoterm");
		jasperwrapper.addParameter("INCOTERM", data);
		
		data = invoice.get_ValueAsString("Cintolo_OrigenPro");
		jasperwrapper.addParameter("ORIGEN_PROCEDENCIA", data);
		
		data = invoice.get_ValueAsString("Cintolo_TipoMercaderia");
		jasperwrapper.addParameter("TIPO_MERCADERIA", data);
		
		// Datos de la entrega
		if(invoice.get_Value("Cintolo_Delivery_Location")!=null) {
			int C_Location_ID = (Integer)invoice.get_Value("Cintolo_Delivery_Location");
			if(C_Location_ID > 0) {
				MLocation lo = MLocation.get(getCtx(), C_Location_ID, get_TrxName());
				region = null;
				if (lo.getC_Region_ID() > 0)
					region = new MRegion(getCtx(), lo.getC_Region_ID(), get_TrxName());
				jasperwrapper.addParameter(
					"DIRECCION_ENTREGA",
					JasperReportsUtil.coalesce(lo.getAddress1(), "")
							+ ". "
							+ JasperReportsUtil.coalesce(lo.getCity(), "")
							+ ". ("
							+ JasperReportsUtil.coalesce(lo.getPostal(), "")
							+ "). "
							+ JasperReportsUtil.coalesce(region == null ? ""
									: region.getName(), ""));
				
			}
			
		}
		
		// Destino final
		if(invoice.get_Value("Cintolo_Final_Destine")!=null) {
			int C_Location_ID = (Integer)invoice.get_Value("Cintolo_Final_Destine");
			if(C_Location_ID > 0) {
				MLocation lo = MLocation.get(getCtx(), C_Location_ID, get_TrxName());
				region = null;
				if (lo.getC_Region_ID() > 0)
					region = new MRegion(getCtx(), lo.getC_Region_ID(), get_TrxName());
				jasperwrapper.addParameter(
						"DIRECCION_DESTINO",
						JasperReportsUtil.coalesce(lo.getAddress1(), "")
						+ ". "
						+ JasperReportsUtil.coalesce(lo.getCity(), "")
						+ ". ("
						+ JasperReportsUtil.coalesce(lo.getPostal(), "")
						+ "). "
						+ JasperReportsUtil.coalesce(region == null ? ""
								: region.getName(), ""));

			}

		}
		
		/**
		 * Agregar leyenda de impuestos agrupados en caso de que asi corresponda
		 * dREHER Mar 25
		 */
		if(invoice.isSOTrx() && Utils.isMostrarImpuestosFCB()) {
			jasperwrapper.addParameter("PREFIJO_IMPUESTOS", Utils.getPrefijoMostrarImpuestosFCB());
			jasperwrapper.addParameter("IVA_LEYENDA", Utils.getStringTotalInvoiceTaxes(invoice.getC_Invoice_ID(), true));
			jasperwrapper.addParameter("IMPUESTOS_LEYENDA", Utils.getStringTotalInvoiceTaxes(invoice.getC_Invoice_ID(), false));
		}
		
		
	}
	
	// CINTOLO. Obtiene el valor en dólares de la factura
	private BigDecimal getTotalUSD(MInvoice invoice) {
		if(invoice.getC_Currency_ID() == 100) return null;
		
		BigDecimal conversion;
		BigDecimal rate = invoice.get_Value("Cintolo_Exchange_Rate") != null ? 
				(BigDecimal)invoice.get_Value("Cintolo_Exchange_Rate") : Env.ZERO;
		
//		if(rate.equals(Env.ZERO)) { jviejo => al utilizar equals falla al comparar la escala
		if(rate.compareTo(Env.ZERO) == 0) {
			conversion = MCurrency.currencyConvert(
				invoice.getGrandTotal(), 
				invoice.getC_Currency_ID(), 
				100, 
				invoice.getDateInvoiced(), 
				invoice.getAD_Org_ID(), 
				getCtx());
		} else {
			conversion = invoice.getGrandTotal().divide(rate,RoundingMode.DOWN);
		}
		return conversion;
	}

	private BigDecimal getTaxAmt(MInvoice invoice, BigDecimal rate)
	{
		BigDecimal retValue = BigDecimal.ZERO;
		int size = invoice.getTaxes(true).length;
		boolean found = false;
		MTax tax = null;
		// buscar y matchear el tax correspondiente
		for (int i=0; i<size && !found; i++)
		{
			tax = new MTax(getCtx(), invoice.getTaxes(false)[i].getC_Tax_ID(), null);
			if (rate == null
					|| (tax.getRate().compareTo(rate) == 0 && tax.getName().toUpperCase().indexOf("IVA") >= 0))
			{
				found = rate != null;
				retValue = retValue.add(invoice.getTaxes(false)[i].getTaxAmt());
			}
		}
		return retValue;
	}

	private Timestamp getFechaVto(MInvoice invoice)
	{
		return invoice.getFechaVto();
	}	
	
	protected void initializeAmts(MInvoice invoice){
		for (MInvoiceLine invoiceLine : invoice.getLines()) {
			// Total de líneas con impuestos
			linesTotalAmt = linesTotalAmt.add(invoiceLine.getTotalPriceEnteredWithTax());
			// Total de líneas sin impuestos
			linesTotalNetAmt = linesTotalNetAmt.add(invoiceLine.getTotalPriceEnteredNet());
			// Total de bonificaciones con impuestos
			linesTotalBonusAmt = linesTotalBonusAmt.add(invoiceLine.getTotalBonusUnityAmtWithTax());
			// Total de bonificaciones sin impuestos
			linesTotalBonusNetAmt = linesTotalBonusNetAmt.add(invoiceLine.getTotalBonusUnityAmtNet());
			// Total de descuento de línea con impuestos
			linesTotalLineDiscountsAmt = linesTotalLineDiscountsAmt.add(invoiceLine.getTotalLineDiscountUnityAmtWithTax());
			// Total de descuento de línea sin impuestos
			linesTotalLineDiscountsNetAmt = linesTotalLineDiscountsNetAmt.add(invoiceLine.getTotalLineDiscountUnityAmtNet());
			// Total de descuento de documento con impuestos
			linesTotalDocumentDiscountsAmt = linesTotalDocumentDiscountsAmt.add(invoiceLine.getTotalDocumentDiscountUnityAmtWithTax());
			// Total de descuento de documento sin impuestos
			linesTotalDocumentDiscountsNetAmt = linesTotalDocumentDiscountsNetAmt.add(invoiceLine.getTotalDocumentDiscountUnityAmtNet());
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
	
	/*
	 * El método retorna una Retencion_Invoice a partir del la factura
	 */
	private X_M_Retencion_Invoice getM_Retencion_Invoice(MInvoice invoice)
	{
		try 
		{
			int m_Retencion_InvoiceID = 0;
			PreparedStatement stmt = DB.prepareStatement("SELECT m_retencion_invoice_id FROM M_Retencion_Invoice WHERE c_invoice_id = ? ORDER BY m_retencion_invoice_id DESC");
			stmt.setInt(1, invoice.getC_Invoice_ID());
			ResultSet rs = stmt.executeQuery();
			if (!rs.next() || rs.getInt(1) == 0)
				return null;
			
			m_Retencion_InvoiceID = rs.getInt(1);
			X_M_Retencion_Invoice m_Retencion_Invoice = new X_M_Retencion_Invoice(getCtx(), m_Retencion_InvoiceID, null);
			return m_Retencion_Invoice;	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * El método retorna todos los DocumentNo. de las facturas asociadas al recibo recibido por parámetro. 
	 */
	private String get_Retencion_Invoices(MAllocationHdr allocation)
	{
		try 
		{
			PreparedStatement stmt = DB.prepareStatement("SELECT DISTINCT factura FROM C_Allocation_Detail_V WHERE C_AllocationHdr_ID = ? ORDER BY factura DESC");
			stmt.setInt(1, allocation.getC_AllocationHdr_ID());
			ResultSet rs = stmt.executeQuery();
			
			String invoices = "- ";
			while (rs.next()){
				invoices = invoices.concat(rs.getString(1).concat(" - "));	
			}
				
			return invoices;	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Agrega la imagen de código de barras de FE como parámetro al reporte si
	 * es que la factura es electrónica
	 * 
	 * @param jasperWrapper
	 *            reporte jasper
	 * @param invoice
	 *            factura
	 * @param docType
	 *            tipo de documento de la factura
	 * @param clientCUIT
	 *            cuit emisora
	 */
	protected void addElectronicInvoiceBarcode(MJasperReport jasperWrapper, MInvoice invoice, MDocType docType, String clientCUIT){
		if(MDocType.isElectronicDocType(invoice.getC_DocTypeTarget_ID())){
			FacturaElectronicaBarcodeGenerator feBarcodeGenerator = new FacturaElectronicaBarcodeGenerator(invoice, docType, clientCUIT);
			if (feBarcodeGenerator.isDisplayed()) { 
				jasperWrapper.addParameter("FACTURA_ELECTRONICA_BARCODE", feBarcodeGenerator.getBarcodeImage(true));
				jasperWrapper.addParameter("FACTURA_ELECTRONICA_CODE", feBarcodeGenerator.getCode());
			}
		}
	}
	
	/**
	 * Agrega la imagen de código QR de FE como parámetro al reporte si
	 * es que la factura es electrónica
	 * 
	 * @param jasperWrapper
	 *            reporte jasper
	 * @param invoice
	 *            factura
	 * @param docType
	 *            tipo de documento de la factura
	 * @param clientCUIT
	 *            cuit emisora
	 */
	protected void addElectronicInvoiceQRCode(MJasperReport jasperWrapper, MInvoice invoice, MDocType docType, String clientCUIT){
		if(MDocType.isElectronicDocType(invoice.getC_DocTypeTarget_ID())){
			FacturaElectronicaQRCodeGenerator feQRCodeGenerator = new FacturaElectronicaQRCodeGenerator(invoice, docType, clientCUIT);
			if (feQRCodeGenerator.isDisplayed()) {
				jasperWrapper.addParameter("FACTURA_ELECTRONICA_QRCODE", feQRCodeGenerator.getQRCode());
			}
		}
	}
	
	protected MJasperReport getJasperReport(String name) throws Exception {
		Integer jasperReport_ID = (Integer) DB
				.getSQLObject(
						get_TrxName(),
						"SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE Name ilike ?",
						new Object[] { name });
		if (jasperReport_ID == null || jasperReport_ID == 0)
			throw new Exception("Jasper Report not found - " + name);

		MJasperReport jasperReport = new MJasperReport(getCtx(), jasperReport_ID, get_TrxName());
		return jasperReport;
	}
	
	protected MJasperReport getPerceptionSubreport() throws Exception {
		return getJasperReport("Factura Electronica - Subreporte");
	}
	
	protected InvoicePerceptionsDataSource getPerceptionsDataSource(MInvoice invoice) throws Exception{
		InvoicePerceptionsDataSource ds = new InvoicePerceptionsDataSource(getCtx(), invoice.getID(), get_TrxName());
		ds.loadData();
		return ds;
	}
	
	/** 
	 * Importe Total en Moneda de Compañía: es el importe Total de la Factura convertido a Moneda de la Compañía según la Tasa de Cambio configurada para la Fecha Contable de la factura
	 */
	protected BigDecimal getClientCurrencyGrandTotal(MInvoice invoice) {
		return MCurrency.currencyConvert(
				invoice.getGrandTotal(),
				invoice.getC_Currency_ID(),
				MClient.get(getCtx()).getC_Currency_ID(),
				invoice.getDateAcct(),
				invoice.getAD_Org_ID(),
				getCtx());
	}
	
	/**
	 * Tasa de Cambio: es la tasa de cambio configurada para la moneda de la factura y la moneda de la Compañía, en la fecha Contable de la Factura.
	 */
	protected BigDecimal getCurrencyRate(MInvoice invoice) {
		return MCurrency.currencyConvert(
				BigDecimal.ONE,
				invoice.getC_Currency_ID(),
				MClient.get(getCtx()).getC_Currency_ID(),
				invoice.getDateAcct(),
				invoice.getAD_Org_ID(),
				getCtx());	
	}
	
	/**
	 * @param invoice factura
	 * @return pedido relacionado a la factura desde las líneas
	 */
	protected MOrder getMOrderByLines(MInvoice invoice) {
		MOrder order = null;
		String sql = "select ol.c_order_id "
					+ "from c_invoiceline il "
					+ "join c_orderline ol on il.c_orderline_id = ol.c_orderline_id "
					+ "where il.c_invoice_id = ? limit 1";
		Integer orderID = DB.getSQLValue(get_TrxName(), sql, invoice.getID());
		if(orderID > 0) {
			order = new MOrder(getCtx(), orderID, get_TrxName());
		}
		return order;
	}
	
	/**
	 * @param invoice factura
	 * @return remito relacionado a la factura desde las líneas
	 */
	protected MInOut getMInOutByLines(MInvoice invoice) {
		MInOut io = null;
		String sql = "select iol.m_inout_id "
					+ "from c_invoiceline il "
					+ "join m_inoutline iol on iol.m_inoutline_id = il.m_inoutline_id "
					+ "where il.c_invoice_id = ? limit 1";
		Integer ioID = DB.getSQLValue(get_TrxName(), sql, invoice.getID());
		if(ioID > 0) {
			io = new MInOut(getCtx(), ioID, get_TrxName());
		}
		return io;
	}
} 
