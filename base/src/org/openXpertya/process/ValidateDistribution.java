package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.openXpertya.util.DB;


/**
 * Descripción de Clase
 *
 * Permite validar la base distributiva de elementos contables segun proyectos y %
 * 
 *
 * @version    20240117
 * @author     dREHER    
 */

public class ValidateDistribution extends SvrProcess {

    /** Descripción de Campos */

    protected int p_C_ElementValue_ID = 0;
    
    
    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_ElementValue_ID" )) {
            	p_C_ElementValue_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
    	
        int p_C_ElementValue_ID = getRecord_ID();

        log.info( "From C_ElementValue_ID=" + p_C_ElementValue_ID );

        if( p_C_ElementValue_ID == 0 ) {
            throw new IllegalArgumentException( "From p_C_ElementValue_ID == 0" );
        }
        
        Double total = 0.00;

        String sqlX = " SELECT sum(percentage) AS porcentaje"
        		+ " FROM c_distributivebase cd "
        		+ " WHERE isActive='Y' AND cd.c_elementvalue_id = " + p_C_ElementValue_ID;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

        	pstmt = DB.prepareStatement(sqlX.toString(), null);
        	rs = pstmt.executeQuery();

        	if(rs.next()) {
        		total = rs.getDouble("porcentaje");
        	}

        } catch (Exception e) { 
        } finally {
        	try {
        		if (rs != null) rs.close();
        		if (pstmt != null) pstmt.close();
        	} catch (SQLException e) {}
        }

        if( Math.abs( 100 - Math.abs(total) ) > 0.1) // dREHER
        	throw new Exception("El total del porcentaje debe sumar 100! Total=" + total);

        // Si llego hasta aca, valido OK la distribucion de este elemento!
        /*
        LP_C_ElementValue ev = new LP_C_ElementValue(Env.getCtx(), p_C_ElementValue_ID, get_TrxName());
        ev.setCintolo_ValidatedDistribution(true);
        if(!ev.save(get_TrxName()))
        	throw new Exception("No se pudo guardar la validacion de Base Distributiva!");
        */
        
        int tmp = DB.executeUpdate("UPDATE C_ElementValue SET Cintolo_ValidatedDistribution='Y' WHERE C_ElementValue_ID=" + p_C_ElementValue_ID, false);
        if(tmp<1)
        	throw new Exception("No se pudo guardar la validacion de Base Distributiva!");
        
        return "Distribucion validada Ok!";
    }    // doIt
    
}    // 



/*
 *  @(#)ValidateDistribution.java   2024-01-17
 * 
 *  Fin del fichero ValidateDistribution.java
 *  
 *  Versión 1.0
 *
 */
