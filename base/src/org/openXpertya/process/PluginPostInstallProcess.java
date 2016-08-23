package org.openXpertya.process;

import java.io.File;
import java.util.HashMap;

import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.install.PluginXMLUpdaterPostInstall;

public class PluginPostInstallProcess extends SvrProcess {

	/* Componentes */
	protected HashMap<String, String> parameters = new HashMap<String, String>();
	
	/* Contenido del XML */
	protected String xmlContent = "";
	
	/* Nombre del jar que se está instalando */
	protected String jarFileURL; 
	
	@Override
	protected void prepare() {

		/**
		 *  Procesar el listado de parámetros e incorporarlo a la map correspondiente 
		 */
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			/* Itera por cada parámetro: uno de estos DEBERA ser el contenido del xml, y otro la URL del Jar a instalar */
			if (para[i].getParameterName().equalsIgnoreCase(PluginConstants.XML_CONTENT_PARAMETER_NAME))
				xmlContent = (String)para[i].valueToString();
			else if	(para[i].getParameterName().equalsIgnoreCase(PluginConstants.JAR_FILE_URL))
				jarFileURL = (String)para[i].valueToString();
			else
				parameters.put(para[i].getParameterName(), para[i].valueToString());
		}

	}
	
	
	/**
	 * Genera el conjunto de sentencias SQL a partir del XML recibido
	 * Realizando la sustitución correspondiente en el contenido según los parametros especificados
	 */
	protected String doIt() throws Exception {
		
		/* Generar las sentencias SQL a partir del xml, sustituyendo como corresponda según los parameters */
		PluginXMLUpdaterPostInstall pupi = new PluginXMLUpdaterPostInstall(xmlContent, get_TrxName(), parameters);
		pupi.processChangeLog();
		return ""; 
	}

	
	/**
	 * Dado un recurso, retornar la ruta completa en conjunto con el directorio contenedor de binarios
	 */
	protected String getBinaryFileURL(String fileName)
	{
		return PluginConstants.POSTINSTALL_BINARIES_DIR + "/" + fileName;
	}

}
