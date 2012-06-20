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



package org.openXpertya.grid;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Customize extends JDialog {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     */

    public Customize( Frame frame ) {
        super( frame,"Customize ",true );

        try {
            jbInit();
            pack();
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructor de la clase ...
     *
     */

    public Customize() {
        this( null );
    }

    /** Descripción de Campos */

    private JPanel panel1 = new JPanel();

    /** Descripción de Campos */

    private BorderLayout borderLayout1 = new BorderLayout();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        panel1.setLayout( borderLayout1 );
        getContentPane().add( panel1 );
    }
}



/*
 *  @(#)Customize.java   02.07.07
 * 
 *  Fin del fichero Customize.java
 *  
 *  Versión 2.2
 *
 */
