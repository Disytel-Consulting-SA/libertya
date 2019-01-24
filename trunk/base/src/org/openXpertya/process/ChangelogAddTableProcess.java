package org.openXpertya.process;

/**
 * Proceso de incorporacion de una tabla a la nomina de tablas a incorporar en el changelog.
 * 
 * Si la tabla ya es parte del tableschema definido, entonces no la incorpora.

 */

import org.openXpertya.model.MTableSchema;
import org.openXpertya.model.MTableSchemaLine;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.replication.AbstractTerminalLaunchProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ChangelogAddTableProcess extends AbstractTerminalLaunchProcess {

	static String p_table 		= null;
	static String p_schema	 	= null;
	static String p_adduid  	= null;
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {

		// Para este proceso en particular se usa la cia 0
		Env.setContext(Env.getCtx(), "#AD_Client_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
		
		// Recuperar el schema sobre el cual incorporar la tabla
		int tableID = M_Table.getTableID(p_table);
		int schemaID = DB.getSQLValue(null, "SELECT AD_TableSchema_ID FROM AD_TableSchema WHERE lower(name) = '" + p_schema.toLowerCase() + "'");
		if (schemaID<=0 || tableID<=0) {
			System.err.println("No se pudo recuperar el ID de la tabla o el schema.  tableID: " + tableID + " schemaID:" + schemaID);
			System.exit(1);			
		}
		
		// Incorporar la tabla al schema
		MTableSchema schema = new MTableSchema(Env.getCtx(), schemaID, get_TrxName());
		MTableSchemaLine schemaLine = schema.addTable(tableID, true);
		System.out.println("Registrando la tabla " + p_table +  " en el esquema " + p_schema + "...");
		if (schemaLine==null || !schemaLine.save()) {
			String err = CLogger.retrieveErrorAsString();
			System.err.println("No se pudo incorporar la tabla al schema: " + (!Util.isEmpty(err)?err:"¿registrada previamente?"));
			if (!Util.isEmpty(err))
				System.exit(1);
		} 
		
		// Indicar que la tabla es bitacoreable
		M_Table table = M_Table.get(Env.getCtx(), tableID);
		if (!table.isChangeLog()) {
			System.out.println("Modificando metadatos de la tabla para generar bitacora...");
			table.setIsChangeLog(true);
			table.save();
		}
				
		// Crear columna AD_ComponentObjectUID si corresponde, tanto estructuralmente como en metadatos
		if ("Y".equalsIgnoreCase(p_adduid)) {
			System.out.println("Creando columna AD_ComponentObjectUID...");
			DB.executeUpdate("update ad_system set dummy = (SELECT addcolumnifnotexists('"+p_table+"','AD_ComponentObjectUID','varchar(100)'))");
			int columnID = DB.getSQLValue(null, "SELECT AD_Column_ID FROM AD_Column WHERE name = 'AD_ComponentObjectUID' AND AD_Table_ID = " + tableID);
			if (columnID>0) {
				System.err.println("La columna en metadatos ya existe!");
				System.exit(1);
			}

			M_Column newColumn = new M_Column(Env.getCtx(), 0, get_TrxName());
			newColumn.setColumnName("AD_ComponentObjectUID");
			newColumn.setName("AD_ComponentObjectUID");
			newColumn.setAD_Table_ID(tableID);
			newColumn.setAD_Reference_ID(DisplayType.String);
			newColumn.setAD_Element_ID(DB.getSQLValue(get_TrxName(), "SELECT AD_Element_ID FROM AD_Element WHERE AD_ComponentObjectUID = 'CORE-AD_Element-1010719'"));
			newColumn.setFieldLength(100);
			System.out.println("Creando metadatos para la columna AD_ComponentObjectUID...");
			if (!newColumn.save()) {
				System.err.println("No se pudo crear la columna: " + CLogger.retrieveErrorAsString());
				System.exit(1);
			}
		}
		
		return "FINALIZADO";
	}
	
	public static void main (String[] args) {
		if (args==null || args.length<3) {
			System.err.println("Se requieren 3 argumentos: 1) el nombre de la tabla a incorporara 2) el nombre del schema donde debe incluirse la tabla 3) crear columna UID (Y/N) ");
			System.exit(1);
		}
		p_table 	= args[0];
		p_schema	= args[1];
		p_adduid	= args[2];
		new ChangelogAddTableProcess().execute();
	}

}
