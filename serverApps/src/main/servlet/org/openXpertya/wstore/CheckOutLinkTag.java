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

import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.ecs.xhtml.*;

import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class CheckOutLinkTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private boolean m_oneLine = false;

    /**
     * Descripción de Método
     *
     *
     * @param var
     */

    public void setOneLine( String var ) {
        m_oneLine = "Y".equals( var );
    }    // setOneLine

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doStartTag() throws JspException {
        HttpSession        session = pageContext.getSession();
        HttpServletRequest request = ( HttpServletRequest )pageContext.getRequest();
        WebBasket wb = ( WebBasket )session.getAttribute( WebBasket.NAME );

        // log.fine("doStartTag - WebBasket=" + wb);

        if( (wb != null) && (wb.getLineCount() > 0) ) {
            log.fine( "doStartTag - WebBasket exists" );

            //

            JspWriter out  = pageContext.getOut();
            HtmlCode  html = new HtmlCode();

            //

            div div = new div();
            div basket = new div();
            
            img img = new img( "cart.png" );

            img.setAlt( "Ver cesta" );
            img.setTitle( "Ver cesta" );
            img.setAlign( "absmiddle" );
            img.setBorder( 0 );

            a a = new a( "basket.jsp" );

            if( m_oneLine ) {
                a.addElement( img );
                a.addElement( "Ver cesta" );
                a.addAttribute( "title", "Ver cesta" );
                html.addElement( a );
                html.addElement( "&nbsp;- " );
            } else {
                // List Content
               
                ArrayList lines = wb.getLines();
           
                for( int i = 0;i < lines.size();i++ ) {
                    Object line = lines.get( i );

                    div = new div();
                    div.addElement( line.toString());
                    div.setClass( "basketline" );
                    
                    basket.addElement( div );
                }
                            
            	a.addElement( img );
                a.addElement( "Ver cesta" );
                a.addAttribute( "title", "Ver cesta" );
                
                div = new div();
                div.addElement( a );
                div.setClass( "basketbutton" );
                              
                basket.addElement( div );
            }

            img = new img( "checkout.png" );
            img.setAlt( "Confirmar pedido" );
            img.setTitle( "Confirmar pedido" );
            img.setAlign( "absmiddle" );
            img.setBorder( 0 );

            String url = CheckOutServlet.NAME;

            if( !request.isSecure()) {
                url = "https://" + request.getServerName() + request.getContextPath() + "/" + CheckOutServlet.NAME;
            }

            a = new a( url );
            a.addElement( img );
            a.addElement( "Confirmar pedido" );
            a.addAttribute( "title", "Confirmar pedido" );

            div = new div();
            div.addElement( a );
            div.setClass( "basketbutton" );
            
            basket.addElement( div );
            basket.setID( "basketmenu" );
            
            html.addElement( basket );

            //

            /*
            if( !m_oneLine ) {
                html.addElement( new hr( "100%","left" ));
            }
            */

            //

            html.output( out );
        }

        return( SKIP_BODY );
    }    // doStartTag

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }    // doEndTag
}    // CheckOutLinkTag



/*
 *  @(#)CheckOutLinkTag.java   12.10.07
 *
 *  Fin del fichero CheckOutLinkTag.java
 *
 *  Versión 2.2
 *
 */
