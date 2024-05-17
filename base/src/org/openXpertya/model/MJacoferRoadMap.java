package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocOptions;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.DocOptionsUtils;
import org.openXpertya.util.Util;

public class MJacoferRoadMap extends LP_M_Jacofer_RoadMap implements DocAction, DocOptions {

	public MJacoferRoadMap(Properties ctx, int M_Jacofer_RoadMap_ID, String trxName) {
		super(ctx, M_Jacofer_RoadMap_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MJacoferRoadMap(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
    @Override
    protected boolean beforeSave(boolean newRecord) {
    	int shipperID = (Integer) get_Value("M_Shipper_ID");
    	
    	if(!Util.isEmpty(shipperID,true)){
    		int vehicleID = (Integer) get_Value("M_Shipper_Vehicle_ID") != null ? (Integer) get_Value("M_Shipper_Vehicle_ID") : 0;
    		
    		MShipper shipper = new MShipper(p_ctx, shipperID, get_TrxName());
    		boolean isTrPropio = (Boolean) shipper.get_Value("Jacofer_IsTransportePropio");
    		
    		if(isTrPropio && Util.isEmpty(vehicleID,true)) {
    			log.saveError(null, "Se debe asignar un Camión.");
                return false;
    		}
    	}
    	
    	return true;
    } // beforeSave
	
	/**
	 * Actualiza los totales de la cabecera a partir de las líneas
	 * 
	 * @throws Exception en caso de error
	 */
	public void updateTotals() {
		String sql = "select coalesce(sum(io.jacofer_packagesqty),0) as packagesqty, " + 
					"		coalesce(sum(io.jacofer_capacity),0) as capacity, " + 
					"		coalesce(sum(io.jacofer_weight),0) as weight " + 
					"from m_jacofer_roadmapline rml " +
					"join m_inout io on io.m_inout_id = rml.m_inout_id " +
					"where rml.m_jacofer_roadmap_id = ? and rml.includeinout = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			if(rs.next()) {
				setTotal_Capacity(rs.getBigDecimal("capacity"));
				setTotal_PackagesQty(rs.getBigDecimal("packagesqty"));
				setTotal_Weight(rs.getBigDecimal("weight"));
			}
		} catch(Exception e) {
			log.severe("Error al actualizar los totales de la hoja de ruta");
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				log.severe("Error al actualizar los totales de la hoja de ruta");
			}
		}
	}
	
	@Override
	public void setProcessed(boolean processed) {
		super.setProcessed(processed);

		if (getID() == 0) {
			return;
		}

		String set = "SET Processed='" + (processed ? "Y" : "N")
				+ "' WHERE M_Jacofer_RoadMap_ID =" + getID();
		int noLine = DB.executeUpdate("UPDATE M_Jacofer_RoadMapLine " + set,
				get_TrxName());
	}
	
	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
        DocumentEngine engine = new DocumentEngine( this,getDocStatus());
        return engine.processIt(action,getDocAction(),log);
	}

	@Override
	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String prepareIt() {
		// Listar los remitos que se encuentran en otra hoja de ruta completa o cerrada
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String remitos = "";
			String sql = "select rml.m_inout_id, io.documentno " + 
						"from m_jacofer_roadmapline rml " +
						"join m_inout io on io.m_inout_id = rml.m_inout_id " +
						"where m_jacofer_roadmap_id = ? and includeinout = 'Y' " + 
						"	and exists (select m_jacofer_roadmapline_id " + 
						"			from m_jacofer_roadmapline ol " + 
						"			join m_jacofer_roadmap rm on rm.m_jacofer_roadmap_id = ol.m_jacofer_roadmap_id " + 
						"			where rm.m_jacofer_roadmap_id <> ? and includeinout = 'Y' and ol.m_inout_id = rml.m_inout_id and rm.docstatus in ('CO','CL'))";
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			ps.setInt(2, getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				remitos += (remitos.length() == 0?"":", ")+rs.getString("documentno");
			}
			// Existen remitos en otras hojas de ruta, error
			if(remitos.length() > 0) {
				setProcessMsg("Los siguientes numeros de remito existen en otras hojas de ruta completas o cerradas: "
						+ remitos);
				return DOCSTATUS_Invalid;
			}
			rs.close();
			ps.close();
		} catch(Exception e) {
			setProcessMsg(e.getMessage());
			return DOCSTATUS_Invalid;
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				log.severe("Error al verificar remitos en otras hojas de ruta");
			}
		}
		
		return DOCSTATUS_InProgress;
	}
	
	/**
	 * Elimina las líneas de la hoja de ruta actual que no se encuentran incluídas
	 * en esta hoja
	 * 
	 * @return cantidad de líneas eliminadas de la hoja de ruta actual que no se
	 *         encuentran incluídas en esta hoja
	 */
	protected int deleteNotIncludedInOuts() {
		String sql = "DELETE FROM m_jacofer_roadmapline WHERE includeinout = 'N' and m_jacofer_roadmap_id = " + getID();
		return DB.executeUpdate(sql, get_TrxName());
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String completeIt() {
		// Eliminar las líneas que no están incluídas
		deleteNotIncludedInOuts();
		setProcessed(true);
		return DOCSTATUS_Completed;
	}

	@Override
	public boolean postIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean voidIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean closeIt() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reActivateIt() {
		setProcessed(false);
		return true;
	}

	@Override
	public int getDoc_User_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {
		// Si el documento está completo, incorporar la opción de Cancelar y Reabrir
		if (DOCSTATUS_Completed.equals(docStatus)) {
			index = DocOptionsUtils.addAction(options, DocAction.ACTION_ReActivate, index);
			index = DocOptionsUtils.addAction(options, DocAction.ACTION_Close, index);
			index = DocOptionsUtils.addAction(options, DocAction.ACTION_Void, index);
		}
		return index;
	}
}
