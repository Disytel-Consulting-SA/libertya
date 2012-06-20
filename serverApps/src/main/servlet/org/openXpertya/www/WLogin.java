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
import java.security.Principal;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.AlignType;
import org.apache.ecs.HtmlColor;
import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.font;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.h3;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.select;
import org.apache.ecs.xhtml.strong;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.MLanguage;
import org.openXpertya.model.MSession;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Login;
import org.openXpertya.util.Msg;
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

public class WLogin extends HttpServlet {

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
            throw new ServletException( "WLogin.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Login";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
        super.destroy();
    }    // destroy

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
        log.info( "doGet" );
        doPost( request,response );
    }    // doGet

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
        log.info( "doPost" );

        // Create New Session

        HttpSession sess = request.getSession( true );

        sess.setMaxInactiveInterval( WebEnv.TIMEOUT );

        // Get Cookie Properties

        Properties cProp = WebUtil.getCookieProprties( request );

        // Create Context

        WebSessionCtx wsc = WebSessionCtx.get( request,true );

        wsc.setLanguage( request );

        // Page

        WebDoc doc = null;

        // Check DB connection

        if( !DB.isConnected()) {
            String msg = Msg.getMsg( wsc.ctx,"WLoginNoDB" );

            if( msg.equals( "WLoginNoDB" )) {
                msg = "No Database Connection";
            }

            doc = WebDoc.createWindow( msg );
        }

        // Login Info from request?

        else {

            // Get Parameters:     UserName/Password

            String usr = WebUtil.getParameter( request,P_USERNAME );
            String pwd = WebUtil.getParameter( request,P_PASSWORD );

            // Get Principle

            Principal userPrincipal = request.getUserPrincipal();

            log.info( "doPost - Principal=" + userPrincipal + "; User=" + usr );

            // Login info not from request and not pre-authorized

            if( (userPrincipal == null) && ( (usr == null) || (pwd == null) ) ) {
                doc = createFirstPage( cProp,request,"" );

                // Login info from request or authorized

            } else {
                KeyNamePair[] roles = null;
                Login         login = new Login( wsc.ctx );

                // Pre-authorized

                if( userPrincipal != null ) {
                    roles = login.getRoles( userPrincipal );
                    usr   = userPrincipal.getName();
                } else {
                    roles = login.getRoles( usr,pwd );
                }

                //

                if( roles == null ) {
                    doc = createFirstPage( cProp,request,Msg.getMsg( wsc.ctx,"UserPwdError" ));
                } else {
                    doc = createSecondPage( request,WebUtil.convertToOption( roles,null ),"" );

                    // Create OpenXpertya Session - user id in ctx

                    MSession.get( wsc.ctx,request.getRemoteAddr(),request.getRemoteHost(),sess.getId());
                }

                // Can we save Cookie ?

                if( WebUtil.getParameter( request,P_STORE ) == null ) {
                    cProp.clear();                          // erase all
                } else                                      // Save Cookie Parameter
                {
                    cProp.setProperty( P_USERNAME,usr );
                    cProp.setProperty( P_STORE,"Y" );
                    cProp.setProperty( P_PASSWORD,pwd );    // For test only
                }
            }
        }

        WebUtil.createResponse( request,response,this,cProp,doc,true );
    }    // doPost

    // Variable Names

    /** Descripción de Campos */

    public static final String P_USERNAME = "User";

    /** Descripción de Campos */

    private static final String P_PASSWORD = "Password";

    /** Descripción de Campos */

    private static final String P_SUBMIT = "Submit";

    // WMenu picks it up

    /** Descripción de Campos */

    protected static final String P_ROLE = "AD_Role_ID";

    /** Descripción de Campos */

    protected static final String P_CLIENT = "AD_Client_ID";

    /** Descripción de Campos */

    protected static final String P_ORG = "AD_Org_ID";

    /** Descripción de Campos */

    protected static final String P_DATE = "Date";

    /** Descripción de Campos */

    protected static final String P_WAREHOUSE = "M_Warehouse_ID";

    /** Descripción de Campos */

    protected static final String P_ERRORMSG = "ErrorMessage";

    /** Descripción de Campos */

    protected static final String P_STORE = "SaveCookie";

    /**
     * Descripción de Método
     *
     *
     * @param cProp
     * @param request
     * @param errorMessage
     *
     * @return
     */

    private WebDoc createFirstPage( Properties cProp,HttpServletRequest request,String errorMessage ) {
        log.info( "createFirstPage - " + errorMessage );

        String AD_Language = ( cProp.getProperty( Env.LANGUAGE,Language.getAD_Language( request.getLocale())));

        //

        String windowTitle = Msg.getMsg( AD_Language,"Login" );
        String usrText     = Msg.getMsg( AD_Language,"User" );
        String pwdText     = Msg.getMsg( AD_Language,"Password" );
        String lngText     = Msg.translate( AD_Language,"AD_Language" );
        String okText      = Msg.getMsg( AD_Language,"OK" );
        String cancelText  = Msg.getMsg( AD_Language,"Cancel" );
        String storeTxt    = Msg.getMsg( AD_Language,"SaveCookie" );

        // Form - post to same URL

        String action = request.getRequestURI();
        form   myForm = null;

        myForm = new form( action ).setName( "Login1" );

        // myForm.setAcceptCharset(WebEnv.CHARACTERSET);

        table table = new table().setAlign( AlignType.CENTER );

        // Username

        String userData = cProp.getProperty( P_USERNAME,"" );
        tr     line     = new tr();
        label  usrLabel = new label().setFor( P_USERNAME + "F" ).addElement( usrText );

        usrLabel.setID( P_USERNAME + "L" );
        line.addElement( new td().addElement( usrLabel ).setAlign( AlignType.RIGHT ));

        input usr = new input( input.TYPE_TEXT,P_USERNAME,userData ).setSize( 20 ).setMaxlength( 30 );

        usr.setID( P_USERNAME + "F" );
        line.addElement( new td().addElement( usr ).setAlign( AlignType.LEFT ));
        table.addElement( line );

        // Password

        String pwdData = cProp.getProperty( P_PASSWORD,"" );

        line = new tr();

        label pwdLabel = new label().setFor( P_PASSWORD + "F" ).addElement( pwdText );

        pwdLabel.setID( P_PASSWORD + "L" );
        line.addElement( new td().addElement( pwdLabel ).setAlign( AlignType.RIGHT ));

        input pwd = new input( input.TYPE_PASSWORD,P_PASSWORD,pwdData ).setSize( 20 ).setMaxlength( 30 );

        pwd.setID( P_PASSWORD + "F" );
        line.addElement( new td().addElement( pwd ).setAlign( AlignType.LEFT ));
        table.addElement( line );

        // Language Pick

        String langData = cProp.getProperty( AD_Language );

        line = new tr();

        label langLabel = new label().setFor( Env.LANGUAGE + "F" ).addElement( lngText );

        langLabel.setID( Env.LANGUAGE + "L" );
        line.addElement( new td().addElement( langLabel ).setAlign( AlignType.RIGHT ));

        Set<String> idiomas = MLanguage.getActiveLanguages();
        
        option options[] = new option[ idiomas.size()];

        for ( int i = 0, j = 0;i < Language.getLanguageCount();i++ ) {
            Language language = Language.getLanguage( i );

            if (idiomas.contains(language.getAD_Language())) {
	            options[ j ] = new option( language.getAD_Language()).addElement( language.getName());
	
	            if( language.getAD_Language().equals( langData )) {
	                options[ j ].setSelected( true );
	            } else {
	                options[ j ].setSelected( false );
	            }
	            
	            j++;
            }
        }

        line.addElement( new td().addElement( new select( Env.LANGUAGE,options ).setID( Env.LANGUAGE + "F" )));
        table.addElement( line );

        // Store Cookie

        String storeData = cProp.getProperty( P_STORE,"N" );

        line = new tr();
        line.addElement( new td());

        input store = new input( input.TYPE_CHECKBOX,P_STORE,"Y" ).addElement( storeTxt ).setChecked( storeData.equals( "Y" ));

        store.setID( P_STORE + "F" );
        line.addElement( new td().addElement( store ).setAlign( AlignType.LEFT ));
        table.addElement( line );

        // ErrorMessage

        if( (errorMessage != null) && (errorMessage.length() > 0) ) {
            line = new tr();

            // line.addElement(new td());

            line.addElement( new td().setColSpan( 2 ).addElement( new font( HtmlColor.red,4 ).addElement( new b( errorMessage ))));    // color, size
            table.addElement( line );
        }

        // Finish

        line = new tr();

        input cancel = new input( input.TYPE_RESET,"Reset",cancelText );

        line.addElement( new td().addElement( cancel ));
        line.addElement( new td().addElement( new input( input.TYPE_SUBMIT,P_SUBMIT,okText )));
        table.addElement( line );

        //

        myForm.addElement( table );

        // Document

        WebDoc doc = WebDoc.createWindow( windowTitle );

        doc.addWindowCenter( true ).addElement( new h3( "Cliente ligero de acceso a openXpertya" )).addElement( myForm );

        // Clear Menu Frame

        doc.getBody().addElement( WebUtil.getClearFrame( WebEnv.TARGET_MENU )).setTitle( windowTitle );

        return doc;
    }    // createFirstPage

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param roleOptions
     * @param errorMessage
     *
     * @return
     */

    private WebDoc createSecondPage( HttpServletRequest request,option[] roleOptions,String errorMessage ) {
        log.info( "createSecondPage - " + errorMessage );

        WebSessionCtx wsc         = WebSessionCtx.get( request );
        String        windowTitle = Msg.getMsg( wsc.language,"LoginSuccess" );

        // Form - Get Menu

        String action = WebEnv.getBaseDirectory( "WMenu" );
        form   myForm = null;

        myForm = new form( action ).setName( "Login2" );
        myForm.setTarget( WebEnv.TARGET_MENU );
        myForm.setAcceptCharset( WebEnv.CHARACTERSET );

        table table = new table().setAlign( AlignType.CENTER );

        // Role Pick

        tr    line      = new tr();
        label roleLabel = new label().setFor( P_ROLE + "F" ).addElement( Msg.translate( wsc.language,"AD_Role_ID" ));

        roleLabel.setID( P_ROLE + "L" );
        line.addElement( new td().addElement( roleLabel ).setAlign( AlignType.RIGHT ));

        select role = new select( P_ROLE,roleOptions );

        role.setID( P_ROLE + "F" );
        role.setOnClick( "fieldUpdate(this);" );    // WFieldUpdate sets Client & Org
        line.addElement( new td().addElement( role ));
        table.addElement( line );

        // Client Pick

        line = new tr();

        label clientLabel = new label().setFor( P_CLIENT + "F" ).addElement( Msg.translate( wsc.language,"AD_Client_ID" ));

        clientLabel.setID( P_CLIENT + "L" );
        line.addElement( new td().addElement( clientLabel ).setAlign( AlignType.RIGHT ));

        select client = new select( P_CLIENT );

        client.setID( P_CLIENT + "F" );
        client.setOnClick( "fieldUpdate(this);" );    // WFieldUpdate sets Org
        line.addElement( new td().addElement( client ));
        table.addElement( line );

        // Org Pick

        line = new tr();

        label orgLabel = new label().setFor( P_ORG + "F" ).addElement( Msg.translate( wsc.language,"AD_Org_ID" ));

        orgLabel.setID( P_ORG + "L" );
        line.addElement( new td().addElement( orgLabel ).setAlign( AlignType.RIGHT ));

        select org = new select( P_ORG );

        org.setID( P_ORG + "F" );
        org.setOnClick( "fieldUpdate(this);" );    // WFieldUpdate sets Org
        line.addElement( new td().addElement( org ));
        table.addElement( line );

        // Warehouse

        line = new tr();

        label whLabel = new label().setFor( P_WAREHOUSE + "F" ).addElement( Msg.translate( wsc.language,"M_Warehouse_ID" ));

        whLabel.setID( P_WAREHOUSE + "L" );
        line.addElement( new td().addElement( whLabel ).setAlign( AlignType.RIGHT ));

        select wh = new select( P_WAREHOUSE );

        wh.setID( P_WAREHOUSE + "F" );
        line.addElement( new td().addElement( wh ));
        table.addElement( line );

        // Date

        String dateData = wsc.dateFormat.format( new java.util.Date());

        line = new tr();

        label dateLabel = new label().setFor( P_DATE + "F" ).addElement( Msg.getMsg( wsc.language,"Date" ));

        dateLabel.setID( P_DATE + "L" );
        line.addElement( new td().addElement( dateLabel ).setAlign( AlignType.RIGHT ));

        input date = new input( input.TYPE_TEXT,P_DATE,dateData ).setSize( 10 ).setMaxlength( 10 );

        date.setID( P_DATE + "F" );
        line.addElement( new td().addElement( date ).setAlign( AlignType.LEFT ));
        table.addElement( line );

        // ErrorMessage

        if( (errorMessage != null) && (errorMessage.length() > 0) ) {
            line = new tr();
            line.addElement( new td().addElement( new strong( errorMessage )).setColSpan( 2 ).setAlign( AlignType.CENTER ));
            table.addElement( line );
        }

        // Finish

        line = new tr();

        input cancel = new input( input.TYPE_RESET,"Reset",Msg.getMsg( wsc.language,"Cancel" ));

        line.addElement( new td().addElement( cancel ));

        input submit = new input( input.TYPE_SUBMIT,"Submit",Msg.getMsg( wsc.language,"OK" ));

        submit.setOnClick( "showLoadingMenu('" + WebEnv.getBaseDirectory( "" ) + "');" );
        line.addElement( new td().addElement( submit ));
        table.addElement( line );

        //

        myForm.addElement( table );

        // Create Document

        WebDoc doc = WebDoc.createWindow( windowTitle );

        doc.addWindowCenter( true ).addElement( new h3( "Escoja su configuración de usuario para acceso por cliente ligero." )).addElement( myForm );

        //

        String script = "fieldUpdate(document.Login2." + P_ROLE + ");";    // init dependency updates

        doc.getBody().addElement( new script( script ));

        // Note

        doc.addWindowFooter().addElement( new p( Msg.getMsg( wsc.language,"WLoginBrowserNote" ),AlignType.CENTER ));

        return doc;
    }    // createSecondPage
}    // WLogin



/*
 *  @(#)WLogin.java   23.03.06
 * 
 *  Fin del fichero WLogin.java
 *  
 *  Versión 2.2
 *
 */
