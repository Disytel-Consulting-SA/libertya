package org.openXpertya.JasperReport.DataSource;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.Env;
import org.openXpertya.util.LibroIVAUtils;
import org.openXpertya.util.Util;


public class LibroIVADataSource extends QueryDataSource implements JRDataSource {

	/** Fecha desde y hasta de las facturas */
	private Date p_dateFrom;
	private Date p_dateTo;
	private int p_orgID;
	
	/** Tipo de transaccion */
	private String p_transactionType;
	
	/** Context */
	private Properties p_ctx;
	
	/** Totales 
	 *  Debido a que existe mas de una tupla por factura (en el resultset, no en el crosstab) 
	 *  para que no se sumen los totales dos veces, se calculan desde esta clase
	 * */
	private BigDecimal neto;
	private BigDecimal totalFacturado;
	private BigDecimal totalGravado;
	private BigDecimal totalNoGravado;
	private Integer signOfSign;
	
	public LibroIVADataSource(String trxName)
	{
		super(trxName);
	}
	

	public LibroIVADataSource (String trxName, Properties ctx, Date dateFrom, Date dateTo, String transactionType, int orgID)	{
		super(trxName);
		p_ctx = ctx;
		p_dateFrom = dateFrom;
		p_dateTo = dateTo;
		p_transactionType = transactionType;
		p_orgID = orgID;
		signOfSign = "V".equals(p_transactionType)?-1:1;
	}


	@Override
	protected Object[] getParameters() {
		Object[] p = {Env.getAD_Client_ID(p_ctx), new java.sql.Date(p_dateFrom.getTime()), new java.sql.Date(p_dateTo.getTime())}; 
		return p;
	}


	@Override
	protected String getQuery() {
        
		// Guarda la moneda en la que se inicio en sistema
		int moneda = Env.getContextAsInt(p_ctx, "$C_Currency_ID");
		
		StringBuffer sqlReal = new StringBuffer(" Select cit.c_tax_id, 	" +
             	" 		inv.c_invoice_id, " +
             	" 		cdt.C_DocType_ID, " +
             	" 		cdt.docbasetype as TipoDocumento, " +
             	"		cdt.c_doctype_name as TipoDocName, " +	
             	// "		inv.documentno, " +
             	"		cdt.c_doctype_name || ' ' || inv.documentno AS documentno, " +
             	"		inv.c_bpartner_id, " +
             	"		cbp.c_bpartner_name, " +
             	"		inv.dateacct, " +
             	"		inv.dateinvoiced, " +
             	"		cbp.taxid, " +
             	"		cbp.c_categoria_iva_id, " +
             	"		cci.c_categoria_via_name, " +
             	"		(currencyconvert((inv.grandtotal-coalesce(taxamtsum,0)), inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo * "+signOfSign+")::numeric(20,2) as neto, " +
             	"		(currencyconvert(inv.grandtotal, inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo * "+signOfSign+")::numeric(20,2) as total, " +
             	"		ct.c_tax_name as item, " +
             	"       (currencyconvert(cit.importe, inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) * cdt.signo * "+signOfSign+")::numeric(20,2) as importe," +
             	"       cdt.signo," +
             	"       cit.AD_Client_ID, " +
             	"       cbp.iibb " +
             	" from (select c_invoice.c_invoice_id, c_invoice.ad_client_id, c_invoice.ad_org_id, c_invoice.c_currency_id, c_invoice.c_conversiontype_id, c_invoice.documentno, c_invoice.c_bpartner_id, c_invoice.dateacct, c_invoice.dateinvoiced, c_invoice.totallines, c_invoice.grandtotal, c_invoice.issotrx, c_doctype.c_doctype_id, c_invoice.fiscalalreadyprinted, c_invoice.cae  " +
             	"       from c_Invoice " +
             	"		INNER JOIN c_doctype ON c_invoice.c_doctypetarget_id = c_doctype.c_doctype_id " +
             	" where c_invoice.ad_client_id = ? " + getOrgCheck("c_invoice") +
             	" AND (c_invoice.isactive = 'Y') "+
             	" AND (date_trunc('day', c_invoice.dateacct) between date_trunc('day',?::timestamp) and date_trunc('day',?::timestamp)) " );
             
			// Si no es ambos
			if (!p_transactionType.equals("B")) {
				// Si es transacción de ventas, C = Customer(Cliente)
				if (p_transactionType.equals("C")) {
					sqlReal.append(
							" AND ((c_invoice.issotrx = 'Y' AND c_doctype.transactiontypefrontliva is null) OR c_doctype.transactiontypefrontliva = '"
									+ MDocType.TRANSACTIONTYPEFRONTLIVA_Sales + "') ");
				}
				// Si es transacción de compra
				else {
					sqlReal.append(
							" AND ((c_invoice.issotrx = 'N' AND c_doctype.transactiontypefrontliva is null) OR c_doctype.transactiontypefrontliva = '"
									+ MDocType.TRANSACTIONTYPEFRONTLIVA_Purchases + "') ");
				}
			}
					
			sqlReal.append(LibroIVAUtils.getDocStatusFilter(p_transactionType, "c_doctype", "c_invoice"));
             
            sqlReal.append(") inv " +
             	"     left join (select c_doctype_id, name as c_doctype_name,docbasetype , signo_issotrx as signo, doctypekey, isfiscaldocument, isfiscal, iselectronic " +
             	"				from c_docType) cdt on cdt.c_doctype_id = inv.c_doctype_id " +
             	"     left join (Select c_tax_id, c_invoice_id, taxamt as importe, ad_client_id " +
             	" 		        from c_invoicetax) cit 	on cit.c_invoice_id = inv.c_invoice_id " +
             	"     left join (Select c_invoice_id, sum(taxamt) as taxamtsum " +
             	" 		        from c_invoicetax" +
             	"				group by c_invoice_id) sumacit 	on sumacit.c_invoice_id = inv.c_invoice_id " +
             	"     left join (Select c_tax_id, name as c_tax_name " +
             	"				from c_tax ) ct on ct.c_tax_id = cit.c_tax_id " +
             	"     left join (Select c_bpartner_id, name as c_bpartner_name, c_categoria_iva_id, taxid, iibb " +
             	" 				from c_bpartner) cbp on inv.c_bpartner_id = cbp.c_bpartner_id " +
             	"     left join (Select c_categoria_iva_id, name as c_categoria_via_name, codigo as codiva " +
             	"				from c_categoria_iva ) cci 	on cbp.c_categoria_iva_id = cci.c_categoria_iva_id " +
             	"	  WHERE cdt.doctypekey not in ('RTR', 'RTI', 'RCR', 'RCI') " + LibroIVAUtils.getDocTypeFilter("cdt", "inv") +
             	"     ORDER BY inv.dateinvoiced ASC, inv.c_doctype_id, inv.documentno ASC, c_tax_id,c_invoice_id");
             //System.out.println(sqlReal);
             return sqlReal.toString();
	}
	
	public void calculateTotals()
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			int i = 1;
			pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE, getQuery(), null, true); 
		
			pstmt.setInt(i++, Env.getAD_Client_ID(p_ctx));
			pstmt.setTimestamp(i++,new Timestamp(p_dateFrom.getTime()));
			pstmt.setTimestamp(i++,new Timestamp(p_dateTo.getTime()));
			rs = pstmt.executeQuery();
						
			int invoiceID = -1;
			neto = new BigDecimal(0);
			totalFacturado = new BigDecimal(0);
			
			while(rs.next())
			{
				if (invoiceID != rs.getInt("c_invoice_id"))
				{
					totalFacturado = totalFacturado.add(rs.getBigDecimal("total"));
					neto = neto.add(rs.getBigDecimal("neto"));
				}
				invoiceID = rs.getInt("c_invoice_id");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public BigDecimal getNeto() {
		return neto;
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
	 * Retorna la localización de la organización del pedido con el siguiente formato.
	 * Ej:
	 * 
	 * De los Napolitanos 6136
	 * CP 5008 - Los Boulevares - Cordoba -  ARGENTINA
	 * Tel: (+54 351) 475 1003 / 1035  Fax (+54 351) 475 0952
	 * E-mail:  intersys@intersyssrl.com.ar
	 * Web:   www.intersyssrl.com.ar
	 * Cuit 33-70718628-9
	 * 
	 * @param order
	 * @return
	 */
	public String getLocalizacion(MOrder order) {
		final String nl = "\n";
		MOrg org = MOrg.get(Env.getCtx(), order.getAD_Org_ID());
		MLocation orgLoc = new MLocation(Env.getCtx(), org.getInfo().getC_Location_ID(), null);
		MClientInfo clientInfo = MClient.get(Env.getCtx()).getInfo();
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
		//String mail = clientInfo.getEMail();
		//String web = clientInfo.getWeb();
		String cuit = clientInfo.getCUIT();
		
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
		/*if (mail.length() > 0)
			loc.append("E-Mail: ").append(mail).append(nl);
		// Web
		if (web.length() > 0)
			loc.append("Web: ").append(web).append(nl);
		*/
		// CUIT
		if (cuit != null){
			if (cuit.length() > 0)
				loc.append("Cuit ").append(cuit);
		}
		
		return loc.toString();
	}
	
	public static Object coalesce(Object object, Object defValue)
	{
		if (object == null)
			return defValue;
		return object;
	}
	
	/**
	 * Validacion por organización
	 */
	protected String getOrgCheck(String alias)
	{
		alias = Util.isEmpty(alias)?"":alias+".";
		return (p_orgID > 0 ? " AND "+alias+"AD_Org_ID = " + p_orgID : "") + " ";
	}
}
