/*
 * @(#)CompiereThemeEditor.java   12.oct 2007  Versión 2.2
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
import org.compiere.swing.CPanel;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ResourceBundle;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *  Java Theme Editor.
 *  Edit the attributes and save them in Ini.properties.
 *  Does not set background of CompiereColorUI.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereThemeEditor.java,v 1.12 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompiereThemeEditor extends JDialog implements ActionListener {

    /** Descripción de Campo */
    static ResourceBundle	s_res	= ResourceBundle.getBundle("org.compiere.plaf.PlafRes");

    /** Descripción de Campo */
    private CButton	primary1	= new CButton();

    /** Descripción de Campo */
    private CButton	primary2	= new CButton();

    /** Descripción de Campo */
    private CButton	primary3	= new CButton();

    /** Descripción de Campo */
    private CButton	secondary1	= new CButton();

    /** Descripción de Campo */
    private CButton	secondary2	= new CButton();

    /** Descripción de Campo */
    private CButton	secondary3	= new CButton();

    /** Descripción de Campo */
    private CButton	controlFont	= new CButton();

    /** Descripción de Campo */
    private CButton	systemFont	= new CButton();

    /** Descripción de Campo */
    private CButton	userFont	= new CButton();

    /** Descripción de Campo */
    private CButton	smallFont	= new CButton();

    /** Descripción de Campo */
    private CButton	mandatory	= new CButton();

    /** Descripción de Campo */
    private CButton	error	= new CButton();

    /** Descripción de Campo */
    private CButton	windowFont	= new CButton();

    /** Descripción de Campo */
    private CButton	white	= new CButton();

    /** Descripción de Campo */
    private CButton	txt_ok	= new CButton();

    /** Descripción de Campo */
    private CButton	txt_error	= new CButton();

    /** Descripción de Campo */
    private CPanel	metalColorPanel	= new CPanel();

    /** Descripción de Campo */
    private GridLayout	metalColorLayout	= new GridLayout();

    /** Descripción de Campo */
    private CButton	menuFont	= new CButton();

    /** Descripción de Campo */
    private CButton	info	= new CButton();

    /** Descripción de Campo */
    private CButton	inactive	= new CButton();

    /** Descripción de Campo */
    private CPanel	fontPanel	= new CPanel();

    /** Descripción de Campo */
    private GridLayout	fontLayout	= new GridLayout();

    /** Descripción de Campo */
    private CPanel	confirmPanel	= new CPanel();

    /** Descripción de Campo */
    private FlowLayout	confirmLayout	= new FlowLayout();

    /** Descripción de Campo */
    private CPanel	compiereColorPanel	= new CPanel();

    /** Descripción de Campo */
    private GridLayout	compiereColorLayout	= new GridLayout();

    /** Descripción de Campo */
    private CPanel	centerPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	centerLayout	= new BorderLayout();

    /** Descripción de Campo */
    private CButton	black	= new CButton();

    /** Descripción de Campo */
    private CButton	bOK	= CompierePLAF.getOKButton();

    /** Descripción de Campo */
    private CButton	bCancel	= CompierePLAF.getCancelButton();

    /** Descripción de Campo */
    private TitledBorder	compiereColorBorder;

    /** Descripción de Campo */
    private TitledBorder	fontBorder;

    /** Descripción de Campo */
    private TitledBorder	metalColorBorder;

    /**
     *  Constructor
     *  @param  owner Frame owner
     */
    public CompiereThemeEditor(JDialog owner) {

        super(owner, s_res.getString("CompiereThemeEditor"), true);

        try {

            jbInit();
            loadTheme();
            dynInit();
            CompierePLAF.showCenterScreen(this);

        } catch (Exception e) {

            System.err.println("CompiereThemeEditor");
            e.printStackTrace();
        }

    }		// CompiereThemeEditor

    /**
     *  Action Listener
     *  @param e
     */
    public void actionPerformed(ActionEvent e) {

        // System.out.println("CompiereThemeEditor.actionPerformed " + e);
        // Confirm
        if (e.getSource() == bOK) {

            CompiereTheme.save();
            dispose();

            return;
        }

        // Cancel
        else if (e.getSource() == bCancel) {

            dispose();

            return;
        }

        CompiereTheme	vt	= new CompiereTheme();

        try {		// to capture errors when Cancel in JColorChooser

            if (e.getSource() == primary1) {
                CompiereTheme.primary1	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Primary1"), CompiereTheme.primary1));
            } else if (e.getSource() == primary2) {
                CompiereTheme.primary2	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Primary2"), CompiereTheme.primary2));
            } else if (e.getSource() == primary3) {
                CompiereTheme.primary3	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Primary3"), CompiereTheme.primary3));
            } else if (e.getSource() == secondary1) {
                CompiereTheme.secondary1	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Secondary1"), CompiereTheme.secondary1));
            } else if (e.getSource() == secondary2) {
                CompiereTheme.secondary2	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Secondary2"), CompiereTheme.secondary2));
            } else if (e.getSource() == secondary3) {
                CompiereTheme.secondary3	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Secondary3"), CompiereTheme.secondary3));
            } else if (e.getSource() == error) {
                CompiereTheme.error	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Error"), CompiereTheme.error));
            } else if (e.getSource() == mandatory) {
                CompiereTheme.mandatory	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Mandatory"), CompiereTheme.mandatory));
            } else if (e.getSource() == inactive) {
                CompiereTheme.inactive	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Inactive"), CompiereTheme.inactive));
            } else if (e.getSource() == info) {
                CompiereTheme.info	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Info"), CompiereTheme.info));
            } else if (e.getSource() == black) {
                CompiereTheme.black	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("Black"), CompiereTheme.black));
            } else if (e.getSource() == white) {
                CompiereTheme.white	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("White"), CompiereTheme.white));
            } else if (e.getSource() == txt_ok) {
                CompiereTheme.txt_ok	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("TextOK"), CompiereTheme.txt_ok));
            } else if (e.getSource() == txt_error) {
                CompiereTheme.txt_error	= new ColorUIResource(JColorChooser.showDialog(this, s_res.getString("TextIssue"), CompiereTheme.txt_error));
            } else if (e.getSource() == controlFont) {
                CompiereTheme.controlFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("ControlFont"), vt.getControlTextFont()));
            } else if (e.getSource() == systemFont) {
                CompiereTheme.systemFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("SystemFont"), vt.getSystemTextFont()));
            } else if (e.getSource() == userFont) {
                CompiereTheme.userFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("UserFont"), vt.getUserTextFont()));
            } else if (e.getSource() == smallFont) {
                CompiereTheme.smallFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("SmallFont"), vt.getSubTextFont()));
            } else if (e.getSource() == menuFont) {
                CompiereTheme.menuFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("MenuFont"), vt.getMenuTextFont()));
            } else if (e.getSource() == windowFont) {
                CompiereTheme.windowFont	= new FontUIResource(FontChooser.showDialog(this, s_res.getString("WindowTitleFont"), vt.getWindowTitleFont()));
            }

        } catch (Exception ee) {}	// to capture errors when Cancel in JColorChooser

        dynInit();
    }		// actionPerformed

    /**
     *  Dynamic Init
     */
    private void dynInit() {

        // System.out.println("CompiereThemeEditor.dynInit");
        CompiereTheme	ct	= CompiereLookAndFeel.getCompiereTheme();

        // Colors
        primary1.setBackground(ct.getPrimary1());
        primary2.setBackground(ct.getPrimary2());
        primary3.setBackground(ct.getPrimary3());
        secondary1.setBackground(ct.getSecondary1());
        secondary2.setBackground(ct.getSecondary2());
        secondary3.setBackground(ct.getSecondary3());

        //
        white.setBackground(ct.getWhite());
        black.setBackground(ct.getSecondary3());
        black.setForeground(ct.getBlack());

        //
        error.setBackground(CompierePLAF.getFieldBackground_Error());
        mandatory.setBackground(CompierePLAF.getFieldBackground_Mandatory());
        inactive.setBackground(CompierePLAF.getFieldBackground_Inactive());
        info.setBackground(CompierePLAF.getInfoBackground());

        //
        txt_ok.setBackground(CompierePLAF.getFieldBackground_Normal());
        txt_ok.setForeground(CompierePLAF.getTextColor_OK());
        txt_error.setBackground(CompierePLAF.getFieldBackground_Normal());
        txt_error.setForeground(CompierePLAF.getTextColor_Issue());

        // Fonts
        controlFont.setFont(ct.getControlTextFont());
        systemFont.setFont(ct.getSystemTextFont());
        userFont.setFont(ct.getUserTextFont());
        smallFont.setFont(ct.getSubTextFont());
        menuFont.setFont(ct.getMenuTextFont());
        windowFont.setFont(ct.getWindowTitleFont());
    }		// dynInit

    /**
     *  Static Init
     *  @throws Exception
     */
    private void jbInit() throws Exception {

        CompiereColor.setBackground(this);
        metalColorBorder	= new TitledBorder(s_res.getString("MetalColors"));
        compiereColorBorder	= new TitledBorder(s_res.getString("CompiereColors"));
        fontBorder	= new TitledBorder(s_res.getString("CompiereFonts"));
        fontPanel.setBorder(fontBorder);
        fontPanel.setOpaque(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        //
        primary1.setToolTipText(s_res.getString("Primary1Info"));
        primary1.setText(s_res.getString("Primary1"));
        primary1.addActionListener(this);
        primary2.setToolTipText(s_res.getString("Primary2Info"));
        primary2.setText(s_res.getString("Primary2"));
        primary2.addActionListener(this);
        primary3.setToolTipText(s_res.getString("Primary3Info"));
        primary3.setText(s_res.getString("Primary3"));
        primary3.addActionListener(this);
        secondary1.setToolTipText(s_res.getString("Secondary1Info"));
        secondary1.setText(s_res.getString("Secondary1"));
        secondary1.addActionListener(this);
        secondary2.setToolTipText(s_res.getString("Secondary2Info"));
        secondary2.setText(s_res.getString("Secondary2"));
        secondary2.addActionListener(this);
        secondary3.setToolTipText(s_res.getString("Secondary3Info"));
        secondary3.setText(s_res.getString("Secondary3"));
        secondary3.addActionListener(this);
        controlFont.setToolTipText(s_res.getString("ControlFontInfo"));
        controlFont.setText(s_res.getString("ControlFont"));
        controlFont.addActionListener(this);
        systemFont.setToolTipText(s_res.getString("SystemFontInfo"));
        systemFont.setText(s_res.getString("SystemFont"));
        systemFont.addActionListener(this);
        userFont.setToolTipText(s_res.getString("UserFontInfo"));
        userFont.setText(s_res.getString("UserFont"));
        userFont.addActionListener(this);
        smallFont.setText(s_res.getString("SmallFont"));
        smallFont.addActionListener(this);
        mandatory.setToolTipText(s_res.getString("MandatoryInfo"));
        mandatory.setText(s_res.getString("Mandatory"));
        mandatory.addActionListener(this);
        error.setToolTipText(s_res.getString("ErrorInfo"));
        error.setText(s_res.getString("Error"));
        error.addActionListener(this);
        info.setToolTipText(s_res.getString("InfoInfo"));
        info.setText(s_res.getString("Info"));
        info.addActionListener(this);
        windowFont.setText(s_res.getString("WindowTitleFont"));
        windowFont.addActionListener(this);
        menuFont.setText(s_res.getString("MenuFont"));
        menuFont.addActionListener(this);
        white.setToolTipText(s_res.getString("WhiteInfo"));
        white.setText(s_res.getString("White"));
        white.addActionListener(this);
        black.setToolTipText(s_res.getString("BlackInfo"));
        black.setText(s_res.getString("Black"));
        black.addActionListener(this);
        inactive.setToolTipText(s_res.getString("InactiveInfo"));
        inactive.setText(s_res.getString("Inactive"));
        inactive.addActionListener(this);
        txt_ok.setToolTipText(s_res.getString("TextOKInfo"));
        txt_ok.setText(s_res.getString("TextOK"));
        txt_ok.addActionListener(this);
        txt_error.setToolTipText(s_res.getString("TextIssueInfo"));
        txt_error.setText(s_res.getString("TextIssue"));
        txt_error.addActionListener(this);

        //
        confirmPanel.setLayout(confirmLayout);
        confirmLayout.setAlignment(FlowLayout.RIGHT);
        centerPanel.setLayout(centerLayout);
        metalColorPanel.setBorder(metalColorBorder);
        metalColorPanel.setOpaque(false);
        metalColorPanel.setLayout(metalColorLayout);
        compiereColorPanel.setLayout(compiereColorLayout);
        compiereColorPanel.setBorder(compiereColorBorder);
        compiereColorPanel.setOpaque(false);
        metalColorLayout.setColumns(3);
        metalColorLayout.setHgap(5);
        metalColorLayout.setRows(3);
        metalColorLayout.setVgap(5);
        compiereColorLayout.setColumns(4);
        compiereColorLayout.setHgap(5);
        compiereColorLayout.setRows(2);
        compiereColorLayout.setVgap(5);
        fontPanel.setLayout(fontLayout);
        fontLayout.setColumns(3);
        fontLayout.setHgap(5);
        fontLayout.setRows(2);
        fontLayout.setVgap(5);
        centerLayout.setVgap(5);
        fontBorder.setTitle(s_res.getString("Fonts"));
        confirmPanel.setOpaque(false);
        this.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
        confirmPanel.add(bCancel, null);
        confirmPanel.add(bOK, null);
        this.getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(metalColorPanel, BorderLayout.NORTH);
        metalColorPanel.add(primary1, null);
        metalColorPanel.add(primary2, null);
        metalColorPanel.add(primary3, null);
        metalColorPanel.add(secondary1, null);
        metalColorPanel.add(secondary2, null);
        metalColorPanel.add(secondary3, null);
        metalColorPanel.add(white, null);
        metalColorPanel.add(black, null);
        centerPanel.add(compiereColorPanel, BorderLayout.CENTER);
        compiereColorPanel.add(txt_error, null);
        centerPanel.add(fontPanel, BorderLayout.SOUTH);
        fontPanel.add(controlFont, null);
        fontPanel.add(systemFont, null);
        fontPanel.add(menuFont, null);
        fontPanel.add(userFont, null);
        fontPanel.add(windowFont, null);
        fontPanel.add(smallFont, null);
        compiereColorPanel.add(error, null);
        compiereColorPanel.add(inactive, null);
        compiereColorPanel.add(txt_ok, null);
        compiereColorPanel.add(mandatory, null);
        compiereColorPanel.add(info, null);
        bCancel.addActionListener(this);
        bOK.addActionListener(this);

    }		// jbInit

    /**
     *  Load Theme from current Setting (if MetalLookAndFeel)
     */
    private void loadTheme() {

        if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
            CompiereTheme.setTheme();
        } else		// Not a Metal Theme
        {

            primary1.setEnabled(false);
            primary2.setEnabled(false);
            primary3.setEnabled(false);
            secondary1.setEnabled(false);
            secondary2.setEnabled(false);
            secondary3.setEnabled(false);
        }

    }			// loadTheme
}	// CompiereThemeEditor



/*
 * @(#)CompiereThemeEditor.java   02.jul 2007
 * 
 *  Fin del fichero CompiereThemeEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
