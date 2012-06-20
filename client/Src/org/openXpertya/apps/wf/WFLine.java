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



package org.openXpertya.apps.wf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.font.TextLayout;

import javax.swing.SwingConstants;

import org.openXpertya.util.CLogger;
import org.openXpertya.wf.MWFNodeNext;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFLine extends Component {

    /**
     * Constructor de la clase ...
     *
     *
     * @param next
     */

    public WFLine( MWFNodeNext next ) {
        m_next = next;

        // setOpaque(false);

        setFocusable( false );

        //

        m_description = next.getDescription();

        if( (m_description != null) && (m_description.length() > 0) ) {
            m_description = "{" + String.valueOf( next.getSeqNo()) + ": " + m_description + "}";
        }
    }    // WFLine

    /** Descripción de Campos */

    private MWFNodeNext m_next = null;

    /** Descripción de Campos */

    private Rectangle m_from = null;

    /** Descripción de Campos */

    private Rectangle m_to = null;

    /** Descripción de Campos */

    private String m_description = null;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Rectangle getFrom() {
        return m_from;
    }    // getFrom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Rectangle getTo() {
        return m_to;
    }    // getTo

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     */

    public void setFromTo( Rectangle from,Rectangle to ) {
        m_from = from;
        m_to   = to;
    }    // setFrom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_WF_Node_ID() {
        return m_next.getAD_WF_Node_ID();    // Node ->
    }                                        // getAD_WF_Node_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_WF_Next_ID() {
        return m_next.getAD_WF_Next_ID();    // -> Next
    }                                        // getAD_WF_Next_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isRightTop()    // \
    {
        return( (m_from.x + m_from.width <= m_to.x    // right.bottom - left.top
            ) && (m_from.y + m_from.height <= m_to.y) );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isBottomTop()    // |
    {
        return( m_from.y + m_from.height <= m_to.y );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isTopBottom()    // |
    {
        return( m_to.y + m_to.height <= m_from.y );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isLeftRight()    // ->
    {
        return( m_to.x + m_to.width <= m_from.x );
    }

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paint( Graphics g ) {
        if( (m_from == null) || (m_to == null) ) {
            return;
        }

        Polygon arrow = new Polygon();
        Point   from  = null;
        Point   to    = null;

        //

        if( isRightTop()) {
            from = addPoint( arrow,m_from,SwingConstants.RIGHT,true );
            to   = addPoint( arrow,m_to,SwingConstants.TOP,false );
        } else if( isBottomTop()) {
            from = addPoint( arrow,m_from,SwingConstants.BOTTOM,true );
            to   = addPoint( arrow,m_to,SwingConstants.TOP,false );
        }

        //

        else if( isTopBottom()) {
            from = addPoint( arrow,m_from,SwingConstants.TOP,true );
            to   = addPoint( arrow,m_to,SwingConstants.BOTTOM,false );
        } else if( isLeftRight()) {
            from = addPoint( arrow,m_from,SwingConstants.LEFT,true );
            to   = addPoint( arrow,m_to,SwingConstants.RIGHT,false );
        } else    // if (isRightLeft())
        {
            from = addPoint( arrow,m_from,SwingConstants.RIGHT,true );
            to   = addPoint( arrow,m_to,SwingConstants.LEFT,false );
        }

        // Paint Arrow

        g.setColor( Color.cyan );
        g.fillPolygon( arrow );

        if( m_next.isUnconditional()) {
            g.setColor( Color.blue );
        } else {
            g.setColor( Color.red );
        }

        g.drawPolygon( arrow );

        // Paint Dot for AND From

        if( m_next.isFromSplitAnd()) {
            g.setColor( Color.magenta );
            g.fillOval( from.x - 3,from.y - 3,6,6 );
        }

        // Paint Dot for AND To

        if( m_next.isToJoinAnd()) {
            g.setColor( Color.magenta );
            g.fillOval( to.x - 3,to.y - 3,6,6 );
        }

        // Paint Description in red

        if( m_description != null ) {
            Graphics2D g2D  = ( Graphics2D )g;
            Font       font = new Font( "Dialog",Font.PLAIN,9 );

            g2D.setColor( Color.red );

            TextLayout layout = new TextLayout( m_description,font,g2D.getFontRenderContext());

            // Mid Point

            int x = 0;

            if( from.x < to.x ) {
                x = from.x + (( to.x - from.x ) / 2 );
            } else {
                x = to.x + (( from.x - to.x ) / 2 );
            }

            int y = 0;

            if( from.y < to.y ) {
                y = from.y + (( to.y - from.y ) / 2 );
            } else {
                y = to.y + (( from.y - to.y ) / 2 );
            }

            // Adjust |

            if( Math.abs( from.y - to.y ) > 3 ) {
                y -= ( layout.getAscent() / 2 );    // exact center
            }

            // Adjust -

            x -= ( layout.getAdvance() / 2 );    // center

            if( x < 2 ) {
                x = 2;
            }

            layout.draw( g2D,x,y );
        }
    }                                            // paintComponent

    /**
     * Descripción de Método
     *
     *
     * @param arrow
     * @param rect
     * @param pos
     * @param from
     *
     * @return
     */

    private Point addPoint( Polygon arrow,Rectangle rect,int pos,boolean from ) {
        int   x     = rect.x;
        int   y     = rect.y;
        Point point = null;

        if( pos == SwingConstants.TOP ) {
            x += rect.width / 2;

            if( from ) {
                arrow.addPoint( x - 2,y );
                arrow.addPoint( x + 2,y );
            } else {
                arrow.addPoint( x,y );
            }

            point = new Point( x,y - 2 );
        } else if( pos == SwingConstants.RIGHT ) {
            x += rect.width;
            y += rect.height / 2;

            if( from ) {
                arrow.addPoint( x,y - 2 );
                arrow.addPoint( x,y + 2 );
            } else {
                arrow.addPoint( x,y );
            }

            point = new Point( x + 2,y );
        } else if( pos == SwingConstants.LEFT ) {
            y += rect.height / 2;

            if( from ) {
                arrow.addPoint( x,y - 2 );
                arrow.addPoint( x,y + 2 );
            } else {
                arrow.addPoint( x,y );
            }

            point = new Point( x - 2,y );
        } else    // if (pos == SwingConstants.BOTTOM)
        {
            x += rect.width / 2;
            y += rect.height;

            if( from ) {
                arrow.addPoint( x - 2,y );
                arrow.addPoint( x + 2,y );
            } else {
                arrow.addPoint( x,y );
            }

            point = new Point( x,y + 2 );
        }

        return point;
    }    // getPoint

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WFLine[" );

        sb.append( getAD_WF_Node_ID()).append( "->" ).append( getAD_WF_Next_ID());
        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // WFLine



/*
 *  @(#)WFLine.java   02.07.07
 * 
 *  Fin del fichero WFLine.java
 *  
 *  Versión 2.2
 *
 */
