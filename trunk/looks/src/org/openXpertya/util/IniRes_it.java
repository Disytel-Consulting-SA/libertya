/*
 * @(#)IniRes_it.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.util;

import java.util.ListResourceBundle;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class IniRes_it extends ListResourceBundle {

    /** Descripción de Campos */
    static final Object[][]	contents	= new String[][] {

        // { "Licencia_OXP",   "License Agreement" },
        { "Licencia_OXP", "Accordo di Licenza" },

        // { "Do_you_accept",      "Do you accept the License ?" },
        { "Do_you_accept", "Accettate la Licenza ?" },

        // { "No",                 "No" },
        { "No", "No" },

        // { "Yes_I_Understand",   "Yes, I Understand and  Accept" },
        { "Yes_I_Understand", "Si, comprendiamo ed accettiamo" },

        // { "license_htm",        "org/openXpertya/install/Licencia.html" },
        { "license_htm", "org/openXpertya/install/Licencia.html" },

        // { "License_rejected",   "License rejected or expired" }
        { "License_rejected", "Licenza rifiutata o scaduta" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Object[][] getContents() {
        return contents;
    }		// getContent
}	// IniRes



/*
 * @(#)IniRes_it.java   02.jul 2007
 * 
 *  Fin del fichero IniRes_it.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
