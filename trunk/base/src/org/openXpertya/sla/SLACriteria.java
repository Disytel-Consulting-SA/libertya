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

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MSLACriteria;
import org.openXpertya.model.MSLAGoal;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class SLACriteria {

    /**
     * Descripción de Método
     *
     *
     * @param goal
     *
     * @return
     */

    public abstract int createMeasures( MSLAGoal goal );

    /**
     * Descripción de Método
     *
     *
     * @param goal
     *
     * @return
     */

    public abstract BigDecimal calculateMeasure( MSLAGoal goal );

    /**
     * Descripción de Método
     *
     *
     * @param criteria
     *
     * @return
     */

    public int createMeasures( MSLACriteria criteria ) {
        int        counter = 0;
        MSLAGoal[] goals   = criteria.getGoals();

        for( int i = 0;i < goals.length;i++ ) {
            MSLAGoal goal = goals[ i ];

            if( goal.isActive()) {
                counter += createMeasures( goal );
            }
        }

        return counter;
    }    // createMeasures

    /**
     * Descripción de Método
     *
     *
     * @param criteria
     */

    public void calculateMeasures( MSLACriteria criteria ) {
        MSLAGoal[] goals = criteria.getGoals();

        for( int i = 0;i < goals.length;i++ ) {
            MSLAGoal goal = goals[ i ];

            if( goal.isActive()) {
                goal.setMeasureActual( calculateMeasure( goal ));
                goal.setDateLastRun( new Timestamp( System.currentTimeMillis()));
                goal.save();
            }
        }
    }    // calculateMeasures
}    // SLACriteria



/*
 *  @(#)SLACriteria.java   02.07.07
 * 
 *  Fin del fichero SLACriteria.java
 *  
 *  Versión 2.2
 *
 */
