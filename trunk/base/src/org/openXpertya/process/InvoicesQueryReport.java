package org.openXpertya.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class InvoicesQueryReport extends SvrProcess {

	// Constantes
	
	/** AND para la cláusula where */
	private static final String AND = " AND ";
	
	/** Alias de la tabla de Facturas */
	private static final String INVOICE_ALIAS = "i";
	
	/** Alias de la tabla de Tipos de Documento */
	private static final String DOCTYPE_ALIAS = "doc";
	
	// Variables de instancia
	
	/** Organización */
	private Integer orgID;
	
	/** Entidad Comercial */
	private Integer bpartnerID;
	
	/** Tipo de Documento */
	private Integer docTypeID;
	
	/** Nro de Documento */
	private String documentNo;
	
	/** Fecha desde de la fecha de facturación */
	private Date dateInvoicedFrom;
	
	/** Fecha hasta de la fecha de facturación */
	private Date dateInvoicedTo;
	
	/** Transacción de ventas */
	private Boolean isSOTrx;
	
		
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
        for( int i = 0;i < para.length;i++ ) {
            name = para[ i ].getParameterName();
            if( name.equals( "AD_Org_ID" )) {
                setOrgID(para[ i ].getParameterAsInt());
            }
            else if( name.equals( "C_BPartner_ID" )) {
                setBpartnerID(para[ i ].getParameterAsInt());
            }
            else if( name.equals( "DateInvoiced" )) {
                setDateInvoicedFrom((Date)para[ i ].getParameter());
                setDateInvoicedTo((Date)para[ i ].getParameter_To());
            }
            else if( name.equals( "C_DocType_ID" )) {
                setDocTypeID(para[ i ].getParameterAsInt());
            }
            else if( name.equals( "DocumentNo" )) {
                setDocumentNo((String)para[ i ].getParameter());
            }
            else if( name.equals( "IsSOTrx" )) {
                setIsSOTrx(((String)para[ i ].getParameter()).equals("Y"));
            }
        }
	}
	
	@Override
	protected String doIt() throws Exception {
		// Eliminación de registros antiguos
		deleteOldRecords("T_Invoice", getAD_PInstance_ID(), null);
		// Creación del insert masivo
		String massiveInsert = getMassiveInsert();
		// Ejecución del insert masivo
		DB.executeUpdate(massiveInsert, get_TrxName());		
		return "";
	}

	/**
	 * @return el insert masivo a la tabla temporal
	 */
	private String getMassiveInsert(){
		StringBuffer insert = new StringBuffer("INSERT INTO t_invoice ");
		insert.append(" (ad_pinstance_id, c_invoice_id, ad_client_id, ad_org_id, isactive,  created,  createdby,  updated,  updatedby,  issotrx,  documentno,  docstatus,  docaction,  processed,  posted,  c_doctype_id,  c_doctypetarget_id,  c_order_id,  description,  isapproved,  istransferred,  isprinted,  salesrep_id,  dateinvoiced,  dateprinted,  dateacct,  c_bpartner_id,  c_bpartner_location_id,  c_currency_id,  paymentrule,  c_paymentterm_id,  c_charge_id,  chargeamt,  totallines,  grandtotal,  m_pricelist_id,  istaxincluded,  c_campaign_id,  c_project_id,  ispaid,  c_cashline_id,  c_conversiontype_id,  ispayschedulevalid,  ref_invoice_id,  c_letra_comprobante_id,  datecai,  numerocomprobante,  puntodeventa,  cai,  cuit,  numerodedocumento,  c_region_id,  c_invoice_orig_id,  nombrecli,  caja,  invoice_adress,  fiscalalreadyprinted,  nroidentificcliente,  cae,  vtocae,  idcae,  caecbte,  caeerror,  createcashline,  iscopy,  authcode,  authmatch,  doctypename,  doctypeprintname,  docbasetype) ");
		insert.append(getInvoiceQuerySql());
		return insert.toString();
	}

	/**
	 * @return el sql de consulta para las factura a impactar en la tabla
	 *         temporal
	 */
	private String getInvoiceQuerySql(){
		// Consulta que determina los comprobantes a partir de los paramétros
		// Armar el where a partir de los parámetros
		StringBuffer whereClause = new StringBuffer();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// Transacción de ventas o compras, Doc Base type
		if(getIsSOTrx() != null){
			whereClause.append(" (" + INVOICE_ALIAS + ".issotrx = '"
					+ (getIsSOTrx() ? "Y" : "N") + "') ");
			whereClause.append(AND);
			whereClause.append(" (" + DOCTYPE_ALIAS + ".docbasetype IN "
					+ (getIsSOTrx() ? "('ARI', 'ARC')" : "('API', 'APC')")
					+ ") ");
			whereClause.append(AND);
		}
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			whereClause.append(" ("+INVOICE_ALIAS+".ad_org_id = "+getOrgID()+") ");
			whereClause.append(AND);
		}
		// Entidad Comercial
		if(!Util.isEmpty(getBpartnerID(), true)){
			whereClause.append(" (c_bpartner_id = "+getBpartnerID()+") ");
			whereClause.append(AND);
		}
		// Tipo de Documento
		if(!Util.isEmpty(getDocTypeID(), true)){
			whereClause.append(" (c_doctypetarget_id = "+getDocTypeID()+") ");
			whereClause.append(AND);
		}
		// Fecha desde
		if(getDateInvoicedFrom() != null){
			whereClause.append(" (dateinvoiced >= '"+df.format(getDateInvoicedFrom())+"'::date) ");
			whereClause.append(AND);
		}
		// Fecha hasta
		if(getDateInvoicedTo() != null){
			whereClause.append(" (dateinvoiced <= '"+df.format(getDateInvoicedTo())+"'::date) ");
			whereClause.append(AND);
		}
		// Nro de Documento
		if(!Util.isEmpty(getDocumentNo())){
			// Determino el operador, si contiene al menos un caracter %,
			// entonces usamos LIKE
			String operador = getDocumentNo().indexOf("%") != -1?" LIKE ":" = ";
			whereClause.append(" (documentno ").append(operador).append(
					"'" + getDocumentNo() + "')");
		}
		// Eliminar el AND posible final
		String finalWhereClause = whereClause.toString();
		if (!Util.isEmpty(finalWhereClause)
				&& finalWhereClause.endsWith(AND)) {
			finalWhereClause = finalWhereClause.substring(0, finalWhereClause
					.lastIndexOf(AND));
		}
		// Consulta final
		StringBuffer finalSQL = new StringBuffer(" SELECT ");
		finalSQL
				.append(+getAD_PInstance_ID()
						+ ", "
						+ INVOICE_ALIAS
						+ ".c_invoice_id, "
						+ INVOICE_ALIAS
						+ ".ad_client_id, "
						+ INVOICE_ALIAS
						+ ".ad_org_id, "
						+ INVOICE_ALIAS
						+ ".isactive,  "
						+ INVOICE_ALIAS
						+ ".created,  "
						+ INVOICE_ALIAS
						+ ".createdby,  "
						+ INVOICE_ALIAS
						+ ".updated,  "
						+ INVOICE_ALIAS
						+ ".updatedby,  "
						+ INVOICE_ALIAS
						+ ".issotrx,  documentno,  docstatus,  docaction,  "
						+ INVOICE_ALIAS
						+ ".processed,  posted,  "
						+ INVOICE_ALIAS
						+ ".c_doctype_id,  c_doctypetarget_id,  c_order_id,  "
						+ INVOICE_ALIAS
						+ ".description,  "
						+ INVOICE_ALIAS
						+ ".isapproved,  istransferred,  isprinted,  salesrep_id,  dateinvoiced,  dateprinted,  dateacct,  c_bpartner_id,  c_bpartner_location_id,  "
						+ INVOICE_ALIAS
						+ ".c_currency_id,  paymentrule,  c_paymentterm_id,  c_charge_id,  chargeamt,  totallines,  grandtotal,  m_pricelist_id,  istaxincluded,  "
						+ INVOICE_ALIAS
						+ ".c_campaign_id,  "
						+ INVOICE_ALIAS
						+ ".c_project_id,  ispaid,  c_cashline_id,  c_conversiontype_id,  ispayschedulevalid,  ref_invoice_id,  c_letra_comprobante_id,  datecai,  numerocomprobante,  puntodeventa,  cai,  cuit,  numerodedocumento,  "
						+ INVOICE_ALIAS
						+ ".c_region_id,  c_invoice_orig_id,  nombrecli,  caja,  invoice_adress,  fiscalalreadyprinted,  nroidentificcliente,  cae,  vtocae,  idcae,  caecbte,  caeerror,  createcashline,  iscopy,  authcode,  authmatch,  "
						+ DOCTYPE_ALIAS + ".name,  " + DOCTYPE_ALIAS
						+ ".printname, " + DOCTYPE_ALIAS + ".docbasetype ");
		finalSQL.append(" FROM ");
		finalSQL.append(MInvoice.Table_Name).append(" as ").append(INVOICE_ALIAS);
		finalSQL.append(" INNER JOIN c_doctype as " + DOCTYPE_ALIAS
				+ " ON (i.c_doctypetarget_id = " + DOCTYPE_ALIAS
				+ ".c_doctype_id) ");
		finalSQL.append(" WHERE ");
		finalSQL.append(whereClause);
		
		return finalSQL.toString();
	}
	
	// Getters y Setters
	
	private void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	private Integer getOrgID() {
		return orgID;
	}

	private void setDocTypeID(Integer docTypeID) {
		this.docTypeID = docTypeID;
	}

	private Integer getDocTypeID() {
		return docTypeID;
	}

	private void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	private String getDocumentNo() {
		return documentNo;
	}

	private void setDateInvoicedFrom(Date dateInvoicedFrom) {
		this.dateInvoicedFrom = dateInvoicedFrom;
	}

	private Date getDateInvoicedFrom() {
		return dateInvoicedFrom;
	}

	private void setDateInvoicedTo(Date dateInvoicedTo) {
		this.dateInvoicedTo = dateInvoicedTo;
	}

	private Date getDateInvoicedTo() {
		return dateInvoicedTo;
	}

	private void setIsSOTrx(Boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

	private Boolean getIsSOTrx() {
		return isSOTrx;
	}

	private void setBpartnerID(Integer bpartnerID) {
		this.bpartnerID = bpartnerID;
	}

	private Integer getBpartnerID() {
		return bpartnerID;
	}
}
