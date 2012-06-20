/*
 * @(#)PlafRes_fi.java   12.oct 2007  Versión 2.2
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
 *  Translation Texts for Look & Feel for Finnish Language
 *
 *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Petteri Soininen (petteri.soininen@netorek.fi)
 *      @version        $Id: PlafRes_fi.java,v 1.5 2005/03/11 20:34:37 jjanke Exp $
 */
public class PlafRes_fi extends ListResourceBundle {

    /**
     * Data
     */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Taustan värityyppi" }, { "BackColType_Flat", "Yksiväri" }, { "BackColType_Gradient", "Gradientti" }, { "BackColType_Lines", "Viiva" }, { "BackColType_Texture", "Kuviointi" },

        //
        { "LookAndFeelEditor", "Käyttötuntuman muokkaus" }, { "LookAndFeel", "Käyttötuntuma" }, { "Theme", "Teema" }, { "EditCompiereTheme", "Muokkaa OpenXpertya-teemaa" }, { "SetDefault", "Oletustaustaväri" }, { "SetDefaultColor", "Taustaväri" }, { "ColorBlind", "Värisokeus" }, { "Example", "Esimerkki" }, { "Reset", "Nollaa" }, { "OK", "Hyväksy" }, { "Cancel", "Peruuta" },

        //
        { "CompiereThemeEditor", "OpenXpertya-teeman muokkaus" }, { "MetalColors", "Metallivärit" }, { "CompiereColors", "OpenXpertya-värit" }, { "CompiereFonts", "OpenXpertya-kirjasimet" }, { "Primary1Info", "Varjostin, Erotin" }, { "Primary1", "Ensisijainen 1" }, { "Primary2Info", "Focus-viiva, Valitty Valikko" }, { "Primary2", "Ensisijainen 2" }, { "Primary3Info", "Taulun Valittu Rivi, Valittu Teksti, ToolTip Tausta" }, { "Primary3", "Ensisijainen 3" }, { "Secondary1Info", "Reunaviivat" }, { "Secondary1", "Toissijainen 1" }, { "Secondary2Info", "Ei-aktiiviset Tabulaattorit, Painetut Kentät, Ei-aktiivinen Reuna + Teksti" }, { "Secondary2", "Toissijainen 2" }, { "Secondary3Info", "Tausta" }, { "Secondary3", "Toissijainen 3" },

        //
        { "ControlFontInfo", "Kontrollikirjasin" }, { "ControlFont", "Nimikekirjasin" }, { "SystemFontInfo", "Tool Tip, Puun Solmut" }, { "SystemFont", "Järjestelmäkirjasin" }, { "UserFontInfo", "Käyttäjän Syöttämä Tieto" }, { "UserFont", "Kenttäkirjasin" },

//      { "SmallFontInfo",          "Raportit" },
        { "SmallFont", "Pieni Kirjasin" }, { "WindowTitleFont", "Otsikkokirjasin" }, { "MenuFont", "Valikkokirjasin" },

        //
        { "MandatoryInfo", "Pakollinen Kenttätausta" }, { "Mandatory", "Pakollinen" }, { "ErrorInfo", "Virhekentän Tausta" }, { "Error", "Virhe" }, { "InfoInfo", "Tietokentän Tausta" }, { "Info", "Tieto" }, { "WhiteInfo", "Viivat" }, { "White", "Valkoinen" }, { "BlackInfo", "Viivat, Teksti" }, { "Black", "Musta" }, { "InactiveInfo", "Ei-aktiivinen Kenttätausta" }, { "Inactive", "Ei-aktiivinen" }, { "TextOKInfo", "Hyväksy Teksti Edusta" }, { "TextOK", "Teksti - Hyväksy" }, { "TextIssueInfo", "Virhetekstin Edusta" }, { "TextIssue", "Teksti - Virhe" },

        //
        { "FontChooser", "Kirjasimen Valitsin" }, { "Fonts", "Kirjasimet" }, { "Plain", "Tavallinen" }, { "Italic", "Kursiivi" }, { "Bold", "Lihavoitu" }, { "BoldItalic", "Lihavoitu ja Kursiivi" }, { "Name", "Nimi" }, { "Size", "Koko" }, { "Style", "Tyyli" }, { "TestString", "Tämä on vain Testi! Nopea ruskea Kettu suorittaa jotain. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "Kirjasin" },

        //
        { "CompiereColorEditor", "OpenXpertya-värimuokkaus" }, { "CompiereType", "Värityyppi" }, { "GradientUpperColor", "Gradientin Ylempi Väri" }, { "GradientLowerColor", "Gradientin Alempi Väri" }, { "GradientStart", "Gradientin Alku" }, { "GradientDistance", "Gradientin Etäisyys" }, { "TextureURL", "Kuvioinnin URL" }, { "TextureAlpha", "Kuvioinnin Alpha" }, { "TextureTaintColor", "Kuvioinnin Korvausväri" }, { "LineColor", "Viivan Väri" }, { "LineBackColor", "Taustan Väri" }, { "LineWidth", "Viivan Paksuus" }, { "LineDistance", "Viivan Etäisyys" }, { "FlatColor", "Tavallinen Väri" }
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
 * @(#)PlafRes_fi.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_fi.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
