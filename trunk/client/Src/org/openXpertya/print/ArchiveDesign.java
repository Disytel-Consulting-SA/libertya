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



package org.openXpertya.print;

import java.io.ByteArrayOutputStream;

import org.openXpertya.model.MArchive;
import org.openXpertya.model.MClient;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.print.layout.LayoutEngine;
import org.openXpertya.print.pdf.text.Rectangle;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ArchiveDesign {

    /**
     * Descripción de Método
     *
     *
     * @param layout
     * @param info
     *
     * @return
     */

    public String archive( LayoutEngine layout,PrintInfo info ) {

        // Do we need to Archive ?

        MClient client   = MClient.get( layout.getCtx());
        String  aaClient = client.getAutoArchive();
        String  aaRole   = null;    // role.getAutoArchive();  //      TODO
        String  aa       = aaClient;

        if( aa == null ) {
            aa = MClient.AUTOARCHIVE_None;
        }

        if( aaRole != null ) {
            if( aaRole.equals( MClient.AUTOARCHIVE_AllReportsDocuments )) {
                aa = aaRole;
            } else if( aaRole.equals( MClient.AUTOARCHIVE_Documents ) &&!aaClient.equals( MClient.AUTOARCHIVE_AllReportsDocuments )) {
                aa = aaRole;
            }
        }

        // Mothing to Archive

        if( aa.equals( MClient.AUTOARCHIVE_None )) {
            return null;
        }

        // Archive External only

        if( aa.equals( MClient.AUTOARCHIVE_ExternalDocuments )) {
            if( info.isReport()) {
                return null;
            }
        }

        // Archive Documents only

        if( aa.equals( MClient.AUTOARCHIVE_Documents )) {
            if( info.isReport()) {
                return null;
            }
        }

        // Create Printable

        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        org.openXpertya.print.pdf.text.Document      document = null;
        org.openXpertya.print.pdf.text.pdf.PdfWriter writer   = null;

        try {
            if( layout.getPageable( false ) instanceof LayoutEngine ) {
                LayoutEngine layoutengine = ( LayoutEngine )( layout.getPageable( false ));
                CPaper cpaper = layoutengine.getPaper();
                int    i      = ( int )cpaper.getWidth( true );
                int    j      = ( int )cpaper.getHeight( true );
                int    k      = 0;

                do {
                    if( k >= layoutengine.getNumberOfPages()) {
                        break;
                    }

                    if( document == null ) {

                        // Paso 1: se crea el documento

                        document = new org.openXpertya.print.pdf.text.Document( new Rectangle( i,j ));

                        // Paso 2: se crea el acceso al documento

                        writer = org.openXpertya.print.pdf.text.pdf.PdfWriter.getInstance( document,bytearrayoutputstream );

                        // Paso 3: se habre el documento

                        document.open();
                    }

                    if( document != null ) {

                        // Paso 4: se completa el contenido y se a�ade al documento

                        // se crea el mapeador de fuentes y se leen todas las fuentes del directorio de fuentes

                        org.openXpertya.print.pdf.text.pdf.DefaultFontMapper mapper = new org.openXpertya.print.pdf.text.pdf.DefaultFontMapper();

                        org.openXpertya.print.pdf.text.FontFactory.registerDirectories();
                        mapper.insertDirectory( "c:\\windows\\fonts" );

                        // se crea una plantilla y el panel Graphics2D para dibujar en �l

                        org.openXpertya.print.pdf.text.pdf.PdfContentByte cb = writer.getDirectContent();
                        org.openXpertya.print.pdf.text.pdf.PdfTemplate tp = cb.createTemplate( i,j );
                        java.awt.Graphics2D g2 = tp.createGraphics( i,j,mapper );

                        layoutengine.print( g2,layoutengine.getPageFormat(),k );
                        g2.dispose();
                        cb.addTemplate( tp,0,0 );
                        document.newPage();
                    }

                    k++;
                } while( true );
            }

            // Paso 5: se cierra el documento

            if( document != null ) {
                document.close();
            }

            bytearrayoutputstream.close();
        } catch( Exception exception ) {
            exception.printStackTrace();
        }

        byte[] data = bytearrayoutputstream.toByteArray();    // No Copy

        if( data == null ) {
            return null;
        }

        // TODO to be done async

        MArchive archive = new MArchive( layout.getCtx(),info,null );

        archive.setBinaryData( data );
        archive.save();

        return null;
    }    // archive

    /**
     * Descripción de Método
     *
     *
     * @param layout
     *
     * @return
     */

    public static boolean isValid( LayoutDesign layout ) {
        return( (layout != null) && true    // Document.isValid((Pageable)layout)
                && (layout.getNumberOfPages() > 0) );
    }                                       // isValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static ArchiveDesign get() {
        if( s_engine == null ) {
            s_engine = new ArchiveDesign();
        }

        return s_engine;
    }    // get

    // Create Archiver

    static {
        s_engine = new ArchiveDesign();
    }

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ArchiveEngine.class );

    /** Descripción de Campos */

    private static ArchiveDesign s_engine = null;

    /**
     * Constructor de la clase ...
     *
     */

    private ArchiveDesign() {
        super();

        if( s_engine == null ) {
            s_engine = this;
        }
    }    // ArchiveDesign

//      private PDFDocument m_document = Document.createBlank();

}    // ArchiveDesign



/*
 *  @(#)ArchiveDesign.java   02.07.07
 * 
 *  Fin del fichero ArchiveDesign.java
 *  
 *  Versión 2.2
 *
 */
