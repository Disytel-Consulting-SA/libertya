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



package org.openXpertya.process;

import java.util.logging.Level;

import org.openXpertya.model.MQuarter;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Quarter_Replenish extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public Quarter_Replenish() {
        super();
    }    // Quarter_Process

    /** Descripción de Campos */

    private int p_M_Warehouse_ID = 0;

    /** Descripción de Campos */

    private String p_Replenishtype = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	log.fine("En Quarter_Replenish, en el prepare");
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_Warehouse_ID" )) {
                p_M_Warehouse_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "Replenishtype" )) {
                p_Replenishtype = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
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
        log.info( "Quarter_Replenish" );
        log.fine("En Quarter_Replenish, en el doIt()");
        if( super.getRecord_ID() < 1 ) {
            throw new Exception( MSG_SaveErrorRowNotFound );
        }

        MQuarter to_MQ = new MQuarter( getCtx(),super.getRecord_ID(),null );
        String   sql   = "UPDATE M_Replenish SET replenishtype=" + p_Replenishtype + " , level_min=" + to_MQ.getIncomings() + " , level_max=" + to_MQ.getIncomings() + " WHERE M_Warehouse_ID=" + p_M_Warehouse_ID + " AND M_Product_ID=" + to_MQ.getM_Product_ID();
        int done = DB.executeUpdate( sql );

        if( done == 0 ) {
            sql = "INSERT INTO M_Replenish (M_Product_ID, M_Warehouse_Id, AD_Client_ID, AD_Org_ID," + " CreatedBy, UpdatedBy, ReplenishType, Level_Min, Level_Max)" + " VALUES (" + to_MQ.getM_Product_ID() + ", " + p_M_Warehouse_ID + ", " + to_MQ.getAD_Client_ID() + ", " + to_MQ.getAD_Org_ID() + ", " + to_MQ.getCreatedBy() + ", " + to_MQ.getUpdatedBy() + ", " + p_Replenishtype + ", " + to_MQ.getIncomings() + ", " + to_MQ.getIncomings() + ")";
            done = DB.executeUpdate( sql );
        }

        return "@Calculated@=" + done;
    }    // doIt
}    // ReportLineSet_Copy



/*
 *  @(#)Quarter_Replenish.java   02.07.07
 * 
 *  Fin del fichero Quarter_Replenish.java
 *  
 *  Versión 2.2
 *
 */
