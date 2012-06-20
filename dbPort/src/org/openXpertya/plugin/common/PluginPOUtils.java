package org.openXpertya.plugin.common;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.DB;

public class PluginPOUtils extends PluginUtils {

	
	/** Listado de packages de plugins */
	private static Vector<String> pluginPackages = null;
	
	
	/**
	 * Obtiene el conjunto de plugins implementados para la PO actual
	 * @return Vector con el conjunto de instancias de persistencia
	 */
	public static Vector<MPluginPO> getPluginList(PO po)
	{
		Vector<MPluginPO> pluginList = new Vector<MPluginPO>();
		
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try
			{
				// Si hay clase implementada para el plugin actual, incorporar al listado			
				MPluginPO aPlugin = getPersistentInstance(aPackage, po);
				
				if (aPlugin != null)
					pluginList.add(aPlugin);
			}
			catch (Exception e) {
				log.log( Level.INFO," Imposible obtener la instancia - package:" + aPackage);
			}
		}
		
		return pluginList;
	}
	
	
	
	/**
	 * Obtiene el conjunto de plugins implementados para el Document actual
	 * @return Vector con el conjunto de instancias de documento
	 */
	public static Vector<MPluginDocAction> getPluginList(DocAction document)
	{
		Vector<MPluginDocAction> pluginList = new Vector<MPluginDocAction>();
		
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Si hay clase implementada para el plugin actual, incorporar al listado			
				MPluginDocAction aPlugin = (MPluginDocAction)getPersistentInstance(aPackage, (PO)document);
				
				if (aPlugin != null)
					pluginList.add(aPlugin);
			}
			catch (Exception e) {
				log.log( Level.INFO," Imposible castear a MPluginDocAction - package:" + aPackage);
			}
		}
		
		return pluginList;
	}
	
	
	
	/**
	 * Obtiene el listado los plugins que se encuentran activos 
	 * @return Un array con el listado de packages donde deberá buscar las clases a instanciar
	 */
	public static Vector<String> getActivePluginPackages()
	{
		try
		{
			if (pluginPackages != null)
				return pluginPackages;
				
			pluginPackages = new Vector<String>();
			
			String sql = " SELECT DISTINCT C.packageName, C.corelevel " +
						 " FROM AD_COMPONENT C " +
						 " INNER JOIN AD_COMPONENTVERSION CV ON C.AD_COMPONENT_ID = CV.AD_COMPONENT_ID " +
						 " INNER JOIN AD_PLUGIN P ON CV.AD_COMPONENTVERSION_ID = P.AD_COMPONENTVERSION_ID " +
						 " WHERE P.isActive = 'Y' " +
						 " AND C.corelevel != 0 " + // core level 0 es el componente System Core
						 " ORDER BY C.corelevel ";
			
			PreparedStatement pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next())
				pluginPackages.add(rs.getString("packageName"));
			
		}
		catch (Exception e)	{
			log.log( Level.INFO," - Versión de BBDD de Libertya sin estructura de datos para soporte de plugins - ");
		}
		
		return pluginPackages;
	}
	
	
	/**
	 * Instancia un objeto del package indicado por parametro, con el nombre de la tabla segun PO
	 * @param aPackage plugin a instanciar
	 * @return objeto plugin
	 * @throws Exception
	 */
	private static MPluginPO getPersistentInstance(String aPackage, PO po) 
	{
		try
		{
			// obtener el primero de los plugins
			Class<?> clazz = M_Table.getClass(po.get_TableName(), aPackage);
			
			// parametros para instanciacion (this, contexto y transaccion)
			Class<?>[] paramTypes = { PO.class, Properties.class, String.class, String.class };
			Object[] args = { po, po.getCtx(), po.get_TrxName(), aPackage };			
			Constructor<?> cons = clazz.getConstructor(paramTypes);
	
			// instanciar el objeto a fin de iniciar el procesamiento 
			Object theObject = cons.newInstance(args);
			return (MPluginPO)theObject;
		}
		catch (Exception e) {
			return null;
		}			
	}
	
}
