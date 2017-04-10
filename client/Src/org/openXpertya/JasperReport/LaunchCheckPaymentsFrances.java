package org.openXpertya.JasperReport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openXpertya.JasperReport.DataSource.CheckPaymentFrancesDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MCheckPrinting;
import org.openXpertya.model.MCheckPrintingLines;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.StringUtil;

/**
 * Reporte para impresión de cheques del Banco Frances.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchCheckPaymentsFrances extends SvrProcess {

	// Parámetros del reporte
	private final static String IMPORTE = "IMPORTE";
	private final static String EMISION_DIA = "EMISION_DIA";
	private final static String EMISION_MES = "EMISION_MES";
	private final static String EMISION_ANIO = "EMISION_ANIO";
	private final static String EMISION_MES_NAME = "EMISION_MES_NAME";
	private final static String PAGO_DIA = "PAGO_DIA";
	private final static String PAGO_MES = "PAGO_MES";
	private final static String PAGO_ANIO = "PAGO_ANIO";
	private final static String PAGO_MES_NAME = "PAGO_MES_NAME";
	private final static String A_LA_ORDEN = "A_LA_ORDEN";
	private final static String IMPORTE_EN_LETRAS = "IMPORTE_EN_LETRAS";
	private final static String CENTAVOS = "CENTAVOS";
	private final static String FECHA_VENCIMIENTO = "FECHA_VENCIMIENTO";

	/** Jasper Report. */
	private int AD_JasperReport_ID;

	/** Jasper Report Wrapper */
	MJasperReport jasperwrapper;

	@Override
	protected void prepare() {

		// Determinar JasperReport para wrapper, tabla y registro actual
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if (proceso.isJasperReport() != true) {
			return;
		}
		AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
		jasperwrapper = new MJasperReport(getCtx(), AD_JasperReport_ID, get_TrxName());
	}

	@Override
	protected String doIt() throws Exception {
		MCheckPrinting checkPrinting = new MCheckPrinting(getCtx(), getRecord_ID(), null);

		OXPJasperDataSource ds = new CheckPaymentFrancesDataSource(get_TrxName());

		try {
			ds.loadData();
		} catch (RuntimeException e) {
			throw new RuntimeException("No se pueden cargar los datos del informe", e);
		}

		addReportParameters(jasperwrapper, checkPrinting);

		try {
			jasperwrapper.fillReport(ds);
			jasperwrapper.showReport(getProcessInfo());
		} catch (RuntimeException e) {
			throw new RuntimeException("No se ha podido rellenar el informe.", e);
		}
		return "";
	}

	protected void addReportParameters(MJasperReport jasperwrapper, MCheckPrinting checkPrinting) throws Exception {

		// Indice de grupo de parámetros.
		// Cada cheque de la hoja es un grupo diferente.
		int group = 1;
		long dueDateLong;
		Calendar dateEmissionCheckCalendar = Calendar.getInstance();
		Calendar dueDateCalendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		for (MCheckPrintingLines line : checkPrinting.getLines()) {

			if (line.getDateEmissionCheck() != null) {
				dateEmissionCheckCalendar.setTimeInMillis(line.getDateEmissionCheck().getTime());
			}
			
			dueDateLong = line.getDateTrx().getTime();
			if(line.getPayment().getDueDate() != null){
				dueDateLong = line.getPayment().getDueDate().getTime();
			}
			dueDateCalendar.setTimeInMillis(dueDateLong);

			jasperwrapper.addParameter(IMPORTE + group, String.valueOf(line.getPayAmt()));
			// La fecha de emisión del cheque, no es un campo obligatorio, por 
			// lo tanto se puede dar el caso en el que este dato no esté presente.
			if (line.getDateEmissionCheck() != null) {
				jasperwrapper.addParameter(EMISION_DIA + group, String.valueOf(dateEmissionCheckCalendar.get(Calendar.DAY_OF_MONTH)));
				jasperwrapper.addParameter(EMISION_MES + group, String.valueOf(dateEmissionCheckCalendar.get(Calendar.MONTH)));
				jasperwrapper.addParameter(EMISION_ANIO + group, String.valueOf(dateEmissionCheckCalendar.get(Calendar.YEAR)));
				jasperwrapper.addParameter(EMISION_MES_NAME + group, StringUtil.fuc(dateEmissionCheckCalendar
						.getDisplayName(Calendar.MONTH, Calendar.LONG, Language.getLoginLanguage().getLocale())));
			}
			jasperwrapper.addParameter(PAGO_DIA + group, String.valueOf(dueDateCalendar.get(Calendar.DAY_OF_MONTH)));
			jasperwrapper.addParameter(PAGO_MES + group, String.valueOf(dueDateCalendar.get(Calendar.MONTH)));
			jasperwrapper.addParameter(PAGO_ANIO + group, String.valueOf(dueDateCalendar.get(Calendar.YEAR)));
			jasperwrapper.addParameter(PAGO_MES_NAME + group, StringUtil.fuc(dueDateCalendar
					.getDisplayName(Calendar.MONTH, Calendar.LONG, Language.getLoginLanguage().getLocale())));
			// El nombre no es un campo obligatorio, por lo tanto se 
			// puede dar el caso en el que este dato no esté presente.
			if (line.getPayment().getA_Name() != null) {
				jasperwrapper.addParameter(A_LA_ORDEN + group, String.valueOf(line.getPayment().getA_Name()));
			}
			jasperwrapper.addParameter(IMPORTE_EN_LETRAS + group,
					String.valueOf(NumeroCastellano.numeroACastellano(line.getPayAmt(), false)));
			jasperwrapper.addParameter(CENTAVOS + group, String.valueOf(line.getPayAmtCents()));
			// La fecha de vencimiento del cheque, no es un campo obligatorio, por 
			// lo tanto se puede dar el caso en el que este dato no esté presente.
			if (line.getPayment().getDueDate() != null) {
				jasperwrapper.addParameter(FECHA_VENCIMIENTO + group, String.valueOf(formatter.format(line.getPayment().getDueDate())));
			}

			group++;
		}

	}

}
