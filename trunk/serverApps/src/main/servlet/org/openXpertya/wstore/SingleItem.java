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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebEnv;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SingleItem extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private int m_AD_Client_ID = -1;

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
            throw new ServletException( "SingleItem" );
        }

        // Get Client

        m_AD_Client_ID = WebEnv.getAD_Client_ID( config );
        log.info( "init - AD_Client_ID=" + m_AD_Client_ID );
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
    }    // destroy

    // Parameter Names

    /** Descripción de Campos */

    private static final String P_ITEM_NAME = "item_name";

    /** Descripción de Campos */

    private static final String P_ITEM_NUMBER = "item_number";

    /** Descripción de Campos */

    private static final String P_AMOUNT = "amount";

    /** Descripción de Campos */

    private static final String P_QUANTITY = "quantity";

    /** Descripción de Campos */

    private static final String P_UNDEFINED_QUANTITY = "undefined_quantity";

    /** Descripción de Campos */

    private static final String P_RETURN_URL = "return";

    /** Descripción de Campos */

    private static final String P_CANCEL_URL = "cancel_return";

    //

    /** Descripción de Campos */

    private static final String P_SUBMIT = "SUBMIT";

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
    }    // doPost
}    // SingleItem



/*
 *  @(#)SingleItem.java   12.10.07
 * 
 *  Fin del fichero SingleItem.java
 *  
 *  Versión 2.2
 *
 */
