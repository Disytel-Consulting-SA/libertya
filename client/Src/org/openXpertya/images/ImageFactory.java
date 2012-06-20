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



package org.openXpertya.images;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.UIResource;

import org.compiere.plaf.CompiereLookAndFeel;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImageFactory {

    /**
     * Descripción de Método
	 *  Get Image Icon or null if not exists
	 *  @param name     file name in org.openXpertya.images
	 *  @return image
	 */

    public static ImageIcon getImageIcon( String name ) {
        URL url = org.openXpertya.OpenXpertya.class.getResource( "images/" + name );

        if( url == null ) {
            log.log( Level.SEVERE,"ImageFactory.getImageIcon - not found: " + name );

            return null;
        }

        return new ImageIcon( url );
    }    // getImageIcon

    /** Descripción de Campos */

    private static Icon s_HomeIcon = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ImageFactory.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Icon getHomeIcon() {
        if( s_HomeIcon == null ) {
            s_HomeIcon = new HomeIcon();
        }

        return s_HomeIcon;
    }    // getHomeIcon

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private static class HomeIcon implements Icon,UIResource {

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
            g.translate( x,y );

            // Draw outside edge of house

            g.setColor( CompiereLookAndFeel.getControlInfo());    // black
            g.drawLine( 8,1,1,8 );                                // left edge of roof
            g.drawLine( 8,1,15,8 );                               // right edge of roof
            g.drawLine( 11,2,11,3 );                              // left edge of chimney
            g.drawLine( 12,2,12,4 );                              // right edge of chimney
            g.drawLine( 3,7,3,15 );                               // left edge of house
            g.drawLine( 13,7,13,15 );                             // right edge of house
            g.drawLine( 4,15,12,15 );                             // bottom edge of house

            // Draw door frame
            // same color as edge of house

            g.drawLine( 6,9,6,14 );      // left
            g.drawLine( 10,9,10,14 );    // right
            g.drawLine( 7,9,9,9 );       // top

            // Draw roof body

            g.setColor( CompiereLookAndFeel.getControlDarkShadow());    // secondary1
            g.fillRect( 8,2,1,1 );    // top toward bottom
            g.fillRect( 7,3,3,1 );
            g.fillRect( 6,4,5,1 );
            g.fillRect( 5,5,7,1 );
            g.fillRect( 4,6,9,2 );

            // Draw doornob
            // same color as roof body

            g.drawLine( 9,12,9,12 );

            // Paint the house

            g.setColor( CompiereLookAndFeel.getPrimaryControl());    // primary3
            g.drawLine( 4,8,12,8 );                                  // above door
            g.fillRect( 4,9,2,6 );                                   // left of door
            g.fillRect( 11,9,2,6 );                                  // right of door
            g.translate( -x,-y );
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getIconWidth() {
            return 18;
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getIconHeight() {
            return 18;
        }
    }    // HomeIcon


    /** Descripción de Campos */

    private static Icon s_FolderIcon = null;

    /** Descripción de Campos */

    private static final Dimension s_icon16Size = new Dimension( 16,16 );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Icon getFolderIcon() {
        if( s_FolderIcon == null ) {
            s_FolderIcon = new FolderIcon();
        }

        return s_FolderIcon;
    }    // getFolderIcon

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private static class FolderIcon extends FolderIcon16 {

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getShift() {
            return -1;
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getAdditionalHeight() {
            return 2;
        }
    }    // FolderIcon


    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    public static class FolderIcon16 implements Icon {

        /** Descripción de Campos */

        transient Image image;

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
            if( image == null ) {
                image = new BufferedImage( getIconWidth(),getIconHeight(),BufferedImage.TYPE_INT_ARGB );

                Graphics imageG = image.getGraphics();

                paintMe( c,imageG );
                imageG.dispose();
            }

            g.drawImage( image,x,y + getShift(),null );
        }

        /**
         * Descripción de Método
         *
         *
         * @param c
         * @param g
         */

        private void paintMe( Component c,Graphics g ) {
            int right  = s_icon16Size.width - 1;
            int bottom = s_icon16Size.height - 1;

            // Draw tab top

            g.setColor( CompiereLookAndFeel.getPrimaryControlDarkShadow());    // primary1
            g.drawLine( right - 5,3,right,3 );
            g.drawLine( right - 6,4,right,4 );

            // Draw folder front

            g.setColor( CompiereLookAndFeel.getPrimaryControl());    // primary3
            g.fillRect( 2,7,13,8 );

            // Draw tab bottom

            g.setColor( CompiereLookAndFeel.getPrimaryControlShadow());    // primary2
            g.drawLine( right - 6,5,right - 1,5 );

            // Draw outline

            g.setColor( CompiereLookAndFeel.getPrimaryControlInfo());    // black
            g.drawLine( 0,6,0,bottom );                                  // left side
            g.drawLine( 1,5,right - 7,5 );                               // first part of top
            g.drawLine( right - 6,6,right - 1,6 );                       // second part of top
            g.drawLine( right,5,right,bottom );                          // right side
            g.drawLine( 0,bottom,right,bottom );                         // bottom

            // Draw highlight

            g.setColor( CompiereLookAndFeel.getPrimaryControlHighlight());    // white
            g.drawLine( 1,6,1,bottom - 1 );
            g.drawLine( 1,6,right - 7,6 );
            g.drawLine( right - 6,7,right - 1,7 );
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getShift() {
            return 0;
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getAdditionalHeight() {
            return 0;
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getIconWidth() {
            return s_icon16Size.width;
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public int getIconHeight() {
            return s_icon16Size.height + getAdditionalHeight();
        }
    }    // FolderIcon16
}        // ImageFactory



/*
 *  @(#)ImageFactory.java   25.03.06
 * 
 *  Fin del fichero ImageFactory.java
 *  
 *  Versión 2.2
 *
 */
