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



package org.openXpertya.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.net.URL;

import org.openXpertya.OpenXpertya;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Splash extends Frame {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Splash getSplash() {
        return getSplash( "Iniciando..." );
    }    // getSplash

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static Splash getSplash( String text ) {
        if( s_splash == null ) {
            s_splash = new Splash( text );
        } else {
            s_splash.setText( text );
        }

        return s_splash;
    }    // getSplash

    /** Descripción de Campos */

    private static Splash s_splash = null;

    /**
     * Constructor de la clase ...
     *
     *
     * @param text
     */

    public Splash( String text ) {
        super( "Libertya" );
        message.setText( text );

        try {
            jbInit();
        } catch( Exception e ) {
            System.out.println( "Splash" );
            e.printStackTrace();
        }

        display();
    }    // Splash

    /** Descripción de Campos */

    private MediaTracker tracker = new MediaTracker( this );

    //

    /** Descripción de Campos */

    private CImage cImage = new CImage();

    /** Descripción de Campos */

    private AImage aImage = new AImage();

    //

    /** Descripción de Campos */

    private Label productLabel = new Label();

    /** Descripción de Campos */

    private Panel contentPanel = new Panel();

    /** Descripción de Campos */

    private GridBagLayout contentLayout = new GridBagLayout();

    /** Descripción de Campos */

    private Label message = new Label();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setBackground( Color.white );
        this.setName( "splash" );
        this.setUndecorated( true );

        //

        productLabel.setAlignment( Label.CENTER );
        message.setFont( new java.awt.Font( "SansSerif",3,20 ));    // italic bold 20 pt
        message.setForeground( SystemColor.activeCaption );
        message.setAlignment( Label.CENTER );
        contentPanel.setLayout( contentLayout );
        contentPanel.setName( "splashContent" );
        contentPanel.setBackground( Color.white );

        //

        productLabel.setForeground( new java.awt.Color( 255, 153, 51 ) );
        productLabel.setText( OpenXpertya.getSubtitle());

        // productLabel.setToolTipText(OpenXpertya.getURL());
        //

        contentPanel.add( cImage,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,5,0,10 ),0,0 ));
        contentPanel.add( productLabel,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,5,0,10 ),0,0 ));
        contentPanel.add( message,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,10,10 ),0,0 ));

        //

        this.add( aImage,BorderLayout.WEST );
        this.add( contentPanel,BorderLayout.EAST );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setText( String text ) {
        message.setText( text );
        display();
    }    // setText

    /**
     * Descripción de Método
     *
     */

    public void show() {
        super.show();
        toFront();
    }    // show

    /**
     * Descripción de Método
     *
     */

    private void display() {
        pack();

        Dimension ss     = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bounds = getBounds();

        setBounds(( ss.width - bounds.width ) / 2,( ss.height - bounds.height ) / 2,bounds.width,bounds.height );
        show();
    }    // display

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        super.dispose();
        s_splash = null;
    }    // dispose

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private class CImage extends Component {

        /**
         * Constructor de la clase ...
         *
         */

        public CImage() {
            m_image = OpenXpertya.getImageLogo();
            tracker.addImage( m_image,0 );
        }

        /** Descripción de Campos */

        private Image m_image = null;

        /* The Dimansion */

        /** Descripción de Campos */

        private Dimension m_dim = null;

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public Dimension getPreferredSize() {
            try {
                tracker.waitForID( 0 );
            } catch( Exception e ) {
                System.err.println( "Splash.CImage" );
                e.printStackTrace();
            }

            m_dim = new Dimension( m_image.getWidth( this ),m_image.getHeight( this ));

            return m_dim;
        }    // getPreferredSize

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void paint( Graphics g ) {
            if( tracker.checkID( 0 )) {
                g.drawImage( m_image,0,0,this );
            }
        }    // paint
    }    // CImage


    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private class AImage extends Component {

        /**
         * Constructor de la clase ...
         *
         */

        public AImage() {
            super();

            URL url = org.openXpertya.OpenXpertya.class.getResource( "images/oXp_anim.gif" );

            if( url == null ) {
                url = org.openXpertya.OpenXpertya.class.getResource( "images/oXp_logo.gif" );
            }

            if( url != null ) {
                m_image = Toolkit.getDefaultToolkit().getImage( url );
                tracker.addImage( m_image,1 );
            }
        }    // AImage

        /** Descripción de Campos */

        private Image m_image = null;

        /** Descripción de Campos */

        private Dimension m_dim = null;

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public Dimension getPreferredSize() {
            try {
                tracker.waitForID( 1 );
            } catch( Exception e ) {
                System.err.println( "Splash.AImage" );
                e.printStackTrace();
            }

            m_dim = new Dimension( m_image.getWidth( this ) + 15,m_image.getHeight( this ) + 15 );

            return m_dim;
        }    // getPreferredSize

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void paint( Graphics g ) {
            if( tracker.checkID( 1 )) {
                g.drawImage( m_image,10,10,this );
            }
        }    // paint

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void update( Graphics g ) {
            paint( g );
        }    // update
    }    // AImage
}        // Splash



/*
 *  @(#)Splash.java   25.03.06
 * 
 *  Fin del fichero Splash.java
 *  
 *  Versión 2.2
 *
 */