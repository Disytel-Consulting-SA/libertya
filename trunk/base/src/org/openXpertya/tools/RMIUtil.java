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



package org.openXpertya.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RMIUtil {

    /**
     * Constructor de la clase ...
     *
     */

    public RMIUtil() {

        // testPort();

        try {
            System.out.println( "Registry ------------------------------------" );

            Registry registry = LocateRegistry.getRegistry();

            System.out.println( "- " + registry );

            String[] list = registry.list();

            System.out.println( "- size=" + list.length );

            for( int i = 0;i < list.length;i++ ) {
                System.out.println( "-- " + list[ i ] );
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }

        try {
            System.out.println( "Server --------------------------------------" );

            // System.out.println("- " + RemoteServer.getClientHost());

            String[] list = Naming.list( "rmi://localhost:1099" );

            System.out.println( "- size=" + list.length );

            for( int i = 0;i < list.length;i++ ) {
                System.out.println( "-- " + list[ i ] );
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }    // RMIUtil

    /**
     * Descripción de Método
     *
     */

    private void testPort() {
        try {
            System.out.println( "Test Port -----------------------------------" );

            Socket socket = new Socket( "localhost",1099 );

            System.out.println( "- Socket=" + socket );

            //

            InputStream in = socket.getInputStream();
            int         i  = 0;

            while( i >= 0 ) {
                i = in.read();

                if( i >= 0 ) {
                    System.out.println(( char )i );
                }
            }
        } catch( IOException ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        new RMIUtil();
    }    // main
}    // RMIUtil



/*
 *  @(#)RMIUtil.java   02.07.07
 * 
 *  Fin del fichero RMIUtil.java
 *  
 *  Versión 2.2
 *
 */
