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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JFileChooser;

import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.openXpertya.print.CPrinter;
import org.openXpertya.print.PrintUtil;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.ExtensionFileFilter;
import org.openXpertya.util.Msg;

import com.adobe.acrobat.PDFDocument;
import com.adobe.acrobat.file.MemByteArraySource;
import com.adobe.acrobat.sidecar.FloatPoint;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PdfPanel extends CScrollPane implements ActionListener,ItemListener,Pageable,Printable {

    /** Descripción de Campos */

    private PdfToolbar pdftoolbar = null;

    /** Descripción de Campos */

    private MyCanvas lienzo = null;

    /** Descripción de Campos */

    private PDFDocument document = null;

    /** Descripción de Campos */

    private CPanel panel = null;

    /** Descripción de Campos */

    private byte[] datos = null;

    /** Descripción de Campos */

    private double escala = 1;

    /** Descripción de Campos */

    private static final double escalaInicial = 1;

    /** Descripción de Campos */

    private int pagina = 1;

    /** Descripción de Campos */

    private int posicion = ARRIBA;

    /** Descripción de Campos */

    private static final int ARRIBA = 1;

    /** Descripción de Campos */

    private static final int DERECHA = 2;

    /** Descripción de Campos */

    private static final int ABAJO = 3;

    /** Descripción de Campos */

    private static final int IZQUIERDA = 4;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PdfPanel.class );
    
    private static File docFile;
    
    
    /**
     * Constructor de la clase ...
     *
     */

    private PdfPanel() {
        super();
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param view
     */

    private PdfPanel( Component view ) {
        super( view );
    }

    /**
     * Descripción de Método
     *
     *
     * @param pdf
     * @param pan
     * @param withOpen
     * @param withSave
     * @param withPrint
     * @param withPage
     * @param withZoom
     * @param withRotate
     *
     * @return
     */

    public static PdfPanel loadPdf( File pdf,CPanel pan,boolean withOpen,boolean withSave,boolean withPrint,boolean withPage,boolean withZoom,boolean withRotate ) {
        try {
            int            w   = PdfToolbar.WIDTH;
            int            h   = pan.getHeight() - PdfToolbar.HEIGTH;
            PDFDocument    doc = new PDFDocument( pdf );
            java.awt.Image imagen;
            PdfPanel       x  = new PdfPanel();
            MyCanvas       li = x.new MyCanvas( w,h );

            if( doc.getNumPages() > 0 ) {
                FloatPoint cropBoxSize = doc.getPageSize( 0 );

                docFile = pdf;
                imagen = createImageFromPDF( doc,pan,cropBoxSize,0,escalaInicial,ARRIBA);
                li.setImage( imagen,( int )( cropBoxSize.x * escalaInicial ),( int )( cropBoxSize.y * escalaInicial ));

                PdfPanel pdfpanel = new PdfPanel( li );

                li.setObserver( pdfpanel );
                li.setSize(( int )( cropBoxSize.x * escalaInicial ),( int )( cropBoxSize.y * escalaInicial ));
                pdfpanel.setMinimumSize( new Dimension( w,h ));
                pan.setMaximumSize( new Dimension( w,h ));
                pdfpanel.setSize( w,h );
                pan.setMinimumSize( new Dimension( w,h ));
                pan.setMaximumSize( new Dimension( w,h ));
                pan.setSize( new Dimension( w,h ));
                pdfpanel.setLienzo( li );
                pdfpanel.setDocument( doc );
                pdfpanel.setPanel( pan );

                PdfToolbar pdftol = new PdfToolbar( pdfpanel,withSave,withOpen,withPrint,withPage,withZoom,withRotate );

                pdftol.getPaginas().setText( "De " + doc.getNumPages());
                pdftol.getPagina().setValue( new Integer( 1 ));
                pdftol.revalidate();
                pdfpanel.setToolbar( pdftol );
                pan.add( pdftol,BorderLayout.NORTH );
                pan.add( pdfpanel,BorderLayout.CENTER );

                return pdfpanel;
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(pdf)",e );
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param pan
     */

    private void setPanel( CPanel pan ) {
        panel = pan;
    }

    /**
     * Descripción de Método
     *
     *
     * @param pdf
     * @param pan
     * @param withOpen
     * @param withSave
     * @param withPrint
     * @param withPage
     * @param withZoom
     * @param withRotate
     *
     * @return
     */

    public static PdfPanel loadPdf( byte[] pdf,CPanel pan,boolean withOpen,boolean withSave,boolean withPrint,boolean withPage,boolean withZoom,boolean withRotate ) {
        try {
            int            w   = PdfToolbar.WIDTH;
            int            h   = pan.getHeight() - PdfToolbar.HEIGTH;
            PDFDocument    doc = new PDFDocument( new MemByteArraySource( pdf ));
            java.awt.Image imagen;
            PdfPanel       x  = new PdfPanel();
            MyCanvas       li = x.new MyCanvas( w,h );

            // pdf temporal para visualizacion  
            OutputStream out = new FileOutputStream("reusablefile.tmp");
            out.write(pdf);
            out.close();
            docFile = new File("reusablefile.tmp");
            
            if( doc.getNumPages() > 0 ) {
                FloatPoint cropBoxSize = doc.getPageSize( 0 );

                imagen = createImageFromPDF( doc,pan,cropBoxSize,0,escalaInicial,ARRIBA);
                li.setImage( imagen,( int )( cropBoxSize.x * escalaInicial ),( int )( cropBoxSize.y * escalaInicial ));

                PdfPanel pdfpanel = new PdfPanel( li );

                li.setObserver( pdfpanel );
                li.setSize(( int )( cropBoxSize.x * escalaInicial ),( int )( cropBoxSize.y * escalaInicial ));
                pdfpanel.setMinimumSize( new Dimension( w,h ));
                pan.setMaximumSize( new Dimension( w,h ));
                pdfpanel.setSize( w,h );
                pan.setMinimumSize( new Dimension( w,h ));
                pan.setMaximumSize( new Dimension( w,h ));
                pan.setSize( new Dimension( w,h ));
                pdfpanel.setLienzo( li );
                pdfpanel.setDocument( doc );
                pdfpanel.setPanel( pan );
                pdfpanel.setDatos( pdf );

                PdfToolbar pdftol = new PdfToolbar( pdfpanel,withOpen,withSave,withPrint,withPage,withZoom,withRotate );

                pdftol.getPaginas().setText( "De " + doc.getNumPages());
                pdftol.getPagina().setValue( new Integer( 1 ));
                pdftol.revalidate();
                pdfpanel.setToolbar( pdftol );
                pan.add( pdftol,BorderLayout.NORTH );
                pan.add( pdfpanel,BorderLayout.CENTER );

                return pdfpanel;
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(pdf)",e );
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param dat
     */

    private void setDatos( byte[] dat ) {
        datos = dat;
    }

    /**
     * Descripción de Método
     *
     *
     * @param doc
     * @param anyComponent
     * @param cropBoxSize
     * @param page
     * @param escal
     * @param pos
     *
     * @return
     */

	public static java.awt.Image createImageFromPDF ( PDFDocument docu,Component anyComponent,FloatPoint cropBoxSize,int page,double escal,int pos)
	{
		try
		{
            RandomAccessFile raf = new RandomAccessFile(docFile, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int)channel.size());
            channel.read(buf);
            PDFFile pdffile = new PDFFile(buf);			
            
			// tomar la pagina page-esima del documento
			PDFPage pageSheet = pdffile.getPage(page + 1);
			Rectangle rect = new Rectangle(0, 0, (int)pageSheet.getBBox().getWidth(), (int)pageSheet.getBBox().getHeight());
        
			//generar la imagen
			Image img = pageSheet.getImage(
                rect.width, rect.height, //ancho y & alto
                rect, // rect
                null, // ImageObserver
                true, // llenar el fondo con blanco?
                true  // bloquear hasta finalizar procesamiento
                );
        
            int    ancho = ( int )( cropBoxSize.x * escal );
            int    alto  = ( int )( cropBoxSize.y * escal );
            double arg1  = 0,
                   arg2  = 0,
                   arg3  = 0,
                   arg4  = 0,
                   arg5  = 0,
                   arg6  = 0;

            switch( pos ) {
            case ARRIBA:
                arg1 = escal;
                arg4 = escal;

                break;
            case DERECHA:
                arg2 = escal * cropBoxSize.y / cropBoxSize.x;
                arg3 = -escal * cropBoxSize.x / cropBoxSize.y;
                arg5 = ancho;

                break;
            case ABAJO:
                arg1 = -escal;
                arg4 = -escal;
                arg5 = ancho;
                arg6 = alto;

                break;
            case IZQUIERDA:
                arg2 = -escal * cropBoxSize.y / cropBoxSize.x;
                arg3 = escal * cropBoxSize.x / cropBoxSize.y;
                arg6 = alto;

                break;
            }
        
            java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform( arg1,arg2,arg3,arg4,arg5,arg6);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR); 
            img = op.filter(createBufferedImage(img),null);
          
            buf.clear();
            channel.close();
            raf.close();
            
            return img;
		}
		catch (Exception e) {
			log.log(Level.SEVERE,"(Create Image from pdf):"+e.getMessage(),e );
			return null;
		}
	}    
   
	public static BufferedImage createBufferedImage(java.awt.Image image)
	{
	   if(image instanceof BufferedImage) {
	      return (BufferedImage)image;
	   }
	  BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null),BufferedImage.TYPE_INT_ARGB); // AlfaRGB
	  Graphics2D g = bi.createGraphics();
	  g.drawImage(image, 0, 0, null);
	  g.dispose(); 	 
	  return bi;
	}

    
    /**
     * Descripción de Método
     *
     *
     * @param pdftb
     */

    private void setToolbar( PdfToolbar pdftb ) {
        pdftoolbar = pdftb;
    }

    /**
     * Descripción de Método
     *
     *
     * @param pdfdoc
     */

    private void setDocument( PDFDocument pdfdoc ) {
        document = pdfdoc;
    }

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    private void setLienzo( MyCanvas l ) {
        lienzo = l;
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( "abrir" )) {}
        else if( e.getActionCommand().equals( "guardar" )) {

            //

            JFileChooser chooser = new JFileChooser();

            chooser.setDialogType( JFileChooser.SAVE_DIALOG );
            chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
            chooser.setDialogTitle( Msg.getMsg( Env.getCtx(),"Save" ));

            //

            chooser.addChoosableFileFilter( new ExtensionFileFilter( "pdf",Msg.getMsg( Env.getCtx(),"FilePDF" )));

            //

            if( chooser.showSaveDialog( this ) != JFileChooser.APPROVE_OPTION ) {
                return;
            }

            // Create File

            File outFile = ExtensionFileFilter.getFile( chooser.getSelectedFile(),chooser.getFileFilter());

            try {
                outFile.createNewFile();
            } catch( IOException ex ) {
                log.log( Level.SEVERE,"",e );
                ADialog.error( 0,this,"FileCannotCreate",ex.getLocalizedMessage());

                return;
            }

            String ext = outFile.getPath();

            // no extension

            if( ext.lastIndexOf( "." ) == -1 ) {
                ADialog.error( 0,this,"FileInvalidExtension" );

                return;
            }

            ext = ext.substring( ext.lastIndexOf( "." ) + 1 ).toLowerCase();
            log.config( "File=" + outFile.getPath() + "; Type=" + ext );
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

            if( ext.equalsIgnoreCase( "pdf" )) {
                try {
                    if( datos != null ) {
                        FileOutputStream fos = new FileOutputStream( outFile );

                        fos.write( datos );
                        fos.close();
                    }
                } catch( IOException ioe ) {
                    log.log( Level.SEVERE,"Saving",ioe );
                }
            }
        } else if( e.getActionCommand().equals( "imprimir" )) {
            try {
                PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

                // media-printable-area = (25.4,25.4)->(165.1,228.6)mm - class javax.print.attribute.standard.MediaPrintableArea

                MediaPrintableArea area = new MediaPrintableArea( 0,0,210,297,MediaPrintableArea.MM );

                pras.add( area );

                // orientation-requested = landscape - class javax.print.attribute.standard.OrientationRequested

                if( (posicion == DERECHA) || (posicion == IZQUIERDA) ) {
                    pras.add( OrientationRequested.LANDSCAPE );
                } else {
                    pras.add( OrientationRequested.PORTRAIT );
                }

                // PrinterJob

                PrinterJob job = CPrinter.getPrinterJob();

                job.setPageable( this );    // no copy

                // Dialog

                try {
                    if( !job.printDialog( pras )) {
                        return;
                    }
                } catch( Exception ex ) {
                    log.log( Level.WARNING,"Operating System Print Issue, check & try again",ex );

                    return;
                }

                // submit

                PrintUtil.print( job,pras,false,false );

//                              PrintUtil.print(this, null, "Print PDF", 1, true);

            } catch( Exception ex ) {
                log.log( Level.SEVERE,"Print",ex );
            }
        } else if( e.getActionCommand().equals( "pagina" )) {
            pagina = Integer.parseInt( pdftoolbar.getPagina().getValue().toString());
        } else if( e.getActionCommand().equals( "anterior" )) {
            if( pagina > 1 ) {
                pagina--;
            }
        } else if( e.getActionCommand().equals( "siguiente" )) {
            try {
                if( pagina < document.getNumPages()) {
                    pagina++;
                }
            } catch( Exception ex ) {
                log.log( Level.SEVERE,"Next",ex );
            }
        } else if( e.getActionCommand().equals( "ultima" )) {
            try {
                pagina = document.getNumPages();
            } catch( Exception ex ) {
                log.log( Level.SEVERE,"Last",ex );
            }
        } else if( e.getActionCommand().equals( "primera" )) {
            pagina = 1;
        } else if( e.getActionCommand().equals( "zoomMenos" )) {
            int     i      = pdftoolbar.getZoom().getSelectedIndex();
            boolean seguir = true;

            if( i == -1 ) {
                for( int x = pdftoolbar.getZoom().getItemCount() - 1;( (x >= 0) && seguir );x-- ) {
                    if( Integer.parseInt( pdftoolbar.getZoom().getItemAt( x ).toString()) < Integer.parseInt( pdftoolbar.getZoom().getValue().toString())) {
                        i      = x + 1;
                        seguir = true;
                    }
                }
            }

            if( i > 0 ) {
                pdftoolbar.getZoom().setSelectedIndex( i - 1 );
            }
        } else if( e.getActionCommand().equals( "zoomMas" )) {
            int     i      = pdftoolbar.getZoom().getSelectedIndex();
            boolean seguir = true;

            if( i == -1 ) {
                for( int x = 0;( (x < pdftoolbar.getZoom().getItemCount()) && seguir );x++ ) {
                    if( Integer.parseInt( pdftoolbar.getZoom().getItemAt( x ).toString()) > Integer.parseInt( pdftoolbar.getZoom().getValue().toString())) {
                        i      = x - 1;
                        seguir = true;
                    }
                }
            }

            if(( i + 1 ) < pdftoolbar.getZoom().getItemCount()) {
                pdftoolbar.getZoom().setSelectedIndex( i + 1 );
            }
        } else if( e.getActionCommand().equals( "giraC" )) {
            if( posicion == ARRIBA ) {
                posicion = IZQUIERDA;
            } else {
                posicion--;
            }
        } else if( e.getActionCommand().equals( "giraR" )) {
            if( posicion == IZQUIERDA ) {
                posicion = ARRIBA;
            } else {
                posicion++;
            }
        }

        loadPage();
    }

    /**
     * Descripción de Método
     *
     *
     * @param itemevent
     */

    public void itemStateChanged( ItemEvent itemevent ) {
        loadPage();
    }

    /**
     * Descripción de Método
     *
     */

    private void loadPage() {
        try {
            java.awt.Image imagen;

            if( document.getNumPages() >= pagina & pagina > 0 ) {
                escala = Double.parseDouble( pdftoolbar.getZoom().getSelectedItem().toString()) / 100;

                FloatPoint cropBoxSize = document.getPageSize( pagina - 1 );

                imagen = createImageFromPDF( document,panel,cropBoxSize,pagina - 1,escala,posicion);

                if( (posicion == ARRIBA) || (posicion == ABAJO) ) {
                    lienzo.setImage( imagen,( int )( cropBoxSize.x * escala ),( int )( cropBoxSize.y * escala ));
                    lienzo.setSize(( int )( cropBoxSize.x * escala ),( int )( cropBoxSize.y * escala ));
                } else    // (posicion == DERECHA || posicion == IZQUIERDA)
                {
                    lienzo.setImage( imagen,( int )( cropBoxSize.y * escala ),( int )( cropBoxSize.x * escala ));
                    lienzo.setSize(( int )( cropBoxSize.y * escala ),( int )( cropBoxSize.x * escala ));
                }

                pdftoolbar.getPagina().setValue( new Integer( pagina ));
                pdftoolbar.revalidate();
                lienzo.repaint();
                lienzo.revalidate();
                repaint();
                revalidate();
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(loadPage)",e );
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        this.remove( lienzo );
        panel.remove( pdftoolbar );
        panel.remove( this );
        pdftoolbar = null;
        lienzo     = null;
    }

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private class MyCanvas extends CPanel {

        /** Descripción de Campos */

        private Image imagen = null;

        /** Descripción de Campos */

        private int ancho = 0;

        /** Descripción de Campos */

        private int alto = 0;

        /** Descripción de Campos */

        private ImageObserver observer = null;

        /** Descripción de Campos */

        private CLogger log = CLogger.getCLogger( MyCanvas.class );

        /**
         * Constructor de la clase ...
         *
         *
         * @param w
         * @param h
         */

        public MyCanvas( int w,int h ) {
            super();
            ancho = w;
            alto  = h;
        }

        /**
         * Descripción de Método
         *
         *
         * @param obs
         */

        public void setObserver( ImageObserver obs ) {
            observer = obs;
        }

        /**
         * Descripción de Método
         *
         *
         * @param im
         * @param w
         * @param h
         */

        public void setImage( Image im,int w,int h ) {
            if( im != null ) {
                imagen = im;
                ancho  = w;
                alto   = h;
            } else {
                log.info( "Null imagen" );
            }
        }

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public Dimension getPreferredSize() {
            return new Dimension( ancho,alto );
        }

        /**
         * Descripción de Método
         *
         *
         * @param g
         */

        public void paint( Graphics g ) {
            if( imagen != null ) {
                g.drawImage( imagen,0,0,ancho,alto,observer );
            }
        }
    }


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNumberOfPages() {
        try {
            return document.getNumPages();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Number of pages",e );
        }

        return 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws IndexOutOfBoundsException
     */

    public PageFormat getPageFormat( int pageIndex ) throws IndexOutOfBoundsException {
        PageFormat pf = new PageFormat();
        Paper      p  = new Paper();

        p.setSize( lienzo.getWidth(),lienzo.getHeight());
        pf.setPaper( p );

        int orient = PageFormat.PORTRAIT;

        if( posicion % 2 == 0 ) {
            orient = PageFormat.LANDSCAPE;
        }

        pf.setOrientation( orient );

        return pf;
    }

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws IndexOutOfBoundsException
     */

    public Printable getPrintable( int pageIndex ) throws IndexOutOfBoundsException {
        return this;
    }

    /**
     * Descripción de Método
     *
     *
     * @param graphics
     * @param pageFormat
     * @param pageIndex
     *
     * @return
     *
     * @throws PrinterException
     */

    public int print( Graphics graphics,PageFormat pageFormat,int pageIndex ) throws PrinterException {
        try {
            pagina = pageIndex;
            pdftoolbar.getZoom().setSelectedItem( "100" );
            loadPage();
            lienzo.paint( graphics );

            return 0;
        } catch( Exception ex ) {
            return 1;
        }
    }
}



/*
 *  @(#)PdfPanel.java   02.07.07
 * 
 *  Fin del fichero PdfPanel.java
 *  
 *  Versión 2.2
 *
 */
