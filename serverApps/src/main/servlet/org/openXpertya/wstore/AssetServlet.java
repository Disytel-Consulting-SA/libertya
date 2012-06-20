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
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MAsset;
import org.openXpertya.model.MAssetDelivery;
import org.openXpertya.model.MProductDownload;
import org.openXpertya.util.CLogger;
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

public class AssetServlet extends HttpServlet {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AssetServlet.class );

    /** Descripción de Campos */

    static public final String NAME = "assetServlet";

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
            throw new ServletException( "AssetServlet.init" );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServletInfo() {
        return "OpenXpertya Web Assets Servlet";
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

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( false );

        //

        String url = "/assets.jsp";

        if( (session == null) || (session.getAttribute( Info.NAME ) == null) ) {
            url = "/login.jsp";
        } else {
            session.removeAttribute( JSPEnv.HDR_MESSAGE );

            Info info = ( Info )session.getAttribute( Info.NAME );

            if( info != null ) {
                info.setMessage( "" );
            }

            // Parameter = Asset_ID - if invoice is valid and belongs to wu then create PDF & stream it

            String msg = streamAsset( request,response );

            if( info != null ) {
                info.setMessage( Msg.parseTranslation( ctx,msg ));
            }

            if( (msg == null    // OK
                    ) || (msg.length() == 0) || msg.startsWith( "**" )) {

                // if not returned - results in exception: Cannot forward after response has been committed

                return;
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
     * @throws IOException
     * @throws ServletException
     */

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
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

    protected String streamAsset( HttpServletRequest request,HttpServletResponse response ) {

        // Get Asset ID

        int A_Asset_ID = WebUtil.getParameterAsInt( request,"Asset_ID" );

        if( A_Asset_ID == 0 ) {
            log.fine( "No ID)" );

            return "No Asset ID";
        }

        byte[] assetInfo = String.valueOf( A_Asset_ID ).getBytes();

        // Get Asset

        Properties  ctx     = JSPEnv.getCtx( request );
        HttpSession session = request.getSession( true );

        WebEnv.dump( request );

        MAsset asset = new MAsset( ctx,A_Asset_ID,null );

        if( asset.getA_Asset_ID() != A_Asset_ID ) {
            log.fine( "Asset not found - ID=" + A_Asset_ID );

            return "Asset not found";
        }

        // Get WebUser & Compare with invoice

        WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

        if( wu.getC_BPartner_ID() != asset.getC_BPartner_ID()) {
            log.warning( "A_Asset_ID=" + A_Asset_ID + " - BP_Invoice=" + asset.getC_BPartner_ID() + " <> BP_User=" + wu.getC_BPartner_ID());

            return "Your asset not found";
        }

        if( !asset.isDownloadable() || wu.isCreditStopHold() ||!wu.isEMailVerified()) {
            return "Asset not downloadable";
        }

        // Name & URL

        String      pd                   = WebUtil.getParameter( request,"PD" );
        String      dl_name              = null;
        String      dl_url               = null;
        InputStream in                   = null;
        int         M_ProductDownload_ID = 0;

        if( (pd != null) && (pd.length() > 0) ) {
            MProductDownload[] pdls = asset.getProductDownloads();

            if( pdls != null ) {
                for( int i = 0;i < pdls.length;i++ ) {
                    if( pdls[ i ].getDownloadURL().indexOf( pd ) != -1 ) {
                        M_ProductDownload_ID = pdls[ i ].getM_ProductDownload_ID();
                        dl_name = pd;
                        dl_url  = pdls[ i ].getDownloadURL();
                        in      = pdls[ i ].getDownloadStream( ctx.getProperty( JSPEnv.CTX_DOCUMENT_DIR ));

                        break;
                    }
                }
            }
        }

        log.fine( dl_name + " - " + dl_url );

        if( (dl_name == null) || (dl_url == null) || (in == null) ) {
            return "@NotFound@ @A_Asset_ID@: " + pd;
        }

        String lot = asset.getLot();

        if( (lot == null) || (lot.length() == 0) ) {
            lot = ".";
        }

        String ser = asset.getSerNo();

        if( (ser == null) || (ser.length() == 0) ) {
            ser = ".";
        }

        Object[] args = new Object[] {
            dl_name,wu.getName() + " - " + wu.getEmail(),asset.getVersionNo(),lot,ser,asset.getGuaranteeDate()
        };
        String readme = Msg.getMsg( ctx,"AssetDeliveryTemplate",args );

        // Send File

        MAssetDelivery ad = asset.confirmDelivery( request,wu.getAD_User_ID());

        if( M_ProductDownload_ID != 0 ) {
            ad.setM_ProductDownload_ID( M_ProductDownload_ID );
        }

        ad.setDescription( dl_name );

        //

        float speed = 0;

        try {
            response.setContentType( "application/zip" );
            response.setHeader( "Content-Location","asset.zip" );

            // response.setContentLength(length);

            int bufferSize = 2048;    // 2k Buffer

            response.setBufferSize( bufferSize );

            //

            log.fine( in + ", available=" + in.available());

            long time = System.currentTimeMillis();

            // Zip Output Stream

            ServletOutputStream out = response.getOutputStream();
            ZipOutputStream     zip = new ZipOutputStream( out );    // Servlet out

            zip.setMethod( ZipOutputStream.DEFLATED );
            zip.setLevel( Deflater.BEST_COMPRESSION );
            zip.setComment( readme );

            // Readme File

            ZipEntry entry = new ZipEntry( "readme.txt" );

            entry.setExtra( assetInfo );
            zip.putNextEntry( entry );
            zip.write( readme.getBytes(),0,readme.length());
            zip.closeEntry();

            // Payload

            entry = new ZipEntry( dl_name );
            entry.setExtra( assetInfo );
            zip.putNextEntry( entry );

            byte[] buffer    = new byte[ bufferSize ];
            int    count     = 0;
            int    totalSize = 0;

            do {
                count = in.read( buffer,0,bufferSize );    // read delivery

                if( count > 0 ) {
                    totalSize += count;
                    zip.write( buffer,0,count );           // write zip
                }
            } while( count != -1 );

            zip.closeEntry();

            // Fini

            zip.finish();
            zip.close();
            in.close();
            time  = System.currentTimeMillis() - time;
            speed = (( float )totalSize / 1024 ) / (( float )time / 1000 );

            String msg = ( totalSize / 1024 ) + "kB - " + time + " ms - " + speed + " kB/sec";

            log.fine( msg );

            // Delivery Record

            ad.setDeliveryConfirmation( msg );
            ad.save();
            asset.save();
        } catch( IOException ex ) {
            String msg = ex.getMessage();

            if( (msg == null) || (msg.length() == 0) ) {
                msg = ex.toString();
            }

            log.warning( msg );

            // Delivery Record

            try {
                if( msg.length() > 120 ) {
                    msg = msg.substring( 0,119 );
                }

                ad.setDeliveryConfirmation( msg );
                ad.save();

                // asset.save();   not delivered

            } catch( Exception ex1 ) {
                log.log( Level.SEVERE,"2 - " + ex );
            }

            // nned to differentiate error message as response committed

            return "** Streaming error; Please Retry";
        }

        //

        return null;
    }    // streamAsset
}    // AssetServlet



/*
 *  @(#)AssetServlet.java   12.10.07
 * 
 *  Fin del fichero AssetServlet.java
 *  
 *  Versión 2.2
 *
 */
