/*
 * @(#)Database.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

/**
 *  General Database Constants and Utilities
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: Database.java,v 1.13 2005/03/11 20:29:01 jjanke Exp $
 */
public class Database {

    /** Oracle ID */
    public static String	DB_ORACLE	= "Oracle";

    /** PostgreSQL ID */
    public static String	DB_POSTGRESQL	= "PostgreSQL";

    /** Sybase ID */
    public static String	DB_SYBASE	= "Sybase";

    /** Supported Databases */
    public static String[]	DB_NAMES	= new String[] {

     //   DB_ORACLE, DB_SYBASE,

        // ,DB_POSTGRESQL
        // begin vpj-c e-evolution 02/08/205 PostgreSQL
         DB_POSTGRESQL

        // end e-evolution 02/08/2005 PostgreSQL
    };

    /** MySQL ID */
    public static String	DB_MYSQL	= "MySQL";

    /** Microsoft ID */
    public static String	DB_MSSQLServer	= "SQLServer";

    /** IBM DB/2 ID */
    public static String	DB_DB2	= "DB2";

    /** Database Classes */
    protected static Class[]	DB_CLASSES	= new Class[] {

      //  DB_Oracle.class, DB_Sybase.class,

        // begin vpj-c e-evolution 02/08/2005 PostgreSQL
        // ,DB_PostgreSQL.class
         DB_PostgreSQL.class

        // end e-evolution 02/08/205     PostgreSQL
    };

    /** Connection Timeout in seconds */
    public static int	CONNECTION_TIMEOUT	= 5;
}	// Database



/*
 * @(#)Database.java   02.jul 2007
 * 
 *  Fin del fichero Database.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
