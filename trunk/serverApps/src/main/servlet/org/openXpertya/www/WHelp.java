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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WHelp extends HttpServlet {

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
            throw new ServletException( "WHelp.init" );
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
        log.fine( "doGet" );

        WWindowStatus ws = WWindowStatus.get( request );

        //

        WebDoc doc = null;

        if( ws == null ) {
            doc = WebDoc.createPopup( "No Context" );
            doc.addPopupClose();
        } else {
            doc = ws.mWindow.getHelpDoc( false );
        }

        //

        WebUtil.createResponse( request,response,this,null,doc,false );
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
        WebDoc doc = WebDoc.create( "Help - Post - Not Implemented" );

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doPost
}    // WHelp



/*
 *  @(#)WHelp.java   23.03.06
 * 
 *  Fin del fichero WHelp.java
 *  
 *  Versión 2.2
 *
 */
