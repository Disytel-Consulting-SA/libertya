package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MLocation;
import org.openXpertya.model.MPreference;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

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

	int m_currentRecord = -1;
	int total_lines = -1;
	private MLibroIVALine[] m_lines;

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

	public LibroIVANewDataSource(Properties ctx, Date p_dateFrom,
			Date p_dateTo, String p_transactionType, int p_OrgID,
			boolean groupCFInvoices) {
		this.m_lines = new MLibroIVALine[10000000];
		this.p_ctx = ctx;
		this.p_dateFrom = p_dateFrom;
		this.p_dateTo = p_dateTo;
		this.p_transactionType = p_transactionType;
		this.p_orgID = p_OrgID;
		this.groupCFInvoices = groupCFInvoices;
		this.signOfSign = "V".equals(p_transactionType)?-1:1;

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
						+ "			inv.nombrecli, "
						+ "			inv.nroidentificcliente, "
						+ "			ct.rate, "
						+ "			coalesce(inv.puntodeventa,0) as puntodeventa, "
						+ "			cdt.c_doctype_id, "
						+ "			(currencyconvert(cit.gravado, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS netoGravado,"
						+ "			(currencyconvert(cit.nogravado, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS netoNoGravado, "
						+ "			(currencyconvert(inv.grandtotal, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS total, "
						+ "			ct.c_tax_name AS item, "
						+ "			(currencyconvert(cit.importe, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS importe "

						+ "		FROM ( SELECT c_invoice.c_invoice_id, c_invoice.ad_client_id, c_invoice.ad_org_id, c_invoice.isactive, c_invoice.created, c_invoice.createdby, c_invoice.updated, c_invoice.updatedby, c_invoice.c_currency_id, c_invoice.c_conversiontype_id, c_invoice.documentno, c_invoice.c_bpartner_id, c_invoice.dateacct, c_invoice.dateinvoiced, c_invoice.totallines, c_invoice.grandtotal, c_invoice.issotrx, c_invoice.c_doctype_id, c_invoice.nombrecli, c_invoice.nroidentificcliente, c_invoice.puntodeventa, c_invoice.fiscalalreadyprinted "
						+ "     	   FROM c_invoice "
						+ "     	   WHERE (c_invoice.docstatus = 'CO'::bpchar OR c_invoice.docstatus = 'CL'::bpchar OR c_invoice.docstatus = 'RE'::bpchar OR c_invoice.docstatus = 'VO'::bpchar OR c_invoice.docstatus = '??'::bpchar) AND c_invoice.isactive = 'Y'::bpchar "
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
				+ "		LEFT JOIN ( SELECT c_doctype.c_doctype_id, c_doctype.name AS c_doctype_name, c_doctype.docbasetype, c_doctype.signo_issotrx AS signo, c_doctype.doctypekey, c_doctype.printname, c_doctype.isfiscaldocument, c_doctype.isfiscal "
				+ " 				FROM c_doctype) cdt ON cdt.c_doctype_id = inv.c_doctype_id "
				+ "		LEFT JOIN ( SELECT c_tax.c_tax_id, c_tax.name AS c_tax_name, c_tax.c_taxcategory_id, c_tax.rate "
				+ " 				FROM c_tax) ct ON ct.c_tax_id = cit.c_tax_id "
				+ "		LEFT JOIN ( SELECT c_taxcategory_id, c_taxcategory.ismanual "
				+ "					FROM c_taxcategory) ctc ON ctc.c_taxcategory_id = ct.c_taxcategory_id"	
				+ "		LEFT JOIN ( SELECT c_bpartner.c_bpartner_id, c_bpartner.name AS c_bpartner_name, c_bpartner.c_categoria_iva_id,c_bpartner.taxid, c_bpartner.iibb "
				+ " 				FROM c_bpartner) cbp ON inv.c_bpartner_id = cbp.c_bpartner_id "
				+ "		LEFT JOIN ( SELECT c_categoria_iva.c_categoria_iva_id, c_categoria_iva.name AS c_categoria_via_name, c_categoria_iva.codigo AS codiva, c_categoria_iva.i_tipo_iva "
				+ " 				FROM c_categoria_iva) cci ON cbp.c_categoria_iva_id = cci.c_categoria_iva_id "
				+ " 	WHERE cdt.doctypekey::text <> ALL (ARRAY['RTR'::character varying, 'RTI'::character varying, 'RCR'::character varying, 'RCI'::character varying]::text[])"
				+ "       AND cdt.isfiscaldocument = 'Y' AND (cdt.isfiscal is null OR cdt.isfiscal = 'N' OR (cdt.isfiscal = 'Y' AND inv.fiscalalreadyprinted = 'Y')) "
				+ " 	ORDER BY date_trunc('day',inv.dateacct), puntodeventa, inv.documentno, cdt.c_doctype_id ");
		return query.toString();
	}

	private MLibroIVALine[] getLines() {
		return m_lines;
	}

	public void loadData() throws RuntimeException {
		try {
			// Inicialización del monto máximo para agrupar facturas a CF
			initCFGroupedAmt();

			int i = 0;
			int j = 1;
			PreparedStatement pstmt = new CPreparedStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					getQuery(), null, true);

			pstmt.setTimestamp(j++, new Timestamp(this.p_dateFrom.getTime()));
			pstmt.setTimestamp(j++, new Timestamp(this.p_dateTo.getTime()));
			ResultSet rs = pstmt.executeQuery();

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

			while (rs.next()) {
				Date dateinvoiced = rs.getDate("dateinvoiced");
				Date dateAcct = rs.getDate("dateacct");
				actualDocTypeID = rs.getInt("c_doctype_id");
				actualDate = simpleDateFormat.format(dateAcct);
				if (groupCFInvoices
						&& ((oldDocTypeID != null && actualDocTypeID.intValue() != oldDocTypeID
								.intValue()) || (oldDate != null && !actualDate
								.equalsIgnoreCase(oldDate)))) {
					oldDocTypeID = null;
					oldDate = null;
				}
				String tipodocname = rs.getString("printname");
				String documentno = rs.getString("documentno");
				String c_bpartner_name = Util.isEmpty(
						rs.getString("nombrecli"), true) ? rs
						.getString("c_bpartner_name") : rs
						.getString("nombrecli");
				String taxid = rs.getString("taxid");
				String ismanual = rs.getString("ismanual");
				if (Util.isEmpty(taxid, true)) {
					String nroIdentificCliente = rs
							.getString("nroidentificcliente");
					taxid = !Util.isEmpty(nroIdentificCliente, true) ? nroIdentificCliente
							: "";
				}
				invoiceID = rs.getInt("c_invoice_id");
				String c_categoria_via_name = rs.getString("i_tipo_iva");
				String item = rs.getString("item");
				if ((item.toLowerCase().indexOf("iva") > -1) && (rs.getBigDecimal("rate").compareTo(BigDecimal.ZERO) == 0) )
					item = "Exento";
			/*	if (rs.getBigDecimal("rate").compareTo(BigDecimal.ZERO) == 0) {
					item = "Exento";
				}*/
				// BigDecimal neto=rs.getBigDecimal("neto");
				BigDecimal netoNoGravado = rs.getBigDecimal("netoNoGravado");
				// BigDecimal netoGravado= new BigDecimal(0);
				BigDecimal netoGravado = rs.getBigDecimal("netoGravado");
				// netoGravado=neto.subtract(netoNoGravado);
				BigDecimal neto = netoNoGravado.add(netoGravado);
				BigDecimal importe = rs.getBigDecimal("importe");
				String isSoTrx = rs.getString("isSoTrx");
				// BigDecimal total=
				// importe.add(netoGravado.add(netoNoGravado));
				BigDecimal realTotal = rs.getBigDecimal("total");
				BigDecimal total = realTotal;
				if (lastInvoiceID == invoiceID) {
					total = null;
				}
				// Si el actual es distinto al anterior tipo de documento,
				// entonces se agregan los datos del anterior como líneas
				if (groupCFInvoices
						&& (oldDocTypeID == null || oldDate == null)) {
					for (String itemIVA : groupedInvoicesByIVA.keySet()) {
						m_lines[i] = groupedInvoicesByIVA.get(itemIVA);
						i++;
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
					m_lines[i] = new MLibroIVALine(dateinvoiced, dateAcct,
							tipodocname, documentno, c_bpartner_name, taxid,
							c_categoria_via_name, neto, netoGravado,
							netoNoGravado, total, item, importe, isSoTrx);
					i++;
				}
				total_lines = i;
				totalFacturado = totalFacturado
						.add(total == null ? BigDecimal.ZERO : total);

				// El totalNoGravado y el totalGravado no debe sumar Persepciones
				if (ismanual.equals("N")) {
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
					m_lines[i] = groupedInvoicesByIVA.get(itemIVA);
					i++;
				}
				groupedInvoicesByIVA.clear();
			}
			totalNeto = totalGravado.add(totalNoGravado);
			totalIVA = totalIVA.add(totalFacturado.subtract(totalNeto));

		} catch (Exception e) {
			e.printStackTrace();
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
		int C_Location_ID = DB.getSQLValue("Location", query, ad_client_id);
		MLocation clientLoc = new MLocation(Env.getCtx(), C_Location_ID, null);
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
		String cuit = DB.getSQLValueString("CUIT", query, ad_client_id);
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

		libroIVALine = getLines()[m_currentRecord];
		return true;
	}

	public JRDataSource getTaxDataSource() {
		TaxDataSource ds = new TaxDataSource();
		ds.loadData();
		return ds;
	}

	/*
	 *  *********************************************************** /
	 * OPDataSource: Clase que contiene la funcionalidad común a todos los
	 * DataSource de los subreportes del reporte.
	 * ***********************************************************
	 */
	class TaxDataSource implements JRDataSource {
		/** Lineas del informe */
		private Object[] m_reportLines;
		/** Registro Actual */
		private int m_currentRecord = -1; // -1 porque lo primero que se hace es
											// un ++

		public boolean next() throws JRException {
			m_currentRecord++;
			if (m_currentRecord >= m_reportLines.length)
				return false;

			return true;
		}

		public Object getFieldValue(JRField jrf) throws JRException {
			return getFieldValue(jrf.getName(), m_reportLines[m_currentRecord]);
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
				return tax.taxBaseAmt;
			} else if (name.equalsIgnoreCase("c_categoria_iva_name")) {
				return tax.categoriaIVAName;
			}

			return null;
		}

		protected Object createRecord(ResultSet rs) throws SQLException {
			return new M_Tax(rs);
		}

		protected String getDataSQL() {
			StringBuffer query = new StringBuffer(
					"     SELECT "
							+ "			ct.c_tax_name AS TaxName, "
							+ "			SUM(currencyconvert(cit.importe, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS TaxAmount, "
							+ "			SUM(currencyconvert(cit.taxbaseamt, inv.c_currency_id, 118, inv.dateacct::timestamp with time zone, inv.c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo::numeric * "+signOfSign+"::numeric)::numeric(20,2) AS TaxBaseAmount, "
							+ "         ct.taxindicator as TaxIndicator, ct.rate as Rate, ct.sopotype as Sopotype, ct.taxtype as TaxType, c_categoria_iva_name "
							+ "		FROM ( SELECT c_invoice.c_invoice_id, c_invoice.ad_client_id, c_invoice.ad_org_id, c_invoice.isactive, c_invoice.created, c_invoice.createdby, c_invoice.updated, c_invoice.updatedby, c_invoice.c_currency_id, c_invoice.c_conversiontype_id, c_invoice.documentno, c_invoice.c_bpartner_id, c_invoice.dateacct, c_invoice.dateinvoiced, c_invoice.totallines, c_invoice.grandtotal, c_invoice.issotrx, c_invoice.c_doctype_id "
							+ "     	   FROM c_invoice "
							+ "     	   WHERE (c_invoice.docstatus = 'CO'::bpchar OR c_invoice.docstatus = 'CL'::bpchar OR c_invoice.docstatus = 'RE'::bpchar OR c_invoice.docstatus = 'VO'::bpchar) AND c_invoice.isactive = 'Y'::bpchar "
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
					+ "						c_invoicetax.taxbaseamt, "
					+ "						c_invoicetax.ad_client_id "
					+ "					 FROM c_invoicetax) cit ON cit.c_invoice_id = inv.c_invoice_id "
					+ "		LEFT JOIN ( SELECT c_doctype.c_doctype_id, c_doctype.name AS c_doctype_name, c_doctype.docbasetype, c_doctype.signo_issotrx AS signo, c_doctype.doctypekey, c_doctype.printname, c_doctype.isfiscaldocument "
					+ " 				FROM c_doctype WHERE isfiscaldocument = 'Y') cdt ON cdt.c_doctype_id = inv.c_doctype_id "  	// <- Clausula incluida
					+ "		LEFT JOIN ( SELECT c_tax.c_tax_id, c_tax.name AS c_tax_name, taxindicator, rate, sopotype, taxtype, issummary "
					+ " 				FROM c_tax WHERE issummary = 'N') ct ON ct.c_tax_id = cit.c_tax_id "  	// <- Clausula incluida
					+ "		LEFT JOIN ( SELECT c_bpartner.c_bpartner_id, c_bpartner.name AS c_bpartner_name, c_bpartner.c_categoria_iva_id,c_bpartner.taxid, c_bpartner.iibb "
					+ " 				FROM c_bpartner) cbp ON inv.c_bpartner_id = cbp.c_bpartner_id "
					+ "		LEFT JOIN ( SELECT c_categoria_iva.c_categoria_iva_id, c_categoria_iva.name AS c_categoria_iva_name, c_categoria_iva.codigo AS codiva, c_categoria_iva.i_tipo_iva "
					+ " 				FROM c_categoria_iva) cci ON cbp.c_categoria_iva_id = cci.c_categoria_iva_id "
					+ " 	WHERE cdt.doctypekey::text <> ALL (ARRAY['RTR'::character varying, 'RTI'::character varying, 'RCR'::character varying, 'RCI'::character varying]::text[])"
//					+ "       AND cdt.isfiscaldocument = 'Y' " // Comentado: La clausula en esta posicion en lugar del SELECT no ayuda a postgre para optimizar el query.  Se incluye dentro del SELECT
//					+ "       AND ct.issummary = 'N' "  // Comentado: La clausula en esta posicion en lugar del SELECT no ayuda a postgre para optimizar el query. Se incluye dentro del SELECT
					+ "     GROUP BY ct.c_tax_name, ct.taxindicator, ct.rate, ct.sopotype, ct.taxtype, c_categoria_iva_name  "
					+ " 	ORDER BY c_categoria_iva_name, ct.rate ");
			return query.toString();
		}

		protected void setQueryParameters(PreparedStatement pstmt)
				throws SQLException {
			int j = 1;
			pstmt.setTimestamp(j++, new Timestamp(p_dateFrom.getTime()));
			pstmt.setTimestamp(j++, new Timestamp(p_dateTo.getTime()));
		}

		/**
		 * POJO de Impuesto.
		 */
		private class M_Tax {

			protected String taxName;
			protected BigDecimal taxAmount;
			protected String taxIndicator;
			protected BigDecimal rate;
			protected String sopoType;
			protected String taxType;
			private BigDecimal taxBaseAmt;
			private String categoriaIVAName;

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
				this.taxBaseAmt = taxBaseAmt;
				this.categoriaIVAName = categoriaIVAName;
			}

			public M_Tax(ResultSet rs) throws SQLException {
				
				this(	rs.getString("TaxName"), 
						rs.getBigDecimal("TaxAmount"), 
						rs.getString("TaxIndicator"), 
						rs.getBigDecimal("Rate"),
						rs.getString("SopoType"), 
						rs.getString("TaxType"), 
						rs.getBigDecimal("TaxBaseAmount"), 
						rs.getString("c_categoria_iva_name"));
			}
			
		}

		public void loadData() {

			// ArrayList donde se guardan los datos del informe.
			ArrayList<Object> list = new ArrayList<Object>();

			try {
				PreparedStatement pstmt = DB.prepareStatement(getDataSQL(),
						null, true);
				setQueryParameters(pstmt);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Object line = createRecord(rs);
					list.add(line);
				}

			} catch (SQLException e) {
				throw new RuntimeException(
						"No se puede ejecutar la consulta para crear las lineas del informe.");
			}

			// Se guarda la lista de líneas en el arreglo de líneas del reporte.
			m_reportLines = new Object[list.size()];
			list.toArray(m_reportLines);
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
