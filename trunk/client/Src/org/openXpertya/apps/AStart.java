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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class AStart extends JApplet {

    /** Descripción de Campos */

    boolean isStandalone = false;

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param def
     *
     * @return
     */

    public String getParameter( String key,String def ) {
        return isStandalone
               ?System.getProperty( key,def )
               :( (getParameter( key ) != null)
                  ?getParameter( key )
                  :def );
    }

    /**
     * Constructor de la clase ...
     *
     */

    public AStart() {}

    /**
     * Descripción de Método
     *
     */

    public void init() {
        try {
            jbInit();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setSize( new Dimension( 400,300 ));
    }

    /**
     * Descripción de Método
     *
     */

    public void start() {}

    /**
     * Descripción de Método
     *
     */

    public void stop() {}

    /**
     * Descripción de Método
     *
     */

    public void destroy() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAppletInfo() {
        return "Start Applet";
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[][] getParameterInfo() {
        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        AStart applet = new AStart();

        applet.isStandalone = true;

        JFrame frame = new JFrame();

        // EXIT_ON_CLOSE == 3

        frame.setDefaultCloseOperation( 3 );
        frame.setTitle( "Start Applet" );
        frame.getContentPane().add( applet,BorderLayout.CENTER );
        applet.init();
        applet.start();
        frame.setSize( 400,320 );

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setLocation(( d.width - frame.getSize().width ) / 2,( d.height - frame.getSize().height ) / 2 );
        frame.setVisible( true );
    }

    // static initializer for setting look & feel

    static {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());

            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        } catch( Exception e ) {
        }
    }
}    // AStert



/*
 *  @(#)AStart.java   02.07.07
 * 
 *  Fin del fichero AStart.java
 *  
 *  Versión 2.2
 *
 */
