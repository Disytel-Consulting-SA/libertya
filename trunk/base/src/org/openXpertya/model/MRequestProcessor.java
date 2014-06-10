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
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRequestProcessor extends X_R_RequestProcessor implements ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MRequestProcessor[] getActive( Properties ctx ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM R_RequestProcessor WHERE IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequestProcessor( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MRequestProcessor[] retValue = new MRequestProcessor[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MRequestProcessor.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_RequestProcessor_ID
     * @param trxName
     */

    public MRequestProcessor( Properties ctx,int R_RequestProcessor_ID,String trxName ) {
        super( ctx,R_RequestProcessor_ID,trxName );

        if( R_RequestProcessor_ID == 0 ) {

            // setName (null);

            setFrequencyType( FREQUENCYTYPE_Day );
            setFrequency( 0 );
            setKeepLogDays( 7 );
            setOverdueAlertDays( 0 );
            setOverdueAssignDays( 0 );
            setRemindDays( 0 );

            // setSupervisor_ID (0);

        }
    }    // MRequestProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequestProcessor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequestProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param Supervisor_ID
     */

    public MRequestProcessor( MClient parent,int Supervisor_ID ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setName( parent.getName() + " - " + Msg.translate( getCtx(),"R_RequestProcessor_ID" ));
        setSupervisor_ID( Supervisor_ID );
    }    // MRequestProcessor

    /** Descripción de Campos */

    private MRequestProcessorRoute[] m_routes = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MRequestProcessorRoute[] getRoutes( boolean reload ) {
        if( (m_routes != null) &&!reload ) {
            return m_routes;
        }

        String sql = "SELECT * FROM R_RequestProcessor_Route WHERE R_RequestProcessor_ID=? ORDER BY SeqNo";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_RequestProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequestProcessorRoute( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
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

        m_routes = new MRequestProcessorRoute[ list.size()];
        list.toArray( m_routes );

        return m_routes;
    }    // getRoutes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * " + "FROM R_RequestProcessorLog " + "WHERE R_RequestProcessor_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_RequestProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequestProcessorLog( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MRequestProcessorLog[] retValue = new MRequestProcessorLog[ list.size()];

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
        
		String sql = 	" DELETE FROM R_RequestProcessorLog " +
						" WHERE R_RequestProcessor_ID=" + getR_RequestProcessor_ID() + 
						" AND Created < ('now'::text)::timestamp(6) - interval '" + getKeepLogDays() + " days'";

        int no = DB.executeUpdate( sql,get_TrxName());

        return no;
    }    // deleteLog

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

        return getDateLastRun();
    }    // getDateNextRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return "RequestProcessor" + getID();
    }    // getServerID
}    // MRequestProcessor



/*
 *  @(#)MRequestProcessor.java   02.07.07
 * 
 *  Fin del fichero MRequestProcessor.java
 *  
 *  Versión 2.2
 *
 */
