package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Configuración de Acceso a Pestaña para un perfil.
 * @author Franco Bonafine - Disytel
 * @date 21/08/2008
 */
public class MTabAccess extends X_AD_Tab_Access {

	/** Static Logger */
	private static CLogger s_log = CLogger.getCLogger (MFieldAccess.class);
	
	private String dataFilterWhere = null;
	
	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param AD_Tab_Access_ID ID del Acceso de Pestaña. 0 para nuevo objeto.
	 * @param trxName Nombre de la transacción
	 */
	public MTabAccess(Properties ctx, int AD_Tab_Access_ID, String trxName) {
		super(ctx, AD_Tab_Access_ID, trxName);
	}

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param rs <code>ResultSet</code> que contiene los datos del objeto.
	 * @param trxName Nombre de la transacción
	 */
	public MTabAccess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Retorna todos los accesos a pestañas configurados para un perfil determinado.
	 * @param roleID Perfil del cual obtener los accessos.
	 * @param trxName Transacción de la BD a utilizar.
	 * @return Lista con los <code>MTabAccess</code> del perfil.
	 */
	public static List<MTabAccess> getOfRoleInList(int roleID, String trxName) {
		// Lista de accesos a pestañas del perfil.
		List<MTabAccess> roleAccess = new ArrayList<MTabAccess>();
		// Se itera sobre el conjunto de Maps de accesos para cada ventana. Cada objeto de
		// iteración es una Map que tiene todos los accesos de pestaña de esa ventana.
		for (Map<Integer,MTabAccess> windowTabAccessMap : getOfRole(roleID, trxName).values()) {
			// Se agrega entonces el conjunto de valores de la Map de accesos a pestañas.
			// El conjunto de valores no es más que una colección con todos los MTabAccess
			roleAccess.addAll(windowTabAccessMap.values());
		}
		return roleAccess;
	}
	
	/**
	 * Carga todos los accesos a pestaña que tiene configurado el perfil.
	 * @param roleID Perfil del cual obtener los accessos.
	 * @param trxName Transacción de la BD a utilizar.
	 * @return <code>Map<Integer,Map<Integer,MTabAccess>></code> en donde la clave
	 * es un ID de ventana y el valor es una Map que contiene los accesos a pestaña
	 * para esa ventana. Esta última Map tiene como clave el ID de pestaña, y como valor
	 * el <code>MTabAccess</code> creado para esa pestaña.
	 */
	public static Map<Integer,Map<Integer,MTabAccess>> getOfRole(int roleID, String trxName) {
		// Se crea una nueva Map.
		Map<Integer,Map<Integer,MTabAccess>>roleTabAccess = 
			new HashMap<Integer,Map<Integer,MTabAccess>>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Consulta que obtiene todos los accesos configurado para este perfil.
		String sql = 
			" SELECT * FROM AD_Tab_Access " +
			" WHERE AD_Role_ID = ? AND IsActive = 'Y'";
		
		try {
			// Se ejecuta la consulta.
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, roleID);
			rs = pstmt.executeQuery();
			// Se recorren los accesos configurados.
			while (rs.next()) {
				// ID de ventana a la que pertenece el acceso.
				int windowID = rs.getInt("AD_Window_ID");
				MTabAccess mTabAccess = new MTabAccess(Env.getCtx(), rs, trxName);
				// Accesos de pestaña para la ventana. Si aún no existe algún acceso se
				// crea el Map que almacena todos los accesos de la ventana.
				// IMPORTANTE: En caso de que no existe algún acceso a pestaña configurado
				// para una ventana, se asume que el Perfil tiene acceso a TODAS las pestañas
				// de la ventana.
				Map<Integer,MTabAccess> windowTabAccess = roleTabAccess.get(windowID);
				if (windowTabAccess == null) {
					windowTabAccess = new HashMap<Integer,MTabAccess>();
					roleTabAccess.put(windowID, windowTabAccess);
				}
				// Se guarda el acceso a pestaña en el Map de la ventana actual.
				windowTabAccess.put(mTabAccess.getAD_Tab_ID(), mTabAccess);
			}

		} catch (Exception e) {
			s_log.log(Level.SEVERE, sql, e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (rs != null) rs.close();
			} catch (Exception e) {}
		}
		s_log.fine("#" + roleTabAccess.size()); 

		return roleTabAccess;
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {

		// Validación de registro duplicado.
		if (!validateDuplicatedTabAccess()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),"DuplicatedAccess",
					new Object[] {Msg.translate(getCtx(),"Tab")}));
			return false;
		}
		
		// Validación de desactivación de registro.
		if (is_ValueChanged("IsActive") && !isActive()) {
			return validateTabDelete();
		}
		
		// Validación de dependencias con pestañas padres. 
		if(!validateTabDependencies())
			return false;
		
		// Si se cambió la validación de datos se borra el filtro que esta
		// almacenado como caché.
		if (is_ValueChanged("AD_Val_Rule_ID")) {
			dataFilterWhere = null;
		}
		
		// Validación de la posible aplicación del filtro de datos configurado.
		if (!validateDataRule())
			return false;
		
		return true;
	}

	@Override
	protected boolean beforeDelete() {
		
		// Validación de accesos a campos asociados a este acceso.
		if (hasFieldAccess()) {
			log.saveError("DeleteError",Msg.getMsg(getCtx(),"HasRelatedAccess",
					new Object[] {Msg.translate(getCtx(),"Field"), Msg.translate(getCtx(),"Tab")}));
			return false;
		}
		
		// Validación de pestañas hijas configuradas.
		return validateTabDelete();
	}

	/**
     * Valida si es posible crear un acceso para la pestaña actual. Valida que las pestañas
     * padres ya tengan configurado un acceso previamente.
     */
    private boolean validateTabDependencies() {
		boolean valid = true;
		// Lista de pestañas padres que no han sido configuradas aún.
		List<Integer> notConfiguredTabs = null;
        try {
			// Se obtienen los IDs de pestañas padres que no están configuradas
        	// con algún acceso para la ventana y el perfil actual.
        	notConfiguredTabs = getNotConfiguredParentTabs(getParentTabIDs());
        
        	if(!notConfiguredTabs.isEmpty()) {
        		valid = false;
        		// Se obtiene una descripción con todos los nombres de las pestañas.
        		String tabNames = list2CommaString(getTabNames(notConfiguredTabs)); 
        		// Se guarda el error en el log.
        		log.saveError("MustConfigureParentTabs", tabNames);
        	}
        
        } catch (Exception e) {
			// En caso de excepcion retorna falso. (el método que lanza la excepción
        	// guarda el Error en el log.
        	return false;
		}
       
		return valid;
    }
	
	protected List<Integer> getParentTabIDs() throws Exception {
		return M_Tab.getParentTabIDs(getAD_Tab_ID(), get_TrxName()); 
	}
		
	/**
	 * A partir de la lista de pestañas padre calcula y retorna una lista con los IDs
	 * de todas las pestañas padres que no contienen una configuración de acceso creada.
	 */
	private List<Integer> getNotConfiguredParentTabs(List<Integer> parentTabsIDs) throws Exception {
		List<Integer> notConfTabs = new ArrayList<Integer>();
		if (!parentTabsIDs.isEmpty()) {

			PreparedStatement pstmt	= null;
			ResultSet rs = null;

			try {
	            // Se ejecuta la consulta que devuelve las pestañas padres que no tienen configurado
				// un acceso a pestaña.
				pstmt = DB.prepareStatement(getNotConfiguredTabsSQL(parentTabsIDs), get_TrxName());
	            pstmt.setInt(1, getAD_Window_ID());
	            pstmt.setInt(2, getAD_Role_ID());
	            rs	= pstmt.executeQuery();
      
	            // Se recorre cada registro de pestaña resultante obteniendo el ID de la pestaña
	            // no configurada.
	            while (rs.next()) {
	            	int tabID = rs.getInt("AD_Tab_ID");
	            	// Se agrega el ID de la pestaña a la lista.
	            	notConfTabs.add(tabID);
	            }
	            
			} catch (Exception e) {
				log.severe("Error while getting not configured parent tabs. " + e.getMessage());
	        	log.saveError("SaveError",e.getMessage());
	        	throw e;
			} finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (SQLException e) {}
			}
		}
		return notConfTabs;
	}
	
	private String getNotConfiguredTabsSQL(List<Integer> parentTabsIDs) {
		// Se crea la condicion para chequear solo las pestañas padres.
		String tabsWhere = "(";
		for (Iterator idsIter = parentTabsIDs.iterator(); idsIter.hasNext();) {
			Integer parentTabID = (Integer) idsIter.next();
			tabsWhere += parentTabID + (idsIter.hasNext()?",":")");
		}

		String sql =
			" SELECT t.AD_Tab_ID, t.Name " +
			" FROM AD_Tab t " +
			" WHERE t.AD_Window_ID = ? AND " +
			//      -- Solo se busca dentro de las pestañas padres
			"       t.AD_Tab_ID IN " + tabsWhere + " AND " +
			//      -- Condición de que no exista un Tab Access para la pestaña padre.
			"       NOT EXISTS ( " +
			"             SELECT ta.AD_Tab_ID " +
			"	          FROM AD_Tab_Access ta " +
			"	          WHERE ta.AD_Window_ID = t.AD_Window_ID AND " +
			"	               ta.AD_Role_ID = ? AND " +
            "				   ta.AD_Tab_ID = t.AD_Tab_ID AND " +
			"                  ta.IsActive = 'Y') " +
			" ORDER BY t.SeqNo ASC ";
		
		return sql;
	}
	
	/**
	 * Valida si es posible eliminar o desactivar el actual acceso a pestaña, buscando si 
	 * existen accesos activos configurados para pestañas que tienen como padre a la pestaña
	 * del acceso actual. De existir al menos una pestaña que es hija de la pestaña actual, 
	 * entonces el método retorna <code>false</code> y se guarda un mensaje de error en el
	 * log del sistema.
	 */
	private boolean validateTabDelete() {
		boolean valid = true;
		List<Integer> childrenTabs = new ArrayList<Integer>();
		
		// Consulta que obtiene todos los accesos configurados para el rol y ventana actual.
		String sql = "SELECT * FROM AD_Tab_Access WHERE AD_Role_ID = ? AND AD_Window_ID = ? AND IsActive = 'Y' ";

		PreparedStatement pstmt	= null;
		ResultSet rs = null;

		try {
			// Se ejecuta la consulta para instanciar cada MTabAccess.
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_Role_ID());
			pstmt.setInt(2, getAD_Window_ID());
            rs	= pstmt.executeQuery();
            // Por cada MTabAccess encontrado, se verifica si es hijo de la pestaña
            // configurada en el acceso actual.
            while(rs.next()) {
            	// Se instancia el acceso a pestaña.
            	MTabAccess mTabAccess = new MTabAccess(getCtx(), rs, get_TrxName());
            	// Se obtienen las pestañas padres de la pestaña configurada en el acceso.
            	List<Integer> parentTabs = mTabAccess.getParentTabIDs();
            	// Si dentro del conjunto de padres se encuentra el actual ID de pestaña,
            	// entonces la pestaña de mTabAccess es hija de la pestaña actual.
            	if (parentTabs.contains(getAD_Tab_ID()))
            		childrenTabs.add(mTabAccess.getAD_Tab_ID());
            }
		} catch (Exception e) {
			log.severe("Error while getting children tabs. " + e.getMessage());
        	log.saveError("SaveError",e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}

		// Si existen pestañas hijo que tiene acceso configurado entonces no es posible
		// eliminar o desactivar este acceso de pestaña.
		if (!childrenTabs.isEmpty()) {
			String tabNames = list2CommaString(getTabNames(childrenTabs));
			log.saveError("TabAccessHasChildren", tabNames);
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * Recibe una lista con IDs de pestañas y retorna una lista con los nombres teniendo
	 * en cuenta el lenguaje actual.
	 */
	private List<String> getTabNames(List<Integer> tabIDs) {
		Vector<String> tabNames = new Vector<String>();
		// Solo se procesa si la lista contiene al menos un ID de pestaña.
		if (!tabIDs.isEmpty()) {
			tabNames.setSize(tabIDs.size());
			PreparedStatement pstmt	= null;
			ResultSet rs = null;
			// Indicadores del lenguaje actual.
			boolean isBaseLanguage = Env.isBaseLanguage(getCtx(),"AD_Tab");
			String currentLanguage = Env.getAD_Language(getCtx());
			// Filtro de pestañas contenidas en la lista.
			String tabsFilter = "AD_Tab_ID IN (" + list2CommaString(tabIDs) + ")";
			// Se crea la consulta para obtener todos los nombres de las pestañas en la lista.
			String sql;
			if (isBaseLanguage) 
				sql = " SELECT AD_Tab_Id, Name FROM AD_Tab WHERE " + tabsFilter;
			else
				sql = " SELECT AD_Tab_Id, Name FROM AD_Tab_Trl WHERE AD_Language = ? AND " + tabsFilter;
			
			try {
	            // Se ejecuta la consulta 
				pstmt = DB.prepareStatement(sql, get_TrxName());
	            if (!isBaseLanguage)
	            	pstmt.setString(1, currentLanguage);
	            rs	= pstmt.executeQuery();
	            // Por cada registro, se inserta el nombre en la lista de nombres en la
	            // misma posición en la que estaba el ID en la lista parámetro.
	            while (rs.next()) {
	            	int tabID = rs.getInt("AD_Tab_ID");
	            	String name = rs.getString("Name"); 
	            	tabNames.set(tabIDs.indexOf(tabID), name);
	            }
	            
			} catch (Exception e) {
				log.severe("Error while getting tab names. " + e.getMessage());
	        	log.saveError("SaveError",e.getMessage());
			} finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (SQLException e) {}
			}
		}
		return tabNames;
	}
	
	/**
	 * Convierte una lista de objetos en un String con cada objeto separado por comas, sin
	 * agregar una coma luego del último objeto.
	 */
	private String list2CommaString(List list) {
		String commaStr = "";
		for (Iterator objectIter = list.iterator(); objectIter.hasNext();) {
			String object = objectIter.next().toString();
			commaStr += object + (objectIter.hasNext()?", ":"");
		}
		return commaStr;
	}
	
	/**
	 * Validación de accesos de pestaña duplicados.
	 * @return TRUE en caso de que no exista un acceso duplicado, FALSE en caso contrario.
	 */
	private boolean validateDuplicatedTabAccess() {
		String sql = " SELECT COUNT(*) FROM AD_Tab_Access " +
				     " WHERE AD_Role_ID = ? AND AD_Window_ID = ? AND " +
				     "       AD_Tab_ID = ? AND AD_Tab_Access_ID <> ? ";
		
		// Filtro de acceso a la consulta.
		sql = MRole.getDefault().addAccessSQL( sql, "AD_Tab_Access", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO );
		
		Long result = (Long)DB.getSQLObject(get_TrxName(), sql, new Object[] {
			getAD_Role_ID(), getAD_Window_ID(), getAD_Tab_ID(), getAD_Tab_Access_ID()});
		
		return result == null || result == 0;
	}
	
	/**
	 * @return Retorna <code>true</code> si existen accesos a campos asociados a la pestaña de este
	 * acceso a pestaña. <code>false</code> en caso contrario.
	 */
	private boolean hasFieldAccess() {
		String sql = " SELECT COUNT(*) " +
					 " FROM AD_Field_Access " +
					 " WHERE AD_Role_ID = ? AND AD_Tab_ID = ? ";

		// Filtro de acceso a la consulta.
		sql = MRole.getDefault().addAccessSQL( sql, "AD_Field_Access", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO );

		Long result = (Long)DB.getSQLObject(get_TrxName(), sql, new Object[] {
			getAD_Role_ID(), getAD_Tab_ID() });
		
		return result != null && result > 0;
	}
	
	/**
	 * Validación del where configurado en el filtro de datos. Verifica que el
	 * código de filtro de la validación de datos sea aplicable sobre la tabla
	 * que muestra la pestaña configurada en este acceso a pestaña.
	 */
	private boolean validateDataRule() {
		boolean valid = true;
		// Se obtiene el where de la validación que filtra los datos.
		String whereClause = getDataFilterWhere(true);
		// Si no hay filtro entonces no se verifica nada, la validación es correcta.
		if (whereClause.length() > 0) {
            // Se eliminan las variables del contexto de la cláusula where a fin de
			// poder comprobar mediante un consulta SELECT si el filtro es aplicable
			// en la tabla que muestra la pestaña.
			whereClause = parseDataFilter(whereClause);
            // En caso de que la claúsula where este mal formada se retorna falso indicando
			// tal problema en el log.
			if (whereClause == null) {
            	log.saveError("SaveError",Msg.translate(getCtx(),"MalformedValidationRule"));
            	return false;
            }
			// Se obtiene el nombre de la tabla que muestra la pestaña.
            String tableName = DB.getSQLValueString(
				get_TrxName(),
				"SELECT TableName FROM AD_Tab p INNER JOIN AD_Table t ON (p.AD_Table_ID = t.AD_Table_ID) WHERE p.AD_Tab_ID = ?",
				getAD_Tab_ID());
			
            // La consulta simplemente hace un SELECT de todos los campos de la tabla
            // aplicando la cláusula where configurada en la regla de validación.
			String conditionTestSQL = "SELECT * FROM " + tableName + " WHERE " + whereClause; 
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				// Se crea la sentencia y se ejecuta la consulta. En caso de que alguna de las
				// columnas contenidas dentro de la cláusula where del filtro de datos
				// no exista en la tabla, entonces aquí se disparará una excepción siendo
				// este el evento que indica que la cláusula where no es aplicable para
				// la tabla en cuestión.
				pstmt = DB.prepareStatement(conditionTestSQL, get_TrxName());
				rs = pstmt.executeQuery();
				// No importa si el rs contiene o no datos, lo que importa es si se dispara
				// o no una excepción SQL.
			} catch (SQLException e) {
				// Si estamos aquí es porque la sintaxis de la consulta es incorrecta, o sea
				// la cláusula where no es aplicable para la tabla. Se indica esto
				// en el log y se retorna falso.
				log.saveError("SaveError", Msg.translate(getCtx(),"InvalidTabDataRule"));
				valid = false;
			} finally {
				try {
					if (rs != null)	rs.close();
					if (pstmt != null) pstmt.close();
				} catch (SQLException e) {}
			}
		}
		return valid;
	}
	
	/**
	 * @return Retorna el segmento SQL que contiene el filtro contenido en la regla
	 * de validación configurada para este acceso de pestaña.
	 */
	public String getDataFilterWhere(boolean reload) {
		if (dataFilterWhere == null || reload) {
			if (getAD_Val_Rule_ID() != 0) {
				X_AD_Val_Rule valRule = new X_AD_Val_Rule(getCtx(), getAD_Val_Rule_ID(), get_TrxName());
				dataFilterWhere = valRule.getCode(); 
			}
		}
		return dataFilterWhere == null? "" : dataFilterWhere;
	}

	/**
	 * @return Retorna el segmento SQL que contiene el filtro contenido en la regla
	 * de validación configurada para este acceso de pestaña.
	 */
	public String getDataFilterWhere() {
		return getDataFilterWhere(false);
	}
	
	/**
	 * Prepara una where SQL a partir de un where que puede contener variables del contexto.
	 * Cada variable del contexto es reemplazada por "NULL".
	 * Ej: whereClause = "IsSOTrx = @IsSOTrx@"<br>
	 * retornará "IsSOTrx = NULL".
	 */
	private String parseDataFilter(String whereClause) {
        String inStr  = new String(whereClause);
        String outStr = new String(whereClause);
        int i      = inStr.indexOf( "@" );
        int j;
        String contextName;
        // Se buscan variables de contexto dentro del where.
        while( i != -1 ) {
        	// Se intenta obtener el nombre del contexto. 
        	inStr = inStr.substring(i + 1, inStr.length());
        	j = inStr.indexOf( "@" );
        	// Si falta la @ final entonces se retorna null (la validación está mal formada)
        	if (j < 0)
        		return null;
        	// Nombre de la variable del contexto que referencia la validación.
        	contextName = inStr.substring(0, j);
        	// Se reemplaza la variable del contexto por NULL en el Srting de retorno.
        	outStr = outStr.replaceAll("@"+contextName+"@", "NULL");
        	inStr = inStr.substring(j + 1, inStr.length());
        	// Se busca el indice de la próxima variable.
        	i = inStr.indexOf( "@" );
        }
        return outStr;
	}

}
