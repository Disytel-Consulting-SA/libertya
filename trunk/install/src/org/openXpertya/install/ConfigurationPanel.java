/*
 * @(#)ConfigurationPanel.java   11.jun 2007  Versión 2.2
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

import org.apache.tools.ant.Main;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CPassword;
import org.compiere.swing.CTextField;

import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.SwingWorker;
import org.openXpertya.util.CLogger;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigurationPanel extends CPanel implements ActionListener {

    /** Descripción de Campo */
    static ResourceBundle	res	= ResourceBundle.getBundle("org.openXpertya.install.SetupRes");

    /** Descripción de Campo */
    private static ImageIcon	iSave	= new ImageIcon(OpenXpertya.class.getResource("images/Save16.gif"));

    /** Descripción de Campo */
    private static ImageIcon	iOpen	= new ImageIcon(ConfigurationPanel.class.getResource("openFile.gif"));

    /** Descripción de Campo */
    private static ImageIcon	iHelp	= new ImageIcon(OpenXpertya.class.getResource("images/Help16.gif"));

    /** Descripción de Campo */
    private static final int	FIELDLENGTH	= 15;

    /** Descripción de Campo */
    private volatile boolean	m_success	= false;

    /** Descripción de Campo */
    private volatile boolean	m_testing	= false;

    /** Descripción de Campo */
    private Setup	m_setup	= null;

    /** Descripción de Campo */
    private ConfigurationData	m_data	= new ConfigurationData(this);

    // Java

    /** Descripción de Campo */
    private CLabel	lJavaHome	= new CLabel();

    // -------------   Static UI

    /** Descripción de Campo */
    private GridBagLayout	gridBagLayout	= new GridBagLayout();

    /** Descripción de Campo */
    CTextField	fJavaHome	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CCheckBox	okJavaHome	= new CCheckBox();

    // OpenXpertya - KeyStore

    /** Descripción de Campo */
    private CLabel	lOXPHome	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lJavaType	= new CLabel();

    /** Descripción de Campo */
    CTextField	fOXPHome	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CComboBox	fJavaType	= new CComboBox(ConfigurationData.JAVATYPE);

    /** Descripción de Campo */
    private CButton	bJavaHome	= new CButton(iOpen);

    /** Descripción de Campo */
    CCheckBox	okOXPHome	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okKeyStore	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okJNPPort	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okDeployDir	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okAppsServer	= new CCheckBox();

    // Web Ports

    /** Descripción de Campo */
    private CLabel	lWebPort	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lKeyStore	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lJNPPort	= new CLabel();

    // Deployment Directory - JNP

    /** Descripción de Campo */
    private CLabel	lDeployDir	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lAppsType	= new CLabel();

    // Apps Server  - Type

    /** Descripción de Campo */
    CLabel	lAppsServer	= new CLabel();

    /** Descripción de Campo */
    CTextField	fWebPort	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CPassword	fKeyStore	= new CPassword();

    /** Descripción de Campo */
    CTextField	fJNPPort	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CTextField	fDeployDir	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CComboBox	fAppsType	= new CComboBox(ConfigurationData.APPSTYPE);

    /** Descripción de Campo */
    CTextField	fAppsServer	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    private CButton	bOXPHome	= new CButton(iOpen);

    /** Descripción de Campo */
    CButton	bDeployDir	= new CButton(iOpen);

    /** Descripción de Campo */
    CCheckBox	okWebPort	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okSSLPort	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okMailUser	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okMailServer	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okDatabaseUser	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okDatabaseSystem	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okDatabaseServer	= new CCheckBox();

    /** Descripción de Campo */
    CCheckBox	okDatabaseSQL	= new CCheckBox();

    /** Descripción de Campo */
    private CLabel	lSystemPassword	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lSSLPort	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lMailUser	= new CLabel();

    //

    /** Descripción de Campo */
    CLabel	lMailServer	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lMailPassword	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lDatabaseUser	= new CLabel();

    // Database

    /** Descripción de Campo */
    private CLabel	lDatabaseType	= new CLabel();

    //

    /** Descripción de Campo */
    CLabel	lDatabaseServer	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lDatabasePort	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lDatabasePassword	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lDatabaseName	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lDatabaseDiscovered	= new CLabel();

    /** Descripción de Campo */
    private CLabel	lAdminEMail	= new CLabel();

    /** Descripción de Campo */
    CPassword	fSystemPassword	= new CPassword();

    /** Descripción de Campo */
    CTextField	fSSLPort	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CTextField	fMailUser	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CTextField	fMailServer	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CPassword	fMailPassword	= new CPassword();

    /** Descripción de Campo */
    CTextField	fDatabaseUser	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CComboBox	fDatabaseType	= new CComboBox(ConfigurationData.DBTYPE);

    /** Descripción de Campo */
    CTextField	fDatabaseServer	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CTextField	fDatabasePort	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CPassword	fDatabasePassword	= new CPassword();

    /** Descripción de Campo */
    CTextField	fDatabaseName	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    CComboBox	fDatabaseDiscovered	= new CComboBox();

    /** Descripción de Campo */
    CTextField	fAdminEMail	= new CTextField(FIELDLENGTH);

    /** Descripción de Campo */
    private CButton	bTest	= new CButton();

    /** Descripción de Campo */
    private CButton	bSave	= new CButton(iSave);

    //

    /** Descripción de Campo */
    private CButton	bHelp	= new CButton(iHelp);

    /** Descripción de Campo */
    private String	m_errorString;

    /** Descripción de Campo */
    private JLabel	m_statusBar;

    /**
     * Constructor ...
     *
     *
     * @param statusBar
     */
    public ConfigurationPanel(JLabel statusBar) {

        m_statusBar	= statusBar;

        try {
            jbInit();
        } catch (Exception e) {

            e.printStackTrace();
            System.exit(1);
        }

    }		// ConfigurationPanel

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (m_testing) {
            return;
        }

        // TNS Name Changed
        if (e.getSource() == fDatabaseDiscovered) {

            String	dbName	= (String) fDatabaseDiscovered.getSelectedItem();

            if ((dbName != null) && (dbName.length() > 0)) {
                fDatabaseName.setText(dbName);
            }
        }

        //
        else if (e.getSource() == fJavaType) {
            m_data.initJava();
        } else if (e.getSource() == fAppsType) {
            m_data.initAppsServer();
        } else if (e.getSource() == fDatabaseType) {

            m_data.initDatabase("");

            //

        } else if (e.getSource() == bJavaHome) {
            setPath(fJavaHome);
        } else if (e.getSource() == bOXPHome) {
            setPath(fOXPHome);
        } else if (e.getSource() == bDeployDir) {
            setPath(fDeployDir);
        } else if (e.getSource() == bHelp) {
            new Setup_Help((Frame) SwingUtilities.getWindowAncestor(this));
        } else if (e.getSource() == bTest) {
            startTest(false);
        } else if (e.getSource() == bSave) {
            startTest(true);
        }

    }		// actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean dynInit() {
        return m_data.load();
    }		// dynInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        this.setLayout(gridBagLayout);

        Insets	bInsets	= new Insets(0, 5, 0, 5);

        // Java
        lJavaHome.setToolTipText(res.getString("JavaHomeInfo"));
        lJavaHome.setText(res.getString("JavaHome"));
        fJavaHome.setText(".");
        okJavaHome.setEnabled(false);
        bJavaHome.setMargin(bInsets);
        bJavaHome.setToolTipText(res.getString("JavaHomeInfo"));
        lJavaType.setToolTipText(res.getString("JavaTypeInfo"));
        lJavaType.setText(res.getString("JavaType"));
        fJavaType.setPreferredSize(fJavaHome.getPreferredSize());
        this.add(lJavaHome, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fJavaHome, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okJavaHome, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        this.add(bJavaHome, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        this.add(lJavaType, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fJavaType, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));

        // OXPHome - KeyStore
        lOXPHome.setToolTipText(res.getString("OXPHomeInfo"));
        lOXPHome.setText(res.getString("OXPHome"));
        fOXPHome.setText(".");
        okOXPHome.setEnabled(false);
        bOXPHome.setMargin(bInsets);
        bOXPHome.setToolTipText(res.getString("OXPHomeInfo"));
        
        lKeyStore.setText(res.getString("KeyStorePassword"));
        lKeyStore.setToolTipText(res.getString("KeyStorePasswordInfo"));
        
        fKeyStore.setText("");
        okKeyStore.setEnabled(false);
        this.add(lOXPHome, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fOXPHome, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okOXPHome, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        this.add(bOXPHome, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        this.add(lKeyStore, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fKeyStore, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okKeyStore, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // Apps Server - Type
        lAppsServer.setToolTipText(res.getString("AppsServerInfo"));
        lAppsServer.setText(res.getString("AppsServer"));
        lAppsServer.setFont(lAppsServer.getFont().deriveFont(Font.BOLD));
        fAppsServer.setText(".");
        okAppsServer.setEnabled(false);
        lAppsType.setToolTipText(res.getString("AppsTypeInfo"));
        lAppsType.setText(res.getString("AppsType"));
        fAppsType.setPreferredSize(fAppsServer.getPreferredSize());
        this.add(lAppsServer, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fAppsServer, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
        this.add(okAppsServer, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 5, 5), 0, 0));
        this.add(lAppsType, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fAppsType, new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));

        // Deployment - JNP
        lDeployDir.setToolTipText(res.getString("DeployDirInfo"));
        lDeployDir.setText(res.getString("DeployDir"));
        fDeployDir.setText(".");
        okDeployDir.setEnabled(false);
        bDeployDir.setMargin(bInsets);
        bDeployDir.setToolTipText(res.getString("DeployDirInfo"));
        lJNPPort.setToolTipText(res.getString("JNPPortInfo"));
        lJNPPort.setText(res.getString("JNPPort"));
        fJNPPort.setText(".");
        okJNPPort.setEnabled(false);
        this.add(lDeployDir, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fDeployDir, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okDeployDir, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        this.add(bDeployDir, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lJNPPort, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fJNPPort, new GridBagConstraints(5, 3, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okJNPPort, new GridBagConstraints(6, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // Web Ports
        lWebPort.setToolTipText(res.getString("WebPortInfo"));
        lWebPort.setText(res.getString("WebPort"));
        fWebPort.setText(".");
        okWebPort.setEnabled(false);
        lSSLPort.setToolTipText(res.getString("SSLInfo"));
        lSSLPort.setText(res.getString("SSL"));
        fSSLPort.setText(".");
        okSSLPort.setEnabled(false);
        this.add(lWebPort, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fWebPort, new GridBagConstraints(1, 4, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okWebPort, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        this.add(lSSLPort, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fSSLPort, new GridBagConstraints(5, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okSSLPort, new GridBagConstraints(6, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // Database Server - Type
        lDatabaseServer.setToolTipText(res.getString("DatabaseServerInfo"));
        lDatabaseServer.setText(res.getString("DatabaseServer"));
        lDatabaseServer.setFont(lDatabaseServer.getFont().deriveFont(Font.BOLD));
        okDatabaseServer.setEnabled(false);
        lDatabaseType.setToolTipText(res.getString("DatabaseTypeInfo"));
        lDatabaseType.setText(res.getString("DatabaseType"));
        fDatabaseType.setPreferredSize(fDatabaseServer.getPreferredSize());
        this.add(lDatabaseServer, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fDatabaseServer, new GridBagConstraints(1, 5, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
        this.add(okDatabaseServer, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 5, 5), 0, 0));
        this.add(lDatabaseType, new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fDatabaseType, new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));

        // DB Name - TNS
        lDatabaseName.setToolTipText(res.getString("DatabaseNameInfo"));
        lDatabaseName.setText(res.getString("DatabaseName"));
        fDatabaseName.setText(".");
        lDatabaseDiscovered.setToolTipText(res.getString("TNSNameInfo"));
        lDatabaseDiscovered.setText(res.getString("TNSName"));
        fDatabaseDiscovered.setEditable(true);
        fDatabaseDiscovered.setPreferredSize(fDatabaseName.getPreferredSize());
        okDatabaseSQL.setEnabled(false);
        this.add(lDatabaseName, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fDatabaseName, new GridBagConstraints(1, 6, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okDatabaseSQL, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        //this.add(lDatabaseDiscovered, new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
        //this.add(fDatabaseDiscovered, new GridBagConstraints(5, 6, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));

        // Port - System
        lDatabasePort.setToolTipText(res.getString("DatabasePortInfo"));
        lDatabasePort.setText(res.getString("DatabasePort"));
        fDatabasePort.setText(".");
        lSystemPassword.setToolTipText(res.getString("SystemPasswordInfo"));
        lSystemPassword.setText(res.getString("SystemPassword"));
        fSystemPassword.setText(".");
        okDatabaseSystem.setEnabled(false);
        this.add(lDatabasePort, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fDatabasePort, new GridBagConstraints(1, 7, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(lSystemPassword, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fSystemPassword, new GridBagConstraints(5, 7, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okDatabaseSystem, new GridBagConstraints(6, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // User - Password
        lDatabaseUser.setToolTipText(res.getString("DatabaseUserInfo"));
        lDatabaseUser.setText(res.getString("DatabaseUser"));
        fDatabaseUser.setText(".");
        lDatabasePassword.setToolTipText(res.getString("DatabasePasswordInfo"));
        lDatabasePassword.setText(res.getString("DatabasePassword"));
        fDatabasePassword.setText(".");
        okDatabaseUser.setEnabled(false);
        this.add(lDatabaseUser, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fDatabaseUser, new GridBagConstraints(1, 8, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(lDatabasePassword, new GridBagConstraints(4, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fDatabasePassword, new GridBagConstraints(5, 8, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okDatabaseUser, new GridBagConstraints(6, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // Mail Server - Email
        lMailServer.setToolTipText(res.getString("MailServerInfo"));
        lMailServer.setText(res.getString("MailServer"));
        lMailServer.setFont(lMailServer.getFont().deriveFont(Font.BOLD));
        fMailServer.setText(".");
        lAdminEMail.setToolTipText(res.getString("AdminEMailInfo"));
        lAdminEMail.setText(res.getString("AdminEMail"));
        fAdminEMail.setText(".");
        okMailServer.setEnabled(false);
        this.add(lMailServer, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fMailServer, new GridBagConstraints(1, 9, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
        this.add(okMailServer, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 5, 5), 0, 0));
        this.add(lAdminEMail, new GridBagConstraints(4, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
        this.add(fAdminEMail, new GridBagConstraints(5, 9, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));

        // Mail User = Password
        lMailUser.setToolTipText(res.getString("MailUserInfo"));
        lMailUser.setText(res.getString("MailUser"));
        fMailUser.setText(".");
        lMailPassword.setToolTipText(res.getString("MailPasswordInfo"));
        lMailPassword.setText(res.getString("MailPassword"));
        fMailPassword.setText(".");
        okMailUser.setEnabled(false);
        this.add(lMailUser, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fMailUser, new GridBagConstraints(1, 10, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(lMailPassword, new GridBagConstraints(4, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(fMailPassword, new GridBagConstraints(5, 10, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
        this.add(okMailUser, new GridBagConstraints(6, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

        // End
        bTest.setToolTipText(res.getString("TestInfo"));
        bTest.setText(res.getString("Test"));
        bSave.setToolTipText(res.getString("SaveInfo"));
        bSave.setText(res.getString("Save"));
        bHelp.setToolTipText(res.getString("HelpInfo"));
        this.add(bTest, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0));
        this.add(bHelp, new GridBagConstraints(3, 11, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0));
        this.add(bSave, new GridBagConstraints(5, 11, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0, 0));

        //
        bOXPHome.addActionListener(this);
        bJavaHome.addActionListener(this);
        bDeployDir.addActionListener(this);
        fJavaType.addActionListener(this);
        fAppsType.addActionListener(this);
        fDatabaseType.addActionListener(this);
        fDatabaseDiscovered.addActionListener(this);
        bHelp.addActionListener(this);
        bTest.addActionListener(this);
        bSave.addActionListener(this);

    }		// jbInit

    /**
     * Descripción de Método
     *
     */
    private void save() {

        if (!m_success) {
            return;
        }

        bSave.setEnabled(false);
        bTest.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (!m_data.save()) {
            return;
        }

        // Final Info
        JOptionPane.showConfirmDialog(this, res.getString("EnvironmentSaved"), res.getString("InstalarServidorOXP"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

        try {

            CLogger.get().info("Arrancando Ant ... ");
            System.setProperty("ant.home", ".");

            String[]	args	= new String[] { "setup" };

            // Launcher.main (args);   //      calls System.exit
            Main	antMain	= new Main();

            antMain.startAnt(args, null, null);

        } catch (Exception e) {
            CLogger.get().log(Level.SEVERE, "ant", e);
        }

        // To be sure
        ((Frame) SwingUtilities.getWindowAncestor(this)).dispose();
        System.exit(0);		// remains active when License Dialog called

    }				// save

    /**
     * Descripción de Método
     *
     *
     * @param cb
     * @param resString
     * @param pass
     * @param critical
     * @param errorMsg
     */
    void signalOK(CCheckBox cb, String resString, boolean pass, boolean critical, String errorMsg) {

        m_errorString	= res.getString(resString);
        cb.setSelected(pass);

        if (pass) {
            cb.setToolTipText(null);
        } else {

            cb.setToolTipText(errorMsg);
            m_errorString	+= " \n(" + errorMsg + ")";
        }

        if (!pass && critical) {
            cb.setBackground(Color.RED);
        } else {
            cb.setBackground(Color.GREEN);
        }

    }		// setOK

    /**
     * Descripción de Método
     *
     *
     * @param saveIt
     *
     * @return
     */
    private SwingWorker startTest(final boolean saveIt) {

        SwingWorker	worker	= new SwingWorker() {

            // Start it
            public Object construct() {

                m_testing	= true;
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                bTest.setEnabled(false);
                m_success	= false;
                m_errorString	= null;

                try {
                    test();
                } catch (Exception ex) {

                    ex.printStackTrace();
                    m_errorString	+= "\n" + ex.toString();
                }

                //
                setCursor(Cursor.getDefaultCursor());

                if (m_errorString == null) {
                    m_success	= true;
                }

                bTest.setEnabled(true);
                m_testing	= false;

                return new Boolean(m_success);
            }

            // Finish it
            public void finished() {

                if (m_errorString != null) {

                    CLogger.get().severe(m_errorString);
                    JOptionPane.showConfirmDialog(m_statusBar.getParent(), m_errorString, res.getString("ServerError"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

                } else if (saveIt) {
                    save();
                }
            }
        };

        worker.start();

        return worker;

    }		// startIt

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void test() throws Exception {

        bSave.setEnabled(false);

        if (!m_data.test()) {
            return;
        }

        //
        m_statusBar.setText(res.getString("Ok"));
        bSave.setEnabled(true);
        m_errorString	= null;

    }		// test

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param field
     */
    private void setPath(CTextField field) {

        JFileChooser	fc	= new JFileChooser(field.getText());

        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle(field.getToolTipText());

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fc.getSelectedFile().getAbsolutePath());
        }

    }		// setPath

    /**
     * Descripción de Método
     *
     *
     * @param text
     */
    protected void setStatusBar(String text) {
        m_statusBar.setText(text);
    }		// setStatusBar
}	// ConfigurationPanel



/*
 * @(#)ConfigurationPanel.java   11.jun 2007
 * 
 *  Fin del fichero ConfigurationPanel.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
