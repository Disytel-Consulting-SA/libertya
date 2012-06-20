package org.openXpertya.plugin.install;

import java.util.HashMap;

import org.openXpertya.plugin.install.PluginXMLUpdater.ChangeGroup;


public class PluginXMLUpdaterPostInstall extends PluginXMLUpdater {
	
	/** Conjunto de parametros redefinidos según los parametros especificados en la especificación del proceso */
	private HashMap<String, String> m_parameters; 
	
	/**
	 * Constructor por defecto
	 * @param xml contenido del xml a procesar
	 */
	public PluginXMLUpdaterPostInstall(String xml, String trxName, HashMap<String, String> params) throws Exception
	{
		super(xml, trxName, false);
		if (params==null)
			throw new Exception (" Parameters required ");
		m_parameters = params;
	}
	
	
	/**
	 * Redefinición de columnas especiales que será necesario cargar con valores específicos, ignorando el valor del XML
	 */
	protected boolean appendSpecialValues(StringBuffer query, Column column, String tableName, boolean useQuotes, ChangeGroup changeGroup) throws Exception
	{
		String quotes = (useQuotes?"'":"");
		/* Primeramente contemplar valores especiales de la superclase */
		boolean retValue = false;
		
		/* Buscar en los parametros en busca de sustituciones, y redefinir según sea necesario */
		String aParam = m_parameters.get(column.getName());
		if (m_parameters.get(column.getName()) != null )
		{
			/*  Determinar como se va a incorporar al SQL */
			Boolean requiresQuotes = requiresQuotes(column);
			
			if (requiresQuotes)
				query.append( quotes + aParam + quotes + "," );
			else 
				query.append( aParam + "," );

			retValue = true;
		}
		
		return retValue || super.appendSpecialValues(query, column, tableName, useQuotes, changeGroup);

	}


}
