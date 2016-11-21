package org.openXpertya.plugin.install;

import org.openXpertya.model.MTableSchema;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;



public class PostInstallBuilder extends ChangeLogXMLBuilder {
	
	// Constructores
	
	public PostInstallBuilder(String path, String fileName, Integer componentVersionID, String trxName) {
		super(path, fileName,componentVersionID, trxName);
	}
	
	public PostInstallBuilder(String path, String fileName, Integer componentVersionID, Integer changeLogIDFrom, Integer changeLogIDTo, Integer userID, String trxName, boolean validateChangelogConsistency, boolean disableInconsistentChangelog) {
		super(path, fileName, componentVersionID, changeLogIDFrom, changeLogIDTo, userID, trxName, validateChangelogConsistency, disableInconsistentChangelog);
	}


	@Override
	protected Integer getTableSchemaID() {
		MTableSchema schema = (MTableSchema)PO.findFirst(Env.getCtx(), "ad_tableschema", "upper(name) = upper(?)", new Object[]{"Data"}, null, null);
		return schema.getID();
	}
}
