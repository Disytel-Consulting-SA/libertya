package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.DiarioMayorJasperDataSource;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.Env;


public class LaunchMayor extends SvrProcess {

	/** Cuenta inicio				*/
	private int			p_1_ElementValue_ID = 0;
	/** Cuenta Fin			*/
	private int			p_2_ElementValue_ID = 0;
	/** Date Acct From			*/
	private Timestamp	p_DateAcct_From = null;
	/** Date Acct To			*/
	private Timestamp	p_DateAcct_To = null;
	
	
	/** Jasper Report			*/
	private int 		AD_JasperReport_ID;
	
	/** Nombre del informe	*/
	private final String p_reportName = "Diario Mayor";
	
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
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
			else
				log.severe("prepare - Unknown Parameter: " + name);
		}
		
		
		ProcessInfo base_pi=getProcessInfo();
		int AD_Process_ID=base_pi.getAD_Process_ID();
		MProcess proceso=MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport()!=true)
			return;
		AD_JasperReport_ID=proceso.getAD_JasperReport_ID();

		
	}	//	prepare
	

	
	protected String doIt() throws Exception {
		MJasperReport jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
		
		DiarioMayorJasperDataSource ds = new DiarioMayorJasperDataSource(getCtx(), p_DateAcct_From, p_DateAcct_To, p_1_ElementValue_ID, p_2_ElementValue_ID);
		
		try {
			ds.loadData();
		}
		catch (RuntimeException e)	{
			throw new RuntimeException("No se pueden cargar los datos del informe", e);
		}
		
		
		// Establecemos parametros
		jasperwrapper.addParameter("TEMPDIR", System.getProperty("java.io.tmpdir"));
		jasperwrapper.addParameter("orgName", (MClient.get(getCtx())).getName());
		
		try {
			jasperwrapper.fillReport(ds);
			
			jasperwrapper.showReport(getProcessInfo());
		}
			
		catch (RuntimeException e)	{
			throw new RuntimeException ("No se han podido rellenar el informe.", e);
		}
		
		return "";

	}
	
}
