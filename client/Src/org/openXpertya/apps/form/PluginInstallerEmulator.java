package org.openXpertya.apps.form;

import org.openXpertya.util.Env;

public class PluginInstallerEmulator extends PluginInstaller {
	
	public static void main(String[] args) {
		Env.setContext(Env.getCtx(), "#EmulateInstall", "Y");
		PluginInstaller.main(args);
	}

}
