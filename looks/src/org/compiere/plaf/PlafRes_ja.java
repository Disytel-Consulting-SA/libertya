/*
 * @(#)PlafRes_ja.java   12.oct 2007  Versión 2.2
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
 *         *     Stefan Christians
 *  @version    $Id: PlafRes_ja.java,v 1.3 2005/03/11 20:34:37 jjanke Exp $
 */
public class PlafRes_ja extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "\u80cc\u666f\u306e\u30b9\u30bf\u30a4\u30eb" }, { "BackColType_Flat", "\u5358\u8272" }, { "BackColType_Gradient", "\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3" }, { "BackColType_Lines", "\u30e9\u30a4\u30f3" }, { "BackColType_Texture", "\u30c6\u30ad\u30b9\u30c1\u30e3\u30fc" },

        //
        { "LookAndFeelEditor", "\u5916\u898b\u30fb\u30a8\u30c7\u30a3\u30bf" }, { "LookAndFeel", "\u5916\u898b" }, { "Theme", "\u30c6\u30fc\u30de" }, { "EditCompiereTheme", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30c6\u30fc\u30de\u30fb\u30a8\u30c7\u30a3\u30bf" }, { "SetDefault", "\u30c7\u30d5\u30a9\u30eb\u30c8\u306e\u80cc\u666f" }, { "SetDefaultColor", "\u80cc\u666f\u306e\u8272" }, { "ColorBlind", "\u8272\u76f2\u76ee" }, { "Example", "\u4f8b" }, { "Reset", "\u30ea\u30bb\u30c3\u30c8" }, { "OK", "OK" }, { "Cancel", "\u30ad\u30e3\u30f3\u30bb\u30eb" },

        //
        { "CompiereThemeEditor", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u30fb\u30c6\u30fc\u30de\u30fb\u30a8\u30c7\u30a3\u30bf" }, { "MetalColors", "\u30e1\u30bf\u30eb\u8272" }, { "CompiereColors", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u8272" }, { "CompiereFonts", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30d5\u30a9\u30f3\u30c8" }, { "Primary1Info", "\u5f71\u3001\u30bb\u30d1\u30ec\u30fc\u30bf" }, { "Primary1", "\u4e00\u756a\u76ee\u306e1" }, { "Primary2Info", "\u30d5\u30a9\u30fc\u30ab\u30b9\u304c\u6709\u308b\u30e9\u30a4\u30f3\u3001\u9078\u629e\u3057\u305f\u30e1\u30cb\u30e5\u30fc" }, { "Primary2", "\u4e00\u756a\u76ee\u306e2" }, { "Primary3Info", "\u9078\u629e\u3057\u305f\u30c6\u30fc\u30d6\u30eb\u306e\u884c\u3001\u9078\u629e\u3057\u305f\u30c6\u30ad\u30b9\u30c8\u3001\u30c4\u30fc\u30eb\u30fb\u30c1\u30c3\u30d7\u306e\u80cc\u666f" }, { "Primary3", "\u4e00\u756a\u76ee\u306e3" }, { "Secondary1Info", "\u5883\u754c\u7dda" }, { "Secondary1", "\u4e8c\u756a\u76ee\u306e1" }, { "Secondary2Info", "\u4e0d\u6d3b\u6027\u30bf\u30d6\u3001\u62bc\u4e0b\u3057\u305f\u30d5\u30a3\u30fc\u30eb\u30c9\u3001\u4e0d\u6d3b\u6027\u5883\u754c\u3068\u30c6\u30ad\u30b9\u30c8" }, { "Secondary2", "\u4e8c\u756a\u76ee\u306e2" }, { "Secondary3Info", "\u80cc\u666f" }, { "Secondary3", "\u4e8c\u756a\u76ee\u306e3" },

        //
        { "ControlFontInfo", "\u30b3\u30f3\u30c8\u30ed\u30fc\u30eb\u306e\u30d5\u30a9\u30f3\u30c8" }, { "ControlFont", "\u30e9\u30d9\u30eb\u306e\u30d5\u30a9\u30f3\u30c8" }, { "SystemFontInfo", "\u30c4\u30fc\u30eb\u30fb\u30c1\u30c3\u30d7\u3001\u30c4\u30ea\u30fc" }, { "SystemFont", "\u30b7\u30b9\u30c6\u30e0\u30d5\u30a9\u30f3\u30c8" }, { "UserFontInfo", "\u30e6\u30fc\u30b6\u304c\u5165\u529b\u3057\u305f\u30c7\u30fc\u30bf" }, { "UserFont", "\u30d5\u30a3\u30fc\u30eb\u30c9\u30d5\u30a9\u30f3\u30c8" },

//      { "SmallFontInfo",          "\u30ec\u30dd\u30fc\u30c8" },
        { "SmallFont", "\u5c0f\u30d5\u30a9\u30f3\u30c8" }, { "WindowTitleFont", "\u30bf\u30a4\u30c8\u30eb\u30d5\u30a9\u30f3\u30c8" }, { "MenuFont", "\u30e1\u30cb\u30e5\u30fc\u30d5\u30a9\u30f3\u30c8" },

        //
        { "MandatoryInfo", "\u5fc5\u9808\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u80cc\u666f" }, { "Mandatory", "\u5fc5\u9808" }, { "ErrorInfo", "\u30a8\u30e9\u30fc\u30fb\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u80cc\u666f" }, { "Error", "\u30a8\u30e9\u30fc" }, { "InfoInfo", "\u60c5\u5831\u30fb\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u80cc\u666f" }, { "Info", "\u60c5\u5831" }, { "WhiteInfo", "\u30e9\u30a4\u30f3" }, { "White", "\u767d" }, { "BlackInfo", "\u30e9\u30a4\u30f3\u3068\u30c6\u30ad\u30b9\u30c8" }, { "Black", "\u9ed2" }, { "InactiveInfo", "\u4e0d\u6d3b\u6027\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u80cc\u666f" }, { "Inactive", "\u4e0d\u6d3b\u6027" }, { "TextOKInfo", "OK\u30fb\u30c6\u30ad\u30b9\u30c8\u306e\u524d\u666f" }, { "TextOK", "\u30c6\u30ad\u30b9\u30c8 - OK" }, { "TextIssueInfo", "\u30a8\u30e9\u30fc\u30fb\u30c6\u30ad\u30b9\u30c8\u306e\u524d\u666f" }, { "TextIssue", "\u30c6\u30ad\u30b9\u30c8 - \u30a8\u30e9\u30fc" },

        //
        { "FontChooser", "\u30d5\u30a9\u30f3\u30c8\u306e\u9078\u629e" }, { "Fonts", "\u30d5\u30a9\u30f3\u30c8" }, { "Plain", "\u666e\u901a" }, { "Italic", "\u659c\u4f53" }, { "Bold", "\u592a\u5b57" }, { "BoldItalic", "\u592a\u5b57\u3068\u659c\u4f53" }, { "Name", "\u30d5\u30a1\u30df\u30ea" }, { "Size", "\u30b5\u30a4\u30ba" }, { "Style", "\u30b9\u30bf\u30a4\u30eb" }, { "TestString", "This is just a Test! The quick brown Fox is doing something. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "\u30d5\u30a9\u30f3\u30c8" },

        //
        { "CompiereColorEditor", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u8272\u30fb\u30a8\u30c7\u30a3\u30bf" }, { "CompiereType", "\u8272\u578b" }, { "GradientUpperColor", "\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u306e\u4e0a\u306e\u8272" }, { "GradientLowerColor", "\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u306e\u4e0b\u306e\u8272" }, { "GradientStart", "\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u306e" }, { "GradientDistance", "\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u306e\u8ddd\u96e2" }, { "TextureURL", "\u30c6\u30ad\u30b9\u30c1\u30e3\u30fc\u306eURL" }, { "TextureAlpha", "\u30c6\u30ad\u30b9\u30c1\u30e3\u30fc\u306e\u03b1\u5024" }, { "TextureTaintColor", "\u30c6\u30ad\u30b9\u30c1\u30e3\u30fc\u306e\u6c5a\u308c\u8272" }, { "LineColor", "\u30e9\u30a4\u30f3\u306e\u8272" }, { "LineBackColor", "\u30e9\u30a4\u30f3\u306e\u80cc\u666f\u8272" }, { "LineWidth", "\u30e9\u30a4\u30f3\u306e\u5e45" }, { "LineDistance", "\u30e9\u30a4\u30f3\u306e\u8ddd\u96e2" }, { "FlatColor", "\u5358\u8272" }
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
 * @(#)PlafRes_ja.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_ja.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
