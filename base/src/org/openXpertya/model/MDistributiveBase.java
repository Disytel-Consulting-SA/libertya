package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Controles para la base distributiva de Proyectos contables
 * 
 * dREHER
 */

public class MDistributiveBase extends X_C_DistributiveBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MDistributiveBase(Properties ctx, int C_DistributiveBase_ID, String trxName) {
		super(ctx, C_DistributiveBase_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		int C_Project_ID = getC_Project_ID();
		
		boolean isOldActive = false;
		
		if(!newRecord)
			isOldActive = (Boolean)get_ValueOld("IsActive");
		
		if(is_Changed()) {
			if(getPercentage().compareTo(((BigDecimal)get_ValueOld("Percentage")))!=0) {
				int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='N' WHERE C_ElementValue_ID=" + getC_ElementValue_ID(), false);
			}
		}
		
		if(!isActive()) {
			if(isOldActive) {
				/*
				LP_C_ElementValue ev = new LP_C_ElementValue(Env.getCtx(), getC_ElementValue_ID(), get_TrxName());
				ev.setCintolo_ValidatedDistribution(false);
				ev.save();
				*/
				int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='N' WHERE C_ElementValue_ID=" + getC_ElementValue_ID(), false);
				
			}
			return true;
		}else {
			if(!isOldActive) {
				int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='N' WHERE C_ElementValue_ID=" + getC_ElementValue_ID(), false);
			}
		}
		
		// Valida que se registre el proyecto
		if(C_Project_ID == 0) {
			log.saveError("Error","Se debe indicar un proyecto para la base distributiva.");
			m_processMsg = "Se debe indicar un proyecto para la base distributiva.";
			return false;
		}
		
		// Valida que el proyecto NO se repita para este elemento
		if(repetido(C_Project_ID, getC_ElementValue_ID(), newRecord)){
			log.saveError("Error","El proyecto ya forma parte de la base distributiva.");
			m_processMsg = "El proyecto ya forma parte de la base distributiva.";
			return false;
		}
		
		// Valida que el proyecto NO se repita para este elemento
		if(getPercentage().compareTo(Env.ZERO) <= 0){
			log.saveError("Error","El porcentaje NO puede ser CERO.");
			m_processMsg = "El porcentaje NO puede ser CERO.";
			return false;
		}
		
		// Si no se guardo el nombre, completarlo con el nombre del proyecto
		MProject project = new MProject(Env.getCtx(), C_Project_ID, get_TrxName());
		if(Util.isEmpty(getName())) {
			setName(project.getName());
		}
		
		// Si es un registro nuevo, cambio el porcentaje o cambio el active, debe volver a validar
		if(newRecord ||
				getPercentage().compareTo(((BigDecimal)get_ValueOld("Percentage")))!=0
				|| 
				(isActive() && !isOldActive)
			) {
			int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='N' WHERE C_ElementValue_ID=" + getC_ElementValue_ID(), false);
			/*
			LP_C_ElementValue ev = new LP_C_ElementValue(Env.getCtx(), getC_ElementValue_ID(), get_TrxName());
			ev.setCintolo_ValidatedDistribution(false);
			ev.save();
			*/
		}
		
		return true;
	}
	
	@Override
	protected boolean afterDelete(boolean success) {
		
		if(success) {
			/*
			LP_C_ElementValue ev = new LP_C_ElementValue(Env.getCtx(), getC_ElementValue_ID(), get_TrxName());
			ev.setCintolo_ValidatedDistribution(false);
			ev.save();
			*/
			int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='N' WHERE C_ElementValue_ID=" + getC_ElementValue_ID(), false);
		}
		
		return true;
	}

	private boolean repetido(int c_Project_ID, int c_ElementValue_ID, boolean newRecord) {
		String sql = "SELECT COUNT(*) FROM C_DistributiveBase " +
					" WHERE C_Project_ID=? AND C_ElementValue_ID=? AND IsActive='Y'";
		
		int veces = DB.getSQLValueEx(get_TrxName(), sql, new Object[] {c_Project_ID, c_ElementValue_ID});
		
		return (newRecord ? veces > 0: veces > 1);
	}

}
