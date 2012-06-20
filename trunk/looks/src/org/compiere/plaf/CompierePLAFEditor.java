/*
 * @(#)CompierePLAFEditor.java   12.oct 2007  Versión 2.2
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



package org.compiere.plaf;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextField;
import org.compiere.swing.CToggleButton;

import org.openXpertya.util.Ini;
import org.openXpertya.util.MiniBrowser;
import org.openXpertya.util.ValueNamePair;

import sun.awt.AppContext;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 *  OpenXpertya PLAF Editor.
 *  <p>
 *  start with <code>new CompierePLAFEditor()</code>
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompierePLAFEditor.java,v 1.29 2005/03/11 20:34:37 jjanke Exp $
 */
public class CompierePLAFEditor extends JDialog implements ActionListener {

    /** Descripción de Campo */
    static ResourceBundle	s_res	= ResourceBundle.getBundle("org.compiere.plaf.PlafRes");

    /** Descripción de Campo */
    static Object[]	s_pos	= new Object[] { "Top", "Left", "Bottom", "Right" };

    /** Descripción de Campo */
    static Object[][]	s_data	= new Object[][] {

        {
            "-00-", "-01-", "-02-", "-03-", "-0O-", "-0l-"
        }, {
            "-10-", "-11-", "-12-", "-13-", "-1O-", "-1l-"
        }, {
            "-20-", "-21-", "-22-", "-23-", "-2O-", "-2l-"
        }, {
            "-30-", "-31-", "-32-", "-33-", "-3O-", "-3l-"
        }, {
            "-O0-", "-O1-", "-O2-", "-O3-", "-OO-", "-Ol-"
        }, {
            "-l0-", "-l1-", "-l2-", "-l3-", "-lO-", "-ll-"
        }
    };

    /** Descripción de Campo */
    static Object[]	s_columns	= new Object[] {
        "-0-", "-1-", "-2-", "-3-", "-O-", "-l-"
    };

    /** Diable Theme Field */
    private boolean	m_setting	= false;

    /** We did test for true color */
    private boolean	m_colorTest	= false;

    /** Descripción de Campo */
    private CPanel	mainPanel	= new CPanel(new BorderLayout());

    /** Descripción de Campo */
    private CPanel	northPanel	= new CPanel();

    /** Descripción de Campo */
    private CPanel	southPanel	= new CPanel();

    /** Descripción de Campo */
    private GridBagLayout	northLayout	= new GridBagLayout();

    /** Descripción de Campo */
    private CLabel	lfLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	lfField	= new CComboBox(CompierePLAF.getPLAFs());

    /** Descripción de Campo */
    private CButton	bOK	= CompierePLAF.getOKButton();

    /** Descripción de Campo */
    private CButton	bHelp	= new CButton();

    /** @todo Help Button */

    /** Descripción de Campo */
    private CButton	bCancel	= CompierePLAF.getCancelButton();

    /** Descripción de Campo */
    private CLabel	themeLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	themeField	= new CComboBox(CompierePLAF.getThemes());

    /** Descripción de Campo */
    private FlowLayout	southLayout	= new FlowLayout();

    /** Descripción de Campo */
    private CButton	rButton	= new CButton();

    /** Descripción de Campo */
    private BorderLayout	mainLayout	= new BorderLayout();

    /** Descripción de Campo */
    private JTree	jTree1	= new JTree();

    /** Descripción de Campo */
    private CToggleButton	jToggleButton1	= new CToggleButton();

    /** Descripción de Campo */
    private JTextPane	jTextPane1	= new JTextPane();

    /** Descripción de Campo */
    private JTextField	jTextFieldTexture	= new JTextField();

    /** Descripción de Campo */
    private CTextField	jTextFieldLines	= new CTextField();

    /** Descripción de Campo */
    private CTextField	jTextFieldGradient	= new CTextField();

    /** Descripción de Campo */
    private JTextField	jTextFieldFlat	= new JTextField();

    /** Descripción de Campo */
    private JTextField	jTextField1	= new JTextField();

    /** Descripción de Campo */
    private JTextArea	jTextArea1	= new JTextArea();

    /** Descripción de Campo */
    private JTable	jTable1	= new JTable(s_data, s_columns);

    /** Descripción de Campo */
    private JSplitPane	jSplitPane1	= new JSplitPane();

    /** Descripción de Campo */
    private JScrollPane	jScrollPane2	= new JScrollPane();

    /** Descripción de Campo */
    private JScrollPane	jScrollPane1	= new JScrollPane();

    /** Descripción de Campo */
    private JRadioButton	jRadioButton1	= new JRadioButton();

    /** Descripción de Campo */
    private JPasswordField	jPasswordField1	= new JPasswordField();

    /** Descripción de Campo */
    private CPanel	jPanelTexture	= new CPanel(new CompiereColor(CompiereColor.class.getResource("vincent.jpg"), Color.lightGray, 0.7f));

    /** Descripción de Campo */
    private CPanel	jPanelLines	= new CPanel(new CompiereColor(new Color(178, 181, 205), new Color(193, 193, 205), 1.0f, 5));

    /** Descripción de Campo */
    private CPanel	jPanelGradient	= new CPanel(new CompiereColor(new Color(233, 210, 210), new Color(217, 210, 233)));

    /** Descripción de Campo */
    private CPanel	jPanelFlat	= new CPanel(new CompiereColor(new Color(255, 205, 255), true));

    /** Descripción de Campo */
    private CPanel	jPanel2	= new CPanel();

    /** Descripción de Campo */
    private CPanel	jPanel1	= new CPanel();

    /** Descripción de Campo */
    private JList	jList1	= new JList(s_columns);

    /** Descripción de Campo */
    private JLabel	jLabelTexture	= new JLabel();

    /** Descripción de Campo */
    private CLabel	jLabelLines	= new CLabel();

    /** Descripción de Campo */
    private CLabel	jLabelGradient	= new CLabel();

    /** Descripción de Campo */
    private JLabel	jLabelFlat	= new JLabel();

    /** Descripción de Campo */
    private JLabel	jLabel1	= new JLabel();

    /** Descripción de Campo */
    private JEditorPane	jEditorPane1	= new JEditorPane();

    /** Descripción de Campo */
    private JComboBox	jComboBoxTexture	= new JComboBox(s_pos);

    /** Descripción de Campo */
    private CComboBox	jComboBoxLines	= new CComboBox(s_pos);

    /** Descripción de Campo */
    private CComboBox	jComboBoxGradient	= new CComboBox(s_pos);

    /** Descripción de Campo */
    private JComboBox	jComboBoxFlat	= new JComboBox(s_pos);

    /** Descripción de Campo */
    private CComboBox	jComboBox1	= new CComboBox(s_columns);

    /** Descripción de Campo */
    private JCheckBox	jCheckBoxTexture	= new JCheckBox();

    /** Descripción de Campo */
    private CCheckBox	jCheckBoxLines	= new CCheckBox();

    /** Descripción de Campo */
    private CCheckBox	jCheckBoxGradient	= new CCheckBox();

    /** Descripción de Campo */
    private JCheckBox	jCheckBoxFlat	= new JCheckBox();

    /** Descripción de Campo */
    private JCheckBox	jCheckBox1	= new JCheckBox();

    /** Descripción de Campo */
    private JButton	jButtonTexture	= new JButton();

    /** Descripción de Campo */
    private CButton	jButtonLines	= new CButton();

    /** Descripción de Campo */
    private CButton	jButtonGardient	= new CButton();

    /** Descripción de Campo */
    private JButton	jButtonFlat	= new JButton();

    /** Descripción de Campo */
    private CButton	jButton1	= new CButton();

    /** Descripción de Campo */
    private GridBagLayout	gridBagLayout1	= new GridBagLayout();

    //

    /** Descripción de Campo */
    private CTabbedPane	example	= new CTabbedPane();

    /** Descripción de Campo */
    private CCheckBox	cDefault	= new CCheckBox();

    /** Descripción de Campo */
    private CButton	cButton	= new CButton();

    /** Descripción de Campo */
    private BorderLayout	borderLayout1	= new BorderLayout();

    /** Descripción de Campo */
    private CLabel	blindLabel	= new CLabel();

    /** Descripción de Campo */
    private CComboBox	blindField	= new CComboBox(ColorBlind.COLORBLIND_TYPE);

    /** Descripción de Campo */
    private CButton	bSetColor	= new CButton();

    /** Descripción de Campo */
    private TitledBorder	exampleBorder;

    /**
     *  Default Constructor
     *  Don't Show Example
     */
    public CompierePLAFEditor() {

        super();
        init(false);

    }		// CompierePLAFEditor

    /**
     *  Constructor
     *  @param showExample if true, show Example
     */
    public CompierePLAFEditor(boolean showExample) {

        super();
        init(showExample);

    }		// CompierePLAFEditor

    /**
     *  Modal Dialog Constructor
     *  @param owner
     *  @param showExample if true, show Example
     */
    public CompierePLAFEditor(Dialog owner, boolean showExample) {

        super(owner, "", true);
        init(showExample);

    }		// CompierePLAFEditor

    /**
     *  Modal Frame Constructor
     *  @param owner
     *  @param showExample if true, show Example
     */
    public CompierePLAFEditor(Frame owner, boolean showExample) {

        super(owner, "", true);
        init(showExample);

    }		// CompierePLAFEditor

    /**
     *  ActionListener
     *  @param e
     */
    public void actionPerformed(ActionEvent e) {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // OK - Save & Finish
        if (e.getSource() == bOK) {

            CompiereTheme.save();
            Ini.saveProperties(true);
            dispose();
        }

        // Cancel - Finish
        else if (e.getSource() == bCancel) {
            dispose();
        } else if (e.getSource() == bHelp) {
            new MiniBrowser("http://www.openXpertya.org/looks/help.html");
        }

        // Look & Feel changed
        else if ((e.getSource() == lfField) &&!m_setting) {

            m_setting	= true;		// disable Theme setting

            // set new theme
            CompierePLAF.setPLAF((ValueNamePair) lfField.getSelectedItem(), null, this);
            setLFSelection();
            setBackgroundToTheme();
            CompierePLAF.setPLAF(this);		// twice ??
            m_setting	= false;		// enable Theme setting
        }

        // Theme Field Changed
        else if ((e.getSource() == themeField) &&!m_setting) {

            Ini.setProperty(Ini.P_UI_THEME, themeField.getSelectedItem().toString());
            CompierePLAF.setPLAF((ValueNamePair) lfField.getSelectedItem(), (ValueNamePair) themeField.getSelectedItem(), this);
            CompiereTheme.setTheme();		// copy Theme
            setBackgroundToTheme();
            CompierePLAF.setPLAF(this);		// twice (??)
        }

        // Start OpenXpertya Theme Editor
        else if (e.getSource() == cButton) {

            new CompiereThemeEditor(this);
            setBackgroundToTheme();
        }

        // Reset PLAFs
        else if (e.getSource() == rButton) {

            CompierePLAF.reset(this);
            setLFSelection();
            ColorBlind.setColorType(ColorBlind.NORMAL);
            CompierePLAF.setPLAF(this);		// twice ??
        }

        // Set Default Background Color
        else if (e.getSource() == bSetColor) {

            CompiereColor	cc	= CompiereColorEditor.showDialog(this, CompierePanelUI.getDefaultBackground());

            CompierePanelUI.setDefaultBackground(cc);
            CompierePLAF.updateUI(this);
            Ini.setProperty(CompiereTheme.P_CompiereColor, cc.toString());
        }

        // Set Background as Default
        else if (e.getSource() == cDefault) {

            CompierePanelUI.setSetDefault(cDefault.isSelected());
            CompierePLAF.updateUI(this);
        }

        // ColorBlind
        else if (e.getSource() == blindField) {

            int	sel	= blindField.getSelectedIndex();

            if (sel != ColorBlind.getColorType()) {

                // Test for True color
                if (!m_colorTest) {

                    m_colorTest	= true;

                    int	size	= Toolkit.getDefaultToolkit().getColorModel().getPixelSize();

                    if (size < 24) {
                        JOptionPane.showMessageDialog(this, "Your environment has only a pixel size of " + size + ".\nTo see the effect, you need to have a pixel size of 24 (true color)", "Insufficient Color Capabilities", JOptionPane.ERROR_MESSAGE);
                    }
                }

                ColorBlind.setColorType(sel);
                CompierePLAF.updateUI(this);
                CompierePLAF.setPLAF(this);	// twice (??)
            }
        }

        // Change Tab Pacement
        else if ((e.getSource() == jComboBoxFlat) || (e.getSource() == jComboBoxGradient) || (e.getSource() == jComboBoxTexture) || (e.getSource() == jComboBoxLines)) {

            if (!m_setting) {

                m_setting	= true;

                int	index	= ((JComboBox) e.getSource()).getSelectedIndex();

                example.setTabPlacement(index + 1);
                jComboBoxFlat.setSelectedIndex(index);
                jComboBoxGradient.setSelectedIndex(index);
                jComboBoxTexture.setSelectedIndex(index);
                jComboBoxLines.setSelectedIndex(index);
                m_setting	= false;
            }
        }

        // Display Options
        else if (e.getSource() == jButtonFlat) {
            JOptionPane.showConfirmDialog(this, "Confirm Dialog");
        } else if (e.getSource() == jButtonGardient) {
            JOptionPane.showInputDialog(this, "Input Dialog");
        } else if (e.getSource() == jButtonTexture) {
            JOptionPane.showMessageDialog(this, "Message Dialog");
        } else if (e.getSource() == jButtonLines) {

            JOptionPane.showMessageDialog(this, "Message Dialog - Error", "Error", JOptionPane.ERROR_MESSAGE);

            // Test

        } else if (e.getSource() == jButton1) {}

        /** **************** */

        // Metal
        boolean	metal	= UIManager.getLookAndFeel() instanceof MetalLookAndFeel;

        themeField.setEnabled(metal);
        themeLabel.setEnabled(metal);

        // ColorBlind - only with OpenXpertya L&F & Theme
        boolean	enable	= metal && CompiereLookAndFeel.NAME.equals(lfField.getSelectedItem().toString()) && (themeField.getSelectedItem() != null) && CompiereTheme.NAME.equals(themeField.getSelectedItem().toString());

        blindField.setEnabled(enable);
        blindLabel.setEnabled(enable);

        if ((e.getSource() != blindField) &&!enable) {
            blindField.setSelectedIndex(0);
        }

        // done
        setCursor(Cursor.getDefaultCursor());

    }		// actionPerformed

    /**
     *  Dispose
     *  Exit, if there is no real owning parent (not modal) - shortcut
     */
    public void dispose() {

        super.dispose();

        if (!isModal()) {
            System.exit(0);
        }

    }		// dispose

    /**
     *  Dynamic Init
     */
    private void dynInit() {

        setLFSelection();

        //
        jPanelGradient.setTabLevel(1);
        jPanelTexture.setTabLevel(2);
        jPanelLines.setTabLevel(1);

        //
        jComboBoxFlat.addActionListener(this);
        jComboBoxGradient.addActionListener(this);
        jComboBoxTexture.addActionListener(this);
        jComboBoxLines.addActionListener(this);

        //
        jButton1.addActionListener(this);
        jButtonFlat.addActionListener(this);
        jButtonGardient.addActionListener(this);
        jButtonTexture.addActionListener(this);
        jButtonLines.addActionListener(this);

        //
        CompierePLAF.setPLAF(this);

    }		// dynInit

    /**
     *  Init Editor
     *  @param showExample if true, show Example
     */
    private void init(boolean showExample) {

        try {

            jbInit();
            dynInit();

            // Display
            example.setVisible(showExample);
            CompierePLAF.showCenterScreen(this);

        } catch (Exception e) {

            System.err.println("CompierePLAFEditor.init");
            e.printStackTrace();
        }

        // CompiereUtils.setNotBuffered(this);

    }		// PLAFEditor

    /**
     *  Static Layout
     *  @throws Exception
     */
    private void jbInit() throws Exception {

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle(s_res.getString("LookAndFeelEditor") + " " + CompierePLAF.VERSION);
        mainPanel.setLayout(mainLayout);
        mainLayout.setHgap(5);
        mainLayout.setVgap(5);
        jTextFieldFlat.setColumns(10);
        jTextFieldGradient.setColumns(10);
        jTextFieldTexture.setColumns(10);
        jTextFieldLines.setColumns(10);
        jCheckBoxLines.setText("jCheckBox");
        jCheckBoxTexture.setText("jCheckBox");
        jCheckBoxGradient.setText("jCheckBox");
        jCheckBoxFlat.setText("jCheckBox");
        jPanelGradient.setToolTipText("Indented Level 1");
        jPanelTexture.setToolTipText("Indented Level 2");
        jPanelLines.setToolTipText("Indented Level 1");
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        CompiereColor.setBackground(this);

        //
        lfLabel.setText(s_res.getString("LookAndFeel"));
        lfField.addActionListener(this);
        themeLabel.setText(s_res.getString("Theme"));
        themeField.addActionListener(this);
        cButton.setText(s_res.getString("EditCompiereTheme"));
        cButton.addActionListener(this);
        rButton.setText(s_res.getString("Reset"));
        rButton.addActionListener(this);
        cDefault.setText(s_res.getString("SetDefault"));
        cDefault.addActionListener(this);
        bSetColor.setText(s_res.getString("SetDefaultColor"));
        bSetColor.addActionListener(this);
        blindLabel.setText(s_res.getString("ColorBlind"));
        blindField.addActionListener(this);

        //
        bOK.addActionListener(this);
        bCancel.addActionListener(this);
        bHelp.addActionListener(this);

        //
        northPanel.setLayout(northLayout);
        southPanel.setLayout(southLayout);
        southLayout.setAlignment(FlowLayout.RIGHT);

        //
        exampleBorder	= new TitledBorder(s_res.getString("Example"));
        example.setBorder(exampleBorder);
        jLabel1.setText("jLabel");
        jTextField1.setText("jTextField");
        jCheckBox1.setText("jCheckBox");
        jRadioButton1.setText("jRadioButton");
        jButton1.setText("jButton");
        jToggleButton1.setText("jToggleButton");
        jTextArea1.setText("jTextArea");
        jTextPane1.setText("jTextPane");
        jEditorPane1.setText("jEditorPane");
        jPasswordField1.setText("jPasswordField");
        jPanel2.setLayout(borderLayout1);
        jPanel1.setLayout(gridBagLayout1);
        jScrollPane1.setPreferredSize(new Dimension(100, 200));
        jScrollPane2.setPreferredSize(new Dimension(100, 200));
        jButtonFlat.setText("Confirm");
        jButtonGardient.setText("Input");
        jButtonTexture.setText("Message");
        jButtonLines.setText("Error");
        jTextFieldFlat.setText("jTextField");
        jLabelFlat.setText("jLabel");
        jTextFieldGradient.setText("jTextField");
        jLabelGradient.setText("jLabel");
        jTextFieldTexture.setText("jTextField");
        jLabelTexture.setText("jLabel");
        jTextFieldLines.setText("jTextField");
        jLabelLines.setText("jLabel");
        mainPanel.add(northPanel, BorderLayout.NORTH);
        northPanel.add(lfLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(12, 12, 5, 5), 0, 0));
        northPanel.add(lfField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(12, 0, 5, 12), 0, 0));
        northPanel.add(themeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 5, 5), 0, 0));
        northPanel.add(themeField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 12), 0, 0));
        northPanel.add(cButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 12), 0, 0));
        northPanel.add(rButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        northPanel.add(bSetColor, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 12), 0, 0));
        northPanel.add(cDefault, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        northPanel.add(blindField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 12), 0, 0));
        northPanel.add(blindLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 12, 5, 5), 0, 0));
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(bCancel, null);
        southPanel.add(bOK, null);
        mainPanel.add(example, BorderLayout.CENTER);
        example.add(jPanel1, "JPanel");
        jPanel1.add(jTextPane1, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jEditorPane1, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jList1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jTextField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jCheckBox1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
        jPanel1.add(jRadioButton1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jButton1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jToggleButton1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jTextArea1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jComboBox1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        jPanel1.add(jPasswordField1, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        example.add(jPanel2, "JPanel");
        jPanel2.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
        jSplitPane1.add(jScrollPane2, JSplitPane.RIGHT);
        jPanelFlat.setName("FlatP");
        jPanelGradient.setName("GradientP");
        jPanelTexture.setName("TextureP");
        jPanelLines.setName("LineP");
        example.add(jPanelFlat, "jPanel Flat");
        jPanelFlat.add(jButtonFlat, null);
        jPanelFlat.add(jComboBoxFlat, null);
        example.add(jPanelGradient, "jPanel Gradient 1");
        jPanelGradient.add(jButtonGardient, null);
        jPanelGradient.add(jComboBoxGradient, null);
        jPanelGradient.add(jLabelGradient, null);
        jPanelGradient.add(jTextFieldGradient, null);
        example.add(jPanelTexture, "jPanel Texture 2");
        jPanelTexture.add(jButtonTexture, null);
        jPanelTexture.add(jComboBoxTexture, null);
        jPanelTexture.add(jLabelTexture, null);
        jPanelTexture.add(jTextFieldTexture, null);
        example.add(jPanelLines, "jPanel Lines 1");
        jPanelLines.add(jButtonLines, null);
        jPanelLines.add(jComboBoxLines, null);
        jPanelLines.add(jLabelLines, null);
        jPanelLines.add(jTextFieldLines, null);
        jScrollPane2.getViewport().add(jTable1, null);
        jScrollPane1.getViewport().add(jTree1, null);
        jPanelFlat.add(jLabelFlat, null);
        jPanelFlat.add(jTextFieldFlat, null);
        jPanelLines.add(jCheckBoxLines, null);
        jPanelTexture.add(jCheckBoxTexture, null);
        jPanelGradient.add(jCheckBoxGradient, null);
        jPanelFlat.add(jCheckBoxFlat, null);

    }		// jbInit

    //~--- set methods --------------------------------------------------------

    /**
     *  Set CompiereColor Background to Theme Background
     */
    private void setBackgroundToTheme() {

        // Not flat for OpenXpertya L&F & Theme
        boolean	notFlat	= (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) && CompiereLookAndFeel.NAME.equals(lfField.getSelectedItem().toString()) && (themeField.getSelectedItem() != null) && CompiereTheme.NAME.equals(themeField.getSelectedItem().toString());
        CompiereColor	cc	= new CompiereColor(CompiereTheme.secondary3, !notFlat);

        CompierePanelUI.setDefaultBackground(cc);
        Ini.setProperty(CompiereTheme.P_CompiereColor, cc.toString());
        CompierePLAF.updateUI(this);
    }		// setBackgroundToTheme

    /**
     *  Set Picks From Environment
     */
    private void setLFSelection() {

        m_setting	= true;

        // Search for PLAF
        ValueNamePair	plaf		= null;
        LookAndFeel	lookFeel	= UIManager.getLookAndFeel();
        String		look		= lookFeel.getClass().getName();

        for (int i = 0; i < CompierePLAF.getPLAFs().length; i++) {

            ValueNamePair	vp	= CompierePLAF.getPLAFs()[i];

            if (vp.getValue().equals(look)) {

                plaf	= vp;

                break;
            }
        }

        if (plaf != null) {
            lfField.setSelectedItem(plaf);
        }

        // Search for Theme
        MetalTheme	t	= null;
        ValueNamePair	theme	= null;
        boolean		metal	= UIManager.getLookAndFeel() instanceof MetalLookAndFeel;

        themeField.setModel(new DefaultComboBoxModel(CompierePLAF.getThemes()));

        if (metal) {

            theme	= null;

            AppContext	context	= AppContext.getAppContext();

            t	= (MetalTheme) context.get("currentMetalTheme");

            if (t != null) {

                String	lookTheme	= t.getName();

                for (int i = 0; i < CompierePLAF.getThemes().length; i++) {

                    ValueNamePair	vp	= CompierePLAF.getThemes()[i];

                    if (vp.getName().equals(lookTheme)) {

                        theme	= vp;

                        break;
                    }
                }
            }

            if (theme != null) {
                themeField.setSelectedItem(theme);
            } else {
                themeField.setSelectedIndex(0);
            }
        }

        m_setting	= false;

        // System.out.println("CompierePLAFEditor.setLFSelection - " + lookFeel.getName() + "=" + plaf + " - " + t.getName() + "=" + theme);

    }		// setLFSelection
}	// CompierePLAFEditor



/*
 * @(#)CompierePLAFEditor.java   02.jul 2007
 * 
 *  Fin del fichero CompierePLAFEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
