/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.report;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class FinReportPeriod {

	
	/**
     * Constructor de la clase ...
     *
     *
     * @param C_Period_ID
     * @param Name
     * @param StartDate
     * @param EndDate
     * @param YearStartDate
     */

    public FinReportPeriod( Date StartDate,Date EndDate, int relativeOffset) {
        //m_C_Period_ID   = C_Period_ID;
        //m_Name          = Name;
        setM_StartDate(StartDate);
        setM_EndDate(EndDate);
        setOffset(relativeOffset);
        this.setDates();
    }    //
    
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param C_Period_ID
     * @param Name
     * @param StartDate
     * @param EndDate
     * @param YearStartDate
     */

    public FinReportPeriod( Date StartDate,Date EndDate) {
    	this(StartDate,EndDate,0);
    }    //
    

    /** Descripción de Campos */

    //private int m_C_Period_ID;

    /** Descripción de Campos */

    //private String m_Name;

    /** Descripción de Campos */

    private Date m_StartDate;

    /** Descripción de Campos */

    private Date m_EndDate;

    /** Offset relativo de las fechas */
    
    private int offset;

    
    /**
     * Setea todas las variables de fechas para luego armar los where
     * @param offset offset del período relativo
     */
    private void setDates(){
    	// Creo los calendarios para poder incrementar o decrementar las fechas de inicio y fin
		Calendar fechaInicio = new GregorianCalendar();
		Calendar fechaFin = new GregorianCalendar();
		
		// Seteo las fechas de inicio y fin
		fechaInicio.setTime(this.getM_StartDate());
		fechaFin.setTime(this.getM_EndDate());
		
		// Calcular período
		this.calcularPeriodo(fechaInicio,fechaFin);
		
		// Seteo la fecha inicio y la fecha de fin con las fechas de los calendarios
		this.setM_StartDate(fechaInicio.getTime());
		this.setM_EndDate(fechaFin.getTime());
    }
    
    /**
     * Calculo el período correspondiente
     * @param inicio calendario inicio
     * @param fin calendario fin
     */
    protected void calcularPeriodo(Calendar inicio,Calendar fin){
    	// Obtengo la diferencia de días
    	int diferencia = this.diferenciaDias();
    	
    	// Obtengo el número de días a sumar
    	int nroASumar = diferencia * this.getOffset();
    	
    	// Sumo la cantidad de días (recordar que offset puede ser negativo)
    	inicio.add(Calendar.DATE, nroASumar);
    	fin.add(Calendar.DATE, nroASumar);
    }
    
    
    /**
     * Obtener la diferencia de días
     * @return días de diferencia
     */
    public int diferenciaDias(){
    	long fechaInicialMs = this.getM_StartDate().getTime();
    	long fechaFinalMs = this.getM_EndDate().getTime();
    	long diferencia = fechaFinalMs - fechaInicialMs;
    	double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
    	return ( (int) dias + 2);
    }
    
    /**
     * Verifica si un date se encuentra dentro del rango
     * @param date
     * @return
     */
    public boolean inPeriod( Date date ) {
        if( date == null ) {
            return false;
        }

        if( date.before( getM_StartDate() )) {
            return false;
        }

        if( date.after( getM_EndDate() )) {
            return false;
        }

        return true;
    }    // inPeriod
    
    
    // Métodos abstractos
    
    /**
     * Confecciona una condicion de where de la consulta en base a los periodos
     * @return condicion del where 
     */
    public abstract String getWhere();
    

    // Getters y Setters
    
    
	protected void setM_StartDate(Date m_StartDate) {
		this.m_StartDate = m_StartDate;
	}


	protected Date getM_StartDate() {
		return m_StartDate;
	}


	protected void setM_EndDate(Date m_EndDate) {
		this.m_EndDate = m_EndDate;
	}


	protected Date getM_EndDate() {
		return m_EndDate;
	}


	protected void setOffset(int offset) {
		this.offset = offset;
	}


	protected int getOffset() {
		return offset;
	}
}    // FinReportPeriod



/*
 *  @(#)FinReportPeriod.java   02.07.07
 * 
 *  Fin del fichero FinReportPeriod.java
 *  
 *  Versión 2.2
 *
 */
