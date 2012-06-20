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



package org.openXpertya.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.openXpertya.interfaces.Server;
import org.openXpertya.interfaces.ServerHome;
import org.openXpertya.interfaces.Status;
import org.openXpertya.interfaces.StatusHome;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class StatusInfo extends HttpServlet {

    /** Descripción de Campos */

    static final private String CONTENT_TYPE = "text/html";

    // Initialize global variables

    /**
     * Descripción de Método
     *
     *
     * @throws ServletException
     */

    public void init() throws ServletException {
        getServletContext().log( "StatusInfo.init" );
    }

    // Process the HTTP Get request

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        response.setContentType( CONTENT_TYPE );

        PrintWriter out = response.getWriter();

        out.println( "<html>" );
        out.println( "<head><title>Status Info</title></head>" );
        out.println( "<body>" );

        InitialContext context = null;

        try {
            context = new InitialContext();
        } catch( Exception ex ) {
            out.println( "<p><b>" + ex + "</b></p>" );
        }

        try {
            StatusHome statusHome = ( StatusHome )context.lookup( StatusHome.JNDI_NAME );
            Status status = statusHome.create();

            out.println( "<p>" + status.getStatus() + "</p>" );
            status.remove();
        } catch( Exception ex ) {
            out.println( "<p><b>" + ex + "</b></p>" );
        }

        try {
            ServerHome serverHome = ( ServerHome )context.lookup( ServerHome.JNDI_NAME );
            Server server = serverHome.create();

            out.println( "<p>" + server.getStatus() + "</p>" );
            server.remove();
        } catch( Exception ex ) {
            out.println( "<p><b>" + ex + "</b></p>" );
        }

        try {
            out.println( "<h2>-- /</h2>" );

            NamingEnumeration ne = context.list( "/" );

            while( ne.hasMore()) {
                out.println( "<br>   " + ne.nextElement());
            }

            out.println( "<h2>-- java</h2>" );
            ne = context.list( "java:" );

            while( ne.hasMore()) {
                out.println( "<br>   " + ne.nextElement());
            }

            out.println( "<h2>-- ejb</h2>" );
            ne = context.list( "ejb" );

            while( ne.hasMore()) {
                out.println( "<br>   " + ne.nextElement());
            }

            //

            out.println( "<h2>-- DS</h2>" );

            DataSource ds = ( DataSource )context.lookup( "java:/OracleDS" );

            out.println( "<br>  DataSource " + ds.getClass().getName() + " LoginTimeout=" + ds.getLoginTimeout());

            Connection con = ds.getConnection( "openxp","openxp" );

            out.println( "<br>  Connection " );
            getServletContext().log( "Connection closed=" + con.isClosed());

            DatabaseMetaData dbmd = con.getMetaData();

            getServletContext().log( "DB " + dbmd.getDatabaseProductName());
            getServletContext().log( "DB V " + dbmd.getDatabaseProductVersion());
            getServletContext().log( "Driver " + dbmd.getDriverName());
            getServletContext().log( "Driver V " + dbmd.getDriverVersion());
            getServletContext().log( "JDBC " + dbmd.getJDBCMajorVersion());
            getServletContext().log( "JDBC mV " + dbmd.getJDBCMinorVersion());
            getServletContext().log( "User " + dbmd.getUserName());
            getServletContext().log( "ANSI 92 " + dbmd.supportsANSI92FullSQL());
            getServletContext().log( "Connection Alter Table ADD" + dbmd.supportsAlterTableWithAddColumn());
            getServletContext().log( "Connection Alter Table DROP " + dbmd.supportsAlterTableWithDropColumn());
            getServletContext().log( "Connection DDL&DML " + dbmd.supportsDataDefinitionAndDataManipulationTransactions());
            getServletContext().log( "Connection CatalogsIn DML " + dbmd.supportsCatalogsInDataManipulation());
            getServletContext().log( "Connection Schema In DML " + dbmd.supportsSchemasInDataManipulation());
        } catch( Exception e ) {
            out.println( "<p><b>" + e + "</b></p>" );
        }

        out.println( "</body></html>" );
    }

    // Process the HTTP Put request

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doPut( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        doGet( request,response );
    }

    // Clean up resources

    /**
     * Descripción de Método
     *
     */

    public void destroy() {
        getServletContext().log( "StatusInfo.destroy" );
    }
}



/*
 *  @(#)StatusInfo.java   25.03.06
 * 
 *  Fin del fichero StatusInfo.java
 *  
 *  Versión 2.2
 *
 */
