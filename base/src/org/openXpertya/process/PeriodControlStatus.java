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

import java.util.logging.Level;

import org.openXpertya.model.MPeriodControl;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PeriodControlStatus extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_PeriodControl_ID = 0;

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
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_PeriodControl_ID = getRecord_ID();
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
        log.info( "C_PeriodControl_ID=" + p_C_PeriodControl_ID );

        MPeriodControl pc = new MPeriodControl( getCtx(),p_C_PeriodControl_ID,get_TrxName());

        if( pc.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@  @C_PeriodControl_ID@=" + p_C_PeriodControl_ID );
        }

        // Permanently closed

        if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals( pc.getPeriodStatus())) {
            throw new ErrorUsuarioOXP( "@PeriodStatus@ = " + pc.getPeriodStatus());
        }

        // No Action

        if( MPeriodControl.PERIODACTION_NoAction.equals( pc.getPeriodAction())) {
            return "@OK@";
        }

        // Open

        if( MPeriodControl.PERIODACTION_OpenPeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Open );
        }

        // Close

        if( MPeriodControl.PERIODACTION_ClosePeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_Closed );
        }

        // Close Permanently

        if( MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals( pc.getPeriodAction())) {
            pc.setPeriodStatus( MPeriodControl.PERIODSTATUS_PermanentlyClosed );
        }

        pc.setPeriodAction( MPeriodControl.PERIODACTION_NoAction );

        //

        boolean ok = pc.save();

        // Reset Cache

        CacheMgt.get().reset( "C_PeriodControl",0 );
        CacheMgt.get().reset( "C_Period",pc.getC_Period_ID());

        if( !ok ) {
            return "@Error@";
        }

        return "@OK@";
    }    // doIt
}    // PeriodControlStatus



/*
 *  @(#)PeriodControlStatus.java   02.07.07
 * 
 *  Fin del fichero PeriodControlStatus.java
 *  
 *  Versión 2.2
 *
 */
