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



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPreference extends X_AD_Preference {

	private static CCache s_CustomPreferences = new CCache("CustomAdPreference", 10);
	
	public static String GetCustomPreferenceValue(String Key) {
		String Value = null;
		
		synchronized (s_CustomPreferences) {
			if (s_CustomPreferences.containsKey(Key)) {
				Value = (String)s_CustomPreferences.get(Key);
			} else {
				Value = (String)DB.getSQLObject(null, "SELECT Value FROM " + Table_Name + " WHERE attribute = ? ", new Object[]{Key});
				
				if (Value == null)
					Value = "";
				
				s_CustomPreferences.put(Key, Value);
			}
		}
		
		// dREHER, validacion por excepciones detectadas
		if (Value == null)
			Value = "";
		
		return Value;
	}
	
	public static String GetCustomPreferenceValue(String Key, Integer clientID) {
		return GetCustomPreferenceValue(Key, clientID, null, null, false);
	}
	
	public static String GetCustomPreferenceValue(String Key, Integer clientID, Integer orgID, Integer userID, boolean ignoreCache) {
		String Value = null;
		
		synchronized (s_CustomPreferences) {
			if (!ignoreCache && s_CustomPreferences.containsKey(Key)) {
				Value = (String)s_CustomPreferences.get(Key);
			} else {
				StringBuffer sql = new StringBuffer( 
					"SELECT Value " +
					"FROM " + Table_Name + " " +
					"WHERE attribute = ? AND IsActive = 'Y' ");
				// Compañía
				if (clientID != null) {
					sql.append(" AND AD_Client_ID = ").append(clientID);
				}
				// Organización
				if(!Util.isEmpty(orgID, false)){
					sql.append(" AND AD_Org_ID = ").append(orgID);
				}
				// Usuario
				if(!Util.isEmpty(userID, true)){
					sql.append(" AND AD_User_ID = ").append(userID);
				}
				else{
					sql.append(" AND (AD_User_ID IS NULL OR AD_User_ID = 0) ");
				}
				Value = (String)DB.getSQLObject(null, sql.toString(), new Object[]{Key});
				
				if (Value == null)
					Value = "";
				
				s_CustomPreferences.put(Key, Value);
			}
		}
		
		return Value;
	}
	
	/**
	 * Este método busca la preferencia con la clave parámetro en el siguiente orden:
	 * <ol>
	 * <li>Usuario</li>
	 * <li>Organización</li>
	 * <li>Organización 0</li>
	 * </ol>
	 * @param Key
	 * @param clientID
	 * @param orgID
	 * @param userID
	 * @param ignoreCache
	 * @return
	 */
	public static String searchCustomPreferenceValue(String Key,
			Integer clientID, Integer orgID, Integer userID, boolean ignoreCache) {
		// Se obtiene el valor de la preferencia que contiene el % de variación permitida
		// 1) Por Usuario; 2) Por Organización; 3) Por Compañía y Organización *
		// 1)
		String value = MPreference.GetCustomPreferenceValue(Key, null,
				null, userID, ignoreCache);
		// 2) 
		if(Util.isEmpty(value, true)){
			value = MPreference.GetCustomPreferenceValue(Key, null, orgID,
					null, ignoreCache);
		}
		// 3)
		if(Util.isEmpty(value, true)){
			value = MPreference.GetCustomPreferenceValue(Key, clientID, 0,
					null, ignoreCache);
		}
		return value;
	}
	
	
	public static void SetCustomPreferenceValue(String Key, String Value) {
		s_CustomPreferences.clear();
		
		PreparedStatement ps = null;
		int n = 0;
		
		try {
			ps = DB.prepareStatement("UPDATE " + Table_Name + " SET Value = ?, Updated = NOW() WHERE attribute = ? ", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, null);
			
			ps.setString(1, Value);
			ps.setString(2, Key);
			
			n = ps.executeUpdate();
			
			if (n < 1) {
				ps.close();
				
				ps = DB.prepareStatement("INSERT INTO " + Table_Name + " (ad_preference_id,isactive,ad_client_id,ad_org_id,createdby,updatedby,value,attribute,updated) VALUES ((select coalesce(max(ad_preference_id)+1,1) from " + Table_Name + " where ad_preference_id < 100000),'Y',0,0,0,0,?,?,NOW()) ", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, null);
				
				ps.setString(1, Value);
				ps.setString(2, Key);
				
				n = ps.executeUpdate();
			}
			
		} catch (SQLException e) {
			CLogger.get().log(Level.SEVERE, "SetCustomPreferenceValue", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}

	}
	
	public static boolean GetCustomPreferenceValueBool(String Key) {
		return "Y".equals(GetCustomPreferenceValue(Key, null));
	}
	
    /** Descripción de Campos */

    public static String NULL = "null";

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Preference_ID
     * @param trxName
     */

    public MPreference( Properties ctx,int AD_Preference_ID,String trxName ) {
        super( ctx,AD_Preference_ID,trxName );

        if( AD_Preference_ID == 0 ) {

            // setAD_Preference_ID (0);
            // setAttribute (null);
            // setValue (null);

        }
    }    // MPreference

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPreference( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPreference

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param Attribute
     * @param Value
     * @param trxName
     */

    public MPreference( Properties ctx,String Attribute,String Value,String trxName ) {
        this( ctx,0,trxName );
        setAttribute( Attribute );
        setValue( Value );
    }    // MPreference


    
	public static MPreference getUserPreference(Properties ctx, String attribute, String trxName) {
		RecordFinder finder = new RecordFinder();
		Map filter = new TreeMap();
		filter.put("AD_User_ID", Env.getContextAsInt(ctx, "#AD_User_ID"));
		filter.put("Attribute", attribute);
		ResultSet rs = finder.find(ctx, filter, Table_Name);
		if (rs != null) {
				return new MPreference(ctx, rs, trxName);
		}
		return null;
	}
	
	public static MPreference getUserPreference(Properties ctx, String attribute, String trxName, Integer AD_Org_ID) {
		RecordFinder finder = new RecordFinder();
		Map filter = new TreeMap();
		filter.put("AD_User_ID", Env.getContextAsInt(ctx, "#AD_User_ID"));
		filter.put("Attribute", attribute);
		filter.put("AD_Org_ID", AD_Org_ID);
		ResultSet rs = finder.find(ctx, filter, Table_Name);
		if (rs != null) {
				return new MPreference(ctx, rs, trxName);
		}
		return null;
	}
    
    
	public static String GetCustomPreferenceValue(String Key, Integer clientID, boolean ignoreChache) {
		s_CustomPreferences.remove(Key);
		return GetCustomPreferenceValue(Key, clientID);
	}
	
}    // MPreference



/*
 *  @(#)MPreference.java   02.07.07
 * 
 *  Fin del fichero MPreference.java
 *  
 *  Versión 2.2
 *
 */
