package org.openXpertya.process;

import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MCentralConfiguration;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;

public class CCACActivationDeactivation extends SvrProcess {

	// Variables de instancia
	
	/** Registro actual */
	
	private MCentralConfiguration centralConfiguration;
	
	
	@Override
	protected void prepare() {
		// Obtengo la configuration de la central del registro actual
		setCentralConfiguration(new MCentralConfiguration(getCtx(),
				getRecord_ID(), get_TrxName()));
	}
	
	@Override
	protected String doIt() throws Exception {
		// Setear el control activado/desactivado negando el actual
		getCentralConfiguration().setIsControlActivated(
				!getCentralConfiguration().isControlActivated());
		// Setear el valor del botón
		getCentralConfiguration()
				.setManageActivation(
						getCentralConfiguration().isControlActivated() ? MCentralConfiguration.MANAGEACTIVATION_CentralizedControlDeactivation
								: MCentralConfiguration.MANAGEACTIVATION_CentralizedControlActivation);
		// Guardar
		if(!getCentralConfiguration().save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Limpiar la caché de manager para que tome uno nuevo en base a
		// configuración
		CurrentAccountManagerFactory.clearCache();
		return getCentralConfiguration().isControlActivated() ? "@ActivationCCACCompletedSuccesfully@"
				: "@DeactivationCCACCompletedSuccesfully@";
	}

	
	// Getters y Setters
	
	protected void setCentralConfiguration(MCentralConfiguration centralConfiguration) {
		this.centralConfiguration = centralConfiguration;
	}

	protected MCentralConfiguration getCentralConfiguration() {
		return centralConfiguration;
	}

}
