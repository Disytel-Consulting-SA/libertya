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



package org.openXpertya.grid.ed;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VOvrCaret extends DefaultCaret {

    /**
     * Constructor de la clase ...
     *
     */

    public VOvrCaret() {
        super();
    }    // VOvrCaret

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paint( Graphics g ) {
        boolean       dotLTR  = true;    // left-to-right
        Position.Bias dotBias = Position.Bias.Forward;

        //

        if( isVisible()) {
            try {
                TextUI    mapper = getComponent().getUI();
                Rectangle r      = mapper.modelToView( getComponent(),getDot(),dotBias );
                Rectangle e = mapper.modelToView( getComponent(),getDot() + 1,dotBias );

                // g.setColor(getComponent().getCaretColor());

                g.setColor( Color.blue );

                //

                int cWidth  = e.x - r.x;
                int cHeight = 4;
                int cThick  = 2;

                //

                g.fillRect( r.x - 1,r.y,cWidth,cThick );              // top
                g.fillRect( r.x - 1,r.y,cThick,cHeight );             // |
                g.fillRect( r.x - 1 + cWidth,r.y,cThick,cHeight );    // |

                //

                int yStart = r.y + r.height;

                g.fillRect( r.x - 1,yStart - cThick,cWidth,cThick );               // button
                g.fillRect( r.x - 1,yStart - cHeight,cThick,cHeight );             // |
                g.fillRect( r.x - 1 + cWidth,yStart - cHeight,cThick,cHeight );    // |
            } catch( BadLocationException e ) {

                // can't render
                // System.err.println("Can't render cursor");

            }
        }    // isVisible
    }        // paint

    /**
     * Descripción de Método
     *
     *
     * @param r
     */

    protected synchronized void damage( Rectangle r ) {
        if( r != null ) {
            x      = r.x - 4;    // start 4 pixles before   (one required)
            y      = r.y;
            width  = 18;         // sufficent for standard font (18-4=14)
            height = r.height;
            repaint();
        }
    }                            // damage
}    // VOvrCaret



/*
 *  @(#)VOvrCaret.java   02.07.07
 * 
 *  Fin del fichero VOvrCaret.java
 *  
 *  Versión 2.2
 *
 */
