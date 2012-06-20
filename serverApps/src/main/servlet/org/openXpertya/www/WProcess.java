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



package org.openXpertya.www;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.i;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProcessPara;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebSessionCtx;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WProcess extends HttpServlet {

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );

        if( !WebEnv.initWeb( config )) {
            throw new ServletException( "WProcess.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {

        // Get Session attributes

        WebSessionCtx wsc = WebSessionCtx.get( request );

        if( wsc == null ) {
            WebUtil.createTimeoutPage( request,response,this,null );

            return;
        }

        WebDoc doc = null;

        // Get Parameter: Menu_ID

        int AD_Menu_ID = WebUtil.getParameterAsInt( request,"AD_Menu_ID" );

        if( AD_Menu_ID > 0 ) {
            log.info( "doGet - AD_Menu_ID=" + AD_Menu_ID );
            doc = createParameterPage( wsc,AD_Menu_ID );
        } else {
            // String fileName        = WebUtil.getParameter( request,"File" );
            int    AD_PInstance_ID = WebUtil.getParameterAsInt( request,"AD_PInstance_ID" );
        	String fileName = getReportPath(AD_PInstance_ID);

            log.info( "doGet - AD_PInstance_ID=" + AD_PInstance_ID + ", File=" + fileName );

            String error = streamResult( request,response,AD_PInstance_ID,fileName );

            if( error == null ) {
                return;
            }

            doc = WebDoc.createWindow( error );
        }

        if( doc == null ) {
            doc = WebDoc.createWindow( "Process Not Found" );
        }

        //

        WebUtil.createResponse( request,response,this,null,doc,true );
    }    // doGet

    public static String getReportPath(int AD_PInstance_ID) {
    	String path = System.getProperty("java.io.tmpdir");
    	
    	if (!path.endsWith(File.separator))
    		path += File.separator;
    	
    	return path + "WProcess" + AD_PInstance_ID + ".pdf";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {

        // Get Session attributes

        WebSessionCtx wsc = WebSessionCtx.get( request );

        if( wsc == null ) {
            WebUtil.createTimeoutPage( request,response,this,null );

            return;
        }

        int AD_Process_ID = WebUtil.getParameterAsInt( request,"AD_Process_ID" );

        log.info( "doGet - AD_Process_ID=" + AD_Process_ID );

        if( AD_Process_ID == 0 ) {
            WebUtil.createErrorPage( request,response,this,"No Process" );

            return;
        }

        WebDoc doc = createProcessPage( request,AD_Process_ID );

        //

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param wsc
     * @param AD_Menu_ID
     *
     * @return
     */

    private WebDoc createParameterPage( WebSessionCtx wsc,int AD_Menu_ID ) {
        MProcess process = MProcess.getFromMenu( wsc.ctx,AD_Menu_ID );

        // need to check if Role can access

        if( process == null ) {
            WebDoc doc = WebDoc.createWindow( "Process Not Found" );

            return doc;
        }

        WebDoc doc    = WebDoc.createWindow( process.getName());
        td     center = doc.addWindowCenter( false );

        if( process.getDescription() != null ) {
            center.addElement( new p( new i( process.getDescription())));
        }

        if( process.getHelp() != null ) {
            center.addElement( new p( process.getHelp(),AlignType.LEFT ));
        }

        //

        form myForm = new form( "WProcess" ).setName( "process" + process.getAD_Process_ID());

        myForm.setOnSubmit( "this.Submit.disabled=true;return true;" );
        myForm.addElement( new input( input.TYPE_HIDDEN,"AD_Process_ID",process.getAD_Process_ID()));

        table myTable = new table( "0","0","5","100%",null );

        myTable.setID( "WProcessParameter" );

        MProcessPara[] parameter = process.getParameters();

        for( int i = 0;i < parameter.length;i++ ) {
            MProcessPara para = parameter[ i ];

            //

            WebField wField = new WebField( wsc,para.getColumnName(),para.getName(),para.getDescription(),

            // no display length

            para.getAD_Reference_ID(),para.getFieldLength(),para.getFieldLength(),false,

            // not r/o, ., not error, not dependent

            false,para.isMandatory(),false,false,false );
            td toField = para.isRange()
                         ?wField.getField( para.getLookup(),Env.parseContext(wsc.ctx, 0, para.getDefaultValue2(), false, true))
                         :new td( WebEnv.NBSP );

            // Add to Table

            myTable.addElement( new tr().addElement( wField.getLabel()).addElement( wField.getField( para.getLookup(),Env.parseContext(wsc.ctx, 0, para.getDefaultValue(), false, true))).addElement( toField ));
        }

        // Submit

        myTable.addElement( new tr().addElement( new td( null,AlignType.LEFT,AlignType.MIDDLE,false,new input( input.TYPE_RESET,"Reset","Reset" ))).addElement( new td( null,AlignType.LEFT,AlignType.MIDDLE,false,null )).addElement( new td( null,AlignType.RIGHT,AlignType.MIDDLE,false,new input( input.TYPE_SUBMIT,"Submit","Submit" ))));
        myForm.addElement( myTable );
        center.addElement( myForm );

        return doc;
    }    // createParameterPage

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param AD_Process_ID
     *
     * @return
     */

    private WebDoc createProcessPage( HttpServletRequest request,int AD_Process_ID ) {
        WebSessionCtx wsc     = WebSessionCtx.get( request );
        MProcess      process = MProcess.get( wsc.ctx,AD_Process_ID );

        // need to check if Role can access

        if( process == null ) {
            WebDoc doc = WebDoc.createWindow( "Process Not Found" );

            return doc;
        }

        WebDoc doc    = WebDoc.createWindow( process.getName());
        td     center = doc.addWindowCenter( false );

        if( process.getDescription() != null ) {
            center.addElement( new p( new i( process.getDescription())));
        }

        if( process.getHelp() != null ) {
            center.addElement( new p( process.getHelp(),AlignType.LEFT ));
        }

        // Create Process Instance

        MPInstance pInstance = fillParameter( request,process );

        //

        ProcessInfo pi = new ProcessInfo( process.getName(),process.getAD_Process_ID());

        pi.setAD_User_ID( Env.getAD_User_ID( wsc.ctx ));
        pi.setAD_Client_ID( Env.getAD_Client_ID( wsc.ctx ));
        pi.setAD_PInstance_ID( pInstance.getAD_PInstance_ID());

        // Info

        p p = new p();

        p.addElement( Msg.translate( wsc.ctx,"AD_PInstance_ID" ) + ": " + pInstance.getAD_PInstance_ID());
        center.addElement( p );

        // Start

        boolean processOK = true;

        if( process.isJavaProcess()) {
            Trx trx = Trx.get( Trx.createTrxName( "WebPrc" ),true );

            try {
                processOK = process.processIt( pi,trx );
                trx.commit();
                trx.close();
            } catch( Throwable t ) {
                trx.rollback();
                trx.close();
            }

            if( !processOK || pi.isError()) {
                center.addElement( new p( "Error:" + pi.getSummary(),AlignType.LEFT ).setClass( "Cerror" ));
                processOK = false;
            }

            center.addElement( new p().addElement( pi.getSummary()));
            center.addElement( pi.getLogInfo( true ));
        }

        // Report

        if( processOK && process.isReport()) {
            ReportEngine re = ReportEngine.get( wsc.ctx,pi );

            if( re == null ) {
                center.addElement( new p( "Could not start ReportEngine",AlignType.LEFT ).setClass( "Cerror" ));
            } else {
                try {
                    // File    file = File.createTempFile( "WProcess",".pdf" );
                	File file = new File(getReportPath(pInstance.getAD_PInstance_ID()));
                    boolean ok   = re.createPDF( file );

                    if( ok ) {
                        String url = "WProcess?AD_PInstance_ID=" + pInstance.getAD_PInstance_ID() /* + "&File=" + URLEncoder.encode( file.getAbsolutePath(),WebEnv.ENCODING ) */ ;
                        a link = new a( url,null,a.TARGET_BLANK,process.getName());

                        center.addElement( new p().addElement( "Report created: " ).addElement( link ));

                        // Marker that Process is OK

                        wsc.ctx.put( "AD_PInstance_ID=" + pInstance.getAD_PInstance_ID(),"ok" );
                    } else {
                        center.addElement( new p( "Could not create Report",AlignType.LEFT ).setClass( "Cerror" ));
                    }
                } catch( Exception e ) {
                    center.addElement( new p( "Could not create Report:",AlignType.LEFT ).setClass( "Cerror" ));
                    center.addElement( e.toString());
                }
            }
        }

        return doc;
    }    // createProcessPage

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param process
     *
     * @return
     */

    private MPInstance fillParameter( HttpServletRequest request,MProcess process ) {
        MPInstance pInstance = new MPInstance( process,0 );

        //

        MPInstancePara[] iParams = pInstance.getParameters();

        for( int pi = 0;pi < iParams.length;pi++ ) {
            MPInstancePara iPara = iParams[ pi ];
            String         key   = iPara.getParameterName();
            MProcessPara   pPara = process.getParameter( key );
            
            if( pPara == null ) {
                log.log( Level.SEVERE,"fillParameter - Parameter not found: " + key );

                continue;
            }

            String valueString = WebUtil.getParameter( request,key );

            if (!pPara.isMandatory() && valueString != null && valueString.equals("-1")) {
            	valueString = null;
            }
            
            log.fine( "fillParameter - " + key + " = " + valueString );

            Object value = valueString;

            if( (valueString != null) && (valueString.length() == 0) ) {
                value = null;
            }

            // No Value

            if( value == null ) {

                // if (pPara.isMandatory())
                // log.log(Level.WARNING,"fillParameter - " + key
                // + " - empty - mandatory!");

            } else {

                // Convert to Type

                try {
                    if( DisplayType.isNumeric( pPara.getAD_Reference_ID()) || DisplayType.isID( pPara.getAD_Reference_ID())) {
                        BigDecimal bd = null;

                        if( value instanceof BigDecimal ) {
                            bd = ( BigDecimal )value;
                        } else if( value instanceof Integer ) {
                            bd = new BigDecimal((( Integer )value ).intValue());
                        } else {
                            bd = WebUtil.parseBD(valueString);
                        }

                        iPara.setP_Number( bd );
                        log.fine( "fillParameter - " + key + " = " + valueString + " (=" + bd + "=)" );
                    } else if( DisplayType.isDate( pPara.getAD_Reference_ID())) {
                        Timestamp ts = null;
                        
                        if( value instanceof Timestamp ) {
                            ts = ( Timestamp )value;
                        } else {
                            ts = WebUtil.parseDate(valueString, null);
                        }

                        iPara.setP_Date( ts );
                        log.fine( "fillParameter - " + key + " = " + valueString + " (=" + ts + "=)" );
                    } else {
                        iPara.setP_String( value.toString());
                    }

                    //

                } catch( Exception e ) {
                    log.warning( "fillParameter - " + key + " = " + valueString + " (" + value + ") " + value.getClass().getName() + " - " + e.getLocalizedMessage());
                }
            }    // not null
            
            if (pPara.isRange()) {
                valueString = WebUtil.getParameter( request,key,1 );

                if (!pPara.isMandatory() && valueString != null && valueString.equals("-1")) {
                	valueString = null;
                }
                
                value = valueString;

                if( (valueString != null) && (valueString.length() == 0) ) {
                    value = null;
                }

                // No Value

                if( value == null ) {

                    // if (pPara.isMandatory())
                    // log.log(Level.WARNING,"fillParameter - " + key
                    // + " - empty - mandatory!");

                } else {

                    // Convert to Type

                    try {
                        if( DisplayType.isNumeric( pPara.getAD_Reference_ID()) || DisplayType.isID( pPara.getAD_Reference_ID())) {
                            BigDecimal bd = null;

                            if( value instanceof BigDecimal ) {
                                bd = ( BigDecimal )value;
                            } else if( value instanceof Integer ) {
                                bd = new BigDecimal((( Integer )value ).intValue());
                            } else {
                                bd = WebUtil.parseBD(valueString);
                            }

                            iPara.setP_Number_To( bd );
                            log.fine( "fillParameter - " + key + " = " + valueString + " (=" + bd + "=)" );
                        } else if( DisplayType.isDate( pPara.getAD_Reference_ID())) {
                            Timestamp ts = null;
                            
                            if( value instanceof Timestamp ) {
                                ts = ( Timestamp )value;
                            } else {
                                ts = WebUtil.parseDate(valueString, null);
                            }

                            iPara.setP_Date_To( ts );
                            log.fine( "fillParameter - " + key + " = " + valueString + " (=" + ts + "=)" );
                        } else {
                            iPara.setP_String_To( value.toString());
                        }

                        //

                    } catch( Exception e ) {
                        log.warning( "fillParameter - " + key + " = " + valueString + " (" + value + ") " + value.getClass().getName() + " - " + e.getLocalizedMessage());
                    }
                }    // not null
            }

            iPara.save();
            
        }        // instance parameter loop

        return pInstance;
    }    // fillParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param AD_PInstance_ID
     * @param fileName
     *
     * @return
     */

    private String streamResult( HttpServletRequest request,HttpServletResponse response,int AD_PInstance_ID,String fileName ) {
        if( AD_PInstance_ID == 0 ) {
            return "Your process not found";
        }

        WebSessionCtx wsc   = WebSessionCtx.get( request );
        Object        value = wsc.ctx.get( "AD_PInstance_ID=" + AD_PInstance_ID );

        if( (value == null) ||!value.equals( "ok" )) {
            return "Process Instance not found";
        }

        //

        if( (fileName == null) || (fileName.length() == 0) ) {
            return "No Process Result";
        }

        File file = new File( fileName );

        if( !file.exists()) {
            return "Process Result not found: " + file;
        }

        // OK

        return WebUtil.streamFile( response,file );
    }    // streamResult
}    // WProcess



/*
 *  @(#)WProcess.java   12.10.07
 * 
 *  Fin del fichero WProcess.java
 *  
 *  Versión 2.2
 *
 */
