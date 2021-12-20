package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MImportClearance extends X_M_Import_Clearance {

	/**
	 * Obtiene la cantidad de stock nacional de un artículo dado
	 * 
	 * @param ctx
	 * @param productID
	 * @param trxName
	 * @return el stock nacional
	 */
	public static BigDecimal getNationalStockFrom(Properties ctx, Integer productID, String trxName) {
		String sql = "select sum(coalesce(stock,0)-coalesce(clearanceqty,0)) " + 
				"from (select sum(qtyonhand) as stock, 0 as clearanceqty" + 
				"	from m_storage s" + 
				"	where m_product_id = " + productID + 
				"	union all " +
				"	select 0 as qtyonhand, sum(qty-qtyused) as clearanceqty" + 
				"	from m_import_clearance  ic" + 
				"	where m_product_id = ?) as q";
		return DB.getSQLValueBD(trxName, sql, productID);
	}
	
	/**
	 * @param ctx
	 * @param productID
	 * @param trxName
	 * @return los despachos de importación disponibles para usar de un artículo
	 */
	public static List<MImportClearance> getAvailablesImportClearance(Properties ctx, Integer productID, String trxName){
		return getImportClearanceFrom(ctx, productID, "qty > qtyused", true, trxName);
	}
	
	/**
	 * @param ctx
	 * @param productID
	 * @param trxName
	 * @return los despachos de importación utilizados
	 */
	public static List<MImportClearance> getUsedImportClearance(Properties ctx, Integer productID, String trxName){
		return getImportClearanceFrom(ctx, productID, "qtyused > 0", false, trxName);
	}
	
	/**
	 * Obtiene los despachos de importación de un artículo dado y una condición
	 * adicional dada, ordenado por fecha ascendente o descendente dependiendo el
	 * parámetro
	 * 
	 * @param ctx
	 * @param productID
	 * @param additionalWhereClause
	 * @param orderAsc
	 * @param trxName
	 * @return lista de despachos de importación del artículo parámetro y unas
	 *         condiciones dadas
	 */
	public static List<MImportClearance> getImportClearanceFrom(Properties ctx, Integer productID, String additionalWhereClause, boolean orderAsc, String trxName){
		List<MImportClearance> ics = new ArrayList<MImportClearance>();
		String sql = "select * from " + Table_Name + " where m_product_id = ?"
				+ (Util.isEmpty(additionalWhereClause, true) ? additionalWhereClause : " AND " + additionalWhereClause)
				+ " ORDER BY movementdate "+(orderAsc?"":" DESC ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, productID);
			rs = ps.executeQuery();
			while(rs.next()) {
				ics.add(new MImportClearance(ctx, rs, trxName));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return ics;
	}
	
	public MImportClearance(Properties ctx, int M_Import_Clearance_ID, String trxName) {
		super(ctx, M_Import_Clearance_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MImportClearance(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return la cantidad disponible de este despacho
	 */
	public BigDecimal getAvailableQty() {
		return getQty().subtract(getQtyUsed());
	}
}
