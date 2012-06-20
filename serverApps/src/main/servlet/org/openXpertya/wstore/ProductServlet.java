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

public class ProductServlet extends HttpServlet {

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
            throw new ServletException( "ProductServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Product Serach Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.info( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static String P_SEARCHSTRING = "SearchString";

    /** Descripción de Campos */

    public static String P_M_PRODUCT_CATEGORY_ID = "M_Product_Category_ID";

    /** Descripción de Campos */
    
	public static String	P_MINIMUM_PRICE = "precioMinimo";
	
    /** Descripción de Campos */
	
	public static String	P_MAXIMUM_PRICE = "precioMaximo";
	
    /** Descripción de Campos */
	
	public static String	P_IN_STOCK = "EnStock";
	
    /** Descripción de Campos */
	
	public static String	P_ORDER= "Orden";

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

        // Web User
        // WebUser wu = (WebUser)session.getAttribute(WebUser.NAME);

        // Save in ctx for PriceListTag

        // Search Parameter

        String searchString = WebUtil.getParameter( request,P_SEARCHSTRING );

        if( searchString != null ) {
            ctx.put( P_SEARCHSTRING,searchString );
        }

        // Product Category

        String category = WebUtil.getParameter( request,P_M_PRODUCT_CATEGORY_ID );

        if( category != null ) {
            ctx.put( P_M_PRODUCT_CATEGORY_ID,category );
        }

		//	Minimun Price
        
		String minimumPrice = WebUtil.getParameter (request, P_MINIMUM_PRICE);
		
		if (minimumPrice != null) {
			ctx.put(P_MINIMUM_PRICE, minimumPrice);
		}
		
		//	Maximum Price
		
		String maximumPrice = WebUtil.getParameter (request, P_MAXIMUM_PRICE);
		
		if (minimumPrice != null) {
			ctx.put(P_MAXIMUM_PRICE, maximumPrice);
		}
		
		//	Order
		
		String order = WebUtil.getParameter (request, P_ORDER);
		
		if (order != null) {
			ctx.put(P_ORDER, order);
		}
		
		//	In Stock
		
		String inStock = WebUtil.getParameter (request, P_IN_STOCK);
		
		if (inStock != null) {
			ctx.put(P_IN_STOCK, inStock);
		}
        
        // Forward

        String url = "/index.jsp";

        log.info( "doPost - Forward to " + url );

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
        log.info( "doGet from " + request.getRemoteHost() + " - " + request.getRemoteAddr());
        doPost( request,response );
    }    // doGet
}    // ProductServlet



/*
 *  @(#)ProductServlet.java   12.10.07
 * 
 *  Fin del fichero ProductServlet.java
 *  
 *  Versión 2.2
 *
 */
