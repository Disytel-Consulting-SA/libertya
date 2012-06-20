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

import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MRequest;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.FileUpload;
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

public class RequestServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( RequestServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "requestServlet";

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
            throw new ServletException( "RequestServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Request Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_FORWARDTO = "ForwardTo";

    /** Descripción de Campos */

    public static final String P_SOURCE = "Source";

    /** Descripción de Campos */

    public static final String P_INFO = "Info";

    /** Descripción de Campos */

    public static final String P_SALESREP_ID = "SalesRep_ID";

    /** Descripción de Campos */

    public static final String P_REQUESTTYPE_ID = "RequestType_ID";

    /** Descripción de Campos */

    public static final String P_SUMMARY = "Summary";

    /** Descripción de Campos */

    public static final String P_CONFIDENTIAL = "Confidential";

    /** Descripción de Campos */

    public static final String P_REQUEST_ID = "R_Request_ID";

    /** Descripción de Campos */

    public static final String P_ATTACHMENT_INDEX = "AttachmentIndex";

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
        log.info( "From " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        String url = "/requestDetails.jsp";

        //

        HttpSession session = request.getSession( false );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        if( (session == null) || (session.getAttribute( Info.NAME ) == null) ) {
            url = "/login.jsp";
        } else {
            Info info = ( Info )session.getAttribute( Info.NAME );

            if( info != null ) {
                info.setMessage( "" );
            }

            // Parameter = Note_ID - if is valid and belongs to wu then create PDF & stream it

            String msg = streamAttachment( request,response );

            if( (msg == null) || (msg.length() == 0) ) {
                return;
            }

            if( info != null ) {
                info.setMessage( msg );
            }
        }

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
     * @return
     */

    private String streamAttachment( HttpServletRequest request,HttpServletResponse response ) {

        // Get Request ID

        int R_Request_ID = WebUtil.getParameterAsInt( request,P_REQUEST_ID );

        if( R_Request_ID == 0 ) {
            log.fine( "No R_Request_ID)" );

            return "No Request ID";
        }

        int attachmentIndex = WebUtil.getParameterAsInt( request,P_ATTACHMENT_INDEX );

        if( attachmentIndex == 0 ) {
            log.fine( "No index)" );

            return "No Request Attachment index";
        }

        log.info( "R_Request_ID=" + R_Request_ID + " / " + attachmentIndex );

        // Get Request

        Properties ctx = JSPEnv.getCtx( request );
        MRequest   doc = new MRequest( ctx,R_Request_ID,null );

        if( doc.getR_Request_ID() != R_Request_ID ) {
            log.fine( "Request not found - R_Request_ID=" + R_Request_ID );

            return "Request not found";
        }

        MAttachment attachment = doc.getAttachment( false );

        if( attachment == null ) {
            log.fine( "No Attachment for R_Request_ID=" + R_Request_ID );

            return "Request Attachment not found";
        }

        // Get WebUser & Compare with invoice

        HttpSession session = request.getSession( true );
        WebUser     wu      = ( WebUser )session.getAttribute( WebUser.NAME );

        if( (wu.getAD_User_ID() == doc.getAD_User_ID()) || (wu.getAD_User_ID() == doc.getSalesRep_ID())) {
            ;
        } else {
            log.warning( "R_Request_ID=" + R_Request_ID + " Web_User=" + wu.getAD_User_ID() + " <> AD_User_ID=" + doc.getAD_User_ID() + " | SalesRep_ID=" + doc.getSalesRep_ID());

            return "Your Request not found";
        }

        // Stream it

        return WebUtil.streamAttachment( response,attachment,attachmentIndex );
    }    // streamAttachment

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
        String contentType = request.getContentType();

        log.info( "From " + request.getRemoteHost() + " - " + request.getRemoteAddr() + " - " + contentType );

        // Get Session attributes

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        //

        Properties ctx = JSPEnv.getCtx( request );
        WebUser    wu  = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            log.warning( "No web user" );
            response.sendRedirect( "loginServlet?ForwardTo=request.jsp" );    // entry

            return;
        }

        if( (contentType != null) && (contentType.indexOf( "multipart/form-data" ) != -1) ) {
            uploadFile( request,response );

            return;
        }

        // Addl Info

        String requestURL = request.getRequestURL().toString();
        String requestRef = request.getHeader( "referer" );
        String source     = WebUtil.getParameter( request,P_SOURCE );
        String info       = WebUtil.getParameter( request,P_INFO );
        String forwardTo  = WebUtil.getParameter( request,P_FORWARDTO );

        log.fine( "Referer=" + requestRef + ", Source=" + source + ", ForwardTo=" + forwardTo );

        if( requestURL == null ) {
            requestURL = "";
        }

        if( requestURL.equals( requestRef ))    // if URL and Referrer are the same, get source
        {
            requestRef = source;
            source     = null;
        }

        int AD_Client_ID     = Env.getContextAsInt( ctx,"AD_Client_ID" );
        int SalesRep_ID      = WebUtil.getParameterAsInt( request,P_SALESREP_ID );
        int R_RequestType_ID = WebUtil.getParameterAsInt( request,P_REQUESTTYPE_ID );
        int R_Request_ID = WebUtil.getParameterAsInt( request,P_REQUEST_ID );

        // The text

        String Summary = WebUtil.getParameter( request,P_SUMMARY );

        if( (Summary == null) || (Summary.length() == 0) ) {
            Summary = "-";    // to avoid save error - should be prevented by form
        }

        boolean Confidential = WebUtil.getParameterAsBoolean( request,P_CONFIDENTIAL );
        MRequest req = null;

        // New SelfService Request

        if( R_Request_ID == 0 ) {
            req = new MRequest( ctx,SalesRep_ID,R_RequestType_ID,Summary,true,null );
            req.setC_BPartner_ID( wu.getC_BPartner_ID());
            req.setAD_User_ID( wu.getAD_User_ID());
            req.setSalesRep_ID( SalesRep_ID );

            if( Confidential ) {
                req.setConfidentialType( MRequest.CONFIDENTIALTYPE_CustomerConfidential );
            }

            //

            StringBuffer sb = new StringBuffer();

            sb.append( "From:" ).append( request.getRemoteHost()).append( "-" ).append( request.getRemoteAddr());
            sb.append( ", Request:" ).append( requestURL ).append( "-" ).append( requestRef );

            if( source != null ) {
                sb.append( "-" ).append( source );
            }

            sb.append( "-" ).append( info );
            sb.append( ", User=" ).append( request.getHeader( "accept-language" )).append( "-" ).append( request.getHeader( "user-agent" ));
            req.setLastResult( sb.toString());

            //

            if( !req.save()) {
                log.log( Level.SEVERE,"New Request NOT saved" );
                WebUtil.createErrorPage( request,response,this,"Request Save Error." );

                return;
            }
        } else    // existing Request
        {
            req = new MRequest( ctx,R_Request_ID,null );

            if( req.getID() == 0 ) {
                log.log( Level.SEVERE,"Request NOT found - R_Request_ID=" + R_Request_ID );
                WebUtil.createErrorPage( request,response,this,"Request Not found." );

                return;
            }

            if( Confidential ) {
                req.setConfidentialTypeEntry( MRequest.CONFIDENTIALTYPEENTRY_CustomerConfidential );
            }

            if( !req.webUpdate( Summary )) {
                WebUtil.createErrorPage( request,response,this,"Request Cannot be updated." );

                return;
            }

            if( !req.save()) {
                log.log( Level.SEVERE,"Request Action Error" );
                WebUtil.createErrorPage( request,response,this,"Request Process Error." );

                return;
            }
        }    // Requests

        // Send EMail to Customer

        String webStoreURL = "http://" + request.getServerName() + request.getContextPath() + "/";
        String subject = Msg.translate( ctx,"R_Request_ID" ) + " " + req.getDocumentNo();
        String message = "Thank you for your request on " + webStoreURL + ":\n\n" + Summary;

        JSPEnv.sendEMail( ctx,wu.getEmail(),subject,message + req.getMailTrailer( webStoreURL ));

        // --  Fini

        if( (forwardTo == null) || (forwardTo.length() == 0) ) {
            forwardTo = requestRef;
        }

        if( (forwardTo != null) && ( (forwardTo.indexOf( "request.jsp" ) != -1) || (forwardTo.indexOf( "requestDetails.jsp" ) != -1) ) ) {
            forwardTo = "requests.jsp";    // list of requests
        }

        if( (forwardTo == null) || (forwardTo.length() == 0) ) {
            forwardTo = webStoreURL;
        }

        if( forwardTo.indexOf( "Servlet" ) != -1 ) {
            forwardTo = webStoreURL;
        }

        WebUtil.createForwardPage( response,"Web Request Received - Thanks",forwardTo,3 );
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

    private void uploadFile( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        FileUpload upload = new FileUpload( request );
        String     error  = upload.getError();

        if( error != null ) {
            WebUtil.createForwardPage( response,error,"requests.jsp",5 );

            return;
        }

        Properties ctx = JSPEnv.getCtx( request );

        // Get Request:

        int      R_Request_ID = upload.getParameterAsInt( "R_Request_ID" );
        MRequest req          = null;

        if( R_Request_ID != 0 ) {
            req = new MRequest( ctx,R_Request_ID,null );
        }

        if( (R_Request_ID == 0) || (req == null) || (req.getID() != R_Request_ID) ) {
            WebUtil.createForwardPage( response,"Request not found","requests.jsp",5 );

            return;
        }

        if( !req.isWebCanUpdate()) {
            WebUtil.createForwardPage( response,"Request cannot be updated","requests.jsp",5 );

            return;
        }

        String fileName = upload.getFileName();

        log.fine( "R_Request_ID=" + R_Request_ID + " - " + fileName );

        // Add Attachment

        MAttachment attachment = req.createAttachment();

        attachment.addEntry( fileName,upload.getData());

        if( attachment.save()) {
            String msg = Msg.parseTranslation( ctx,"@Added@: @AD_Attachment_ID@ " + fileName );

            req.webUpdate( msg );
            req.save();
            WebUtil.createForwardPage( response,msg,"requests.jsp",10 );
        } else {
            WebUtil.createForwardPage( response,"File Upload Error - Please try again","requests.jsp",10 );
        }

        log.fine( attachment.toString());
    }    // uploadFile
}    // RequestServlet



/*
 *  @(#)RequestServlet.java   12.10.07
 * 
 *  Fin del fichero RequestServlet.java
 *  
 *  Versión 2.2
 *
 */
