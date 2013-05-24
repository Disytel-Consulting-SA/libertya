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

import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import javax.print.DocFlavor;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import javax.xml.transform.stream.StreamResult;

import org.openXpertya.print.export.PrintDataExcelExporter;
import org.apache.ecs.XhtmlDocument;
import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.link;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.print.IHTMLExtension;
import org.openXpertya.print.MPrintFormatItem;
import org.openXpertya.print.PrintData;
import org.openXpertya.print.PrintDataElement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_C_DunningRunEntry;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_PaySelectionCheck;
import org.openXpertya.model.X_C_Project;
import org.openXpertya.model.X_C_RfQResponse;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.print.layout.LayoutEngine;
import org.openXpertya.print.pdf.text.Rectangle;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Language;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportEngine implements PrintServiceAttributeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param pf
     * @param query
     * @param info
     */

    public ReportEngine( Properties ctx,MPrintFormat pf,MQuery query,PrintInfo info ) {
        if( pf == null ) {
            throw new IllegalArgumentException( "ReportEngine - no PrintFormat" );
        }

        log.info( pf + " -- " + query );
        m_ctx = ctx;

        //

        m_printFormat = pf;
        m_info        = info;
        setQuery( query );    // loads Data
    }                         // ReportEngine

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ReportEngine.class );

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private MPrintFormat m_printFormat;

    /** Descripción de Campos */

    private PrintInfo m_info;

    /** Descripción de Campos */

    private MQuery m_query;

    /** Descripción de Campos */

    private PrintData m_printData;

    /** Descripción de Campos */

    private LayoutEngine m_layout = null;

    /** Descripción de Campos */

    private String m_printerName = Ini.getProperty( Ini.P_PRINTER );

    /** Descripción de Campos */

    private View m_view = null;

    private boolean m_summary = false;
    
    /**
     * Descripción de Método
     *
     *
     * @param pf
     */

    public void setPrintFormat( MPrintFormat pf )    // convertido a publico por ConSerTi
    {
        m_printFormat = pf;

        if( m_layout != null ) {
            setPrintData();
            m_layout.setPrintFormat( pf,false );
            m_layout.setPrintData( m_printData,m_query,true );    // format changes data
        }

        if( m_view != null ) {
            m_view.revalidate();
        }
    }    // setPrintFormat

    /**
     * Descripción de Método
     *
     *
     * @param query
     */

    public void setQuery( MQuery query ) {
        m_query = query;

        if( query == null ) {
            return;
        }

        //

        setPrintData();

        if( m_layout != null ) {
            m_layout.setPrintData( m_printData,m_query,true );
        }

        if( m_view != null ) {
            m_view.revalidate();
        }
    }    // setQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuery getQuery() {
        return m_query;
    }    // getQuery

    /**
     * Descripción de Método
     *
     */

    private void setPrintData() {
        if( m_query == null ) {
            return;
        }

        DataEngine de = new DataEngine( m_printFormat.getLanguage());

        setPrintData( de.getPrintData( m_ctx,m_printFormat,m_query ));

        // m_printData.dump();

    }    // setPrintData

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintData getPrintData() {
        return m_printData;
    }    // getPrintData

    /**
     * Descripción de Método
     *
     *
     * @param printData
     */

    public void setPrintData( PrintData printData ) {
        if( printData == null ) {
            return;
        }

        m_printData = printData;
    }    // setPrintData

    /**
     * Descripción de Método
     *
     */

    private void layout() {
        if( m_printFormat == null ) {
            throw new IllegalStateException( "ReportEngine.layout - no print format" );
        }

        if( m_printData == null ) {
            throw new IllegalStateException( "ReportEngine.layout - no print data (Delete Print Format and restart)" );
        }

        m_layout = new LayoutEngine( m_printFormat,m_printData,m_query );
    }    // layout

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public LayoutEngine getLayout() {
        if( m_layout == null ) {
            layout();
        }

        return m_layout;
    }    // getLayout

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_printFormat.getName();
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPrintFormat getPrintFormat() {
        return m_printFormat;
    }    // getPrintFormat

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintInfo getPrintInfo() {
        return m_info;
    }    // getPrintInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Properties getCtx() {
        return getLayout().getCtx();
    }    // getCtx

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getRowCount() {
        return m_printData.getRowCount();
    }    // getRowCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getColumnCount() {
        if( m_layout != null ) {
            return m_layout.getColumnCount();
        }

        return 0;
    }    // getColumnCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public View getView() {
        if( m_layout == null ) {
            layout();
        }

        if( m_view == null ) {
            m_view = new View( m_layout );
        }

        return m_view;
    }    // getView

    /**
     * Descripción de Método
     *
     */

    public void print() {
        log.info( m_info.toString());

        if( m_layout == null ) {
            layout();
        }

        // Paper Attributes:       media-printable-area, orientation-requested, media

        PrintRequestAttributeSet prats = m_layout.getPaper().getPrintRequestAttributeSet();

        // add:                            copies, job-name, priority

        if( m_info.isDocumentCopy() || (m_info.getCopies() < 1) ) {
            prats.add( new Copies( 1 ));
        } else {
            prats.add( new Copies( m_info.getCopies()));
        }

        Locale locale = Language.getLoginLanguage().getLocale();

        prats.add( new JobName( m_printFormat.getName(),locale ));
        prats.add( PrintUtil.getJobPriority( m_layout.getNumberOfPages(),m_info.getCopies(),true ));

        try {

            // PrinterJob

            PrinterJob job = getPrinterJob( m_info.getPrinterName());

            // job.getPrintService().addPrintServiceAttributeListener(this);

            job.setPageable( m_layout.getPageable( false ));    // no copy

            // Dialog

            try {
                if( m_info.isWithDialog() &&!job.printDialog( prats )) {
                    return;
                }
            } catch( Exception e ) {
                log.log( Level.WARNING,"Operating System Print Issue, check & try again",e );

                return;
            }

            // submit

            boolean printCopy = m_info.isDocumentCopy() && (m_info.getCopies() > 1);

            ArchiveEngine.get().archive( m_layout,m_info );
            PrintUtil.print( job,prats,false,printCopy );

            // Document: Print Copies

            if( printCopy ) {
                log.info( "Copy " + ( m_info.getCopies() - 1 ));
                prats.add( new Copies( m_info.getCopies() - 1 ));
                job = getPrinterJob( m_info.getPrinterName());

                // job.getPrintService().addPrintServiceAttributeListener(this);

                job.setPageable( m_layout.getPageable( true ));    // Copy
                PrintUtil.print( job,prats,false,false );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }                                                              // print

    /**
     * Descripción de Método
     *
     *
     * @param psae
     */

    public void attributeUpdate( PrintServiceAttributeEvent psae ) {
        log.fine( "attributeUpdate - " + psae );

        // PrintUtil.dump (psae.getAttributes());

    }    // attributeUpdate

    /**
     * Descripción de Método
     *
     *
     * @param printerName
     *
     * @return
     */

    private PrinterJob getPrinterJob( String printerName ) {
        if( (printerName != null) && (printerName.length() > 0) ) {
            return CPrinter.getPrinterJob( printerName );
        }

        return CPrinter.getPrinterJob( m_printerName );
    }    // getPrinterJob

    /**
     * Descripción de Método
     *
     */

    public void pageSetupDialog() {
        if( m_layout == null ) {
            layout();
        }

        m_layout.pageSetupDialog( getPrinterJob( m_printerName ));

        if( m_view != null ) {
            m_view.revalidate();
        }
    }    // pageSetupDialog

    /**
     * Descripción de Método
     *
     *
     * @param printerName
     */

    public void setPrinterName( String printerName ) {
        if( printerName == null ) {
            m_printerName = Ini.getProperty( Ini.P_PRINTER );
        } else {
            m_printerName = printerName;
        }
    }    // setPrinterName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPrinterName() {
        return m_printerName;
    }    // getPrinterName

    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param onlyTable
     * @param language
     *
     * @return
     */

    public boolean createHTML( File file,boolean onlyTable,Language language ) {
        try {
            Language lang = language;

            if( lang == null ) {
                lang = Language.getLoginLanguage();
            }

            FileWriter fw = new FileWriter( file,false );

            return createHTML( new BufferedWriter( fw ),onlyTable,lang );
        } catch( FileNotFoundException fnfe ) {
            log.log( Level.SEVERE,"(f) - " + fnfe.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(f)",e );
        }

        return false;
    }    // createHTML

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param onlyTable
     * @param language
     *
     * @return
     */

    public boolean createHTML( Writer writer,boolean onlyTable,Language language ) {
        try {
            table table = new table();

            //
            // for all rows (-1 = header row)

            for( int row = -1;row < m_printData.getRowCount();row++ ) {
                tr tr = new tr();

                table.addElement( tr );

                if( row != -1 ) {
                    m_printData.setRowIndex( row );
                }

                // for all columns

                for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                    MPrintFormatItem item = m_printFormat.getItem( col );

                    if( item.isPrinted()) {

                        // header row

                        if( row == -1 ) {
                            th th = new th();

                            tr.addElement( th );
                            th.addElement( Util.maskHTML( item.getPrintName( language )));
                        } else {
                            td td = new td();

                            tr.addElement( td );

                            Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));

                            if( obj == null ) {
                                td.addElement( "&nbsp;" );
                            } else if( obj instanceof PrintDataElement ) {
                                String value = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted

                                td.addElement( Util.maskHTML( value ));
                            } else if( obj instanceof PrintData ) {

                                // ignore contained Data

                            } else {
                                log.log( Level.SEVERE,"createHTML - Element not PrintData(Element) " + obj.getClass());
                            }
                        }
                    }    // printed
                }        // for all columns
            }            // for all rows

            //

            PrintWriter w = new PrintWriter( writer );

            if( onlyTable ) {
                table.output( w );
            } else {
                XhtmlDocument doc = new XhtmlDocument();

                doc.appendBody( table );
                doc.output( w );
            }

            w.flush();
            w.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createHTML(w)",e );
        }

        return false;
    }    // createHTML

	/**************************************************************************
	 * 	Create HTML File
	 * 	@param file file
	 *  @param onlyTable if false create complete HTML document
	 *  @param language optional language - if null the default language is used to format nubers/dates
	 *  @param extension optional extension for html output
	 * 	@return true if success
	 */
	public boolean createHTML (File file, boolean onlyTable, Language language, IHTMLExtension extension)
	{
		try
		{
			Language lang = language;
			if (lang == null)
				lang = Language.getLoginLanguage();
			Writer fw = new OutputStreamWriter(new FileOutputStream(file, false), Ini.getCharset()); // teo_sarca: save using adempiere charset [ 1658127 ]
			return createHTML (new BufferedWriter(fw), onlyTable, lang, extension);
		}
		catch (FileNotFoundException fnfe)
		{
			log.log(Level.SEVERE, "(f) - " + fnfe.toString());
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "(f)", e);
		}
		return false;
	}	//	createHTML
    
    
	/**
	 * 	Write HTML to writer
	 * 	@param writer writer
	 *  @param onlyTable if false create complete HTML document
	 *  @param language optional language - if null numbers/dates are not formatted
	 *  @param extension optional extension for html output
	 * 	@return true if success
	 */
	public boolean createHTML (Writer writer, boolean onlyTable, Language language, IHTMLExtension extension)
	{
		try
		{
			String cssPrefix = extension != null ? extension.getClassPrefix() : null;
			if (cssPrefix != null && cssPrefix.trim().length() == 0)
				cssPrefix = null;
			
			table table = new table();
			if (cssPrefix != null)
				table.setClass(cssPrefix + "-table");
			//
			//	for all rows (-1 = header row)
			for (int row = -1; row < m_printData.getRowCount(); row++)
			{
				tr tr = new tr();
				table.addElement(tr);
				if (row != -1)
				{
					m_printData.setRowIndex(row);					
					if (extension != null)
					{
						extension.extendRowElement(tr, m_printData);
					}
				}
				//	for all columns
				for (int col = 0; col < m_printFormat.getItemCount(); col++)
				{
					MPrintFormatItem item = m_printFormat.getItem(col);
					if (item.isPrinted())
					{
						//	header row
						if (row == -1)
						{
							th th = new th();
							tr.addElement(th);
							th.addElement(Util.maskHTML(item.getPrintName(language)));
						}
						else
						{
							td td = new td();
							tr.addElement(td);
							Object obj = m_printData.getNode(new Integer(item.getAD_Column_ID()));
							if (obj == null)
								td.addElement("&nbsp;");
							else if (obj instanceof PrintDataElement)
							{
								PrintDataElement pde = (PrintDataElement) obj;
								String value = pde.getValueDisplay(language);	//	formatted
								if (pde.getColumnName().endsWith("_ID") && extension != null)
								{
									//link for column
									a href = new a("javascript:void(0)");									
									href.setID(pde.getColumnName() + "_" + row + "_a");									
									td.addElement(href);
									href.addElement(Util.maskHTML(value));
									if (cssPrefix != null)
										href.setClass(cssPrefix + "-href");
									
									extension.extendIDColumn(row, td, href, pde);
																											
								}
								else
								{
									td.addElement(Util.maskHTML(value));
								}
								if (cssPrefix != null)
								{
									if (DisplayType.isNumeric(pde.getDisplayType()))
										td.setClass(cssPrefix + "-number");
									else if (DisplayType.isDate(pde.getDisplayType()))
										td.setClass(cssPrefix + "-date");
									else
										td.setClass(cssPrefix + "-text");
								}								
							}
							else if (obj instanceof PrintData)
							{
								//	ignore contained Data
							}
							else
								log.log(Level.SEVERE, "Element not PrintData(Element) " + obj.getClass());
						}
					}	//	printed
				}	//	for all columns
			}	//	for all rows

			//
			PrintWriter w = new PrintWriter(writer);
			if (onlyTable)
				table.output(w);
			else
			{
				XhtmlDocument doc = new XhtmlDocument();
				doc.appendBody(table);
				if (extension.getStyleURL() != null)
				{
					link l = new link(extension.getStyleURL(), "stylesheet", "text/css");
					doc.appendHead(l);					
				}
				if (extension.getScriptURL() != null)
				{
					script jslink = new script();
					jslink.setLanguage("javascript");
					jslink.setSrc(extension.getScriptURL());
					doc.appendHead(jslink);
				}
				doc.output(w);
			}
			w.flush();
			w.close();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "(w)", e);
		}
		return false;
	}	//	createHTML
    
    /**
     * Descripción de Método
     *
     *
     * @param file
     * @param delimiter
     * @param language
     *
     * @return
     */

    public boolean createCSV( File file,char delimiter,Language language ) {
        try {
            FileWriter fw = new FileWriter( file,false );

            return createCSV( new BufferedWriter( fw ),delimiter,language );
        } catch( FileNotFoundException fnfe ) {
            log.log( Level.SEVERE,"(f) - " + fnfe.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(f)",e );
        }

        return false;
    }    // createCSV

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param delimiter
     * @param language
     *
     * @return
     */

    public boolean createCSV( Writer writer,char delimiter,Language language ) {
        if( delimiter == 0 ) {
            delimiter = '\t';
        }

        try {

            // for all rows (-1 = header row)

            for( int row = -1;row < m_printData.getRowCount();row++ ) {
                StringBuffer sb = new StringBuffer();

                if( row != -1 ) {
                    m_printData.setRowIndex( row );
                }

                // for all columns

                boolean first = true;    // first column to print

                for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                    MPrintFormatItem item = m_printFormat.getItem( col );

                    if( item.isPrinted()) {

                        // column delimiter (comma or tab)

                        if( first ) {
                            first = false;
                        } else {
                            sb.append( delimiter );
                        }

                        // header row

                        if( row == -1 ) {
                            createCSVvalue( sb,delimiter,m_printFormat.getItem( col ).getPrintName( language ));
                        } else {
                            Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                            String data = "";

                            if( obj == null ) {
                                ;
                            } else if( obj instanceof PrintDataElement ) {
                                data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            } else if( obj instanceof PrintData ) {}
                            else {
                                log.log( Level.SEVERE,"createCSV - Element not PrintData(Element) " + obj.getClass());
                            }

                            createCSVvalue( sb,delimiter,data );
                        }
                    }    // printed
                }        // for all columns

                writer.write( sb.toString());
                writer.write( Env.NL );
            }            // for all rows

            //

            writer.flush();
            writer.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createCSV(w)",e );
        }

        return false;
    }    // createCSV

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param delimiter
     * @param content
     */

    private void createCSVvalue( StringBuffer sb,char delimiter,String content ) {

        // nothing to add

        if( (content == null) || (content.length() == 0) ) {
            return;
        }

        //

        boolean      needMask = false;
        StringBuffer buff     = new StringBuffer();
        char         chars[]  = content.toCharArray();

        for( int i = 0;i < chars.length;i++ ) {
            char c = chars[ i ];

            if( c == '"' ) {
                needMask = true;
                buff.append( c );    // repeat twice
            }                        // mask if any control character
                    else if( !needMask && ( (c == delimiter) ||!Character.isLetterOrDigit( c ))) {
                needMask = true;
            }

            buff.append( c );
        }

        // Optionally mask value

        if( needMask ) {
            sb.append( '"' ).append( buff ).append( '"' );
        } else {
            sb.append( buff );
        }
    }    // addCSVColumnValue

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public boolean createXML( File file ) {
        try {
            FileWriter fw = new FileWriter( file,false );

            return createXML( new BufferedWriter( fw ));
        } catch( FileNotFoundException fnfe ) {
            log.log( Level.SEVERE,"createXML(f) - " + fnfe.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createXML(f)",e );
        }

        return false;
    }    // createXML

    /**
     * Descripción de Método
     *
     *
     * @param writer
     *
     * @return
     */

    public boolean createXML( Writer writer ) {
        try {
            m_printData.createXML( new StreamResult( writer ));
            writer.flush();
            writer.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createXML(w)",e );
        }

        return false;
    }    // createXML

    
	/**
	 * Create Excel file
	 * @param outFile output file
	 * @param language
	 * @throws Exception if error
	 */
	public void createXLS(File outFile, Language language)
	throws Exception
	{
		PrintDataExcelExporter exp = new PrintDataExcelExporter(getPrintData(), getPrintFormat());
		exp.export(outFile, language);
	}

    
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public File getPDF() {
        return getPDF( null );
    }    // getPDF

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public File getPDF( File file ) {
        try {
            if( file == null ) {
                file = File.createTempFile( "ReportEngine",".pdf" );
            }
        } catch( IOException e ) {
            log.log( Level.SEVERE,"",e );
        }

        if( createPDF( file )) {
            return file;
        }

        return null;
    }    // getPDF

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public boolean createPDF( File file ) {
        String fileName = null;
        URI    uri      = null;

        try {
            if( file == null ) {
                file = File.createTempFile( "ReportEngine",".pdf" );
            }

            fileName = file.getAbsolutePath();
            uri      = file.toURI();

            if( file.exists()) {
                file.delete();
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"file",e );

            return false;
        }

        log.fine( uri.toString());

        try {
            if( m_layout == null ) {
                layout();
            }

            ArchiveEngine.get().archive( m_layout,m_info );

            org.openXpertya.print.pdf.text.Document      document = null;
            org.openXpertya.print.pdf.text.pdf.PdfWriter writer   = null;

            if( m_layout.getPageable( false ) instanceof LayoutEngine ) {
                LayoutEngine layoutengine = ( LayoutEngine )( m_layout.getPageable( false ));
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
                        document.addTitle( m_info.getName());
                        document.addAuthor( "openXpertya" );
                        document.addSubject( "openXpertya" + m_info.getDescription());
                        document.addKeywords( "openXpertya" );
                        document.addCreator( "openXpertya" );

                        // Paso 2: se crea el acceso al documento

                        writer = org.openXpertya.print.pdf.text.pdf.PdfWriter.getInstance( document,new FileOutputStream( fileName ));

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
        } catch( Exception e ) {
            log.log( Level.SEVERE,"PDF",e );

            return false;
        }

        File file2 = new File( fileName );

        log.info( file2.getAbsolutePath() + " - " + file2.length());

        return file2.exists();
    }    // createPDF

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public byte[] createPDFData() {
        try {
            if( m_layout == null ) {
                layout();
            }

            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            org.openXpertya.print.pdf.text.Document      document = null;
            org.openXpertya.print.pdf.text.pdf.PdfWriter writer   = null;

            try {
                if( m_layout.getPageable( false ) instanceof LayoutEngine ) {
                    LayoutEngine layoutengine = ( LayoutEngine )( m_layout.getPageable( false ));
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

            return bytearrayoutputstream.toByteArray();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"PDF",e );
        }

        return null;
    }    // createPDFData

    /**
     * Descripción de Método
     *
     *
     * @param file
     *
     * @return
     */

    public boolean createPS( File file ) {
        try {
            return createPS( new FileOutputStream( file ));
        } catch( FileNotFoundException fnfe ) {
            log.log( Level.SEVERE,"createPS(f) - " + fnfe.toString());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createPS(f)",e );
        }

        return false;
    }    // createPS

	/**
	 * 	Write PostScript to writer
	 * 	@param os output stream
	 * 	@return true if success
	 */
	public boolean createPS (OutputStream os)
	{
		try
		{
			String outputMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();
			DocFlavor docFlavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
			StreamPrintServiceFactory[] spsfactories =
				StreamPrintServiceFactory.lookupStreamPrintServiceFactories(docFlavor, outputMimeType);
			if (spsfactories.length == 0)
			{
				log.log(Level.SEVERE, "(fos) - No StreamPrintService");
				return false;
			}
			//	just use first one - sun.print.PSStreamPrinterFactory
			//	System.out.println("- " + spsfactories[0]);
			StreamPrintService sps = spsfactories[0].getPrintService(os);
			//	get format
			if (m_layout == null)
				layout();
			//	print it
			sps.createPrintJob().print(m_layout.getPageable(false), 
				new HashPrintRequestAttributeSet());
			//
			os.flush();
			//following 2 line for backward compatibility
			if (os instanceof FileOutputStream)
				((FileOutputStream)os).close();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "(fos)", e);
		}
		return false;
	}	//	createPS

	
    
    /**
     * Descripción de Método
     *
     *
     * @param fos
     *
     * @return
     */

    public boolean createPS( FileOutputStream fos ) {
        try {
            String outputMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();
            DocFlavor                   docFlavor    = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
            StreamPrintServiceFactory[] spsfactories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories( docFlavor,outputMimeType );

            if( spsfactories.length == 0 ) {
                log.log( Level.SEVERE,"createPS(fos) - No StreamPrintService" );

                return false;
            }

            // just use first one - sun.print.PSStreamPrinterFactory
            // System.out.println("- " + spsfactories[0]);

            StreamPrintService sps = spsfactories[ 0 ].getPrintService( fos );

            // get format

            if( m_layout == null ) {
                layout();
            }

            // print it

            sps.createPrintJob().print( m_layout.getPageable( false ),new HashPrintRequestAttributeSet());

            //

            fos.flush();
            fos.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createPS(fos)",e );
        }

        return false;
    }    // createPS

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param pi
     *
     * @return
     */

    static public ReportEngine get( Properties ctx,ProcessInfo pi ) {
        int AD_Client_ID = Env.getAD_Client_ID( ctx );

        //

        int     AD_Table_ID       = 0;
        int     AD_ReportView_ID  = 0;
        String  TableName         = null;
        String  whereClause       = "";
        int     AD_PrintFormat_ID = 0;
        boolean IsForm            = false;
        int     Client_ID         = -1;

        // Get AD_Table_ID and TableName

        String sql = "SELECT rv.AD_ReportView_ID,rv.WhereClause," + " t.AD_Table_ID,t.TableName, pf.AD_PrintFormat_ID, pf.IsForm, pf.AD_Client_ID " + "FROM AD_PInstance pi" + " INNER JOIN AD_Process p ON (pi.AD_Process_ID=p.AD_Process_ID)" + " INNER JOIN AD_ReportView rv ON (p.AD_ReportView_ID=rv.AD_ReportView_ID)" + " INNER JOIN AD_Table t ON (rv.AD_Table_ID=t.AD_Table_ID)" + " LEFT OUTER JOIN AD_PrintFormat pf ON (p.AD_ReportView_ID=pf.AD_ReportView_ID AND pf.AD_Client_ID IN (0,?)) " + "WHERE pi.AD_PInstance_ID=? "    // #2
                     + "ORDER BY pf.AD_Client_ID DESC, pf.IsDefault DESC";    // own first

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setInt( 2,pi.getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            // Just get first

            if( rs.next()) {
                AD_ReportView_ID = rs.getInt( 1 );    // required
                whereClause      = rs.getString( 2 );

                if( rs.wasNull()) {
                    whereClause = "";
                }

                //

                AD_Table_ID       = rs.getInt( 3 );
                TableName         = rs.getString( 4 );                 // required for query
                AD_PrintFormat_ID = rs.getInt( 5 );                    // required
                IsForm            = "Y".equals( rs.getString( 6 ));    // required
                Client_ID         = rs.getInt( 7 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"get(1)",e1 );
        }

        // Nothing found
// Comentado: 	en realidad si el proceso tiene definido un formato de impresión deberá utilizar  
//	        	dicho formato en lugar de la vista (pero respetando el where de dicha vista)
//        if( AD_ReportView_ID == 0 ) {

            // Check Print format in Report Directly

            sql = "SELECT t.AD_Table_ID,t.TableName, pf.AD_PrintFormat_ID, pf.IsForm " + "FROM AD_PInstance pi" + " INNER JOIN AD_Process p ON (pi.AD_Process_ID=p.AD_Process_ID)" + " INNER JOIN AD_PrintFormat pf ON (p.AD_PrintFormat_ID=pf.AD_PrintFormat_ID)" + " INNER JOIN AD_Table t ON (pf.AD_Table_ID=t.AD_Table_ID) " + "WHERE pi.AD_PInstance_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,pi.getAD_PInstance_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
//                    whereClause       = "";  // basarse en el del reportView si es que éste lo tiene
                    AD_Table_ID       = rs.getInt( 1 );
                    TableName         = rs.getString( 2 );                 // required for query
                    AD_PrintFormat_ID = rs.getInt( 3 );                    // required
                    IsForm            = "Y".equals( rs.getString( 4 ));    // required
                    Client_ID         = AD_Client_ID;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e1 ) {
                log.log( Level.SEVERE,"get(2)",e1 );
            }

            if( AD_PrintFormat_ID == 0 && AD_ReportView_ID == 0) {
                log.log( Level.SEVERE,"Report Info NOT found AD_PInstance_ID=" + pi.getAD_PInstance_ID() + ",AD_Client_ID=" + AD_Client_ID );

                return null;
            }
//        }

        // Create Query from Parameters

        MQuery query = null;

        if( IsForm && (pi.getRecord_ID() != 0) ) {    // Form = one record
            query = MQuery.getEqualQuery( TableName + "_ID",pi.getRecord_ID());
        } else {
            query = MQuery.get( ctx,pi.getAD_PInstance_ID(),TableName );
        }

        // Add to static where clause from ReportView

        if( whereClause.length() != 0 ) {
            query.addRestriction( whereClause );
        }

        // Get PrintFormat

        MPrintFormat format = null;

        if( AD_PrintFormat_ID != 0 ) {

            // We have a PrintFormat with the correct Client

        	// Se deshabilita la copia del formato cuando éste pertenece a la compañía System
            if( Client_ID == AD_Client_ID || Client_ID == 0) {
                format = MPrintFormat.get( ctx,AD_PrintFormat_ID,false );
            } else {
                format = MPrintFormat.copyToClient( ctx,AD_PrintFormat_ID,AD_Client_ID );
            }
        }

        if( (format != null) && (format.getItemCount() == 0) ) {
            log.info( "No Items - recreating:  " + format );
            format.delete( true );
            format = null;
        }

        // Create it

        if( (format == null) && (AD_ReportView_ID != 0) ) {
            format = MPrintFormat.createFromReportView( ctx,AD_ReportView_ID,pi.getTitle());
        }

        if( format == null ) {
            return null;
        }

        //

        PrintInfo info = new PrintInfo( pi );

        info.setAD_Table_ID( AD_Table_ID );

        return new ReportEngine( ctx,format,query,info );
    }    // get

	/** Order = 0				*/
	public static final int		ORDER = 0;
	/** Shipment = 1				*/
	public static final int		SHIPMENT = 1;
	/** Invoice = 2				*/
	public static final int		INVOICE = 2;
	/** Project = 3				*/
	public static final int		PROJECT = 3;
	/** RfQ = 4					*/
	public static final int		RFQ = 4;
	/** Remittance = 5			*/
	public static final int		REMITTANCE = 5;
	/** Check = 6				*/
	public static final int		CHECK = 6;
	/** Dunning = 7				*/
	public static final int		DUNNING = 7;    
	/** Manufacturing Order = 8  */
	public static final int		MANUFACTURING_ORDER = 8;
	/** Distribution Order = 9  */
	public static final int		DISTRIBUTION_ORDER = 9;


    /** Descripción de Campos */

    private static final String[] DOC_TABLES = new String[] {
        "C_Order_Header_v","M_InOut_Header_v","C_Invoice_Header_v","C_Project_Header_v","C_RfQResponse_v","C_PaySelection_Check_v","C_PaySelection_Check_v","C_DunningRunEntry_v"
    };

    /** Descripción de Campos */

    private static final String[] DOC_BASETABLES = new String[] {
        "C_Order","M_InOut","C_Invoice","C_Project","C_RfQResponse","C_PaySelectionCheck","C_PaySelectionCheck","C_DunningRunEntry"
    };

    /** Descripción de Campos */

    private static final String[] DOC_IDS = new String[] {
        "C_Order_ID","M_InOut_ID","C_Invoice_ID","C_Project_ID","C_RfQResponse_ID","C_PaySelectionCheck_ID","C_PaySelectionCheck_ID","C_DunningRunEntry_ID"
    };

    /** Descripción de Campos */

    private static final int[] DOC_TABLE_ID = new int[] {
        X_C_Order.Table_ID,X_M_InOut.Table_ID,X_C_Invoice.Table_ID,X_C_Project.Table_ID,X_C_RfQResponse.Table_ID,X_C_PaySelectionCheck.Table_ID,X_C_PaySelectionCheck.Table_ID,X_C_DunningRunEntry.Table_ID
    };

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param type
     * @param Record_ID
     *
     * @return
     */

    public static ReportEngine get( Properties ctx,int type,int Record_ID ) {

        // Order - Print Shipment or Invoice

        if( type == ORDER ) {
            int[] what = getDocumentWhat( Record_ID );

            if( what != null ) {
                type      = what[ 0 ];
                Record_ID = what[ 1 ];
            }
        }    // Order

        //

        String JobName           = DOC_BASETABLES[ type ] + "_Print";
        int    AD_PrintFormat_ID = 0;
        int    C_BPartner_ID     = 0;
        String DocumentNo        = null;
        int    copies            = 1;

        // Language

        MClient  client   = MClient.get( ctx );
        Language language = client.getLanguage();

        // Get Document Info

        String sql = null;

        if( type == CHECK ) {
            sql = "SELECT bad.Check_PrintFormat_ID,"                                                 // 1
                  + "     c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.DocumentNo "    // 2..5
                  + "FROM C_PaySelectionCheck d" + " INNER JOIN C_PaySelection ps ON (d.C_PaySelection_ID=ps.C_PaySelection_ID)" + " INNER JOIN C_BankAccountDoc bad ON (ps.C_BankAccount_ID=bad.C_BankAccount_ID AND d.PaymentRule=bad.PaymentRule)" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_PaySelectionCheck_ID=?";    // info from BankAccount
        } else if( type == DUNNING ) {
            sql = "SELECT dl.Dunning_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,dr.DunningDate " + "FROM C_DunningRunEntry d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN C_DunningRun dr ON (d.C_DunningRun_ID=dr.C_DunningRun_ID)" + " INNER JOIN C_DunningLevel dl ON (dr.C_DunningLevel_ID=dr.C_DunningLevel_ID) " + "WHERE d.C_DunningRunEntry_ID=?";    // info from Dunning
        } else if( type == REMITTANCE ) {
            sql = "SELECT pf.Remittance_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.DocumentNo " + "FROM C_PaySelectionCheck d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_PaySelectionCheck_ID=?"    // info from PrintForm
                  + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) ORDER BY pf.AD_Org_ID DESC";
        } else if( type == PROJECT ) {
            sql = "SELECT pf.Project_PrintFormat_ID," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,d.Value " + "FROM C_Project d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " LEFT OUTER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID) " + "WHERE d.C_Project_ID=?"    // info from PrintForm
                  + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) ORDER BY pf.AD_Org_ID DESC";
        } else if( type == RFQ ) {
            sql = "SELECT COALESCE(t.AD_PrintFormat_ID, pf.AD_PrintFormat_ID)," + " c.IsMultiLingualDocument,bp.AD_Language,bp.C_BPartner_ID,rr.Name " + "FROM C_RfQResponse rr" + " INNER JOIN C_RfQ r ON (rr.C_RfQ_ID=r.C_RfQ_ID)" + " INNER JOIN C_RfQ_Topic t ON (r.C_RfQ_Topic_ID=t.C_RfQ_Topic_ID)" + " INNER JOIN AD_Client c ON (rr.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (rr.C_BPartner_ID=bp.C_BPartner_ID)," + " AD_PrintFormat pf " + "WHERE pf.AD_Client_ID IN (0,rr.AD_Client_ID)" + " AND pf.AD_Table_ID=725 AND pf.IsTableBased='N'"    // from RfQ PrintFormat
                  + " AND rr.C_RfQResponse_ID=? "    // Info from RfQTopic
                  + "ORDER BY t.AD_PrintFormat_ID, pf.AD_Client_ID DESC, pf.AD_Org_ID DESC";
        } else {                                                                  // Get PrintFormat from Org or 0 of document client
            sql = "SELECT pf.Order_PrintFormat_ID,pf.Shipment_PrintFormat_ID,"    // 1..2

            // Prio: 1. BPartner 2. DocType, 3. PrintFormat (Org)      //      see InvoicePrint

            + " COALESCE (bp.Invoice_PrintFormat_ID,dt.AD_PrintFormat_ID,pf.Invoice_PrintFormat_ID),"    // 3
            + " pf.Project_PrintFormat_ID, pf.Remittance_PrintFormat_ID,"    // 4..5
            + " c.IsMultiLingualDocument, bp.AD_Language,"                        // 6..7
            + " COALESCE(dt.DocumentCopies,0)+COALESCE(bp.DocumentCopies,1), "    // 8
            + " dt.AD_PrintFormat_ID,bp.C_BPartner_ID,d.DocumentNo "                                                                                                                                                                                                                                                                                                         // 9..11
            + "FROM " + DOC_BASETABLES[ type ] + " d" + " INNER JOIN AD_Client c ON (d.AD_Client_ID=c.AD_Client_ID)" + " INNER JOIN AD_PrintForm pf ON (c.AD_Client_ID=pf.AD_Client_ID)" + " INNER JOIN C_BPartner bp ON (d.C_BPartner_ID=bp.C_BPartner_ID)" + " LEFT OUTER JOIN C_DocType dt ON (d.C_DocType_ID=dt.C_DocType_ID) " + "WHERE d." + DOC_IDS[ type ] + "=?"    // info from PrintForm
            + " AND pf.AD_Org_ID IN (0,d.AD_Org_ID) " + "ORDER BY pf.AD_Org_ID DESC";
        }

        //

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,Record_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next())    // first record only
            {
                if( (type == CHECK) || (type == DUNNING) || (type == REMITTANCE) || (type == PROJECT) || (type == RFQ) ) {
                    AD_PrintFormat_ID = rs.getInt( 1 );
                    copies            = 1;

                    // Set Language when enabled

                    String AD_Language = rs.getString( 3 );

                    if( AD_Language != null ) {    // && "Y".equals(rs.getString(2)))      //      IsMultiLingualDocument
                        language = Language.getLanguage( AD_Language );
                    }

                    C_BPartner_ID = rs.getInt( 4 );

                    if( type == DUNNING ) {
                        Timestamp ts = rs.getTimestamp( 5 );

                        DocumentNo = ts.toString();
                    } else {
                        DocumentNo = rs.getString( 5 );
                    }
                } else {

                    // Set PrintFormat

                    AD_PrintFormat_ID = rs.getInt( type + 1 );

                    if( rs.getInt( 9 ) != 0 ) {    // C_DocType.AD_PrintFormat_ID
                        AD_PrintFormat_ID = rs.getInt( 9 );
                    }

                    copies = rs.getInt( 8 );

                    // Set Language when enabled

                    String AD_Language = rs.getString( 7 );

                    if( AD_Language != null ) {    // && "Y".equals(rs.getString(6)))     //      IsMultiLingualDocument
                        language = Language.getLanguage( AD_Language );
                    }

                    C_BPartner_ID = rs.getInt( 10 );
                    DocumentNo    = rs.getString( 11 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Record_ID=" + Record_ID + ", SQL=" + sql,e );
        }

        if( AD_PrintFormat_ID == 0 ) {
            log.log( Level.SEVERE,"No PrintFormat found for Type=" + type + ", Record_ID=" + Record_ID );

            return null;
        }

        // Get Format & Data

        MPrintFormat format = MPrintFormat.get( ctx,AD_PrintFormat_ID,false );

        format.setLanguage( language );    // BP Language if Multi-Lingual

        // if (!Env.isBaseLanguage(language, DOC_TABLES[type]))

        format.setTranslationLanguage( language );

        // query

        MQuery query = new MQuery( DOC_TABLES[ type ] );

        query.addRestriction( DOC_IDS[ type ],MQuery.EQUAL,new Integer( Record_ID ));

        // log.config( "ReportCtrl.startDocumentPrint - " + format, query + " - " + language.getAD_Language());
        //

        if( (DocumentNo == null) || (DocumentNo.length() == 0) ) {
            DocumentNo = "DocPrint";
        }

        PrintInfo info = new PrintInfo( DocumentNo,DOC_TABLE_ID[ type ],Record_ID,C_BPartner_ID );

        info.setCopies( copies );
        info.setDocumentCopy( false );    // true prints "Copy" on second
        info.setPrinterName( format.getPrinterName());

        // Engine

        ReportEngine re = new ReportEngine( ctx,format,query,info );

        return re;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     *
     * @return
     */

    private static int[] getDocumentWhat( int C_Order_ID ) {
        int[] what = new int[ 2 ];

        what[ 0 ] = ORDER;
        what[ 1 ] = C_Order_ID;

        //

        String SQL = "SELECT dt.DocSubTypeSO " + "FROM C_DocType dt, C_Order o " + "WHERE o.C_DocType_ID=dt.C_DocType_ID" + " AND o.C_Order_ID=?";
        String DocSubTypeSO = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                DocSubTypeSO = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"DocSubType",e1 );

            return null;    // error
        }

        if( DocSubTypeSO == null ) {
            DocSubTypeSO = "";
        }

        // WalkIn Receipt, WalkIn Invoice,

        if( DocSubTypeSO.equals( "WR" ) || DocSubTypeSO.equals( "WI" )) {
            what[ 0 ] = INVOICE;

            // WalkIn Pickup,

        } else if( DocSubTypeSO.equals( "WP" )) {
            what[ 0 ] = SHIPMENT;

            // Offer Binding, Offer Nonbinding, Standard Order

        } else {
            return what;
        }

        // Get Record_ID of Invoice/Receipt

        if( what[ 0 ] == INVOICE ) {
            SQL = "SELECT C_Invoice_ID REC FROM C_Invoice WHERE C_Order_ID=?"    // 1
                  + " ORDER BY C_Invoice_ID DESC";
        } else {
            SQL = "SELECT M_InOut_ID REC FROM M_InOut WHERE C_Order_ID=?"    // 1
                  + " ORDER BY M_InOut_ID DESC";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // if (i == 1 && ADialog.ask(0, null, what[0] == INVOICE ? "PrintOnlyRecentInvoice?" : "PrintOnlyRecentShipment?")) break;

                what[ 1 ] = rs.getInt( 1 );
            } else {    // No Document Found
                what[ 0 ] = ORDER;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e2 ) {
            log.log( Level.SEVERE,"Record_ID",e2 );

            return null;
        }

        log.fine( "Order => " + what[ 0 ] + " ID=" + what[ 1 ] );

        return what;
    }    // getDocumentWhat

    /**
     * Descripción de Método
     *
     *
     * @param type
     * @param Record_ID
     */

    public static void printConfirm( int type,int Record_ID ) {
        StringBuffer sql = new StringBuffer();

        if( (type == ORDER) || (type == SHIPMENT) || (type == INVOICE) ) {
            sql.append( "UPDATE " ).append( DOC_BASETABLES[ type ] ).append( " SET DatePrinted=SysDate, IsPrinted='Y' WHERE " ).append( DOC_IDS[ type ] ).append( "=" ).append( Record_ID );
        }

        //

        if( sql.length() > 0 ) {
            int no = DB.executeUpdate( sql.toString());

            if( no != 1 ) {
                log.log( Level.SEVERE,"Updated records=" + no + " - should be just one" );
            }
        }
    }    // printConfirm

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param language
     * @param prefijo
     * @param valor
     * @param sufijo
     */

    public void createLabelFile( Writer writer,Language language,String prefijo,String valor,String sufijo ) {
        for( int row = 0;row < m_printData.getRowCount();row++ ) {
            m_printData.setRowIndex( row );

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {
                    sb.append( prefijo );
                    sb.append( valor );
                    sb.append( item.getPrintNameSuffix());

                    Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                    String data = "";

                    if( obj != null ) {
                        if( obj instanceof PrintDataElement ) {
                            data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            sb.append( data );
                            sb.append( sufijo );
                            guardarLinea( writer,sb );
                        } else if( !( obj instanceof PrintData )) {
                            log.log( Level.SEVERE,"createLabelFile - Element not PrintData(Element) " + obj.getClass());
                        }
                    }
                }    // printed
            }        // for all columns
        }
    }                // createLabelFile

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param linea
     */

    private void guardarLinea( Writer writer,StringBuffer linea ) {
        try {
            if( linea != null ) {
                if( linea.length() > 0 ) {
                    writer.write( linea.toString());
                    writer.write( Env.NL );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createLabelFile - writer",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param language
     * @param prefijo
     * @param valor
     * @param sufijo
     */

    public void createLabelString( StringBuffer text,Language language,String prefijo,String valor,String sufijo ) {
        for( int row = 0;row < m_printData.getRowCount();row++ ) {
            m_printData.setRowIndex( row );

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {
                    sb.append( prefijo );
                    sb.append( valor );

                    if( item.getPrintNameSuffix() != null ) {
                        sb.append( item.getPrintNameSuffix());
                    }

                    Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                    String data = "";

                    if( obj != null ) {
                        if( obj instanceof PrintDataElement ) {
                            data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            sb.append( data );
                            sb.append( sufijo );

                            if( item.isFixedWidth()) {
                                char[] field = sb.toString().toCharArray();

                                for( int j = 0;j < item.getMaxWidth();j++ ) {
                                    if( j < field.length ) {
                                        text.append( field[ j ] );
                                    } else {
                                        text.append( " " );
                                    }
                                }
                            } else {
                                text.append( sb.toString());
                            }

                            if( item.isNextLine()) {
                                text.append( "\n" );
                            }
                        } else if( !( obj instanceof PrintData )) {
                            log.log( Level.SEVERE,"createLabelString - Element not PrintData(Element) " + obj.getClass());
                        }
                    }
                }    // printed
            }        // for all columns
        }
    }                // createLabelString

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param language
     * @param prefijo
     * @param valor
     * @param sufijo
     */

    public void printLabelDirect( StringBuffer text,Language language,String prefijo,String valor,String sufijo ) {
        for( int row = 0;row < m_printData.getRowCount();row++ ) {
            m_printData.setRowIndex( row );

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {
                    sb.append( prefijo );
                    sb.append( valor );
                    sb.append( item.getPrintNameSuffix());

                    Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                    String data = "";

                    if( obj != null ) {
                        if( obj instanceof PrintDataElement ) {
                            data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                            sb.append( data );
                            sb.append( sufijo );
                            sb.append( "\n" );
                            text.append( sb.toString());
                        } else if( !( obj instanceof PrintData )) {
                            log.log( Level.SEVERE,"printLabelDirect - Element not PrintData(Element) " + obj.getClass());
                        }
                    }
                }    // printed
            }        // for all columns
        }
    }                // printLabelDirect

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param language
     * @param delimiter
     * @param header
     * @param printAll
     */

    public void createFileText( Writer writer,Language language,String delimiter,boolean header,boolean printAll ) {
        int begin = 0;

        if( header ) {
            begin = -1;
        }

        for( int row = begin;row < m_printData.getRowCount();row++ ) {
            if( row != -1 ) {
                m_printData.setRowIndex( row );
            }

            boolean first = true;

            for( int col = 0;col < m_printFormat.getItemCount();col++ ) {
                StringBuffer     sb   = new StringBuffer();
                MPrintFormatItem item = m_printFormat.getItem( col );

                if( item.isPrinted()) {

                    // column delimiter (comma or tab)

                    if( first ) {
                        first = false;
                    } else {
                        sb.append( delimiter );
                    }

                    // header row

                    if( row == -1 ) {
                        if( header ) {
                            guardarLineaTexto( writer,sb.append( m_printFormat.getItem( col ).getPrintName( language )).toString());
                        }
                    } else {
                        if( item.isTypeField() || printAll ) {
                            Object obj = m_printData.getNode( new Integer( item.getAD_Column_ID()));
                            String data = "";

                            if( obj != null ) {
                                if( obj instanceof PrintDataElement ) {
                                    data = (( PrintDataElement )obj ).getValueDisplay( language );    // formatted
                                    sb.append( data );
                                } else if( !( obj instanceof PrintData )) {
                                    log.log( Level.SEVERE,"createLabelFile - Element not PrintData(Element) " + obj.getClass());
                                }
                            }
                        }

                        guardarLineaTexto( writer,sb.toString());
                    }
                }    // printed
            }

            guardarLineaTexto( writer,Env.NL );
        }
    }                // createLabelFile

    /**
     * Descripción de Método
     *
     *
     * @param writer
     * @param linea
     */

    private void guardarLineaTexto( Writer writer,String linea ) {
        try {
            if( linea != null ) {
                if( linea.length() > 0 ) {
                    writer.write( linea );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createIntrastatFile - writer",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        //

        int    AD_Table_ID = 100;
        MQuery q           = new MQuery( "AD_Table" );

        q.addRestriction( "AD_Table_ID","<",108 );

        //

        MPrintFormat f = MPrintFormat.createFromTable( Env.getCtx(),AD_Table_ID );
        PrintInfo i = new PrintInfo( "test",AD_Table_ID,108,0 );

        i.setAD_Table_ID( AD_Table_ID );

        ReportEngine re = new ReportEngine( Env.getCtx(),f,q,i );

        re.layout();
        re.print();

        // re.print(true, 1, false, "Epson Stylus COLOR 900 ESC/P 2");             //      Dialog
        // re.print(true, 1, false, "HP LaserJet 3300 Series PCL 6");              //      Dialog
        // re.print(false, 1, false, "Epson Stylus COLOR 900 ESC/P 2");    //      Dialog

        System.exit( 0 );
    }    // main
    
	public void setSummary(boolean summary)
	{
		m_summary = summary;
	}

}    // ReportEngine



/*
 *  @(#)ReportEngine.java   23.03.06
 * 
 *  Fin del fichero ReportEngine.java
 *  
 *  Versión 2.2
 *
 */
