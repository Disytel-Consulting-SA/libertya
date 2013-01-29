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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class JdbcTestPG extends Thread {

    // Default no of threads to 10

    /** Descripción de Campos */

    private static final int NUM_OF_THREADS = 10;

    /** Descripción de Campos */

    private static final String CONNECTION = "jdbc:postgresql://linux:5432/openxp";

    /** Descripción de Campos */

    private static final String UID = "openxp";

    /** Descripción de Campos */

    private static final String PWD = "openxp";

    /** Descripción de Campos */

    private static final String STATEMENT = "SELECT * FROM AD_Column";

    /** Descripción de Campos */

    private static final boolean WITH_OUTPUT = false;

    /** Descripción de Campos */

    private static boolean s_do_yield = true;

    /** Descripción de Campos */

    private static Connection s_sconn = null;

    /** Descripción de Campos */

    private static Connection[] s_conn = null;

    /** Descripción de Campos */

    private static int s_fetchSize = 10;

    // Connection

    /** Descripción de Campos */

    private static int s_cType = 0;

    /** Descripción de Campos */

    private static final String[] C_INFO = {
        "Shared Connection    ","Multiple Connections ","Multiple PreCreated  ",

        // "Data Source          ",
        // "Connection Cache     "

    };

    /** Descripción de Campos */

    private static final int C_SHARED = 0;

    /** Descripción de Campos */

    private static final int C_MULTIPLE = 1;

    /** Descripción de Campos */

    private static final int C_PRECREATED = 2;

//      private static final int        C_DATASOURCE = 3;
//      private static final int        C_CACHE = 4;

    // Data

    /** Descripción de Campos */

    private static int s_rType = 0;

    /** Descripción de Campos */

    private static final String[] R_INFO = {
        "ResultSet            ",

//              "Cached RowSet        ",
//              "JDBC RowSet          "

    };

    /** Descripción de Campos */

    private static final int R_RESULTSET = 0;

//      private static final int        R_CACHED_ROWSET = 1;
//      private static final int        R_JDBC_ROWSET = 2;

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String args[] ) {
        try {

            /* Load the JDBC driver */

            DriverManager.registerDriver( new org.postgresql.Driver());
            s_cType = C_MULTIPLE;
            statementTiming();
            statementTiming();
            statementTiming();

            //

            s_fetchSize = 10;    // standard value

            //

            s_cType    = C_SHARED;
            s_do_yield = false;
            runTest();
            runTest();
            s_do_yield = true;
            runTest();
            runTest();

            //

            s_cType    = C_MULTIPLE;
            s_do_yield = false;
            runTest();
            runTest();
            s_do_yield = true;
            runTest();
            runTest();

            //

            s_cType    = C_PRECREATED;
            s_do_yield = false;
            runTest();
            runTest();
            s_do_yield = true;
            runTest();
            runTest();

            //

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }    // main

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    static void runTest() throws Exception {

        // Create the threads

        Thread[] threadList = new Thread[ NUM_OF_THREADS ];

        s_conn = new Connection[ NUM_OF_THREADS ];

        if( s_cType == C_SHARED ) {
            s_sconn = DriverManager.getConnection( CONNECTION,UID,PWD );
        }

        //
        // spawn threads

        for( int i = 0;i < NUM_OF_THREADS;i++ ) {
            if( WITH_OUTPUT ) {
                System.out.println( "Starting #" + i );
            }

            if( s_cType == C_PRECREATED ) {
                s_conn[ i ] = DriverManager.getConnection( CONNECTION,UID,PWD );
            }

            //

            threadList[ i ] = new JdbcTestPG( i );
            threadList[ i ].start();
        }

        // Start everyone at the same time

        long start = System.currentTimeMillis();

        setGreenLight();

        // wait for all threads to end

        for( int i = 0;i < NUM_OF_THREADS;i++ ) {
            threadList[ i ].join();
        }

        //

        if( s_sconn != null ) {
            s_sconn.close();
        }

        s_sconn = null;

        for( int i = 0;i < NUM_OF_THREADS;i++ ) {
            if( s_conn[ i ] != null ) {
                s_conn[ i ].close();
            }

            s_conn[ i ] = null;
        }

        long result = System.currentTimeMillis() - start;

        System.out.print( C_INFO[ s_cType ] + "Threads=" + NUM_OF_THREADS + " \tYield=" + s_do_yield + " \tms= " + result + " \teach= " + ( result / NUM_OF_THREADS ));
        System.out.println();
    }    // runTest

    /**
     * Descripción de Método
     *
     */

    private static void statementTiming() {
        try {
            long       startConnection = System.currentTimeMillis();
            Connection conn            = null;

            if( s_cType == C_MULTIPLE ) {
                conn = DriverManager.getConnection( CONNECTION,UID,PWD );
            }

            long      startStatement = System.currentTimeMillis();
            Statement stmt           = conn.createStatement();

            // stmt.setFetchSize(s_fetchSize);

            long      startQuery     = System.currentTimeMillis();
            ResultSet rs             = stmt.executeQuery( STATEMENT );
            int       i              = 0;
            long      startRetrieve  = System.currentTimeMillis();

            while( rs.next()) {
                rs.getString( 1 );
                i++;
            }

            long endRetrieve = System.currentTimeMillis();

            // System.out.println(i);

            rs.close();
            rs = null;

            long endQuery = System.currentTimeMillis();

            stmt.close();
            stmt = null;

            long endStatement = System.currentTimeMillis();

            conn.close();
            conn = null;

            long endConnection = System.currentTimeMillis();

            //

            System.out.println( C_INFO[ s_cType ] + "Fetch=" + s_fetchSize + " \tConn=" + ( startStatement - startConnection ) + " \tStmt=" + ( startQuery - startStatement ) + " \tQuery=" + ( startRetrieve - startQuery ) + " \tRetrieve=" + ( endRetrieve - startRetrieve ) + " \tClRs=" + ( endQuery - endRetrieve ) + " \tClStmt=" + ( endStatement - endQuery ) + " \tClConn=" + ( endConnection - endStatement ) + " \t- Total=" + ( endConnection - startConnection ) + " \tStmt=" + ( endStatement - startStatement ) + " \tQuery=" + ( endQuery - startQuery ));
        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }    // statementTiming

    /**
     * Constructor de la clase ...
     *
     *
     * @param id
     */

    public JdbcTestPG( int id ) {
        super();
        m_myId = id;
    }

    /** Descripción de Campos */

    private int m_myId = 0;

    /**
     * Descripción de Método
     *
     */

    public void run() {
        ResultSet rs   = null;
        Statement stmt = null;

        try {
            if( WITH_OUTPUT ) {
                System.out.println( "Thread " + m_myId + " waiting" );
            }

            while( !getGreenLight()) {
                yield();
            }

            if( WITH_OUTPUT ) {
                System.out.println( "Thread " + m_myId + " started" );
            }

            // Get the connection & statement

            if( s_cType == C_SHARED ) {
                stmt = s_sconn.createStatement();
            } else if( s_cType == C_MULTIPLE ) {
                s_conn[ m_myId ] = DriverManager.getConnection( CONNECTION,UID,PWD );
                stmt = s_conn[ m_myId ].createStatement();
            } else if( s_cType == C_PRECREATED ) {
                stmt = s_conn[ m_myId ].createStatement();
            }

            // stmt.setFetchSize(s_fetchSize);

            // Execute the Query

            rs = stmt.executeQuery( STATEMENT );

            // Loop through the results

            while( rs.next()) {
                if( s_do_yield ) {
                    yield();    // Yield To other threads
                }
            }

            // Close all the resources

            rs.close();
            rs = null;

            // Close the statement

            stmt.close();
            stmt = null;

            // Close the local connection

            if( (s_cType == C_SHARED) || (s_cType == C_PRECREATED) ) {
                ;
            } else {
                s_conn[ m_myId ].close();
                s_conn[ m_myId ] = null;
            }
        } catch( Exception e ) {
            System.out.println( "Thread " + m_myId + " got Exception: " + e );
            e.printStackTrace();

            return;
        }

        if( WITH_OUTPUT ) {
            System.out.println( "Thread " + m_myId + " finished" );
        }
    }

    /** Descripción de Campos */

    static boolean greenLight = false;

    /**
     * Descripción de Método
     *
     */

    static synchronized void setGreenLight() {
        greenLight = true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    synchronized boolean getGreenLight() {
        return greenLight;
    }
}    // JdbcTestPG



/*
 *  @(#)JdbcTestPG.java   25.03.06
 * 
 *  Fin del fichero JdbcTestPG.java
 *  
 *  Versión 2.2
 *
 */
