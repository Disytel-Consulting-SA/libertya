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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MSLAGoal;
import org.openXpertya.model.MSLAMeasure;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DeliveryAccuracy extends SLACriteria {

    /**
     * Constructor de la clase ...
     *
     */

    public DeliveryAccuracy() {
        super();
    }    // DeliveryAccuracy

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param goal
     *
     * @return
     */

    public int createMeasures( MSLAGoal goal ) {
        String sql = "SELECT M_InOut_ID, io.MovementDate-o.DatePromised,"    // 1..2
                     + " io.MovementDate, o.DatePromised, o.DocumentNo " + "FROM M_InOut io" + " INNER JOIN C_Order o ON (io.C_Order_ID=o.C_Order_ID) " + "WHERE io.C_BPartner_ID=?" + " AND NOT EXISTS " + "(SELECT * FROM PA_SLA_Measure m " + "WHERE m.PA_SLA_Goal_ID=?" + " AND m.AD_Table_ID=" + MInOut.Table_ID + " AND m.Record_ID=io.M_InOut_ID)";
        int               counter = 0;
        PreparedStatement pstmt   = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,goal.getC_BPartner_ID());
            pstmt.setInt( 2,goal.getPA_SLA_Goal_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int        M_InOut_ID    = rs.getInt( 1 );
                BigDecimal MeasureActual = rs.getBigDecimal( 2 );
                Timestamp  MovementDate  = rs.getTimestamp( 3 );
                String     Description   = rs.getString( 5 ) + ": " + rs.getTimestamp( 4 );

                if( goal.isDateValid( MovementDate )) {
                    MSLAMeasure measure = new MSLAMeasure( goal,MovementDate,MeasureActual,Description );

                    measure.setLink( MInOut.Table_ID,M_InOut_ID );

                    if( measure.save()) {
                        counter++;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createMeasures",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return counter;
    }    // createMeasures

    /**
     * Descripción de Método
     *
     *
     * @param goal
     *
     * @return
     */

    public BigDecimal calculateMeasure( MSLAGoal goal ) {

        // Average

        BigDecimal retValue = Env.ZERO;
        BigDecimal total    = Env.ZERO;
        int        count    = 0;

        //

        MSLAMeasure[] measures = goal.getAllMeasures();

        for( int i = 0;i < measures.length;i++ ) {
            MSLAMeasure measure = measures[ i ];

            if( !measure.isActive() || ( (goal.getValidFrom() != null) && measure.getDateTrx().before( goal.getValidFrom())) || ( (goal.getValidTo() != null) && measure.getDateTrx().after( goal.getValidTo()))) {
                continue;
            }

            //

            total = total.add( measure.getMeasureActual());
            count++;

            //

            if( !measure.isProcessed()) {
                measure.setProcessed( true );
                measure.save();
            }
        }

        // Goal Expired

        if( (goal.getValidTo() != null) && goal.getValidTo().after( new Timestamp( System.currentTimeMillis()))) {
            goal.setProcessed( true );
        }

        // Calculate with 2 digits precision

        if( count != 0 ) {
            retValue = total.divide( new BigDecimal( count ),2,BigDecimal.ROUND_HALF_UP );
        }

        return retValue;
    }    // calculateMeasure
}    // DeliveryAccuracy



/*
 *  @(#)DeliveryAccuracy.java   02.07.07
 * 
 *  Fin del fichero DeliveryAccuracy.java
 *  
 *  Versión 2.2
 *
 */
