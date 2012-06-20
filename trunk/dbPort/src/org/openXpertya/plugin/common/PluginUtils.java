package org.openXpertya.plugin.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openXpertya.OpenXpertya;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;


public class PluginUtils {
	
	public interface PluginStatusListener {
		public void statusChanged(String status, boolean isError, boolean newLine);
	}
	
	/* Logger de eventos */
	protected static CLogger log = CLogger.getCLogger( PluginUtils.class );

	/* Transaccion creada al momento de instalar el plugin */
	private static String instalationTrxName = null;
	
	/* Detalle del log de instalacion */
	private static StringBuffer installStatus = null; 
	
	/* Detalle de errores */
	private static StringBuffer errorStatus = null;
	
	private static PluginStatusListener statusListener = null;
	
	/* Instalación activa */
	private static boolean isActive;
	
	/**
	 * Setea el nombre de la transaccion utilizada durante la instalacion
	 */
	public static void startInstalation(String trxName) {
		instalationTrxName = trxName;
		resetStatus();
	}

	/**
	 * Resetea el nombre de la transaccion utilizada durante la instalacion
	 */
	public static void stopInstalation() {
		instalationTrxName = null;
	}
	
	/**
	 * Devuelve el nombre de la transaccion en curso
	 */
	public static String getPluginInstallerTrxName() {
		return instalationTrxName;
	}
	
	/**
	 * Devuelve TRUE si hay una transaccion en curso, o FALSE en caso contrario
	 */
	public static boolean isInstallingPlugin() {
		return instalationTrxName != null;
	}
	
	
	/**
	 * Almacena una nueva linea en el log de instalacion 
	 */
	public static void appendStatus(String statusLine)
	{
		appendStatus(statusLine, true, false, true, true);
	}
	
	
	/**
	 * Almacena una nueva linea en el log de instalacion 
	 */
	public static void appendStatus(String statusLine, boolean notifyListener, boolean isError, boolean newLine, boolean toConsole)
	{
		if (toConsole)
		{
			System.out.print(statusLine);
			if (newLine) 
				System.out.println();
		}
		
		if (installStatus!=null) {
			installStatus.append(statusLine + (newLine?"\n":""));
		}
		if (notifyListener) {
			fireStatusChanged(statusLine, isError, newLine);
		}
	}
	
	/**
	 * Almacena una nueva linea en el log de error 
	 */
	public static void appendError(String errorLine)
	{
		if (errorStatus!=null) {
			errorStatus.append(errorLine + "\n");
		}
	}
	
	/**
	 * Reinicia el log de instalacion
	 */
	public static void resetStatus()
	{
		installStatus = new StringBuffer();
		errorStatus = new StringBuffer();
	}
	
	/**
	 * Retorna el log de instalacion
	 */
	public static String getInstallStatus()
	{
		return installStatus==null?"":installStatus.toString();
	}
	
	/**
	 * Retorna el log de errores
	 */
	public static String getErrorStatus()
	{
		return errorStatus==null?"":errorStatus.toString();
	}

	/**
	 * @return el valor de statusListener
	 */
	public static PluginStatusListener getStatusListener() {
		return statusListener;
	}

	/**
	 * @param statusListener el valor de statusListener a asignar
	 */
	public static void setStatusListener(PluginStatusListener statusListener) {
		PluginUtils.statusListener = statusListener;
	}
	
	private static void fireStatusChanged(String statusLine, boolean isError, boolean newLine) {
		if (getStatusListener() != null) {
			getStatusListener().statusChanged(statusLine, isError, newLine);
		}
	}
	
	/**
	 * Almacena en archivo el changelog generado hasta el momento
	 * @param path
	 * @param fileName
	 */
	public static void writeInstallLog(String path, String fileName)
	{
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + fileName));
			out.write(PluginUtils.getInstallStatus());
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception generando el archivo de log:" + e.getMessage());
		}

	}
}
