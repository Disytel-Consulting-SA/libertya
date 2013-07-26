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



package org.openXpertya.apps;

import java.awt.Container;
import java.io.InvalidClassException;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.IProcessParameter;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.common.PluginProcessUtils;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.process.DocActionStatusEvent;
import org.openXpertya.process.DocActionStatusListener;
import org.openXpertya.process.ProcessCall;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfo.JasperReportDTO;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
import org.openXpertya.wf.MWFProcess;
import org.openXpertya.wf.MWorkflow;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProcessCtl extends Thread {

	public static final String DYNAMIC_JASPER_CLASSNAME = "org.openXpertya.JasperReport.ReportStarter";
    /**
     * Descripción de Método
     *
     *
     * @param parent
     * @param WindowNo
     * @param pi
     * @param trx
     *
     * @return
     */

    public static ProcessCtl process( ASyncProcess parent,int WindowNo,ProcessInfo pi,Trx trx ) {
        log.fine( "En processCtl process con WindowNo=" + WindowNo + " - " + pi );

        // Get Instance

        String trxName = null;

        if( trx != null ) {
            trxName = trx.getTrxName();
        }

        MPInstance instance = new MPInstance( Env.getCtx(),pi.getAD_Process_ID(),pi.getRecord_ID(),trxName );

        if( !instance.save()) {
            pi.setSummary( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));
            pi.setError( true );

            return null;
        }

        pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

        // Get Parameters (Dialog)

        ProcessParameter para = new ProcessParameter( Env.getFrame(( Container )parent ),WindowNo,pi );

        if( para.initDialog()) {
            para.setVisible(true);

            if( !para.isOK()) {
                pi.setSummary( Msg.getMsg( Env.getCtx(),"ProcessCancelled" ));
                pi.setError( true );

                return null;
            }
        }

        // execute

        ProcessCtl worker = new ProcessCtl( parent,pi,trx );

        worker.start();    // MUST be start!

        return worker;
    }    // execute

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param pi
     * @param trx
     */

    public ProcessCtl( ASyncProcess parent,ProcessInfo pi,Trx trx ) {
        m_parent = parent;
        m_pi     = pi;
        m_trx    = trx;    // handeled correctly
    }                      // ProcessCtl

    /** Descripción de Campos */

    private ASyncProcess m_parent;

    /** Descripción de Campos */

    private ProcessInfo m_pi;

    /** Descripción de Campos */

    private Trx m_trx;

    /** Descripción de Campos */

    private Waiting m_waiting;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ProcessCtl.class );

    /**
     * Descripción de Método
     *
     */

    public void run() {
        log.fine( "En processCtl.Run conAD_PInstance_ID=" + m_pi.getAD_PInstance_ID() + ", Record_ID=" + m_pi.getRecord_ID());

        // Lock

        lock();

        // try {System.out.println(">> sleeping ..");sleep(20000);System.out.println(".. sleeping <<");} catch (Exception e) {}

        // Get Process Information: Name, Procedure Name, ClassName, IsReport, IsDirectPrint

        String  ProcedureName    = "";
        int     AD_ReportView_ID = 0;
        int     AD_Workflow_ID   = 0;
        int     AD_PrintFormat_ID= 0;  
        boolean IsReport         = false;
        boolean IsDirectPrint    = false;

        //

        String SQL = "SELECT p.Name, p.ProcedureName,p.ClassName, p.AD_Process_ID,"    // 1..4
                     + " p.isReport,p.IsDirectPrint,p.AD_ReportView_ID,p.AD_Workflow_ID,"    // 5..8
                     + " CASE WHEN p.Statistic_Count=0 THEN 0 ELSE p.Statistic_Seconds/p.Statistic_Count END, dynamicreport, JasperReport "    // 9
                     + "FROM AD_Process p, AD_PInstance i " + "WHERE p.AD_Process_ID=i.AD_Process_ID AND p.IsActive='Y'" + " AND i.AD_PInstance_ID=?";

        if( !Env.isBaseLanguage( Env.getCtx(),"AD_Process" )) {
            SQL = "SELECT t.Name, p.ProcedureName,p.ClassName, p.AD_Process_ID,"    // 1..4
                  + " p.isReport, p.IsDirectPrint,p.AD_ReportView_ID,p.AD_Workflow_ID,"    // 5..8
                  + " CASE WHEN p.Statistic_Count=0 THEN 0 ELSE p.Statistic_Seconds/p.Statistic_Count END, p.AD_PrintFormat_ID, dynamicreport, JasperReport " + "FROM AD_Process p, AD_Process_Trl t, AD_PInstance i " + "WHERE p.AD_Process_ID=i.AD_Process_ID" + " AND p.AD_Process_ID=t.AD_Process_ID AND p.IsActive='Y'" + " AND i.AD_PInstance_ID=?" + " AND t.AD_Language='" + Env.getAD_Language( Env.getCtx()) + "'";
        }

        //

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY, m_trx!=null?m_trx.getTrxName():null );

            pstmt.setInt( 1,m_pi.getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_pi.setTitle( rs.getString( 1 ));

                if( m_waiting != null ) {
                    m_waiting.setTitle( m_pi.getTitle());
                }

                ProcedureName = rs.getString( 2 );
                m_pi.setClassName( rs.getString( 3 ));
                
                // Si tiene definido un jasper dinamico, entonces usar la clase encargada para ésto
                if (rs.getString("dynamicreport") != null && "Y".equalsIgnoreCase(rs.getString("dynamicreport")) &&
                	rs.getString("jasperreport") != null && rs.getString("jasperreport").trim().length() > 0) 
                	m_pi.setClassName( DYNAMIC_JASPER_CLASSNAME );
                else{
	                /**
	                 * Logica para plugins, verificar si existe una clase que redefina el proceso original 
	                 */
	                String pluginProcessClassName = PluginProcessUtils.findPluginProcessClass(rs.getString(3));
	                if (pluginProcessClassName != null)
	                	m_pi.setClassName(pluginProcessClassName);
                }
                
                m_pi.setAD_Process_ID( rs.getInt( 4 ));

                // Report

                if( "Y".equals( rs.getString( 5 ))) {
                    IsReport = true;

                    if( "Y".equals( rs.getString( 6 )) &&!Ini.getPropertyBool( Ini.P_PRINTPREVIEW )) {
                        IsDirectPrint = true;
                    }
                }

                AD_ReportView_ID = rs.getInt( 7 );
                AD_Workflow_ID   = rs.getInt( 8 );
                AD_PrintFormat_ID = rs.getInt("AD_PrintFormat_ID");
                //

                int estimate = rs.getInt( 9 );

                if( estimate != 0 ) {
                    m_pi.setEstSeconds( estimate + 1 );    // admin overhead

                    if( m_waiting != null ) {
                        m_waiting.setTimerEstimate( m_pi.getEstSeconds());
                    }
                }
            } else {
                log.log( Level.SEVERE,"No AD_PInstance_ID=" + m_pi.getAD_PInstance_ID());
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            m_pi.setSummary( Msg.getMsg( Env.getCtx(),"ProcessNoProcedure" ) + " " + e.getLocalizedMessage(),true );
            unlock();
            log.log( Level.SEVERE,"run",e );

            return;
        }
        
        // -- Disytel
        // Se agrega el listener de DocAction Events a la información del proceso.
        m_pi.setDocActionStatusListener(docActionStatusListener);
        // --
        
        // No PL/SQL Procedure

        if( ProcedureName == null ) {
            ProcedureName = "";
        }

        if( AD_Workflow_ID > 0 ) {
            startWorkflow( AD_Workflow_ID );
            unlock();

            return;
        }

        if( m_pi.getClassName() != null ) {

            // Run Class

            if( !startProcess()) {
                unlock();

                return;
            }

            // No Optional SQL procedure ... done

            if( !IsReport && (ProcedureName.length() == 0) ) {
                unlock();

                return;
            }

            // No Optional Report ... done

            if( IsReport && (AD_ReportView_ID == 0) && (AD_PrintFormat_ID == 0)) {
                unlock();

                return;
            }
        }

        // If not a report, we need a prodedure name

        if( !IsReport && (ProcedureName.length() == 0) ) {
            m_pi.setSummary( Msg.getMsg( Env.getCtx(),"ProcessNoProcedure" ),true );
            unlock();

            return;
        }

        if( IsReport ) {

            // Optional Pre-Report Process

            if( ProcedureName.length() > 0 ) {
                if( !startDBProcess( ProcedureName )) {
                    unlock();

                    return;
                }
            }    // Pre-Report

            // Start Report    -----------------------------------------------

            boolean ok = ReportCtl.start( m_pi,IsDirectPrint );

            m_pi.setSummary( "Report",!ok );
            unlock();
        } else {
            if( !startDBProcess( ProcedureName )) {
                unlock();

                return;
            }

            // Success - getResult

            ProcessInfoUtil.setSummaryFromDB( m_pi );
            unlock();
        }    // *** Process submission ***

        // log.fine(Log.l3_Util, "ProcessCtl.run - done");

    }    // run

    /**
     * Inicialización de datos adicionales al ProcessInfo
     */
    private void initilizeProcessInfoAditionals(){
		if (!Util.isEmpty(m_pi.getTable_ID(), true)
				&& m_pi.getRecord_ID() > 0) {
			Integer docTypeID = null;
			String documentNo = null;
			M_Table table = M_Table.get(Env.getCtx(), m_pi.getTable_ID());
			PO po = table.getPO(m_pi.getRecord_ID(), table.get_TrxName());
			// Si no se obtiene un PO, no resta nada por hacer
			if (po == null)
				return;
			// Verifico si en la tabla actual existe la columna C_DocTypeTarget_ID o
			// C_DocType_ID, si es así entonces obtengo su valor
			String docTypeColumnName = "C_DocTypeTarget_ID";
			int docTypeIndex = po.get_ColumnIndex(docTypeColumnName);
			// Si no existe C_DocTypeTarget_ID, busco C_DocType_ID
			if(docTypeIndex == -1){
				docTypeColumnName = "C_DocType_ID";
				docTypeIndex = po.get_ColumnIndex(docTypeColumnName);
			}
			// Si tengo la columna con el tipo de documento, entonces obtengo su valor
			if(docTypeIndex > -1){
				docTypeID = (Integer)po.get_Value(docTypeIndex);
			}
			// Verifico si en la tabla actual existe la columna DocumentNo, si es
			// asi obtengo su valor
			int documentNoIndex = po.get_ColumnIndex("DocumentNo");
			if(documentNoIndex > -1){
				documentNo = (String)po.get_Value(documentNoIndex);
			}
	    	// Si tengo el tipo de documento, entonces guardo el jasperDTO en el Process Info
			if(!Util.isEmpty(docTypeID, true)){
		    	JasperReportDTO jasperDTO = m_pi.new JasperReportDTO();
		    	jasperDTO.setDocTypeID(docTypeID);
		    	jasperDTO.setDocumentNo(documentNo);
		    	m_pi.setJasperReportDTO(jasperDTO);
			}
    	}
    }
    
    /**
     * Descripción de Método
     *
     */

    private void lock() {

        // log.info("...");
		//m_parent is null for synchrous execution
		if (m_parent != null)
		{
			if (m_parent instanceof Container)
			{
				//swing client
				JFrame frame = Env.getFrame((Container)m_parent);
				if (frame instanceof AWindow)
					((AWindow)frame).setBusyTimer(m_pi.getEstSeconds());
				else
					m_waiting = new Waiting (frame, Msg.getMsg(Env.getCtx(), "Processing"), false, m_pi.getEstSeconds());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						log.finer("lock");
						m_parent.lockUI(m_pi);
					}
				});
				if (m_waiting != null)
				{
					m_waiting.toFront();
					m_waiting.setVisible(true);
				}
			}
			else
			{
				//other client
				log.finer("lock");
				m_parent.lockUI(m_pi);
			}
		}
    }    // lock

    /**
     * Descripción de Método
     *
     */

    private void unlock() {

        // log.info("...");
//		if (m_pi.isBatch())
//			m_pi.setIsTimeout(true);
		if (m_parent != null)
		{
			if (m_parent instanceof Container)
			{
				//swing client
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String summary = m_pi.getSummary();
						log.finer("unlock - " + summary);
						if (summary != null && summary.indexOf('@') != -1)
							m_pi.setSummary(Msg.parseTranslation(Env.getCtx(), summary));
						m_parent.unlockUI(m_pi);
					}
				});
				//	Remove Waiting/Processing Indicator
				if (m_waiting != null)
					m_waiting.dispose();
				m_waiting = null;
			}
			else
			{
				//other client
				m_parent.unlockUI(m_pi);
			}
		}
    }    // unlock

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workflow_ID
     *
     * @return
     */

    private boolean startWorkflow( int AD_Workflow_ID ) {
        log.fine( AD_Workflow_ID + " - " + m_pi );

        boolean started = false;

        if( DB.isRemoteProcess() && !m_pi.isAlwaysInClient()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    m_pi = server.workflow( Env.getCtx(),m_pi,AD_Workflow_ID );
                    log.finest( "server => " + m_pi );
                    started = true;
                }
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"AppsServer error",ex );
                started = false;
            }
        }

        // Run locally

        if( !started ) {
            MWorkflow  wf        = MWorkflow.get( Env.getCtx(),AD_Workflow_ID );
            MWFProcess wfProcess = wf.startWait( m_pi );    // may return null

            started = wfProcess != null;
        }

        return started;
    }    // startWorkflow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean startProcess() {
        log.fine( m_pi.toString());
        
		// Inicializar datos del proceso para documentos e impresiones jasper en
		// el process info
        initilizeProcessInfoAditionals();
        
        boolean started = false;

        if( DB.isRemoteProcess() && !m_pi.isAlwaysInClient()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {    // See ServerBean
                    m_pi = server.process( Env.getCtx(),m_pi );
                    log.finest( "server => " + m_pi );
                    started = true;
                }
            } catch( UndeclaredThrowableException ex ) {
                Throwable cause = ex.getCause();

                if( cause != null ) {
                    if( cause instanceof InvalidClassException ) {
                        log.log( Level.SEVERE,"Version Server <> Client: " + cause.toString() + " - " + m_pi,ex );
                    } else {
                        log.log( Level.SEVERE,"AppsServer error(1b): " + cause.toString() + " - " + m_pi,ex );
                    }
                } else {
                    log.log( Level.SEVERE," AppsServer error(1) - " + m_pi,ex );
                }

                started = false;
            } catch( RemoteException ex ) {
                Throwable cause = ex.getCause();

                if( cause == null ) {
                    cause = ex;
                }

                log.log( Level.SEVERE,"AppsServer error - " + m_pi,cause );
                started = false;
            }
        }
        
        // Run locally

        if( !started ) {
            ProcessCall myObject = null;
            boolean error = false;
            // Manejo de transacciones.
            // Si se invocó el proceso con una transacción creada, entonces no se administra
            // esta transacción internamente derivando la ejecución de commit o rollback al
            // cliente que invoca este proceso.
            // Si no se asignó una transacción externa, se crea una transacción interna y se
            // administra en este método a fin de que la ejecución de un proceso sea transaccional
            // (en caso de que las subclases de SvrProcess utilicen el get_TrxName() )
            Trx trx = null;
            boolean ownedTrx = (m_trx == null);
            if (m_trx == null) {
            	trx = Trx.get(Trx.createTrxName("ProcessCtl.startProcess"), true);
            	trx.start();
            } else
            	trx = m_trx;

            try {
                Class myClass = Class.forName( m_pi.getClassName());

                myObject = ( ProcessCall )myClass.newInstance();

                if( myObject == null ) {
                    m_pi.setSummary( "No Instance for " + m_pi.getClassName(),true );
                } else {
                    error = !myObject.startProcess( Env.getCtx(),m_pi,trx );
                }
                // Solo se hace commit o rollback de la transacción si es una trx
                // controlada localmente.
                if(ownedTrx && trx != null ) {
                    if (error)
                    	trx.rollback();
                    else
                    	trx.commit();
                    
                    trx.close();
                }
            } catch( Exception e ) {
                if( trx != null ) {
                    trx.rollback();
                    trx.close();
                }

                m_pi.setSummary( "Error starting Class " + m_pi.getClassName(),true );
                log.log( Level.SEVERE,m_pi.getClassName(),e );
            }
        }

        return !m_pi.isError();
    }    // startProcess

    /**
     * Descripción de Método
     *
     *
     * @param ProcedureName
     *
     * @return
     */

    private boolean startDBProcess( String ProcedureName ) {

        // execute on this thread/connection

        log.fine( ProcedureName + "(" + m_pi.getAD_PInstance_ID() + ")" );

        String sql = "{call " + ProcedureName + "(?)}";

        try {
            CallableStatement cstmt = DB.prepareCall( sql );    // ro??

            cstmt.setInt( 1,m_pi.getAD_PInstance_ID());
            cstmt.executeUpdate();
            cstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
            m_pi.setSummary( Msg.getMsg( Env.getCtx(),"ProcessRunError" ) + " " + e.getLocalizedMessage());
            m_pi.setError( true );

            return false;
        }

        // log.fine(Log.l4_Data, "ProcessCtl.startProcess - done");

        return true;
    }    // startDBProcess
    
    private DocActionStatusListener docActionStatusListener = new DocActionStatusListener() {

		public void docActionStatusChanged(DocActionStatusEvent event) {
			// Evento: Impresión fiscal de documento. 
			if(event.getDocActionStatus() == DocActionStatusEvent.ST_FISCAL_PRINT_DOCUMENT) {
		    	PO document = (PO)event.getSource();
		    	// Se obtiene el nombre del tipo de documento.
		    	Integer docTypeID = (Integer)document.get_Value("C_DocTypeTarget_ID");
		    	String docTypeName = "";
		    	if(docTypeID != null && docTypeID > 0) {
		    		MDocType docType = MDocType.get(Env.getCtx(), docTypeID);
		    		docTypeName = " - " + docType.getPrintName();
		    	}
				// Se crea la ventana que muestra el estado de la impresora fiscal.
		    	final AInfoFiscalPrinter infoFiscalPrinter = 
		    		new AInfoFiscalPrinter(
		    				null,
		    				m_pi.getWindowNo(),
		    				Msg.parseTranslation(Env.getCtx(),"@PrintingFiscalDocument@" + docTypeName)		
		    		);
		    	FiscalDocumentPrint fdp = (FiscalDocumentPrint)event.getParameter(0);
		    	// Se setea  la ventana de información tanto como listener de estado de la 
		    	// impresora como el estado de la impresión del documento.
		    	fdp.addDocumentPrintListener(infoFiscalPrinter);
		    	fdp.setPrinterEventListener(infoFiscalPrinter);
		    	// Se efectúa la referencia cruzada entre el Impresor y la
		    	// ventana de información.
		    	infoFiscalPrinter.setFiscalDocumentPrint(fdp);
		    	// Se muestra la ventana en el thread de Swing.
		    	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						infoFiscalPrinter.setVisible(true);
					}
		    	});
			}
			
		}
    	
    };
    
    /**
	 *	Async Process - Do it all.
	 *  <code>
	 *	- Get Instance ID
	 *	- Get Parameters
	 *	- execute (lock - start process - unlock)
	 *  </code>
	 *  Creates a ProcessCtl instance, which calls
	 *  lockUI and unlockUI if parent is a ASyncProcess
	 *  <br>
	 *	Called from ProcessDialog.actionPerformed
	 *
	 *  @param parent ASyncProcess & Container
	 *  @param WindowNo window no
	 *  @param paraPanel Process Parameter Panel
	 *  @param pi ProcessInfo process info
	 *  @param trx Transaction
	 *  @return worker started ProcessCtl instance or null for workflow
	 */
	public static ProcessCtl process(ASyncProcess parent, int WindowNo, IProcessParameter parameter, ProcessInfo pi, Trx trx)
	{
		log.fine("WindowNo=" + WindowNo + " - " + pi);

		MPInstance instance = null; 
		try 
		{ 
			instance = new MPInstance(Env.getCtx(), pi.getAD_Process_ID(), pi.getRecord_ID()); 
		} 
		catch (Exception e) 
		{ 
			pi.setSummary (e.getLocalizedMessage()); 
			pi.setError (true); 
			log.warning(pi.toString()); 
			return null; 
		} 
		catch (Error e) 
		{ 
			pi.setSummary (e.getLocalizedMessage()); 
			pi.setError (true); 
			log.warning(pi.toString()); 
			return null; 
		}
		if (!instance.save())
		{
			pi.setSummary (Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
			pi.setError (true);
			return null;
		}
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());
		
		//	Get Parameters
		if (parameter != null) {
			if (!parameter.saveParameters())
			{
				pi.setSummary (Msg.getMsg(Env.getCtx(), "ProcessCancelled"));
				pi.setError (true);
				return null;
			}
		}

		//	execute
		ProcessCtl worker = new ProcessCtl(parent, WindowNo, pi, trx);
		if (parent != null)
		{
			worker.start();
		}
		else
		{
			//synchrous
			worker.run();
		}
		return worker;
	}	//	execute

	/**************************************************************************
	 *  Constructor
	 *  @param parent Container & ASyncProcess
	 *  @param pi Process info
	 *  @param trx Transaction
	 *  Created in process(), VInvoiceGen.generateInvoices
	 */
	public ProcessCtl (ASyncProcess parent, int WindowNo, ProcessInfo pi, Trx trx)
	{
		windowno = WindowNo;
		m_parent = parent;
		m_pi = pi;
		m_trx = trx;	//	handeled correctly
	}   //  ProcessCtl
	/** Windowno */
	int windowno;

	
}    // ProcessCtl



/*
 *  @(#)ProcessCtl.java   02.07.07
 * 
 *  Fin del fichero ProcessCtl.java
 *  
 *  Versión 2.2
 *
 */
