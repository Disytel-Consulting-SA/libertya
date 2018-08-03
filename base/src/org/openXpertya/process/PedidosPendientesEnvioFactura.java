package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.openXpertya.model.X_T_OrderLine_Pending;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PedidosPendientesEnvioFactura extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		deleteOldRecords(X_T_OrderLine_Pending.Table_Name, getAD_PInstance_ID(), get_TrxName());
		// Parametros
		Integer clientID = !Util.isEmpty((Integer) getParametersValues().get("AD_CLIENT_ID"), true)
				? (Integer) getParametersValues().get("AD_CLIENT_ID") : Env.getAD_Client_ID(getCtx());
		Integer orgID = !Util.isEmpty((Integer) getParametersValues().get("AD_ORG_ID"), true)
				? (Integer) getParametersValues().get("AD_ORG_ID") : 0;
		String isSOTrx = (String)getParametersValues().get("ISSOTRX");
		Timestamp dateFrom = (Timestamp)getParametersValues().get("DATEORDERED");
		Timestamp dateTo = (Timestamp)getParametersValues().get("DATEORDERED_TO");
		Integer bPartnerID = (Integer)getParametersValues().get("C_BPARTNER_ID");
		Integer productID = (Integer)getParametersValues().get("M_PRODUCT_ID");
		String status = (String)getParametersValues().get("STATUS");
		// Armado de consulta
		StringBuffer sql = new StringBuffer(" SELECT o.ad_client_id, o.ad_org_id, o.isactive, "
													+ "o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, "
													+ "o.documentno, o.dateordered::date AS dateordered, "
													+ "o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, "
													+ "ol.c_orderline_id, ol.m_product_id, ol.qtyordered, ol.qtyinvoiced, "
													+ "ol.qtydelivered, ol.qtytransferred, ol.qtyreturned, "
													+ "ol.qtyordered - ol.qtyinvoiced AS pendinginvoice, "
													+ "ol.qtyreserved AS pendingdeliver ");
		sql.append(" FROM c_order o ");
		sql.append(" JOIN c_orderline ol ON o.c_order_id = ol.c_order_id ");
		sql.append(" WHERE o.ad_client_id = ? ");
		sql.append(" AND (ol.qtyreserved <> 0 OR ol.qtyordered <> ol.qtyinvoiced) ");
		sql.append(" AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) ");
		sql.append(" AND ol.m_product_id IS NOT NULL ");
		// Parámetros del informe
		// Organización
		if(!Util.isEmpty(orgID, true)){
			sql.append(" AND o.ad_org_id = ").append(orgID);
		}
		// Transacción de ventas
		sql.append(" AND o.issotrx = '").append(isSOTrx).append("'");
		// Fechas
		if(dateFrom != null){
			sql.append(" AND o.dateordered::date >= '")
					.append(Env.getDateFormatted(dateFrom))
					.append("'::date ");
		}
		if(dateTo != null){
			sql.append(" AND o.dateordered::date <= '")
					.append(Env.getDateFormatted(dateTo))
					.append("'::date ");
		}
		// Entidad Comercial
		if(!Util.isEmpty(bPartnerID, true)){
			sql.append(" AND o.c_bpartner_id = ").append(bPartnerID);
		}
		// Artículo
		if(!Util.isEmpty(productID, true)){
			sql.append(" AND ol.m_product_id = ").append(productID);
		}
		// Estado
		if(!Util.isEmpty(status, true)){
			// D = Pendiente de Entrega
			// I = Pendiente de Facturación
			if(status.equals("D")){
				sql.append(" AND ol.qtyreserved <> 0 ");
			}
			else if(status.equals("I")){
				sql.append(" AND ol.qtyordered <> ol.qtyinvoiced ");
			}
		}
		sql.append(" ORDER BY o.dateordered, o.documentno ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, clientID);
			rs = ps.executeQuery();
			X_T_OrderLine_Pending op;
			while (rs.next()) {
				op = new X_T_OrderLine_Pending(getCtx(), 0, get_TrxName());
				op.setClientOrg(clientID, orgID);
				op.setAD_PInstance_ID(getAD_PInstance_ID());
				
				op.setIsSOTrx(isSOTrx.equals("Y"));
				op.setC_BPartner_ID(!Util.isEmpty(bPartnerID, true) ? bPartnerID : rs.getInt("c_bpartner_id"));
				op.setM_Product_ID(!Util.isEmpty(productID, true) ? productID : rs.getInt("m_product_id"));
				op.setDateOrdered(rs.getTimestamp("dateordered"));
				op.setDatePromised(rs.getTimestamp("datepromised"));
				
				op.setC_Order_ID(rs.getInt("c_order_id"));
				op.setC_OrderLine_ID(rs.getInt("c_orderline_id"));
				op.setDocumentNo(rs.getString("documentno"));
				
				op.setQtyOrdered(rs.getBigDecimal("qtyordered"));
				op.setQtyInvoiced(rs.getBigDecimal("qtyinvoiced"));
				op.setQtyDelivered(rs.getBigDecimal("qtydelivered"));
				op.setQtyTransferred(rs.getBigDecimal("qtytransferred"));
				op.setQtyReturned(rs.getBigDecimal("qtyreturned"));
				
				op.setPendingDeliver(rs.getBigDecimal("pendingdeliver"));
				op.setPendingInvoice(rs.getBigDecimal("pendinginvoice"));
				
				if(!Util.isEmpty(status, true)){
					op.setStatus(status);
				}
				
				if(!op.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		} catch(Exception e){
			throw e;
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		
		return "";
	}

}
