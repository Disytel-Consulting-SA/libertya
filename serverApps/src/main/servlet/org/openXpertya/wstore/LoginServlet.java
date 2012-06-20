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
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MSession;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.EMail;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LoginServlet extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static public final String NAME = "loginServlet";

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
            throw new ServletException( "LoginServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Login Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_ForwardTo = "ForwardTo";

    /** Descripción de Campos */

    public static final String P_SalesRep_ID = "SalesRep_ID";

    /** Descripción de Campos */

    public static final String LOGIN_JSP = "/login.jsp";

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

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        //
        // WEnv.dump(request);

        // save forward parameter

        String forward = WebUtil.getParameter( request,P_ForwardTo );    // get forward from request

        if( forward != null ) {
            session.setAttribute( P_ForwardTo,forward );
        }

        String salesRep = WebUtil.getParameter( request,P_SalesRep_ID );    // get SalesRep from request

        if( salesRep != null ) {
            session.setAttribute( P_SalesRep_ID,salesRep );
        }

        //

        String url = LOGIN_JSP;

        // Mode

        String  mode         = WebUtil.getParameter( request,"mode" );
        boolean deleteCookie = "deleteCookie".equals( mode );

        if( deleteCookie ) {
            log.fine( "** deleteCookie" );
            JSPEnv.deleteCookieWebUser( request,response );
        }

        boolean logout = "logout".equals( mode );

        if( logout || deleteCookie ) {
            log.fine( "** logout" );

            if( session != null ) {
                Properties ctx      = JSPEnv.getCtx( request );
                MSession   cSession = MSession.get( ctx,false );

                if( cSession != null ) {
                    cSession.logout();
                }

                //

                WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

                if( wu != null ) {
                    wu.logout();
                }

                session.setMaxInactiveInterval( 1 );
                session.invalidate();
            }

            // Forward to unsecure /

            WebUtil.createForwardPage( response,"Logout","http://" + request.getServerName() + "/",2 );

            return;
        }

        if( !url.startsWith( "/" )) {
            url = "/" + url;
        }

        log.info( "doGet - Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );

        return;
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
        log.info( "doPost from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        // WEnv.dump(session);
        // WEnv.dump(request);

        // Forward URL

        String url = WebUtil.getParameter( request,P_ForwardTo );    // get forward from request
        String salesRep = WebUtil.getParameter( request,P_SalesRep_ID );    // get SalesRep from request

        if( salesRep != null ) {
            session.setAttribute( P_SalesRep_ID,salesRep );
        }

        boolean checkOut = "Y".equals( session.getAttribute( CheckOutServlet.ATTR_CHECKOUT ));

        // Set in login.jsp & addressInfo.jsp

        boolean addressConfirm = "Y".equals( WebUtil.getParameter( request,"AddressConfirm" ));

        if( checkOut ) {
            if( addressConfirm ) {
                url = "/orderServlet";
            } else {
                url = "/addressInfo.jsp";
            }
        } else {
            addressConfirm = false;
        }

        if( (url == null) || (url.length() == 0) ) {
            url = ( String )session.getAttribute( P_ForwardTo );    // get from session

            if( (url == null) || (url.length() == 0) ) {
                url = "/index.jsp";
            }
        } else {
            if( !url.startsWith( "/" )) {
                url = "/" + url;
            }

            session.setAttribute( P_ForwardTo,url );    // save for log in issues
        }

        // SalesRep Parameter

        salesRep = ( String )session.getAttribute( P_SalesRep_ID );    // get SalesRep from session

        if( salesRep != null ) {
            url += "?SalesRep_ID=" + salesRep;
        }

        //

        String mode = WebUtil.getParameter( request,"Mode" );

        log.fine( "- targeting url=" + url + " - mode=" + mode );

        // Web User

        WebUser wu = WebUser.get( request );

        // Get Base Info

        String email = WebUtil.getParameter( request,"EMail" );

        if( email == null ) {
            email = "";
        }

        email = email.trim();

        String password = WebUtil.getParameter( request,"Password" );

        if( password == null ) {
            password = "";    // null loads w/o check
        }

        password = password.trim();

        // Send EMail                              ***     Send Password EMail Request

        if( "SendEMail".equals( mode )) {
            log.info( "** send mail" );
            wu = WebUser.get( ctx,email );    // find it

            if( !wu.isEMailValid()) {
                wu.setPasswordMessage( "EMail not found in system" );
            } else {
                wu.setPassword();             // set password to current

                String context = request.getServerName() + request.getContextPath() + "/";
                StringBuffer sb = new StringBuffer( "http://" ).append( context ).append( " received a Send Password request from\n" + request.getRemoteHost() + " - " + request.getRemoteAddr() + ".\n\nYour password is: " ).append( wu.getPassword()).append( "\n\nThank you for using " ).append( context );

                //

                String msg = JSPEnv.sendEMail( ctx,email,context + " Password request",sb.toString());

                if( EMail.SENT_OK.equals( msg )) {
                    wu.setPasswordMessage( "EMail sent" );
                } else {
                    wu.setPasswordMessage( "Problem sending EMail: " + msg );
                }
            }

            url = LOGIN_JSP;
        }    // SendEMail

        // Login

        else if( "Login".equals( mode )) {
            log.info( "** login " + email + "/" + password );

            // add Cookie

            JSPEnv.addCookieWebUser( request,response,email );

            // Always re-query

            wu = WebUser.get( ctx,email,password,false );
            wu.login( password );

            // Password valid

            if( wu.isLoggedIn()) {
                if( url.equals( LOGIN_JSP )) {
                    url = "/index.jsp";
                }

                // Create Session with User ID

                MSession cSession = MSession.get( ctx,request.getRemoteAddr(),request.getRemoteHost(),session.getId());

                if( cSession != null ) {
                    cSession.setWebStoreSession( true );
                }
            } else {
                url = LOGIN_JSP;
                log.fine( "- PasswordMessage=" + wu.getPasswordMessage());
            }

            session.setAttribute( Info.NAME,new Info( ctx,wu ));
        }    // Login

        // Login New

        else if( "LoginNew".equals( mode )) {
            log.info( "** loginNew" );
            JSPEnv.addCookieWebUser( request,response,"" );
            wu  = WebUser.get( ctx,"" );
            url = LOGIN_JSP;
        }

        // Submit - update/new Contact

        else if( "Submit".equals( mode )) {
            log.info( "** submit " + email + "/" + password + " - AddrConf=" + addressConfirm );

            // we have a record for address update

            if( (wu != null) && wu.isLoggedIn() && addressConfirm ) {    // address update
                ;
            } else {                                         // Submit - always re-load user record
                wu = WebUser.get( ctx,email,null,false );    // load w/o password check direct
            }

            //

            if( wu.getAD_User_ID() != 0 )    // existing BPC
            {
                String passwordNew = WebUtil.getParameter( request,"PasswordNew" );

                if( passwordNew == null ) {
                    passwordNew = "";
                }

                boolean passwordChange = (passwordNew.length() > 0) &&!passwordNew.equals( password );

                if( addressConfirm || wu.login( password )) {

                    // Create / set session

                    if( wu.isLoggedIn()) {
                        MSession cSession = MSession.get( ctx,request.getRemoteAddr(),request.getRemoteHost(),session.getId());

                        if( cSession != null ) {
                            cSession.setWebStoreSession( true );
                        }
                    }

                    //

                    if( passwordChange ) {
                        log.fine( "- update Pwd " + email + ", Old=" + password + ", DB=" + wu.getPassword() + ", New=" + passwordNew );
                    }

                    if( updateFields( request,wu,passwordChange )) {
                        if( passwordChange ) {
                            session.setAttribute( JSPEnv.HDR_MESSAGE,"Password changed" );
                        }

                        session.setAttribute( Info.NAME,new Info( ctx,wu ));
                    } else {
                        url = LOGIN_JSP;
                        log.warning( " - update not done" );
                    }
                } else {
                    url = LOGIN_JSP;
                    session.setAttribute( JSPEnv.HDR_MESSAGE,"Email/Password not correct" );
                    log.warning( " - update not confirmed" );
                }
            } else    // new
            {
                log.fine( "- new " + email + "/" + password );
                wu.setEmail( email );
                wu.setPassword( password );

                if( updateFields( request,wu,true )) {
                    if( wu.login( password )) {
                        session.setAttribute( Info.NAME,new Info( ctx,wu ));

                        // Create / set session

                        MSession cSession = MSession.get( ctx,request.getRemoteAddr(),request.getRemoteHost(),session.getId());

                        if( cSession != null ) {
                            cSession.setWebStoreSession( true );
                        }
                    } else {
                        url = LOGIN_JSP;
                    }
                } else {
                    log.fine( "- failed - " + wu.getSaveErrorMessage() + " - " + wu.getPasswordMessage());
                    url = LOGIN_JSP;
                }
            }    // new
        }        // Submit
                else {
            log.log( Level.SEVERE,"Unknown request - " + mode );
        }

        session.setAttribute( WebUser.NAME,wu );

        if( !url.startsWith( "/" )) {
            url = "/" + url;
        }

        log.info( "doPost - Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param wu
     * @param updateEMailPwd
     *
     * @return
     */

    private boolean updateFields( HttpServletRequest request,WebUser wu,boolean updateEMailPwd ) {
        if( updateEMailPwd ) {
            String s = WebUtil.getParameter( request,"PasswordNew" );

            wu.setPasswordMessage( null );
            wu.setPassword( s );

            if( wu.getPasswordMessage() != null ) {
                return false;
            }

            //

            s = WebUtil.getParameter( request,"EMail" );

            if( !WebUtil.isEmailValid( s )) {
                wu.setPasswordMessage( "EMail Invalid" );

                return false;
            }

            wu.setEmail( s.trim());
        }

        //

        StringBuffer mandatory = new StringBuffer();
        String       s         = WebUtil.getParameter( request,"Name" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setName( s.trim());
        } else {
            //mandatory.append( " - Name" );
            mandatory.append( " - Nombre" );
        }

        s = WebUtil.getParameter( request,"Company" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setCompany( s );
        }

        s = WebUtil.getParameter( request,"Title" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setTitle( s );
        }

        //

        s = WebUtil.getParameter( request,"Address" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setAddress( s );
        } else {
            //mandatory.append( " - Address" );
            mandatory.append( " - Direcci\u00f3n 1" );
        }

        s = WebUtil.getParameter( request,"Address2" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setAddress2( s );
        }

        //

        s = WebUtil.getParameter( request,"City" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setCity( s );
        } else {
            //mandatory.append( " - City" );
            mandatory.append( " - Ciudad" );
        }

        s = WebUtil.getParameter( request,"Postal" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setPostal( s );
        } else {
            //mandatory.append( " - Postal" );
            mandatory.append( " - C\u00f3digo postal" );
        }

        //

        s = WebUtil.getParameter( request,"C_Country_ID" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setC_Country_ID( s );
        }

        s = WebUtil.getParameter( request,"C_Region_ID" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setC_Region_ID( s );
        }

        s = WebUtil.getParameter( request,"RegionName" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setRegionName( s );
        }

        //

        s = WebUtil.getParameter( request,"Phone" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setPhone( s );
        }

        s = WebUtil.getParameter( request,"Phone2" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setPhone2( s );
        }

        s = WebUtil.getParameter( request,"Fax" );

        if( (s != null) && (s.length() != 0) ) {
            wu.setFax( s );
        }

        //

        if( mandatory.length() > 0 ) {
            mandatory.insert( 0,"Enter Mandatory" );
            wu.setSaveErrorMessage( mandatory.toString());

            return false;
        }

        return wu.save();
    }    // updateFields

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param wu
     */

    private void sendEMail( HttpServletRequest request,Properties ctx,WebUser wu ) {
        String subject = "OpenXpertya Web - Account " + wu.getEmail();
        String message = "Thank you for your setting up an account at http://" + request.getServerName() + request.getContextPath() + "/";

        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );
    }    // sendEMail
}    // LoginServlet



/*
 *  @(#)LoginServlet.java   12.10.07
 * 
 *  Fin del fichero LoginServlet.java
 *  
 *  Versión 2.2
 *
 */