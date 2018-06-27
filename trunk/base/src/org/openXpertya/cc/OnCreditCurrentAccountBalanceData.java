package org.openXpertya.cc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class OnCreditCurrentAccountBalanceData extends
		CurrentAccountBalanceData {

	protected OnCreditCurrentAccountBalanceData(String paymentRule) {
		super(paymentRule);
	}

	@Override
	public void loadBalanceData() {
		CurrentAccountQuery caQuery = new CurrentAccountQuery(getBpartner().getCtx(), null, null, false, null, null,
				MInvoice.PAYMENTRULE_OnCredit, getBpartner().getID(), null);
		
		String sql = caQuery.getBalanceQuery();
		
		int client_Currency_ID = Env.getContextAsInt(getBpartner().getCtx(), "$C_Currency_ID");
		int clientID = Env.getAD_Client_ID(getBpartner().getCtx());
		
		PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	ps = DB.prepareStatement( sql, getBpartner().get_TrxName());
            int i = 1;
			// Parámetros de sqlDoc
			ps.setInt(i++, 1);
			ps.setInt(i++, client_Currency_ID);
			ps.setInt(i++, -1);
			ps.setInt(i++, client_Currency_ID);
			ps.setInt(i++, clientID);
			ps.setInt(i++, getBpartner().getID());
            
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
