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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.compiere.swing.CPanel;
import org.openXpertya.model.MQuery;
import org.openXpertya.print.layout.LayoutEngine;
import org.openXpertya.print.layout.Page;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class View extends CPanel {

    /**
     * Constructor de la clase ...
     *
     *
     * @param layout
     */

    public View( LayoutEngine layout ) {
        m_layout = layout;
    }    // View

    /** Descripción de Campos */

    private LayoutEngine m_layout;

    /** Descripción de Campos */

    private int m_zoomLevel = 0;

    /** Descripción de Campos */

    public static final String[] ZOOM_OPTIONS = new String[]{ "100%","75%","50%" };

    /** Descripción de Campos */

    public static int MARGIN = 5;

    /** Descripción de Campos */

    private static Color COLOR_BACKGROUND = Color.lightGray;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( View.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getMinimumSize() {
        return getMaximumSize();
    }    // getMinimumSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getMaximumSize() {
        return new Dimension( getPaperWidth() + ( 2 * MARGIN ),( getPaperHeight() + MARGIN ) * getPageCount() + MARGIN );
    }    // getMaximumSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getPreferredSize() {
        return getMaximumSize();
    }    // getPreferredSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isArchivable() {
        return ArchiveEngine.isValid( m_layout );
    }    // IsArchivable

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paintComponent( Graphics g ) {

        // log.fine( "View.paintComponent", g.getClip());

        Graphics2D g2D    = ( Graphics2D )g;
        Rectangle  bounds = g2D.getClipBounds();

        //

        g2D.setColor( COLOR_BACKGROUND );
        g2D.fillRect( bounds.x,bounds.y,bounds.width,bounds.height );

        // for all pages

        for( int page = 0;page < m_layout.getPages().size();page++ ) {
            Rectangle pageRectangle = getRectangleOfPage( page + 1 );

            if( bounds.intersects( pageRectangle )) {
                Page p = ( Page )m_layout.getPages().get( page );

                p.paint( g2D,pageRectangle,true,false );    // sets context
                m_layout.getHeaderFooter().paint( g2D,pageRectangle,true );
            }                                               // paint page
        }                                                   // for all pages
    }                                                       // paintComponent

    /**
     * Descripción de Método
     *
     *
     * @param level
     */

    public void setZoomLevel( int level ) {
        m_zoomLevel = level;
    }    // setZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @param levelString
     */

    public void setZoomLevel( String levelString ) {
        for( int i = 0;i < ZOOM_OPTIONS.length;i++ ) {
            if( ZOOM_OPTIONS[ i ].equals( levelString )) {
                m_zoomLevel = i;

                break;
            }
        }
    }    // setZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getZoomLevel() {
        return m_zoomLevel;
    }    // getZoomLevel

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public Rectangle getRectangleOfPage( int pageNo ) {
        int y = MARGIN + (( pageNo - 1 ) * ( getPaperHeight() + MARGIN ));

        return new Rectangle( MARGIN,y,getPaperWidth(),getPaperHeight());
    }    // getRectangleOfPage

    /**
     * Descripción de Método
     *
     *
     * @param p
     *
     * @return
     */

    public float getPageNoAt( Point p ) {
        float y          = p.y;
        float pageHeight = getPaperHeight() + MARGIN;

        return 1f + ( y / pageHeight );
    }    // getPageAt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPageCount() {
        return m_layout.getPages().size();
    }    // getPageCount

    /**
     * Descripción de Método
     *
     *
     * @param pageNo
     *
     * @return
     */

    public String getPageInfo( int pageNo ) {
        return m_layout.getPageInfo( pageNo );
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPageInfoMax() {
        return m_layout.getPageInfoMax();
    }    // getPageInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public CPaper getPaper() {
        return m_layout.getPaper();
    }    // getPaper

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPaperHeight() {
        return( int )m_layout.getPaper().getHeight( true );
    }    // getPaperHeight

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPaperWidth() {
        return( int )m_layout.getPaper().getWidth( true );
    }    // getPaperHeight

    /**
     * Descripción de Método
     *
     *
     * @param absolutePoint
     *
     * @return
     */

    public MQuery getDrillDown( Point absolutePoint ) {
        int       pageNo        = ( int )getPageNoAt( absolutePoint );
        Rectangle pageRectangle = getRectangleOfPage( pageNo );
        Point     relativePoint = new Point( absolutePoint.x - pageRectangle.x,absolutePoint.y - pageRectangle.y );
        Page page = ( Page )m_layout.getPages().get( pageNo - 1 );

        //

        log.config( "Relative=" + relativePoint + ", " + page );

        // log.config("AbsolutePoint=" + absolutePoint + ", PageNo=" + pageNo + ", pageRectangle=" + pageRectangle);

        MQuery retValue = page.getDrillDown( relativePoint );

        if( retValue == null ) {
            retValue = m_layout.getHeaderFooter().getDrillDown( relativePoint );
        }

        return retValue;
    }    // getDrillDown

    /**
     * Descripción de Método
     *
     *
     * @param absolutePoint
     *
     * @return
     */

    public MQuery getDrillAcross( Point absolutePoint ) {
        int       pageNo        = ( int )getPageNoAt( absolutePoint );
        Rectangle pageRectangle = getRectangleOfPage( pageNo );
        Point     relativePoint = new Point( absolutePoint.x - pageRectangle.x,absolutePoint.y - pageRectangle.y );
        Page page = ( Page )m_layout.getPages().get( pageNo - 1 );

        //

        log.config( "Relative=" + relativePoint + ", " + page );

        // log.config("AbsolutePoint=" + absolutePoint + ", PageNo=" + pageNo + ", pageRectangle=" + pageRectangle);

        return page.getDrillAcross( relativePoint );
    }    // getDrillAcross
}    // View



/*
 *  @(#)View.java   23.03.06
 * 
 *  Fin del fichero View.java
 *  
 *  Versión 2.2
 *
 */
