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



package org.openXpertya.impexp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class OFX1ToXML extends InputStream implements Runnable {

    /** Descripción de Campos */

    private PipedReader m_reader = new PipedReader();

    /** Descripción de Campos */

    private BufferedWriter m_writer;

    /** Descripción de Campos */

    private String m_ofx = "";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Constructor de la clase ...
     *
     *
     * @param is
     *
     * @throws IOException
     */

    public OFX1ToXML( InputStream is ) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader( is ));

        init( br );
    }    // OFX1ToXML

    /**
     * Constructor de la clase ...
     *
     *
     * @param br
     *
     * @throws IOException
     */

    public OFX1ToXML( BufferedReader br ) throws IOException {
        init( br );
    }    // OFX1ToXML

    /**
     * Descripción de Método
     *
     *
     * @param br
     *
     * @throws IOException
     */

    public void init( BufferedReader br ) throws IOException {
        m_writer = new BufferedWriter( new PipedWriter( m_reader ));

        String line = br.readLine();

        write( "<?xml version=\"1.0\"?>\n" );
        write( "<?OFX " );

        while( line.indexOf( "<" ) != 0 ) {
            if( line.length() > 0 ) {
                write( line.replaceAll( ":","=\"" ) + "\" " );
            }

            line = br.readLine();
        }

        write( "?>\n" );

        while( line != null ) {
            m_ofx += line + "\n";
            line  = br.readLine();
        }

        br.close();
        new Thread( this ).start();
    }    // i     nit

    /**
     * Descripción de Método
     *
     */

    public void run() {
        boolean addCloseTag;
        int     tag2Start;
        int     tagStart;
        int     tagEnd;
        String  tag;
        String  line = "";

        try {
            while( m_ofx != "" ) {
                addCloseTag = false;
                tagStart    = m_ofx.indexOf( "<" );

                if( tagStart == -1 ) {
                    break;
                }

                tagEnd = m_ofx.indexOf( ">" );

                if( tagEnd <= tagStart + 1 ) {
                    throw new IOException( "PARSE ERROR: Invalid tag" );
                }

                tag = m_ofx.substring( tagStart + 1,tagEnd );

                if( tag.indexOf( " " ) != -1 ) {
                    throw new IOException( "PARSE ERROR: Invalid tag" );
                }

                if( !tag.startsWith( "/" )) {
                    addCloseTag = ( m_ofx.indexOf( "</" + tag + ">" ) == -1 );
                }

                tag2Start = m_ofx.indexOf( "<",tagEnd );

                if( m_ofx.indexOf( "\n",tagEnd ) < tag2Start ) {
                    tag2Start = m_ofx.indexOf( "\n",tagEnd );
                }

                if( tag2Start == -1 ) {
                    tag2Start = m_ofx.length();
                }

                line  = m_ofx.substring( 0,tag2Start );
                m_ofx = m_ofx.substring( tag2Start );

                if( addCloseTag ) {
                    line += "</" + tag + ">";
                }

                write( line );
            }

            write( m_ofx );
            m_writer.close();
        } catch( IOException e ) {
            log.log( Level.SEVERE,"Ofx1To2Convertor: IO Exception",e );
        }
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @param str
     *
     * @throws IOException
     */

    private void write( String str ) throws IOException {
        m_writer.write( str,0,str.length());
    }    // write

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws IOException
     */

    public int read() throws IOException {
        return m_reader.read();
    }    // read

    /**
     * Descripción de Método
     *
     *
     * @param cbuf
     * @param off
     * @param len
     *
     * @return
     *
     * @throws IOException
     */

    public int read( char[] cbuf,int off,int len ) throws IOException {
        return m_reader.read( cbuf,off,len );
    }    // read
}    // Ofx1To2Convertor



/*
 *  @(#)OFX1ToXML.java   02.07.07
 * 
 *  Fin del fichero OFX1ToXML.java
 *  
 *  Versión 2.2
 *
 */
