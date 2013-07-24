package org.adempiere.webui.plugin.common;

import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.panel.InfoPanel;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginInfoUtils;
import org.openXpertya.plugin.common.PluginPOUtils;
import org.openXpertya.plugin.common.PluginProcessUtils;

public class PluginInfoPanelUtils extends PluginInfoUtils {

	/**
	 * Verifica la existencia de una clase perteneciente a un plugin que redefina el Info por defecto
	 */
	public static InfoPanel getInfoPanel(String tableName, int WindowNo, String value, boolean multiSelection, String whereClause, Integer M_Warehouse_ID, Integer M_PriceList_ID, Boolean isSOTrx, String keyColumn)
	{
		/* Buscar si algun plugin redefine la clase Info */
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Obtener el nombre de la clase que realiza el override del proceso 
				String aInfoClass = aPackage + "." + PluginConstants.PACKAGE_NAME_INFO_PANEL + "." + getInfoPanelClassName(tableName);
	
				// Si se encontró 
				if (PluginProcessUtils.checkClass(aInfoClass))
					return createInfoPanel(aInfoClass, WindowNo, value, multiSelection, whereClause, M_Warehouse_ID, M_PriceList_ID, isSOTrx, tableName, keyColumn);
			}
			catch (Exception e) {
				log.log( Level.WARNING," Error al determinar la clase Info - tableName:" + tableName) ;
				; // Error al determinar la clase 
			}
		}
		return null;
	}
	
	
	/**
	 * Crea la instancia de info, asignando los parametros correspondientes
	 */
	private static InfoPanel createInfoPanel(String aInfoPanelClass, int WindowNo, String value, boolean multiSelection, String whereClause, Integer M_Warehouse_ID, Integer M_PriceList_ID, Boolean isSOTrx, String tableName, String keyColumn)
	{
		try 
		{
			/** obtener una instancia de PO a fin de acceder a su actualizacion */
			Class<?> clazz = Class.forName(aInfoPanelClass);

			/** parametros para instanciacion */
			Vector<Class<?>> paramTypes = new Vector<Class<?>>();
			Vector<Object> args = new Vector<Object>();
			
			insert(paramTypes, args, int.class, 	WindowNo,			true);
			insert(paramTypes, args, int.class, 	M_Warehouse_ID,		false);	// usada en InfoProduct
			insert(paramTypes, args, int.class, 	M_PriceList_ID,		false);	// usada en InfoProduct
			insert(paramTypes, args, String.class, 	value,				true);
			insert(paramTypes, args, boolean.class, isSOTrx,			false); // usada en InfoBPartner
			insert(paramTypes, args, boolean.class, multiSelection,		true);
			insert(paramTypes, args, String.class, 	whereClause,		true);

			/** instanciar y devolver el info correspondiente */
			Class<?>[] paramTypesArray = new Class<?>[paramTypes.size()];
			Constructor<?> cons = clazz.getConstructor(paramTypes.toArray(paramTypesArray));
			return (InfoPanel)cons.newInstance(args.toArray());
		}
		catch (Exception e)
		{
			log.log( Level.WARNING," Imposible determinar pluginClassName para la ventana Info - aInfoClass:" + aInfoPanelClass + " e.msg:" + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Dada una tableName, retorna el ClassName a instanciar para ejecutar el plugin
	 * x ej.  C_InvoiceLine se traduce en InfoInvoiceLinePanel
	 * @param tableName
	 * @return
	 */
	public static String getInfoPanelClassName(String tableName)
	{
		int firstUnderScore = tableName.indexOf("_");
		return PluginConstants.CLASS_INFO_PREFIX + tableName.substring(firstUnderScore+1) + PluginConstants.CLASS_INFO_SUFFIX;
	}
}
