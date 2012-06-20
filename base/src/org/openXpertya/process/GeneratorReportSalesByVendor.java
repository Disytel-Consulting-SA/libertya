package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.X_T_SaleByVendor;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class GeneratorReportSalesByVendor extends SvrProcess {

	/** Fecha desde para filtrar transacciones de ventas y compras */
	private Timestamp p_DateFrom;
	/** Fecha hasta para filtrar transacciones de ventas y compras */
	private Timestamp p_DateTo;
	/** Se buscan artículos de este proveedor */
	private Integer   p_BPartner_ID = 0;
	/** Organización para filtrar transacciones de ventas y compras */
	private Integer   p_Org_ID = 0;
	/** Organización para filtrar transacciones de ventas y compras */
	private Integer p_Client_ID = null;
	/** Categoría de productos en la que se está interesado */
	private Integer   p_ProductCategory_ID = 0;
	/** Almacén para obtener el stock actual */
	private Integer   p_Warehouse_ID = 0;
		
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		 for( int i = 0;i < para.length;i++ ) {
	            log.fine( "prepare - " + para[ i ] );

	            String name = para[ i ].getParameterName();

	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else
	            if(name.equals( "register_date" )) {
	            	   	p_DateFrom = ( Timestamp )para[ i ].getParameter();
	            	   	p_DateTo   = ( Timestamp )para[ i ].getParameter_To();
	            } else if (name.equals( "C_BPartner_ID" )) {
	            		p_BPartner_ID = ((BigDecimal)para[ i ].getParameter()).intValue();
	            } else if ((name.equals( "AD_Org_ID" ))) {
	            		p_Org_ID =((BigDecimal)para[ i ].getParameter()).intValue(); 
	            } else if (name.equals( "M_Product_Category_ID" )) {
	            		p_ProductCategory_ID = ((BigDecimal)para[ i ].getParameter()).intValue();
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
		 
		 p_Client_ID = Env.getAD_Client_ID(getCtx());
	}

	@Override
	protected String doIt() throws Exception {
		// Se borran datos en la tabla temporal que tengan mas de una semana.
    	// Se borran datos de informes previos.
		String sql	= "DELETE FROM T_SaleByVendor WHERE AD_Client_ID = "+ p_Client_ID +" AND AD_PInstance_ID = " + getAD_PInstance_ID()+ " OR CREATED < ('now'::text)::timestamp(6) - interval '7 days'";
        DB.executeUpdate(sql, get_TrxName());
       
        // Consulta que obtiene todos los artículos del proveedor que estan activos.
        // Aplica filtros según parámetros.
		sql = 
			" SELECT ppo.ad_client_id, " +
			"        ppo.ad_org_id, " +
			"        ppo.c_bpartner_id, " +
			"        ppo.m_product_id, " +
			"        p.value, " +
			"        p.m_product_category_id, " +
			"        COALESCE(bomQtyAvailable(ppo.m_product_id,?,0),0.0) as stock "+
            " FROM m_product_po ppo " +    
            " INNER JOIN m_product p ON (p.m_product_id = ppo.m_product_id) " +
            " WHERE (ppo.isactive = 'Y') "+
            "   AND (ppo.c_bpartner_id = ? OR 0 = ?) "+ 		
            "   AND (p.m_product_category_id = ? OR 0 = ?) "; 
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
 		try {
 			// Se prepara la consulta de artículos
 			pstmt = DB.prepareStatement(sql, get_TrxName());
 			int i = 1;
 			pstmt.setInt(i++, p_Warehouse_ID);
 			pstmt.setInt(i++, p_BPartner_ID);
 			pstmt.setInt(i++, p_BPartner_ID);
 			pstmt.setInt(i++,p_ProductCategory_ID);
 			pstmt.setInt(i++,p_ProductCategory_ID);
 			rs = pstmt.executeQuery();
 			// Por cada artículo de proveedor, se calculan las ventas y las compras y 
 			// se guardan los datos en la tabla temporal.
 			while(rs.next()) {
 		        // Se crea un nuevo registro en la tabla temporal donde se guardan los datos
 		        // del informe.
 				X_T_SaleByVendor line = new X_T_SaleByVendor(getCtx(),0,get_TrxName());
 				line.setAD_PInstance_ID(getAD_PInstance_ID());
 				line.setregister_date(p_DateTo);
 				// Datos generales del artículo.
 				line.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
 				line.setM_Product_ID(rs.getInt("m_product_id"));
 				line.setM_Product_Category_ID(rs.getInt("m_product_category_id"));
 				line.setValue(rs.getString("value"));
 				line.setstored_units(rs.getBigDecimal("stock"));
 				
 				// Datos de venta del articulo.
 				setProductSales(line);
 				
 				// Datos de compra del artículo.
 				setProductPurchases(line);

 				// Se guarda la línea.
 				if(!line.save()){
 					log.saveError("Error", "Cannot save report line in temporary table.");
 				}
 			}
 			// Se borran los valores de los parámetros para esta instacia ya que sino
 			// el Engine filtra tuplas por estos valores, lo cual no es correcto para este caso
 			// dado que los parámetros ya se utilizaron para poblar la tabla temporal T_SalesByVendor.
 			//DB.executeUpdate("DELETE FROM AD_PInstance_Para WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
 			DB.executeUpdate("UPDATE T_SaleByVendor SET AD_Org_ID = " + p_Org_ID + " WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
 		
 		} catch (Exception e) { 
 			log.saveError("T_SaleByVendor - doIt", e);
 			return "@Error@ " + e;
 		} finally {
 			try {
 				if (rs != null) rs.close();
 				if (pstmt != null) pstmt.close();
 			} catch (SQLException e) {}
 		}
 		
 	  return null;
	}
	
	/**
	 * Asigna los datos de venta a la línea de la tabla temporal.
	 */
	private void setProductSales(X_T_SaleByVendor line) throws Exception {
		String sql =
			" SELECT COALESCE(SUM(il.qtyinvoiced),0.0) AS QtyInvoiced, " +
			"        COALESCE(SUM(il.linenetamt),0.0) AS NetAmt, " +
			"        COALESCE(SUM(il.linetotalamt),0.0) AS TotalAmt  " +
			" FROM c_invoiceline il " +
			" INNER JOIN c_invoice i ON (il.c_invoice_id = i.c_invoice_id) " +
			//-- Facturas de venta completas o cerradas que esten activas.
			" WHERE  i.docstatus in ('CL', 'CO') AND i.isactive = 'Y' AND i.issotrx = 'Y' AND " +
			"       (i.dateinvoiced BETWEEN ? AND ?) AND " +
            "        il.m_product_id = ? AND " + 
            "       (i.ad_org_id = ? OR 0 = ?) AND" +
            "        i.AD_Client_ID = ? ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			int i = 1;
			pstmt.setTimestamp(i++,p_DateFrom);
			pstmt.setTimestamp(i++, p_DateTo);
			pstmt.setInt(i++, line.getM_Product_ID());
			pstmt.setInt(i++, p_Org_ID);
			pstmt.setInt(i++, p_Org_ID);
			pstmt.setInt(i++, p_Client_ID);
			rs = pstmt.executeQuery();
			// Cantidades por defecto.
			BigDecimal qtyInvoiced = BigDecimal.ZERO;
			BigDecimal netAmt = BigDecimal.ZERO;
			BigDecimal totalAmt = BigDecimal.ZERO;
			// Se obtienen las cantidades del registro
			if (rs.next()) {
				qtyInvoiced = rs.getBigDecimal("QtyInvoiced");
				netAmt = rs.getBigDecimal("NetAmt");
				totalAmt = rs.getBigDecimal("TotalAmt");
			}
			// Se asignan las cantidades a la linea.
			line.setsold_units(qtyInvoiced);
			line.settotalneto(netAmt);
			line.setTotalAmt(totalAmt);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while setting product sales", e);
			throw e;
 		} finally {
 			try {
 				if (rs != null) rs.close();
 				if (pstmt != null) pstmt.close();
 			} catch (SQLException e) {}
 		}
	}
	
	/**
	 * Asigna los datos de compra a la línea de la tabla temporal.
	 */
	private void setProductPurchases(X_T_SaleByVendor line) throws Exception {
		String sql =
			" SELECT COALESCE(SUM(iol.qtyentered),0.0) AS QtyPurchased " +
			" FROM m_inoutline iol " +
			" INNER JOIN m_inout io ON (iol.m_inout_id = io.m_inout_id) " +
			//-- Remitos de compra completos o cerrados que esten activos.
			" WHERE  io.docstatus in ('CL', 'CO') AND io.isactive = 'Y' AND io.issotrx = 'N' AND " +
			"      (io.movementdate BETWEEN ? AND ?) AND " +
			"       iol.m_product_id = ? AND " + 
			"      (io.ad_org_id = ? OR 0 = ?) AND" +
			"       io.ad_client_id = ? "; 

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			int i = 1;
			pstmt.setTimestamp(i++,p_DateFrom);
			pstmt.setTimestamp(i++, p_DateTo);
			pstmt.setInt(i++, line.getM_Product_ID());
			pstmt.setInt(i++, p_Org_ID);
			pstmt.setInt(i++, p_Org_ID);
			pstmt.setInt(i++, p_Client_ID);
			rs = pstmt.executeQuery();
			// Cantidades por defecto.
			BigDecimal qtyPurchased = BigDecimal.ZERO;
			// Se obtienen las cantidades del registro
			if (rs.next()) {
				qtyPurchased = rs.getBigDecimal("QtyPurchased");
			}
			// Se asignan las cantidades a la linea.
			line.setentered_units(qtyPurchased);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while setting product purchases", e);
			throw e;
 		} finally {
 			try {
 				if (rs != null) rs.close();
 				if (pstmt != null) pstmt.close();
 			} catch (SQLException e) {}
 		}
	}

	@Override
	protected String get_TrxName() {
		return super.get_TrxName();
	}
	
	
}
