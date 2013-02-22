package org.openXpertya.replication;

import org.openXpertya.OpenXpertya;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * 
 * @author usuario
 *
 */

public abstract class AbstractTerminalLaunchProcess extends SvrProcess {


	public void execute()
	{
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");
	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar la configuracion de replicacion ");

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null)
	  		showHelp("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");

	  	// Informar a usuario e Iniciar la transacción
		String message = "Iniciando proceso. ";
	  	System.out.println(message + "(" + DB.getDatabaseInfo() + ")");

	  	try {
	  		prepare();
	  		System.out.println(doIt());
	  	}
	  	catch (Exception e) {
	  		e.printStackTrace();
	  		showHelp(e.toString());
	  	}
	}
	
	protected static void showHelp(String message)
	{
		String help = " [[ " + message + " ]] ";
		System.out.println(help);
		System.exit(1);
	}
}
