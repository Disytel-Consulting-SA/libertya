package org.openXpertya.apps.form;

import org.openXpertya.util.Env;

public class PluginInstallerEmulator extends PluginInstaller {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Debe especificar la ruta al archivo de instalacion");
			System.exit(1);
		}
		Env.setContext(Env.getCtx(), "#EmulateInstall", "Y");
		PluginInstaller.main(new String[] {args[0], "N", "N"});
	}

}
