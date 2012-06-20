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



package org.openXpertya.apps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AGlassPane extends JPanel implements MouseListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public AGlassPane() {
        this.setOpaque( false );
        this.setVisible( false );
        this.addMouseListener( this );
    }    // AGlassPane

    /** Descripción de Campos */

    public static Image s_image = Env.getImage( "OXP10030.gif" );

    /** Descripción de Campos */

    public static Font s_font = new Font( "Dialog",3,14 );

    /** Descripción de Campos */

    public static Color s_color = CompierePLAF.getTextColor_OK();

    /** Descripción de Campos */

    private static final int GAP = 4;

    /** Descripción de Campos */

    private String m_message = Msg.getMsg( Env.getCtx(),"Processing" );

    /** Descripción de Campos */

    private Timer m_timer;

    /** Descripción de Campos */

    private int m_timervalue = 0;

    /** Descripción de Campos */

    private int m_timermax = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AGlassPane.class );

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     */

    public void setMessage( String AD_Message ) {
        if( AD_Message == null ) {
            m_message = Msg.getMsg( Env.getCtx(),"Processing" );
        } else if( AD_Message.length() == 0 ) {
            m_message = AD_Message;    // nothing
        } else {
            m_message = Msg.getMsg( Env.getCtx(),AD_Message );
        }

        if( isVisible()) {
            repaint();
        }
    }    // setMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessage() {
        return m_message;
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @param time
     */

    public void setBusyTimer( int time ) {
        log.config( "AGlassPane.setBusyTimer - " + time );

        // should we display a progress bar?

        if( time < 2 ) {
            m_timermax = 0;

            if( isVisible()) {
                repaint();
            }

            return;
        }

        m_timermax   = time;
        m_timervalue = 0;

        // Start Timer

        m_timer = new Timer( 1000,this );    // every second
        m_timer.start();

        if( !isVisible()) {
            setVisible( true );
        }

        repaint();
    }    // setBusyTimer

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_timermax > 0 ) {
            m_timervalue++;

            if( m_timervalue > m_timermax ) {
                m_timervalue = 0;
            }

            repaint();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param g
     */

    public void paintComponent( Graphics g ) {
        Dimension panelSize = getSize();

        g.setColor( new Color( 1f,1f,1f,0.4f ));    // .5 is a bit too light
        g.fillRect( 0,0,panelSize.width,panelSize.height );

        //

        g.setFont( s_font );
        g.setColor( s_color );

        FontMetrics fm          = g.getFontMetrics();
        Dimension   messageSize = new Dimension( fm.stringWidth( m_message ),fm.getAscent() + fm.getDescent());
        Dimension imageSize = new Dimension( s_image.getWidth( this ),s_image.getHeight( this ));
        Dimension progressSize = new Dimension( 150,15 );

        // System.out.println("Panel=" + panelSize + " - Message=" + messageSize + " - Image=" + imageSize + " - Progress=" + progressSize);

        // Horizontal layout

        int height = imageSize.height + GAP + messageSize.height + GAP + progressSize.height;

        if( height > panelSize.height ) {
            log.log( Level.SEVERE,"AGlassPane.paintComponent - Panel too small - height=" + panelSize.height );

            return;
        }

        int yImage    = ( panelSize.height / 2 ) - ( height / 2 );
        int yMessage  = yImage + imageSize.height + GAP + fm.getAscent();
        int yProgress = yMessage + fm.getDescent() + GAP;

        // System.out.println("yImage=" + yImage + " - yMessage=" + yMessage);

        // Vertical layout

        if( (imageSize.width > panelSize.width) || (messageSize.width > panelSize.width) ) {
            log.log( Level.SEVERE,"AGlassPane.paintComponent - Panel too small - width=" + panelSize.width );

            return;
        }

        int xImage    = ( panelSize.width / 2 ) - ( imageSize.width / 2 );
        int xMessage  = ( panelSize.width / 2 ) - ( messageSize.width / 2 );
        int xProgress = ( panelSize.width / 2 ) - ( progressSize.width / 2 );

        g.drawImage( s_image,xImage,yImage,this );
        g.drawString( m_message,xMessage,yMessage );

        if( m_timermax > 0 ) {
            int pWidth = progressSize.width / m_timermax * m_timervalue;

            g.setColor( CompierePLAF.getPrimary3());
            g.fill3DRect( xProgress,yProgress,pWidth,progressSize.height,true );
            g.setColor( CompierePLAF.getPrimary2());
            g.draw3DRect( xProgress,yProgress,progressSize.width,progressSize.height,true );
        }
    }    // paintComponent

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        if( isVisible()) {
            e.consume();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {
        if( isVisible()) {
            e.consume();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {
        if( isVisible()) {
            e.consume();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {
        if( isVisible()) {
            e.consume();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {
        if( isVisible()) {
            e.consume();
        }
    }
}    // AGlassPane



/*
 *  @(#)AGlassPane.java   02.07.07
 * 
 *  Fin del fichero AGlassPane.java
 *  
 *  Versión 2.2
 *
 */
