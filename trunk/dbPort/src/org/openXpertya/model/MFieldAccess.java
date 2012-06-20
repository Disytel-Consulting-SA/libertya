package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Configuación de acceso a campo de pestaña para un perfil.
 * @author Franco Bonafine
 * @date 27/08/2008
 */
public class MFieldAccess extends X_AD_Field_Access {

	/** Static Logger */
	private static CLogger s_log = CLogger.getCLogger (MFieldAccess.class);

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param AD_Field_Access_ID ID del Acceso de Campo. 0 para nuevo objeto.
	 * @param trxName Nombre de la transacción
	 */
	public MFieldAccess(Properties ctx, int AD_Field_Access_ID, String trxName) {
		super(ctx, AD_Field_Access_ID, trxName);
	}

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param rs <code>ResultSet</code> que contiene los datos del objeto.
	 * @param trxName Nombre de la transacción
	 */
	public MFieldAccess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Carga todos los accesos a campos que tiene un perfil determinado.
	 * @param roleID Perfil del cual se obtienen los accesos de campos.
	 * @param trxName Nombre de la transacción de BD a utilizar en las consultas.
	 * @return <code>Map<Integer,MFieldAccess></code> donde la clave es el ID
	 * del campo y el valor es el acceso configurado para él.
	 */
	public static Map<Integer,MFieldAccess> getOfRole(int roleID, String trxName) {
		// Se crea una nueva Map.
		Map<Integer,MFieldAccess> roleFieldAccess = new HashMap<Integer,MFieldAccess>();
		List<Integer> fieldIDs = new ArrayList<Integer>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Consulta que obtiene todos los accesos configurado para el perfil.
		String sql = 
			" SELECT * FROM AD_Field_Access " +
			" WHERE AD_Role_ID = ? AND IsActive = 'Y'";
		
		try {
			// Se ejecuta la consulta.
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, roleID);
			rs = pstmt.executeQuery();
			// Se recorren los accesos configurados.
			while (rs.next()) {
				// ID de campo al que pertenece el acceso.
				int fieldID = rs.getInt("AD_Field_ID");
				MFieldAccess mFieldAccess = new MFieldAccess(Env.getCtx(), rs, trxName);
				// Se almacena el acceso para el campo en la Map de retorno.
				roleFieldAccess.put(fieldID, mFieldAccess);
				// Se guarda el ID de campo al que pertenece el acceso para calcular
				// los accesos de los campos hijos que dependen de este.
				fieldIDs.add(fieldID);
			}

		} catch (Exception e) {
			s_log.log(Level.SEVERE, sql, e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (rs != null) rs.close();
			} catch (Exception e) {}
		}
		s_log.fine("#" + roleFieldAccess.size());
		// Por cada campo que tiene una configuración de acceso, se cargan accesos para los campos
		// hijos que dependen de él según indica la lógica de visibilidad.
		for (Integer fieldID : fieldIDs) {
			loadChildrenFieldAccess(roleFieldAccess.get(fieldID), roleFieldAccess, trxName);
		}
		
		return roleFieldAccess;
	}
	
	/**
	 * Carga el tipo de acceso de un campo a todos los campos que son dependientes de él
	 * según la lógica de visibilidad.
	 */
	private static void loadChildrenFieldAccess(MFieldAccess fieldAccess, 
			Map<Integer,MFieldAccess> roleFieldAccess, String trxName) {
		
		// Se obtiene el campo "padre".
		M_Field mField = new M_Field(Env.getCtx(), fieldAccess.getAD_Field_ID(), trxName);
		// Se obtiene el nombre de columna a la que referencia el campo.
		String fieldColumnName = DB.getSQLValueString(trxName, 
				"SELECT ColumnName FROM AD_Column WHERE AD_Column_ID = ?", mField.getAD_Column_ID()); 
	
		// Consulta que obtiene todos los campos de la pestaña a la cual pertenece
		// el campo padre.
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM AD_Field WHERE AD_Tab_ID = ?";
		
		try {
			// Se ejecuta la consulta.
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, mField.getAD_Tab_ID());
			rs = pstmt.executeQuery();
			// Se recorren los potenciales campos hijos.
			while (rs.next()) {
				// Se obtiene el ID y la lógica de despliegue del campo.
				int auxField_ID = rs.getInt("AD_Field_ID");
				String displayLogic = rs.getString("DisplayLogic");
				// Se obtiene la lista de columnas de las que depende el campo.
				List<String> dependentColumnNames =	getDependensFieldColumNames(displayLogic);
				// Se verifica si la columna del campo padre esta contenida dentro
				// del conjunto de dependencias del campo hijo.
				if (dependentColumnNames.contains(fieldColumnName)) {
					// Si está contenida y no hay algun acceso configurado manualmente para
					// el campo, entonces se agrega el acceso del campo padre como acceso
					// del hijo.
					if (!roleFieldAccess.containsKey(auxField_ID))
						roleFieldAccess.put(auxField_ID, fieldAccess);
				}
			}

		} catch (Exception e) {
			s_log.log(Level.SEVERE, sql, e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (rs != null) rs.close();
			} catch (Exception e) {}
		}
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// Validación de registro duplicado.
		if (!validateDuplicatedFieldAccess()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),"DuplicatedAccess",
					new Object[] {Msg.translate(getCtx(),"Field")}));
			return false;
		}

		return true;
	}
	
	/**
	 * Validación de accesos de campos duplicados.
	 * @return TRUE en caso de que no exista un acceso duplicado, FALSE en caso contrario.
	 */
	private boolean validateDuplicatedFieldAccess() {
		String sql = " SELECT COUNT(*) FROM AD_Field_Access " +
				     " WHERE AD_Role_ID = ? AND AD_Tab_ID = ? AND " +
				     "       AD_Field_ID = ? AND AD_Field_Access_ID <> ? ";
		
		// Filtro de acceso a la consulta.
		sql = MRole.getDefault().addAccessSQL( sql, "AD_Field_Access", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO );
		
		Long result = (Long)DB.getSQLObject(get_TrxName(), sql, new Object[] {
			getAD_Role_ID(), getAD_Tab_ID(), getAD_Field_ID(), getAD_Field_Access_ID()});
		
		return result == null || result == 0;
	}

    /**
     * Retorna una lista con los nombres de las columnas que estan en un String, utilizadas
     * como variables.
     * @param parseString String a parsear.
     * @return Lista de nombres de columnas. Lista vacía si no hay variables.
     */
    public static List<String> getDependensFieldColumNames(String parseString) {
    	ArrayList<String> list = new ArrayList<String>();

        if( (parseString == null) || (parseString.length() == 0) ) {
            return list;
        }

        String s = parseString;

        // while we have variables

        while( s.indexOf( "@" ) != -1 ) {
            int pos = s.indexOf( "@" );

            s   = s.substring( pos + 1 );
            pos = s.indexOf( "@" );

            if( pos == -1 ) {
                continue;    // error number of @@ not correct
            }

            String variable = s.substring( 0,pos );

            s = s.substring( pos + 1 );

            // log.fine( variable);

            list.add( variable );
        }

    	return list;
    }
}
