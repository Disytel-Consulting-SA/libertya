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

import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WorkflowValidate extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Worlflow_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        p_AD_Worlflow_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        MWorkflow wf = MWorkflow.get( getCtx(),p_AD_Worlflow_ID );

        log.info( "doIt - " + wf );

        String msg = wf.validate();

        wf.save();

        if( msg.length() > 0 ) {
            throw new ErrorUsuarioOXP( Msg.getMsg( getCtx(),"WorflowNotValid" ) + " - " + msg );
        }

        return wf.isValid()
               ?"@OK@"
               :"@Error@";
    }    // doIt
}    // WorkflowValidate



/*
 *  @(#)WorkflowValidate.java   02.07.07
 * 
 *  Fin del fichero WorkflowValidate.java
 *  
 *  Versión 2.2
 *
 */
