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



package org.openXpertya.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.button;
import org.apache.ecs.xhtml.hr;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.small;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MAttachmentEntry;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class WebUtil {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( WebUtil.class );

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     * @param message
     *
     * @throws IOException
     * @throws ServletException
     */

    public static void createTimeoutPage( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,String message ) throws ServletException,IOException {
        s_log.info( message );

        WebSessionCtx wsc         = WebSessionCtx.get( request );
        String        windowTitle = "Timeout";

        if( wsc != null ) {
            windowTitle = Msg.getMsg( wsc.ctx,"Timeout" );
        }

        WebDoc doc = WebDoc.create( windowTitle );

        // Body

        body body = doc.getBody();

        // optional message

        if( (message != null) && (message.length() > 0) ) {
            body.addElement( new p( message,AlignType.CENTER ));
        }

        // login button

        body.addElement( getLoginButton( (wsc == null)
                                         ?null
                                         :wsc.ctx ));

        //

        body.addElement( new hr());
        body.addElement( new small( servlet.getClass().getName()));

        // fini

        createResponse( request,response,servlet,null,doc,false );
    }    // createTimeoutPage

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     * @param message
     *
     * @throws IOException
     * @throws ServletException
     */

    public static void createErrorPage( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,String message ) throws ServletException,IOException {
        s_log.info( message );

        WebSessionCtx wsc         = WebSessionCtx.get( request );
        String        windowTitle = "Error";

        if( wsc != null ) {
            windowTitle = Msg.getMsg( wsc.ctx,"Error" );
        }

        if( message != null ) {
            windowTitle += ": " + message;
        }

        WebDoc doc = WebDoc.create( windowTitle );

        // Body

        body b = doc.getBody();

        b.addElement( new p( servlet.getServletName(),AlignType.CENTER ));
        b.addElement( new br());

        // fini

        createResponse( request,response,servlet,null,doc,true );
    }    // createErrorPage

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     * @param ctx
     * @param AD_Message
     *
     * @throws IOException
     * @throws ServletException
     */

    public static void createLoginPage( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,Properties ctx,String AD_Message ) throws ServletException,IOException {
        request.getSession().invalidate();

        String url = WebEnv.getBaseDirectory( "index.html" );

        //

        WebDoc doc = null;

        if( (ctx != null) && (AD_Message != null) &&!AD_Message.equals( "" )) {
            doc = WebDoc.create( Msg.getMsg( ctx,AD_Message ));
        } else if( AD_Message != null ) {
            doc = WebDoc.create( AD_Message );
        } else {
            doc = WebDoc.create( false );
        }

        script script = new script( "window.top.location.replace('" + url + "');" );

        doc.getBody().addElement( script );

        //

        createResponse( request,response,servlet,null,doc,false );
    }    // createLoginPage

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static button getLoginButton( Properties ctx ) {
        String text = "Login";

        if( ctx != null ) {
            text = Msg.getMsg( ctx,"Login" );
        }

        button button = new button();

        button.setType( "button" ).setName( "Login" ).addElement( text );

        StringBuffer cmd = new StringBuffer( "window.top.location.replace('" );

        cmd.append( WebEnv.getBaseDirectory( "index.html" ));
        cmd.append( "');" );
        button.setOnClick( cmd.toString());

        return button;
    }    // getLoginButton

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static Properties getCookieProprties( HttpServletRequest request ) {

        // Get Properties

        Cookie[] cookies = request.getCookies();

        if( cookies != null ) {
            for( int i = 0;i < cookies.length;i++ ) {
                if( cookies[ i ].getName().equals( WebEnv.COOKIE_INFO )) {
                    return propertiesDecode( cookies[ i ].getValue());
                }
            }
        }

        return new Properties();
    }    // getProperties

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static String getParameter( HttpServletRequest request,String parameter ) {
        return getParameter(request, parameter, 0);
    }    // getParameterAsInt


    public static String getParameter( HttpServletRequest request,String parameter, int index) {
        if( (request == null) || (parameter == null) ) {
            return null;
        }

        String[] dataArr = request.getParameterValues( parameter );;
        String data = null;
        
        if (dataArr != null && index < dataArr.length)
        	data = dataArr[index];
        
        try {
            if( (data != null) && (data.length() > 0) ) {

                // only NN6 accepts the form attribute: accept-charset="utf-8"

                String newData = new String( data.getBytes( "ISO-8859-1" ),"UTF-8" );

                data = newData;
            }
        } catch( UnsupportedEncodingException ex ) {
            s_log.warning( parameter + "=" + data + " - " + ex );
        }

        return data;
    }    // getParameterAsInt

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static int getParameterAsInt( HttpServletRequest request,String parameter ) {
        if( (request == null) || (parameter == null) ) {
            return 0;
        }

        String data = getParameter( request,parameter );

        if( (data == null) || (data.length() == 0) ) {
            return 0;
        }

        try {
            return Integer.parseInt( data );
        } catch( Exception e ) {
            s_log.warning( parameter + "=" + data + " - " + e );
        }

        return 0;
    }    // getParameterAsInt

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static BigDecimal getParameterAsBD( HttpServletRequest request,String parameter ) {
        if( (request == null) || (parameter == null) ) {
            return Env.ZERO;
        }

        String data = getParameter( request,parameter );

        BigDecimal bd = parseBD(data);
        
        if (bd == null) {
        	s_log.fine( parameter + "=" + data );
        	bd = Env.ZERO;
        }
        
        return bd;
        
    }    // getParameterAsBD

    public static BigDecimal parseBD(String data) {
    	if( (data == null) || (data.length() == 0) ) {
            return Env.ZERO;
        }

        try {
            return new BigDecimal( data );
        } catch( Exception e ) {
        }

        try {
            DecimalFormat format = DisplayType.getNumberFormat( DisplayType.Number );
            Object oo = format.parseObject( data );

            if( oo instanceof BigDecimal ) {
                return( BigDecimal )oo;
            } else if( oo instanceof Number ) {
                return new BigDecimal((( Number )oo ).doubleValue());
            }

            return new BigDecimal( oo.toString());
        } catch( Exception e ) {
        	s_log.fine( "parseBD" + e );
            return null;
        }
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static Timestamp getParameterAsDate( HttpServletRequest request,String parameter ) {
        return getParameterAsDate( request,parameter,null );
    }    // getParameterAsDate

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     * @param language
     *
     * @return
     */

    public static Timestamp getParameterAsDate( HttpServletRequest request,String parameter,Language language ) {
        if( (request == null) || (parameter == null) ) {
            return null;
        }

        String data = getParameter( request,parameter );

        if( (data == null) || (data.length() == 0) ) {
            return null;
        }

        Timestamp ts = parseDate(data, language);
        
        if (ts == null)
            s_log.warning( parameter + " - cannot parse: " + data );

        return ts;
    }    // getParameterAsDate

    public static Timestamp parseDate( String data,Language language ) {

        // Language Date Format

        if( language != null ) {
            try {
                DateFormat format = DisplayType.getDateFormat( DisplayType.Date,language );
                java.util.Date date = format.parse( data );

                if( date != null ) {
                    return new Timestamp( date.getTime());
                }
            } catch( Exception e ) {
            }
        }

        // Default Simple Date Format

        try {
            SimpleDateFormat format = DisplayType.getDateFormat( DisplayType.Date );
            java.util.Date date = format.parse( data );

            if( date != null ) {
                return new Timestamp( date.getTime());
            }
        } catch( Exception e ) {
        }

        // JDBC Format

        try {
            return Timestamp.valueOf( data );
        } catch( Exception e ) {
        }

        return null;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static boolean getParameterAsBoolean( HttpServletRequest request,String parameter ) {
        return getParameterAsBoolean( request,parameter,null );
    }    // getParameterAsBoolean

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     * @param expected
     *
     * @return
     */

    public static boolean getParameterAsBoolean( HttpServletRequest request,String parameter,String expected ) {
        if( (request == null) || (parameter == null) ) {
            return false;
        }

        String data = getParameter( request,parameter );

        if( (data == null) || (data.length() == 0) ) {
            return false;
        }

        // Ignore actual value

        if( expected == null ) {
            return true;
        }

        //

        return expected.equalsIgnoreCase( data );
    }    // getParameterAsBoolean

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     * @param servlet
     * @param cookieProperties
     * @param doc
     * @param debug
     *
     * @throws IOException
     */

    public static void createResponse( HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,Properties cookieProperties,WebDoc doc,boolean debug ) throws IOException {
        response.setHeader( "Cache-Control","no-cache" );

        // response.setContentType("text/html; charset=ISO-8859-1");   //  default

        response.setContentType( "text/html; charset=UTF-8" );

        //
        // Update Cookie - overwrite

        if( cookieProperties != null ) {
            Cookie cookie = new Cookie( WebEnv.COOKIE_INFO,propertiesEncode( cookieProperties ));

            cookie.setComment( "(c) FUNDESLE" );
            cookie.setSecure( false );
            cookie.setPath( "/" );

            if( cookieProperties.size() == 0 ) {
                cookie.setMaxAge( 0 );          // delete cookie
            } else {
                cookie.setMaxAge( 2592000 );    // 30 days in seconds   60*60*24*30
            }

            response.addCookie( cookie );
        }

        // add diagnostics

        if( debug && WebEnv.DEBUG ) {

            // doc.output(System.out);

            WebEnv.addFooter( request,response,servlet,doc.getBody());

            // doc.output(System.out);

        }

        // String content = doc.toString();
        // response.setContentLength(content.length());    //  causes problems at the end of the output

        // print document

        PrintWriter out = response.getWriter();    // with character encoding support

        doc.output( out );
        out.flush();

        if( out.checkError()) {
            s_log.log( Level.SEVERE,"error writing" );
        }

        // binary output (is faster but does not do character set conversion)
        // OutputStream out = response.getOutputStream();
        // byte[] data = doc.toString().getBytes();
        // response.setContentLength(data.length);
        // out.write(doc.toString().getBytes());
        //

        out.close();
    }    // createResponse

    /**
     * Descripción de Método
     *
     *
     * @param targetFrame
     *
     * @return
     */

    public static script getClearFrame( String targetFrame ) {
        StringBuffer cmd = new StringBuffer();

        cmd.append( "<!-- clear frame\n" ).append( "var d = parent." ).append( targetFrame ).append( ".document;\n" ).append( "d.open();\n" ).append( "d.write('<link href=\"" ).append( WebEnv.getStylesheetURL()).append( "\" rel=\"stylesheet\">');\n" ).append( "d.close();\n" ).append( "// -- clear frame -->" );

        //

        return new script( cmd.toString());
    }    // getClearFrame

    /**
     * Descripción de Método
     *
     *
     * @param url
     * @param delaySec
     *
     * @return
     */

    public static HtmlCode getForward( String url,int delaySec ) {
        if( delaySec <= 0 ) {
            delaySec = 3;
        }

        HtmlCode retValue = new HtmlCode();

        // Link

        a a = new a( url );

        a.addElement( url );
        retValue.addElement( a );

        // Java Script     - document.location -

        script script = new script( "setTimeout(\"window.top.location.replace('" + url + "')\"," + ( delaySec + 1000 ) + ");" );

        retValue.addElement( script );

        //

        return retValue;
    }    // getForward

    /**
     * Descripción de Método
     *
     *
     * @param response
     * @param title
     * @param forwardURL
     * @param delaySec
     *
     * @throws IOException
     * @throws ServletException
     */

    public static void createForwardPage( HttpServletResponse response,String title,String forwardURL,int delaySec ) throws ServletException,IOException {
        response.setContentType( "text/html; charset=UTF-8" );

        WebDoc doc = WebDoc.create( title );
        body   b   = doc.getBody();

        b.addElement( getForward( forwardURL,delaySec ));

        PrintWriter out = response.getWriter();

        doc.output( out );
        out.flush();

        if( out.checkError()) {
            s_log.log( Level.SEVERE,"Error writing" );
        }

        out.close();
        s_log.fine( forwardURL + " - " + title );
    }    // createForwardPage

    /**
     * Descripción de Método
     *
     *
     * @param test
     *
     * @return
     */

    public static boolean exists( String test ) {
        if( test == null ) {
            return false;
        }

        return test.length() > 0;
    }    // exists

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param parameter
     *
     * @return
     */

    public static boolean exists( HttpServletRequest request,String parameter ) {
        if( (request == null) || (parameter == null) ) {
            return false;
        }

        return exists( request.getParameter( parameter ));
    }    // exists

    /**
     * Descripción de Método
     *
     *
     * @param email
     *
     * @return
     */

    public static boolean isEmailValid( String email ) {
        if( (email == null) || (email.length() == 0) ) {
            return false;
        }

        try {
            InternetAddress ia = new InternetAddress( email,true );

            return true;
        } catch( AddressException ex ) {
            s_log.warning( email + " - " + ex.getLocalizedMessage());
        }

        return false;
    }    // isEmailValid

    /**
     * Descripción de Método
     *
     *
     * @param pp
     *
     * @return
     */

    public static String propertiesEncode( Properties pp ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            pp.store( bos,"OpenXpertya" );    // Header
        } catch( IOException e ) {
            s_log.log( Level.SEVERE,"store",e );
        }

        String result = new String( bos.toByteArray());

        // System.out.println("String=" + result);

        try {
            result = URLEncoder.encode( result,WebEnv.ENCODING );
        } catch( UnsupportedEncodingException e ) {
            s_log.log( Level.SEVERE,"encode" + WebEnv.ENCODING,e );

            String enc = System.getProperty( "file.encoding" );    // Windows default is Cp1252

            try {
                result = URLEncoder.encode( result,enc );
                s_log.info( "encode: " + enc );
            } catch( Exception ex ) {
                s_log.log( Level.SEVERE,"encode",ex );
            }
        }

        // System.out.println("String-Encoded=" + result);

        return result;
    }    // propertiesEncode

    /**
     * Descripción de Método
     *
     *
     * @param data
     *
     * @return
     */

    public static Properties propertiesDecode( String data ) {
        String result = null;

        // System.out.println("String=" + data);

        try {
            result = URLDecoder.decode( data,WebEnv.ENCODING );
        } catch( UnsupportedEncodingException e ) {
            s_log.log( Level.SEVERE,"decode" + WebEnv.ENCODING,e );

            String enc = System.getProperty( "file.encoding" );    // Windows default is Cp1252

            try {
                result = URLEncoder.encode( data,enc );
                s_log.log( Level.SEVERE,"decode: " + enc );
            } catch( Exception ex ) {
                s_log.log( Level.SEVERE,"decode",ex );
            }
        }

        // System.out.println("String-Decoded=" + result);

        ByteArrayInputStream bis = new ByteArrayInputStream( result.getBytes());
        Properties pp = new Properties();

        try {
            pp.load( bis );
        } catch( IOException e ) {
            s_log.log( Level.SEVERE,"load",e );
        }

        return pp;
    }    // propertiesDecode

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param default_ID
     *
     * @return
     */

    public static option[] convertToOption( NamePair[] list,String default_ID ) {
        int      size     = list.length;
        option[] retValue = new option[ size ];

        for( int i = 0;i < size;i++ ) {
            boolean selected = false;

            // select first entry

            if( (i == 0) && ( (default_ID == null) || (default_ID.length() == 0) ) ) {
                selected = true;
            }

            // Create option

            retValue[ i ] = new option( list[ i ].getID()).addElement( list[ i ].getName());

            // Select if ID/Key is same as default ID

            if( (default_ID != null) && default_ID.equals( list[ i ].getID())) {
                selected = true;
            }

            retValue[ i ].setSelected( selected );
        }

        return retValue;
    }    // convertToOption

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param FORMNAME
     * @param PARAMETER
     * @param labelText
     * @param inputType
     * @param value
     * @param sizeDisplay
     * @param size
     * @param longField
     * @param mandatory
     * @param onChange
     * @param script
     *
     * @return
     */

    static public tr createField( tr line,String FORMNAME,String PARAMETER,String labelText,String inputType,Object value,int sizeDisplay,int size,boolean longField,boolean mandatory,String onChange,StringBuffer script ) {
        if( line == null ) {
            line = new tr();
        }

        String labelInfo = labelText;

        if( mandatory ) {
            labelInfo += "&nbsp;<font color=\"red\">*</font>";

            String fName = "document." + FORMNAME + "." + PARAMETER;

            script.append( fName ).append( ".required=true; " );
        }

        label llabel = new label().setFor( PARAMETER ).addElement( labelInfo );

        llabel.setID( "ID_" + PARAMETER + "_Label" );

        // label.setTitle(description);

        line.addElement( new td().addElement( llabel ).setAlign( AlignType.RIGHT ));

        input iinput = new input( inputType,PARAMETER,(value == null)
                ?""
                :value.toString());

        iinput.setSize( sizeDisplay ).setMaxlength( size );
        iinput.setID( "ID_" + PARAMETER );

        if( (onChange != null) && (onChange.length() > 0) ) {
            iinput.setOnChange( onChange );
        }

        iinput.setTitle( labelText );

        td field = new td().addElement( iinput ).setAlign( AlignType.LEFT );

        if( longField ) {
            field.setColSpan( 3 );
        }

        line.addElement( field );

        return line;
    }    // addField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static input createClosePopupButton() {
        input close = new input( input.TYPE_BUTTON,"closePopup","Close" );

        close.setTitle( "Close PopUp" );    // Help
        close.setOnClick( "closePopup();return false;" );

        return close;
    }    // getClosePopupButton

    /**
     * Descripción de Método
     *
     *
     * @param response
     * @param attachment
     * @param attachmentIndex
     *
     * @return
     */

    public static String streamAttachment( HttpServletResponse response,MAttachment attachment,int attachmentIndex ) {
        if( attachment == null ) {
            return "No Attachment";
        }

        int                realIndex = -1;
        MAttachmentEntry[] entries   = attachment.getEntries();

        for( int i = 0;i < entries.length;i++ ) {
            MAttachmentEntry entry = entries[ i ];

            if( entry.getIndex() == attachmentIndex ) {
                realIndex = i;

                break;
            }
        }

        if( realIndex < 0 ) {
            s_log.fine( "No Attachment Entry for Index=" + attachmentIndex + " - " + attachment );

            return "Attachment Entry not found";
        }

        MAttachmentEntry entry = entries[ realIndex ];

        if( entry.getData() == null ) {
            s_log.fine( "Empty Attachment Entry for Index=" + attachmentIndex + " - " + attachment );

            return "Attachment Entry empty";
        }

        // Stream Attachment Entry

        try {
            int bufferSize = 2048;    // 2k Buffer
            int fileLength = entry.getData().length;

            //

            response.setContentType( entry.getContentType());
            response.setBufferSize( bufferSize );
            response.setContentLength( fileLength );

            //

            s_log.fine( entry.toString());

            long time = System.currentTimeMillis();    // timer start

            //

            ServletOutputStream out = response.getOutputStream();

            out.write( entry.getData());
            out.flush();
            out.close();

            //

            time = System.currentTimeMillis() - time;

            double speed = ( fileLength / 1024 ) / (( double )time / 1000 );

            s_log.info( "Length=" + fileLength + " - " + time + " ms - " + speed + " kB/sec - " + entry.getContentType());
        } catch( IOException ex ) {
            s_log.log( Level.SEVERE,ex.toString());

            return "Streaming error - " + ex;
        }

        return null;
    }    // streamAttachment

    /**
     * Descripción de Método
     *
     *
     * @param response
     * @param file
     *
     * @return
     */

    public static String streamFile( HttpServletResponse response,File file ) {
        if( file == null ) {
            return "No File";
        }

        if( !file.exists()) {
            return "File not found: " + file.getAbsolutePath();
        }

        MimeType mimeType = MimeType.get( file.getAbsolutePath());

        // Stream File

        try {
            int bufferSize = 2048;    // 2k Buffer
            int fileLength = ( int )file.length();

            //

            response.setContentType( mimeType.getMimeType());
            response.setBufferSize( bufferSize );
            response.setContentLength( fileLength );

            //

            s_log.fine( file.toString());

            long time = System.currentTimeMillis();    // timer start

            // Get Data

            FileInputStream     in  = new FileInputStream( file );
            ServletOutputStream out = response.getOutputStream();
            int                 c   = 0;

            while(( c = in.read()) != -1 ) {
                out.write( c );
            }

            //

            out.flush();
            out.close();
            in.close();

            //

            time = System.currentTimeMillis() - time;

            double speed = ( fileLength / 1024 ) / (( double )time / 1000 );

            s_log.info( "Length=" + fileLength + " - " + time + " ms - " + speed + " kB/sec - " + mimeType );
        } catch( IOException ex ) {
            s_log.log( Level.SEVERE,ex.toString());

            return "Streaming error - " + ex;
        }

        return null;
    }    // streamFile
    
    /**
	 * 	Get Close PopUp Buton
	 *	@return button
	 */
	public static input createClosePopupButton(Properties ctx)
	{
		String text = "Close";
		if (ctx != null)
			text = Msg.getMsg (ctx, "Close");
		
		input close = new input("button", text, "  "+text);		
		close.setID(text);
		close.setClass("closebtn");		
		close.setTitle ("Close PopUp");	//	Help
		//close.setOnClick ("closePopup();return false;");
		close.setOnClick ("self.close();return false;");
		return close;
	}	//	getClosePopupButton

}    // WUtil



/*
 *  @(#)WebUtil.java   02.07.07
 * 
 *  Fin del fichero WebUtil.java
 *  
 *  Versión 2.2
 *
 */
