package org.openXpertya.process;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.jobs.Import;
import org.openXpertya.process.customImport.centralPos.jobs.ImportAmex;
import org.openXpertya.process.customImport.centralPos.jobs.ImportFirstData;
import org.openXpertya.process.customImport.centralPos.jobs.ImportNaranja;
import org.openXpertya.process.customImport.centralPos.jobs.ImportVisa;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación desde CentralPos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFromCentralPos extends SvrProcess {

	private int p_M_EntidadFinanciera_ID; // Entidad financiera.
	private String p_CreditCardType; // Tipo de tarjeta.
	private Timestamp p_Date_From; // Fecha desde.
	private Timestamp p_Date_To; // Fecha hasta.
	private int p_DaysBack; // Días hacia atrás.

	@Override
	protected void prepare() {
		ProcessInfoParameter[] params = getParameter();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getParameterName();
			if (params[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("Date")) {
				p_Date_From = (Timestamp) params[i].getParameter();
				p_Date_To = (Timestamp) params[i].getParameter_To();
			} else if (name.equalsIgnoreCase("DaysBack")) {
				p_DaysBack = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("CreditCardType")) {
				p_CreditCardType = (String) params[i].getParameter();
			} else if (name.equalsIgnoreCase("EntidadFinanciera")) {
				p_M_EntidadFinanciera_ID = params[i].getParameterAsInt();
			} else {
				log.log(Level.SEVERE, "ImportFromCentralPos.prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		Import importJob = null;

		if (p_CreditCardType != null) {
			if (p_CreditCardType.equals(CentralPosImport.FIRSTDATA)) {
				importJob = new ImportFirstData(getCtx(), get_TrxName());
			} else if (p_CreditCardType.equals(CentralPosImport.NARANJA)) {
				importJob = new ImportNaranja(getCtx(), get_TrxName());
			} else if (p_CreditCardType.equals(CentralPosImport.AMEX)) {
				importJob = new ImportAmex(getCtx(), get_TrxName());
			} else if (p_CreditCardType.equals(CentralPosImport.VISA)) {
				importJob = new ImportVisa(getCtx(), get_TrxName());
			} else {
				throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "UnknownCreditCardTypeParam"));
			}
		} else {
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "InvalidCreditCardTypeParam"));
		}

		// Llegado a este punto, importJob no es null.
		importJob.setCLogger(log);

		// TODO definir qué fecha es la que se debe utilizar para cada caso,
		// según el tipo de tarjeta. Actualmente asigné campos de fecha a mi
		// criterio.

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (p_DaysBack > 0) {
			if (p_Date_To == null) {
				long currentMilis = new Date().getTime();
				p_Date_To = new Timestamp(currentMilis);
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(p_Date_To);
			cal.add(Calendar.DATE, -p_DaysBack);

			p_Date_From = new Timestamp(cal.getTimeInMillis());
		}
		if (p_M_EntidadFinanciera_ID > 0) {
			// TODO definir qué hacer con este filtro.
		}

		String msg = "";

		if (p_CreditCardType.equals(CentralPosImport.FIRSTDATA)) {

			if (p_Date_From != null) {
				importJob.addParam("fecha_presentacion-min", sdf.format(new Date(p_Date_From.getTime())));
			}
			if (p_Date_To != null) {
				importJob.addParam("fecha_presentacion-max", sdf.format(new Date(p_Date_To.getTime())));
			}
			try {
				msg = importJob.excecute();
			} catch (SaveFromAPIException e) {
				return e.getMessage();
			}

		} else if (p_CreditCardType.equals(CentralPosImport.NARANJA)) {

			if (p_Date_From != null) {
				importJob.addParam("fecha_pago-min", sdf.format(new Date(p_Date_From.getTime())));
			}
			if (p_Date_To != null) {
				importJob.addParam("fecha_pago-max", sdf.format(new Date(p_Date_To.getTime())));
			}
			try {
				msg = importJob.excecute();
			} catch (SaveFromAPIException e) {
				return e.getMessage();
			}

		} else if (p_CreditCardType.equals(CentralPosImport.AMEX)) {

			if (p_Date_From != null) {
				importJob.addParam("fecha_pago-min", sdf.format(new Date(p_Date_From.getTime())));
			}
			if (p_Date_To != null) {
				importJob.addParam("fecha_pago-max", sdf.format(new Date(p_Date_To.getTime())));
			}
			try {
				msg = importJob.excecute();
			} catch (SaveFromAPIException e) {
				return e.getMessage();
			}

		} else if (p_CreditCardType.equals(CentralPosImport.VISA)) {

			if (p_Date_From != null) {
				importJob.addParam("fpres-min", sdf.format(new Date(p_Date_From.getTime())));
			}
			if (p_Date_To != null) {
				importJob.addParam("fpres-max", sdf.format(new Date(p_Date_To.getTime())));
			}
			try {
				msg = importJob.excecute();
			} catch (SaveFromAPIException e) {
				return e.getMessage();
			}

		}
		return msg;
	}

}
