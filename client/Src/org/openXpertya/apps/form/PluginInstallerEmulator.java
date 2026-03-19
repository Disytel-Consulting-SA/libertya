package org.openXpertya.apps.form;

import org.openXpertya.util.Env;

public class PluginInstallerEmulator extends PluginInstaller {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Debe especificar la ruta al archivo de instalacion");
			System.exit(1);
		}
		Env.setContext(Env.getCtx(), "#EmulateInstall", "Y");
		
		// Se puede indicar Y Y dado que nunca se efectivizarán los cambios, y esto permite llegar hasta los queries de registracion.
		PluginInstaller.main(new String[] {args[0], "Y", "Y"});	
	}

}
