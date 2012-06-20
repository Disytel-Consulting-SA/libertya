/*
 * @(#)Trace.java   12.oct 2007  Versión 2.2
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

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class Trace {

    /**
     * Descripción de Método
     *
     */
    public static void printStack() {
        printStack(true, true);
    }		// printStack

    /**
     * Descripción de Método
     *
     *
     * @param oxpOnly
     * @param first8only
     */
    public static void printStack(boolean oxpOnly, boolean first8only) {

        Throwable	t	= new Throwable();

        // t.printStackTrace();
        int			counter		= 0;
        StackTraceElement[]	elements	= t.getStackTrace();

        for (int i = 1; i < elements.length; i++) {

            if (elements[i].getClassName().indexOf("util.Trace") != -1) {
                continue;
            }

            if (!oxpOnly || (oxpOnly && elements[i].getClassName().startsWith("org.openXpertya"))) {

            	Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(i + ": " + elements[i]);

                if (first8only && (++counter > 7)) {
                    break;
                }
            }
        }

    }		// printStack

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param nestLevel
     *
     * @return
     */
    public static String getCallerClass(int nestLevel) {

        String[]	array	= getCallerClasses(null, nestLevel);

        if (array.length < nestLevel) {
            return "";
        }

        return array[nestLevel];

    }		// getCallerClass

    /**
     * Descripción de Método
     *
     *
     * @param caller
     * @param maxNestLevel
     *
     * @return
     */
    public static String[] getCallerClasses(Throwable caller, int maxNestLevel) {

        int	nestLevel	= maxNestLevel;

        if (nestLevel < 1) {
            nestLevel	= 99;
        }

        //
        ArrayList	list	= new ArrayList();
        Throwable	t	= caller;

        if (t == null) {
            t	= new Throwable();
        }

        StackTraceElement[]	elements	= t.getStackTrace();

        for (int i = 0; (i < elements.length) && (list.size() <= maxNestLevel); i++) {

            String	className	= elements[i].getClassName();

            // System.out.println(list.size() + ": " + className);
            if (!(className.startsWith("org.openXpertya.util.Trace") || className.startsWith("java.lang.Throwable"))) {
                list.add(className);
            }
        }

        String[]	retValue	= new String[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getCallerClasses

    /**
     * Descripción de Método
     *
     *
     * @param className
     *
     * @return
     */
    public static boolean isCalledFrom(String className) {

        if ((className == null) || (className.length() == 0)) {
            return false;
        }

        return getCallerClass(1).indexOf(className) != -1;

    }		// isCalledFrom
}	// Trace



/*
 * @(#)Trace.java   02.jul 2007
 * 
 *  Fin del fichero Trace.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
