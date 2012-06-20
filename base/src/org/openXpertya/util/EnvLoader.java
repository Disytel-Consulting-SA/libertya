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

import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class EnvLoader {

    /** Descripción de Campos */

    private static final boolean DEBUG = false;

    /**
     * Descripción de Método
     *
     *
     * @param prefix
     *
     * @return
     */

    public static boolean load( String prefix ) {
        Properties prop = getEnv( prefix );

        if( prop == null ) {
            return false;
        }

        // load

        Object[] pp = prop.keySet().toArray();

        for( int i = 0;i < pp.length;i++ ) {
            String key   = pp[ i ].toString();
            String value = prop.getProperty( key );

            System.setProperty( key,value );
        }

        CLogMgt.printProperties( System.getProperties(),"System with Environment",false );

        return true;
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @param prefix
     *
     * @return
     */

    public static Properties getEnv( String prefix ) {
        String cmd = "cmd /c set";    // Windows

        if( !System.getProperty( "os.name","" ).startsWith( "Win" )) {
            cmd = "set";    // Unix/Linux
        }

        String result = execCommand( cmd );

        if( (result == null) || (result.length() == 0) ) {
            return null;
        }

        //

        if( prefix == null ) {
            prefix = "";
        }

        return parseEnv( result,prefix );
    }    // getEnv

    /**
     * Descripción de Método
     *
     *
     * @param command
     *
     * @return
     */

    private static String execCommand( String command ) {
        Process cmd;

        try {
            cmd = Runtime.getRuntime().exec( command );
        } catch( Exception e ) {
            System.err.println( "-- Error executing command: " + command + " - " + e.toString());

            return null;
        }

        if( DEBUG ) {
            System.out.println( "** Command executed: " + command );
        }

        StringBuffer bufOut = new StringBuffer();
        StringBuffer bufErr = new StringBuffer();

        try {
            InputStream in  = cmd.getInputStream();
            InputStream err = cmd.getErrorStream();

            //

            int c;

            while(( c = in.read()) != -1 ) {
                bufOut.append(( char )c );
            }

            in.close();

            //

            while(( c = err.read()) != -1 ) {
                bufErr.append(( char )c );
            }

            err.close();
        } catch( Exception e ) {
            System.err.println( "-- Error reading output: " + e.toString());

            return null;
        }

        if( DEBUG ) {
            System.out.println( "** Command result: " + bufOut.toString());
            System.out.println( "** Command error: " + bufErr.toString());
        }

        return bufOut.toString();
    }    // execCommand

    /**
     * Descripción de Método
     *
     *
     * @param input
     * @param prefix
     *
     * @return
     */

    private static Properties parseEnv( String input,String prefix ) {
        Properties prop = new Properties();

        //

        String          separator = System.getProperty( "line.separator","\n" );
        StringTokenizer st        = new StringTokenizer( input,separator );

        while( st.hasMoreTokens()) {
            String s = st.nextToken();

            // System.out.println(">" + s + "<");

            int pos = s.indexOf( "=" );    // first "="

            if( pos > 0 ) {
                prop.setProperty( prefix + s.substring( 0,pos ),s.substring( pos + 1 ));
            }
        }

        if( DEBUG ) {
            System.out.println( "** Loaded " + prop.size() + " Properties" );
        }

        return prop;
    }    // parseEnv
}    // EnvLoader



/*
 *  @(#)EnvLoader.java   02.07.07
 * 
 *  Fin del fichero EnvLoader.java
 *  
 *  Versión 2.2
 *
 */
