package org.openXpertya.process.release;

import org.openXpertya.process.PluginPostInstallProcess;

public class PostInstallUpgradeFrom21_0 extends PluginPostInstallProcess {
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		/*
		 * Actualizacion de binarios
		 * """"""""""""""""""""""""" 
		 * Utilizar SIEMPRE los métodos MJasperReport.updateBinaryData() y MProcess.addAttachment() 
		 * para la carga de informes tipo Jasper, el primero para la carga en AD_JasperReport y el 
		 * segundo en reportes dinámicos, los cuales van adjuntos en el informe/proceso correspondiente.
		 * 
		 * Idealmente incorporar informes ya precompilados para evitar 
		 * la necesidad de contar con javac en los equipos de los usuarios
		 */
		
		return " ";
	}

}