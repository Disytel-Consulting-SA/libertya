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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInventoryLineMA extends X_M_InventoryLineMA {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_InventoryLine_ID
     * @param trxName
     *
     * @return
     */

    public static MInventoryLineMA[] get( Properties ctx,int M_InventoryLine_ID,String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_InventoryLineMA WHERE M_InventoryLine_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_InventoryLine_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInventoryLineMA( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MInventoryLineMA[] retValue = new MInventoryLineMA[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param M_Inventory_ID
     * @param trxName
     *
     * @return
     */

    public static int deleteInventoryMA( int M_Inventory_ID,String trxName ) {
        String sql = "DELETE FROM M_InventoryLineMA ma WHERE EXISTS " + "(SELECT * FROM M_InventoryLine l WHERE l.M_InventoryLine_ID=ma.M_InventoryLine_ID" + " AND M_Inventory_ID=" + M_Inventory_ID + ")";

        return DB.executeUpdate( sql,trxName );
    }    // deleteInventoryMA

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInventoryLineMA.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InventoryLineMA_ID
     * @param trxName
     */

    public MInventoryLineMA( Properties ctx,int M_InventoryLineMA_ID,String trxName ) {
        super( ctx,M_InventoryLineMA_ID,trxName );

        if( M_InventoryLineMA_ID != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MInventoryLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInventoryLineMA( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInventoryLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     */

    public MInventoryLineMA( MInventoryLine parent,int M_AttributeSetInstance_ID,BigDecimal MovementQty ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setM_InventoryLine_ID( parent.getM_InventoryLine_ID());

        //

        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setMovementQty( MovementQty );
    }    // MInventoryLineMA

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInventoryLineMA[" );

        sb.append( "M_InventoryLine_ID=" ).append( getM_InventoryLine_ID()).append( ",M_AttributeSetInstance_ID=" ).append( getM_AttributeSetInstance_ID()).append( ", Qty=" ).append( getMovementQty()).append( "]" );

        return sb.toString();
    }    // toString
}    // MInventoryLineMA



/*
 *  @(#)MInventoryLineMA.java   02.07.07
 * 
 *  Fin del fichero MInventoryLineMA.java
 *  
 *  Versión 2.2
 *
 */
