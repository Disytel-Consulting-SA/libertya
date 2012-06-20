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

import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.Properties;

import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CPaper extends Paper {

    /**
     * Constructor de la clase ...
     *
     *
     * @param pf
     */

    public CPaper( PageFormat pf ) {
        super();
        m_landscape = pf.getOrientation() != PageFormat.PORTRAIT;

        // try to find MediaSize

        float         x   = ( float )pf.getWidth();
        float         y   = ( float )pf.getHeight();
        MediaSizeName msn = MediaSize.findMedia( x / 72,y / 72,MediaSize.INCH );
        MediaSize ms = null;

        if( msn == null ) {
            msn = MediaSize.findMedia( y / 72,x / 72,MediaSize.INCH );    // flip it
        }

        if( msn != null ) {
            ms = MediaSize.getMediaSizeForName( msn );
        }

        setMediaSize( ms,m_landscape );

        // set size directly

        setSize( pf.getWidth(),pf.getHeight());
        setImageableArea( pf.getImageableX(),pf.getImageableY(),pf.getImageableWidth(),pf.getImageableHeight());
    }    // CPaper

    /**
     * Constructor de la clase ...
     *
     *
     * @param landscape
     */

    public CPaper( boolean landscape ) {
        this( Language.getLoginLanguage(),landscape );
    }    // CPaper

    /**
     * Constructor de la clase ...
     *
     *
     * @param language
     * @param landscape
     */

    private CPaper( Language language,boolean landscape ) {
        this( language.getMediaSize(),landscape );
    }    // CPaper

    /**
     * Constructor de la clase ...
     *
     *
     * @param mediaSize
     * @param landscape
     */

    private CPaper( MediaSize mediaSize,boolean landscape ) {
        this( mediaSize,landscape,36,36,36,36 );
    }    // CPaper

    /**
     * Constructor de la clase ...
     *
     *
     * @param mediaSize
     * @param landscape
     * @param left
     * @param top
     * @param right
     * @param bottom
     */

    public CPaper( MediaSize mediaSize,boolean landscape,double left,double top,double right,double bottom ) {
        super();
        setMediaSize( mediaSize,landscape );
        setImageableArea( left,top,getWidth() - left - right,getHeight() - top - bottom );
    }    // CPaper

    /** Descripción de Campos */

    private MediaSize m_mediaSize;

    /** Descripción de Campos */

    private boolean m_landscape = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( CPaper.class );

    /**
     * Descripción de Método
     *
     *
     * @param mediaSize
     * @param landscape
     */

    public void setMediaSize( MediaSize mediaSize,boolean landscape ) {
        if( mediaSize == null ) {
            throw new IllegalArgumentException( "MediaSize is null" );
        }

        m_mediaSize = mediaSize;
        m_landscape = landscape;

        // Get Sise in Inch * 72

        double width  = m_mediaSize.getX( MediaSize.INCH ) * 72;
        double height = m_mediaSize.getY( MediaSize.INCH ) * 72;

        // Set Size

        setSize( width,height );
        log.fine( mediaSize.getMediaSizeName() + ": " + m_mediaSize + " - Landscape=" + m_landscape );
    }    // setMediaSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MediaSizeName getMediaSizeName() {
        return m_mediaSize.getMediaSizeName();
    }    // getMediaSizeName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MediaSize getMediaSize() {
        return m_mediaSize;
    }    // getMediaSize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MediaPrintableArea getMediaPrintableArea() {
        MediaPrintableArea area = new MediaPrintableArea(( float )getImageableX() / 72,( float )getImageableY() / 72,( float )getImageableWidth() / 72,( float )getImageableHeight() / 72,MediaPrintableArea.INCH );

        // log.fine( "CPaper.getMediaPrintableArea", area.toString(MediaPrintableArea.INCH, "\""));

        return area;
    }    // getMediaPrintableArea

    /**
     * Descripción de Método
     *
     *
     * @param area
     */

    public void setMediaPrintableArea( MediaPrintableArea area ) {
        int inch = MediaPrintableArea.INCH;

        log.fine( area.toString( inch,"\"" ));
        setImageableArea( area.getX( inch ) * 72,area.getY( inch ) * 72,area.getWidth( inch ) * 72,area.getHeight( inch ) * 72 );
    }    // setMediaPrintableArea

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLandscape() {
        return m_landscape;
    }    // isLandscape

    /**
     * Descripción de Método
     *
     *
     * @param job
     *
     * @return
     */

    public boolean pageSetupDialog( PrinterJob job ) {
        PrintRequestAttributeSet prats = getPrintRequestAttributeSet();

        // Page Dialog

        PageFormat pf = job.pageDialog( prats );

        setPrintRequestAttributeSet( prats );

        return true;
    }    // pageSetupDialog

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintRequestAttributeSet getPrintRequestAttributeSet() {
        PrintRequestAttributeSet pratts = new HashPrintRequestAttributeSet();

        // media-printable-area = (25.4,25.4)->(165.1,228.6)mm - class javax.print.attribute.standard.MediaPrintableArea

        pratts.add( getMediaPrintableArea());

        // orientation-requested = landscape - class javax.print.attribute.standard.OrientationRequested

        if( isLandscape()) {
            pratts.add( OrientationRequested.LANDSCAPE );
        } else {
            pratts.add( OrientationRequested.PORTRAIT );
        }

        // media = na-legal

        pratts.add( getMediaSizeName());

        return pratts;
    }    // getPrintRequestAttributes

    /**
     * Descripción de Método
     *
     *
     * @param prats
     */

    public void setPrintRequestAttributeSet( PrintRequestAttributeSet prats ) {
        boolean            landscape = m_landscape;
        MediaSize          ms        = m_mediaSize;
        MediaPrintableArea area      = getMediaPrintableArea();
        Attribute[]        atts      = prats.toArray();

        for( int i = 0;i < atts.length;i++ ) {
            if( atts[ i ] instanceof OrientationRequested ) {
                OrientationRequested or = ( OrientationRequested )atts[ i ];

                if( or.getName().equals( OrientationRequested.PORTRAIT.getName())) {
                    landscape = false;
                } else {
                    landscape = true;
                }
            } else if( atts[ i ] instanceof MediaSizeName ) {
                MediaSizeName msn = ( MediaSizeName )atts[ i ];

                ms = MediaSize.getMediaSizeForName( msn );
            } else if( atts[ i ] instanceof MediaPrintableArea ) {
                area = ( MediaPrintableArea )atts[ i ];
            } else {    // unhandeled
                System.out.println( atts[ i ].getName() + " = " + atts[ i ] + " - " + atts[ i ].getCategory());
            }
        }

        //

        setMediaSize( ms,landscape );
        setMediaPrintableArea( area );
    }    // getPrintRequestAttributes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PageFormat getPageFormat() {
        PageFormat pf = new PageFormat();

        pf.setPaper( this );

        int orient = PageFormat.PORTRAIT;

        if( m_landscape ) {
            orient = PageFormat.LANDSCAPE;
        }

        pf.setOrientation( orient );

        return pf;
    }    // getPageFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "CPaper[" );

        sb.append( getWidth() / 72 ).append( "x" ).append( getHeight() / 72 ).append( '"' ).append( m_landscape
                ?" Landscape "
                :" Portrait " ).append( "x=" ).append( getImageableX()).append( ",y=" ).append( getImageableY()).append( " w=" ).append( getImageableWidth()).append( ",h=" ).append( getImageableHeight()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public String toString( Properties ctx ) {
        StringBuffer sb = new StringBuffer();

        // Print Media size

        sb.append( m_mediaSize.getMediaSizeName());

        // Print dimension

        String name = m_mediaSize.getMediaSizeName().toString();

        if( !name.startsWith( "iso" )) {
            sb.append( " - " ).append( m_mediaSize.toString( MediaSize.INCH,"\"" )).append( " (" ).append( getMediaPrintableArea().toString( MediaPrintableArea.INCH,"\"" ));
        }

        if( !name.startsWith( "na" )) {
            sb.append( " - " ).append( m_mediaSize.toString( MediaSize.MM,"mm" )).append( " (" ).append( getMediaPrintableArea().toString( MediaPrintableArea.MM,"mm" ));
        }

        // Print Orientation

        sb.append( ") - " ).append( Msg.getMsg( ctx,m_landscape
                ?"Landscape"
                :"Portrait" ));

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */

    public boolean equals( Object obj ) {
        if( obj instanceof CPaper ) {
            CPaper cp = ( CPaper )obj;

            if( cp.isLandscape() != m_landscape ) {
                return false;
            }

            // media size is more descriptive

            if( (getImageableX() == cp.getImageableX()) && (getImageableY() == cp.getImageableY()) && (getImageableWidth() == cp.getImageableWidth()) && (getImageableHeight() == cp.getImageableHeight())) {
                return true;
            }
        }

        return false;
    }    // equals

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getWidth( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getHeight();
        }

        return super.getWidth();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getHeight( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getWidth();
        }

        return super.getHeight();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getImageableY( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getImageableX();
        }

        return super.getImageableY();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getImageableX( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getImageableY();
        }

        return super.getImageableX();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getImageableHeight( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getImageableWidth();
        }

        return super.getImageableHeight();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public double getImageableWidth( boolean orientationCorrected ) {
        if( orientationCorrected && m_landscape ) {
            return super.getImageableHeight();
        }

        return super.getImageableWidth();
    }

    /**
     * Descripción de Método
     *
     *
     * @param orientationCorrected
     *
     * @return
     */

    public Insets getMargin( boolean orientationCorrected ) {
        return new Insets(( int )getImageableY( orientationCorrected ),                                                                                        // top
                          ( int )getImageableX( orientationCorrected ),                                                                                        // left
                          ( int )( getHeight( orientationCorrected ) - getImageableY( orientationCorrected ) - getImageableHeight( orientationCorrected )),    // bottom
                          ( int )( getWidth( orientationCorrected ) - getImageableX( orientationCorrected ) - getImageableWidth( orientationCorrected )));    // right
    }    // getMargin
}    // CPapaer



/*
 *  @(#)CPaper.java   23.03.06
 * 
 *  Fin del fichero CPaper.java
 *  
 *  Versión 2.2
 *
 */
