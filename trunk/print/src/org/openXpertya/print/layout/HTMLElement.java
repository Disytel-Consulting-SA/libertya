/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.print.layout;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Properties;

//import org.openXpertya.model.*;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class HTMLElement extends PrintElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param html
     */

    public HTMLElement( String html ) {
        if( (html == null) || html.equals( "" )) {
            throw new IllegalArgumentException( "HTMLElement is null" );
        }

        log.fine( "Length=" + html.length());

        // Create View

        m_renderer = HTMLRenderer.get( html );
    }    // HTMLElement

    /** Descripci�n de Campos */

    private HTMLRenderer m_renderer;

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        if( p_sizeCalculated ) {
            return true;
        }

        //

        p_height = m_renderer.getHeight();
        p_width  = m_renderer.getWidth();

        // Limits

        if( p_maxWidth != 0f ) {
            p_width = p_maxWidth;
        }

        if( p_maxHeight != 0f ) {
            if( p_maxHeight == -1f ) {    // one line only
                p_height = m_renderer.getHeightOneLine();
            } else {
                p_height = p_maxHeight;
            }
        }

        // System.out.println("HTMLElement.calculate size - Width="
        // + p_width + "(" + p_maxWidth + ") - Height=" + p_height + "(" + p_maxHeight + ")");
        //

        m_renderer.setAllocation(( int )p_width,( int )p_height );

        return true;
    }    // calculateSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @param g2D
     * @param pageNo
     * @param pageStart
     * @param ctx
     * @param isView
     */

    public void paint( Graphics2D g2D,int pageNo,Point2D pageStart,Properties ctx,boolean isView ) {

        // 36.0/137.015625, Clip=java.awt.Rectangle[x=0,y=0,width=639,height=804], Translate=1.0/56.0, Scale=1.0/1.0, Shear=0.0/0.0
        // log.finest( "HTMLElement.paint", p_pageLocation.x + "/" + p_pageLocation.y
        // + ", Clip=" + g2D.getClip()
        // + ", Translate=" + g2D.getTransform().getTranslateX() + "/" + g2D.getTransform().getTranslateY()
        // + ", Scale=" + g2D.getTransform().getScaleX() + "/" + g2D.getTransform().getScaleY()
        // + ", Shear=" + g2D.getTransform().getShearX() + "/" + g2D.getTransform().getShearY());
        //

        Point2D.Double location = getAbsoluteLocation( pageStart );

        // log.finest( "HTMLElement.paint - PageStart=" + pageStart + ", Location=" + location);
        //

        Rectangle allocation = m_renderer.getAllocation();

        g2D.translate( location.x,location.y );
        m_renderer.paint( g2D,allocation );
        g2D.translate( -location.x,-location.y );
    }    // paint

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "HTMLElement[" );

        sb.append( "Bounds=" ).append( getBounds()).append( ",Height=" ).append( p_height ).append( "(" ).append( p_maxHeight ).append( "),Width=" ).append( p_width ).append( "(" ).append( p_maxHeight ).append( "),PageLocation=" ).append( p_pageLocation ).append( " - " );
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripci�n de M�todo
     *
     *
     * @param content
     *
     * @return
     */

    public static boolean isHTML( Object content ) {
        if( content == null ) {
            return false;
        }

        String s = content.toString();

        if( s.length() < 20 ) {    // assumption
            return false;
        }

        s = s.trim().toUpperCase();

        if( s.startsWith( "<HTML>" )) {
            return true;
        }

        return false;
    }    // isHTML
}    // HTMLElement



/*
 *  @(#)HTMLElement.java   12.10.07
 * 
 *  Fin del fichero HTMLElement.java
 *  
 *  Versión 2.2
 *
 */
