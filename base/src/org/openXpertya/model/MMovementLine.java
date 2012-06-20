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

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MMovementLine extends X_M_MovementLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MovementLine_ID
     * @param trxName
     */

    public MMovementLine( Properties ctx,int M_MovementLine_ID,String trxName ) {
        super( ctx,M_MovementLine_ID,trxName );

        if( M_MovementLine_ID == 0 ) {

            // setM_LocatorTo_ID (0);  // @M_LocatorTo_ID@
            // setM_Locator_ID (0);    // @M_Locator_ID@
            // setM_MovementLine_ID (0);
            // setLine (0);
            // setM_Product_ID (0);

            setM_AttributeSetInstance_ID( 0 );    // ID
            setMovementQty( Env.ZERO );           // 1
            setTargetQty( Env.ZERO );             // 0
            setScrappedQty( Env.ZERO );
            setConfirmedQty( Env.ZERO );
            setProcessed( false );
        }
    }                                             // MMovementLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMovementLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMovementLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MMovementLine( MMovement parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setM_Movement_ID( parent.getM_Movement_ID());
    }    // MMovementLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_AttributeSetInstanceTo_ID() {
        int M_AttributeSetInstanceTo_ID = super.getM_AttributeSetInstanceTo_ID();

        if( M_AttributeSetInstanceTo_ID == 0 ) {
            M_AttributeSetInstanceTo_ID = super.getM_AttributeSetInstance_ID();
        }

        return M_AttributeSetInstanceTo_ID;
    }    // getM_AttributeSetInstanceTo_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_MovementLine WHERE M_Movement_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getM_Movement_ID());

            setLine( ii );
        }

        return true;
    }    // beforeSave
}    // MMovementLine



/*
 *  @(#)MMovementLine.java   02.07.07
 * 
 *  Fin del fichero MMovementLine.java
 *  
 *  Versión 2.2
 *
 */
