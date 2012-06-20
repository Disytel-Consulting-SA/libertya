/*
 * @(#)Setup_Help.java   11.jun 2007  Versión 2.2
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



package org.openXpertya.install;

import org.openXpertya.apps.OnlineHelp;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * DescripciÃÂ³n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Setup_Help extends JDialog implements ActionListener {

    /** Descripción de Campo */
    static ResourceBundle	res	= ResourceBundle.getBundle("org.openXpertya.install.SetupRes");

    /** Descripción de Campo */
    private JPanel	mainPanel	= new JPanel();

    /** Descripción de Campo */
    private JPanel	southPanel	= new JPanel();

    /** Descripción de Campo */
    private BorderLayout	mainLayout	= new BorderLayout();

    /** Descripción de Campo */
    private JEditorPane	editorPane	= new OnlineHelp();

    /** Descripción de Campo */
    private JScrollPane	centerScrollPane	= new JScrollPane();

    /** Descripción de Campo */
    private JButton	bOK	= new JButton();

    /**
     * Constructor ...
     *
     *
     * @param parent
     */
    public Setup_Help(Dialog parent) {

        super(parent, true);
        init(parent);

    }		// Setup_Help

    /**
     * Constructor ...
     *
     *
     * @param parent
     */
    public Setup_Help(Frame parent) {

        super(parent, true);
        init(parent);

    }		// Setup_Help

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == bOK) {
            dispose();
        }

    }		// actionPerformed

    /**
     * Descripción de Método
     *
     */
    private void dynInit() {

        try {
            editorPane.setPage("http://www.openxpertya.org/apps/2_0/ayuda/configurar.html");
        } catch (IOException ex) {
            editorPane.setText(res.getString("PleaseCheck") + " http://www.openxpertya.org/apps/2_0/soporte <p>(" + res.getString("UnableToConnect") + ")");
        }

    }		// dynInit

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */
    private void init(Window parent) {

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try {

            jbInit();
            dynInit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Dimension	dlgSize	= getPreferredSize();
        Dimension	frmSize	= parent.getSize();
        Point		loc	= parent.getLocation();

        setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);

        try {

            pack();
            setVisible(true);	// HTML load errors

        } catch (Exception ex) {}

    }			// init

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        // imageLabel.setIcon(new ImageIcon(SetupFrame_AboutBox.class.getResource("[Your Image]")));
        this.setTitle(res.getString("InstalarServidorOXP") + " " + res.getString("Help"));
        mainPanel.setLayout(mainLayout);
        bOK.setText(res.getString("Ok"));
        bOK.addActionListener(this);
        centerScrollPane.setPreferredSize(new Dimension(600, 400));
        this.getContentPane().add(mainPanel, null);
        southPanel.add(bOK, null);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        setResizable(true);
        mainPanel.add(centerScrollPane, BorderLayout.CENTER);
        centerScrollPane.getViewport().add(editorPane, null);
    }		// jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    protected void processWindowEvent(WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
        }

        super.processWindowEvent(e);

    }		// processWindowEvent
}	// Setup_Help



/*
 * @(#)Setup_Help.java   11.jun 2007
 * 
 *  Fin del fichero Setup_Help.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007