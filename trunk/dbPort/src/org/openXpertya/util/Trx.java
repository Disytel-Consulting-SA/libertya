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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.logging.Level;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Trx implements VetoableChangeListener {

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     * @param createNew
     *
     * @return
     */

    public static Trx get( String trxName,boolean createNew ) {
        if( (trxName == null) || (trxName.length() == 0) ) {
            throw new IllegalArgumentException( "No Transaction Name" );
        }

        if( s_cache == null ) {
            s_cache = new CCache( "Trx",10,-1 );    // no expiration
            s_cache.addVetoableChangeListener( new Trx( "controller" ));
        }

        Trx retValue = ( Trx )s_cache.get( trxName );

        if( (retValue == null) && createNew ) {
            retValue = new Trx( trxName );
            s_cache.put( trxName,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = null;    // create change listener

    /**
     * Descripción de Método
     *
     *
     * @param prefix
     *
     * @return
     */

    public static String createTrxName( String prefix ) {
        if( (prefix == null) || (prefix.length() == 0) ) {
            prefix = "Trx";
        }

        prefix += "_" + System.currentTimeMillis();

        return prefix;
    }    // createTrxName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String createTrxName() {
        return createTrxName( null );
    }    // createTrxName

    /**
     * Constructor de la clase ...
     *
     *
     * @param trxName
     */

    private Trx( String trxName ) {
        this( trxName,null );
    }    // Trx

    /**
     * Constructor de la clase ...
     *
     *
     * @param trxName
     * @param con
     */

    private Trx( String trxName,Connection con ) {

        // log.info (trxName);

        setTrxName( trxName );
        setConnection( con );
    }    // Trx

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Connection m_connection = null;

    /** Descripción de Campos */

    private String m_trxName = null;

    /** Descripción de Campos */

    private Savepoint m_savepoint = null;

    /** Descripción de Campos */

    private boolean m_active = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Connection getConnection() {
        log.log( Level.ALL,"Active=" + isActive() + ", Connection=" + m_connection );

        if( m_connection == null ) {    // get new Connection
            setConnection( DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED ));
        }

        if( !isActive()) {
            start();
        }

        // System.err.println ("Trx.getConnection - " + m_name + ": "+ m_connection);
        // Trace.printStack();

        return m_connection;
    }    // getConnection

    /**
     * Descripción de Método
     *
     *
     * @param conn
     */

    private void setConnection( Connection conn ) {
        if( conn == null ) {
            return;
        }

        m_connection = conn;
        log.finest( "Connection=" + conn );

        try {
            m_connection.setAutoCommit( false );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"connection",e );
        }
    }    // setConnection

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     */

    private void setTrxName( String trxName ) {
        if( (trxName == null) || (trxName.length() == 0) ) {
            throw new IllegalArgumentException( "No Transaction Name" );
        }

        m_trxName = trxName;
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxName() {
        return m_trxName;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean start() {
        if( (m_savepoint != null) || m_active ) {
            log.warning( "Trx in progress " + m_trxName + " - " + m_savepoint );

            return false;
        }

        m_active = true;

        try {
            if( m_connection != null ) {
                m_savepoint = m_connection.setSavepoint( m_trxName );
                log.info( "**** " + getTrxName());
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,m_trxName,e );
            m_savepoint = null;

            return false;
        }

        return true;
    }    // startTrx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Savepoint getSavepoint() {
        return m_savepoint;
    }    // getSavepoint

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isActive() {
        return m_active;
    }    // isActive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rollback() {
        try {
            if( m_connection != null ) {
                if( m_savepoint == null ) {
                    m_connection.rollback();
                } else {
                    m_connection.rollback( m_savepoint );
                }

                log.info( "**** " + m_trxName );
                m_savepoint = null;
                m_active    = false;

                return true;
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,m_trxName,e );
        }

        m_savepoint = null;
        m_active    = false;

        return false;
    }    // rollback

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean commit() {
        try {
            if( m_connection != null ) {
                m_connection.commit();
                log.info( "**** " + m_trxName );
                m_savepoint = null;
                m_active    = false;

                return true;
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,m_trxName,e );
        }

        m_savepoint = null;
        m_active    = false;

        return false;
    }    // commit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean close() {
        if( s_cache != null ) {
            s_cache.remove( getTrxName());
        }

        //

        if( m_connection == null ) {
            return true;
        }

        if( (m_savepoint != null) || isActive()) {
            commit();
        }

        // Close Connection

        try {
            m_connection.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,m_trxName,e );
        }

        m_savepoint  = null;
        m_connection = null;
        m_active     = false;
        log.config( m_trxName );

        return true;
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Trx[" );

        sb.append( getTrxName()).append( ",Active=" ).append( isActive()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */

    public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {
        log.info( evt.toString());
    }    // vetoableChange
    
    
	/**
	 * Crea y retorna una transacción
	 * @return una nueva transacción 
	 */
	public static Trx createTrx(String trxName){
		//Creo la transacción
		return Trx.get(trxName, true);
	}
	
	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre contenido en la variable de instancia o una nueva
	 */
	public static Trx getTrx(String trxName){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(trxName, false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx(trxName);
		}
		
		return trx;
	}   
    
}    // Trx



/*
 *  @(#)Trx.java   25.03.06
 * 
 *  Fin del fichero Trx.java
 *  
 *  Versión 2.2
 *
 */
