/*
 * @(#)PlafRes_pl.java   12.oct 2007  Versión 2.2
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
 *         *     Adam Bodurka
 *  @version    $Id: PlafRes_pl.java,v 1.7 2005/03/11 20:34:37 jjanke Exp $
 */
public class PlafRes_pl extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Typ koloru t\u0142a" }, { "BackColType_Flat", "P\u0142aski" }, { "BackColType_Gradient", "Stopniowany" }, { "BackColType_Lines", "Linie" }, { "BackColType_Texture", "Tekstura" },

        //
        { "LookAndFeelEditor", "Edytor Wygl\u0105du" }, { "LookAndFeel", "Wygl\u0105d" }, { "Theme", "Temat" }, { "EditCompiereTheme", "Edytuj Temat Compiera" }, { "SetDefault", "Domy\u015blne T\u0142o" }, { "SetDefaultColor", "Domy\u015blny Kolor" }, { "Example", "Przyk\u0142ad" }, { "Reset", "Resetuj" }, { "OK", "OK" }, { "Cancel", "Anuluj" },

        //
        { "CompiereThemeEditor", "Edytor Tematu Compiera" }, { "MetalColors", "Kolory Metalowe" }, { "CompiereColors", "Kolory Compiera" }, { "CompiereFonts", "Czcionki Compiera" }, { "Primary1Info", "Tekst Etykiety" }, { "Primary1", "Podstawowy 1" }, { "Primary2Info", "Linia Fokusu, Wybrany CheckBox" }, { "Primary2", "Podstawowy 2" }, { "Primary3Info", "Wybrany Wiersz Tabeli, Wybrany Tekst, T\u0142o Podpowiedzi" }, { "Primary3", "Podstawowy 3" }, { "Secondary1Info", "Linie Obramowania" }, { "Secondary1", "Drugorz\u0119dny 1" }, { "Secondary2Info", "Nieaktywne Zak\u0142adki, Naci\u015bni\u0119te Pola, Nieaktywna Ramka + Tekst" }, { "Secondary2", "Drugorz\u0119dny 2" }, { "Secondary3Info", "T\u0142o" }, { "Secondary3", "Drugorz\u0119dny 3" },

        //
        { "ControlFontInfo", "Czcionka Kontrolki" }, { "ControlFont", "Czcionka Etykiety" }, { "SystemFontInfo", "Podpowiedzi, Ga\u0142\u0119zie drzewa" }, { "SystemFont", "Czcionka Systemowa" }, { "UserFontInfo", "Dane wprowadzone przez U\u017cytkownika" }, { "UserFont", "Czcionka Pola" },

//      { "SmallFontInfo",          "Raporty" },
        { "SmallFont", "Ma\u0142a Czcionka" }, { "WindowTitleFont", "Czcionka Tytu\u0142u" }, { "MenuFont", "Czcionka Menu" },

        //
        { "MandatoryInfo", "Obowi\u0105zkowe T\u0142o Pola" }, { "Mandatory", "Obowi\u0105zkowe" }, { "ErrorInfo", "B\u0142\u0105d T\u0142a Pola" }, { "Error", "B\u0142\u0105d" }, { "InfoInfo", "Informacja T\u0142a Pola" }, { "Info", "Informacja" }, { "WhiteInfo", "Linie" }, { "White", "Bia\u0142y" }, { "BlackInfo", "Linie, Tekst" }, { "Black", "Czarny" }, { "InactiveInfo", "Nieaktywne T\u0142o Pola" }, { "Inactive", "Nieaktywny" }, { "TextOKInfo", "OK Pierwszoplanowy Tekst" }, { "TextOK", "Tekst - OK" }, { "TextIssueInfo", "B\u0142\u0105d Pierwszoplanowego Tekstu" }, { "TextIssue", "Tekst - B\u0142\u0105d" },

        //
        { "FontChooser", "Wyb\u00f3r Czcionki" }, { "Fonts", "Czcionki" }, { "Plain", "G\u0142adki" }, { "Italic", "Kursywa" }, { "Bold", "Pogrubiony" }, { "BoldItalic", "Pogrubiony i Kursywa" }, { "Name", "Nazwa" }, { "Size", "Rozmiar" }, { "Style", "Styl" }, { "TestString", "To jest tylko test!" }, { "FontString", "Czcionka" },

        //
        { "CompiereColorEditor", "Edytor Koloru Compiera" }, { "CompiereType", "Typ Koloru" }, { "GradientUpperColor", "G\u00f3rny Kolor Stopniowania" }, { "GradientLowerColor", "Dolny Kolor Stopniowania" }, { "GradientStart", "Pocz\u0105tek Stopniowania" }, { "GradientDistance", "Odst\u0119p Stopniowania" }, { "TextureURL", "Tekstura URL" }, { "TextureAlpha", "Tekstura Alpha" }, { "TextureTaintColor", "Kolor t\u0142a Tekstury" }, { "LineColor", "Kolor Linii" }, { "LineBackColor", "Kolor T\u0142a" }, { "LineWidth", "Grubo\u015b\u0107 Linii" }, { "LineDistance", "Odst\u0119p Linii" }, { "FlatColor", "Kolor P\u0142aski" }
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
 * @(#)PlafRes_pl.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_pl.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
