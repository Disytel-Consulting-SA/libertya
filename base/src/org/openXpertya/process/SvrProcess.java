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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class SvrProcess implements ProcessCall {

    /**
     * Constructor de la clase ...
     *
     */

    public SvrProcess() {}    // SvrProcess

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private ProcessInfo m_pi;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger(SvrProcess.class);

    /** Descripción de Campos */

    private boolean m_locked = false;

    /** Descripción de Campos */

    private PO m_lockedObject = null;

    /** Descripción de Campos */

    private Trx m_trx;

    /** Descripción de Campos */

    protected static String MSG_SaveErrorRowNotFound = "@SaveErrorRowNotFound@";

    /** Descripción de Campos */

    protected static String MSG_InvalidArguments = "@InvalidArguments@";

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param pi
     * @param trx
     *
     * @return
     */

    public final boolean startProcess( Properties ctx,ProcessInfo pi,Trx trx ) {

        // Preparation

        m_ctx = (ctx == null)
                ?Env.getCtx()
                :ctx;
        m_pi  = pi;
        m_trx = trx;
        lock();

        //

        process();
        unlock();

        return !m_pi.isError();
    }    // startProcess

    /**
     * Descripción de Método
     *
     */

    private void process() {
        String  msg   = null;
        boolean error = false;

        // Agregado por para validacion de acceso a Workflow
		if(Ini.isClient()){
			MRole rol = MRole.getDefault();
			Boolean acceso = rol.getProcessAccess( m_pi.getAD_Process_ID() );

			/* Es el proceso de postInstalacion de un componente? En ese caso permitir la ejecucion */
			acceso = (1 == DB.getSQLValue( m_trx.getTrxName() , " SELECT COUNT(1) FROM AD_Process_Access WHERE AD_Process_ID = " + m_pi.getAD_Process_ID() + " AND AD_Role_ID = 0"  ) ) || (acceso != null && acceso);
			
			/* Logica de plugins: Si se está realizando la instalación de un plugin, permitir la ejecucion de procesos (para ejecucion del postinstall ad-hoc) */
			if (PluginUtils.getPluginInstallerTrxName() != null)
				acceso = true;
			
			if (acceso==null || !acceso.booleanValue()) 
			{
				msg ="Usuario no autorizado"; 
				//log.debug (msg);
				error=true;
				unlock();			
				//	Parse Variables
				msg = Msg.parseTranslation(m_ctx, msg);
				m_pi.setSummary (msg, error);
				ProcessInfoUtil.saveLogToDB(m_pi);
				return;
			}
		}
		// fin modificacion

        try {
            prepare();
            msg = doIt();
        } catch( Exception e ) {
            msg = e.getMessage();

            if( msg == null ) {
                msg = e.toString();
            }

            if( e.getCause() != null ) {
                log.log( Level.SEVERE,msg,e.getCause());
            } else if( CLogMgt.isLevelFine()) {
                log.log( Level.WARNING,msg,e );
            } else {
                log.warning( msg );
            }

            error = true;

            // throw new RuntimeException(e);

        }

        unlock();

        // Parse Variables

        msg = Msg.parseTranslation( m_ctx,msg );
        m_pi.setSummary( msg,error );
        ProcessInfoUtil.saveLogToDB( m_pi );
    }    // process

    /**
     * Descripción de Método
     *
     */

    abstract protected void prepare();

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    abstract protected String doIt() throws Exception;

    /**
     * Descripción de Método
     *
     *
     * @param po
     *
     * @return
     */

    protected boolean lockObject( PO po ) {

        // Unlock existing

        if( m_locked || (m_lockedObject != null) ) {
            unlockObject();
        }

        // Nothing to lock

        if( po == null ) {
            return false;
        }

        m_lockedObject = po;
        m_locked       = m_lockedObject.lock();

        return m_locked;
    }    // lockObject

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean isLocked() {
        return m_locked;
    }    // isLocked

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean unlockObject() {
        boolean success = true;

        if( m_locked || (m_lockedObject != null) ) {
            success = m_lockedObject.unlock();
        }

        m_locked       = false;
        m_lockedObject = null;

        return success;
    }    // unlock

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcessInfo getProcessInfo() {
        return m_pi;
    }    // getProcessInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx() {
        return m_ctx;
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getName() {
        return m_pi.getTitle();
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getAD_PInstance_ID() {
        return m_pi.getAD_PInstance_ID();
    }    // getAD_PInstance_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getTable_ID() {
        return m_pi.getTable_ID();
    }    // getRecord_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getRecord_ID() {
        return m_pi.getRecord_ID();
    }    // getRecord_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getAD_User_ID() {
        if( (m_pi.getAD_User_ID() == null) || (m_pi.getAD_Client_ID() == null) ) {
            String sql = "SELECT AD_User_ID, AD_Client_ID FROM AD_PInstance WHERE AD_PInstance_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql, m_trx.getTrxName() );

                pstmt.setInt( 1,m_pi.getAD_PInstance_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    m_pi.setAD_User_ID( rs.getInt( 1 ));
                    m_pi.setAD_Client_ID( rs.getInt( 2 ));
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        if( m_pi.getAD_User_ID() == null ) {
            return 0;
        }

        return m_pi.getAD_User_ID().intValue();
    }    // getAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getAD_Client_ID() {
        if( m_pi.getAD_Client_ID() == null ) {
            getAD_User_ID();    // sets also Client

            if( m_pi.getAD_Client_ID() == null ) {
                return 0;
            }
        }

        return m_pi.getAD_Client_ID().intValue();
    }    // getAD_Client_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected ProcessInfoParameter[] getParameter() {
    	// Recuperar los parametros actuales (incluyendo eventuales predefinidos desde codigo NO existentes en Metadatos, como por  
    	// ejemplo los parametros PluginConstants.XML_CONTENT_PARAMETER_NAME y PluginConstants.JAR_FILE_URL en VPluginInstallerUtils)
        ProcessInfoParameter[] currentParams = m_pi.getParameter();

        // Recuperar los parametros especificados desde los metadatos
        ProcessInfoUtil.setParameterFromDB( m_pi, m_trx.getTrxName() );
        ProcessInfoParameter[] paramsOnDB = m_pi.getParameter();

        // Incorporar a los existentes en metadatos los eventuales predefinidos, omitiendo duplicados
        if (currentParams != null) {
        	for (ProcessInfoParameter aParam : currentParams) 
        		paramsOnDB = ProcessInfoUtil.addToArray(paramsOnDB, aParam);
        }
        
        m_pi.setParameter(paramsOnDB);
        return paramsOnDB;
    }    // getParameter

    /**
     * Descripción de Método
     *
     *
     * @param id
     * @param date
     * @param number
     * @param msg
     */

    public void addLog( int id,Timestamp date,BigDecimal number,String msg ) {
        if( m_pi != null ) {
            m_pi.addLog( id,date,number,msg );
        }

        log.info( id + " - " + date + " - " + number + " - " + msg );
    }    // addLog

    /**
     * Descripción de Método
     *
     *
     * @param className
     * @param methodName
     * @param args
     *
     * @return
     */

    public Object doIt( String className,String methodName,Object args[] ) {
    	//JOptionPane.showMessageDialog( null,"En SvrProcess-doIt, className:="+className,"..Fin", JOptionPane.INFORMATION_MESSAGE );
        try {
            Class    clazz   = Class.forName( className );
            Object   object  = clazz.newInstance();
            Method[] methods = clazz.getMethods();

            for( int i = 0;i < methods.length;i++ ) {
                if( methods[ i ].getName().equals( methodName )) {
                    return methods[ i ].invoke( object,args );
                }
            }
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"doIt",ex );

            throw new RuntimeException( ex );
        }

        return null;
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void lock() {
        log.fine( "AD_PInstance_ID=" + m_pi.getAD_PInstance_ID());
        DB.executeUpdate( "UPDATE AD_PInstance SET IsProcessing='Y' WHERE AD_PInstance_ID=" + m_pi.getAD_PInstance_ID(), get_TrxName());
    }    // lock

    /**
     * Descripción de Método
     *
     */

    private void unlock() {
        MPInstance mpi = new MPInstance( getCtx(),m_pi.getAD_PInstance_ID(), get_TrxName() );

        if( mpi.getID() == 0 ) {
            log.log( Level.SEVERE,"Did not find PInstance " + m_pi.getAD_PInstance_ID());

            return;
        }

        mpi.setIsProcessing( false );
        mpi.setResult( m_pi.isError());
        mpi.setErrorMsg( m_pi.getSummary());
        mpi.save();
        log.fine( mpi.toString());
    }    // unlock

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String get_TrxName() {

        // Most Processes are "trx" save !!  DO NOT ENABLE
        if (m_trx != null)
        	return m_trx.getTrxName();

        return null;
    }    // get_TrxName
    
    
	/**
	 * Crea y retorna una transacción
	 * @return una nueva transacción 
	 */
	
	protected Trx createTrx(String trxName){
		//Creo la transacción
		return Trx.get(trxName, true);
	}
	
	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre contenido en la variable de instancia o una nueva
	 */
	protected Trx getTrx(String trxName){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(trxName, false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx(trxName);
		}
		
		return trx;
	}

	/**
	 * Eliminar las entradas anteriores en la tabla temporal parámetro. Elimina
	 * los registros de la misma instancia del proceso y los registros de una
	 * semana anterior para atrás.
	 * 
	 * @param temporalTableName
	 *            nombre de la tabla temporal
	 * @param pInstanceID
	 *            id de instancia del proceso
	 * @param trxName
	 *            nombre de la transacción
	 */
	protected void deleteOldRecords(String temporalTableName, Integer pInstanceID, String trxName){
		deleteOldRecords(temporalTableName, "Created", pInstanceID, trxName);
	}

	/**
	 * Eliminar las entradas anteriores en la tabla temporal parámetro. Elimina
	 * los registros de la misma instancia del proceso y los registros de una
	 * semana anterior para atrás, teniendo en cuenta el valor de la columna de
	 * fecha parámetro.
	 * 
	 * @param temporalTableName
	 *            nombre de la tabla temporal
	 * @param dateColumnName
	 *            nombre de la columna de fecha para la obtención de los
	 *            registros de una semana hacia atrás
	 * @param pInstanceID
	 *            id de instancia del proceso
	 * @param trxName
	 *            nombre de la transacción
	 */
	protected void deleteOldRecords(String temporalTableName, String dateColumnName, Integer pInstanceID, String trxName){
		// Eliminación de registros creados hace una semana
		DB
				.executeUpdate(
						"DELETE FROM "
								+ temporalTableName
								+ " WHERE "+dateColumnName+" < ('now'::text)::timestamp(6) - interval '7 days'",
						trxName);		
		// Eliminación de registros de esta instancia
		if(!Util.isEmpty(pInstanceID, true)){
			DB.executeUpdate("DELETE FROM " + temporalTableName
					+ " WHERE AD_PInstance_ID = " + pInstanceID, trxName);
		}
	}
	
}    // SvrProcess



/*
 *  @(#)SvrProcess.java   25.03.06
 * 
 *  Fin del fichero SvrProcess.java
 *  
 *  Versión 2.2
 *
 */
