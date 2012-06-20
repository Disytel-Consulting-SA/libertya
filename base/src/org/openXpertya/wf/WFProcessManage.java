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

import java.util.logging.Level;

import org.openXpertya.model.MUser;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.StateEngine;
import org.openXpertya.process.SvrProcess;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFProcessManage extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_IsAbort = false;

    /** Descripción de Campos */

    private int p_AD_User_ID = 0;

    /** Descripción de Campos */

    private int p_AD_WF_Responsible_ID = 0;

    /** Descripción de Campos */

    private int p_AD_WF_Process_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "IsAbort" )) {
                p_IsAbort = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "AD_User_ID" )) {
                p_AD_User_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_WF_Responsible_ID" )) {
                p_AD_WF_Responsible_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_AD_WF_Process_ID = getRecord_ID();
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
        MWFProcess process = new MWFProcess( getCtx(),p_AD_WF_Process_ID,get_TrxName());

        log.info( "doIt - " + process );

        MUser user = MUser.get( getCtx(),getAD_User_ID());

        // Abort

        if( p_IsAbort ) {
            String msg = user.getName() + ": Abort";

            process.setTextMsg( msg );
            process.setAD_User_ID( getAD_User_ID());
            process.setWFState( StateEngine.STATE_Aborted );

            return msg;
        }

        String msg = null;

        // Change User

        if( (p_AD_User_ID != 0) && (process.getAD_User_ID() != p_AD_User_ID) ) {
            MUser from = MUser.get( getCtx(),process.getAD_User_ID());
            MUser to   = MUser.get( getCtx(),p_AD_User_ID );

            msg = user.getName() + ": " + from.getName() + " -> " + to.getName();
            process.setTextMsg( msg );
            process.setAD_User_ID( p_AD_User_ID );
        }

        // Change Responsible

        if( (p_AD_WF_Responsible_ID != 0) && (process.getAD_WF_Responsible_ID() != p_AD_WF_Responsible_ID) ) {
            MWFResponsible from = MWFResponsible.get( getCtx(),process.getAD_WF_Responsible_ID());
            MWFResponsible to = MWFResponsible.get( getCtx(),p_AD_WF_Responsible_ID );
            String msg1 = user.getName() + ": " + from.getName() + " -> " + to.getName();

            process.setTextMsg( msg1 );
            process.setAD_WF_Responsible_ID( p_AD_WF_Responsible_ID );

            if( msg == null ) {
                msg = msg1;
            } else {
                msg += " - " + msg1;
            }
        }

        //

        process.save();

        return "OK";
    }    // doIt
}    // WFProcessManage



/*
 *  @(#)WFProcessManage.java   02.07.07
 * 
 *  Fin del fichero WFProcessManage.java
 *  
 *  Versión 2.2
 *
 */
