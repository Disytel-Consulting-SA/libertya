/*
 * @(#)TestConnection.java   12.oct 2007  Versión 2.2
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *      Test Connection (speed)
 *
 *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *      @version        $Id: TestConnection.java,v 1.6 2005/03/11 20:29:00 jjanke Exp $
 */
public class TestConnection {

    /** Descripción de Campo */
    private String	m_uid	= "openxp";

    /** Descripción de Campo */
    private String	m_sql	= "SELECT * FROM AD_Element";

    /** Descripción de Campo */
    private String	m_pwd	= "openxp";

    /** Descripción de Campo */
    private Connection	m_conn;

    /** Descripción de Campo */
    private String	m_jdbcURL;

    /**
     *      Test Connection
     *
     *      @param jdbcURL JDBC URL
     *      @param uid user
     *      @param pwd password
     */
    public TestConnection(String jdbcURL, String uid, String pwd) {

        System.out.println("Test Connection for " + jdbcURL);
        m_jdbcURL	= jdbcURL;
        m_uid		= uid;
        m_pwd		= pwd;
        init();

        if (m_conn != null) {

            long	time	= test();

            time	+= test();
            time	+= test();
            time	+= test();
            System.out.println("");
            System.out.println("Total Average (" + m_jdbcURL + ")= " + (time / 4) + "ms");
        }

    }		// TestConnection

    /**
     *      Initialize & Open Connection
     */
    private void init() {

        long	start	= System.currentTimeMillis();
        Driver	driver	= null;

        try {
            driver	= DriverManager.getDriver(m_jdbcURL);
        } catch (SQLException ex) {

            // System.err.println("Init - get Driver: " + ex);
        }

        if (driver == null) {

            try {
                DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            } catch (SQLException ex) {
                System.err.println("Init = register Driver: " + ex);
            }
        }

        long	end	= System.currentTimeMillis();

        System.out.println("(1) Driver = " + (end - start) + "ms");

        //
        start	= System.currentTimeMillis();

        try {
            m_conn	= DriverManager.getConnection(m_jdbcURL, m_uid, m_pwd);
        } catch (SQLException ex) {
            System.err.println("Init = get Connection: " + ex);
        }

        end	= System.currentTimeMillis();
        System.out.println("(2) Get Connection = " + (end - start) + "ms");

        //
        start	= System.currentTimeMillis();

        try {

            if (m_conn != null) {
                m_conn.close();
            }

        } catch (SQLException ex) {
            System.err.println("Init = close Connection: " + ex);
        }

        end	= System.currentTimeMillis();
        System.out.println("(3) Close Connection = " + (end - start) + "ms");

    }		// init

    /**
     * **********************************************************************
     *
     * @param args
     */

    /**
     *      Test Connection.
     *  java -cp dbPort.jar;oracle.jar org.openXpertya.db.TestConnection
     *      @param args arguments optional <jdbcURL> <uid> <pwd>
     *      Example: jdbc:oracle:thin:@dev:1521:dev openxp openxp
     */
    public static void main(String[] args) {

        String	url	= "jdbc:oracle:thin:@//localhost:1521/lap11";
        String	uid	= "openxp";
        String	pwd	= "openxp";

        //
        if (args.length == 0) {

            System.out.println("TestConnection <jdbcUrl> <uid> <pwd>");
            System.out.println("Example: jdbc:oracle:thin:@//dev:1521/dev openxp openxp");
            System.out.println("Example: jdbc:oracle:oci8:@dev openxp openxp");

        } else if (args.length > 0) {
            url	= args[0];
        } else if (args.length > 1) {
            url	= args[1];
        } else if (args.length > 2) {
            url	= args[2];
        }

        System.out.println("");

        TestConnection	test	= new TestConnection(url, uid, pwd);

    }		// main

    /**
     *      Test ResultSet
     *      @return time in ms
     */
    private long test() {

        System.out.println("");

        long	totalStart	= System.currentTimeMillis();
        long	start		= System.currentTimeMillis();

        try {
            m_conn	= DriverManager.getConnection(m_jdbcURL, m_uid, m_pwd);
        } catch (SQLException ex) {

            System.err.println("Test get Connection: " + ex);

            return -1;
        }

        long	end	= System.currentTimeMillis();

        System.out.println("(A) Get Connection = " + (end - start) + "ms");

        //
        try {

            start	= System.currentTimeMillis();

            Statement	stmt	= m_conn.createStatement();

            end	= System.currentTimeMillis();
            System.out.println("(B) Create Statement = " + (end - start) + "ms");

            //
            start	= System.currentTimeMillis();

            ResultSet	rs	= stmt.executeQuery(m_sql);

            end	= System.currentTimeMillis();
            System.out.println("(C) Execute Query = " + (end - start) + "ms");

            //
            int	no	= 0;

            start	= System.currentTimeMillis();

            while (rs.next()) {

                int	i	= rs.getInt("AD_Client_ID");
                String	s	= rs.getString("Name");

                i	+= s.length();
                no++;
            }

            end	= System.currentTimeMillis();
            System.out.println("(D) Read ResultSet = " + (end - start) + "ms - per 10 rows " + ((end - start) / (no / 10)) + "ms");

            //
            start	= System.currentTimeMillis();
            rs.close();
            end	= System.currentTimeMillis();
            System.out.println("(E) Close ResultSet = " + (end - start) + "ms");

            //
            start	= System.currentTimeMillis();
            stmt.close();
            end	= System.currentTimeMillis();
            System.out.println("(F) Close Statement = " + (end - start) + "ms");

        } catch (SQLException e) {
            System.err.println("Test: " + e);
        }

        //
        start	= System.currentTimeMillis();

        try {

            if (m_conn != null) {
                m_conn.close();
            }

        } catch (SQLException ex) {
            System.err.println("Test close Connection: " + ex);
        }

        end	= System.currentTimeMillis();
        System.out.println("(G) Close Connection = " + (end - start) + "ms");

        long	totalEnd	= System.currentTimeMillis();

        System.out.println("Total Test = " + (totalEnd - totalStart) + "ms");

        return (totalEnd - totalStart);

    }		// test
}	// TestConnection



/*
 * @(#)TestConnection.java   02.jul 2007
 * 
 *  Fin del fichero TestConnection.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
