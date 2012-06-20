/*
 * @(#)PlafRes_sv.java   12.oct 2007  Versión 2.2
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
 *  Swedish Translation Texts for Look & Feel
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Thomas Dilts
 *  @version    $Id: PlafRes_sv.java,v 1.5 2005/03/11 20:34:37 jjanke Exp $
 */
public class PlafRes_sv extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Bakgrundsfärgtyp" }, { "BackColType_Flat", "Platt" }, { "BackColType_Gradient", "Toning" }, { "BackColType_Lines", "Linjer" }, { "BackColType_Texture", "Struktur" },

        //
        { "LookAndFeelEditor", "Utseenderedigeringsprogram" }, { "LookAndFeel", "Utseendet" }, { "Theme", "Tema" }, { "EditCompiereTheme", "Redigera compiere tema" }, { "SetDefault", "Standardbakgrund" }, { "SetDefaultColor", "Bakgrundsfärg" }, { "ColorBlind", "Bristfälligfärg" }, { "Example", "Exempel" }, { "Reset", "Återställa" }, { "OK", "OK" }, { "Cancel", "Avbryt" },

        //
        { "CompiereThemeEditor", "OpenXpertya temaredigeringsprogram" }, { "MetalColors", "Metal färg" }, { "CompiereColors", "OpenXpertya färger" }, { "CompiereFonts", "OpenXpertya teckensnitt" }, { "Primary1Info", "Skugga, avskiljare" }, { "Primary1", "Primär 1" }, { "Primary2Info", "Fokus linje, vald meny" }, { "Primary2", "Primär 2" }, { "Primary3Info", "Tabel vald rad, vald text, knappbeskrivning bakgrund" }, { "Primary3", "Primär 3" }, { "Secondary1Info", "Ram linjer" }, { "Secondary1", "Sekundär 1" }, { "Secondary2Info", "Inactiv tabbar, nedtrycktfält, inactive ram + text" }, { "Secondary2", "Sekundär 2" }, { "Secondary3Info", "Bakgrund" }, { "Secondary3", "Sekundär 3" },

        //
        { "ControlFontInfo", "Kontrolteckensnitt" }, { "ControlFont", "Textetiketter teckensnitt" }, { "SystemFontInfo", "Knappbeskrivning, Träd gren" }, { "SystemFont", "System teckensnitt" }, { "UserFontInfo", "Användare angiven data" }, { "UserFont", "Fält teckensnitt" },

//      { "SmallFontInfo",          "Reports" },
        { "SmallFont", "Liten teckensnitt" }, { "WindowTitleFont", "Rubrik teckensnitt" }, { "MenuFont", "Meny teckensnitt" },

        //
        { "MandatoryInfo", "Obligatoriskt fält bakgrund" }, { "Mandatory", "Obligatorisk" }, { "ErrorInfo", "Fel fält bakgrund" }, { "Error", "Fel" }, { "InfoInfo", "Information fält bakgrund" }, { "Info", "Information" }, { "WhiteInfo", "Linjer" }, { "White", "Vit" }, { "BlackInfo", "Linjer, text" }, { "Black", "Svart" }, { "InactiveInfo", "Inactiv fält bakgrund" }, { "Inactive", "Inactiv" }, { "TextOKInfo", "OK text förgrund" }, { "TextOK", "Text - OK" }, { "TextIssueInfo", "Fel text förgrund" }, { "TextIssue", "Text - Fel" },

        //
        { "FontChooser", "Teckensnittväljare" }, { "Fonts", "Teckensnitt" }, { "Plain", "Oformaterad" }, { "Italic", "Kursiv" }, { "Bold", "Fet" }, { "BoldItalic", "Fet & kursiv" }, { "Name", "Namn" }, { "Size", "Storlek" }, { "Style", "Stil" }, { "TestString", "Denna är en test! ABCDEFG abcdefg ÄäÅåÖö. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "Teckensnitt" },

        //
        { "CompiereColorEditor", "OpenXpertya färgredigeringsprogram" }, { "CompiereType", "Färgtyp" }, { "GradientUpperColor", "Toning överfärg" }, { "GradientLowerColor", "Toning underfärg" }, { "GradientStart", "Toning start" }, { "GradientDistance", "Toning längd" }, { "TextureURL", "Struktur URL" }, { "TextureAlpha", "Struktur Alpha" }, { "TextureTaintColor", "Struktur fläckfärg" }, { "LineColor", "Linje färg" }, { "LineBackColor", "Bakgrundsfärg" }, { "LineWidth", "Linje bred" }, { "LineDistance", "Linje längd" }, { "FlatColor", "Mattfärg" }
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
 * @(#)PlafRes_sv.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_sv.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
