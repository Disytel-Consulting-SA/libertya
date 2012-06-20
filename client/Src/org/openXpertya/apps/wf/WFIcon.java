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
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import org.openXpertya.model.MTreeNode;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFIcon implements Icon {

    /**
     * Constructor de la clase ...
     *
     *
     * @param type
     */

    public WFIcon( int type ) {
        if( (type > 0) && (type < MTreeNode.IMAGES.length) ) {
            m_type = type;
        }
    }    // WFIcon

    /**
     * Constructor de la clase ...
     *
     *
     * @param action
     */

    public WFIcon( String action ) {
        if( action != null ) {
            m_type = MTreeNode.getImageIndex( action );
        }
    }    // WFIcon

    /** Descripción de Campos */

    private static int WIDTH = 20;    // Image is 16x16

    /** Descripción de Campos */

    private static int HEIGHT = 20;

    /** Descripción de Campos */

    private int m_type = 0;

    /**
     * Descripción de Método
     *
     *
     * @param c
     * @param g
     * @param x
     * @param y
     */

    public void paintIcon( Component c,Graphics g,int x,int y ) {
        Graphics2D g2D  = ( Graphics2D )g;
        Icon       icon = MTreeNode.getIcon( m_type );

        if( icon != null ) {
            int xI = x + (( WIDTH - icon.getIconWidth()) / 2 );
            int yI = y + (( HEIGHT - icon.getIconHeight()) / 2 );

            icon.paintIcon( c,g,xI,yI );
        } else    // draw dot
        {
            int size = 10;
            int xI   = x + (( WIDTH - size ) / 2 );
            int yI   = y + (( HEIGHT - size ) / 2 );

            g2D.setColor( Color.magenta );
            g2D.fillOval( xI,yI,size,size );
        }
    }             // PaintIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getIconWidth() {
        return WIDTH;
    }    // getIconWidth

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getIconHeight() {
        return HEIGHT;
    }    // getIconHeight
}    // WFIcon



/*
 *  @(#)WFIcon.java   02.07.07
 * 
 *  Fin del fichero WFIcon.java
 *  
 *  Versión 2.2
 *
 */
