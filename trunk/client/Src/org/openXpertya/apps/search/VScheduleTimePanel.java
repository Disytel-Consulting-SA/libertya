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



package org.openXpertya.apps.search;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.sql.Timestamp;
import java.util.logging.Level;

import javax.swing.JComponent;

import org.compiere.plaf.CompiereUtils;
import org.openXpertya.model.MAssignmentSlot;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VScheduleTimePanel extends JComponent {

    /**
     * Constructor de la clase ...
     *
     */

    public VScheduleTimePanel() {
        setOpaque( false );
        setSize();
    }

    /** Descripción de Campos */

    private MAssignmentSlot[] m_timeSlots = null;

    /** Descripción de Campos */

    private String[] m_lines = new String[]{ "" };

    /** Descripción de Campos */

    public static int LINE_HEIGHT = 35;

    /** Descripción de Campos */

    public static int HEADING = 25;

    /** Descripción de Campos */

    private Font m_font = new Font( "serif",Font.PLAIN,12 );

    /** Descripción de Campos */

    private int m_width = 120;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VScheduleTimePanel.class );

    /**
     * Descripción de Método
     *
     *
     * @param timeSlots
     */

    public void setTimeSlots( MAssignmentSlot[] timeSlots ) {
        log.fine( "VScheduleTimePanel.setTimeSlots" );
        m_timeSlots = timeSlots;
        m_lines     = new String[ m_timeSlots.length ];

        //

        FontMetrics fm = null;
        Graphics    g  = getGraphics();

        if( g == null ) {
            g = Env.getGraphics( this );
        }

        if( g != null ) {
            fm = g.getFontMetrics( m_font );    // the "correct" way
        } else {
            log.log( Level.SEVERE,"No Graphics" );

            // fm = getToolkit().getFontMetrics(m_font);

        }

        m_width = 0;

        for( int i = 0;i < m_lines.length;i++ ) {
            m_lines[ i ] = m_timeSlots[ i ].getInfoTimeFrom();

            int width = 0;

            if( fm != null ) {
                width = fm.stringWidth( m_lines[ i ] );
            }

            if( width > m_width ) {
                m_width = width;
            }
        }

        setSize();

        // repaint();

    }    // setTimeSlots

    /**
     * Descripción de Método
     *
     */

    private void setSize() {

        // Width

        int width = m_width + 10;    // slack

        if( width <= 10 ) {
            width = 120;    // default size
        }

        // Height

        int height = LINE_HEIGHT;
        int lines  = m_lines.length;

        if( lines < 2 ) {
            height *= 10;    // default
        } else {
            height *= lines;
        }

        height += HEADING;

        //

        Dimension size = new Dimension( width,height );

        setPreferredSize( size );
        setMinimumSize( size );
        setMaximumSize( size );
    }    // setSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getHeaderHeight() {
        return HEADING;
    }    // getHeaderHeight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSlotHeight() {
        int height = getPreferredSize().height;
        int part   = ( height - HEADING ) / m_lines.length;

        return part;
    }    // getSlotHeight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSlotCount() {
        return m_lines.length;
    }    // getSlotCount

    /**
     * Descripción de Método
     *
     *
     * @param slot
     *
     * @return
     */

    public int getSlotYStart( int slot ) {
        int part = getSlotHeight();
        int y    = HEADING + ( slot * part );

        return y;
    }    // getSlotYStart

    /**
     * Descripción de Método
     *
     *
     * @param slot
     *
     * @return
     */

    public int getSlotYEnd( int slot ) {
        int part = getSlotHeight();
        int y    = HEADING + (( slot + 1 ) * part );

        return y;
    }    // getSlotYEnd

    /**
     * Descripción de Método
     *
     *
     * @param time
     * @param endTime
     *
     * @return
     */

    public int getTimeSlotIndex( Timestamp time,boolean endTime ) {

        // Just one slot

        if( m_timeSlots.length <= 1 ) {
            return 0;
        }

        // search for it

        for( int i = 0;i < m_timeSlots.length;i++ ) {
            if( m_timeSlots[ i ].inSlot( time,endTime )) {
                return i;
            }
        }

        log.log( Level.SEVERE,"VScheduleTimePanel.getSlotIndex - did not find Slot for " + time + " end=" + endTime );

        return 0;
    }    // getTimeSlotIndex

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public MAssignmentSlot getTimeSlot( int index ) {
        if( (index < 0) || (index > m_timeSlots.length) ) {
            return null;
        }

        return m_timeSlots[ index ];
    }    // getTimeSlot

    /**
     * Descripción de Método
     *
     *
     * @param yPos
     *
     * @return
     */

    public int getTimeSlotIndex( int yPos ) {
        int index = yPos - getHeaderHeight();

        index /= getSlotHeight();

        if( index < 0 ) {
            return 0;
        }

        if( index >= m_timeSlots.length ) {
            return m_timeSlots.length - 1;
        }

        return index;
    }    // getTimeSlotIndex

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paint( Graphics g ) {

        // log.fine( "VScheduleTimePanel.paint", g.getClip());

        Graphics2D g2D = ( Graphics2D )g;

        g2D.setFont( m_font );

        Dimension size = getPreferredSize();
        int       w    = size.width;
        int       h    = size.height;

        // Paint Background

        g2D.setPaint( Color.white );
        g2D.fill3DRect( 1,1,w - 2,h - 2,true );

        // Header Background

        Rectangle where = new Rectangle( 0,0,w,getHeaderHeight());

        CompiereUtils.paint3Deffect( g2D,where,false,true );

        // heading

        TextLayout layout = null;

        // layout = new TextLayout ("Heading", m_font, g2D.getFontRenderContext());
        // float hh = layout.getAscent() + layout.getDescent();
        // layout.draw (g2D, (w - layout.getAdvance())/2,          //      center
        // ((HEADING - hh)/2) + layout.getAscent());               //      center

        // horizontal lines & text

        g2D.setStroke( getStroke( true ));

        for( int i = 0;i < m_lines.length;i++ ) {
            int yy = getSlotYStart( i );

            if( (m_lines[ i ] != null) && (m_lines[ i ].length() > 0) ) {
                layout = new TextLayout( m_lines[ i ],m_font,g2D.getFontRenderContext());
                g2D.setPaint( Color.blue );
                layout.draw( g2D,w - layout.getAdvance() - 3,    // right aligned with 2 pt space
                    yy + layout.getAscent() + layout.getLeading());    // top aligned with leading space
            }

            //

            g2D.setPaint( Color.gray );
            g2D.drawLine( 2,yy,w - 2,yy );    // top horiz line
        }

        // Paint Borders

        g2D.setPaint( Color.black );
        g2D.setStroke( getStroke( false ));
        g2D.drawLine( 1,1,1,h - 1 );                                   // left
        g2D.drawLine( w - 1,1,w - 1,h - 1 );                           // right
        g2D.drawLine( 1,1,w - 1,1 );                                   // top
        g2D.drawLine( 1,getHeaderHeight(),w - 1,getHeaderHeight());    // header
        g2D.drawLine( 1,h - 1,w - 1,h - 1 );                           // bottom line
    }                                                                  // paintComponent

    /**
     * Descripción de Método
     *
     *
     * @param slotLine
     *
     * @return
     */

    public static Stroke getStroke( boolean slotLine ) {
        if( slotLine ) {
            return new BasicStroke( 1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f,new float[]{ 2.0f,0.5f },0.0f );
        }

        return new BasicStroke( 1.0f );
    }    // getStroke
}    // VScheduleTimePanel



/*
 *  @(#)VScheduleTimePanel.java   02.07.07
 * 
 *  Fin del fichero VScheduleTimePanel.java
 *  
 *  Versión 2.2
 *
 */
