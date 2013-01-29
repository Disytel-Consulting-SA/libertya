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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.ecs.AlignType;
import org.apache.ecs.Element;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.h1;
import org.apache.ecs.xhtml.head;
import org.apache.ecs.xhtml.html;
import org.apache.ecs.xhtml.img;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.link;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.title;
import org.apache.ecs.xhtml.tr;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebDoc {

    /**
     * Descripción de Método
     *
     *
     * @param plain
     * @param title
     * @param javaClient
     *
     * @return
     */

    public static WebDoc create( boolean plain,String title,boolean javaClient ) {
        WebDoc doc = new WebDoc();

        doc.setUp( plain,javaClient,title );

        return doc;
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @param plain
     *
     * @return
     */

    public static WebDoc create( boolean plain ) {
        return create( plain,null,false );
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @param title
     *
     * @return
     */

    public static WebDoc createPopup( String title ) {
        WebDoc doc = create( title );

        doc.getHead().addElement( new script(( Element )null,"window.js" ));
        doc.getHead().addElement( new link( "popup.css",link.REL_STYLESHEET,link.TYPE_CSS ));
        doc.setClasses( "popupTable","popupHeader" );
        doc.getTable().setCellSpacing( 5 );

        return doc;
    }    // createPopup

    /**
     * Descripción de Método
     *
     *
     * @param title
     *
     * @return
     */

    public static WebDoc createWindow( String title ) {
        WebDoc doc = create( title );

        doc.getHead().addElement( new link( "window.css",link.REL_STYLESHEET,link.TYPE_CSS ));
        doc.getHead().addElement( new script(( Element )null,"window.js" ));
        doc.setClasses( "windowTable","windowHeader" );
        doc.getTable().setCellSpacing( 5 );

        return doc;
    }    // createWindow

    /**
     * Descripción de Método
     *
     *
     * @param title
     *
     * @return
     */

    public static WebDoc create( String title ) {
        return create( false,title,false );
    }    // create

    /** Descripción de Campos */

    public static final String NBSP = "&nbsp;";

    /**
     * Constructor de la clase ...
     *
     */

    private WebDoc() {}    // WDoc

    /** Descripción de Campos */

    private html m_html = new html();

    /** Descripción de Campos */

    private head m_head = new head();

    /** Descripción de Campos */

    private body m_body = new body();

    /** Descripción de Campos */

    private table m_table = null;

    /** Descripción de Campos */

    private tr m_topRow = null;

    /** Descripción de Campos */

    private td m_topRight = null;

    /** Descripción de Campos */

    private td m_topLeft = null;

    /**
     * Descripción de Método
     *
     *
     * @param plain
     * @param javaClient
     * @param title
     */

    private void setUp( boolean plain,boolean javaClient,String title ) {
        m_html.addElement( m_head );
        m_html.addElement( m_body );
        m_body.addElement( new a().setName( "top" ));

        if( title != null ) {
            m_head.addElement( new title( title ));
        }

        if( plain ) {
            return;
        }

        // css, js

        if( javaClient ) {
            m_head.addElement( new link( "http://www.openxpertya.org/apps/2_0/standard.css",link.REL_STYLESHEET,link.TYPE_CSS ));
        } else {
            m_head.addElement( new link( WebEnv.getStylesheetURL(),link.REL_STYLESHEET,link.TYPE_CSS ));
            m_head.addElement( new script(( Element )null,WebEnv.getBaseDirectory( "standard.js" )));
        }

        m_table  = new table( "0","2","0","100%",null );    // spacing 2
        m_topRow = new tr();

        // Title

        m_topLeft = new td();

        if( title == null ) {
            m_topLeft.addElement( NBSP );
        } else {
            m_topLeft.addElement( new h1( title ));
        }

        m_topRow.addElement( m_topLeft );

        // Logo

        m_topRight = new td().setAlign( "right" );

        if( javaClient ) {
            //m_topRight.addElement( new img( "http://www.openxpertya.org/apps/2_0/logo.png" )

            // Changing the copyright notice in any way violates the license
            // and you'll be held liable for any damage claims

            //.setAlign( AlignType.RIGHT ).setAlt( "&copy; FUNDESLE/OpenXpertya" ));
        } else {
            m_topRight.addElement( WebEnv.getLogo());
        }

        m_topRow.addElement( m_topRight );
        m_table.addElement( m_topRow );

        //

        m_body.addElement( m_table );
    }    // setUp

    /**
     * Descripción de Método
     *
     *
     * @param tableClass
     * @param tdClass
     */

    public void setClasses( String tableClass,String tdClass ) {
        if( (m_table != null) && (tableClass != null) ) {
            m_table.setClass( tableClass );
        }

        if( (m_topLeft != null) && (tdClass != null) ) {
            m_topLeft.setClass( tdClass );
        }

        if( (m_topRight != null) && (tdClass != null) ) {
            m_topRight.setClass( tdClass );
        }
    }    // setClasses

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public body getBody() {
        return m_body;
    }    // getBody

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public head getHead() {
        return m_head;
    }    // getHead

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public table getTable() {
        return m_table;
    }    // getTable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public tr getTopRow() {
        return m_topRow;
    }    // getTopRow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td getTopLeft() {
        return m_topLeft;
    }    // getTopLeft

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td getTopRight() {
        return m_topRight;
    }    // getTopRight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return m_html.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param out
     */

    public void output( OutputStream out ) {
        m_html.output( out );
    }    // output

    /**
     * Descripción de Método
     *
     *
     * @param out
     */

    public void output( PrintWriter out ) {
        m_html.output( out );
    }    // output

    /**
     * Descripción de Método
     *
     *
     * @param nowrap
     *
     * @return
     */

    public td addPopupCenter( boolean nowrap ) {
        if( m_table == null ) {
            return null;
        }

        //

        td center = new td( "popupCenter",AlignType.CENTER,AlignType.MIDDLE,nowrap );

        center.setColSpan( 2 );
        m_table.addElement( new tr().addElement( center ));

        return center;
    }    // addPopupCenter

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td[] addPopupClose() {
        input button = WebUtil.createClosePopupButton();

        if( m_table == null ) {
            m_body.addElement( button );

            return null;
        }

        //

        td left = new td( "popupFooter",AlignType.LEFT,AlignType.MIDDLE,false,null );
        td right = new td( "popupFooter",AlignType.RIGHT,AlignType.MIDDLE,false,button );

        m_table.addElement( new tr().addElement( left ).addElement( right ));

        return new td[]{ left,right };
    }    // addPopupClose

    /**
     * Descripción de Método
     *
     *
     * @param nowrap
     *
     * @return
     */

    public td addWindowCenter( boolean nowrap ) {
        if( m_table == null ) {
            return null;
        }

        //

        td center = new td( "windowCenter",AlignType.CENTER,AlignType.MIDDLE,nowrap );

        center.setColSpan( 2 );
        m_table.addElement( new tr().addElement( center ));

        return center;
    }    // addWindowCenter

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td[] addWindowFooters() {
        if( m_table == null ) {
            return null;
        }

        //

        td left = new td( "windowFooter",AlignType.LEFT,AlignType.MIDDLE,false );
        td right = new td( "windowFooter",AlignType.RIGHT,AlignType.MIDDLE,false );

        m_table.addElement( new tr().addElement( left ).addElement( right ));

        return new td[]{ left,right };
    }    // addWindowFooters

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public td addWindowFooter() {
        if( m_table == null ) {
            return null;
        }

        //

        td center = new td( "windowFooter",AlignType.CENTER,AlignType.MIDDLE,false );

        m_table.addElement( new tr().addElement( center ));

        return center;
    }    // addWindowFooter

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        WebDoc doc = WebDoc.create( "Test" );

        System.out.println( doc.toString());
        System.out.println( "---------" );
        doc.output( System.out );
    }    // main
    
    /**
	 * 	Add Popup Close Footer
	 *	@return null or array with left/right td
	 */
	public td[] addPopupClose(Properties ctx)
	{
		input button = WebUtil.createClosePopupButton(ctx); 
		if (m_table == null)
		{
			m_body.addElement(button);
			return null;
		}
		//
		td left = new td("popupFooter", AlignType.LEFT, AlignType.MIDDLE, false, null);
		td right = new td("popupFooter", AlignType.RIGHT, AlignType.MIDDLE, false, button); 
		m_table.addElement(new tr()
			.addElement(left)
			.addElement(right));
		return new td[] {left, right};
	}	//	addPopupClose

}    // WDoc



/*
 *  @(#)WebDoc.java   02.07.07
 * 
 *  Fin del fichero WebDoc.java
 *  
 *  Versión 2.2
 *
 */