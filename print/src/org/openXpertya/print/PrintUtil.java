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

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Properties;
import java.util.logging.Level;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUIFactory;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintUtil {

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PrintUtil.class );

    /** Descripción de Campos */

    private static PrintRequestAttributeSet s_prats = new HashPrintRequestAttributeSet();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static PrintRequestAttributeSet getDefaultPrintRequestAttributes() {
        return s_prats;
    }    // getDefaultPrintRequestAttributes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static DocFlavor getDefaultFlavor() {
        return DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    }    // getDefaultFlavor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static PrintService[] getPrintServices() {
        return PrintServiceLookup.lookupPrintServices( getDefaultFlavor(),getDefaultPrintRequestAttributes());
    }    // getPrintServices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static PrintService getDefaultPrintService() {
        return PrintServiceLookup.lookupDefaultPrintService();
    }    // getPrintServices

    /**
     * Descripción de Método
     *
     *
     * @param pageable
     * @param printerName
     * @param jobName
     * @param copies
     * @param withDialog
     */

    static public void print( Pageable pageable,String printerName,String jobName,int copies,boolean withDialog ) {
        if( pageable == null ) {
            return;
        }

        String name = "openXpertya_";

        if( jobName != null ) {
            name += jobName;
        }

        //

        PrinterJob job = CPrinter.getPrinterJob( printerName );

        job.setJobName( name );
        job.setPageable( pageable );

        // Attributes

        HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();

        prats.add( new Copies( copies ));

        // Set Orientation

        if( pageable.getPageFormat( 0 ).getOrientation() == PageFormat.PORTRAIT ) {
            prats.add( OrientationRequested.PORTRAIT );
        } else {
            prats.add( OrientationRequested.LANDSCAPE );
        }

        prats.add( new JobName( name,Language.getLoginLanguage().getLocale()));
        prats.add( getJobPriority( pageable.getNumberOfPages(),copies,withDialog ));

        //

        print( job,prats,withDialog,false );
    }    // print

    /**
     * Descripción de Método
     *
     *
     * @param pageable
     * @param prats
     */

    static public void print( Pageable pageable,PrintRequestAttributeSet prats ) {
        PrinterJob job = CPrinter.getPrinterJob();

        job.setPageable( pageable );
        print( job,prats,true,false );
    }    // print

    /**
     * Descripción de Método
     *
     *
     * @param job
     * @param prats
     * @param withDialog
     * @param waitForIt
     */

    static public void print( final PrinterJob job,final PrintRequestAttributeSet prats,boolean withDialog,boolean waitForIt ) {
        if( job == null ) {
            return;
        }

        boolean printed = true;

        if( withDialog ) {
            printed = job.printDialog( prats );
        }

        if( printed ) {
            if( withDialog ) {
                Attribute[] atts = prats.toArray();

                for( int i = 0;i < atts.length;i++ ) {
                    log.fine( atts[ i ].getName() + "=" + atts[ i ] );
                }
            }

            //

            if( waitForIt ) {
                log.fine( "(wait) " + job.getPrintService());

                try {
                    job.print( prats );
                } catch( Exception ex ) {
                    log.log( Level.SEVERE,"(wait)",ex );
                }
            } else    // Async
            {

                // Create Thread

                Thread printThread = new Thread() {
                    public void run() {
                        log.fine( "print: " + job.getPrintService());

                        try {
                            job.print( prats );
                        } catch( Exception ex ) {
                            log.log( Level.SEVERE,"print",ex );
                        }
                    }
                };

                printThread.start();
            }    // Async
        }        // printed
    }            // printAsync

    /**
     * Descripción de Método
     *
     *
     * @param pages
     * @param copies
     * @param withDialog
     *
     * @return
     */

    static public JobPriority getJobPriority( int pages,int copies,boolean withDialog ) {

        // Set priority (the more pages, the lower the priority)

        int priority = copies * pages;

        if( withDialog ) {    // prefer direct print
            priority *= 2;
        }

        priority = 100 - priority;    // convert to 1-100 supported range

        if( priority < 10 ) {
            priority = 10;
        } else if( priority > 100 ) {
            priority = 100;
        }

        return new JobPriority( priority );
    }    // getJobPriority

    /**
     * Descripción de Método
     *
     *
     * @param job
     */

    public static void dump( PrinterJob job ) {
        StringBuffer sb = new StringBuffer( job.getJobName());

        sb.append( "/" ).append( job.getUserName()).append( " Service=" ).append( job.getPrintService().getName()).append( " Copies=" ).append( job.getCopies());

        PageFormat pf = job.defaultPage();

        sb.append( " DefaultPage " ).append( "x=" ).append( pf.getImageableX()).append( ",y=" ).append( pf.getImageableY()).append( " w=" ).append( pf.getImageableWidth()).append( ",h=" ).append( pf.getImageableHeight());
        System.out.println( sb.toString());
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param psas
     */

    public static void dump( PrintServiceAttributeSet psas ) {
        System.out.println( "PrintServiceAttributeSet - length=" + psas.size());

        Attribute[] ats = psas.toArray();

        for( int i = 0;i < ats.length;i++ ) {
            System.out.println( ats[ i ].getName() + " = " + ats[ i ] + "  (" + ats[ i ].getCategory() + ")" );
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param prats
     */

    public static void dump( PrintRequestAttributeSet prats ) {
        System.out.println( "PrintRequestAttributeSet - length=" + prats.size());

        Attribute[] ats = prats.toArray();

        for( int i = 0;i < ats.length;i++ ) {
            System.out.println( ats[ i ].getName() + " = " + ats[ i ] + "  (" + ats[ i ].getCategory() + ")" );
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param docFlavor
     * @param outputMimeType
     */

    public static void dump( DocFlavor docFlavor,String outputMimeType ) {
        System.out.println();
        System.out.println( "DocFlavor=" + docFlavor + ", Output=" + outputMimeType );

        StreamPrintServiceFactory[] spsfactories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories( docFlavor,outputMimeType );

        for( int i = 0;i < spsfactories.length;i++ ) {
            System.out.println( "- " + spsfactories[ i ] );

            DocFlavor dfs[] = spsfactories[ i ].getSupportedDocFlavors();

            for( int j = 0;j < dfs.length;j++ ) {
                System.out.println( "   -> " + dfs[ j ] );
            }
        }
    }    // dump

    /**
     * Descripción de Método
     *
     *
     * @param docFlavor
     */

    public static void dump( DocFlavor docFlavor ) {
        System.out.println();
        System.out.println( "DocFlavor=" + docFlavor );

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrintService[]           pss  = PrintServiceLookup.lookupPrintServices( docFlavor,pras );

        for( int i = 0;i < pss.length;i++ ) {
            PrintService ps = pss[ i ];

            System.out.println( "- " + ps );
            System.out.println( "  Factory=" + ps.getServiceUIFactory());

            ServiceUIFactory uiF = pss[ i ].getServiceUIFactory();

            if( uiF != null ) {
                System.out.println( "about" );

                JDialog about = ( JDialog )uiF.getUI( ServiceUIFactory.ABOUT_UIROLE,ServiceUIFactory.JDIALOG_UI );

                about.setVisible(true);
                System.out.println( "admin" );

                JDialog admin = ( JDialog )uiF.getUI( ServiceUIFactory.ADMIN_UIROLE,ServiceUIFactory.JDIALOG_UI );

                admin.setVisible(true);
                System.out.println( "main" );

                JDialog main = ( JDialog )uiF.getUI( ServiceUIFactory.MAIN_UIROLE,ServiceUIFactory.JDIALOG_UI );

                main.setVisible(true);
                System.out.println( "reserved" );

                JDialog res = ( JDialog )uiF.getUI( ServiceUIFactory.RESERVED_UIROLE,ServiceUIFactory.JDIALOG_UI );

                res.setVisible(true);
            }

            //

            DocFlavor dfs[] = pss[ i ].getSupportedDocFlavors();

            System.out.println( "  - Supported Doc Flavors" );

            for( int j = 0;j < dfs.length;j++ ) {
                System.out.println( "    -> " + dfs[ j ] );
            }

            //

            Class[] attCat = pss[ i ].getSupportedAttributeCategories();

            System.out.println( "  - Supported Attribute Categories" );

            for( int j = 0;j < attCat.length;j++ ) {
                System.out.println( "    -> " + attCat[ j ].getName() + " = " + pss[ i ].getDefaultAttributeValue( attCat[ j ] ));
            }

            //

        }
    }    // dump

    /**
     * Descripción de Método
     *
     */

    private static void testPS() {
        PrintService     ps      = getDefaultPrintService();
        ServiceUIFactory factory = ps.getServiceUIFactory();

        System.out.println( factory );

        if( factory != null ) {
            System.out.println( "Factory" );

            JPanel p0 = ( JPanel )factory.getUI( ServiceUIFactory.ABOUT_UIROLE,ServiceUIFactory.JDIALOG_UI );

            p0.setVisible( true );

            JPanel p1 = ( JPanel )factory.getUI( ServiceUIFactory.ADMIN_UIROLE,ServiceUIFactory.JDIALOG_UI );

            p1.setVisible( true );

            JPanel p2 = ( JPanel )factory.getUI( ServiceUIFactory.MAIN_UIROLE,ServiceUIFactory.JDIALOG_UI );

            p2.setVisible( true );
        }

        System.out.println( "1----------" );

        PrinterJob               pj     = PrinterJob.getPrinterJob();
        PrintRequestAttributeSet pratts = getDefaultPrintRequestAttributes();

        // Page Dialog

        PageFormat pf = pj.pageDialog( pratts );

        System.out.println( "Pratts Size = " + pratts.size());

        Attribute[] atts = pratts.toArray();

        for( int i = 0;i < atts.length;i++ ) {
            System.out.println( atts[ i ].getName() + " = " + atts[ i ] + " - " + atts[ i ].getCategory());
        }

        System.out.println( "PageFormat h=" + pf.getHeight() + ",w=" + pf.getWidth() + " - x=" + pf.getImageableX() + ",y=" + pf.getImageableY() + " - ih=" + pf.getImageableHeight() + ",iw=" + pf.getImageableWidth() + " - Orient=" + pf.getOrientation());
        ps = pj.getPrintService();
        System.out.println( "PrintService = " + ps.getName());

        // Print Dialog

        System.out.println( "2----------" );
        pj.printDialog( pratts );
        System.out.println( "Pratts Size = " + pratts.size());
        atts = pratts.toArray();

        for( int i = 0;i < atts.length;i++ ) {
            System.out.println( atts[ i ].getName() + " = " + atts[ i ] + " - " + atts[ i ].getCategory());
        }

        pf = pj.defaultPage();
        System.out.println( "PageFormat h=" + pf.getHeight() + ",w=" + pf.getWidth() + " - x=" + pf.getImageableX() + ",y=" + pf.getImageableY() + " - ih=" + pf.getImageableHeight() + ",iw=" + pf.getImageableWidth() + " - Orient=" + pf.getOrientation());
        ps = pj.getPrintService();
        System.out.println( "PrintService= " + ps.getName());
        System.out.println( "3----------" );

        try {
            pj.setPrintService( ps );
        } catch( PrinterException pe ) {
            System.out.println( pe );
        }

        pf = pj.validatePage( pf );
        System.out.println( "PageFormat h=" + pf.getHeight() + ",w=" + pf.getWidth() + " - x=" + pf.getImageableX() + ",y=" + pf.getImageableY() + " - ih=" + pf.getImageableHeight() + ",iw=" + pf.getImageableWidth() + " - Orient=" + pf.getOrientation());
        ps = pj.getPrintService();
        System.out.println( "PrintService= " + ps.getName());
        System.out.println( "4----------" );
        pj.printDialog();
    }    // testPS

    /**
     * Descripción de Método
     *
     */

    private static void testSPS() {

        // dump (DocFlavor.INPUT_STREAM.GIF, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType());
        // dump (DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType());
        // dump (DocFlavor.INPUT_STREAM.GIF, DocFlavor.BYTE_ARRAY.PDF.getMimeType());
        // dump (DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.BYTE_ARRAY.GIF.getMediaSubtype());
        // dump (DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.BYTE_ARRAY.JPEG.getMediaSubtype());

        // dump (DocFlavor.SERVICE_FORMATTED.PAGEABLE);                                    //      lists devices able to output pageable
        // dump (DocFlavor.SERVICE_FORMATTED.PRINTABLE);
        // dump (DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST);
        // dump (DocFlavor.INPUT_STREAM.POSTSCRIPT);

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrintService[]           pss  = PrintServiceLookup.lookupPrintServices( DocFlavor.SERVICE_FORMATTED.PAGEABLE,pras );

        for( int i = 0;i < pss.length;i++ ) {
            PrintService ps   = pss[ i ];
            String       name = ps.getName();

            if( (name.indexOf( "PDF" ) != -1) || (name.indexOf( "Acrobat" ) != -1) ) {
                System.out.println( "----" );
                System.out.println( ps );

                Class[] cat = ps.getSupportedAttributeCategories();

                for( int j = 0;j < cat.length;j++ ) {
                    System.out.println( "- " + cat[ j ] );
                }
            }
        }

        // dump (null, DocFlavor.BYTE_ARRAY.PDF.getMimeType());                    //      lists PDF output
        // dump (null, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMediaType());    //      lists PS output

        // dump(null, null);

    }    // testSPS

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     */

    public static void setupPrintForm( int AD_Client_ID ) {
        log.config( "AD_Client_ID=" + AD_Client_ID );

        Properties ctx = Env.getCtx();

        CLogMgt.enable( false );

        //
        // Order Template

        int Order_PrintFormat_ID = MPrintFormat.copyToClient( ctx,100,AD_Client_ID ).getID();
        int OrderLine_PrintFormat_ID = MPrintFormat.copyToClient( ctx,101,AD_Client_ID ).getID();

        updatePrintFormatHeader( Order_PrintFormat_ID,OrderLine_PrintFormat_ID );

        // Invoice

        int Invoice_PrintFormat_ID = MPrintFormat.copyToClient( ctx,102,AD_Client_ID ).getID();
        int InvoiceLine_PrintFormat_ID = MPrintFormat.copyToClient( ctx,103,AD_Client_ID ).getID();

        updatePrintFormatHeader( Invoice_PrintFormat_ID,InvoiceLine_PrintFormat_ID );

        // Shipment

        int Shipment_PrintFormat_ID = MPrintFormat.copyToClient( ctx,104,AD_Client_ID ).getID();
        int ShipmentLine_PrintFormat_ID = MPrintFormat.copyToClient( ctx,105,AD_Client_ID ).getID();

        updatePrintFormatHeader( Shipment_PrintFormat_ID,ShipmentLine_PrintFormat_ID );

        // Check

        int Check_PrintFormat_ID = MPrintFormat.copyToClient( ctx,106,AD_Client_ID ).getID();
        int RemittanceLine_PrintFormat_ID = MPrintFormat.copyToClient( ctx,107,AD_Client_ID ).getID();

        updatePrintFormatHeader( Check_PrintFormat_ID,RemittanceLine_PrintFormat_ID );

        // Remittance

        int Remittance_PrintFormat_ID = MPrintFormat.copyToClient( ctx,108,AD_Client_ID ).getID();

        updatePrintFormatHeader( Remittance_PrintFormat_ID,RemittanceLine_PrintFormat_ID );

        // TODO: MPrintForm
        // MPrintForm form = new MPrintForm();

        int    AD_PrintForm_ID = DB.getNextID( AD_Client_ID,"AD_PrintForm",null );
        String sql             = "INSERT INTO AD_PrintForm(AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,AD_PrintForm_ID," + "Name,Order_PrintFormat_ID,Invoice_PrintFormat_ID,Remittance_PrintFormat_ID,Shipment_PrintFormat_ID)"

        //

        + " VALUES (" + AD_Client_ID + ",0,'Y',SysDate,0,SysDate,0," + AD_PrintForm_ID + "," + "'" + Msg.translate( ctx,"Standard" ) + "'," + Order_PrintFormat_ID + "," + Invoice_PrintFormat_ID + "," + Remittance_PrintFormat_ID + "," + Shipment_PrintFormat_ID + ")";
        int no = DB.executeUpdate( sql );

        if( no != 1 ) {
            log.log( Level.SEVERE,"PrintForm NOT inserted" );
        }

        //

        CLogMgt.enable( true );
    }    // createDocuments

    /**
     * Descripción de Método
     *
     *
     * @param Header_ID
     * @param Line_ID
     */

    static private void updatePrintFormatHeader( int Header_ID,int Line_ID ) {
        StringBuffer sb = new StringBuffer();

        sb.append( "UPDATE AD_PrintFormatItem SET AD_PrintFormatChild_ID=" ).append( Line_ID ).append( " WHERE AD_PrintFormatChild_ID IS NOT NULL AND AD_PrintFormat_ID=" ).append( Header_ID );

        int no = DB.executeUpdate( sb.toString());
    }    // updatePrintFormatHeader

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {

        // org.openXpertya.OpenXpertya.startupClient();
        // setupPrintForm (11);
        // setupPrintForm (1000000);

        testPS();    // Print Services

        // testSPS();      //      Stream Print Services

        // dumpSPS(null, null);

    }    // main
}    // PrintUtil



/*
 *  @(#)PrintUtil.java   23.03.06
 * 
 *  Fin del fichero PrintUtil.java
 *  
 *  Versión 2.2
 *
 */
