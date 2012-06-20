/*
 * @(#)IniDialog.java   12.oct 2007  Versión 2.2
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.compiere.plaf.CompierePLAF;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public final class IniDialog extends JDialog implements ActionListener {

    /** Descripción de Campos */
    static ResourceBundle	s_res	= ResourceBundle.getBundle("org.openXpertya.util.IniRes");

    /** Descripción de Campos */
    private static Logger	log	= Logger.getLogger(IniDialog.class.getName());

    /** Descripción de Campos */
    private boolean	m_accept	= false;

    /** Descripción de Campos */
    private JPanel	mainPanel	= new JPanel();

    /** Descripción de Campos */
    private BorderLayout	mainLayout	= new BorderLayout();

    /** Descripción de Campos */
    private JScrollPane	scrollPane	= new JScrollPane();

    /** Descripción de Campos */
    private JPanel	southPanel	= new JPanel();

    /** Descripción de Campos */
    private FlowLayout	southLayout	= new FlowLayout();

    /** Descripción de Campos */
    private JLabel	southLabel	= new JLabel();

    /** Descripción de Campos */
    private JEditorPane	licensePane	= new JEditorPane();

    /** Descripción de Campos */
    private JButton	bReject	= CompierePLAF.getCancelButton();

    /** Descripción de Campos */
    private JButton	bAccept	= CompierePLAF.getOKButton();

    /**
     * Constructor de la clase ...
     *
     */
    public IniDialog() {

        super();

        try {

            jbInit();

            // get License file
            String	where	= s_res.getString("license_htm");
          

            if ((where == null) || (where.length() == 0)) {

                log.fine("No license pointer in resource");
                where	= "org/openXpertya/install/Licencia.html";
            }

            URL		url	= null;
            ClassLoader	cl	= getClass().getClassLoader();

            if (cl != null) {		// Bootstrap
                url	= cl.getResource(where);
            }

            if (url == null) {

                log.fine("No encuentro la licencia localmente");
                url	= new URL("http://www.libertya.org/LBY-1.0.html");
            }

            if (url == null) {
                cmd_reject();
            }

            //
            licensePane.setPage(url);
            CompierePLAF.showCenterScreen(this);

        } catch (Exception ex) {

            log.log(Level.SEVERE, "init", ex);
            cmd_reject();
        }

    }		// IniDialog

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static final boolean accept() {

        IniDialog	id	= new IniDialog();

        if (id.isAccepted()) {

            log.info("Licencia aceptada");

            return true;
        }

        System.exit(10);

        return false;		// nunca ejecutada.

    }				// aceptada

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public final void actionPerformed(ActionEvent e) {

        if (e.getSource() == bAccept) {
            m_accept	= true;
        }

        dispose();

    }		// actionPerformed

    /**
     * Descripción de Método
     *
     */
    public final void cmd_reject() {

        String	info	= "License rejected or expired";

        try {
            info	= s_res.getString("License_rejected");
        } catch (Exception e) {}

        log.severe(info);
        System.exit(10);

    }		// cmd_reject

    /**
     * Descripción de Método
     *
     */
    public final void dispose() {

        super.dispose();

        if (!m_accept) {
            cmd_reject();
        }

    }		// dispose

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        setTitle("Libertya Software Libre de Gestion - " + s_res.getString("Licencia_OXP"));
        southLabel.setText(s_res.getString("Do_you_accept"));
        bReject.setText(s_res.getString("No"));
        bAccept.setText(s_res.getString("Yes_I_Understand"));

        //
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);

        //
        mainPanel.setLayout(mainLayout);
        bReject.setForeground(Color.red);
        bReject.addActionListener(this);
        bAccept.addActionListener(this);
        southPanel.setLayout(southLayout);
        southLayout.setAlignment(FlowLayout.RIGHT);
        licensePane.setEditable(false);
        licensePane.setContentType("text/html");
        scrollPane.setPreferredSize(new Dimension(700, 400));
        southPanel.add(southLabel, null);
        getContentPane().add(mainPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().add(licensePane, null);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(bReject, null);
        southPanel.add(bAccept, null);

    }		// jbInit

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public final boolean isAccepted() {
        return m_accept;
    }		// isAccepted
}	// IniDialog



/*
 * @(#)IniDialog.java   02.jul 2007
 * 
 *  Fin del fichero IniDialog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
