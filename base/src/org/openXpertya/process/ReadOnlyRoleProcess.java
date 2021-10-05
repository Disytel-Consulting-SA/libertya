package org.openXpertya.process;

import java.util.HashMap;
import java.util.Map;

import org.openXpertya.model.X_AD_Form_Access;
import org.openXpertya.model.X_AD_Process_Access;
import org.openXpertya.model.X_AD_Role_OrgAccess;
import org.openXpertya.model.X_AD_Task_Access;
import org.openXpertya.model.X_AD_Window_Access;
import org.openXpertya.model.X_AD_Workflow_Access;
import org.openXpertya.util.DB;

public class ReadOnlyRoleProcess extends AbstractSvrProcess {

	private static Map<String, String> accessTables;
	
	static {
		accessTables = new HashMap<String, String>();
		accessTables.put(X_AD_Role_OrgAccess.Table_Name, "isReadOnly");
		accessTables.put(X_AD_Window_Access.Table_Name, "isReadWrite");
		accessTables.put(X_AD_Process_Access.Table_Name, "isReadWrite");
		accessTables.put(X_AD_Form_Access.Table_Name, "isReadWrite");
		accessTables.put(X_AD_Workflow_Access.Table_Name, "isReadWrite");
		accessTables.put(X_AD_Task_Access.Table_Name, "isReadWrite");
	}
	
	private String isReadOnlyParamValue() {
		return (String)getParametersValues().get("ISREADONLY");
	}
	
	public String isReadOnly() {
		return isReadOnlyParamValue();
	}
	
	public String isReadWrite() {
		return isReadOnlyParamValue().equals("Y")?"N":"Y";
	}
	
	@Override
	protected String doIt() throws Exception {
		// Aplicar el sólo lectura o no a todos los accesos del perfil actual
		for (String atn : accessTables.keySet()) {			
			DB.executeUpdate("UPDATE " + atn + " SET " + accessTables.get(atn) + " = '"
					+ getClass().getMethod(accessTables.get(atn)).invoke(this) + "' WHERE ad_role_id = "
					+ getRecord_ID(), get_TrxName());
		}
		
		return "@ProcessOK@";
	}

	
}
