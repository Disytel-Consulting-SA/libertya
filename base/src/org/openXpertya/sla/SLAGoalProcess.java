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



package org.openXpertya.sla;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MSLACriteria;
import org.openXpertya.model.MSLAGoal;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SLAGoalProcess extends SvrProcess {

    /** Descripción de Campos */

    private int p_PA_SLA_Goal_ID;

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
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_PA_SLA_Goal_ID = getRecord_ID();
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
        log.info( "PA_SLA_Goal_ID=" + p_PA_SLA_Goal_ID );

        MSLAGoal goal = new MSLAGoal( getCtx(),p_PA_SLA_Goal_ID,get_TrxName());

        if( goal.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@PA_SLA_Goal_ID@ " + p_PA_SLA_Goal_ID );
        }

        MSLACriteria criteria = MSLACriteria.get( getCtx(),goal.getPA_SLA_Criteria_ID(),get_TrxName());

        if( criteria.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@PA_SLA_Criteria_ID@ " + goal.getPA_SLA_Criteria_ID());
        }

        SLACriteria pgm = criteria.newInstance();
        int         no  = pgm.createMeasures( goal );

        //

        goal.setMeasureActual( pgm.calculateMeasure( goal ));
        goal.setDateLastRun( new Timestamp( System.currentTimeMillis()));
        goal.save();

        //

        return "@Created@ " + no + " - @MeasureActual@=" + goal.getMeasureActual();
    }    // doIt
}    // SLAGoalProcess



/*
 *  @(#)SLAGoalProcess.java   02.07.07
 * 
 *  Fin del fichero SLAGoalProcess.java
 *  
 *  Versión 2.2
 *
 */
