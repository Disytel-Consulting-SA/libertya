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

public class MInOutLineMA extends X_M_InOutLineMA {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_InOutLine_ID
     * @param trxName
     *
     * @return
     */

    public static MInOutLineMA[] get( Properties ctx,int M_InOutLine_ID,String trxName ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM M_InOutLineMA WHERE M_InOutLine_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_InOutLine_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOutLineMA( ctx,rs,trxName ));
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

        MInOutLineMA[] retValue = new MInOutLineMA[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param M_InOut_ID
     * @param trxName
     *
     * @return
     */

    public static int deleteInOutMA( int M_InOut_ID,String trxName ) {
        String sql = "DELETE FROM M_InOutLineMA ma WHERE EXISTS " + "(SELECT * FROM M_InOutLine l WHERE l.M_InOutLine_ID=ma.M_InOutLine_ID" + " AND M_InOut_ID=" + M_InOut_ID + ")";

        return DB.executeUpdate( sql,trxName );
    }    // deleteInOutMA


    
    //Elmina las lineas de asignacion del remito
    // Jorge Vidal - Disytel
    public static int deleteInOutMALine( MInOutLine line,String trxName ) {
        
        PreparedStatement pstmt = null;
        int rno=-1;
        try {
            pstmt = DB.prepareStatement( "DELETE FROM M_InOutLineMA ma WHERE M_INOUTLINE_ID=?", trxName);
            pstmt.setInt( 1,line.getID() );
            rno=pstmt.executeUpdate();
                       
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"deleteInOutLineMALine",e );
        }

       
        return rno;
    }    // deleteInOutMALine
    
    
    
    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInOutLineMA.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InOutLineMA_ID
     * @param trxName
     */

    public MInOutLineMA( Properties ctx,int M_InOutLineMA_ID,String trxName ) {
        super( ctx,M_InOutLineMA_ID,trxName );

        if( M_InOutLineMA_ID != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MInOutLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInOutLineMA( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInOutLineMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     */

    public MInOutLineMA( MInOutLine parent,int M_AttributeSetInstance_ID,BigDecimal MovementQty ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setM_InOutLine_ID( parent.getM_InOutLine_ID());

        //

        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setMovementQty( MovementQty );
    }    // MInOutLineMA

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInOutLineMA[" );

        sb.append( "M_InOutLine_ID=" ).append( getM_InOutLine_ID()).append( ",M_AttributeSetInstance_ID=" ).append( getM_AttributeSetInstance_ID()).append( ", Qty=" ).append( getMovementQty()).append( "]" );

        return sb.toString();
    }    // toString
}    // MInOutLineMA



/*
 *  @(#)MInOutLineMA.java   02.07.07
 * 
 *  Fin del fichero MInOutLineMA.java
 *  
 *  Versión 2.2
 *
 */
