package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.jfree.util.Log;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.apps.ADialog;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.X_AD_JasperReport;
import org.openXpertya.print.CPrinter;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfo.JasperReportDTO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;


public class MJasperReport extends X_AD_JasperReport
{	
	/**	Static Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MJasperReport.class);

	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_Attachment_ID id
	 */
	public MJasperReport(Properties ctx, int C_JasperReport_ID, String trxName)
	{
		super (ctx, C_JasperReport_ID, trxName);
		initJasper();
	}	//	MJasperReport

	public static MJasperReport get(Properties ctx, String name, String trxName){
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * \n");
			sql.append("FROM   AD_JasperReport \n");
			sql.append("WHERE  name LIKE ?");
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				return new MJasperReport(ctx, rs,trxName);
			}
		}catch(Exception ex){
			Log.error("Error al buscar el Jasper Report: "+ex.toString());
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public MJasperReport(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
		initJasper();
	}	//	MJasperReport
		
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("MJasperReport[");
		sb.append(getAD_JasperReport_ID());
		sb.append("]");
		return sb.toString();
	}	//	toString

	
	
	// Interface con Jasper

	/** Parametros el informe				*/
	private HashMap Parameters = new HashMap();
	
	/** Informe Jasper		*/
	JasperReport JReport = null;
	
	/** El informe relleno	*/
    JasperPrint JPrint = null;
	
	
    private static String OUTPUT_VIEWER = "VIEWER";
    
    private static String OUTPUT_DISK = "DISK";
    
    private static String OUTPUTDISKFORMAT_PDF = "PDF";
    
    private static String OUTPUTDISKFORMAT_HTML = "HTML";

	
    private void initJasper()	{
        
    	// Evitamos que borre el fichero .java
    	System.setProperty ("jasper.reports.compile.keep.java.file", "false");
    
    	// directorio temporal
    	System.setProperty( "jasper.reports.compile.temp", System.getProperty("java.io.tmpdir"));
    	
    	// Para evitar que se conecte a las X
    	System.setProperty("java.awt.headless", "true");  
	}
	
	/**
	 * Añade un parametro.
	 * @param parametro
	 * @param valor
	 */
	public void addParameter(String parametro, Object valor)
    {
    	Parameters.put(parametro, valor);
    }
	
	/**
     * Rellenamos el informe almacenado en base de datos con C_JasperReport
     * con los datos del JRDataSource dataSource
     * 
     * @param C_JasperReport_ID
     * @param dataSource
     * @throws RuntimeException
     */
    public void fillReport(JRDataSource dataSource) throws RuntimeException
    {  
        // Rellenamos el informe
        try
        {
        	
        	byte[] report = getBinaryData();
        	if (report == null)	{
        		throw new RuntimeException("No se ha podido cargar el informe precompilado.");
        	}
        	// Rellenamos el informe 
        	JPrint = JasperFillManager.fillReport(new ByteArrayInputStream(report), Parameters, dataSource);
        }
        catch (JRException exception)
        {
        	throw new RuntimeException("Error rellenando report.", exception);
            
        }
        catch(NullPointerException fexception)
        {
        	throw new RuntimeException("Null PointerException rellenando el report.", fexception);
        }
    }

    /**
     * Rellenamos el informe almacenado en base de datos con C_JasperReport
     * con los datos de la sql del informe y la conexion enviada como parametro.
     * @param C_JasperReport_ID
     * @param conn
     * @throws RuntimeException
     */
    public void fillReport(Connection conn) throws RuntimeException
    {
        // Rellenamos el informe
        try
        {       	
        	// Rellenamos el informe 
        	JPrint = JasperFillManager.fillReport(new ByteArrayInputStream(getBinaryData()), Parameters, conn);
        }
        catch (JRException exception)
        {
        	throw new RuntimeException("Error rellenando report.", exception);
            
        }
        catch(NullPointerException fexception)
        {
        	throw new RuntimeException("Null PointerException rellenando el report.", fexception);
        }
    }
    
    /**
     * Muestra el informe por el formato en el visor 
     * 
     */   
    public void showReport(ProcessInfo pi) throws Exception {
    	showReport(getCtx(), pi, JPrint, getName());
    }
    
    public static void showReport(Properties ctx, ProcessInfo pi, JasperPrint jPrint, String name) throws Exception {
    	// Si es invocacion server-side, delegar a ViewerProvider
    	if (!Ini.isClient()) {
            JRViewerProvider viewerLauncher = ReportStarter.getReportViewerProvider();
            viewerLauncher.openViewer(jPrint, pi.getTitle());
            return;
    	}
    	if(JasperReportsUtil.isPrintPreview(ctx, pi)){
        	OXPJasperViewer v = OXPJasperViewer.viewReport(ctx, pi, jPrint, false);
        	v.setTitle(name);
        	v=null;
    	}
    	else{
    		printReport(ctx, pi, jPrint);
    	}
    } 

    public static void printReport(Properties ctx, ProcessInfo pi, JasperPrint jPrint)	throws Exception{
    	// Realizar las validaciones de nombre de impresora dentro del tipo de
		// documento si es que existe
    	JasperReportDTO jasperDTO = pi.getJasperReportDTO();
    	if(jasperDTO != null){
    		// Si hay un tipo de documento lo obtengo
			if(!Util.isEmpty(jasperDTO.getDocTypeID(), true)){
				MDocType docType = new MDocType(ctx, jasperDTO.getDocTypeID(), null);
				String printerName = docType.getPrinterName();
				if(!Util.isEmpty(printerName, true)){
					if(!CPrinter.existsPrinterName(printerName)){
						if (Util.isEmpty(CPrinter.getDefaultPrinterName(), true)
								|| CPrinter.getPrinterNames().length == 0) {
							// No existe una impresora por defecto o No existen impresoras
							throw new RuntimeException(Msg.getMsg(ctx, "NoPrintersAndDefaultFound"));
						}
						// Si no existe la impresora, se pregunta si se desea
						// imprimir de todas maneras con la impresora por
						// defecto
						if (!ADialog.ask(pi.getWindowNo(), null,
								"DocTypePrinterNameNotExist",
								new Object[] { printerName }, null)) {
							return;
						}
					}
				}
			}
    	}
    	JasperReportsUtil.printJasperReport(ctx, jPrint, pi, false);
    }
    
	/**
	 * Imprime el informe sin previsualizar
	 * 
	 * @param pi
	 *            información de Proceso
	 * @throws JRException
	 *             en caso de error
	 */
    public void printReport(ProcessInfo pi)	throws Exception{
		printReport(getCtx(), pi, JPrint);
    }
    
    public JasperPrint getJasperPrint(){
    	return JPrint;
    }
    
    /**
     * Actualiza el contenido del campo que contiene el precompilado de un informe Jasper
     * @param trxName transacción a utilizar
     * @param ctx contexto
     * @param componentObjectUID identificador universal
     * @param data el .jasper precompilado
     * @throws Exception
     */
    public static void updateBinaryData(String trxName, Properties ctx, String componentObjectUID, byte[] data) throws Exception
    {
    	// Si el .jasper parámetro es null no hago nada
    	if(data == null || data.length <= 0){
    		return;
    	}
    	
    	// recuperar un Jasper a partir de su UID
    	String getIDFromUID = " SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE AD_ComponentObjectUID = ? ";
		MJasperReport libroIvaJR = new MJasperReport(ctx, DB.getSQLValue(trxName, getIDFromUID, componentObjectUID), trxName);
		
		
		// Setear el contenido binario correspondiente
		libroIvaJR.setBinaryData(data);
		
		// Persistir y elevar excepción en caso de error
		if (!libroIvaJR.save())
			throw new Exception ("Error al actualizar reporte " + libroIvaJR.getName());
    }
}	

