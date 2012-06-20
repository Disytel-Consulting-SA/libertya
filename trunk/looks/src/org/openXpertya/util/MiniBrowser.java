/*
 * @(#)MiniBrowser.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import org.compiere.plaf.CompierePLAF;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class MiniBrowser extends JDialog {

    /** Descripción de Campos */
    private JScrollPane	scrollPane	= new JScrollPane();

    /** Descripción de Campos */
    private JEditorPane	editorPane	= new JEditorPane();

    /**
     * Constructor de la clase ...
     *
     */
    public MiniBrowser() {
        this(null);
    }		// MiniBrowser

    /**
     * Constructor de la clase ...
     *
     *
     * @param url
     */
    public MiniBrowser(String url) {

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setURL(url);
        CompierePLAF.showCenterScreen(this);

    }		// MiniBrowser

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        scrollPane.setPreferredSize(new Dimension(500, 500));
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().add(editorPane, null);

    }		// jbInit

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param url
     */
    private void setURL(String url) {

        String	myURL	= url;

        if (url == null) {
            myURL	= "http://www.openxpertya.org";
        }

        this.setTitle(myURL);

        // Set URL
        URL	realURL	= null;

        try {
            realURL	= new URL(myURL);
        } catch (Exception e) {
            System.err.println("MiniBrowser.setURL (set) - " + e.toString());
        }

        if (realURL == null) {
            return;
        }

        // Open
        try {
            editorPane.setPage(realURL);
        } catch (Exception e) {
            System.err.println("MiniBrowser.setURL (open) - " + e.toString());
        }

    }		// setURL
}	// MiniBrowser



/*
 * @(#)MiniBrowser.java   02.jul 2007
 * 
 *  Fin del fichero MiniBrowser.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
