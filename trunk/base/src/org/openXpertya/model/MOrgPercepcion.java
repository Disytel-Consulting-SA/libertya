package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class MOrgPercepcion extends X_AD_Org_Percepcion {

	public static List<MOrgPercepcion> getOrgPercepciones(Properties ctx, String trxName){
		return getOrgPercepciones(ctx, Env.getAD_Org_ID(ctx), trxName);
	}
	
	// dREHER, TODO: en un futuro buscar configuracion de percepciones respetando org segun relacion padre/hijos ???
	public static List<MOrgPercepcion> getOrgPercepciones(Properties ctx, Integer orgID, String trxName){
		List<MOrgPercepcion> percepciones = new ArrayList<MOrgPercepcion>();
		String sql = "SELECT * FROM ad_org_percepcion WHERE ad_org_id = ? AND isactive = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, orgID);
			rs = ps.executeQuery();
			while(rs.next()){
				percepciones.add(new MOrgPercepcion(ctx, rs, trxName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return percepciones;
	}
	
	public MOrgPercepcion(Properties ctx, int AD_Org_Percepcion_ID,
			String trxName) {
		super(ctx, AD_Org_Percepcion_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MOrgPercepcion(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// dREHER, si la organizacion es Padre, no permitir guardar registro y mostrar error
		
		int AD_Org_ID = this.getAD_Org_ID();
		MOrg org = new MOrg(Env.getCtx(), AD_Org_ID, get_TrxName());
		if(org!=null){
			boolean isCarpeta = org.isSummary();
			if(isCarpeta){
				log.saveError("OrgPercepcionInOrgSummary", "No se pueden configurar percepciones en organizaciones del tipo carpeta!");
				return false;
			}
		}
		// Si el procesador está marcado como que soporta padrón, entonces no se
		// marca como convenio multilateral
		MRetencionProcessor processor = new MRetencionProcessor(getCtx(),
				getC_RetencionProcessor_ID(), get_TrxName());
		if(processor.isSupportRegister()){
			setIsConvenioMultilateral(false);
		}
		return true;
	}	
	
	public static MOrgPercepcion getOrgPercepcion(Properties ctx, Integer orgID, int taxID, String trxName){
		String sql = "SELECT * FROM ad_org_percepcion WHERE ad_org_id = ? AND isactive = 'Y' AND c_tax_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, orgID);
			ps.setInt(2, taxID);
			rs = ps.executeQuery();
			if(rs.next()){
				return (new MOrgPercepcion(ctx, rs, trxName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}
}
