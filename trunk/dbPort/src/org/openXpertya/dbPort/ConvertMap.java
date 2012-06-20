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



package org.openXpertya.dbPort;

import java.util.TreeMap;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ConvertMap {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static TreeMap getSybaseMap() {
        if( s_sybase.size() == 0 ) {
            initSybase();
        }

        return s_sybase;
    }    // getSybaseMap

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static TreeMap getPostgeSQLMap() {
        if( s_pg.size() == 0 ) {
            initPostgreSQL();
        }

        return s_pg;
    }    // getPostgreSQLMap

    /** Descripción de Campos */

    private static TreeMap s_sybase = new TreeMap();

    /** Descripción de Campos */

    private static TreeMap s_pg = new TreeMap();

    /**
     * Descripción de Método
     *
     */

    static private void initSybase() {

        // Oracle Pattern                  Replacement

        // Data Types

        s_sybase.put( "\\bNUMBER\\b","NUMERIC" );
        s_sybase.put( "\\bDATE\\b","DATETIME" );
        s_sybase.put( "\\bVARCHAR2\\b","VARCHAR" );
        s_sybase.put( "\\bNVARCHAR2\\b","NVARCHAR" );
        s_sybase.put( "\\bNCHAR\\b","NCHAR" );
        s_sybase.put( "\\bBLOB\\b","IMAGE" );
        s_sybase.put( "\\bCLOB\\b","TEXT" );

        // Storage

        s_sybase.put( "\\bCACHE\\b","" );
        s_sybase.put( "\\bUSING INDEX\\b","" );
        s_sybase.put( "\\bTABLESPACE\\s\\w+\\b","" );
        s_sybase.put( "\\bSTORAGE\\([\\w\\s]+\\)","" );

        //

        s_sybase.put( "\\bBITMAP INDEX\\b","INDEX" );

        // Select

        s_sybase.put( "\\bFOR UPDATE\\b","" );
        s_sybase.put( "\\bTRUNC\\(","convert(date," );

        // Functions

        s_sybase.put( "\\bSysDate\\b","getdate()" );
        s_sybase.put( "\\bSYSDATE\\b","getdate()" );
        s_sybase.put( "\\bNVL\\b","COALESCE" );
        s_sybase.put( "\\bAND ROWNUM=1\\b","LIMIT 1");
        s_sybase.put( "\\bTO_DATE\\b","TO_TIMESTAMP" );
        //Añadido por ConSerTi.
        s_sybase.put( "\\bDUMP","");

        //

        s_sybase.put( "\\bDBMS_OUTPUT.PUT_LINE\\b","RAISE NOTICE" );

        // Temporary

        s_sybase.put( "\\bGLOBAL TEMPORARY\\b","TEMPORARY" );
        s_sybase.put( "\\bON COMMIT DELETE ROWS\\b","" );
        s_sybase.put( "\\bON COMMIT PRESERVE ROWS\\b","" );

        // DROP TABLE x CASCADE CONSTRAINTS

        s_sybase.put( "\\bCASCADE CONSTRAINTS\\b","" );

        // Select

        s_sybase.put( "\\sFROM\\s+DUAL\\b","" );

        // Statements

        s_sybase.put( "\\bELSIF\\b","ELSE IF" );

        // Sequences

        s_sybase.put( "\\bSTART WITH\\b","START" );
        s_sybase.put( "\\bINCREMENT BY\\b","INCREMENT" );
    }    // initSybase

    /**
     * Descripción de Método
     *
     */

    static private void initPostgreSQL() {

        // Oracle Pattern                  Replacement

        // Data Types

        s_pg.put( "\\bNUMBER\\b","NUMERIC" );
        s_pg.put( "\\bDATE\\b","TIMESTAMP" );
        s_pg.put( "\\bVARCHAR2\\b","VARCHAR" );
        s_pg.put( "\\bNVARCHAR2\\b","VARCHAR" );
        s_pg.put( "\\bNCHAR\\b","CHAR" );

        // begin vpj-cd e-evolution 03/11/2005 PostgreSQL

        s_pg.put( "\\bBLOB\\b","BYTEA" );    // BLOB not directly supported
        s_pg.put( "\\bCLOB\\b","BYTEA" );    // CLOB not directly supported

        // s_pg.put("\\bBLOB\\b",                  "OID");                 //  BLOB not directly supported
        // s_pg.put("\\bCLOB\\b",                  "OID");                //  CLOB not directly supported
        // end vpj-cd e-evolution 03/11/2005 PostgreSQL

        // Storage

        s_pg.put( "\\bCACHE\\b","" );
        s_pg.put( "\\bUSING INDEX\\b","" );
        s_pg.put( "\\bTABLESPACE\\s\\w+\\b","" );
        s_pg.put( "\\bSTORAGE\\([\\w\\s]+\\)","" );

        //

        s_pg.put( "\\bBITMAP INDEX\\b","INDEX" );

        // Functions

        s_pg.put( "\\bSYSDATE\\b","CURRENT_TIMESTAMP" );    // alternative: NOW()

        // begin vpj-cd e-evolution 03/11/2005 PostgreSQL

        s_pg.put( "\\bgetDate()\\b","NOW()" );    // alternative: NOW()

        // end vpj-cd e-evolution 03/11/2005 PostgreSQL
        //Añadido por ConSerTi
        s_pg.put( "\\bDUMP","");
        s_pg.put( "\\bNVL\\b","COALESCE" );
        s_pg.put( "\\bTO_DATE\\b","TO_TIMESTAMP" );

        //

        s_pg.put( "\\bDBMS_OUTPUT.PUT_LINE\\b","RAISE NOTICE" );

        // Temporary

        s_pg.put( "\\bGLOBAL TEMPORARY\\b","TEMPORARY" );
        s_pg.put( "\\bON COMMIT DELETE ROWS\\b","" );
        s_pg.put( "\\bON COMMIT PRESERVE ROWS\\b","" );

        // DROP TABLE x CASCADE CONSTRAINTS

        s_pg.put( "\\bCASCADE CONSTRAINTS\\b","" );

        // Select

        s_pg.put( "\\sFROM\\s+DUAL\\b","" );

        // Statements

        s_pg.put( "\\bELSIF\\b","ELSE IF" );

        // begin vpj-cd e-evolution 03/11/2005 PostgreSQL

        s_pg.put( "\\bREC \\b","AS REC " );

        // s_pg.put("\\bAND\\sROWNUM=\\b",                 "LIMIT ");
        // end vpj-cd e-evolution 03/11/2005 PostgreSQL

        // Sequences

        s_pg.put( "\\bSTART WITH\\b","START" );
        s_pg.put( "\\bINCREMENT BY\\b","INCREMENT" );
    }    // initPostgreSQL
}    // ConvertMap



/*
 *  @(#)ConvertMap.java   25.03.06
 * 
 *  Fin del fichero ConvertMap.java
 *  
 *  Versión 2.2
 *
 */
