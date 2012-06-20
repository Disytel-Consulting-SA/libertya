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

import org.openXpertya.model.MInventory;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InventoryCountUpdate extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Inventory_ID = 0;

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
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_M_Inventory_ID = getRecord_ID();
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
        log.info( "M_Inventory_ID=" + p_M_Inventory_ID );

        MInventory inventory = new MInventory( getCtx(),p_M_Inventory_ID,get_TrxName());

        if( inventory.getID() == 0 ) {
            throw new ErrorOXPSystem( "Not found: M_Inventory_ID=" + p_M_Inventory_ID );
        }

        String sql = "UPDATE M_InventoryLine l SET QtyBook = (SELECT QtyOnHand FROM M_Storage s  WHERE s.M_Product_ID=l.M_Product_ID AND s.M_Locator_ID=l.M_Locator_ID AND COALESCE(s.M_AttributeSetInstance_ID,0)=COALESCE(l.M_AttributeSetInstance_ID,0)), QtyCount = (SELECT QtyOnHand FROM M_Storage s  WHERE s.M_Product_ID=l.M_Product_ID AND s.M_Locator_ID=l.M_Locator_ID AND COALESCE(s.M_AttributeSetInstance_ID,0)=COALESCE(l.M_AttributeSetInstance_ID,0)), Updated=SysDate, UpdatedBy=" + getAD_User_ID()

        + " WHERE M_Inventory_ID=" + p_M_Inventory_ID + " AND EXISTS (SELECT * FROM M_Storage s " + "WHERE s.M_Product_ID=l.M_Product_ID AND s.M_Locator_ID=l.M_Locator_ID" + " AND COALESCE(s.M_AttributeSetInstance_ID,0)=COALESCE(l.M_AttributeSetInstance_ID,0))";

        // log.fine("doIt - " + sql);

        int no = DB.executeUpdate( sql,get_TrxName());

        // Multiple Lines for one item

        sql = "UPDATE M_InventoryLine SET IsActive='N' " + "WHERE M_Inventory_ID=" + p_M_Inventory_ID + " AND (M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID) IN " + "(SELECT M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID " + "FROM M_InventoryLine " + "WHERE M_Inventory_ID=" + p_M_Inventory_ID + " GROUP BY M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID " + "HAVING COUNT(*) > 1)";

        int multiple = DB.executeUpdate( sql,get_TrxName());

        if( multiple > 0 ) {
            return "@M_InventoryLine_ID@ - #" + no + " --> @InventoryProductMultiple@";
        }

        return "@M_InventoryLine_ID@ - #" + no;
    }    // doIt
}    // InventoryCountUpdate



/*
 *  @(#)InventoryCountUpdate.java   02.07.07
 * 
 *  Fin del fichero InventoryCountUpdate.java
 *  
 *  Versión 2.2
 *
 */
