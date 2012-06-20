package org.openXpertya.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ITime;
import org.openXpertya.util.Util;

public class MAmortization extends X_M_Amortization {

	/**
	 * @param ctx
	 *            contexto
	 * @param amortizationID
	 *            id de amortización de exclusión
	 * @param periodID
	 *            id del período actual dependiendo del tipo de aplicación
	 * @param trxName
	 *            transacción actual
	 * @return la amortización que corresponde para ese período, dependiendo del
	 *         tipo de aplicación del método actual del esquema contable. El
	 *         parámetro que corresponde al id de amortización es para
	 *         exclusión, o sea, el registro que se obtenga no debe tener el
	 *         mismo id al pasado como parámetro.
	 */
	public static MAmortization get(Properties ctx, Integer amortizationID, Integer periodID, String trxName){
		StringBuffer whereClause = new  StringBuffer(getPeriodColumnName(ctx) + " = ? AND ad_client_id = ?");
		List<Object> whereParams = new ArrayList<Object>();
		whereParams.add(periodID);
		whereParams.add(Env.getAD_Client_ID(ctx));
		if(!Util.isEmpty(amortizationID, true)){
			whereClause.append(" AND m_amortization_id <> ? ");
			whereParams.add(amortizationID);
		}
		return (MAmortization) PO.findFirst(ctx, X_M_Amortization.Table_Name,
				whereClause.toString(), whereParams.toArray(), null, trxName);
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param amortizationID
	 *            id de amortización de exclusión
	 * @param date
	 *            fecha
	 * @param trxName
	 *            transacción actual
	 * @return la amortización que corresponde para el período que incluye la
	 *         fecha parámetro, dependiendo también del tipo de aplicación del
	 *         método actual del esquema contable. El parámetro que corresponde
	 *         al id de amortización es para exclusión, o sea, el registro que
	 *         se obtenga no debe tener el mismo id al pasado como parámetro.
	 */
	public static MAmortization get(Properties ctx, Integer amortizationID, Timestamp date, String trxName){
		MAmortization amortization = null;
		Integer periodID = null;
		String appPeriod = Env.getContext(ctx, "$AmortizationAppPeriod");
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			MYear year = MYear.get(ctx, date, trxName);
			if(year != null){
				periodID = year.getID();
			}
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			MPeriod period = MPeriod.get(ctx, date);
			if(period != null){
				periodID = period.getID();
			}
		}
		if(!Util.isEmpty(periodID, true)){
			amortization = get(ctx, amortizationID, periodID, trxName);
		}
		return amortization;
	}

	/**
	 * Obtener una amortización en base al delta sumado al tiempo parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param amortizationID
	 *            id de amortización de excepción
	 * @param time
	 *            tiempo del período de la amortización a comparar
	 * @param delta
	 *            delta sumado al tiempo del período parámetro
	 * @param trxName
	 *            nombre de transacción actual
	 * @return amortización del delta sumando al tiempo parámetro
	 */
	public static MAmortization get(Properties ctx, Integer amortizationID, ITime time, Integer delta, String trxName){
		// Obtener la fecha real a partir del delta y de la fecha actual
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getDateFrom().getTime());
		calendar.add(time.getDateField(), delta);
		return MAmortization.get(ctx, amortizationID,
				new Timestamp(calendar.getTimeInMillis()), trxName);
	}
	
	/**
	 * Obtener una amortización en base al delta sumado al tiempo parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param time
	 *            tiempo del período de la amortización a comparar
	 * @param delta
	 *            delta sumado al tiempo del período parámetro
	 * @param trxName
	 *            nombre de transacción actual
	 * @return amortización del delta sumando al tiempo parámetro
	 */
	public static MAmortization get(Properties ctx, ITime time, Integer delta, String trxName){
		// Obtener la fecha real a partir del delta y de la fecha actual
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getDateFrom().getTime());
		calendar.add(time.getDateField(), delta);
		return MAmortization.get(ctx, 0,
				new Timestamp(calendar.getTimeInMillis()), trxName);
	}
	
	/**
	 * Obtiene el valor en la columna del período específico para la
	 * amortización parámetro. Por ejemplo si el período de aplicación es anual,
	 * entonces devuelve el valor que contiene la columna C_Year_ID.
	 * 
	 * @param amortization
	 *            amortización
	 * @return valor de la columna
	 */
	public static Integer getPeriodValueID(MAmortization amortization){
		String appPeriod = Env.getContext(amortization.getCtx(), "$AmortizationAppPeriod");
		Integer periodValueID = null; 
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			periodValueID = amortization.getC_Year_ID();
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			periodValueID = amortization.getC_Period_ID();
		}
		return periodValueID;
	}

	/**
	 * Obtiene el nombre de la tabla del período específico. Por ejemplo si el
	 * período de aplicación es anual, entonces devuelve el nombre de la tabla
	 * del período específico, en este caso C_Year.
	 * 
	 * @param ctx
	 *            contexto
	 * @return nombre de la tabla para el período
	 */
	public static String getPeriodTableName(Properties ctx){
		String appPeriod = Env.getContext(ctx, "$AmortizationAppPeriod");
		String periodTableName = null; 
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			periodTableName = X_C_Year.Table_Name;
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			periodTableName = X_C_Period.Table_Name;
		}
		return periodTableName;
	}

	/**
	 * Obtiene el nombre de la columna de la tabla M_Amortization del período
	 * específico. Por ejemplo si el período de aplicación es anual, entonces
	 * devuelve el nombre de la columna específico, en este caso C_Year_ID.
	 * 
	 * @param ctx
	 *            contexto
	 * @return nombre de la columna de la tabla M_Amortization para el período
	 */
	public static String getPeriodColumnName(Properties ctx){
		String appPeriod = Env.getContext(ctx, "$AmortizationAppPeriod");
		String periodColumnName = null; 
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			periodColumnName = X_C_Year.Table_Name;
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			periodColumnName = X_C_Period.Table_Name;
		}
		periodColumnName+="_ID";
		return periodColumnName;
	}

	/**
	 * Determina si la amortización correspondiente al período del delta sumado
	 * a la amortización parámetro está procesada. Por ejemplo, si la
	 * amortización parámetro es del año 2011 y el delta es -1, entonces se
	 * verifica si la del año 2010 se encuentra procesada.
	 * 
	 * @param ctx
	 *            contexto
	 * @param amortizationID
	 *            id de amortización base en busca de la amortización
	 *            correspondiente al delta
	 * @param delta
	 *            delta de período
	 * @param trxName
	 *            transacción actual
	 * @return true si la amortización correspondiente al delta existe y está
	 *         procesada, false si no existe o no se encuentra procesada
	 */
	public static boolean isAmortizationProcessed(Properties ctx, Integer amortizationID, Integer delta, String trxName){
		return isAmortizationProcessed(ctx, new MAmortization(ctx, amortizationID,
				trxName), delta, trxName);
	}

	/**
	 * Determina si la amortización correspondiente al período del delta sumado
	 * a la amortización parámetro está procesada. Por ejemplo, si la
	 * amortización parámetro es del año 2011 y el delta es -1, entonces se
	 * verifica si la del año 2010 se encuentra procesada.
	 * 
	 * @param amortization
	 *            amortización base en busca de la amortización correspondiente
	 *            al delta
	 * @param delta
	 *            delta de período
	 * @return true si la amortización correspondiente al delta existe y está
	 *         procesada, false si no existe o no se encuentra procesada
	 */
	public static boolean isAmortizationProcessed(Properties ctx, MAmortization amortization, Integer delta, String trxName){
		return isAmortizationProcessed(ctx,
				amortization.getID(),
				getPeriodValueID(amortization),
				getPeriodTableName(amortization.getCtx()), delta,
				trxName);
	}
	
	/**
	 * Determina si la amortización correspondiente al período del delta sumado
	 * a la amortización parámetro está procesada. Por ejemplo, si la
	 * amortización parámetro es del año 2011 y el delta es -1, entonces se
	 * verifica si la del año 2010 se encuentra procesada.
	 * 
	 * @param amortization
	 *            amortización base en busca de la amortización correspondiente
	 *            al delta
	 * @param delta
	 *            delta de período
	 * @return true si la amortización correspondiente al delta existe y está
	 *         procesada, false si no existe o no se encuentra procesada
	 */
	public static boolean isAmortizationProcessed(Properties ctx, Integer amortizationID, Integer periodID, String tableName, Integer delta, String trxName){
		return isAmortizationProcessed(ctx, amortizationID,
				getITime(ctx, periodID, trxName), delta, trxName);
	}
	
	/**
	 * 
	 * @param ctx
	 * @param amortizationID
	 * @param time
	 * @param delta
	 * @param trxName
	 * @return
	 */
	public static boolean isAmortizationProcessed(Properties ctx, Integer amortizationID, ITime time, Integer delta, String trxName){
		// Obtener la fecha real a partir del delta y de la fecha actual
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getDateFrom().getTime());
		calendar.add(time.getDateField(), delta);
		MAmortization amortization = MAmortization.get(ctx, amortizationID,
				new Timestamp(calendar.getTimeInMillis()), trxName);
		return amortization == null ? false : amortization
				.getRunAmortizationProcess().equals("Y");
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return cantidad de registros de amortizaciones para la compañía actual
	 */
	private static int getAmortizationsCount(Properties ctx, String trxName){
		return DB.getSQLValue(trxName,
			"SELECT coalesce(count(*),0) " +
			"FROM " + X_M_Amortization.Table_Name +
			" WHERE ad_client_id = ?", Env.getAD_Client_ID(ctx));
	}

	/**
	 * Obtener el tiempo del período que incluye a la fecha parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param date
	 *            fecha
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return tiempo del período que incluye a esa fecha o null si no existe
	 */
	public static ITime getITime(Properties ctx, Timestamp date, String trxName){
		Integer periodID = null;
		String tableName = null;
		ITime time = null;
		String appPeriod = Env.getContext(ctx, "$AmortizationAppPeriod");
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			MYear year = MYear.get(ctx, date, trxName);
			if(year != null){
				periodID = year.getID();
				tableName = X_C_Year.Table_Name;
			}
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			MPeriod period = MPeriod.get(ctx, date);
			if(period != null){
				periodID = period.getID();
				tableName = X_C_Period.Table_Name;
			}
		}
		if(!Util.isEmpty(periodID, true)){
			time = getITime(ctx, periodID, tableName, trxName);
		}
		return time;
	}
	
	/**
	 * Obtener el tiempo del período que incluye a la fecha parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param date
	 *            fecha
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return tiempo del período que incluye a esa fecha o null si no existe
	 */
	public static ITime getITime(Properties ctx, Date date, String trxName){
		Integer periodID = null;
		String tableName = null;
		ITime time = null;
		Timestamp thedate = new Timestamp(date.getTime());
		String appPeriod = Env.getContext(ctx, "$AmortizationAppPeriod");
		if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Annual)){
			MYear year = MYear.get(ctx, thedate, trxName);
			if(year != null){
				periodID = year.getID();
				tableName = X_C_Year.Table_Name;
			}
		} else if(appPeriod.equals(MAmortizationMethod.AMORTIZATIONAPPPERIOD_Monthly)){
			MPeriod period = MPeriod.get(ctx, thedate);
			if(period != null){
				periodID = period.getID();
				tableName = X_C_Period.Table_Name;
			}
		}
		if(!Util.isEmpty(periodID, true)){
			time = getITime(ctx, periodID, tableName, trxName);
		}
		return time;
	}

	/***
	 * Obtengo el tiempo de período parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param periodID
	 *            id del período
	 * @param trxName
	 *            nombre de la transacción
	 * @return el tiempo del período
	 */
	public static ITime getITime(Properties ctx, Integer periodID, String trxName){
		return getITime(ctx, periodID, getPeriodTableName(ctx), trxName);
	}

	/***
	 * Obtengo el tiempo de período del id parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param periodID
	 *            id de período
	 * @param tableName
	 *            nombre de tabla del período
	 * @param trxName
	 *            nombre de la transacción
	 * @return el tiempo del período
	 */
	public static ITime getITime(Properties ctx, Integer periodID, String tableName, String trxName){
		M_Table table = M_Table.get(ctx, tableName);
		return (ITime) table.getPO(periodID, trxName);
	}
	
	public MAmortization(Properties ctx, int M_Amortization_ID, String trxName) {
		super(ctx, M_Amortization_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAmortization(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	protected boolean beforeSave( boolean newRecord ) {
		// Validación para no permitir cargar una amortización si no tenemos un
		// método de amortización configurado en el esquema contable
		if (Util.isEmpty(
				Env.getContextAsInt(getCtx(), "$M_Amortization_Method_ID"),
				true)) {
			log.saveError("NotExistAmortizationMethodConfigured", "");
			return false;
		}
		// Validar que no exista una amortización para el mismo período
		if (MAmortization.get(getCtx(), getID(), getPeriodValueID(this),
				get_TrxName()) != null) {
			log.saveError("ExistsAmortizationInSamePeriod", "");
			return false;
		}
		// Validación de carga de amortización donde obligatoriamente debe
		// existir la amortización para el período anterior procesada
		if(!isAmortizationProcessed(getCtx(), this, -1, get_TrxName())){
			// Si es el primer registro a insertar, no se debe realizar la
			// validación indicada de período anterior procesado
			if(getAmortizationsCount(getCtx(), get_TrxName()) >= 1){
				log.saveError("BeforeAmortizationNotProcessed", "");
				return false;
			}
		}
		
		// Seteo el nombre con respecto al período actual
		if(newRecord){
			setName(getITime(getCtx(), getPeriodValueID(this), get_TrxName())
					.getITimeDescription());
			setC_DocType_ID(MDocType.getDocType(getCtx(),
					MDocType.DOCTYPE_Amortization, get_TrxName()).getID());
			setC_Currency_ID(Env.getContextAsInt(getCtx(), "$C_Currency_ID"));
		}
		
		return true;
	}
	
}
