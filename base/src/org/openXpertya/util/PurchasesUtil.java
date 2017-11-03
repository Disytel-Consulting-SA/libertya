package org.openXpertya.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.HTMLMsg.HTMLList;

public class PurchasesUtil {

	/**
	 * Realiza el control de los artículos del proveedor
	 * @return resultado
	 */
	public static CallResult controlVendorProducts(Properties ctx, Integer headerID, String headerIDColumnName, String detailTableName, Integer vendorID, String trxName){
		String sqlVendor = "select ol.m_product_id, p.value, p.name "
				+ "from "+detailTableName+" ol "
				+ "inner join m_product p on p.m_product_id = ol.m_product_id "
				+ "left join (select m_product_id "
				+ "				from m_product_po "
				+ "				where c_bpartner_id = ? and isactive = 'Y') po on ol.m_product_id = po.m_product_id "
				+ "where ol."+headerIDColumnName+" = ? and po.m_product_id is null";
    	PreparedStatement ps = null; 
    	ResultSet rs = null;
    	CallResult result = new CallResult();
    	try {
    		ps = DB.prepareStatement(sqlVendor, trxName);
    		ps.setInt(1, vendorID);
        	ps.setInt(2, headerID);
        	rs = ps.executeQuery();
        	HTMLMsg msg = new HTMLMsg();
			HTMLList list = msg.createList("onlyvendorproducts", "ul", Msg.getMsg(ctx, "OnlyVendorProducts"));
        	while(rs.next()){
				msg.createAndAddListElement(rs.getString("value"),
						rs.getString("value") + " - " + rs.getString("name"), list);
				result.setError(true);
        	}
        	msg.addList(list);
        	result.setMsg(msg.toString());
		} catch (Exception e) {
			result.setMsg(e.getMessage(), true);
		} finally {
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				result.setMsg(e2.getMessage(), true);
			}
		}
    	return result;
	}

}
