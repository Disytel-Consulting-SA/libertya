package org.openXpertya.process;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.X_AD_JasperReport;
import org.openXpertya.model.X_AD_Process;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
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

	/**
	 * Lee el contenido de un archivo binario ubicado dentro de un jar
	 * @param jarURL jarURL ubicación en el file system
	 * @param resource resource recurso a leer dentro del jar
	 * @return el array de bytes correspondiente
	 * @throws Exception en caso de error
	 */
	public static byte[] readBinaryFromJar(String jarURL, String resource) throws Exception
	{
	       JarFile jarFile = new JarFile(jarURL);
	       JarEntry entry = jarFile.getJarEntry(resource);
	       
	       /* Si no existe el archivo, elevar la excepción */
	       if (entry == null) {
	    	   PluginUtils.appendStatus("WARNING: Recurso " + resource + " no encontrado en archivo " + jarURL);
	    	   return null;
	       }
	       
	       /* Obtener el contenido del archivo */
	       InputStream input = jarFile.getInputStream(entry);
	       ByteArrayOutputStream output	= new ByteArrayOutputStream();
	       byte[]			buffer	= new byte[1024 * 8];		// 8kB
           int				length	= -1;
           
           /* Escribir a la salida para luego convertir a array de bytes */
           while ((length = input.read(buffer)) != -1) {
               output.write(buffer, 0, length);
           }
	       
	       /* Retornar el bytearray */
	       return output.toByteArray();
	}
	
	/**
	 * Actualiza el jasper report parámetro
	 * 
	 * @param uid      UID del Jasper Report
	 * @param filename Nombre del archivo
	 */
	protected void updateJasperReport(String uid, String filename) throws Exception {
		MJasperReport.updateBinaryData(get_TrxName(), getCtx(), uid,
				readBinaryFromJar(jarFileURL, getBinaryFileURL(filename)));
	}
	
	/**
	 * Actualiza el adjunto del proceso parámetro
	 * 
	 * @param uid      UID del AD_Process
	 * @param filename Nombre del archivo
	 */
	protected void updateAttachment(String uid, String filename) throws Exception {
		MProcess.addAttachment(get_TrxName(), getCtx(), uid, filename,
				readBinaryFromJar(jarFileURL, getBinaryFileURL(filename)));
	}
	
	/**
	 * Actualiza un informe, si el UID contiene AD_Process es un attachment y si
	 * contiene AD_JasperReport es un Jasper Report. Extender el método en caso de
	 * ser necesario actualizar otros componentes a futuro
	 * 
	 * @param uid      UID del componente de LY
	 * @param filename Nombre del Archivo
	 * @throws Exception
	 */
	protected void updateReport(String uid, String filename) throws Exception {
		if(filename != null && uid != null) {
			if(uid.contains(X_AD_Process.Table_Name)) {
				updateAttachment(uid, filename);
			}
			else if(uid.contains(X_AD_JasperReport.Table_Name)) {
				updateJasperReport(uid, filename);
			}
		}
	}
}
