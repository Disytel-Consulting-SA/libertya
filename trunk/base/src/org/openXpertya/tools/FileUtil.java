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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FileUtil {

    /**
     * Constructor de la clase ...
     *
     *
     * @param file
     * @param filter
     * @param action
     * @param p1
     * @param p2
     */

    public FileUtil( String file,String filter,String action,String p1,String p2 ) {
        this( new File( file ),filter,action,p1,p2 );
    }    // FileUtil

    /**
     * Constructor de la clase ...
     *
     *
     * @param file
     * @param filter
     * @param action
     * @param p1
     * @param p2
     */

    public FileUtil( File file,String filter,String action,String p1,String p2 ) {
        if( (action == null) || (action.length() == 0) ) {
            System.err.println( "FileUtil: No Action" );
        } else if( !validAction( action )) {
            System.err.println( "FileUtil: Action not valid: " + action + ACTIONS );
        } else if( file == null ) {
            System.err.println( "FileUtil: No Input file" );
        } else if( !file.exists()) {
            System.err.println( "FileUtil: Input file does not exist: " + file );
        } else {
            System.out.println( "FileUtil (" + file + ", Filter=" + filter + ", Action=" + action + ")" );
            m_filterString = filter;
            processFile( file,p1,p2 );
            System.out.println( "FileUtil  Process count = " + m_count + "  actions=" + m_actions );
        }
    }    // FileUtil

    /** Descripción de Campos */

    private String m_filterString = null;

    /** Descripción de Campos */

    private FileUtilFilter m_filter = new FileUtilFilter();

    /** Descripción de Campos */

    private int m_count = 0;

    /** Descripción de Campos */

    private int m_actions = 0;

    /** Descripción de Campos */

    private int m_actionIndex = -1;

    /** Descripción de Campos */

    public static final String[] ACTIONS = new String[]{ "List","Replace","Latex" };

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     */

    private boolean validAction( String action ) {
        for( int i = 0;i < ACTIONS.length;i++ ) {
            if( ACTIONS[ i ].equals( action )) {
                m_actionIndex = i;

                return true;
            }
        }

        return false;
    }    // validAction

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param p1
     * @param p2
     */

    private void processFile( File file,String p1,String p2 ) {
        if( file == null ) {
            return;
        } else if( !file.exists()) {
            return;
        } else if( file.isDirectory()) {
            File[] dirFiles = file.listFiles( m_filter );

            for( int i = 0;i < dirFiles.length;i++ ) {
                processFile( dirFiles[ i ],p1,p2 );
            }
        } else {
            System.out.println( " ProcessFile=" + file.getAbsolutePath());
            m_count++;
            processFileAction( file,p1,p2 );
        }
    }    // processFile

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param p1
     * @param p2
     */

    void processFileAction( File file,String p1,String p2 ) {
        try {
            if( m_actionIndex == 0 ) {           // List
                ;
            } else if( m_actionIndex == 1 ) {    // Replace
                replaceString( file,p1,p2 );
            } else if( m_actionIndex == 2 ) {    // Latex
                latex( file );
            }
        } catch( Exception ex ) {
        }
    }                                            // processFileAction

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param from
     * @param to
     *
     * @throws IOException
     */

    private void replaceString( File file,String from,String to ) throws IOException {
        String         fileName = file.getAbsolutePath();
        BufferedReader in       = new BufferedReader( new FileReader( file ));

        //

        File           tmpFile = new File( fileName + ".tmp" );
        BufferedWriter out     = new BufferedWriter( new FileWriter( tmpFile,false ));
        boolean found  = false;
        String  line   = null;
        int     lineNo = 0;

        while(( line = in.readLine()) != null ) {
            lineNo++;

            if( line.indexOf( from ) != -1 ) {
                found = true;
                System.out.println( "  " + lineNo + ": " + line );
                line = Util.replace( line,from,to );
                m_actions++;
            }

            out.write( line );
            out.newLine();
        }    // while reading file

        //

        in.close();
        out.close();

        //

        if( found ) {
            File oldFile = new File( fileName + ".old" );

            if( file.renameTo( oldFile )) {
                if( tmpFile.renameTo( new File( fileName ))) {
                    if( oldFile.delete()) {
                        System.out.println( " - File updated: " + fileName );
                    } else {
                        System.err.println( " - Old File not deleted - " + fileName );
                    }
                } else {
                    System.err.println( " - New File not renamed - " + fileName );
                }
            } else {
                System.err.println( " - Old File not renamed - " + fileName );
            }
        } else {
            if( !tmpFile.delete()) {
                System.err.println( " - Temp file not deleted - " + tmpFile.getAbsolutePath());
            }
        }
    }    // replaceString

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @throws IOException
     */

    private void latex( File file ) throws IOException {
        String         fileName = file.getAbsolutePath();
        BufferedReader in       = new BufferedReader( new FileReader( file ));

        //

        File           outFile = new File( fileName + ".txt" );
        BufferedWriter out     = new BufferedWriter( new FileWriter( outFile,false ));
        String line   = null;
        int    lineNo = 0;

        while(( line = in.readLine()) != null ) {
            lineNo++;

            boolean ignore = false;

            //

            char[]       inLine = line.toCharArray();
            StringBuffer sb     = new StringBuffer();

            for( int i = 0;i < inLine.length;i++ ) {
                char c = inLine[ i ];

                if( c == '\\' ) {
                    ignore = true;
                } else if( c == '{' ) {
                    ignore = false;
                } else if( c == '}' ) {
                    ;
                } else if( !ignore ) {
                    sb.append( c );
                }
            }

            //

            out.write( sb.toString());
            out.newLine();
        }    // while reading file

        //

        in.close();
        out.close();
        System.out.println( "File " + fileName + " - lines=" + lineNo );
    }    // latex

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class FileUtilFilter implements FilenameFilter {

        /**
         * Descripción de Método
         *
         *
         * @param dir
         * @param name
         *
         * @return
         */

        public boolean accept( File dir,String name ) {

            // System.out.println("  Dir=" + dir + ", Name=" + name);

            File file = new File( dir,name );

            if( file.isDirectory()) {
                return true;
            }

            if( (m_filterString == null) || (m_filterString.length() == 0) ) {
                return true;
            }

            if( name == null ) {
                return false;
            }

            // ignore files with ~ and this file

            if( (name.indexOf( "~" ) != -1) || name.equals( "FileUtil.java" )) {
                return false;
            }

            //

            return name.indexOf( m_filterString ) != -1;
        }    // accept
    }    // FileUtilFilter


    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        String directory = "C:\\oxp2_2\\";
        String filter    = ".sql";
        String action    = "Replace";

        if( args.length == 1 ) {
            directory = args[ 0 ];
        }

        if( args.length == 2 ) {
            filter = args[ 1 ];
        }

        if( filter == null ) {
            filter = "";
        }

        //

        new FileUtil( directory,filter,action,"ERP+CPM","ERP+CRM" );
    }    // main
}    // FileUtil



/*
 *  @(#)FileUtil.java   02.07.07
 * 
 *  Fin del fichero FileUtil.java
 *  
 *  Versión 2.2
 *
 */
