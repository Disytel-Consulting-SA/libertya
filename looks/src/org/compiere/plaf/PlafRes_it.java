/*
 * @(#)PlafRes_it.java   12.oct 2007  Versión 2.2
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
 *         *     Gabriele Vivinetto - gabriele.mailing@rvmgroup.it
 *  @version    $Id: PlafRes_it.java,v 1.5 2005/03/11 20:34:37 jjanke Exp $
 */
public class PlafRes_it extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        // { "BackColType",            "Background Color Type" },
        { "BackColType", "Tipo Colore di Sfondo" },

        // { "BackColType_Flat",       "Flat" },
        { "BackColType_Flat", "Piatto" },

        // { "BackColType_Gradient",   "Gradient" },
        { "BackColType_Gradient", "Gradiente" },

        // { "BackColType_Lines",      "Lines" },
        { "BackColType_Lines", "Linee" },

        // { "BackColType_Texture",    "Texture" }, //Need to be checked. How to translate "Texture" ?
        { "BackColType_Texture", "Texture" },

        //
        // { "LookAndFeelEditor",      "Look & Feel Editor" }, //Need to be checked
        { "LookAndFeelEditor", "Editor aspetto" },

        // { "LookAndFeel",            "Look & Feel" },
        { "LookAndFeel", "Aspetto" },

        // { "Theme",                  "Theme" },
        { "Theme", "Tema" },

        // { "EditCompiereTheme",      "Edit OpenXpertya Theme" }, //Need to be checked
        { "EditCompiereTheme", "Modifica Tema di OpenXpertya" },

        // { "SetDefault",             "Default Background" },
        { "SetDefault", "Sfondo di Default" },

        // { "SetDefaultColor",        "Background Color" },
        { "SetDefaultColor", "Colore di Sfondo" },

        // { "ColorBlind",             "Color Deficiency" }, //Need to be checked
        { "ColorBlind", "Mancanza di Colore" },

        // { "Example",                "Example" },
        { "Example", "Esempio" },

        // { "Reset",                  "Reset" }, //Need to be checked
        { "Reset", "Resetta" },

        // { "OK",                     "OK" },
        { "OK", "OK" },

        // { "Cancel",                 "Cancel" },
        { "Cancel", "Cancella" },

        //
        // { "CompiereThemeEditor",    "OpenXpertya Theme Editor" }, //Need to be checked
        { "CompiereThemeEditor", "Editor di Tema OpenXpertya" },

        // { "MetalColors",            "Metal Colors" },
        { "MetalColors", "Colori Metallici" },

        // { "CompiereColors",         "OpenXpertya Colors" },
        { "CompiereColors", "Colori di OpenXpertya" },

        // { "CompiereFonts",          "OpenXpertya Fonts" }, //Need to be checked. Mantain the word "Font" ?
        { "CompiereFonts", "Caratteri di OpenXpertya" },

        // { "Primary1Info",           "Shadow, Separator" }, //Need to be checked
        { "Primary1Info", "Ombra, Separatore" },

        // { "Primary1",               "Primary 1" },
        { "Primary1", "Primario 1" },

        // { "Primary2Info",           "Focus Line, Selected Menu" }, //Need to be checked
        { "Primary2Info", "Linea di Selezione, Menu Selezionato" },

        // { "Primary2",               "Primary 2" },
        { "Primary2", "Primario 2" },

        // { "Primary3Info",           "Table Selected Row, Selected Text, ToolTip Background" }, //Need to be checked
        { "Primary3Info", "Riga di Tabella Selezionata, Testo Selezionato, Sfondo ToolTip " },

        // { "Primary3",               "Primary 3" },
        { "Primary3", "Primario 3" },

        // { "Secondary1Info",         "Border Lines" },
        { "Secondary1Info", "Linee di Bordo" },

        // { "Secondary1",             "Secondary 1" },
        { "Secondary1", "Secondario 1" },

        // { "Secondary2Info",         "Inactive Tabs, Pressed Fields, Inactive Border + Text" }, //Need to be checked
        { "Secondary2Info", "Tab inattive, Campi selezionati, Bordo + Testo inattivo" },

        // { "Secondary2",             "Secondary 2" },
        { "Secondary2", "Secondario 2" },

        // { "Secondary3Info",         "Background" },
        { "Secondary3Info", "Sfondo" },

        // { "Secondary3",             "Secondary 3" },
        { "Secondary3", "Secondario 3" },

        //
        // { "ControlFontInfo",        "Control Font" }, //Need to be checked
        { "ControlFontInfo", "Font di Controllo" },

        // { "ControlFont",            "Label Font" }, //Need to be checked
        { "ControlFont", "Font di Etichetta" },

        // { "SystemFontInfo",         "Tool Tip, Tree nodes" }, //Need to be checked
        { "SystemFontInfo", "Tool Tip, Nodi ad Albero" },

        // { "SystemFont",             "System Font" }, //Need to be checked
        { "SystemFont", "Carattere di Sistema" },

        // { "UserFontInfo",           "User Entered Data" },
        { "UserFontInfo", "Dati immessi dall'utente" },

        // { "UserFont",               "Field Font" }, //Need to be checked
        { "UserFont", "Carattere del Campo" },

//      { "SmallFontInfo",          "Reports" },
        // { "SmallFont",              "Small Font" }, //Need to be checked
        { "SmallFont", "Carattere piccolo" },

        // { "WindowTitleFont",         "Title Font" }, //Need to be checked
        { "WindowTitleFont", "Carattere Titolo" },

        // { "MenuFont",               "Menu Font" }, //Need to be checked
        { "MenuFont", "Carattere Menu" },

        //
        // { "MandatoryInfo",          "Mandatory Field Background" },
        { "MandatoryInfo", "Sfondo Campo Obbligatorio" },

        // { "Mandatory",              "Mandatory" },
        { "Mandatory", "Obbligatorio" },

        // { "ErrorInfo",              "Error Field Background" },
        { "ErrorInfo", "Sfondo Campo di Errore" },

        // { "Error",                  "Error" },
        { "Error", "Errore" },

        // { "InfoInfo",               "Info Field Background" }, //Need to be checked. Is it better "Informativo" ? What with the following ?
        { "InfoInfo", "Sfondo Campo Informazione" },

        // { "Info",                   "Info" },
        { "Info", "Informazione" },

        // { "WhiteInfo",              "Lines" },
        { "WhiteInfo", "Linee" },

        // { "White",                  "White" },
        { "White", "Bianco" },

        // { "BlackInfo",              "Lines, Text" },
        { "BlackInfo", "Linee, Testo" },

        // { "Black",                  "Black" },
        { "Black", "Nero" },

        // { "InactiveInfo",           "Inactive Field Background" },
        { "InactiveInfo", "Sfondo Campo Inattivo" },

        // { "Inactive",               "Inactive" },
        { "Inactive", "Inattivo" },

        // { "TextOKInfo",             "OK Text Foreground" }, //Need to be checked. How to translate Foreground ?
        { "TextOKInfo", "Colore Testo OK" },

        // { "TextOK",                 "Text - OK" }, //Need to be checked
        { "TextOK", "Testo - OK" },

        // { "TextIssueInfo",          "Error Text Foreground" }, //Need to be checked
        { "TextIssueInfo", "Colore Testo di Errore" },

        // { "TextIssue",              "Text - Error" },
        { "TextIssue", "Testo - Error" },

        //
        // { "FontChooser",            "Font Chooser" }, //Need to be checked
        { "FontChooser", "Selezionatore Carattere" },

        // { "Fonts",                  "Fonts" }, //Need to be checked
        { "Fonts", "Caratteri" },

        // { "Plain",                  "Plain" }, //Need to be checked
        { "Plain", "Normale" },

        // { "Italic",                 "Italic" },
        { "Italic", "Corsivo" },

        // { "Bold",                   "Bold" },
        { "Bold", "Grassetto" },

        // { "BoldItalic",             "Bold & Italic" },
        { "BoldItalic", "Grassetto & Corsivo" },

        // { "Name",                   "Name" },
        { "Name", "Nome" },

        // { "Size",                   "Size" },
        { "Size", "Dimensione" },

        // { "Style",                  "Style" },
        { "Style", "Stile" },

        // { "TestString",             "This is just a Test! The quick brown Fox is doing something. 12,3456.78 LetterLOne = l1 LetterOZero = O0" },
        { "TestString", "Questo è solo un Test! La veloce volpe marrone stà facendo qualcosa. 12,3456.78 LetteraLUno = l1 LetteraOZero = O0" },

        // { "FontString",             "Font" },
        { "FontString", "Carattere" },		// Need to be checked

        //
        // { "CompiereColorEditor",    "OpenXpertya Color Editor" }, //Need to be checked
        { "CompiereColorEditor", "Editor di Colori OpenXpertya" },

        // { "CompiereType",           "Color Type" },
        { "CompiereType", "Tipo Colore" },

        // { "GradientUpperColor",     "Gradient Upper Color" },
        { "GradientUpperColor", "Colore Gradiente Superiore" },

        // { "GradientLowerColor",     "Gradient Lower Color" },
        { "GradientLowerColor", "Colore Gradiente Inferiore" },

        // { "GradientStart",          "Gradient Start" },
        { "GradientStart", "Inizio Gradiente" },

        // { "GradientDistance",       "Gradient Distance" },
        { "GradientDistance", "Distanza Gradiente" },

        // { "TextureURL",             "Texture URL" }, //Need to be checked. How to translate "Texture" ?
        { "TextureURL", "URL Texture" },

        // { "TextureAlpha",           "Texture Alpha" }, //Need to be checked. How to translate ?
        { "TextureAlpha", "Texture Alpha" },

        // { "TextureTaintColor",      "Texture Taint Color" }, //Need to be checked. How to translate ?
        { "TextureTaintColor", "Texture Taint Color" },

        // { "LineColor",              "Line Color" },
        { "LineColor", "Colore Linea" },

        // { "LineBackColor",          "Background Color" },
        { "LineBackColor", "Colore Sfondo" },

        // { "LineWidth",              "Line Width" },
        { "LineWidth", "Spessore Linea" },

        // { "LineDistance",           "Line Distance" },
        { "LineDistance", "Distanza Linea" },

        // { "FlatColor",              "Flat Color" }
        { "FlatColor", "Colore Piatto" }
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
 * @(#)PlafRes_it.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_it.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
