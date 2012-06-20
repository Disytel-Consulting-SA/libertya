package org.openXpertya.process;

import java.sql.ResultSet;
import java.util.logging.Level;
import org.openXpertya.model.X_T_Allocation_Detail;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;

public class OrdenPagoReport extends SvrProcess {

	private int p_c_allocationhdr_id = 0;
	
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
        	Object pp = para[ i ].getParameter();
            String name = para[ i ].getParameterName();

            if( pp != null ) {
	            if ( name.equalsIgnoreCase( "C_AllocationHdr_ID" )) {
	            	p_c_allocationhdr_id = para[ i ].getParameterAsInt();
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		// delete all rows older than a week
    	// borro la antigua consulta si la hay
		
		 String	sql_del	= "DELETE FROM T_Allocation_Detail WHERE AD_Client_ID = "+ getAD_Client_ID()+" AND (AD_PInstance_ID = " + getAD_PInstance_ID() + " OR CREATED < ('now'::text)::timestamp(6) - interval '7 days' )";
         DB.executeUpdate(sql_del, get_TrxName());
		
        // busco los datos que se necesitan mostrar
		
		StringBuffer sql = new StringBuffer("");
		
		sql.append(" SELECT  c_allocation_detail_v_id, ad_client_id, ad_org_id, fecha, factura, c_currency_id, montofactura, PagoNro, Tipo, Cash, MontoSaldado FROM c_allocation_detail_v WHERE c_allocation_detail_v_id = ");
		sql.append( p_c_allocationhdr_id );
		
    try {
			
			CPreparedStatement ps = DB.prepareStatement(sql.toString(), get_TrxName());
				
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				/* Creo una nueva línea para ver en el informe */
				
				X_T_Allocation_Detail tad = new X_T_Allocation_Detail(getCtx(),0, get_TrxName());
				
				/* seteo los datos del T_allocation_Detail */
				tad.setAD_PInstance_ID(getAD_PInstance_ID());
				tad.setC_AllocationHdr_ID(rs.getInt("c_allocation_detail_v_id"));
				tad.setDateTrx(rs.getTimestamp("fecha"));
				tad.setDocumentNo(rs.getString("factura"));
				tad.setC_Currency_ID(rs.getInt("c_currency_id"));
				tad.setGrandTotal(rs.getBigDecimal("montofactura"));
				if (rs.getString("PagoNro")!= null)
					tad.setpagonro(rs.getString("PagoNro"));
				tad.settipo(rs.getString("Tipo"));
				tad.setcash(rs.getBoolean("Cash"));
				tad.setmontosaldado(rs.getBigDecimal("MontoSaldado"));
				 
				/* Salvo los cambios */
				tad.save();
			}
			
		} catch (Exception e) {
			log.saveError("Error ", "OrdenPagoReport - "+ e);
		}
		
		return "";
	}

}
