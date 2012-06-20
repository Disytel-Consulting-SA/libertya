/*
 * @(#)PlafRes_dk.java   12.oct 2007  Versión 2.2
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
 *         *     Jorg Janke
 *  @version    $Id: PlafRes_dk.java,v 1.5 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_dk extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Baggrund: Farvetype" }, { "BackColType_Flat", "Fast" }, { "BackColType_Gradient", "Farveforløb" }, { "BackColType_Lines", "Linjer" }, { "BackColType_Texture", "Struktur" },

        //
        { "LookAndFeelEditor", "Redigér & udseende" }, { "LookAndFeel", "Udseende" }, { "Theme", "Tema" }, { "EditCompiereTheme", "Redigér OpenXpertya tema" }, { "SetDefault", "Baggrund: Standard" }, { "SetDefaultColor", "Baggrundsfarve" }, { "ColorBlind", "Farvereduktion" }, { "Example", "Eksempel" }, { "Reset", "Gendan" }, { "OK", "OK" }, { "Cancel", "Annullér" },

        //
        { "CompiereThemeEditor", "OpenXpertya-tema: Redigér" }, { "MetalColors", "Metal-farver" }, { "CompiereColors", "OpenXpertya-farver" }, { "CompiereFonts", "OpenXpertya-skrifttyper" }, { "Primary1Info", "Skygge, Separator" }, { "Primary1", "Primær 1" }, { "Primary2Info", "Markeret element, Markeret menu" }, { "Primary2", "Primær 2" }, { "Primary3Info", "Markeret række i tabel, Markeret tekst, Værktøjstip - baggr." }, { "Primary3", "Primær 3" }, { "Secondary1Info", "Rammelinjer" }, { "Secondary1", "Sekundær 1" }, { "Secondary2Info", "Ikke-aktive faner, Markerede felter, Ikke-aktiv ramme + tekst" }, { "Secondary2", "Sekundær 2" }, { "Secondary3Info", "Baggrund" }, { "Secondary3", "Sekundær 3" },

        //
        { "ControlFontInfo", "Skrifttype: Knapper" }, { "ControlFont", "Skrifttype: Etiket" }, { "SystemFontInfo", "Værktøjstip, Strukturknuder" }, { "SystemFont", "Skrifttype: System" }, { "UserFontInfo", "Anvend" }, { "UserFont", "Skrifttype: Felt" },

//      { "SmallFontInfo",          "Rapporter" },
        { "SmallFont", "Lille" }, { "WindowTitleFont", "Skrifttype: Titellinje" }, { "MenuFont", "Skrifttype: Menu" },

        //
        { "MandatoryInfo", "Tvungen feltbaggrund" }, { "Mandatory", "Tvungen" }, { "ErrorInfo", "Fejl: Feltbaggrund" }, { "Error", "Fejl" }, { "InfoInfo", "Info: Feltbaggrund" }, { "Info", "Info" }, { "WhiteInfo", "Linjer" }, { "White", "Hvid" }, { "BlackInfo", "Linjer, Tekst" }, { "Black", "Sort" }, { "InactiveInfo", "Inaktiv feltbaggrund" }, { "Inactive", "Inaktiv" }, { "TextOKInfo", "OK: Tekstforgrund" }, { "TextOK", "Tekst: OK" }, { "TextIssueInfo", "Fejl: Tekstforgrund" }, { "TextIssue", "Tekst: Fejl" },

        //
        { "FontChooser", "Skriftype" }, { "Fonts", "Skrifttyper" }, { "Plain", "Normal" }, { "Italic", "Kursiv" }, { "Bold", "Fed" }, { "BoldItalic", "Fed & kursiv" }, { "Name", "Navn" }, { "Size", "Størrelse" }, { "Style", "Type" }, { "TestString", "Dette er en prøve! 12.3456,78 BogstavLEn = l1 BogstavONul = O0" }, { "FontString", "Skrifttype" },

        //
        { "CompiereColorEditor", "OpenXpertya-farveeditor" }, { "CompiereType", "Farvetype" }, { "GradientUpperColor", "Farveforløb: Farve 1" }, { "GradientLowerColor", "Farveforløb: Farve 2" }, { "GradientStart", "Farveforløb: Start" }, { "GradientDistance", "Farveforløb: Afstand" }, { "TextureURL", "Struktur: URL" }, { "TextureAlpha", "Struktur: Alpha" }, { "TextureTaintColor", "Struktur: Pletvis" }, { "LineColor", "Linjefarve" }, { "LineBackColor", "Baggrundsfarve" }, { "LineWidth", "Linjebredde" }, { "LineDistance", "Linjeafstand" }, { "FlatColor", "Fast farve" }
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
 * @(#)PlafRes_dk.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_dk.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
