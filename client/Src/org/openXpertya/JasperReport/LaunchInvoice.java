package org.openXpertya.JasperReport;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.openXpertya.JasperReport.DataSource.InvoiceDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoicePaySchedule;
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
import org.openXpertya.util.Util;

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
		
		InvoiceDataSource ds = new InvoiceDataSource(getCtx(), invoice);
		
		 try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
			// Agrego los parámetros al reporte
			addReportParameter(jasperwrapper, invoice, bpartner);
			
			try {
				jasperwrapper.fillReport(ds);
				jasperwrapper.showReport(getProcessInfo());
			}
				
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se ha podido rellenar el informe.", e);
			}
			
			return "";
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


		// Descuentos aplicados totales
		jasperwrapper.addParameter("NROCOMPROBANTE", invoice.getNumeroDeDocumento());
		jasperwrapper.addParameter("TIPOCOMPROBANTE", JasperReportsUtil
			.getDocTypeName(getCtx(), invoice.getC_DocTypeTarget_ID(),
					"FACTURA", get_TrxName()));
		
		jasperwrapper.addParameter("DOCTYPEKEY", (new MDocType(getCtx(), invoice.getC_DocTypeTarget_ID(), get_TrxName())).getDocTypeKey());
				
		jasperwrapper.addParameter("FECHA", invoice.getDateInvoiced());
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(invoice.getDateInvoiced().getTime());
		jasperwrapper.addParameter("DIA", Integer.toString(c.get(Calendar.DATE)));
		jasperwrapper.addParameter("MES", Integer.toString(c.get(Calendar.MONTH)+1)); // Mas 1 porque el Calendar maneja los meses de 0 a 11
		jasperwrapper.addParameter("ANIO", Integer.toString(c.get(Calendar.YEAR)));		
		jasperwrapper.addParameter("RAZONSOCIAL", JasperReportsUtil.coalesce(invoice.getNombreCli(), bpartner.getName()) ); 
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
		if(!Util.isEmpty(bpartner.getC_Categoria_Iva_ID(), true)){
			jasperwrapper.addParameter(
				"TIPO_IVA",
				JasperReportsUtil.getCategoriaIVAName(getCtx(),
						bpartner.getC_Categoria_Iva_ID(), get_TrxName()));
		}
		jasperwrapper.addParameter("LETRA", JasperReportsUtil.coalesce(invoice.getLetra(),""));
		jasperwrapper.addParameter("CUIT", JasperReportsUtil.coalesce(bpartner.getTaxID(),""));
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
		jasperwrapper.addParameter("NRODOCORIG", JasperReportsUtil.coalesce(invoice.getPOReference(), "") );
		if(!Util.isEmpty(invoice.getC_Order_ID(), true)){
			jasperwrapper.addParameter("NRO_OC", JasperReportsUtil.coalesce(
				(new MOrder(getCtx(), invoice.getC_Order_ID(),
						get_TrxName())).getDocumentNo(), ""));
		} 
		String fechaVto = (String)JasperReportsUtil.coalesce(getFechaVto(invoice), invoice.getDateInvoiced().toString());
		fechaVto = fechaVto.substring(0, 11); // quitamos la hora del string
		jasperwrapper.addParameter("VCTO", "Vencimiento: " +  fechaVto);			
		jasperwrapper.addParameter("CODVTA", JasperReportsUtil.getListName(
			getCtx(), X_C_Invoice.PAYMENTRULE_AD_Reference_ID,
			invoice.getPaymentRule()));
		try{
			Set<String> inouts = JasperReportsUtil.getInOutsDocumentsNo(
				getCtx(), invoice, get_TrxName());
			Iterator<String> inoutsIt = inouts.iterator();
			int i = 1;
			while (inoutsIt.hasNext()) {
				jasperwrapper.addParameter("NROREMITO_"+(i++), inoutsIt.next());
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
		jasperwrapper.addParameter("SUBIVA_105", getTaxAmt(invoice, new BigDecimal(10.5)) );
		jasperwrapper.addParameter("SUBIVA_21", getTaxAmt(invoice, new BigDecimal(21)) );
		jasperwrapper.addParameter("SUBDESC", invoice.getDiscountsAmt());
		jasperwrapper.addParameter("TOTAL", invoice.getGrandTotal() );
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
				
				
		jasperwrapper.addParameter("CLIENT_CUIT",client.getCUIT(invoice.getAD_Org_ID()));
		jasperwrapper.addParameter(
					"CLIENT_CATEGORIA_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(),
							client.getCategoriaIva(invoice.getAD_Org_ID()), get_TrxName()));
		
		/* Codigo original
		jasperwrapper.addParameter("CLIENT_CUIT",clientInfo.getCUIT());
		jasperwrapper.addParameter(
			"CLIENT_CATEGORIA_IVA",
			JasperReportsUtil.getCategoriaIVAName(getCtx(),
					clientInfo.getC_Categoria_Iva_ID(), get_TrxName()));
		*/
		
		jasperwrapper.addParameter(
				"ORG",
				JasperReportsUtil.getOrgName(getCtx(), invoice.getAD_Org_ID()));
		jasperwrapper.addParameter(
				"ORG_LOCATION_DESCRIPTION",
				JasperReportsUtil.getLocalizacion(getCtx(),
					invoice.getAD_Client_ID(), invoice.getAD_Org_ID(),
					get_TrxName()));
		jasperwrapper.addParameter("PERCEPCION_TOTAL_AMT",
			invoice.getPercepcionesTotalAmt());
		
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

			MAllocationHdr allocation = new MAllocationHdr(getCtx(), retencion_invoice.getC_AllocationHdr_ID(), null);
			// Monto del Recibo
			jasperwrapper.addParameter("RET_ALLOC_AMOUNT", allocation.getGrandTotal());
			// Comprobante/s que origina/n la retención. (Números de Documento de las facturas en el Recibo) 
			jasperwrapper.addParameter("RET_ALLOC_INVOICES", get_Retencion_Invoices(allocation));	
		}
	}
	
	
	private BigDecimal getTaxAmt(MInvoice invoice, BigDecimal rate)
	{
		BigDecimal retValue = new BigDecimal(0);
		int size = invoice.getTaxes(true).length;
		boolean found = false;
		MTax tax = null;
		// buscar y matchear el tax correspondiente
		for (int i=0; i<size && !found; i++)
		{
			tax = new MTax(getCtx(), invoice.getTaxes(false)[i].getC_Tax_ID(), null);
			if (tax.getRate().compareTo(rate) == 0 && tax.getName().toUpperCase().indexOf("IVA") >= 0 )
			{
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
}
