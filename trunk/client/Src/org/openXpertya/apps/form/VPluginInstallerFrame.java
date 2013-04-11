package org.openXpertya.apps.form;

/**
 * Beta - Ventana independiente para instalacion de plugins
 */

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALogin;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Splash;

public class VPluginInstallerFrame extends FormFrame {

	
	
	public static void main(String[] args)
	{
		
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) {
	  		System.out.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )){
	  		System.out.println("ERROR: Error al iniciar la configuracion de replicacion ");
	  		System.exit(1);
	  	}

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");

		new VPluginInstallerFrame();
	}
	
	
	
	public VPluginInstallerFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Instalador de Componentes");
		
		VPluginInstaller installer = new VPluginInstaller();
		installer.init(0, this);
		
		JPanel contentPane	= (JPanel) this.getContentPane();
		contentPane.add(installer);
		
		 AEnv.showCenterScreen(this);
	}
	
}
