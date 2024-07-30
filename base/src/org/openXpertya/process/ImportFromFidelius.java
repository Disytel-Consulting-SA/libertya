package org.openXpertya.process;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.process.customImport.fidelius.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.fidelius.jobs.Import;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Importación desde fidelius, basado en CentralPos
 * @author Original Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 * dREHER
 */
public class ImportFromFidelius extends SvrProcess {

	private ImportSettlements createSettlementsProcess;

	private int p_M_EntidadFinanciera_ID; // Entidad financiera.
	private String p_CreditCardType; // Tipo de tarjeta.
	private Timestamp p_Date_From; // Fecha desde.
	private Timestamp p_Date_To; // Fecha hasta.
	private Timestamp p_Hoy; // Fecha actual.
	private int p_DaysFromBack; // Días Desde hacia atrás.
	private int p_DaysToBack; // Días Hasta hacia atrás.
	private String p_TipoDatos; // "liquidaciones" / "cupones / Pendietes" (L/C/P)
	private String p_Estado; // "estado filtro: false=filtra por fecha de venta, true=filtra por fecha de pago
	private boolean p_IsGenerarLiquidacion = false; // Genera registros en liquidacion ? por defecto NO, solo quedan en las tablas de importacion.
	private String p_OrgName = ""; // Nombre de la organizacion en Fidelius
	
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
			} else if (name.equalsIgnoreCase("DaysFromBack")) {
				p_DaysFromBack = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("DaysToBack")) {
				p_DaysToBack = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("Type")) {
				p_TipoDatos = (String) params[i].getParameter();
			} else if (name.equalsIgnoreCase("Estado")) {
				p_Estado = (String) params[i].getParameter();
			} else if (name.equalsIgnoreCase("CreditCardType")) {
				p_CreditCardType = (String) params[i].getParameter();
			} else if (name.equalsIgnoreCase("EntidadFinanciera")) {
				p_M_EntidadFinanciera_ID = params[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("GeneraRegistrosTarjetas")) {
				p_IsGenerarLiquidacion = ((String) params[i].getParameter()).equals("Y");
			} else if (name.equalsIgnoreCase("OrgName")) {
					p_OrgName = (String) params[i].getParameter();
			} else {
				log.log(Level.SEVERE, "ImportFromFidelius.prepare - Unknown Parameter: " + name);
			}
		}
		
		/**
		 * Fidelius NO permite filtrar por Tarjeta, trae todas las tarjetas e identifica en cada
		 * registro a que tarjeta pertenece la liquidacion.
		 * 
		 * El servicio externo se configura como "Fidelius"
		 */
		if(p_CreditCardType == null)
			p_CreditCardType = "Fidelius";
		
		// por defecto filtra por fecha de venta
		if(p_Estado == null) {
			int C_ExternalService_ID = Utilidades.getExternalServiceByName("Fidelius");
			p_Estado = Utilidades.getDataExtraSE(C_ExternalService_ID, "estado");
			if(p_Estado == null)
				p_Estado = "false";
		}
		
		// Proceso para crear los registros de liquidacion en borrador...
		if(p_IsGenerarLiquidacion)
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

		/** 
		 * Si se recibe como parametro la cantidad de dias hacia atras para calcular
		 * fecha desde, calcula rango de fechas a partir de este parametro e ignora
		 * el rango de fechas seleccionado en el proceso...
		*/ 
		if (p_DaysFromBack > 0) {
			
			if (p_Hoy == null) {
				long currentMilis = new Date().getTime();
				p_Hoy = new Timestamp(currentMilis);
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(p_Hoy);
			cal.add(Calendar.DATE, -p_DaysFromBack);

			p_Date_From = new Timestamp(cal.getTimeInMillis());
			
			if (p_DaysToBack > 0) {
				
				cal.setTime(p_Hoy);
				cal.add(Calendar.DATE, -p_DaysToBack);

				p_Date_To = new Timestamp(cal.getTimeInMillis());
			}else
				p_Date_To = p_Hoy;
				
		}
		
		if (p_M_EntidadFinanciera_ID > 0) {
			// TODO definir qué hacer con este filtro.
		}

		importJob.setDateFromParam(p_Date_From);
		importJob.setDateToParam(p_Date_To);
		importJob.setType(p_TipoDatos);
		importJob.setEstado(p_Estado);

		// Si llega el parametro de nombre comercio (orgName para fidelius) setearlo al trabajo de importacion
		if(!Util.isEmpty(p_OrgName, true)) {
			log.info("Seteo el nombre de comercio: " + p_OrgName);
			importJob.setOrgName(p_OrgName);
		}
		
		String msg = "";
		try {
			msg = importJob.excecute();
		} catch (SaveFromAPIException e) {
			msg = e.getMessage();
			if(msg==null)
				msg = "Error al guardar informacion desde Fidelius!";
			
			log.warning(msg);
			
			return msg;
		} catch (Exception e) {
			msg = e.getMessage();
			if(msg==null)
				msg = "Error al leer desde Fidelius, verifique los parametros por favor!";
			
			log.warning(msg);
			
			return msg;
		} 
		
		// Solo genera los movimientos en las tablas de liquidaciones de tarjetas/cupones/pendientes si se paso el parametro correspondiente en TRUE
		// Para CentralPoS se importaba directamente, para Fidelius NO
		// Solo se deja el codigo por compatibilidad con el proceso de CentralPoS
		if(p_IsGenerarLiquidacion) {
			boolean processSuccess = createSettlementsProcess.startProcess(getCtx(), getProcessInfo(), getTrx(get_TrxName()));

			if (!processSuccess) {
				throw new Exception( createSettlementsProcess.getProcessInfo().getSummary() + " - "
						+ createSettlementsProcess.getProcessInfo().getLogInfo());
			}
			else {
				msg = createSettlementsProcess.getProcessInfo().getSummary();
			}
		}
		
		return msg;
	}

}
