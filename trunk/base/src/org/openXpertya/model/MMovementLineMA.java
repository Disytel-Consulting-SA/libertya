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

public class MMovementLineMA extends X_M_MovementLineMA {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_MovementLine_ID
     * @param trxName
     *
     * @return
     */

    public static MMovementLineMA[] get( Properties ctx,int M_MovementLine_ID,String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_MovementLineMA WHERE M_MovementLine_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_MovementLine_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMovementLineMA( ctx,rs,trxName ));
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

        MMovementLineMA[] retValue = new MMovementLineMA[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param M_Movement_ID
     * @param trxName
     *
     * @return
     */

    public static int deleteMovementMA( int M_Movement_ID,String trxName ) {
        String sql = "DELETE FROM M_MovementLineMA ma WHERE EXISTS " + "(SELECT * FROM M_MovementLine l WHERE l.M_MovementLine_ID=ma.M_MovementLine_ID" + " AND M_Movement_ID=" + M_Movement_ID + ")";

        return DB.executeUpdate( sql,trxName );
    }    // deleteInOutMA

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MMovementLineMA.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MovementLineMA_ID
     * @param trxName
     */

    public MMovementLineMA( Properties ctx,int M_MovementLineMA_ID,String trxName ) {
        super( ctx,M_MovementLineMA_ID,trxName );

        if( M_MovementLineMA_ID != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MMovementLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMovementLineMA( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMovementLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     */

    public MMovementLineMA( MMovementLine parent,int M_AttributeSetInstance_ID,BigDecimal MovementQty ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setM_MovementLine_ID( parent.getM_MovementLine_ID());

        //

        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setMovementQty( MovementQty );
    }    // MMovementLineMA

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MMovementLineMA[" );

        sb.append( "M_MovementLine_ID=" ).append( getM_MovementLine_ID()).append( ",M_AttributeSetInstance_ID=" ).append( getM_AttributeSetInstance_ID()).append( ", Qty=" ).append( getMovementQty()).append( "]" );

        return sb.toString();
    }    // toString
}    // MMovementLineMA



/*
 *  @(#)MMovementLineMA.java   02.07.07
 * 
 *  Fin del fichero MMovementLineMA.java
 *  
 *  Versión 2.2
 *
 */
