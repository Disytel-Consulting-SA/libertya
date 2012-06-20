/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CLogMgtLog4J {

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     */

    protected static void initialize( boolean isClient ) {
        if( isClient ) {
            LogManager.resetConfiguration();

            Logger rootLogger = LogManager.getRootLogger();

            rootLogger.setLevel( s_currentLevelLog4J );
        }
    }    // initialize

    /** Descripción de Campos */

    private static Level s_currentLevelLog4J = Level.WARN;

    /**
     * Descripción de Método
     *
     *
     * @param enableLogging
     */

    public static void enable( boolean enableLogging ) {
        Logger rootLogger = LogManager.getRootLogger();

        if( enableLogging ) {
            rootLogger.setLevel( s_currentLevelLog4J );
        } else {
            Level level = rootLogger.getLevel();

            rootLogger.setLevel( Level.OFF );
            s_currentLevelLog4J = level;
        }
    }    // enableLog4J
}    // ClientLogMgtLog4J



/*
 *  @(#)CLogMgtLog4J.java   25.03.06
 * 
 *  Fin del fichero CLogMgtLog4J.java
 *  
 *  Versión 2.2
 *
 */
