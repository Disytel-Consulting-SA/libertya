/*
 * @(#)PlafRes_zh_CN.java   12.oct 2007  Versión 2.2
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
 *         *     ZhaoXing Meng
 *  @version    $Id: PlafRes_zh_CN.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_zh_CN extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "\u80cc\u666f\u989c\u8272" }, { "BackColType_Flat", "\u5355\u8272" }, { "BackColType_Gradient", "\u6e10\u53d8" }, { "BackColType_Lines", "\u6761\u7eb9" }, { "BackColType_Texture", "\u56fe\u6587" },

        //
        { "LookAndFeelEditor", "\u5916\u89c2\u7f16\u8f91\u5668" }, { "LookAndFeel", "\u5916\u89c2" }, { "Theme", "\u5e03\u666f" }, { "EditCompiereTheme", "\u7f16\u8f91 OpenXpertya \u5e03\u666f" }, { "SetDefault", "\u8bbe\u6210\u9884\u5b9a\u80cc\u666f" }, { "SetDefaultColor", "\u80cc\u666f\u989c\u8272" }, { "ColorBlind", "\u8272\u5dee" }, { "Example", "\u8303\u4f8b" }, { "Reset", "\u91cd\u8bbe" }, { "OK", "\u786e\u8ba4" }, { "Cancel", "\u64a4\u6d88" },

        //
        { "CompiereThemeEditor", "OpenXpertya \u5e03\u666f\u7f16\u8f91\u5668" }, { "MetalColors", "\u91d1\u5c5e\u8272\u7cfb" }, { "CompiereColors", "OpenXpertya \u989c\u8272" }, { "CompiereFonts", "OpenXpertya \u5b57\u4f53" }, { "Primary1Info", "\u9634\u5f71, \u5206\u9694" }, { "Primary1", "\u4e3b\u8981\u8272 1" }, { "Primary2Info", "\u7126\u70b9\u7ebf\u6846, \u83dc\u5355\u7126\u70b9" }, { "Primary2", "\u4e3b\u8981\u8272 2" }, { "Primary3Info", "\u9009\u62e9\u7684\u884c, \u9009\u62e9\u7684\u6587\u5b57, \u5de5\u5177\u63d0\u793a\u80cc\u666f" }, { "Primary3", "\u4e3b\u8981\u8272 3" }, { "Secondary1Info", "\u8fb9\u6846" }, { "Secondary1", "\u6b21\u8981\u8272 1" }, { "Secondary2Info", "\u975e\u6d3b\u52a8\u6807\u7b7e, \u6309\u4e0b\u7684\u6309\u94ae\u8fb9\u7f18, \u975e\u6d3b\u52a8\u533a\u8fb9\u6846 + \u6587\u5b57" }, { "Secondary2", "\u6b21\u8981\u8272 2" }, { "Secondary3Info", "\u80cc\u666f" }, { "Secondary3", "\u6b21\u8981\u8272 3" },

        //
        { "ControlFontInfo", "\u63a7\u5236\u4fe1\u606f\u5b57\u4f53" }, { "ControlFont", "\u6807\u7b7e\u5b57\u4f53" }, { "SystemFontInfo", "\u5de5\u5177\u63d0\u793a, \u6811\u72b6\u76ee\u5f55" }, { "SystemFont", "\u7cfb\u7edf\u5b57\u4f53" }, { "UserFontInfo", "\u4f7f\u7528\u8005\u8f93\u5165\u6570\u636e" }, { "UserFont", "\u5b57\u6bb5\u5b57\u4f53" },

//      { "SmallFontInfo",          "\u62a5\u8868" },
        { "SmallFont", "\u5c0f\u5b57\u4f53" }, { "WindowTitleFont", "\u6807\u9898\u5b57\u4f53" }, { "MenuFont", "\u83dc\u5355\u5b57\u4f53" },

        //
        { "MandatoryInfo", "\u9009\u9879\u5b57\u6bb5\u80cc\u666f" }, { "Mandatory", "\u9009\u9879" }, { "ErrorInfo", "\u9519\u8bef\u5b57\u6bb5\u80cc\u666f" }, { "Error", "\u9519\u8bef\u4fe1\u606f" }, { "InfoInfo", "\u4e00\u822c\u4fe1\u606f\u5b57\u6bb5\u80cc\u666f" }, { "Info", "\u4e00\u822c\u4fe1\u606f" }, { "WhiteInfo", "\u7ebf\u6761" }, { "White", "\u767d" }, { "BlackInfo", "\u7ebf\u6761, \u6587\u5b57" }, { "Black", "\u9ed1" }, { "InactiveInfo", "\u975e\u6d3b\u52a8\u5b57\u6bb5\u80cc\u666f" }, { "Inactive", "\u975e\u6d3b\u52a8" }, { "TextOKInfo", "\u786e\u8ba4 - \u524d\u666f\u8272" }, { "TextOK", "\u6587\u5b57 - \u786e\u8ba4" }, { "TextIssueInfo", "\u9519\u8bef\u6587\u5b57\u524d\u666f\u8272" }, { "TextIssue", "\u6587\u5b57 - \u9519\u8bef" },

        //
        { "FontChooser", "\u5b57\u4f53\u9009\u62e9" }, { "Fonts", "\u5b57\u4f53" }, { "Plain", "\u666e\u901a" }, { "Italic", "\u659c\u4f53" }, { "Bold", "\u7c97\u4f53" }, { "BoldItalic", "\u7c97\u659c\u4f53" }, { "Name", "\u540d\u79f0" }, { "Size", "\u5927\u5c0f" }, { "Style", "\u7c7b\u578b" }, { "TestString", "\u8fd9\u662f\u6d4b\u8bd5! The quick brown Fox is doing something. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "\u5b57\u4f53" },

        //
        { "CompiereColorEditor", "OpenXpertya \u989c\u8272\u7f16\u8f91\u5668" }, { "CompiereType", "\u989c\u8272\u79cd\u7c7b" }, { "GradientUpperColor", "\u6e10\u589e\u8272 1" }, { "GradientLowerColor", "\u6e10\u51cf\u8272 2" }, { "GradientStart", "\u6e10\u589e\u65b9\u5411" }, { "GradientDistance", "\u95f4\u8ddd" }, { "TextureURL", "\u56fe\u6848 URL" }, { "TextureAlpha", "\u56fe\u6848\u900f\u660e\u5ea6" }, { "TextureTaintColor", "\u56fe\u6848\u7740\u8272" }, { "LineColor", "\u7ebf\u6761\u989c\u8272" }, { "LineBackColor", "\u80cc\u666f\u989c\u8272" }, { "LineWidth", "\u7ebf\u6761\u5bbd\u5ea6" }, { "LineDistance", "\u7ebf\u6761\u95f4\u8ddd" }, { "FlatColor", "\u5355\u8272" }
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
 * @(#)PlafRes_zh_CN.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_zh_CN.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
