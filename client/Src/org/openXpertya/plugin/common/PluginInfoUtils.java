package org.openXpertya.plugin.common;

import java.awt.Frame;
import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.logging.Level;
import org.openXpertya.apps.search.Info;


public class PluginInfoUtils extends PluginUtils {

	/**
	 * Verifica la existencia de una clase perteneciente a un plugin que redefina el Info por defecto
	 */
	public static Info getInfo(String tableName, Frame frame, boolean modal, int WindowNo, String value, boolean multiSelection, String whereClause, Integer M_Warehouse_ID, Integer M_PriceList_ID, Boolean isSOTrx, String keyColumn)
	{
		/* Buscar si algun plugin redefine la clase Info */
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Obtener el nombre de la clase que realiza el override del proceso 
				String aInfoClass = aPackage + "." + PluginConstants.PACKAGE_NAME_INFO + "." + getInfoClassName(tableName);
	
				// Si se encontró 
				if (PluginProcessUtils.checkClass(aInfoClass))
					return createInfo(aInfoClass, frame, modal, WindowNo, value, multiSelection, whereClause, M_Warehouse_ID, M_PriceList_ID, isSOTrx, tableName, keyColumn);
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
	private static Info createInfo(String aInfoClass, Frame frame, boolean modal, int WindowNo, String value, boolean multiSelection, String whereClause, Integer M_Warehouse_ID, Integer M_PriceList_ID, Boolean isSOTrx, String tableName, String keyColumn)
	{
		try 
		{
			/** obtener una instancia de PO a fin de acceder a su actualizacion */
			Class<?> clazz = Class.forName(aInfoClass);

			/** parametros para instanciacion */
			Vector<Class<?>> paramTypes = new Vector<Class<?>>();
			Vector<Object> args = new Vector<Object>();
			
			insert(paramTypes, args, Frame.class, 	frame,				true);
			insert(paramTypes, args, boolean.class, modal,				true);
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
			return (Info)cons.newInstance(args.toArray());
		}
		catch (Exception e)
		{
			log.log( Level.WARNING," Imposible determinar pluginClassName para la ventana Info - aInfoClass:" + aInfoClass + " e.msg:" + e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * Dada una tableName, retorna el ClassName a instanciar para ejecutar el plugin
	 * x ej.  C_InvoiceLine se traduce en InfoInvoiceLine
	 * @param tableName
	 * @return
	 */
	public static String getInfoClassName(String tableName)
	{
		int firstUnderScore = tableName.indexOf("_");
		return PluginConstants.CLASS_INFO_PREFIX + tableName.substring(firstUnderScore+1);
	}
	
	
	/**
	 * Inserta en la lista de tipos de parametros y argumentos una nueva entrada, en caso que la misma no sea null 
	 */
	private static void insert(Vector<Class<?>> paramTypes, Vector<Object> args, Class<?> clazz, Object arg, boolean addIfNull)
	{
		if (arg != null || addIfNull)
		{
			paramTypes.add(clazz);
			args.add(arg);
		}
	}

}
