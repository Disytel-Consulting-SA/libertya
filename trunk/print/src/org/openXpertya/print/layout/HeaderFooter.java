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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MQuery;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class HeaderFooter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public HeaderFooter( Properties ctx ) {
        m_ctx = ctx;
    }    // HeaderFooter

    /** Descripci�n de Campos */

    private Properties m_ctx;

    /** Descripci�n de Campos */

    private ArrayList m_elements = new ArrayList();

    /** Descripci�n de Campos */

    private PrintElement[] m_pe = null;

    /**
     * Descripci�n de M�todo
     *
     *
     * @param element
     */

    public void addElement( PrintElement element ) {
        if( element != null ) {
            m_elements.add( element );
        }

        m_pe = null;
    }    // addElement

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public PrintElement[] getElements() {
        if( m_pe == null ) {
            m_pe = new PrintElement[ m_elements.size()];
            m_elements.toArray( m_pe );
        }

        return m_pe;
    }    // getElements

    /**
     * Descripci�n de M�todo
     *
     *
     * @param g2D
     * @param bounds
     * @param isView
     */

    public void paint( Graphics2D g2D,Rectangle bounds,boolean isView ) {
        Point pageStart = new Point( bounds.getLocation());

        getElements();

        for( int i = 0;i < m_pe.length;i++ ) {
            m_pe[ i ].paint( g2D,0,pageStart,m_ctx,isView );
        }
    }    // paint

    /**
     * Descripci�n de M�todo
     *
     *
     * @param relativePoint
     *
     * @return
     */

    public MQuery getDrillDown( Point relativePoint ) {
        MQuery retValue = null;

        for( int i = 0;(i < m_elements.size()) && (retValue == null);i++ ) {
            PrintElement element = ( PrintElement )m_elements.get( i );

            retValue = element.getDrillDown( relativePoint,1 );
        }

        return retValue;
    }    // getDrillDown
}    // HeaderFooter



/*
 *  @(#)HeaderFooter.java   12.10.07
 * 
 *  Fin del fichero HeaderFooter.java
 *  
 *  Versión 2.2
 *
 */
