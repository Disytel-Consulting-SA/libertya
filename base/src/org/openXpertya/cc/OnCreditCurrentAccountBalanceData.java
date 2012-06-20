package org.openXpertya.cc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.util.DB;


public class OnCreditCurrentAccountBalanceData extends
		CurrentAccountBalanceData {

	protected OnCreditCurrentAccountBalanceData(String paymentRule) {
		super(paymentRule);
	}

	@Override
	public void loadBalanceData() {
		// Actualizar el saldo global de la entidad 
		String     sql                 = "SELECT " +
		
		"COALESCE((SELECT SUM(currencyBase(i.grandtotal,i.C_Currency_ID,i.DateOrdered, i.AD_Client_ID,i.AD_Org_ID)* cast(dt.signo_issotrx as numeric) )" +
		"			FROM C_Invoice i " +
		"			INNER JOIN c_doctype AS dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
		"			WHERE i.C_BPartner_ID=? and (docstatus IN ('CO','CL'))),0) " +
		" - " +
		"COALESCE((SELECT SUM(currencyBase(p.PayAmt,p.C_Currency_ID,p.DateTrx,p.AD_Client_ID,p.AD_Org_ID) * (case isreceipt WHEN 'Y' THEN 1 ELSE -1 END)) " +
		"			FROM C_Payment p " +
		"			WHERE p.C_BPartner_ID=? and (docstatus IN ('CO','CL'))),0) " +
		" -	" +
		"COALESCE((SELECT SUM(currencyBase(cl.amount,cl.C_Currency_ID,c.statementdate,cl.AD_Client_ID,cl.AD_Org_ID)) " +
		"			FROM C_cashline cl " +
		"			INNER JOIN c_cash as c ON (cl.c_cash_id = c.c_cash_id) " +
		"			LEFT JOIN c_invoice as inv ON (cl.c_invoice_id = inv.c_invoice_id) " +
		"			WHERE (CL.C_BPartner_ID=? or (cl.c_bpartner_id is null and inv.C_BPartner_ID=?)) and (cl.docstatus IN ('CO','CL'))),0) ";			

		PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            ps = DB.prepareStatement( sql, getBpartner().get_TrxName());
            ps.setInt( 1, getBpartner().getID());
            ps.setInt( 2, getBpartner().getID());
            ps.setInt( 3, getBpartner().getID());
            ps.setInt( 4, getBpartner().getID());
            
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
