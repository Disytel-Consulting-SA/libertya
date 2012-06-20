/*
 * @(#)CompierePLAF.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.Ini;
import org.openXpertya.util.ValueNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 *  Variable Pluggable Look And Feel.
 *  Provides an easy access to the required currently active PLAF information
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompierePLAF.java,v 1.35 2005/03/11 20:34:36 jjanke Exp $
 */
public final class CompierePLAF {

    /** Version tag */
    public static final String	VERSION	= "R1.2.0";

    /** Key of Client Property for CPanel */
    public static final String	TABLEVEL	= "CompiereTabLevel";

    /** Key of Client Property for Rectangle Items - if exists, the standard background is used */
    public static final String	BACKGROUND_FILL	= "CompiereBackgroundFill";

    /** Key of Client Property to paint in CompiereColor */
    public static final String	BACKGROUND	= "CompiereBackground";

    /** Logger */
    private static Logger	log	= Logger.getLogger(CompierePLAF.class.getName());

    /** Default Theme Name */
    public static final String	DEFAULT_THEME	= "";

    /** **** Available L&F ************************************************** */

    /** Default PLAF Name */
    public static final String	DEFAULT_PLAF	= CompiereLookAndFeel.NAME;

    /** Availablle Looks */
    private static ValueNamePair[]	s_looks	= null;

    /** Default PLAF */
    private static ValueNamePair	s_defaultPLAF	= null;

    /** Availablle Themes */
    private static ValueNamePair[]	s_themes	= null;

    /** Descripción de Campo */
    private static ValueNamePair	s_vp_compiereTheme	= null;

    /** Descripción de Campo */
    private static ValueNamePair	s_vp_metalTheme	= null;

    /** Descripción de Campo */
    private static ValueNamePair	s_vp_kunststoffTheme	= null;

    /** Descripción de Campo */
    private static ValueNamePair	s_vp_openxpertyaliquidTheme	= null;

    /** ********************************************************************* */
    static ResourceBundle	s_res	= ResourceBundle.getBundle("org.compiere.plaf.PlafRes");

    /**
     * PLAF class list
     *       com.sun.java.swing.plaf.windows.WindowsLookAndFeel
     *       com.sun.java.swing.plaf.motif.MotifLookAndFeel
     *       javax.swing.plaf.metal.MetalLookAndFeel
     *       com.birosoft.liquid.LiquidLookAndFeel
     *
     *  Static Initializer.
     *  - Fill available PLAFs and Themes
     */
    static {

        ArrayList	plafList	= new ArrayList();
        ValueNamePair	vp		= new ValueNamePair("org.compiere.plaf.CompiereLookAndFeel", CompiereLookAndFeel.NAME);

        plafList.add(vp);

        // Themes
        ArrayList	themeList	= new ArrayList();

        vp	= new ValueNamePair("org.compiere.plaf.CompiereTheme", CompiereTheme.NAME);
        themeList.add(vp);
        s_vp_compiereTheme	= vp;
        vp			= new ValueNamePair("javax.swing.plaf.metal.DefaultMetalTheme", "Steel");
        s_vp_metalTheme	= vp;
        themeList.add(vp);

        // Descubrir e Instalar - OpenXpertya Liquid
        try {

            Class	c	= Class.forName("com.birosoft.liquid.LiquidLookAndFeel");

            vp	= new ValueNamePair("com.birosoft.liquid.LiquidLookAndFeel", "OpenXpertyaLiquid");
            plafList.add(vp);
            vp	= new ValueNamePair("com.birosoft.liquid.LiquidLookAndFeel", "OpenXpertyaLiquid");
            themeList.add(vp);
            s_vp_openxpertyaliquidTheme	= vp;

        } catch (Exception e) {

            // System.err.println("CompierePLAF - Kuststoff not found");
        }

        // Discover and Install - Kuststoff
        try {

            Class	c	= Class.forName("com.incors.plaf.kunststoff.KunststoffLookAndFeel");

            vp	= new ValueNamePair("com.incors.plaf.kunststoff.KunststoffLookAndFeel", "Kunststoff");
            plafList.add(vp);
            vp	= new ValueNamePair("com.incors.plaf.kunststoff.KunststoffTheme", "Kuststoff");
            themeList.add(vp);
            s_vp_kunststoffTheme	= vp;

        } catch (Exception e) {

            // System.err.println("CompierePLAF - Kuststoff not found");
        }

        // Install discovered PLAFs
        for (int i = 0; i < plafList.size(); i++) {

            vp	= (ValueNamePair) plafList.get(i);
            UIManager.installLookAndFeel(vp.getName(), vp.getValue());
        }

        // Fill Available PLAFs
        plafList	= new ArrayList();

        UIManager.LookAndFeelInfo[]	lfInfo	= UIManager.getInstalledLookAndFeels();

        for (int i = 0; i < lfInfo.length; i++) {

            vp	= new ValueNamePair(lfInfo[i].getClassName(), lfInfo[i].getName());
            plafList.add(vp);

            if (lfInfo[i].getName().equals(DEFAULT_PLAF)) {
                s_defaultPLAF	= vp;
            }
        }

        s_looks	= new ValueNamePair[plafList.size()];
        plafList.toArray(s_looks);

        // Fill Available Themes
        s_themes	= new ValueNamePair[themeList.size()];
        themeList.toArray(s_themes);

        //
        // printPLAFDefaults();

    }		// static Initializer

    /**
     * **********************************************************************
     *
     * @param args
     */

    /**
     *  Start Class With OpenXpertya Look or OpenXpertya PLAF Editor
     *  @param args first parameter is class to start, if none start PLAF Editor
     */
    public static void main(String[] args) {

        String	jVersion	= System.getProperty("java.version");

        if (!(jVersion.startsWith("1.4")) && !(jVersion.startsWith("1.5")) && !(jVersion.startsWith("1.6"))) {

            JOptionPane.showMessageDialog(null, "Require Java Version 1.4 or up - Not " + jVersion, "CompierePLAF - Version Conflict", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // set the defined PLAF
        Ini.loadProperties(true);
        CompiereTheme.load();
        setPLAF(null);

        //
        if (args.length == 0) {

            CompierePLAFFrame	frame	= new CompierePLAFFrame();

            return;
        }

        String	className	= args[0];

        // find class
        Class	startClass	= null;

        try {
            startClass	= Class.forName(className);
        } catch (Exception e) {

            System.err.println("Did not find: " + className);
            e.printStackTrace();
            System.exit(1);
        }

        // try static main method
        try {

            Method[]	methods	= startClass.getMethods();

            for (int i = 0; i < methods.length; i++) {

                if (Modifier.isStatic(methods[i].getModifiers()) && methods[i].getName().equals("main")) {

                    String[]	startArgs	= new String[args.length - 1];

                    for (int ii = 1; ii < args.length; ii++) {
                        startArgs[ii - i]	= args[ii];
                    }

                    methods[i].invoke(null, new Object[] { startArgs });
                }

                return;
            }

        } catch (Exception ee) {

            System.err.println("Problems invoking main");
            ee.printStackTrace();
        }

        // start the class
        try {
            startClass.newInstance();
        } catch (Exception e) {

            System.err.println("Cannot start: " + className);
            e.printStackTrace();
            System.exit(1);
        }

    }		// main

    /**
     *  Print current UIDefaults
     */
    public static void printPLAFDefaults() {

        System.out.println(UIManager.getLookAndFeel());

        Object[]	keys	= UIManager.getLookAndFeelDefaults().keySet().toArray();

        Arrays.sort(keys);

        char	lastStart	= ' ';

        for (int i = 0; i < keys.length; i++) {

            StringBuffer	sb	= new StringBuffer();

            sb.append(keys[i]).append(" = ").append(UIManager.get(keys[i]));

            if (keys[i].toString().charAt(0) != lastStart) {

                System.out.println();
                lastStart	= keys[i].toString().charAt(0);
            }

            System.out.println(sb);
        }

    }		// printPLAFDefaults

    /**
     *  Reset PLAF Settings
     *  @param win Window to be reset
     */
    public static void reset(Window win) {

        // Clean Theme Properties
        CompiereTheme.reset();		// sets properties
        CompierePLAF.setPLAF(win);
    }					// reset

    /**
     *  Center Window on Screen and show it
     *  @param window window
     */
    public static void showCenterScreen(Window window) {

        window.pack();

        Dimension	sSize	= Toolkit.getDefaultToolkit().getScreenSize();
        Dimension	wSize	= window.getSize();

        window.setLocation(((sSize.width - wSize.width) / 2), ((sSize.height - wSize.height) / 2));
        window.toFront();
        window.setVisible(true);

    }		// showCenterScreen

    /**
     *  Update UI of this and parent Windows
     *  @param win window
     */
    public static void updateUI(Window win) {

        if (win == null) {
            return;
        }

        Window	c	= win;

        do {

            SwingUtilities.updateComponentTreeUI(c);
            c.invalidate();
            c.pack();
            c.validate();
            c.repaint();
            c	= c.getOwner();

        } while (c != null);

    }		// updateUI

    //~--- get methods --------------------------------------------------------

    /**
     *  Create Cancel Button
     *  @return Cancel button
     */
    public static CButton getCancelButton() {

        CButton	b	= new CButton();

        b.setIcon(new ImageIcon(CompierePLAF.class.getResource("icons/Cancel24.gif")));
        b.setMargin(new Insets(0, 10, 0, 10));
        b.setToolTipText(s_res.getString("Cancel"));

        return b;

    }		// getCancelButton

    /**
     *  Return Error field background (CompiereTheme)
     *  @return Color
     */
    public static Color getFieldBackground_Error() {
        return ColorBlind.getDichromatColor(CompiereTheme.error);
    }		// getFieldBackground_Error

    /**
     *  Return Inactive field background color (CompiereTheme)
     *  @return Color
     */
    public static Color getFieldBackground_Inactive() {
        return ColorBlind.getDichromatColor(CompiereTheme.inactive);
    }		// getFieldBackground_Inactive

    /**
     *  Return Mandatory field background color (CompiereTheme)
     *  @return Color
     */
    public static Color getFieldBackground_Mandatory() {
        return ColorBlind.getDichromatColor(CompiereTheme.mandatory);
    }		// getFieldBackground_Mandatory

    /**
     * **** Background ******************************************************
     *
     * @return
     */

    /**
     *  Return Normal field background color "text".
     *  Windows = white
     *  @return Color
     */
    public static Color getFieldBackground_Normal() {

        // window => white
        return ColorBlind.getDichromatColor(UIManager.getColor("text"));
    }		// getFieldBackground_Normal

    /**
     *  Get Field Font
     *  @return font
     */
    public static Font getFont_Field() {

        return CompiereTheme.userFont;

        // return UIManager.getFont("TextField.font");

    }		// getFont_Field

    /**
     * **** Fonts ***********************************************************
     *
     * @return
     */

    /**
     *  Get Header Font (window/label font)
     *  @return font
     */
    public static Font getFont_Header() {

        return CompiereTheme.windowFont;

        // return UIManager.getFont("Label.font");

    }		// getFont_Header

    /**
     *  Get Label Font
     *  @return font
     */
    public static Font getFont_Label() {

        return CompiereTheme.controlFont;

        // return UIManager.getFont("Label.font");

    }		// setFont_Label

    /**
     *  Get Small (report) Font
     *  @return font
     */
    public static Font getFont_Small() {
        return CompiereTheme.smallFont;
    }		// setFont_Small

    /**
     *  Return form background color "control".
     *  Windows = lightGray
     *  @return Color
     */
    public static Color getFormBackground() {
        return ColorBlind.getDichromatColor(UIManager.getColor("control"));
    }		// getFormBackground

    /**
     *      Info Background Color "info"
     *  Windows = info (light yellow)
     *  @return Color
     */
    public static Color getInfoBackground() {
        return ColorBlind.getDichromatColor(CompiereTheme.info);
    }		// getInfoBackground

    /**
     *  Create OK Button
     *  @return OK button
     */
    public static CButton getOKButton() {

        CButton	b	= new CButton();

        b.setIcon(new ImageIcon(CompierePLAF.class.getResource("icons/Ok24.gif")));
        b.setMargin(new Insets(0, 10, 0, 10));
        b.setToolTipText(s_res.getString("OK"));

        return b;

    }		// getOKButton

    /**
     *  Get available Look And Feels
     *  @return Array of ValueNamePair with name and class of Look and Feel
     */
    public static ValueNamePair[] getPLAFs() {
        return s_looks;
    }		// getPLAFs

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getPrimary1() {
        return ColorBlind.getDichromatColor(CompiereTheme.primary1);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getPrimary2() {
        return ColorBlind.getDichromatColor(CompiereTheme.primary2);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getPrimary3() {
        return ColorBlind.getDichromatColor(CompiereTheme.primary3);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getSecondary1() {
        return ColorBlind.getDichromatColor(CompiereTheme.secondary1);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getSecondary2() {
        return ColorBlind.getDichromatColor(CompiereTheme.secondary2);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Color getSecondary3() {
        return ColorBlind.getDichromatColor(CompiereTheme.secondary3);
    }

    /**
     *      Issue Text Foreground Color (Theme)
     *  @return Color
     */
    public static Color getTextColor_Issue() {
        return ColorBlind.getDichromatColor(CompiereTheme.txt_error);
    }		// getText_Issue

    /**
     *  Label Text foreground Color "controlText"
     *  Windows = black
     *  @return Color
     */
    public static Color getTextColor_Label() {
        return ColorBlind.getDichromatColor(UIManager.getColor("controlText"));
    }		// getTextColor_Label

    /**
     * **** Text ************************************************************
     *
     * @return
     */

    /**
     *      Normal field text foreground color "textText"
     *  Windows = black
     *  @return Color
     */
    public static Color getTextColor_Normal() {
        return ColorBlind.getDichromatColor(UIManager.getColor("textText"));
    }		// getText_Normal

    /**
     *      OK Text Foreground Color (Theme)
     *  @return Color
     */
    public static Color getTextColor_OK() {
        return ColorBlind.getDichromatColor(CompiereTheme.txt_ok);
    }		// getText_OK

    /**
     *  Get the list of available Metal Themes if the current L&F is a Metal L&F
     *  @return Array of Strings with Names of Metal Themes
     */
    public static ValueNamePair[] getThemes() {

        if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
            return s_themes;
        }

        return new ValueNamePair[0];

    }		// getThemes

    /**
     *  Is CompiereL&F the active L&F
     *  @return true if L&F is OpenXpertya
     */
    public static boolean isActive() {
        return UIManager.getLookAndFeel() instanceof CompiereLookAndFeel;
    }		// isActive

    //~--- set methods --------------------------------------------------------

    /**
     *  Set PLAF based on Ini Properties
     *  @param win Optional Window
     */
    public static void setPLAF(Window win) {

        String	look		= Ini.getProperty(Ini.P_UI_LOOK);
        String	lookTheme	= Ini.getProperty(Ini.P_UI_THEME);

        // Search for PLAF
        ValueNamePair	plaf	= null;

        for (int i = 0; i < s_looks.length; i++) {

            if (s_looks[i].getName().equals(look)) {

                plaf	= s_looks[i];

                break;
            }
        }

        // Search for Theme
        ValueNamePair	theme	= null;

        for (int i = 0; i < s_themes.length; i++) {

            if (s_themes[i].getName().equals(lookTheme)) {

                theme	= s_themes[i];

                break;
            }
        }

        // Set PLAF
        setPLAF((plaf == null)
                ? s_defaultPLAF
                : plaf, theme, win);

    }		// setPLAF

    /**
     *  Set PLAF and update Ini
     *
     *  @param plaf     ValueNamePair of the PLAF to be set
     *  @param theme    Optional Theme name
     *  @param win      Optional Window
     */
    public static void setPLAF(ValueNamePair plaf, ValueNamePair theme, Window win) {

        if (plaf == null) {
            return;
        }

        log.config(plaf + ((theme == null)
                           ? ""
                           : (" - " + theme)));

        // Look & Feel
        try {
            UIManager.setLookAndFeel(plaf.getValue());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }

        LookAndFeel	laf	= UIManager.getLookAndFeel();

        Ini.setProperty(Ini.P_UI_LOOK, plaf.getName());

        // Optional Theme
        Ini.setProperty(Ini.P_UI_THEME, "");

        // Default Theme
        if ((theme == null) && (laf instanceof MetalLookAndFeel)) {

            String	className	= laf.getClass().getName();

            if (className.equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
                theme	= s_vp_metalTheme;
            } else if (className.equals("org.compiere.plaf.CompiereLookAndFeel")) {
                theme	= s_vp_compiereTheme;
            } else if (className.equals("com.incors.plaf.kunststoff.KunststoffLookAndFeel")) {
                theme	= s_vp_kunststoffTheme;
            } else if (className.equals("com.birosoft.liquid.LiquidLookAndFeel")) {
                theme	= s_vp_openxpertyaliquidTheme;
            }
        }

        if ((theme != null) && (laf instanceof MetalLookAndFeel) && (theme.getValue().length() > 0)) {

            try {

                Class		c	= Class.forName(theme.getValue());
                MetalTheme	t	= (MetalTheme) c.newInstance();

                if (laf instanceof CompiereLookAndFeel) {
                    CompiereLookAndFeel.setCurrentTheme(t);
                } else {
                    MetalLookAndFeel.setCurrentTheme(t);
                }

                //
                CompiereTheme.setTheme(t);	// copies it if not CompiereTheme
                Ini.setProperty(Ini.P_UI_THEME, theme.getName());

            } catch (Exception e) {
                System.err.println("CompierePLAF.setPLAF Theme - " + e.getMessage());
            }
        }

        updateUI(win);

        // printPLAFDefaults();

    }		// setPLAF
}	// CompierePLAF


/**
 *  Frame to display Editor
 */
class CompierePLAFFrame extends JFrame {

    /**
     *  Frame to display Editor
     */
    public CompierePLAFFrame() {

        super("CompierePLAF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(CompierePLAF.class.getResource("icons/CL16.gif")));
        CompierePLAF.showCenterScreen(this);

    }		// CompierePLAFFrame

    /**
     *  Show Editor
     *  @param e event
     */
    protected void processWindowEvent(WindowEvent e) {

        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_OPENED) {

            CompierePLAFEditor	ed	= new CompierePLAFEditor(this, true);

            dispose();
        }

    }		// processWindowEvents
}	// CompierePLAFFrame



/*
 * @(#)CompierePLAF.java   02.jul 2007
 * 
 *  Fin del fichero CompierePLAF.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
