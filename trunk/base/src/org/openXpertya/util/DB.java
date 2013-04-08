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



package org.openXpertya.util;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.sql.RowSet;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import oracle.jdbc.OracleConnection;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.BaseDatosOXP;
import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MLanguage;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.MSystem;
import org.openXpertya.process.SequenceCheck;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class DB {
	
	/** dREHER, Compatibilidad Jasper Adempiere 
	 
     * convenient method to close result set and statement
     * @param rs result set
     * @param st statement
     * @see #close(ResultSet)
     * @see #close(Statement)
     */
    public static void close(ResultSet rs, Statement st) {
        close(rs);
        close(st);
    }
    
    /**
     * convenient method to close result set
     * @param rs
     */
    public static void close( ResultSet rs) {
        try {
            if (rs!=null) rs.close();
        } catch (SQLException e) {
            ;
        }
    }

    /**
     * convenient method to close statement
     * @param st
     */
    public static void close( Statement st) {
        try {
            if (st!=null) st.close();
        } catch (SQLException e) {
            ;
        }
    }
/** Compatibilidad Jasper Adempiere */

    /** Descripción de Campos */

    private static CConnection s_cc = null;

    /** Descripción de Campos */

    private static Connection[] s_connections = null;

    /** Descripción de Campos */

    private static int s_conCacheSize = Ini.isClient()
            ?1
            :1;

    /** Descripción de Campos */

    private static int s_conCount = 0;

    /** Descripción de Campos */

    private static Connection s_connectionRW = null;

    /** Descripción de Campos */

    private static Connection s_connectionID = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( DB.class );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static boolean afterMigration( Properties ctx ) {
        MSystem system = MSystem.get( ctx );

        if( !system.isJustMigrated()) {
            return false;
        }

        // Role update

        log.info( "Role" );

        String            sql   = "SELECT * FROM AD_Role";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRole role = new MRole( ctx,rs,null );

                role.updateAccessRecords();
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(1)",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Language check

        log.info( "Language" );
        MLanguage.maintain( ctx );

        // Sequence check

        log.info( "Sequence" );
        SequenceCheck.validate( ctx );

        // Costing Setup

        log.info( "Costing" );

        MAcctSchema[] ass = MAcctSchema.getClientAcctSchema( ctx,0 );

        for( int i = 0;i < ass.length;i++ ) {
            ass[ i ].checkCosting();
            ass[ i ].save();
        }

        try {
            Class clazz = Class.forName( "org.openXpertya.MigrateData" );

            clazz.newInstance();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Data",e );
        }

        // Reset Flag

        system.setIsJustMigrated( false );

        return system.save();
    }    // afterMigration

    /**
     * Descripción de Método
     *
     *
     * @param cc
     */

    public static void setDBTarget( CConnection cc ) {
        if( cc == null ) {
            throw new IllegalArgumentException( "Connection is NULL" );
        }

        DB.closeTarget();

        //

        if( s_cc == null ) {
            s_cc = cc;
        }

        synchronized( s_cc )    // use as mutex
        {
            s_cc           = cc;
            s_connections  = null;
            s_connectionRW = null;
        }

        s_cc.setDataSource();
        log.config( s_cc + " - DS=" + s_cc.isDataSource());

        // Trace.printStack();

    }    // setDBTarget

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isConnected() {
        try {
            getConnectionRW();    // try to get a connection

            return true;
        } catch( Exception e ) {
        }

        return false;
    }    // isConnected

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Connection getConnectionRW() {

        // check health of connection

        try {
            if( s_connectionRW == null ) {
                ;
            } else if( s_connectionRW.isClosed()) {
                log.finest( "Closed" );
                s_connectionRW = null;
            } else if( (s_connectionRW instanceof OracleConnection) && (( OracleConnection )s_connectionRW ).pingDatabase( 1 ) < 0 ) {
                log.warning( "No ping" );
                s_connectionRW = null;
            } else {
                if( s_connectionRW.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED ) {
                    s_connectionRW.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
                }
            }
        } catch( Exception e ) {
            s_connectionRW = null;
        }

        // Get new

        if( s_connectionRW == null ) {
            s_connectionRW = s_cc.getConnection( true,Connection.TRANSACTION_READ_COMMITTED );
            log.finest( "Con=" + s_connectionRW );
        }

        if( s_connectionRW == null ) {
            throw new UnsupportedOperationException( "No DBConnection" );
        }

        //
        // System.err.println ("DB.getConnectionRW - " + s_connectionRW);
        // Trace.printStack();

        return s_connectionRW;
    }    // getConnectionRW

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Connection getConnectionID() {
        if( s_connectionID != null ) {
            try {
                if( s_connectionID.isClosed()) {
                    s_connectionID = null;
                }
            } catch( Exception e ) {
                s_connectionID = null;
            }
        }

        if( s_connectionID == null ) {
            s_connectionID = s_cc.getConnection( false,Connection.TRANSACTION_READ_COMMITTED );
        }

        if( s_connectionID == null ) {
            throw new UnsupportedOperationException( "No DBConnection" );
        }

        log.log( Level.ALL,s_connectionID.toString());

        return s_connectionID;
    }    // getConnectionID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Connection getConnectionRO() {
        try {
            synchronized( s_cc )    // use as mutex as s_connection is null the first time
            {
                if( s_connections == null ) {
                    s_connections = createConnections( Connection.TRANSACTION_READ_COMMITTED );    // see below
                    
                    //ADER: No rechequeos en conexiones (evita accesos SHOW TRANSACTION LEVEL)
                    //modificaciones para crear en modo read only de entrada
                    //ESTO ES NECESARIO, ya que no se va a rechquear luego
                    for (int i = 0; i<s_connections.length; i++)
                    {
                    	if (s_connections[i]!= null)
                    		s_connections[i].setReadOnly(true);
                    }
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"RO",e );
        }

        // check health of connection

        int        pos          = s_conCount++;
        int        connectionNo = pos % s_conCacheSize;
        Connection connection   = s_connections[ connectionNo ];

        try {
            if( connection == null ) {
                ;
            } else if( connection.isClosed()) {

                // RowSet.close also closes connection!
                // System.out.println("DB.getConnectionRO - closed #" + connectionNo);

                connection = null;
            } else if( (connection instanceof OracleConnection) && (( OracleConnection )connection ).pingDatabase( 1 ) < 0 ) {
                log.warning( "No ping #" + connectionNo );
                connection = null;
            } else {
            	/* ADER: evitar accesos SHOW TRANSACTION LEVEL; las siguiente sentencias
            	Acceden SIEMPRE a la base de datos, al menos bajo PostGreSql
                if( !connection.isReadOnly()) {
                    connection.setReadOnly( true );
                }

                if( connection.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED ) {
                    connection.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
                }
                */
            }
        } catch( Exception e ) {
            //log.severe( "#" + connectionNo + " - " + e.toString());
            connection = null;
        }

        // Get new

        if( connection == null ) {
            log.finest( "Replacing connection #" + connectionNo );
            connection = s_cc.getConnection( true,Connection.TRANSACTION_READ_COMMITTED );    // see above

            try {
                if( connection != null ) {
                    connection.setReadOnly( true );
                }
            } catch( Exception e ) {
                log.severe( "Cannot set to R/O - " + e );
            }

            s_connections[ connectionNo ] = connection;
        }

        if( connection == null ) {
            throw new UnsupportedOperationException( "DB.getConnectionRO - @NoDBConnection@" );
        }

        //log.log( Level.ALL,"#" + connectionNo + " - " + connection );

        // System.err.println ("DB.getConnectionRO - " + connection);

        return connection;
    }    // getConnectionRO

    /**
     * Descripción de Método
     *
     *
     * @param autoCommit
     * @param trxLevel
     *
     * @return
     */

    public static Connection createConnection( boolean autoCommit,int trxLevel ) {
        Connection conn = s_cc.getConnection( autoCommit,trxLevel );

        if( CLogMgt.isLevelFinest()) {}

        return conn;
    }    // createConnection

    /**
     * Descripción de Método
     *
     *
     * @param trxLevel
     *
     * @return
     */

    private static Connection[] createConnections( int trxLevel ) {
        log.finest( "(" + s_conCacheSize + ") " + s_cc.getConnectionURL() + ", UserID=" + s_cc.getDbUid() + ", TrxLevel=" + CConnection.getTransactionIsolationInfo( trxLevel ));

        Connection cons[] = new Connection[ s_conCacheSize ];

        try {
            for( int i = 0;i < s_conCacheSize;i++ ) {
                cons[ i ] = s_cc.getConnection( true,trxLevel );    // auto commit

                if( cons[ i ] == null ) {
                    log.warning( "Connection is NULL" );            // don't use log
                }
            }
        } catch( Exception e ) {
            log.severe( e.getMessage());
        }

        return cons;
    }    // createConnections

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static BaseDatosOXP getDatabase() {
        if( s_cc != null ) {
            return s_cc.getDatabase();
        }

        return null;
    }    // getDatabase

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isOracle() {
        if( s_cc != null ) {
            return s_cc.isOracle();
        }

        return false;
    }    // isOracle

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isSybase() {
        if( s_cc != null ) {
            return s_cc.isSybase();
        }

        return false;
    }    // isSybase

    // begin vpj-cd e-evolution 02/07/2005 PostgreSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isPostgreSQL() {
        if( s_cc != null ) {
            return s_cc.isPostgreSQL();
        }

        return false;
    }    // isPostgreSQL

    // end vpj-cd e-evolution 02/07/2005 PostgreSQL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getDatabaseInfo() {
        if( s_cc != null ) {
            return s_cc.toStringDetail();
        }

        return "No DB";
    }    // getDatabaseInfo

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static boolean isDatabaseOK( Properties ctx ) {

        // Check Version

        String version = "?";
        String sql     = "SELECT Version FROM AD_System";

        try {
            PreparedStatement pstmt = prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            if( rs.next()) {
                version = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Problem with AD_System Table - Run system.sql script - " + e.toString());

            return false;
        }

        log.info( "DB_Version=" + version );

        // Identical DB version

        if( OpenXpertya.DB_VERSION.equals( version )) {
            return true;
        }

        String AD_Message = "DatabaseVersionError";
        String title      = org.openXpertya.OpenXpertya.getName() + " " + Msg.getMsg( ctx,AD_Message,true );

        // Code assumes Database version {0}, but Database has Version {1}.

        String msg = Msg.getMsg( ctx,AD_Message );    // complete message

        msg = MessageFormat.format( msg,new Object[]{ OpenXpertya.DB_VERSION,version } );

        Object[] options = { UIManager.get( "OptionPane.noButtonText" ), "Migrar" };
        int no = JOptionPane.showOptionDialog( null,msg,title,JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,UIManager.getIcon( "OptionPane.errorIcon" ),options,options[ 0 ] );

        if( no == 1 ) {
            JOptionPane.showMessageDialog( null,"Para actualizar la versión ver:\n http://www.libertya.org",title,JOptionPane.INFORMATION_MESSAGE );
            Env.exitEnv( 1 );
        }

        return false;
    }    // isDatabaseOK

    /**
     * Descripción de Método
     *
     */

    public static void closeTarget() {
        boolean closed = false;

        // RO connection

        if( s_connections != null ) {
            for( int i = 0;i < s_conCacheSize;i++ ) {
                try {
                    if( s_connections[ i ] != null ) {
                        closed = true;
                        s_connections[ i ].close();
                    }
                } catch( SQLException e ) {
                    log.warning( "#" + i + " - " + e.getMessage());
                }

                s_connections[ i ] = null;
            }
        }

        s_connections = null;

        // RW connection

        try {
            if( s_connectionRW != null ) {
                closed = true;
                s_connectionRW.close();
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"R/W",e );
        }

        s_connectionRW = null;

        // CConnection

        if( s_cc != null ) {
            closed = true;
            s_cc.setDataSource( null );
        }

        s_cc = null;

        if( closed ) {
            log.fine( "closed" );
        }
    }    // closeTarget

    /**
     * Descripción de Método
     *
     *
     * @param sqlQuery
     *
     * @return
     */

    public static CallableStatement prepareCall(String sqlQuery, int resultSetConcurrency, boolean rw, String trxName) {
        if( (sqlQuery == null) || (sqlQuery.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing - " + sqlQuery );
        }

        //

        String sql = getDatabase().convertStatement( sqlQuery );

        try {
            // Modificado por TecnoXP: permite utilizar la conexion en modo
        	// lectura/escritura para consultas que necesiten esta modalidad
            Connection conn = null;
            Trx        trx  = (trxName == null)
                              ?null
                              :Trx.get( trxName,true );

            if( trx != null ) {
            	log.fine("estamos en trx!=null");
                conn = trx.getConnection();
            } else {    
	        	if(rw)
		    		conn = getConnectionRW();
		    	else
		    		conn = getConnectionRO();
            }        	
            return conn.prepareCall( sql,ResultSet.TYPE_FORWARD_ONLY, resultSetConcurrency );
            
            ////
            
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            // throw new DBException(e);

        }

        return null;
    }    // prepareCall

    public static CallableStatement prepareCall( String RO_SQL ) {
    	return prepareCall(RO_SQL, ResultSet.CONCUR_READ_ONLY, false, null);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param RO_SQL
     *
     * @return
     */

    public static CPreparedStatement prepareStatement( String RO_SQL ) {
        return prepareStatement( RO_SQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,null, false );
    }    // prepareStatement

    /**
     * Descripción de Método
     *
     *
     * @param RO_SQL
     * @param trxName
     *
     * @return
     */

    public static CPreparedStatement prepareStatement( String RO_SQL,String trxName ) {
        return prepareStatement( RO_SQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,trxName,false );
    }    // prepareStatement
    
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static CPreparedStatement prepareStatement( String RO_SQL,String trxName, boolean noConvert) {
    	return prepareStatement( RO_SQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,trxName, noConvert );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     *
     * @return
     */

    public static CPreparedStatement prepareStatement( String sql,int resultSetType,int resultSetConcurrency ) {
        return prepareStatement( sql,resultSetType,resultSetConcurrency,null,false );
    }    // prepareStatement

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @param trxName
     *
     * @return
     */
    public static CPreparedStatement prepareStatement( String sql,int resultSetType,int resultSetConcurrency,String trxName  ) {
    	return prepareStatement(sql, resultSetType, resultSetConcurrency, trxName, false);
    }
    
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static CPreparedStatement prepareStatement( String sql,int resultSetType,int resultSetConcurrency,String trxName, boolean noConvert ) {
        if( (sql == null) || (sql.length() == 0) ) {
            throw new IllegalArgumentException( "DB.prepareStatement - No SQL" );
        }

        //

        return new CPreparedStatement( resultSetType,resultSetConcurrency,sql,trxName, noConvert );
    }    // prepareStatement
    


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Statement createStatement(String trxName) {
        return createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY, trxName );
    }    // createStatement

    public static Statement createStatement() {
        return createStatement(null );
    }    // createStatement

    /**
     * Descripción de Método
     *
     *
     * @param resultSetType
     * @param resultSetConcurrency
     * @param trxName
     *
     * @return
     */

    public static Statement createStatement( int resultSetType,int resultSetConcurrency,String trxName ) {
        return new CStatement( resultSetType,resultSetConcurrency,trxName );
    }    // createStatement

    /**
     * Descripción de Método
     *
     *
     * @param sql
     *
     * @return
     */

    public static int executeUpdate( String sql ) {
        return executeUpdate( sql,false,null );
    }    // executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param trxName
     *
     * @return
     */

    public static int executeUpdate( String sql,String trxName ) {
        return executeUpdate( sql,false,trxName );
    }    // executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param ignoreError
     *
     * @return
     */

    public static int executeUpdate( String sql,boolean ignoreError ) {
        return executeUpdate( sql,ignoreError,null );
    }    // executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param ignoreError
     * @param trxName
     *
     * @return
     */

    
    public static int executeUpdate( String sql,boolean ignoreError,String trxName ) {
    	return executeUpdate( sql, ignoreError,trxName, false);
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int executeUpdate( String sql,boolean ignoreError,String trxName, boolean noConvert ) {
        if( (sql == null) || (sql.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing - " + sql );
        }

        //
        //JOptionPane.showMessageDialog( null,"En ejecuteUpdate con Sql= "+ sql+ "trxName= "+trxName,null, JOptionPane.INFORMATION_MESSAGE );
        int                no = -1;
        CPreparedStatement cs = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE,sql,trxName, noConvert );    // converted in call

        try {
            no = cs.executeUpdate();

            // No Transaction - Commit

            if( trxName == null ) {
                cs.commit();    // Local commit

                // Connection conn = cs.getConnection();
                // if (conn != null && !conn.getAutoCommit())      //      is null for remote
                // conn.commit();

            }
        } catch( SQLException e ) {
            if( ignoreError ) {
                log.log( Level.SEVERE,"Update=" + cs.getSql() + " [" + trxName + "] - " + e.getMessage());
            } else {
                log.log( Level.SEVERE,"Update=" + cs.getSql() + " [" + trxName + "]",e );
                log.info("UpdateDb.java=" + cs.getSql() + " [" + trxName + "] - " + e.getMessage());
                log.saveError( "DBExecuteErrorrr",e );
            }

            // throw new DBException(e);

        } finally {

            // Always close cursor

            try {
                cs.close();
            } catch( SQLException e2 ) {
                log.log( Level.SEVERE,"Cannot close statement" );
            }
        }

        return no;
    }    // executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param SQL
     * @param trxName
     *
     * @return
     *
     * @throws SQLException
     */

    public static int executeUpdateEx( String SQL,String trxName ) throws SQLException {
        if( (SQL == null) || (SQL.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing - " + SQL );
        }

        //

        String       sql  = getDatabase().convertStatement( SQL );
        int          no   = -1;
        SQLException ex   = null;
        Connection   conn = null;
        Statement    stmt = null;

        try {
            Trx trx = (trxName == null)
                      ?null
                      :Trx.get( trxName,true );

            if( trx != null ) {
                conn = trx.getConnection();
            } else {
                conn = DB.getConnectionRW();
            }

            stmt = conn.createStatement();
            no   = stmt.executeUpdate( sql );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql + " [" + trxName + "]",e );
            ex = e;
        } finally {

            // Always close cursor

            try {
                stmt.close();
            } catch( SQLException e2 ) {
                log.log( Level.SEVERE,"Cannot close statement" );
            }
        }

        if( ex != null ) {
            throw new SQLException( ex.getMessage(),ex.getSQLState(),ex.getErrorCode());
        }

        return no;
    }    // execute Update

    /**
     * Descripción de Método
     *
     *
     * @param throwException
     * @param trxName
     *
     * @return
     *
     * @throws SQLException
     */

    public static boolean commit( boolean throwException,String trxName ) throws SQLException {
        try {
            Connection conn = null;
            Trx        trx  = (trxName == null)
                              ?null
                              :Trx.get( trxName,true );

            if( trx != null ) {
                conn = trx.getConnection();
            } else {
                conn = DB.getConnectionRW();
            }

            // if (!conn.getAutoCommit())

            conn.commit();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"[" + trxName + "]",e );

            if( throwException ) {
                throw e;
            }

            return false;
        }

        return true;
    }    // commit

    /**
     * Descripción de Método
     *
     *
     * @param throwException
     * @param trxName
     *
     * @return
     *
     * @throws SQLException
     */

    public static boolean rollback( boolean throwException,String trxName ) throws SQLException {
        try {
            Connection conn = null;
            Trx        trx  = (trxName == null)
                              ?null
                              :Trx.get( trxName,true );

            if( trx != null ) {
                conn = trx.getConnection();
            } else {
                conn = DB.getConnectionRW();
            }

            // if (!conn.getAutoCommit())

            conn.rollback();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"[" + trxName + "]",e );

            if( throwException ) {
                throw e;
            }

            return false;
        }

        return true;
    }    // commit

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param local
     *
     * @return
     */

    public static RowSet getRowSet( String sql,boolean local ) {
        RowSet       retValue = null;
        CStatementVO info     = new CStatementVO( RowSet.TYPE_SCROLL_INSENSITIVE,RowSet.CONCUR_READ_ONLY,sql );
        CPreparedStatement stmt = new CPreparedStatement( info );

        if( local ) {
            retValue = stmt.local_getRowSet();
        } else {
            retValue = stmt.remote_getRowSet();
        }

        return retValue;
    }    // getRowSet

       
    
    public static Object getSQLObject( String trxName, String sql, Object[] params ) {
    	return getSQLObject( trxName, sql, params, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static Object getSQLObject( String trxName, String sql, Object[] params, boolean noConvert ) {
        Object               retObj = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );

            if (params != null) {
            	for (int i=0; i<params.length; i++)
            		pstmt.setObject(i+1, params[i]);
            }
            
            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
            	retObj = rs.getObject( 1 );
            } else {
                log.fine( "No Value " + sql );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retObj;
    }    // getSQLValue
    
    
    
    
    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     *
     * @return
     */
    public static int getSQLValue( String trxName,String sql ) {
    	return getSQLValue( trxName, sql, false );
    }

    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int getSQLValue( String trxName,String sql, boolean noConvert ) {
        int               retValue = -1;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            } else {
                log.fine( "No Value " + sql );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValue

    
    
    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param int_param1
     *
     * @return
     */
    public static int getSQLValue( String trxName,String sql,int int_param1 ) {
    	return getSQLValue( trxName,sql, int_param1, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int getSQLValue( String trxName,String sql,int int_param1, boolean noConvert ) {
        int               retValue = -1;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setInt( 1,int_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + int_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValue


    
    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param int_param1
     * @param int_param2
     *
     * @return
     */
    public static int getSQLValue( String trxName,String sql,int int_param1,int int_param2 ) {
    	return getSQLValue( trxName,sql, int_param1, int_param2, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int getSQLValue( String trxName,String sql,int int_param1,int int_param2, boolean noConvert ) {
        int               retValue = -1;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setInt( 1,int_param1 );
            pstmt.setInt( 2,int_param2 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + int_param1 + ",Param2=" + int_param2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + ",Param2=" + int_param2 + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValue

    	
    
    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param str_param1
     *
     * @return
     */
    public static int getSQLValue( String trxName,String sql,String str_param1 ) {
    	return getSQLValue(trxName, sql, str_param1, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int getSQLValue( String trxName,String sql,String str_param1, boolean noConvert ) {
        int               retValue = -1;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setString( 1,str_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + str_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + str_param1,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValue
    
    


    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param int_param1
     * @param s_param2
     *
     * @return
     */
    public static int getSQLValue( String trxName,String sql,int int_param1,String s_param2) {
    	return getSQLValue( trxName, sql, int_param1, s_param2, false );
    }    
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static int getSQLValue( String trxName,String sql,int int_param1,String s_param2, boolean noConvert ) {
        int               retValue = -1;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setInt( 1,int_param1 );
            pstmt.setString( 2,s_param2 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            } else {
                log.info( "No Value: " + sql + " - Param1=" + int_param1 + ",Param2=" + s_param2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + ",Param2=" + s_param2,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValue
    


    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param int_param1
     *
     * @return
     */
    public static String getSQLValueString( String trxName,String sql,int int_param1 ) {
    	return getSQLValueString( trxName, sql, int_param1, false );
    }

    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static String getSQLValueString( String trxName,String sql,int int_param1, boolean noConvert ) {
        String            retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setInt( 1,int_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + int_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + int_param1,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValueString


    
    
    public static String getSQLValueString( String trxName,String sql,String str_param1) {
    	return getSQLValueString( trxName, sql, str_param1, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static String getSQLValueString( String trxName,String sql,String str_param1, boolean noConvert ) {
        String            retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setString( 1,str_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + str_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + str_param1,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValueString
    

    
    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param sql
     * @param int_param1
     *
     * @return
     */
    public static BigDecimal getSQLValueBD( String trxName,String sql,int int_param1) {
    	return getSQLValueBD( trxName, sql, int_param1, false );
    }
    
    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
    public static BigDecimal getSQLValueBD( String trxName,String sql,int int_param1, boolean noConvert ) {
        BigDecimal        retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );
            pstmt.setInt( 1,int_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );
            } else {
                log.info( "No Value " + sql + " - Param1=" + int_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValueBD
    
    
   

    public static Timestamp getSQLValueTimestamp( String trxName,String sql) {
    	return getSQLValueTimestamp( trxName, sql, false); 
    }

    /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */    
    public static Timestamp getSQLValueTimestamp( String trxName,String sql, boolean noConvert) {
        Timestamp        retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = prepareStatement( sql,trxName, noConvert );


            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getTimestamp( 1 );
            } else {
                log.info( "No Value " + sql );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValueTimestamp

    
    
    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param optional
     *
     * @return
     */

    public static KeyNamePair[] getKeyNamePairs( String sql,boolean optional ) {
        PreparedStatement pstmt = null;
        ArrayList         list  = new ArrayList();

        if( optional ) {
            list.add( new KeyNamePair( -1,"" ));
        }

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
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

        KeyNamePair[] retValue = new KeyNamePair[ list.size()];

        list.toArray( retValue );

        // s_log.fine("getKeyNamePairs #" + retValue.length);

        return retValue;
    }    // getKeyNamePairs
    
    /**
     * Return a list generated from a result set 
     * @param rs result set
     * @return the list
     * 
     */
    public static List<List<Object>> toList(ResultSet rs){
    	
    	List<List<Object>> listOfRecord = new ArrayList<List<Object>>();
    	
    	try{
    		
    		// Obtengo el número de columnas del result set 
	    	int columns = rs.getMetaData().getColumnCount();
	    	
	    	//
	    	while (rs.next()) {
	    		
	    		List<Object> record = new ArrayList<Object>();
	
	    		for (int i = 1; i <= columns ; i++) {
			    	Object value = rs.getObject(i);
			    	record.add(value);
		    	}
		    	
	    		listOfRecord.add(record);
	    	}
    	} catch(Exception e){
    		log.log( Level.SEVERE,"Result set to List failed",e );
    	}
    	
    	return listOfRecord;
    } //	toList
    

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     * @param whereClause
     *
     * @return
     */

    public static boolean isSOTrx( String TableName,String whereClause ) {
        return isSOTrx(TableName, whereClause, true);
    }    // isSOTrx

    /**
     * Obtiene el valor del campo IsSOTrx de un registro en una tabla específica. La tabla puede
     * no contener el campo IsSOTrx, con lo cual en este caso se retorna el valor por defecto indicado.
     * @param TableName Nombre de la tabla a la que se realiza la consulta.
     * @param whereClause Filtro para obtener el registro a comprobar.
     * @param defValue Valor retornado en caso de que la tabla no contenga la columna IsSOTrx.
     * @return El valor <code>Boolean</code> del campo IsSOTrx del registro, o el <code>defValue</code> si
     * la tabla no tiene el campo IsSOTrx.
     */
    public static boolean isSOTrx( String TableName,String whereClause, boolean defValue) {
        if( (TableName == null) || (TableName.length() == 0) ) {
            log.severe( "No TableName" );

            return true;
        }

        if( (whereClause == null) || (whereClause.length() == 0) ) {
            log.severe( "No Where Clause" );

            return true;
        }

        //

        boolean isSOTrx = defValue;
        String  sql     = "SELECT IsSOTrx FROM " + TableName + " WHERE " + whereClause;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                isSOTrx = "Y".equals( rs.getString( 1 ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.finest( sql + " - " + e.getMessage());
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return isSOTrx;
    }
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param TableName
     * @param trxName
     *
     * @return
     */

    public static int getNextID( Properties ctx,String TableName,String trxName ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Context missing" );
        }

        if( (TableName == null) || (TableName.length() == 0) ) {
            throw new IllegalArgumentException( "TableName missing" );
        }

        return getNextID( Env.getAD_Client_ID( ctx ),TableName,trxName );
    }    // getNextID

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param TableName
     * @param trxName
     *
     * @return
     */

    public static int getNextID( int AD_Client_ID,String TableName,String trxName ) {
        if( ( (trxName == null) || (trxName.length() == 0) ) && isRemoteObjects()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    int id = server.getNextID( AD_Client_ID,TableName,null );

                    log.finest( "server => " + id );

                    if( id < 0 ) {
                        throw new DBException( "No NextID" );
                    }

                    return id;
                }

                log.log( Level.SEVERE,"AppsServer not found - " + TableName );
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"AppsServer error",ex );
            }

            // Try locally

        }

        int id = MSequence.getNextID( AD_Client_ID,TableName,trxName );    // tries 3 times

        // if (id <= 0)
        // throw new DBException("No NextID (" + id + ")");

        return id;
    }    // getNextID

    /**
     * Descripción de Método
     *
     *
     * @param C_DocType_ID
     * @param trxName
     *
     * @return
     */

    public static String getDocumentNo( int C_DocType_ID,String trxName ) {
        if( ( (trxName == null) || (trxName.length() == 0) ) && isRemoteObjects()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    String dn = server.getDocumentNo( C_DocType_ID,trxName );

                    log.finest( "Server => " + dn );

                    if( dn != null ) {
                        return dn;
                    }
                }

                log.log( Level.SEVERE,"AppsServer not found - " + C_DocType_ID );
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"AppsServer error",ex );
            }
        }

        // fallback

        String dn = MSequence.getDocumentNo( C_DocType_ID,trxName );

        if( dn == null ) {    // try again
            dn = MSequence.getDocumentNo( C_DocType_ID,trxName );
        }

        // if (dn == null)
        // throw new DBException ("No DocumentNo");

        return dn;
    }    // getDocumentNo

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param TableName
     * @param trxName
     *
     * @return
     */

    public static String getDocumentNo( int AD_Client_ID,String TableName,String trxName ) {
        if( ( (trxName == null) || (trxName.length() == 0) ) && isRemoteObjects()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    String dn = server.getDocumentNo( AD_Client_ID,TableName,trxName );

                    log.finest( "Server => " + dn );

                    if( dn != null ) {
                        return dn;
                    }
                }

                log.log( Level.SEVERE,"AppsServer not found - " + TableName );
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"AppsServer error",ex );
            }
        }

        // fallback

        String dn = MSequence.getDocumentNo( AD_Client_ID,TableName,trxName );

        if( dn == null ) {    // try again
            dn = MSequence.getDocumentNo( AD_Client_ID,TableName,trxName );
        }

        if( dn == null ) {
            throw new DBException( "No DocumentNo" );
        }

        return dn;
    }    // getDocumentNo

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param TableName
     * @param onlyDocType
     * @param trxName
     *
     * @return
     */

    public static String getDocumentNo( Properties ctx,int WindowNo,String TableName,boolean onlyDocType,String trxName ) {
        if( (ctx == null) || (TableName == null) || (TableName.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing" );
        }

        int AD_Client_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Client_ID" );

        // Get C_DocType_ID from context - NO Defaults -

        int C_DocType_ID = Env.getContextAsInt( ctx,WindowNo + "|C_DocTypeTarget_ID" );

        if( C_DocType_ID == 0 ) {
            C_DocType_ID = Env.getContextAsInt( ctx,WindowNo + "|C_DocType_ID" );
        }

        if( C_DocType_ID == 0 ) {
            log.fine( "Window=" + WindowNo + " - Target=" + Env.getContextAsInt( ctx,WindowNo + "|C_DocTypeTarget_ID" ) + "/" + Env.getContextAsInt( ctx,WindowNo,"C_DocTypeTarget_ID" ) + " - Actual=" + Env.getContextAsInt( ctx,WindowNo + "|C_DocType_ID" ) + "/" + Env.getContextAsInt( ctx,WindowNo,"C_DocType_ID" ));

            return getDocumentNo( AD_Client_ID,TableName,trxName );
        }

        String retValue = getDocumentNo( C_DocType_ID,trxName );

        if( !onlyDocType && (retValue == null) ) {
            return getDocumentNo( AD_Client_ID,TableName,trxName );
        }

        return retValue;
    }    // getDocumentNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isRemoteObjects() {
        return Ini.isClient() && ( Ini.isServerObjects() || CConnection.get().isRMIoverHTTP()) && CConnection.get().isAppsServerOK( false );
    }    // isRemoteObjects

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isRemoteProcess() {
        return Ini.isClient() && ( Ini.isServerProcess() || CConnection.get().isRMIoverHTTP()) && CConnection.get().isAppsServerOK( false );
    }    // isRemoteProcess

    /**
     * Descripción de Método
     *
     *
     * @param comment
     * @param warning
     */

    public static void printWarning( String comment,SQLWarning warning ) {
        if( (comment == null) || (warning == null) || (comment.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing" );
        }

        log.warning( comment );

        if( warning == null ) {
            return;
        }

        //

        SQLWarning warn = warning;

        while( warn != null ) {
            StringBuffer buffer = new StringBuffer();

            buffer.append( warn.getMessage()).append( "; State=" ).append( warn.getSQLState()).append( "; ErrorCode=" ).append( warn.getErrorCode());
            log.warning( buffer.toString());
            warn = warn.getNextWarning();
        }
    }    // printWarning

    /**
     * Descripción de Método
     *
     *
     * @param time
     * @param dayOnly
     *
     * @return
     */

    public static String TO_DATE( Timestamp time,boolean dayOnly ) {
        return s_cc.getDatabase().TO_DATE( time,dayOnly );
    }    // TO_DATE
    
    
    /**
     * Descripción de Método
     *
     *
     * @param day
     *
     * @return
     */

    public static String TO_DATE( Timestamp day ) {
        return TO_DATE( day,true );
    }    // TO_DATE

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param displayType
     * @param AD_Language
     *
     * @return
     */

    public static String TO_CHAR( String columnName,int displayType,String AD_Language ) {
        if( (columnName == null) || (AD_Language == null) || (columnName.length() == 0) ) {
            throw new IllegalArgumentException( "Required parameter missing" );
        }

        return s_cc.getDatabase().TO_CHAR( columnName,displayType,AD_Language );
    }    // TO_CHAR

    /**
     * Descripción de Método
     *
     *
     * @param number
     * @param displayType
     *
     * @return
     */

    public static String TO_NUMBER( BigDecimal number,int displayType ) {
        return s_cc.getDatabase().TO_NUMBER( number,displayType );
    }    // TO_NUMBER

    /**
     * Descripción de Método
     *
     *
     * @param txt
     *
     * @return
     */

    public static String TO_STRING( String txt ) {
        return TO_STRING( txt,0 );
    }    // TO_STRING

    /**
     * Descripción de Método
     *
     *
     * @param txt
     * @param maxLength
     *
     * @return
     */

    public static String TO_STRING( String txt,int maxLength ) {
        if( (txt == null) || (txt.length() == 0) ) {
            return "NULL";
        }

        // Length

        String text = txt;

        if( (maxLength != 0) && (text.length() > maxLength) ) {
            text = txt.substring( 0,maxLength );
        }

        // copy characters             (we need to look through anyway)

        StringBuffer out = new StringBuffer();

        out.append( QUOTE );    // '

        for( int i = 0;i < text.length();i++ ) {
            char c = text.charAt( i );

            if( c == QUOTE ) {
                out.append( "''" );
            } else {
                out.append( c );
            }
        }

        out.append( QUOTE );    // '

        //

        return out.toString();
    }    // TO_STRING

    /** Descripción de Campos */

    private static final char QUOTE = '\'';

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        OpenXpertya.startup( true );

        MSystem system = MSystem.get( Env.getCtx());

        system.setIsJustMigrated( true );
        afterMigration( Env.getCtx());
    }    // main
    
    public static String getErrorMsg(SQLException exception) {
    	return Msg.translate(Env.getCtx(), getDatabase().getErrorMsg(exception));
    }
    
    public static String TO_DATEFORMAT( String columnName,int displayType,String AD_Language ) {
    	if( (columnName == null) || (AD_Language == null) || (columnName.length() == 0) ) {
    		throw new IllegalArgumentException( "Required parameter missing" );
    	}
    	
    	return s_cc.getDatabase().TO_DATEFORMAT( columnName,displayType,AD_Language );
    }    // TO_CHAR

	//TODO Hernandez
	/**
     * Get String Value from sql
     * @param trxName trx
     * @param sql sql
     * @param params array of parameters
     * @return first value or null
     * @throws DBException if there is any SQLException
     */
    public static String getSQLValueStringEx (String trxName, String sql, Object... params)
    {
    	String retValue = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try
    	{
    		pstmt = prepareStatement(sql, trxName);
    		setParameters(pstmt, params);
    		rs = pstmt.executeQuery();
    		if (rs.next())
    			retValue = rs.getString(1);
    		else
    			log.info("No Value " + sql);
    	}
    	catch (SQLException e)
    	{
    		throw new DBException(e, sql);
    	}
    	finally
    	{
    		close(rs, pstmt);
    		rs = null; pstmt = null;
    	}
    	return retValue;
    }
    
    /**
     * Get String Value from sql
     * @param trxName trx
     * @param sql sql
     * @param params array of parameters
     * @return first value or null
     */
    public static String getSQLValueString (String trxName, String sql, Object... params)
    {
    	String retValue = null;
    	try
    	{
    		retValue = getSQLValueStringEx(trxName, sql, params);
    	}
    	catch (Exception e)
    	{
    		log.log(Level.SEVERE, sql, getSQLException(e));
    	}
    	return retValue;
    }
    
    /**
	 * Set parameters for given statement
	 * @param stmt statements
	 * @param params parameters list; if null or empty list, no parameters are set
	 */
	public static void setParameters(PreparedStatement stmt, List<?> params)
	throws SQLException
	{
		if (params == null || params.size() == 0)
		{
			return;
		}
		for (int i = 0; i < params.size(); i++)
		{
			setParameter(stmt, i+1, params.get(i));
		}
	}


	   //TODO Hernandez
    /**
     * Get int Value from sql
     * @param trxName trx
     * @param sql sql
     * @param params array of parameters
     * @return first value or -1 if not found
     * @throws DBException if there is any SQLException
     */
    public static int getSQLValueEx (String trxName, String sql, Object... params) throws DBException
    {
    	int retValue = -1;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try
    	{
    		pstmt = prepareStatement(sql, trxName);
    		setParameters(pstmt, params);
    		rs = pstmt.executeQuery();
    		if (rs.next())
    			retValue = rs.getInt(1);
    		else
    			log.info("No Value " + sql);
    	}
    	catch (SQLException e)
    	{
    		throw new DBException(e, sql);
    	}
    	finally
    	{
    		close(rs, pstmt);
    		rs = null; pstmt = null;
    	}
    	return retValue;
    }
    
    /**
	 * Set parameters for given statement
	 * @param stmt statements
	 * @param params parameters array; if null or empty array, no parameters are set
	 */
	public static void setParameters(PreparedStatement stmt, Object[] params)
	throws SQLException
	{
		if (params == null || params.length == 0) {
			return;
		}
		//
		for (int i = 0; i < params.length; i++)
		{
			setParameter(stmt, i+1, params[i]);
		}
	}
	/**
	 * Set PreparedStatement's parameter.
	 * Similar with calling <code>pstmt.setObject(index, param)</code>
	 * @param pstmt
	 * @param index
	 * @param param
	 * @throws SQLException
	 */
	public static void setParameter(PreparedStatement pstmt, int index, Object param)
	throws SQLException
	{
		if (param == null)
			pstmt.setObject(index, null);
		else if (param instanceof String)
			pstmt.setString(index, (String)param);
		else if (param instanceof Integer)
			pstmt.setInt(index, ((Integer)param).intValue());
		else if (param instanceof BigDecimal)
			pstmt.setBigDecimal(index, (BigDecimal)param);
		else if (param instanceof Timestamp)
			pstmt.setTimestamp(index, (Timestamp)param);
		else if (param instanceof Boolean)
			pstmt.setString(index, ((Boolean)param).booleanValue() ? "Y" : "N");
		else
			throw new DBException("Unknown parameter type "+index+" - "+param);
	}
	
    /**
	 * Try to get the SQLException from Exception
	 * @param e Exception
	 * @return SQLException if found or provided exception elsewhere
	 */
    public static Exception getSQLException(Exception e)
    {
    	Throwable e1 = e;
    	while (e1 != null)
    	{
	    	if (e1 instanceof SQLException)
	    		return (SQLException)e1;
	    	e1 = e1.getCause();
    	}
    	return e;
    }
	/** SQL Statement Separator "; "	*/
	public static final String SQLSTATEMENT_SEPARATOR = "; "; 
	
	public static Timestamp getDBTimestamp(String trxName){
		return getSQLValueTimestamp(trxName, "SELECT now()");
	}
}    // DB



/*
 *  @(#)DB.java   25.03.06
 * 
 *  Fin del fichero DB.java
 *  
 *  Versión 2.2
 *
 */
