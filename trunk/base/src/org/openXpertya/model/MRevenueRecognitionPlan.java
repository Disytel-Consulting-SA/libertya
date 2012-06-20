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



package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRevenueRecognitionPlan extends X_C_RevenueRecognition_Plan {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RevenueRecognition_Plan_ID
     * @param trxName
     */

    public MRevenueRecognitionPlan( Properties ctx,int C_RevenueRecognition_Plan_ID,String trxName ) {
        super( ctx,C_RevenueRecognition_Plan_ID,trxName );

        if( C_RevenueRecognition_Plan_ID == 0 ) {

            // setC_AcctSchema_ID (0);
            // setC_Currency_ID (0);
            // setC_InvoiceLine_ID (0);
            // setC_RevenueRecognition_ID (0);
            // setC_RevenueRecognition_Plan_ID (0);
            // setP_Revenue_Acct (0);
            // setUnEarnedRevenue_Acct (0);

            setTotalAmt( Env.ZERO );
            setRecognizedAmt( Env.ZERO );
        }
    }    // MRevenueRecognitionPlan

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRevenueRecognitionPlan( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRevenueRecognitionPlan

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( newRecord ) {
            MRevenueRecognition rr = new MRevenueRecognition( getCtx(),getC_RevenueRecognition_ID(),get_TrxName());

            if( rr.isTimeBased()) {}
        }

        return success;
    }    // afterSave
}    // MRevenueRecognitionPlan



/*
 *  @(#)MRevenueRecognitionPlan.java   02.07.07
 * 
 *  Fin del fichero MRevenueRecognitionPlan.java
 *  
 *  Versión 2.2
 *
 */
