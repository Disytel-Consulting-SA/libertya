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



package org.openXpertya.wf;

import org.openXpertya.model.DocWorkflowMgr;
import org.openXpertya.model.PO;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluator;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocWorkflowManager implements DocWorkflowMgr {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static DocWorkflowManager get() {
        if( s_mgr == null ) {
            s_mgr = new DocWorkflowManager();
        }

        return s_mgr;
    }    // get

    // Set PO Workflow Manager

    static {
        PO.setDocWorkflowMgr( get());
    }

    /** Descripción de Campos */

    private static DocWorkflowManager s_mgr = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( DocWorkflowManager.class );

    /**
     * Constructor de la clase ...
     *
     */

    private DocWorkflowManager() {
        super();

        if( s_mgr == null ) {
            s_mgr = this;
        }
    }    // DocWorkflowManager

    /** Descripción de Campos */

    private int m_noCalled = 0;

    /** Descripción de Campos */

    private int m_noStarted = 0;

    /**
     * Descripción de Método
     *
     *
     * @param document
     * @param AD_Table_ID
     *
     * @return
     */

    public boolean process( PO document,int AD_Table_ID ) {
        m_noCalled++;

        MWorkflow[] wfs = MWorkflow.getDocValue( document.getCtx(),document.getAD_Client_ID(),AD_Table_ID );

        if( (wfs == null) || (wfs.length == 0) ) {
            return false;
        }

        boolean started = false;

        for( int i = 0;i < wfs.length;i++ ) {
            MWorkflow wf = wfs[ i ];

            // We have a Document Workflow

            String logic = wf.getDocValueLogic();

            if( (logic == null) || (logic.length() == 0) ) {
                log.severe( "Workflow has no Logic - " + wf.getName());

                continue;
            }

            // Re-check: Document must be same Client as workflow

            if( wf.getAD_Client_ID() != document.getAD_Client_ID()) {
                continue;
            }

            // Check Logic

            if( !Evaluator.evaluateLogic( document,wf.getDocValueLogic())) {
                log.fine( "Logic evaluated to false (" + logic + ")" );

                continue;
            }

            // Start Workflow

            log.fine( logic );

            int         AD_Process_ID = 305;    // HARDCODED
            ProcessInfo pi            = new ProcessInfo( wf.getName(),AD_Process_ID,AD_Table_ID,document.getID());

            pi.setAD_User_ID( Env.getAD_User_ID( document.getCtx()));
            pi.setAD_Client_ID( document.getAD_Client_ID());

            //

            if( wf.start( pi ) != null ) {
                log.config( wf.getName());
                m_noStarted++;
                started = true;
            }
        }

        return started;
    }    // process

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "DocWorkflowManager[" );

        sb.append( "Called=" ).append( m_noCalled ).append( ",Stated=" ).append( m_noStarted ).append( "]" );

        return sb.toString();
    }    // toString
}    // DocWorkflowManager



/*
 *  @(#)DocWorkflowManager.java   02.07.07
 * 
 *  Fin del fichero DocWorkflowManager.java
 *  
 *  Versión 2.2
 *
 */
