package org.openXpertya.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class RenumeracionAsientosContables extends SvrProcess {
	private static final int NUMERATION_START_FROM_1 = 1;
	private static final int NUMERATION_CONTINUE = 0;

	/** Parámetro fecha desde. */
	private Date dateFrom;
	/** Parámetro fecha hasta. */
	private Date dateTo;
	/** Id de tipo de numeración seleccionado. */
	private int numerationId;

	@Override
	protected void prepare() {
		numerationId = -1;
		dateFrom = null;
		dateTo = null;

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equals("date")) {
				dateFrom = (Date) para[i].getParameter();
				dateTo = (Date) para[i].getParameter_To();
			} else if (name.equals("numeration")) {
				numerationId = para[i].getParameterAsInt();
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		// Obtengo el número de asiento de acuerdo al tipo de numeración elejido. 
		int number = getMaxNumber();
		// Renumero asientos
		if (renumberEntries(number))
			return getMsg("SuccessfulRenumbering");
		else
			return getMsg("UnsuccessfulRenumbering");
	}

	protected String getMsg(String msg) {
		return Msg.translate(getCtx(), msg);
	}

	/**
	 * Obtiene el número de asiento de acuerdo al tipo de numeración elejido.
	 * Iniciar desde 1: Devuelve 0 como número inicial.
	 * Continuar numeración: Devuelve el número de asiento máximo.
	 * @return número de asiento inicial.
	 * @throws Exception El tipo de numeración seleccionado es desconocido.
	 */
	private int getMaxNumber() throws Exception {
		if (numerationId == NUMERATION_CONTINUE) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COALESCE(MAX(journalno), 0) ");
			sql.append("FROM fact_acct ");
			int number = DB.getSQLValue(get_TrxName(), sql.toString());
			return number;
		}
		else if (numerationId == NUMERATION_START_FROM_1) {
			return 0;
		}
		else {
			String msg = "Unknown numeration type refference: " + numerationId;
			log.log(Level.SEVERE, "getMaxNumber - " + msg);
			throw new Exception(msg);
		}
	}

	/**
	 * Arma un filtro de fechas SQL a partir de los parámetros de fecha.
	 * @param field columna por la cual filtrar.
	 * @return un SQL que permite filtrar los resultados por fecha.
	 */
	private String dateFilter(String field) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// Rango de fecha no definida
		if (dateFrom == null && dateTo == null) {
			return " 1 = 1 ";
		}
		// Fecha desde no definida
		else if (dateFrom == null) {
			return " " + field + "::date <= '" + dateFormat.format(dateTo) + "'::date ";
		}
		// Fecha hasta no definida
		else if (dateTo == null) {
			return " " + field + "::date >= '" + dateFormat.format(dateFrom) + "'::date ";
		}
		// Rango definido
		else {
			return " " + field + "::date >= '" + dateFormat.format(dateFrom) + "'::date AND " +
					field + "::date <= '" + dateFormat.format(dateTo) + "'::date ";
		}
	}

	/**
	 * Ejecuta código SQL de actualización que renumera los asientos.
	 * @param number número inicial. Se enumerará desde (number + 1). 
	 * @return true si se realizaron cambios a filas de la tabla, false caso contrario.
	 */
	private boolean renumberEntries(int number) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	fact_acct f ");
		sql.append("SET ");
		sql.append("	journalno = d.groupno ");
		sql.append("FROM ");
		sql.append("	(SELECT ");
		sql.append("		fact_acct_id, ");
		sql.append("		DENSE_RANK() OVER (ORDER BY dateacct, ad_table_id,record_id ASC) + " + number + " AS groupno ");
		sql.append("	FROM ");
		sql.append("		fact_acct ");
		sql.append("	WHERE ");
		sql.append("		" + dateFilter("dateacct"));
		sql.append("		AND (amtacctcr <> 0 OR amtacctdr <> 0) ");
		sql.append("	) AS d ");
		sql.append("WHERE f.fact_acct_id = d.fact_acct_id ");
		sql.append("AND " + dateFilter("f.dateacct"));
		sql.append("AND (f.amtacctcr <> 0 OR f.amtacctdr <> 0) ");

		int rowsAffected = DB.executeUpdate(sql.toString(), get_TrxName());

		return rowsAffected != -1;
	}

}
