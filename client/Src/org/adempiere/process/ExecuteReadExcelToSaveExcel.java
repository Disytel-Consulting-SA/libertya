package org.adempiere.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.adempiere.util.ProcessUtil;
import org.adempiere.utils.Miscfunc;
import org.compiere.interfaces.MD5;
import org.compiere.interfaces.MD5Home;
import org.openXpertya.JasperReport.DigestOfFile;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MAttachmentEntry;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Trx;


// import org.apache.util.Base64;	// en realidad estaba el comentado


// dREHER, 12/05/2015
// Permite configurar attachando una plantilla de excel para llamar el proceso
// ReadExcelToSaveExcel en forma interna.
//

public class ExecuteReadExcelToSaveExcel
	extends SvrProcess 
{
	public static final String RESULTADO_OK = "@ProcessOK@";
		
	private String p_path    = "";
	private ProcessInfoParameter[] para = null;
	private static File REPORT_HOME = null;
	
	protected void prepare() 
	{
		para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;			
			else if (name.equalsIgnoreCase("Path")){
				
				p_path = ((String) para[i].getParameter()).toString();
				
			}else
				log.log(Level.SEVERE, "Parametro Desconocido: " + name);
		}
	}


	protected String doIt() 
		throws Exception 
	{
		String ret = RESULTADO_OK;
		
		System.setProperty( "javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
	    System.setProperty( "org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");

	    String reportPath = System.getProperty("org.compiere.report.path");
	    if (reportPath == null) {
	    	REPORT_HOME = new File(Ini.getOXPHome() + File.separator + "reports");
	    } else {
	    	REPORT_HOME = new File(reportPath);
	    }

	    debug("Report Home: " + REPORT_HOME.getAbsolutePath());
		ret = ExecuteProcess();
		
		return ret;

	}
	
	private void debug(String string) {
		System.out.println("--> ExecuteReadExcelToSaveExcel: " + string);
	}


	private String ExecuteProcess() {
		String ret = "";
		
		// Recupero ID del reporte
		int proc_id = -1;
		
		String rptName = "ReadExcelToSaveExcel";
		String sql = "SELECT ad_process_id FROM ad_process WHERE name=?";

		proc_id = DB.getSQLValue(null, sql, rptName);
		if (proc_id == -1)
			ret = "No esta definido ningun proceso llamado " + rptName;
		else{
			
			// Instancio la Instancia de Proceso
			MPInstance p = new MPInstance(getCtx(), proc_id, proc_id, get_TrxName());    
			
			p.save();

			MProcess pro = new MProcess (getCtx(), proc_id, null);
			String proName = pro.getName();
			
			debug("ProcessName=" + proName + " ID=" + proc_id);
			
			ProcessInfo pi = new ProcessInfo(proName, proc_id);
			
			ReportData rp = getReportData(this.getProcessInfo(), get_TrxName());
			
			File file = getReportFile(rp.getReportFilePath(), this.getProcessInfo());
			
			ProcessInfoParameter p_fin_p[] = new ProcessInfoParameter[para.length+1];
			if(file!=null){
				
				ProcessInfoParameter pip = new ProcessInfoParameter("Path",file.getAbsolutePath(),null,null,null);
				
				Object[] ob = Miscfunc.addItemToArray((Object[])para, (Object)pip);
				int x = 0;
				for(Object pp: ob){
					ProcessInfoParameter tmp = (ProcessInfoParameter) pp;
					p_fin_p[x] = tmp;
					x++;
				}
			}else
				p_fin_p = para;
			
			pi.setParameter(p_fin_p);
			pi.setRecord_ID(proc_id);
			pi.setAD_Process_ID(proc_id);
			pi.setAD_PInstance_ID(p.getID());

			// org.compiere.process.startReport
			
			pi.setClassName("org.adempiere.process.ReadExcelToSaveExcel");  // Process class
			Trx trx = Trx.get(get_TrxName(), false);
			
			if (!ProcessUtil.startJavaProcess(getCtx(), pi, trx)) 
				ret = "No se pudo generar el Reporte: " + pi.getSummary();
			else
				ret = pi.getSummary();
			
		}
		
		debug("ExecuteProcess.ret= " + ret);
		
		return ret;
	}
	
	class ReportData {
        private String reportFilePath;
        private boolean directPrint;

        public ReportData(String reportFilePath, boolean directPrint) {
            this.reportFilePath = reportFilePath;
            this.directPrint = directPrint;
        }

        public String getReportFilePath() {
            return reportFilePath;
        }

        public boolean isDirectPrint() {
            return directPrint;
        }
    }
	
	   /**
     * @author rlemeill
     * @param ProcessInfo
     * @return ReportData
     */
    public ReportData getReportData(ProcessInfo pi, String trxName) {
    	log.info("");
        String sql = "SELECT pr.JasperReport, pr.IsDirectPrint "
        		   + "FROM AD_Process pr, AD_PInstance pi "
                   + "WHERE pr.AD_Process_ID = pi.AD_Process_ID "
                   + " AND pi.AD_PInstance_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, trxName);
            pstmt.setInt(1, pi.getAD_PInstance_ID());
            rs = pstmt.executeQuery();
            String path = null;
            boolean	directPrint = false;
            boolean isPrintPreview = true; // TODO ver data pi.isPrintPreview();
            if (rs.next()) {
                path = rs.getString(1);
				
				if ("Y".equalsIgnoreCase(rs.getString(2)) && !Ini.getPropertyBool(Ini.P_PRINTPREVIEW)
						&& !isPrintPreview )
					directPrint = true;
            } else {
                log.severe("data not found; sql = "+sql);
				return null;
            }
            
            return new ReportData( path, directPrint);
            
        } catch (SQLException e) {
            log.severe("sql = "+sql+"; e.getMessage() = "+ e.getMessage());
            return null;
        } finally {
            DB.close(rs);
            DB.close(pstmt);
            
        }
    }
	
	/**
     * @author alinv
     * @param reportPath
     * @param reportType
     * @return the abstract file corresponding to typed report
     */
	protected File getReportFile(String reportPath, String reportType, ProcessInfo pi) {
		
		if (reportType != null)
		{
			int cpos = reportPath.lastIndexOf('.');
			reportPath = reportPath.substring(0, cpos) + "_" + reportType + reportPath.substring(cpos, reportPath.length());
		}
		
		return getReportFile(reportPath, pi);
	}
	
	/**
	 * @author alinv
	 * @param reportPath
	 * @return the abstract file corresponding to report
	 */
	protected File getReportFile(String reportPath, ProcessInfo pi) {
		File reportFile = null;
		
		// Reports deployement on web server Thanks to Alin Vaida
		if (reportPath.startsWith("http://") || reportPath.startsWith("https://")) {
			reportFile = httpDownloadedReport(reportPath);
		} else if (reportPath.startsWith("attachment:")) {
			//report file from process attachment
			reportFile = downloadAttachment(reportPath, pi);
		} else if(reportPath.startsWith("/")) {
			reportFile = new File(reportPath);
		} else if (reportPath.startsWith("file:/")) {
			try {
				reportFile = new File(new URI(reportPath));
			} catch (URISyntaxException e) {
				log.warning(e.getLocalizedMessage());
				reportFile = null;
			}
		} else if (reportPath.startsWith("resource:")) {
			try {
				reportFile = getFileAsResource(reportPath);
			} catch (Exception e) {
				log.warning(e.getLocalizedMessage());
				reportFile = null;
			}
		} else {
			reportFile = new File(REPORT_HOME, reportPath);
		}
		
		// Set org.compiere.report.path because it is used in reports which refer to subreports
		if(reportFile != null)
			System.setProperty("org.compiere.report.path", reportFile.getParentFile().getAbsolutePath());
		
		return reportFile;
	}

	/**
	 * Download db attachment 
	 * @param reportPath must of syntax attachment:filename
	 * @return File
	 */
	private File downloadAttachment(String reportPath, ProcessInfo processInfo) {
		File reportFile = null;
		String name = reportPath.substring("attachment:".length()).trim();
		MProcess process = new MProcess(Env.getCtx(), processInfo.getAD_Process_ID(), processInfo.getTransactionName()); 
		MAttachment attachment = process.getAttachment();
		if (attachment != null) {
			MAttachmentEntry[] entries = attachment.getEntries();
			MAttachmentEntry entry = null;
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getName().equals(name)) {
					entry = entries[i];
					break;
				}
			}
			if (entry != null) {
				reportFile = getAttachmentEntryFile(entry);
			}	
		}
		return reportFile;
	}
	
	/**
	 * Download db attachment to local file
	 * @param entry
	 * @return File
	 */
	private File getAttachmentEntryFile(MAttachmentEntry entry) {
		
		String localFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + entry.getName();
		String downloadedLocalFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+"TMP" + entry.getName();
		File reportFile = new File(localFile);
		if (reportFile.exists()) {
			// String localMD5hash = DigestOfFile.GetLocalMD5Hash(reportFile);
			// String entryMD5hash = DigestOfFile.getMD5Hash(entry.getData());
			// if (localMD5hash.equals(entryMD5hash))
			// {
			// 	log.info(" no need to download: local report is up-to-date");
			// }
			// else
			if(true) // fuerza reescritura
			{
				log.info(" report on server is different that local one, download and replace");
				File downloadedFile = new File(downloadedLocalFile);
				entry.getFile(downloadedFile);
				try{
					reportFile.delete();
				}catch(Exception ex){
					log.warning("Error al borrar: " + ex.toString());
				}
				try{
					downloadedFile.renameTo(reportFile);
				}catch(Exception ex){
					log.warning("Error al copiar: " + ex.toString());
				}
			}
		} else {
			entry.getFile(reportFile);
		}
		return reportFile;
		
		/*String localFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + entry.getName();
		String downloadedLocalFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+"TMP" + entry.getName();
		File reportFile = new File(localFile);
		if (reportFile.exists()) {
			String localMD5hash = DigestOfFile.GetLocalMD5Hash(reportFile);
			String entryMD5hash = DigestOfFile.getMD5Hash(entry.getData());
			if (localMD5hash.equals(entryMD5hash))
			{
				log.info(" no need to download: local report is up-to-date");
			}
			else
			{
				log.info(" report on server is different that local one, download and replace");
				File downloadedFile = new File(downloadedLocalFile);
				entry.getFile(downloadedFile);
				reportFile.delete();
				downloadedFile.renameTo(reportFile);
			}
		} else {
			entry.getFile(reportFile);
		}
		return reportFile;*/
	}

	 /**
     * @author rlemeill
     * @param reportLocation http string url ex: http://adempiereserver.domain.com/webApp/standalone.jrxml
     * @return downloaded File (or already existing one)
     */
    private File httpDownloadedReport(String reportLocation)
    {
    	File reportFile = null;
    	File downloadedFile = null;
    	log.info(" report deployed to " + reportLocation);
    	try {
    		
    		
    		String[] tmps = reportLocation.split("/");
    		String cleanFile = tmps[tmps.length-1];
    		String localFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + cleanFile;
    		String downloadedLocalFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+"TMP" + cleanFile;
    		
    		reportFile = new File(localFile);
    		
    		
    		if (reportFile.exists())
    		{
    			String localMD5hash = DigestOfFile.GetLocalMD5Hash(reportFile);
    			String remoteMD5Hash = ejbGetRemoteMD5(reportLocation);
    			log.info("MD5 for local file is "+localMD5hash );
    			if ( remoteMD5Hash != null)
    			{
    				if (localMD5hash.equals(remoteMD5Hash))
    				{
    					log.info(" no need to download: local report is up-to-date");
    				}
    				else
    				{
    					log.info(" report on server is different that local one, download and replace");
    					downloadedFile = getRemoteFile(reportLocation, downloadedLocalFile);
    					reportFile.delete();
    					downloadedFile.renameTo(reportFile);
    				}
    			}
    			else
    			{
    				log.warning("Remote hashing is not available did you deployed webApp.ear?");
    				downloadedFile = getRemoteFile(reportLocation, downloadedLocalFile);
    				//    				compare hash of existing and downloaded
    				if ( DigestOfFile.md5localHashCompare(reportFile,downloadedFile) )
    				{
    					//nothing file are identic
    					log.info(" no need to replace your existing report");
    				}
    				else
    				{
    					log.info(" report on server is different that local one, replacing");
    					reportFile.delete();
    					downloadedFile.renameTo(reportFile);
    				}
    			}
    		}
    		else
    		{
    			reportFile = getRemoteFile(reportLocation,localFile);
    		}
    		
    	}
    	catch (Exception e) {
    		log.severe("Unknown exception: "+ e.getMessage());
    		return null;
    	}
    	return reportFile;
    }
    
    /**
     * @param requestedURLString
     * @return md5 hash of remote file computed directly on application server
     * 			null if problem or if report doesn't seem to be on AS (different IP or 404)
     */
    private String ejbGetRemoteMD5(String requestedURLString)
    {
		InitialContext context = null;
		String md5Hash = null;
    	try {
    		URL requestURL = new URL(requestedURLString);
    		//String requestURLHost = requestURL.getHost();
    		Hashtable env = new Hashtable();
    		env.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
    		env.put(InitialContext.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
    		env.put(InitialContext.PROVIDER_URL, requestURL.getHost() + ":" + CConnection.get().getAppsPort());
    		context = new InitialContext(env);
    		if (isRequestedonAS(requestURL) && isMD5HomeInterfaceAvailable())
    		{
    			MD5Home home = (MD5Home)context.lookup(MD5Home.JNDI_NAME);
    			MD5 md5 = (MD5) home.create();
    			md5Hash = md5.getFileMD5(requestedURLString);
    			log.info("MD5 for " + requestedURLString + " is " + md5Hash);
    			md5.remove();
    		}
  
    	}
    	catch (MalformedURLException e) {
    		log.severe("URL is invalid: "+ e.getMessage());
    		return null;
    	}
    	catch (NamingException e){
    		log.warning("Unable to create jndi context did you deployed webApp.ear package?\nRemote hashing is impossible");
    		return null;
    	}
    	catch (RemoteException e){
    		log.warning("Unknown remote error exception");
    		return null;
    	}
    	catch(CreateException e){
    		log.warning("Error in RemoteInterface creation");
			return null;
    	}
    	catch(RemoveException e){
    		log.warning("Error in RemoteInterface removing");
    		return null;
    	}
    	return md5Hash;
    }

    /**
     * @param requestURL
     * @return true if the report is on the same ip address than Application Server
     */
    private boolean isRequestedonAS(URL requestURL)
    {
    	boolean tBool = false;
    	try{
    		InetAddress[] request_iaddrs = InetAddress.getAllByName(requestURL.getHost());
    		InetAddress as_iaddr = InetAddress.getByName(CConnection.get().getAppsHost());
    		for(int i=0;i<request_iaddrs.length;i++)
    		{
    			log.info("Got "+request_iaddrs[i].toString()+" for "+requestURL+" as address #"+i);
    			if(request_iaddrs[i].equals(as_iaddr))
    			{
    				log.info("Requested report is on application server host");
    				tBool = true;
    				break;
    			}
    		}
    	}
    	catch (UnknownHostException e) {
    		log.severe("Unknown dns lookup error");
    		return false;
    	}
    	return tBool;
    	
    }
    
    /**
     * @return true if the class org.compiere.interfaces.MD5Home is present
     */
    private boolean isMD5HomeInterfaceAvailable()
    {
    	try
		{
    		Class md5HomeClass = Class.forName("org.compiere.interfaces.MD5Home");
    		log.info("EJB client for MD5 remote hashing is present");
    		return true;
		}
    	catch (ClassNotFoundException e)
		{
    		log.warning("EJB Client for MD5 remote hashing absent\nyou need the class org.compiere.interfaces.MD5Home - from webEJB-client.jar - in classpath");
    		return false;	
		}
    }
    
    
    
    /**
     * @author rlemeill
     * @param reportLocation http://applicationserver/webApp/standalone.jrxml for exemple
     * @param localPath Where to put the http downloadede file
     * @return abstract File wich represent the downloaded file
     */
    private File getRemoteFile(String reportLocation, String localPath)
    {
    	try{
    		URL reportURL = new URL(reportLocation); 
			InputStream in = reportURL.openStream();
			
    		File downloadedFile = new File(localPath);

    		if (downloadedFile.exists())
    		{
    			downloadedFile.delete();
    		}
    		
    		FileOutputStream fout = new FileOutputStream(downloadedFile);
			
			byte buf[] = new byte[1024];
			int s = 0;
			long tl = 0;
			
			while((s = in.read(buf, 0, 1024)) > 0)
				fout.write(buf, 0, s);
			
    		in.close();
    		fout.flush();
    		fout.close();
    		return downloadedFile;
    	} catch (FileNotFoundException e) {
			if(reportLocation.indexOf("Subreport") == -1) // Only show the warning if it is not a subreport
				log.warning("404 not found: Report cannot be found on server "+ e.getMessage());
    		return null;
    	} catch (IOException e) {
			log.severe("I/O error when trying to download (sub)report from server "+ e.getMessage());
    		return null;
    	}
    }
    
	/**
	 * @param reportPath
	 * @return
	 * @throws Exception 
	 */
	private File getFileAsResource(String reportPath) throws Exception {
		File reportFile;
		String name = reportPath.substring("resource:".length()).trim();
		String localName = name.replace('/', '_');
		log.info("reportPath = " + reportPath);
		log.info("getting resource from = " + getClass().getClassLoader().getResource(name));
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
		String localFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + localName;
		log.info("localFile = " + localFile);
		reportFile = new File(localFile);

		OutputStream out = null;
		out = new FileOutputStream(reportFile);
		if (out != null){
			byte buf[]=new byte[1024];
			int len;
			while((len=inputStream.read(buf))>0)
				out.write(buf,0,len);
			out.close();
			inputStream.close();
		}

		return reportFile;
	}

	
	
}
