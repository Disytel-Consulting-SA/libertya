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
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentValidate;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.EMail;
import org.openXpertya.util.EMailUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PaymentServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PaymentServlet.class );

    /** Descripción de Campos */

    public static final String ATTR_PAYMENT = "payment";

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
            throw new ServletException( "PaymentServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Payment Servlet";
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

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        // WEnv.dump(session);
        // WEnv.dump(request);

        // Non existing user or Existing Web Payment

        WebUser  wu = ( WebUser )session.getAttribute( WebUser.NAME );
        MPayment p  = ( MPayment )session.getAttribute( ATTR_PAYMENT );

        if( wu == null ) {
            log.info( "No User" );

            String url = "/index.jsp";

            log.info( "Forward to " + url );

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

            dispatcher.forward( request,response );
        }

        // Remove any open Order

        session.removeAttribute( WebOrder.NAME );

        // Payment Amount

        String amtParam = WebUtil.getParameter( request,"Amt" );

        if( (amtParam == null) || (amtParam.length() == 0) ) {
            log.info( "No Payment Amount (" + amtParam + ")" );
            doPost( request,response );

            return;
        }

        char[]       chars   = amtParam.toCharArray();
        StringBuffer sb      = new StringBuffer();
        boolean      decimal = false;

        for( int i = chars.length - 1;i >= 0;i-- ) {
            char c = chars[ i ];

            if( (c == ',') || (c == '.') ) {
                if( !decimal ) {
                    sb.insert( 0,'.' );
                    decimal = true;
                }
            } else if( Character.isDigit( c )) {
                sb.insert( 0,c );
            }
        }

        BigDecimal amt = null;

        try {
            if( sb.length() > 0 ) {
                amt = new BigDecimal( sb.toString());
                amt = amt.abs();    // make it positive
            }
        } catch( Exception ex ) {
            log.warning( "Parsing Amount=" + amtParam + " (" + sb + ") - " + ex.toString());
        }

        // Need to be positive amount

        if( (amt == null) || (amt.compareTo( Env.ZERO ) < 0) ) {
            log.info( "No valid Payment Amount (" + amtParam + ") - " + amt );
            doPost( request,response );

            return;
        }

        String invoiceParam = WebUtil.getParameter( request,"C_Invoice_ID" );
        int    C_Invoice_ID = 0;

        try {
            if( invoiceParam != null ) {
                C_Invoice_ID = Integer.parseInt( invoiceParam );
            }
        } catch( NumberFormatException ex ) {
            log.warning( "Parsing C_Invoice_ID=" + invoiceParam + " - " + ex.toString());
        }

        log.info( "Amt=" + amt + ", C_Invoice_ID=" + C_Invoice_ID );

        // Create New Payment for Amt & optional Invoice
        // see OrderServlet.createPayment

        p = new MPayment( ctx,0,null );
        p.setIsSelfService( true );
        p.setAmount( 0,amt );    // for CC selection ges default from Acct Currency
        p.setIsOnline( true );

        // Sales CC Trx

        p.setC_DocType_ID( true );
        p.setTrxType( MPayment.TRXTYPE_Sales );
        p.setTenderType( MPayment.TENDERTYPE_CreditCard );

        // Payment Info

        p.setC_Invoice_ID( C_Invoice_ID );

        // BP Info

        p.setBP_BankAccount( wu.getBankAccount());

        //
        // p.save();

        session.setAttribute( ATTR_PAYMENT,p );

        String url = "/paymentInfo.jsp";

        log.info( "Forward to " + url );

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
        log.info( "Post from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        // WEnv.dump(session);
        // WEnv.dump(request);

        // Web User/Payment

        WebUser  wu  = ( WebUser )session.getAttribute( WebUser.NAME );
        MPayment p   = ( MPayment )session.getAttribute( ATTR_PAYMENT );
        WebOrder wo  = ( WebOrder )session.getAttribute( WebOrder.NAME );
        String   url = null;

        if( (wu == null) || (p == null) ) {
            url = "/index.jsp";
        } else if( processPayment( request,ctx,p,wu,wo )) {
            url = "/confirm.jsp";
        } else {
            url = "/paymentInfo.jsp";
        }

        log.info( "Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param payment
     * @param wu
     * @param wo
     *
     * @return
     */

    private boolean processPayment( HttpServletRequest request,Properties ctx,MPayment payment,WebUser wu,WebOrder wo ) {
        boolean ok = processParameter( request,ctx,payment,wu );

        if( ok ) {

            // if negative amount - make it positive

            if( payment.getPayAmt().compareTo( Env.ZERO ) < 0 ) {
                payment.setPayAmt( payment.getPayAmt().abs());
            }

            ok = payment.processOnline();

            if( ok ) {

                // Process Web Order and Set Invoice ID

                if( wo != null ) {
                    if( !wo.isCompleted()) {
                        wo.process( payment );
                    }

                    if( !wo.isCompleted()) {
                        log.warning( "Order not processed " + wo );
                    }
                } else {
                    log.warning( "No Order" );
                }

                //

                payment.processIt( DocAction.ACTION_Complete );
                payment.save();
                sendThanksEMail( request,ctx,payment,wu,wo );
            } else {
                log.fine( payment.getErrorMessage());

                String errMsg = payment.getErrorMessage();

                payment.save();
                payment.setErrorMessage( errMsg );
                request.getSession().setAttribute( JSPEnv.HDR_MESSAGE,errMsg );

                //

                sendDeclineEMail( request,ctx,payment,wu );
            }
        }

        return ok;
    }    // processPayment

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param p
     * @param wu
     *
     * @return
     */

    private boolean processParameter( HttpServletRequest request,Properties ctx,MPayment p,WebUser wu ) {
        StringBuffer sb = new StringBuffer();

        p.setTenderType( MPayment.TENDERTYPE_CreditCard );
        p.setTrxType( MPayment.TRXTYPE_Sales );
        p.setA_EMail( wu.getEmail());

        // CC & Number

        String ccType = WebUtil.getParameter( request,"CreditCard" );

        p.setCreditCardType( ccType );

        String ccNumber = WebUtil.getParameter( request,"CreditCardNumber" );

        p.setCreditCardNumber( ccNumber );

        String AD_Message = MPaymentValidate.validateCreditCardNumber( ccNumber,ccType );

        if( AD_Message.length() > 0 ) {
            sb.append( Msg.getMsg( ctx,AD_Message )).append( " - " );
        }

        // Optional Verification Code

        String ccVV = WebUtil.getParameter( request,"CreditCardVV" );

        p.setCreditCardVV( ccVV );

        if( (ccVV != null) && (ccVV.length() > 0) ) {
            AD_Message = MPaymentValidate.validateCreditCardVV( ccVV,ccType );

            if( AD_Message.length() > 0 ) {
                sb.append( Msg.getMsg( ctx,AD_Message )).append( " - " );
            }
        }

        // Exp

        int mm = WebUtil.getParameterAsInt( request,"CreditCardExpMM" );

        p.setCreditCardExpMM( mm );

        int yy = WebUtil.getParameterAsInt( request,"CreditCardExpYY" );

        p.setCreditCardExpYY( yy );
        AD_Message = MPaymentValidate.validateCreditCardExp( mm,yy );

        if( AD_Message.length() > 0 ) {
            sb.append( Msg.getMsg( ctx,AD_Message )).append( " - " );
        }

        // Account Info

        String aName = WebUtil.getParameter( request,"A_Name" );

        if( (aName == null) || (aName.length() == 0) ) {
            sb.append( "Name - " );
        } else {
            p.setA_Name( aName );
        }

        String aStreet = WebUtil.getParameter( request,"A_Street" );

        p.setA_Street( aStreet );

        String aCity = WebUtil.getParameter( request,"A_City" );

        if( (aCity == null) || (aCity.length() == 0) ) {
            sb.append( "City - " );
        } else {
            p.setA_City( aCity );
        }

        String aState = WebUtil.getParameter( request,"A_State" );

        p.setA_State( aState );

        String aZip = WebUtil.getParameter( request,"A_Zip" );

        if( (aZip == null) || (aZip.length() == 0) ) {
            sb.append( "Zip - " );
        } else {
            p.setA_Zip( aZip );
        }

        String aCountry = WebUtil.getParameter( request,"A_Country" );

        p.setA_Country( aCountry );

        // Error Message

        boolean ok = sb.length() == 0;

        p.setErrorMessage( sb.toString());    // always set

        // Save BP Bank Account

        if( ok ) {
            String SP          = "SavePayment";
            String SavePayment = WebUtil.getParameter( request,SP );

            if( SP.equals( SavePayment )) {
                p.saveToBP_BankAccount( wu.getBankAccount());
            }
        }

        //

        return ok;
    }    // processParameter

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param p
     * @param wu
     * @param wo
     */

    private void sendThanksEMail( HttpServletRequest request,Properties ctx,MPayment p,WebUser wu,WebOrder wo ) {
        String subject = "OpenXpertya Web - " + p.getPayAmt() + " Payment - " + p.getDocumentNo();
        String message = "Dear " + wu.getName() + "\nThank you for your payment of " + p.getPayAmt() + " (Reference=" + p.getR_PnRef() + ")";

        if( wo != null ) {
            message += "\nfor Order: " + wo.getDocumentNo();
        }

        message += "\nYou can view your orders, payments and assets at http://" + request.getServerName() + request.getContextPath() + "/" + "\nSincerely," + "\n  The " + ctx.getProperty( "description" ) + " Team";
        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );

        // SalesRep EMail

        if( (wo != null) && (wo.getSalesRep_ID() != 0) ) {
            MClient client = MClient.get( ctx );
            String  to     = EMailUtil.getEMail_User( wo.getSalesRep_ID(),true,null );

            if( (to != null) && (to.length() > 0) ) {
                EMail email = new EMail( client,null,to,"CC: " + subject,message );

                email.send();
            }
        }
    }    // sendEMail

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param ctx
     * @param p
     * @param wu
     */

    private void sendDeclineEMail( HttpServletRequest request,Properties ctx,MPayment p,WebUser wu ) {
        String subject = "OpenXpertya Web - Declined Payment " + p.getDocumentNo();
        String message = "Payment of " + p.getPayAmt() + " (Reference=" + p.getR_PnRef() + ")" + "\nwas declined " + p.getErrorMessage() + "\nUser=" + wu.getName() + " - " + wu.getEmail();

        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message );
    }    // sendDeclineEMail
}    // PaymentServlet



/*
 *  @(#)PaymentServlet.java   12.10.07
 * 
 *  Fin del fichero PaymentServlet.java
 *  
 *  Versión 2.2
 *
 */
