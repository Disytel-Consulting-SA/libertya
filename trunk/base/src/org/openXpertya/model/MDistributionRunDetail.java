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
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDistributionRunDetail extends X_T_DistributionRunDetail {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_DistributionRun_ID
     * @param orderBP
     *
     * @return
     */

    static public MDistributionRunDetail[] get( Properties ctx,int M_DistributionRun_ID,boolean orderBP ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM T_DistributionRunDetail WHERE M_DistributionRun_ID=? ";

        if( orderBP ) {
            sql += "ORDER BY C_BPartner_ID, C_BPartner_Location_ID";
        } else {
            sql += "ORDER BY M_DistributionRunLine_ID";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_DistributionRun_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDistributionRunDetail( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MDistributionRunDetail[] retValue = new MDistributionRunDetail[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MDistributionRunDetail.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistributionRunDetail( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // DistributionRunDetail

    /** Descripción de Campos */

    private int m_precision = 0;

    /**
     * Descripción de Método
     *
     *
     * @param precision
     */

    public void round( int precision ) {
        boolean dirty = false;

        m_precision = precision;

        BigDecimal min = getMinQty();

        if( min.scale() > m_precision ) {
            setMinQty( min.setScale( m_precision,BigDecimal.ROUND_HALF_UP ));
            dirty = true;
        }

        BigDecimal qty = getQty();

        if( qty.scale() > m_precision ) {
            setQty( qty.setScale( m_precision,BigDecimal.ROUND_HALF_UP ));
            dirty = true;
        }

        if( dirty ) {
            save();
        }
    }    // round

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCanAdjust() {
        return( getQty().compareTo( getMinQty()) > 0 );
    }    // isCanAdjust

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getActualAllocation() {
        if( getQty().compareTo( getMinQty()) > 0 ) {
            return getQty();
        } else {
            return getMinQty();
        }
    }    // getActualAllocation

    /**
     * Descripción de Método
     *
     *
     * @param difference
     *
     * @return
     */

    public BigDecimal adjustQty( BigDecimal difference ) {
        BigDecimal diff = difference.setScale( m_precision,BigDecimal.ROUND_HALF_UP );
        BigDecimal qty       = getQty();
        BigDecimal max       = getMinQty().subtract( qty );
        BigDecimal remaining = Env.ZERO;

        if( max.compareTo( diff ) > 0 )    // diff+max are negative
        {
            remaining = diff.subtract( max );
            setQty( qty.add( max ));
        } else {
            setQty( qty.add( diff ));
        }

        log.fine( "adjustQty - Qty=" + qty + ", Min=" + getMinQty() + ", Max=" + max + ", Diff=" + diff + ", newQty=" + getQty() + ", Remaining=" + remaining );

        return remaining;
    }    // adjustQty
}    // DistributionRunDetail



/*
 *  @(#)MDistributionRunDetail.java   02.07.07
 * 
 *  Fin del fichero MDistributionRunDetail.java
 *  
 *  Versión 2.2
 *
 */
