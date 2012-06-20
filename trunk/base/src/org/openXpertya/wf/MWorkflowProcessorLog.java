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

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.ProcesadorLogOXP;
import org.openXpertya.model.X_AD_WorkflowProcessorLog;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWorkflowProcessorLog extends X_AD_WorkflowProcessorLog implements ProcesadorLogOXP {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WorkflowProcessorLog_ID
     * @param trxName
     */

    public MWorkflowProcessorLog( Properties ctx,int AD_WorkflowProcessorLog_ID,String trxName ) {
        super( ctx,AD_WorkflowProcessorLog_ID,trxName );

        if( AD_WorkflowProcessorLog_ID == 0 ) {
            setIsError( false );
        }
    }    // MWorkflowProcessorLog

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWorkflowProcessorLog( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWorkflowProcessorLog

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param Summary
     */

    public MWorkflowProcessorLog( MWorkflowProcessor parent,String Summary ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setAD_WorkflowProcessor_ID( parent.getAD_WorkflowProcessor_ID());
        setSummary( Summary );
    }    // MWorkflowProcessorLog
}    // MWorkflowProcessorLog



/*
 *  @(#)MWorkflowProcessorLog.java   02.07.07
 * 
 *  Fin del fichero MWorkflowProcessorLog.java
 *  
 *  Versión 2.2
 *
 */
