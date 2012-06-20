package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MTableSchema;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class SchemaTablesAdd extends SvrProcess {

	/** Esquema de tabla al cual se le agregan las tablas */
	private Integer tableSchemaID = null;
	/** Prefijo de tablas a importar (null = todas) */
	private String tablePrefix = null;
	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        // Se obtienen los parámetros
        for( int i = 0;i < para.length;i++ ) {
            String name = para[i].getParameterName();

            if( para[i].getParameter() == null ) {
                ;
            } else if( name.equals( "TablePrefix" )) {
                tablePrefix = (String)para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
        // El esquema destino es el ID del registro que invocó el proceso.
		tableSchemaID = getRecord_ID();
	}
	
	@Override
	protected String doIt() throws Exception {
		// Cantidad total de tablas a agregar.
		int tablesCount = 0;  
		// Tablas agregadas correctamente.
		List<String> tablesAdded = new ArrayList<String>();
				
		// Consulta para obtener las tablas a agregar.
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT AD_Table_ID, TableName FROM AD_Table");
		if (tablePrefix != null) {
			sql.append(" WHERE TableName ilike '" + tablePrefix + "_%'");
		}
		sql.append(" ORDER BY TableName ASC");
		
		// Se obtiene el esquema de tabla destino.
		MTableSchema tableSchema = new MTableSchema(getCtx(), tableSchemaID, get_TrxName());
	
		// Ejecuta la consulta y agrega las líneas al esquema.
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			int tableID;
			String tableName;
			while (rs.next()) {
				tableID = rs.getInt("AD_Table_ID");
				tableName = rs.getString("TableName");
				tablesCount++;
				// Se agrega la tabla al esquema.
				if (tableSchema.addTable(tableID, false) != null) {
					tablesAdded.add(tableName);
				}
					
			}
		} catch (Exception e) {
			throw new Exception("Process Error", e);
		}
		
		// Se crea el log resultante de la agregación de tablas.
		if (tablesAdded.size() > 0) {
			addLog( 0,null,null,Msg.translate(getCtx(), "AddedTables"));
			for (String tableName : tablesAdded) {
				addLog(0, null, null, "- " + tableName);
			}
		}
		
		return Msg.parseTranslation(getCtx(), "@Total@: # " + tablesCount + ", @AddedTables@: # " + tablesAdded.size());
	}

}
