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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MAdvertisement;
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

public class AdvertisementServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AdvertisementServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "AdvertisementServlet";

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
            throw new ServletException( "AdvertisementServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Avertisement Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_ADVERTISEMENT_ID = "W_Advertisement_ID";

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
        log.info( "Get from " + request.getRemoteHost() + " - " + request.getRemoteAddr() + " - forward to request.jsp" );
        response.sendRedirect( "advertisements.jsp" );
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

        // Get Session attributes

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        //

        Properties ctx = JSPEnv.getCtx( request );
        WebUser    wu  = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            log.warning( "No web user" );
            response.sendRedirect( "loginServlet?ForwardTo=advertisement.jsp" );    // entry

            return;
        }

        int W_Advertisement_ID = WebUtil.getParameterAsInt( request,P_ADVERTISEMENT_ID );
        MAdvertisement ad = new MAdvertisement( ctx,W_Advertisement_ID,null );

        if( ad.getID() == 0 ) {
            WebUtil.createForwardPage( response,"Web Advertisement Not Found","advertisements.jsp",0 );

            return;
        }

        StringBuffer info = new StringBuffer();

        //

        String Name = WebUtil.getParameter( request,"Name" );

        if( (Name != null) && (Name.length() > 0) &&!Name.equals( ad.getName())) {
            ad.setName( Name );
            info.append( "Name - " );
        }

        String Description = WebUtil.getParameter( request,"Description" );

        if( (Description != null) && (Description.length() > 0) &&!Description.equals( ad.getDescription())) {
            ad.setDescription( Description );
            info.append( "Description - " );
        }

        String ImageURL = null;
        String AdText   = WebUtil.getParameter( request,"AdText" );

        if( (AdText != null) && (AdText.length() > 0) &&!AdText.equals( ad.getAdText())) {
            ad.setAdText( AdText );
            info.append( "AdText - " );
        }

        String ClickTargetURL = WebUtil.getParameter( request,"ClickTargetURL" );

        if( (ClickTargetURL != null) && (ClickTargetURL.length() > 0) &&!ClickTargetURL.equals( ad.getClickTargetURL())) {
            ad.setClickTargetURL( ClickTargetURL );
            info.append( "ClickTargetURL - " );
        }

        if( info.length() > 0 ) {
            if( ad.save()) {
                WebUtil.createForwardPage( response,"Web Advertisement Updated: " + info.toString(),"advertisements.jsp",0 );
            } else {
                WebUtil.createForwardPage( response,"Web Advertisement Update Error","advertisements.jsp",0 );
            }
        } else {
            WebUtil.createForwardPage( response,"Web Advertisement not changed","advertisements.jsp",0 );
        }
    }    // doPost
}    // AdvertisementSerlet



/*
 *  @(#)AdvertisementServlet.java   12.10.07
 * 
 *  Fin del fichero AdvertisementServlet.java
 *  
 *  Versión 2.2
 *
 */
