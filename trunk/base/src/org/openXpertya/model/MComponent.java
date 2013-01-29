package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Util;

public class MComponent extends X_AD_Component {

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
}
