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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
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

public class MRequestType extends X_R_RequestType {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param R_RequestType_ID
     *
     * @return
     */

    public static MRequestType get( Properties ctx,int R_RequestType_ID ) {
        Integer      key      = new Integer( R_RequestType_ID );
        MRequestType retValue = ( MRequestType )s_cache.get( key );

        if( retValue == null ) {
            retValue = new MRequestType( ctx,R_RequestType_ID,null );
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MRequestType.class );

    /** Descripción de Campos */

    static private CCache s_cache = new CCache( "R_RequestType",10 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MRequestType getDefault( Properties ctx ) {
        MRequestType retValue     = null;
        int          AD_Client_ID = Env.getAD_Client_ID( ctx );
        String       sql          = "SELECT * FROM R_RequestType " + "WHERE AD_Client_ID IN (0,11) " + "ORDER BY IsDefault DESC, AD_Client_ID DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MRequestType( ctx,rs,null );

                if( !retValue.isDefault()) {
                    retValue = null;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        return retValue;
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_RequestType_ID
     * @param trxName
     */

    public MRequestType( Properties ctx,int R_RequestType_ID,String trxName ) {
        super( ctx,R_RequestType_ID,trxName );

        if( R_RequestType_ID == 0 ) {

            // setR_RequestType_ID (0);
            // setName (null);

            setDueDateTolerance( 7 );
            setIsDefault( false );
            setIsEMailWhenDue( false );
            setIsEMailWhenOverdue( false );
            setIsSelfService( true );    // Y
            setAutoDueDateDays( 0 );
            setConfidentialType( CONFIDENTIALTYPE_CustomerConfidential );
            setIsAutoChangeRequest( false );
            setIsConfidentialInfo( false );
        }
    }                                    // MRequestType

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequestType( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequestType

    /** Descripción de Campos */

    private long m_nextStats = 0;

    /** Descripción de Campos */

    private int m_openNo = 0;

    /** Descripción de Campos */

    private int m_totalNo = 0;

    /** Descripción de Campos */

    private int m_new30No = 0;

    /** Descripción de Campos */

    private int m_closed30No = 0;

    /**
     * Descripción de Método
     *
     */

    private synchronized void updateStatistics() {
        if( System.currentTimeMillis() < m_nextStats ) {
            return;
        }

        String sql = "SELECT " + "(SELECT COUNT(*) FROM R_Request r" + " INNER JOIN R_Status s ON (r.R_Status_ID=s.R_Status_ID AND s.IsOpen='Y') " + "WHERE r.R_RequestType_ID=x.R_RequestType_ID) AS OpenNo, " + "(SELECT COUNT(*) FROM R_Request r " + "WHERE r.R_RequestType_ID=x.R_RequestType_ID) AS TotalNo, " + "(SELECT COUNT(*) FROM R_Request r " + "WHERE r.R_RequestType_ID=x.R_RequestType_ID AND Created>SysDate-30) AS New30No, " + "(SELECT COUNT(*) FROM R_Request r" + " INNER JOIN R_Status s ON (r.R_Status_ID=s.R_Status_ID AND s.IsClosed='Y') " + "WHERE r.R_RequestType_ID=x.R_RequestType_ID AND r.Updated>SysDate-30) AS Closed30No "

        //

        + "FROM R_RequestType x WHERE R_RequestType_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_RequestType_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_openNo     = rs.getInt( 1 );
                m_totalNo    = rs.getInt( 2 );
                m_new30No    = rs.getInt( 3 );
                m_closed30No = rs.getInt( 4 );
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

        m_nextStats = System.currentTimeMillis() + 3600000;    // every hour
    }                                                          // updateStatistics

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTotalNo() {
        updateStatistics();

        return m_totalNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getOpenNo() {
        updateStatistics();

        return m_openNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getClosed30No() {
        updateStatistics();

        return m_closed30No;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNew30No() {
        updateStatistics();

        return m_new30No;
    }

    /**
     * Descripción de Método
     *
     *
     * @param selfService
     * @param C_BPartner_ID
     *
     * @return
     */

    public MRequest[] getRequests( boolean selfService,int C_BPartner_ID ) {
        String sql = "SELECT * FROM R_Request WHERE R_RequestType_ID=?";

        if( selfService ) {
            sql += " AND IsSelfService='Y'";
        }

        if( C_BPartner_ID == 0 ) {
            sql += " AND ConfidentialType='A'";
        } else {
            sql += " AND (ConfidentialType='A' OR C_BPartner_ID=" + C_BPartner_ID + ")";
        }

        sql += " ORDER BY DocumentNo DESC";

        //

        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_RequestType_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequest( getCtx(),rs,null ));
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

        MRequest[] retValue = new MRequest[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getRequests

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequest[] getRequests() {
        return getRequests( true,0 );
    }    // getRequests

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRequestType[" );

        sb.append( getID()).append( "-" ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString
}    // MRequestType



/*
 *  @(#)MRequestType.java   02.07.07
 * 
 *  Fin del fichero MRequestType.java
 *  
 *  Versión 2.2
 *
 */
