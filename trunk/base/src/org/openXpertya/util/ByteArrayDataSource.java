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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ByteArrayDataSource implements DataSource {

    /**
     * Constructor de la clase ...
     *
     *
     * @param is
     * @param type
     */

    public ByteArrayDataSource( InputStream is,String type ) {
        m_type = type;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int                   ch;

            while(( ch = is.read()) != -1 ) {

                // XXX - must be made more efficient by
                // doing buffered reads, rather than one byte reads

                os.write( ch );
            }

            m_data = os.toByteArray();
        } catch( IOException ioex ) {
            System.err.println( "ByteArrayDataSource - " + ioex );
        }
    }    // ByteArrayDataSource

    /**
     * Constructor de la clase ...
     *
     *
     * @param data
     * @param type
     */

    public ByteArrayDataSource( byte[] data,String type ) {
        m_data = data;
        m_type = type;
    }    // ByteArrayDataSource

    /**
     * Constructor de la clase ...
     *
     *
     * @param asciiData
     * @param type
     */

    public ByteArrayDataSource( String asciiData,String type ) {
        try {
            m_data = asciiData.getBytes( "iso-8859-1" );
        } catch( UnsupportedEncodingException uex ) {
            System.err.println( "ByteArrayDataSource - " + uex );
        }

        m_type = type;
    }    // ByteArrayDataSource

    /** Descripción de Campos */

    private byte[] m_data = null;

    /** Descripción de Campos */

    private String m_type = "text/plain";

    /** Descripción de Campos */

    private String m_name = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws IOException
     */

    public InputStream getInputStream() throws IOException {
        if( m_data == null ) {
            throw new IOException( "no data" );
        }

        // a new stream must be returned each time.

        return new ByteArrayInputStream( m_data );
    }    // getInputStream

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws IOException
     */

    public OutputStream getOutputStream() throws IOException {
        throw new IOException( "cannot do this" );
    }    // getOutputStream

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getContentType() {
        return m_type;
    }    // getContentType

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @return
     */

    public ByteArrayDataSource setName( String name ) {
        m_name = name;

        return this;
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        if( m_name != null ) {
            return m_name;
        }

        return "ByteArrayDataStream " + m_type;
    }    // getName
}    // ByteArrayDataStream



/*
 *  @(#)ByteArrayDataSource.java   02.07.07
 * 
 *  Fin del fichero ByteArrayDataSource.java
 *  
 *  Versión 2.2
 *
 */
