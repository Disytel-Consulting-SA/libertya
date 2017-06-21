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

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;


import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MTable;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessCall;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Trx;
import org.openXpertya.apps.ADialog;
import org.openXpertya.model.MPaySelectionCheck;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.print.ReportViewerProvider;
/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReportCtl {
	
	/**
	 *	Constructor - prevent instance
	 */
	private ReportCtl()
	{
	}	//	ReportCtrl

	/**
	 * Constants used to pass process parameters to Jasper Process
	 */
	public static final String PARAM_PRINTER_NAME = "PRINTER_NAME";
	public static final String PARAM_PRINT_FORMAT = "PRINT_FORMAT";
	public static final String PARAM_PRINT_INFO = "PRINT_INFO";
	
	/**	Static Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (ReportCtl.class);
	
	private static ReportViewerProvider viewerProvider = new SwingViewerProvider(); 
	
	/**
	 *	Create Report.
	 *	Called from ProcessCtl.
	 *	- Check special reports first, if not, create standard Report
	 *
	 *  @param pi process info
	 *  @param IsDirectPrint if true, prints directly - otherwise View
	 *  @return true if created
	 */
	static public boolean start (ProcessInfo pi, boolean IsDirectPrint)
	{
		return start(null, -1, pi, IsDirectPrint);
	}
	
	/**
	 *	Create Report.
	 *	Called from ProcessCtl.
	 *	- Check special reports first, if not, create standard Report
	 *
	 *  @param parent The window which invoked the printing
	 *  @param WindowNo The windows number which invoked the printing
	 *  @param pi process info
	 *  @param IsDirectPrint if true, prints directly - otherwise View
	 *  @return true if created
	 */
	static public boolean start (ASyncProcess parent, int WindowNo, ProcessInfo pi, boolean IsDirectPrint)
	{
		pi.setPrintPreview(!IsDirectPrint);
		return start(parent, WindowNo, pi);
	}
	
	/**
	 *	Create Report.
	 *	Called from ProcessCtl.
	 *	- Check special reports first, if not, create standard Report
	 *
	 *  @param parent The window which invoked the printing
	 *  @param WindowNo The windows number which invoked the printing
	 *  @param pi process info
	 *  @param IsDirectPrint if true, prints directly - otherwise View
	 *  @return true if created
	 */
	static public boolean start (ASyncProcess parent, int WindowNo, ProcessInfo pi)
	{
		s_log.info("start - " + pi);

		/**
		 *	Order Print
		 */
		if (pi.getAD_Process_ID() == 110)			//	C_Order
			return startDocumentPrint(ReportEngine.ORDER, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		if (pi.getAD_Process_ID() ==  MProcess.getProcess_ID("Rpt PP_Order", null))			//	C_Order
			return startDocumentPrint(ReportEngine.MANUFACTURING_ORDER, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		if (pi.getAD_Process_ID() ==  MProcess.getProcess_ID("Rpt DD_Order", null))			//	C_Order
			return startDocumentPrint(ReportEngine.DISTRIBUTION_ORDER, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		else if (pi.getAD_Process_ID() == 116)		//	C_Invoice
			return startDocumentPrint(ReportEngine.INVOICE, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		else if (pi.getAD_Process_ID() == 117)		//	M_InOut
			return startDocumentPrint(ReportEngine.SHIPMENT, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		else if (pi.getAD_Process_ID() == 217)		//	C_Project
			return startDocumentPrint(ReportEngine.PROJECT, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		else if (pi.getAD_Process_ID() == 276)		//	C_RfQResponse
			return startDocumentPrint(ReportEngine.RFQ, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
		else if (pi.getAD_Process_ID() == 313)		//	C_Payment
			return startCheckPrint(pi.getRecord_ID(), !pi.isPrintPreview());
		/**
        else if (pi.getAD_Process_ID() == 290)      // Movement Submission by VHARCQ
            return startDocumentPrint(ReportEngine.MOVEMENT, pi.getRecord_ID(), parent, WindowNo, IsDirectPrint);
		else if (pi.AD_Process_ID == 9999999)	//	PaySelection
			return startDocumentPrint(CHECK, pi, IsDirectPrint);
		else if (pi.AD_Process_ID == 9999999)	//	PaySelection
			return startDocumentPrint(REMITTANCE, pi, IsDirectPrint);
		**/
		else if (pi.getAD_Process_ID() == 159)		//	Dunning
			return startDocumentPrint(ReportEngine.DUNNING, pi.getRecord_ID(), parent, WindowNo, !pi.isPrintPreview());
	   else if (pi.getAD_Process_ID() == 202			//	Financial Report
			|| pi.getAD_Process_ID() == 204)			//	Financial Statement
		   return startFinReport (pi);
		/********************
		 *	Standard Report
		 *******************/
		return startStandardReport (pi);
	}	//	create

	/**************************************************************************
	 *	Start Standard Report.
	 *  - Get Table Info & submit
	 *  @param pi Process Info
	 *  @param IsDirectPrint if true, prints directly - otherwise View
	 *  @return true if OK
	 */
	static public boolean startStandardReport (ProcessInfo pi, boolean IsDirectPrint)
	{
		pi.setPrintPreview(!IsDirectPrint);
		return startStandardReport(pi);
	}
	
	static ReportEngine re = null;
	
	/**************************************************************************
	 *	Start Standard Report.
	 *  - Get Table Info & submit.<br>
	 *  A report can be created from:
	 *  <ol>
	 *  <li>attached MPrintFormat, if any (see {@link ProcessInfo#setTransientObject(Object)}, {@link ProcessInfo#setSerializableObject(java.io.Serializable)}
	 *  <li>process information (AD_Process.AD_PrintFormat_ID, AD_Process.AD_ReportView_ID)
	 *  </ol>
	 *  @param pi Process Info
	 *  @param IsDirectPrint if true, prints directly - otherwise View
	 *  @return true if OK
	 */
	static public boolean startStandardReport (ProcessInfo pi)
	{
		//
		// Create Report Engine by using attached MPrintFormat (if any)
		Object o = pi.getTransientObject();
		if (o == null)
			o = pi.getSerializableObject();
		if (o != null && o instanceof MPrintFormat) {
			Properties ctx = Env.getCtx();
			MPrintFormat format = (MPrintFormat)o;
			String TableName = M_Table.getTableName(ctx, format.getAD_Table_ID());
			MQuery query = MQuery.get (ctx, pi.getAD_PInstance_ID(), TableName);
			PrintInfo info = new PrintInfo(pi);
			re = new ReportEngine(ctx, format, query, info);
			createOutput(re, pi.isPrintPreview(), null);
			return true;
		}
		//
		// Create Report Engine normally
		else {
			re = ReportEngine.get(Env.getCtx(), pi, false);
			re.setQuery();
			if (re == null)
			{
				pi.setSummary("No ReportEngine");
				return false;
			}
		}
		
		createOutput(re, pi.isPrintPreview(), null);
		return true;
	}	//	startStandardReport

	static public void cancelReport () {
		if (re!=null && re.de!=null && re.de.pstmt!=null)
			DB.cancelStatement(re.de.pstmt);
	}
	
	/**
	 *	Start Financial Report.
	 *  @param pi Process Info
	 *  @return true if OK
	 */
	static public boolean startFinReport (ProcessInfo pi)
	{
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());

		//  Create Query from Parameters
		String TableName = pi.getAD_Process_ID() == 202 ? "T_Report" : "T_ReportStatement";
		MQuery query = MQuery.get (Env.getCtx(), pi.getAD_PInstance_ID(), TableName);

		//	Get PrintFormat
		MPrintFormat format = (MPrintFormat)pi.getTransientObject();
		if (format == null)
			format = (MPrintFormat)pi.getSerializableObject();
		if (format == null)
		{
			s_log.log(Level.SEVERE, "startFinReport - No PrintFormat");
			return false;
		}
		PrintInfo info = new PrintInfo(pi);

		ReportEngine re = new ReportEngine(Env.getCtx(), format, query, info);
		createOutput(re, pi.isPrintPreview(), null);
		return true;
	}	//	startFinReport
	
	/**
	 * 	Start Document Print for Type.
	 *  	Called also directly from ProcessDialog, VInOutGen, VInvoiceGen, VPayPrint
	 * 	@param type document type in ReportEngine
	 * 	@param Record_ID id
	 * 	@param IsDirectPrint if true, prints directly - otherwise View
	 * 	@return true if success
	 */
	public static boolean startDocumentPrint (int type, int Record_ID, boolean IsDirectPrint)
	{
		return startDocumentPrint(type, Record_ID, null, -1, IsDirectPrint);
	}
	
	/**
	 * 	Start Document Print for Type with specified printer. Always direct print.
	 * 	@param type document type in ReportEngine
	 *  @param customPrintFormat	Custom print format. Can be null.
	 * 	@param Record_ID id
	 *  @param parent The window which invoked the printing
	 *  @param WindowNo The windows number which invoked the printing
	 * 	@param printerName 	Specified printer name
	 * 	@return true if success
	 */
	public static boolean startDocumentPrint(int type, MPrintFormat customPrintFormat, int Record_ID, ASyncProcess parent, int WindowNo, String printerName) 
	{
		return(startDocumentPrint(type, customPrintFormat, Record_ID, parent, WindowNo, true, printerName));
	}

	/**
	 * 	Start Document Print for Type.
	 *  	Called also directly from ProcessDialog, VInOutGen, VInvoiceGen, VPayPrint
	 * 	@param type document type in ReportEngine
	 * 	@param Record_ID id
	 *  @param parent The window which invoked the printing
	 *  @param WindowNo The windows number which invoked the printing
	 * 	@param IsDirectPrint if true, prints directly - otherwise View
	 * 	@return true if success
	 */
	public static boolean startDocumentPrint(int type, int Record_ID, ASyncProcess parent, int WindowNo,
			boolean IsDirectPrint) 
	{
		return(startDocumentPrint(type, null, Record_ID, parent, WindowNo, IsDirectPrint, null ));
	}

	/**
	 * 	Start Document Print for Type with specified printer.
	 * 	@param type document type in ReportEngine
	 * 	@param Record_ID id
	 *  @param parent The window which invoked the printing
	 *  @param WindowNo The windows number which invoked the printing
	 * 	@param printerName 	Specified printer name
	 * 	@return true if success
	 */
	public static boolean startDocumentPrint (int type, MPrintFormat customPrintFormat, int Record_ID, ASyncProcess parent, int WindowNo, 
			boolean IsDirectPrint, String printerName)
	{
		ReportEngine re = ReportEngine.get (Env.getCtx(), type, Record_ID);
		if (re == null)
		{
			if (Ini.isClient())
			{
				ADialog.error(0, null, "NoDocPrintFormat");
			}
			else
			{
				try 
				{
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					if (loader == null)
						loader = ReportCtl.class.getClassLoader();
					Class<?> clazz = loader.loadClass("org.adempiere.webui.window.FDialog");
					Method m = clazz.getMethod("error", Integer.TYPE, String.class);
					m.invoke(null, 0, "NoDocPrintFormat");
				} catch (Exception e) 
				{
					throw new /*Adempiere*/RuntimeException(e);
				}
			}
			return false;
		}
		if (customPrintFormat!=null) {
			// Use custom print format if available
			re.setPrintFormat(customPrintFormat);
		}
		
		if(re.getPrintFormat()!=null)
		{
			MPrintFormat format = re.getPrintFormat();
			
			// We have a Jasper Print Format
			// ==============================
			if(format.getJasperProcess_ID() > 0)	
			{
				ProcessInfo pi = new ProcessInfo ("", format.getJasperProcess_ID());
				pi.setPrintPreview( !IsDirectPrint );
				pi.setRecord_ID ( Record_ID );
				Vector<ProcessInfoParameter> jasperPrintParams = new Vector<ProcessInfoParameter>();
				ProcessInfoParameter pip;
				if (printerName!=null && printerName.trim().length()>0) {
					// Override printer name
					pip = new ProcessInfoParameter(PARAM_PRINTER_NAME, printerName, null, null, null);
					jasperPrintParams.add(pip);
				}
				pip = new ProcessInfoParameter(PARAM_PRINT_FORMAT, format, null, null, null);
				jasperPrintParams.add(pip);
				pip = new ProcessInfoParameter(PARAM_PRINT_INFO, re.getPrintInfo(), null, null, null);
				jasperPrintParams.add(pip);
				
				pi.setParameter(jasperPrintParams.toArray(new ProcessInfoParameter[]{}));
				
				//	Execute Process
				if (Ini.isClient())
				{
					ProcessCtl.process(null,		// Parent set to null for synchronous processing, see bugtracker 3010932  
									   WindowNo,
									   pi,
									   null); 
				}
				else
				{
					try 
					{
						ClassLoader loader = Thread.currentThread().getContextClassLoader();
						if (loader == null)
							loader = ReportCtl.class.getClassLoader();
						Class<?> clazz = loader.loadClass("org.adempiere.webui.apps.WProcessCtl");
						Method method = clazz.getDeclaredMethod("process", ASyncProcess.class, Integer.TYPE, ProcessInfo.class, Trx.class);
						method.invoke(null, parent, WindowNo, pi, null);
					}
					catch (Exception e)
					{
						throw new /*Adempiere*/RuntimeException(e);
					}
				}
			}
			else
			// Standard Print Format (Non-Jasper)
			// ==================================
			{
				createOutput(re, !IsDirectPrint, printerName);
				if (IsDirectPrint)
				{
					ReportEngine.printConfirm (type, Record_ID);
				}
			}
		}
		return true;
	}	//	StartDocumentPrint
	
	/**
	 * 	Start Check Print.
	 * 	Find/Create
	 *	@param C_Payment_ID Payment
	 * 	@param IsDirectPrint if true, prints directly - otherwise View
	 * 	@return true if success
	 */
	public static boolean startCheckPrint (int C_Payment_ID, boolean IsDirectPrint)
	{
		
		// afalcone - [ 1871567 ] Wrong value in Payment document
//		boolean ok = MPaySelectionCheck.deleteGeneratedDraft(Env.getCtx(), C_Payment_ID, null);
		//
		
		int C_PaySelectionCheck_ID = 0;
		MPaySelectionCheck psc = MPaySelectionCheck.getOfPayment(Env.getCtx(), C_Payment_ID, null);
		
		if (psc != null)
			C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
		else
		{
			psc = MPaySelectionCheck.createForPayment(Env.getCtx(), C_Payment_ID, null);
			if (psc != null)
				C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
		}
		return startDocumentPrint (ReportEngine.CHECK, C_PaySelectionCheck_ID, null, -1, IsDirectPrint);
	}	//	startCheckPrint
	
	private static void createOutput(ReportEngine re, boolean printPreview, String printerName)
	{
		if (printPreview)
			preview(re);
		else {
				if (printerName!=null) {
					re.getPrintInfo().setPrinterName(printerName);
				}
				re.print();
		}
	}
	
	/**
	 * Launch viewer for report
	 * @param re
	 */
	public static void preview(ReportEngine re) 
	{
		ReportViewerProvider provider = getReportViewerProvider();
		provider.openViewer(re);
	}
	
	public static void setReportViewerProvider(ReportViewerProvider provider)
	{
		if (provider == null)
			throw new IllegalArgumentException("Cannot set report viewer provider to null");
		viewerProvider = provider;
	}
	
	public static ReportViewerProvider getReportViewerProvider()
	{
		return viewerProvider;
	}


}    // ReportCtl



/*
 *  @(#)ReportCtl.java   02.07.07
 * 
 *  Fin del fichero ReportCtl.java
 *  
 *  Versión 2.2
 *
 */
