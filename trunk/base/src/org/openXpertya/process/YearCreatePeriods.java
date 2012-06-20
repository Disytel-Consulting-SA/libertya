/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MYear;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;


/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class YearCreatePeriods extends SvrProcess {

    /** Descripci�n de Campos */

    private int p_C_Year_ID = 0;

    /**
     * Descripci�n de M�todo
     *
     */

    protected void prepare() {
        p_C_Year_ID = getRecord_ID();
    }    // prepare
    

    /**
     * Descripci�n de M�todo
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
    	
        MYear year = new MYear( getCtx(),p_C_Year_ID,get_TrxName());
        
        // Desarrollado por Dataware SL
        // Inicio
        String sql = "SELECT * FROM c_period WHERE c_year_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,p_C_Year_ID);
            
            ResultSet rs = pstmt.executeQuery();

            if ( rs.next()) {
                return "@Error@";
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Process - YearCreatePeriod ",e );
            return "@Error@";
        } 
        // Fin
        // Desarrollado por Dataware SL
        
        if( (p_C_Year_ID == 0) || (year.getID() != p_C_Year_ID) ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @C_Year_ID@ - " + p_C_Year_ID );
        }

        log.info( year.toString());

        //

        if( year.createStdPeriods( null )) {
            return "@OK@";
        }

        return "@Error@";
    }    // doIt
}    // YearCreatePeriods



/*
 *  @(#)YearCreatePeriods.java   02.07.07
 * 
 *  Fin del fichero YearCreatePeriods.java
 *  
 *  Versión 2.2
 *
 */