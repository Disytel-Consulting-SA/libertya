package org.openXpertya.cc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class OnCreditCurrentAccountBalanceData extends
		CurrentAccountBalanceData {

	protected OnCreditCurrentAccountBalanceData(String paymentRule) {
		super(paymentRule);
	}

	@Override
	public void loadBalanceData() {
		// Actualizar el saldo global de la entidad 
		String     sql                 = "SELECT " +
		/**
		 * El saldo de las Invoices se calcula haciendo:
		 * La sumatoria de lo facturado (amount + writeoffamt + discountamt de C_AllocationLine) 
		 * +  
		 * La sumatoria de lo pendiente por las Facturas (invoiceOpen) convertido a tasa actual.
		 */
		"COALESCE((SELECT SUM ((select (CASE WHEN SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END ) ) IS NULL THEN 0.0 ELSE SUM((al.amount + al.writeoffamt + al.discountamt) * cast(dt.signo_issotrx as numeric)) END) FROM C_AllocationLine al WHERE (al.c_invoice_id = i.c_invoice_id) OR (al.c_invoice_credit_id = i.c_invoice_id))) " +
		"			FROM C_Invoice i " +
		"			INNER JOIN c_doctype AS dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
		"			WHERE i.C_BPartner_ID=? and (docstatus IN ('CO','CL')))" +
		" 		  + " +
		" 		  (SELECT SUM (currencyconvert(invoiceOpen(i.c_invoice_id, (SELECT C_InvoicePaySchedule_ID FROM c_invoicepayschedule ips WHERE ips.c_invoice_id = i.c_invoice_id)), i.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(i.c_conversiontype_id,0), i.ad_client_id, i.ad_org_id )) " + 
		" 		   FROM C_Invoice i " +
		"          INNER JOIN c_doctype AS dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
		"          WHERE i.C_BPartner_ID=? and (docstatus IN ('CO','CL'))) " +
		",0) " +
		" - " +
		/**
		 * El saldo de los Payments se calcula haciendo:
		 * La sumatoria de lo cobrado (amount de C_AllocationLine) 
		 * +  
		 * La sumatoria de lo pendiente por las pagos (paymentavailable) convertido a tasa actual.
		 */
		"COALESCE((SELECT SUM ((select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM((al.amount) * (case isreceipt WHEN 'Y' THEN 1 ELSE -1 END)) END) FROM C_AllocationLine al WHERE (al.c_payment_id = p.c_payment_id)))  " +
		"			FROM C_Payment p " +
		"			WHERE p.C_BPartner_ID=? and (docstatus IN ('CO','CL')))" +
		" 			+ " +
		" 			(SELECT SUM (currencyconvert(paymentavailable(p.c_payment_id), p.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) ) " + 
		" 			FROM C_Payment p " +
		" 			WHERE p.C_BPartner_ID=? and (docstatus IN ('CO','CL'))) " +
		",0) " +
		" -	" +
		/**
		 * El saldo de las CashLine se calcula haciendo:
		 * La sumatoria de lo cobrado (amount de C_AllocationLine) 
		 * +  
		 * La sumatoria de lo pendiente por las pagos (cashlineavailable) convertido a tasa actual.
		 */
		"COALESCE((SELECT SUM((select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = cl.c_cashline_id)) ) " +
		"			FROM C_cashline cl " +
		"			INNER JOIN c_cash as c ON (cl.c_cash_id = c.c_cash_id) " +
		"			LEFT JOIN c_invoice as inv ON (cl.c_invoice_id = inv.c_invoice_id) " +
		"			WHERE (CL.C_BPartner_ID=? or (cl.c_bpartner_id is null and inv.C_BPartner_ID=?)) and (cl.docstatus IN ('CO','CL')))" +
		" 			+ " +
		" 		  (SELECT SUM (currencyconvert(cashlineavailable(cl.c_cashline_id), cl.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(inv.c_conversiontype_id,0), cl.ad_client_id, cl.ad_org_id ) ) " + 
		"			FROM C_cashline cl " +
		" 			INNER JOIN c_cash as c ON (cl.c_cash_id = c.c_cash_id) " +
		"  			LEFT JOIN c_invoice as inv ON (cl.c_invoice_id = inv.c_invoice_id) " +
		" 			WHERE (CL.C_BPartner_ID=? or (cl.c_bpartner_id is null and inv.C_BPartner_ID=?)) and (cl.docstatus IN ('CO','CL'))) " +
		",0) ";			
		
		int client_Currency_ID = Env.getContextAsInt(getBpartner().getCtx(), "$C_Currency_ID");
		
		PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            ps = DB.prepareStatement( sql, getBpartner().get_TrxName());
            ps.setInt( 1, getBpartner().getID());
            ps.setInt( 2, client_Currency_ID);
            ps.setInt( 3, getBpartner().getID());
            ps.setInt( 4, getBpartner().getID());
            ps.setInt( 5, client_Currency_ID);
            ps.setInt( 6, getBpartner().getID());
            ps.setInt( 7, getBpartner().getID());
            ps.setInt( 8, getBpartner().getID());
            ps.setInt( 9, client_Currency_ID);
            ps.setInt( 10, getBpartner().getID());
            ps.setInt( 11, getBpartner().getID());
            
            rs = ps.executeQuery();
            if( rs.next()) {
//                setCreditUsed(rs.getBigDecimal( 1 ));
                setBalance(rs.getBigDecimal( 1 ));
//                setActualLifeTimeValue(rs.getBigDecimal( 3 ));
            }
        } catch( Exception e ) {
            e.printStackTrace();
        } finally{
        	try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
        }
	}

}
