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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MSLAMeasure extends X_PA_SLA_Measure {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_SLA_Measure_ID
     * @param trxName
     */

    public MSLAMeasure( Properties ctx,int PA_SLA_Measure_ID,String trxName ) {
        super( ctx,PA_SLA_Measure_ID,trxName );

        if( PA_SLA_Measure_ID == 0 ) {

            // setPA_SLA_Goal_ID (0);

            setDateTrx( new Timestamp( System.currentTimeMillis()));
            setMeasureActual( Env.ZERO );
            setProcessed( false );
        }
    }    // MSLAMeasure

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MSLAMeasure( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MSLAMeasure

    /**
     * Constructor de la clase ...
     *
     *
     * @param goal
     * @param DateTrx
     * @param MeasureActual
     * @param Description
     */

    public MSLAMeasure( MSLAGoal goal,Timestamp DateTrx,BigDecimal MeasureActual,String Description ) {
        super( goal.getCtx(),0,goal.get_TrxName());
        setClientOrg( goal );
        setPA_SLA_Goal_ID( goal.getPA_SLA_Goal_ID());

        if( DateTrx != null ) {
            setDateTrx( DateTrx );
        } else {
            setDateTrx( new Timestamp( System.currentTimeMillis()));
        }

        if( MeasureActual != null ) {
            setMeasureActual( MeasureActual );
        } else {
            setMeasureActual( Env.ZERO );
        }

        if( Description != null ) {
            setDescription( Description );
        }
    }    // MSLAMeasure

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     */

    public void setLink( int AD_Table_ID,int Record_ID ) {
        setAD_Table_ID( AD_Table_ID );
        setRecord_ID( Record_ID );
    }    // setLink

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MSLAMeasure[" );

        sb.append( getID()).append( "-PA_SLA_Goal_ID=" ).append( getPA_SLA_Goal_ID()).append( "," ).append( getDateTrx()).append( ",Actual=" ).append( getMeasureActual()).append( "]" );

        return sb.toString();
    }    // toString
}    // MSLAMeasure



/*
 *  @(#)MSLAMeasure.java   02.07.07
 * 
 *  Fin del fichero MSLAMeasure.java
 *  
 *  Versión 2.2
 *
 */
