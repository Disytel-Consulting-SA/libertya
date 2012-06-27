package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.openXpertya.util.DB;

public class VendorMovements extends SvrProcess {
	/** Entidad Comercial de los comprobantes a consultar */
	private Integer p_C_BPartnerID;
	/** Fecha inicial del rango de fechas de la transacción */
	private Timestamp  p_Date_From;
	/** Fecha final del rango de fechas de la transacción */
	private Timestamp  p_Date_To;
	/** Incluir documentos no fiscales */
	private boolean    p_includeDocumentNoFiscal;
	private String    p_includeDocumentNoFiscal_char;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_BPartner_ID" )) {
            	p_C_BPartnerID = ((BigDecimal)para[ i ].getParameter()).intValue();
            } else if( name.equalsIgnoreCase( "isfiscaldocument" )) {
            	p_includeDocumentNoFiscal_char 	= ( String )para[ i ].getParameter();
            	p_includeDocumentNoFiscal 		= "Y".equals(( String )para[ i ].getParameter());
           } else if( name.equalsIgnoreCase( "DateInvoiced" )) {
            	p_Date_From = ( Timestamp )para[ i ].getParameter();
            	p_Date_To = ( Timestamp )para[ i ].getParameter_To();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		StringBuffer sqlInvoice = new StringBuffer();
		sqlInvoice.append(" SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, d.name, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, NULL::unknown AS duedate, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, ");
		sqlInvoice.append("CASE ");
		sqlInvoice.append("WHEN \"substring\"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric ");
		sqlInvoice.append("ELSE 1::numeric ");
		sqlInvoice.append("END AS multiplierap, d.docbasetype, d.isfiscaldocument ");
		sqlInvoice.append("FROM c_invoice i ");
		sqlInvoice.append("JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id ");
		sqlInvoice.append("WHERE i.ispayschedulevalid <> 'Y'::bpchar AND i.issotrx = 'N'::bpchar AND i.docstatus <> 'DR' ");
		sqlInvoice.append("UNION ALL ");  
		sqlInvoice.append("SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, d.name, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, ips.duedate, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, ");
		sqlInvoice.append("CASE ");
		sqlInvoice.append("WHEN \"substring\"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric ");
		sqlInvoice.append("ELSE 1::numeric ");
		sqlInvoice.append("END AS multiplierap, d.docbasetype, d.isfiscaldocument ");
		sqlInvoice.append("FROM c_invoice i ");
		sqlInvoice.append("JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id ");
		sqlInvoice.append("JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id ");
		sqlInvoice.append("WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar AND i.issotrx = 'N'::bpchar AND i.docstatus <> 'DR'");       
		sqlInvoice.append("; ");
		PreparedStatement pstmt = DB.prepareStatement(sqlInvoice.toString());
				
		ResultSet rs = pstmt.executeQuery();
		StringBuffer usql = new StringBuffer();
		while (rs.next())
		{
			usql.append(" INSERT INTO t_vendormovements (AD_PINSTANCE_ID, AD_CLIENT_ID, AD_ORG_ID, ISACTIVE, CREATED, CREATEDBY, UPDATED, UPDATEDBY, ISSOTRX, DOCUMENTNO, C_DOCTYPE_ID, DATEACCT, DUEDATE, DATEORDERED, C_BPARTNER_ID, C_INVOICE_ID, NAME, DATEINVOICED, ISPAID, DOCBASETYPE, ISFISCALDOCUMENT) " +
				    " VALUES (" + getAD_PInstance_ID() + ", "  +
				    			  getAD_Client_ID() + ", " +
				    			  rs.getInt("AD_ORG_ID") + ", '" + 
				    			  rs.getString("IsActive") + "' , '" + 
				    			  rs.getTimestamp("Created") + "'::timestamp ," + 
				    			  rs.getInt("CreatedBy") + ", '" + 
				    			  rs.getTimestamp("Updated") + "'::timestamp, " +
				    			  rs.getInt("UpdatedBy") + ", '" + 
				    			  rs.getString("issotrx") +  "' , '" + 
				                  rs.getString("DocumentNo") + "' , " + 
				                  rs.getInt("C_DocType_ID") + ", " +
				                  (rs.getTimestamp("DateAcct") == null? null : "'"+ rs.getTimestamp("DateAcct") + "'::timestamp")+ ", " +		
				                  (rs.getTimestamp("DueDate") == null? null : "'"+ rs.getTimestamp("DueDate") + "'::timestamp")+ ", " +
				                  (rs.getTimestamp("DateOrdered") == null? null : "'"+ rs.getTimestamp("DateOrdered") + "'::timestamp")+ ", " +
				                  rs.getInt("C_BPartner_ID") + ", " +
				                  rs.getInt("C_Invoice_ID") + ", '" +
				                  rs.getString("Name") + "', " + 
				                  (rs.getTimestamp("DateInvoiced") == null? null : "'"+ rs.getTimestamp("DateInvoiced") + "'::timestamp")+ ", '" +
				                  rs.getString("IsPaid") +  "' , '" +
				                  rs.getString("docbasetype") + "' , " + 
				                  (p_includeDocumentNoFiscal? "'Y'" : ( ("Y".equals(rs.getString("Isfiscaldocument"))? "'N'" : "'Y'") ) ) + ");");
		}
		
		if (usql.length() > 0)
			// Se insertan todas las líneas en la tabla.
			DB.executeUpdate(usql.toString(), get_TrxName());
		
		return "";
	}

}
