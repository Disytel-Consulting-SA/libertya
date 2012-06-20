/*
 * @(#)PlafRes_nl.java   12.oct 2007  Versión 2.2
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
 *         *     Eldir Tomassen
 *  @version    $Id: PlafRes_nl.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_nl extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Achtergrond Kleur Type" }, { "BackColType_Flat", "Egaal" }, { "BackColType_Gradient", "Verloop" }, { "BackColType_Lines", "Lijnen" }, { "BackColType_Texture", "Reliï¿½f" },

        //
        { "LookAndFeelEditor", "Look & Feel Editor" }, { "LookAndFeel", "Look & Feel" }, { "Theme", "Thema" }, { "EditCompiereTheme", "OpenXpertya Theme Bewerken" }, { "SetDefault", "Standaard Achtergrond" }, { "SetDefaultColor", "Achtergrond Kleur" }, { "ColorBlind", "Kleur Verloop" }, { "Example", "Voorbeeld" }, { "Reset", "Ongedaan maken" }, { "OK", "OK" }, { "Cancel", "Annuleren" },

        //
        { "CompiereThemeEditor", "OpenXpertya Thema Editor" }, { "MetalColors", "Metaal Kleuren" }, { "CompiereColors", "OpenXpertya Kleuren" }, { "CompiereFonts", "OpenXpertya Lettertypen" }, { "Primary1Info", "Shaduw, Schijdingsteken" }, { "Primary1", "Primair 1" }, { "Primary2Info", "Schijdingslijn, Geselecteerd Menu" }, { "Primary2", "Primair 2" }, { "Primary3Info", "Tabel Geselecteerde Rij, Geselecteerde Tekst, ToolTip Achtergrond" }, { "Primary3", "Primair 3" }, { "Secondary1Info", "Begrenzing Lijnen" }, { "Secondary1", "Secundair 2" }, { "Secondary2Info", "Inactieve Tabs, Geselecteerde Velden, Inactieve Begrenzing + Tekst" }, { "Secondary2", "Secundait 2" }, { "Secondary3Info", "Achtergrond" }, { "Secondary3", "Secondair 3" },

        //
        { "ControlFontInfo", "Beheer Lettertype" }, { "ControlFont", "Label Lettertype" }, { "SystemFontInfo", "Tool Tip, Boom Iconen" }, { "SystemFont", "Systeem Letterype" }, { "UserFontInfo", "Door Gebruiker ingevoerde gegevens" }, { "UserFont", "Veld Lettertype" },

//      { "SmallFontInfo",          "Reports" },
        { "SmallFont", "Klein Lettertype" }, { "WindowTitleFont", "Titel Lettertype" }, { "MenuFont", "Menu Lettertype" },

        //
        { "MandatoryInfo", "Verplicht Veld Achtergrond" }, { "Mandatory", "Verplicht" }, { "ErrorInfo", "Foutief Veld Achtergrond" }, { "Error", "Foutief" }, { "InfoInfo", "Informatie Veld Achtergrond" }, { "Info", "Informatie" }, { "WhiteInfo", "Lijnen" }, { "White", "Wit" }, { "BlackInfo", "Lijnen, Tekst" }, { "Black", "Zwart" }, { "InactiveInfo", "Inactief Veld Achtergrond" }, { "Inactive", "Inactief" }, { "TextOKInfo", "OK Tekst Voorgrond" }, { "TextOK", "Tekst - OK" }, { "TextIssueInfo", "Foutief Tekst Voorgrond" }, { "TextIssue", "Tekst - Foutief" },

        //
        { "FontChooser", "Lettertype Selecteren" }, { "Fonts", "Lettertypen" }, { "Plain", "Normaal" }, { "Italic", "Schuin" }, { "Bold", "Vet" }, { "BoldItalic", "Vet & Schuin" }, { "Name", "Naam" }, { "Size", "Formaat" }, { "Style", "Stijl" }, { "TestString", "Dit is een test! De thema brwoser is bezig. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "Lettertype" },

        //
        { "CompiereColorEditor", "OpenXpertya Kleur Editor" }, { "CompiereType", "Kleur Type" }, { "GradientUpperColor", "Verloop Bovenste Kleur" }, { "GradientLowerColor", "Verloop Onderste Kleur" }, { "GradientStart", "Verloop Start" }, { "GradientDistance", "Verloop Afstand" }, { "TextureURL", "Textuur URL" }, { "TextureAlpha", "Textuur Alpha" }, { "TextureTaintColor", "Textuur Taint Kleur" }, { "LineColor", "Lijn Kleur" }, { "LineBackColor", "Achtergrond Kleur" }, { "LineWidth", "Lijn Breedte" }, { "LineDistance", "Lijn Reikwijdte" }, { "FlatColor", "Egale Kleur" }
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
 * @(#)PlafRes_nl.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_nl.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
