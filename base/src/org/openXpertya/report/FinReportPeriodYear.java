package org.openXpertya.report;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openXpertya.util.DB;

public class FinReportPeriodYear extends FinReportPeriod {
	
	
	// Constructores
	
	public FinReportPeriodYear(Date StartDate, Date EndDate,int relativeOffset) {
		super(StartDate, EndDate, relativeOffset);
	}

	public FinReportPeriodYear(Date StartDate, Date EndDate) {
		super(StartDate, EndDate);
	}

	/**
	 * Seteo las fechas en base al período actual que se encuentran dentro de los calendarios
	 * @param inicio calendario inicio
	 * @param fin calendario fin
	 */
	protected void calcularPeriodo(Calendar inicio, Calendar fin){
		// Incremento o decremento el año, según sea el offset
		inicio.add(Calendar.YEAR, this.getOffset());
		fin.add(Calendar.YEAR, this.getOffset());
		
		// Seteo el mes Enero, dia 1 y hora, minuto y segundos en 0
		inicio.set(inicio.get(Calendar.YEAR), 0, 1,0,0,0);		
	}
	
    /**
     * Confecciona una condicion de where de la consulta en base a los periodos
     * @return condicion del where 
     */
	public String getWhere(){
		StringBuffer sql = new StringBuffer( "BETWEEN " );
        sql.append( DB.TO_DATE( new Timestamp(this.getM_StartDate().getTime()) )).append( " AND " ).append( DB.TO_DATE( new Timestamp(getM_EndDate().getTime()) ));

        return sql.toString();
	}
	
	
}
