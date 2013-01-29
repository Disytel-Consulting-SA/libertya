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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;
import org.openXpertya.interfaces.Status;
import org.openXpertya.interfaces.StatusHome;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ConnectTest {

    /**
     * Constructor de la clase ...
     *
     *
     * @param serverName
     */

    public ConnectTest( String serverName ) {
        System.out.println( "ConnectTest: " + serverName );
        System.out.println();

        //

        Hashtable env = new Hashtable();

        env.put( InitialContext.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory" );
        env.put( InitialContext.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces" );
        env.put( InitialContext.PROVIDER_URL,serverName );

        // env.put(InitialContext.SECURITY_PROTOCOL, "");                          //      "ssl"
        // env.put(InitialContext.SECURITY_AUTHENTICATION, "none");        //      "none", "simple", "strong"
        // env.put(InitialContext.SECURITY_PRINCIPAL, "");
        // env.put(InitialContext.SECURITY_CREDENTIALS, "");

        // Get Context

        System.out.println( "Creating context ..." );
        System.out.println( "  " + env );

        InitialContext context = null;

        try {
            context = new InitialContext( env );
        } catch( Exception e ) {
            System.err.println( "ERROR: Could not create context: " + e );

            return;
        }

        testJNP( serverName,context );
        testEJB( serverName,context );
    }    // ConnectTest

    /**
     * Descripción de Método
     *
     *
     * @param serverName
     * @param context
     */

    private void testJNP( String serverName,InitialContext context ) {

        // Connect to MBean

        System.out.println();
        System.out.println( "Connecting to MBean ..." );

        try {
            String     connectorName = "jmx:" + serverName + ":rmi";
            RMIAdaptor server        = ( RMIAdaptor )context.lookup( connectorName );

            System.out.println( "- have Server" );
            System.out.println( "- Default Domain=" + server.getDefaultDomain());
            System.out.println( "- MBeanCount = " + server.getMBeanCount());

            // ObjectName serviceName = new ObjectName ("OpenXpertya:service=openXpertyaCtrl");
            // System.out.println("- " + serviceName + " is registered=" + server.isRegistered(serviceName));

            // System.out.println("  - openxpSummary= "
            // + server.getAttribute(serviceName, "openxpSummary"));

            Object[] params    = {};
            String[] signature = {};
        } catch( Exception e ) {
            System.err.println( "ERROR: Could not contact MBean: " + e );

            return;
        }

        // List Context

        System.out.println();
        System.out.println( " Examining context ...." );

        try {
            System.out.println( "  Namespace=" + context.getNameInNamespace());
            System.out.println( "  Environment=" + context.getEnvironment());
            System.out.println( "  Context '/':" );

            NamingEnumeration ne = context.list( "/" );

            while( ne.hasMore()) {
                System.out.println( "  - " + ne.nextElement());
            }

            //

            System.out.println( "  Context 'ejb':" );
            ne = context.list( "ejb" );

            while( ne.hasMore()) {
                System.out.println( "  - " + ne.nextElement());
            }

            //

            System.out.println( "  Context 'ejb/openXpertya':" );
            ne = context.list( "ejb/openXpertya" );

            while( ne.hasMore()) {
                System.out.println( "  - " + ne.nextElement());
            }
        } catch( Exception e ) {
            System.err.println( "ERROR: Could not examine context: " + e );

            return;
        }
    }    // testJNP

    /**
     * Descripción de Método
     *
     *
     * @param serverName
     * @param context
     */

    private void testEJB( String serverName,InitialContext context ) {
        System.out.println();
        System.out.println( "Connecting to EJB server ..." );

        try {
            System.out.println( "  Name=" + StatusHome.JNDI_NAME );

            StatusHome staHome = ( StatusHome )context.lookup( StatusHome.JNDI_NAME );

            System.out.println( "  .. home created" );

            Status sta = staHome.create();

            System.out.println( "  .. bean created" );
            System.out.println( "  ServerVersion=" + sta.getMainVersion() + " " + sta.getDateVersion());
            sta.remove();
            System.out.println( "  .. bean removed" );
        } catch( Exception e ) {
            System.err.println( "ERROR: Could not connect: " + e );

            return;
        }

        System.out.println();
        System.out.println( "SUCCESS !!" );
    }    // testEJB

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        String serverName = null;

        if( args.length > 0 ) {
            serverName = args[ 0 ];
        }

        if( (serverName == null) || (serverName.length() == 0) ) {
            try {
                serverName = InetAddress.getLocalHost().getHostName();
            } catch( UnknownHostException ex ) {
                ex.printStackTrace();
            }
        }

        // Start

        ConnectTest ct = new ConnectTest( serverName );
    }    // main
}    // ConnectionTest



/*
 *  @(#)ConnectTest.java   25.03.06
 * 
 *  Fin del fichero ConnectTest.java
 *  
 *  Versión 2.2
 *
 */
