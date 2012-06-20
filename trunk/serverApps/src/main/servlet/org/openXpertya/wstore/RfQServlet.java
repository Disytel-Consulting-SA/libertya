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
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQResponse;
import org.openXpertya.model.MRfQResponseLine;
import org.openXpertya.model.MRfQResponseLineQty;
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

public class RfQServlet extends HttpServlet {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static public final String NAME = "RfQServlet";

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
            throw new ServletException( "RfQServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web RfQ Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
    }    // destroy

    /** Descripción de Campos */

    public static final String P_RfQResponse_ID = "C_RfQResponse_ID";

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

        String url = "/rfqs.jsp";

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

            // Parameter = Note_ID - if is valid create PDF & stream it

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

        // Get Note ID

        int C_RfQ_ID = WebUtil.getParameterAsInt( request,"C_RfQ_ID" );

        if( C_RfQ_ID == 0 ) {
            log.fine( "streamAttachment - no ID)" );

            return "No RfQ ID";
        }

        // Get Note

        Properties ctx = JSPEnv.getCtx( request );
        MRfQ       doc = new MRfQ( ctx,C_RfQ_ID,null );

        if( doc.getC_RfQ_ID() != C_RfQ_ID ) {
            log.fine( "streamAttachment - RfQ not found - ID=" + C_RfQ_ID );

            return "RfQ not found";
        }

        if( !doc.isPdfAttachment()) {
            return "No PDF Attachment found";
        }

        byte[] data = doc.getPdfAttachment();

        if( data == null ) {
            return "No PDF Attachment";
        }

        // Send PDF

        try {
            int bufferSize = 2048;    // 2k Buffer
            int fileLength = data.length;

            //

            response.setContentType( "application/pdf" );
            response.setBufferSize( bufferSize );
            response.setContentLength( fileLength );

            //

            log.fine( "streamAttachment - length=" + fileLength );

            long time = System.currentTimeMillis();    // timer start

            //

            ServletOutputStream out = response.getOutputStream();

            out.write( data );
            out.flush();
            out.close();

            //

            time = System.currentTimeMillis() - time;

            double speed = ( fileLength / 1024 ) / (( double )time / 1000 );

            log.fine( "streamInvoice - length=" + fileLength + " - " + time + " ms - " + speed + " kB/sec" );
        } catch( IOException ex ) {
            log.log( Level.SEVERE,"streamAttachment - " + ex );

            return "Streaming error";
        }

        return null;
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

        // Log.setTraceLevel(9);
        // WebEnv.dump(request);
        // WebEnv.dump(request.getSession());

        // Get Session attributes

        HttpSession session = request.getSession( true );

        session.removeAttribute( JSPEnv.HDR_MESSAGE );

        //

        Properties ctx = JSPEnv.getCtx( request );
        WebUser    wu  = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu == null ) {
            log.warning( "doPost - no web user" );
            response.sendRedirect( "loginServlet?ForwardTo=note.jsp" );    // entry

            return;
        }

        int C_RfQResponse_ID = WebUtil.getParameterAsInt( request,P_RfQResponse_ID );
        int          C_RfQ_ID    = WebUtil.getParameterAsInt( request,"C_RfQ_ID" );
        MRfQResponse rfqResponse = new MRfQResponse( ctx,C_RfQResponse_ID,null );

        if( (C_RfQResponse_ID == 0) || (rfqResponse == null) || (rfqResponse.getID() != C_RfQResponse_ID) ) {
            WebUtil.createForwardPage( response,"RfQ Response not found","rfqs.jsp",5 );

            return;
        }

        if( wu.getC_BPartner_ID() != rfqResponse.getC_BPartner_ID()) {
            WebUtil.createForwardPage( response,"Your RfQ Response not found","rfqs.jsp",5 );

            return;
        }

        // Update Data

        String msg = updateResponse( request,rfqResponse );

        session.setAttribute( JSPEnv.HDR_MESSAGE,msg );

        String url = "/rfqDetails.jsp?C_RfQ_ID=" + C_RfQ_ID;

        //

        log.info( "doGet - Forward to " + url );

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );

        dispatcher.forward( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param rfqResponse
     *
     * @return
     */

    private String updateResponse( HttpServletRequest request,MRfQResponse rfqResponse ) {
        log.fine( "updateResponse - " + rfqResponse );

        String saveError = "RfQ NOT updated";
        String msg       = "RfQ updated";

        // RfQ Response

        rfqResponse.setName( WebUtil.getParameter( request,"Name" ));
        rfqResponse.setDescription( WebUtil.getParameter( request,"Description" ));
        rfqResponse.setHelp( WebUtil.getParameter( request,"Help" ));
        rfqResponse.setDateWorkStart( WebUtil.getParameterAsDate( request,"DateWorkStart" ));
        rfqResponse.setDateWorkComplete( WebUtil.getParameterAsDate( request,"DateWorkComplete" ));
        rfqResponse.setDeliveryDays( WebUtil.getParameterAsInt( request,"DeliveryDays" ));
        rfqResponse.setPrice( WebUtil.getParameterAsBD( request,"Price" ));
        rfqResponse.setIsSelfService( true );
        rfqResponse.setDateResponse( new Timestamp( System.currentTimeMillis()));

        // Check for Completeness

        if( WebUtil.getParameterAsBoolean( request,"IsComplete" )) {
            String msgComplete = rfqResponse.checkComplete();

            if( (msgComplete != null) && (msgComplete.length() > 0) ) {
                msg = msgComplete;
            }
        }

        if( !rfqResponse.save()) {
            return saveError;
        }

        // RfQ Response Line

        MRfQResponseLine[] lines = rfqResponse.getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MRfQResponseLine line = lines[ i ];

            if( !line.isActive()) {
                continue;
            }

            String paraAdd = "_" + line.getC_RfQResponseLine_ID();

            line.setDescription( WebUtil.getParameter( request,"Description" + paraAdd ));
            line.setHelp( WebUtil.getParameter( request,"Help" + paraAdd ));
            line.setDateWorkStart( WebUtil.getParameterAsDate( request,"DateWorkStart" + paraAdd ));
            line.setDateWorkComplete( WebUtil.getParameterAsDate( request,"DateWorkComplete" + paraAdd ));
            line.setDeliveryDays( WebUtil.getParameterAsInt( request,"DeliveryDays" + paraAdd ));
            line.setIsSelfService( true );

            if( !line.save()) {
                return saveError;
            }

            // RfQ Response Line Qty

            MRfQResponseLineQty[] qtys = line.getQtys( true );

            for( int j = 0;j < qtys.length;j++ ) {
                MRfQResponseLineQty qty = qtys[ j ];

                if( !qty.isActive()) {
                    continue;
                }

                paraAdd = "_" + qty.getC_RfQResponseLineQty_ID();
                qty.setDiscount( WebUtil.getParameterAsBD( request,"Discount" + paraAdd ));
                qty.setPrice( WebUtil.getParameterAsBD( request,"Price" + paraAdd ));

                if( !qty.save()) {
                    return saveError;
                }
            }
        }

        log.fine( "updateResponse - complete - " + rfqResponse );

        return msg;
    }    // updateResponse
}    // NoteServlet



/*
 *  @(#)RfQServlet.java   12.10.07
 * 
 *  Fin del fichero RfQServlet.java
 *  
 *  Versión 2.2
 *
 */
