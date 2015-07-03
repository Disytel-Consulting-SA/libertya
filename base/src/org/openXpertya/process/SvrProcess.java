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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Process;
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

    /** Nómina de procesos en ejecución definidos con limitacion de acceso concurrente por cliente LY */
    protected static HashSet<Integer> processCurrentlyExecuting = new HashSet<Integer>();
    
    /**
     * Validación de ejecución concurrente según la configuración del proceso 
     * en AD_Process,bajo el campo ConcurrentExecution
     * @return true si puede ejecutar correctamente, o false en cc
     */
    protected synchronized boolean checkConcurrentAccess(Properties ctx, ProcessInfo pi) {
    	X_AD_Process aProcess = new X_AD_Process(ctx, pi.getAD_Process_ID(), get_TrxName());
    	
    	// Si no hay restricciones, todo ok
    	if (X_AD_Process.CONCURRENTEXECUTION_NoRestrictions.equals(aProcess.getConcurrentExecution()))
    		return true;

    	// Restricción por cliente Libertya
    	if (X_AD_Process.CONCURRENTEXECUTION_Client.equals(aProcess.getConcurrentExecution())) {
    		if (processCurrentlyExecuting.contains(pi.getAD_Process_ID()))
    			return false;
    		processCurrentlyExecuting.add(pi.getAD_Process_ID());
    		return true;
    	}
    	
    	// Restricciones global    	
    	if (X_AD_Process.CONCURRENTEXECUTION_Global.equals(aProcess.getConcurrentExecution())) {
    		
    		Connection		conn	= null;
    		PreparedStatement	pstmt	= null;
    		ResultSet rs = null;
    		try {
    			// Lockear entrada para el proceso en cuestión.  
    			// Importante: Utilizamos el registro de AD_Process_Trl es_AR porque sabemos que no es referenciado al ejecutar el proceso
    			// (a diferencia de AD_Process que sí es referenciado, y conlleva eventuales bloqueos).  Adicionalmente, sabemos que la entrada existe siempre.
    			// Esto evita la problemática de tener una tabla independiente de validación de accesos a procesos en la cual sería necesario o bien
    			// insertar un nuevo registro cada vez que se genera un nuevo proceso (a fin de garantizar su existencia pero con riesgos de bloqueos al querer 
    			// insertar y que ya exista), o bien tener una tabla en la que ante cualquier inserción en ad_process se genere su correspondiente entrada
    			// de validación de acceso concurrente.  Esto último es lo que se hace con AD_Process_Trl cada vez que se crea un nuevo proceso, con lo cual
    			// no es realmente necesario crear una tabla con información redundante para la finalidad del caso.  Usamos es_AR aunque pdría ser alguna otra, es lo mismo.
    			String selectSQL = "SELECT * FROM AD_Process_Trl WHERE AD_Language = 'es_AR' AND AD_Process_ID = " + aProcess.getAD_Process_ID() + " FOR UPDATE NOWAIT";
	    		Trx	trx	= Trx.get(get_TrxName(), false);
	            conn	= (trx != null ? trx.getConnection() : DB.getConnectionID());
	    		pstmt	= conn.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
	            rs = pstmt.executeQuery();
	            return true;
    		}
    		catch (Exception e) {
    			log.log( Level.SEVERE, e.getMessage(), e.getCause());
    			return false;	
    		} finally {
    			try {
	    			if (pstmt!=null)
	    				pstmt.close();
	    			if (rs!=null)
	    				rs.close();
	    			pstmt=null;
	    			rs=null;
	    		} 
    			catch (Exception e) {
    				log.log( Level.SEVERE, e.getMessage(), e.getCause());
	    		}
    		}
    		
    		
    		// return 0 == DB.getSQLValue(null, "SELECT count(1) FROM AD_PInstance WHERE IsProcessing='Y' AND AD_PInstance_ID=" + pi.getAD_PInstance_ID());
    	}
    	
    	return true;
    	
    }
    
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

    	// Validación de acceso concurrente
    	if (!checkConcurrentAccess(ctx, pi)) {
    		pi.setSummary("Error de acceso concurrente.  Ya existe una ejecución activa del proceso.  Puede revisar la configuración de acceso concurrente en la definición de Informe/Proceso.", true);
    		return !pi.isError();
    	}
        
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
        // La utilidad semántica de este lock quedó deprecada.  Ver: checkConcurrentAccess().
        // Se deja solo por eventual referencia de otras funcionalidades existentes no consideradas.
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
        
        // Acceso concurrente a ejecución de proceso.  Ejecución única por instancia: Quitar la entrada de ejecución del proceso (ya finalizó) 
        processCurrentlyExecuting.remove(m_pi.getAD_Process_ID());
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
