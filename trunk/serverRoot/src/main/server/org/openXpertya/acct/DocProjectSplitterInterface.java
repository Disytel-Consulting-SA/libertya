package org.openXpertya.acct;

import java.math.BigDecimal;
import java.util.HashMap;



public interface DocProjectSplitterInterface {

	
	
    /**
     * Debe retorar una map con dos valores para el FactLine especificado:
     * 		1) "Project": el C_Project_ID correspondiente
     * 		2) "Percent": el porcentaje correspondiente en escala de valores entre 0 y 1 para dicho project.   
     */
	public HashMap<Integer, BigDecimal> getProjectPercentageQuery(FactLine factLine);
	
	
    /**
     * Debe devolver el número de proyectos distintos dentro de factLine
     */
	public int getProjectsInLinesQuery(FactLine factLine);
	
	
	
	/**
	 * Debe retornar si dicha linea necesita ser spliteada, según el tipo de documento que está siendo aplicado contablemente 
	 */
	public boolean requiresSplit(FactLine factLine);
	
}
