package org.openXpertya.JasperReport.DataSource;

import java.awt.print.PrinterJob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.openXpertya.model.MActivity;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCampaign;
import org.openXpertya.model.MCategoriaIva;
import org.openXpertya.model.MCharge;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPaymentTerm;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MShipper;
import org.openXpertya.model.MUser;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.print.CPrinter;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfo.JasperReportDTO;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Util;

public class JasperReportsUtil {

	public JasperReportsUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Obtiene la configuración del entorno en base a si se debe previsualizar
	 * las impresiones o imprimir directamente hacia la impresora
	 * 
	 * @param ctx
	 *            contexto actual
	 * @return true si se debe previsualizar la impresión, false caso contrario
	 */
	public static boolean isPrintPreview(Properties ctx){
		return Env.getContext(ctx, "#"+Ini.P_PRINTPREVIEW).equals("Y");
	}
	
	/**
	 * Determina si se debe realiza vista previa de la impresión jasper. Si es
	 * impresión de documento entonces se verifica vista previa de impresión en
	 * el propio tipo de documento, sino se toma la configuración de las
	 * Preferencias
	 * 
	 * @param ctx
	 *            contexto
	 * @param info
	 *            info del proceso
	 * @return true si se debe realizar vista previa de la impresión jasper o va
	 *         directo
	 */
    public static boolean isPrintPreview(Properties ctx, ProcessInfo pi){
		JasperReportDTO jasperDTO = pi != null ? pi.getJasperReportDTO() : null;
    	boolean printPreview = true;
		// Si existe el dto de jasper, verifico si el print preview del tipo de
		// documento configurado si es que existe, sino tomo el de Preferencias
    	if(jasperDTO != null){
    		if(!Util.isEmpty(jasperDTO.getDocTypeID(), true)){
				MDocType docType = new MDocType(ctx, jasperDTO.getDocTypeID(), null);
				printPreview = docType.isPrintPreview();
    		}
    		else{
    			printPreview = JasperReportsUtil.isPrintPreview(ctx);
    		}
    	}
		// Sino verifico el print preview de Preferencias
    	else{
    		printPreview = JasperReportsUtil.isPrintPreview(ctx);
    	}
    	return printPreview;
    }
	
	
	public static void printJasperReport(Properties ctx, JasperPrint jasperPrint, ProcessInfo info, boolean showDialog) throws Exception{
		// Datos de la impresión jasper
		JasperReportDTO jasperDTO = info.getJasperReportDTO();
		String jobName = Util.isEmpty(info.getTitle(), true) ? "Impresion"
				: info.getTitle();
		int copies = 1;
		String printerName = null;
		PrinterJob job = null;
		PrintService service = null;
		if(jasperDTO != null){
			// Si hay un tipo de documento lo obtengo
			if(!Util.isEmpty(jasperDTO.getDocTypeID(), true)){
				MDocType docType = new MDocType(ctx, jasperDTO.getDocTypeID(), null);
				jobName = docType.getName()
						+ (Util.isEmpty(jasperDTO.getDocumentNo(), true) ? ""
								: "_" + jasperDTO.getDocumentNo());
				copies = docType.getDocumentCopies();
				printerName = docType.getPrinterName();
			}
			copies = !Util.isEmpty(jasperDTO.getNumCopies(), true) ? jasperDTO
					.getNumCopies() : copies;
		}		
		// Si las copias del documento son 0, entonces es 1
		if(copies <= 0){
			copies = 1;
		}
		
		// Trabajo de Impresión
		job = CPrinter.getPrinterJob(printerName);
    	service = job.getPrintService();
		
    	// Atributos de impresión
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(new Copies(copies));
    	printRequestAttributeSet.add(new JobName(jobName,null));
    	
    	// Exportador de Jasper
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE,
				service);
		exporter.setParameter(
				JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
				service.getAttributes());
		exporter.setParameter(
				JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
				Boolean.FALSE);
		exporter.setParameter(
				JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
				showDialog);
    	exporter.setParameter(
				JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,
				printRequestAttributeSet);
    	exporter.exportReport();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param clientID
	 *            id de compañía
	 * @return compañía
	 */
	public static MClient getClient(Properties ctx, Integer clientID){
		return MClient.get(ctx, clientID);
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param clientID
	 *            id de compañía
	 * @return información de la compañía
	 */
	public static MClientInfo getClientInfo(Properties ctx, Integer clientID, String trxName){
		return MClientInfo.get(ctx, clientID, trxName);
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param clientID
	 *            id de compañía
	 * @return nombre de la compañía
	 */
	public static String getClientName(Properties ctx, Integer clientID){
		return MClient.get(ctx, clientID).getName();
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param orgID
	 *            id de organización
	 * @return nombre de la organización
	 */
	public static String getOrgName(Properties ctx, Integer orgID){
		return MOrg.get(ctx, orgID).getName();
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param bpartnerID
	 *            id de entidad comercial
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre de la entidad comercial
	 */
	public static String getBPartnerName(Properties ctx, Integer bpartnerID, String trxName){
		return new MBPartner(ctx, bpartnerID, trxName).getName();
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param projectID
	 *            id de proyecto
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre de proyecto
	 */
	public static String getProjectName(Properties ctx, Integer projectID, String trxName){
		return new MProject(ctx, projectID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param activityID
	 *            id de actividad
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre de la actividad
	 */
	public static String getActivityName(Properties ctx, Integer activityID, String trxName){
		return new MActivity(ctx, activityID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param campaignID
	 *            id de campaña
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre de la campaña
	 */
	public static String getCampaignName(Properties ctx, Integer campaignID, String trxName){
		return new MCampaign(ctx, campaignID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param chargeID
	 *            id de cargo
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre del cargo
	 */
	public static String getChargeName(Properties ctx, Integer chargeID, String trxName){
		return new MCharge(ctx, chargeID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param currencyID
	 *            id de la moneda
	 * @param trxName
	 *            nombre de la transacción
	 * @return descripción de la moneda: iso_descripción
	 */
	public static String getCurrencyDescription(Properties ctx, Integer currencyID, String trxName){
		MCurrency currency = new MCurrency(ctx, currencyID, trxName);
		return currency.getISO_Code()+"_"+currency.getDescription();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param currencyID
	 *            id de la moneda
	 * @param trxName
	 *            nombre de la transacción
	 * @return símbolo de la moneda
	 */
	public static String getCurrencySymbol(Properties ctx, Integer currencyID, String trxName){
		MCurrency currency = new MCurrency(ctx, currencyID, trxName);
		return currency.getCurSymbol();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param userID
	 *            id de usuario
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre del usuario parámetro
	 */
	public static String getUserName(Properties ctx, Integer userID, String trxName){
		return new MUser(ctx, userID, trxName).getName();
	}
	

	/**
	 * Si el objeto no es null, devuelvo ese objeto. Caso contrario si es null,
	 * devuelvo el valor por defecto.
	 * 
	 * @param object
	 *            objeto a verificar
	 * @param defValue
	 *            valor por defecto
	 * @return el objeto si no es null, caso contrario el valor por defecto
	 */
	public static Object coalesce(Object object, Object defValue){
		if (object == null)
			return defValue;
		return object;
	}

	/**
	 * Determina el nombre del tipo de documento. El nombre por defecto
	 * parámetro se utiliza cuando no se encuentra el tipo de documento
	 * parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param docTypeID
	 *            id de tipo de documento o null
	 * @param defaultDocTypeName
	 *            nombre por defecto a retornar si el tipo de documento no se
	 *            encuentra
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre del tipo de documento
	 */
	public static String getDocTypeName(Properties ctx, Integer docTypeID, String defaultDocTypeName, String trxName) {
		String tipoCbante = defaultDocTypeName;
		if(!Util.isEmpty(docTypeID, true)){
			MDocType docType = MDocType.get(ctx, docTypeID);
			if (docType != null)
				tipoCbante = docType.getPrintName();
		}
		return tipoCbante.toUpperCase();
	}

	/**
	 * Retorna la descripción del PO parámetro a partir de las columnas
	 * identificadoras
	 * 
	 * @param ctx
	 *            contexto
	 * @param po
	 *            PO con los valores
	 * @param tableID
	 *            id de la tabla del PO
	 * @param trxName
	 *            nombre de la transacción
	 * @return display del PO con los valores de las columnas identificadoras
	 */
	public static String getPODisplayByIdentifiers(Properties ctx, PO po, Integer tableID, String trxName){
		return DisplayUtil.getDisplayByIdentifiers(ctx, po, tableID, trxName);
	}
	
	/**
	 * Obtengo el display de la factura a partir de las columnas identificadoras
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoiceID
	 *            id de factura
	 * @param trxName
	 *            nombre de transacción
	 * @return descripción de la factura a partir de los identificadores
	 */
	public static String getInvoiceDisplay(Properties ctx, Integer invoiceID, String trxName){
		return getPODisplayByIdentifiers(ctx, new MInvoice(ctx, invoiceID,
				trxName), X_C_Invoice.Table_ID, trxName);
	}

	/**
	 * Obtengo el display del pedido a partir de las columnas identificadoras
	 * 
	 * @param ctx
	 *            contexto
	 * @param orderID
	 *            id del pedido
	 * @param trxName
	 *            nombre de transacción
	 * @return descripción del pedido a partir de los identificadores
	 */
	public static String getOrderDisplay(Properties ctx, Integer orderID, String trxName){
		return getPODisplayByIdentifiers(ctx, new MOrder(ctx, orderID,
				trxName), X_C_Order.Table_ID, trxName);
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param paymentTermID
	 *            id de esquema de vencimientos
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre del esquema de vencimientos
	 */
	public static String getPaymentTermName(Properties ctx, Integer paymentTermID, String trxName){
		return new MPaymentTerm(ctx, paymentTermID, trxName).getName();
	}

	/**
	 * Nombre del valor de la lista parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param referenceID
	 *            id de la referencia
	 * @param value
	 *            valor de la lista
	 * @return Nombre del valor de la lista parámetro
	 */
	public static String getListName(Properties ctx, Integer referenceID, String value){
		String name = "";
		if(!Util.isEmpty(value)){
			name = MRefList.getListName(ctx, referenceID, value);
		}
		return name;
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param priceListID
	 *            id de tarifa
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre de la tarifa
	 */
	public static String getPriceListName(Properties ctx, Integer priceListID, String trxName){
		return MPriceList.get(ctx, priceListID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param shipperID
	 *            id de transporte
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre del transporte
	 */
	public static String getShipperName(Properties ctx, Integer shipperID, String trxName){
		return new MShipper(ctx, shipperID, trxName).getName();
	}

	/**
	 * Arma la descripción de la información del transportista. Queda: Nombre de
	 * transportista + localización de la entidad comercial relacionada al
	 * transportista
	 * 
	 * @param ctx
	 *            contexto
	 * @param shipperID
	 *            id del transportista
	 * @param trxName
	 *            nombre de transacción en curso
	 * @return descripción del transportista: Nombre + localización de la
	 *         entidad comercial relacionada
	 */
	public static String getShipperData(Properties ctx, Integer shipperID, String trxName){
		String res = "";
		MShipper shipper = new MShipper(ctx, shipperID, trxName); 
		MBPartner shipperBP = null;
		MLocation location = null;
		// nombre del transporte
		res += shipper.getName() + ". ";		
		// localizacion
		if (shipper.getC_BPartner_ID() > 0)	{
			shipperBP = new MBPartner(ctx, shipper.getC_BPartner_ID(), trxName);			
			MBPartnerLocation BPLocation = (MBPartnerLocation.getForBPartner(
					ctx, shipper.getC_BPartner_ID()))[0];			
			if (BPLocation != null) {
				location = new MLocation(ctx, BPLocation.getC_Location_ID(), trxName);
				res += JasperReportsUtil.coalesce(location.getAddress1(), "")
						+ ". "
						+ JasperReportsUtil.coalesce(location.getRegionName(),
								"") + ". ";				
			}			
			// cuit
			res += JasperReportsUtil.coalesce(shipperBP.getTaxID(),"");
		}		
		return res;
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param warehouseID
	 *            id de almacén
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre del almacén
	 */
	public static String getWarehouseName(Properties ctx, Integer warehouseID, String trxName){
		return new MWarehouse(ctx, warehouseID, trxName).getName();
	}
	
	/**
	 * @param ctx
	 *            contexto
	 * @param warehouseID
	 *            id de almacén
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return nombre del almacén
	 */
	public static String getCategoriaIVAName(Properties ctx, Integer categoriaIVAID, String trxName){
		return new MCategoriaIva(ctx, categoriaIVAID, trxName).getName();
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param regionID
	 *            id de región
	 * @param trxName
	 *            nombre de la transacción
	 * @return nombre de la región
	 */
	public static String getRegionName(Properties ctx, Integer regionID, String trxName){
		return new MRegion(ctx, regionID, trxName).getName();
	}

	/**
	 * Obtengo los nros de documento de los remitos relacionados con la factura
	 * parámetro. Si no encuentro relacionados a ella y la factura posee un
	 * pedido relacionado entonces busco los remitos relacionados a ese pedido.
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura
	 * @param trxName
	 *            trx actual
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getInOutsDocumentsNo(Properties ctx, MInvoice invoice, String trxName) throws Exception{
		Set<String> inoutsDocsNo = new HashSet<String>();
		String sql = null;
		Object[] params = new Object[] { invoice.getID() };
		// Si es issotrx = 'N' se debe verificar en MMatchInv, como último el
		// remito puede estar relacionado a la factura misma 
		if(!invoice.isSOTrx()){
			sql = "SELECT distinct io.documentno " +
						 "FROM m_matchinv as minv " +
						 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = minv.m_inoutline_id) " +
						 "INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = minv.c_invoiceline_id) " +
						 "INNER JOIN m_inout as io ON (io.m_inout_id = iol.m_inout_id) " + 
						 "WHERE il.c_invoice_id = ? AND io.docstatus IN ('CO','CL')";
			inoutsDocsNo.addAll(getSetOfStringByQuery(sql, params, trxName));
		}
		
		// Obtener el remito directamente desde la factura misma, para los casos
		// también de issotrx = 'Y'
		sql = "SELECT distinct documentno FROM m_inout WHERE c_invoice_id = ? AND docstatus IN ('CO','CL')";
		inoutsDocsNo.addAll(getSetOfStringByQuery(sql, params, trxName));
		
		// Si no pude obtener ningún remito, entonces veo por el lado del pedido
		// de la factura
		if (inoutsDocsNo.isEmpty()
				&& !Util.isEmpty(invoice.getC_Order_ID(), true)) {
			inoutsDocsNo.addAll(getInOutsDocumentsNo(ctx, new MOrder(ctx,
					invoice.getC_Order_ID(), trxName), trxName));			
		}
		
		return inoutsDocsNo;
	}

	/**
	 * Obtengo los nros de documento de los remitos relacionados con el pedido
	 * parámetro.
	 * 
	 * @param ctx
	 *            contexto
	 * @param order
	 *            pedido
	 * @param trxName
	 *            trx actual
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getInOutsDocumentsNo(Properties ctx, MOrder order, String trxName) throws Exception{
		Set<String> inoutsDocsNo = new HashSet<String>();
		String sql = null;
		Object[] params = new Object[] { order.getID() };
		// Si es issotrx = 'N' se debe verificar en MMatchPO, como último el
		// remito puede estar relacionado al pedido mismo 
		if(!order.isSOTrx()){
			sql = "SELECT distinct io.documentno " +
					 "FROM m_matchpo as mpo " +
					 "INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id) " +
					 "INNER JOIN c_orderline as ol ON (ol.c_orderline_id = mpo.c_orderline_id) " +
					 "INNER JOIN m_inout as io ON (io.m_inout_id = iol.m_inout_id) " + 
					 "WHERE ol.c_order_id = ? AND io.docstatus IN ('CO','CL')";
			inoutsDocsNo.addAll(getSetOfStringByQuery(sql, params, trxName));
		}
		
		// Obtener el remito directamente desde el pedido mismo, para los casos
		// también de issotrx = 'Y'
		sql = "SELECT distinct documentno FROM m_inout WHERE c_order_id = ? AND docstatus IN ('CO','CL')";
		inoutsDocsNo.addAll(getSetOfStringByQuery(sql, params, trxName));
		return inoutsDocsNo;
	}
	
	/**
	 * Ejecuta la query parámetro y obtiene la lista de strings de la consulta
	 * 
	 * @param query
	 *            consulta
	 * @param parameters
	 *            parametros de la consulta
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return lista de valores de la columna string resultante de la query
	 * @throws Exception
	 */
	public static Set<String> getSetOfStringByQuery(String query, Object[] parameters, String trxName) throws Exception{
		Set<String> strValues = new HashSet<String>();
		PreparedStatement ps = DB.prepareStatement(query, trxName);
		if(parameters != null){ 
			for (int i = 0; i < parameters.length; i++) {
				ps.setObject(i+1, parameters[i]);
			}
		}
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			strValues.add(rs.getString(1));
		}
		return strValues;
	} 
	
	
	/**
	 * Retorna la localización de la organización parámetro con el siguiente
	 * formato. Ej:
	 * 
	 * De los Napolitanos 6136<br>
	 * CP 5008 - Los Boulevares - Cordoba - ARGENTINA<br>
	 * Tel: (+54 351) 475 1003 / 1035 Fax (+54 351) 475 0952<br>
	 * E-mail: intersys@intersyssrl.com.ar<br>
	 * Web: www.intersyssrl.com.ar<br>
	 * 
	 * @param ctx
	 *            contexto
	 * @param clientID
	 *            id de compañía
	 * @param orgID
	 *            id de organización
	 * @param trxName
	 *            transacción en curso
	 * @return la localización de la organización + datos de la compañía como
	 *         muestra el ejemplo en el comentario
	 */
	public static String getLocalizacion(Properties ctx, Integer clientID, Integer orgID, String trxName) {
		final String nl = "\n";
		MOrg org = MOrg.get(ctx, orgID);
		MLocation orgLoc = new MLocation(ctx, org.getInfo().getC_Location_ID(), trxName);
		MClientInfo clientInfo = MClient.get(ctx).getInfo();
		StringBuffer loc = new StringBuffer();
		String address = (String)coalesce(orgLoc.getAddress1(), "");
		String postal = (String)coalesce(orgLoc.getPostal(), "");
		String city = (String)coalesce(orgLoc.getCity(), "");
		String region;
		if (orgLoc.getC_Region_ID() > 0)
			region = orgLoc.getRegion().getName();
		else
			region = (String)coalesce(orgLoc.getRegionName(), "");
		String country = (String)coalesce(orgLoc.getCountryName(), "");
		String phone = (String)coalesce(org.getInfo().gettelephone(), "");
		String fax = (String)coalesce(org.getInfo().getfaxnumber(), "");
		String mail = (String)coalesce(clientInfo.getEMail(),"");
		String web = (String)coalesce(clientInfo.getWeb(),"");
		
		// Calle / Nro
		loc.append(address).append(nl);
		// Cod. Postal - Ciudad - Provincia - Pais 
		if (postal.length() > 0)
			loc.append("CP ").append(postal);
		if (city.length() > 0)
			loc.append(" - ").append(city);
		if (region.length() > 0)
			loc.append(" - ").append(region);
		if (country.length() > 0)
			loc.append(" - ").append(country);
		loc.append(nl);
		// Teléfono - Fax
		if (phone.length() > 0)
			loc.append("Tel: ").append(phone);
		if (fax.length() > 0)
			loc.append(" Fax: ").append(fax);
		loc.append(nl);
		// E-mail
		if (mail.length() > 0)
			loc.append("E-Mail: ").append(mail).append(nl);
		// Web
		if (web.length() > 0)
			loc.append("Web: ").append(web).append(nl);
		
		return loc.toString();
	}

	/**
	 * Devuelve una dirección en una línea.
	 * Ejemplo:
	 * 
	 * De los Napolitanos 6136, Los Boulevares (5008), Cordoba, Argentina
	 * 
	 * @param ctx
	 * @param locationID
	 * @param includeCountry Indica si incluye el nombre del país o no
	 * @return
	 */
	public static String formatLocation(Properties ctx, int locationID, boolean includeCountry) {
		MLocation location = new MLocation(ctx, locationID, null);
		StringBuffer loc = new StringBuffer("");
		String address1 = (String)coalesce(location.getAddress1(), "");
		String postal = (String)coalesce(location.getPostal(), "");
		String city = (String)coalesce(location.getCity(), "");
		String region;
		if (location.getC_Region_ID() > 0)
			region = location.getRegion().getName();
		else
			region = (String)coalesce(location.getRegionName(), "");
		String country = (String)coalesce(location.getCountryName(), "");

		// Dirección 1
		if (address1.length() > 0)
			loc.append(address1);
		if (city.length() > 0) {
			loc.append(", ").append(city); 
			if (postal.length() > 0) {
				loc.append(" (").append(postal).append(")");
			}
		}
		if (region.length() > 0)
			loc.append(", ").append(region);
		if (country.length() > 0 && includeCountry)
			loc.append(", ").append(country);
		
		return loc.toString();
	}
}
