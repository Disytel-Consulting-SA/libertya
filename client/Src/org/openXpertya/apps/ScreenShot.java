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



package org.openXpertya.apps;

import java.awt.Component;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.ExtensionFileFilter;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ScreenShot {

    /**
     * Descripción de Método
     *
     *
     * @param window
     * @param fileName
     *
     * @return
     */

    public static boolean createJPEG( Window window,String fileName ) {
        if( (window == null) || (fileName == null) ) {
            new IllegalArgumentException( "ScreenShot.createJPEG Window os NULL" );
        }

        // Get File

        File file = getJPGFile( window );

        if( file == null ) {
            return false;
        }

        log.config( "File=" + file );

        if( file.exists()) {
            file.delete();
        }

        // Get Writer

        Iterator    writers = ImageIO.getImageWritersByFormatName( "jpg" );
        ImageWriter writer  = ( ImageWriter )writers.next();

        if( writer == null ) {
            log.log( Level.SEVERE,"no ImageWriter" );

            return false;
        }

        // Get Image

        BufferedImage bi = getImage( window );

        // Write Image

        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream( file );

            writer.setOutput( ios );
            writer.write( bi );
            ios.flush();
            ios.close();
        } catch( IOException ex ) {
            log.log( Level.SEVERE,"ex",ex );

            return false;
        }

        return true;
    }    // createJPEG

    /**
     * Descripción de Método
     *
     *
     * @param parent
     *
     * @return
     */

    protected static File getJPGFile( Component parent ) {
        JFileChooser fc = new JFileChooser();

        fc.addChoosableFileFilter( new ExtensionFileFilter( "jpg",Msg.getMsg( Env.getCtx(),"FileJPEG" )));

        if( fc.showSaveDialog( parent ) != JFileChooser.APPROVE_OPTION ) {
            return null;
        }

        File file = fc.getSelectedFile();

        if( file == null ) {
            return null;
        }

        String fileName = file.getAbsolutePath();

        if( !( fileName.toUpperCase().equals( ".JPG" ) || fileName.toUpperCase().equals( ".JPEG" ))) {
            fileName += ".jpg";
        }

        return new File( fileName );
    }    // getFile

    /**
     * Descripción de Método
     *
     *
     * @param window
     *
     * @return
     */

    protected static BufferedImage getImage( Window window ) {
        BufferedImage bi = new BufferedImage( window.getWidth(),window.getHeight(),BufferedImage.TYPE_INT_RGB );    // TYPE_INT_ARGB is tinted red

        window.paintAll( bi.createGraphics());

        return bi;
    }    // getImage

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ScreenShot.class );
}    // ScreenShot



/*
 *  @(#)ScreenShot.java   02.07.07
 * 
 *  Fin del fichero ScreenShot.java
 *  
 *  Versión 2.2
 *
 */
