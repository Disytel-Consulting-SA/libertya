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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAttachment;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.util.Env;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImageElement extends PrintElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param image
     */

    public ImageElement( Image image ) {
        m_image = image;

        if( m_image != null ) {
            log.fine( "Image=" + image );
        } else {
            log.log( Level.SEVERE,"Image is NULL" );
        }
    }    // ImageElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param imageURLstring
     */

    public ImageElement( String imageURLstring ) {
        URL imageURL = getURL( imageURLstring );

        if( imageURL != null ) {
            m_image = Toolkit.getDefaultToolkit().getImage( imageURL );

            if( m_image != null ) {
                log.fine( "URL=" + imageURL );
            } else {
                log.log( Level.SEVERE,"Not loaded - URL=" + imageURL );
            }
        } else {
            log.log( Level.SEVERE,"Invalid URL=" + imageURLstring );
        }
    }    // ImageElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param imageURL
     */

    public ImageElement( URL imageURL ) {
        if( imageURL != null ) {
            m_image = Toolkit.getDefaultToolkit().getImage( imageURL );

            if( m_image != null ) {
                log.fine( "URL=" + imageURL );
            } else {
                log.log( Level.SEVERE,"Not loaded - URL=" + imageURL );
            }
        } else {
            log.severe( "ImageURL is NULL" );
        }
    }    // ImageElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_PrintFormatItem_ID
     */

    public ImageElement( int AD_PrintFormatItem_ID ) {
        loadAttachment( AD_PrintFormatItem_ID );
    }    // ImageElement

    /** Descripci�n de Campos */

    private Image m_image = null;

    /**
     * Descripci�n de M�todo
     *
     *
     * @param urlString
     *
     * @return
     */

    private URL getURL( String urlString ) {
        URL url = null;

        // not a URL - may be a resource

        if( urlString.indexOf( "://" ) == -1 ) {
            ClassLoader cl = getClass().getClassLoader();

            url = cl.getResource( urlString );

            if( url != null ) {
                return url;
            }

            log.log( Level.SEVERE,"Not found - " + urlString );

            return null;
        }

        // load URL

        try {
            url = new URL( urlString );
        } catch( MalformedURLException ex ) {
            log.log( Level.SEVERE,"getURL",ex );
        }

        return url;
    }    // getURL;

    /**
     * Descripci�n de M�todo
     *
     *
     * @param AD_PrintFormatItem_ID
     */

    private void loadAttachment( int AD_PrintFormatItem_ID ) {
        MAttachment attachment = MAttachment.get( Env.getCtx(),MPrintFormatItem.Table_ID,AD_PrintFormatItem_ID );

        if( attachment == null ) {
            log.log( Level.SEVERE,"No Attachment - AD_PrintFormatItem_ID=" + AD_PrintFormatItem_ID );

            return;
        }

        if( attachment.getEntryCount() != 1 ) {
            log.log( Level.SEVERE,"Need just 1 Attachment Entry = " + attachment.getEntryCount());

            return;
        }

        byte[] imageData = attachment.getEntryData( 0 );

        if( imageData != null ) {
            m_image = Toolkit.getDefaultToolkit().createImage( imageData );
        }

        if( m_image != null ) {
            log.fine( attachment.getEntryName( 0 ) + " - Size=" + imageData.length );
        } else {
            log.log( Level.SEVERE,attachment.getEntryName( 0 ) + " - not loaded (must be gif or jpg) - AD_PrintFormatItem_ID=" + AD_PrintFormatItem_ID );
        }
    }    // loadAttachment

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    protected boolean calculateSize() {
        p_width  = 0;
        p_height = 0;

        if( m_image == null ) {
            return true;
        }

        // we have an image

        waitForLoad( m_image );

        if( m_image != null ) {
            p_width  = m_image.getWidth( this );
            p_height = m_image.getHeight( this );
        }

        return true;
    }    // calculateSize

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     */

    public Image getImage() {
        return m_image;
    }    // getImage

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
        if( m_image == null ) {
            return;
        }

        // Position

        Point2D.Double location = getAbsoluteLocation( pageStart );
        int            x        = ( int )location.x;

        if( MPrintFormatItem.FIELDALIGNMENTTYPE_TrailingRight.equals( p_FieldAlignmentType )) {
            x += p_maxWidth - p_width;
        } else if( MPrintFormatItem.FIELDALIGNMENTTYPE_Center.equals( p_FieldAlignmentType )) {
            x += ( p_maxWidth - p_width ) / 2;
        }

        int y = ( int )location.y;

        //

        g2D.drawImage( m_image,x,y,this );
    }    // paint
}    // ImageElement



/*
 *  @(#)ImageElement.java   12.10.07
 * 
 *  Fin del fichero ImageElement.java
 *  
 *  Versión 2.2
 *
 */
