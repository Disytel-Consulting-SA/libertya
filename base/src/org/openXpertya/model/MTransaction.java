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

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTransaction extends X_M_Transaction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Transaction_ID
     * @param trxName
     */

    public MTransaction( Properties ctx,int M_Transaction_ID,String trxName ) {
        super( ctx,M_Transaction_ID,trxName );

        if( M_Transaction_ID == 0 ) {

            // setM_Transaction_ID (0);                //      PK
            // setM_Locator_ID (0);
            // setM_Product_ID (0);

            setMovementDate( new Timestamp( System.currentTimeMillis()));
            setMovementQty( Env.ZERO );

            // setMovementType (MOVEMENTTYPE_CustomerShipment);

        }
    }    // MTransaction

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTransaction( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTransaction

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param MovementType
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     * @param MovementDate
     * @param trxName
     */

    public MTransaction( Properties ctx,String MovementType,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,BigDecimal MovementQty,Timestamp MovementDate,String trxName ) {
        super( ctx,0,trxName );
        setMovementType( MovementType );

        if( M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "No Locator" );
        }

        setM_Locator_ID( M_Locator_ID );

        if( M_Product_ID == 0 ) {
            throw new IllegalArgumentException( "No Product" );
        }

        setM_Product_ID( M_Product_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );

        //

        if( MovementQty != null ) {    // Can be 0
            setMovementQty( MovementQty );
        }

        if( MovementDate == null ) {
            setMovementDate( new Timestamp( System.currentTimeMillis()));
        } else {
            setMovementDate( MovementDate );
        }
    }    // MTransaction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MTransaction[" );

        sb.append( getID()).append( "," ).append( getMovementType()).append( ",Qty=" ).append( getMovementQty()).append( ",M_Product_ID=" ).append( getM_Product_ID()).append( ",ASI=" ).append( getM_AttributeSetInstance_ID()).append( "]" );

        return sb.toString();
    }    // toString
}    // MTransaction



/*
 *  @(#)MTransaction.java   02.07.07
 * 
 *  Fin del fichero MTransaction.java
 *  
 *  Versión 2.2
 *
 */
