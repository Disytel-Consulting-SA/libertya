package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JREmptyDataSource;

import org.openXpertya.JasperReport.DataSource.OrdenPagoDataSource;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class LaunchOrdenPago extends SvrProcess {

	/** Contexto de la aplicación */
	private Properties ctx = Env.getCtx();
	/** ID de la cabecera de la imputación de la Orden de Pago */
	private int p_C_AllocationHdr_ID = 0;
	/** ID del informe jasper */
	private int m_AD_JasperReport_ID = 0;
	
	/** Instancia de la Orden de Pago: Allocation Header que representa la OP */
	private MAllocationHdr mAllocationHdr;
	
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
        	Object pp = para[ i ].getParameter();
            String name = para[ i ].getParameterName();

            if( pp != null ) {
	            
            	if ( name.equalsIgnoreCase( "C_AllocationHdr_ID" )) {
	            	p_C_AllocationHdr_ID = para[ i ].getParameterAsInt();
            	} else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
            
            }
        }
        
        if (p_C_AllocationHdr_ID == 0)
        	p_C_AllocationHdr_ID  = getRecord_ID();
        
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if(proceso.isJasperReport()!=true)
			return;
		m_AD_JasperReport_ID=proceso.getAD_JasperReport_ID();
	}

	@Override
	protected String doIt() throws Exception {
		MJasperReport jasperWrapper = new MJasperReport(getCtx(), m_AD_JasperReport_ID, get_TrxName());
		OrdenPagoDataSource opDataSource = getDataSource();
		
		///////////////////////////////////////
		// Subreporte de Comprobantes pagados.
		MJasperReport documentsSubreport = getDocumentsSubreport(); 
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_DOCS", new ByteArrayInputStream(documentsSubreport.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_DOCS_DATASOURCE", opDataSource.getDocumentsDataSource());

		///////////////////////////////////////
		// Subreporte de cheques.
		MJasperReport checksSubreport = getChecksSubreport(); 
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_CHECKS", new ByteArrayInputStream(checksSubreport.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_CHECKS_DATASOURCE", opDataSource.getChecksDataSource());

		///////////////////////////////////////
		// Subreporte de Otros Medios de Pago.
		MJasperReport otherPayments = getOtherPaymentsSubreport(); 
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_OTHER_PAYMENTS", new ByteArrayInputStream(otherPayments.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_OTHER_PAYMENTS_DATASOURCE", opDataSource.getOtherPaymentsDataSource());
		
		// Se cargan los parámetros del reporte.
		loadReportParameters(jasperWrapper);
		
		try {
			jasperWrapper.fillReport(new JREmptyDataSource());
			jasperWrapper.showReport(getProcessInfo());
		} catch (RuntimeException e) {
			throw new Exception ("No se han podido rellenar el informe.", e);
		}
		return "doIt";
	}
	
	/**
	 * @return Retorna el MJasperReport del subreporte de comprobantes.
	 */
	protected MJasperReport getDocumentsSubreport() throws Exception {
		return getJasperReport("OrdenPago-Cbantes");
	}

	/**
	 * @return Retorna el MJasperReport del subreporte de cheques.
	 */
	protected MJasperReport getChecksSubreport() throws Exception {
		return getJasperReport("OrdenPago-Cheques");
	}
	
	/**
	 * @return Retorna el MJasperReport del subreporte de otros pagos.
	 */
	protected MJasperReport getOtherPaymentsSubreport() throws Exception {
		return getJasperReport("OrdenPago-OtrosMedios");
	}

	/**
	 * @return Retorna el MJasperReport con el nombre indicado.
	 */
	private MJasperReport getJasperReport(String name) throws Exception {
		Integer jasperReport_ID = 
			(Integer)DB.getSQLObject(get_TrxName(), "SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE Name ilike ?", new Object[] { name });
		if(jasperReport_ID == null || jasperReport_ID == 0)
			throw new Exception("Jasper Report not found - OrdenPago-Cheques");
		
		MJasperReport jasperReport = new MJasperReport(ctx, jasperReport_ID, get_TrxName());
		return jasperReport;
	}

	/**
	 * @return FactoryMethod: Retorna el data source del reporte.
	 */
	protected OrdenPagoDataSource getDataSource() {
		return new OrdenPagoDataSource(p_C_AllocationHdr_ID);
	}
	
	/**
	 * Carga los parámetros del reporte.
	 * @param jasperWrapper Reporte Jasper
	 */
	protected void loadReportParameters(MJasperReport jasperWrapper) {
		// Se obtienen los parámetros necesarios del reporte.
		MAllocationHdr op   = getAllocationHdr();
		String clientName   = (String)DB.getSQLValueString(get_TrxName(), "SELECT Name FROM AD_Client WHERE Ad_Client_ID = ?", op.getAD_Client_ID());
		String orgName      = (String)DB.getSQLValueString(get_TrxName(), "SELECT Name FROM AD_Org WHERE AD_Org_ID = ?", op.getAD_Org_ID());
		String cityName     = (String)DB.getSQLValueString(get_TrxName(), "SELECT city FROM ad_orginfo oi join c_location l on oi.c_location_id = l.c_location_id WHERE oi.ad_org_id = ?", op.getAD_Org_ID());
		Timestamp opDate    = op.getDateTrx();
		String opNumber     = op.getDocumentNo();
		String bPartner     =  (String)DB.getSQLValueString(get_TrxName(), "SELECT Name FROM C_BPartner WHERE C_BPartner_ID = ?", op.getC_BPartner_ID());
		BigDecimal opAmount = op.getGrandTotal();
		BigDecimal retencionesAmount = op.getRetencion_Amt();
		
		// Se asignan los parámetros al wrapper.
		jasperWrapper.addParameter("CLIENT_NAME", clientName);
		jasperWrapper.addParameter("ORG_NAME",    orgName);
		jasperWrapper.addParameter("CITY_NAME",   cityName);
		jasperWrapper.addParameter("OP_DATE",     opDate);
		jasperWrapper.addParameter("OP_NUMBER",   opNumber);
		jasperWrapper.addParameter("BPARTNER",    bPartner);
		jasperWrapper.addParameter("OP_AMOUNT",   opAmount);
		jasperWrapper.addParameter("RETENCIONES_AMOUNT", retencionesAmount);
	}
	
	protected int getAllocationHdrID() {
		return p_C_AllocationHdr_ID;
	}

	/**
	 * @return the AllocationHdr
	 */
	protected MAllocationHdr getAllocationHdr() {
		if (mAllocationHdr == null)
			mAllocationHdr = new MAllocationHdr(ctx, p_C_AllocationHdr_ID, get_TrxName());
		return mAllocationHdr;
	}
	
}
