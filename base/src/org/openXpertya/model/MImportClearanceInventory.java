package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MImportClearanceInventory extends X_M_Import_Clearance_Inventory {

	/**
	 * Obtener los despachos utilizados para esta línea de inventario
	 * 
	 * @param ctx             contexto actual
	 * @param inventoryLineID id de inventario
	 * @param trxName         trx actual
	 * @return lista de despachos y cantidades utilizadas para este inventario
	 */
	public static List<MImportClearanceInventory> getFromInventoryLine(Properties ctx, Integer inventoryLineID, String trxName){
		List<MImportClearanceInventory> icis = new ArrayList<MImportClearanceInventory>();
		String sql = "select * from "+Table_Name+" where m_inventoryline_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, inventoryLineID);
			rs = ps.executeQuery();
			while(rs.next()) {
				icis.add(new MImportClearanceInventory(ctx, rs, trxName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return icis;
	}
	
	public MImportClearanceInventory(Properties ctx, int M_Import_Clearance_Inventory_ID, String trxName) {
		super(ctx, M_Import_Clearance_Inventory_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MImportClearanceInventory(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
