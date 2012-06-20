/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 *  
 */

package org.openXpertya.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Env;

public class CReportLine extends MReportLine {
    
	/** Valores de cada columna que compone el informe	*/
	private BigDecimal m_col_values[];
	
	/**	Ha sido procesado ya por FinReport	*/
	private boolean isProcessed = false;
	
	/** Informacion de las columnas de la linea	*/
	private MReportColumn[] m_columns;
	
	
	/**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_ReportLine_ID
     * @param trxName
     */

    public CReportLine( Properties ctx,int PA_ReportLine_ID,String trxName ) {
        super( ctx,PA_ReportLine_ID, trxName );
    }    // MReportLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public CReportLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs, trxName );
    }    // MReportLine

    /**
     * Inicializa el numero de columnas de la linea
     * @param Cols
     */
    public void initCols(MReportColumn[] columns)	{
    	m_columns = columns;
    	
    	m_col_values = new BigDecimal[m_columns.length];
    	
    	// Ponemos todos sus valores a 0, o sea, las columnas dentro de cada línea
    	for (int i=0; i< m_columns.length; i++)	{
    		m_col_values [ i ] = Env.ZERO;
    	}
    	
    }

    /**
     * Establece el valor de una columna.
     * @param index
     * @param value
     */
    public void setColValue(int index, BigDecimal value)		{
    	if (value == null)	{
    		value = Env.ZERO;
    	}
    	
    	if (m_columns [ index ].isPrinted() && isNegativeAsZero())	{
    		if (value.compareTo(Env.ZERO) == -1 )	{
    			value = Env.ZERO;
    		}
    	}
    	
    	m_col_values[ index ] = value;
    }
    
    
    /**
     * Obtiene el valor de una columna.
     * @param index
     * @return
     */
    public BigDecimal getColValue(int index)	{
    	BigDecimal val =  m_col_values[ index ] ;
    	if (val == null)	{
    		return Env.ZERO;
    	}
    	return val;
    }

    
    public boolean isProcessed()	{
    	return isProcessed;
    }
    
    public void setProcessed(boolean isproc)	{
    	isProcessed = isproc;
    }
    
    
}





