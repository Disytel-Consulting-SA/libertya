package org.openXpertya.plugin.install;

import org.openXpertya.model.MTableSchema;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;

public class PluginInstallBuilder extends ChangeLogXMLBuilder {

	// Constructores
	
	public PluginInstallBuilder(String path, String fileName, Integer componentVersionID, String trxName) {
		super(path, fileName, componentVersionID, trxName);
	}
	
	public PluginInstallBuilder(String path, String fileName, Integer componentVersionID, Integer changeLogIDFrom, Integer changeLogIDTo, Integer userID, String trxName) {
		super(path, fileName, componentVersionID, changeLogIDFrom, changeLogIDTo, userID, trxName);
	}

	// Heredados
	
	@Override
	protected Integer getTableSchemaID() {
		MTableSchema schema = (MTableSchema)PO.findFirst(Env.getCtx(), "ad_tableschema", "upper(name) = upper(?)", new Object[]{"Metadata"}, null, trxName);
		return schema.getID();
	}

}
