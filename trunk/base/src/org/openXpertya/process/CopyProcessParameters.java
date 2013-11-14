package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProcessPara;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Util;

public class CopyProcessParameters extends SvrProcess {

	/** Parámetros de proceso */
	private MProcess process;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name;
		Integer processID = 0;
        for( int i = 0;i < para.length;i++ ) {
        	name = para[ i ].getParameterName();

            if( name.equals( "AD_Process_ID" )) {
                processID = (( BigDecimal )para[ i ].getParameter()).intValue();
            }
        }
        if(!Util.isEmpty(processID, true)){
        	setProcess(MProcess.get(getCtx(), processID, get_TrxName()));
        }
	}

	@Override
	protected String doIt() throws Exception {
		if(getProcess() == null){
        	throw new Exception("Process not exist");
        }
		// Obtengo los parámetros a copiar del proceso parámetro
		MProcessPara[] paramsSrc = getProcess().getParameters();
		MProcessPara newParam;
		for (int i = 0; i < paramsSrc.length; i++) {
			newParam = new MProcessPara(getCtx(), 0, get_TrxName());
			PO.copyValues(paramsSrc[i], newParam);
			newParam.setAD_Process_ID(getRecord_ID());
			if(!newParam.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			// Copiar traducciones
			newParam.copyTranslation(paramsSrc[i]);
		}
		return "@CopiedParameters@: "+paramsSrc.length;
	}

	private MProcess getProcess() {
		return process;
	}

	private void setProcess(MProcess process) {
		this.process = process;
	}

}
