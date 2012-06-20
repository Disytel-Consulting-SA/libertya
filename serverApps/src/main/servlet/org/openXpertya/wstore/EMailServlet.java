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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

public class EMailServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( EMailServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "emailServlet";

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
            throw new ServletException( "EMailServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya EMail";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "" );
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
        log.info( "Get from " + request.getRemoteHost() + " - " + request.getRemoteAddr());
        doGet( request,response );
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
        log.info( "Post from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        String url = WebUtil.getParameter( request,"ForwardTo" );

        if( (url == null) || (url.length() == 0) ) {
            url = "emailVerify.jsp";
        }

        // Web User

        WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            log.warning( "No web user" );
            response.sendRedirect( "loginServlet?ForwardTo=" + url );

            return;
        }

        log.info( url + " - " + wu.toString());

        //

        String cmd = WebUtil.getParameter( request,"ReSend" );

        if( (cmd != null) && (cmd.length() > 1) ) {
            resendCode( ctx,wu );
        } else {
            wu.setEMailVerifyCode( WebUtil.getParameter( request,"VerifyCode" ),request.getRemoteAddr());
        }

        url = "/" + url;
        log.info( "Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param wu
     */

    private void resendCode( Properties ctx,WebUser wu ) {
        String subject = "OpenXpertya Web - Validation";
        String message = "You requested the Validation Code: " + wu.getEMailVerifyCode();
        String msg = JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );

        if( EMail.SENT_OK.equals( msg )) {
            wu.setPasswordMessage( "EMail sent" );
        } else {
            wu.setPasswordMessage( "Problem sending EMail: " + msg );
        }
    }    // resendCode
}    // EMailServlet



/*
 *  @(#)EMailServlet.java   12.10.07
 * 
 *  Fin del fichero EMailServlet.java
 *  
 *  Versión 2.2
 *
 */
