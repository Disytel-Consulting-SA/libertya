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
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BasketServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( BasketServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "basketServlet";

    /** Descripción de Campos */

    static public final String P_SalesRep_ID = "SalesRep_ID";

    /** Descripción de Campos */

    static public final String P_Product_ID = "M_Product_ID";

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
            throw new ServletException( "BasketServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Basket";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "" );
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
        log.info( "From " + request.getRemoteHost() + " - " + request.getRemoteAddr() + " - " + request.getRequestURL());

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        // Create WebBasket

        WebBasket wb = ( WebBasket )session.getAttribute( WebBasket.NAME );

        if( wb == null ) {
            wb = new WebBasket();
        }

        session.setAttribute( WebBasket.NAME,wb );

        // SalesRep

        int SalesRep_ID = WebUtil.getParameterAsInt( request,P_SalesRep_ID );

        if( SalesRep_ID != 0 ) {
            wb.setSalesRep_ID( SalesRep_ID );
            log.fine( "SalesRep_ID=" + SalesRep_ID );
        }

        // Get Price List

        PriceList pl = ( PriceList )session.getAttribute( PriceList.NAME );

        if( pl == null ) {
            log.fine( "No Price List in session" );
            pl = ( PriceList )request.getAttribute( PriceList.NAME );
        }

        log.fine( "PL=" + pl );

        // Do we delete?   Delete_x

        deleteLine( request,wb );

        // Do we add?      Add_x

        addLine( request,pl,wb );
        log.info( wb.toString());

        // Go back to basket

        String url = "/basket.jsp";

        log.info( "Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doGet

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param pl
     * @param wb
     */

    private void addLine( HttpServletRequest request,PriceList pl,WebBasket wb ) {
        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        // Get Parameter

        int M_PriceList_ID = WebUtil.getParameterAsInt( request,"M_PriceList_ID" );
        int M_PriceList_Version_ID = WebUtil.getParameterAsInt( request,"M_PriceList_Version_ID" );

        wb.setM_PriceList_ID( M_PriceList_ID );
        wb.setM_PriceList_Version_ID( M_PriceList_Version_ID );

        //

        int    M_Product_ID = WebUtil.getParameterAsInt( request,P_Product_ID );
        String Name         = WebUtil.getParameter( request,"Name" );
        String sQuantity    = WebUtil.getParameter( request,"Quantity" );
        String sPrice       = WebUtil.getParameter( request,"Price" );

        // Search for Product ID   Add_134 = Add

        Enumeration en = request.getParameterNames();

        while( (M_Product_ID == 0) && en.hasMoreElements()) {
            String parameter = ( String )en.nextElement();

            if( parameter.startsWith( "Add_" )) {
                if( WebUtil.exists( request,parameter ))    // to be sure
                {
                    try {
                        M_Product_ID = Integer.parseInt( parameter.substring( 4 ));
                        log.fine( "Found Parameter=" + parameter + " -> " + M_Product_ID );

                        if( !WebUtil.exists( sQuantity )) {
                            sQuantity = WebUtil.getParameter( request,"Qty_" + M_Product_ID );
                        }

                        if( !WebUtil.exists( sPrice )) {
                            sPrice = WebUtil.getParameter( request,"Price_" + M_Product_ID );
                        }

                        if( !WebUtil.exists( Name )) {
                            Name = WebUtil.getParameter( request,"Name_" + M_Product_ID );
                        }

                        log.fine( "Found Parameters " + Name + ",Qty=" + sQuantity + ",Price=" + sPrice );
                    } catch( Exception ex ) {
                        log.warning( "ParseError for " + parameter + " - " + ex.toString());
                    }
                }
            }
        }

        if( M_Product_ID == 0 ) {
            return;
        }

        // ****    Set Qty

        BigDecimal Qty = null;

        try {
            if( (sQuantity != null) && (sQuantity.length() > 0) ) {
                Qty = new BigDecimal( sQuantity );
            }
        } catch( Exception ex1 ) {
            log.warning( "(qty) - " + ex1.toString());
        }

        if( Qty == null ) {
            Qty = Env.ONE;
        }

        // ****    Set Price

        BigDecimal Price = null;

        // Find info in current price list

        if( (M_Product_ID != 0) && (pl != null) ) {
            PriceListProduct plp = pl.getPriceListProduct( M_Product_ID );

            if( plp != null ) {
                Price = plp.getPrice();
                Name  = plp.getName();
                log.fine( "Found in PL = " + Name + " - " + Price );
            }
        }

        // Price not in session price list and not as parameter

        if( (Price == null) && ( (pl == null) || pl.isNotAllPrices())) {

            // Create complete Price List

            int AD_Client_ID = Env.getContextAsInt( ctx,"AD_Client_ID" );

            pl = PriceList.get( ctx,AD_Client_ID,M_PriceList_ID,null,null,true,null,null );
            session.setAttribute( PriceList.NAME,pl );    // set on session level

            PriceListProduct plp = pl.getPriceListProduct( M_Product_ID );

            if( plp != null ) {
                Price = plp.getPrice();
                Name  = plp.getName();
                log.fine( "Found in complete PL = " + Name + " - " + Price );
            }
        }

        if( Price != null ) {
            WebBasketLine wbl = wb.add( M_Product_ID,Name,Qty,Price );

            log.fine( wbl.toString());
        } else {    // Price not found
            log.warning( "Product Price not found" );
        }
    }               // addLine

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param wb
     */

    private void deleteLine( HttpServletRequest request,WebBasket wb ) {
        Enumeration en = request.getParameterNames();

        while( en.hasMoreElements()) {
            String parameter = ( String )en.nextElement();

            if( parameter.startsWith( "Delete_" )) {
                try {
                    int line = Integer.parseInt( parameter.substring( 7 ));

                    log.fine( "Delete parameter=" + parameter + " -> " + line );
                    wb.delete( line );
                } catch( NumberFormatException ex ) {
                    log.warning( "ParseError for " + parameter + " - " + ex.toString());
                }
            }
        }
    }    // deleteLine

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

        // log.info("Post from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        doGet( request,response );
    }
}    // Basket



/*
 *  @(#)BasketServlet.java   12.10.07
 * 
 *  Fin del fichero BasketServlet.java
 *  
 *  Versión 2.2
 *
 */
