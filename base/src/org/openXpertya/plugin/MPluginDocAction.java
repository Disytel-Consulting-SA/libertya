package org.openXpertya.plugin;

import java.util.Properties;


import org.openXpertya.model.PO;
import org.openXpertya.process.DocAction;

public abstract class MPluginDocAction extends MPluginPO {


	/**
	 * Arquitectura de Plugins
	 * """""""""""""""""""""""
	 * Todo plugin que comprenda logica de documentos deberá extender esta clase
	 * Será deberán redefinir los métodos que sean necesarios.  
	 * Cada método deberá retornar un MPluginStatusDocAction indicando:
	 * 	1) El estado próximo de la ejecución a realizar (continueStatus)
	 *  2) El m_processMsg
	 *  3) El docStatus 
	 */
	
	public MPluginDocAction(PO po, Properties ctx, String trxName, String aPackage) {
		super(po, ctx, trxName, aPackage);
	}


	/** Estado de la ejecucion */
	public MPluginStatusDocAction status_docAction = new MPluginStatusDocAction();
	

	/**
	 * Ejecución previa al unlockIt
	 * @return estado del procesamiento
	 */	
    public MPluginStatusDocAction preUnlockIt(DocAction document) {
    	return status_docAction;
    }
    
    
    
	/**
	 * Ejecución posterior al unlockIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postUnlockIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al prepareInvalidateIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preInvalidateIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al prepareInvalidateIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postInvalidateIt(DocAction document) {
    	return status_docAction;
    }

    
	/**
	 * Ejecución previa al prepareIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction prePrepareIt(DocAction document) {
		return status_docAction;
	}
	

	/**
	 * Ejecución posterior al prepareIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction postPrepareIt(DocAction document) {
		return status_docAction;
	}

	
	/**
	 * Ejecución previa al completeIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction preCompleteIt(DocAction document) {
		return status_docAction;
	}
	

	/**
	 * Ejecución posterior al completeIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction postCompleteIt(DocAction document) {
		return status_docAction;
	}
		

	/**
	 * Ejecución previa al voidIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction preVoidIt(DocAction document) {
		return status_docAction;
	}
	

	/**
	 * Ejecución posterior al voidIt
	 * @return estado del procesamiento
	 */
	public MPluginStatusDocAction postVoidIt(DocAction document) {
		return status_docAction;
	}
	
	
	/**
	 * Ejecución previa al postIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction prePostIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posteior al voidIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postPostIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al closeIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preCloseIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al closeIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postCloseIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al reverseCorrectIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preReverseCorrectIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al reverseCorrectIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postReverseCorrectIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al reverseAccrualIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preReverseAccrualIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al reverseAccrualIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postReverseAccrualIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al reActivateIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preReActivateIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al reActivateIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postReActivateIt(DocAction document) {
    	return status_docAction;
    }
    
    

	/**
	 * Ejecución previa al approveIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preApproveIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al approveIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postApproveIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución previa al rejectIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction preRejectIt(DocAction document) {
    	return status_docAction;
    }
    
    
	/**
	 * Ejecución posterior al rejectIt
	 * @return estado del procesamiento
	 */
    public MPluginStatusDocAction postRejectIt(DocAction document) {
    	return status_docAction;
    }
	
}
