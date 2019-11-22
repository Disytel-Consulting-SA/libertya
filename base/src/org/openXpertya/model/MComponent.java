package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MComponent extends X_AD_Component {

	/** Caracteres no permitidos */
	public static String[] FORBIDDEN_PREFIX_CHARARCTERS = {"-"};
	
	public MComponent(Properties ctx, int AD_Component_ID, String trxName) {
		super(ctx, AD_Component_ID, trxName);
	}

	public MComponent(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
    protected boolean makeAndSetComponentObjectUID(){
		if (!Util.isEmpty(getAD_ComponentObjectUID()))
			return true;
		boolean ok = true;
		// Seteo el campo AD_ComponentObjectUID
		List<String> list = new ArrayList<String>();
		list.add(getPrefix());
		list.add(get_TableName());
		list.add(String.valueOf(getID()));
		setAD_ComponentObjectUID(makeUID(list));
		return ok;
    }
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Solo caracteres permitidos
		for (String forbiddenChar : FORBIDDEN_PREFIX_CHARARCTERS) {
			if (getPrefix().contains(forbiddenChar)) {
				log.saveError("Error", "El prefijo no puede contener el caracter: " + forbiddenChar);
				return false;
			}
		}
		// Si estamos desarrollando un micro compoennte, validar que sea uno de los prefijos reservados 
		if (isMicroComponent()) { 
			int count = DB.getSQLValue(get_TrxName(), "SELECT COUNT(1) FROM AD_MicroComponentPrefix WHERE IsActive = 'Y' AND Prefix = '" + getPrefix() + "'");
			if (count<=0) {
				log.saveError("Error", "El prefijo especificado no se encuentra registrado en la tabla de prefijos de micro componentes reservados, o no se encuentra activo");
				return false;
			}
		}
		return true;
	}
}
