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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRecurringRun extends X_C_Recurring_Run {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Recurring_Run_ID
     * @param trxName
     */

    public MRecurringRun( Properties ctx,int C_Recurring_Run_ID,String trxName ) {
        super( ctx,C_Recurring_Run_ID,trxName );
    }    // MRecurringRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param recurring
     */

    public MRecurringRun( Properties ctx,MRecurring recurring ) {
        super( ctx,0,recurring.get_TrxName());

        if( recurring != null ) {
            setAD_Client_ID( recurring.getAD_Client_ID());
            setAD_Org_ID( recurring.getAD_Org_ID());
            setC_Recurring_ID( recurring.getC_Recurring_ID());
            setDateDoc( recurring.getDateNextRun());
        }
    }    // MRecurringRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRecurringRun( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRecurringRun
}    // MRecurringRun



/*
 *  @(#)MRecurringRun.java   02.07.07
 * 
 *  Fin del fichero MRecurringRun.java
 *  
 *  Versión 2.2
 *
 */
