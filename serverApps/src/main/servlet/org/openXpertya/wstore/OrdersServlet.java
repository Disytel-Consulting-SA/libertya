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
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OrdersServlet extends HttpServlet {

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
            throw new ServletException( "OrdersServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Order Search Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
    }    // destroy

    // Variables para definir la busqueda de pedidos

    /** Descripción de Campos */

    /*public static int[] posiciones = {
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
    };*/

    /** Descripción de Campos */

    public static String P_FECHA_MINIMA = "fechaMinimaPedido";

    /** Descripción de Campos */

    public static String P_FECHA_MAXIMA = "fechaMaximaPedido";

    /** Descripción de Campos */

    public static String[] P_VARIABLES = {
        "Borrador",
        "Completo",
        "Aprobado",
        "NoAprobado",
        "Anulado",
        "Inactivo",
        "Revertido",
        "Cerrado",
        "Desconocido",
        "EsperandoConfirmacion",
        "EnProceso",
        "PagosEnEspera"
    };

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
        log.info( "metodo Post desde " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        // Salvar en el ctx para la busqueda de pedidos

        for( int i = 0; i < P_VARIABLES.length; i++ ) {
            if( WebUtil.getParameter( request,P_VARIABLES[ i ] ) != null ) {
                ctx.put( P_VARIABLES[ i ],WebUtil.getParameter( request,P_VARIABLES[ i ] ));
                //posiciones[ i ] = i;
            } else {
                ctx.remove( P_VARIABLES[ i ] );
                //posiciones[ i ] = -1;
            }
        }

        // Fecha Minima De Pedido

        String fechaMinima = WebUtil.getParameter( request,P_FECHA_MINIMA );

        if( fechaMinima.equals( "" ) != true ) {
            ctx.put( P_FECHA_MINIMA,fechaMinima );
        } else {
            ctx.put( P_FECHA_MINIMA,"" );
        }

        // Fecha Maxima De Pedido

        String fechaMaxima = WebUtil.getParameter( request,P_FECHA_MAXIMA );

        if( fechaMaxima.equals( "" ) != true ) {
            ctx.put( P_FECHA_MAXIMA,fechaMaxima );
        } else {
            ctx.put( P_FECHA_MAXIMA,"" );
        }

        // Forward

        String url = "/orders.jsp";

        log.info( "Haciendo Post - Reenviando hacia " + url );
        log.info( " Este es el Contexto :" + ctx );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
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

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.info( "Haciendo Get desde " + request.getRemoteHost() + " - " + request.getRemoteAddr());
        doPost( request,response );
    }    // doGet
}    // OrdersServlet



/*
 *  @(#)OrdersServlet.java   12.10.07
 * 
 *  Fin del fichero OrdersServlet.java
 *  
 *  Versión 2.2
 *
 */
