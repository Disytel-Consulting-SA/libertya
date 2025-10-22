package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import net.sf.jasperreports.engine.JRException;

import org.openXpertya.JasperReport.DataSource.SumasYSaldosJasperDataSource;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProject;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.Env;

public class LaunchSumasYSaldos extends SvrProcess {
	
	
	/** Cuenta inicio				*/
	private int			p_1_ElementValue_ID = 0;
	/** Cuenta Fin			*/
	private int			p_2_ElementValue_ID = 0;
	/** Date Acct From			*/
	private Timestamp	p_DateAcct_From = null;
	/** Date Acct To			*/
	private Timestamp	p_DateAcct_To = null;
	
	/** C_Project_ID			*/
	private int 		p_C_Project_ID = 0;
	
	/** Jasper Report			*/
	private int 		AD_JasperReport_ID;
	
	/** dREHER */
	private boolean 	p_groupByProjects = false;
	
	/** Nombre del informe	*/
	private final String p_reportName = "Sumas y Saldos";
	

	/**
	 * Captura los parametros
	 */
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			log.fine("prepare - " + para[i]);
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_ElementValue_ID"))	{
				p_1_ElementValue_ID = para[i].getParameterAsInt();
				p_2_ElementValue_ID = para[i].getParameter_ToAsInt();
			
			}
			else if (name.equals("DateAcct"))
			{
				p_DateAcct_From = (Timestamp)para[i].getParameter();
				p_DateAcct_To = (Timestamp)para[i].getParameter_To();
			}
			else if (name.equals("C_Project_ID"))	{
				p_C_Project_ID = para[i].getParameterAsInt();
			}
			else if(name.equalsIgnoreCase( "isGroupByProject" )){ // dREHER
				p_groupByProjects = ((String)para[i].getParameter()).equals("Y");
			}
			else
				log.severe("prepare - Unknown Parameter= " + name);
		}
		
		
		ProcessInfo base_pi=getProcessInfo();
		int AD_Process_ID=base_pi.getAD_Process_ID();
		MProcess proceso=MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport()!=true)
			return;
		AD_JasperReport_ID=proceso.getAD_JasperReport_ID();
	}

	
	
	protected String doIt() throws Exception {
		return createReport();
	}

	private String createReport()	throws Exception{
		MJasperReport jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
		
		 SumasYSaldosJasperDataSource ds = new SumasYSaldosJasperDataSource(getCtx(), p_DateAcct_From, p_DateAcct_To, 
				 p_1_ElementValue_ID, p_2_ElementValue_ID, 0, p_C_Project_ID, p_groupByProjects);
		
		 try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
			
			// Establecemos parametros
			jasperwrapper.addParameter("TEMPDIR", System.getProperty("java.io.tmpdir"));
			jasperwrapper.addParameter("orgName", (MClient.get(getCtx())).getName());
			
			// dREHER si se filtro un proyecto, mostrar cual es...
			if(p_C_Project_ID > 0) {
				MProject p = new MProject(Env.getCtx(), p_C_Project_ID, get_TrxName());
				jasperwrapper.addParameter("projectName", p.getName());
			}else
				jasperwrapper.addParameter("projectName", "Todos los Proyectos");
			
			try {
				jasperwrapper.fillReport(ds, this);
				
				jasperwrapper.showReport(getProcessInfo());
			}
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se han podido rellenar el informe.", e);
			}
			
			return "doIt";
	}
	
	// dREHER	
	protected int getC_Project_ID() {
		return p_C_Project_ID;
	}

	protected void setC_Project_ID(int C_Project_ID) {
		this.p_C_Project_ID = C_Project_ID;
	}
	//

}
