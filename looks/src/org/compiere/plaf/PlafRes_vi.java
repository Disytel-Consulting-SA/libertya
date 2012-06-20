/*
 * @(#)PlafRes_vi.java   12.oct 2007  Versión 2.2
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
 *  Translation Texts for Look & Feel
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Bui Chi Trung
 *  @version    $Id: PlafRes_vi.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_vi extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Lo\u1EA1i màu n\u1EC1n" }, { "BackColType_Flat", "Ph\u1EB3ng" }, { "BackColType_Gradient", "D\u1ED1c" }, { "BackColType_Lines", "\u0110\u01B0\u1EDDng th\u1EB3ng" }, { "BackColType_Texture", "\u0110an chéo" },

        //
        { "LookAndFeelEditor", "Công c\u1EE5 thay \u0111\u1ED5i V\u1EBB ngoài và C\u1EA3m nh\u1EADn" }, { "LookAndFeel", "V\u1EBB ngoài và C\u1EA3m nh\u1EADn" }, { "Theme", "Ch\u1EE7 \u0111\u1EC1" }, { "EditCompiereTheme", "Hi\u1EC7u ch\u1EC9nh ch\u1EE7 \u0111\u1EC1 C\u0103mpia-\u01A1" }, { "SetDefault", "N\u1EC1n m\u1EB7c nhiên" }, { "SetDefaultColor", "Màu n\u1EC1n" }, { "ColorBlind", "Mù màu" }, { "Example", "Ví d\u1EE5" }, { "Reset", "C\u1EA5u hình l\u1EA1i" }, { "OK", "\u0110\u1ED3ng ý" }, { "Cancel", "H\u1EE7y" },

        //
        { "CompiereThemeEditor", "Công c\u1EE5 thay \u0111\u1ED5i ch\u1EE7 \u0111\u1EC1 C\u0103mpia-\u01A1" }, { "MetalColors", "B\u1ED9 màu ánh kim" }, { "CompiereColors", "B\u1ED9 màu C\u0103mpia-\u01A1" }, { "CompiereFonts", "B\u1ED9 Font C\u0103mpia-\u01A1" }, { "Primary1Info", "Bóng, Tách bi\u1EC7t" }, { "Primary1", "Ch\u1EE7 \u0111\u1EA1o 1" }, { "Primary2Info", "\u0110\u01B0\u1EDDng tiêu \u0111i\u1EC3m, Th\u1EF1c \u0111\u01A1n \u0111\u01B0\u1EE3c ch\u1ECDn" }, { "Primary2", "Ch\u1EE7 \u0111\u1EA1o 2" }, { "Primary3Info", "Table Selected Row, Selected Text, ToolTip Background" }, { "Primary3", "Primary 3" }, { "Secondary1Info", "\u0110\u01B0\u1EDDng biên" }, { "Secondary1", "Ph\u1EE5 1" }, { "Secondary2Info", "Inactive Tabs, Pressed Fields, Inactive Border + Text" }, { "Secondary2", "Ph\u1EE5 2" }, { "Secondary3Info", "N\u1EC1n" }, { "Secondary3", "Ph\u1EE5 3" },

        //
        { "ControlFontInfo", "Control Font" }, { "ControlFont", "Label Font" }, { "SystemFontInfo", "Tool Tip, Tree nodes" }, { "SystemFont", "System Font" }, { "UserFontInfo", "User Entered Data" }, { "UserFont", "Field Font" },

//      { "SmallFontInfo",          "Reports" },
        { "SmallFont", "Small Font" }, { "WindowTitleFont", "Title Font" }, { "MenuFont", "Menu Font" },

        //
        { "MandatoryInfo", "N\u1EC1n c\u1EE7a tr\u01B0\u1EDDng b\u1EAFt bu\u1ED9c" }, { "Mandatory", "B\u1EAFt bu\u1ED9c" }, { "ErrorInfo", "N\u1EC1n c\u1EE7a tr\u01B0\u1EDDng b\u1ECB l\u1ED7i" }, { "Error", "L\u1ED7i" }, { "InfoInfo", "N\u1EC1n c\u1EE7a tr\u01B0\u1EDDng thông tin" }, { "Info", "Thông tin" }, { "WhiteInfo", "\u0110\u01B0\u1EDDng th\u1EB3ng" }, { "White", "Tr\u1EAFng" }, { "BlackInfo", "\u0110\u01B0\u1EDDng th\u1EB3ng, \u0110o\u1EA1n v\u0103n" }, { "Black", "Black" }, { "InactiveInfo", "Inactive Field Background" }, { "Inactive", "Inactive" }, { "TextOKInfo", "OK Text Foreground" }, { "TextOK", "Text - OK" }, { "TextIssueInfo", "Error Text Foreground" }, { "TextIssue", "Text - Error" },

        //
        { "FontChooser", "Ch\u1ECDn Font" }, { "Fonts", "Fonts" }, { "Plain", "Plain" }, { "Italic", "Italic" }, { "Bold", "Bold" }, { "BoldItalic", "Bold & Italic" }, { "Name", "Tên" }, { "Size", "Kích th\u01B0\u1EDBc" }, { "Style", "Ki\u1EC3u" }, { "TestString", "\u0110ây ch\u1EC9 là ph\u1EA7n th\u1EED nghi\u1EC7m" }, { "FontString", "Font" },

        //
        { "CompiereColorEditor", "Công c\u1EE5 thay \u0111\u1ED5i màu C\u0103mpia-\u01A1" }, { "CompiereType", "Lo\u1EA1i màu" }, { "GradientUpperColor", "Gradient Upper Color" }, { "GradientLowerColor", "Gradient Lower Color" }, { "GradientStart", "Gradient Start" }, { "GradientDistance", "Gradient Distance" }, { "TextureURL", "Texture URL" }, { "TextureAlpha", "Texture Alpha" }, { "TextureTaintColor", "Texture Taint Color" }, { "LineColor", "Line Color" }, { "LineBackColor", "Background Color" }, { "LineWidth", "Chi\u1EC1u r\u1ED9ng \u0111\u01B0\u1EDDng" }, { "LineDistance", "Chi\u1EC1u xa \u0111\u01B0\u1EDDng" }, { "FlatColor", "Màu ph\u1EB3ng" }
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
 * @(#)PlafRes_vi.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_vi.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
