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

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ZipUtil {

    /**
     * Constructor de la clase ...
     *
     */

    public ZipUtil() {}    // ZipUtil

    /**
     * Constructor de la clase ...
     *
     *
     * @param fileName
     */

    public ZipUtil( String fileName ) {
        open( fileName );
    }    // ZipUtil

    /**
     * Constructor de la clase ...
     *
     *
     * @param file
     */

    public ZipUtil( File file ) {
        open( file );
    }    // ZipUtil

    /** Descripción de Campos */

    private File m_file;

    /** Descripción de Campos */

    private ZipFile m_zipFile;

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     *
     * @return
     */

    public boolean open( String fileName ) {
        if( fileName == null ) {
            return false;
        }

        try {
            return open( new File( fileName ));
        } catch( Exception ex ) {
            System.err.println( "ZipUtil.open - " + ex );
        }

        return false;
    }    // open

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public boolean open( File file ) {
        if( file == null ) {
            return false;
        }

        m_file = file;

        try {
            if( file.getName().endsWith( "jar" )) {
                m_zipFile = new JarFile( file,false,JarFile.OPEN_READ );
            } else {
                m_zipFile = new ZipFile( file,ZipFile.OPEN_READ );
            }
        } catch( IOException ex ) {
            System.err.println( "ZipUtil.open - " + ex );
            m_zipFile = null;

            return false;
        }

        return true;
    }    // open

    /**
     * Descripción de Método
     *
     */

    public void close() {
        try {
            if( m_zipFile != null ) {
                m_zipFile.close();
            }
        } catch( IOException ex ) {
            System.err.println( "ZipUtil.close - " + ex );
        }

        m_zipFile = null;
    }    // close

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOpen() {
        return m_zipFile != null;
    }    // isOpen

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isJar() {
        return( (m_zipFile != null) && (m_zipFile instanceof JarFile) );
    }    // isJar

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public JarFile getJar() {
        if( (m_zipFile != null) && (m_zipFile instanceof JarFile) ) {
            return( JarFile )m_zipFile;
        }

        return null;
    }    // getJar

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        if( m_zipFile != null ) {
            return m_zipFile.toString();
        }

        return "ZipUtil";
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getContent() {
        if( !isOpen()) {
            return null;
        }

        Enumeration e    = m_zipFile.entries();
        ArrayList   list = new ArrayList();

        while( e.hasMoreElements()) {
            list.add( e.nextElement());
        }

        // return sorted array

        String[] retValue = new String[ list.size()];

        for( int i = 0;i < retValue.length;i++ ) {
            retValue[ i ] = (( ZipEntry )list.get( i )).getName();
        }

        Arrays.sort( retValue );

        return retValue;
    }    // getContent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Enumeration entries() {
        if( !isOpen()) {
            return null;
        }

        return m_zipFile.entries();
    }    // entries

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @return
     */

    public ZipEntry getEntry( String name ) {
        if( !isOpen()) {
            return null;
        }

        return m_zipFile.getEntry( name );
    }    // getEntry

    /**
     * Descripción de Método
     *
     *
     * @param name
     *
     * @return
     */

    public String getEntryInfo( String name ) {
        StringBuffer sb = new StringBuffer( name );
        ZipEntry     e  = getEntry( name );

        if( e == null ) {
            sb.append( ": -" );
        } else {
            Timestamp ts = new Timestamp( e.getTime());

            sb.append( ": " ).append( ts ).append( " - " ).append( e.getSize());
        }

        return sb.toString();
    }    // getEntryInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Manifest getManifest() {
        try {
            JarFile jar = getJar();

            if( jar != null ) {
                return jar.getManifest();
            }
        } catch( IOException ex ) {
            System.err.println( "ZipUtil.getManifest - " + ex );
        }

        return null;
    }    // getManifest

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     * @param entryName
     *
     * @return
     */

    static public ZipEntry getEntry( String fileName,String entryName ) {
        if( (fileName == null) || (entryName == null) ) {
            return null;
        }

        // File

        File file = new File( fileName );

        if( !file.exists()) {
            String fn = findInPath( fileName );

            if( fn == null ) {
                return null;    // file not found
            }

            file = new File( fn );
        }

        ZipUtil zu = new ZipUtil( file );

        if( !zu.isOpen()) {
            return null;
        }

        // Entry

        ZipEntry retValue = zu.getEntry( entryName );

        if( retValue == null ) {
            Enumeration e = zu.entries();

            while( e.hasMoreElements()) {
                ZipEntry entry = ( ZipEntry )e.nextElement();

                if( entry.getName().indexOf( entryName ) != -1 ) {
                    retValue = entry;

                    break;
                }
            }
        }

        zu.close();

        return retValue;
    }    // getEntry

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     *
     * @return
     */

    static public JarFile getJar( String fileName ) {
        if( fileName == null ) {
            return null;
        }

        // File

        File file = new File( fileName );

        if( !file.exists()) {
            String fn = findInPath( fileName );

            if( fn == null ) {
                return null;    // file not found
            }

            file = new File( fn );
        }

        ZipUtil zu = new ZipUtil( file );

        return zu.getJar();
    }    // getJar

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     *
     * @return
     */

    static public Manifest getManifest( String fileName ) {
        if( fileName == null ) {
            return null;
        }

        JarFile jar = getJar( fileName );

        if( jar == null ) {
            return null;
        }

        try {
            return jar.getManifest();
        } catch( IOException ex ) {
            System.err.println( "ZipUtil.getManifest - " + ex );
        }

        return null;
    }    // getManifest

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     * @param jarEntry
     *
     * @return
     */

    static public JarEntry getJarEntry( String fileName,String jarEntry ) {
        if( fileName == null ) {
            return null;
        }

        JarFile jar = getJar( fileName );

        if( jar == null ) {
            return null;
        }

        return jar.getJarEntry( jarEntry );
    }    // getManifest

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     */

    static public void dumpManifest( String fileName ) {
        Manifest mf = getManifest( fileName );

        if( mf == null ) {
            System.out.println( "No Jar file: " + fileName );

            return;
        }

        //

        System.out.println( mf.getEntries());
    }    // dumpManifest

    /**
     * Descripción de Método
     *
     *
     * @param fileName
     * @param entryName
     *
     * @return
     */

    static public String getEntryTime( String fileName,String entryName ) {
        ZipEntry entry = getEntry( fileName,entryName );

        if( entry == null ) {
            return null;
        }

        Timestamp ts = new Timestamp( entry.getTime());

        return ts.toString();
    }    // getEntryTime

    /**
     * Descripción de Método
     *
     *
     * @param jarFile
     *
     * @return
     */

    static public String findInPath( String jarFile ) {
        String   path        = System.getProperty( "java.class.path" );
        String[] pathEntries = path.split( System.getProperty( "path.separator" ));

        for( int i = 0;i < pathEntries.length;i++ ) {

            // System.out.println(pathEntries[i]);

            if( pathEntries[ i ].indexOf( jarFile ) != -1 ) {
                return pathEntries[ i ];
            }
        }

        path        = System.getProperty( "sun.boot.class.path" );
        pathEntries = path.split( System.getProperty( "path.separator" ));

        for( int i = 0;i < pathEntries.length;i++ ) {

            // System.out.println(pathEntries[i]);

            if( pathEntries[ i ].indexOf( jarFile ) != -1 ) {
                return pathEntries[ i ];
            }
        }

        return null;
    }    // findInPath

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        try {

            // Get Jar File

            JarFile jar = ZipUtil.getJar( "CClient.jar" );

            if( jar == null ) {
                jar = ZipUtil.getJar( "OXPTools.jar" );
            }

            if( jar == null ) {
                return;
            }

            // JarEntry je = jar.getJarEntry(JarFile.MANIFEST_NAME);
            // if (je != null)
            // System.out.println("Time " + new Date(je.getTime()));

            Manifest mf = jar.getManifest();

            if( mf != null ) {
                Attributes atts = mf.getMainAttributes();

                atts.getValue( "Implementation-Vendor" );
                atts.getValue( "Implementation-Version" );

                //

            }
        } catch( IOException ex ) {
            System.err.println( ex );
        }
    }
}    // ZipUtil



/*
 *  @(#)ZipUtil.java   25.03.06
 * 
 *  Fin del fichero ZipUtil.java
 *  
 *  Versión 2.2
 *
 */
