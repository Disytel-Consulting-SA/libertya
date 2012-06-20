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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MResourceAssignment extends X_S_ResourceAssignment {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param S_ResourceAssignment_ID
     * @param trxName
     */

    public MResourceAssignment( Properties ctx,int S_ResourceAssignment_ID,String trxName ) {
        super( ctx,S_ResourceAssignment_ID,trxName );
        p_info.setUpdateable( true );    // default table is not updateable

        // Default values

        if( S_ResourceAssignment_ID == 0 ) {
            setAssignDateFrom( new Timestamp( System.currentTimeMillis()));
            setQty( new BigDecimal( 1.0 ));
            setName( "." );
            setIsConfirmed( false );
        }
    }    // MResourceAssignment

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MResourceAssignment( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MResourceAssignment

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

        /*
         * v_Description := :new.Name;
         * IF (:new.Description IS NOT NULL AND LENGTH(:new.Description) > 0) THEN
         * v_Description := v_Description || ' (' || :new.Description || ')';
         * END IF;
         *
         * -- Update Expense Line
         * UPDATE S_TimeExpenseLine
         * SET  Description = v_Description,
         * Qty = :new.Qty
         * WHERE S_ResourceAssignment_ID = :new.S_ResourceAssignment_ID
         * AND (Description <> v_Description OR Qty <> :new.Qty);
         *
         * -- Update Order Line
         * UPDATE C_OrderLine
         * SET  Description = v_Description,
         * QtyOrdered = :new.Qty
         * WHERE S_ResourceAssignment_ID = :new.S_ResourceAssignment_ID
         * AND (Description <> v_Description OR QtyOrdered <> :new.Qty);
         *
         * -- Update Invoice Line
         * UPDATE C_InvoiceLine
         * SET  Description = v_Description,
         * QtyInvoiced = :new.Qty
         * WHERE S_ResourceAssignment_ID = :new.S_ResourceAssignment_ID
         * AND (Description <> v_Description OR QtyInvoiced <> :new.Qty);
         */

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MResourceAssignment[ID=" );

        sb.append( getID()).append( ",S_Resource_ID=" ).append( getS_Resource_ID()).append( ",From=" ).append( getAssignDateFrom()).append( ",To=" ).append( getAssignDateTo()).append( ",Qty=" ).append( getQty()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        // allow to delete, when not confirmed

        if( isConfirmed()) {
            return false;
        }

        return true;
    }    // beforeDelete
}    // MResourceAssignment



/*
 *  @(#)MResourceAssignment.java   02.07.07
 * 
 *  Fin del fichero MResourceAssignment.java
 *  
 *  Versión 2.2
 *
 */
