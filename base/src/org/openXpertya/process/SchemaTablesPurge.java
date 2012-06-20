package org.openXpertya.process;

import java.util.List;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_TableSchemaLine;
import org.openXpertya.util.Msg;

public class SchemaTablesPurge extends SvrProcess {

	/** Esquema de tabla a limpiar */
	private Integer tableSchemaID = null;
	
	@Override
	protected void prepare() {
		tableSchemaID = getRecord_ID();
	}
	
	@Override
	protected String doIt() throws Exception {
		int deletedCount = 0;
		// Borra todas las líneas del esquema que no están dentro de la lista
		// (IsInList = 'N')
		List<PO> schemaLines = PO.find(getCtx(), "AD_TableSchemaLine", "(AD_TableSchema_ID = ?) AND (IsInList = 'N')", new Object[]{tableSchemaID}, null, get_TrxName());
		// Se ejecuta la consulta.
		try {
			for (PO schemaLine : schemaLines) {
				if(!schemaLine.delete(false, get_TrxName())){
					throw new Exception("ERROR al eliminar la linea del esquema con la tabla "+((X_AD_TableSchemaLine)schemaLine).get_TableName());
				}
				deletedCount++;
			}
		} catch (Exception e) {
			throw new Exception("Process Error", e);
		}
		return Msg.translate(getCtx(), "@Deleted@: # " + deletedCount);
	}


}
