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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFLayoutManager implements LayoutManager {

    /**
     * Constructor de la clase ...
     *
     */

    public WFLayoutManager() {}    // WFLayoutManager

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Dimension m_size = null;

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param comp
     */

    public void addLayoutComponent( String name,Component comp ) {
        invalidateLayout();
    }    // addLayoutComponent

    /**
     * Descripción de Método
     *
     *
     * @param comp
     */

    public void removeLayoutComponent( Component comp ) {
        if( comp == null ) {
            return;
        }

        invalidateLayout();
    }    // removeLayoutComponent

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public Dimension preferredLayoutSize( Container parent ) {
        if( m_size == null ) {
            layoutContainer( parent );
        }

        return m_size;
    }    // preferredLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    public Dimension minimumLayoutSize( Container parent ) {
        return preferredLayoutSize( parent );
    }    // minimumLayoutSize

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void layoutContainer( Container parent ) {
        Insets insets = parent.getInsets();

        //

        int width  = insets.left;
        int height = insets.top;

        // We need to layout

        if( needLayout( parent )) {
            int x = 5;
            int y = 5;

            // Go through all components

            for( int i = 0;i < parent.getComponentCount();i++ ) {
                Component comp = parent.getComponent( i );

                if( comp.isVisible() && (comp instanceof WFNode) ) {
                    Dimension ps = comp.getPreferredSize();

                    comp.setLocation( x,y );
                    comp.setBounds( x,y,ps.width,ps.height );

                    //

                    width  = x + ps.width;
                    height = y + ps.height;

                    // next pos

                    if( x == 5 ) {
                        x = 170;
                    } else {
                        x = 5;
                        y += 80;
                    }

                    // x += ps.width-20;
                    // y += ps.height+20;

                }
            }
        } else    // we have an Layout
        {

            // Go through all components

            for( int i = 0;i < parent.getComponentCount();i++ ) {
                Component comp = parent.getComponent( i );

                if( comp.isVisible() && (comp instanceof WFNode) ) {
                    Dimension ps        = comp.getPreferredSize();
                    Point     loc       = comp.getLocation();
                    int       maxWidth  = comp.getX() + ps.width;
                    int       maxHeight = comp.getY() + ps.height;

                    if( width < maxWidth ) {
                        width = maxWidth;
                    }

                    if( height < maxHeight ) {
                        height = maxHeight;
                    }

                    comp.setBounds( loc.x,loc.y,ps.width,ps.height );
                }
            }     // for all components
        }         // have layout

        // Create Lines

        WFContentPanel panel = ( WFContentPanel )parent;

        panel.createLines();

        // Calculate size

        width  += insets.right;
        height += insets.bottom;

        // return size

        m_size = new Dimension( width,height );
        log.fine( "layoutContainer - " + m_size );
    }    // layoutContainer

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    private boolean needLayout( Container parent ) {
        Point p00 = new Point( 0,0 );

        // Go through all components

        for( int i = 0;i < parent.getComponentCount();i++ ) {
            Component comp = parent.getComponent( i );

            if( (comp instanceof WFNode) && comp.getLocation().equals( p00 )) {
                log.fine( "needLayout - " + comp );

                return true;
            }
        }

        return false;
    }    // needLayout

    /**
     * Descripción de Método
     *
     */

    private void invalidateLayout() {
        m_size = null;
    }    // invalidateLayout
}    // WFLayoutManager



/*
 *  @(#)WFLayoutManager.java   02.07.07
 * 
 *  Fin del fichero WFLayoutManager.java
 *  
 *  Versión 2.2
 *
 */
