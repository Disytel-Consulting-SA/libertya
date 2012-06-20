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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.sql.RowSet;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.interfaces.ServerHome;
import org.openXpertya.model.MReplication;
import org.openXpertya.model.MReplicationLog;
import org.openXpertya.model.MReplicationRun;
import org.openXpertya.model.MSystem;
import org.openXpertya.util.CCachedRowSet;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReplicationLocal extends SvrProcess {

    /** Descripción de Campos */

    private MSystem m_system = null;

    /** Descripción de Campos */

    private MReplication m_replication = null;

    /** Descripción de Campos */

    private MReplicationRun m_replicationRun = null;

    /** Descripción de Campos */

    private Boolean m_test = Boolean.FALSE;

    /** Descripción de Campos */

    private boolean m_replicated = true;

    /** Descripción de Campos */

    private Server m_serverRemote = null;

    /** Descripción de Campos */

    private long m_start = System.currentTimeMillis();

    /** Descripción de Campos */

    private Timestamp m_replicationStart = new Timestamp( m_start );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( ReplicationLocal.class );

    /** Descripción de Campos */

    private static String REMOTE = "org.openXpertya.process.ReplicationRemote";

    /** Descripción de Campos */

    protected static String START = "com.openXpertya.client.StartReplication";

    /**
     * Descripción de Método
     *
     */

    public void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "IsTest" )) {
                m_test = Boolean.valueOf( "Y".equals( para[ i ].getParameter()));
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        m_system = MSystem.get( getCtx());
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    public String doIt() throws Exception {
        if( (m_system == null) ||!m_system.isValid()) {
            return( "SystemNotSetupForReplication" );
        }

        //

        log.info( "doIt - Record_ID=" + getRecord_ID() + ", test=" + m_test );
        connectRemote();

        //

        setupRemote();
        mergeData();
        sendUpdates();

        // Save Info

        log.info( "doIt - Replicated=" + m_replicated + " - " + m_replicationStart );
        m_replicationRun.setIsReplicated( m_replicated );

        double sec = ( System.currentTimeMillis() - m_start );

        sec /= 1000;
        m_replicationRun.setDescription( sec + " s" );
        m_replicationRun.save();

        if( m_replicated ) {
            m_replication.setDateLastRun( m_replicationStart );
            m_replication.save();
        }

        //

        exit();

        return m_replicated
               ?"Replicated"
               :"Replication Error";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void connectRemote() throws Exception {

        // Replication Info

        m_replication = new MReplication( getCtx(),getRecord_ID(),get_TrxName());

        //

        String  AppsHost    = m_replication.getHostAddress();
        int     AppsPort    = m_replication.getHostPort();
        boolean RMIoverHTTP = m_replication.isRMIoverHTTP();

        log.info( "connectRemote - " + AppsHost + ":" + AppsPort + " - HTTP Tunnel=" + RMIoverHTTP );

        InitialContext ic = CConnection.getInitialContext( CConnection.getInitialEnvironment( AppsHost,AppsPort,RMIoverHTTP ));

        if( ic == null ) {
            throw new Exception( "NoInitialContext" );
        }

        try {
            ServerHome serverHome = ( ServerHome )ic.lookup( ServerHome.JNDI_NAME );

            // log.fine("- ServerHome: " + serverHome);

            if( serverHome == null ) {
                throw new Exception( "NoServer" );
            }

            m_serverRemote = serverHome.create();

            // log.fine("- Server: " + m_serverRemote);
            // log.fine("- Remote Status = " + m_serverRemote.getStatus());

        } catch( Exception ex ) {
            log.log( Level.SEVERE,"connectRemote",ex );

            throw new Exception( ex );
        }
    }    // connectRemote

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void setupRemote() throws Exception {
        log.info( "setupRemote" );

        //

        String sql = "SELECT rt.AD_Table_ID, rt.ReplicationType, t.TableName " + "FROM AD_ReplicationTable rt" + " INNER JOIN AD_Table t ON (rt.AD_Table_ID=t.AD_Table_ID) " + "WHERE rt.IsActive='Y' AND t.IsActive='Y'" + " AND AD_ReplicationStrategy_ID=? "    // #1
                     + "ORDER BY t.LoadSeq";
        RowSet rowset = getRowSet( sql,new Object[]{ new Integer( m_replication.getAD_ReplicationStrategy_ID())} );

        if( rowset == null ) {
            throw new Exception( "setupRemote - No RowSet Data" );
        }

        // Data Info

        RemoteSetupVO data = new RemoteSetupVO();

        data.Test             = m_test;
        data.ReplicationTable = rowset;    // RowSet
        data.IDRangeStart     = m_replication.getIDRangeStart();
        data.IDRangeEnd       = m_replication.getIDRangeEnd();
        data.AD_Client_ID     = m_replication.getRemote_Client_ID();
        data.AD_Org_ID        = m_replication.getRemote_Org_ID();
        data.Prefix           = m_replication.getPrefix();
        data.Suffix           = m_replication.getSuffix();

        // Process Info

        ProcessInfo pi = new ProcessInfo( data.toString(),0 );

        pi.setClassName( REMOTE );
        pi.setSerializableObject( data );

        Object result = doIt( START,"init",new Object[]{ m_system } );

        if( (result == null) ||!Boolean.TRUE.equals( result )) {
            throw new Exception( "setupRemote - Init Error - " + result );
        }

        // send it

        pi = m_serverRemote.process( new Properties(),pi );

        ProcessInfoLog[] logs    = pi.getLogs();
        Timestamp        dateRun = null;

        if( (logs != null) && (logs.length > 0) ) {
            dateRun = logs[ 0 ].getP_Date();    // User Remote Timestamp!
        }

        //

        log.info( "setupRemote - " + pi + " - Remote Timestamp = " + dateRun );

        if( dateRun != null ) {
            m_replicationStart = dateRun;
        }

        m_replicationRun = new MReplicationRun( getCtx(),m_replication.getAD_Replication_ID(),m_replicationStart,get_TrxName());
        m_replicationRun.save();
    }    // setupRemote

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void mergeData() throws Exception {
        log.info( "mergeData" );

        //

        String sql = "SELECT rt.AD_Table_ID, rt.ReplicationType, t.TableName, rt.AD_ReplicationTable_ID " + "FROM AD_ReplicationTable rt" + " INNER JOIN AD_Table t ON (rt.AD_Table_ID=t.AD_Table_ID) " + "WHERE rt.IsActive='Y' AND t.IsActive='Y'" + " AND AD_ReplicationStrategy_ID=?"    // #1
                     + " AND rt.ReplicationType='M' "    // Merge
                     + "ORDER BY t.LoadSeq";
        RowSet rowset = getRowSet( sql,new Object[]{ new Integer( m_replication.getAD_ReplicationStrategy_ID())} );

        try {
            while( rowset.next()) {
                mergeDataTable( rowset.getInt( 1 ),rowset.getString( 3 ),rowset.getInt( 4 ));
            }

            rowset.close();
            rowset = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"mergeData",ex );
            m_replicated = false;
        }
    }    // mergeData

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param TableName
     * @param AD_ReplicationTable_ID
     *
     * @return
     *
     * @throws Exception
     */

    private boolean mergeDataTable( int AD_Table_ID,String TableName,int AD_ReplicationTable_ID ) throws Exception {
        RemoteMergeDataVO data = new RemoteMergeDataVO();

        data.Test      = m_test;
        data.TableName = TableName;

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT * FROM " ).append( TableName ).append( " WHERE AD_Client_ID=" ).append( m_replication.getRemote_Client_ID());

        if( m_replication.getRemote_Org_ID() != 0 ) {
            sql.append( " AND AD_Org_ID IN (0," ).append( m_replication.getRemote_Org_ID()).append( ")" );
        }

        if( m_replication.getDateLastRun() != null ) {
            sql.append( " AND Updated >= " ).append( DB.TO_DATE( m_replication.getDateLastRun(),false ));
        }

        sql.append( " ORDER BY " );
        data.KeyColumns = getKeyColumns( AD_Table_ID );

        if( (data.KeyColumns == null) || (data.KeyColumns.length == 0) ) {
            log.log( Level.SEVERE,"mergeDataTable - No KeyColumns for " + TableName );
            m_replicated = false;

            return false;
        }

        for( int i = 0;i < data.KeyColumns.length;i++ ) {
            if( i > 0 ) {
                sql.append( "," );
            }

            sql.append( data.KeyColumns[ i ] );
        }

        data.Sql = sql.toString();

        // New Central Data

        data.CentralData = getRowSet( data.Sql,null );

        if( data.CentralData == null ) {
            log.fine( "mergeDataTable - CentralData is Null - " + TableName );
            m_replicated = false;

            return false;
        }

        // Process Info

        ProcessInfo pi = new ProcessInfo( "MergeData",0 );

        pi.setClassName( REMOTE );
        pi.setSerializableObject( data );

        // send it

        pi = m_serverRemote.process( new Properties(),pi );

        ProcessInfoLog[] logs = pi.getLogs();
        String           msg  = "< ";

        if( (logs != null) && (logs.length > 0) ) {
            msg += logs[ 0 ].getP_Msg();    // Remote Message
        }

        log.info( "mergeDataTable - " + pi );

        //

        MReplicationLog rLog = new MReplicationLog( getCtx(),m_replicationRun.getAD_Replication_Run_ID(),AD_ReplicationTable_ID,msg,get_TrxName());

        if( pi.isError()) {
            log.severe( "mergeDataTable Error - " + pi );
            m_replicated = false;
            rLog.setIsReplicated( false );
        } else                                                   // import data fom remote
        {
            RowSet sourceRS = ( RowSet )pi.getSerializableObject();
            RowSet targetRS = getRowSet( data.Sql,null );
            Object result   = doIt( START,"sync",new Object[]    // Merge
            {
                data.TableName,data.KeyColumns,sourceRS,targetRS,m_test,Boolean.TRUE
            } );
            boolean replicated = isReplicated( result );

            if( replicated ) {
                log.fine( "mergeDataTable -> " + TableName + " - " + result );
            } else {
                m_replicated = false;
                log.severe( "mergeDataTable -> " + TableName + " - " + result );
            }

            rLog.setIsReplicated( replicated );

            if( result != null ) {
                rLog.setP_Msg( "< " + result.toString());
            }

            sourceRS.close();
            sourceRS = null;
            targetRS.close();
            targetRS = null;
        }

        rLog.save();

        return !pi.isError();
    }    // mergeDataTable

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     *
     * @return
     */

    public String[] getKeyColumns( int AD_Table_ID ) {
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {

            // Get Keys

            String sql = "SELECT ColumnName FROM AD_Column " + "WHERE AD_Table_ID=?" + " AND IsKey='Y'";

            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( rs.getString( 1 ));
            }

            rs.close();

            // no keys - search for parents

            if( list.size() == 0 ) {
                sql = "SELECT ColumnName FROM AD_Column " + "WHERE AD_Table_ID=?" + " AND IsParent='Y'";
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,AD_Table_ID );
                rs = pstmt.executeQuery();

                while( rs.next()) {
                    list.add( rs.getString( 1 ));
                }

                rs.close();
            }

            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getKeyColumns",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( Exception e ) {
        }

        // Convert to Array

        String[] retValue = new String[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getKeyColumns

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void sendUpdates() throws Exception {
        log.info( "sendUpdates" );

        //

        String sql = "SELECT rt.AD_Table_ID, rt.ReplicationType, t.TableName, rt.AD_ReplicationTable_ID " + "FROM AD_ReplicationTable rt" + " INNER JOIN AD_Table t ON (rt.AD_Table_ID=t.AD_Table_ID) " + "WHERE rt.IsActive='Y' AND t.IsActive='Y'" + " AND AD_ReplicationStrategy_ID=?"    // #1
                     + " AND rt.ReplicationType='R' "    // Reference
                     + "ORDER BY t.LoadSeq";
        RowSet rowset = getRowSet( sql,new Object[]{ new Integer( m_replication.getAD_ReplicationStrategy_ID())} );

        try {
            while( rowset.next()) {
                sendUpdatesTable( rowset.getInt( 1 ),rowset.getString( 3 ),rowset.getInt( 4 ));
            }

            rowset.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"sendUpdates",ex );
            m_replicated = false;
        }
    }    // sendUpdates

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param TableName
     * @param AD_ReplicationTable_ID
     *
     * @return
     *
     * @throws Exception
     */

    private boolean sendUpdatesTable( int AD_Table_ID,String TableName,int AD_ReplicationTable_ID ) throws Exception {
        RemoteUpdateVO data = new RemoteUpdateVO();

        data.Test      = m_test;
        data.TableName = TableName;

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT * FROM " ).append( TableName ).append( " WHERE AD_Client_ID=" ).append( m_replication.getRemote_Client_ID());

        if( m_replication.getRemote_Org_ID() != 0 ) {
            sql.append( " AND AD_Org_ID IN (0," ).append( m_replication.getRemote_Org_ID()).append( ")" );
        }

        if( m_replication.getDateLastRun() != null ) {
            sql.append( " AND Updated >= " ).append( DB.TO_DATE( m_replication.getDateLastRun(),false ));
        }

        sql.append( " ORDER BY " );
        data.KeyColumns = getKeyColumns( AD_Table_ID );

        if( (data.KeyColumns == null) || (data.KeyColumns.length == 0) ) {
            log.log( Level.SEVERE,"sendUpdatesTable - No KeyColumns for " + TableName );
            m_replicated = false;

            return false;
        }

        for( int i = 0;i < data.KeyColumns.length;i++ ) {
            if( i > 0 ) {
                sql.append( "," );
            }

            sql.append( data.KeyColumns[ i ] );
        }

        data.Sql = sql.toString();

        // New Data

        data.CentralData = getRowSet( data.Sql,null );

        if( data.CentralData == null ) {
            log.fine( "sendUpdatesTable - Null - " + TableName );
            m_replicated = false;

            return false;
        }

        int rows = 0;

        try {
            if( data.CentralData.last()) {
                rows = data.CentralData.getRow();
            }

            data.CentralData.beforeFirst();    // rewind
        } catch( SQLException ex ) {
            log.fine( "RowCheck  " + ex );
            m_replicated = false;

            return false;
        }

        if( rows == 0 ) {
            log.fine( "No Rows - " + TableName );

            return true;
        } else {
            log.fine( TableName + " #" + rows );
        }

        // Process Info

        ProcessInfo pi = new ProcessInfo( "SendUpdates",0 );

        pi.setClassName( REMOTE );
        pi.setSerializableObject( data );

        // send it

        pi = m_serverRemote.process( new Properties(),pi );
        log.info( "sendUpdatesTable - " + pi );

        ProcessInfoLog[] logs = pi.getLogs();
        String           msg  = "> ";

        if( (logs != null) && (logs.length > 0) ) {
            msg += logs[ 0 ].getP_Msg();    // Remote Message
        }

        //

        MReplicationLog rLog = new MReplicationLog( getCtx(),m_replicationRun.getAD_Replication_Run_ID(),AD_ReplicationTable_ID,msg,get_TrxName());

        if( pi.isError()) {
            m_replicated = false;
        }

        rLog.setIsReplicated( !pi.isError());
        rLog.save();

        return !pi.isError();
    }    // sendUpdatesTable

    /**
     * Descripción de Método
     *
     */

    private void exit() {
        log.info( "exit" );

        Object      result = doIt( START,"exit",null );
        ProcessInfo pi     = new ProcessInfo( "Exit",0 );

        pi.setClassName( REMOTE );
        pi.setSerializableObject( m_replicationStart );

        // send it

        try {
            m_serverRemote.process( new Properties(),pi );
        } catch( Exception ex ) {
        }
    }    // exit

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param args
     *
     * @return
     */

    public static RowSet getRowSet( String sql,Object[] args ) {

        // shared connection

        Connection        conn   = DB.getConnectionRO();
        PreparedStatement pstmt  = null;
        RowSet            rowSet = null;

        //

        try {
            pstmt = conn.prepareStatement( sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );

            // Set Parameters

            if( args != null ) {
                for( int i = 0;i < args.length;i++ ) {
                    if( args[ i ] == null ) {
                        s_log.log( Level.SEVERE,"NULL Argument " + i );
                    } else if( args[ i ] instanceof Integer ) {
                        pstmt.setInt( i + 1,(( Integer )args[ i ] ).intValue());
                    } else if( args[ i ] instanceof Timestamp ) {
                        pstmt.setTimestamp( i + 1,( Timestamp )args[ i ] );
                    } else if( args[ i ] instanceof BigDecimal ) {
                        pstmt.setBigDecimal( i + 1,( BigDecimal )args[ i ] );
                    } else {
                        pstmt.setString( i + 1,args[ i ].toString());
                    }
                }
            }

            //

            ResultSet rs = pstmt.executeQuery();

            rowSet = CCachedRowSet.getRowSet( rs );
            pstmt.close();
            pstmt = null;
        } catch( Exception ex ) {
            s_log.log( Level.SEVERE,sql,ex );

            throw new RuntimeException( ex );
        }

        // Close Cursor

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"close pstmt",e );
        }

        return rowSet;
    }    // getRowSet

    /**
     * Descripción de Método
     *
     *
     * @param result
     *
     * @return
     */

    public static boolean isReplicated( Object result ) {
        boolean replicated = (result != null) &&!Boolean.FALSE.equals( result );

        if( replicated ) {
            replicated = result.toString().endsWith( "Errors=0" );
        }

        return replicated;
    }    // isReplicated
}    // ReplicationLocal



/*
 *  @(#)ReplicationLocal.java   02.07.07
 * 
 *  Fin del fichero ReplicationLocal.java
 *  
 *  Versión 2.2
 *
 */
