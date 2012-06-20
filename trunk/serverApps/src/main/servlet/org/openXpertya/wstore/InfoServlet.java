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

import org.openXpertya.model.MContactInterest;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoServlet extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

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
            throw new ServletException( "InfoServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Interest Area Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
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
        log.info( "doGet from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        // WEnv.dump(session);
        // WEnv.dump(request);

        boolean success = processParameter( request );
        String  url     = "/info.jsp";

        log.info( "doPost - Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
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
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    private boolean processParameter( HttpServletRequest request ) {
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        Properties ctx = JSPEnv.getCtx( request );

        // mode = subscribe

        String mode = WebUtil.getParameter( request,"mode" );

        if( mode == null ) {
            return false;
        }

        boolean subscribe = !mode.startsWith( "un" );

        // area = 101

        int R_InterestArea_ID = WebUtil.getParameterAsInt( request,"area" );

        // contact = -1

        int AD_User_ID = WebUtil.getParameterAsInt( request,"contact" );

        //

        log.fine( "processParameter - subscribe=" + subscribe + ",R_InterestArea_ID=" + R_InterestArea_ID + ",AD_User_ID=" + AD_User_ID );

        if( (R_InterestArea_ID == 0) || (AD_User_ID == 0) ) {
            return false;
        }

        //

        MContactInterest ci = MContactInterest.get( ctx,R_InterestArea_ID,AD_User_ID );

        ci.subscribe( subscribe );

        boolean ok = ci.save();

        if( ok ) {
            log.fine( "processParameter - success" );
        } else {
            log.log( Level.SEVERE,"processParameter - subscribe failed" );
        }

        // Lookup user if direct link

        WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            wu = WebUser.get( ctx,AD_User_ID );
            session.setAttribute( WebUser.NAME,wu );
        }

        return ok;
    }    // processParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param wu
     */

    private void sendEMail( HttpServletRequest request,Properties ctx,WebUser wu ) {
        String subject = "OpenXpertya Web - Interest Area";
        String message = "Thank you - http://" + request.getServerName() + request.getContextPath() + "/";

        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );
    }    // sendEMail
}    // InfoServlet



/*
 *  @(#)InfoServlet.java   12.10.07
 * 
 *  Fin del fichero InfoServlet.java
 *  
 *  Versión 2.2
 *
 */
