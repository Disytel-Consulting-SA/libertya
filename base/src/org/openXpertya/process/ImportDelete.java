/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class ImportDelete extends AbstractSvrProcess {

	@Override
	protected String doIt() throws Exception {
		StringBuffer sqlWhere = new StringBuffer("AD_Client_ID = "+getAD_Client_ID());
        M_Table table = new M_Table( getCtx(),getParamValueAsInt("AD_TABLE_ID"),get_TrxName());
        String tableName = table.getTableName();
        // Armar where en base a los valores de los parámetros de usuario y registros a eliminar
        // Si es usuario de login, agrego el filtro
        if(((String)getParametersValues().get("USER")).equalsIgnoreCase("L")) {
        	sqlWhere.append(" AND CreatedBy = "	+ Env.getAD_User_ID(getCtx()));
        }
        
        // Tipo de registros a eliminar
        if(!((String)getParametersValues().get("RECORDTYPE")).equalsIgnoreCase("A")) {
			sqlWhere.append(
					" AND I_IsImported = '" + (((String) getParametersValues().get("RECORDTYPE")).equalsIgnoreCase("I")
							? "Y"
							: "N") + "'");
        }
        
        // Delete
		String sql = "DELETE FROM " + tableName + " WHERE "+sqlWhere.toString();
        int    no  = DB.executeUpdate( sql,get_TrxName());
        String msg = Msg.translate( getCtx(),tableName + "_ID" ) + " #" + no;

        return msg;
    }
}
