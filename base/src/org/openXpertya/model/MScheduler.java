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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MScheduler extends X_AD_Scheduler implements ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MScheduler[] getActive( Properties ctx ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM AD_Scheduler WHERE IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MScheduler( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getActive",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MScheduler[] retValue = new MScheduler[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MScheduler.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Scheduler_ID
     * @param trxName
     */

    public MScheduler( Properties ctx,int AD_Scheduler_ID,String trxName ) {
        super( ctx,AD_Scheduler_ID,trxName );
    }    // MScheduler

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MScheduler( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MScheduler

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return "Scheduler" + getID();
    }    // getServerID

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public Timestamp getDateNextRun( boolean requery ) {
        if( requery ) {
            load( get_TrxName());
        }

        return getDateNextRun();
    }    // getDateNextRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * " + "FROM AD_SchedulerLog " + "WHERE AD_Scheduler_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Scheduler_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MSchedulerLog( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLogs",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MSchedulerLog[] retValue = new MSchedulerLog[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLogs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int deleteLog() {
        if( getKeepLogDays() < 1 ) {
            return 0;
        }

		String sql = 	" DELETE FROM AD_SchedulerLog " +
						" WHERE AD_Scheduler_ID=" + getAD_Scheduler_ID() + 
						" AND Created < ('now'::text)::timestamp(6) - interval '" + getKeepLogDays() + " days'";
        int no = DB.executeUpdate( sql,get_TrxName());

        return no;
    }    // deleteLog
}    // MScheduler



/*
 *  @(#)MScheduler.java   02.07.07
 * 
 *  Fin del fichero MScheduler.java
 *  
 *  Versión 2.2
 *
 */
