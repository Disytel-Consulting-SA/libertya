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

import java.applet.Applet;
import java.awt.HeadlessException;
import java.awt.TextArea;

import org.openXpertya.OpenXpertya;
import org.openXpertya.util.Env;
import org.openXpertya.util.Splash;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AApplet extends Applet {

    /**
     * Constructor de la clase ...
     *
     *
     * @throws HeadlessException
     */

    public AApplet() throws HeadlessException {
        super();
    }    // AApplet

    /**
     * Descripción de Método
     *
     */

    public void init() {
        super.init();

        TextArea ta = new TextArea( OpenXpertya.getSummary());

        add( ta );
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void start() {
        super.start();
        showStatus( OpenXpertya.getSummary());

        //

        Splash splash = Splash.getSplash();

        OpenXpertya.startup( true );    // needs to be here for UI

        AMenu menu = new AMenu();
    }    // start

    /**
     * Descripción de Método
     *
     */

    public void stop() {
        super.stop();
    }    // stop

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        super.destroy();
        Env.exitEnv( 0 );
    }    // destroy
}    // AApplet



/*
 *  @(#)AApplet.java   02.07.07
 * 
 *  Fin del fichero AApplet.java
 *  
 *  Versión 2.2
 *
 */
