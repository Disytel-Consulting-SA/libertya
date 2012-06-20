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



package org.openXpertya.apps;

import java.awt.Color;
import java.awt.Cursor;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.compiere.plaf.CompierePLAF;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OnlineHelp extends JEditorPane implements HyperlinkListener {

    /**
     * Constructor de la clase ...
     *
     */

    public OnlineHelp() {
        super();
        setEditable( false );
        setContentType( "text/html" );
        addHyperlinkListener( this );
    }    // OnlineHelp

    /**
     * Constructor de la clase ...
     *
     *
     * @param url
     */

    public OnlineHelp( String url ) {
        this();

        try {
            if( (url != null) && (url.length() > 0) ) {
                setPage( url );
            }
        } catch( Exception e ) {
            System.err.println( "OnlineHelp URL=" + url + " - " + e );
        }
    }    // OnlineHelp

    /**
     * Constructor de la clase ...
     *
     *
     * @param loadOnline
     */

    public OnlineHelp( boolean loadOnline ) {
        this( loadOnline
              ?BASE_URL
              :null );
    }    // OnlineHelp

    /** Descripción de Campos */

    protected static final String BASE_URL = "http://www.libertya.org/loginHelp.html";

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void hyperlinkUpdate( HyperlinkEvent e ) {

        // System.out.println("OnlineHelp.hyperlinkUpdate - " + e.getDescription() + " " + e.getURL());

        if( e.getEventType() != HyperlinkEvent.EventType.ACTIVATED ) {
            return;
        }

        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        if( e instanceof HTMLFrameHyperlinkEvent ) {
            HTMLFrameHyperlinkEvent evt = ( HTMLFrameHyperlinkEvent )e;
            HTMLDocument            doc = ( HTMLDocument )getDocument();

            doc.processHTMLFrameHyperlinkEvent( evt );
        } else if( e.getURL() == null ) {

            // remove # of the reference

            scrollToReference( e.getDescription().substring( 1 ));
        } else {
            try {
                setPage( e.getURL());
            } catch( Throwable t ) {
                System.err.println( "Help.hyperlinkUpdate - " + t.toString());
                displayError( "Error",e.getURL(),t );
            }
        }

        this.setCursor( Cursor.getDefaultCursor());
    }    // hyperlinkUpdate

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setText( String text ) {
        setBackground( CompierePLAF.getInfoBackground());
        super.setText( text );
        setCaretPosition( 0 );    // scroll to top
    }                             // setText

    /**
     * Descripción de Método
     *
     *
     * @param url
     */

    public void setPage( final URL url ) {
        setBackground( Color.white );

        Runnable pgm = new Runnable() {
            public void run() {
                loadPage( url );
            }
        };

        new Thread( pgm ).start();
    }    // setPage

    /**
     * Descripción de Método
     *
     *
     * @param url
     */

    private void loadPage( URL url ) {
        try {
            super.setPage( url );
        } catch( Exception e ) {
            displayError( "Error: URL not found",url,e );
        }
    }    // loadPage

    /**
     * Descripción de Método
     *
     *
     * @param header
     * @param url
     * @param exception
     */

    protected void displayError( String header,Object url,Object exception ) {
        StringBuffer msg = new StringBuffer( "<HTML><BODY>" );

        msg.append( "<H1>" ).append( header ).append( "</H1>" ).append( "<H3>URL=" ).append( url ).append( "</H3>" ).append( "<H3>Error=" ).append( exception ).append( "</H3>" ).append( "<p>&copy;&nbsp;Libertya &nbsp; " ).append( "<A HREF=\"" ).append( BASE_URL ).append( "\">Online Help</A></p>" ).append( "</BODY></HTML>" );
        setText( msg.toString());
    }    // displayError

    /** Descripción de Campos */

    private static HashMap s_links = new HashMap();

    static {
        new Worker( BASE_URL,s_links ).start();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isAvailable() {
        return s_links.size() != 0;
    }    // isAvailable
}    // OnlineHelp


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class Worker extends Thread {

    /**
     * Constructor de la clase ...
     *
     *
     * @param urlString
     * @param links
     */

    Worker( String urlString,HashMap links ) {
        m_urlString = urlString;
        m_links     = links;
        setPriority( Thread.MIN_PRIORITY );
    }    // Worker

    /** Descripción de Campos */

    private String m_urlString = null;

    /** Descripción de Campos */

    private HashMap m_links = null;

    /**
     * Descripción de Método
     *
     */

    public void run() {
        if( m_links == null ) {
            return;
        }

        URL url = null;

        try {
            url = new URL( m_urlString );
        } catch( Exception e ) {
            System.err.println( "OnlineHelp.Worker.run (url) - " + e );
        }

        if( url == null ) {
            return;
        }

        // Read Reference Page

        try {
            URLConnection conn = url.openConnection();
            InputStream   is   = conn.getInputStream();
            HTMLEditorKit kit  = new HTMLEditorKit();
            HTMLDocument  doc  = ( HTMLDocument )kit.createDefaultDocument();

            doc.putProperty( "IgnoreCharsetDirective",new Boolean( true ));
            kit.read( new InputStreamReader( is ),doc,0 );

            // Get The Links to the Help Pages

            HTMLDocument.Iterator it     = doc.getIterator( HTML.Tag.A );
            Object                target = null;
            Object                href   = null;

            while( (it != null) && it.isValid()) {
                AttributeSet as = it.getAttributes();

                // ~ href=/help/100/index.html target=Online title=My Test
                // System.out.println("~ " + as);

                // key keys

                if( (target == null) || (href == null) ) {
                    Enumeration en = as.getAttributeNames();

                    while( en.hasMoreElements()) {
                        Object o = en.nextElement();

                        if( (target == null) && o.toString().equals( "target" )) {
                            target = o;    // javax.swing.text.html.HTML$Attribute
                        } else if( (href == null) && o.toString().equals( "href" )) {
                            href = o;
                        }
                    }
                }

                if( target != null && "Online".equals( as.getAttribute( target ))) {

                    // Format: /help/<AD_Window_ID>/index.html

                    String hrefString = ( String )as.getAttribute( href );

                    if( hrefString != null ) {
                        try {

                            // System.err.println(hrefString);

                            String AD_Window_ID = hrefString.substring( hrefString.indexOf( '/',1 ),hrefString.lastIndexOf( '/' ));

                            m_links.put( AD_Window_ID,hrefString );
                        } catch( Exception e ) {
                            System.err.println( "OnlineHelp.Worker.run (help) - " + e );
                        }
                    }
                }

                it.next();
            }

            is.close();
        } catch( ConnectException e ) {

            // System.err.println("OnlineHelp.Worker.run URL=" + url + " - " + e);

        } catch( UnknownHostException uhe ) {

            // System.err.println("OnlineHelp.Worker.run " + uhe);

        } catch( Exception e ) {
            System.err.println( "OnlineHelp.Worker.run (e) " + e );

            // e.printStackTrace();

        } catch( Throwable t ) {
            System.err.println( "OnlineHelp.Worker.run (t) " + t );

            // t.printStackTrace();

        }

        // System.out.println("OnlineHelp - Links=" + m_links.size());

    }    // run

    /**
     * Descripción de Método
     *
     *
     * @param doc
     * @param tag
     */

    private void dumpTags( HTMLDocument doc,HTML.Tag tag ) {
        System.out.println( "Doc=" + doc.getBase() + ", Tag=" + tag );

        HTMLDocument.Iterator it = doc.getIterator( tag );

        while( (it != null) && it.isValid()) {
            AttributeSet as = it.getAttributes();

            System.out.println( "~ " + as );
            it.next();
        }
    }    // printTags
}    // Worker



/*
 *  @(#)OnlineHelp.java   02.07.07
 * 
 *  Fin del fichero OnlineHelp.java
 *  
 *  Versión 2.2
 *
 */