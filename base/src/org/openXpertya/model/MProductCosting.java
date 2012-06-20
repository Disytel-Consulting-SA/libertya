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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductCosting extends X_M_Product_Costing {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static MProductCosting[] getOfProduct( Properties ctx,int M_Product_ID,String trxName ) {
        String            sql   = "SELECT * FROM M_Product_Costing WHERE M_Product_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProductCosting( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getOfProduct",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        MProductCosting[] retValue = new MProductCosting[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfProduct

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MProductCosting.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MProductCosting( Properties ctx,int ignored,String trxName ) {
        super( ctx,ignored,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        } else {

            // setM_Product_ID (0);
            // setC_AcctSchema_ID (0);
            //

            setCostAverage( Env.ZERO );
            setCostAverageCumAmt( Env.ZERO );
            setCostAverageCumQty( Env.ZERO );
            setCostStandard( Env.ZERO );
            setCostStandardCumAmt( Env.ZERO );
            setCostStandardCumQty( Env.ZERO );
            setCostStandardPOAmt( Env.ZERO );
            setCostStandardPOQty( Env.ZERO );
            setCurrentCostPrice( Env.ZERO );
            setFutureCostPrice( Env.ZERO );
            setPriceLastInv( Env.ZERO );
            setPriceLastPO( Env.ZERO );
            setTotalInvAmt( Env.ZERO );
            setTotalInvQty( Env.ZERO );
        }
    }    // MProductCosting

    /**
     * Constructor de la clase ...
     *
     *
     * @param product
     * @param C_AcctSchema_ID
     */

    public MProductCosting( MProduct product,int C_AcctSchema_ID ) {
        super( product.getCtx(),0,product.get_TrxName());
        setClientOrg( product );
        setM_Product_ID( product.getM_Product_ID());
        setC_AcctSchema_ID( C_AcctSchema_ID );
    }    // MProductCosting

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductCosting( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductCosting
}    // MProductCosting



/*
 *  @(#)MProductCosting.java   02.07.07
 * 
 *  Fin del fichero MProductCosting.java
 *  
 *  Versión 2.2
 *
 */
