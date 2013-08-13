/*
 * @(#)MProcess.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.openXpertya.process.ProcessCall;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
//~--- Importaciones JDK ------------------------------------------------------

/**
 *  Process Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MProcess.java,v 1.17 2005/05/28 21:18:03 jjanke Exp $
 */
public class MProcess extends X_AD_Process {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Process", 20);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MProcess.class);

    /** Parameters */
    private MProcessPara[]	m_parameters	= null;

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Process_ID process
     * @param trxName
     */
    public MProcess(Properties ctx, int AD_Process_ID, String trxName) {

        super(ctx, AD_Process_ID, trxName);

        if (AD_Process_ID == 0) {

            // setValue (null);
            // setName (null);
            setIsReport(false);
            setAccessLevel(ACCESSLEVEL_All);
            setEntityType(ENTITYTYPE_UserMaintained);
            setIsBetaFunctionality(false);
        }

    }		// MProcess

    /**
     *      Load Contsructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MProcess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MProcess

    /**
     *      Update Statistics
     *      @param seconds sec
     */
    public void addStatistics(int seconds) {

        setStatistic_Count(getStatistic_Count() + 1);
        setStatistic_Seconds(getStatistic_Seconds() + seconds);

    }		// addStatistics

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord) {

            int			AD_Role_ID	= Env.getAD_Role_ID(getCtx());
            MProcessAccess	pa		= new MProcessAccess(this, AD_Role_ID);

            pa.save();
        }

        // Menu/Workflow
        else if (is_ValueChanged("IsActive") || is_ValueChanged("Name") || is_ValueChanged("Description") || is_ValueChanged("Help")) {

            MMenu[]	menues	= MMenu.get(getCtx(), "AD_Process_ID=" + getAD_Process_ID());

            for (int i = 0; i < menues.length; i++) {

                menues[i].setIsActive(isActive());
                menues[i].setName(getName());
                menues[i].setDescription(getDescription());
                menues[i].save();
            }

            X_AD_WF_Node[]	nodes	= M_Window.getWFNodes(getCtx(), "AD_Process_ID=" + getAD_Process_ID());

            for (int i = 0; i < nodes.length; i++) {

                boolean	changed	= false;

                if (nodes[i].isActive() != isActive()) {

                    nodes[i].setIsActive(isActive());
                    changed	= true;
                }

                if (nodes[i].isCentrallyMaintained()) {

                    nodes[i].setName(getName());
                    nodes[i].setDescription(getDescription());
                    nodes[i].setHelp(getHelp());
                    changed	= true;
                }

                if (changed) {
                    nodes[i].save();
                }
            }
        }

        return success;

    }		// afterSave

    /**
     *      Process w/o parameter
     *      @param Record_ID record
     * @param trx
     *      @return Process Instance
     */
    public MPInstance processIt(int Record_ID, Trx trx) {

        MPInstance	pInstance	= new MPInstance(this, Record_ID);

        // Lock
        pInstance.setIsProcessing(true);
        pInstance.save();

        boolean	ok	= true;

        // PL/SQL Procedure
        String	ProcedureName	= getProcedureName();

        if ((ProcedureName != null) && (ProcedureName.length() > 0)) {
            ok	= startProcess(ProcedureName, pInstance);
        }

        // Unlock
        pInstance.setResult(ok
                            ? MPInstance.RESULT_OK
                            : MPInstance.RESULT_ERROR);
        pInstance.setIsProcessing(false);
        pInstance.save();

        //
        pInstance.log();

        return pInstance;

    }		// process

    /**
     *      Process It (sync)
     *
     * @param pi
     * @param trx
     *      @return Process Instance
     */
    public boolean processIt(ProcessInfo pi, Trx trx) {

        if (pi.getAD_PInstance_ID() == 0) {

            MPInstance	pInstance	= new MPInstance(this, pi.getRecord_ID());

            // Lock
            pInstance.setIsProcessing(true);
            pInstance.save();
        }

        boolean	ok	= false;

        // Java Class
        String	Classname	= getClassname();

        if ((Classname != null) && (Classname.length() > 0)) {
            ok	= startClass(Classname, pi, trx);
        } else {
            log.severe("No Classname");
        }

        return ok;

    }		// process

    /**
     *      Process It (sync)
     *
     * @param pi
     * @param trx
     *      @return Process Instance
     */
    public boolean processIt(Properties ctx,ProcessInfo pi, Trx trx) {

        if (pi.getAD_PInstance_ID() == 0) {

            MPInstance	pInstance	= new MPInstance(this, pi.getRecord_ID());

            // Lock
            pInstance.setIsProcessing(true);
            pInstance.save();
        }

        boolean	ok	= false;

        // Java Class
        String	Classname	= getClassname();

        if ((Classname != null) && (Classname.length() > 0)) {
            ok	= startClass(ctx,Classname, pi, trx);
        } else {
            log.severe("No Classname");
        }

        return ok;

    }		// process
    
    /**
     *  Start Java Class (sync).
     *      instanciate the class implementing the interface ProcessCall.
     *  The class can be a Server/Client class (when in Package
     *  org compiere.process or org.openXpertya.model) or a client only class
     *  (e.g. in org.openXpertya.report)
     *
     *  @param Classname    name of the class to call
     *  @param pi   process info
     * @param trx
     *  @return     true if success
     *  @see org.openXpertya.model.ProcessCall
     *      see ProcessCtl.startClass
     */
    private boolean startClass(String Classname, ProcessInfo pi, Trx trx) {

    	return this.startClass(getCtx(), Classname, pi, trx);
    	
        /*log.info(Classname + "(" + pi + ")");

        boolean		retValue	= false;
        ProcessCall	myObject	= null;

        try {

            Class	myClass	= Class.forName(Classname);

            myObject	= (ProcessCall) myClass.newInstance();

            if (myObject == null) {
                retValue	= false;
            } else {
                retValue	= myObject.startProcess(getCtx(), pi, trx);
            }

        } catch (Exception e) {

            pi.setSummary("Error Start Class " + Classname, true);
            log.log(Level.SEVERE, Classname, e);

            throw new RuntimeException(e);
        }

        return true;*/

    }		// startClass
    
    
    /**
     *  Start Java Class (sync).
     *      instanciate the class implementing the interface ProcessCall.
     *  The class can be a Server/Client class (when in Package
     *  org compiere.process or org.openXpertya.model) or a client only class
     *  (e.g. in org.openXpertya.report)
     *
     *  @param Classname    name of the class to call
     *  @param pi   process info
     * @param trx
     * @param ctx
     *  @return     true if success
     *  @see org.openXpertya.model.ProcessCall
     *      see ProcessCtl.startClass
     */
    private boolean startClass(Properties ctx,String Classname, ProcessInfo pi, Trx trx) {    	
    	
        log.info(Classname + "(" + pi + ")");

        boolean		retValue	= false;
        ProcessCall	myObject	= null;

        try {

            Class	myClass	= Class.forName(Classname);

            myObject	= (ProcessCall) myClass.newInstance();

            if (myObject == null) {
                retValue	= false;
            } else {
                retValue	= myObject.startProcess(ctx, pi, trx);
            }

        } catch (Exception e) {

            pi.setSummary("Error Start Class " + Classname, true);
            log.log(Level.SEVERE, Classname, e);

            throw new RuntimeException(e);
        }

        return true;

    }		// startClass

    /**
     *  Start Database Process
     *  @param ProcedureName PL/SQL procedure name
     *  @param pInstance process instance
     *      see ProcessCtl.startProcess
     *  @return true if success
     */
    private boolean startProcess(String ProcedureName, MPInstance pInstance) {

        int	AD_PInstance_ID	= pInstance.getAD_PInstance_ID();

        // execute on this thread/connection
        log.info("startProcess - " + ProcedureName + "(" + AD_PInstance_ID + ")");

        String	sql	= "{call " + ProcedureName + "(?)}";

        try {

            CallableStatement	cstmt	= DB.prepareCall(sql);		// ro??

            cstmt.setInt(1, AD_PInstance_ID);
            cstmt.executeUpdate();
            cstmt.close();

        } catch (Exception e) {

            log.log(Level.SEVERE, sql, e);
            pInstance.setResult(MPInstance.RESULT_ERROR);
            pInstance.setErrorMsg(e.getLocalizedMessage());

            return false;
        }

        pInstance.setResult(MPInstance.RESULT_OK);

        return true;

    }		// startProcess

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MProcess[").append(getID()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     * Compatibilidad para métodos que no utilizan transaccion
     */
    public static MProcess get(Properties ctx, int AD_Process_ID) {
    	return get(ctx, AD_Process_ID, null);
    	
    }
    
    /**
     *      Get MProcess from Cache
     *      @param ctx context
     *      @param AD_Process_ID id
     *      @return MProcess
     */
    public static MProcess get(Properties ctx, int AD_Process_ID, String trxName) {

        Integer		key		= new Integer(AD_Process_ID);
        MProcess	retValue	= (MProcess) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MProcess(ctx, AD_Process_ID, trxName);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get MProcess from Menu
     *      @param ctx context
     *      @param AD_Menu_ID id
     *      @return MProcess or null
     */
    public static MProcess getFromMenu(Properties ctx, int AD_Menu_ID) {

        MProcess	retValue	= null;
        String		sql		= "SELECT * FROM AD_Process p " + "WHERE EXISTS (SELECT * FROM AD_Menu m " + "WHERE m.AD_Process_ID=p.AD_Process_ID AND m.AD_Menu_ID=?)";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Menu_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                retValue	= new MProcess(ctx, rs, null);

                // Save in cache
                Integer	key	= new Integer(retValue.getAD_Process_ID());

                s_cache.put(key, retValue);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getFromMenu", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// getFromMenu

    /**
     *      Get Parameter with ColumnName
     *      @param name column name
     *      @return parameter or null
     */
    public MProcessPara getParameter(String name) {

        getParameters();

        for (int i = 0; i < m_parameters.length; i++) {

            if (m_parameters[i].getColumnName().equals(name)) {
                return m_parameters[i];
            }
        }

        return null;

    }		// getParameter

    /**
     *      Get Parameters
     *      @return parameters
     */
    public MProcessPara[] getParameters() {

        if (m_parameters != null) {
            return m_parameters;
        }

        ArrayList	list	= new ArrayList();

        //
        String	sql	= "SELECT * FROM AD_Process_Para WHERE AD_Process_ID=? ORDER BY SeqNo";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getAD_Process_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MProcessPara(getCtx(), rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getParameter", e);
        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }

        //
        m_parameters	= new MProcessPara[list.size()];
        list.toArray(m_parameters);

        return m_parameters;

    }		// getParameters

    /**
     *      Is this a Java Process
     *      @return true if java process
     */
    public boolean isJavaProcess() {

        String	Classname	= getClassname();

        return ((Classname != null) && (Classname.length() > 0));

    }		// is JavaProcess

    /**
     *      Is it a Workflow
     *      @return true if Workflow
     */
    public boolean isWorkflow() {
        return getAD_Workflow_ID() > 0;
    }		// isWorkflow
    
	/**
	 * Crea y retorna una transacción
	 * @return una nueva transacción con el nombre pasado como parametro
	 */
	
	private Trx createTrx(String trxName){
		//Creo la transacción
		return Trx.get(trxName, true);
	}
	
	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre pasado como parametro
	 */
	private Trx getTrx(String trxName){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(trxName, false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx(trxName);
		}
		
		return trx;
	}
    
    
	/*
     * --------------------------------------------
     * 				Execute a process
     * --------------------------------------------
     */
    
    
	/**
     * Executa el proceso pasado como parametro sin tener en cuenta los parametros. 
     * Antes de realizar la llamada a este metodo, se deberían cargar los valores de los parametros. 
     * @param process proceso a ejecutar
     * @param pi info del proceso
     */
    public static void execute(MProcess process, ProcessInfo pi){
    	// Creo un nombre para la transaccion
    	String trxName = process.getName()+"_"+System.currentTimeMillis()+"_"+Thread.currentThread().getId();
    	// Proceso el proceso y lo ejecuto
    	process.processIt(pi, process.getTrx(trxName));
    }
    
    
	/**
     * Executa el proceso pasado como parametro sin tener en cuenta los parametros. 
     * Antes de realizar la llamada a este metodo, se deberían cargar los valores de los parametros. 
     * @param process proceso a ejecutar
     * @param pi info del proceso
     * @param ctx context
     */
    public static void execute(Properties ctx,MProcess process, ProcessInfo pi){
    	execute(ctx,process,pi,null);
    }
    
    
    /**
     * Executa el proceso pasado como parametro sin tener en cuenta los parametros. 
     * Antes de realizar la llamada a este metodo, se deberían cargar los valores de los parametros. 
     * @param process proceso a ejecutar
     * @param pi info del proceso
     * @param ctx context
     */
    public static void execute(Properties ctx,MProcess process, ProcessInfo pi, String trxName){
    	boolean create = false;
    	if(trxName == null){
    		// Creo un nombre para la transaccion
        	trxName = process.getName()+"_"+System.currentTimeMillis()+"_"+Thread.currentThread().getId();
        	create = true;
    	}    	
    	// Proceso el proceso y lo ejecuto
    	process.processIt(ctx,pi, process.getTrx(trxName));
    	// Si vino una transacción es porque el que la trajo se encarga de commitear o rollback
    	if(create){
	     	// Commitear o rollbackear segun estado resultante
	    	if (!pi.isError()){
	    		process.getTrx(trxName).commit();
	    	}
	    	else{
	    		process.getTrx(trxName).rollback();
	    	}
	    	process.getTrx(trxName).close();
    	}
    }
	
	
    /**
     * Executa el proceso sin tener en cuenta los parametros. 
     * Antes de realizar la llamada a este metodo, se deberían cargar los valores de los parametros. 
     * @param AD_Process_ID id del proceso
     * @param ctx context
     */
    public static ProcessInfo execute(Properties ctx,int AD_Process_ID){
    	// Obtengo el proceso con ese id
    	MProcess process = MProcess.get(ctx, AD_Process_ID);
    	if(process == null){
    		s_log.severe("No existe proceso con ese id");
    		return null;
    	}
    	MPInstance instance = new MPInstance(ctx,process.getID(),0,null);
    	instance.setIsProcessing(true);
    	if(!instance.save()){
    		s_log.severe("Error al crear ad_pinstance");
    		return null;
    	}
    	// Creo el process info de este proceso
    	ProcessInfo pi = new ProcessInfo(process.getName(),process.getID());
    	pi.setAD_PInstance_ID(instance.getID());
    	
    	// Ejecuto el proceso
    	MProcess.execute(ctx,process,pi);
    	return pi;
    }
    
    /**
     * Ejecuta un proceso a partir del process id y de un conjunto de parametros con la forma
     * Map<String,Object> donde String es el nombre de la columna del parametro y Object es el 
     * valor a setear en dicho parametro. Cabe destacar que para los tipos rango, al nombre de
     * columna se le debe concatenar el string "_to" (case insensitive), o sea, por ejemplo si la columna se llama
     * "Date_Trx" y es rango, entonces el string que debe venir dentro del Map debe ser "Date_Trx_To".
     * Este metodo se puede llamar con una Map vacia y solo ejecutara el proceso, aunque si desea
     * realizar la ejecucion del proceso sin tener en cuenta los parametro use los otros metodos 
     * execute() definidos dentro de la clase.
     * @param AD_Process_ID id del proceso a ejecutar
     * @param parameters parametros del proceso
     * @param ctx context
     */
    public static ProcessInfo execute(Properties ctx,int AD_Process_ID, Map<String, Object> parameters){
    	return execute(ctx, AD_Process_ID, parameters, null);
    }
    
    
    public static ProcessInfo execute(Properties ctx,int AD_Process_ID, Map<String, Object> parameters, String trxName){
    	// Obtengo el proceso con ese id
    	MProcess process = MProcess.get(ctx, AD_Process_ID, trxName);
    	if(process == null){
    		s_log.severe("No existe proceso con ese id");
    		return null;
    	}
    	MPInstance instance = new MPInstance(ctx,process.getID(),0,trxName);
    	if(!instance.save()){
    		s_log.severe("Error al crear ad_pinstance");
    		return null;
    	}
    	// Creo el process info de este proceso
    	ProcessInfo pi = new ProcessInfo(process.getName(),process.getID());
    	pi.setAD_PInstance_ID(instance.getID());
    	
    	// Procesamiento de parametros
    	
    	Set<String> params = parameters.keySet();
    	MProcessPara procPara;
    	MPInstancePara instancePara = null;
    	for (String para : params) {
    		instancePara = null;
    		procPara = process.getParameter(para);
    		boolean isTO = para.toUpperCase().endsWith("_TO");
    		Object value = parameters.get(para);
    		
    		// Si el parámetro no existe, verifico si existe el parámetro TO.
    		if (procPara == null && isTO) {
   				procPara = process.getParameter(para.substring(0, para.toUpperCase().lastIndexOf("_TO")));
    		}
    		// Si existe el parametro tengo que crear su instancia para esta instancia especifica 
    		// del proceso. Siempre y cuando el valor para asignarle no sea null, ya que si es
    		// null, los parametros no se insertan dentro de sus instancias
    		if (procPara != null) {
    			// Si el parámetro no tiene valor asignado, se obtiene el valor por defecto.
    			if (value == null) 
    				value = isTO ? procPara.getDefaultValue2() : procPara.getDefaultValue();
        		// Si el valor a asignar no es null, entonces creo la instancia y le asigno el valor    			
    			if (value != null) {
    				instancePara = MPInstancePara.get(ctx, instance.getID(), procPara.getSeqNo(), trxName);
    				if(instancePara == null){
    					instancePara = new MPInstancePara(ctx,instance.getID(),procPara.getSeqNo(), trxName);
    				}    				
    				instancePara.setParameter(procPara.getAD_Reference_ID(),value,isTO);
    				instancePara.setParameterName(procPara.getColumnName());
    			}
    			// Verificar si tiene valor por defecto
    			else{
    				// Si tiene valor por defecto crear la instancia del parametro
    				if(procPara.getDefaultValue() != null){
    					instancePara = MPInstancePara.get(ctx, instance.getID(), procPara.getSeqNo(), trxName);
        				if(instancePara == null){
        					instancePara = new MPInstancePara(ctx,instance.getID(),procPara.getSeqNo(), trxName);
        				}
    					instancePara.setParameter(procPara.getAD_Reference_ID(),procPara.getDefaultValue(),isTO);
        				instancePara.setParameterName(procPara.getColumnName());
    				}
    			}
    		}
    		// Verificar si es de tipo rango, entonces habria que parsear el nombre del parametro
    		// y asignarle el valor
    		else{ 
    			if(para.toUpperCase().endsWith("_TO")){
    				procPara = process.getParameter(para.substring(0, para.toUpperCase().lastIndexOf("_TO")));
    				if(procPara.isRange()){
    					if(parameters.get(para) != null){
    						instancePara = MPInstancePara.get(ctx, instance.getID(), procPara.getSeqNo(), trxName);
    	    				if(instancePara == null){
    	    					instancePara = new MPInstancePara(ctx,instance.getID(),procPara.getSeqNo(), trxName);
    	    				}
	    					instancePara.setParameter(procPara.getAD_Reference_ID(),parameters.get(para),isTO);
	        				instancePara.setParameterName(procPara.getColumnName());
    					}
    				}
    				// Verificar si tiene valor por defecto
        			else{
        				// Si tiene valor por defecto crear la instancia del parametro
        				if(procPara.getDefaultValue2() != null){
        					instancePara = MPInstancePara.get(ctx, instance.getID(), procPara.getSeqNo(), trxName);
            				if(instancePara == null){
            					instancePara = new MPInstancePara(ctx,instance.getID(),procPara.getSeqNo(), trxName);
            				}
        					instancePara.setParameter(procPara.getAD_Reference_ID(),procPara.getDefaultValue2(),isTO);
            				instancePara.setParameterName(procPara.getColumnName());
        				}
        			}
    			}
    		}
    		
    		// Si es no es null entonces lo guardo
    		if(instancePara != null){
    			if(!instancePara.save()){
    				s_log.severe("Error al tratar de guardar la instancia del parametro del proceso. Param=" + para + ", value=" + value);
    				return null;
    			}
    		}
    	}
    	
    	MProcess.execute(ctx,process,pi,trxName);
    	return pi;
    }
    
	/**
	 * Grant independence to GenerateModel from AD_Process_ID
	 * @param value
	 * @param trxName
	 * @return
	 */
	public static int getProcess_ID(String value, String trxName)
	{
		int retValue = DB.getSQLValueEx(trxName, "SELECT AD_Process_ID FROM AD_Process WHERE Value=?", value);
		return retValue;
	}

    /**
     * Guarda un adjunto para este proceso
     * @param trxName
     * @param ctx
     * @param componentObjectUID
     * @param attachmentName
     * @param data
     * @throws Exception
     */
	public static void addAttachment(String trxName, Properties ctx, String componentObjectUID, String attachmentName, byte[] data) throws Exception {
		// Si el attachment parámetro es null no hago nada
    	if(data == null || data.length <= 0){
    		return;
    	}
    	
		// Obtener el id del proceso
		int processID = DB.getSQLValue(trxName,
				"SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = '"
						+ componentObjectUID + "'");
		// Obtener el id del adjunto del proceso
		int attachmentID = DB
				.getSQLValue(
						trxName,
						"SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?",
						Table_ID, processID);
		// Si existe lo elimino
		if(!Util.isEmpty(attachmentID, true)){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "
					+ Table_ID + " AND Record_ID = " + processID, trxName);
		}
		// Creo el adjunto y lo guardo
		MAttachment att  = new MAttachment(ctx, 0, trxName);
		att.setAD_Table_ID(Table_ID);
		att.setRecord_ID(processID);
		att.addEntry(attachmentName, data);
		if(!att.save()){
			throw new Exception ("ERROR saving attachment "+attachmentName);
		}
	}
	
}	// MProcess