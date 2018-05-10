package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_T_LibroIva;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.LibroIVAUtils;
import org.openXpertya.util.Util;



public class GenerateLibroIva extends SvrProcess {
    
	/** Preferencia optativa para limitar numero de dias maximo a consultar */
	public static final String PREFERENCE_MAX_DAYS_LIMIT = "GenerateLibroIvaMaxDaysLimit";
	
    private Timestamp date_from;
    private Timestamp date_to;
    private String transaction;
    private int orgID;
    
    private int invoiceID;
    private BigDecimal neto;
    private BigDecimal totalFacturado;
    
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();
		 for( int i = 0;i < para.length;i++ ) {
	            log.fine( "prepare - " + para[ i ] );

	            String name = para[ i ].getParameterName();

	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equals( "DateFrom" )) {
	                date_from = ( Timestamp )para[ i ].getParameter();
	            } else if( name.equals( "DateTo" )) {
	                date_to = ( Timestamp )para[ i ].getParameter();
	            } else if( name.equals( "transactiontype" )) {
	            	transaction = (String)para[ i ].getParameter();
	            } else if( name.equals( "AD_Org_ID" )) {
	            	orgID = (Integer)para[ i ].getParameterAsInt();
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
		// veo que no falten los parametros !!!!!!
		
	}
	
	protected String doIt() throws java.lang.Exception {
		
		// Validar que el intervalo de dias a consultar sea uno aceptable, en funcion de la preferencia
		// Se adiciona 1 día dado que el intervalo de fechas es inclusivo (ejemplo: del 5 al 6 en realidad son 2 días de reporte)
		String limitPref = MPreference.GetCustomPreferenceValue(PREFERENCE_MAX_DAYS_LIMIT);
		if (limitPref != null && limitPref.length() > 0) {
			int limit = Integer.parseInt(limitPref);
			int days = (int)((date_to.getTime() - date_from.getTime()) / (1000 * 60 * 60 * 24)) + 1;

			// Si el numero de dias especificado supera el limite -> impedir ejecucion
			if (days > limit) {
				throw new Exception("El intervalo de fechas especificado (" + days + " dias) es superior al limite permitido (" + limit + " dias)");
			}
		}
		
		
		// delete all rows older than a week
    	// borro la antigua consulta si la hay
		
		 String	sql	= "DELETE FROM T_LibroIva WHERE AD_Client_ID = "+ getAD_Client_ID()+" AND AD_PInstance_ID = " + getAD_PInstance_ID() + " OR CREATED < ('now'::text)::timestamp(6) - interval '7 days'";
         DB.executeUpdate(sql, get_TrxName());

         // realizo una nueva consulta
         
         // Guarda la moneda en la que se inicio el sistema 
         int moneda = Env.getContextAsInt(getCtx(), "$C_Currency_ID");
         
         StringBuffer sqlReal = new StringBuffer(" Select cit.c_tax_id, 	" +
         	" 		inv.c_invoice_id, " +
         	" 		cdt.C_DocType_ID, " +
         	" 		cdt.docbasetype as TipoDocumento, " +
         	"		inv.documentno, " +
         	"		inv.c_bpartner_id, " +
         	"		cbp.c_bpartner_name, " +
         	"		inv.dateacct, " +
         	"		inv.dateinvoiced, " +
         	"		cbp.taxid, " +
         	"		cbp.c_categoria_iva_id, " +
         	"		cci.c_categoria_via_name, " +
         	"		currencyconvert(inv.netamount, inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) as neto, " +
         	"		currencyconvert(inv.grandtotal, inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) as total, " +
         	"		ct.c_tax_name as item, " +
         	"		currencyconvert(cit.importe, inv.c_currency_id, " + moneda + ", inv.dateacct, c_conversiontype_id, inv.ad_client_id, inv.ad_org_id) as importe, " +
         	"       cdt.signo," +
         	"       cit.AD_Client_ID, " +
         	"       cbp.iibb " +
         	" from (select c_invoice.c_invoice_id, c_invoice.ad_client_id, c_invoice.ad_org_id, c_invoice.c_currency_id, c_invoice.c_conversiontype_id, c_invoice.documentno, c_invoice.c_bpartner_id, c_invoice.dateacct, c_invoice.dateinvoiced, c_invoice.totallines, c_invoice.grandtotal, c_invoice.issotrx, c_invoice.c_doctypetarget_id, c_invoice.fiscalalreadyprinted, c_invoice.netamount, c_invoice.cae  " +
         	"       from c_invoice " +
         	"		INNER JOIN c_doctype ON c_invoice.c_doctypetarget_id = c_doctype.c_doctype_id " +
         	"       where c_invoice.ad_client_id = ? "+ 
         	"			AND (c_invoice.isactive = 'Y') " +
        	"			AND (c_invoice.dateacct::date between ?::date and ?::date) " 
        	+ getOrgCheck("c_invoice"));
         
		// Si no es ambos
		if (!transaction.equals("B")) {
			// Si es transacción de ventas, C = Customer(Cliente)
			if (transaction.equals("C")) {
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
				
		sqlReal.append(LibroIVAUtils.getDocStatusFilter(transaction, "c_doctype", "c_invoice"));
         
         String dateOrder = isPurchase() ? "inv.dateinvoiced" : "inv.dateacct";		
         sqlReal.append(") inv " +
         	"     left join (select c_doctype_id, name as c_doctype_name,docbasetype , signo_issotrx as signo, doctypekey, isfiscaldocument, isfiscal, iselectronic " +
         	"				from c_docType) cdt on cdt.c_doctype_id = inv.c_doctypetarget_id " +
         	"     left join (Select c_tax_id, c_invoice_id, taxamt as importe, ad_client_id " +
         	" 		        from c_invoicetax) cit 	on cit.c_invoice_id = inv.c_invoice_id " +
         	"     left join (Select c_tax_id, name as c_tax_name " +
         	"				from c_tax) ct on ct.c_tax_id = cit.c_tax_id " +
         	"     left join (Select c_bpartner_id, name as c_bpartner_name, c_categoria_iva_id, taxid, iibb " +
         	" 				from c_bpartner) cbp on inv.c_bpartner_id = cbp.c_bpartner_id " +
         	"     left join (Select c_categoria_iva_id, name as c_categoria_via_name " +
         	"				from c_categoria_iva) cci 	on cbp.c_categoria_iva_id = cci.c_categoria_iva_id " +
         	"	  WHERE cdt.doctypekey not in ('RTR', 'RTI', 'RCR', 'RCI') "
         	+ LibroIVAUtils.getDocTypeFilter("cdt", "inv") +
         	"     ORDER BY "+ dateOrder +", inv.c_invoice_id, cbp.taxid, inv.c_doctypetarget_id, inv.documentno, cit.c_tax_id ASC"); 
        
 		PreparedStatement pstmt = null;
 		try
 		{
 			pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE,sqlReal.toString(), null, true); 			
 			pstmt.setInt(1, getAD_Client_ID());
 			pstmt.setTimestamp(2,date_from);
 			pstmt.setTimestamp(3,date_to);
 			ResultSet rs = pstmt.executeQuery();
 			invoiceID = -1;
 			neto = new BigDecimal(0);
 			totalFacturado = new BigDecimal(0);
 			// Para compras se deben intercambiar los signos
 			Integer signOfSignInt = "V".equals(transaction)?-1:1;
 			BigDecimal signOfSign = new BigDecimal(signOfSignInt);
 			BigDecimal realSign = signOfSign;
 			while(rs.next())
 			{
 		        // cargo los datos dentro de la tabla T_LibroIVA
 				X_T_LibroIva linea = new X_T_LibroIva(getCtx(),0,get_TrxName());
 				linea.setDirectInsert(true);
 				linea.setSkipHandlers(true);
 				// Esto es necesario dado que estamos usando SkipHandlers (no realiza ningun tipo de actividad previa/posterior al insert)
 				linea.setT_Libroiva_ID(DB.getNextID(getAD_Client_ID(), X_T_LibroIva.Table_Name, get_TrxName()));
 				
 				linea.setAD_PInstance_ID(getAD_PInstance_ID());
 				linea.setAD_Org_ID(getOrgID());
 				linea.setC_Invoice_ID(rs.getInt("c_invoice_id"));
 				linea.setDocumentNo(rs.getString("documentno"));
 				linea.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
 				linea.setC_Bpartner_name(rs.getString("c_bpartner_name"));
 				// FB - Indicado por Antonio el 19-08-2011
 				// Para IVA Compras debe mostrar la fecha de facturación (NO la contable).
 				if ("B".equals(transaction) || "V".equals(transaction)) {
 					linea.setDateAcct(rs.getTimestamp("dateinvoiced"));
 				// Para IVA Ventas muestra la fecha contable (que es igual a la fecha de facturación).
 				} else {
 					linea.setDateAcct(rs.getTimestamp("dateacct")); // debe utilizar la fecha de cg, no de facturacion	
 				}
				// --
 				linea.setTaxID(rs.getString("taxid"));
 				linea.setIIBB(rs.getString("iibb"));
 				linea.setC_Categoriaiva_ID(rs.getInt("c_categoria_iva_id"));
 				linea.setcategoria_name(rs.getString("c_categoria_via_name"));
 				realSign = rs.getBigDecimal("signo").multiply(signOfSign);
 				// indicar neto y total unicamente en la primer linea
 				if (invoiceID != linea.getC_Invoice_ID())
 				{
 					linea.setneto(rs.getBigDecimal("neto").multiply(realSign));
 					linea.settotalfacturado(rs.getBigDecimal("total").multiply(realSign));
 				}
 				linea.setitem(rs.getString("item"));
 				linea.setImporte(rs.getBigDecimal("importe").multiply(realSign));
 				linea.setC_DocType_ID(rs.getInt("C_DocType_ID"));
 				linea.setDateFrom(date_from);
 				linea.setDateTo(date_to);
 				linea.setTransactionType(transaction);
 				linea.save();
 				
 				if (invoiceID != linea.getC_Invoice_ID())
 				{
 					totalFacturado = totalFacturado.add(linea.gettotalfacturado());
 					neto = neto.add(linea.getneto());
 				}
 				invoiceID = linea.getC_Invoice_ID();
 			}
 			rs.close();
 			pstmt.close();
 			pstmt = null;
 			
 			
 			// CALCULAR TOTALES E INSERTARLOS AL FINAL DE LAS LINEAS DE DETALLE
 			// ----------------------------------------------------------------
 			// LOS TOTALES SON TOMADOS UNICAMENTE A PARTIR DE LA INFORMACION INSERTADA 
 			// EN LA TABLA T_LIBROIVA, FILTRANDO POR EL AD_PINSTANCE ESPECIFICO.
 			insertNewTotal("- TOTAL - ", neto, totalFacturado, null);
 			calculateTotal();

 		}
 		catch (Exception e)
 		{ 
 			log.saveError("T_LibroIVA - DoIt", e);
 		}	
 		return null;
	}	
	
	private void calculateTotal() throws Exception
	{
		String totales = getTotalSQLClause();
		PreparedStatement pstmt = DB.prepareStatement(totales, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			insertNewTotal(rs.getString("item"), null, null, rs.getBigDecimal("importe"));
	}
	
	
	private String getTotalSQLClause()
	{
		StringBuffer sql = new StringBuffer(
				" select item, coalesce(sum(importe),0) as importe" + 
				" from t_libroiva " +
				" where ad_pinstance_id = " + getAD_PInstance_ID() +
				" and dateacct is not null and c_invoice_id is not null " + 
		        " group by item " );
		return sql.toString();
	}
	
	private void insertNewTotal(String criterio, BigDecimal neto, BigDecimal total, BigDecimal importe)
	{
		X_T_LibroIva linea = new X_T_LibroIva(getCtx(),0,get_TrxName());
		linea.setAD_PInstance_ID(getAD_PInstance_ID());
		linea.setC_Invoice_ID(0);
		linea.setDocumentNo(criterio);
		linea.setC_BPartner_ID(0);
		linea.setC_Bpartner_name("");
		linea.setDateAcct(null); 
		linea.setTaxID("");
		linea.setIIBB("");
		linea.setC_Categoriaiva_ID(0);
		linea.setcategoria_name("");
		linea.setneto(neto);
		linea.settotalfacturado(total);
		linea.setitem("");
		linea.setImporte(importe);
		linea.setC_DocType_ID(0);
		linea.setDateFrom(date_from);
		linea.setDateTo(date_to);
		linea.setTransactionType(transaction);
		linea.save();
	}
	
	protected Integer getOrgID(){
		return Util.isEmpty(orgID)?0:orgID;
	}
	
	/**
	 * Validacion por organización
	 */
	protected String getOrgCheck(String alias)
	{
		alias = Util.isEmpty(alias)?"":alias+".";
		return (getOrgID() > 0 ? " AND "+alias+"AD_Org_ID = " + orgID : "") + " ";
	}
	
	public boolean isPurchase() {
		return "B".equals(transaction) || "V".equals(transaction);
	}
}
