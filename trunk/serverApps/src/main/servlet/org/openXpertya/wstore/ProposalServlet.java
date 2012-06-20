/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.wstore;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProposalServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ProposalServlet.class );

//      private static Logger   s_log = Logger.getCLogger(ProposalServlet.class);

    /** Descripción de Campos */

    static public final String NAME = "proposalServlet";

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
            throw new ServletException( "ProposalServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Proposal Servlet";
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
        log.info( "Get from " + request.getRemoteHost() + " - " + request.getRemoteAddr());
        doPost( request,response );
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

        // Web User/Basket

        WebUser   wu    = ( WebUser )session.getAttribute( WebUser.NAME );
        WebBasket wb    = ( WebBasket )session.getAttribute( WebBasket.NAME );
        MOrder    order = null;
        boolean   done  = false;
        String    url   = "/proposalInfo.jsp";
        String DocAction = WebUtil.getParameter( request,"DocAction" );
        
        // Not logged in

        if( (wu == null) ||!wu.isLoggedIn()) {
            session.setAttribute( "CheckOut","Y" );    // indicate checkout
            url  = "/login.jsp";
            done = true;
        } else {
            order = getOrder( request,ctx );
        }

        // We have an Order

        if( !done && (order != null) ) {
            if( processOrder( request,order ) ) {
            	if( (DocAction == null) || (DocAction.length() == 0) ) {
            		url = "/requisitionInfo.jsp"; 
            	} else {
            		url = "/proposals.jsp";
            	}
            } else {
                WebOrder wo = new WebOrder( order );
                MPayment p  = createPayment( session,ctx,wu,wo );

                if( p != null ) {
                    session.setAttribute( PaymentServlet.ATTR_PAYMENT,p );
                    session.setAttribute( WebOrder.NAME,wo );
                } else {
                    url = "/proposals.jsp";
                }
            }

            done = true;
        }

        // Nothing in basket

        if( !done && ( (wb == null) || (wb.getLineCount() == 0) ) ) {
            url  = "/basket.jsp";
            done = true;
        }

        // Create Proposal & Payment Info

        if( !done ) {
            WebOrder wo = new WebOrder( wu,wb,ctx );

            // We have an proposal - do delete basket & checkout indicator

            if( wo.isInProgress() || wo.isCompleted()) {
                session.removeAttribute( CheckOutServlet.ATTR_CHECKOUT );
                session.removeAttribute( WebBasket.NAME );
                sendEMail( request,ctx,wo,wu );
            }

            // If the Order is negative, don't create a payment

            if( wo.getGrandTotal().compareTo( Env.ZERO ) > 0 ) {
                session.setAttribute( WebOrder.NAME,wo );

                MPayment p = createPayment( session,ctx,wu,wo );

                if( p == null ) {
                    WebUtil.createForwardPage( response,"Payment could not be created","proposals.jsp",5 );

                    return;
                } else {
                    session.setAttribute( PaymentServlet.ATTR_PAYMENT,p );
                }
            } else {
                url = "/proposals.jsp";
            }
        }

        log.info( "Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param session
     * @param ctx
     * @param wu
     * @param wo
     *
     * @return
     */

    private MPayment createPayment( HttpSession session,Properties ctx,WebUser wu,WebOrder wo ) {

        // See PaymentServlet.doGet

        MPayment p = new MPayment( ctx,0,null );

        p.setIsSelfService( true );
        p.setAmount( wo.getC_Currency_ID(),wo.getGrandTotal());    // for CC selection
        p.setIsOnline( true );

        // Sales CC Trx

        p.setC_DocType_ID( true );
        p.setTrxType( MPayment.TRXTYPE_Sales );
        p.setTenderType( MPayment.TENDERTYPE_CreditCard );

        // Proposal Info

        p.setC_Order_ID( wo.getC_Order_ID());

        // BP Info

        p.setBP_BankAccount( wu.getBankAccount());

        //

        return p;
    }    // createPayment

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     *
     * @return
     */

    private MOrder getOrder( HttpServletRequest request,Properties ctx ) {

        // Proposal

        String para = WebUtil.getParameter( request,"C_Order_ID" );

        if( (para == null) || (para.length() == 0) ) {
            return null;
        }

        int C_Order_ID = 0;

        try {
            C_Order_ID = Integer.parseInt( para );
        } catch( NumberFormatException ex ) {
        }

        if( C_Order_ID == 0 ) {
            return null;
        }

        log.fine( "C_Order_ID=" + C_Order_ID );

        return new MOrder( ctx,C_Order_ID,null );
    }    // getOrder

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param order
     *
     * @return
     */

    private boolean processOrder( HttpServletRequest request,MOrder order ) {

        // Doc Action

        String DocAction = WebUtil.getParameter( request,"DocAction" );

        if( (DocAction == null) || (DocAction.length() == 0) ) {
            java.util.Date today = new java.util.Date();
            Timestamp now = new java.sql.Timestamp( today.getTime() );
                    
            MOrder newOrder = MOrder.copyFrom( order, now, 1000334, true, false, true, null ); // copy ASI
                       
            boolean ok = newOrder.save();
            
            if( !ok ) {
                throw new IllegalStateException( "Could not create new Order" );
            }

            newOrder.setC_DocType_ID( 1000334 ); // HARDCODED solicitud
            newOrder.save();

            order.setDocStatus( "CO" );
            order.save();
            
            return true;
        }

        MDocType dt = MDocType.get( order.getCtx(),order.getC_DocType_ID());

        if( !order.isSOTrx() || (order.getGrandTotal().compareTo( Env.ZERO ) <= 0) ||!MDocType.DOCBASETYPE_SalesOrder.equals( dt.getDocBaseType())) {
            log.warning( "Not a valid Sales Order " + order );

            return true;
        }

        // We have a Order No & DocAction

        log.fine( "DocAction=" + DocAction );

        if( !MOrder.DOCACTION_Void.equals( DocAction )) {

            // Do not complete Prepayment

            if( MOrder.STATUS_WaitingPayment.equals( order.getDocStatus())) {
                return false;
            }

            if( MDocType.DOCSUBTYPESO_PrepayOrder.equals( dt.getDocSubTypeSO())) {
                return false;
            }

            if( !MOrder.DOCACTION_Complete.equals( DocAction )) {
                log.warning( "Invalid DocAction=" + DocAction );

                return true;
            }
        }

        order.setDocAction( DocAction,true );    // force creation
        
        boolean ok = order.processIt( DocAction );
        
        order.save();
               
        return ok;
    }    // processOrder

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param wo
     * @param wu
     */

    private void sendEMail( HttpServletRequest request,Properties ctx,WebOrder wo,WebUser wu ) {
        String subject = "OpenXpertya Web - Proposal " + wo.getDocumentNo();
        String message = "Thank you for your purchase." + "\nYou can view your proposal, invoices and open amounts at" + "\nhttp://" + request.getServerName() + request.getContextPath() + "/" + "\n\nProposal " + wo.getDocumentNo() + " - Amount " + wo.getGrandTotal() + "\n";

        //

        MOrder mo = wo.getOrder();

        if( mo != null ) {
            MOrderLine[] ol = mo.getLines( true,null );

            for( int i = 0;i < ol.length;i++ ) {
                message += "\n" + ol[ i ].getQtyOrdered() + " * " + ol[ i ].getName();

                if( ol[ i ].getDescription() != null ) {
                    message += " - " + ol[ i ].getDescription();
                }

                message += " (" + ol[ i ].getPriceActual() + ") = " + ol[ i ].getLineNetAmt();
            }    // line
        }        // order

        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );
    }    // sendEMail
}    // ProposalServlet



/*
 *  @(#)ProposalServlet.java   22.03.06
 * 
 *  Fin del fichero ProposalServlet.java
 *  
 *  Versión 2.0
 *
 */
 
