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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Strip {

    /** Descripción de Campos */

    private static final boolean VERBOSE = false;

    /**
     * Constructor de la clase ...
     *
     */

    public Strip() {}    // Strip

    /**
     * Descripción de Método
     *
     *
     * @param directory
     * @param nameMustContain
     */

    public void stripDirectory( String directory,String nameMustContain ) {
        if( directory == null ) {
            throw new NullPointerException( "Strip: directory cannot be null" );
        }

        File dir = new File( directory );

        if( !dir.exists() ||!dir.isDirectory()) {
            throw new IllegalArgumentException( "Strip: directory  does not exist or is not a directory: " + dir );
        }

        File[] list = dir.listFiles();

        if( list == null ) {
            return;
        }

        if( VERBOSE ) {
            System.out.println( "Stripping directory: " + dir );
        }

        for( int i = 0;i < list.length;i++ ) {
            String name = list[ i ].getAbsolutePath();

            if( list[ i ].isDirectory()) {
                stripDirectory( name,nameMustContain );
            } else if( (nameMustContain == null) || (name.indexOf( nameMustContain ) != -1) ) {
                strip( list[ i ],null );
            }
        }
    }    // stripDirectory

    /**
     * Descripción de Método
     *
     *
     * @param infile
     * @param outfile
     *
     * @return
     */

    public boolean strip( String infile,String outfile ) {
        if( infile == null ) {
            throw new NullPointerException( "Strip: infile cannot be null" );
        }

        File in  = new File( infile );
        File out = null;

        if( outfile != null ) {
            out = new File( outfile );
        }

        //

        return strip( in,out );
    }    // strip

    /**
     * Descripción de Método
     *
     *
     * @param infile
     * @param outfile
     *
     * @return
     */

    public boolean strip( File infile,File outfile ) {
        if( infile == null ) {
            throw new NullPointerException( "Strip: infile cannot ne null" );
        }

        // infile

        if( !infile.exists() ||!infile.isFile()) {
            throw new IllegalArgumentException( "Strip: infile does not exist or is not a file: " + infile );
        }

        System.out.println( "Stripping file: " + infile );

        // outfile

        if( infile.equals( outfile )) {
            outfile = null;
        }

        boolean tempfile = false;

        if( outfile == null ) {
            try {
                outfile = File.createTempFile( "strip",".txt" );
            } catch( IOException ioe ) {
                System.err.println( ioe );

                return false;
            }

            tempfile = true;
        }

        //

        try {
            if( VERBOSE ) {
                System.out.println( "Creating: " + outfile );
            }

            outfile.createNewFile();
        } catch( IOException ioe ) {
            System.err.println( ioe );

            return false;
        }

        if( !outfile.exists() ||!outfile.canWrite()) {
            throw new IllegalArgumentException( "Strip output file cannot be created or written: " + outfile );
        }

        // copy it

        if( !copy( infile,outfile )) {
            return false;
        }

        // rename outfile

        if( tempfile ) {
            if( VERBOSE ) {
                System.out.print( "Renaming original: " + infile );
            }

            if( !infile.renameTo( new File( infile.getAbsolutePath() + ".bak" ))) {
                System.err.println( "Could not rename original file: " + infile );
            }

            if( VERBOSE ) {
                System.out.println( " - Renaming: " + outfile + " to: " + infile );
            }

            if( !outfile.renameTo( infile )) {
                System.err.println( "Could not rename " + outfile + " to: " + infile );
            }
        }

        return true;
    }    // strip

    /**
     * Descripción de Método
     *
     *
     * @param infile
     * @param outfile
     *
     * @return
     */

    private boolean copy( File infile,File outfile ) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream( infile );
        } catch( FileNotFoundException fnfe ) {
            System.err.println( fnfe );

            return false;
        }

        //

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream( outfile,false );    // no append
        } catch( FileNotFoundException fnfe ) {
            System.err.println( fnfe );

            return false;
        }

        int noIn    = 0;
        int noOut   = 0;
        int noLines = 1;

        try {
            int c;

            while(( c = fis.read()) != -1 ) {
                noIn++;

                if( c != 10 )      // lf
                {
                    fos.write( c );
                    noOut++;
                }

                if( c == 13 ) {    // cr
                    noLines++;
                }
            }

            fis.close();
            fos.close();
        } catch( IOException ioe ) {
            System.err.println( ioe );

            return false;
        }

        System.out.println( "  read: " + noIn + ", written: " + noOut + " - lines: " + noLines );

        return true;
    }    // stripIt

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        if( args.length == 0 ) {
            System.err.println( "Syntax: Strip infile outfile" );
            System.exit( -1 );
        }

        String p2 = null;

        if( args.length > 1 ) {
            p2 = args[ 1 ];
        }

        //

        new Strip().strip( args[ 0 ],p2 );
    }    // main
}    // Strip



/*
 *  @(#)Strip.java   02.07.07
 * 
 *  Fin del fichero Strip.java
 *  
 *  Versión 2.2
 *
 */
