/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.Vector;
import java.util.logging.Logger;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.common.PluginPOUtils;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionApproveItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionCloseItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionCompleteItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionInvalidateItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionPostItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionPrepareItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionReActivateItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionRejectItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionReverseAccrualItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionReverseCorrectItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionUnlockItHandler;
import org.openXpertya.plugin.handlersDocAction.PluginDocActionVoidItHandler;
import org.openXpertya.plugin.handlersPO.PluginPOHandler;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocumentEngine implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param po
     */

    public DocumentEngine( DocAction po ) {
        this( po,STATUS_Drafted );
    }    // DocActionEngine

    /**
     * Constructor de la clase ...
     *
     *
     * @param po
     * @param docStatus
     */

    public DocumentEngine( DocAction po,String docStatus ) {
        m_document = po;

        if( docStatus != null ) {
            m_status = docStatus;
        }
    }    // DocActionEngine

    /** Descripción de Campos */

    private DocAction m_document;

    /** Descripción de Campos */

    private String m_status = STATUS_Drafted;

    /** Descripción de Campos */

    private String m_message = null;

    /** Descripción de Campos */

    private String m_action = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatus() {
        return m_status;
    }    // getDocStatus

    /**
     * Descripción de Método
     *
     *
     * @param ignored
     */

    public void setDocStatus( String ignored ) {}    // setDocStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDrafted() {
        return STATUS_Drafted.equals( m_status );
    }    // isDrafted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInvalid() {
        return STATUS_Invalid.equals( m_status );
    }    // isInvalid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInProgress() {
        return STATUS_InProgress.equals( m_status );
    }    // isInProgress

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isApproved() {
        return STATUS_Approved.equals( m_status );
    }    // isApproved

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotApproved() {
        return STATUS_NotApproved.equals( m_status );
    }    // isNotApproved

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWaiting() {
        return STATUS_WaitingPayment.equals( m_status ) || STATUS_WaitingConfirmation.equals( m_status );
    }    // isWaitingPayment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCompleted() {
        return STATUS_Completed.equals( m_status );
    }    // isCompleted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReversed() {
        return STATUS_Reversed.equals( m_status );
    }    // isReversed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isClosed() {
        return STATUS_Closed.equals( m_status );
    }    // isClosed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isVoided() {
        return STATUS_Voided.equals( m_status );
    }    // isVoided

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUnknown() {
        return STATUS_Unknown.equals( m_status ) ||!( isDrafted() || isInvalid() || isInProgress() || isNotApproved() || isApproved() || isWaiting() || isCompleted() || isReversed() || isClosed() || isVoided());
    }    // isUnknown

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     * @param docAction
     * @param log
     *
     * @return
     */

    public boolean processIt( String processAction,String docAction,CLogger log ) {
        m_message = null;
        m_action  = null;

        // Std User Workflows - see MWFNodeNext.isValidFor

        if( isValidAction( processAction )) {       // WF Selection first
            m_action = processAction;

            //

        } else if( isValidAction( docAction )) {    // User Selection second
            m_action = docAction;

            // Nothing to do

        } else if( processAction.equals( ACTION_None ) || docAction.equals( ACTION_None )) {
            log.info( "**** No Action (Prc=" + processAction + "/Doc=" + docAction + ") " + m_document );

            return true;
        } else {
            throw new IllegalStateException( "Status=" + getDocStatus() + " - Invalid Actions: Process=" + processAction + ", Doc=" + docAction );
        }

        log.info( "**** Action=" + m_action + " (Prc=" + processAction + "/Doc=" + docAction + ") " + m_document );

        boolean success = processIt( m_action );

        log.fine( "**** Action=" + m_action + " - Success=" + success );

        return success;
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public boolean processIt( String action ) {
        m_message = null;
        m_action  = action;
        setDocumentAction(action);
        //

        if( ACTION_Unlock.equals( m_action )) {
            return unlockIt();
        }

        if( ACTION_Invalidate.equals( m_action )) {
            return invalidateIt();
        }

        if( ACTION_Prepare.equals( m_action )) {
            return STATUS_InProgress.equals( prepareIt());
        }

        if( ACTION_Approve.equals( m_action )) {
            return approveIt();
        }

        if( ACTION_Reject.equals( m_action )) {
            return rejectIt();
        }

        if( ACTION_Complete.equals( m_action ) || ACTION_WaitComplete.equals( m_action )) {
            String status = null;

            if( isDrafted() || isInvalid())    // prepare if not prepared yet
            {
                status = prepareIt();

                if( !STATUS_InProgress.equals( status )) {
                    return false;
                }
            }

            status = completeIt();

            return STATUS_Completed.equals( status ) || STATUS_InProgress.equals( status ) || STATUS_WaitingPayment.equals( status ) || STATUS_WaitingConfirmation.equals( status );
        }

        if( ACTION_ReActivate.equals( m_action )) {
            return reActivateIt();
        }

        if( ACTION_Reverse_Accrual.equals( m_action )) {
            return reverseAccrualIt();
        }

        if( ACTION_Reverse_Correct.equals( m_action )) {
            return reverseCorrectIt();
        }

        if( ACTION_Close.equals( m_action )) {
            return closeIt();
        }

        if( ACTION_Void.equals( m_action )) {
            return voidIt();
        }

        return false;
    }    // processDocument

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        if( !isValidAction( ACTION_Unlock )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionUnlockItHandler(), true)) {
                m_status = STATUS_Drafted;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Drafted;

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        if( !isValidAction( ACTION_Invalidate )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionInvalidateItHandler(), true)) {
                m_status = STATUS_Invalid;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Invalid;

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        if( !isValidAction( ACTION_Prepare )) {
            return m_status;
        }

        if( m_document != null ) {
            m_status = handleDocAction(new PluginDocActionPrepareItHandler());
            m_document.setDocStatus( m_status );
        }

        return m_status;
    }    // processIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        if( !isValidAction( ACTION_Approve )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionApproveItHandler(), true)) {
                m_status = STATUS_Approved;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Approved;

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        if( !isValidAction( ACTION_Reject )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionRejectItHandler(), true)) {
                m_status = STATUS_NotApproved;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_NotApproved;

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt() {
        if( !isValidAction( ACTION_Complete )) {
            return m_status;
        }

        if( m_document != null ) {
            m_status = handleDocAction(new PluginDocActionCompleteItHandler());
            m_document.setDocStatus( m_status );
        }

        return m_status;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        if( !isValidAction( ACTION_Post )) {
            return false;
        }

        if( m_document != null ) {
            return handleDocAction(new PluginDocActionPostItHandler(), true);
        }

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
        if( !isValidAction( ACTION_Void )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionVoidItHandler(), true)) {
                m_status = STATUS_Voided;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Voided;

        return true;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        if( !isValidAction( ACTION_Close )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionCloseItHandler(), true)) {
                m_status = STATUS_Closed;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Closed;

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        if( !isValidAction( ACTION_Reverse_Correct )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionReverseCorrectItHandler(), true)) {
                m_status = STATUS_Reversed;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Reversed;

        return true;
    }    // reverseCorrectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        if( !isValidAction( ACTION_Reverse_Accrual )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionReverseAccrualItHandler(), true)) {
                m_status = STATUS_Reversed;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_Reversed;

        return true;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        if( !isValidAction( ACTION_ReActivate )) {
            return false;
        }

        if( m_document != null ) {
            if( handleDocAction(new PluginDocActionReActivateItHandler(), true)) {
                m_status = STATUS_InProgress;
                m_document.setDocStatus( m_status );

                return true;
            }

            return false;
        }

        m_status = STATUS_InProgress;

        return true;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @param newStatus
     */

    void setStatus( String newStatus ) {
        m_status = newStatus;
    }    // setStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getActionOptions() {
        if( isInvalid()) {
            return new String[]{ ACTION_Prepare,ACTION_Invalidate,ACTION_Unlock,ACTION_Void };
        }

        if( isDrafted()) {
            return new String[]{ ACTION_Prepare,ACTION_Invalidate,ACTION_Complete,ACTION_Unlock,ACTION_Void };
        }

        if( isInProgress() || isApproved()) {
            return new String[] {
                ACTION_Complete,ACTION_WaitComplete,ACTION_Approve,ACTION_Reject,ACTION_Unlock,ACTION_Void,ACTION_Prepare
            };
        }

        if( isNotApproved()) {
            return new String[]{ ACTION_Reject,ACTION_Prepare,ACTION_Unlock,ACTION_Void };
        }

        if( isWaiting()) {
            return new String[]{ ACTION_Complete,ACTION_WaitComplete,ACTION_Void };
        }

        if( isCompleted()) {
            return new String[] {
                ACTION_Close,ACTION_ReActivate,ACTION_Reverse_Accrual,ACTION_Reverse_Correct,ACTION_Post,ACTION_Void
            };
        }

        if( isClosed()) {
            return new String[]{ ACTION_Post,ACTION_ReOpen };
        }

        if( isReversed() || isVoided()) {
            return new String[]{ ACTION_Post };
        }

        return new String[]{};
    }    // getActionOptions

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public boolean isValidAction( String action ) {
        String[] options = getActionOptions();

        for( int i = 0;i < options.length;i++ ) {
            if( options[ i ].equals( action )) {
                return true;
            }
        }

        return false;
    }    // isValidAction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProcessMsg() {
        return m_message;
    }    // getProcessMsg
    
    /**
     * Descripción de Método
     *
     *
     * @param msg
     */

    public void setProcessMsg( String msg ) {
        m_message = msg;
    }    // setProcessMsg

    /** Descripción de Campos */

    private static String EXCEPTION_MSG = "Document Engine is no Document";

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocAction() {
        return m_action;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean save() {
        throw new IllegalStateException( EXCEPTION_MSG );
    }
    
    
	/**
	 * Ampliación para lógica de flujo de trabajo con plugins
	 * @return
	 */
	private String handleDocAction(PluginDocActionHandler handler)
	{
		// Cargar listado de plugins que deben ejecutarse
		Vector<MPluginDocAction> pluginList = PluginPOUtils.getPluginList(m_document);
		
		// Invocar el procesamiento segun la lista de plugins
		return handler.processAction(m_document, pluginList);
	}
	
	/**
	 * Sobrecarga para metodos que no son prepareIt o completeIt (todos retornan true o false menos estos dos)
	 * @return
	 */
	private boolean handleDocAction(PluginDocActionHandler handler, boolean dummyParameter)
	{
		// Cargar listado de plugins que deben ejecutarse
		String result = handleDocAction(handler);
		
		return result.equalsIgnoreCase(PluginDocActionHandler.TRUE);
	}

	/**
	 * Realiza el procesamiento de un acción sobre el documento y luego guarda
	 * los cambios.
	 * 
	 * @param document
	 *            Documento que se quiere procesar
	 * @param docAction
	 *            Acción a ejecutar
	 * @param saveFirst
	 *            Si es <code>true</code> primero realiza un {@link PO#save()}
	 *            del documento antes de ejecutar el
	 *            {@link DocAction#processIt(String)} sobre el mismo. Es
	 *            recomendable utilizar esta opción para documentos que aún no
	 *            han sido guardados en la BD.
	 * @return <code>true</code> si el procesado y guardado fue satisfactorio,
	 *         <code>false</code> si hubo error. El mensaje de error se puede
	 *         obtener luego con el método {@link DocAction#getProcessMsg()}.
	 */
	public static boolean processAndSave(DocAction document, String docAction, boolean saveFirst) {
		String errorMsg = null;
		boolean error = false;
		
		// Cast para ejecutar el save().
		PO docPO = (PO)document;
		
		// Primero guarda el documento si así se indicó
		if (saveFirst) {
			if (!docPO.save()) {
				errorMsg = CLogger.retrieveErrorAsString();
				error = true;
			}
		}
		
		// Si no hubo error en el guardado inicial intenta procesar y guardar el
		// documento.
		if (!error) {
			try {
				// Procesado
				if (!document.processIt(docAction)) {
					errorMsg = document.getProcessMsg();
					error = true;
				// Guardado final.
				} else if (!docPO.save()) {
					errorMsg = CLogger.retrieveErrorAsString();
					error = true;
				}
			} catch (Exception e) {
				errorMsg = CLogger.retrieveErrorAsString();
				if (errorMsg == null || errorMsg.trim().isEmpty()) {
					errorMsg = document.getProcessMsg();
				}
				error = true;
			}
		}
		
		// Si hubo error, guarda el mensaje en el ProcessMsg del documento
		if (error) {
			docPO.setProcessMsg(errorMsg);
		}
		
		return !error;
	}

	/**
	 * Seteo la acción definitiva al documento actual si es que es un PO y la
	 * columna DocAction no existe.
	 * 
	 * @param action
	 *            acción a realizar
	 */
	public void setDocumentAction(String action){
		if (m_document != null && m_document instanceof PO) {
			if(((PO)m_document).get_ColumnIndex("DocAction") != -1){
				((PO)m_document).set_Value("DocAction", action);
			}
		}
	}
	
}    // Doc



/*
 *  @(#)DocumentEngine.java   25.03.06
 * 
 *  Fin del fichero DocumentEngine.java
 *  
 *  Versión 2.2
 *
 */
