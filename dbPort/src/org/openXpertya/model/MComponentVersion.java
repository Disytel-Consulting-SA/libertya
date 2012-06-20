package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MComponentVersion extends X_AD_ComponentVersion {

	/** Uso de cache a fin de evitar una consulta en cada guardado o borrado */
	/* Ya fue seteado el MComponentVersion (bien con uno Component existente o con null)? */
	private static boolean 			 isSetcurrentComponentVersionInDevelopment 	= false;
	
	/* Componente actualmente en desarrollo */
	private static MComponentVersion currentComponentVersionInDevelopment 		= null;
	
	
	// Métodos de clase
	
	/**
	 * @return version de componente actualmente en desarrollo, 
	 * null caso contrario
	 */
	public static MComponentVersion getCurrentComponentVersion(Properties ctx, String trxName){
		
		/* Cache: Está seteado correctamente el valor? */
		if (isSetcurrentComponentVersionInDevelopment)
			return currentComponentVersionInDevelopment;
		
		String sql = "SELECT * " +
					 "FROM ad_componentversion " +
					 "WHERE currentDevelopment = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		currentComponentVersionInDevelopment = null;
		try{
			ps = DB.prepareStatement(sql,trxName);
			rs = ps.executeQuery();
			if(rs.next()){
				currentComponentVersionInDevelopment = new MComponentVersion(ctx, rs, trxName);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
				ps = null;
				rs = null;
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		/* Por null o por Component correcto, ya se seteó el valor de currentComponentVersionInDevelopment */
		isSetcurrentComponentVersionInDevelopment = true;
		return currentComponentVersionInDevelopment;
	}
	
	
	// Constructores
	
	public MComponentVersion(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MComponentVersion(Properties ctx, int AD_ComponentVersion_ID, String trxName) {
		super(ctx, AD_ComponentVersion_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		// Setear el campo nombre mediante el plubic name y la version
		MComponent component = new MComponent(getCtx(), getAD_Component_ID(), get_TrxName());
		setName(component.getPublicName() + " " + getVersion());
		
		return true;
	}
	
	
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		/* Si hubo un cambio, eliminar la información de caché */
		if (success)
			isSetcurrentComponentVersionInDevelopment = false;
		
		return success;
	}	//	afterSave
	
	
	@Override
    protected boolean makeAndSetComponentObjectUID(){
		if (!Util.isEmpty(getAD_ComponentObjectUID()))
				return true;
		boolean ok = true;
		// Seteo el campo AD_ComponentObjectUID
		MComponent component = new MComponent(getCtx(), getAD_Component_ID(), get_TrxName());
		List<String> list = new ArrayList<String>();
		list.add(component.getPrefix());
		list.add(get_TableName());
		list.add(String.valueOf(getID()));
		setAD_ComponentObjectUID(makeUID(list));
		return ok;
    }
	
}