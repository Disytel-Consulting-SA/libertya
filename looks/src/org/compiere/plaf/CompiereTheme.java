/*
 * @(#)CompiereTheme.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.Ini;

import sun.awt.AppContext;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import java.util.logging.Logger;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalTheme;

/**
 *  OpenXpertya User definable Theme (if used in Metal L&F).
 *  In other Environments, it provides UI extensions (e.g. Error Color)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereTheme.java,v 1.14 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompiereTheme extends MetalTheme {

    /** Theme Name */
    public static final String	NAME	= "OpenXpertya Theme";

    /** Logger */
    private static Logger	log	= Logger.getLogger(CompiereTheme.class.getName());

    /** Blue 51,51,102 */
    protected static ColorUIResource	primary0	= new ColorUIResource(51, 51, 102);

    /** Descripción de Campo */
    private static final String	P_Window	= "#FontWindow";

    /** Descripción de Campo */
    private static final String	P_White	= "#ColorWhite";

    /** Descripción de Campo */
    private static final String	P_User	= "#FontUser";

    /** Descripción de Campo */
    private static final String	P_Txt_OK	= "#ColorTextOK";

    /** Descripción de Campo */
    private static final String	P_Txt_Error	= "#ColorTextError";

    /** Descripción de Campo */
    private static final String	P_System	= "#FontSystem";

    /** Descripción de Campo */
    private static final String	P_Small	= "#FontSmall";

    /** Descripción de Campo */
    private static final String	P_Secondary3	= "#ColorSecondary3";

    /** Descripción de Campo */
    private static final String	P_Secondary2	= "#ColorSecondary2";

    /** Descripción de Campo */
    private static final String	P_Secondary1	= "#ColorSecondary1";

    /** Descripción de Campo */
    private static final String	P_Primary3	= "#ColorPrimary3";

    /** Descripción de Campo */
    private static final String	P_Primary2	= "#ColorPrimary2";

    // Static property info

    /** Descripción de Campo */
    private static final String	P_Primary1	= "#ColorPrimary1";

    /** Descripción de Campo */
    private static final String	P_Menu	= "#FontMenu";

    /** Descripción de Campo */
    private static final String	P_Mandatory	= "#ColorMandatory";

    /** Descripción de Campo */
    private static final String	P_Info	= "#ColorInfo";

    /** Descripción de Campo */
    private static final String	P_Inactive	= "#ColorInactive";

    /** Descripción de Campo */
    private static final String	P_Error	= "#ColorError";

    //

    /** Descripción de Campo */
    private static final String	P_Control	= "#FontControl";

    //

    /** Descripción de Campo */
    protected static final String	P_CompiereColor	= "#CompiereColor";

    /** Descripción de Campo */
    private static final String	P_Black	= "#ColorBlack";

    /** Default Font Size */
    public static final int	FONT_SIZE	= 12;

    /** Default Font */
    public static final String	FONT_DEFAULT	= "Dialog";

    /** Black */
    protected static ColorUIResource	black;

    /** Control font */
    protected static FontUIResource	controlFont;

    /** Background for fields in error */
    protected static ColorUIResource	error;

    /** Background for inactive fields */
    protected static ColorUIResource	inactive;

    /** Background for info fields */
    protected static ColorUIResource	info;

    /** Background for mandatory fields */
    protected static ColorUIResource	mandatory;

    /** Menu font */
    protected static FontUIResource	menuFont;

    /** Blue 102, 102, 153 */
    protected static ColorUIResource	primary1;

    /** Blue 153, 153, 204 */
    protected static ColorUIResource	primary2;

    /** Blue 204, 204, 255 */
    protected static ColorUIResource	primary3;

    /** Gray 102, 102, 102 */
    protected static ColorUIResource	secondary1;

    /** Gray 153, 153, 153 */
    protected static ColorUIResource	secondary2;

    /** Gray 204, 204, 204 */
    protected static ColorUIResource	secondary3;

    /** Small font */
    protected static FontUIResource	smallFont;

    /** System font */
    protected static FontUIResource	systemFont;

    /** Foreground Text Error */
    protected static ColorUIResource	txt_error;

    /** Foreground Text OK */
    protected static ColorUIResource	txt_ok;

    /** User font */
    protected static FontUIResource	userFont;

    /** White */
    protected static ColorUIResource	white;

    /** Window Title font */
    protected static FontUIResource	windowFont;

    /**
     *  Static Init
     */
    static {
        setDefault();
    }

    /** Black */
    protected final ColorUIResource	secondary0	= new ColorUIResource(0, 0, 0);

    /** White */
    protected final ColorUIResource	secondary4	= new ColorUIResource(255, 255, 255);

    /**
     *  Constructor - nop
     */
    public CompiereTheme() {}		// CompiereTheme

    /**
     *  Control Font (plain)
     *  @return font
     */
    private static FontUIResource _getControlTextFont() {

        if (controlFont == null) {

            try {
                controlFont	= new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font(FONT_DEFAULT, Font.PLAIN, FONT_SIZE)));
            } catch (Exception e) {
                controlFont	= new FontUIResource(FONT_DEFAULT, Font.PLAIN, FONT_SIZE);
            }
        }

        return controlFont;
    }

    /**
     *  Menu
     *  @return font
     */
    private static FontUIResource _getMenuTextFont() {

        if (menuFont == null) {

            try {
                menuFont	= new FontUIResource(Font.getFont("swing.plaf.metal.menuFont", new Font(FONT_DEFAULT, Font.PLAIN, FONT_SIZE)));
            } catch (Exception e) {
                menuFont	= new FontUIResource(FONT_DEFAULT, Font.PLAIN, FONT_SIZE);
            }
        }

        return menuFont;
    }

    /**
     *  Sub Text
     *  @return font
     */
    private static FontUIResource _getSubTextFont() {

        if (smallFont == null) {

            try {
                smallFont	= new FontUIResource(Font.getFont("swing.plaf.metal.smallFont", new Font(FONT_DEFAULT, Font.PLAIN, FONT_SIZE - 2)));
            } catch (Exception e) {
                smallFont	= new FontUIResource(FONT_DEFAULT, Font.PLAIN, FONT_SIZE - 2);
            }
        }

        return smallFont;
    }

    /**
     *  System Font
     *  @return font
     */
    private static FontUIResource _getSystemTextFont() {

        if (systemFont == null) {

            try {
                systemFont	= new FontUIResource(Font.getFont("swing.plaf.metal.systemFont", new Font(FONT_DEFAULT, Font.PLAIN, FONT_SIZE)));
            } catch (Exception e) {
                systemFont	= new FontUIResource(FONT_DEFAULT, Font.PLAIN, FONT_SIZE);
            }
        }

        return systemFont;
    }

    /**
     *  User Font
     *  @return font
     */
    private static FontUIResource _getUserTextFont() {

        if (userFont == null) {

            try {
                userFont	= new FontUIResource(Font.getFont("swing.plaf.metal.userFont", new Font(FONT_DEFAULT, Font.PLAIN, FONT_SIZE)));
            } catch (Exception e) {
                userFont	= new FontUIResource(FONT_DEFAULT, Font.PLAIN, FONT_SIZE);
            }
        }

        return userFont;
    }

    /**
     *  Window Title
     *  @return font
     */
    private static FontUIResource _getWindowTitleFont() {

        if (windowFont == null) {

            try {
                windowFont	= new FontUIResource(Font.getFont("swing.plaf.metal.windowFont", new Font(FONT_DEFAULT, Font.BOLD, FONT_SIZE + 2)));
            } catch (Exception e) {
                windowFont	= new FontUIResource(FONT_DEFAULT, Font.BOLD, FONT_SIZE + 2);
            }
        }

        return windowFont;
    }

    /**
     *  Load Properties from Ini
     */
    public static void load() {

        primary1	= parseColor(Ini.getProperty(P_Primary1), primary1);
        primary2	= parseColor(Ini.getProperty(P_Primary2), primary2);
        primary3	= parseColor(Ini.getProperty(P_Primary3), primary3);
        secondary1	= parseColor(Ini.getProperty(P_Secondary1), secondary1);
        secondary2	= parseColor(Ini.getProperty(P_Secondary2), secondary2);
        secondary3	= parseColor(Ini.getProperty(P_Secondary3), secondary3);
        error		= parseColor(Ini.getProperty(P_Error), error);
        info		= parseColor(Ini.getProperty(P_Info), info);
        mandatory	= parseColor(Ini.getProperty(P_Mandatory), mandatory);
        inactive	= parseColor(Ini.getProperty(P_Inactive), inactive);
        white		= parseColor(Ini.getProperty(P_White), white);
        black		= parseColor(Ini.getProperty(P_Black), black);
        txt_ok		= parseColor(Ini.getProperty(P_Txt_OK), txt_ok);
        txt_error	= parseColor(Ini.getProperty(P_Txt_Error), txt_error);

        //
        controlFont	= parseFont(Ini.getProperty(P_Control), controlFont);
        systemFont	= parseFont(Ini.getProperty(P_System), systemFont);
        userFont	= parseFont(Ini.getProperty(P_User), userFont);
        smallFont	= parseFont(Ini.getProperty(P_Small), smallFont);
        windowFont	= parseFont(Ini.getProperty(P_Window), windowFont);
        menuFont	= parseFont(Ini.getProperty(P_Menu), menuFont);

        //
        CompiereColor.setDefaultBackground(CompiereColor.parse(Ini.getProperty(P_CompiereColor)));

    }		// load

    /**
     *  Parse Color.
     *  <p>
     *  Color - [r=102,g=102,b=153,a=0]
     *
     *  @param information string information to be parsed
     *  @param stdColor color used if info cannot parsed
     *  @return color
     *  @see #getColorAsString
     */
    protected static ColorUIResource parseColor(String information, ColorUIResource stdColor) {

        if ((information == null) || (information.length() == 0) || (information.trim().length() == 0)) {
            return stdColor;
        }

        // System.out.print("ParseColor=" + info);
        try {

            int	r	= Integer.parseInt(information.substring(information.indexOf("r=") + 2, information.indexOf(",g=")));
            int	g	= Integer.parseInt(information.substring(information.indexOf("g=") + 2, information.indexOf(",b=")));
            int	b	= 0;
            int	a	= 255;

            if (information.indexOf("a=") == -1) {
                b	= Integer.parseInt(information.substring(information.indexOf("b=") + 2, information.indexOf("]")));
            } else {

                b	= Integer.parseInt(information.substring(information.indexOf("b=") + 2, information.indexOf(",a=")));
                a	= Integer.parseInt(information.substring(information.indexOf("a=") + 2, information.indexOf("]")));
            }

            ColorUIResource	retValue	= new ColorUIResource(new Color(r, g, b, a));

            // System.out.println(" - " + retValue.toString());
            return retValue;

        } catch (Exception e) {
            log.config(information + " - cannot parse: " + e.toString());
        }

        return stdColor;

    }		// parseColor

    /**
     *  Parse Font
     *  <p>
     *  javax.swing.plaf.FontUIResource[family=dialog.bold,name=Dialog,style=bold,size=12]
     *
     *  @param information string information to be parsed
     *  @param stdFont font used if info cannot be parsed
     *  @return font
     */
    private static FontUIResource parseFont(String information, FontUIResource stdFont) {

        if ((information == null) || (information.length() == 0) || (information.trim().length() == 0)) {
            return stdFont;
        }

        // System.out.print("ParseFont=" + info);
        try {

            String	name	= information.substring(information.indexOf("name=") + 5, information.indexOf(",style="));
            String	s	= information.substring(information.indexOf("style=") + 6, information.indexOf(",size="));
            int	style	= Font.PLAIN;

            if (s.equals("bold")) {
                style	= Font.BOLD;
            } else if (s.equals("italic")) {
                style	= Font.ITALIC;
            } else if (s.equals("bolditalic")) {
                style	= Font.BOLD | Font.ITALIC;
            }

            int	size	= Integer.parseInt(information.substring(information.indexOf(",size=") + 6, information.lastIndexOf("]")));
            FontUIResource	retValue	= new FontUIResource(name, style, size);

            // System.out.println(" - " + retValue.toString());
            return retValue;

        } catch (Exception e) {
            log.config(information + " - cannot parse: " + e.toString());
        }

        return stdFont;

    }		// parseFont

    /**
     *  Reset Info in Properties
     */
    public static void reset() {

/**
                Ini.remove (P_Primary1);
                Ini.remove (P_Primary2);
                Ini.remove (P_Primary3);
                Ini.remove (P_Secondary1);
                Ini.remove (P_Secondary2);
                Ini.remove (P_Secondary3);
                Ini.remove (P_Error);
                Ini.remove (P_Info);
                Ini.remove (P_Mandatory);
                Ini.remove (P_Inactive);
                Ini.remove (P_White);
                Ini.remove (P_Black);
                Ini.remove (P_Txt_OK);
                Ini.remove (P_Txt_Error);
                //
                Ini.remove (P_Control);
                Ini.remove (P_System);
                Ini.remove (P_User);
                Ini.remove (P_Small);
                Ini.remove (P_Window);
                Ini.remove (P_Menu);
                //  CompiereColor
                Ini.remove(P_CompiereColor);
**/

        // Initialize
        Ini.setProperty(Ini.P_UI_LOOK, CompiereLookAndFeel.NAME);
        Ini.setProperty(Ini.P_UI_THEME, NAME);

        //
        setDefault();

        // Background
        // CompiereColor cc = new CompiereColor(SystemColor.control);      //  flat Windows 212-208-200
        // CompiereColor cc = new CompiereColor(secondary3);               //  flat Metal   204-204-204
        CompiereColor	cc	= new CompiereColor(secondary3, false);

        CompiereColor.setDefaultBackground(cc);

        //
        save();		// save properties
    }			// reset

    /**
     *  Save information in Properties
     */
    public static void save() {

        // System.out.println("CompiereTheme.save - " + CompiereColor.getDefaultBackground().toString());
        //
        Ini.setProperty(P_Primary1, getColorAsString(primary1));
        Ini.setProperty(P_Primary2, getColorAsString(primary2));
        Ini.setProperty(P_Primary3, getColorAsString(primary3));
        Ini.setProperty(P_Secondary1, getColorAsString(secondary1));
        Ini.setProperty(P_Secondary2, getColorAsString(secondary2));
        Ini.setProperty(P_Secondary3, getColorAsString(secondary3));
        Ini.setProperty(P_Error, getColorAsString(error));
        Ini.setProperty(P_Info, getColorAsString(info));
        Ini.setProperty(P_Mandatory, getColorAsString(mandatory));
        Ini.setProperty(P_Inactive, getColorAsString(inactive));
        Ini.setProperty(P_White, getColorAsString(white));
        Ini.setProperty(P_Black, getColorAsString(black));
        Ini.setProperty(P_Txt_OK, getColorAsString(txt_ok));
        Ini.setProperty(P_Txt_Error, getColorAsString(txt_error));

        //
        Ini.setProperty(P_Control, ((Font) controlFont).toString());
        Ini.setProperty(P_System, ((Font) systemFont).toString());
        Ini.setProperty(P_User, ((Font) userFont).toString());
        Ini.setProperty(P_Small, ((Font) smallFont).toString());
        Ini.setProperty(P_Window, ((Font) windowFont).toString());
        Ini.setProperty(P_Menu, ((Font) menuFont).toString());

        //
        Ini.setProperty(P_CompiereColor, CompiereColor.getDefaultBackground().toString());
    }		// save

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getBlack() {
        return ColorBlind.getDichromatColorUIResource(black);
    }

    /**
     *  Parses Color into String representation.
     *  Required as SystemColors and Alpha Colors have different formats
     *  @param c Color
     *  @return [r=102,g=102,b=153,a=255]
     *  @see #parseColor
     */
    public static String getColorAsString(Color c) {

        if (c == null) {
            c	= SystemColor.control;
        }

        StringBuffer	sb	= new StringBuffer("[r=").append(c.getRed()).append(",g=").append(c.getGreen()).append(",b=").append(c.getBlue()).append(",a=").append(c.getAlpha()).append("]");

        // System.out.println(sb.toString());
        return sb.toString();

    }		// getColorAsString

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getControlTextFont() {
        return _getControlTextFont();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getMenuTextFont() {
        return _getMenuTextFont();
    }

    /**
     *  Return Theme Name
     *  @return Theme Name
     */
    public String getName() {
        return NAME;
    }		// getName

    /**
     *  Get Primary 1 (blue in default Metal Theme)
     *  @return color
     */
    public ColorUIResource getPrimary1() {
        return ColorBlind.getDichromatColorUIResource(primary1);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getPrimary2() {
        return ColorBlind.getDichromatColorUIResource(primary2);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getPrimary3() {
        return ColorBlind.getDichromatColorUIResource(primary3);
    }

    /**
     *  Get Seconary 1 (gray in default Metal Theme)
     *  @return color
     */
    public ColorUIResource getSecondary0() {
        return ColorBlind.getDichromatColorUIResource(secondary0);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getSecondary1() {
        return ColorBlind.getDichromatColorUIResource(secondary1);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getSecondary2() {
        return ColorBlind.getDichromatColorUIResource(secondary2);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getSecondary3() {
        return ColorBlind.getDichromatColorUIResource(secondary3);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getSecondary4() {
        return ColorBlind.getDichromatColorUIResource(secondary4);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getSubTextFont() {
        return _getSubTextFont();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getSystemTextFont() {
        return _getSystemTextFont();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getUserTextFont() {
        return _getUserTextFont();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public ColorUIResource getWhite() {
        return ColorBlind.getDichromatColorUIResource(white);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public FontUIResource getWindowTitleFont() {
        return _getWindowTitleFont();
    }

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Defaults
     */
    private static void setDefault() {

        /** Blue 102, 102, 153 */
        primary1	= new ColorUIResource(102, 102, 153);

        /** Blue 153, 153, 204 */
        primary2	= new ColorUIResource(153, 153, 204);

        /** Blue 204, 204, 255 */
        primary3	= new ColorUIResource(204, 204, 255);

        /** Gray 102, 102, 102 */
        secondary1	= new ColorUIResource(102, 102, 102);

        /** Gray 153, 153, 153 */
        secondary2	= new ColorUIResource(153, 153, 153);

        /** Gray 204, 204, 204 */
        secondary3	= new ColorUIResource(204, 204, 204);

        /** Black */
        black	= new ColorUIResource(Color.black);

        /** White */
        white	= new ColorUIResource(Color.white);

        /** Background for mandatory fields */
        mandatory	= new ColorUIResource(224, 224, 255);		// blue-isch

        /** Background for fields in error */
        error	= new ColorUIResource(255, 204, 204);		// red-isch

        /** Background for inactive fields */
        inactive	= new ColorUIResource(234, 234, 234);		// light gray

        /** Background for info fields */
        info	= new ColorUIResource(253, 237, 207);		// light yellow

        /** Foreground Text OK */
        txt_ok	= new ColorUIResource(51, 51, 102);	// dark blue

        /** Foreground Text Error */
        txt_error	= new ColorUIResource(204, 0, 0);	// dark red

        /** Control font */
        controlFont	= null;
        _getControlTextFont();

        /** System font */
        systemFont	= null;
        _getSystemTextFont();

        /** User font */
        userFont	= null;
        _getUserTextFont();

        /** Small font */
        smallFont	= null;
        _getSubTextFont();

        /** Window Title font */
        windowFont	= null;
        _getWindowTitleFont();

        /** Menu font */
        menuFont	= null;
        _getMenuTextFont();
    }		// setDefault

    /**
     *  Set Theme to current Metal Theme and copy it
     */
    public static void setTheme() {

        AppContext	context		= AppContext.getAppContext();
        MetalTheme	copyFrom	= (MetalTheme) context.get("currentMetalTheme");

        setTheme(copyFrom);

    }		// setTheme

    /**
     *  Set Theme to current Metal Theme and copy it
     *  @param copyFrom
     */
    public static void setTheme(MetalTheme copyFrom) {

        if ((copyFrom == null) || (copyFrom instanceof CompiereTheme)) {
            return;
        }

        // May not be correct, if Themes overwrites default methods
        primary1	= copyFrom.getPrimaryControlDarkShadow();
        primary2	= copyFrom.getPrimaryControlShadow();
        primary3	= copyFrom.getPrimaryControl();
        secondary1	= copyFrom.getControlDarkShadow();
        secondary2	= copyFrom.getControlShadow();
        secondary3	= copyFrom.getControl();
        CompierePanelUI.setDefaultBackground(new CompiereColor(secondary3, true));
        white	= copyFrom.getPrimaryControlHighlight();
        black	= copyFrom.getPrimaryControlInfo();

        //
        controlFont	= copyFrom.getControlTextFont();
        systemFont	= copyFrom.getSystemTextFont();
        userFont	= copyFrom.getUserTextFont();
        smallFont	= copyFrom.getSubTextFont();
        menuFont	= copyFrom.getMenuTextFont();
        windowFont	= copyFrom.getWindowTitleFont();

    }		// setTheme
}	// CompiereTheme



/*
 * @(#)CompiereTheme.java   02.jul 2007
 * 
 *  Fin del fichero CompiereTheme.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
