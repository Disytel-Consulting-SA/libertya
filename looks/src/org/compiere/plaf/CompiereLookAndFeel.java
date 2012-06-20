/*
 * @(#)CompiereLookAndFeel.java   12.oct 2007  Versión 2.2
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

import java.awt.Color;
import java.awt.Component;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 *  OpenXpertya Look & Feel.
 *  We wanted a nice UI not the battleship gray based stuff.
 *  I guess a matter of taste.
 *  <code>
 *  :
 *  UIManager.setLookAndFeel(new com.compiere.plaf.CompiereLookAndFeel());
 *  // or UIManager.setLookAndFeel("com.compiere.plaf.CompiereLookAndFeel");
 *  </code>
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereLookAndFeel.java,v 1.15 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompiereLookAndFeel extends MetalLookAndFeel {

    /** The name */
    public static final String	NAME	= "OpenXpertya";

    /** The Theme */
    private static CompiereTheme	s_compiereTheme	= new CompiereTheme();

    /** Descripción de Campo */
    private static MetalTheme	s_theme	= s_compiereTheme;

    /** Paint Round Corners */
    protected static boolean	ROUND	= false;

    /**
     *  Constructor
     */
    public CompiereLookAndFeel() {

        super();

        // System.setProperty("awt.visualbell", "true");

    }		// CompiereLookAndFeel

    /** ********************************************************************* */

    /**
     *  Create Default Thems
     */
    protected void createDefaultTheme() {
        setCurrentTheme(s_theme);
    }		// createDefaultTheme

    /**
     * Creates the mapping from UI class IDs to <code>ComponentUI</code> classes,
     * putting the ID-<code>ComponentUI</code> pairs in the passed-in defaults table.
     * Each <code>JComponent</code> class specifies its own UI class ID string.
     *
     * @param table UI Defaults
     */
    protected void initClassDefaults(UIDefaults table) {

        // System.out.println("CompiereLookAndFeel.initClassDefaults");
        super.initClassDefaults(table);

        // Overwrite
        putDefault(table, "PanelUI");
        putDefault(table, "ButtonUI");
        putDefault(table, "ToggleButtonUI");
        putDefault(table, "TabbedPaneUI");
        putDefault(table, "TableHeaderUI");
        putDefault(table, "RadioButtonUI");
        putDefault(table, "CheckBoxUI");
        putDefault(table, "ComboBoxUI");
        putDefault(table, "MenuUI");
        putDefault(table, "MenuBarUI");
        putDefault(table, "ToolBarUI");
        putDefault(table, "RootPaneUI");
        putDefault(table, "ViewportUI");
        putDefault(table, "SplitPaneUI");
        putDefault(table, "ScrollPaneUI");

        /**
         *  CheckBoxMenuItemUI=javax.swing.plaf.basic.BasicCheckBoxMenuItemUI
         *  CheckBoxUI=javax.swing.plaf.metal.MetalCheckBoxUI
         *  ColorChooserUI=javax.swing.plaf.basic.BasicColorChooserUI
         *  DesktopIconUI=javax.swing.plaf.metal.MetalDesktopIconUI
         *  DesktopPaneUI=javax.swing.plaf.basic.BasicDesktopPaneUI
         *  EditorPaneUI=javax.swing.plaf.basic.BasicEditorPaneUI
         *  FileChooserUI=javax.swing.plaf.metal.MetalFileChooserUI
         *  FormattedTextFieldUI=javax.swing.plaf.basic.BasicFormattedTextFieldUI
         *  InternalFrameUI=javax.swing.plaf.metal.MetalInternalFrameUI
         *  LabelUI=javax.swing.plaf.metal.MetalLabelUI
         *  ListUI=javax.swing.plaf.basic.BasicListUI
         *  MenuItemUI=javax.swing.plaf.basic.BasicMenuItemUI
         *  MenuUI=javax.swing.plaf.basic.BasicMenuUI
         *  OptionPaneUI=javax.swing.plaf.basic.BasicOptionPaneUI
         *  PasswordFieldUI=javax.swing.plaf.basic.BasicPasswordFieldUI
         *  PopupMenuSeparatorUI=javax.swing.plaf.metal.MetalPopupMenuSeparatorUI
         *  PopupMenuUI=javax.swing.plaf.basic.BasicPopupMenuUI
         *  ProgressBarUI=javax.swing.plaf.metal.MetalProgressBarUI
         *  RadioButtonMenuItemUI=javax.swing.plaf.basic.BasicRadioButtonMenuItemUI
         *  ScrollBarUI=javax.swing.plaf.metal.MetalScrollBarUI
         *  ScrollPaneUI=javax.swing.plaf.metal.MetalScrollPaneUI
         *  SeparatorUI=javax.swing.plaf.metal.MetalSeparatorUI
         *  SliderUI=javax.swing.plaf.metal.MetalSliderUI
         *  SpinnerUI=javax.swing.plaf.basic.BasicSpinnerUI
         *  TableUI=javax.swing.plaf.basic.BasicTableUI
         *  TextAreaUI=javax.swing.plaf.basic.BasicTextAreaUI
         *  TextFieldUI=javax.swing.plaf.metal.MetalTextFieldUI
         *  TextPaneUI=javax.swing.plaf.basic.BasicTextPaneUI
         *  ToolBarSeparatorUI=javax.swing.plaf.basic.BasicToolBarSeparatorUI
         *  ToolTipUI=javax.swing.plaf.metal.MetalToolTipUI
         *  TreeUI=javax.swing.plaf.metal.MetalTreeUI
         */
    }		// initClassDefaaults

    /**
     *  For overwriting Component defaults
     *  @param table
     */
    protected void initComponentDefaults(UIDefaults table) {

        // System.out.println("CompiereLookAndFeel.initComponentDefaults");
        super.initComponentDefaults(table);

        // ComboBox defaults
        Color	c	= table.getColor("TextField.background");

        table.put("ComboBox.background", c);
        table.put("ComboBox.listBackground", c);
    }		// initComponentDefaults

    /**
     *  For overwriting Component defaults
     *  @param table
     */
    protected void initSystemColorDefaults(UIDefaults table) {

        // System.out.println("CompiereLookAndFeel.initSystemColorDefaults");
        super.initSystemColorDefaults(table);

        // we made the color a bit darker
        // table.put("textHighlight", CompiereUtils.getTranslucentColor(getTextHighlightColor(), 128));
    }		// initSystemColorDefaults

    /**
     *  Error Feedback.
     *  <p>
     *  Invoked when the user attempts an invalid operation,
     *  such as pasting into an uneditable <code>JTextField</code>
     *  that has focus.
     *  </p>
     *  <p>
     *  If the user has enabled visual error indication on
     *  the desktop, this method will flash the caption bar
     *  of the active window. The user can also set the
     *  property awt.visualbell=true to achieve the same
     *  results.
     *  </p>
     *  @param component Component the error occured in, may be
     *                      null indicating the error condition is
     *                      not directly associated with a
     *                      <code>Component</code>.
     */
    public void provideErrorFeedback(Component component) {
        super.provideErrorFeedback(component);
    }		// provideErrorFeedback

    /**
     *  Put "uiKey - ClassName" pair in UIDefaults
     *  @param table
     *  @param uiKey
     */
    private void putDefault(UIDefaults table, String uiKey) {

        try {

            String	className	= "org.compiere.plaf.Compiere" + uiKey;

            table.put(uiKey, className);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }		// putDefault

    //~--- get methods --------------------------------------------------------

    /**
     *  Get OpenXpertya Theme
     *  @return Metal Theme
     */
    public static CompiereTheme getCompiereTheme() {
        return s_compiereTheme;
    }		// getCurrentTheme

    /**
     *  Get Current Theme
     *  @return Metal Theme
     */
    public static MetalTheme getCurrentTheme() {
        return s_theme;
    }		// getCurrentTheme

    /**
     * **********************************************************************
     *
     * @return
     */

    /**
     *  Get/Create Defaults
     *  @return UI Defaults
     */
    public UIDefaults getDefaults() {

        // System.out.println("CompiereLookAndFeel.getDefaults");
        // Theme already created/set
        MetalLookAndFeel.setCurrentTheme(s_theme);

        UIDefaults	defaults	= super.getDefaults();		// calls init..Defaults

        return defaults;
    }		// getDefaults

    /**
     *  The Description
     *  @return description
     */
    public String getDescription() {
        return "OpenXpertya Look & Feel - (c) 2001-2004 Jorg Janke";
    }		// getDescription

    /**
     *  The ID
     *  @return Name
     */
    public String getID() {
        return NAME;
    }		// getID

    /**
     *  The Name
     *  @return Name
     */
    public String getName() {
        return NAME;
    }		// getName

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Current Theme
     *  @param theme metal theme
     */
    public static void setCurrentTheme(MetalTheme theme) {

        if (theme != null) {
            s_theme	= theme;
        }

        MetalLookAndFeel.setCurrentTheme(s_theme);

    }		// setCurrentTheme
}	// CompiereLookAndFeel



/*
 * @(#)CompiereLookAndFeel.java   02.jul 2007
 * 
 *  Fin del fichero CompiereLookAndFeel.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
