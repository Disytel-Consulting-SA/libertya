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



package org.openXpertya.print;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CornerElement {

    /** Descripción de Campos */

    private float cornerX = 50;

    /** Descripción de Campos */

    private float cornerY = 50;

    /** Descripción de Campos */

    private int m_corner;

    /** Descripción de Campos */

    private DesignElement m_designElement;

    /** Descripción de Campos */

    private ImageDesElement m_imageDesElement;

    /** Descripción de Campos */

    private BoxDesignElement m_boxDesignElement;

    /** Descripción de Campos */

    public int de = 0;

    /** Descripción de Campos */

    public int ide = 0;

    /** Descripción de Campos */

    public int bde = 0;

    /** Descripción de Campos */

    public static final int CORNERSIZE = 6;

    /** Descripción de Campos */

    public static final int TOPLEFT = 1;

    /** Descripción de Campos */

    public static final int TOPRIGHT = 2;

    /** Descripción de Campos */

    public static final int DOWNLEFT = 3;

    /** Descripción de Campos */

    public static final int DOWNRIGHT = 4;

    // Para el rectangulo discontinuo

    /** Descripción de Campos */

    final static float dash[] = { 5.0f };

    /** Descripción de Campos */

    final static BasicStroke dashed = new BasicStroke( 1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dash,0.0f );

    /**
     * Constructor de la clase ...
     *
     *
     * @param corner
     * @param dsge
     */

    public CornerElement( int corner,DesignElement dsge ) {
        m_corner        = corner;
        m_designElement = dsge;
        de              = 1;
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param corner
     * @param idsge
     */

    public CornerElement( int corner,ImageDesElement idsge ) {
        m_corner          = corner;
        m_imageDesElement = idsge;
        ide               = 1;
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param corner
     * @param bdsge
     */

    public CornerElement( int corner,BoxDesignElement bdsge ) {
        m_corner           = corner;
        m_boxDesignElement = bdsge;
        bde                = 1;
    }

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param x
     * @param y
     * @param p_width
     * @param p_height
     */

    public void puntos( Graphics2D g2D,int x,int y,int p_width,int p_height ) {
        g2D.setColor( Color.BLACK );
        g2D.setStroke( dashed );
        g2D.drawRect( x,y,p_width,p_height );
    }

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param x
     * @param y
     */

    public void paint( Graphics2D g2D,int x,int y ) {
        g2D.setColor( Color.BLACK );

        if( de == 1 ) {
            if( m_corner == m_designElement.getCornerClicked()) {
                g2D.setColor( Color.BLUE );
            }
        }

        if( ide == 1 ) {
            if( m_corner == m_imageDesElement.getCornerClicked()) {
                g2D.setColor( Color.BLUE );
            }
        }

        g2D.fillRect( x,y,CORNERSIZE,CORNERSIZE );
        cornerX = x;
        cornerY = y;
        g2D.setColor( Color.BLACK );
    }

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public boolean isCornerClicked( int x,int y ) {
        Rectangle bounds = new Rectangle(( int )cornerX,( int )cornerY,CORNERSIZE,CORNERSIZE );

        if( bounds.contains( x,y )) {
            return true;
        } else {
            return false;
        }
    }
}



/*
 *  @(#)CornerElement.java   02.07.07
 * 
 *  Fin del fichero CornerElement.java
 *  
 *  Versión 2.2
 *
 */
