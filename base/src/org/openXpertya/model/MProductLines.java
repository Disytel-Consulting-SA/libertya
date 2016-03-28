package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MProductLines extends X_M_Product_Lines {

	/**
	 * @param ctx
	 * @param productID
	 * @param trxName
	 * @return el id de la línea de artículo asignada al artículo, null si no posee
	 */
	public static Integer getLineFromProduct(Properties ctx, Integer productID, String trxName){
		Integer productLine = null;
		String sql = "SELECT pg.M_Product_Lines_ID "
					+ "FROM M_Product p "
					+ "INNER JOIN M_Product_Category pc ON pc.M_Product_Category_ID = p.M_Product_Category_ID "
					+ "INNER JOIN M_Product_Gamas pg ON pg.M_Product_Gamas_ID = pc.M_Product_Gamas_ID "
					+ "WHERE p.M_Product_ID = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, productID);
			rs = ps.executeQuery();
			if(rs.next()){
				productLine = rs.getInt("M_Product_Lines_ID");
			}
			if(Util.isEmpty(productLine , true)){
				productLine = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return productLine;
	}
	
	public MProductLines(Properties ctx, int M_Product_Lines_ID, String trxName) {
		super(ctx, M_Product_Lines_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MProductLines(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
