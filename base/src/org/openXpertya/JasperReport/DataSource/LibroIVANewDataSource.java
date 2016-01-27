package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MPreference;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class LibroIVANewDataSource implements JRDataSource {

	/**
	 * Nombre de la preferencia que contiene el valor máximo para agrupar
	 * facturas a CF
	 */
	private static final String GROUPED_CF_INVOICES_PREFERENCE_NAME = "L_AR_LibroIVA_CFMontoMaximoAgrupacion";

	/**
	 * Nombre de la preferencia que contiene el valor máximo de facturas para CF
	 * para impresión fiscal. Se utiliza este valor si no existe la preferencia
	 * de valor máximo de agrupación
	 */
	private static final String CF_MAX_AMT_L_AR = "L_AR_CFMontoMaximo";

	/**
	 * Valor por defecto para agrupación de facturas a CF si ninguna de las 2
	 * preferencias anteriores existen
	 */
	private static final BigDecimal CF_MAX_AMT_DEFAULT = new BigDecimal(1000);

	/** Fecha desde y hasta de las facturas */
	private Date p_dateFrom;
	private Date p_dateTo;
	private int p_orgID;
	private boolean groupCFInvoices;
	private BigDecimal groupCFInvoicesAmt;

	/** Tipo de transaccion */
	private String p_transactionType;

	private MLibroIVALine libroIVALine = null;

	/** Context */
	private Properties p_ctx;
	private String trxName = null;

	int m_currentRecord = -1;
	int total_lines = -1;
	private List<MLibroIVALine> m_lines;

	/**
	 * Totales Debido a que existe mas de una tupla por factura (en el
	 * resultset, no en el crosstab) para que no se sumen los totales dos veces,
	 * se calculan desde esta clase
	 * */
	private BigDecimal totalNeto;
	private BigDecimal totalFacturado;
	private BigDecimal totalGravado;
	private BigDecimal totalNoGravado;
	private BigDecimal totalIVA;
	private Integer signOfSign;

	/** Utilizado para mapear los campos con las invocaciones de los metodos */
	HashMap<String, String> methodMapper = new HashMap<String, String>();
	
	/** Data Sources */
	private TaxDataSource taxDataSource;
	private DocumentsDataSource documentsDataSource;
	private DebitsDataSource debitsDataSource;
	private CreditsDataSource creditsDataSource;

	public LibroIVANewDataSource(Properties ctx, Date p_dateFrom,
			Date p_dateTo, String p_transactionType, int p_OrgID,
			boolean groupCFInvoices, String trxName) {
		this.m_lines = new ArrayList<MLibroIVALine>();
		this.p_ctx = ctx;
		this.p_dateFrom = p_dateFrom;
		this.p_dateTo = p_dateTo;
		this.p_transactionType = p_transactionType;
		this.p_orgID = p_OrgID;
		this.groupCFInvoices = groupCFInvoices;
		this.signOfSign = "V".equals(p_transactionType)?-1:1;
		setTrxName(trxName);

		methodMapper.put("AD_CLIENT_ID", "getAd_client_id");
		methodMapper.put("AD_ORG_ID", "getAd_org_id");
		methodMapper.put("ISACTIVE", "getIsActive");
		methodMapper.put("CREATED", "getCreated");
		methodMapper.put("CREATEDBY", "getCreatedby");
		methodMapper.put("UPDATED", "getUpdated");
		methodMapper.put("UPDATEDBY", "getUpdatedby");
		methodMapper.put("C_INVOICE_ID", "getC_invoice_id");
		methodMapper.put("ISSOTRX", "isSoTrx");
		methodMapper.put("DATEACCT", "getDateacct");
		methodMapper.put("DATEINVOICED", "getDateinvoiced");
		methodMapper.put("TIPODOCNAME", "getTipodocname");
		methodMapper.put("DOCUMENTNO", "getDocumentno");
		methodMapper.put("C_BPARTNER_NAME", "getBpartner_name");
		methodMapper.put("C_CATEGORIA_VIA_NAME", "getC_categoria_via_name");
		methodMapper.put("TAXID", "getTaxid");
		methodMapper.put("ITEM", "getItem");
		methodMapper.put("NETO", "getNeto");
		methodMapper.put("NETONOGRAVADO", "getNetoNoGravado");
		methodMapper.put("NETOGRAVADO", "getNetoGravado");
		methodMapper.put("IMPORTE", "getImporte");
		methodMapper.put("TOTAL", "getTotal");

	}

	private String getQuery() {
		StringBuffer query = new StringBuffer(
				"     SELECT "
						+ "			inv.ad_client_id, "
						+ "			inv.ad_org_id, "
						+ "			inv.isactive, "
						+ "			inv.created, "
						+ "			inv.createdby, "
						+ "			inv.updated, "
						+ "			inv.updatedby, "
						+ "			inv.c_invoice_id, "
						+ "			inv.issotrx, "
						+ "			date_trunc('day',inv.dateacct) as dateacct, "
						+ "			date_trunc('day',inv.dateinvoiced) as dateinvoiced, "
						+ "			cdt.printname, "
						+ "			inv.documentno, "
						+ "			cbp.c_bpartner_name, "
						+ "			cbp.taxid, "
						+ "			ctc.ismanual,"
						+ "			cci.i_tipo_iva, "
						+ "			c_categoria_iva_name, "
						+ "			inv.nombrecli, "
						+ "			inv.nroidentificcliente, "
						+ "			ct.rate, "
						+ "			ct.taxindicator, " 
						+ "			ct.sopotype, " 
						+ "			ct.taxtype, "
						+ "			coalesce(inv.puntodeventa,0) as puntodeventa, "
						+ "			cdt.c_doctype_id, "
						+ "			cdt.docbasetype, "
						+ "			(currencyconvert(cit.gravado, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS netoGravado,"
						+ "			(currencyconvert(cit.nogravado, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS netoNoGravado, "
						+ "			(currencyconvert(inv.grandtotal, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS total, "
						+ "			ct.c_tax_name AS item, "
						+ "			(currencyconvert(cit.importe, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS importe "

						+ "		FROM ( SELECT c_invoice.c_invoice_id, c_invoice.ad_client_id, c_invoice.ad_org_id, c_invoice.isactive, c_invoice.created, c_invoice.createdby, c_invoice.updated, c_invoice.updatedby, c_invoice.c_currency_id, c_invoice.c_conversiontype_id, c_invoice.documentno, c_invoice.c_bpartner_id, c_invoice.dateacct, c_invoice.dateinvoiced, c_invoice.totallines, c_invoice.grandtotal, c_invoice.issotrx, c_invoice.c_doctype_id, c_invoice.nombrecli, c_invoice.nroidentificcliente, c_invoice.puntodeventa, c_invoice.fiscalalreadyprinted "
						+ "     	   FROM c_invoice "
						+ "     	   WHERE ad_client_id = ? "
						+ "				 AND (c_invoice.docstatus = 'CO'::bpchar OR c_invoice.docstatus = 'CL'::bpchar OR c_invoice.docstatus = 'RE'::bpchar OR c_invoice.docstatus = 'VO'::bpchar OR c_invoice.docstatus = '??'::bpchar) AND c_invoice.isactive = 'Y'::bpchar "
						+ " 		     AND (dateacct::date between ? ::date and ? ::date) "
						+ getOrgCheck());// '2012/06/01' and '2012/08/31')
											// "+getOrgCheck())

		// Si no es ambos
		if (!p_transactionType.equals("B")) {
			// Si es transacción de ventas, C = Customer(Cliente)
			if (p_transactionType.equals("C")) {
				query.append(" AND (issotrx = 'Y')");
			}
			// Si es transacción de compra
			else {
				query.append(" AND (issotrx = 'N') ");
			}
		}
		query.append(" ) inv "
				+ " 	INNER JOIN ( SELECT c_invoicetax.c_tax_id, c_invoicetax.c_invoice_id, c_invoicetax.taxamt AS importe, "
				+ " 				   	CASE "
				+ "							WHEN c_invoicetax.taxamt = 0::numeric THEN c_invoicetax.taxbaseamt "
				+ "							WHEN c_invoicetax.taxamt <> 0::numeric THEN 0.00 "
				+ "							ELSE NULL::numeric "
				+ "					    END AS nogravado, "
				+ " 				   	CASE "
				+ "							WHEN c_invoicetax.taxamt = 0::numeric THEN 0.00 "
				+ "							WHEN c_invoicetax.taxamt <> 0::numeric THEN c_invoicetax.taxbaseamt "
				+ "							ELSE NULL::numeric "
				+ "					    END AS gravado, "
				+ "						c_invoicetax.ad_client_id "
				+ "					 FROM c_invoicetax) cit ON cit.c_invoice_id = inv.c_invoice_id "
				+ "		INNER JOIN ( SELECT c_doctype.c_doctype_id, c_doctype.name AS c_doctype_name, c_doctype.docbasetype, c_doctype.signo_issotrx AS signo, c_doctype.doctypekey, c_doctype.printname, c_doctype.isfiscaldocument, c_doctype.isfiscal "
				+ " 				FROM c_doctype"
				+ "					WHERE isfiscaldocument = 'Y') cdt ON cdt.c_doctype_id = inv.c_doctype_id "
				+ "		INNER JOIN ( SELECT c_tax.c_tax_id, c_tax.name AS c_tax_name, c_tax.c_taxcategory_id, c_tax.rate, taxindicator, sopotype, taxtype "
				+ " 				FROM c_tax) ct ON ct.c_tax_id = cit.c_tax_id "
				+ "		INNER JOIN ( SELECT c_taxcategory_id, c_taxcategory.ismanual "
				+ "					FROM c_taxcategory) ctc ON ctc.c_taxcategory_id = ct.c_taxcategory_id"	
				+ "		LEFT JOIN ( SELECT c_bpartner.c_bpartner_id, c_bpartner.name AS c_bpartner_name, c_bpartner.c_categoria_iva_id,c_bpartner.taxid, c_bpartner.iibb "
				+ " 				FROM c_bpartner) cbp ON inv.c_bpartner_id = cbp.c_bpartner_id "
				+ "		LEFT JOIN ( SELECT c_categoria_iva.c_categoria_iva_id, c_categoria_iva.name AS c_categoria_iva_name, c_categoria_iva.codigo AS codiva, c_categoria_iva.i_tipo_iva "
				+ " 				FROM c_categoria_iva) cci ON cbp.c_categoria_iva_id = cci.c_categoria_iva_id "
				+ " 	WHERE cdt.doctypekey::text <> ALL (ARRAY['RTR'::character varying, 'RTI'::character varying, 'RCR'::character varying, 'RCI'::character varying]::text[])"
				+ "       AND (cdt.isfiscal is null OR cdt.isfiscal = 'N' OR (cdt.isfiscal = 'Y' AND inv.fiscalalreadyprinted = 'Y')) "
				+ " 	ORDER BY date_trunc('day',inv.dateacct), puntodeventa, inv.documentno, cdt.c_doctype_id ");
		return query.toString();
	}

	private List<MLibroIVALine> getLines() {
		return m_lines;
	}

	public void loadData() throws RuntimeException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Inicialización del monto máximo para agrupar facturas a CF
			initCFGroupedAmt();

			int j = 1;
			pstmt = new CPreparedStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					getQuery(), getTrxName(), true);
			
			pstmt.setInt(j++, Env.getAD_Client_ID(Env.getCtx()));
			pstmt.setTimestamp(j++, new Timestamp(this.p_dateFrom.getTime()));
			pstmt.setTimestamp(j++, new Timestamp(this.p_dateTo.getTime()));
			rs = pstmt.executeQuery();

			int invoiceID = 0;
			int lastInvoiceID = -1;
			totalNeto = new BigDecimal(0);
			totalNoGravado = new BigDecimal(0);
			totalGravado = new BigDecimal(0);
			totalFacturado = new BigDecimal(0);
			totalIVA = new BigDecimal(0);
			Map<String, MLibroIVALine> groupedInvoicesByIVA = new HashMap<String, MLibroIVALine>();
			Integer oldDocTypeID = null;
			Integer actualDocTypeID = null;
			String oldDate = null;
			String actualDate = null;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd-MM-yyyy");
			MLibroIVALine lineAux;

			Map<String, M_Tax> allByCategoriaIVA = new HashMap<String, M_Tax>();
			Map<String, M_Tax> debitsByCategoriaIVA = new HashMap<String, M_Tax>();
			Map<String, M_Tax> creditsByCategoriaIVA = new HashMap<String, M_Tax>();
			
			Map<String, BigDecimal> allTotalsTaxBaseAmtByCategoriaIVA = new HashMap<String, BigDecimal>();
			Map<String, BigDecimal> debitsTotalsTaxBaseAmtByCategoriaIVA = new HashMap<String, BigDecimal>();
			Map<String, BigDecimal> creditsTotalsTaxBaseAmtByCategoriaIVA = new HashMap<String, BigDecimal>();
			
			Date dateinvoiced, dateAcct;
			String docBaseType, tipodocname, documentno, c_bpartner_name, taxid, ismanual, c_categoria_via_name, item, isSoTrx, categoriaIVA;  
			BigDecimal netoNoGravado, netoGravado, neto, importe, realTotal, total;
			boolean manualTax, isCredit;
			
			while (rs.next()) {
				dateinvoiced = rs.getDate("dateinvoiced");
				dateAcct = rs.getDate("dateacct");
				actualDocTypeID = rs.getInt("c_doctype_id");
				actualDate = simpleDateFormat.format(dateAcct);
				if (groupCFInvoices
						&& ((oldDocTypeID != null && actualDocTypeID.intValue() != oldDocTypeID
								.intValue()) || (oldDate != null && !actualDate
								.equalsIgnoreCase(oldDate)))) {
					oldDocTypeID = null;
					oldDate = null;
				}
				docBaseType = rs.getString("docbasetype");
				isCredit = MDocType.DOCBASETYPE_APCreditMemo.equals(docBaseType)
						|| MDocType.DOCBASETYPE_ARCreditMemo.equals(docBaseType);
				tipodocname = rs.getString("printname");
				documentno = rs.getString("documentno");
				c_bpartner_name = Util.isEmpty(
						rs.getString("nombrecli"), true) ? rs
						.getString("c_bpartner_name") : rs
						.getString("nombrecli");
				taxid = rs.getString("taxid");
				ismanual = rs.getString("ismanual");
				if (Util.isEmpty(taxid, true)) {
					String nroIdentificCliente = rs
							.getString("nroidentificcliente");
					taxid = !Util.isEmpty(nroIdentificCliente, true) ? nroIdentificCliente
							: "";
				}
				invoiceID = rs.getInt("c_invoice_id");
				c_categoria_via_name = rs.getString("i_tipo_iva");
				item = rs.getString("item");
				if ((item.toLowerCase().indexOf("iva") > -1) && (rs.getBigDecimal("rate").compareTo(BigDecimal.ZERO) == 0) )
					item = "Exento";
			/*	if (rs.getBigDecimal("rate").compareTo(BigDecimal.ZERO) == 0) {
					item = "Exento";
				}*/
				// BigDecimal neto=rs.getBigDecimal("neto");
				netoNoGravado = rs.getBigDecimal("netoNoGravado");
				// BigDecimal netoGravado= new BigDecimal(0);
				netoGravado = rs.getBigDecimal("netoGravado");
				// netoGravado=neto.subtract(netoNoGravado);
				neto = netoNoGravado.add(netoGravado);
				importe = rs.getBigDecimal("importe");
				isSoTrx = rs.getString("isSoTrx");
				// BigDecimal total=
				// importe.add(netoGravado.add(netoNoGravado));
				realTotal = rs.getBigDecimal("total");
				total = realTotal;
				if (lastInvoiceID == invoiceID) {
					total = null;
				}
				// Si el actual es distinto al anterior tipo de documento,
				// entonces se agregan los datos del anterior como líneas
				if (groupCFInvoices
						&& (oldDocTypeID == null || oldDate == null)) {
					for (String itemIVA : groupedInvoicesByIVA.keySet()) {
						m_lines.add(groupedInvoicesByIVA.get(itemIVA));
					}
					groupedInvoicesByIVA.clear();
				}

				// Si se debe agrupar facturas a CF, se suma por iva el monto
				// total de la factura
				if (groupCFInvoices
						&& c_categoria_via_name.equalsIgnoreCase("CF")
						&& realTotal.compareTo(groupCFInvoicesAmt) <= 0
						&& (oldDocTypeID == null || actualDocTypeID.intValue() == oldDocTypeID
								.intValue())
						&& (oldDate == null || actualDate
								.equalsIgnoreCase(oldDate))) {
					lineAux = groupedInvoicesByIVA.get(item);
					if (lineAux == null) {
						lineAux = new MLibroIVALine(dateinvoiced, dateAcct,
								tipodocname, documentno, c_bpartner_name,
								taxid, c_categoria_via_name,
								neto == null ? BigDecimal.ZERO : neto,
								netoGravado == null ? BigDecimal.ZERO
										: netoGravado,
								netoNoGravado == null ? BigDecimal.ZERO
										: netoNoGravado,
								total == null ? BigDecimal.ZERO : realTotal,
								item, importe == null ? BigDecimal.ZERO
										: importe, isSoTrx);
					} else {
						lineAux.setDocumentno(rs.getInt("puntodeventa") == 0 ? "-"
								: String.valueOf(rs.getInt("puntodeventa")));
						lineAux.setBpartner_name("");
						lineAux.setTaxid("");
						lineAux.setNeto(lineAux.getNeto().add(
								neto == null ? BigDecimal.ZERO : neto));
						lineAux.setNetoGravado(lineAux.getNetoGravado().add(
								netoGravado == null ? BigDecimal.ZERO
										: netoGravado));
						lineAux.setNetoNoGravado(lineAux.getNetoNoGravado()
								.add(netoNoGravado == null ? BigDecimal.ZERO
										: netoNoGravado));
						lineAux.setTotal(lineAux.getTotal().add(
								total == null ? BigDecimal.ZERO : total));
						lineAux.setImporte(lineAux.getImporte().add(
								importe == null ? BigDecimal.ZERO : importe));
					}
					groupedInvoicesByIVA.put(item, lineAux);
				} else {
					m_lines.add(new MLibroIVALine(dateinvoiced, dateAcct,
							tipodocname, documentno, c_bpartner_name, taxid,
							c_categoria_via_name, neto, netoGravado,
							netoNoGravado, total, item, importe, isSoTrx));
				}
				totalFacturado = totalFacturado
						.add(total == null ? BigDecimal.ZERO : total);
				
				categoriaIVA = rs.getString("c_categoria_iva_name");
				manualTax = ismanual.equals("Y");
				addTaxLine(categoriaIVA, item, allByCategoriaIVA, allTotalsTaxBaseAmtByCategoriaIVA, neto, importe,
						rs.getBigDecimal("rate"), rs.getString("taxindicator"), rs.getString("sopotype"),
						rs.getString("taxtype"), manualTax);
				if(!isCredit){
					addTaxLine(categoriaIVA, item, debitsByCategoriaIVA, debitsTotalsTaxBaseAmtByCategoriaIVA, neto, importe,
							rs.getBigDecimal("rate"), rs.getString("taxindicator"), rs.getString("sopotype"),
							rs.getString("taxtype"), manualTax);
				}
				else{
					addTaxLine(categoriaIVA, item, creditsByCategoriaIVA, creditsTotalsTaxBaseAmtByCategoriaIVA, neto, importe,
							rs.getBigDecimal("rate"), rs.getString("taxindicator"), rs.getString("sopotype"),
							rs.getString("taxtype"), manualTax);
				}
				
				// El totalNoGravado y el totalGravado no debe sumar Persepciones
				if (!manualTax) {
					totalNoGravado = totalNoGravado.add(netoNoGravado);
					totalGravado = totalGravado.add(netoGravado);					
				}

				lastInvoiceID = invoiceID;
				if (groupCFInvoices
						&& (oldDocTypeID == null || oldDate == null)) {
					oldDocTypeID = actualDocTypeID;
					oldDate = actualDate;
				}
			}

			if (groupedInvoicesByIVA.keySet().size() > 0) {
				for (String itemIVA : groupedInvoicesByIVA.keySet()) {
					m_lines.add(groupedInvoicesByIVA.get(itemIVA));
				}
				groupedInvoicesByIVA.clear();
			}
			totalNeto = totalGravado.add(totalNoGravado);
			totalIVA = totalIVA.add(totalFacturado.subtract(totalNeto));
			total_lines = m_lines.size();
			// Crear los data sources de los subreportes
			// Tabla de Impuestos
			taxDataSource = new TaxDataSource();
			loadTaxBaseAmtTotals(allByCategoriaIVA, allTotalsTaxBaseAmtByCategoriaIVA);
			List<M_Tax> allByCategoriaIVAList = new ArrayList<M_Tax>(allByCategoriaIVA.values());
			Collections.sort(allByCategoriaIVAList);
			taxDataSource.setReportLines(allByCategoriaIVAList);
			
			// Resumen por categoría de iva para todos los documentos
			documentsDataSource = new DocumentsDataSource();
			documentsDataSource.setReportLines(allByCategoriaIVAList);
			
			// Resumen por categoría de iva para los débitos
			debitsDataSource = new DebitsDataSource();
			loadTaxBaseAmtTotals(debitsByCategoriaIVA, debitsTotalsTaxBaseAmtByCategoriaIVA);
			List<M_Tax> debitsByCategoriaIVAList = new ArrayList<M_Tax>(debitsByCategoriaIVA.values());
			Collections.sort(debitsByCategoriaIVAList);
			debitsDataSource.setReportLines(debitsByCategoriaIVAList);
			
			// Resumen por categoría de iva para los créditos
			creditsDataSource = new CreditsDataSource();
			loadTaxBaseAmtTotals(creditsByCategoriaIVA, creditsTotalsTaxBaseAmtByCategoriaIVA);
			List<M_Tax> creditsByCategoriaIVAList = new ArrayList<M_Tax>(creditsByCategoriaIVA.values());
			Collections.sort(creditsByCategoriaIVAList);
			creditsDataSource.setReportLines(creditsByCategoriaIVAList);
			
			allByCategoriaIVA = null;
			debitsByCategoriaIVA = null;
			creditsByCategoriaIVA = null;
			
			allTotalsTaxBaseAmtByCategoriaIVA = null;
			debitsTotalsTaxBaseAmtByCategoriaIVA = null;
			creditsTotalsTaxBaseAmtByCategoriaIVA = null;
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private void initCFGroupedAmt() {
		// Busco la preferencia con el valor límite para agrupar ventas
		BigDecimal groupedCFInvoicesAmt = CF_MAX_AMT_DEFAULT;
		if (groupCFInvoices) {
			// 1) Busco la preferencia de monto máximo de agrupación de facturas
			// a CF
			String value = MPreference.searchCustomPreferenceValue(
					GROUPED_CF_INVOICES_PREFERENCE_NAME,
					Env.getAD_Client_ID(p_ctx),
					p_orgID > 0 ? p_orgID : Env.getAD_Org_ID(p_ctx), null,
					false);
			// 2) Si no existe 1), busco la preferencia de monto máximo para
			// facturas a CF en impresión fiscal
			if (Util.isEmpty(value, true)) {
				value = MPreference.searchCustomPreferenceValue(
						CF_MAX_AMT_L_AR, Env.getAD_Client_ID(p_ctx),
						p_orgID > 0 ? p_orgID : Env.getAD_Org_ID(p_ctx), null,
						false);
			}

			if (!Util.isEmpty(value, true)) {
				groupedCFInvoicesAmt = new BigDecimal(value);
			}
		}
		this.groupCFInvoicesAmt = groupedCFInvoicesAmt;
	}

	/* Retorna el valor correspondiente al campo indicado */
	public Object getFieldValue(JRField field) throws JRException {

		String name = null;
		Class<?> clazz = null;
		Method method = null;
		Object output = null;
		try {
			// Invocar al metodo segun el campo correspondiente
			name = field.getName().toUpperCase();
			clazz = Class
					.forName("org.openXpertya.JasperReport.DataSource.MLibroIVALine");
			method = clazz.getMethod(methodMapper.get(name));
			output = (Object) method.invoke(libroIVALine);
		} catch (ClassNotFoundException e) {
			throw new JRException("No se ha podido obtener el valor del campo "
					+ name);
		} catch (NoSuchMethodException e) {
			throw new JRException("No se ha podido invocar el metodo "
					+ methodMapper.get(name));
		} catch (InvocationTargetException e) {
			throw new JRException("Excepcion al invocar el método "
					+ methodMapper.get(name));
		} catch (Exception e) {
			throw new JRException("Excepcion general al acceder al campo "
					+ name);
		}
		return output;
	}

	public BigDecimal getTotalIVA() {
		return totalIVA;
	}

	public BigDecimal getNeto() {
		return totalNeto;
	}

	public BigDecimal getTotalFacturado() {
		return totalFacturado;
	}

	public BigDecimal getTotalGravado() {
		return totalGravado;
	}

	public BigDecimal getTotalNoGravado() {
		return totalNoGravado;
	}

	/**
	 * Retorna la localización de la organización del pedido con el siguiente
	 * formato. Ej:
	 * 
	 * De los Napolitanos 6136 CP 5008 - Los Boulevares - Cordoba - ARGENTINA
	 * Tel: (+54 351) 475 1003 / 1035 Fax (+54 351) 475 0952 E-mail:
	 * intersys@intersyssrl.com.ar Web: www.intersyssrl.com.ar Cuit
	 * 33-70718628-9
	 * 
	 * @param order
	 * @return
	 */
	public String getLocalizacion(int ad_client_id) {
		final String nl = "\n";
		String query = "SELECT c_location_id FROM ad_clientinfo WHERE ad_client_id = ?";
		int C_Location_ID = DB.getSQLValue(getTrxName(), query, ad_client_id);
		MLocation clientLoc = new MLocation(Env.getCtx(), C_Location_ID, getTrxName());
		StringBuffer loc = new StringBuffer();
		String address = (String) coalesce(clientLoc.getAddress1(), "");
		String postal = (String) coalesce(clientLoc.getPostal(), "");
		String city = (String) coalesce(clientLoc.getCity(), "");
		String region;
		if (clientLoc.getC_Region_ID() > 0)
			region = clientLoc.getRegion().getName();
		else
			region = (String) coalesce(clientLoc.getRegionName(), "");
		String country = (String) coalesce(clientLoc.getCountryName(), "");
		// String phone = (String)coalesce(org.getInfo().gettelephone(), "");
		// String fax = (String)coalesce(org.getInfo().getfaxnumber(), "");
		// String mail = clientInfo.getEMail();
		// String web = clientInfo.getWeb();
		query = "SELECT cuit FROM ad_clientinfo WHERE ad_client_id = ?";
		String cuit = DB.getSQLValueString(getTrxName(), query, ad_client_id);
		// String cuit = clientInfo.getCUIT();

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
		/*
		 * if (phone.length() > 0) loc.append("Tel: ").append(phone); if
		 * (fax.length() > 0) loc.append(" Fax: ").append(fax); loc.append(nl);
		 */
		// E-mail
		/*
		 * if (mail.length() > 0)
		 * loc.append("E-Mail: ").append(mail).append(nl); // Web if
		 * (web.length() > 0) loc.append("Web: ").append(web).append(nl);
		 */
		// CUIT
		if (cuit != null) {
			if (cuit.length() > 0)
				loc.append("Cuit ").append(cuit);
		}

		return loc.toString();
	}

	public static Object coalesce(Object object, Object defValue) {
		if (object == null)
			return defValue;
		return object;
	}

	/**
	 * Validacion por organización
	 */
	protected String getOrgCheck() {
		return (p_orgID > 0 ? " AND AD_Org_ID = " + p_orgID : "") + " ";
	}

	public boolean next() throws JRException {
		m_currentRecord++;

		if (m_currentRecord >= total_lines) {
			return false;
		}

		libroIVALine = getLines().get(m_currentRecord);
		return true;
	}
	
	private void addTaxLine(String categoriaIVAName, String taxName, Map<String, M_Tax> allByCategoriaIVA, Map<String, BigDecimal> allTaxAmountByCategoriaIVA, BigDecimal taxBaseAmt, BigDecimal taxAmt, BigDecimal taxRate, String taxIndicator, String sopoType, String taxType, boolean isManualTax){
		String key = categoriaIVAName+"_"+taxName;
		// Linea de categoría de iva e impuesto
		M_Tax taxLine = allByCategoriaIVA.get(key);
		if(taxLine == null){
			taxLine = new M_Tax(taxName, BigDecimal.ZERO, taxIndicator, taxRate, sopoType, taxType, BigDecimal.ZERO, categoriaIVAName);
		}
		taxLine.addTaxAmt(taxAmt);
		taxLine.addTaxBaseAmt(taxBaseAmt);
		// Total neto por categoria de iva e impuesto sólo si no es manual
		if(!isManualTax){
			BigDecimal allTaxAmount = allTaxAmountByCategoriaIVA.get(categoriaIVAName);
			if(allTaxAmount == null){
				allTaxAmount = BigDecimal.ZERO;
			}
			allTaxAmount = allTaxAmount.add(taxBaseAmt);
			allTaxAmountByCategoriaIVA.put(categoriaIVAName, allTaxAmount);
		}
		allByCategoriaIVA.put(key, taxLine);
	}

	private void loadTaxBaseAmtTotals(Map<String, M_Tax> allByCategoriaIVA, Map<String, BigDecimal> allTotalsTaxBaseAmtByCategoriaIVA){
		M_Tax taxAux;
		for (String allCategoriaIVAKey : allByCategoriaIVA.keySet()) {
			taxAux = allByCategoriaIVA.get(allCategoriaIVAKey);
			taxAux.totalTaxBaseAmtByCategoriaIVA = allTotalsTaxBaseAmtByCategoriaIVA.get(taxAux.categoriaIVAName);
		}
	}
	
	public JRDataSource getTaxDataSource() {
		return taxDataSource;
	}
	
	public JRDataSource getTotalGeneralDataSource() {
		return documentsDataSource;
	}
	
	public JRDataSource getTotalCreditsDataSource() {
		return creditsDataSource;
	}
	
	public JRDataSource getTotalDebitsDataSource() {
		return debitsDataSource;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	/*
	 *  *********************************************************** /
	 * OPDataSource: Clase que contiene la funcionalidad común a todos los
	 * DataSource de los subreportes del reporte.
	 * ***********************************************************
	 */
	class TaxDataSource implements JRDataSource {
		/** Lineas del informe */
		private List<M_Tax> m_reportLines;
		/** Registro Actual */
		private int m_currentRecord = -1; // -1 porque lo primero que se hace es
											// un ++

		protected void setReportLines(List<M_Tax> reportLines){
			m_reportLines = reportLines;
		}
		
		protected List<M_Tax> getReportLines(){
			return m_reportLines;
		}
		
		public boolean next() throws JRException {
			m_currentRecord++;
			if (m_currentRecord >= m_reportLines.size())
				return false;

			return true;
		}

		public Object getFieldValue(JRField jrf) throws JRException {
			return getFieldValue(jrf.getName(), m_reportLines.get(m_currentRecord));
		}

		protected Object getFieldValue(String name, Object record)
				throws JRException {
			M_Tax tax = (M_Tax) record;
			if (name.toUpperCase().equals("TAXNAME")) {				
				return tax.taxName;
			} else if (name.toUpperCase().equals("TAXAMOUNT")) {
				return tax.taxAmount;
			} else if (name.toUpperCase().equals("TAXINDICATOR")) {
				return tax.taxIndicator;
			} else if (name.toUpperCase().equals("RATE")) {
				return tax.rate;
			} else if (name.toUpperCase().equals("SOPOTYPE")) {
				return tax.sopoType;
			} else if (name.toUpperCase().equals("TAXTYPE")) {
				return tax.taxType;
			} else if (name.toUpperCase().equals("TAXBASEAMOUNT")) {
				return tax.taxBaseAmount;
			} else if (name.equalsIgnoreCase("c_categoria_iva_name")) {
				return tax.categoriaIVAName;
			} else if(name.equalsIgnoreCase("total_categoria_iva_base_amt")){
				return tax.totalTaxBaseAmtByCategoriaIVA;
			}

			return null;
		}
	}

	/**
	 * POJO de Impuesto.
	 */
	protected class M_Tax implements Comparable<M_Tax>{

		protected String taxName;
		protected BigDecimal taxAmount = BigDecimal.ZERO;
		protected String taxIndicator;
		protected BigDecimal rate;
		protected String sopoType;
		protected String taxType;
		private BigDecimal taxBaseAmount = BigDecimal.ZERO;
		private String categoriaIVAName;
		private BigDecimal totalTaxBaseAmtByCategoriaIVA = BigDecimal.ZERO;
		
		public M_Tax(String taxName, BigDecimal taxAmount,
				String taxIndicator, BigDecimal rate, String sopoType,
				String taxType, BigDecimal taxBaseAmt,
				String categoriaIVAName) {
			super();
			
			this.taxName = taxName;
			this.taxAmount = taxAmount;
			this.taxIndicator = taxIndicator;
			this.rate = rate;
			
			/*if (this.rate.compareTo(BigDecimal.ZERO) == 0) 
				this.taxName = "Exento";
			*/
			this.sopoType = sopoType;
			this.taxType = taxType;
			this.taxBaseAmount = taxBaseAmt;
			this.categoriaIVAName = categoriaIVAName;
		}

		protected void addTaxAmt(BigDecimal taxAmt){
			taxAmount = taxAmount.add(taxAmt);
		}
		
		protected void addTaxBaseAmt(BigDecimal taxBaseAmt){
			taxBaseAmount = taxBaseAmount.add(taxBaseAmt);
		}
		
		protected void addTotalTaxBaseAmtByCategoriaIVA(BigDecimal totalTaxBaseAmt){
			totalTaxBaseAmtByCategoriaIVA = totalTaxBaseAmtByCategoriaIVA.add(totalTaxBaseAmt);
		}

		@Override
		public int compareTo(M_Tax o) {
			return categoriaIVAName.compareTo(o.categoriaIVAName);
		}		
	}
	
	class DocumentsDataSource extends TaxDataSource{
		
		@Override
		protected Object getFieldValue(String name, Object record)
				throws JRException {
			M_Tax tax = (M_Tax) record;
			if (name.equalsIgnoreCase("c_categoria_iva_name")) {
				return tax.categoriaIVAName + getAdditionalCategoriaIVANameDescription();
			}
			else{
				return super.getFieldValue(name, record);
			}
		}
		
		protected String getAdditionalCategoriaIVANameDescription(){
			return "";
		}		
	}
	
	class DebitsDataSource extends DocumentsDataSource{
		
		@Override		
		protected String getAdditionalCategoriaIVANameDescription(){
			return " - DEBITOS";
		}
	}
	
	class CreditsDataSource extends DocumentsDataSource{
		
		@Override		
		protected String getAdditionalCategoriaIVANameDescription(){
			return " - CREDITOS";
		}
	}
}

class MLibroIVALine {

	private Date dateinvoiced;
	private Date dateacct;
	private String tipodocname;
	private String documentno;
	private String bpartner_name;
	private String taxid;
	private String c_categoria_via_name;
	private BigDecimal neto;
	private BigDecimal netoGravado;
	private BigDecimal netoNoGravado;
	private BigDecimal total;
	private String item;
	private BigDecimal importe;
	private String ad_client_id;
	private int ad_org_id;
	private int c_invoice_id;
	private Date created;
	private String createdby;
	private String updatedby;
	private Date updated;
	private boolean isActive;
	private String isSoTrx;

	public MLibroIVALine(Date dateinvoiced, Date dateacct, String tipodocname,
			String documentno, String bpartnerName, String taxid,
			String cCategoriaViaName, BigDecimal neto, BigDecimal netoGravado,
			BigDecimal netoNoGravado, BigDecimal total, String item,
			BigDecimal importe, String isSoTrx) {
		super();
		this.dateinvoiced = dateinvoiced;
		this.dateacct = dateacct;
		this.tipodocname = tipodocname;
		this.documentno = documentno;
		bpartner_name = bpartnerName;
		this.taxid = taxid;
		c_categoria_via_name = cCategoriaViaName;
		this.neto = neto;
		this.netoGravado = netoGravado;
		this.netoNoGravado = netoNoGravado;
		this.item = item;
		this.importe = importe;
		this.total = total;
		this.isSoTrx = isSoTrx;
	}

	public Date getDateinvoiced() {
		return dateinvoiced;
	}

	public void setDateinvoiced(Date dateinvoiced) {
		this.dateinvoiced = dateinvoiced;
	}

	public Date getDateacct() {
		return dateacct;
	}

	public void setDateacct(Date dateacct) {
		this.dateacct = dateacct;
	}

	public String getTipodocname() {
		return tipodocname;
	}

	public void setTipodocname(String tipodocname) {
		this.tipodocname = tipodocname;
	}

	public String getDocumentno() {
		return documentno;
	}

	public void setDocumentno(String documentno) {
		this.documentno = documentno;
	}

	public BigDecimal getNetoNoGravado() {
		return netoNoGravado;
	}

	public void setNetoNoGravado(BigDecimal netoNoGravado) {
		this.netoNoGravado = netoNoGravado;
	}

	public String getBpartner_name() {
		return bpartner_name;
	}

	public void setBpartner_name(String bpartnerName) {
		bpartner_name = bpartnerName;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public String getC_categoria_via_name() {
		return c_categoria_via_name;
	}

	public void setC_categoria_via_name(String cCategoriaViaName) {
		c_categoria_via_name = cCategoriaViaName;
	}

	public BigDecimal getNeto() {
		return neto;
	}

	public void setNeto(BigDecimal neto) {
		this.neto = neto;
	}

	public BigDecimal getNetoGravado() {
		return netoGravado;
	}

	public void setNetoGravado(BigDecimal netoGravado) {
		this.netoGravado = netoGravado;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void BigDecimal(BigDecimal total) {
		this.total = total;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	} // setActive

	public boolean getIsActive() {
		return isActive;
	} // isActive

	public String getAd_client_id() {
		return ad_client_id;
	}

	public void setAd_client_id(String adClientId) {
		ad_client_id = adClientId;
	}

	public int getAd_org_id() {
		return ad_org_id;
	}

	public void setAd_org_id(int adOrgId) {
		ad_org_id = adOrgId;
	}

	public int getC_invoice_id() {
		return c_invoice_id;
	}

	public void setC_invoice_id(int cInvoiceId) {
		c_invoice_id = cInvoiceId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String isSoTrx() {
		return isSoTrx;
	}

	public void setSoTrx(String isSoTrx) {
		this.isSoTrx = isSoTrx;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
