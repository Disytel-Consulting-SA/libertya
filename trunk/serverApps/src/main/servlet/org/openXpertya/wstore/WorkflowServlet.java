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

import org.openXpertya.model.MAttachment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;
import org.openXpertya.wf.MWFActivity;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WorkflowServlet extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static public final String NAME = "WorkflowServlet";

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
            throw new ServletException( "WorkflowServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Workflow Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_WF_Activity_ID = "AD_WF_Activity_ID";

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
        log.info( "doGet from " + request.getRemoteHost() + " - " + request.getRemoteAddr() + " - forward to notes.jsp" );

        String url = "/notes.jsp";

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

            // Parameter = Activity_ID - if valid and belongs to wu then create PDF & stream it

            String msg = streamAttachment( request,response );

            if( (msg == null) || (msg.length() == 0) ) {
                return;
            }

            if( info != null ) {
                info.setMessage( msg );
            }
        }

        log.info( "doGet - Forward to " + url );

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

        // Get Activity ID

        int AD_WF_Activity_ID = WebUtil.getParameterAsInt( request,P_WF_Activity_ID );

        if( AD_WF_Activity_ID == 0 ) {
            log.fine( "streamAttachment - no AD_WF_Activity_ID)" );

            return "No Activity ID";
        }

        int attachmentIndex = WebUtil.getParameterAsInt( request,P_ATTACHMENT_INDEX );

        if( attachmentIndex == 0 ) {
            log.fine( "streamAttachment - no index)" );

            return "No Request Attachment index";
        }

        log.info( "streamAttachment - AD_WF_Activity_ID=" + AD_WF_Activity_ID + " / " + attachmentIndex );

        // Get Note

        Properties  ctx = JSPEnv.getCtx( request );
        MWFActivity doc = new MWFActivity( ctx,AD_WF_Activity_ID,null );

        if( doc.getID() != AD_WF_Activity_ID ) {
            log.fine( "streamAttachment - Activity not found - ID=" + AD_WF_Activity_ID );

            return "Activity not found";
        }

        MAttachment attachment = doc.getAttachment( false );

        if( attachment == null ) {
            log.fine( "streamAttachment - No Attachment for AD_WF_Activity_ID=" + AD_WF_Activity_ID );

            return "Notice Attachment not found";
        }

        // Get WebUser & Compare with invoice

        HttpSession session = request.getSession( true );
        WebUser     wu      = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu.getAD_User_ID() != doc.getAD_User_ID()) {
            log.warning( "streamAttachment - AD_WF_Activity_ID=" + AD_WF_Activity_ID + " - User_Activity=" + doc.getAD_User_ID() + " = Web_User=" + wu.getAD_User_ID());

            return "Your Activity not found";
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
        log.info( "doPost from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        String url = "/notes.jsp";

        //
        // Log.setTraceLevel(9);
        // WebEnv.dump(request);
        //

        HttpSession session = request.getSession( false );

        if( (session == null) || (session.getAttribute( Info.NAME ) == null) ) {
            url = "/login.jsp";
        } else {
            session.removeAttribute( JSPEnv.HDR_MESSAGE );

            Properties ctx = JSPEnv.getCtx( request );
            WebUser    wu  = ( WebUser )session.getAttribute( WebUser.NAME );

            if( wu == null ) {
                log.warning( "doPost - no web user" );
                response.sendRedirect( "loginServlet?ForwardTo=note.jsp" );    // entry

                return;
            }

            // Get Feedback

            int AD_WF_Activity_ID = WebUtil.getParameterAsInt( request,P_WF_Activity_ID );
            boolean isConfirmed = WebUtil.getParameterAsBoolean( request,"IsConfirmed" );
            boolean isApproved = WebUtil.getParameterAsBoolean( request,"IsApproved" );
            boolean isRejected = WebUtil.getParameterAsBoolean( request,"IsApproved" );
            String textMsg = WebUtil.getParameter( request,"textMsg" );

            log.fine( "doPost - TextMsg=" + textMsg );

            //

            MWFActivity act = new MWFActivity( ctx,AD_WF_Activity_ID,null );

            log.fine( "doPost - " + act );

            if( (AD_WF_Activity_ID == 0) || (act == null) || (act.getAD_WF_Activity_ID() != AD_WF_Activity_ID) ) {
                session.setAttribute( JSPEnv.HDR_MESSAGE,"Activity not found" );
            } else {
                if( act.isUserApproval() && ( isApproved || isRejected )) {
                    try {
                        act.setUserChoice( wu.getAD_User_ID(),isApproved
                                ?"Y"
                                :"N",DisplayType.YesNo,textMsg );
                        act.save();
                    } catch( Exception e ) {
                    }
                }    // approval
                        else if( act.isUserManual() && isConfirmed ) {
                    act.setUserConfirmation( wu.getAD_User_ID(),textMsg );
                    act.save();
                } else if( (textMsg != null) && (textMsg.length() > 0) ) {
                    act.setTextMsg( textMsg );
                    act.save();
                }
            }
        }

        log.info( "doGet - Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost
}    // WorkflowServlet



/*
 *  @(#)WorkflowServlet.java   12.10.07
 * 
 *  Fin del fichero WorkflowServlet.java
 *  
 *  Versión 2.2
 *
 */
