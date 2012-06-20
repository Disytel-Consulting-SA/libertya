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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.compiere.swing.CPanel;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFContentPanel extends CPanel implements MouseListener,MouseMotionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public WFContentPanel() {
        super( new WFLayoutManager());

        // setBorder (BorderFactory.createEmptyBorder(5,5,5,5));
        // centerPanel.setBackground(new Color(236,236,236));
        // setOpaque(false);

    }    // WFContentPanel

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private ArrayList m_nodes = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_lines = new ArrayList();

    /** Descripción de Campos */

    private Point m_draggedStart = null;

    /** Descripción de Campos */

    private WFNode m_draggedNode = null;

    /** Descripción de Campos */

    private boolean m_dragged = false;

    /**
     * Descripción de Método
     *
     */

    public void removeAll() {
        m_nodes.clear();
        m_lines.clear();

        Component[] components = getComponents();

        for( int i = 0;i < components.length;i++ ) {
            Component component = components[ i ];

            component.removeMouseListener( this );
            component.removeMouseMotionListener( this );
        }

        super.removeAll();
    }    // removeAll

    /**
     * Descripción de Método
     *
     *
     * @param comp
     * @param readWrite
     *
     * @return
     */

    public Component add( Component comp,boolean readWrite ) {
        if( comp instanceof WFLine ) {
            m_lines.add( comp );

            return comp;
        }

        if( comp instanceof WFNode ) {
            m_nodes.add( comp );
            comp.addMouseListener( this );

            if( readWrite ) {
                comp.addMouseMotionListener( this );
            }
        }

        return super.add( comp );
    }    // add

    /**
     * Descripción de Método
     *
     */

    protected void createLines() {
        for( int i = 0;i < m_lines.size();i++ ) {
            WFLine    line = ( WFLine )m_lines.get( i );
            Rectangle from = findBounds( line.getAD_WF_Node_ID());
            Rectangle to   = findBounds( line.getAD_WF_Next_ID());

            line.setFromTo( from,to );

            // same bounds as parent
            // line.setBounds(0,0, width, height);

        }    // for all lines
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    private Rectangle findBounds( int AD_WF_Node_ID ) {
        for( int i = 0;i < m_nodes.size();i++ ) {
            WFNode node = ( WFNode )m_nodes.get( i );

            if( node.getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                return node.getBounds();
            }
        }

        return null;
    }    // findBounds

    /**
     * Descripción de Método
     *
     *
     * @param p
     *
     * @return
     */

    public Component getComponentAt( Point p ) {
        return getComponentAt( p.x,p.y );
    }    // getComponentAt

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public Component getComponentAt( int x,int y ) {
        Component comp = super.getComponentAt( x,y );

        if( comp instanceof WFNode ) {
            return comp;
        }

        for( int i = 0;i < m_nodes.size();i++ ) {
            WFNode node = ( WFNode )m_nodes.get( i );
            int    xx   = x - node.getX();
            int    yy   = y - node.getY();

            if( node.contains( xx,yy )) {
                return node;
            }
        }

        return comp;
    }    // getComponentAt

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        if( e.getSource() instanceof WFNode ) {
            log.fine( e.getSource().toString());

            WFNode selected = ( WFNode )e.getSource();

            for( int i = 0;i < m_nodes.size();i++ ) {
                WFNode node = ( WFNode )m_nodes.get( i );

                if( selected.getAD_WF_Node_ID() == node.getAD_WF_Node_ID()) {
                    node.setSelected( true );
                } else {
                    node.setSelected( false );
                }
            }
        }

        m_dragged = false;
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}    // mouseEntered

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}    // mouseExited

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {
        if( e.getSource() instanceof WFNode ) {
            WFNode node = ( WFNode )e.getSource();

            if( node.isEditable()) {
                m_draggedNode  = node;
                m_draggedStart = SwingUtilities.convertPoint( m_draggedNode,e.getX(),e.getY(),this );
            } else {
                m_dragged      = false;
                m_draggedNode  = null;
                m_draggedStart = null;
            }
        }
    }    // mousePressed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseDragged( MouseEvent e ) {

        // Nothing selected

        if( (m_draggedNode == null) || (e.getSource() != m_draggedNode) ) {
            if( e.getSource() instanceof WFNode ) {
                WFNode node = ( WFNode )e.getSource();

                if( node.isEditable()) {
                    m_draggedNode = node;
                }

                m_draggedStart = null;
            }
        }

        // Move Node

        if( m_draggedNode != null ) {
            m_dragged = true;

            if( m_draggedStart == null ) {
                m_draggedStart = SwingUtilities.convertPoint( m_draggedNode,e.getX(),e.getY(),this );
            }

            // If not converted to coordinate system of parent, it gets jumpy

            Point mousePosition = SwingUtilities.convertPoint( m_draggedNode,e.getX(),e.getY(),this );
            int   xDelta      = mousePosition.x - m_draggedStart.x;
            int   yDelta      = mousePosition.y - m_draggedStart.y;
            Point newLocation = m_draggedNode.getLocation();

            newLocation.x += xDelta;

            if( newLocation.x < 0 ) {
                newLocation.x = 0;
            }

            newLocation.y += yDelta;

            if( newLocation.y < 0 ) {
                newLocation.y = 0;
            }

            m_draggedNode.setLocation( newLocation.x,newLocation.y );

            // log.fine("mouseDragged - " + m_draggedNode + " - " + e);
            // log.fine("mouseDragged - Delta=" + xDelta + "/" + yDelta);

            m_draggedStart = mousePosition;
            invalidate();
            validate();
            repaint();
        }
    }    // mouseDragged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {

        // log.fine("mouseReleased - " + m_draggedNode);

        m_dragged      = false;
        m_draggedNode  = null;
        m_draggedStart = null;
        repaint();
    }    // mouseReleased

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseMoved( MouseEvent e ) {}    // mouseMoved

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        // Paint Lines

        for( int i = 0;i < m_lines.size();i++ ) {
            WFLine line = ( WFLine )m_lines.get( i );

            line.paint( g );
        }

        // Paint Position = right next to the box

        if( m_dragged && (m_draggedNode != null) ) {
            Point      loc  = m_draggedNode.getLocation();
            String     text = "(" + loc.x + "," + loc.y + ")";
            Graphics2D g2D  = ( Graphics2D )g;
            Font       font = new Font( "Dialog",Font.PLAIN,10 );

            g2D.setColor( Color.magenta );

            TextLayout layout = new TextLayout( text,font,g2D.getFontRenderContext());

            loc.x += m_draggedNode.getWidth();
            loc.y += layout.getAscent();
            layout.draw( g2D,loc.x,loc.y );
        }
    }    // paintComponents
}    // WFContentPanel



/*
 *  @(#)WFContentPanel.java   02.07.07
 * 
 *  Fin del fichero WFContentPanel.java
 *  
 *  Versión 2.2
 *
 */
