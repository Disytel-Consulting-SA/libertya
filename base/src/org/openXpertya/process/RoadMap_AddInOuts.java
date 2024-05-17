package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.LP_M_Jacofer_RoadMapLine;
import org.openXpertya.model.MJacoferRoadMap;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class RoadMap_AddInOuts extends AbstractSvrProcess {

	public RoadMap_AddInOuts() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doIt() throws Exception {
		// Registro actual del roadmap
		Integer roadMapID = getRecord_ID();
		// Busco los remitos de salida con vía de entrega Entrega o Transporte, no
		// asociados a ninguna Hoja de Ruta
		String sql = "select io.m_inout_id " + 
				"from m_inout io " + 
				"join c_doctype dt on dt.c_doctype_id = io.c_doctype_id " + 
				"where io.docstatus in ('CO','CL') " + 
				"	and io.issotrx = 'Y' " +
				"	and io.deliveryviarule in ('D','S') " +
				"	and dt.signo_issotrx = '-1' " + 
				"	and io.m_inout_id not in (select m_inout_id " + 
				"								from m_jacofer_roadmapline rl " + 
				"								join m_jacofer_roadmap r on r.m_jacofer_roadmap_id = rl.m_jacofer_roadmap_id " + 
				"								where (docstatus in ('CO','CL','IP') and includeinout = 'Y') or r.m_jacofer_roadmap_id = ?)";
		PreparedStatement ps = DB.prepareStatement(sql, get_TrxName());
		ps.setInt(1, roadMapID);
		ResultSet rs = ps.executeQuery();
		LP_M_Jacofer_RoadMapLine rmLine = null;
		int lines = 0;
		int lastLine = DB.getSQLValue(get_TrxName(),
				"select coalesce(max(line),0) from m_jacofer_roadmapline where m_jacofer_roadmap_id = ?", roadMapID);
		while (rs.next()) {
			rmLine = new LP_M_Jacofer_RoadMapLine(getCtx(), 0, get_TrxName());
			rmLine.setM_Jacofer_RoadMap_ID(roadMapID);
			rmLine.setM_InOut_ID(rs.getInt("m_inout_id"));
			rmLine.setLine(++lastLine);
			if(!rmLine.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			lines++;
		}
		
		// Si se creó al menos uno, entonces actualizo la cabecera
		if(lines > 0) {
			MJacoferRoadMap rm = new MJacoferRoadMap(getCtx(), roadMapID, get_TrxName());
			rm.updateTotals();
			if(!rm.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		
		rs.close();
		ps.close();
		
		return "Cantidad de remitos agregados a la hoja de ruta: "+lines;
	}

}
