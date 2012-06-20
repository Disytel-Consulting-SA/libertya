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

public class MAlertProcessor extends X_AD_AlertProcessor implements ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MAlertProcessor[] getActive( Properties ctx ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM AD_AlertProcessor WHERE IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAlertProcessor( ctx,rs,null ));
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

        MAlertProcessor[] retValue = new MAlertProcessor[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAlertProcessor.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_AlertProcessorLog_ID
     * @param trxName
     */

    public MAlertProcessor( Properties ctx,int AD_AlertProcessorLog_ID,String trxName ) {
        super( ctx,AD_AlertProcessorLog_ID,trxName );
    }    // MAlertProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAlertProcessor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlertProcessor

    /** Descripción de Campos */

    private MAlert[] m_alerts = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return "AlertProcessor" + getID();
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
        String    sql  = "SELECT * " + "FROM AD_AlertProcessorLog " + "WHERE AD_AlertProcessor_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_AlertProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAlertProcessorLog( getCtx(),rs,null ));
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

        MAlertProcessorLog[] retValue = new MAlertProcessorLog[ list.size()];

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

        String sql = "DELETE AD_AlertProcessorLog " + "WHERE AD_AlertProcessor_ID=" + getAD_AlertProcessor_ID() + " AND Created+ cast(cast(" + getKeepLogDays() + "as text)|| 'days' as interval) < SysDate";

        return 0;
    }    // deleteLog

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MAlert[] getAlerts( boolean reload ) {
        if( (m_alerts != null) &&!reload ) {
            return m_alerts;
        }

        String sql = "SELECT * FROM AD_Alert " + "WHERE AD_AlertProcessor_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_AlertProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAlert( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAlerts",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_alerts = new MAlert[ list.size()];
        list.toArray( m_alerts );

        return m_alerts;
    }    // getAlerts
}    // MAlertProcessor



/*
 *  @(#)MAlertProcessor.java   02.07.07
 * 
 *  Fin del fichero MAlertProcessor.java
 *  
 *  Versión 2.2
 *
 */
