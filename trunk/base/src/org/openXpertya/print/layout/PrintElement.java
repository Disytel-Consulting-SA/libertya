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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MQuery;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.util.CLogger;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class PrintElement implements ImageObserver {

    /**
     * Constructor de la clase ...
     *
     */

    protected PrintElement() {}    // PrintElement

    /** Descripci�n de Campos */

    public static final Color LINK_COLOR = Color.blue;

    /** Descripci�n de Campos */

    protected float p_width = 0f;

    /** Descripci�n de Campos */

    protected float p_height = 0f;

    /** Descripci�n de Campos */

    protected boolean p_sizeCalculated = false;

    /** Descripci�n de Campos */

    protected float p_maxWidth = 0f;

    /** Descripci�n de Campos */

    protected float p_maxHeight = 0f;

    /** Descripci�n de Campos */

    protected String p_FieldAlignmentType;

    /** Descripci�n de Campos */

    protected Point2D.Double p_pageLocation = null;

    /** Descripci�n de Campos */

    private boolean m_imageNotLoaded = true;

    /** Descripci�n de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getWidth() {
        if( !p_sizeCalculated ) {
            p_sizeCalculated = calculateSize();
        }

        return p_width;
    }    // getWidth

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public float getHeight() {
        if( !p_sizeCalculated ) {
            p_sizeCalculated = calculateSize();
        }

        return p_height;
    }    // getHeight

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageNo
     *
     * @return
     */

    public float getHeight( int pageNo ) {
        return getHeight();
    }    // getHeight

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public int getPageCount() {
        return 1;
    }    // getPageCount

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected abstract boolean calculateSize();

    /**
     * Descripci�n de M�todo
     *
     *
     * @param maxWidth
     * @param maxHeight
     * @param isHeightOneLine
     * @param FieldAlignmentType
     */

    public void layout( float maxWidth,float maxHeight,boolean isHeightOneLine,String FieldAlignmentType ) {
        if( isHeightOneLine ) {
            p_maxHeight = -1f;
        } else if( maxHeight > 0f ) {
            p_maxHeight = maxHeight;
        }

        p_maxWidth = maxWidth;

        //

        p_FieldAlignmentType = FieldAlignmentType;

        if( (p_FieldAlignmentType == null) || (p_FieldAlignmentType == MPrintFormatItem.FIELDALIGNMENTTYPE_Default) ) {
            p_FieldAlignmentType = MPrintFormatItem.FIELDALIGNMENTTYPE_LeadingLeft;
        }

        //

        p_sizeCalculated = calculateSize();
    }    // layout

    /**
     * Descripci�n de M�todo
     *
     *
     * @param maxHeight
     */

    public void setMaxHeight( float maxHeight ) {
        p_maxHeight = maxHeight;
    }    // setMaxHeight

    /**
     * Descripci�n de M�todo
     *
     *
     * @param maxWidth
     */

    public void setMaxWidth( float maxWidth ) {
        p_maxWidth = maxWidth;
    }    // setMaxWidth

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageLocation
     */

    public void setLocation( Point2D pageLocation ) {
        p_pageLocation = new Point2D.Double( pageLocation.getX(),pageLocation.getY());
    }    // setLocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Point2D getLocation() {
        return p_pageLocation;
    }    // getLocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @param pageStart
     *
     * @return
     */

    protected Point2D.Double getAbsoluteLocation( Point2D pageStart ) {
        Point2D.Double retValue = new Point2D.Double( p_pageLocation.x + pageStart.getX(),p_pageLocation.y + pageStart.getY());

        // log.finest( "PrintElement.getAbsoluteLocation", "PageStart=" + pageStart.getX() + "/" + pageStart.getY()
        // + ",PageLocaton=" + p_pageLocation.x + "/" + p_pageLocation.y + " => " + retValue.x + "/" + retValue.y);

        return retValue;
    }    // getAbsoluteLocation

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Rectangle getBounds() {
        if( p_pageLocation == null ) {
            return new Rectangle( 0,0,( int )p_width,( int )p_height );
        }

        return new Rectangle(( int )p_pageLocation.x,( int )p_pageLocation.y,( int )p_width,( int )p_height );
    }    // getBounds

    /**
     * Descripci�n de M�todo
     *
     *
     * @param relativePoint
     * @param pageNo
     *
     * @return
     */

    public MQuery getDrillDown( Point relativePoint,int pageNo ) {
        return null;
    }    // getDrillDown

    /**
     * Descripci�n de M�todo
     *
     *
     * @param relativePoint
     * @param pageNo
     *
     * @return
     */

    public MQuery getDrillAcross( Point relativePoint,int pageNo ) {
        return null;
    }    // getDrillAcross

    /**
     * Descripci�n de M�todo
     *
     *
     * @param ctx
     */

    public void translate( Properties ctx ) {

        // noop

    }    // translate

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public boolean isTranslated() {
        return false;
    }    // translate

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

    public abstract void paint( Graphics2D g2D,int pageNo,Point2D pageStart,Properties ctx,boolean isView );

    /**
     * Descripci�n de M�todo
     *
     *
     * @param img
     * @param infoflags
     * @param x
     * @param y
     * @param width
     * @param height
     *
     * @return
     */

    public boolean imageUpdate( Image img,int infoflags,int x,int y,int width,int height ) {

        // copied from java.awt.component

        m_imageNotLoaded = ( infoflags & ( ALLBITS | ABORT )) == 0;

        // if (CLogMgt.isLevel(9))

        log.finest( "Flags=" + infoflags + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + " - loading=" + m_imageNotLoaded );

        return m_imageNotLoaded;
    }    // imageUpdate

    /**
     * Descripci�n de M�todo
     *
     *
     * @param image
     *
     * @return
     */

    public boolean waitForLoad( Image image ) {
        long start = System.currentTimeMillis();

        Thread.yield();

        int count = 0;

        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            while( !toolkit.prepareImage( image,-1,-1,this ))    // ImageObserver calls imageUpdate
            {

                // Timeout

                if( count > 1000 )    // about 16 sec overall
                {
                    log.severe( this + " - Timeout - " + ( System.currentTimeMillis() - start ) + "ms - #" + count );

                    return false;
                }

                try {
                    Thread.sleep( 10 );
                } catch( InterruptedException ex ) {
                    log.log( Level.SEVERE,"waitForLoad",ex );

                    break;
                }

                count++;
            }
        } catch( Exception e )    // java.lang.SecurityException
        {
            log.log( Level.SEVERE,"waitForLoad",e );

            return false;
        }

        if( count > 0 ) {
            log.fine(( System.currentTimeMillis() - start ) + "ms - #" + count );
        }

        return true;
    }    // waitForLoad

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected String getDetailInfo() {
        return "";
    }    // getDetailInfo

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public String toString() {
        String       cn = getClass().getName();
        StringBuffer sb = new StringBuffer();

        sb.append( cn.substring( cn.lastIndexOf( '.' ) + 1 )).append( "[" );
        sb.append( "Bounds=" ).append( getBounds()).append( ",Height=" ).append( p_height ).append( "(" ).append( p_maxHeight ).append( "),Width=" ).append( p_width ).append( "(" ).append( p_maxHeight ).append( "),PageLocation=" ).append( p_pageLocation );
        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // PrintElement



/*
 *  @(#)PrintElement.java   12.10.07
 * 
 *  Fin del fichero PrintElement.java
 *  
 *  Versión 2.2
 *
 */
