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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FileUpload {

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    public static form createForm( String action ) {
        form upload = new form( action,form.METHOD_POST,form.ENC_UPLOAD );

        upload.addElement( new label( "File" ).setFor( "file" ));
        upload.addElement( new input( input.TYPE_FILE,"file","" ).setSize( 40 ));
        upload.addElement( new input( input.TYPE_SUBMIT,"upload","Upload" ));

        return upload;
    }    // createForm

    /**
     * Constructor de la clase ...
     *
     *
     * @param request
     */

    public FileUpload( HttpServletRequest request ) {
        super();

        try {
            m_error = upload( request );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"FileUpload",e );
            m_error = e.getLocalizedMessage();

            if( (m_error == null) || (m_error.length() == 0) ) {
                m_error = e.toString();
            }
        }
    }    // FileUpload

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private String m_fileName = null;

    /* File Data */

    /** Descripción de Campos */

    private byte[] m_data = null;

    /** Descripción de Campos */

    private String m_error = null;

    /** Descripción de Campos */

    private String m_requestDataString = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public byte[] getData() {
        return m_data;
    }    // getData

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFileName() {
        if( m_fileName != null )    // eliminate path
        {
            int index = Math.max( m_fileName.lastIndexOf( '/' ),m_fileName.lastIndexOf( '\\' ));

            if( index > 0 ) {
                return m_fileName.substring( index + 1 );
            }
        }

        return m_fileName;
    }    // getFileName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getError() {
        return m_error;
    }    // getError

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     *
     * @throws IOException
     * @throws ServletException
     */

    private String upload( HttpServletRequest request ) throws ServletException,IOException {
        final int MAX_KB = 300;    // Max Upload Size in kB

        //

        int    formDataLength = request.getContentLength();
        String contentType    = request.getContentType();
        int    index          = contentType.lastIndexOf( "=" );
        String boundary       = contentType.substring( index + 1 );

        log.fine( "upload - " + formDataLength + " - " + boundary );

        if(( formDataLength / 1024 ) > MAX_KB ) {    // 100k
            return "File too large = " + ( formDataLength / 1024 ) + "kB - Allowed = " + MAX_KB + "kB";
        }

        DataInputStream in             = new DataInputStream( request.getInputStream());
        byte[]          data           = new byte[ formDataLength ];
        int             bytesRead      = 0;
        int             totalBytesRead = 0;

        while( totalBytesRead < formDataLength ) {
            bytesRead      = in.read( data,totalBytesRead,formDataLength );
            totalBytesRead += bytesRead;
        }

        // Convert to String for easy manipulation

        m_requestDataString = new String( data,"ISO-8859-1" );

        if( m_requestDataString.length() != data.length ) {
            return "Internal conversion Error";
        }

        // File Name:
       
        index      = m_requestDataString.indexOf( "filename=\"" );
        m_fileName = m_requestDataString.substring( index + 10 );
        index      = m_fileName.indexOf( '"' );

        if( index < 1 ) {
            return "No File Name";
        }

        m_fileName = m_fileName.substring( 0,index );
        log.fine( "upload - " + m_fileName );

        int posStart = m_requestDataString.indexOf( "filename=\"" );

        posStart = m_requestDataString.indexOf( "\n",posStart ) + 1;    // end of Context-Disposition
        posStart = m_requestDataString.indexOf( "\n",posStart ) + 1;    // end of Content-Type
        posStart = m_requestDataString.indexOf( "\n",posStart ) + 1;    // end of empty line

        int posEnd = m_requestDataString.indexOf( boundary,posStart ) - 4;
        int length = posEnd - posStart;

        //

        log.fine( "uploadFile - Start=" + posStart + ", End=" + posEnd + ", Length=" + length );

        // Final copy

        m_data = new byte[ length ];

        for( int i = 0;i < length;i++ ) {
            m_data[ i ] = data[ posStart + i ];
        }

        return null;
    }    // uploadFile

    /**
     * Descripción de Método
     *
     *
     * @param parameterName
     *
     * @return
     */

    public String getParameter( String parameterName ) {
        if( m_requestDataString == null ) {
            return null;
        }

        String retValue = null;
        String search   = "name=\"" + parameterName + "\"";
        int    index    = m_requestDataString.indexOf( search );

        if( index > 0 ) {
            retValue = m_requestDataString.substring( index );
            retValue = retValue.substring( retValue.indexOf( "\n" ) + 1 );    // eol
            retValue = retValue.substring( retValue.indexOf( "\n" ) + 1 );    // empty line
            retValue = retValue.substring( 0,retValue.indexOf( "\n" ));    // cr
            retValue = retValue.trim();
        } else {
            log.warning( "getParameter Not found - " + parameterName );

            return null;
        }

        log.fine( "getParameter = " + parameterName + "=" + retValue );

        return retValue;
    }    // getMultiPartParameter

    /**
     * Descripción de Método
     *
     *
     * @param parameterName
     *
     * @return
     */

    public int getParameterAsInt( String parameterName ) {
        String result = getParameter( parameterName );

        try {
            if( (result != null) && (result.length() > 0) ) {
                return Integer.parseInt( result );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getParameterAsInt - " + parameterName + "=" + result,e );
        }

        return 0;
    }    // getParameterAsInt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "FileUpload[" );

        if( m_fileName != null ) {
            sb.append( m_fileName );
        }

        if( m_error != null ) {
            sb.append( ";Error=" ).append( m_error );
        }

        if( m_data != null ) {
            sb.append( ";Length=" ).append( m_data.length );
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // FileUpload



/*
 *  @(#)FileUpload.java   02.07.07
 * 
 *  Fin del fichero FileUpload.java
 *  
 *  Versión 2.2
 *
 */
