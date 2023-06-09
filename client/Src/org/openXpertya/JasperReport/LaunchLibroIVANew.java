package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.LibroIVANewDataSource;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

	public class LaunchLibroIVANew extends SvrProcess {

		/** Jasper Report			*/
		private int AD_JasperReport_ID;
		
		/** Table					*/
		private int AD_Table_ID;
		
		/** Date Acct From			*/
		protected Timestamp	p_dateFrom = null;
		/** Date Acct To			*/
		protected Timestamp	p_dateTo = null;
		
		protected String	p_transactionType = null;
		
		private int p_hoja;
		
		private String p_tipoLibro = null;
		
		protected int p_OrgID = -1;
		
		protected boolean groupCFInvoices = false;
		
		/** Nombre del informe	*/
		private final String p_reportName = "Libro de IVA";
		
		@Override
		protected void prepare() {

			// Determinar JasperReport para wrapper, tabla y registro actual
			ProcessInfo base_pi = getProcessInfo();
			int AD_Process_ID = base_pi.getAD_Process_ID();
			MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
			if(proceso.isJasperReport() != true)
				return;

			AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
			AD_Table_ID = getTable_ID();	
			
	        ProcessInfoParameter[] para = getParameter();
	        for( int i = 0;i < para.length;i++ ) {
	            String name = para[ i ].getParameterName();
	            if( para[ i ].getParameter() == null ) ;	            
	            else {
	            	if(name.equals("DateAcct"))
					{
						p_dateFrom = (Timestamp)para[i].getParameter();
						p_dateTo = (Timestamp)para[i].getParameter_To();
					}
		            if(name.equals("IsSOTrx"))
		            {
		            	p_transactionType = (String)para[ i ].getParameter();
		            }
		            if(name.equals("AD_Org_ID"))
		            {
		            	BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
		            	p_OrgID = tmp == null ? null : tmp.intValue();
		            }
		            if(name.equals("Hoja"))
		            {
		            	BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
		            	p_hoja = tmp == null ? null : tmp.intValue();
		            }
		            if(name.equals("GroupCFInvoices"))
		            {
		            	String tmp = ( String )para[ i ].getParameter();
		            	groupCFInvoices = tmp == null ? false : tmp.equalsIgnoreCase("Y");
		            }
	            }
	        }
			
		}
		
		@Override
		protected String doIt() throws Exception {
			return createReport();
		}

		private String createReport() throws Exception	{
						
			MJasperReport jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
			
			p_tipoLibro="";
			
			LibroIVANewDataSource ds = getDataSource();
			
			try {
				ds.loadData();
			}
			catch (RuntimeException e)	{
				throw new RuntimeException("No se pueden cargar los datos del informe", e);
			}
			
			// Agregar subreportes
			addSubReports(jasperwrapper, ds);			
			
			// Establecemos parametros
		 	Integer clientID = Env.getAD_Client_ID(getCtx());
			jasperwrapper.addParameter("AD_Client_ID", clientID);
			jasperwrapper.addParameter("TOTALCOMPROBANTES", ds.getTotalFacturado());
			jasperwrapper.addParameter("TOTALIMPORTES", ds.getTotalIVA());
			jasperwrapper.addParameter("TOTALGRAVADOS", ds.getTotalGravado());
			jasperwrapper.addParameter("TOTALNOGRAVADOS", ds.getTotalNoGravado());
			jasperwrapper.addParameter("HOJA", p_hoja);

			jasperwrapper.addParameter("COMPANIA", JasperReportsUtil.getClientName(getCtx(), clientID));
			jasperwrapper.addParameter("LOCALIZACION", ds.getLocalizacion(clientID));
			if(!Util.isEmpty(p_OrgID, true)){
				jasperwrapper.addParameter("ORG_NAME",
						JasperReportsUtil.getOrgName(getCtx(), p_OrgID));
				jasperwrapper.addParameter("ORG_LOCALIZATION",
						JasperReportsUtil.getLocalizacion(getCtx(), clientID,
								p_OrgID, get_TrxName()));
			}
			jasperwrapper.addParameter("REPORT_NAME", p_reportName);
			jasperwrapper.addParameter("FECHADESDE", (Date)p_dateFrom);
			jasperwrapper.addParameter("FECHAHASTA",(Date) p_dateTo);
			if(!p_transactionType.equals("B")){
            	 //Si es transacción de ventas, C = Customer(Cliente)
            	 if(p_transactionType.equals("C")){
            		 p_tipoLibro = "VENTAS";
            	 }
            	 else{
            		//Si es transacción de compra
            		 p_tipoLibro="COMPRAS";
            	 }
             }
			jasperwrapper.addParameter("TIPOLIBRO", p_tipoLibro);
			
			try {
				jasperwrapper.fillReport(ds, this);
				jasperwrapper.showReport(getProcessInfo());
			}
				
			catch (RuntimeException e)	{
				throw new RuntimeException ("No se ha podido rellenar el informe.", e);
			}
			
			return "doIt";
		}
		
		protected MJasperReport getTaxSubreport() throws Exception {
			return getJasperReport("Tax - Libro IVA");
		}
		
		protected MJasperReport getTotalSubreport() throws Exception {
			return getJasperReport("Total - Libro IVA");
		}
		
		protected MJasperReport getManualTaxSubreport() throws Exception {
			return getJasperReport("Manual Tax - Libro IVA");
		}
		
		/**
		 * @return Retorna el MJasperReport con el nombre indicado.
		 */
		protected MJasperReport getJasperReport(String name) throws Exception {
			Integer jasperReport_ID = 
				(Integer) DB.getSQLValue(get_TrxName(),
						"SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE Name ilike '" + name + "'");
			if(jasperReport_ID == null || jasperReport_ID == 0)
				throw new Exception("Jasper Report not found - "+name);
			
			MJasperReport jasperReport = new MJasperReport(getCtx(), jasperReport_ID, get_TrxName());
			return jasperReport;
		}
		
		protected LibroIVANewDataSource getDataSource() {
			return new LibroIVANewDataSource(getCtx(),
					(Date) p_dateFrom, (Date) p_dateTo, p_transactionType, p_OrgID,
					groupCFInvoices, get_TrxName());
		}
		
		/**
		 * Agregar los subreportes del informe
		 * @param jasperwrapper
		 * @param ds
		 * @throws Exception
		 */
		protected void addSubReports(MJasperReport jasperwrapper, LibroIVANewDataSource ds) throws Exception {
			///////////////////////////////////////
			// Subreporte de Impuestos.
			MJasperReport taxSubreport = getTaxSubreport(); 
			// Se agrega el informe compilado como parámetro.
			jasperwrapper.addParameter("COMPILED_SUBREPORT_TAXS", new ByteArrayInputStream(taxSubreport.getBinaryData()));
			// Se agrega el datasource del subreporte.
			jasperwrapper.addParameter("SUBREPORT_TAXS_DATASOURCE", ds.getAutomaticTaxDataSource());
			///////////////////////////////////////
			// Subreporte de Totales 
			MJasperReport documentsSubreport = getTotalSubreport(); 
			// Se agrega el informe compilado como parámetro.
			jasperwrapper.addParameter("COMPILED_SUBREPORT_TOTAL", new ByteArrayInputStream(documentsSubreport.getBinaryData()));
			if(!Util.isEmpty(ds.getTotalGeneralDataSource().getReportLines())) {
				jasperwrapper.addParameter("SUBREPORT_TOTAL_GENERAL_DATASOURCE", ds.getTotalGeneralDataSource());
			}
			///////////////////////////////////////
			// Subreporte de Totales de Créditos
			// Se agrega el datasource del subreporte.
			if(!Util.isEmpty(ds.getTotalCreditsDataSource().getReportLines())) {
				jasperwrapper.addParameter("SUBREPORT_TOTAL_CREDITS_DATASOURCE", ds.getTotalCreditsDataSource());
			}
			///////////////////////////////////////
			// Subreporte de Totales de Débitos
			// Se agrega el datasource del subreporte.
			if(!Util.isEmpty(ds.getTotalDebitsDataSource().getReportLines())) {
				jasperwrapper.addParameter("SUBREPORT_TOTAL_DEBITS_DATASOURCE", ds.getTotalDebitsDataSource());			
			}
			///////////////////////////////////////
			// Subreporte de Impuestos Manuales
			MJasperReport manualTaxSubreport = getManualTaxSubreport(); 
			// Se agrega el informe compilado como parámetro.
			jasperwrapper.addParameter("COMPILED_MANUAL_SUBREPORT_TAXS",
					new ByteArrayInputStream(manualTaxSubreport.getBinaryData()));
			// Se agrega el datasource del subreporte de percepciones
			if(!Util.isEmpty(ds.getPercepcionesTaxDataSource().getReportLines())) {
				jasperwrapper.addParameter("SUBREPORT_PERCEPCIONES_DATASOURCE", ds.getPercepcionesTaxDataSource());
			}
			// Se agrega el datasource del subreporte de impuestos manuales
			if(!Util.isEmpty(ds.getManualTaxDataSource().getReportLines())) {
				jasperwrapper.addParameter("SUBREPORT_MANUAL_DATASOURCE", ds.getManualTaxDataSource());
			}
		}
}
