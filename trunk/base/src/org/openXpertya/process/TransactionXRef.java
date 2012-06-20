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

import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TransactionXRef extends SvrProcess {

    /** Descripción de Campos */

    private int p_Search_InOut_ID = 0;

    /** Descripción de Campos */

    private int p_Search_Order_ID = 0;

    /** Descripción de Campos */

    private int p_Search_Invoice_ID = 0;

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
            } else if( name.equals( "Search_InOut_ID" )) {
                p_Search_InOut_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "Search_Order_ID" )) {
                p_Search_Order_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "Search_Invoice_ID" )) {
                p_Search_Invoice_ID = para[ i ].getParameterAsInt();
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
        log.info( "M_InOut_ID=" + p_Search_InOut_ID + ", C_Order_ID=" + p_Search_Order_ID + ", C_Invoice_ID=" + p_Search_Invoice_ID );

        //

        if( p_Search_InOut_ID != 0 ) {
            insertTrx( "SELECT NVL(ma.M_AttributeSetInstance_ID,iol.M_AttributeSetInstance_ID) " + "FROM M_InOutLine iol" + " LEFT OUTER JOIN M_InOutLineMA ma ON (iol.M_InOutLine_ID=ma.M_InOutLine_ID) " + "WHERE M_InOut_ID=" + p_Search_InOut_ID );
        } else if( p_Search_Order_ID != 0 ) {
            insertTrx( "SELECT NVL(ma.M_AttributeSetInstance_ID,iol.M_AttributeSetInstance_ID) " + "FROM M_InOutLine iol" + " LEFT OUTER JOIN M_InOutLineMA ma ON (iol.M_InOutLine_ID=ma.M_InOutLine_ID) " + " INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID)" + "WHERE io.C_Order_ID=" + p_Search_Order_ID );
        } else if( p_Search_Invoice_ID != 0 ) {
            insertTrx( "SELECT NVL(ma.M_AttributeSetInstance_ID,iol.M_AttributeSetInstance_ID) " + "FROM M_InOutLine iol" + " LEFT OUTER JOIN M_InOutLineMA ma ON (iol.M_InOutLine_ID=ma.M_InOutLine_ID) " + " INNER JOIN C_InvoiceLine il ON (iol.M_InOutLine_ID=il.M_InOutLine_ID) " + "WHERE il.C_Invoice_ID=" + p_Search_Invoice_ID );
        } else {
            throw new ErrorUsuarioOXP( "Select one Parameter" );
        }

        //

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param sqlSubSelect
     */

    private void insertTrx( String sqlSubSelect ) {
        String sql = "INSERT INTO T_Transaction " + "(AD_PInstance_ID, M_Transaction_ID," + " AD_Client_ID, AD_Org_ID, IsActive, Created,CreatedBy, Updated,UpdatedBy," + " MovementType, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID," + " MovementDate, MovementQty," + " M_InOutLine_ID, M_InOut_ID," + " M_MovementLine_ID, M_Movement_ID," + " M_InventoryLine_ID, M_Inventory_ID, " + " C_ProjectIssue_ID, C_Project_ID, " + " M_ProductionLine_ID, M_Production_ID, " + " Search_Order_ID, Search_Invoice_ID, Search_InOut_ID) "

        // Data

        + "SELECT " + getAD_PInstance_ID() + ", M_Transaction_ID," + " AD_Client_ID, AD_Org_ID, IsActive, Created,CreatedBy, Updated,UpdatedBy," + " MovementType, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID," + " MovementDate, MovementQty," + " M_InOutLine_ID, M_InOut_ID, " + " M_MovementLine_ID, M_Movement_ID," + " M_InventoryLine_ID, M_Inventory_ID, " + " C_ProjectIssue_ID, C_Project_ID, " + " M_ProductionLine_ID, M_Production_ID, "

        // Parameter

        + p_Search_Order_ID + ", " + p_Search_Invoice_ID + "," + p_Search_InOut_ID + " "

        //

        + "FROM M_Transaction_v " + "WHERE M_AttributeSetInstance_ID > 0 AND M_AttributeSetInstance_ID IN (" + sqlSubSelect + ") ORDER BY M_Transaction_ID";

        //

        int no = DB.executeUpdate( sql );

        log.fine( sql );
        log.config( "#" + no );

        // Multi-Level

    }    // insertTrx
}    // TransactionXRef



/*
 *  @(#)TransactionXRef.java   02.07.07
 * 
 *  Fin del fichero TransactionXRef.java
 *  
 *  Versión 2.2
 *
 */
