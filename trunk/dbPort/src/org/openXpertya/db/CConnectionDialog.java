/*
 * @(#)CConnectionDialog.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Ini;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *  Connection Dialog.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Marek Mosiewicz<marek.mosiewicz@jotel.com.pl> - support for RMI over HTTP
 *  @version    $Id: CConnectionDialog.java,v 1.23 2005/03/11 20:29:00 jjanke Exp $
 */
public class CConnectionDialog extends JDialog implements ActionListener {

    /** Descripción de Campo */
    private static ResourceBundle	res	= ResourceBundle.getBundle("org.openXpertya.db.DBRes");

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(CConnectionDialog.class);

    /** Descripción de Campo */
    public static final String	APPS_PORT_JNP	= "1099";

    /** Descripción de Campo */
    public static final String	APPS_PORT_HTTP	= "80";

    /** Descripción de Campo */
    private CConnection	m_cc	= null;

    /** Descripción de Campo */
    private CConnection	m_ccResult	= null;

    /** Descripción de Campo */
    private boolean	m_updating	= false;

    /** Descripción de Campo */
    private boolean	m_saved	= false;

    /** Descripción de Campo */
    private CPanel	mainPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	mainLayout	= new BorderLayout();

    /** Descripción de Campo */
    private CPanel	centerPanel	= new CPanel();

    /** Descripción de Campo */
    private CPanel	southPanel	= new CPanel();

    /** Descripción de Campo */
    private FlowLayout	southLayout	= new FlowLayout();

    /** Descripción de Campo */
    private CLabel	sidLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	sidField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	portLabel	= new CLabel();

    /** Descripción de Campo */
    private CLabel	nameLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	nameField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	hostLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	hostField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	fwPortLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fwPortField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	fwHostLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fwHostField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	dbUidLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	dbUidField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	dbTypeLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	dbTypeField	= new CComboBox(Database.DB_NAMES);

    /** Descripción de Campo */
    private JPasswordField	dbPwdField	= new JPasswordField();

    /** Descripción de Campo */
    private CTextField	dbPortField	= new CTextField();

    /** Descripción de Campo */
    private GridBagLayout	centerLayout	= new GridBagLayout();

    /** Descripción de Campo */
    private CCheckBox	cbRMIoverHTTP	= new CCheckBox();

    /** Descripción de Campo */
    private CCheckBox	cbOverwrite	= new CCheckBox();

    /** Descripción de Campo */
    private CCheckBox	cbFirewall	= new CCheckBox();

    /** Descripción de Campo */
    private CCheckBox	cbBequeath	= new CCheckBox();

    /** Descripción de Campo */
    private CButton	bTestDB	= new CButton();

    /** Descripción de Campo */
    private CButton	bTestApps	= new CButton();

    /** Descripción de Campo */
    private CButton	bOK	= CompierePLAF.getOKButton();

    /** Descripción de Campo */
    private CButton	bCancel	= CompierePLAF.getCancelButton();

    /** Descripción de Campo */
    private CLabel	appsPortLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	appsPortField	= new CTextField();

    /** Descripción de Campo */
    private CLabel	appsHostLabel	= new CLabel();

    /** Descripción de Campo */
    private CTextField	appsHostField	= new CTextField();

    /**
     *  Connection Dialog using current Connection
     */
    public CConnectionDialog() {
        this(null);
    }		// CConnectionDialog

    /**
     *  Connection Dialog
     *  @param cc OpenXpertya Connection
     */
    public CConnectionDialog(CConnection cc) {

        super((Frame) null, true);

        try {

            jbInit();
            setConnection(cc);

        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }

        CompierePLAF.showCenterScreen(this);

    }		// CConnection

    /**
     *  ActionListener
     *  @param e event
     */
    public void actionPerformed(ActionEvent e) {

        if (m_updating) {
            return;
        }

        Object	src	= e.getSource();

        if (src == bOK) {

            m_cc.setName();
            m_ccResult	= m_cc;
            dispose();

            return;

        } else if (src == bCancel) {

            m_cc.setName();
            dispose();

            return;

        } else if (src == cbRMIoverHTTP) {

            if (cbRMIoverHTTP.isSelected()) {
                appsPortField.setText(APPS_PORT_HTTP);
            } else {
                appsPortField.setText(APPS_PORT_JNP);
            }

            return;

        } else if (src == dbTypeField) {

            if (dbTypeField.getSelectedItem() == null) {
                return;
            }
        }

        if (Ini.isClient()) {

            m_cc.setAppsHost(appsHostField.getText());
            m_cc.setAppsPort(appsPortField.getText());

        } else {
            m_cc.setAppsHost("localhost");
        }

        //
        m_cc.setRMIoverHTTP(cbRMIoverHTTP.isSelected());

        //
        m_cc.setType((String) dbTypeField.getSelectedItem());
        m_cc.setDbHost(hostField.getText());
        m_cc.setDbPort(dbPortField.getText());
        m_cc.setDbName(sidField.getText());
        m_cc.setDbUid(dbUidField.getText());
        m_cc.setDbPwd(String.valueOf(dbPwdField.getPassword()));
        m_cc.setBequeath(cbBequeath.isSelected());
        m_cc.setViaFirewall(cbFirewall.isSelected());
        m_cc.setFwHost(fwHostField.getText());
        m_cc.setFwPort(fwPortField.getText());

        //
        if (src == bTestApps) {

            cmd_testApps();

            // Database Selection Changed

        } else if (src == dbTypeField) {

            m_cc.setType((String) dbTypeField.getSelectedItem());
            dbPortField.setText(String.valueOf(m_cc.getDbPort()));
            cbBequeath.setSelected(m_cc.isBequeath());
            fwPortField.setText(String.valueOf(m_cc.getFwPort()));
        }

        //
        else if (src == bTestDB) {
            cmd_testDB();
        }

        // Name
        if (src == nameField) {
            m_cc.setName(nameField.getText());
        }

        updateInfo();

    }		// actionPerformed

    /**
     *  Test Application connection
     */
    private void cmd_testApps() {

        setBusy(true);

        Exception	e	= m_cc.testAppsServer();

        if (e != null) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), res.getString("ServerNotActive") + " - " + m_cc.getAppsHost(), JOptionPane.ERROR_MESSAGE);
        }

        setBusy(false);

    }		// cmd_testApps

    /**
     *  Test Database connection
     */
    private void cmd_testDB() {

        setBusy(true);

        Exception	e	= m_cc.testDatabase();

        if (e != null) {

            JOptionPane.showMessageDialog(this, e,	// message
                res.getString("ConnectionError") + ": " + m_cc.getConnectionURL(), JOptionPane.ERROR_MESSAGE);
        }

        setBusy(false);

    }		// cmd_testDB

    /**
     *  Static Layout
     *  @throws Exception
     */
    private void jbInit() throws Exception {

        this.setTitle(res.getString("CConnectionDialog"));
        CompiereColor.setBackground(this);
        mainPanel.setLayout(mainLayout);
        southPanel.setLayout(southLayout);
        southLayout.setAlignment(FlowLayout.RIGHT);
        centerPanel.setLayout(centerLayout);
        nameLabel.setText(res.getString("Name"));
        nameField.setColumns(30);
        nameField.setReadWrite(false);
        hostLabel.setText(res.getString("DBHost"));
        hostField.setColumns(30);
        portLabel.setText(res.getString("DBPort"));
        dbPortField.setColumns(10);
        sidLabel.setText(res.getString("DBName"));
        cbFirewall.setToolTipText("");
        cbFirewall.setText(res.getString("ViaFirewall"));
        fwHostLabel.setText(res.getString("FWHost"));
        fwHostField.setColumns(30);
        fwPortLabel.setText(res.getString("FWPort"));
        bTestDB.setText(res.getString("TestConnection"));
        bTestDB.setHorizontalAlignment(JLabel.LEFT);
        dbTypeLabel.setText(res.getString("Type"));
        sidField.setColumns(30);
        fwPortField.setColumns(10);
        cbBequeath.setText(res.getString("BequeathConnection"));
        appsHostLabel.setText(res.getString("AppsHost"));
        appsHostField.setColumns(30);
        appsPortLabel.setText(res.getString("AppsPort"));
        appsPortField.setColumns(10);
        bTestApps.setText(res.getString("TestApps"));
        bTestApps.setHorizontalAlignment(JLabel.LEFT);
        cbOverwrite.setText(res.getString("Overwrite"));
        dbUidLabel.setText(res.getString("DBUidPwd"));
        dbUidField.setColumns(10);
        cbRMIoverHTTP.addActionListener(this);
        cbRMIoverHTTP.setText(res.getString("RMIoverHTTP"));
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(bCancel, null);
        southPanel.add(bOK, null);
        centerPanel.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(12, 12, 5, 5), 0, 0));
        centerPanel.add(nameField, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(12, 0, 5, 12), 0, 0));
        centerPanel.add(hostLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        centerPanel.add(hostField, new GridBagConstraints(1, 6, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 12), 0, 0));
        centerPanel.add(portLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(dbPortField, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        centerPanel.add(sidLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(sidField, new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 12), 0, 0));
        centerPanel.add(cbFirewall, new GridBagConstraints(1, 10, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 12), 0, 0));
        centerPanel.add(fwHostLabel, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(fwHostField, new GridBagConstraints(1, 11, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 12), 0, 0));
        centerPanel.add(fwPortLabel, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(fwPortField, new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        centerPanel.add(bTestDB, new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 12, 0), 0, 0));
        centerPanel.add(dbTypeLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        centerPanel.add(dbTypeField, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
        centerPanel.add(appsHostLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        centerPanel.add(appsHostField, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 12), 0, 0));
        centerPanel.add(appsPortLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(appsPortField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        centerPanel.add(cbBequeath, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 12), 0, 0));
        centerPanel.add(cbOverwrite, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 5, 0, 12), 0, 0));
        centerPanel.add(dbUidLabel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        centerPanel.add(dbUidField, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        centerPanel.add(dbPwdField, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 12), 0, 0));
        centerPanel.add(bTestApps, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, -1, 2, 1), 0, 0));
        centerPanel.add(cbRMIoverHTTP, new GridBagConstraints(2, 2, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 12), 0, 0));

        //
        nameField.addActionListener(this);
        appsHostField.addActionListener(this);
        appsPortField.addActionListener(this);
        cbOverwrite.addActionListener(this);
        bTestApps.addActionListener(this);

        //
        dbTypeField.addActionListener(this);
        hostField.addActionListener(this);
        dbPortField.addActionListener(this);
        sidField.addActionListener(this);
        cbBequeath.addActionListener(this);
        cbFirewall.addActionListener(this);
        fwHostField.addActionListener(this);
        fwPortField.addActionListener(this);
        bTestDB.addActionListener(this);
        bOK.addActionListener(this);
        bCancel.addActionListener(this);

        // Server
        if (!Ini.isClient()) {

            appsHostLabel.setVisible(false);
            appsHostField.setVisible(false);
            appsPortLabel.setVisible(false);
            appsPortField.setVisible(false);
            bTestApps.setVisible(false);
            cbRMIoverHTTP.setVisible(false);

        } else {	// Client
            cbBequeath.setVisible(false);
        }

    }			// jbInit

    /**
     *  Update Fields from Connection
     */
    private void updateInfo() {

        m_updating	= true;
        nameField.setText(m_cc.getName());
        appsHostField.setText(m_cc.getAppsHost());
        appsPortField.setText(String.valueOf(m_cc.getAppsPort()));
        cbRMIoverHTTP.setSelected(m_cc.isRMIoverHTTP());
        bTestApps.setIcon(getStatusIcon(m_cc.isAppsServerOK(false)));

        // bTestApps.setToolTipText(m_cc.getRmiUri());
        cbOverwrite.setVisible(m_cc.isAppsServerOK(false));

        boolean	rw	= cbOverwrite.isSelected() ||!m_cc.isAppsServerOK(false);

        //
        dbTypeLabel.setReadWrite(rw);
        dbTypeField.setReadWrite(rw);
        dbTypeField.setSelectedItem(m_cc.getType());

        //
        hostLabel.setReadWrite(rw);
        hostField.setReadWrite(rw);
        hostField.setText(m_cc.getDbHost());
        portLabel.setReadWrite(rw);
        dbPortField.setReadWrite(rw);
        dbPortField.setText(String.valueOf(m_cc.getDbPort()));
        sidLabel.setReadWrite(rw);
        sidField.setReadWrite(rw);
        sidField.setText(m_cc.getDbName());

        //
        dbUidField.setText(m_cc.getDbUid());
        dbPwdField.setText(m_cc.getDbPwd());

        //
        cbBequeath.setReadWrite(rw);
        cbBequeath.setEnabled(m_cc.isOracle());
        cbBequeath.setSelected(m_cc.isBequeath());

        //
        boolean	fwEnabled	= m_cc.isViaFirewall() && m_cc.isOracle();

        cbFirewall.setReadWrite(rw);
        cbFirewall.setSelected(fwEnabled);
        cbFirewall.setEnabled(m_cc.isOracle());
        fwHostLabel.setReadWrite(fwEnabled && rw);
        fwHostField.setReadWrite(fwEnabled && rw);
        fwHostField.setText(m_cc.getFwHost());
        fwPortLabel.setReadWrite(fwEnabled && rw);
        fwPortField.setReadWrite(fwEnabled && rw);
        fwPortField.setText(String.valueOf(m_cc.getFwPort()));

        //
        bTestDB.setToolTipText(m_cc.getConnectionURL());
        bTestDB.setIcon(getStatusIcon(m_cc.isDatabaseOK()));
        m_updating	= false;

    }		// updateInfo

    //~--- get methods --------------------------------------------------------

    /**
     *  Get Connection
     *  @return CConnection
     */
    public CConnection getConnection() {
        return m_ccResult;
    }		// getConnection;

    /**
     *  Get Status Icon - ok or not
     *  @param ok ok
     *  @return Icon
     */
    private Icon getStatusIcon(boolean ok) {

        if (ok) {
            return bOK.getIcon();
        } else {
            return bCancel.getIcon();
        }

    }		// getStatusIcon

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Busy - lock UI
     *  @param busy busy
     */
    private void setBusy(boolean busy) {

        if (busy) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            this.setCursor(Cursor.getDefaultCursor());
        }

        m_updating	= busy;

    }		// setBusy

    /**
     *  Set Connection
     *  @param cc - if null use current connection
     */
    public void setConnection(CConnection cc) {

        m_cc	= cc;

        if (m_cc == null) {

            m_cc	= CConnection.get();
            m_cc.setName();
        }

        // Should copy values
        m_ccResult	= m_cc;

        //
        String	type	= m_cc.getType();

        if ((type == null) || (type.length() == 0)) {
            dbTypeField.setSelectedItem(null);
        } else {
            m_cc.setType(m_cc.getType());	// sets defaults
        }

        updateInfo();

    }		// setConnection
}	// CConnectionDialog



/*
 * @(#)CConnectionDialog.java   02.jul 2007
 * 
 *  Fin del fichero CConnectionDialog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
