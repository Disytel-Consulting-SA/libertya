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
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MPreference;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Trx implements VetoableChangeListener {

	/** Entrada en AD_Preference de tipo Entero */
	protected static final String OPEN_TRX_LOWER_LIMIT_PREFERENCE 	= "OpenTrxLowerLimit";
	/** Entrada en AD_Preference de tipo Long */
	protected static final String OPEN_TRX_TIMEOUT_MS_PREFERENCE 	= "OpenTrxTimeOutMS";
	/** Entrada en AD_Preference de tipo Entero  */
	protected static final String OPEN_TRX_UPPER_LIMIT_PREFERENCE 	= "OpenTrxUpperLimit";
	
	/** Numero maximo de transacciones abiertas.  Sin embargo, si el tiempo de vida de una Trx es menor a OPEN_TRX_TIMEOUT_MS    
	 *  (o sea que son recientes), entonces podrán existir hasta OPEN_TRX_UPPER_LIMIT conexiones) */
	public static int OPEN_TRX_LOWER_LIMIT = 0;		// 		3;
	
	/** Tiempo minimo para que pueda transaccion pueda ser cerrada (si todavía no se llegó al límite superior  
	 *  OPEN_TRX_UPPER_LIMIT.  Si se llegó a este límite no se contempla el tiempo, forzándo a cerrar la Trx "más vieja" indefectiblemente) */
	public static long OPEN_TRX_TIMEOUT_MS = 0; 	//		60000L * 60;

	/** Numero maximo total de transacciones abiertas sin contemplar el tiempo de las Trx
	 *  (si se llega a este limite, se van cerrando sin importar la antigüedad de las mismas) */
	public static int OPEN_TRX_UPPER_LIMIT = 0;		//		6;

	/** Transacciones abiertas */
	protected static ArrayList<Trx> openTrx = new ArrayList<Trx>();

	static {
		try {
			// Lower limit preference
			String value = MPreference.searchCustomPreferenceValue(OPEN_TRX_LOWER_LIMIT_PREFERENCE, 0, 0, 0, true);
			if (!Util.isEmpty(value))
				OPEN_TRX_LOWER_LIMIT = Integer.parseInt(value);
			// Upper limit preference
			value = MPreference.searchCustomPreferenceValue(OPEN_TRX_UPPER_LIMIT_PREFERENCE, 0, 0, 0, true);
			if (!Util.isEmpty(value))
				OPEN_TRX_UPPER_LIMIT = Integer.parseInt(value);
			// Time Out limit preference
			value = MPreference.searchCustomPreferenceValue(OPEN_TRX_TIMEOUT_MS_PREFERENCE, 0, 0, 0, true);
			if (!Util.isEmpty(value))
				OPEN_TRX_TIMEOUT_MS = Long.parseLong(value);
		}
		catch (Exception e) { 
			CLogger.getLogger(Trx.class.toString()).log(Level.WARNING, "Error al configurar limites en transacciones: " + e.getMessage()); 
		}
	}
	
	
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
            
            // Logica de contención por eventuales connection leaks.
            // =====================================================
        	openTrx.add(retValue);
        	// Si se superó el primer limite máximo de transacciones abiertas,
        	// recuperar la transaccion más vieja y evaluar si es factible cerrarla
        	if (OPEN_TRX_LOWER_LIMIT > 0 && openTrx.size() > OPEN_TRX_LOWER_LIMIT) {
        		Trx target = openTrx.get(0);
        		// Si la transacción no es "demasiado vieja", no cerrarla todavía
        		if (OPEN_TRX_TIMEOUT_MS > 0 && System.currentTimeMillis() - target.getCreated() > OPEN_TRX_TIMEOUT_MS)
        			target.close();
        	}
        	// Si se superó el segundo límite máximo de transacciones abiertas, recuperar  
        	// y cerrar la transacción más vieja sin contemplar la antigüedad de la misma
        	if (OPEN_TRX_UPPER_LIMIT > 0 && openTrx.size() > OPEN_TRX_UPPER_LIMIT)
        		openTrx.get(0).close();
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
        setCreated( System.currentTimeMillis() );
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

    /** Created timestamp */ 
    
    private long created = -1;
    
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
        
        // Remover la trx de las abiertas (ver metodo get(String, boolean))
        if (openTrx!=null)
        	openTrx.remove(this);

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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
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
