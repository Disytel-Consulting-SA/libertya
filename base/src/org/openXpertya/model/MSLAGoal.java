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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MSLAGoal extends X_PA_SLA_Goal {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_SLA_Goal_ID
     * @param trxName
     */

    public MSLAGoal( Properties ctx,int PA_SLA_Goal_ID,String trxName ) {
        super( ctx,PA_SLA_Goal_ID,trxName );

        if( PA_SLA_Goal_ID == 0 ) {
            setMeasureActual( Env.ZERO );
            setMeasureTarget( Env.ZERO );
            setProcessed( false );
        }
    }    // MSLAGoal

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MSLAGoal( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MSLAGoal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MSLAMeasure[] getAllMeasures() {
        String sql = "SELECT * FROM PA_SLA_Measure " + "WHERE PA_SLA_Goal_ID=? " + "ORDER BY DateTrx";

        return getMeasures( sql );
    }    // getAllMeasures

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MSLAMeasure[] getNewMeasures() {
        String sql = "SELECT * FROM PA_SLA_Measure " + "WHERE PA_SLA_Goal_ID=?" + " AND Processed='N' " + "ORDER BY DateTrx";

        return getMeasures( sql );
    }    // getNewMeasures

    /**
     * Descripción de Método
     *
     *
     * @param sql
     *
     * @return
     */

    private MSLAMeasure[] getMeasures( String sql ) {
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getPA_SLA_Goal_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MSLAMeasure( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getMeasures",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MSLAMeasure[] retValue = new MSLAMeasure[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getMeasures

    /**
     * Descripción de Método
     *
     *
     * @param date
     *
     * @return
     */

    public boolean isDateValid( Timestamp date ) {
        if( date == null ) {
            return false;
        }

        if( (getValidFrom() != null) && date.before( getValidFrom())) {
            return false;
        }

        if( (getValidTo() != null) && date.after( getValidTo())) {
            return false;
        }

        return true;
    }    // isDateValid
}    // MSLAGoal



/*
 *  @(#)MSLAGoal.java   02.07.07
 * 
 *  Fin del fichero MSLAGoal.java
 *  
 *  Versión 2.2
 *
 */
