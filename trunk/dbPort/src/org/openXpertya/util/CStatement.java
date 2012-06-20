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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.sql.RowSet;

import org.openXpertya.db.BaseDatosOXP;
import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CStatement implements Statement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param resultSetType
     * @param resultSetConcurrency
     * @param trxName
     */

    public CStatement( int resultSetType,int resultSetConcurrency,String trxName ) {
        p_vo = new CStatementVO( resultSetType,resultSetConcurrency );

        // Local access

        if( !DB.isRemoteObjects()) {
            try {
                Connection conn = null;
                Trx        trx  = (trxName == null)
                                  ?null
                                  :Trx.get( trxName,true );

                if( trx != null ) {
                    conn = trx.getConnection();
                } else {
                    if( resultSetConcurrency == ResultSet.CONCUR_UPDATABLE ) {
                        conn = DB.getConnectionRW();
                    } else {
                        conn = DB.getConnectionRO();
                    }
                }

                if( conn == null ) {
                    throw new DBException( "No Connection" );
                }

                p_stmt = conn.createStatement( resultSetType,resultSetConcurrency );

                return;
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"CStatement",e );
            }
        }
    }    // CPreparedStatement

    /**
     * Constructor de la clase ...
     *
     */

    protected CStatement() {
        super();
    }    // CStatement

    /**
     * Constructor de la clase ...
     *
     *
     * @param vo
     */

    public CStatement( CStatementVO vo ) {
        p_vo = vo;
    }    // CPreparedStatement

    /** Descripción de Campos */

    protected transient CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    protected transient Statement p_stmt = null;

    /** Descripción de Campos */

    protected CStatementVO p_vo = null;

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     *
     * @return
     *
     * @throws SQLException
     */

    public ResultSet executeQuery( String sql0 ) throws SQLException {

        // Convert SQL

        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {    // local
            return p_stmt.executeQuery( p_vo.getSql());
        }

        // Client -> remote sever

        log.finest( "server => " + p_vo + ", Remote=" + DB.isRemoteObjects());

        try {
            if( DB.isRemoteObjects() && CConnection.get().isAppsServerOK( false )) {
                Server server = CConnection.get().getServer();

                if( server != null ) {
                    ResultSet rs = server.stmt_getRowSet( p_vo );

                    if( rs == null ) {
                        log.warning( "ResultSet is null - " + p_vo );
                    }

                    return rs;
                }

                log.log( Level.SEVERE,"AppsServer not found" );
            }
        } catch( RemoteException ex ) {
            log.log( Level.SEVERE,"AppsServer error",ex );
        }

        // Try locally

        log.warning( "execute locally" );

        Statement stmt = local_getStatement( false,null );    // shared connection

        return stmt.executeQuery( p_vo.getSql());
    }    // executeQuery

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     *
     * @return
     *
     * @throws SQLException
     */

    public int executeUpdate( String sql0 ) throws SQLException {

        // Convert SQL

        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {    // local
            return p_stmt.executeUpdate( p_vo.getSql());
        }

        // Client -> remote sever

        log.finest( "server => " + p_vo + ", Remote=" + DB.isRemoteObjects());

        try {
            if( DB.isRemoteObjects() && CConnection.get().isAppsServerOK( false )) {
                Server server = CConnection.get().getServer();

                if( server != null ) {
                    int result = server.stmt_executeUpdate( p_vo );

                    p_vo.clearParameters();    // re-use of result set

                    return result;
                }

                log.log( Level.SEVERE,"AppsServer not found" );
            }
        } catch( RemoteException ex ) {
            log.log( Level.SEVERE,"AppsServer error",ex );
        }

        // Try locally

        log.warning( "execute locally" );

        Statement pstmt = local_getStatement( false,null );    // shared connection

        return pstmt.executeUpdate( p_vo.getSql());
    }    // executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSql() {
        if( p_vo != null ) {
            return p_vo.getSql();
        }

        return null;
    }    // getSql

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public Connection getConnection() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getConnection();
        }

        return null;
    }    // getConnection

    /**
     * Descripción de Método
     *
     *
     * @throws SQLException
     */

    public void commit() throws SQLException {
        Connection conn = getConnection();

        if( (conn != null) &&!conn.getAutoCommit()) {
            conn.commit();
            log.fine( "commit" );
        }
    }    // commit

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param autoGeneratedKeys
     *
     * @return
     *
     * @throws SQLException
     */

    public int executeUpdate( String sql0,int autoGeneratedKeys ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.executeUpdate( p_vo.getSql(),autoGeneratedKeys );
        }

        throw new java.lang.UnsupportedOperationException( "Method executeUpdate() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param columnIndexes
     *
     * @return
     *
     * @throws SQLException
     */

    public int executeUpdate( String sql0,int[] columnIndexes ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.executeUpdate( p_vo.getSql(),columnIndexes );
        }

        throw new java.lang.UnsupportedOperationException( "Method executeUpdate() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param columnNames
     *
     * @return
     *
     * @throws SQLException
     */

    public int executeUpdate( String sql0,String[] columnNames ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.executeUpdate( p_vo.getSql(),columnNames );
        }

        throw new java.lang.UnsupportedOperationException( "Method executeUpdate() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean execute( String sql0 ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.execute( p_vo.getSql());
        }

        throw new java.lang.UnsupportedOperationException( "Method execute() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param autoGeneratedKeys
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean execute( String sql0,int autoGeneratedKeys ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.execute( p_vo.getSql(),autoGeneratedKeys );
        }

        throw new java.lang.UnsupportedOperationException( "Method execute() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param columnIndexes
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean execute( String sql0,int[] columnIndexes ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.execute( p_vo.getSql(),columnIndexes );
        }

        throw new java.lang.UnsupportedOperationException( "Method execute() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql0
     * @param columnNames
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean execute( String sql0,String[] columnNames ) throws SQLException {
        p_vo.setSql( DB.getDatabase().convertStatement( sql0 ));

        if( p_stmt != null ) {
            return p_stmt.execute( p_vo.getSql(),columnNames );
        }

        throw new java.lang.UnsupportedOperationException( "Method execute() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getMaxFieldSize() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getMaxFieldSize();
        }

        throw new java.lang.UnsupportedOperationException( "Method getMaxFieldSize() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param max
     *
     * @throws SQLException
     */

    public void setMaxFieldSize( int max ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setMaxFieldSize( max );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setMaxFieldSize() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getMaxRows() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getMaxRows();
        }

        throw new java.lang.UnsupportedOperationException( "Method getMaxRows() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param max
     *
     * @throws SQLException
     */

    public void setMaxRows( int max ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setMaxRows( max );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setMaxRows() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param sql
     *
     * @throws SQLException
     */

    public void addBatch( String sql ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.addBatch( sql );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method addBatch() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @throws SQLException
     */

    public void clearBatch() throws SQLException {
        if( p_stmt != null ) {
            p_stmt.clearBatch();
        } else {
            throw new java.lang.UnsupportedOperationException( "Method clearBatch() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int[] executeBatch() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.executeBatch();
        }

        throw new java.lang.UnsupportedOperationException( "Method executeBatch() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param current
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean getMoreResults( int current ) throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getMoreResults( current );
        }

        throw new java.lang.UnsupportedOperationException( "Method getMoreResults() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public ResultSet getGeneratedKeys() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getGeneratedKeys();
        }

        throw new java.lang.UnsupportedOperationException( "Method getGeneratedKeys() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getResultSetHoldability() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getResultSetHoldability();
        }

        throw new java.lang.UnsupportedOperationException( "Method getResultSetHoldability() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param enable
     *
     * @throws SQLException
     */

    public void setEscapeProcessing( boolean enable ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setEscapeProcessing( enable );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setEscapeProcessing() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getQueryTimeout() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getQueryTimeout();
        }

        throw new java.lang.UnsupportedOperationException( "Method getQueryTimeout() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param seconds
     *
     * @throws SQLException
     */

    public void setQueryTimeout( int seconds ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setQueryTimeout( seconds );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setQueryTimeout() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @throws SQLException
     */

    public void cancel() throws SQLException {
        if( p_stmt != null ) {
            p_stmt.cancel();
        } else {
            throw new java.lang.UnsupportedOperationException( "Method cancel() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public SQLWarning getWarnings() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getWarnings();
        }

        throw new java.lang.UnsupportedOperationException( "Method getWarnings() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @throws SQLException
     */

    public void clearWarnings() throws SQLException {
        if( p_stmt != null ) {
            p_stmt.clearWarnings();
        } else {
            throw new java.lang.UnsupportedOperationException( "Method clearWarnings() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @throws SQLException
     */

    public void setCursorName( String name ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setCursorName( name );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setCursorName() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public ResultSet getResultSet() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getResultSet();
        }

        throw new java.lang.UnsupportedOperationException( "Method getResultSet() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getUpdateCount() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getUpdateCount();
        }

        throw new java.lang.UnsupportedOperationException( "Method getUpdateCount() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public boolean getMoreResults() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getMoreResults();
        }

        throw new java.lang.UnsupportedOperationException( "Method getMoreResults() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param direction
     *
     * @throws SQLException
     */

    public void setFetchDirection( int direction ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setFetchDirection( direction );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setFetchDirection() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getFetchDirection() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getFetchDirection();
        }

        throw new java.lang.UnsupportedOperationException( "Method getFetchDirection() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @param rows
     *
     * @throws SQLException
     */

    public void setFetchSize( int rows ) throws SQLException {
        if( p_stmt != null ) {
            p_stmt.setFetchSize( rows );
        } else {
            throw new java.lang.UnsupportedOperationException( "Method setFetchSize() not yet implemented." );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getFetchSize() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getFetchSize();
        }

        throw new java.lang.UnsupportedOperationException( "Method getFetchSize() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getResultSetConcurrency() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getResultSetConcurrency();
        }

        throw new java.lang.UnsupportedOperationException( "Method getResultSetConcurrency() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws SQLException
     */

    public int getResultSetType() throws SQLException {
        if( p_stmt != null ) {
            return p_stmt.getResultSetType();
        }

        throw new java.lang.UnsupportedOperationException( "Method getResultSetType() not yet implemented." );
    }

    /**
     * Descripción de Método
     *
     *
     * @throws SQLException
     */

    public void close() throws SQLException {
        if( p_stmt != null ) {
            p_stmt.close();
        }
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int remote_executeUpdate() {
        log.finest( "" );

        try {
            BaseDatosOXP db = CConnection.get().getDatabase();

            if( db == null ) {
                throw new NullPointerException( "Remote - No Database" );
            }

            //

            Statement pstmt = local_getStatement( false,null );    // shared connection
            int result = pstmt.executeUpdate( p_vo.getSql());

            pstmt.close();

            //

            return result;
        } catch( Exception ex ) {
            log.log( Level.SEVERE,p_vo.toString(),ex );

            throw new RuntimeException( ex );
        }
    }    // remote_executeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param dedicatedConnection
     * @param trxName
     *
     * @return
     */

    private Statement local_getStatement( boolean dedicatedConnection,String trxName ) {
        log.finest( "" );

        Connection conn = null;
        Trx        trx  = (trxName == null)
                          ?null
                          :Trx.get( trxName,true );

        if( trx != null ) {
            conn = trx.getConnection();
        } else {
            if( dedicatedConnection ) {
                conn = DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED );
            } else {
                conn = local_getConnection( trxName );
            }
        }

        Statement stmt = null;

        try {
            stmt = conn.createStatement( p_vo.getResultSetType(),p_vo.getResultSetConcurrency());
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"local",ex );

            try {
                if( stmt != null ) {
                    stmt.close();
                }

                stmt = null;
            } catch( SQLException ex1 ) {
            }
        }

        return stmt;
    }    // local_getStatement

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     *
     * @return
     */

    protected Connection local_getConnection( String trxName ) {
        Connection conn = null;
        Trx        trx  = (trxName == null)
                          ?null
                          :Trx.get( trxName,true );

        if( trx != null ) {
            conn = trx.getConnection();
        } else {
            if( p_vo.getResultSetConcurrency() == ResultSet.CONCUR_UPDATABLE ) {
                conn = DB.getConnectionRW();
            } else {
                conn = DB.getConnectionRO();
            }
        }

        return conn;
    }    // local_getConnection

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public RowSet remote_getRowSet() {
        log.finest( "remote" );

        // Shared Connection

        Connection        conn   = local_getConnection( null );
        PreparedStatement pstmt  = null;
        RowSet            rowSet = null;

        try {
            pstmt = conn.prepareStatement( p_vo.getSql(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );

            // Set Parameters

            ArrayList parameters = p_vo.getParameters();

            for( int i = 0;i < parameters.size();i++ ) {
                Object o = parameters.get( i );

                if( o == null ) {
                    throw new IllegalArgumentException( "Null Parameter #" + i );
                } else if( o instanceof NullParameter ) {
                    int type = (( NullParameter )o ).getType();

                    pstmt.setNull( i + 1,type );
                    log.finest( "#" + ( i + 1 ) + " - Null" );
                } else if( o instanceof Integer ) {
                    pstmt.setInt( i + 1,(( Integer )o ).intValue());
                    log.finest( "#" + ( i + 1 ) + " - int=" + o );
                } else if( o instanceof String ) {
                    pstmt.setString( i + 1,( String )o );
                    log.finest( "#" + ( i + 1 ) + " - String=" + o );
                } else if( o instanceof Timestamp ) {
                    pstmt.setTimestamp( i + 1,( Timestamp )o );
                    log.finest( "#" + ( i + 1 ) + " - Timestamp=" + o );
                } else if( o instanceof BigDecimal ) {
                    pstmt.setBigDecimal( i + 1,( BigDecimal )o );
                    log.finest( "#" + ( i + 1 ) + " - BigDecimal=" + o );
                } else {
                    throw new java.lang.UnsupportedOperationException( "Unknown Parameter Class=" + o.getClass());
                }
            }

            //

            ResultSet rs = pstmt.executeQuery();

            rowSet = CCachedRowSet.getRowSet( rs );
            pstmt.close();
            pstmt = null;
        } catch( Exception ex ) {
            log.log( Level.SEVERE,p_vo.toString(),ex );

            throw new RuntimeException( ex );
        }

        // Close Cursor

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"close pstmt",e );
        }

        return rowSet;
    }    // remote_getRowSet

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}    // CStatement



/*
 *  @(#)CStatement.java   25.03.06
 * 
 *  Fin del fichero CStatement.java
 *  
 *  Versión 2.2
 *
 */
