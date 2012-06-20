/*
 * @(#)Setup.java   11.jun 2007  Versión 2.2
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

import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.AEnv;
import org.openXpertya.util.CLogFile;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * DescripciÃÂ³n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Setup extends JFrame implements ActionListener {

    // Static UI

    /** Descripción de Campo */
    static ResourceBundle	res	= ResourceBundle.getBundle("org.openXpertya.install.SetupRes");

    /** Descripción de Campo */
    private JMenuBar	menuBar	= new JMenuBar();

    /** Descripción de Campo */
    private JMenu	menuFile	= new JMenu();

    /** Descripción de Campo */
    private JMenuItem	menuFileExit	= new JMenuItem();

    /** Descripción de Campo */
    private JMenu	menuHelp	= new JMenu();

    /** Descripción de Campo */
    private JMenuItem	menuHelpInfo	= new JMenuItem();

    /** Descripción de Campo */
    private JLabel	statusBar	= new JLabel();

    /** Descripción de Campo */
    private ConfigurationPanel	configurationPanel	= new ConfigurationPanel(statusBar);

    /** Descripción de Campo */
    private BorderLayout	borderLayout	= new BorderLayout();

    /** Descripción de Campo */
    private JPanel	contentPane;

    /**
     * Constructor ...
     *
     */
    public Setup() {

        CLogger.get().info(OpenXpertya.getSummaryAscii());
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // addWindowListener(this);
        try {
            jbInit();
        } catch (Exception e) {

            e.printStackTrace();
            System.exit(1);
        }

        AEnv.showCenterScreen(this);

        try {

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            configurationPanel.dynInit();
            AEnv.positionCenterScreen(this);
            setCursor(Cursor.getDefaultCursor());

        } catch (Exception e) {

            e.printStackTrace();
            System.exit(1);
        }

    }		// Setup

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == menuFileExit) {
            System.exit(0);
        } else if (e.getSource() == menuHelpInfo) {
            new Setup_Help(this);
        }

    }		// actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        this.setIconImage(OpenXpertya.getImage16());
        contentPane	= (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout);
        this.setTitle(res.getString("InstalarServidorOXP"));
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setText(" ");
        menuFile.setText(res.getString("File"));
        menuFileExit.setText(res.getString("Exit"));
        menuFileExit.addActionListener(this);
        menuHelp.setText(res.getString("Help"));
        menuHelpInfo.setText(res.getString("Help"));
        menuHelpInfo.addActionListener(this);
        borderLayout.setHgap(5);
        borderLayout.setVgap(5);
        menuFile.add(menuFileExit);
        menuHelp.add(menuHelpInfo);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        this.setJMenuBar(menuBar);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(configurationPanel, BorderLayout.CENTER);

    }		// jbInit

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        CLogMgt.initialize(true);

        Handler	fileHandler	= new CLogFile(System.getProperty("user.dir"), false);

        CLogMgt.addHandler(fileHandler);

        // Log Level
        if (args.length > 0) {
            CLogMgt.setLevel(args[0]);
        } else {
            CLogMgt.setLevel(Level.INFO);
        }

        // File Loger at least FINE
        if (fileHandler.getLevel().intValue() > Level.FINE.intValue()) {
            fileHandler.setLevel(Level.FINE);
        }

        new Setup();

    }		// main
}	// Setup



/*
 * @(#)Setup.java   11.jun 2007
 * 
 *  Fin del fichero Setup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
