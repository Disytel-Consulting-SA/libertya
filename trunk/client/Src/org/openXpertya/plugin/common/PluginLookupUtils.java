package org.openXpertya.plugin.common;

import java.lang.reflect.Constructor;
import java.util.Vector;

import javax.swing.JMenuItem;

public class PluginLookupUtils {
	
	/** Clase encargada de devolver una entrada para el lookup */
	public static final String LOOKUP_ENTRIES_CLASS = "VLookupEntries";
	
	/**
	 * Interacciona con cada uno de los plugins a fin de que
	 * estos se encarguen de ampliar los items dentro de las 
	 * posibles opciones existentes para el menu contextual
	 * que se presenta al hacer click derecho sobre el lookup
	 * de entidades comerciales (bpartner) 
	 * @param entries
	 */
	public static void insertLookupEntries(Vector<PluginLookupInterface> entries)
	{
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try
			{
				// Obtener el nombre de la clase que define las entradas del popup menu
				String aLookupClass = aPackage + "." + PluginConstants.PACKAGE_NAME_LOOKUP + "." + LOOKUP_ENTRIES_CLASS;
				
				// Delegar la insercion en el vector al plugin
				Class<?> clazz = Class.forName(aLookupClass);
				Class<?>[] paramTypes = {  };
				Object[] args = {  };			
				Constructor<?> cons = clazz.getConstructor(paramTypes);
		
				// Instanciar el objeto a fin de iniciar el procesamiento 
				PluginLookupInterface aLookup = (PluginLookupInterface)cons.newInstance(args);
				
				// Invocar al metodo encargado de rellenar el vector de entradas 
				entries.add(aLookup);
			}
			catch (Exception e)
			{
				; // no existe una implementacion para el plugin dado
			}
		}
	
	}


	
}
