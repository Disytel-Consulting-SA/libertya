package org.openXpertya.process;

/**
 * Al desarrollar un componente, luego es necesario realizar validaciones en ejecución.
 * Sin embargo, el framework verifica que exista una entrada en AD_Plugin referenciando
 * a la versión de componente en cuestión.  En caso contrario no detectará la presencia
 * del componente.  Por lo tanto se requiere una entrada en AD_Plugin, de manera similar
 * a lo que realiza la instalación de un componente, simulando que el mismo fue instalado
 * y logrando de esta manera que sea encontrado como un componente activo más.
 */

import org.openXpertya.model.X_AD_Plugin;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class SimulateCVInstalationProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {
		// Verificar existencia previa de registracion
		String active = DB.getSQLValueString(null, "SELECT isactive FROM AD_Plugin WHERE AD_ComponentVersion_ID = ?", getRecord_ID());

		// ¿No existe una entrada?
		if (active == null) {
			// Registrar y persistir la entrada 
			X_AD_Plugin aPlugin = new X_AD_Plugin(getCtx(), 0, get_TrxName());
			aPlugin.setAD_ComponentVersion_ID(getRecord_ID());
			aPlugin.setClientOrg(0, 0);
			aPlugin.setComponent_Last_Changelog("Registracion manual simulada");
			if (!aPlugin.save()) {
				throw new Exception("Error al registrar la versión de componente en AD_Plugin: " + CLogger.retrieveErrorAsString());
			}
			return "Se ha registrado la versión de componente en AD_Plugin satisfactoriamente.";
		}

		// Existe una entrada?
		if ("N".equalsIgnoreCase(active)) {
			// Se encuentra desactivada? Activarla.
			DB.executeUpdate("UPDATE AD_Plugin SET isactive = 'Y' WHERE AD_ComponentVersion_ID = " + getRecord_ID());
			return "La versión de componente ya se encontraba registrada en AD_Plugin pero desactivada.  Se activó dicha entrada.";
		} else {
			// Se encuentra activada? Informar al usuario que no hay actividad a realizar.
			return "La versión de componente ya se encuentra registrada en AD_Plugin.";
		}

	}

}
