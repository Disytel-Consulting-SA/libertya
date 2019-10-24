package org.openXpertya.process;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.jobs.Import;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación desde CentralPos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFromCentralPos extends SvrProcess {

	private ImportSettlements createSettlementsProcess;

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
		createSettlementsProcess = new ImportSettlements();
	}

	@Override
	protected String doIt() throws Exception {
		Import importJob = Import.get(getCtx(), p_CreditCardType, get_TrxName());
		if(importJob == null){
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "UnknownCreditCardTypeParam"));
		}

		// Llegado a este punto, importJob no es null.
		importJob.setCLogger(log);

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

		importJob.setDateFromParam(p_Date_From);
		importJob.setDateToParam(p_Date_To);
		
		String msg = "";
		try {
			msg = importJob.excecute();
		} catch (SaveFromAPIException e) {
			return e.getMessage();
		} catch (Exception e) {
			return e.getMessage();
		} 
		
		boolean processSuccess = createSettlementsProcess.startProcess(getCtx(), getProcessInfo(), getTrx(get_TrxName()));

		if (!processSuccess) {
			throw new Exception( createSettlementsProcess.getProcessInfo().getSummary() + " - "
					+ createSettlementsProcess.getProcessInfo().getLogInfo());
		}
		else {
			msg = createSettlementsProcess.getProcessInfo().getSummary();
		}
		return msg;
	}

}
