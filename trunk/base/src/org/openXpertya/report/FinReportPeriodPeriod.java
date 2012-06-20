package org.openXpertya.report;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openXpertya.util.DB;

public class FinReportPeriodPeriod extends FinReportPeriod {

	
	// COnstructores
	
	public FinReportPeriodPeriod(Date StartDate, Date EndDate,int relativeOffset) {
		super(StartDate, EndDate, relativeOffset);
	}

	public FinReportPeriodPeriod(Date StartDate, Date EndDate){
		super(StartDate, EndDate);
	}

	
	/**
	 * Seteo las fechas en base al período actual que se encuentran dentro de los calendarios
	 * @param inicio calendario inicio
	 * @param fin calendario fin
	 */
	protected void calcularPeriodo(Calendar inicio,Calendar fin){
		// La superclase hace lo que tiene que hacer el periodo
    	super.calcularPeriodo(inicio, fin);
	}
	
    /**
     * Confecciona una condicion de where de la consulta en base a los periodos
     * @return condicion del where 
     */
	public String getWhere(){
		StringBuffer sql = new StringBuffer( "BETWEEN " );
        sql.append( DB.TO_DATE( new Timestamp(getM_StartDate().getTime()))).append( " AND " ).append( DB.TO_DATE( new Timestamp(getM_EndDate().getTime())));

        return sql.toString();
	}

	
}
