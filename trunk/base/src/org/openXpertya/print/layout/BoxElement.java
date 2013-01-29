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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Properties;

import org.openXpertya.print.MPrintFormatItem;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BoxElement extends PrintElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param item
     * @param color
     */

    public BoxElement( MPrintFormatItem item,Color color ) {
        super();

        if( (item != null) && item.isTypeBox()) {
            m_item  = item;
            m_color = color;
        }
    }    // BoxElement

    /** Descripci�n de Campos */

    private MPrintFormatItem m_item = null;

    /** Descripci�n de Campos */

    private Color m_color = Color.BLACK;

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        p_width  = 0;
        p_height = 0;

        if( m_item == null ) {
            return true;
        }

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
        if( m_item == null ) {
            return;
        }

        //

        g2D.setColor( m_color );

        BasicStroke s = new BasicStroke( m_item.getLineWidth());

        g2D.setStroke( s );

        //

        Point2D.Double location = getAbsoluteLocation( pageStart );
        int            x        = ( int )location.x;
        int            y        = ( int )location.y;
        int            width    = m_item.getMaxWidth();
        int            height   = m_item.getMaxHeight();

        if( m_item.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
            g2D.drawLine( x,y,x + width,y + height );
        } else {
            String type = m_item.getShapeType();

            if( type == null ) {
                type = "";
            }

            if( m_item.isFilledRectangle()) {
                if( type.equals( MPrintFormatItem.SHAPETYPE_3DRectangle )) {
                    g2D.fill3DRect( x,y,width,height,true );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_Oval )) {
                    g2D.fillOval( x,y,width,height );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_RoundRectangle )) {
                    g2D.fillRoundRect( x,y,width,height,m_item.getArcDiameter(),m_item.getArcDiameter());
                } else {
                    g2D.fillRect( x,y,width,height );
                }
            } else {
                if( type.equals( MPrintFormatItem.SHAPETYPE_3DRectangle )) {
                    g2D.draw3DRect( x,y,width,height,true );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_Oval )) {
                    g2D.drawOval( x,y,width,height );
                } else if( type.equals( MPrintFormatItem.SHAPETYPE_RoundRectangle )) {
                    g2D.drawRoundRect( x,y,width,height,m_item.getArcDiameter(),m_item.getArcDiameter());
                } else {
                    g2D.drawRect( x,y,width,height );
                }
            }
        }
    }    // paint
}    // BoxElement



/*
 *  @(#)BoxElement.java   12.10.07
 * 
 *  Fin del fichero BoxElement.java
 *  
 *  Versión 2.2
 *
 */
