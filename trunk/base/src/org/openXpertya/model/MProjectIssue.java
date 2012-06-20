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
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectIssue extends X_C_ProjectIssue {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectIssue_ID
     * @param trxName
     */

    public MProjectIssue( Properties ctx,int C_ProjectIssue_ID,String trxName ) {
        super( ctx,C_ProjectIssue_ID,trxName );
    }    // MProjectIssue

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectIssue( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectIssue

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     */

    public MProjectIssue( MProject project ) {
        super( project.getCtx(),0,project.get_TrxName());
        setClientOrg( project.getAD_Client_ID(),project.getAD_Org_ID());
        setC_Project_ID( project.getC_Project_ID());    // Parent

        // setC_ProjectIssue_ID (0);                                               //      PK

        setLine( getNextLine());

        //

        setM_Locator_ID( 0 );
        setM_Product_ID( 0 );

        //

        setMovementDate( new Timestamp( System.currentTimeMillis()));
        setMovementQty( Env.ZERO );
        setPosted( false );
        setProcessed( false );
    }    // MProjectIssue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getNextLine() {
        return DB.getSQLValue( get_TrxName(),"SELECT COALESCE(MAX(Line),0)+10 FROM C_ProjectIssue WHERE C_Project_ID=?",getC_Project_ID());
    }    // getLineFromProject

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param MovementQty
     */

    public void setMandatory( int M_Locator_ID,int M_Product_ID,BigDecimal MovementQty ) {
        setM_Locator_ID( M_Locator_ID );
        setM_Product_ID( M_Product_ID );
        setMovementQty( MovementQty );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean process() {
        if( !save()) {
            return false;
        }

        if( getM_Product_ID() == 0 ) {
            log.log( Level.SEVERE,"process - No Product" );

            return false;
        }

        MProduct product = MProduct.get( getCtx(),getM_Product_ID());

        // If not a stocked Item nothing to do

        if( product.isStocked()) {
            setProcessed( true );

            return save();
        }

        // **      Create Material Transactions **

        MTransaction mTrx = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_WorkOrderPlus,getM_Locator_ID(),getM_Product_ID(),getM_AttributeSetInstance_ID(),getMovementQty().negate(),getMovementDate(),get_TrxName());

        mTrx.setC_ProjectIssue_ID( getC_ProjectIssue_ID());

        //

        MLocator loc = MLocator.get( getCtx(),getM_Locator_ID());

        if( MStorage.add( getCtx(),getM_Locator_ID(),loc.getM_Warehouse_ID(),getM_Product_ID(),getM_AttributeSetInstance_ID(),getM_AttributeSetInstance_ID(),getMovementQty().negate(),null,null,get_TrxName())) {
            if( mTrx.save( get_TrxName())) {
                setProcessed( true );

                if( save()) {
                    return true;
                } else {
                    log.log( Level.SEVERE,"Issue not saved" );    // requires trx !!
                }
            } else {
                log.log( Level.SEVERE,"Transaction not saved" );    // requires trx !!
            }
        } else {
            log.log( Level.SEVERE,"Storage not updated" );    // OK
        }

        //

        return false;
    }    // process
}    // MProjectIssue



/*
 *  @(#)MProjectIssue.java   02.07.07
 * 
 *  Fin del fichero MProjectIssue.java
 *  
 *  Versión 2.2
 *
 */
