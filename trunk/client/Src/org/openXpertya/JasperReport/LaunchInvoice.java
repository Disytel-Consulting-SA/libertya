package org.openXpertya.JasperReport;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.openXpertya.JasperReport.DataSource.InvoiceDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MUser;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
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
	
	/** Tipo de impresion		*/
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
		
        ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( para[ i ].getParameter() == null ) ;
            else 
            if( name.equalsIgnoreCase( "TipoDeImpresion" )) {
            	printType = (String)para[ i ].getParameter();
            }
        }
		
	}
	
	@Override
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport()	throws Exception{
		
		MInvoice invoice = new MInvoice(getCtx(), AD_Record_ID, null);
		MBPartner bpartner = new MBPartner(getCtx(), invoice.getC_BPartner_ID(), null);
		
		//Ader mejora de caches
		invoice.initCaches();
		
		MJasperReport jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
		
		InvoiceDataSource ds = new InvoiceDataSource(getCtx(), invoice);
		
		 try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
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
			jasperwrapper.addParameter("FECHA", invoice.getDateInvoiced());
			jasperwrapper.addParameter("RAZONSOCIAL", JasperReportsUtil.coalesce(invoice.getNombreCli(), bpartner.getName()) ); 
			jasperwrapper.addParameter("RAZONSOCIAL2", JasperReportsUtil.coalesce(bpartner.getName2(), "") );
			jasperwrapper.addParameter("CODIGO", bpartner.getValue());
			jasperwrapper.addParameter(
				"DIRECCION",
				JasperReportsUtil.coalesce(location.getAddress1(), "")
						+ ". "
						+ JasperReportsUtil.coalesce(location.getCity(), "")
						+ ". ("
						+ JasperReportsUtil.coalesce(location.getPostal(), "")
						+ "). "
						+ JasperReportsUtil.coalesce(region == null ? ""
								: region.getName(), ""));
			jasperwrapper.addParameter("CIUDAD", JasperReportsUtil.coalesce(location.getCity(),""));
			jasperwrapper.addParameter("PAIS", JasperReportsUtil.coalesce(location.getCountry().getName(),""));
			if(!Util.isEmpty(bpartner.getC_Categoria_Iva_ID(), true)){
				jasperwrapper.addParameter(
					"TIPO_IVA",
					JasperReportsUtil.getCategoriaIVAName(getCtx(),
							bpartner.getC_Categoria_Iva_ID(), get_TrxName()));
			}			
			jasperwrapper.addParameter("CUIT", JasperReportsUtil.coalesce(bpartner.getTaxID(),""));
			jasperwrapper.addParameter("INGBRUTO", bpartner.getIIBB());
			jasperwrapper.addParameter("VENDEDOR", (new MUser(getCtx(), invoice.getSalesRep_ID(), null).getName()) );
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
					jasperwrapper.addParameter("NROREMITO_"+i, inoutsIt.next());
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
			jasperwrapper.addParameter("TIPOORIGEN", printType );
			
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
			jasperwrapper.addParameter("CLIENT_CUIT",clientInfo.getCUIT());
			jasperwrapper.addParameter(
				"CLIENT_CATEGORIA_IVA",
				JasperReportsUtil.getCategoriaIVAName(getCtx(),
						clientInfo.getC_Categoria_Iva_ID(), get_TrxName()));
			jasperwrapper.addParameter(
					"ORG",
					JasperReportsUtil.getOrgName(getCtx(), invoice.getAD_Org_ID()));
			jasperwrapper.addParameter(
					"ORG_LOCATION_DESCRIPTION",
					JasperReportsUtil.getLocalizacion(getCtx(),
						invoice.getAD_Client_ID(), invoice.getAD_Org_ID(),
						get_TrxName()));
			
			try {
				jasperwrapper.fillReport(ds);
				jasperwrapper.showReport(getProcessInfo());
			}
				
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se ha podido rellenar el informe.", e);
			}
			
			return "doIt";
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
}
