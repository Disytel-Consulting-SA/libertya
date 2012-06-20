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

import java.io.File;
import java.io.FileInputStream;
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

import org.openXpertya.model.MInvoice;
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

public class InvoiceServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( InvoiceServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "invoiceServlet";

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
            throw new ServletException( "InvoiceServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Invoice Servlet";
    }    // getServletInfo

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        log.fine( "destroy" );
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
        log.info( "doGet from " + request.getRemoteHost() + " - " + request.getRemoteAddr());

        String url = "/invoices.jsp";

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

            // Parameter = Invoice_ID - if invoice is valid and belongs to wu then create PDF & stream it

            String msg = streamInvoice( request,response );

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
     * @throws IOException
     * @throws ServletException
     */

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.info( "doPost from " + request.getRemoteHost() + " - " + request.getRemoteAddr());
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @return
     */

    private String streamInvoice( HttpServletRequest request,HttpServletResponse response ) {
        int MIN_SIZE = 2000;    // if not created size is 1015

        // Get Invoice ID

        int C_Invoice_ID = WebUtil.getParameterAsInt( request,"Invoice_ID" );

        if( C_Invoice_ID == 0 ) {
            log.fine( "streamInvoice - no ID)" );

            return "No Invoice ID";
        }

        // Get Invoice

        Properties ctx     = JSPEnv.getCtx( request );
        MInvoice   invoice = new MInvoice( ctx,C_Invoice_ID,null );

        if( invoice.getC_Invoice_ID() != C_Invoice_ID ) {
            log.fine( "streamInvoice - Invoice not found - ID=" + C_Invoice_ID );

            return "Invoice not found";
        }

        // Get WebUser & Compare with invoice

        HttpSession session = request.getSession( true );
        WebUser     wu      = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu.getC_BPartner_ID() != invoice.getC_BPartner_ID()) {
            log.warning( "streamInvoice - Invoice from BPartner - C_Invoice_ID=" + C_Invoice_ID + " - BP_Invoice=" + invoice.getC_BPartner_ID() + " = BP_Web=" + wu.getC_BPartner_ID());

            return "Your invoice not found";
        }

        // Check Directory

        String dirName = ctx.getProperty( "documentDir","." );

        try {
            File dir = new File( dirName );

            if( !dir.exists()) {
                dir.mkdir();
            }
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"streamInvoice - Could not create directory " + dirName,ex );

            return "Streaming error - directory";
        }

        // Check if Invoice already created

        String fileName = invoice.getPDFFileName( dirName );
        File   file     = new File( fileName );

        if( file.exists() && file.isFile() && (file.length() > MIN_SIZE) ) {
            log.info( "streamInvoice - existing: " + file + " - " + new Timestamp( file.lastModified()));
        } else {
            log.info( "streamInvoice - new: " + fileName );
            file = invoice.createPDF( file );

            if( file != null ) {
                invoice.setDatePrinted( new Timestamp( System.currentTimeMillis()));
                invoice.save();
            }
        }

        // Issue Error

        if( (file == null) ||!file.exists() || (file.length() < MIN_SIZE) ) {
            log.warning( "streamInvoice - File does not exist - " + file );

            return "Streaming error - file";
        }

        // Send PDF

        try {
            int bufferSize = 2048;    // 2k Buffer
            int fileLength = ( int )file.length();

            //

            response.setContentType( "application/pdf" );
            response.setBufferSize( bufferSize );
            response.setContentLength( fileLength );

            //

            log.fine( "streamInvoice - " + file.getAbsolutePath() + ", length=" + fileLength );

            long time = System.currentTimeMillis();    // timer start

            //

            FileInputStream     in        = new FileInputStream( file );
            ServletOutputStream out       = response.getOutputStream();
            byte[]              buffer    = new byte[ bufferSize ];
            double              totalSize = 0;
            int                 count     = 0;

            do {
                count = in.read( buffer,0,bufferSize );

                if( count > 0 ) {
                    totalSize += count;
                    out.write( buffer,0,count );
                }
            } while( count != -1 );

            out.flush();
            out.close();

            //

            in.close();
            time = System.currentTimeMillis() - time;

            double speed = ( totalSize / 1024 ) / (( double )time / 1000 );

            log.fine( "streamInvoice - length=" + totalSize + " - " + time + " ms - " + speed + " kB/sec" );
        } catch( IOException ex ) {
            log.log( Level.SEVERE,"streamInvoice - " + ex );

            return "Streaming error";
        }

        return null;
    }    // streamInvoice
}    // InvoiceServlet



/*
 *  @(#)InvoiceServlet.java   12.10.07
 * 
 *  Fin del fichero InvoiceServlet.java
 *  
 *  Versión 2.2
 *
 */
