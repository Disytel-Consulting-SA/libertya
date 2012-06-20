/**
 *   
 * Codigo original de Indeos Consultoria S.L. para el proyecto OpenXpertya 2007
 *  
 */

package org.openXpertya.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_T_Report;

public class MTReport extends X_T_Report {

    /**
     * Constructor
     *
     *
     * @param ctx
     * @param PA_Report_ID
     * @param trxName
     */

    public MTReport( Properties ctx,int PA_Report_ID,String trxName ) {
        super( ctx,PA_Report_ID, trxName );
    }    // MReport

    /**
     * Constructor
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTReport( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs, trxName );
    }
    
    /**
     * Guarda el valor de la columna dada.
     * @param col
     * @param value
     */
    public void setColumnValue(int col, BigDecimal value)	{
    	String name = "Col_" + col;
    	set_ValueNoCheck(name, value);
    }
    
	
}
