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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Dimension2DImpl extends Dimension2D {

    /**
     * Constructor de la clase ...
     *
     */

    public Dimension2DImpl() {}    // Dimension2DImpl

    /**
     * Constructor de la clase ...
     *
     *
     * @param dim
     */

    public Dimension2DImpl( Dimension dim ) {
        setSize( dim );
    }    // Dimension2DImpl

    /**
     * Constructor de la clase ...
     *
     *
     * @param width
     * @param height
     */

    public Dimension2DImpl( double width,double height ) {
        setSize( width,height );
    }    // Dimension2DImpl

    /** Descripci�n de Campos */

    public double width = 0;

    /** Descripci�n de Campos */

    public double height = 0;

    /**
     * Descripci�n de M�todo
     *
     *
     * @param width
     * @param height
     */

    public void setSize( double width,double height ) {
        this.width  = width;
        this.height = height;
    }    // setSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @param dim
     */

    public void setSize( Dimension dim ) {
        this.width  = dim.getWidth();
        this.height = dim.getHeight();
    }    // setSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @param dWidth
     * @param dHeight
     */

    public void addBelow( double dWidth,double dHeight ) {
        if( this.width < dWidth ) {
            this.width = dWidth;
        }

        this.height += dHeight;
    }    // addBelow

    /**
     * Descripci�n de M�todo
     *
     *
     * @param dim
     */

    public void addBelow( Dimension dim ) {
        addBelow( dim.width,dim.height );
    }    // addBelow

    /**
     * Descripci�n de M�todo
     *
     */

    public void roundUp() {
        width  = Math.ceil( width );
        height = Math.ceil( height );
    }    // roundUp

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public double getWidth() {
        return width;
    }    // getWidth

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public double getHeight() {
        return height;
    }    // getHeight

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int hashCode() {
        long bits = Double.doubleToLongBits( width );

        bits ^= Double.doubleToLongBits( height ) * 31;

        return((( int )bits ) ^ (( int )( bits >> 32 )));
    }    // hashCode

    /**
     * Descripci�n de M�todo
     *
     *
     * @param obj
     *
     * @return
     */

    public boolean equals( Object obj ) {
        if( (obj != null) && (obj instanceof Dimension2D) ) {
            Dimension2D d = ( Dimension2D )obj;

            if( (d.getWidth() == width) && (d.getHeight() == height) ) {
                return true;
            }
        }

        return false;
    }    // equals

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append( "Dimension2D[w=" ).append( width ).append( ",h=" ).append( height ).append( "]" );

        return sb.toString();
    }    // toString
}    // Dimension2DImpl



/*
 *  @(#)Dimension2DImpl.java   12.10.07
 * 
 *  Fin del fichero Dimension2DImpl.java
 *  
 *  Versión 2.2
 *
 */
