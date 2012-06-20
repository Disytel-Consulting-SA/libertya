/*
 * @(#)PlafRes_ca.java   12.oct 2007  Versión 2.2
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
 *         * Jaume Teixi
 *  @version    $Id: PlafRes_ca.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class PlafRes_ca extends ListResourceBundle {

    /** The data */
    static final Object[][]	contents	= new String[][] {

        { "BackColType", "Tipus Color Fons" }, { "BackColType_Flat", "Pla" }, { "BackColType_Gradient", "Gradient" }, { "BackColType_Lines", "L?nes" }, { "BackColType_Texture", "Textura" },

        //
        { "LookAndFeelEditor", "Editor Aparen?a i Comportament" }, { "LookAndFeel", "Aparen?a i Comportament" }, { "Theme", "Tema" }, { "EditCompiereTheme", "Editar Tema OpenXpertya" }, { "SetDefault", "Fons Per Defecte" }, { "SetDefaultColor", "Color Fons" }, { "ColorBlind", "Defici?ncia Color" }, { "Example", "Exemple" }, { "Reset", "Reiniciar" }, { "OK", "D'Acord" }, { "Cancel", "Cancel.lar" },

        //
        { "CompiereThemeEditor", "Editor Tema OpenXpertya" }, { "MetalColors", "Colors Metal" }, { "CompiereColors", "Colors OpenXpertya" }, { "CompiereFonts", "Fonts OpenXpertya" }, { "Primary1Info", "Ombra, Separador" }, { "Primary1", "Primari 1" }, { "Primary2Info", "L?nia Focus, Men? Seleccionat" }, { "Primary2", "Primari 2" }, { "Primary3Info", "Taula Fila Seleccionada, Texte Seleccionat, Indicador Fons" }, { "Primary3", "Primari 3" }, { "Secondary1Info", "L?nies Marc" }, { "Secondary1", "Secondari 1" }, { "Secondary2Info", "Pestanyes Innactives, Camps Premuts, Texte + Marc Innactius" }, { "Secondary2", "Secondari 2" }, { "Secondary3Info", "Fons" }, { "Secondary3", "Secondari 3" },

        //
        { "ControlFontInfo", "Font Control" }, { "ControlFont", "Font Etiqueta" }, { "SystemFontInfo", "Indicador, Nodes Arbre" }, { "SystemFont", "Font Sistema" }, { "UserFontInfo", "Dades Entrades Per l'Usuari" }, { "UserFont", "Font Camp" },

//      { "SmallFontInfo",          "Informes" },
        { "SmallFont", "Font Petita" }, { "WindowTitleFont", "Font T?tol" }, { "MenuFont", "Font Men?" },

        //
        { "MandatoryInfo", "Camp de Fons Obligatori" }, { "Mandatory", "Obligatori" }, { "ErrorInfo", "Camp de Fons Error" }, { "Error", "Error" }, { "InfoInfo", "Camp de Fons Informaci?" }, { "Info", "Informaci?" }, { "WhiteInfo", "L?nies" }, { "White", "Blanc" }, { "BlackInfo", "L?nies, Text" }, { "Black", "Negre" }, { "InactiveInfo", "Camp de Fons Innactiu" }, { "Inactive", "Innactiu" }, { "TextOKInfo", "Texte Superior OK" }, { "TextOK", "Texte - OK" }, { "TextIssueInfo", "Texte Superior Error" }, { "TextIssue", "Texte - Error" },

        //
        { "FontChooser", "Escollidor Font" }, { "Fonts", "Fonts" }, { "Plain", "Plana" }, { "Italic", "It?lica" }, { "Bold", "Negreta" }, { "BoldItalic", "Negreta & It?lica" }, { "Name", "Nom" }, { "Size", "Tamany" }, { "Style", "Estil" }, { "TestString", "Aix? ?s nom?s una Prova! La Guineu marr? r?pida ?st? fent quelcom. 12,3456.78 LetterLOne = l1 LetterOZero = O0" }, { "FontString", "Font" },

        //
        { "CompiereColorEditor", "Editor Color OpenXpertya" }, { "CompiereType", "Tipus Color" }, { "GradientUpperColor", "Color Dalt Degradat" }, { "GradientLowerColor", "Color Baix Degradat" }, { "GradientStart", "Inici Degradat" }, { "GradientDistance", "Dist?ncia Degradat" }, { "TextureURL", "Textura URL" }, { "TextureAlpha", "Textura Alfa" }, { "TextureTaintColor", "Textura Color Corrupci?" }, { "LineColor", "Color L?nia" }, { "LineBackColor", "Color Fons" }, { "LineWidth", "Ampla L?nia" }, { "LineDistance", "Dist?ncia L?nia" }, { "FlatColor", "Color Pla" }
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
 * @(#)PlafRes_ca.java   02.jul 2007
 * 
 *  Fin del fichero PlafRes_ca.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
