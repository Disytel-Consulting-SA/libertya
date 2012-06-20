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



package org.openXpertya.wstore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MRegistration;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RegistrationServlet extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static public final String NAME = "RegistrationServlet";

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
            throw new ServletException( "RegistrationServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Registration Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_REGISTRATION_ID = "A_Registration_ID";

    /** Descripción de Campos */

    private String THANKS = "Thank you for your registration!";

    /** Descripción de Campos */

    private String PROBLEM = "Thank you for your registration - We experienced a problem - please let us know!";

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
        log.info( "doGet from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        if( !processSystemRegistration( request,response )) {
            log.info( "doGet forward to registration.jsp" );
            response.sendRedirect( "registration.jsp" );
        }
    }    // doGet

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @return
     *
     * @throws IOException
     * @throws ServletException
     */

    private boolean processSystemRegistration( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {

        // System Info

        String name     = WebUtil.getParameter( request,"Name" );
        String userName = WebUtil.getParameter( request,"UserName" );
        String password = WebUtil.getParameter( request,"Password" );

        // Not a System registration

        if( ( (name == null) || (name.length() == 0) ) && ( (userName == null) || (userName.length() == 0) ) && ( (password == null) || (password.length() == 0) ) ) {
            return false;
        }

        log.info( "processSystemRegistration - Name=" + name + ", User=" + userName );

        // Registration Info

        String  description  = WebUtil.getParameter( request,"Description" );
        boolean inProduction = WebUtil.getParameterAsBoolean( request,"IsInProduction","Y" );
        Timestamp startDate = WebUtil.getParameterAsDate( request,"StartProductionDate" );

        if( startDate == null ) {
            startDate = new Timestamp( System.currentTimeMillis());
        }

        boolean allowPublish = WebUtil.getParameterAsBoolean( request,"IsAllowPublish","Y" );
        boolean registered = WebUtil.getParameterAsBoolean( request,"IsRegistered","Y" );
        int Record_ID = WebUtil.getParameterAsInt( request,"Record_ID" );

        // Find User

        Properties ctx        = JSPEnv.getCtx( request );
        MUser      user       = null;
        int        AD_User_ID = DB.getSQLValue( null,"SELECT AD_User_ID FROM AD_User WHERE EMail=?",userName );

        if( AD_User_ID > 0 ) {
            user = MUser.get( ctx,AD_User_ID );
        } else {
            log.warning( "processSystemRegistration - User Not found=" + userName );
        }

        // Registration

        MRegistration reg = null;

        if( Record_ID > 0 ) {
            reg = new MRegistration( ctx,Record_ID,null );

            if( reg.getID() != Record_ID ) {
                log.warning( "processSystemRegistration - Registration Not found=" + Record_ID );
                reg = null;
            } else if( user != null ) {
                if( reg.getC_BPartner_ID() != user.getC_BPartner_ID()) {
                    log.warning( "processSystemRegistration - Registration for different BP - AD_User_ID=" + AD_User_ID + "(" + user.getEMail() + "), BP RegistrationBP=" + reg.getC_BPartner_ID() + "<>UserBP=" + user.getC_BPartner_ID());
                    reg = null;
                }

                if( !password.equals( user.getPassword())) {
                    log.warning( "processSystemRegistration - Password does not match - AD_User_ID=" + AD_User_ID + "(" + user.getEMail() + ")" );

                    // ??

                }
            }
        }

        if( reg == null ) {
            log.fine( "New Registration" );
            reg = new MRegistration( ctx,name,allowPublish,inProduction,startDate,null );
            Record_ID = 0;
        }

        // Common Update

        reg.setDescription( description );
        reg.setRemote_Addr( request.getRemoteAddr());
        reg.setRemote_Host( request.getRemoteHost());

        // User

        if( user != null ) {
            reg.setAD_User_ID( user.getAD_User_ID());
            reg.setC_BPartner_ID( user.getC_BPartner_ID());
        }

        if( reg.save()) {
            if( Record_ID == 0 ) {
                reg.loadAttributeValues( request );      // new
            } else {
                reg.updateAttributeValues( request );    // existing
            }

            sendAnswer( response,THANKS + " Record_ID=" + reg.getA_Registration_ID());
        } else {
            log.log( Level.SEVERE,"doPost - Registration not saved" );
            sendAnswer( response,PROBLEM + " Record_ID=0" );
        }

        return true;
    }    // processSystemRegistration

    /**
     * Descripción de Método
     *
     *
     * @param response
     * @param answer
     *
     * @throws IOException
     */

    private void sendAnswer( HttpServletResponse response,String answer ) throws IOException {
        response.setHeader( "Cache-Control","no-cache" );
        response.setContentType( "text/html; charset=UTF-8" );

        PrintWriter out = response.getWriter();    // with character encoding support

        out.print( answer );
        out.flush();
    }    // sendAnswer

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
        log.info( "doPost from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        // Get Session attributes

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        //

        Properties ctx = JSPEnv.getCtx( request );
        WebUser    wu  = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            log.warning( "doPost - no web user" );
            response.sendRedirect( "loginServlet?ForwardTo=registration.jsp" );    // entry

            return;
        }

        int A_Registration_ID = WebUtil.getParameterAsInt( request,P_REGISTRATION_ID );
        MRegistration reg = null;

        if( A_Registration_ID > 0 ) {
            reg = new MRegistration( ctx,A_Registration_ID,null );
        }

        if( reg == null ) {
            reg               = new MRegistration( ctx,0,null );
            A_Registration_ID = 0;
        }

        //

        String name = WebUtil.getParameter( request,"Name" );

        if( (name == null) || (name.length() == 0) ) {
            WebUtil.createForwardPage( response,"Name is Mandatory","registrations.jsp",4 );

            return;
        }

        reg.setName( name );

        String description = WebUtil.getParameter( request,"Description" );

        if( (description != null) && (description.length() > 0) ) {
            reg.setDescription( description );
        }

        boolean isInProduction = WebUtil.getParameterAsBoolean( request,"IsInProduction" );

        reg.setIsInProduction( isInProduction );

        Timestamp assetServiceDate = WebUtil.getParameterAsDate( request,"AssetServiceDate" );

        if( assetServiceDate == null ) {
            assetServiceDate = new Timestamp( System.currentTimeMillis());
        }

        reg.setAssetServiceDate( assetServiceDate );

        boolean isAllowPublish = WebUtil.getParameterAsBoolean( request,"IsAllowPublish" );

        reg.setIsAllowPublish( isAllowPublish );

        if( reg.save()) {
            if( A_Registration_ID == 0 ) {
                reg.loadAttributeValues( request );      // new
            } else {
                reg.updateAttributeValues( request );    // existing
            }

            WebUtil.createForwardPage( response,THANKS,"registrations.jsp",3 );
        } else {
            log.log( Level.SEVERE,"doPost - Registration not saved" );
            WebUtil.createForwardPage( response,PROBLEM,"registrations.jsp",3 );
        }
    }    // doPost
}    // RegistrationSerlet



/*
 *  @(#)RegistrationServlet.java   12.10.07
 * 
 *  Fin del fichero RegistrationServlet.java
 *  
 *  Versión 2.2
 *
 */
