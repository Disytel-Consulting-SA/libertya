/*
 * @(#)PlafRes_th.java   12.oct 2007  Versión 2.2
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

import java.util.ListResourceBundle;

/**
 *  Translation Texts for Look & Feel (Thai)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Sureeraya Limpaibul
 *  @version    $Id: PlafRes_th.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_th extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "\u0e23\u0e39\u0e1b\u0e41\u0e1a\u0e1a\u0e02\u0e2d\u0e07\u0e1e\u0e37\u0e49\u0e19\u0e2b\u0e25\u0e31\u0e07" }, { "BackColType_Flat", "Flat" }, { "BackColType_Gradient", "Gradient" }, { "BackColType_Lines", "Lines" }, { "BackColType_Texture", "Texture" },

        //
        { "LookAndFeelEditor", "Look & Feel Editor" }, { "LookAndFeel", "Look & Feel" }, { "Theme", "Theme" }, { "EditCompiereTheme", "Edit OpenXpertya Theme" }, { "SetDefault", "Default Background" }, { "SetDefaultColor", "Background Color" }, { "ColorBlind", "Color Deficiency" }, { "Example", "\u0e15\u0e31\u0e27\u0e2d\u0e22\u0e48\u0e32\u0e07" }, { "Reset", "Reset" }, { "OK", "\u0e15\u0e01\u0e25\u0e07" }, { "Cancel", "\u0e22\u0e01\u0e40\u0e25\u0e34\u0e01" },

        //
        { "CompiereThemeEditor", "OpenXpertya Theme Editor" }, { "MetalColors", "Metal Colors" }, { "CompiereColors", "OpenXpertya Colors" }, { "CompiereFonts", "OpenXpertya Fonts" }, { "Primary1Info", "Shadow, Separator" }, { "Primary1", "Primary 1" }, { "Primary2Info", "Focus Line, Selected Menu" }, { "Primary2", "Primary 2" }, { "Primary3Info", "Table Selected Row, Selected Text, ToolTip Background" }, { "Primary3", "Primary 3" }, { "Secondary1Info", "Border Lines" }, { "Secondary1", "Secondary 1" }, { "Secondary2Info", "Inactive Tabs, Pressed Fields, Inactive Border + Text" }, { "Secondary2", "Secondary 2" }, { "Secondary3Info", "Background" }, { "Secondary3", "Secondary 3" },

        //
        { "ControlFontInfo", "Control Font" }, { "ControlFont", "Label Font" }, { "SystemFontInfo", "Tool Tip, Tree nodes" }, { "SystemFont", "System Font" }, { "UserFontInfo", "User Entered Data" }, { "UserFont", "Field Font" },

//      { "SmallFontInfo",          "Reports" },
        { "SmallFont", "\u0e2d\u0e31\u0e01\u0e29\u0e23\u0e02\u0e19\u0e32\u0e14\u0e40\u0e25\u0e47\u0e01" }, { "WindowTitleFont", "Title Font" }, { "MenuFont", "\u0e40\u0e21\u0e19\u0e39\u0e2d\u0e31\u0e01\u0e29\u0e23" },

        //
        { "MandatoryInfo", "Mandatory Field Background" }, { "Mandatory", "Mandatory" }, { "ErrorInfo", "Error Field Background" }, { "Error", "Error" }, { "InfoInfo", "Info Field Background" }, { "Info", "Info" }, { "WhiteInfo", "Lines" }, { "White", "White" }, { "BlackInfo", "Lines, Text" }, { "Black", "Black" }, { "InactiveInfo", "Inactive Field Background" }, { "Inactive", "Inactive" }, { "TextOKInfo", "OK Text Foreground" }, { "TextOK", "Text - OK" }, { "TextIssueInfo", "Error Text Foreground" }, { "TextIssue", "Text - Error" },

        //
        { "FontChooser", "\u0e40\u0e25\u0e37\u0e2d\u0e01\u0e41\u0e1a\u0e1a\u0e2d\u0e31\u0e01\u0e29\u0e23" }, { "Fonts", "\u0e15\u0e31\u0e27\u0e2d\u0e31\u0e01\u0e29\u0e23" }, { "Plain", "\u0e18\u0e23\u0e23\u0e21\u0e14\u0e32" }, { "Italic", "\u0e15\u0e31\u0e27\u0e40\u0e2d\u0e35\u0e22\u0e07" }, { "Bold", "\u0e15\u0e31\u0e27\u0e2b\u0e19\u0e32" }, { "BoldItalic", "\u0e15\u0e31\u0e27\u0e2b\u0e19\u0e32 \u0e15\u0e31\u0e27\u0e40\u0e2d\u0e35\u0e22\u0e07" }, { "Name", "\u0e0a\u0e37\u0e48\u0e2d" }, { "Size", "\u0e02\u0e19\u0e32\u0e14" }, { "Style", "\u0e25\u0e31\u0e01\u0e29\u0e13\u0e30\u0e41\u0e1a\u0e1a\u0e2d\u0e31\u0e01\u0e29\u0e23" }, { "TestString", "This is just a Test! The quick brown Fox is doing something. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "\u0e15\u0e31\u0e27\u0e2d\u0e31\u0e01\u0e29\u0e23" },

        //
        { "CompiereColorEditor", "OpenXpertya Color Editor" }, { "CompiereType", "Color Type" }, { "GradientUpperColor", "Gradient Upper Color" }, { "GradientLowerColor", "Gradient Lower Color" }, { "GradientStart", "Gradient Start" }, { "GradientDistance", "Gradient Distance" }, { "TextureURL", "Texture URL" }, { "TextureAlpha", "Texture Alpha" }, { "TextureTaintColor", "Texture Taint Color" }, { "LineColor", "Line Color" }, { "LineBackColor", "Background Color" }, { "LineWidth", "Line Width" }, { "LineDistance", "Line Distance" }, { "FlatColor", "Flat Color" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Get Contents
     * @return contents
     */
    public Object[][] getContents() {
        return contents;
    }
}	// Res



/*
 * @(#)PlafRes_th.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_th.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
