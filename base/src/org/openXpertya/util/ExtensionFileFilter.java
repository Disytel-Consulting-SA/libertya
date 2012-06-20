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
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ExtensionFileFilter extends FileFilter implements Serializable {

    /**
     * Constructor de la clase ...
     *
     */

    public ExtensionFileFilter() {
        this( "","" );
    }    // ExtensionFileFilter

    /**
     * Constructor de la clase ...
     *
     *
     * @param extension
     * @param description
     */

    public ExtensionFileFilter( String extension,String description ) {
        setDescription( description );
        setExtension( extension );
    }    // ExtensionFileFilter

    /** Descripción de Campos */

    private String m_extension;

    //

    /** Descripción de Campos */

    private String m_description;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }

    /**
     * Descripción de Método
     *
     *
     * @param newDescription
     */

    public void setDescription( String newDescription ) {
        m_description = newDescription;
    }

    /**
     * Descripción de Método
     *
     *
     * @param newExtension
     */

    public void setExtension( String newExtension ) {
        m_extension = newExtension;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getExtension() {
        return m_extension;
    }

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public boolean accept( File file ) {

        // Need to accept directories

        if( file.isDirectory()) {
            return true;
        }

        String ext = file.getName();
        int    pos = ext.lastIndexOf( '.' );

        // No extension

        if( pos == -1 ) {
            return false;
        }

        ext = ext.substring( pos + 1 );

        if( m_extension.equalsIgnoreCase( ext )) {
            return true;
        }

        return false;
    }    // accept

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param filter
     *
     * @return
     */

    public static String getFileName( File file,FileFilter filter ) {
        return getFile( file,filter ).getAbsolutePath();
    }    // getFileName

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param filter
     *
     * @return
     */

    public static File getFile( File file,FileFilter filter ) {
        String fName = file.getAbsolutePath();

        if( (fName == null) || fName.equals( "" )) {
            fName = "OpenXpertya";
        }

        //

        ExtensionFileFilter eff = null;

        if( filter instanceof ExtensionFileFilter ) {
            eff = ( ExtensionFileFilter )filter;
        } else {
            return file;
        }

        //

        int pos = fName.lastIndexOf( '.' );

        // No extension

        if( pos == -1 ) {
            fName += '.' + eff.getExtension();

            return new File( fName );
        }

        String ext = fName.substring( pos + 1 );

        // correct extension

        if( ext.equalsIgnoreCase( eff.getExtension())) {
            return file;
        }

        fName += '.' + eff.getExtension();

        return new File( fName );
    }    // getFile
}    // ExtensionFileFilter



/*
 *  @(#)ExtensionFileFilter.java   02.07.07
 * 
 *  Fin del fichero ExtensionFileFilter.java
 *  
 *  Versión 2.2
 *
 */
