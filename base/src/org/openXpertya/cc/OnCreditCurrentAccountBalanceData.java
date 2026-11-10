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
            
            // dREHER Abril 25 verificar si la query requiere llenar los parametros aca o ya vienen llenos...
            if(sql.indexOf("?") > -1) {

			// Parámetros de sqlDoc
			ps.setInt(i++, 1);
			ps.setInt(i++, -1);
			ps.setInt(i++, client_Currency_ID);
			ps.setInt(i++, clientID);
			ps.setInt(i++, getBpartner().getID());
            
            }
            
            long t0 = System.currentTimeMillis();
            rs = ps.executeQuery();
            long elapsed = System.currentTimeMillis() - t0;
            System.out.println("[CC loadBalanceData] bp=" + getBpartner().getID() + " executeQuery " + elapsed + "ms");
            // Si tarda, se imprime la consulta completa para poder analizarla con EXPLAIN
            if (elapsed > 3000) {
            	System.out.println("[CC loadBalanceData] CONSULTA LENTA (" + elapsed + "ms): " + sql);
            }
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
