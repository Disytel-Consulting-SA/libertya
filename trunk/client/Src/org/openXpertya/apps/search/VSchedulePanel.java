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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.swing.JComponent;

import org.compiere.plaf.CompiereUtils;
import org.openXpertya.grid.ed.VAssignmentDialog;
import org.openXpertya.model.MAssignmentSlot;
import org.openXpertya.model.MResourceAssignment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VSchedulePanel extends JComponent implements MouseListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VSchedulePanel() {
        setHeight( 250 );
        addMouseListener( this );
    }    // VSchedulePanel

    /** Descripción de Campos */

    private int m_noDays = 1;

    /** Descripción de Campos */

    private int m_height = 250;

    /** Descripción de Campos */

    private int m_dayWidth = 170;

    /** Descripción de Campos */

    private VScheduleTimePanel m_timePanel = null;

    /** Descripción de Campos */

    private MAssignmentSlot[] m_slots = null;

    /** Descripción de Campos */

    private Rectangle[] m_where = null;

    /** Descripción de Campos */

    private Timestamp m_startDate = null;

    /** Descripción de Campos */

    private boolean m_createNew = false;

    /** Descripción de Campos */

    private int m_S_Resource_ID = 0;

    /** Descripción de Campos */

    private InfoSchedule m_infoSchedule = null;

    /** Descripción de Campos */

    private static final int MARGIN = 2;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VSchedulePanel.class );

    /**
     * Descripción de Método
     *
     *
     * @param type
     */

    public void setType( int type ) {
        if( type == VSchedule.TYPE_MONTH ) {
            m_noDays = 31;
        } else if( type == VSchedule.TYPE_WEEK ) {
            m_noDays = 7;
        } else {
            m_noDays = 1;
        }

        setSize();
    }    // setType

    /**
     * Descripción de Método
     *
     *
     * @param is
     */

    public void setInfoSchedule( InfoSchedule is ) {
        m_infoSchedule = is;
    }

    /**
     * Descripción de Método
     *
     *
     * @param createNew
     */

    public void setCreateNew( boolean createNew ) {
        m_createNew = createNew;
    }    // setCreateNew

    /**
     * Descripción de Método
     *
     *
     * @param height
     */

    public void setHeight( int height ) {
        m_height = height;
        setSize();
    }    // setHeight

    /**
     * Descripción de Método
     *
     */

    public void setSize() {

        // width

        FontMetrics fm = null;
        Graphics    g  = getGraphics();

        if( g == null ) {
            g = Env.getGraphics( this );
        }

        if( g != null ) {
            fm = g.getFontMetrics( g.getFont());    // the "correct" way
        }

        m_dayWidth = 0;

        for( int i = 0;i < m_noDays;i++ ) {
            if( fm != null ) {
                String string = getHeading( i );
                int    width  = fm.stringWidth( string );

                if( width > m_dayWidth ) {
                    m_dayWidth = width;
                }
            }
        }

        m_dayWidth += 20;

        if( m_dayWidth < 180 ) {    // minimum width
            m_dayWidth = 180;
        }

        int w = m_noDays * m_dayWidth;

        //

        Dimension size = new Dimension( w,m_height );

        setPreferredSize( size );
        setMinimumSize( size );
        setMaximumSize( size );
    }    // setHeight

    /**
     * Descripción de Método
     *
     *
     * @param timePanel
     */

    public void setTimePanel( VScheduleTimePanel timePanel ) {
        m_timePanel = timePanel;
    }    // setTimePanel

    /**
     * Descripción de Método
     *
     *
     * @param mass
     * @param S_Resource_ID
     * @param startDate
     * @param endDate
     */

    public void setAssignmentSlots( MAssignmentSlot[] mass,int S_Resource_ID,Timestamp startDate,Timestamp endDate ) {
        log.fine( "S_Resource_ID=" + S_Resource_ID );
        m_S_Resource_ID = S_Resource_ID;
        m_noDays        = TimeUtil.getDaysBetween( startDate,endDate );
        m_startDate     = startDate;

        //

        m_slots = mass;
        m_where = new Rectangle[ m_slots.length ];

        //
        // Calculate Assignments

        for( int i = 0;(m_slots != null) && (i < m_slots.length);i++ ) {
            MAssignmentSlot mas      = m_slots[ i ];
            int             dayIndex = TimeUtil.getDaysBetween( m_startDate,mas.getStartTime());

            if( (dayIndex < 0) || (dayIndex >= m_noDays) ) {
                System.out.println( "Assignment " + i + " Invalid DateRange " + mas.getInfo());
            }

            //

            int xWidth = m_dayWidth / mas.getXMax();
            int xStart = dayIndex * m_dayWidth;    // start day slot

            xStart += mas.getXPos() * xWidth;      // offset

            int xEnd   = xStart + xWidth;
            int yStart = m_timePanel.getSlotYStart( mas.getYStart());
            int yEnd   = m_timePanel.getSlotYEnd( mas.getYEnd());

            // System.out.println("Assignment " + i + ", Xpos=" + mas.getXPos() + ", Xmax=" + mas.getXMax()
            // + ", Ystart=" + mas.getYStart() + ", Yend=" + mas.getYEnd() + " " + mas.getInfo());

            m_where[ i ] = new Rectangle( xStart + 1,yStart + 1,xWidth - 1,yEnd - yStart - 1 );
        }    // calculate assignments

        //

        setSize();
        repaint();
    }    // setAssignmentSlots

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paint( Graphics g ) {

        // log.fine( "VSchedulePanel.paint", g.getClip());

        Graphics2D g2D        = ( Graphics2D )g;
        Dimension  size       = getPreferredSize();
        Rectangle  clipBounds = g2D.getClipBounds();
        int        w          = size.width;
        int        h          = size.height;

        // Paint Background

        g2D.setPaint( Color.white );
        g2D.fill3DRect( 1,1,w - 2,h - 2,true );

        if( m_timePanel == null ) {    // required
            return;
        }

        int headerHeight = m_timePanel.getHeaderHeight();

        // horizontal lines -

        g2D.setStroke( VScheduleTimePanel.getStroke( true ));

        for( int i = 1;i < m_timePanel.getSlotCount();i++ ) {
            g2D.setPaint( Color.gray );

            int yy = m_timePanel.getSlotYStart( i );

            g2D.drawLine( 1,yy,w - 2,yy );    // top horiz line
        }

        // heading and right vertical lines |

        g2D.setStroke( VScheduleTimePanel.getStroke( false ));

        for( int i = 0;i < m_noDays;i++ ) {
            Rectangle where = new Rectangle( i * m_dayWidth,0,m_dayWidth,headerHeight );

            if( !where.intersects( clipBounds )) {
                continue;
            }

            // Header Background

            CompiereUtils.paint3Deffect( g2D,where,false,true );
            g2D.setPaint( Color.blue );

            TextLayout layout = new TextLayout( getHeading( i ),g2D.getFont(),g2D.getFontRenderContext());
            float hh = layout.getAscent() + layout.getDescent();

            layout.draw( g2D,where.x + ( m_dayWidth - layout.getAdvance()) / 2,    // center
                (( where.height - hh ) / 2 ) + layout.getAscent());    // center

            // line

            g2D.setPaint( Color.black );

            int xEnd = ( i + 1 ) * m_dayWidth;

            g2D.drawLine( xEnd,1,xEnd,h - 1 );    // right part
        }

        // Paint Assignments

        for( int i = 0;(m_slots != null) && (i < m_slots.length);i++ ) {
            if( !m_where[ i ].intersects( clipBounds )) {
                continue;
            }

            // Background

            g2D.setColor( m_slots[ i ].getColor( true ));
            g2D.fill( m_where[ i ] );

            // Text

            String           string = m_slots[ i ].getInfoNameDescription();
            AttributedString as     = new AttributedString( string );

            as.addAttribute( TextAttribute.FONT,g2D.getFont());
            as.addAttribute( TextAttribute.FOREGROUND,m_slots[ i ].getColor( false ));

            // Italics for Description

            int startIt = string.indexOf( '(' );
            int endIt   = string.lastIndexOf( ')' );

            if( (startIt != -1) && (endIt != -1) ) {
                as.addAttribute( TextAttribute.POSTURE,TextAttribute.POSTURE_OBLIQUE,startIt,endIt );
            }

            // Paint multiple lines if required

            AttributedCharacterIterator aci      = as.getIterator();
            LineBreakMeasurer           measurer = new LineBreakMeasurer( aci,g2D.getFontRenderContext());
            float      wrappingWidth = m_where[ i ].width - ( 2 * MARGIN );
            float      curY          = m_where[ i ].y + MARGIN;
            TextLayout layout        = null;
            int        yEnd          = m_where[ i ].y + m_where[ i ].height;

            while( (measurer.getPosition() < aci.getEndIndex()) && (curY < yEnd) ) {
                layout = measurer.nextLayout( wrappingWidth );
                curY   += layout.getAscent();

                if( curY < yEnd ) {
                    layout.draw( g2D,m_where[ i ].x + MARGIN,curY );
                }
            }
        }    // assignments

        // Paint Borders

        g2D.setPaint( Color.black );
        g2D.setStroke( VScheduleTimePanel.getStroke( false ));
        g2D.drawLine( 1,1,1,h - 1 );    // left
        g2D.drawLine( 1,1,w - 1,1 );    // top

        // heading line

        g2D.drawLine( 1,headerHeight,w - 1,headerHeight );

        // Final lines

        g2D.setStroke( VScheduleTimePanel.getStroke( false ));
        g2D.drawLine( w - 1,1,w - 1,h - 1 );    // right
        g2D.drawLine( 1,h - 1,w - 1,h - 1 );    // bottom line
    }                                           // paint

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    private String getHeading( int index ) {
        GregorianCalendar cal = new GregorianCalendar();

        if( m_startDate != null ) {
            cal.setTime( m_startDate );
        }

        cal.add( java.util.Calendar.DAY_OF_YEAR,index );

        //

        SimpleDateFormat format = ( SimpleDateFormat )DateFormat.getDateInstance( DateFormat.FULL,Language.getLoginLanguage().getLocale());

        return format.format( cal.getTime());
    }    // getHeading

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        if( e.getClickCount() < 2 ) {
            return;
        }

        log.finer( e.toString());

        Rectangle hitRect = new Rectangle( e.getX() - 1,e.getY() - 1,3,3 );

        // Day

        int dayIndex = e.getX() / m_dayWidth;

        if( dayIndex >= m_noDays ) {
            dayIndex = m_noDays - 1;
        }

        // System.out.println("DayIndex=" + dayIndex + ": " + TimeUtil.addDays(m_startDate, dayIndex));

        // Time

        int timeIndex = m_timePanel.getTimeSlotIndex( e.getY());

        // System.out.println("TimeIndex=" + timeIndex + ": " + m_timePanel.getTimeSlot(timeIndex));

        // check if there is an existing assignment

        for( int i = 0;i < m_slots.length;i++ ) {
            if( m_where[ i ].intersects( hitRect )) {
                MAssignmentSlot mas = m_slots[ i ];

                System.out.println( "Existing=" + mas.getInfo());

                if( !mas.isAssignment()) {
                    return;
                }

                //

                VAssignmentDialog vad = new VAssignmentDialog( Env.getFrame( this ),m_slots[ i ].getMAssignment(),false,m_createNew );

                m_infoSchedule.mAssignmentCallback( vad.getMResourceAssignment());

                return;
            }
        }

        if( m_createNew ) {
            MResourceAssignment ma = new MResourceAssignment( Env.getCtx(),0,null );

            ma.setS_Resource_ID( m_S_Resource_ID );
            ma.setAssignDateFrom( TimeUtil.getDayTime( TimeUtil.addDays( m_startDate,dayIndex ),m_timePanel.getTimeSlot( timeIndex ).getStartTime()));
            ma.setQty( new BigDecimal( 1 ));

            VAssignmentDialog vad = new VAssignmentDialog( Env.getFrame( this ),ma,false,m_createNew );

            m_infoSchedule.mAssignmentCallback( vad.getMResourceAssignment());

            return;
        }
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_infoSchedule = null;
        m_timePanel    = null;
        m_where        = null;
        m_slots        = null;
        this.removeAll();
    }    // dispose
}    // VSchedulePanel



/*
 *  @(#)VSchedulePanel.java   02.07.07
 * 
 *  Fin del fichero VSchedulePanel.java
 *  
 *  Versión 2.2
 *
 */
