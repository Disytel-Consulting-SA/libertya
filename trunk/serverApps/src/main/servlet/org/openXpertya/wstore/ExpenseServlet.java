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

public class ExpenseServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ExpenseServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "ExpenseServlet";

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
            throw new ServletException( "ExpenseServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Expense Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_Expense_ID = "W_Expense_ID";

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
        response.sendRedirect( "expenses.jsp" );
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
            response.sendRedirect( "loginServlet?ForwardTo=expense.jsp" );    // entry

            return;
        }

        int W_Expense_ID = WebUtil.getParameterAsInt( request,P_Expense_ID );

        WebUtil.createForwardPage( response,"Web Expense Under Construction","expenses.jsp",0 );
    }    // doPost
}    // ExpenseSerlet



/*
 *  @(#)ExpenseServlet.java   12.10.07
 * 
 *  Fin del fichero ExpenseServlet.java
 *  
 *  Versión 2.2
 *
 */
