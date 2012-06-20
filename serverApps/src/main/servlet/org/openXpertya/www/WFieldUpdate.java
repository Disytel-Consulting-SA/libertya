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

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.script;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Login;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebSessionCtx;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFieldUpdate extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WFieldUpdate.class );

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
            throw new ServletException( "WFieldUpdate.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void destroy() {}    // destroy

    /** Descripción de Campos */

    private static final String FORM_NAME = "fieldUpdate";

    //

    /** Descripción de Campos */

    private static final String FIELD_FORM = "formName";

    /** Descripción de Campos */

    private static final String FIELD_NAME = "fieldName";

    /** Descripción de Campos */

    private static final String FIELD_VALUE = "fieldValue";

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
        doPost( request,response );
    }    // doPost

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

        // Get Session Info

        WebSessionCtx wsc = WebSessionCtx.get( request );
        WWindowStatus ws  = WWindowStatus.get( request );

        if( (wsc == null) || (ws == null) ) {    // ws can be null for Login
            ;
        }

        // Get Parameter

        String formName   = WebUtil.getParameter( request,FIELD_FORM );
        String fieldName  = WebUtil.getParameter( request,FIELD_NAME );
        String fieldValue = WebUtil.getParameter( request,FIELD_VALUE );

        log.info( "doPost - Form=" + formName + " - Field=" + fieldName + " - Value=" + fieldValue );

        // Document

        WebDoc doc = createPage( wsc,ws,formName,fieldName,fieldValue );

        // The Form

        form fu = new form( request.getRequestURI());

        fu.setName( FORM_NAME );
        fu.addElement( new input( input.TYPE_HIDDEN,FIELD_FORM,"y" ));
        fu.addElement( new input( input.TYPE_HIDDEN,FIELD_NAME,"y" ));
        fu.addElement( new input( input.TYPE_HIDDEN,FIELD_VALUE,"y" ));
        doc.getBody().addElement( fu );

        // log.trace(log.l1_User, "WFieldUpdate=" + doc.toString());

        // Answer

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param wsc
     * @param ws
     * @param formName
     * @param fieldName
     * @param fieldValue
     *
     * @return
     */

    private static WebDoc createPage( WebSessionCtx wsc,WWindowStatus ws,String formName,String fieldName,String fieldValue ) {
        WebDoc doc  = WebDoc.create( true );    // plain
        body   body = doc.getBody();

        // Info

        StringBuffer sb = new StringBuffer( "FieldUpdate - " ).append( FIELD_FORM ).append( "=" ).append( formName ).append( ", " ).append( FIELD_NAME ).append( "=" ).append( fieldName ).append( ", " ).append( FIELD_VALUE ).append( "=" ).append( fieldValue );

        body.addElement( new p().addElement( sb.toString()));

        // Called manually - do nothing

        if( (formName == null) || (fieldName == null) ) {
            ;

            //

        } else if( formName.equals( "Login2" ) && fieldName.equals( WLogin.P_ROLE )) {
            reply_Login2_Role( body,wsc,formName,fieldValue );

            //

        } else if( formName.equals( "Login2" ) && fieldName.equals( WLogin.P_CLIENT )) {
            reply_Login2_Client( body,wsc,formName,fieldValue );

            //

        } else if( formName.equals( "Login2" ) && fieldName.equals( WLogin.P_ORG )) {
            reply_Login2_Org( body,wsc,ws,formName,fieldValue );
        }

        //

        return doc;
    }    // getReply

    /**
     * Descripción de Método
     *
     *
     * @param body
     * @param wsc
     * @param formName
     * @param fieldValue
     */

    private static void reply_Login2_Role( body body,WebSessionCtx wsc,String formName,String fieldValue ) {

        // Formname

        String form = "top." + WebEnv.TARGET_WINDOW + ".document.forms." + formName + ".";
        Login login = new Login( wsc.ctx );

        // Get Data

        KeyNamePair[] clients = login.getClients( new KeyNamePair( Integer.parseInt( fieldValue ),fieldValue ));

        // Set Client ----

        StringBuffer script = new StringBuffer();

        // var A=top.WWindow.document.formName.selectName.options;

        script.append( "var A=" ).append( form ).append( WLogin.P_CLIENT ).append( ".options; " );

        // A.length=0;                         //  resets options

        script.append( "A.length=0; " );

        // A[0]=new Option('text','value');    //  add new oprtion

        for( int i = 0;i < clients.length;i++ ) {
            KeyNamePair p = clients[ i ];

            script.append( "A[" ).append( i ).append( "]=new Option('" );
            script.append( p.getName());    // text
            script.append( "','" );
            script.append( p.getKey());     // value
            script.append( "'); " );
        }

        script.append( "\n" );

        // Set Organization ----

        if( clients.length > 0 ) {

            // var A=top.WWindow.document.formName.selectName.options;

            script.append( "var B=" ).append( form ).append( WLogin.P_ORG ).append( ".options; " );

            // A.length=0;                         //  resets options

            script.append( "B.length=0; " );

            // A[0]=new Option('text','value');    //  add new oprtion

            KeyNamePair[] orgs = login.getOrgs( clients[ 0 ] );

            for( int i = 0;i < orgs.length;i++ ) {
                KeyNamePair p = orgs[ i ];

                script.append( "B[" ).append( i ).append( "]=new Option('" );
                script.append( p.getName());    // text
                script.append( "','" );
                script.append( p.getKey());     // value
                script.append( "'); " );
            }

            script.append( "\n" );

            // Set Warehouse ----

            if( orgs.length > 0 ) {

                // var A=top.WWindow.document.formName.selectName.options;

                script.append( "var C=" ).append( form ).append( WLogin.P_WAREHOUSE ).append( ".options; " );

                // A.length=0;                         //  resets options

                script.append( "C.length=0; " );

                // A[0]=new Option('text','value');    //  add new oprtion

                KeyNamePair[] whs = login.getWarehouses( orgs[ 0 ] );

                if( whs != null ) {
                    for( int i = 0;i < whs.length;i++ ) {
                        KeyNamePair p = whs[ i ];

                        script.append( "C[" ).append( i ).append( "]=new Option('" );
                        script.append( p.getName());    // text
                        script.append( "','" );
                        script.append( p.getKey());     // value
                        script.append( "'); " );
                    }
                }
            }                                           // we have a org
        }                                               // we have a client

        // add script

        body.addElement( new p().addElement( WLogin.P_CLIENT + "=" ));
        body.addElement( new script( script.toString()));

        // log.trace(log.l6_Database, "reply_Login2_Role - Script=" + script.toString());

    }    // reply_Login2_Role

    /**
     * Descripción de Método
     *
     *
     * @param body
     * @param wsc
     * @param formName
     * @param fieldValue
     */

    private static void reply_Login2_Client( body body,WebSessionCtx wsc,String formName,String fieldValue ) {

        // Formname

        String form = "top." + WebEnv.TARGET_WINDOW + ".document." + formName + ".";
        StringBuffer script = new StringBuffer();

        // Set Organization ----

        // var A=top.WWindow.document.formName.selectName.options;

        script.append( "var B=" ).append( form ).append( WLogin.P_ORG ).append( ".options; " );

        // A.length=0;                         //  resets options

        script.append( "B.length=0; " );

        // A[0]=new Option('text','value');    //  add new oprtion

        KeyNamePair client = new KeyNamePair( Integer.parseInt( fieldValue ),fieldValue );
        Login         login = new Login( wsc.ctx );
        KeyNamePair[] orgs  = login.getOrgs( client );

        for( int i = 0;i < orgs.length;i++ ) {
            KeyNamePair p = orgs[ i ];

            script.append( "B[" ).append( i ).append( "]=new Option('" );
            script.append( p.getName());    // text
            script.append( "','" );
            script.append( p.getKey());     // value
            script.append( "'); " );
        }

        script.append( "\n" );

        // Set Warehouse ----

        // var A=top.WWindow.document.formName.selectName.options;

        script.append( "var C=" ).append( form ).append( WLogin.P_WAREHOUSE ).append( ".options; " );

        // A.length=0;                         //  resets options

        script.append( "C.length=0; " );

        // A[0]=new Option('text','value');    //  add new oprtion

        KeyNamePair[] whs = login.getWarehouses( orgs[ 0 ] );

        if( whs != null ) {
            for( int i = 0;i < whs.length;i++ ) {
                KeyNamePair p = whs[ i ];

                script.append( "C[" ).append( i ).append( "]=new Option('" );
                script.append( p.getName());    // text
                script.append( "','" );
                script.append( p.getKey());     // value
                script.append( "'); " );
            }
        }

        // add script

        body.addElement( new p().addElement( WLogin.P_WAREHOUSE + "=" ));
        body.addElement( new script( script.toString()));

        // log.trace(log.l6_Database, "Login2-Client - Script=" + script.toString());

    }    // reply_Login2_Client

    /**
     * Descripción de Método
     *
     *
     * @param body
     * @param wsc
     * @param ws
     * @param formName
     * @param fieldValue
     */

    private static void reply_Login2_Org( body body,WebSessionCtx wsc,WWindowStatus ws,String formName,String fieldValue ) {

        // Formname

        String form = "top." + WebEnv.TARGET_WINDOW + ".document." + formName + ".";
        StringBuffer script = new StringBuffer();

        // Set Warehouse ----

        // var A=top.WWindow.document.formName.selectName.options;

        script.append( "var C=" ).append( form ).append( WLogin.P_WAREHOUSE ).append( ".options; " );

        // A.length=0;                         //  resets options

        script.append( "C.length=0; " );

        // A[0]=new Option('text','value');    //  add new oprtion

        KeyNamePair org = new KeyNamePair( Integer.parseInt( fieldValue ),fieldValue );
        Login  login = new Login( wsc.ctx );
        CallResult result = login.validateLogin( org );

        if( result != null && result.isError() ) {
            log.severe( result.getMsg() );    // todo graceful dead
            ws.mWindow = null;
            wsc.ctx    = new Properties();

            return;
        }

        KeyNamePair[] whs = login.getWarehouses( org );

        if( whs != null ) {
            for( int i = 0;i < whs.length;i++ ) {
                KeyNamePair p = whs[ i ];

                script.append( "C[" ).append( i ).append( "]=new Option('" );
                script.append( p.getName());    // text
                script.append( "','" );
                script.append( p.getKey());     // value
                script.append( "'); " );
            }
        }

        // add script

        body.addElement( new p().addElement( WLogin.P_WAREHOUSE + "=" ));
        body.addElement( new script( script.toString()));

        // log.trace(log.l6_Database, "Login2-Org - Script=" + script.toString());

    }    // reply_Login2_Org
}    // WFieldUpdate



/*
 *  @(#)WFieldUpdate.java   23.03.06
 * 
 *  Fin del fichero WFieldUpdate.java
 *  
 *  Versión 2.2
 *
 */
