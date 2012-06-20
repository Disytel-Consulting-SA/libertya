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



package org.openXpertya.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MCountry;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProcessPara;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MScheduler;
import org.openXpertya.model.MSchedulerLog;
import org.openXpertya.model.MSchedulerPara;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Login;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Scheduler extends ServidorOXP {
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     */

    public Scheduler( MScheduler model ) {
        super( model,240 );    // nap
        setM_model(model);
        this.setContext(new Properties());
        this.setContext(this.getContext(),"#AD_Session_ID",Env.getContextAsInt(this.getM_model().getCtx(),"#AD_Session_ID" ));
        this.initializeDataContext();
        //Get parameters of process in the scheduler
        this.setParams(MSchedulerPara.getOfScheduler(this.getContext(), this.getM_model().getID(), this.getM_model().get_TrxName()));
    }    // Scheduler

    /** Descripción de Campos */

    private MScheduler m_model = null;
    
    /** Parametros del scheduler */

    private List<MSchedulerPara> params; 
    
    /** Descripción de Campos */

    private StringBuffer m_summary = new StringBuffer();

    /** Descripción de Campos */

    private MClient m_client = null;
    
    /** Organización con el cual fue creado el scheduler */
    
    private MOrg m_org = null;
    
    /** Contexto para poder actuar con los procesos del sistema */
    
    private Properties context;
    
    /** Log */
    
    private CLogger log = CLogger.getCLogger( Scheduler.class );

    
    /**
     * Inicializa el contexto a utilizar para los procesos
     */
    protected void initializeDataContext(){
    	// Client
    	m_client = MClient.get( this.getContext(),this.getM_model().getAD_Client_ID());
        this.setContext(this.getContext(), "#AD_Client_ID", m_client.getID());
        this.setContext( this.getContext(),"#AD_Client_Name",m_client.getName());
        
        // Organization
        m_org = MOrg.get(this.getContext(),this.getM_model().getAD_Org_Login_ID());
        this.setContext(this.getContext(), "#AD_Org_ID", m_org.getID());
        this.setContext(this.getContext(), "#AD_Org_Name", m_org.getName());
        
        // Role
        MRole role = MRole.get(this.getContext(),this.getM_model().getAD_Role_ID(),null);
        this.setContext( this.getContext(),"#AD_Role_ID",this.getM_model().getAD_Role_ID());
        this.setContext( this.getContext(),"#AD_Role_Name",role.getName());
                
        // User
        this.setContext( this.getContext(),"#AD_User_Name",100 );
        this.setContext( this.getContext(),"#AD_User_ID",100);
        this.setContext( this.getContext(),"#SalesRep_ID",100);
                       
        // User Level        
        String sql = "SELECT DISTINCT r.UserLevel, c.AD_Client_ID,c.Name "                                                                                    // 2/3
            		+ "FROM AD_Role r " 
            		+ "INNER JOIN AD_Client c " 
            		+ "ON (r.AD_Client_ID=c.AD_Client_ID) "
            		+ "WHERE r.AD_Role_ID=?"    // #1
            			+ " AND r.IsActive='Y' AND c.IsActive='Y'";
        
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
        	ps = DB.prepareStatement(sql);
        	ps.setInt(1, role.getAD_Role_ID());
        	rs = ps.executeQuery();
        	if(rs.next()){
        		this.setContext( this.getContext(),"#User_Level",rs.getString( 1 ));
        	}
        } catch(Exception e){
        	log.severe("No se pudo setear el user level en el contexto");
        	m_summary.append("No se pudo setear el user level en el contexto \n");
        	e.printStackTrace();
        }
        
        // Warehouse
        MOrgInfo orgInfo = m_org.getInfo();
        //int m_warehouse_id;
        if(orgInfo.getM_Warehouse_ID() != 0){
        	this.setContext(this.getContext(), "#M_Warehouse_ID", orgInfo.getM_Warehouse_ID());        	
        }
        
        // Date
        this.setContext(this.getContext(),"#Date",new Timestamp(System.currentTimeMillis()));
        
        // Acct
        this.setContext(this.getContext(),"#ShowAcct","Y");
        this.setContext( this.getContext(),"#YYYY","Y" );
        this.setContext( this.getContext(),"#StdPrecision",2 );
        try{
        	Login.getSchemaInfo(this.getContext(), m_client.getID(), role.getAD_Role_ID());
        } catch(Exception e){
        	log.severe("No se pudo setear el esquema contable en el contexto");
        	m_summary.append("No se pudo setear el esquema contable en el contexto \n");
        	e.printStackTrace();
        }
        
        // Country
        this.setContext( this.getContext(),"#C_Country_ID",MCountry.getDefault( this.getContext() ).getC_Country_ID());
        
        // Language
        if(m_client.getAD_Language() != null){
        	this.setContext(this.getContext(),"#AD_Language",m_client.getAD_Language());
        }
        else{
        	this.setContext(this.getContext(),"#AD_Language",Language.getBaseLanguage().getName());
        }
    }    
    
    
    /**
     * Descripción de Método
     *
     */

    protected void doWork() {
        // Arm the Map for the parameters
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	MProcessPara procPara;
        for(MSchedulerPara para : this.getParams()){
        	procPara = new MProcessPara(this.getContext(),para.getAD_Process_Para_ID(),null);
        	parameters.put(procPara.getColumnName(), para.getParameterDefault());
        }
        
        // Ejecuto el proceso
        MProcess.execute(this.getContext(),this.getM_model().getAD_Process_ID(), parameters);
        
        int no = getM_model().deleteLog();

        getM_summary().append( "Logs deleted=" ).append( no );

        //

        MSchedulerLog pLog = new MSchedulerLog( getM_model(),getM_summary().toString());

        pLog.setReference( "#" + String.valueOf( p_runCount ) + " - " + TimeUtil.formatElapsed( new Timestamp( p_startWork )));
        pLog.save();
    }    // doWork

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerInfo() {
        return "#" + p_runCount + " - Last=" + getM_summary().toString();
    }    // getServerInfo

	protected void setM_summary(StringBuffer m_summary) {
		this.m_summary = m_summary;
	}

	protected StringBuffer getM_summary() {
		return m_summary;
	}

		
	
	public void setContext(Properties ctx, String context, String value){
		Env.setContext(ctx, context, value);
	}
	
	public void setContext(Properties ctx, String context, Integer value){
		Env.setContext(ctx, context, value);
	}
	
	public void setContext(Properties ctx, String context, Timestamp value){
		Env.setContext(ctx, context, value);
	}
	
	protected void setM_model(MScheduler m_model) {
		this.m_model = m_model;
	}

	protected MScheduler getM_model() {
		return m_model;
	}

	protected void setParams(List<MSchedulerPara> params) {
		this.params = params;
	}

	protected List<MSchedulerPara> getParams() {
		return params;
	}

	protected void setContext(Properties context) {
		this.context = context;
	}

	protected Properties getContext() {
		return context;
	}
}    // Scheduler



/*
 *  @(#)Scheduler.java   24.03.06
 * 
 *  Fin del fichero Scheduler.java
 *  
 *  Versión 2.2
 *
 */
