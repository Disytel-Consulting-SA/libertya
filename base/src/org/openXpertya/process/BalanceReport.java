package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class BalanceReport extends SvrProcess {

	/** Organización de los comprobantes a consultar */
	private int    p_AD_Org_ID;
	/** Visualizar solo clientes con facturas impagas o todos (AL (All), OO (Open invoices Only) */
	private String     p_Scope; 
	/** Criterio de ordenamiento (BP (BPartner), BL (Balance), OI (Oldest open Invoice) */
	private String     p_Sort_Criteria;
	/** Grupo de entidad comercial */
	private int    p_C_BP_Group_ID;
	/** Fecha hasta de la transacción */
	private Timestamp  p_DateTrx_To;
	
	/** Moneda en la que trabaja la compañía */
	private int client_Currency_ID;
	
	@Override
	protected void prepare() {

        ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "Scope" )) {
            	p_Scope = (String)para[ i ].getParameter();
        	} else if( name.equalsIgnoreCase( "SortCriteria" )) {
            	p_Sort_Criteria = (String)para[ i ].getParameter();
        	} else if( name.equalsIgnoreCase( "AD_Org_ID" )) {
        		BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
        		p_AD_Org_ID = tmp == null ? null : tmp.intValue();
        	} else if( name.equalsIgnoreCase( "C_BP_Group_ID" )) {
        		p_C_BP_Group_ID = para[ i ].getParameterAsInt();
        	} else if( name.equalsIgnoreCase( "TrueDateTrx" )) {
        		p_DateTrx_To = ( Timestamp )para[ i ].getParameter();
        	}
        }
        
     // Moneda de la compañía utilizada para conversión de montos de documentos.
        client_Currency_ID = Env.getContextAsInt(getCtx(), "$C_Currency_ID");
	}

	
	@Override
	protected String doIt() throws Exception {

		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_BALANCEREPORT WHERE DATECREATED < ('now'::text)::timestamp(6) - interval '7 days'");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_BALANCEREPORT WHERE AD_PInstance_ID = " + getAD_PInstance_ID());

		// calcular el estado de cuenta de cada E.C.
		StringBuffer sqlDoc = new StringBuffer();
		sqlDoc.append(" SELECT T.C_BPARTNER_ID, T.NAME, C_BP_Group_ID, COALESCE(T.so_description,'') AS so_description, COALESCE(SUM(t.Credit),0.0) AS Credit, COALESCE(SUM(t.Debit),0.0) AS Debit, COALESCE(SUM(t.Credit),0.0) - COALESCE(SUM(t.Debit),0.0) AS balance,  ");
		sqlDoc.append(" 	( SELECT dateacct FROM C_INVOICE WHERE issotrx = 'Y' AND invoiceopen(c_invoice_id, 0) > 0	AND C_BPartner_id = t.c_bpartner_id ORDER BY DATEACCT asc LIMIT 1 ) as fecha_fact_antigua, ");
		sqlDoc.append(" 	( SELECT dateacct FROM C_INVOICE WHERE issotrx = 'Y' AND invoiceopen(c_invoice_id, 0) > 0	AND C_BPartner_id = t.c_bpartner_id ORDER BY DATEACCT desc LIMIT 1 ) as fecha_fact_reciente ");
		sqlDoc.append(" FROM ");
		sqlDoc.append(" ( ");
		sqlDoc.append(" 	SELECT " ); 
		sqlDoc.append(" 		d.c_bpartner_id, ");
		sqlDoc.append(" 		bp.name, ");
		sqlDoc.append(" 		bp.C_BP_Group_ID, ");
		sqlDoc.append(" 		bp.so_description, ");
		sqlDoc.append(" 		CASE WHEN d.signo_issotrx = 1 THEN "); 
		sqlDoc.append(" 			currencyconvert(d.amount, d.c_currency_id, ").append(client_Currency_ID).append(", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) "); 
		sqlDoc.append(" 		ELSE 0.0 END AS Debit, "); 
		sqlDoc.append(" 		CASE WHEN d.signo_issotrx = -1 THEN "); 
		sqlDoc.append(" 			currencyconvert(d.amount, d.c_currency_id, ").append(client_Currency_ID).append(", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) "); 
		sqlDoc.append(" 		ELSE 0.0 END AS Credit ");
		sqlDoc.append(" 	FROM V_Documents d "); 
		sqlDoc.append(" 	INNER JOIN c_bpartner bp on d.c_bpartner_id = bp.c_bpartner_id ");
		sqlDoc.append(" 	WHERE d.DocStatus IN ('CO', 'CL', 'RE', 'VO') ");
		if (p_AD_Org_ID > 0)			// filtrar comprobantes para una organizacion especifica (o no)
			sqlDoc.append(" AND d.AD_Org_ID = ").append(p_AD_Org_ID);
		if(p_C_BP_Group_ID > 0){		// filtrar comprobantes para un grupo de EC especifico (o no)
			sqlDoc.append(" AND bp.C_BP_Group_ID = ").append(p_C_BP_Group_ID);
		}
		if ("OO".equals(p_Scope))		// filtrar E.C.: solo las que adeudan, o listar todas
		{
			sqlDoc.append(" AND bp.C_BPartner_ID IN (");
			sqlDoc.append(" 	SELECT DISTINCT c_bpartner_id FROM c_invoice ");
			sqlDoc.append(" 	WHERE invoiceopen(c_invoice_id, 0) > 0 AND issotrx = 'Y' AND AD_Client_ID = ").append(getAD_Client_ID()).append(")");
		}
		sqlDoc.append(" 	AND d.AD_Client_ID = ").append(getAD_Client_ID()); 
		sqlDoc.append(" 	AND bp.isactive = 'Y' ");
		sqlDoc.append(" 	AND d.truedatetrx <= ?");
		sqlDoc.append(" ) AS T ");
		sqlDoc.append(" GROUP BY T.c_bpartner_id, T.name, T.C_BP_Group_ID, T.so_description ");
		sqlDoc.append(" ORDER BY ");
		if ("BP".equals(p_Sort_Criteria))
			sqlDoc.append("T.name");
		if ("BL".equals(p_Sort_Criteria))
			sqlDoc.append("balance");		
		if ("OI".equals(p_Sort_Criteria))
			sqlDoc.append("fecha_fact_antigua");		
		
		// ejecutar la consulta y cargar la tabla temporal
		PreparedStatement pstmt = DB.prepareStatement(sqlDoc.toString(), get_TrxName());
		int i = 1;
		// Parámetros de sqlDoc
		pstmt.setTimestamp(i++, p_DateTrx_To);
		ResultSet rs = pstmt.executeQuery();
		
		int subindice=0;
		StringBuffer usql = new StringBuffer();
		while (rs.next())
		{
			subindice++;
			usql.append(" INSERT INTO T_BALANCEREPORT (ad_pinstance_id, ad_client_id, ad_org_id, subindice, c_bpartner_id, observaciones, ");
			usql.append("								credit, debit, balance, date_oldest_open_invoice, date_newest_open_invoice, sortcriteria, scope, c_bp_group_id, truedatetrx ) ");
			usql.append(" VALUES ( ")	.append(getAD_PInstance_ID()).append(",")
										.append(getAD_Client_ID()).append(",")
										.append(p_AD_Org_ID).append(",")
										.append(subindice).append(",")
										.append(rs.getInt("C_BPartner_ID")).append(", '")
										.append(rs.getString("SO_DESCRIPTION")).append("', ")
										.append(rs.getBigDecimal("Credit")).append(",")
										.append(rs.getBigDecimal("Debit")).append(",")
										.append(rs.getBigDecimal("Balance")).append(", ");
			if (rs.getTimestamp("fecha_fact_antigua")!=null)
				usql.append(" '").append(rs.getTimestamp("fecha_fact_antigua")).append("', ");
			else
				usql.append("null, ");
			if (rs.getTimestamp("fecha_fact_reciente")!=null)
				usql.append(" '").append(rs.getTimestamp("fecha_fact_reciente")).append("', ");
			else
				usql.append("null, ");
			usql.append(" '").append(p_Sort_Criteria).append("', ")
				.append(" '").append(p_Scope).append("', ")
				.append(rs.getInt("C_BP_Group_ID")).append(", ");
			if (p_DateTrx_To!=null)
				usql.append(" '").append(p_DateTrx_To).append("' ");
			else
				usql.append("null ");
			usql.append(" ); ");
		}
		
		// si no hubo entradas directamente no se ejecuta sentencia de insercion alguna
		if (subindice > 0)
			DB.executeUpdate(usql.toString(), get_TrxName());
		
		return "OK";
		
	}


}
